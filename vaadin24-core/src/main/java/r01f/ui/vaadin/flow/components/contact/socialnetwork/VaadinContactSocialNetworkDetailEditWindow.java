package r01f.ui.vaadin.flow.components.contact.socialnetwork;

import r01f.locale.I18NKey;
import r01f.ui.i18n.UII18NService;
import r01f.ui.presenter.UIPresenterSubscriber;
import r01f.ui.vaadin.flow.components.form.VaadinDetailEditFormWindowBase;

public class VaadinContactSocialNetworkDetailEditWindow
     extends VaadinDetailEditFormWindowBase<VaadinViewContactSocialNetwork,
			 						   		VaadinContactSocialNetworkDetailEdit> {

	private static final long serialVersionUID = 2550850462553697426L;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinContactSocialNetworkDetailEditWindow(final UII18NService i18n) {
		super(i18n,
			  new VaadinContactSocialNetworkDetailEdit(i18n),	// form
			  VaadinViewContactSocialNetwork::new);				// view obj factory
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public I18NKey getNewItemCaptionI18NKey() {
		return I18NKey.named("contact.socialNetwork.new");
	}
	@Override
	public I18NKey getEditItemCaptionI18NKey() {
		return I18NKey.named("contact.socialNetwork.edit");
	}
	@Override
	public void forEditing(final VaadinViewContactSocialNetwork viewObj,
						   final UIPresenterSubscriber<VaadinViewContactSocialNetwork> saveSubscriber,
						   final UIPresenterSubscriber<VaadinViewContactSocialNetwork> deleteSubscriber) {
		super.forEditing(viewObj, saveSubscriber, null);
	}
}
