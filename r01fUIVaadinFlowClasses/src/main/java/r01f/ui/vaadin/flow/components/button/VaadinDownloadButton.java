package r01f.ui.vaadin.flow.components.button;

import java.util.Collection;
import java.util.concurrent.ExecutorService;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.server.StreamResource;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.file.FileName;
import r01f.guids.OIDs;
import r01f.mime.MimeType;
import r01f.patterns.Provider;
import r01f.ui.vaadin.flow.components.window.VaadinAsyncGenerateFileContentProgressWindow;
import r01f.ui.vaadin.flow.notifications.VaadinErrorNotification;
import r01f.ui.vaadin.flow.streams.VaadinResourceStreams;
import r01f.ui.vaadin.flow.streams.VaadinResourceStreams.VaadinResourceStreamGenerator;
import r01f.ui.vaadin.flow.streams.VaadinResourceStreams.VaadinResourceStreamingListener;
import r01f.util.types.collections.Lists;

/**
 * A download button that runs the [download payload] generation in a separate thread
 * Usage:
 * <pre>
 * 		// a [download payload] generator
 * 		VaadinDownloadedPayloadGenerator payloadGen = (out) -> {
 * 															// generate the [download payload] writing to out
 * 															// ...
 * 													  };
 *		// a button that starts the downloading when clicked
 * 		VaadinDownloadButton btnDownload = new VaadinDownloadButton(payloadGen,
 * 																	executorService);
 * 		btnDownload.setContentType("application/zip");
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
 * 
 * 
 * see: https://cookbook.vaadin.com/csv-download
 */
@Slf4j
@Accessors(prefix="_",chain=true)
public class VaadinDownloadButton 
	 extends Composite<Div> {

	private static final long serialVersionUID = -6075512584826294059L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final Collection<VaadinResourceStreamingListener> _downloadCompletedListeners = Lists.newArrayList();

	private final StreamResource _streamResource;
	
	private final Element _elObject;
	private final Icon _icon = new Icon(VaadinIcon.DOWNLOAD);
	private final NativeButton _button = new NativeButton();
	
	@Getter @Setter private Provider<MimeType> _mimeTypeProvider;
	@Getter @Setter private Provider<FileName> _fileNameProvider;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinDownloadButton() {
		this(null,	// ser the writer later!!
			 null);	// no executor service will be used
	}
	public VaadinDownloadButton(final VaadinResourceStreamGenerator downloadPayloadGenertor,
								final ExecutorService executorService) {
		////////// Stream resource
		_streamResource = VaadinResourceStreams.createStreamResourceUsing(// file name provider
																	      () -> _fileNameProvider != null ? _fileNameProvider.provideValue()
																			 						      : FileName.of(this.getFileName()),
																	      // mime type provider
																	      _mimeTypeProvider,
																	      // stream resource generator
																	      VaadinResourceStreams.createResourceStreamAsyncGeneratorFrom(downloadPayloadGenertor, 
																			 													       executorService),
																	      // streaming listeners
																	      _downloadCompletedListeners);
		////////// UI
		// An [object] html element that contains the stream
		_elObject = new Element("object");
		_elObject.getStyle().set("display","block");
		
		// layout
		this.getElement()	// div
			.appendChild(_icon.getElement(),_elObject,_button.getElement());
		
		////////// Behavior
		_setBehavior();
	}
	private void _setBehavior() {
		// default download completed listener
		_downloadCompletedListeners.add(new VaadinResourceStreamingListener() {
												private static final long serialVersionUID = -7909702215349409799L;
												
												@Override
												public void onCompleted() {
													log.debug("Resource streamed OK!");
												}
												@Override
												public void onError(final Throwable th) {
													VaadinErrorNotification notifError = new VaadinErrorNotification("Error",th);
													notifError.open();
												}
										});
		// when the button is clicked, generate the payload
		_button.addClickListener(clickEvent -> {
										if (_mimeTypeProvider != null) {
											_elObject.setAttribute("type",_mimeTypeProvider.provideValue().asString());
										} else {
											_elObject.removeAttribute("type");
										}
										
										// The StreamResource
										//		- provides the data
										// 		- is automatically converted into a URL which gets converted to the "data" attribute 
										//		  of the [object] element
										// 		  The file name given to a StreamResource is used as a part of the URL and also becomes the filename									
									    _elObject.setAttribute("data",_streamResource);
								 });
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DOWNLOAD COMPLETE LISTENER
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Adds a listener that is notified when the download has been sent to the client. 
	 * Note that you need to enable push connection or use UI.setPollingInterval to make 
	 * UI modifications visible automatically.
	 * 
	 * @param listener the listener to be notified
	 * @return the download button
	 */
	public VaadinDownloadButton addDownloadCompletedListener(final VaadinResourceStreamingListener listener) {
		_downloadCompletedListeners.add(listener);
		return this;
	}
	public void removeDownloadCompletedListener(final VaadinResourceStreamingListener listener) {
		_downloadCompletedListeners.remove(listener);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GET & SET
/////////////////////////////////////////////////////////////////////////////////////////	
	public VaadinDownloadButton setContentType(final String contentType) {
		_streamResource.setContentType(contentType);
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
	public VaadinDownloadButton withText(final String caption) {
		_button.setText(caption);
		return this;
	}
}