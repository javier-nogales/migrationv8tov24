package r01ui.base.components.button;

import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import r01f.locale.I18NKey;
import r01f.ui.i18n.UII18NService;
import r01f.ui.vaadin.view.VaadinViewI18NMessagesCanBeUpdated;

/**
 * A popup window 
 */
public class VaadinPopUpDeleteConfirm 
	 extends Window
  implements VaadinViewI18NMessagesCanBeUpdated{

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
		
		////////// Style
		_deleteOKButton.setStyleName(ValoTheme.BUTTON_DANGER);

		////////// Layout
		_txtMessage.setEnabled(false);
		_txtMessage.setWidthFull();
		_txtMessage.setStyleName("no-border");
		HorizontalLayout lyButtons = new HorizontalLayout(_btnPopUpClose,_deleteOKButton);

		VerticalLayout ly = new VerticalLayout(_txtMessage,lyButtons);
		ly.setComponentAlignment(lyButtons,Alignment.MIDDLE_RIGHT);
		
		this.setContent(ly);
		this.setModal(true);
		this.setDraggable(false);
		this.setResizable(false);
		this.setWindowMode(WindowMode.NORMAL);
		this.setWidth(20,Unit.PERCENTAGE);
		this.setStyleName("customWindowClass");
		this.setCaption("");
		
		_setBehavior();
		this.updateI18NMessages(i18n);
	}
	private void _setBehavior() {
		// if CANCEL, just close the window (no data is committed to the [view object])
		_btnPopUpClose.addClickListener(clickEvent -> {
											this.close();
										});
	}
	public void addDeleteButtonClickListener(final ClickListener clickListener) {
		// BEWARE!!! 	The click listener is attached to the [deletion confirm OK button]
		_deleteOKButton.addClickListener(clickEvent -> {	
												this.close();
												// just delegate to the listener
												clickListener.buttonClick(clickEvent);
										 });
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
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
		_btnPopUpClose.setCaption(i18n.getMessage("cancel"));
		_btnPopUpCancel.setCaption(i18n.getMessage("cancel"));
		_deleteOKButton.setCaption(i18n.getMessage("delete"));
	}
}
