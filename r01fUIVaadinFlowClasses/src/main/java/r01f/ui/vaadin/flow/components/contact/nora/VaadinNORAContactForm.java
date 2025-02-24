package r01f.ui.vaadin.flow.components.contact.nora;

import java.util.stream.Stream;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.shared.Tooltip.TooltipPosition;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.CallbackDataProvider;

import lombok.Getter;
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
import r01f.ui.i18n.UII18NService;
import r01f.ui.presenter.UIPresenterSubscriber;
import r01f.ui.vaadin.flow.annotations.LangIndependentVaadinViewField;
import r01f.ui.vaadin.flow.annotations.VaadinViewComponentLabels;
import r01f.ui.vaadin.flow.annotations.VaadinViewField;
import r01f.ui.vaadin.flow.components.button.VaadinToggleButton;
import r01f.ui.vaadin.flow.components.contact.nora.VaadinNORAContactGeoPostion2DComponent.VaadinNORACoordsChangeListener;
import r01f.ui.vaadin.flow.components.form.VaadinFormEditsViewObject;
import r01f.ui.vaadin.flow.components.form.VaadinViewTracksChanges;
import r01f.ui.vaadin.flow.i18n.VaadinI18NMessagesCanBeUpdated;
import r01f.ui.vaadin.flow.view.VaadinView;
import r01f.ui.vaadin.flow.view.VaadinViews;
import r01f.util.types.Strings;
import r01f.util.types.locale.Languages;

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
  			 VaadinI18NMessagesCanBeUpdated {

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
	private final VaadinNoraGeoLocationItemField<GeoCountryID> _cmbCountry = new VaadinNoraGeoLocationItemField<>();
	
	////////// State
	@VaadinViewField(bindToViewObjectFieldNamed=VaadinViewGeoPosition.STATE_FIELD,
					 bindStringConverter=false,required=false)
	@LangIndependentVaadinViewField
	@VaadinViewComponentLabels(captionI18NKey="geo.state",
							   useCaptionI18NKeyAsPlaceHolderKey=true)
	private final VaadinNoraGeoLocationItemField<GeoStateID> _cmbState = new VaadinNoraGeoLocationItemField<>();	
	
	@VaadinViewComponentLabels(captionI18NKey="geo.state",
							   useCaptionI18NKeyAsPlaceHolderKey=false)
	private final TextField _txtNotNormalizedState = new TextField();
	
	////////// County
	@VaadinViewField(bindToViewObjectFieldNamed=VaadinViewGeoPosition.COUNTY_FIELD,
					 bindStringConverter=false,required=false)
	@LangIndependentVaadinViewField
	@VaadinViewComponentLabels(captionI18NKey="geo.county",
							   useCaptionI18NKeyAsPlaceHolderKey=true)
	private final VaadinNoraGeoLocationItemField<GeoCountyID> _cmbCounty = new VaadinNoraGeoLocationItemField<>();

	
	////////// Municipality
	@VaadinViewField(bindToViewObjectFieldNamed=VaadinViewGeoPosition.MUNICIPALITY_FIELD,
					 bindStringConverter=false,required=false)
	@LangIndependentVaadinViewField
	@VaadinViewComponentLabels(captionI18NKey="geo.municipality",
							   useCaptionI18NKeyAsPlaceHolderKey=true)
	private final VaadinNoraGeoLocationItemField<GeoMunicipalityID> _cmbMunicipality = new VaadinNoraGeoLocationItemField<>();

	
	////////// Locality
	@VaadinViewField(bindToViewObjectFieldNamed=VaadinViewGeoPosition.LOCALITY_FIELD,
					 bindStringConverter=false,required=false)
	@LangIndependentVaadinViewField
	@VaadinViewComponentLabels(captionI18NKey="geo.locality",
							   useCaptionI18NKeyAsPlaceHolderKey=true)
	private final VaadinNoraGeoLocationItemField<GeoLocalityID> _cmbLocality = new VaadinNoraGeoLocationItemField<>();

	
	////////// Street
	@VaadinViewField(bindToViewObjectFieldNamed=VaadinViewGeoPosition.STREET_FIELD,
					 bindStringConverter=false,required=false)
	@LangIndependentVaadinViewField
	@VaadinViewComponentLabels(captionI18NKey="geo.street",
							   useCaptionI18NKeyAsPlaceHolderKey=false)
	private final VaadinNoraGeoLocationItemField<GeoStreetID> _cmbStreet = new VaadinNoraGeoLocationItemField<>();

		
	private final VaadinToggleButton _btnToggleStreet = new VaadinToggleButton();
	
	////////// Portal
	@VaadinViewField(bindToViewObjectFieldNamed=VaadinViewGeoPosition.PORTAL_FIELD,
					 bindStringConverter=false,required=false)
	@LangIndependentVaadinViewField
	@VaadinViewComponentLabels(captionI18NKey="geo.portal",
							   useCaptionI18NKeyAsPlaceHolderKey=true)
	private final VaadinNoraGeoLocationItemField<GeoPortalID> _cmbPortal = new VaadinNoraGeoLocationItemField<>();

	////////// ZipCode
	@VaadinViewField(bindToViewObjectFieldNamed=VaadinViewGeoPosition.ZIP_CODE_FIELD,
					 bindStringConverter=false,required=false)
	@LangIndependentVaadinViewField
	@VaadinViewComponentLabels(captionI18NKey="geo.zip",
							   useCaptionI18NKeyAsPlaceHolderKey=true)
	private final TextField _txtZipCode = new TextField();
	
	private final Button _btnSearchByZipCode = new Button();
	
	////////// Position
	@VaadinViewField(bindToViewObjectFieldNamed=VaadinViewGeoPosition.POSITION_FIELD,
					 bindStringConverter=false,required=false)
	@LangIndependentVaadinViewField
	private final VaadinNORAContactGeoPostion2DComponent _coords;
	
	
	
	private final Button _btnClear = new Button();
/////////////////////////////////////////////////////////////////////////////////////////
//	BINDER
/////////////////////////////////////////////////////////////////////////////////////////	
	// Binder
	@Getter private final Binder<VaadinViewGeoPosition> _vaadinUIBinder = new Binder<>(VaadinViewGeoPosition.class);
	
/////////////////////////////////////////////////////////////////////////////////////////
//	STATE (avoid as much as possible)
/////////////////////////////////////////////////////////////////////////////////////////
	private VaadinViewGeoPosition _viewObject;
	
	private int _zoomLevel = VaadinNORAContactConstants.COUNTRY_ZOOM; 
	
	private boolean _changed = false;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinNORAContactForm(final UII18NService i18n,
								 final VaadinNORAContactFormPresenter presenter) {
		super();
		_i18n = i18n;
		_presenter = presenter;
		
		////////// ui
		_btnSearchByZipCode.setIcon(new Icon(VaadinIcon.SEARCH));
		_btnSearchByZipCode.setWidth(50,Unit.PIXELS);
		
		_btnClear.setIcon(new Icon(VaadinIcon.ERASER));
		_btnClear.setWidth(50,Unit.PIXELS);
		_btnClear.setTooltipText(i18n.getMessage("clear"));
		Tooltip tooltip = _btnClear.getTooltip().withPosition(TooltipPosition.BOTTOM_END);
		
		_coords = new VaadinNORAContactGeoPostion2DComponent(_i18n,
															 presenter);
		
		_txtNotNormalizedState.setWidth(100,Unit.PERCENTAGE);
		
		_cmbStreet.setComboPlaceholder("Filtrar por texto");
		
		_btnToggleStreet.setWidth(50,Unit.PIXELS);
		_btnToggleStreet.setTooltipText(i18n.getMessage("contact.street.tooltip"));

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
		
		////////// Layout & style
		HorizontalLayout hlStreet = new HorizontalLayout(new Div(new Icon(VaadinIcon.FILTER),_cmbStreet),_btnToggleStreet);
		hlStreet.setSpacing(false);
		hlStreet.setWidthFull();
		
		HorizontalLayout hlZip = new HorizontalLayout(_txtZipCode,_btnSearchByZipCode);
		hlZip.setSpacing(false);
		HorizontalLayout hlZipBtn = _createHorizontalLayout(hlZip,_btnClear);
		
		VerticalLayout ly = new VerticalLayout(hlZipBtn,
											   _createHorizontalLayout(_cmbCountry,_cmbState),
											   _createHorizontalLayout(_cmbCounty,_cmbMunicipality),
											   _createHorizontalLayout(_cmbLocality,hlStreet),
											   _createHorizontalLayout(_cmbPortal,new Label()),
											   _coords);
		ly.setSizeFull();
		ly.setSpacing(true);
		ly.setMargin(false);
		
		////////// Behavior
		_setBehavior();
		
	}
	private static HorizontalLayout _createHorizontalLayout(final Component c1,final Component c2) {
		HorizontalLayout hl = new HorizontalLayout(c1,c2);
		hl.setWidthFull();
		return hl;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	private boolean _canQueryStreetByText(final String text)  {
		return _cmbState.getValue() != null 
		    && _cmbCounty.getValue() != null 
		    && _cmbMunicipality.getValue() != null 
		    && _cmbLocality.getValue() != null 
		    && Strings.isNOTNullOrEmpty(text) && text.length() > 2;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	COORDS
/////////////////////////////////////////////////////////////////////////////////////////
	public void setVaadinNORACoordsChangeListener(final VaadinNORACoordsChangeListener listener) {
		_coords.setVaadinNORACoordsChangeListener(listener);
	}
	public GeoPosition2D getGeoPositionForETRS89Standard() {
		return _coords.getGeoPositionForETRS89Standard();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GET / SET VALUE
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	protected VaadinViewGeoPosition generateModelValue() {
		return _viewObject;
	}
	@Override
	protected void setPresentationValue(final VaadinViewGeoPosition value) {
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
		// To avoid change to true due to listener at binding at the first read 
		_changed = false;
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
/////////////////////////////////////////////////////////////////////////////////////////
//	ATTACH
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	protected void onAttach(final AttachEvent attachEvent) {
		super.onAttach(attachEvent);
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
			_zoomLevel = VaadinNORAContactConstants.COUNTRY_ZOOM;
		}
		if (_cmbState.getValue() != null) {
			_zoomLevel = VaadinNORAContactConstants.STATE_ZOOM;
		}
		if (_cmbMunicipality.getValue() != null) {
			_zoomLevel = VaadinNORAContactConstants.MUNICIPALITY_ZOOM;
		}
		if (_cmbPortal.getValue() != null) {
			_zoomLevel = VaadinNORAContactConstants.PORTAL_ZOOM;
		}
		return _zoomLevel;
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
		_cmbMunicipality.addValueChangeListener(valChangEvent -> _loadLocalityCmb());
		_cmbLocality.addValueChangeListener(valChangEvent -> _loadStreetCmb());
		_cmbStreet.addValueChangeListener(valChangEvent -> _loadPortalCmb());
		_cmbPortal.addValueChangeListener(valChangEvent -> {
												if (_cmbPortal.getValueId() != null) {	
													_loadCoords2D(_cmbPortal.getValueId());
													_zoomLevel = VaadinNORAContactConstants.PORTAL_ZOOM;
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
																  						Notification.show("Error al buscar por codigo postal "+_txtZipCode.getValue());
																  					})
																			  	  );
											 });
		_coords.addSearchByGeoPosition2DBtnClickListener(clickEvent -> {
						   									if (_coords.getValue() != null
						   									 && Strings.isNOTNullOrEmpty(_coords.getCoords2DAsString())) {
																_cmbState.clear();
																_cmbCounty.clear();
																_cmbMunicipality.clear();
																_cmbLocality.clear();
																_cmbStreet.clear();
																_cmbPortal.clear();
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
																					  						Notification.show("Error al buscar por coordenadas "+th.getMessage());
																					  					}));
						   									}
														});
		_btnToggleStreet.addClickListener(clickEvent -> {
												_cmbStreet.normalized(!_btnToggleStreet.getValue());
												_cmbPortal.normalized(!_btnToggleStreet.getValue());
												setViewDataChanged(true);
										  });
		_loadCountryCmb();
		
		_btnClear.addClickListener(event -> {	
										_txtZipCode.clear();
								  		_cmbCountry.clear();
								  		_cmbState.clear();
										_cmbCounty.clear();
										_cmbMunicipality.clear();
										_cmbLocality.clear();
										_cmbStreet.clear();
										_cmbPortal.clear();
										_loadCountryCmb();
										_coords.setValue(null);
										_coords.clear();
										((VaadinNORAContactComponent)this.getParent().get()).hideMap(true);
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
							  						Notification.show("Error loading countries " + th.getMessage());
							  					})
											);
		_cmbState.clear();
		_cmbCounty.clear();
		_cmbMunicipality.clear();
		_cmbLocality.clear();
		_cmbStreet.clear();
		_cmbPortal.clear();
	}

	private void _loadStateCmb() {
		if (_cmbCountry.getValue() ==  null 
		 || _cmbCountry.getValueId() == null) {
			_cmbState.clear();
			_zoomLevel = VaadinNORAContactConstants.COUNTRY_ZOOM; 
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
								  						 Notification.show("Error loading states " + th.getMessage());
								  					 })
												 );
				_cmbCounty.clear();
				_cmbMunicipality.clear();
				_cmbLocality.clear();
				_cmbStreet.clear();
				_cmbPortal.clear();
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
									  						 Notification.show("Error loading counties " + th.getMessage());
									  					 })
												    );
			}
			_cmbMunicipality.clear();
			_cmbLocality.clear();
			_cmbStreet.clear();
			_cmbPortal.clear();
		}
		_cmbStreet.normalized(_cmbCountry.getValueId().equals(NORAGeoIDs.SPAIN) && !_btnToggleStreet.getValue());
		_cmbPortal.normalized(_cmbCountry.getValueId().equals(NORAGeoIDs.SPAIN) && !_btnToggleStreet.getValue());
	}
	private void _loadMunicipalityCmb() {
		if (_cmbCounty.getValue() ==  null 
		 || _cmbCounty.getValue().getId() == null) {
			_cmbMunicipality.clear();
			_zoomLevel = VaadinNORAContactConstants.STATE_ZOOM;
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
											        			   Notification.show("Error loading municipalities " + th.getMessage());
											        		   })
														 );
			}
			_cmbLocality.clear();
			_cmbStreet.clear();
			_cmbPortal.clear();
		}
	}
	private void _loadLocalityCmb() {
		if (_cmbMunicipality.getValue() ==  null 
		 || _cmbMunicipality.getValue().getId() == null) {
			_cmbLocality.clear();
			_zoomLevel = VaadinNORAContactConstants.STATE_ZOOM;
			return;
		}
		_zoomLevel = VaadinNORAContactConstants.MUNICIPALITY_ZOOM;
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
										        			   Notification.show("Error loading localities " + th.getMessage());
										        		   })
													 );
			}
			_cmbStreet.clear();
			_cmbPortal.clear();
		}
		_cmbStreet.setEnabled(true);
	}
	private void _loadStreetCmb() {
		_cmbStreet.clear();
		if (_cmbLocality.getValueId() !=  null) {
			_cmbStreet.setEnabled(true);
		}
		if (_cmbLocality.getValue() ==  null 
		 || _cmbLocality.getValue().getId() == null) {
			_zoomLevel = VaadinNORAContactConstants.STATE_ZOOM;
			return;
		}
		_zoomLevel = VaadinNORAContactConstants.MUNICIPALITY_ZOOM;
		_loadCoords2D(_cmbLocality.getValueId());
		_cmbPortal.clear();
	}
	private void _loadPortalCmb() {
		if (_cmbStreet.getValue() ==  null 
		 || _cmbStreet.getValue().getId() == null) {
			_zoomLevel = VaadinNORAContactConstants.MUNICIPALITY_ZOOM;
			_cmbPortal.clear();
			return;
		}
		_zoomLevel = VaadinNORAContactConstants.PORTAL_ZOOM;
		_loadCoords2D(_cmbStreet.getValue().getId());
		_presenter.onPortalLoadRequested(_cmbLocality.getValueId(),_cmbStreet.getValueId(),
										 _i18n.getCurrentLanguage(),
							             UIPresenterSubscriber.from(
							        		   // onsuccess
							        		   portals ->  { 
				        			   				_cmbPortal.clear();
								 					_cmbPortal.setItems(portals);
								 					_cmbPortal.setEnabled(true);
							        		   },
							        		   // on error
							        		   th -> {
							        			   Notification.show("Error loading portals " + th.getMessage());
							        		   })
										);
	}
	private <OID extends GeoIDBase> void _loadCoords2D(final OID oid) {
		GeoCountyID countyId = null;
		if (oid instanceof GeoRegionID 
		 || oid instanceof GeoMunicipalityID) {
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
						        			   Notification.show("Error loading coords for oid type=" + oid.getClass().getName() + " > " + 
						        					   		     th.getMessage());
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
		final boolean changed = _changed;
		_changed = false;
		return _vaadinUIBinder.hasChanges() ||
					_cmbStreet.hasViewDataChanged() ||
					_cmbPortal.hasViewDataChanged() ||
					changed;
	}
}
