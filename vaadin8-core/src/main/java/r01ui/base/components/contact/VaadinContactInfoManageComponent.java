package r01ui.base.components.contact;

import java.util.Collection;

import com.google.common.collect.Lists;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.data.provider.DataProviderListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalLayout;

import r01f.locale.Language;
import r01f.types.contact.ContactMeanType;
import r01f.types.geo.GeoLocationItem;
import r01f.types.geo.GeoOIDs.GeoCountryID;
import r01f.types.geo.GeoOIDs.GeoCountyID;
import r01f.types.geo.GeoOIDs.GeoLocalityID;
import r01f.types.geo.GeoOIDs.GeoMunicipalityID;
import r01f.types.geo.GeoOIDs.GeoPortalID;
import r01f.types.geo.GeoOIDs.GeoStateID;
import r01f.types.geo.GeoOIDs.GeoStreetID;
import r01f.ui.i18n.UII18NService;
import r01f.ui.vaadin.annotations.VaadinViewField;
import r01f.ui.vaadin.styles.VaadinValoTheme;
import r01f.ui.vaadin.view.VaadinViewI18NMessagesCanBeUpdated;
import r01f.util.types.locale.Languages;
import r01ui.base.components.contact.email.VaadinContactEMailManage;
import r01ui.base.components.contact.email.VaadinViewContactEmail;
import r01ui.base.components.contact.nora.VaadinNORAContactComponent;
import r01ui.base.components.contact.nora.VaadinNORAContactFormPresenter;
import r01ui.base.components.contact.nora.VaadinNORAContactGeoPostion2DComponent.VaadinNORACoordsChangeListener;
import r01ui.base.components.contact.phone.VaadinContactPhoneManage;
import r01ui.base.components.contact.phone.VaadinViewContactPhone;
import r01ui.base.components.contact.socialnetwork.VaadinContactSocialNetworkManage;
import r01ui.base.components.contact.website.VaadinContactWebSiteManage;
import r01ui.base.components.form.VaadinFormEditsViewObject;
import r01ui.base.components.form.VaadinViewTracksChanges;

/**
 * A configurable [contact info] component like:
 * <pre>
 *    +++++++++++++++++++++++++++++++++++++++++++++++
 *	  + +-----------------------------------------+ +
 *	  + | EMails Component						  | +
 *	  + +-----------------------------------------+ +
 *	  + +-----------------------------------------+ +
 *	  + | Phones Component						  | +
 *	  + +-----------------------------------------+ +
 *	  + +-----------------------------------------+ +
 *	  + | Social Networks Component			      | +
 *	  + +-----------------------------------------+ +
 *	  + +-----------------------------------------+ +
 *	  + | Web Sites Component					  | +
 *	  + +-----------------------------------------+ +
 *	  + +-----------------------------------------+ +
 *	  + | Prefered language combo				  | +
 *	  + +-----------------------------------------+ +
 *	  +++++++++++++++++++++++++++++++++++++++++++++++
 * </pre>
 */
