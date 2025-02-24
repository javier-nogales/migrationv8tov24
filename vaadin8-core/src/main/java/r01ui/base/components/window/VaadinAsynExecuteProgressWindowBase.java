package r01ui.base.components.window;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.securitycontext.SecurityContext;
import r01f.securitycontext.SecurityContextStoreAtThreadLocalStorage;

/**
 * A pop-up window where a lengthy operation is run in a background thread
 * Usage:
 * <pre class='brush:java'>
 * 		// [1] - Create the popup window
 * 		VaadinAsynExecuteProgressWindowBase winProgress = new VaadinAsynExecuteProgressWindowBase(_i18n,
 *																								  _executorService);	// do NOT use indeterminate progress (it'll be updated -see progress subscriber-)
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
 *		UISubscriber<Callable<InputStream>> resultSub = callable -> {
 *															// show a popup that will show a progress bar while the file is being generated
 *															winProgress.runTask(callable);
 *														};
 *		// call the presenter (remember that the CORE just returns a Callable<InputStream> which is scheduled by thw progress popup)
 *		_importExportPresenter.onGenerateFileRequested(...,
 *													   // progress subscriber > it's handed to the CORE and updated while the generation process progresses (if the CORE is async)
 *													   progressSubscriber,
 *													   // what do
 *													   resultSub);
 * </pre>
 * @param <T>
 */
