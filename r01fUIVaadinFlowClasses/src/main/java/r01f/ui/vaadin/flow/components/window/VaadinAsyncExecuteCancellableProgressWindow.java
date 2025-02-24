package r01f.ui.vaadin.flow.components.window;

import java.util.Collection;
import java.util.EventListener;
import java.util.EventObject;
import java.util.concurrent.ExecutorService;

import com.google.common.collect.Lists;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.shared.Registration;

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
		
		////////// Window content
		_lblDescription = new Label(description);
		
		_progressBar = new ProgressBar();
		_progressBar.setIndeterminate(useIndeterminateProgress);
		
		_lblProgress = new Label();
		
		
		
		////////// layout
		// Header -------
		this.setHeaderTitle(caption);
		
		// Body --------
		VerticalLayout vly = new VerticalLayout();
		vly.setAlignItems(Alignment.CENTER);
		vly.add(_lblDescription, 
				_progressBar,
				_lblProgress);
		// Footer -------
		_btnCancel = new Button(cancel);
		this.getFooter()
			.add(_btnCancel);
		
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
		_lblProgress.setText(progressMsg);
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
		double current = _progressBar.getValue();
		if (current < 1.0d) _progressBar.setValue(current + incr);
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
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public static class VaadinExecutionFinishedEvent<T> 
				extends EventObject {
		
		private static final long serialVersionUID = 460949189812864921L;
		
		@Getter private final T _result;
		
		public VaadinExecutionFinishedEvent(final Component source,
										    final T result) {
			super(source);
			_result = result;
		}
	}
	public interface VaadinExecutionFinishedListener<T> 
			 extends EventListener {
		public void onExecutionFinished(final VaadinExecutionFinishedEvent<T> event);
	}
}