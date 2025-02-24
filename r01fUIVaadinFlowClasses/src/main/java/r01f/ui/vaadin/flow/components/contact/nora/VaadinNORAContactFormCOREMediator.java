package r01f.ui.vaadin.flow.components.contact.nora;

import java.util.Collection;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import r01f.ejie.nora.NORAService;
import r01f.ejie.nora.NORAService.NORANaming;
import r01f.locale.Language;
import r01f.patterns.ProviderFrom;
import r01f.types.geo.GeoCountry;
import r01f.types.geo.GeoCounty;
import r01f.types.geo.GeoLocality;
import r01f.types.geo.GeoLocation;
import r01f.types.geo.GeoLocationItem;
import r01f.types.geo.GeoMunicipality;
import r01f.types.geo.GeoOIDs.GeoCountryID;
import r01f.types.geo.GeoOIDs.GeoCountyID;
import r01f.types.geo.GeoOIDs.GeoID;
import r01f.types.geo.GeoOIDs.GeoLocalityID;
import r01f.types.geo.GeoOIDs.GeoMunicipalityID;
import r01f.types.geo.GeoOIDs.GeoPortalID;
import r01f.types.geo.GeoOIDs.GeoRegionID;
import r01f.types.geo.GeoOIDs.GeoStateID;
import r01f.types.geo.GeoOIDs.GeoStreetID;
import r01f.types.geo.GeoOIDs.GeoZipCode;
import r01f.types.geo.GeoPortal;
import r01f.types.geo.GeoPosition;
import r01f.types.geo.GeoPosition2D;
import r01f.types.geo.GeoPosition2D.GeoPositionStandard;
import r01f.types.geo.GeoPosition2DByStandard;
import r01f.types.geo.GeoState;
import r01f.types.geo.GeoStreet;
import r01f.ui.coremediator.UICOREMediator;
import r01f.util.types.collections.CollectionUtils;

