package r01f.ui.vaadin.flow.components.contact.phone;

import r01f.locale.I18NKey;
import r01f.ui.i18n.UII18NService;
import r01f.ui.presenter.UIPresenterSubscriber;
import r01f.ui.vaadin.flow.components.form.VaadinDetailEditFormWindowBase;

public class VaadinContactPhoneDetailEditWindow
     extends VaadinDetailEditFormWindowBase<VaadinViewContactPhone,
			 					 	   		VaadinContactPhoneDetailEdit> {
	private static final long serialVersionUID = -8502135692216603837L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinContactPhoneDetailEditWindow(final UII18NService i18n) {
		super(i18n,
			  new VaadinContactPhoneDetailEdit(i18n),
			  VaadinViewContactPhone::new);				// view obj factory
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public I18NKey getNewItemCaptionI18NKey() {
		return I18NKey.named("contact.phone.new");
	}
	@Override
	public I18NKey getEditItemCaptionI18NKey() {
		return I18NKey.named("contact.phone.edit");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void forEditing(final VaadinViewContactPhone viewObj,
						   final UIPresenterSubscriber<VaadinViewContactPhone> saveSubscriber,
						   final UIPresenterSubscriber<VaadinViewContactPhone> deleteSubscriber) {
		super.forEditing(viewObj, saveSubscriber, null);
	}
}
