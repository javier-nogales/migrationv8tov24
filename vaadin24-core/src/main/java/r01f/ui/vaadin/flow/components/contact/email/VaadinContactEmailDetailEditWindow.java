package r01f.ui.vaadin.flow.components.contact.email;

import r01f.locale.I18NKey;
import r01f.ui.i18n.UII18NService;
import r01f.ui.presenter.UIPresenterSubscriber;
import r01f.ui.vaadin.flow.components.form.VaadinDetailEditFormWindowBase;

public class VaadinContactEmailDetailEditWindow
     extends VaadinDetailEditFormWindowBase<VaadinViewContactEmail,
			 						    	VaadinContactEmailDetailEdit> {

	private static final long serialVersionUID = 5765469965554072345L;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinContactEmailDetailEditWindow(final UII18NService i18n) {
		super(i18n,
			  new VaadinContactEmailDetailEdit(i18n),
			  VaadinViewContactEmail::new);				// view obj factory
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public I18NKey getNewItemCaptionI18NKey() {
		return I18NKey.named("contact.email.new");
	}
	@Override
	public I18NKey getEditItemCaptionI18NKey() {
		return I18NKey.named("contact.email.edit");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void forEditing(final VaadinViewContactEmail viewObj,
						   final UIPresenterSubscriber<VaadinViewContactEmail> saveSubscriber,
						   final UIPresenterSubscriber<VaadinViewContactEmail> deleteSubscriber) {
		super.forEditing(viewObj, saveSubscriber, null);
	}
}
