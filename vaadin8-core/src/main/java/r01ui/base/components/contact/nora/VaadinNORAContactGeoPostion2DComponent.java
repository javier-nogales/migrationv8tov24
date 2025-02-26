package r01ui.base.components.contact.nora;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.types.geo.GeoPosition2D;
import r01f.types.geo.GeoPosition2D.GeoPositionStandard;
import r01f.types.geo.GeoPosition2DByStandard;
import r01f.ui.i18n.UII18NService;
import r01f.ui.presenter.UIPresenterSubscriber;
import r01f.ui.vaadin.annotations.VaadinViewComponentLabels;
import r01f.ui.vaadin.view.VaadinComponent;
import r01f.util.types.Strings;
import r01ui.base.components.form.VaadinViewTracksChanges;

@Accessors(prefix="_")
public class VaadinNORAContactGeoPostion2DComponent 
     extends CustomField<GeoPosition2D> 
  implements VaadinComponent,
  			 VaadinViewTracksChanges{
	private static final long serialVersionUID = -7048701253834066457L;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private final transient UII18NService _i18n;
	private final transient VaadinNORAContactFormPresenter _presenter;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDs
/////////////////////////////////////////////////////////////////////////////////////////
	@VaadinViewComponentLabels(captionI18NKey="geo.coords",
							   useCaptionI18NKeyAsPlaceHolderKey=true)
	@Getter private final TextField _coords2D = new TextField();
	
	@VaadinViewComponentLabels(captionI18NKey="geo.coords.standard",
							   useCaptionI18NKeyAsPlaceHolderKey=true)
	@Getter private final ComboBox<GeoPositionStandard> _coordsStandard2D = new ComboBox<>();
	@Getter private final Button _searchByGeoPosition2DBtn = new Button();
	@Getter private final Label _info = new Label();
/////////////////////////////////////////////////////////////////////////////////////////
//	STATUS (avoid as much as possible)
/////////////////////////////////////////////////////////////////////////////////////////
	private GeoPosition2DByStandard _geoPosition2DByStandard;
	private boolean _coords2DListener = true;
	private boolean _changed = false;
/////////////////////////////////////////////////////////////////////////////////////////
//	LISTENERS
/////////////////////////////////////////////////////////////////////////////////////////	
	private VaadinNORACoordsChangeListener _listener = null;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinNORAContactGeoPostion2DComponent (final UII18NService i18n,
												   final VaadinNORAContactFormPresenter presenter) {
		super();
		_i18n = i18n;
		_presenter = presenter;
	}
	@Override
	protected Component initContent() {
		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth(100, Unit.PERCENTAGE);
		_searchByGeoPosition2DBtn.setIcon(VaadinIcons.SEARCH);
		_searchByGeoPosition2DBtn.setWidth(50,Unit.PIXELS);
		_coords2D.setWidth(100, Unit.PERCENTAGE);
		_coordsStandard2D.setWidth(100, Unit.PERCENTAGE);
		_info.setValue(VaadinIcons.INFO.getHtml());
		_info.setContentMode(ContentMode.HTML);
		_info.setDescription("", ContentMode.HTML);
		_info.setWidth(30,Unit.PIXELS);
		HorizontalLayout hl2D = new HorizontalLayout(_coords2D,_searchByGeoPosition2DBtn);
		hl2D.setComponentAlignment(_searchByGeoPosition2DBtn, Alignment.BOTTOM_LEFT);
		hl2D.setSpacing(false);
		hl2D.setWidth(100, Unit.PERCENTAGE);
		hl2D.setExpandRatio(_searchByGeoPosition2DBtn, 1);
		hl2D.setExpandRatio(_coords2D, 3);
		_coordsStandard2D.setItems(GeoPositionStandard.values());
		_coordsStandard2D.setItemCaptionGenerator(item -> _i18n.getMessage("geo.coords."+item.name().toLowerCase()));
		_coordsStandard2D.setEmptySelectionAllowed(false);
		if	(_coordsStandard2D.getValue() == null) {
			_coordsStandard2D.setValue(GeoPositionStandard.ETRS89);
		}
		HorizontalLayout hlStandard = new HorizontalLayout(_coordsStandard2D,_info);
		hlStandard.setComponentAlignment(_info, Alignment.BOTTOM_LEFT);
		hlStandard.setSpacing(false);
		hlStandard.setWidth(100, Unit.PERCENTAGE);
		hlStandard.setExpandRatio(_coordsStandard2D, 3.5F);
		hlStandard.setExpandRatio(_info, 0.5F);
		hl.addComponents(hlStandard, hl2D);
		hl.setExpandRatio(hlStandard, 2);
		hl.setExpandRatio(hl2D, 3);
		_coords2D.addValueChangeListener(event -> {
													_geoPosition2DByStandard = null; //reset values
													if (_coords2DListener)
															_showInfo();
													if (_listener != null) _listener.onCoordsChange(this);
													setViewDataChanged(true);
										});
		_coordsStandard2D.addValueChangeListener(event -> {
																if (_geoPosition2DByStandard != null && _geoPosition2DByStandard.contains(_coordsStandard2D.getValue())) {
																	GeoPosition2D geoPosition2D = _geoPosition2DByStandard.get(_coordsStandard2D.getValue());
																	_coords2DListener = false;
																	_coords2D.setValue(geoPosition2D.getX() +", "+ geoPosition2D.getY());
																	_coords2DListener = true;
																	loadGeoPosition2DByStandard();
																}
																setViewDataChanged(true);
														  });
		if (_coords2DListener) {
			_showInfo();
		}
		return hl;
	}
	
	@Override
	public GeoPosition2D getValue() {
		GeoPosition2D value = new GeoPosition2D(_coordsStandard2D.getValue());
		String values = _coords2D.getValue();
		if (Strings.isNOTNullOrEmpty(values) && values.indexOf(",") != -1) {
			String[] valueSplit = values.split(",");
			value.setX(Double.valueOf(valueSplit[0]));
			value.setY(Double.valueOf(valueSplit[1]));
		}
		
		return value;
	}
	@Override
	protected void doSetValue(final GeoPosition2D value) {
		if (value != null) {
			_coordsStandard2D.setValue(value.getStandard() != null ? value.getStandard() : GeoPositionStandard.ETRS89 );
			if (value.getX() != 0 && value.getY() != 0) {
				_coords2D.setValue(value.getX() +", "+value.getY());
				_showInfo();
			} else {
				_coords2D.setValue("");
			}
		}
		if (_listener != null) _listener.onCoordsChange(this);
	}
	
	public GeoPosition2D getGeoPositionForETRS89Standard() {
		GeoPosition2D geoPosition = null;
		if (_coordsStandard2D != null && _coordsStandard2D.getValue() != null && _coordsStandard2D.getValue().equals(GeoPositionStandard.GOOGLE)) {
			geoPosition = getValue();
		} else if (_geoPosition2DByStandard == null) {
			loadGeoPosition2DByStandard();
		} 
		if (_geoPosition2DByStandard != null && _geoPosition2DByStandard.contains(GeoPositionStandard.GOOGLE)) {
			geoPosition =  _geoPosition2DByStandard.get(GeoPositionStandard.GOOGLE);
		}
		return geoPosition;
	}
	
	public void loadGeoPosition2DByStandard() {
		String valueStr = _coords2D.getValue();
		if (Strings.isNOTNullOrEmpty(valueStr) && valueStr.indexOf(",") != -1) {
			String[] value = valueStr.split(",");
			GeoPosition2D geoPosition2D = new GeoPosition2D(_coordsStandard2D.getValue(), Double.parseDouble(value[0].trim()), Double.parseDouble(value[1].trim()));
			if (geoPosition2D.getStandard() != null && geoPosition2D.getX() != 0 && geoPosition2D.getY() != 0) {
				_presenter.onTransformGeoPositionByStatandard(geoPosition2D,
															  UIPresenterSubscriber.from(
															  //onsuccess
															  outGeoPosition2DByStandard ->_geoPosition2DByStandard = outGeoPosition2DByStandard
															  ,
															  // on error
											  				  th -> {
											  					  		Notification.show(th.getMessage(),
											  								 		      Type.ERROR_MESSAGE);
											  				  }));
			}
		}
		
	}
	
	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
//		_coordsStandard2D.setEnabled(enabled);
//		_coords2D.setEnabled(enabled);
//		_searchByGeoPosition2DBtn.setEnabled(enabled);
		
	}
	
	private void _printInfo() {
		_info.setDescription("", ContentMode.HTML);
		if (_geoPosition2DByStandard != null) {
			_info.setDescription("<ul>", ContentMode.HTML);
			_geoPosition2DByStandard.forEach(
											(outGeoPosition2DStandard,
											 outGeoPosition2D) -> {
												 	String html = _info.getValue();
													html = "<li>";
												 	html += outGeoPosition2DStandard.name();
												 	html += " x: ";
												 	html += outGeoPosition2D.getX();
												 	html += " y: ";
												 	html += outGeoPosition2D.getY();
												 	html += "</li>";
												 	_info.setDescription(_info.getDescription()+ html, ContentMode.HTML);
											 });
			_info.setDescription(_info.getDescription()+ "</ul>", ContentMode.HTML);
		}
	}
	
	private void _showInfo() {
		loadGeoPosition2DByStandard();
		_printInfo();
	}
	
	@Override
	public void clear() {
		_coords2D.setValue("");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EVENTS                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	public void setVaadinNORACoordsChangeListener(final VaadinNORACoordsChangeListener listener) {
		_listener = listener;
	}	
	
	public interface VaadinNORACoordsChangeListener {
		public void onCoordsChange(final VaadinNORAContactGeoPostion2DComponent component );
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
		return _changed;
	}
}
