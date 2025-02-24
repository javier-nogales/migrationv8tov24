package r01ui.base.components.contact.nora;

import java.util.stream.Stream;

import com.vaadin.data.Binder;
import com.vaadin.data.provider.CallbackDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.ejie.nora.NORAGeoIDs;
import r01f.ejie.nora.NORAService.NORANaming;
import r01f.locale.Language;
import r01f.types.geo.GeoLocationItem;
import r01f.types.geo.GeoOIDs.GeoCountryID;
import r01f.types.geo.GeoOIDs.GeoCountyID;
import r01f.types.geo.GeoOIDs.GeoIDBase;
import r01f.types.geo.GeoOIDs.GeoLocalityID;
import r01f.types.geo.GeoOIDs.GeoMunicipalityID;
import r01f.types.geo.GeoOIDs.GeoPortalID;
import r01f.types.geo.GeoOIDs.GeoRegionID;
import r01f.types.geo.GeoOIDs.GeoStateID;
import r01f.types.geo.GeoOIDs.GeoStreetID;
import r01f.types.geo.GeoPosition;
import r01f.types.geo.GeoPosition2D;
import r01f.types.geo.GeoStreet;
import r01f.ui.i18n.UII18NService;
import r01f.ui.presenter.UIPresenterSubscriber;
import r01f.ui.vaadin.annotations.LangIndependentVaadinViewField;
import r01f.ui.vaadin.annotations.VaadinViewComponentLabels;
import r01f.ui.vaadin.annotations.VaadinViewField;
import r01f.ui.vaadin.styles.VaadinValoTheme;
import r01f.ui.vaadin.view.VaadinView;
import r01f.ui.vaadin.view.VaadinViewI18NMessagesCanBeUpdated;
import r01f.ui.vaadin.view.VaadinViews;
import r01f.util.types.Strings;
import r01f.util.types.locale.Languages;
import r01ui.base.components.button.VaadinToggleButton;
import r01ui.base.components.contact.nora.VaadinNORAContactGeoPostion2DComponent.VaadinNORACoordsChangeListener;
import r01ui.base.components.form.VaadinFormEditsViewObject;
import r01ui.base.components.form.VaadinViewTracksChanges;

/**
 * A form used to edit a geo position
 * <pre>
 * 		+==================================================+
 * 		|      Country [                               \/] |
 * 		|        State [                               \/] |
 * 		|       County [                               \/] |
 * 		| Municipality [                               \/] |
 * 		|     Locality [                               \/] |
 * 		|       Street [                               \/] |
 * 		|       Portal [                               \/] |
 * 		|     ZIP Code [                               \/] |
 * 		+==================================================+
 * </pre>
 */
