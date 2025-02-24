package r01ui.base.components.window;

import java.util.Collection;
import java.util.concurrent.ExecutorService;

import com.google.common.collect.Lists;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.VerticalLayout;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * A window that asynchronously executes a lengthy operation that can be cancelled
 * Usage:
 * <pre class='brush:java'>
 * 		// [1] - Create the popup window
 * 		VaadinAsyncExecuteCancellableProgressWindow winProgress = new VaadinAsyncExecuteCancellableProgressWindow("async execution of lengthy operation","....","Cancel",
 *																												  _executorService, 
 *																												  false);	// do NOT use indeterminate progress (it'll be updated -see progress subscriber-)
 *		// [2] - Set the listener for execution finished events
 *		winProgress.addExecutionFinishedListener(isReadyEvent -> {
													// anything to be done when the execution has finished
 *												});
 *		// [3] - Create a Subscriber that "listens" for the progress of the async execution
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
 *		_presenter.onLengthyOperationRequested(..., 
 *											   // progress subscriber > it's handed to the CORE and updated while the generation process progresses (if the CORE is async)
 *											   progressSubscriber,
 *											   // The PRESENTER logic just returns a Callable that THIS popup will execute in a separate thread 
 *											   // (the CORE logic is NOT executed until THIS popup schedules the Callable)
 *											   callable -> {
 *													// show a popup that will show a progress bar while the file is being generated
 *													winProgress.runTask(callable);
 *											   });
 * </pre>
 */
public class VaadinAsyncExecuteCancellableProgressWindow<T> 
     extends VaadinAsynExecuteProgressWindowBase<T> {
	
	private static final long serialVersionUID = -6575352956457549849L;
/////////////////////////////////////////////////////////////////////////////////////////
//	UI
/////////////////////////////////////////////////////////////////////////////////////////
	protected final Label _lblDescription;
	protected final ProgressBar _progressBar;
	protected final Label _lblProgress;
	protected final Button _btnCancel;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private final Collection<VaadinExecutionFinishedListener<T>> _eventListenersForExecutionFinished = Lists.newArrayList();
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR / BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinAsyncExecuteCancellableProgressWindow(final String caption,final String description,
										  			   final ExecutorService executorService,
										  			   final boolean useIndeterminateProgress) {
		this(caption,description,"Cancel",
			 executorService,
			 useIndeterminateProgress);
	}
	public VaadinAsyncExecuteCancellableProgressWindow(final String caption,final String description,final String cancel,
										  			   final ExecutorService executorService,
										  			   final boolean useIndeterminateProgress) {
		super(executorService);
		
		// Window properties
		this.setCaption(caption);
		
		////////// Window content
		_lblDescription = new Label(description,ContentMode.HTML);
		
		_progressBar = new ProgressBar();
		_progressBar.setIndeterminate(useIndeterminateProgress);
		
		_lblProgress = new Label();
		
		_btnCancel = new Button(cancel);
		
		////////// layout
		VerticalLayout vly = new VerticalLayout();
		vly.setHeight(250, Unit.PIXELS);
		vly.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		vly.addComponents(_lblDescription, 
						  _progressBar,
						  _lblProgress,
						  _btnCancel);
		vly.setComponentAlignment(_btnCancel,Alignment.BOTTOM_RIGHT);
		this.setContent(vly);
		
		////////// behavior
		_setBehavior();
	}
	private void _setBehavior() {
		_btnCancel.addClickListener(clickEvent -> {
										_btnCancel.setEnabled(false);
										_cleanExit();
									});
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EVENTS
/////////////////////////////////////////////////////////////////////////////////////////
	public Registration addExecutionFinishedListener(final VaadinExecutionFinishedListener<T> listener) {
		if (listener == null) return () -> {/* nothing to remove */};
		_eventListenersForExecutionFinished.add(listener);
		return () -> _eventListenersForExecutionFinished.remove(listener);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PROGRESS
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isProgressBarIndeterminate() {
		return _progressBar.isIndeterminate();
	}
	public void updateProgressMessage(final String progressMsg) {
		_lblProgress.setValue(progressMsg);
	}
	public void updateProgress(final float progress) {
		if (_progressBar.isIndeterminate()) return;
		_progressBar.setValue(progress);
	}
	public void updateProgress(final long totalCount,final long currentCount,
							   final float progress) {
		this.updateProgressMessage(Strings.customized("{} / {}",
											   		  currentCount,totalCount));
		this.updateProgress(progress);
	}	
	public void increaseProgress(final float incr) {
		if (_progressBar.isIndeterminate()) return;
		float current = _progressBar.getValue();
		if (current < 1.0f) _progressBar.setValue(current + incr);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	protected void _onSuccess(final T result) {
		this.updateProgress(1.0f);	// completed
	
		// tell the listeners
		if (CollectionUtils.hasData(_eventListenersForExecutionFinished)) {
			_eventListenersForExecutionFinished.stream()
											   .forEach(listener -> listener.onExecutionFinished(new VaadinExecutionFinishedEvent<>(this,		// event source
													   																				result)));
		}
	}
	@Override
	public void setDescription(String description) {
		_lblDescription.setDescription(description, ContentMode.HTML);		
	};	
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public static class VaadinExecutionFinishedEvent<T> 
		  extends Component.Event {
		
		private static final long serialVersionUID = 460949189812864921L;
		
		@Getter private final T _result;
		
		public VaadinExecutionFinishedEvent(final Component source,
										    final T result) {
			super(source);
			_result = result;
		}
	}
	public interface VaadinExecutionFinishedListener<T> {
		public void onExecutionFinished(final VaadinExecutionFinishedEvent<T> event);
	}
}