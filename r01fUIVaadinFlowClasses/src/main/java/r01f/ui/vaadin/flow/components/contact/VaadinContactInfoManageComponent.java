package r01f.ui.vaadin.flow.components.contact;

import java.util.Collection;

import com.google.common.collect.Lists;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;

import r01f.locale.Language;
import r01f.types.contact.ContactMeanType;
import r01f.ui.i18n.UII18NService;
import r01f.ui.vaadin.flow.annotations.VaadinViewField;
import r01f.ui.vaadin.flow.components.contact.email.VaadinContactEMailManage;
import r01f.ui.vaadin.flow.components.contact.nora.VaadinNORAContactComponent;
import r01f.ui.vaadin.flow.components.contact.nora.VaadinNORAContactFormPresenter;
import r01f.ui.vaadin.flow.components.contact.phone.VaadinContactPhoneManage;
import r01f.ui.vaadin.flow.components.contact.socialnetwork.VaadinContactSocialNetworkManage;
import r01f.ui.vaadin.flow.components.contact.website.VaadinContactWebSiteManage;
import r01f.ui.vaadin.flow.components.form.VaadinFormEditsViewObject;
import r01f.ui.vaadin.flow.components.form.VaadinViewTracksChanges;
import r01f.ui.vaadin.flow.components.layout.VaadinVerticalLayoutWithCaption;
import r01f.ui.vaadin.flow.i18n.VaadinI18NMessagesCanBeUpdated;
import r01f.util.types.locale.Languages;

/**
 * A configurable [contact info] component like:
 * <pre>
 *    +=============================================+
 *	  | +-----------------------------------------+ |
 *	  | | EMails Component						  | |
 *	  | +-----------------------------------------+ |
 *	  | +-----------------------------------------+ |
 *	  | | Phones Component						  | |
 *	  | +-----------------------------------------+ |
 *	  | +-----------------------------------------+ |
 *	  | | Social Networks Component			      | |
 *	  | +-----------------------------------------+ |
 *	  | +-----------------------------------------+ |
 *	  | | Web Sites Component					  | |
 *	  | +-----------------------------------------+ |
 *	  | +-----------------------------------------+ |
 *	  | | Prefered language combo				  | |
 *	  | +-----------------------------------------+ |
 *	  +=============================================+
 * </pre>
 */
