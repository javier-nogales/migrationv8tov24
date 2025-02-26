package r01ui.base.components.button;

import java.util.stream.Stream;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Composite;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout.MarginHandler;
import com.vaadin.ui.VerticalLayout;

import lombok.experimental.Accessors;
import r01f.locale.I18NKey;
import r01f.ui.i18n.UII18NService;
import r01f.util.types.collections.CollectionUtils;

/**
 * A layout of [accept] [cancel] and [delete] buttons like
 * <pre>
 * 		+-------------------------------------------+
 * 		| [Delete]                [Cancel] [Accept] |
 * 		+-------------------------------------------+
 * </pre>
 */
@Accessors( prefix="_" ) 
public class VaadinAcceptCancelDeleteButtons
	 extends Composite 
  implements MarginHandler {

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
		_btnAccept.setCaption(i18n.getMessage("save"));
		_btnAccept.setStyleName("primary");

		// cancel
		_btnCancel.setCaption(i18n.getMessage("cancel"));

		// delete
		_btnDelete.setCaption(i18n.getMessage("delete"));
		_btnDelete.setStyleName("danger");

		////////// layout
		HorizontalLayout lyButtons = _createButtonsLayout();
		
		VerticalLayout vLy = new VerticalLayout(lyButtons);
		vLy.setComponentAlignment(lyButtons,Alignment.MIDDLE_RIGHT);
		vLy.setSizeFull();

		////////// composition
		this.setCompositionRoot(vLy);
		this.setSizeFull();
	}
	public VaadinAcceptCancelDeleteButtons(final UII18NService i18n,
										   final I18NKey acceptButtonCaptionKey,
										   final I18NKey cancelButtonCaptionKey,
										   final I18NKey deleteButtonCaptionKey) {
		this(i18n);
		if (acceptButtonCaptionKey != null) _btnAccept.setCaption(i18n.getMessage(acceptButtonCaptionKey));
		if (cancelButtonCaptionKey != null) _btnCancel.setCaption(i18n.getMessage(cancelButtonCaptionKey));
		if (deleteButtonCaptionKey != null) _btnDelete.setCaption(i18n.getMessage(deleteButtonCaptionKey));
	}
	protected HorizontalLayout _createButtonsLayout() {
		HorizontalLayout lyButtons = new HorizontalLayout(_btnDelete,_btnCancel,_btnAccept);		
		return lyButtons;
	}
	protected void _setBehavior() {
		// nothing
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	protected VerticalLayout _getContainerLayout() {
		return (VerticalLayout)this.getCompositionRoot();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public void addCancelButtonClickListner(final ClickListener clickListener) {
		_btnCancel.addClickListener(clickListener);
	}
	public void addAcceptButtonClickListner(final ClickListener clickListener) {
		_btnAccept.addClickListener(clickListener);
	}
	public void addDeleteButtonClickListner(final ClickListener clickListener) {
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
//	MARGIN
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public void setMargin(final boolean enabled) {
		MarginHandler hly = (MarginHandler)this.getCompositionRoot();
		hly.setMargin(enabled);
	}
	@Override
	public void setMargin(final MarginInfo marginInfo) {
		MarginHandler hly = (MarginHandler)this.getCompositionRoot();
		hly.setMargin(marginInfo);
	}
	@Override
	public MarginInfo getMargin() {
		MarginHandler hly = (MarginHandler)this.getCompositionRoot();	
		return hly.getMargin();
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
