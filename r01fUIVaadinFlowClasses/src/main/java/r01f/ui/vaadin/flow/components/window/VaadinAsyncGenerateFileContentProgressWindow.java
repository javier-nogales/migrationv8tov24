package r01f.ui.vaadin.flow.components.window;

import java.io.InputStream;
import java.util.EventListener;
import java.util.EventObject;
import java.util.concurrent.ExecutorService;

import com.vaadin.flow.shared.Registration;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.ui.i18n.UII18NService;

/**
 * A window that asynchronously generates a file
 * Usage:
 * <pre class='brush:java'>
 * 		// [1] - Create the popup window
 * 		VaadinAsyncGenerateFileContentProgressWindow winProgress = new VaadinAsyncGenerateFileContentProgressWindow(_i18n,
 *																													_executorService, 
 *																													false);	// do NOT use indeterminate progress (it'll be updated -see progress subscriber-)
 *		// [2] - Set the listener for generated file ready events
 *		// When the content is generated DOWNLOAD it
 *		// BEWARE!! the resource MUST be attached to THIS window since the [progress window] is CLOSED when
 *		//			the file is generated so it would NOT be available if attached to the [progress window]
 *		winProgress.addGeneratedFileAvailableListener(isReadyEvent -> {
 *															// This is the way to make a StreamResource available without using a FileDownloader 
 *															// attached to a [button]
 *															// BEWARE!! the resource should be bound to a component (ie this window) to allow it to be garbage
 *															//		    collected when there are no longer any ways of reaching the resource.
 *															//			... so BEWARE when this WIN is NOT removed and multiple downloads area attached to the window!!!!
 *
 *															// a) Register a resource with this connector using the given key. 
 *															// 	  This will make the URL for retrieving the resource available to the client-side
 *															// 	  connector using {@link com.vaadin.terminal.gwt.client.ui.AbstractConnector#getResourceUrl(String)}
 *															// 	  with the same key.
 *															@SuppressWarnings("resource")
 *															VaadinStreamResource res = VaadinResourceStreams.createStreamResourceUsing(isReadyEvent.getStream(),			// resource
 *																										  							   "myFile.xls",	
 *																										  							   this);				// client connector & key used to attach the ResourceReference
 *															this.setResource(res.getReferenceKey(),	
 *																			 res.getStream()); 	// now it's available for download
 *															// b) Redir to the resource url
 *															//	  BEWARE!!! the resource is attached to THIS window: if closed it's no longer available 
 *															//				and the download fails-
 *															Page.getCurrent()
 *																.open(res.getReferenceUrl(),
 *																	  null);	// window name
 *													   });
 *		// [3] - Create a Subscriber that "listens" for the progress of the export file generation
 *		ProgressSubscriber<MyProgress> progressSubscriber = progress -> {
 *																 log.info("...processed item {} of {}",
 *																		  progress.getCurrentlyProcessedItems(),progress.getTotalItemsToBeProcessed());
 *																 // update the progress bar
 *																 winProgress.updateProgress(progress.getTotalItemsToBeProcessed(),progress.getCurrentlyProcessedItems(),
 *																		 					progress.getProgrees());
 *															};
 *		// [4] - Create a Susbscriber for the CORE response
 *		//		 The PRESENTER logic just returns a Callable that THIS popup will execute in a separate thread 
 *		//		 (the CORE logic is NOT executed until THIS popup schedules the Callable)
 *		// call the presenter (remember that the CORE just returns a Callable<InputStream> which is scheduled by thw progress popup)
 *		_importExportPresenter.onGenerateFileRequested(..., 
 *													   // progress subscriber > it's handed to the CORE and updated while the generation process progresses (if the CORE is async)
 *													   progressSubscriber,
 *													   // The PRESENTER logic just returns a Callable that THIS popup will execute in a separate thread 
 *													   // (the CORE logic is NOT executed until THIS popup schedules the Callable)
 *													   callable -> {
 *															// show a popup that will show a progress bar while the file is being generated
 *															winProgress.runTask(callable);
 *														});
 * </pre>
 */
public class VaadinAsyncGenerateFileContentProgressWindow  
     extends VaadinAsyncExecuteCancellableProgressWindow<InputStream> {
	
	private static final long serialVersionUID = -6575352956457549849L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR / BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinAsyncGenerateFileContentProgressWindow(final UII18NService i18n,
										  				final ExecutorService executorService,
										  				final boolean useIndeterminateProgress) {
		this(i18n.getMessage("export.window.caption"),i18n.getMessage("export.window.message"),i18n.getMessage("cancel"),
			 executorService,
			 useIndeterminateProgress);
	}
	public VaadinAsyncGenerateFileContentProgressWindow(final String caption,final String description,
										  				final ExecutorService executorService,
										  				final boolean useIndeterminateProgress) {
		this(caption,description,"Cancel",
			 executorService,
			 useIndeterminateProgress);
	}
	public VaadinAsyncGenerateFileContentProgressWindow(final String caption,final String description,final String cancel,
										  				final ExecutorService executorService,
										  				final boolean useIndeterminateProgress) {
		super(caption,description,cancel,
			  executorService,
			  useIndeterminateProgress);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EVENTS
/////////////////////////////////////////////////////////////////////////////////////////
	public Registration addGeneratedFileAvailableListener(final VaadinGeneratedFileAvailableListener listener) {
		if (listener == null) return () -> {/* nothing to remove */};
		return super.addExecutionFinishedListener(// wrap event listener just to raise a VaadinGeneratedFileAvailableEvent
												  event -> {
														@SuppressWarnings("resource")
														InputStream result = event.getResult();
														listener.onGeneratedFileAvailable(new VaadinGeneratedFileAvailableEvent(result));
												  });
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	
	@Accessors(prefix="_")
	public class VaadinGeneratedFileAvailableEvent 
		 extends EventObject {
		
		private static final long serialVersionUID = 460949189812864921L;
		
		@Getter private final InputStream _stream;
		
		public VaadinGeneratedFileAvailableEvent(final InputStream is) {
			super(VaadinAsyncGenerateFileContentProgressWindow.this);
			_stream = is;
		}
	}
	public interface VaadinGeneratedFileAvailableListener 
			 extends EventListener {
		public void onGeneratedFileAvailable(final VaadinGeneratedFileAvailableEvent event);
	}
}