public class VaadinContactInfoManageComponent
	 extends VerticalLayout
  implements VaadinFormEditsViewObject<VaadinViewContactInfo>,
  			 VaadinViewTracksChanges,
  			 VaadinI18NMessagesCanBeUpdated {

	private static final long serialVersionUID = -543903229607808643L;
/////////////////////////////////////////////////////////////////////////////////////////
//	SERVICES
/////////////////////////////////////////////////////////////////////////////////////////
	private final transient UII18NService _i18n;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	protected final Collection<ContactMeanType> _allowedMediumTypes;
	protected final VaadinNORAContactComponent _noraComponent;
	//private final TinyMCETextFieldComponent _txtGeoPosition;
	protected final VaadinContactEMailManage _emailsComponent;
	protected final VaadinContactPhoneManage _phonesComponent;
	protected final VaadinContactSocialNetworkManage _socialNetworksComponent;
	protected final VaadinContactWebSiteManage _webSitesComponent;

	@VaadinViewField(bindToViewObjectFieldNamed=VaadinViewContactInfo.PREFERED_LANGUAGE_CHANNELS_FIELD,
					 bindStringConverter=false,
					 required=true)
	protected final Select<Language> _cmbPreferedLanguage = new Select<>();
	
	private boolean _changed = false;
	

/////////////////////////////////////////////////////////////////////////////////////////
//	BINDED OBJECT
/////////////////////////////////////////////////////////////////////////////////////////
//	/**
//	 * This field ONLY is set when calling #bindViewTo
//	 * and is ONLY readed when calling #getViewObject
//	 */
//	private VaadinViewContactInfo _viewObject;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinContactInfoManageComponent(final UII18NService i18n) {
		this(i18n,
			 Lists.newArrayList(ContactMeanType.values()));
	}
	public VaadinContactInfoManageComponent(final UII18NService i18n,
										    final Collection<ContactMeanType> types) {
		this(i18n, types, null);
	}
	public VaadinContactInfoManageComponent(final UII18NService i18n,
										    final Collection<ContactMeanType> types,
										    final VaadinNORAContactFormPresenter noraPresenter) {
		_i18n = i18n;
		_allowedMediumTypes = types;

		
		
		_emailsComponent = types.contains(ContactMeanType.EMAIL) ? new VaadinContactEMailManage(i18n) : null;
		_phonesComponent = types.contains(ContactMeanType.PHONE) ? new VaadinContactPhoneManage(i18n) : null;
		_socialNetworksComponent = types.contains(ContactMeanType.SOCIAL_NETWORK) ? new VaadinContactSocialNetworkManage(i18n) : null;
		_webSitesComponent = types.contains(ContactMeanType.WEB_SITE) ? new VaadinContactWebSiteManage(i18n) : null;

		// preferred language
		_cmbPreferedLanguage.setWidth(100,Unit.PERCENTAGE);
		_cmbPreferedLanguage.setItems(Language.values());
		_cmbPreferedLanguage.setReadOnly(false);
		_cmbPreferedLanguage.setLabel(i18n.getMessage("contact.preferredLanguage"));
		_cmbPreferedLanguage.setItemLabelGenerator(lang -> Languages.nameUsing(i18n)
															  		.of(lang));

		////////// Layout
		// geo position
		_noraComponent = new VaadinNORAContactComponent(i18n, noraPresenter);
		_noraComponent.setLabel(i18n.getMessage("geo.address"));
		if (noraPresenter != null) {
			this.add(_noraComponent);
		}
		
		VaadinVerticalLayoutWithCaption vlContact = new VaadinVerticalLayoutWithCaption();
		vlContact.setLabel(i18n.getMessage("contact"));
		vlContact.setMargin(false);
		if (_emailsComponent != null) 			vlContact.add(_emailsComponent);
		if (_phonesComponent != null) 			vlContact.add(_phonesComponent);
		if (_socialNetworksComponent != null) 	vlContact.add(_socialNetworksComponent);
		if (_webSitesComponent != null) 		vlContact.add(_webSitesComponent);
		
		// others
		VaadinVerticalLayoutWithCaption vlOthers = new VaadinVerticalLayoutWithCaption(_cmbPreferedLanguage);
		vlOthers.setLabel(i18n.getMessage("contact.others").toUpperCase());
		vlOthers.setMargin(true);

		this.add(vlContact);
		this.add(vlOthers);
		
		_cmbPreferedLanguage.addValueChangeListener(prefLang -> setViewDataChanged(true));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Binding
/////////////////////////////////////////////////////////////////////////////////////////
	////////// [viewObject] > [UI control] --------------
	@Override
	public void editViewObject(final VaadinViewContactInfo viewObj) {
		// Set the [ui control] values from [view object]'s properties
		if (viewObj == null) throw new IllegalArgumentException("Cannot bind a null object!");

		// bind the individual components to the [view object] underlying collection
		_noraComponent.setValue(viewObj.getViewGeoPosition());
		_emailsComponent.setItems(viewObj.getViewContactMails());
		_phonesComponent.setItems(viewObj.getViewContactPhones());
		_socialNetworksComponent.setItems(viewObj.getViewContactSocialNetworks());
		_webSitesComponent.setItems(viewObj.getViewContactWebSites());
		if (viewObj.getWrappedModelObject().getPreferedLanguage() != null) {
			_cmbPreferedLanguage.setValue(viewObj.getPreferredLanguage());
		}
		
	}
	////////// [UI control] > [viewObject] --------------
	@Override
	public void writeAsDraftEditedViewObjectTo(final VaadinViewContactInfo viewObj) {
		// ensure the binded [view object] is updated
		viewObj.setViewGeoPosition(_noraComponent.getValue());
		viewObj.setViewContactMails(_emailsComponent.getItems());
		viewObj.setViewContactPhones(_phonesComponent.getItems());
		viewObj.setViewContactSocialNetworks(_socialNetworksComponent.getItems());
		viewObj.setViewContactWebSites(_webSitesComponent.getItems());
		viewObj.setPreferredLanguage(_cmbPreferedLanguage.getValue());
	}
	@Override
	public boolean writeIfValidEditedViewObjectTo(final VaadinViewContactInfo viewObj) {
		this.writeAsDraftEditedViewObjectTo(viewObj);
		return true;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void updateI18NMessages(final UII18NService i18n) {
		_noraComponent.setLabel(i18n.getMessage("geo.address"));
		if (_emailsComponent != null) _emailsComponent.updateI18NMessages(i18n);
		if (_phonesComponent != null) _phonesComponent.updateI18NMessages(i18n);
		if (_socialNetworksComponent != null) _socialNetworksComponent.updateI18NMessages(i18n);
		if (_webSitesComponent != null) _webSitesComponent.updateI18NMessages(i18n);
	}
	
	@Override
	public void setEnabled(final boolean enabled) {
		if (!enabled) {
			_emailsComponent.setEnabled(false);			
			_emailsComponent.setItems(null); 
			_phonesComponent.setEnabled(false);
			_phonesComponent.setItems(null);
			_socialNetworksComponent.setEnabled(false);
			_socialNetworksComponent.setItems(null);
			_webSitesComponent.setEnabled(false);
			_webSitesComponent.setItems(null);
		} else {
			_emailsComponent.setEnabled(true); 
			_phonesComponent.setEnabled(true);
			_socialNetworksComponent.setEnabled(true);
			_webSitesComponent.setEnabled(true);
		}
		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CHANGES
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void setViewDataChanged(final boolean changed) {
		_changed = changed;
		
	}
	@Override
	public boolean hasViewDataChanged() {
		//System.out.println("_noraComponent ----> " +_noraComponent.hasViewDataChanged());
		//System.out.println("_emailsComponent ----> " +_emailsComponent.hasViewDataChanged());
		//System.out.println("_phonesComponent ----> " +_phonesComponent.hasViewDataChanged());
		//System.out.println("_socialNetworksComponent ----> " +_socialNetworksComponent.hasViewDataChanged());
		//System.out.println("_webSitesComponent ----> " +_webSitesComponent.hasViewDataChanged());
		final boolean changed =  _noraComponent.hasViewDataChanged() || 
								 _emailsComponent.hasViewDataChanged() ||
								 _phonesComponent.hasViewDataChanged() ||
								 _socialNetworksComponent.hasViewDataChanged() ||
								 _webSitesComponent.hasViewDataChanged() ||
								 _changed;
		//reset ViewDataChanged value
		_emailsComponent.setViewDataChanged(false);
		_phonesComponent.setViewDataChanged(false);
		_socialNetworksComponent.setViewDataChanged(false);
		_webSitesComponent.setViewDataChanged(false);
		_changed = false;
		//System.out.println("contactinfo changed ----> " +changed);
		return changed;
	}
}
