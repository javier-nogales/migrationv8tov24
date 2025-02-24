package r01ui.base.components.contact.nora;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;

import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import r01f.types.geo.GeoLocationItem;
import r01f.types.geo.GeoOIDs.GeoCountryID;
import r01f.types.geo.GeoOIDs.GeoCountyID;
import r01f.types.geo.GeoOIDs.GeoLocalityID;
import r01f.types.geo.GeoOIDs.GeoMunicipalityID;
import r01f.types.geo.GeoOIDs.GeoPortalID;
import r01f.types.geo.GeoOIDs.GeoStateID;
import r01f.types.geo.GeoOIDs.GeoStreetID;
import r01f.types.geo.GeoPosition2D;
import r01f.ui.i18n.UII18NService;
import r01f.ui.vaadin.styles.VaadinValoTheme;
import r01f.util.types.Strings;
import r01ui.base.components.contact.nora.VaadinNORAContactGeoPostion2DComponent.VaadinNORACoordsChangeListener;
import r01ui.base.components.form.VaadinViewTracksChanges;

@Slf4j
public class VaadinNORAContactComponent
     extends CustomField<VaadinViewGeoPosition>
  implements VaadinViewTracksChanges {
	private static final long serialVersionUID = 5018317833169484242L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final VaadinNORAContactForm _contactForm;
	private final BrowserFrame _map;
	private VaadinViewGeoPosition _viewObject;

	private boolean _changed = false;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public VaadinNORAContactComponent(final UII18NService i18n,
									  final VaadinNORAContactFormPresenter presenter) {
		_contactForm = new VaadinNORAContactForm(i18n,
												 presenter);
		_map = new BrowserFrame("Mapa");
		_map.setWidth("600px");
		_map.setHeight("400px");
		_contactForm.getCoords().setVaadinNORACoordsChangeListener(coordsChangeEvent -> {
																		GeoPosition2D geo = _contactForm.getCoords().getGeoPositionForETRS89Standard();
																		if (geo != null) {
																			if (geo.getX() == 0 || geo.getY() == 0) {
																				_map.setSource(null);
																			} else {
																				String resourceName = Strings.customized("components/geocoder/previewMap.html?x={}&y={}&zoom={}",
																														 geo.getX(),geo.getY(),_contactForm.getZoomLevel());
																				log.debug("coords x,y={},{} (format={}) zoom={} > resource={}",
																						  geo.getStandard(),
																						  geo.getX(),geo.getY(),
																						  _contactForm.getZoomLevel(),
																						  resourceName);
																				_map.setSource(new ThemeResource(resourceName));
																			}
																		}
		});
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected Component initContent() {
		HorizontalLayout hl = new HorizontalLayout();
		hl.addStyleName(VaadinValoTheme.LAYOUT_WHITE_BORDERED);
		hl.setSizeFull();
		hl.setMargin(true);
		hl.addComponent(_contactForm);
		hl.addComponent(_map);
		hl.setComponentAlignment(_map, Alignment.MIDDLE_RIGHT);
		return hl;
	}
	@Override
	public VaadinViewGeoPosition getValue() {
		if (_viewObject != null) _contactForm.writeAsDraftEditedViewObjectTo(_viewObject);
		return _viewObject;
	}
	@Override
	protected void doSetValue(final VaadinViewGeoPosition value) {
		_viewObject = value;
		_contactForm.editViewObject(_viewObject);

	}
	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
		if (!enabled) _map.setSource(null);
		_contactForm.setEnabled(enabled);
	}

	public void hideMap (final boolean hide) {
		if (hide) {
			_map.setSource(null);
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
		//System.out.println("NORA ----> " +_changed);
		return _contactForm.hasViewDataChanged() || _changed;
	}
//////////////////////////////////////////////////////////////////////////////////////////
//	LISTENERS
/////////////////////////////////////////////////////////////////////////////////////////
	public void addTxtZipCodeChangeListener(final ValueChangeListener<String> listener) {
		_contactForm.addTxtZipCodeChangeListener(listener);
	}
	public void addCmbCountryChangeListener(final ValueChangeListener<GeoLocationItem<GeoCountryID>> listener) {
		_contactForm.addCmbCountryChangeListener(listener);
	}
	public void addCmbStateChangeListener(final ValueChangeListener<GeoLocationItem<GeoStateID>> listener) {
		_contactForm.addCmbStateChangeListener(listener);
	}

	public void addTxtNotNormalizedStateChangeListener(final ValueChangeListener<String> listener) {
		_contactForm.addTxtNotNormalizedStateChangeListener(listener);
	}

	public void addCmbCountyChangeListener(final ValueChangeListener<GeoLocationItem<GeoCountyID>> listener) {
		_contactForm.addCmbCountyChangeListener(listener);
	}
	public void addTxtNotNormalizedCountyChangeListener(final ValueChangeListener<String> listener) {
		_contactForm.addTxtNotNormalizedCountyChangeListener(listener);
	}
	public void addCmbMunicipalityChangeListener(final ValueChangeListener<GeoLocationItem<GeoMunicipalityID>> listener) {
		_contactForm.addCmbMunicipalityChangeListener(listener);
	}
	public void addTxtNotNormalizedMunicipalityChangeListener(final ValueChangeListener<String> listener) {
		_contactForm.addTxtNotNormalizedMunicipalityChangeListener(listener);
	}
	public void addCmbLocalityChangeListener(final ValueChangeListener<GeoLocationItem<GeoLocalityID>> listener) {
		_contactForm.addCmbLocalityChangeListener(listener);
	}
	public void addTxtNotNormalizedLocalityChangeListener(final ValueChangeListener<String> listener) {
		_contactForm.addTxtNotNormalizedLocalityChangeListener(listener);
	}
	public void addCmbStreetChangeListener(final ValueChangeListener<GeoLocationItem<GeoStreetID>> listener) {
		_contactForm.addCmbStreetChangeListener(listener);
	}
	public void addTxtNotNormalizedStreetChangeListener(final ValueChangeListener<String> listener) {
		_contactForm.addTxtNotNormalizedStreetChangeListener(listener);
	}
	public void addCmbPortalChangeListener(final ValueChangeListener<GeoLocationItem<GeoPortalID>> listener) {
		_contactForm.addCmbPortalChangeListener(listener);
	}
	public void addTxtFloorChangeListener(final ValueChangeListener<String> listener) {
		_contactForm.addTxtFloorChangeListener(listener);
	}
	public void addTxtNotNormalizedPortalChangeListener(final ValueChangeListener<String> listener) {
		_contactForm.addTxtNotNormalizedPortalChangeListener(listener);
	}
	public void addCoordsChangeListener(final VaadinNORACoordsChangeListener listener) {
		_contactForm.addCoordsChangeListener(listener);
	}
}
