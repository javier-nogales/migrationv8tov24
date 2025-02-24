package r01ui.base.components.button;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import r01f.ui.i18n.UII18NService;
import r01ui.base.components.window.VaadinProceedGateDialogWindow.VaadinProceedPuzzleCheck;

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
		VerticalLayout lyContainer = _getContainerLayout(); 
		lyContainer.addComponent(lyDelConfirm);
		
		// Behavior
		_setBehavior();
	}
	private HorizontalLayout _createDelConfirmLayout(final UII18NService i18n) {
		_btnDelOK.setEnabled(false);
		_btnDelOK.setIcon(VaadinIcons.ARROW_RIGHT);
		_btnDelOK.setStyleName("danger");

		_btnDelCancel.setIcon(VaadinIcons.ARROW_CIRCLE_LEFT);

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
	public void addDeleteButtonClickListner(final ClickListener clickListener) {
		if (_requireDeleteConfirmPopUp) {
			_btnDelete.addClickListener(clickEvent -> {	
											_popUpDelConfirm.addDeleteButtonClickListener(clickEvent2 -> {																									
																								this.exitDeletionCheckingMode();
																								clickListener.buttonClick(clickEvent2);
																						  });
											UI.getCurrent()
											  .addWindow(_popUpDelConfirm);
									  });
			_btnDelOK.addClickListener(event -> {	
											this.exitDeletionCheckingMode();
											clickListener.buttonClick(event);
										});
		} else {
			super.addDeleteButtonClickListner(clickListener);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private HorizontalLayout _getAcceptCancelDeleteButtonsLayout() {
		return (HorizontalLayout)_getContainerLayout().getComponent(0);
	}
	private HorizontalLayout _getDeleteConfirmLayout() {
		return (HorizontalLayout)_getContainerLayout().getComponent(1);
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
