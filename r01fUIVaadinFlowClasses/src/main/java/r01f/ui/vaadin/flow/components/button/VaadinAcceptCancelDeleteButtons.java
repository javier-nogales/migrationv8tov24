package r01f.ui.vaadin.flow.components.button;

import java.util.stream.Stream;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import lombok.experimental.Accessors;
import r01f.locale.I18NKey;
import r01f.ui.i18n.UII18NService;
import r01f.util.types.collections.CollectionUtils;

/**
 * An [accept] [cancel] [delete] buttons layout with a confirmation 
 * <pre class='brush:java'>
 *    	+============================+  <-- a vertical layout (container)
 *    	| +------------------------+ |
 *    	| |[accept][cancel][delete]|<------ an horizontal layout
 *    	| +------------------------+ |
 *    	+============================+
 * </pre>
 */
@Accessors( prefix="_" ) 
public class VaadinAcceptCancelDeleteButtons
	 extends Composite<VerticalLayout> {

	private static final long serialVersionUID = 5892967547409836937L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	protected final Button _btnAccept = new Button();
	protected final Button _btnCancel = new Button();
	protected final Button _btnDelete = new Button();
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinAcceptCancelDeleteButtons(final UII18NService i18n) {
		////////// Create components
		// accept
		_btnAccept.setText(i18n.getMessage("save"));
		_btnAccept.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		// cancel
		_btnCancel.setText(i18n.getMessage("cancel"));

		// delete
		_btnDelete.setText(i18n.getMessage("delete"));
		_btnDelete.addThemeVariants(ButtonVariant.LUMO_ERROR);

		////////// composition
		HorizontalLayout hlyButtons = new HorizontalLayout(_btnDelete,_btnCancel,_btnAccept);
		this.getContent()
			.add(new VerticalLayout(hlyButtons));	// wrap the horizontal layout inside a VerticalLayout
													// since types extending this might add more components
													// ie: add a confirmation layer
	}
	public VaadinAcceptCancelDeleteButtons(final UII18NService i18n,
										   final I18NKey acceptButtonCaptionKey,
										   final I18NKey cancelButtonCaptionKey,
										   final I18NKey deleteButtonCaptionKey) {
		this(i18n);
		if (acceptButtonCaptionKey != null) _btnAccept.setText(i18n.getMessage(acceptButtonCaptionKey));
		if (cancelButtonCaptionKey != null) _btnCancel.setText(i18n.getMessage(cancelButtonCaptionKey));
		if (deleteButtonCaptionKey != null) _btnDelete.setText(i18n.getMessage(deleteButtonCaptionKey));
	}
	protected void _setBehavior() {
		// nothing
	}
	protected HorizontalLayout _getAcceptCancelDeleteButtonsLayout() {
		return (HorizontalLayout)this.getContent().getComponentAt(0);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public void addCancelButtonClickListner(final ComponentEventListener<ClickEvent<Button>> clickListener) {
		_btnCancel.addClickListener(clickListener);
	}
	public void addAcceptButtonClickListner(final ComponentEventListener<ClickEvent<Button>> clickListener) {
		_btnAccept.addClickListener(clickListener);
	}
	public void addDeleteButtonClickListner(final ComponentEventListener<ClickEvent<Button>> clickListener) {
		_btnDelete.addClickListener(clickListener);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public void setCreatingNewRecordStatus() {
		_btnDelete.setVisible(false);
	}
	public void setEditingExistingRecordStatus() {
		_btnDelete.setVisible(true);
	}
	public void cancel() {
		_btnCancel.click();		// simulate the click event
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	VISIBLE                                                                         
/////////////////////////////////////////////////////////////////////////////////////////
	public void setAcceptButtonVisible(final boolean visible) {
		_btnAccept.setVisible(visible);
	}
	public boolean isAcceptButtonVisible() {
		return _btnAccept.isVisible();
	}
	public void setCancelButtonVisible(final boolean visible) {
		_btnCancel.setVisible(visible);
	}
	public boolean isCancelButtonVisible() {
		return _btnCancel.isVisible();
	}
	public void setDeleteButtonVisible(final boolean visible) {
		_btnDelete.setVisible(visible);
	}
	public boolean isDeleteButtonVisible() {
		return _btnDelete.isVisible();
	}
	public void setButtonsVisibleStatus(final boolean visible,
										final VaadinAcceptCancelDeleteButton... button) {
		if (CollectionUtils.isNullOrEmpty(button)) return;
		Stream.of(button)
			  .forEach(btn -> {
				  			switch (btn) {
							case ACCEPT:
								this.setAcceptButtonVisible(visible);
								break;
							case CANCEL:
								this.setCancelButtonVisible(visible);
								break;
							case DELETE:
								this.setDeleteButtonVisible(visible);
								break;
							default:
								throw new IllegalArgumentException();
				  			}
			  		   });
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ENABLE                                                                         
/////////////////////////////////////////////////////////////////////////////////////////
	public void setAcceptButtonEnabled(final boolean enabled) {
		_btnAccept.setEnabled(enabled);
	}
	public boolean isAcceptButtonEnabled() {
		return _btnAccept.isEnabled();
	}
	public void setCancelButtonEnabled(final boolean enabled) {
		_btnCancel.setEnabled(enabled);
	}
	public boolean isCancelButtonEnabled() {
		return _btnCancel.isEnabled();
	}
	public void setDeleteButtonEnabled(final boolean enabled) {
		_btnDelete.setEnabled(enabled);
	}
	public boolean isDeleteButtonEnabled() {
		return _btnDelete.isEnabled();
	}
	public void setButtonsEnableStatus(final boolean enabled,
									   final VaadinAcceptCancelDeleteButton... button) {
		if (CollectionUtils.isNullOrEmpty(button)) return;
		Stream.of(button)
			  .forEach(btn -> {
				  			switch (btn) {
							case ACCEPT:
								this.setAcceptButtonEnabled(enabled);
								break;
							case CANCEL:
								this.setCancelButtonEnabled(enabled);
								break;
							case DELETE:
								this.setDeleteButtonEnabled(enabled);
								break;
							default:
								throw new IllegalArgumentException();
				  			}
			  		   });
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public void setButtonId(final VaadinAcceptCancelDeleteButton btn, final String id) {
		switch (btn) {
		case ACCEPT:
			_btnAccept.setId(id);
			break;
		case CANCEL:
			_btnCancel.setId(id);
			break;
		case DELETE:
			_btnDelete.setId(id);
			break;
		default:
			throw new IllegalArgumentException();
		}
	}
	public enum VaadinAcceptCancelDeleteButton {
		ACCEPT,
		CANCEL,
		DELETE;
	}
}