@Slf4j
@Accessors(prefix = "_")
public abstract class VaadinAsynExecuteProgressWindowBase<T>
			  extends Window
		   implements FocusListener {

	private static final long serialVersionUID = -7308316232500994425L;
/////////////////////////////////////////////////////////////////////////////////////////
//	SERVICES
/////////////////////////////////////////////////////////////////////////////////////////
	protected final transient ExecutorService _executorService;
/////////////////////////////////////////////////////////////////////////////////////////
//	STATUS (avoid as possible)
/////////////////////////////////////////////////////////////////////////////////////////
	private ListenableFuture<T> _futureTask;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR / BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinAsynExecuteProgressWindowBase(final ExecutorService executorService) {
		this(null,	// window caption
			 executorService);
	}
	public VaadinAsynExecuteProgressWindowBase(final String winCaption,
											   final ExecutorService executorService) {
		////////// Services
		_executorService = executorService;

		////////// Window properties
		if (winCaption != null) this.setCaption(winCaption);
		this.setClosable(false);
		this.setResizable(false);
		this.center();

		// The only way to make [Progress Window] visible BEFORE the [lengthy process] is started: make the [progress window] MODAL
		// ... when it gets focus, ask server to launch lengthy job
		this.setModal(true);	// BEWARE!!! SetModal call will force focus on THIS window, then, focus will cause the lengthy operation to start
		this.addFocusListener(this);

		// behavior
		_setBehavior();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BEHAVIOR
/////////////////////////////////////////////////////////////////////////////////////////
	private void _setBehavior() {
		this.addCloseListener(event -> _cleanExit());
	}
	@Override
	public void focus(final FocusEvent event) {
		// nothing
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PUBLIC METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public void show() {
		UI.getCurrent().addWindow(this);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	RUN & CANCEL
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isTaskRunning() {
		return _futureTask != null
		    && !(_futureTask.isDone() || _futureTask.isCancelled());
	}
	public boolean isTaskDone() {
		return _futureTask != null && _futureTask.isDone();
	}
	public boolean isTaskCancelled() {
		return _futureTask != null && _futureTask.isCancelled();
	}
	public void cancelTaskIfRunning() {
		if (this.isTaskRunning()) {
			log.warn("[vaadin async exec progress win] > cancel task if running");
			_futureTask.cancel(true); 	// may be interrupted if running
		} else {
			log.warn("[vaadin async exec progress win] > NO running task: nothing to be cancelled");
		}
	}

	public void runTask(final Callable<T> lengthyOperation) {
		runTask(lengthyOperation,
				2000); // server push: pooling every 2sg
	}

	public void runTask(final Callable<T> lengthyOperation,
						final int intervalInMillis) {
		// processWasStarted flag to protect against multiple run
		if (this.isTaskDone() || this.isTaskCancelled()) {
			log.warn("[vaadin async exec progress win] > cannot run operation: there's another one in progress");
			return;
		}

		this.show();
		try {
			// get the UI, Session and SecurityContext from the MAIN thread
			// ... and "transfer" them to the new Thread started to run the
			//	   lenghty operation
			UI vaadinUI = UI.getCurrent();
			VaadinSession vaadinCurSession = VaadinSession.getCurrent();
			SecurityContext securityContext = SecurityContextStoreAtThreadLocalStorage.get();

			boolean pushEnabled = vaadinUI.getPushConfiguration().getPushMode().isEnabled();
			vaadinUI.setPollInterval(intervalInMillis);	// server push: pooling every intervalInMillis (default 200 sg)


			// Get an executor service
			ExecutorService executorService = _executorService != null ? _executorService
																	   : Executors.newSingleThreadExecutor();
			ListeningExecutorService listeningExecService = MoreExecutors.listeningDecorator(executorService);

			// create a NEW Thread where the lengthy operation will be run
			Callable<T> callable = () -> {
										// Setting vaadin current session and UI to allow
										// [cancel] button click, Vaadin notifications, etc...
										// THIS NEW Thread must have the SAME VaadinSession, UI and SecurityContext
										// as the MAIN Thread
										VaadinSession.setCurrent(vaadinCurSession);
										UI.setCurrent(vaadinUI);
										SecurityContextStoreAtThreadLocalStorage.set(securityContext);

										// start lengthy operation on THIS new thread
										return lengthyOperation.call();
								   };
			log.warn("[vaadin async exec progress win] > START async task");
			_futureTask = listeningExecService.submit(callable);
			Futures.addCallback(_futureTask,
								new FutureCallback<T>() {
									@Override
									public void onSuccess(final T result) {
										log.warn("[vaadin async exec progress win] > END async task");

										// open generate report if is not canceled
										vaadinUI.access(// runnable
														() -> {
															if (!_futureTask.isCancelled()) {
																_onSuccess(result);
															} else if (_futureTask.isCancelled()) {
																_onCancelled();
															} else {
																throw new IllegalStateException();
															}
															vaadinUI.setPollInterval(-1);	// cancel polling
															_cleanExit();
														});
									}
									@Override
									public void onFailure(final Throwable th) {
										log.warn("[vaadin async exec progress win] > ERROR in async task: {}",th.getMessage(),th);

										// Do reasonable things to handle failure
										// cancel task throws exception
										vaadinUI.access(() -> {
															if (!CancellationException.class.isAssignableFrom(th.getClass())) {
																_onFailure(th);
															} else {
																_onCancelled();
															}
															vaadinUI.setPollInterval(-1);	// cancel polling
															_cleanExit();
												   		});
									}
							    },
								// where the CALLBACK is executed
								listeningExecService);
		} catch (final Exception e) {
			_onFailure(e);
		}
	}
	protected void _cleanExit() {
		// cancel the task if already running
		this.cancelTaskIfRunning();
		this.cleanFutureTask();

		// cancel polling
		UI ui = this.getUI();
		if (ui != null)
		 {
			ui.setPollInterval(-1);	// cancel polling
		}

		// close this window
		this.close();
	}

	protected void cleanFutureTask() {
		_futureTask = null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * By default when the content is ready it'll be opened
	 * Overwrite to make something after process finish (ex: open file)
	 * @param streamResource
	 */
	@SuppressWarnings("unused")
	protected void _onSuccess(final T result) {
		log.info("[vaadin async exec progress win] > SUCCEEDED");
	}
	@SuppressWarnings("static-method")
	protected void _onCancelled() {
		log.info("[vaadin async exec progress win] > CANCELLED");
	}
	@SuppressWarnings("static-method")
	protected void _onFailure(final Throwable th) {
		log.info("[vaadin async exec progress win] > ERROR: {}",th.getMessage());
		Notification.show(th.getMessage());
		log.error("Error in thread process: {}",
				  th.getMessage(),th);
	}
}
