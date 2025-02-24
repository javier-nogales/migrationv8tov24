package r01f.ui.vaadin;

import java.io.InputStream;

import com.vaadin.server.ClientConnector;
import com.vaadin.server.DownloadStream;
import com.vaadin.server.ResourceReference;
import com.vaadin.server.StreamResource;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.file.FileName;
import r01f.guids.OIDs;
import r01f.mime.MimeType;
import r01f.patterns.Provider;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class VaadinResourceStreams {
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
		String contentDisposition = streamResource.getStream()
												  .getParameter(DownloadStream.CONTENT_DISPOSITION);
		if (contentDisposition == null) {
			contentDisposition = "attachment; " + DownloadStream.getContentDispositionFilename(streamResource.getFilename());
			streamResource.getStream()
						  .setParameter(DownloadStream.CONTENT_DISPOSITION,
								  		contentDisposition);
		}
		// tell the browser to do NOT cache
		streamResource.getStream().setCacheTime(0);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static StreamResource createStreamResourceUsing(final InputStream inputStream,
														   final FileName fileName) {
		return VaadinResourceStreams.createStreamResourceUsing(() -> inputStream,
															   () -> fileName,null);	// no mime-type provider
	}
	public static StreamResource createStreamResourceUsing(final InputStream inputStream,
														   final FileName fileName,final MimeType mimeType) {
		return VaadinResourceStreams.createStreamResourceUsing(() -> inputStream,
															   () -> fileName,() -> mimeType);	// no mime-type provider
	}
	public static StreamResource createStreamResourceUsing(final InputStream inputStream,
														   final Provider<FileName> fileNameProvider) {
		return VaadinResourceStreams.createStreamResourceUsing(() -> inputStream,
															   fileNameProvider,null);	// no mime-type provider
	}
	public static StreamResource createStreamResourceUsing(final StreamResource.StreamSource streamSource,
														   final Provider<FileName> fileNameProvider) {
		return VaadinResourceStreams.createStreamResourceUsing(streamSource,
															   fileNameProvider,null);	// no mime-type provider
	}
	public static StreamResource createStreamResourceUsing(final StreamResource.StreamSource streamSource,
														   final Provider<FileName> fileNameProvider,final Provider<MimeType> mimeTypeProvider) {
		String defFileName = OIDs.supplyShortOid();
		StreamResource outStreamResource = new StreamResource(// the [stream source]
															  // new StreamResource.StreamSource() {
															  // 		@Override 
															  //		public InputStream getStream() {
															  //			...
															  //		}
															  streamSource,
															  // the file name
															  fileNameProvider != null ? fileNameProvider.provideValue().asString() 
																	  				   : defFileName);
		if (mimeTypeProvider != null) outStreamResource.setMIMEType(mimeTypeProvider.provideValue().asString());
		return outStreamResource;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static VaadinStreamResource createStreamResourceUsing(final InputStream inputStream,
																 final FileName fileName,
																 final ClientConnector clientConnector) {
		return VaadinResourceStreams.createStreamResourceUsing(inputStream,
															   () -> fileName,	// filename provider
															   clientConnector);
	}
	public static VaadinStreamResource createStreamResourceUsing(final InputStream inputStream,
																 final Provider<FileName> fileNameProvider,
																 final ClientConnector clientConnector) {
		// https://stackoverflow.com/questions/20400017/how-to-start-a-file-download-in-vaadin-without-button
		
		// the key used to attach the stream as a resource (used later to get an url to the resource)
		String key = "stream-resource-" + OIDs.supplyShortOid(); 
		
		// When the FutureTask finished creating the file (and returns an InputStream to it's content)
		// create a StreamResource to it
		StreamResource streamResource = VaadinResourceStreams.createStreamResourceUsing(inputStream,
																						fileNameProvider != null ? fileNameProvider.provideValue() 
																												 : FileName.of(key));	// no filename provider > use the key as last resource 
		// ensure the stream has a [Content-Disposition] header
		// ...or else browser will try to open resource instead of download it		
		VaadinResourceStreams.ensureResourceStreamCanBeDownloaded(streamResource);
		
		// Register a resource with this connector using the given key. 
		// This will make the URL for retrieving the resource available to the client-side
		// connector using {@link com.vaadin.terminal.gwt.client.ui.AbstractConnector#getResourceUrl(String)}
		// with the same key.
		ResourceReference ref = new ResourceReference(streamResource,	// resource
													  clientConnector,				// client connector
													  key);
		return new VaadinStreamResource(streamResource,
										key,ref);
	}
	@Accessors(prefix="_")
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class VaadinStreamResource {
		@Getter private final StreamResource _stream;
		@Getter private final String _referenceKey;
		@Getter private final ResourceReference _reference;
		
		public String getReferenceUrl() {
			return _reference.getURL();
		}
	}
}
