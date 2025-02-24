package r01f.ui.vaadin.flow.components.button;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import r01f.ui.i18n.UII18NService;
import r01f.ui.vaadin.flow.components.window.VaadinProceedGateDialogWindow.VaadinProceedPuzzleCheck;

/**
 * An [accept] [cancel] [delete] buttons layout with a confirmation 
 * <pre class='brush:java'>
 *    	+============================+  <-- a vertical layout (container)
 *    	| +------------------------+ |
 *    	| |[accept][cancel][delete]|<------ an horizontal layout
 *    	| +------------------------+ |
 *    	| +------------------------+ |
 *    	| |     confirmation       | |
 *    	| +------------------------+ |
 *    	+============================+
 * </pre>
 */
public class VaadinAcceptCancelDeleteButtonsWithDeleteConfirmation 
	 extends VaadinAcceptCancelDeleteButtons {

	private static final long serialVersionUID = 5991415152756687476L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final VaadinProceedPuzzleCheck _puzzleCheck;
	private final VaadinPopUpDeleteConfirm _popUpDelConfirm;
	
	// Delete confirmation
	private final TextField _txtToCheckDeletion = new TextField();
	private final Button _btnDelOK = new Button();
	private final Button _btnDelCancel = new Button();
/////////////////////////////////////////////////////////////////////////////////////////
//	STATUS (avoid as much as possible)
/////////////////////////////////////////////////////////////////////////////////////////	
	private boolean _requireDeleteConfirmPopUp = false;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public VaadinAcceptCancelDeleteButtonsWithDeleteConfirmation(final UII18NService i18n,
																 final VaadinProceedPuzzleCheck puzzleCheck) {
		super(i18n);
		_puzzleCheck = puzzleCheck;
		_popUpDelConfirm = new VaadinPopUpDeleteConfirm(i18n);
		
		// Delete confirm
		// 		[..text] [OK][cancel]
		HorizontalLayout lyDelConfirm = _createDelConfirmLayout(i18n);
		
		// layout
		VerticalLayout lyContainer = this.getContent();
		lyContainer.add(lyDelConfirm);
		
		// Behavior
		_setBehavior();
	}
	private HorizontalLayout _createDelConfirmLayout(final UII18NService i18n) {
		_btnDelOK.setEnabled(false);
		_btnDelOK.setIcon(new Icon(VaadinIcon.ARROW_RIGHT));
		_btnDelOK.addThemeVariants(ButtonVariant.LUMO_ERROR);

		_btnDelCancel.setIcon(new Icon(VaadinIcon.ARROW_CIRCLE_LEFT));

		_txtToCheckDeletion.setSizeFull();
		
		// Layout
		HorizontalLayout lyDelConfirm = new HorizontalLayout(_txtToCheckDeletion,_btnDelOK,_btnDelCancel);
		lyDelConfirm.setSizeFull();
		lyDelConfirm.setVisible(false);	// not visible by default
		
		return lyDelConfirm;
	}
	@Override
	protected void _setBehavior() {
		super._setBehavior();
		
		// pluzzle: when the text to be checked changes, check if it puzzle is correct
		// 			... so the OK button can be activated
		_txtToCheckDeletion.addValueChangeListener(changeEvent -> {	
														// enable the [delete ok] button if the puzzle is correct
														boolean delPuzzleOK = _puzzleCheck.check(changeEvent.getValue());
														_btnDelOK.setEnabled(delPuzzleOK);
												   });
		_btnDelCancel.addClickListener(clickEvent -> this.exitDeletionCheckingMode());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DELETE CONFIRM MESSAGE
/////////////////////////////////////////////////////////////////////////////////////////
	public void setDeleteConfirmMessage(final String text) {
		_popUpDelConfirm.setText(text);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DELETE BUTTON
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void addDeleteButtonClickListner(final ComponentEventListener<ClickEvent<Button>> clickListener) {
		if (_requireDeleteConfirmPopUp) {
			_btnDelete.addClickListener(clickEvent -> {
											// wrap the click event and call #exitDeletionCheckingMode()
											// BEFORE executing the event
											_popUpDelConfirm.addDeleteButtonClickListener(clickEvent2 -> {																									
																								this.exitDeletionCheckingMode();
																								clickListener.onComponentEvent(clickEvent2);
																						  });
											_popUpDelConfirm.show();
									  });
			_btnDelOK.addClickListener(clickEvent -> {	
											// wrap the click event and call #exitDeletionCheckingMode()
											// BEFORE executing the event
											this.exitDeletionCheckingMode();
											clickListener.onComponentEvent(clickEvent);
										});
		} else {
			super.addDeleteButtonClickListner(clickListener);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	protected HorizontalLayout _getDeleteConfirmLayout() {
		// remember that the layout is like:
		//		+============================+
		//		| +------------------------+ |
		//		| |[accept][cancel][delete]| |
		//		| +------------------------+ |
		//		| +------------------------+ |
		//		| |     confirmation       | |
		//		| +------------------------+ |
		//		+============================+
		return (HorizontalLayout)this.getContent().getComponentAt(1);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public void enterDeletionCheckingMode() {
		_getAcceptCancelDeleteButtonsLayout().setVisible(false);	// the first HorizontalLayout for the [accept] [cancel] [delete] buttons
		_getDeleteConfirmLayout().setVisible(true);					// the second HorizontalLayout for the [..text] [OK][cancel] buttons
	}
	public void exitDeletionCheckingMode() {
		_txtToCheckDeletion.setValue("");	// clear the previous puzzle text

		_getAcceptCancelDeleteButtonsLayout().setVisible(true);	// the first HorizontalLayout for the [accept] [cancel] [delete] buttons
		_getDeleteConfirmLayout().setVisible(false);			// the second HorizontalLayout for the [..text] [OK][cancel] buttons
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CONFIRMATION
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isRequiringDeleteConfirmation() {
		return _requireDeleteConfirmPopUp;
	}
	public void setRequireDeleteConfirmation(final boolean require) {
		_requireDeleteConfirmPopUp = require;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
}
