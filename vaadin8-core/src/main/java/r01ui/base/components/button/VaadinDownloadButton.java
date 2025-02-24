package r01ui.base.components.button;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.concurrent.ExecutorService;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.file.FileName;
import r01f.guids.OIDs;
import r01f.mime.MimeType;
import r01f.patterns.Provider;
import r01f.ui.vaadin.VaadinResourceStreams;
import r01f.ui.vaadin.streams.VaadinThrowingStreamSource;
import r01ui.base.components.window.VaadinAsyncGenerateFileContentProgressWindow;

/**
 * A download button that runs the [download payload] generation in a separate thread
 * Usage:
 * <pre>
 * 		// a [download payload] generator
 * 		R01UIDownloadedPayloadGenerator payloadGen = (out) -> {
 * 															// generate the [download payload] writing to out
 * 															// ...
 * 													 };
 *		// a button that starts the downloading when clicked
 * 		VaadinDownloadButton btnDownload = new VaadinDownloadButton(payloadGen,
 * 																	executorService);
 * 		btnDownload.setMimeType("application/zip");
 *		btnDownload.setFileName("export.zip");
 * 		btnDownload.addDownloadCompletedListener(() -> {
 * 													log.info("Download completed!!");
 * 												 };
 * </pre>
 * 
 * BEWARE!!
 * This component is suitable when the [download payload] generation is NOT a lengthy operation 
 * since it DOES NOT show a [progress window]
 * For lengthy operations where a [progress window] is advisable see {@link VaadinAsyncGenerateFileContentProgressWindow}
 */