@Singleton
public class VaadinNORAContactFormCOREMediator 
  implements UICOREMediator {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final NORAService _nora;
	
	@Inject
	public VaadinNORAContactFormCOREMediator(final NORAService noraService) {
		super();
		_nora = noraService;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public NORANaming getNORANamingIn(final Language lang) {
		return _nora.forNamesIn(lang);
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//	LOAD
/////////////////////////////////////////////////////////////////////////////////////////
	public Collection<GeoLocationItem<GeoCountryID>> loadCountries(final Language lang) {
		return _toGeoLocationItemCollection(_nora.getServicesForCountries()
												 .getCountries(),
											lang);
	}
	public Collection<GeoLocationItem<GeoStateID>> loadStates(final Language lang) {
		return _toGeoLocationItemCollection(_nora.getServicesForStates()
												 .getStatesOf(),
											lang);
	} 
	public Collection<GeoLocationItem<GeoCountyID>> loadCounties(final GeoStateID stateId,
																 final Language lang) {
		return _toGeoLocationItemCollection(_nora.getServicesForCounties()
											     .getCountiesOf(stateId),
											lang);
	} 
	public Collection<GeoLocationItem<GeoCountyID>> loadCounties(final GeoStateID stateId,final Collection<GeoCountyID> countyIds,
																 final Language lang) {
		// Load [counties] from the ids
		Collection<GeoCounty> counties = stateId != null 
									  && CollectionUtils.hasData(countyIds) ? countyIds.stream()
																					   .map(countyId -> _nora.getServicesForCounties()
																							 			     .getCounty(stateId,countyId))
																					   .collect(Collectors.toList())
																		    : Lists.newArrayList();
		return _toGeoLocationItemCollection(counties,
											lang);
	} 
	public Collection<GeoLocationItem<GeoRegionID>> loadRegions(final GeoCountyID countyId,
																final Language lang) {
		return _toGeoLocationItemCollection(_nora.getServicesForRegions()
												 .getRegionsOf(countyId),
											lang);
	} 
	public Collection<GeoLocationItem<GeoMunicipalityID>> loadMunicipalities(final GeoStateID stateId,final GeoCountyID countyId,
																			 final Language lang) {
		return _toGeoLocationItemCollection(_nora.getServicesForMunicipalities()
										  	     .getMunicipalitiesOf(stateId,countyId),
										  	lang);
	}
	public Collection<GeoLocationItem<GeoLocalityID>> loadLocalities(final GeoStateID stateId,final GeoCountyID countyId,final GeoMunicipalityID municipalityId,
																	 final Language lang) {
		return _toGeoLocationItemCollection(_nora.getServicesForLocalities()
										  		 .getLocalitiesOf(stateId,countyId,municipalityId),
										    lang);
	}
	public Collection<GeoLocationItem<GeoStreetID>> loadStreets(final GeoStateID stateId,final GeoCountyID countyId,final GeoMunicipalityID municipalityId,final GeoLocalityID localityId,
																final Language lang) {
		return _toGeoLocationItemCollection(_nora.getServicesForStreets()
											 	  .getStreetsOf(stateId,countyId,municipalityId,localityId),
											lang);
	}
	public Collection<GeoLocationItem<GeoStreetID>> findStreets(final GeoStateID stateId,final GeoCountyID countyId,final GeoMunicipalityID municipalityId,final GeoLocalityID localityId,
																final String text,
																final Language lang) {
		return _toGeoLocationItemCollection(_nora.getServicesForStreets()
											     .findStreetsWithTextOf(stateId,countyId,municipalityId,localityId,
											    		 				text),
											lang);
	}
	public Collection<GeoLocationItem<GeoPortalID>> loadPortals(final GeoLocalityID localityId,final GeoStreetID streetId,
																final Language lang) {
		return _toGeoLocationItemCollection(_nora.getServicesForPortal()
											 	 .getPortalsOf(localityId,streetId),
											lang);
	}
	private static <I extends GeoID,G extends GeoLocation<I>> 
			Collection<GeoLocationItem<I>> _toGeoLocationItemCollection(final Collection<G> geoLocations,
																	    final Language lang) {
		return _toGeoLocationItemCollection(geoLocations,
											item -> NORAService.getName(item,lang),
											lang);
	}
	private static <I extends GeoID,G extends GeoLocation<I>> 
			Collection<GeoLocationItem<I>> _toGeoLocationItemCollection(final Collection<G> geoLocations,
																		final ProviderFrom<G,String> nameProvider,
																		final Language lang) {
		return CollectionUtils.hasData(geoLocations) ? geoLocations.stream()
															 	   .map(item -> new GeoLocationItem<>(item.getId(),nameProvider.from(item)))
															 	   .collect(Collectors.toList())
													 : Lists.newArrayList();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	FIND
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoCounty loadCounty(final GeoStateID stateId,final GeoCountyID countyId) {
		GeoCounty county = _nora.getServicesForCounties().getCounty(stateId, countyId);
		return county;
	}
	public GeoMunicipality loadMunicipality(final GeoStateID stateId,final GeoCountyID countyId,final GeoMunicipalityID munId) {
		GeoMunicipality municipality = _nora.getServicesForMunicipalities().getMunicipality(stateId, countyId, munId);
		return municipality;
	}
	public GeoStreet loadStreet(final GeoStateID stateId,final GeoCountyID countyId,final GeoMunicipalityID munId,final GeoStreetID streetId) {
		GeoStreet street = _nora.getServicesForStreets().getStreet(stateId, countyId, munId, streetId);
		return street;
	}
	public GeoPortal loadPortal(final GeoStateID stateId,final GeoCountyID countyId,final GeoMunicipalityID munId,final GeoLocalityID localityId,final GeoStreetID streetId,final GeoPortalID portalId) {
		GeoPortal portal = _nora.getServicesForPortal().getPortal(stateId, countyId, munId, localityId, streetId, portalId);
		return portal;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SEARCH
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoPosition searchByZipCode(final String zipCode,
									   final Language lang) {
		Collection<GeoLocality> locs = _nora.getServicesForLocalities().getLocalitybyZipCode(GeoZipCode.forId(zipCode));
		GeoLocality loc = locs.iterator().next();
		GeoPosition geo = _loadFromLocality(loc,
											lang);
		if (locs.size() > 2) { //look if municipality id are the same or not 
			geo.setLocality(null);
			boolean diferentMun  = locs.stream()
								   .anyMatch(curLoc -> !curLoc.getMunicipalityId().equals(loc.getMunicipalityId()));
			if (diferentMun) {
				geo.setMunicipality(null);
			}
		}
		return geo;
	}
	public GeoPosition searchByGeoPosition2D(final GeoPosition2D geoPosition2D,
											 final Language lang) {
		GeoLocality loc = _nora.getServicesForLocalities().getLocalitybyGeoPosition2D(geoPosition2D);
		return _loadFromLocality(loc,lang);
	}
	private GeoPosition _loadFromLocality(final GeoLocality loc,
										  final Language lang) {
		GeoPosition geo = new GeoPosition();
		geo.setLocality(loc.asLocationItemIn(lang));
		GeoCountry country = _nora.getServicesForCountries().getCountry(loc.getCountryId());
		geo.setCountry(country.asLocationItemIn(lang));
		if (loc.getStateId() != null) {
			GeoState state = _nora.getServicesForStates().getState(loc.getStateId());
			geo.setState(state.asLocationItemIn(lang));
			if (loc.getCountyId() != null) {
				GeoCounty county = _nora.getServicesForCounties().getCounty(loc.getStateId(), loc.getCountyId());
				geo.setCounty(county.asLocationItemIn(lang));
				if (loc.getMunicipalityId() != null) {
					GeoMunicipality mun = _nora.getServicesForMunicipalities().getMunicipality(loc.getStateId(), loc.getCountyId(), loc.getMunicipalityId());
					geo.setMunicipality(mun.asLocationItemIn(lang));
				}
			}
		}
		return geo;
	}
	public <I extends GeoID> GeoPosition2D searchGeoPosition2D(final I oid,final GeoCountyID countyId) {
		GeoPosition2D geoPosition2D = new GeoPosition2D();
		if (oid instanceof GeoCountryID) {
			geoPosition2D = _nora.getServicesForCountries()
								 .getCountry((GeoCountryID)oid)
								 .getPosition2D();
		} else if (oid instanceof GeoStateID) {
			geoPosition2D = _nora.getServicesForStates()
								 .getState((GeoStateID)oid)
								 .getPosition2D();
		} else if (oid instanceof GeoCountyID) {
			geoPosition2D =  _nora.getServicesForCounties()
								  .getCounty(null,(GeoCountyID)oid)
								  .getPosition2D();
		} else if (oid instanceof GeoMunicipalityID) {
			geoPosition2D =  _nora.getServicesForMunicipalities()
								  .getMunicipality(null,countyId,(GeoMunicipalityID)oid)
								  .getPosition2D();
		} else if (oid instanceof GeoLocalityID) {
			geoPosition2D =  _nora.getServicesForLocalities()
								  .getLocality(null,null,null,(GeoLocalityID)oid)
								  .getPosition2D();
		} else if (oid instanceof GeoStreetID) {
			geoPosition2D =  _nora.getServicesForStreets()
								  .getStreet(null,null,null,(GeoStreetID)oid)
								  .getPosition2D();
		} else if (oid instanceof GeoPortalID) {
			geoPosition2D =  _nora.getServicesForPortal()
								  .getPortal(null,null,null,null,null,(GeoPortalID)oid)
								  .getPosition2D();
		}
		return geoPosition2D;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GeoPosition2D Transform
/////////////////////////////////////////////////////////////////////////////////////////	
	public GeoPosition2D getGeoPositionFromETRS89toED50(final GeoPosition2D geoPosition2D) {
		GeoPosition2D outGeoPosition2D = _nora.getServicesForGeoPosition2D().getGeoPositionFromETRS89toED50(geoPosition2D);
		return outGeoPosition2D;
	}
	public GeoPosition2D getGeoPositionFromED50toETRS89(final GeoPosition2D geoPosition2D) {
		GeoPosition2D outGeoPosition2D = _nora.getServicesForGeoPosition2D().getGeoPositionFromED50toETRS89(geoPosition2D);
		return outGeoPosition2D;
	}
	public GeoPosition2D getGeoPositionFromED50toWGS84(final GeoPosition2D geoPosition2D) {
		GeoPosition2D outGeoPosition2D = _nora.getServicesForGeoPosition2D().getGeoPositionFromED50toWGS84(geoPosition2D);
		return outGeoPosition2D;
	}
	public GeoPosition2D getGeoPositionFromWGS84toED50(final GeoPosition2D geoPosition2D) {
		GeoPosition2D outGeoPosition2D = _nora.getServicesForGeoPosition2D().getGeoPositionFromWGS84toED50(geoPosition2D);
		return outGeoPosition2D;
	}
	public GeoPosition2DByStandard getGeoPosition2DByStandard(final GeoPosition2D geoPosition2D) {
		GeoPosition2DByStandard geoPosition2DByStandard = new GeoPosition2DByStandard();
		geoPosition2DByStandard.add(geoPosition2D);
		if (geoPosition2D.getStandard().equals(GeoPositionStandard.ETRS89)) {
			GeoPosition2D geoPosition2DED50 = getGeoPositionFromETRS89toED50(geoPosition2D);
			geoPosition2DByStandard.add(geoPosition2DED50);
			GeoPosition2D geoPosition2DWGS84 = getGeoPositionFromED50toWGS84(geoPosition2DED50);
			geoPosition2DByStandard.add(geoPosition2DWGS84);
		} else if (geoPosition2D.getStandard().equals(GeoPositionStandard.ED50)) {
			GeoPosition2D geoPosition2DWGS84 = getGeoPositionFromED50toWGS84(geoPosition2D);
			geoPosition2DByStandard.add(geoPosition2DWGS84);
			GeoPosition2D geoPosition2DETRS89 = getGeoPositionFromED50toETRS89(geoPosition2D);
			geoPosition2DByStandard.add(geoPosition2DETRS89);
		} else if (geoPosition2D.getStandard().equals(GeoPositionStandard.GOOGLE)) {
			GeoPosition2D geoPosition2DED50 = getGeoPositionFromWGS84toED50(geoPosition2D);
			geoPosition2DByStandard.add(geoPosition2DED50);
			GeoPosition2D geoPosition2DETRS89 = getGeoPositionFromED50toETRS89(geoPosition2DED50);
			geoPosition2DByStandard.add(geoPosition2DETRS89);
		}
		return geoPosition2DByStandard;
	}
}