@Accessors(prefix="_")
public class VaadinNORAContactForm 
     extends CustomField<VaadinViewGeoPosition>
  implements VaadinView,
  			 VaadinFormEditsViewObject<VaadinViewGeoPosition>,
  			 VaadinViewTracksChanges,
  			 VaadinViewI18NMessagesCanBeUpdated {

	private static final long serialVersionUID = -7975207266889688539L;
/////////////////////////////////////////////////////////////////////////////////////////
//  SERVICES
/////////////////////////////////////////////////////////////////////////////////////////
	private final transient UII18NService _i18n;
	private final transient VaadinNORAContactFormPresenter _presenter;
/////////////////////////////////////////////////////////////////////////////////////////
//	UI
/////////////////////////////////////////////////////////////////////////////////////////
	////////// Country
	@VaadinViewField(bindToViewObjectFieldNamed=VaadinViewGeoPosition.COUNTRY_FIELD,
					 bindStringConverter=false,required=false)
	@LangIndependentVaadinViewField
	@VaadinViewComponentLabels(captionI18NKey="geo.country",
							   useCaptionI18NKeyAsPlaceHolderKey=true)
	@Getter @Setter private VaadinNoraGeoLocationItemField<GeoCountryID> _cmbCountry = new VaadinNoraGeoLocationItemField<>();
	private Registration _cmbCountryRegistration;
	
	////////// State
	@VaadinViewField(bindToViewObjectFieldNamed=VaadinViewGeoPosition.STATE_FIELD,
					 bindStringConverter=false,required=false)
	@LangIndependentVaadinViewField
	@VaadinViewComponentLabels(captionI18NKey="geo.state",
							   useCaptionI18NKeyAsPlaceHolderKey=true)
	@Getter @Setter private VaadinNoraGeoLocationItemField<GeoStateID> _cmbState = new VaadinNoraGeoLocationItemField<>();	
	private Registration _cmbStateRegistration;
	
	@VaadinViewComponentLabels(captionI18NKey="geo.state",
							   useCaptionI18NKeyAsPlaceHolderKey=false)
	@Getter @Setter private TextField _txtNotNormalizedState = new TextField();
	
	////////// County
	@VaadinViewField(bindToViewObjectFieldNamed=VaadinViewGeoPosition.COUNTY_FIELD,
					 bindStringConverter=false,required=false)
	@LangIndependentVaadinViewField
	@VaadinViewComponentLabels(captionI18NKey="geo.county",
							   useCaptionI18NKeyAsPlaceHolderKey=true)
	@Getter @Setter private VaadinNoraGeoLocationItemField<GeoCountyID> _cmbCounty = new VaadinNoraGeoLocationItemField<>();
	private Registration _cmbCountyRegistration;

	
	////////// Municipality
	@VaadinViewField(bindToViewObjectFieldNamed=VaadinViewGeoPosition.MUNICIPALITY_FIELD,
					 bindStringConverter=false,required=false)
	@LangIndependentVaadinViewField
	@VaadinViewComponentLabels(captionI18NKey="geo.municipality",
							   useCaptionI18NKeyAsPlaceHolderKey=true)
	@Getter @Setter private VaadinNoraGeoLocationItemField<GeoMunicipalityID> _cmbMunicipality = new VaadinNoraGeoLocationItemField<>();
	private Registration _cmbMunicipalityRegistration;

	
	////////// Locality
	@VaadinViewField(bindToViewObjectFieldNamed=VaadinViewGeoPosition.LOCALITY_FIELD,
					 bindStringConverter=false,required=false)
	@LangIndependentVaadinViewField
	@VaadinViewComponentLabels(captionI18NKey="geo.locality",
							   useCaptionI18NKeyAsPlaceHolderKey=true)
	@Getter @Setter private VaadinNoraGeoLocationItemField<GeoLocalityID> _cmbLocality = new VaadinNoraGeoLocationItemField<>();
	private Registration _cmbLocalityRegistration;

	
	////////// Street
	@VaadinViewField(bindToViewObjectFieldNamed=VaadinViewGeoPosition.STREET_FIELD,
					 bindStringConverter=false,required=false)
	@LangIndependentVaadinViewField
	@VaadinViewComponentLabels(captionI18NKey="geo.street",
							   useCaptionI18NKeyAsPlaceHolderKey=false)
	@Getter @Setter private VaadinNoraGeoLocationItemField<GeoStreetID> _cmbStreet = new VaadinNoraGeoLocationItemField<>();
	private Registration _cmbStreetRegistration;

		
	private final VaadinToggleButton _btnToggleStreet = new VaadinToggleButton();
	
	////////// Portal
	@VaadinViewField(bindToViewObjectFieldNamed=VaadinViewGeoPosition.PORTAL_FIELD,
					 bindStringConverter=false,required=false)
	@LangIndependentVaadinViewField
	@VaadinViewComponentLabels(captionI18NKey="geo.portal",
							   useCaptionI18NKeyAsPlaceHolderKey=true)
	@Getter @Setter private VaadinNoraGeoLocationItemField<GeoPortalID> _cmbPortal = new VaadinNoraGeoLocationItemField<>();
	private Registration _cmbPortalRegistration;
	
	////////// Floor
	@VaadinViewField(bindToViewObjectFieldNamed=VaadinViewGeoPosition.FLOOR_FIELD,
					 bindStringConverter=false,required=false)
	@LangIndependentVaadinViewField
	@VaadinViewComponentLabels(captionI18NKey="geo.building-floor-door")
	@Getter @Setter private TextField _txtFloor = new TextField();
	private Registration _cmbFloorRegistration;

	////////// ZipCode
	@VaadinViewField(bindToViewObjectFieldNamed=VaadinViewGeoPosition.ZIP_CODE_FIELD,
					 bindStringConverter=false,required=false)
	@LangIndependentVaadinViewField
	@VaadinViewComponentLabels(captionI18NKey="geo.zip",
							   useCaptionI18NKeyAsPlaceHolderKey=true)
	@Getter @Setter private TextField _txtZipCode = new TextField();
	private Registration _txtZipCodeRegistration;
	
	private final Button _btnSearchByZipCode = new Button();
	
	////////// Position
	@VaadinViewField(bindToViewObjectFieldNamed=VaadinViewGeoPosition.POSITION_FIELD,
					 bindStringConverter=false,required=false)
	@LangIndependentVaadinViewField
	@Getter private VaadinNORAContactGeoPostion2DComponent _coords;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	BINDER
/////////////////////////////////////////////////////////////////////////////////////////	
	// Binder
	@Getter private final Binder<VaadinViewGeoPosition> _vaadinUIBinder = new Binder<>(VaadinViewGeoPosition.class);
	
/////////////////////////////////////////////////////////////////////////////////////////
//	STATE (avoid as much as possible)
/////////////////////////////////////////////////////////////////////////////////////////
	private VaadinViewGeoPosition _viewObject;
	
	private int _zoom_level = VaadinNORAContactConstants.COUNTRY_ZOOM; 
	
	private boolean _changed = false;
	
	private Button _clearBtn;

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinNORAContactForm(final UII18NService i18n,
								 final VaadinNORAContactFormPresenter presenter) {
		super();
		_i18n = i18n;
		_presenter = presenter;
		
		////////// ui
		_btnSearchByZipCode.setIcon(VaadinIcons.SEARCH);
		_btnSearchByZipCode.setWidth(50,Unit.PIXELS);
		_clearBtn = new Button();
		_clearBtn.setIcon(VaadinIcons.ERASER);
		_clearBtn.addStyleName(VaadinValoTheme.BUTTON_BORDERLESS_COLORED);
		_clearBtn.setWidth(50,Unit.PIXELS);
		_clearBtn.setDescription(i18n.getMessage("clear"));
		
		_coords = new VaadinNORAContactGeoPostion2DComponent(_i18n,
															 presenter);
		
		_cmbCountry.setWidth(100,Unit.PERCENTAGE);
		_cmbCounty.setWidth(100,Unit.PERCENTAGE);
		
		_cmbState.setWidth(100,Unit.PERCENTAGE);
		_txtNotNormalizedState.setWidth(100,Unit.PERCENTAGE);
		
		_cmbMunicipality.setWidth(100,Unit.PERCENTAGE);
		
		_cmbLocality.setWidth(100,Unit.PERCENTAGE);
		
		_cmbStreet.setWidth(100,Unit.PERCENTAGE);
		_cmbStreet.addComboStyleName(VaadinValoTheme.COMBO_SUGGESTION);
		_cmbStreet.setComboIcon(VaadinIcons.FILTER);
		_cmbStreet.addComboStyleName("inline-icon");
		_cmbStreet.setPlaceholder("Filtrar por texto");
		
		_btnToggleStreet.setWidth(50,Unit.PIXELS);
		_btnToggleStreet.addStyleName(VaadinValoTheme.DIRECTORY_TOGGLE_COMPONENT);
		_btnToggleStreet.setDescription(i18n.getMessage("contact.street.tooltip"), ContentMode.HTML);

		
		///////// Street data provider
		CallbackDataProvider<GeoLocationItem<GeoStreetID>,String> streetSearchDataProvider = null;
		streetSearchDataProvider = new CallbackDataProvider<>(
											// query
											query -> {
												String text = query.getFilter().orElse(null);
												// do not allow searching if [state], [county], [municipality], [locality] and [street] text are empty
												if (!_canQueryStreetByText(text)) return Stream.empty();
												
												GeoStateID stateId = _cmbState.getValueId();
												GeoCountyID countyId = _cmbCounty.getValueId();
												GeoMunicipalityID municipalityId = _cmbMunicipality.getValueId();
												GeoLocalityID locId = _cmbLocality.getValueId();
												return _presenter.findStreets(stateId,countyId,municipalityId,locId,
																			  text,
																			  _i18n.getCurrentLanguage())
																 .stream();
											},
											// count
											query -> {
												String text = query.getFilter().orElse(null);
												
												// do not allow searching if [state], [county], [municipality], [locality] and [street] text are empty
												if (!_canQueryStreetByText(text)) return 0;
												
												GeoStateID stateId = _cmbState.getValueId();
												GeoCountyID countyId = _cmbCounty.getValueId();
												GeoMunicipalityID municipalityId = _cmbMunicipality.getValueId();
												GeoLocalityID locId = _cmbLocality.getValueId();
												return (int)_presenter.findStreets(stateId,countyId,municipalityId,locId,
																			  	   text,
																			  	   _i18n.getCurrentLanguage())
																 	   .stream()
																 	   .count();
											});
		_cmbStreet.setDataProvider(streetSearchDataProvider);
		
	
		////////// Bind: automatic binding using using @VaadinViewField annotation of view fields
		_viewObject = new VaadinViewGeoPosition();
		
		VaadinViews.using(_vaadinUIBinder,_i18n)
				   .bindComponentsOf(this)
				   .toViewObjectOfType(VaadinViewGeoPosition.class);
		
		////////// I18N
		VaadinViews.using(_i18n)
				   .setI18NLabelsOf(this);
		
		////////// Behavior
		_setBehavior();
		
	}
	private boolean _canQueryStreetByText(final String text)  {
		return _cmbState.getValue() != null 
		    && _cmbCounty.getValue() != null 
		    && _cmbMunicipality.getValue() != null 
		    //&& _cmbLocality.getValue() != null 
		    && Strings.isNOTNullOrEmpty(text) && text.length() > 2;
	}
	@Override
	protected Component initContent() {
		////////// Layout & style
		HorizontalLayout streetHl = new HorizontalLayout(_cmbStreet,_btnToggleStreet);
		streetHl.setSpacing(false);
		streetHl.setWidth(100, Unit.PERCENTAGE);
		streetHl.setExpandRatio(_cmbStreet, 3);
		streetHl.setExpandRatio(_btnToggleStreet, 1);
		streetHl.setComponentAlignment(_btnToggleStreet, Alignment.BOTTOM_LEFT);
		
		HorizontalLayout hlZip = new HorizontalLayout(_txtZipCode,_btnSearchByZipCode);
		hlZip.setSpacing(false);
		hlZip.setComponentAlignment(_btnSearchByZipCode, Alignment.BOTTOM_LEFT);
		HorizontalLayout hlZipBtn = _createHorizontalLayout(hlZip,_clearBtn);
		_clearBtn.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
		hlZipBtn.setComponentAlignment(_clearBtn, Alignment.BOTTOM_RIGHT);
		hlZipBtn.setExpandRatio(hlZip, 35);
		
		VerticalLayout ly = new VerticalLayout(hlZipBtn,
											   _createHorizontalLayout(_cmbCountry,_cmbState),
											   _createHorizontalLayout(_cmbCounty,_cmbMunicipality),
											   _createHorizontalLayout(_cmbLocality,streetHl),
											   _createHorizontalLayout(_cmbPortal,_txtFloor),
											   _coords);
		ly.setSizeFull();
		ly.setSpacing(true);
		ly.setMargin(false);
		
		return ly;
	}
	private static HorizontalLayout _createHorizontalLayout(final Component c1,final Component c2) {
		HorizontalLayout hl = new HorizontalLayout(c1, c2);
		hl.setWidth(100, Unit.PERCENTAGE);
		hl.setExpandRatio(c1, 2);
		hl.setExpandRatio(c2, 3);
		return hl;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GET / SET VALUE
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public VaadinViewGeoPosition getValue() {
		return _viewObject;
	}
	@Override
	protected void doSetValue(final VaadinViewGeoPosition value) {
		this.editViewObject(value);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	FORM
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void editViewObject(final VaadinViewGeoPosition viewObj) {
		_setNamigForCaption(viewObj);
		_viewObject = viewObj;
		_vaadinUIBinder.readBean(viewObj);
		_btnToggleStreet.setValue(viewObj.getStreet() != null && 
								  !viewObj.isStreetNormalized() &&  
								  Strings.isNOTNullOrEmpty(viewObj.getStreet().getName()));
		// To avoid change to true due to listener at binding at the first read or after saving
		setAllComponentsViewDataChanged(false);
	}
	@Override
	public void writeAsDraftEditedViewObjectTo(final VaadinViewGeoPosition viewObj) {
		_vaadinUIBinder.writeBeanAsDraft(viewObj);
	}
	@Override
	public boolean writeIfValidEditedViewObjectTo(final VaadinViewGeoPosition viewObj) {
		this.writeAsDraftEditedViewObjectTo(viewObj);
		return true;
	}
	@Override
	public void attach() {
		super.attach();
		if (_cmbCountry.getValue() != null) {
			_cmbState.normalized(_cmbCountry.getValue() != null && _cmbCountry.getValue().getId().is(NORAGeoIDs.SPAIN));
			_cmbCounty.normalized(_cmbCountry.getValue() != null && _cmbCountry.getValue().getId().is(NORAGeoIDs.SPAIN));
			_cmbMunicipality.normalized(_cmbCountry.getValue() != null && _cmbCountry.getValue().getId().is(NORAGeoIDs.SPAIN));
			_cmbLocality.normalized(_cmbCountry.getValue() != null && _cmbCountry.getValue().getId().equals(NORAGeoIDs.SPAIN));
			_cmbStreet.normalized(_cmbCountry.getValue() != null && _cmbCountry.getValue().getId().is(NORAGeoIDs.SPAIN) && !_btnToggleStreet.getValue());
			_cmbPortal.normalized(_cmbCountry.getValue() != null && _cmbCountry.getValue().getId().is(NORAGeoIDs.SPAIN) && !_btnToggleStreet.getValue());
		} else {
			_cmbCountry.normalized(true);
			_cmbState.normalized(true);
			_cmbCounty.normalized(true);
			_cmbMunicipality.normalized(true);
			_cmbLocality.normalized(true);
			_cmbStreet.normalized(true);
			_cmbPortal.normalized(true);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public int getZoomLevel() {
		if (_cmbCountry.getValue() != null) {
			_zoom_level = VaadinNORAContactConstants.COUNTRY_ZOOM;
		}
		if (_cmbState.getValue() != null) {
			_zoom_level = VaadinNORAContactConstants.STATE_ZOOM;
		}
		if (_cmbMunicipality.getValue() != null) {
			_zoom_level = VaadinNORAContactConstants.MUNICIPALITY_ZOOM;
		}
		if (_cmbPortal.getValue() != null) {
			_zoom_level = VaadinNORAContactConstants.PORTAL_ZOOM;
		}
		return _zoom_level;
	}
	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
		if (enabled) { 
			_cmbCountry.setEnabled(enabled);
		}
		_coords.setEnabled(enabled);
		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BEHAVIOR
/////////////////////////////////////////////////////////////////////////////////////////
	private void _setBehavior() {
		Language lang = _i18n.getCurrentLanguage();
		
		_cmbCountry.addValueChangeListener(valChangEvent -> _loadStateCmb());
		_cmbState.addValueChangeListener(valChangEvent -> _loadCountyCmb());
		_cmbCounty.addValueChangeListener(valChangEvent -> _loadMunicipalityCmb());
		_cmbMunicipality.addValueChangeListener(valChangEvent -> {
														_loadLocalityCmb();
														_loadStreetCmb();
												});
		_cmbLocality.addValueChangeListener(valChangEvent -> {
												if (valChangEvent.getOldValue() != null) {
													_loadStreetCmb();
												}
											});
		_cmbStreet.addValueChangeListener(valChangEvent -> {
													if (valChangEvent.getValue() != null
													 && _cmbLocality.getValue() == null
													 && valChangEvent.getValue().getId() != null) {
														GeoStreet street = _presenter.getStreet(null, null, null, valChangEvent.getValue().getId());
														if (street != null) {
															_cmbLocality.setValue(new GeoLocationItem<>(street.getLocalityId(),
																									  				 _presenter.getNORANamingIn(Languages.of(UI.getCurrent()
																									  						 								   .getLocale()))
																									  				 		   .getLocalityOf(GeoPosition.createNew()
																									  				 				   					 .withNormalizedState(street.getStateId())
																									  				 				   					 .withNormalizedCounty(street.getCountyId())
																									  				 				   					 .withNormalizedMunicipality(street.getMunicipalityId())
																									  				 				   					 .withNormalizedLocality(street.getLocalityId())
																									  				 				   					 .build())));
														}
													}
													_loadPortalCmb();
													
		});
		_cmbPortal.addValueChangeListener(valChangEvent -> {
												if (_cmbPortal.getValueId() != null) {	
													_loadCoords2D(_cmbPortal.getValueId());
													_zoom_level = VaadinNORAContactConstants.PORTAL_ZOOM;
												}
										  });
		
			_btnSearchByZipCode.addClickListener(clickEvent -> {
													if (Strings.isNullOrEmpty(_txtZipCode.getValue())) return;
													
													_cmbState.clear();
													_cmbCounty.clear();
													_cmbMunicipality.clear();
													_cmbLocality.clear();
													_cmbStreet.clear();
													_cmbPortal.clear();
													_txtFloor.clear();
													_presenter.onSearchByZipRequested(_txtZipCode.getValue(), 
																				  	  lang, 
																				  	  UIPresenterSubscriber.from(
																							// onsuccess
																							viewObj ->  {
																									_cmbCountry.setValue(viewObj.getCountry());
																									_cmbState.setValue(viewObj.getState());
																									_cmbState.setEnabled(true);
																									_cmbCounty.setValue(viewObj.getCounty());
																									_cmbMunicipality.setValue(viewObj.getMunicipality());
																									_cmbLocality.setValue(viewObj.getLocality());
																							},
																							// on error
																		  					th -> {
																		  						Notification.show("Error al buscar por codigo postal "+_txtZipCode.getValue(),
																		  								 		   Type.ERROR_MESSAGE);
																		  					}));
												});
		_coords.getSearchByGeoPosition2DBtn()
			   .addClickListener(clickEvent -> {
   									if (_coords.getValue() == null 
   									 || _coords.getCoords2D() == null 
   									 || Strings.isNullOrEmpty(_coords.getCoords2D().getValue())) return;
   									
									_cmbState.clear();
									_cmbCounty.clear();
									_cmbMunicipality.clear();
									_cmbLocality.clear();
									_cmbStreet.clear();
									_cmbPortal.clear();
									_txtFloor.clear();
									GeoPosition2D geoPosition2D = _coords.getValue();
									
									if (geoPosition2D == null) return;
									_presenter.onSearchByGeoPosition2D(geoPosition2D, 
																	   lang,
																	   UIPresenterSubscriber.from(
																			// onsuccess
																			viewObj ->  {
																					_cmbCountry.setValue(viewObj.getCountry());
																					_cmbState.setValue(viewObj.getState());
																					_cmbState.setEnabled(true);
																					_cmbCounty.setValue(viewObj.getCounty());
																					_cmbMunicipality.setValue(viewObj.getMunicipality());
																					if (viewObj.getLocality() != null && viewObj.getLocality().getId()!= null) {
																						_cmbLocality.setValue(viewObj.getLocality());
																					}
																			},
																			// on error
														  					th -> {
														  						Notification.show("Error al buscar por coordenadas "+th.getMessage(),
														  								 		   Type.ERROR_MESSAGE);
														  					}));
								});
		_btnToggleStreet.addClickListener(clickEvent -> {
												_cmbStreet.normalized(!_btnToggleStreet.getValue());
												_cmbPortal.normalized(!_btnToggleStreet.getValue());
												setViewDataChanged(true);
										  });
		_loadCountryCmb();
		
		_clearBtn.addClickListener(event -> {	
										_txtZipCode.clear();
								  		_cmbCountry.clear();
								  		_cmbState.clear();
										_cmbCounty.clear();
										_cmbMunicipality.clear();
										_cmbLocality.clear();
										_cmbStreet.clear();
										_cmbPortal.clear();
										_txtFloor.clear();
										_loadCountryCmb();
										_coords.setValue(null);
										_coords.clear();
										((VaadinNORAContactComponent)this.getParent().getParent()).hideMap(true);
								  });
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LOAD METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	private void _setNamigForCaption(final VaadinViewGeoPosition viewObj) {
		NORANaming noraNaming = _presenter.getNORANamingIn(Languages.of(UI.getCurrent().getLocale()));
		GeoPosition geo = new GeoPosition();
		// country
		if (viewObj.getCountry() != null && Strings.isNullOrEmpty(viewObj.getCountry().getName())) {
			geo.setCountry(viewObj.getCountry());
			viewObj.getCountry().setName(noraNaming.getCountryOf(geo));
		}
		// state
		if (viewObj.getState() != null && Strings.isNullOrEmpty(viewObj.getState().getName())) {
			geo.setState(viewObj.getState());
			viewObj.getState().setName(noraNaming.getStateOf(geo));
		}
		// county
		if (viewObj.getCounty() != null && Strings.isNullOrEmpty(viewObj.getCounty().getName())) {
			geo.setCounty(viewObj.getCounty());
			viewObj.getCounty().setName(noraNaming.getCountyOf(geo));
		}
		// municipality
		if (viewObj.getMunicipality() != null && Strings.isNullOrEmpty(viewObj.getMunicipality().getName())) {
			geo.setMunicipality(viewObj.getMunicipality());
			viewObj.getMunicipality().setName(noraNaming.getMunicipalityOf(geo));
		}
		// locality
		if (viewObj.getLocality() != null && Strings.isNullOrEmpty(viewObj.getLocality().getName())) {
			geo.setLocality(viewObj.getLocality());
			viewObj.getLocality().setName(noraNaming.getLocalityOf(geo));
		}
		// street
		if (viewObj.getStreet() != null && Strings.isNullOrEmpty(viewObj.getStreet().getName())) {
			geo.setStreet(viewObj.getStreet());
			viewObj.getStreet().setName(noraNaming.getStreetOf(geo));
		}
		// portal
		if (viewObj.getPortal() != null && Strings.isNullOrEmpty(viewObj.getPortal().getName())) {
			geo.setPortal(viewObj.getPortal());
			viewObj.getPortal().setName(noraNaming.getPortalOf(geo));
		}
		
	}
	private void _loadCountryCmb() {
		_presenter.onCountriesLoadRequested(_i18n.getCurrentLanguage(),
											UIPresenterSubscriber.from(
													// onsuccess
													countries -> {
														_cmbCountry.clear();
														_cmbCountry.setItems(countries);
														_cmbCountry.setEnabled(true);
													},
													// on error
								  					th -> {
								  						Notification.show("Error loading countries " + th.getMessage(),
								  								 		   Type.ERROR_MESSAGE);
								  					})
											);
		_cmbState.clear();
		_cmbCounty.clear();
		_cmbMunicipality.clear();
		_cmbLocality.clear();
		_cmbStreet.clear();
		_cmbPortal.clear();
		_txtFloor.clear();
	}

	private void _loadStateCmb() {
		if (_cmbCountry.getValue() ==  null 
		 || _cmbCountry.getValueId() == null) {
			_cmbState.clear();
			_zoom_level = VaadinNORAContactConstants.COUNTRY_ZOOM; 
		}
		if (_cmbCountry.getValue() !=  null) { 
			if (_cmbCountry.getValueId().equals(NORAGeoIDs.SPAIN)) {
				_loadCoords2D(_cmbCountry.getValueId());
				_presenter.onStatesLoadRequested(_i18n.getCurrentLanguage(),
												 UIPresenterSubscriber.from(
														 // onsuccess
														 states ->  {
												 			_cmbState.clear();
												 			_cmbState.setItems(states);
												 			_cmbState.setEnabled(true);
														 },
														 // on error
									  					 th -> {
									  						 Notification.show("Error loading states " + th.getMessage(),
									  								 		   Type.ERROR_MESSAGE);
									  					 })
				);
				_cmbCounty.clear();
				_cmbMunicipality.clear();
				_cmbLocality.clear();
				_cmbStreet.clear();
				_cmbPortal.clear();
				_txtFloor.clear();
			}
			_cmbState.normalized(_cmbCountry.getValueId().equals(NORAGeoIDs.SPAIN));
			_cmbCounty.normalized(_cmbCountry.getValueId().equals(NORAGeoIDs.SPAIN));
			_cmbMunicipality.normalized(_cmbCountry.getValueId().equals(NORAGeoIDs.SPAIN));
			_cmbLocality.normalized(_cmbCountry.getValueId().equals(NORAGeoIDs.SPAIN));
			_cmbStreet.normalized(_cmbCountry.getValueId().equals(NORAGeoIDs.SPAIN) && !_btnToggleStreet.getValue());
			_cmbPortal.normalized(_cmbCountry.getValueId().equals(NORAGeoIDs.SPAIN) && !_btnToggleStreet.getValue());
		} 

	}
	private void _loadCountyCmb() {
		if (_cmbState.getValueId() == null) {
			_cmbCounty.clear();
			return;
		}
		
		if (_cmbState.getValue() != null) {
			if (_cmbCountry.getValueId().equals(NORAGeoIDs.SPAIN)) {
				_loadCoords2D(_cmbState.getValueId());
				_presenter.onCountiesLoadRequested(_cmbState.getValueId(),
												   _i18n.getCurrentLanguage(),
												   UIPresenterSubscriber.from(
														 // onsuccess
														 counties -> { 
													 			_cmbCounty.clear();
													 			_cmbCounty.setItems(counties);
													 			_cmbCounty.setEnabled(true);
														 },
														 // on error
									  					 th -> {
									  						 Notification.show("Error loading counties " + th.getMessage(),
									  								 		   Type.ERROR_MESSAGE);
									  					 })
												    );
			}
			_cmbMunicipality.clear();
			_cmbLocality.clear();
			_cmbStreet.clear();
			_cmbPortal.clear();
			_txtFloor.clear();
		}
		_cmbStreet.normalized(_cmbCountry.getValueId().equals(NORAGeoIDs.SPAIN) && !_btnToggleStreet.getValue());
		_cmbPortal.normalized(_cmbCountry.getValueId().equals(NORAGeoIDs.SPAIN) && !_btnToggleStreet.getValue());
	}
	private void _loadMunicipalityCmb() {
		if (_cmbCounty.getValue() ==  null || _cmbCounty.getValue().getId() == null) {
			_cmbMunicipality.clear();
			_zoom_level = VaadinNORAContactConstants.STATE_ZOOM;
			return;
		}
		if (_cmbCounty.getValue() != null) {
			if (_cmbCountry.getValueId().equals(NORAGeoIDs.SPAIN)) {
				_loadCoords2D(_cmbCounty.getValueId());
				_presenter.onMunicipalitiesLoadRequested(_cmbState.getValueId(),_cmbCounty.getValueId(),
														 _i18n.getCurrentLanguage(),
											             UIPresenterSubscriber.from(
											        		   // onsuccess
											        		   muns ->  { 
										        			   		_cmbMunicipality.clear();
														 			_cmbMunicipality.setItems(muns);
														 			_cmbMunicipality.setEnabled(true);
											        		   },
											        		   // on error
											        		   th -> {
											        			   Notification.show("Error loading municipalities " + th.getMessage(),
									  								 		   	     Type.ERROR_MESSAGE);
											        		   })
														);
			}
			_cmbLocality.clear();
			_cmbStreet.clear();
			_cmbPortal.clear();
			_txtFloor.clear();
		}
	}
	private void _loadLocalityCmb() {
		if (_cmbMunicipality.getValue() ==  null || _cmbMunicipality.getValue().getId() == null) {
			_cmbLocality.clear();
			_zoom_level = VaadinNORAContactConstants.STATE_ZOOM;
			return;
		}
		_zoom_level = VaadinNORAContactConstants.MUNICIPALITY_ZOOM;
		if (_cmbMunicipality.getValue() !=  null) {
			if (_cmbCountry.getValueId().equals(NORAGeoIDs.SPAIN)) {
				_loadCoords2D(_cmbMunicipality.getValueId());
				_presenter.onLocalitiesLoadRequested(_cmbState.getValueId(),_cmbCounty.getValueId(),_cmbMunicipality.getValueId(),
												     _i18n.getCurrentLanguage(),
										             UIPresenterSubscriber.from(
										        		   // onsuccess
										        		   locs ->  { 
								        			   			_cmbLocality.clear();
								        			   			_cmbLocality.setItems(locs);
								        			   			_cmbLocality.setEnabled(true);
										        		   },
										        		   // on error
										        		   th -> {
										        			   Notification.show("Error loading localities " + th.getMessage(),
								  								 		   	     Type.ERROR_MESSAGE);
										        		   })
													 );
			}
			_cmbStreet.clear();
			_cmbPortal.clear();
			_txtFloor.clear();
		}
		_cmbStreet.setEnabled(true);
	}
	private void _loadStreetCmb() {
		_cmbStreet.clear();
		if (_cmbLocality.getValueId() !=  null 
				|| _cmbMunicipality.getValueId() != null) {
			_cmbStreet.setEnabled(true);
		} else {
			_cmbStreet.setEnabled(false);
		}
		_zoom_level = VaadinNORAContactConstants.MUNICIPALITY_ZOOM;
		_loadCoords2D(_cmbLocality.getValueId() != null 
							? _cmbLocality.getValueId()
							: _cmbMunicipality.getValueId());
		_cmbPortal.clear();
		_txtFloor.clear();
	}
	private void _loadPortalCmb() {
		if (_cmbStreet.getValue() ==  null || _cmbStreet.getValue().getId() == null) {
			_zoom_level = VaadinNORAContactConstants.MUNICIPALITY_ZOOM;
			_cmbPortal.clear();
			_txtFloor.clear();
			return;
		}
		_zoom_level = VaadinNORAContactConstants.PORTAL_ZOOM;
		_loadCoords2D(_cmbStreet.getValueId());
		_presenter.onPortalLoadRequested(_cmbLocality.getValueId(),_cmbStreet.getValueId(),
										 _i18n.getCurrentLanguage(),
							             UIPresenterSubscriber.from(
							        		   // onsuccess
							        		   portals ->  { 
				        			   				_cmbPortal.clear();
				        			   				_txtFloor.clear();
								 					_cmbPortal.setItems(portals);
								 					_cmbPortal.setEnabled(true);
							        		   },
							        		   // on error
							        		   th -> {
							        			   Notification.show("Error loading portals " + th.getMessage(),
					  								 		   	     Type.ERROR_MESSAGE);
							        		   })
										);
	}
	private <OID extends GeoIDBase> void _loadCoords2D(final OID oid) {
		GeoCountyID countyId = null;
		if (oid instanceof GeoRegionID || oid instanceof GeoMunicipalityID) {
			countyId = _cmbCounty.getValueId();
		}
		_presenter.onSearchPosition2D(oid,
									  countyId, 
									  UIPresenterSubscriber.from(
							        		   // onsuccess
							        		   geoPosition2D -> {
			        			   					if (geoPosition2D != null) {
			        			   						_coords.setValue(geoPosition2D);
			        			   					}
							        		   },
							        		   // on error
							        		   th -> {
							        			   Notification.show("Error loading coords for oid type=" + oid.getClass().getName()+ " > " + th.getMessage(),
					  								 		   	     Type.ERROR_MESSAGE);
							        		   })
									   );
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
		return _vaadinUIBinder.hasChanges()
					// Check changes for combos in text mode (Foreign country or text input)
			|| _cmbState.hasViewDataChanged() 
			|| _cmbCounty.hasViewDataChanged() 
			|| _cmbMunicipality.hasViewDataChanged() 
			|| _cmbLocality.hasViewDataChanged() 
			|| _cmbStreet.hasViewDataChanged() 
			|| _cmbPortal.hasViewDataChanged() 
			|| _coords.hasViewDataChanged() 
			|| _changed;
	}
	public void setAllComponentsViewDataChanged(final boolean changed) {
		setViewDataChanged(changed);
		_cmbState.setViewDataChanged(changed);
		_cmbCounty.setViewDataChanged(changed);
		_cmbMunicipality.setViewDataChanged(changed);
		_cmbLocality.setViewDataChanged(changed);
		_cmbStreet.setViewDataChanged(changed);
		_cmbPortal.setViewDataChanged(changed);
		_coords.setViewDataChanged(changed);
	}
//////////////////////////////////////////////////////////////////////////////////////////
//	REGISTRATIONS
/////////////////////////////////////////////////////////////////////////////////////////
	public void addTxtZipCodeChangeListener(final ValueChangeListener<String> listener) {
		// Add listener only first time, if not we will have an exponential list of listeners raising events
		if (_txtZipCodeRegistration == null) { 
			_txtZipCodeRegistration = _txtZipCode.addValueChangeListener(listener);
		}
	}
	
	public void addCmbCountryChangeListener(final ValueChangeListener<GeoLocationItem<GeoCountryID>> listener) {
		// Add listener only first time, if not we will have an exponential list of listeners raising events
		if (_cmbCountryRegistration == null) { 
			_cmbCountryRegistration = _cmbCountry.addValueChangeListener(listener);
		}
	}
	
	public void addCmbStateChangeListener(final ValueChangeListener<GeoLocationItem<GeoStateID>> listener) {
		// Add listener only first time, if not we will have an exponential list of listeners raising events
		if (_cmbStateRegistration == null) { 
			_cmbStateRegistration = _cmbState.addValueChangeListener(listener);
		}
	}
	public void addTxtNotNormalizedStateChangeListener(final ValueChangeListener<String> listener) {
		_cmbState.addTxtNotNormalizedChangeListener(listener);
	}
	
	public void addCmbCountyChangeListener(final ValueChangeListener<GeoLocationItem<GeoCountyID>> listener) {
		// Add listener only first time, if not we will have an exponential list of listeners raising events
		if (_cmbCountyRegistration == null) { 
			_cmbCountyRegistration = _cmbCounty.addValueChangeListener(listener);
		}
	}
	public void addTxtNotNormalizedCountyChangeListener(final ValueChangeListener<String> listener) {
		_cmbCounty.addTxtNotNormalizedChangeListener(listener);
	}
	
	public void addCmbMunicipalityChangeListener(final ValueChangeListener<GeoLocationItem<GeoMunicipalityID>> listener) {
		// Add listener only first time, if not we will have an exponential list of listeners raising events
		if (_cmbMunicipalityRegistration == null) { 
			_cmbMunicipalityRegistration = _cmbMunicipality.addValueChangeListener(listener);
		}
	}
	public void addTxtNotNormalizedMunicipalityChangeListener(final ValueChangeListener<String> listener) {
		_cmbMunicipality.addTxtNotNormalizedChangeListener(listener);
	}
	
	public void addCmbLocalityChangeListener(final ValueChangeListener<GeoLocationItem<GeoLocalityID>> listener) {
		// Add listener only first time, if not we will have an exponential list of listeners raising events
		if (_cmbLocalityRegistration == null) { 
			_cmbLocalityRegistration = _cmbLocality.addValueChangeListener(listener);
		}
	}
	public void addTxtNotNormalizedLocalityChangeListener(final ValueChangeListener<String> listener) {
		_cmbLocality.addTxtNotNormalizedChangeListener(listener);
	}

	public void addCmbStreetChangeListener(final ValueChangeListener<GeoLocationItem<GeoStreetID>> listener) {
		// Add listener only first time, if not we will have an exponential list of listeners raising events
		if (_cmbStreetRegistration == null) { 
			_cmbStreetRegistration = _cmbStreet.addValueChangeListener(listener);
		}
	}
	
	public void addTxtNotNormalizedStreetChangeListener(final ValueChangeListener<String> listener) {
		_cmbStreet.addTxtNotNormalizedChangeListener(listener);
	}
	
	public void addCmbPortalChangeListener(final ValueChangeListener<GeoLocationItem<GeoPortalID>> listener) {
		// Add listener only first time, if not we will have an exponential list of listeners raising events
		if (_cmbPortalRegistration == null) { 
			_cmbPortalRegistration = _cmbPortal.addValueChangeListener(listener);
		}
	}
	
	public void addTxtFloorChangeListener(final ValueChangeListener<String> listener) {
		// Add listener only first time, if not we will have an exponential list of listeners raising events
		if (_cmbFloorRegistration == null) { 
			_cmbFloorRegistration = _txtFloor.addValueChangeListener(listener);
		}
	}
	
	public void addTxtNotNormalizedPortalChangeListener(final ValueChangeListener<String> listener) {
		_cmbPortal.addTxtNotNormalizedChangeListener(listener);
	}
	
	public void addCoordsChangeListener(final VaadinNORACoordsChangeListener listener) {
		_coords.setVaadinNORACoordsChangeListener(listener);
	}
}