public class VaadinContactInfoManageComponent
	 extends VerticalLayout
  implements VaadinFormEditsViewObject<VaadinViewContactInfo>,
  			 VaadinViewTracksChanges,
  			 VaadinViewI18NMessagesCanBeUpdated {

	private static final long serialVersionUID = -543903229607808643L;
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
	private final UII18NService _i18n;

	@VaadinViewField(bindToViewObjectFieldNamed=VaadinViewContactInfo.PREFERED_LANGUAGE_CHANNELS_FIELD,
					 bindStringConverter=false,
					 required=false)
	protected final ComboBox<Language> _cmbPreferedLanguage = new ComboBox<>();
	
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
		_cmbPreferedLanguage.setTextInputAllowed(false);
		_cmbPreferedLanguage.setCaption(i18n.getMessage("contact.preferredLanguage"));
		_cmbPreferedLanguage.setItemCaptionGenerator(lang -> Languages.nameUsing(i18n)
															  		  .of(lang));
		_cmbPreferedLanguage.addStyleName(VaadinValoTheme.COMBO_MEDIUM_SIZE);

		////////// Layout
		// geo position
		_noraComponent = new VaadinNORAContactComponent(i18n, noraPresenter);
		_noraComponent.setCaption(i18n.getMessage("geo.address"));
		if (noraPresenter != null) {
			this.addComponent(_noraComponent);
		}
		
		VerticalLayout vlContact = new VerticalLayout();
		vlContact.setCaption(i18n.getMessage("contact"));
		vlContact.setMargin(false);
		vlContact.addStyleName(VaadinValoTheme.NO_PADDING_TOP);
		vlContact.addStyleName(VaadinValoTheme.NO_PADDING_LEFT);
		if (_emailsComponent != null) 			vlContact.addComponent(_emailsComponent);
		if (_phonesComponent != null) 			vlContact.addComponent(_phonesComponent);
		if (_socialNetworksComponent != null) 	vlContact.addComponent(_socialNetworksComponent);
		if (_webSitesComponent != null) 		vlContact.addComponent(_webSitesComponent);
		
		// others
		VerticalLayout vlOthers = new VerticalLayout(_cmbPreferedLanguage);
		vlOthers.setCaption(i18n.getMessage("contact.others").toUpperCase());
		vlOthers.setMargin(true);
		vlOthers.addStyleName(VaadinValoTheme.LAYOUT_WHITE_BORDERED);

		this.addComponent(vlContact);
		this.addComponent(vlOthers);
		this.addStyleName(VaadinValoTheme.NO_PADDING);
		
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
		if (_emailsComponent != null) _emailsComponent.setItems(viewObj.getViewContactMails());
		if (_phonesComponent != null) _phonesComponent.setItems(viewObj.getViewContactPhones());
		if (_socialNetworksComponent != null) _socialNetworksComponent.setItems(viewObj.getViewContactSocialNetworks());
		if (_webSitesComponent != null) _webSitesComponent.setItems(viewObj.getViewContactWebSites());
		if (viewObj.getWrappedModelObject().getPreferedLanguage() != null) {
			_cmbPreferedLanguage.setSelectedItem(viewObj.getPreferredLanguage());
		}
		// To avoid change to true due to listener at binding at the first read or after saving
		setAllComponentsViewDataChanged(false);
	}
	////////// [UI control] > [viewObject] --------------
	@Override
	public void writeAsDraftEditedViewObjectTo(final VaadinViewContactInfo viewObj) {
		// ensure the binded [view object] is updated
		viewObj.setViewGeoPosition(_noraComponent.getValue());
		if (_emailsComponent != null) viewObj.setViewContactMails(_emailsComponent.getItems());
		if (_phonesComponent != null) viewObj.setViewContactPhones(_phonesComponent.getItems());
		if (_socialNetworksComponent != null) viewObj.setViewContactSocialNetworks(_socialNetworksComponent.getItems());
		if (_webSitesComponent != null) viewObj.setViewContactWebSites(_webSitesComponent.getItems());
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
		_noraComponent.setCaption(i18n.getMessage("geo.address"));
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
		final boolean changed =  _noraComponent.hasViewDataChanged() || 
								 _emailsComponent.hasViewDataChanged() ||
								 _phonesComponent.hasViewDataChanged() ||
								 _socialNetworksComponent != null 
								 	&& _socialNetworksComponent.hasViewDataChanged() ||
							 	 _webSitesComponent != null
							 	 	&& _webSitesComponent.hasViewDataChanged() ||
								 _changed;
		return changed;
	}
	public void setAllComponentsViewDataChanged(final boolean changed) {
		//reset ViewDataChanged value
		setViewDataChanged(changed);
		_noraComponent.setViewDataChanged(changed);
		_emailsComponent.setViewDataChanged(changed);
		_phonesComponent.setViewDataChanged(changed);
		if (_socialNetworksComponent != null) {
			_socialNetworksComponent.setViewDataChanged(changed);
		}
		if (_webSitesComponent != null) {
			_webSitesComponent.setViewDataChanged(changed);
		}
	}

