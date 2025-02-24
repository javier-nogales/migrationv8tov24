package r01f.ui.vaadin.flow.components.contact.nora;

import r01f.types.geo.GeoLocationItem;
import r01f.types.geo.GeoOIDs.GeoCountryID;
import r01f.types.geo.GeoOIDs.GeoCountyID;
import r01f.types.geo.GeoOIDs.GeoLocalityID;
import r01f.types.geo.GeoOIDs.GeoMunicipalityID;
import r01f.types.geo.GeoOIDs.GeoPortalID;
import r01f.types.geo.GeoOIDs.GeoStateID;
import r01f.types.geo.GeoOIDs.GeoStreetID;
import r01f.types.geo.GeoOIDs.GeoZipCode;
import r01f.types.geo.GeoPosition;
import r01f.types.geo.GeoPosition2D;
import r01f.ui.viewobject.UIViewObjectWrappedBase;
import r01f.util.types.Strings;

public class VaadinViewGeoPosition 
     extends UIViewObjectWrappedBase<GeoPosition> {

	private static final long serialVersionUID = -4291550776181866804L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinViewGeoPosition() {
		this(new GeoPosition());
	}
	public VaadinViewGeoPosition(final GeoPosition geoPos) {
		super(geoPos);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ADDRES TEXT
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String ADDRES_TEXT_FIELD = "addressText";
	
	public String getAddressText() {
		return _wrappedModelObject.getAddressText() != null ? _wrappedModelObject.getAddressText() : "";
	}
	public void setAddressText(final String text) {
		_wrappedModelObject.setAddressText(text);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  COUNTRY
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String COUNTRY_FIELD = "country";
	
	public GeoLocationItem<GeoCountryID> getCountry() {
		return _wrappedModelObject.getCountry();
	}
	public void setCountry(final GeoLocationItem<GeoCountryID> val) {
		_wrappedModelObject.setCountry(val);

	}
	
	public static final String COUNTRY_ID_FIELD = "countryId";

	public GeoCountryID getCountryId() {
		return _wrappedModelObject.getCountryId();
	}
	public void setCountryId(final GeoCountryID id) {
		_wrappedModelObject.setCountryId(id);
	}
	
	public boolean isCountryNormalized() {
		return _wrappedModelObject.getCountry() != null ? _wrappedModelObject.getCountry().isNormalized() : false;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  STATE
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String STATE_FIELD = "state";

	public GeoLocationItem<GeoStateID> getState() {
		return _wrappedModelObject.getState();
	}
	public void setState(final GeoLocationItem<GeoStateID> val) {
		_wrappedModelObject.setState(val);
	}
	

	
	public boolean isStateNormalized() {
		return _wrappedModelObject.getState() != null ? _wrappedModelObject.getState().isNormalized() : false;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  COUNTY
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String COUNTY_FIELD = "county";
	
	public GeoLocationItem<GeoCountyID> getCounty() {
		return _wrappedModelObject.getCounty();
	}
	public void setCounty(final GeoLocationItem<GeoCountyID> val) {
		_wrappedModelObject.setCounty(val);
	}
		
	public boolean isCountyNormalized() {
		return _wrappedModelObject.getCounty() != null ? _wrappedModelObject.getCounty().isNormalized() : false;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MUNICIPALITY
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String MUNICIPALITY_FIELD = "municipality";
	
	public GeoLocationItem<GeoMunicipalityID> getMunicipality() {
		return _wrappedModelObject.getMunicipality();
	}
	public void setMunicipality(final GeoLocationItem<GeoMunicipalityID> val) {
		_wrappedModelObject.setMunicipality(val);
	}
	
	public boolean isMunicipalityNormalized() {
		return _wrappedModelObject.getMunicipality() != null ? _wrappedModelObject.getMunicipality().isNormalized() : false;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  LOCALITY
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String LOCALITY_FIELD = "locality";
	
	public GeoLocationItem<GeoLocalityID> getLocality() {
		return _wrappedModelObject.getLocality();
	}
	public void setLocality(final GeoLocationItem<GeoLocalityID> val) {
		_wrappedModelObject.setLocality(val);
	}
	
	public boolean isLocalityNormalized() {
		return _wrappedModelObject.getLocality() != null ? _wrappedModelObject.getLocality().isNormalized() : false;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  STREET
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String STREET_FIELD = "street";
	
	public GeoLocationItem<GeoStreetID> getStreet() {
		return _wrappedModelObject.getStreet();
	}
	public void setStreet(final GeoLocationItem<GeoStreetID> val) {
		_wrappedModelObject.setStreet(val);
	}
	
	public boolean isStreetNormalized() {
		return _wrappedModelObject.getStreet() != null ? _wrappedModelObject.getStreet().isNormalized() : false;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PORTAL
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String PORTAL_FIELD = "portal";
	
	public GeoLocationItem<GeoPortalID> getPortal() {
		return _wrappedModelObject.getPortal();
	}
	public void setPortal(final GeoLocationItem<GeoPortalID> val) {
		_wrappedModelObject.setPortal(val);
	}
	
	public boolean isPortalNormalized() {
		return _wrappedModelObject.getPortal() != null ? _wrappedModelObject.getPortal().isNormalized() : false;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  GEOPOSITION 2D
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String POSITION_FIELD = "position";

	public GeoPosition2D getPosition() {
		return _wrappedModelObject.getPosition();
	}
	public void setPosition(final GeoPosition2D position) {
		_wrappedModelObject.setPosition(position);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ZIP CODE
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String ZIP_CODE_FIELD = "zipCode";
	
	public String getZipCode() {
		return _wrappedModelObject.getZipCode() != null 
			 && Strings.isNOTNullOrEmpty(_wrappedModelObject.getZipCode().asString()) ? _wrappedModelObject.getZipCode().getId() : null;
	}
	public void setZipCode(final String zipCode) {
		_wrappedModelObject.setZipCode(Strings.isNOTNullOrEmpty(zipCode) ? GeoZipCode.forId(zipCode) : null);
	}
}
