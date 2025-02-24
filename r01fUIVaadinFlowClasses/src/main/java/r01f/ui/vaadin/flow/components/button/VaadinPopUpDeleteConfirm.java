package r01f.ui.vaadin.flow.components.button;


import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;

import r01f.locale.I18NKey;
import r01f.ui.i18n.UII18NService;
import r01f.ui.vaadin.flow.i18n.VaadinI18NMessagesCanBeUpdated;

/**
 * A popup window 
 */
public class VaadinPopUpDeleteConfirm 
	 extends Dialog
  implements VaadinI18NMessagesCanBeUpdated{

	private static final long serialVersionUID = -4456052328339135780L;
/////////////////////////////////////////////////////////////////////////////////////////
//	SERVICES
/////////////////////////////////////////////////////////////////////////////////////////
	protected final transient UII18NService _i18n;
/////////////////////////////////////////////////////////////////////////////////////////
//	UI
/////////////////////////////////////////////////////////////////////////////////////////
	protected final Button _btnPopUpClose = new Button();
	protected final Button _btnPopUpCancel = new Button();
	protected final Button _deleteOKButton = new Button();
	protected final TextArea _txtMessage = new TextArea();
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinPopUpDeleteConfirm(final UII18NService i18n) {
		_i18n = i18n;
		
		////////// Layout
		// body
		_txtMessage.setEnabled(false);
		_txtMessage.setWidthFull();
		this.add(_txtMessage);
		
		// footer
		HorizontalLayout lyButtons = new HorizontalLayout(_btnPopUpClose,_deleteOKButton);
		this.getFooter()
			.add(lyButtons);

		////////// Style
		_deleteOKButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
		this.setModal(true);
		this.setDraggable(false);
		this.setResizable(false);
		this.setWidth(20,Unit.PERCENTAGE);
		
		////////// Behavior & i18n
		_setBehavior();
		this.updateI18NMessages(i18n);
	}
	private void _setBehavior() {
		// if CANCEL, just close the window (no data is committed to the [view object])
		_btnPopUpClose.addClickListener(clickEvent -> {
											this.close();
										});
	}
	public void addDeleteButtonClickListener(final ComponentEventListener<ClickEvent<Button>> clickListener) {
		// BEWARE!!! 	The click listener is attached to the [deletion confirm OK button]
		_deleteOKButton.addClickListener(clickEvent -> {	
												this.close();
												// just delegate to the listener
												clickListener.onComponentEvent(clickEvent);
										 });
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SHOW HIDE
/////////////////////////////////////////////////////////////////////////////////////////
	public void show() {
		this.setVisible(true);
	}
	public void hide() {
		this.setVisible(false);
	}
	public void toggleVisible() {
		this.setVisible(!this.isVisible());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TEXT
/////////////////////////////////////////////////////////////////////////////////////////	
	public void setText(final String text) {
		_txtMessage.setValue(text);
	}
	public void setText(final I18NKey textI18NKey) {
		_txtMessage.setValue(_i18n.getMessage(textI18NKey));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	I18N
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void updateI18NMessages(final UII18NService i18n) {
		_btnPopUpClose.setText(i18n.getMessage("cancel"));
		_btnPopUpCancel.setText(i18n.getMessage("cancel"));
		_deleteOKButton.setText(i18n.getMessage("delete"));
	}
}
