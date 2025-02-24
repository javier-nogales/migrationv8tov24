package r01f.ui.vaadin.flow.components.contact.nora;

import java.util.Collection;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import r01f.ejie.nora.NORAService.NORANaming;
import r01f.locale.Language;
import r01f.types.geo.GeoCounty;
import r01f.types.geo.GeoLocationItem;
import r01f.types.geo.GeoMunicipality;
import r01f.types.geo.GeoOIDs.GeoCountryID;
import r01f.types.geo.GeoOIDs.GeoCountyID;
import r01f.types.geo.GeoOIDs.GeoIDBase;
import r01f.types.geo.GeoOIDs.GeoLocalityID;
import r01f.types.geo.GeoOIDs.GeoMunicipalityID;
import r01f.types.geo.GeoOIDs.GeoPortalID;
import r01f.types.geo.GeoOIDs.GeoStateID;
import r01f.types.geo.GeoOIDs.GeoStreetID;
import r01f.types.geo.GeoPortal;
import r01f.types.geo.GeoPosition2D;
import r01f.types.geo.GeoPosition2DByStandard;
import r01f.types.geo.GeoStreet;
import r01f.ui.presenter.UIPresenter;
import r01f.ui.presenter.UIPresenterSubscriber;

@Singleton
public class VaadinNORAContactFormPresenter 
  implements UIPresenter {
	private static final long serialVersionUID = 5141850348403136735L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final VaadinNORAContactFormCOREMediator _mediator;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public VaadinNORAContactFormPresenter(final VaadinNORAContactFormCOREMediator mediator) {
		_mediator = mediator;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	NAMING
/////////////////////////////////////////////////////////////////////////////////////////
	public NORANaming getNORANamingIn(final Language lang) {
		return _mediator.getNORANamingIn(lang);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	COMBO LOAD
/////////////////////////////////////////////////////////////////////////////////////////	
	public void onCountriesLoadRequested(final Language lang,
										 final UIPresenterSubscriber<Collection<GeoLocationItem<GeoCountryID>>> subscriber) {
		try  {
			Collection<GeoLocationItem<GeoCountryID>> contries = _mediator.loadCountries(lang);
			subscriber.onSuccess(contries);
		} catch (Throwable th) {
			subscriber.onError(th);
		}
	}
	public void onStatesLoadRequested(final Language lang,
									  final UIPresenterSubscriber<Collection<GeoLocationItem<GeoStateID>>> subscriber) {
		try  {
			Collection<GeoLocationItem<GeoStateID>> states = _mediator.loadStates(lang);
			subscriber.onSuccess(states);
		} catch (Throwable th) {
			subscriber.onError(th);
		}
	}
	public void onCountiesLoadRequested(final GeoStateID stateId,
										final Language lang,
									    final UIPresenterSubscriber<Collection<GeoLocationItem<GeoCountyID>>> subscriber) {
		try  {
			Collection<GeoLocationItem<GeoCountyID>> counties = _mediator.loadCounties(stateId,
																					   lang);
			subscriber.onSuccess(counties);
		} catch (Throwable th) {
			subscriber.onError(th);
		}
	}
	public void onCountiesLoadRequested(final GeoStateID stateId,final Collection<GeoCountyID> countyIds,
										final Language lang,
										final UIPresenterSubscriber<Collection<GeoLocationItem<GeoCountyID>>> subscriber) {
		try  {
			Collection<GeoLocationItem<GeoCountyID>> counties = _mediator.loadCounties(stateId,countyIds,
																					   lang);
			subscriber.onSuccess(counties);
		} catch (Throwable th) {
			subscriber.onError(th);
		}
	}
	public void onMunicipalitiesLoadRequested(final GeoStateID stateId,final GeoCountyID countyId,
											  final Language lang,
									          final UIPresenterSubscriber<Collection<GeoLocationItem<GeoMunicipalityID>>> subscriber) {
		try  {
			Collection<GeoLocationItem<GeoMunicipalityID>> muns = _mediator.loadMunicipalities(stateId,countyId,
																		    				   lang);
			subscriber.onSuccess(muns);
		} catch (Throwable th) {
			subscriber.onError(th);
		}
	}
	public void onLocalitiesLoadRequested(final GeoStateID stateId,final GeoCountyID countyId,final GeoMunicipalityID municipalityId,
										  final Language lang,
									      final UIPresenterSubscriber<Collection<GeoLocationItem<GeoLocalityID>>> subscriber) {
		try  {
			Collection<GeoLocationItem<GeoLocalityID>> loc = _mediator.loadLocalities(stateId,countyId,municipalityId,
																  					  lang);
			subscriber.onSuccess(loc);
		} catch (Throwable th) {
			subscriber.onError(th);
		}
	}
	public void onFindStreetsRequested(final GeoStateID stateId,final GeoCountyID countyId,final GeoMunicipalityID municipalityId,final GeoLocalityID localityId,
									   final String text,
									   final Language lang,
									   final UIPresenterSubscriber<Collection<GeoLocationItem<GeoStreetID>>> subscriber) {
		try  {
			Collection<GeoLocationItem<GeoStreetID>> streets = this.findStreets(stateId,countyId,municipalityId,localityId,
																				text,
																  				lang);
			subscriber.onSuccess(streets);
		} catch (Throwable th) {
			subscriber.onError(th);
		}
	}
	public void onStreetLoadRequested(final GeoStateID stateId,final GeoCountyID countyId,final GeoMunicipalityID municipalityId,final GeoLocalityID localityId,
									  final Language lang,
									  final UIPresenterSubscriber<Collection<GeoLocationItem<GeoStreetID>>> subscriber) {
		try  {
			Collection<GeoLocationItem<GeoStreetID>> streets = _mediator.loadStreets(stateId,countyId,municipalityId,localityId,
																  					 lang);
			subscriber.onSuccess(streets);
		} catch (Throwable th) {
			subscriber.onError(th);
		}
	}
	public void onPortalLoadRequested(final GeoLocalityID localityId,final GeoStreetID streertId,
									  final Language lang,
									  final UIPresenterSubscriber<Collection<GeoLocationItem<GeoPortalID>>> subscriber) {
		try  {
			Collection<GeoLocationItem<GeoPortalID>> portals = _mediator.loadPortals(localityId,streertId,
																  					 lang);
			subscriber.onSuccess(portals);
		} catch (Throwable th) {
			subscriber.onError(th);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	FIND
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoCounty getCounty(final GeoStateID stateId,final GeoCountyID countyId) {
		GeoCounty county = _mediator.loadCounty(stateId, countyId);
		return county;
	}
	public GeoMunicipality getMunicipality(final GeoStateID stateId,final GeoCountyID countyId,final GeoMunicipalityID munId) {
		GeoMunicipality municipality = _mediator.loadMunicipality(stateId, countyId, munId);
		return municipality;
	}
	public GeoStreet getStreet(final GeoStateID stateId,final GeoCountyID countyId,final GeoMunicipalityID munId, final GeoStreetID streetId) {
		GeoStreet street = _mediator.loadStreet(stateId, countyId, munId, streetId);
		return street;
	}
	public GeoPortal getPortal(final GeoStateID stateId,final GeoCountyID countyId,final GeoMunicipalityID munId, final GeoLocalityID localityId, final GeoStreetID streetId, final GeoPortalID portalId) {
		GeoPortal portal = _mediator.loadPortal(stateId, countyId, munId, localityId, streetId, portalId);
		return portal;
	}
	public Collection<GeoLocationItem<GeoStreetID>> findStreets(final GeoStateID stateId,final GeoCountyID countyId,final GeoMunicipalityID municipalityId,final GeoLocalityID localityId,
									   							final String text,
									   							final Language lang) {
		return _mediator.findStreets(stateId,countyId,municipalityId,localityId,
									 text,
				  					 lang);
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//	SEARCH
/////////////////////////////////////////////////////////////////////////////////////////	
	public void onSearchByZipRequested(final String zipCode,
									    final Language lang, 
									   final UIPresenterSubscriber<VaadinViewGeoPosition> subscriber) {
		
		try  {
			VaadinViewGeoPosition noraContactViewObject = new VaadinViewGeoPosition(_mediator.searchByZipCode(zipCode,lang));
			subscriber.onSuccess(noraContactViewObject);
		} catch (Throwable th) {
			subscriber.onError(th);
		}
	}
	public void onSearchByGeoPosition2D(final GeoPosition2D geoPosition2D, 
										final Language lang,
										final UIPresenterSubscriber<VaadinViewGeoPosition> subscriber ) {
		try  {
			VaadinViewGeoPosition noraContactViewObject = new VaadinViewGeoPosition(_mediator.searchByGeoPosition2D(geoPosition2D,lang));
			subscriber.onSuccess(noraContactViewObject);
		} catch (Throwable th) {
			subscriber.onError(th);
		}
	}
	public <OID extends GeoIDBase>void onSearchPosition2D(final OID oid,final GeoCountyID countyId,
														  final UIPresenterSubscriber<GeoPosition2D> subscriber) {
		try  {
			GeoPosition2D geoPosition2D = _mediator.searchGeoPosition2D(oid, countyId);
			subscriber.onSuccess(geoPosition2D);
		} catch (Throwable th) {
			subscriber.onError(th);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GeoPosition2D Transform
/////////////////////////////////////////////////////////////////////////////////////////	
	public void onTransformGeoPositionFromETRS89toED50(final GeoPosition2D geoPosition2D,
													   final UIPresenterSubscriber<GeoPosition2D> subscriber) {
		try  {
			GeoPosition2D outGeoPosition2D = _mediator.getGeoPositionFromETRS89toED50(geoPosition2D);
			subscriber.onSuccess(outGeoPosition2D);
		} catch (Throwable th) {
			subscriber.onError(th);
		}
	}
	public void onTransformGeoPositionFromED50toETRS89(final GeoPosition2D geoPosition2D,
													   final UIPresenterSubscriber<GeoPosition2D> subscriber) {
		try  {
			GeoPosition2D outGeoPosition2D = _mediator.getGeoPositionFromED50toETRS89(geoPosition2D);
			subscriber.onSuccess(outGeoPosition2D);
		} catch (Throwable th) {
			subscriber.onError(th);
		}
	}
	public void onTransformGeoPositionFromED50toWGS84(final GeoPosition2D geoPosition2D,
													  final UIPresenterSubscriber<GeoPosition2D> subscriber) {
		try  {
			GeoPosition2D outGeoPosition2D = _mediator.getGeoPositionFromED50toWGS84(geoPosition2D);
			subscriber.onSuccess(outGeoPosition2D);
		} catch (Throwable th) {
			subscriber.onError(th);
		}
	}
	public void onTransformGeoPositionFromWGS84toED50(final GeoPosition2D geoPosition2D,
													  final UIPresenterSubscriber<GeoPosition2D> subscriber) {
		try  {
			GeoPosition2D outGeoPosition2D = _mediator.getGeoPositionFromWGS84toED50(geoPosition2D);
			subscriber.onSuccess(outGeoPosition2D);
		} catch (Throwable th) {
			subscriber.onError(th);
		}
	}
	public void onTransformGeoPositionByStatandard(final GeoPosition2D geoPosition2D,
												   final UIPresenterSubscriber<GeoPosition2DByStandard> subscriber) {
		try  {
			GeoPosition2DByStandard outGeoPosition2DByStandard = _mediator.getGeoPosition2DByStandard(geoPosition2D);
			subscriber.onSuccess(outGeoPosition2DByStandard);
		} catch (Throwable th) {
			subscriber.onError(th);
		}
	}
}
