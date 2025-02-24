package r01f.ui.vaadin.flow.streams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.ExecutorService;

import com.vaadin.flow.internal.EncodeUtil;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.StreamResourceWriter;
import com.vaadin.flow.server.VaadinSession;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.file.FileName;
import r01f.guids.OIDs;
import r01f.mime.MimeType;
import r01f.patterns.Provider;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@Slf4j
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class VaadinResourceStreams {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String CONTENT_DISPOSITION = "Content-Disposition";
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Ensures a {@link StreamResource} can be DOWNLOADED and NOT oppened by the browser
	 * @param streamResource
	 * @return
	 */
	public static void ensureResourceStreamCanBeDownloaded(final StreamResource streamResource) {
		// ensure the stream has a [Content-Disposition] header
		// ...or else browser will try to open resource instead of download it	
		String contentDisposition = streamResource.getHeader(CONTENT_DISPOSITION)
												  .orElse(null);
		if (contentDisposition == null) {
			String encodedFilename = EncodeUtil.rfc5987Encode(streamResource.getName());
			contentDisposition = Strings.customized("attachment; filename=\"{}\"; filename*=utf-8''{}",
													encodedFilename,encodedFilename);
			streamResource.setHeader(CONTENT_DISPOSITION,
									 contentDisposition);
		}
		// tell the browser to do NOT cache
		streamResource.setCacheTime(0);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates {@link StreamResource} from an {@link InputStream}
	 * NOTE: in order to create an {@link InputStream} from the content written to an {@link OutputStream} use:
	 * <pre class='brush:java'>
	 *		InputStreamFactory _inputStreamFactory = () -> {
	 *													// data is written to the outputstream and readed at the inputstream
	 *													PipedOutputStream out = new PipedOutputStream();
	 *													PipedInputStream in = new PipedInputStream(out,PIPE_BUFFER_SIZE);
	 *													
	 *													// generates the download payload 
	 *													// using an executor service if available
	 *													this.generateDownloadPayload(out);
	 *													
	 *													// return the [in] where the content written at [out] will be available
	 *													return in;
	 *												}
	 * </pre> 
	 * @param fileNameProvider
	 * @param mimeTypeProvider
	 * @param streamFactory
	 * @return
	 */
	public static StreamResource createStreamResourceUsing(final Provider<FileName> fileNameProvider,final Provider<MimeType> mimeTypeProvider,
														   final InputStreamFactory streamFactory) {
		return _createStreamResourceUsing(fileNameProvider,mimeTypeProvider,
										  streamFactory);	
	}
	/**
	 * Creates {@link StreamResource} from an {@link InputStream}
	 * NOTE: in order to create an {@link InputStream} from the content written to an {@link OutputStream} use:
	 * <pre class='brush:java'>
	 *		InputStreamFactory _inputStreamFactory = () -> {
	 *													// data is written to the outputstream and readed at the inputstream
	 *													PipedOutputStream out = new PipedOutputStream();
	 *													PipedInputStream in = new PipedInputStream(out,PIPE_BUFFER_SIZE);
	 *													
	 *													// generates the download payload 
	 *													// using an executor service if available
	 *													this.generateDownloadPayload(out);
	 *													
	 *													// return the [in] where the content written at [out] will be available
	 *													return in;
	 *												}
	 * </pre> 
	 * @param fileNameProvider
	 * @param mimeTypeProvider
	 * @param streamFactory
	 * @param streamingListeners
	 * @return
	 */
	public static StreamResource createStreamResourceUsing(final Provider<FileName> fileNameProvider,final Provider<MimeType> mimeTypeProvider,
														   final InputStreamFactory streamFactory,
														   final Collection<VaadinResourceStreamingListener> streamingListeners) {
		return _createStreamResourceUsing(fileNameProvider,mimeTypeProvider,
										  new VaadinStreamResourcePiped(streamFactory,
												   					    streamingListeners));
	}
	/**
	 * Creates {@link StreamResource} 
	 * @param fileNameProvider
	 * @param mimeTypeProvider
	 * @param resourceStreamGenerator
	 * @return
	 */
	public static StreamResource createStreamResourceUsing(final Provider<FileName> fileNameProvider,final Provider<MimeType> mimeTypeProvider,
														   final VaadinResourceStreamGenerator resourceStreamGenerator) {
		return VaadinResourceStreams.createStreamResourceUsing(fileNameProvider,mimeTypeProvider,
															   resourceStreamGenerator,
															   null);	// no listeners
	}
	/**
	 * Creates {@link StreamResource} 
	 * @param fileNameProvider
	 * @param mimeTypeProvider
	 * @param resourceStreamGenerator
	 * @param streamingListeners
	 * @return
	 */
	public static StreamResource createStreamResourceUsing(final Provider<FileName> fileNameProvider,final Provider<MimeType> mimeTypeProvider,
														   final VaadinResourceStreamGenerator resourceStreamGenerator,
														   final Collection<VaadinResourceStreamingListener> streamingListeners) {
		return _createStreamResourceUsing(fileNameProvider,mimeTypeProvider,
										  new VaadinStreamResourceDirect(resourceStreamGenerator,
												   						 streamingListeners));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	private static StreamResource _createStreamResourceUsing(final Provider<FileName> fileNameProvider,final Provider<MimeType> mimeTypeProvider,
														     final StreamResourceWriter writer) {
		String defFileName = OIDs.supplyShortOid();
		StreamResource outStreamResource = new StreamResource(// the file name
															  fileNameProvider != null ? fileNameProvider.provideValue().asString() 
																	  				   : defFileName,
															  // the [stream resource] writer
															  writer);
		if (mimeTypeProvider != null) outStreamResource.setContentType(mimeTypeProvider.provideValue().asString());
		return outStreamResource;
	}
	private static StreamResource _createStreamResourceUsing(final Provider<FileName> fileNameProvider,final Provider<MimeType> mimeTypeProvider,
														     final InputStreamFactory inputStreamFactory) {
		String defFileName = OIDs.supplyShortOid();
		StreamResource outStreamResource = new StreamResource(// the file name
															  fileNameProvider != null ? fileNameProvider.provideValue().asString() 
																	  				   : defFileName,
															  // the [stream resource] input stream factory
															  inputStreamFactory);
		if (mimeTypeProvider != null) outStreamResource.setContentType(mimeTypeProvider.provideValue().asString());
		return outStreamResource;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	INTERFACES
/////////////////////////////////////////////////////////////////////////////////////////
	@FunctionalInterface
	public interface VaadinResourceStreamingListener 
			 extends Serializable {
		void onCompleted();
		default void onError(final Throwable th) {
			// do nothing
		}
	}
	@FunctionalInterface
	public interface VaadinResourceStreamGenerator 
			 extends Serializable {
		void generateResourceStreamWritingTo(OutputStream stream) throws IOException;
		
	    default boolean requiresVaadinSessionLock() {
	        return false;
	    }
	}
	public static VaadinResourceStreamGenerator createResourceStreamAsyncGeneratorFrom(final VaadinResourceStreamGenerator gen,
																					   final ExecutorService executorService) {
		return (os) -> {
					// the download payload generation is spawned in a separate thread
					Runnable runnable = () -> {
											try {
												gen.generateResourceStreamWritingTo(os);
											} catch (IOException ioEx) {
												log.error("Error streaming resource: {}",
														  ioEx.getMessage(),ioEx);
											}
										};
					
					if (executorService != null) {
						// use the executor service if available to write the output to the client
						executorService.execute(runnable);
					} else {
						// ... or spawn a new thread to write the output to the client
						new Thread(runnable).start();
					}
			   };
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	WRITE DIRECTLY TO THE OUTPUTSTREAM
/////////////////////////////////////////////////////////////////////////////////////////
	private static class VaadinStreamResourceDirect 
			  implements StreamResourceWriter {
		private static final long serialVersionUID = 7110262528415571970L;

		private final VaadinResourceStreamGenerator _streamGenerator;
		private final Collection<VaadinResourceStreamingListener> _resourceStreamingListeners;
		
		private VaadinStreamResourceDirect(final VaadinResourceStreamGenerator streamGenerator) {
			this(streamGenerator,
				 null);	// no listeners
		}
		private VaadinStreamResourceDirect(final VaadinResourceStreamGenerator streamGenerator,
										   final Collection<VaadinResourceStreamingListener> resourceStreamingListeners) {
			_streamGenerator = streamGenerator;
			_resourceStreamingListeners = resourceStreamingListeners;
		}
		
		@Override
		public void accept(final OutputStream out, 
						   final VaadinSession vaadinSession) throws IOException {
			try {
				// lock session if required
				if (_streamGenerator.requiresVaadinSessionLock()) vaadinSession.lock();
				
				// generate content
				_streamGenerator.generateResourceStreamWritingTo(out);
				out.flush();
				out.close();
				
				// listener
				if (CollectionUtils.hasData(_resourceStreamingListeners)) _resourceStreamingListeners.forEach(listener -> listener.onCompleted());
			} catch (Throwable ioEx) {
				if ("Broken pipe".equals(ioEx.getMessage())) {
					log.debug("The client browser has most likely cancelled the request.",
							  ioEx);
				} else {
					throw ioEx;
				}
				// listener
				if (CollectionUtils.hasData(_resourceStreamingListeners)) _resourceStreamingListeners.forEach(listener -> listener.onError(ioEx));
			} finally {
				// unlock session if required
				if (_streamGenerator.requiresVaadinSessionLock()) vaadinSession.unlock();
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PIPES the InputStream to the OutputStream
/////////////////////////////////////////////////////////////////////////////////////////
	private static class VaadinStreamResourcePiped 
			  implements StreamResourceWriter {

		private static final long serialVersionUID = 2189711838127263628L;

		private static final int DEF_BUFFER_SIZE = 1 * 1024 * 1024;	// 1 MB

		private final InputStreamFactory _isFactory;
		private final int _bufferSize;
		private final Collection<VaadinResourceStreamingListener> _resourceStreamingListeners;

		private VaadinStreamResourcePiped(final InputStreamFactory inputStreamFactory) {
			this(inputStreamFactory,DEF_BUFFER_SIZE,
				 null);
		}
		private VaadinStreamResourcePiped(final InputStreamFactory inputStreamFactory,
										  final Collection<VaadinResourceStreamingListener> downloadCompletedListeners) {
			this(inputStreamFactory,DEF_BUFFER_SIZE,
				 downloadCompletedListeners);
		}
		private VaadinStreamResourcePiped(final InputStreamFactory inputStreamFactory,final int bufferSize,
										  final Collection<VaadinResourceStreamingListener> downloadCompletedListeners) {
			_isFactory = inputStreamFactory;
			_bufferSize = bufferSize;
			_resourceStreamingListeners = downloadCompletedListeners;
		}
		@Override
		public void accept(final OutputStream out, 
						   final VaadinSession vaadinSession) throws IOException {
			try (InputStream source = _createInputStream(vaadinSession)) {
				// copy the generated data to the outputstream
				_copy(vaadinSession,
					  source,out);
				// listener
				if (CollectionUtils.hasData(_resourceStreamingListeners)) _resourceStreamingListeners.forEach(listener -> listener.onCompleted());
			} catch (IOException ioEx) {
				if ("Broken pipe".equals(ioEx.getMessage())) {
					log.debug("The client browser has most likely cancelled the request.",
							  ioEx);
				} else {
					throw ioEx;
				}
				// listener
				if (CollectionUtils.hasData(_resourceStreamingListeners)) _resourceStreamingListeners.forEach(listener -> listener.onError(ioEx));
			}
		}
		private InputStream _createInputStream(final VaadinSession vaadinSession) {
			vaadinSession.lock();
			try {
				return _isFactory.createInputStream();
			} finally {
				vaadinSession.unlock();
			}
		}
		private void _copy(final VaadinSession session, 
						   final InputStream source,final OutputStream out) throws IOException {
			byte[] buf = new byte[_bufferSize];
			int n;
			while ((n = _read(session, 
							  source,buf)) >= 0) {
				out.write(buf,0,n);
			}
		}
		private int _read(final VaadinSession vaadinSession,
						  final InputStream source,final byte[] buffer) throws IOException {
			if (_isFactory.requiresLock()) {
				vaadinSession.lock();
				try {
					return source.read(buffer);
				} finally {
					vaadinSession.unlock();
				}
			} else {
				return source.read(buffer);
			}
		}
	}
}