@Accessors(prefix="_",chain=true)
public class VaadinDownloadButton 
	 extends Button {

	private static final long serialVersionUID = -6075512584826294059L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final int PIPE_BUFFER_SIZE = 1 * 1024 * 1024;	// 1 MB
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@FunctionalInterface
	public interface R01UIDownloadedPayloadGenerator 
			 extends Serializable {
		void generateDownloadPayloadWritingTo(OutputStream stream);
	}
	public interface R01UIDownloadCompletedListener 
			 extends Serializable {
		void onCompleted();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SERVICES
/////////////////////////////////////////////////////////////////////////////////////////
	// Manages the ExecutorService where the task will run
	private final transient ExecutorService _executorService;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private Collection<R01UIDownloadCompletedListener> _downloadCompletedListeners;

	@Getter @Setter private Provider<MimeType> _mimeTypeProvider;
	@Getter @Setter private Provider<FileName> _fileNameProvider;
	
	@Getter @Setter private R01UIDownloadedPayloadGenerator _downloadPayloadGenertor;
	
	// override a vaadin's StreamResource used to download from a stream
	@SuppressWarnings("resource")
	private final VaadinThrowingStreamSource _streamSource = 
													// 		@Override 
													//		public InputStream getStream() {
													//			...
													//		}
													() -> {
														if (_downloadPayloadGenertor == null) throw new IllegalStateException("NO download payload generator set!");
														
														// data is written to the outputstream and readed at the inputstream
														PipedOutputStream out = new PipedOutputStream();
														PipedInputStream in = new PipedInputStream(out,PIPE_BUFFER_SIZE);
														
														// generates the download payload
														this.generateDownloadPayload(out);
														
														// return the [in] where the content written at [out] will be available
														return in;
													};
	protected final StreamResource _streamResource = VaadinResourceStreams.createStreamResourceUsing(_streamSource,
																								     // file name provider
																									 () -> _fileNameProvider != null ? _fileNameProvider.provideValue()
																											 						 : FileName.of(this.getFileName()),
																									 // mime type provider
																									 _mimeTypeProvider);
	protected void generateDownloadPayload(final OutputStream out) {
		// the download payload generation is spawned in a separate thread
		Runnable runnable = () -> {
								try {
									_downloadPayloadGenertor.generateDownloadPayloadWritingTo(out);
									out.close();
								} catch (Throwable th) {
									UI.getCurrent()
									  .access(() -> _handleErrorInFileGeneration(th));
								}
						   };				
		if (_executorService != null) {
			// use the executor service if available to write the output to the client
			_executorService.execute(runnable);
		} else {
			// ... or spawn a new thread to write the output to the client
			new Thread(runnable).start();
		}
	}
	protected void _handleErrorInFileGeneration(final Throwable th) {
		this.setComponentError(new UserError(th.getMessage()));
		this.fireComponentErrorEvent();
		throw new RuntimeException(th);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinDownloadButton() {
		this(null,	// ser the writer later!!
			 null);	// no executor service will be used
	}
	public VaadinDownloadButton(final R01UIDownloadedPayloadGenerator generator,
								final ExecutorService executorService) {
		_executorService = executorService;
		_downloadPayloadGenertor = generator;
		
		// Extension that starts a download when the extended component is clicked
		// This is used to overcome two challenges:
		// 		- Resource should be bound to a component to allow it to be garbage
		//		  collected when there are no longer any ways of reaching the resource.
		//		- Download should be started directly when the user clicks 
		//		  ie. a Button without going through a server-side click listener to avoid triggering
		//		  	  security warnings in some browsers.
		// Please note that the download will be started in an iframe, which means that
		// care should be taken to avoid serving content types that might make the
		// browser attempt to show the content using a plugin instead of downloading it
		new FileDownloader(_streamResource) {
				private static final long serialVersionUID = -362906453359065643L;
	
				@Override
				public boolean handleConnectorRequest(final VaadinRequest request,final VaadinResponse response, 
													  final String path) throws IOException {
					boolean handleConnectorRequest = super.handleConnectorRequest(request, 
																				  response, 
																				  path);
					// tell [download completed listeners] that the response has been written
					if (handleConnectorRequest) VaadinDownloadButton.this.afterResponseWritten();
					return handleConnectorRequest;
				}
		}.extend(this);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DOWNLOAD COMPLETE LISTENER
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * A hook for extension to do something after the response has been sent to the
	 * client.
	 */
	protected void afterResponseWritten() {
		if (_downloadCompletedListeners == null) return;
		this.getUI()
			// provides exclusive access to this UI from outside a request handling thread
			// (beware that this method is called from a spawned thread at #writeResponse method)
			.access(() -> {
						if (_downloadCompletedListeners == null) return;
						_downloadCompletedListeners.forEach(R01UIDownloadCompletedListener::onCompleted);
					});
	}
	/**
	 * Adds a listener that is notified when the download has been sent to the client. 
	 * Note that you need to enable push connection or use UI.setPollingInterval to make 
	 * UI modifications visible automatically.
	 * 
	 * @param listener the listener to be notified
	 * @return the download button
	 */
	public VaadinDownloadButton addDownloadCompletedListener(final R01UIDownloadCompletedListener listener) {
		if (_downloadCompletedListeners == null) _downloadCompletedListeners = new LinkedHashSet<>();
		_downloadCompletedListeners.add(listener);
		return this;
	}
	public void removeDownloadCompletedListener(final R01UIDownloadCompletedListener listener) {
		_downloadCompletedListeners.remove(listener);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GET & SET
/////////////////////////////////////////////////////////////////////////////////////////	
	public String getMimeType() {
		return _streamResource.getMIMEType();
	}
	public VaadinDownloadButton setMimeType(final String mimeType) {
		_streamResource.setMIMEType(mimeType);
		return this;
	}
	public VaadinDownloadButton setCacheTime(final long cacheTime) {
		_streamResource.setCacheTime(cacheTime);
		return this;
	}
	public String getFileName() {
		return _fileNameProvider != null ? _fileNameProvider.provideValue().asString()
										 : OIDs.supplyShortOid();
	}
	public VaadinDownloadButton setFileName(final String fileName) {
		_fileNameProvider = () -> FileName.of(fileName);	// FileNameProvider by default
		return this;
	}
	public VaadinDownloadButton withIcon(final Resource icon) {
		this.setIcon(icon);
		return this;
	}
	public VaadinDownloadButton withCaption(final String caption) {
		this.setCaption(caption);
		return this;
	}
}