//////////////////////////////////////////////////////////////////////////////////////////
//	LISTENERS
/////////////////////////////////////////////////////////////////////////////////////////
	
	public void addTxtZipCodeChangeListener(final ValueChangeListener<String> listener) {
		_noraComponent.addTxtZipCodeChangeListener(listener);
	}
	
	public void addCmbCountryChangeListener(final ValueChangeListener<GeoLocationItem<GeoCountryID>> listener) {
		_noraComponent.addCmbCountryChangeListener(listener);
	}
	
	public void addCmbStateChangeListener(final ValueChangeListener<GeoLocationItem<GeoStateID>> listener) {
		_noraComponent.addCmbStateChangeListener(listener);
	}
	
	public void addTxtNotNormalizedStateChangeListener(final ValueChangeListener<String> listener) {
		_noraComponent.addTxtNotNormalizedStateChangeListener(listener);
	}
	
	public void addCmbCountyChangeListener(final ValueChangeListener<GeoLocationItem<GeoCountyID>> listener) {
		_noraComponent.addCmbCountyChangeListener(listener);
	}
	
	public void addTxtNotNormalizedCountyChangeListener(final ValueChangeListener<String> listener) {
		_noraComponent.addTxtNotNormalizedCountyChangeListener(listener);
	}
	
	public void addCmbMunicipalityChangeListener(final ValueChangeListener<GeoLocationItem<GeoMunicipalityID>> listener) {
		_noraComponent.addCmbMunicipalityChangeListener(listener);
	}
	
	public void addTxtNotNormalizedMunicipalityChangeListener(final ValueChangeListener<String> listener) {
		_noraComponent.addTxtNotNormalizedMunicipalityChangeListener(listener);
	}
	
	public void addCmbLocalityChangeListener(final ValueChangeListener<GeoLocationItem<GeoLocalityID>> listener) {
		_noraComponent.addCmbLocalityChangeListener(listener);
	}
	
	public void addTxtNotNormalizedLocalityChangeListener(final ValueChangeListener<String> listener) {
		_noraComponent.addTxtNotNormalizedLocalityChangeListener(listener);
	}
	
	public void addCmbStreetChangeListener(final ValueChangeListener<GeoLocationItem<GeoStreetID>> listener) {
		_noraComponent.addCmbStreetChangeListener(listener);
	}
	
	public void addTxtNotNormalizedStreetChangeListener(final ValueChangeListener<String> listener) {
		_noraComponent.addTxtNotNormalizedStreetChangeListener(listener);
	}
	
	public void addCmbPortalChangeListener(final ValueChangeListener<GeoLocationItem<GeoPortalID>> listener) {
		_noraComponent.addCmbPortalChangeListener(listener);
	}
	
	public void addTxtFloorChangeListener(final ValueChangeListener<String> listener) {
		_noraComponent.addTxtFloorChangeListener(listener);
	}
	
	public void addTxtNotNormalizedPortalChangeListener(final ValueChangeListener<String> listener) {
		_noraComponent.addTxtNotNormalizedPortalChangeListener(listener);
	}
	
	public void addCoordsChangeListener(final VaadinNORACoordsChangeListener listener) {
		_noraComponent.addCoordsChangeListener(listener);
	}
	
	public void addEmailsGridChangeListener(final DataProviderListener<VaadinViewContactEmail> listener) {
		_emailsComponent.addEmailsGridChangeListener(listener);
	}
	
	public void addPhonesGridChangeListener(final DataProviderListener<VaadinViewContactPhone> listener) {
		_phonesComponent.addPhonesGridChangeListener(listener);
	}
	
}
