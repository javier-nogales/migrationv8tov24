package r01f.ui.vaadin.flow.components.contact.socialnetwork;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

import r01f.types.contact.ContactSocialNetworkType;
import r01f.ui.i18n.UII18NService;
import r01f.ui.vaadin.flow.annotations.VaadinViewComponentLabels;
import r01f.ui.vaadin.flow.annotations.VaadinViewField;
import r01f.ui.vaadin.flow.components.contact.VaadinContactMeanDetailEditBase;

public class VaadinContactSocialNetworkDetailEdit
	 extends VaadinContactMeanDetailEditBase<VaadinViewContactSocialNetwork> {

	private static final long serialVersionUID = -3069365158386075376L;
/////////////////////////////////////////////////////////////////////////////////////////
//  UI
/////////////////////////////////////////////////////////////////////////////////////////
	@VaadinViewField(bindToViewObjectFieldNamed=VaadinViewContactSocialNetwork.TYPE_FIELD,
					 bindStringConverter=false,
					 required=true)
	@VaadinViewComponentLabels(captionI18NKey="contact.socialNetwork.type",useCaptionI18NKeyAsPlaceHolderKey=true)
	protected final ComboBox<ContactSocialNetworkType> _cmbType = new ComboBox<ContactSocialNetworkType>();

	@VaadinViewField(bindToViewObjectFieldNamed=VaadinViewContactSocialNetwork.USER_FIELD,required=true)
	@VaadinViewComponentLabels(captionI18NKey="contact.socialNetwork.user",useCaptionI18NKeyAsPlaceHolderKey=true)
	private final TextField _txtUser = new TextField();

	@VaadinViewField(bindToViewObjectFieldNamed=VaadinViewContactSocialNetwork.PROFILE_URL_FIELD,required=false)
	@VaadinViewComponentLabels(captionI18NKey="contact.socialNetwork.profile.url",useCaptionI18NKeyAsPlaceHolderKey=true)
	private final TextField _txtProfileUrl = new TextField();
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinContactSocialNetworkDetailEdit(final UII18NService i18n) {
		super(VaadinViewContactSocialNetwork.class,
			  i18n);
		// type
		_cmbType.setItems(ContactSocialNetworkType.values());
		_cmbType.setItemLabelGenerator(type -> type.nameUsing(i18n));

		// user
		_txtUser.setWidth(100,Unit.PERCENTAGE);
		_txtUser.setReadOnly(false);

		// profileUrl
		_txtProfileUrl.setWidth(100,Unit.PERCENTAGE);
		_txtProfileUrl.setReadOnly(false);

		// layout: DO NOT FORGET!
		super.add(new HorizontalLayout(_cmbUsage,_cmbType,_txtUser),
				  new HorizontalLayout(_txtProfileUrl),
				  new HorizontalLayout(_chkDefault,_chkPrivate));

		////////// Init the form components (DO NOT FORGET!!)
		_initFormComponents();
	}
}
