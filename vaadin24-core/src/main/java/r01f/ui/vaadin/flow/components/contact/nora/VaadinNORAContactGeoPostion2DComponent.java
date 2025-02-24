package r01f.ui.vaadin.flow.components.contact.nora;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;

import lombok.experimental.Accessors;
import r01f.types.geo.GeoPosition2D;
import r01f.types.geo.GeoPosition2D.GeoPositionStandard;
import r01f.types.geo.GeoPosition2DByStandard;
import r01f.ui.i18n.UII18NService;
import r01f.ui.presenter.UIPresenterSubscriber;
import r01f.ui.vaadin.flow.annotations.VaadinViewComponentLabels;
import r01f.ui.vaadin.flow.components.VaadinComponent;
import r01f.ui.vaadin.flow.components.label.VaadinInfo;
import r01f.util.types.Strings;

@Accessors(prefix="_")
public class VaadinNORAContactGeoPostion2DComponent 
     extends CustomField<GeoPosition2D> 
  implements VaadinComponent {
	private static final long serialVersionUID = -7048701253834066457L;
/////////////////////////////////////////////////////////////////////////////////////////
//	SERVICES
/////////////////////////////////////////////////////////////////////////////////////////
	private final transient UII18NService _i18n;
	private final transient VaadinNORAContactFormPresenter _presenter;
/////////////////////////////////////////////////////////////////////////////////////////
//	FORM FIELDs
/////////////////////////////////////////////////////////////////////////////////////////
	@VaadinViewComponentLabels(captionI18NKey="geo.coords",
							   useCaptionI18NKeyAsPlaceHolderKey=true)
	private final TextField _txtCoords2D = new TextField();
	
	@VaadinViewComponentLabels(captionI18NKey="geo.coords.standard",
							   useCaptionI18NKeyAsPlaceHolderKey=true)
	private final ComboBox<GeoPositionStandard> _cmbCoordsStandard2D = new ComboBox<>();
	
	
	private final Button _btnSearchByGeoPosition2D = new Button();
	private final VaadinInfo _lblInfo = new VaadinInfo();
/////////////////////////////////////////////////////////////////////////////////////////
//	STATUS (avoid as much as possible)
/////////////////////////////////////////////////////////////////////////////////////////	
	private GeoPosition2DByStandard _geoPosition2DByStandard;
	private boolean _coords2DListener = true;
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
		
		////////// UI
		_btnSearchByGeoPosition2D.setIcon(new Icon(VaadinIcon.SEARCH));
		_btnSearchByGeoPosition2D.setWidth(50,Unit.PIXELS);
		
		_txtCoords2D.setWidthFull();
		_cmbCoordsStandard2D.setWidthFull();
		
		_cmbCoordsStandard2D.setItems(GeoPositionStandard.values());
		_cmbCoordsStandard2D.setItemLabelGenerator(item -> _i18n.getMessage("geo.coords."+item.name().toLowerCase()));
		_cmbCoordsStandard2D.setRequired(true);
		if (_cmbCoordsStandard2D.getValue() == null) _cmbCoordsStandard2D.setValue(GeoPositionStandard.ETRS89);
		
		_lblInfo.setIcon(new Icon(VaadinIcon.INFO));
		_lblInfo.setWidth(30,Unit.PIXELS);
		
		// Layout
		HorizontalLayout hl2D = new HorizontalLayout(_txtCoords2D,_btnSearchByGeoPosition2D);
		hl2D.setSpacing(false);
		hl2D.setWidthFull();
		
		HorizontalLayout hlStandard = new HorizontalLayout(_cmbCoordsStandard2D,_lblInfo);
		hlStandard.setSpacing(false);
		hlStandard.setWidthFull();
		
		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidthFull();
		hl.add(hlStandard,hl2D);
		
		////////// Behavior
		if (_coords2DListener) _showInfo();
		_setBehavior();
	}
	private void _setBehavior() {
		_txtCoords2D.addValueChangeListener(event -> {
												_geoPosition2DByStandard = null; // reset values
												if (_coords2DListener) _showInfo();
												if (_listener != null) _listener.onCoordsChange(this);
										   });
		_cmbCoordsStandard2D.addValueChangeListener(event -> {
														if (_geoPosition2DByStandard != null 
														 && _geoPosition2DByStandard.contains(_cmbCoordsStandard2D.getValue())) {
															GeoPosition2D geoPosition2D = _geoPosition2DByStandard.get(_cmbCoordsStandard2D.getValue());
															_coords2DListener = false;
															_txtCoords2D.setValue(geoPosition2D.getX() +", "+ geoPosition2D.getY());
															_coords2DListener = true;
															loadGeoPosition2DByStandard();
														}
												 	});
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected GeoPosition2D generateModelValue() {
		GeoPosition2D value = new GeoPosition2D(_cmbCoordsStandard2D.getValue());
		String values = _txtCoords2D.getValue();
		if (Strings.isNOTNullOrEmpty(values) && values.indexOf(",") != -1) {
			String[] valueSplit = values.split(",");
			value.setX(Double.valueOf(valueSplit[0]));
			value.setY(Double.valueOf(valueSplit[1]));
		}
		return value;
	}
	@Override
	protected void setPresentationValue(final GeoPosition2D value) {
		if (value != null) {
			_cmbCoordsStandard2D.setValue(value.getStandard() != null ? value.getStandard() : GeoPositionStandard.ETRS89 );
			if (value.getX() != 0 && value.getY() != 0) {
				_txtCoords2D.setValue(value.getX() + ", " + value.getY());
				_showInfo();
			} else {
				_txtCoords2D.setValue("");
			}
		}
		if (_listener != null) _listener.onCoordsChange(this);
	}
	public String getCoords2DAsString() {
		return _txtCoords2D.getValue(); 
	}
	public GeoPosition2D getGeoPositionForETRS89Standard() {
		GeoPosition2D geoPosition = null;
		if (_cmbCoordsStandard2D != null 
		 && _cmbCoordsStandard2D.getValue() != null 
		 && _cmbCoordsStandard2D.getValue().equals(GeoPositionStandard.GOOGLE)) {
			geoPosition = getValue();
		} else if (_geoPosition2DByStandard == null) {
			this.loadGeoPosition2DByStandard();
		} 
		if (_geoPosition2DByStandard != null 
		 && _geoPosition2DByStandard.contains(GeoPositionStandard.GOOGLE)) {
			geoPosition = _geoPosition2DByStandard.get(GeoPositionStandard.GOOGLE);
		}
		return geoPosition;
	}
	public void loadGeoPosition2DByStandard() {
		String valueStr = _txtCoords2D.getValue();
		if (Strings.isNOTNullOrEmpty(valueStr) 
	     && valueStr.indexOf(",") != -1) {
			String[] value = valueStr.split(",");
			GeoPosition2D geoPosition2D = new GeoPosition2D(_cmbCoordsStandard2D.getValue(), Double.parseDouble(value[0].trim()), Double.parseDouble(value[1].trim()));
			if (geoPosition2D.getStandard() != null 
			 && geoPosition2D.getX() != 0 && geoPosition2D.getY() != 0) {
				_presenter.onTransformGeoPositionByStatandard(geoPosition2D,
															  UIPresenterSubscriber.from(
																  // on success
																  outGeoPosition2DByStandard -> _geoPosition2DByStandard = outGeoPosition2DByStandard,
																  // on error
												  				  th -> Notification.show(th.getMessage())
												  			  ));
			}
		}
	}
	private void _showInfo() {
		this.loadGeoPosition2DByStandard();
		
		if (_geoPosition2DByStandard != null) {
			UnorderedList ul = new UnorderedList();
			_geoPosition2DByStandard.forEach(
											(outGeoPosition2DStandard,outGeoPosition2D) -> {
												ul.add(new ListItem(Strings.customized("{} x,y={},{}",
																					   outGeoPosition2DStandard.name(),
																					   outGeoPosition2D.getX(),outGeoPosition2D.getY())));
											 });
			_lblInfo.setInfoComponent(ul);
		}
	}
	@Override
	public void clear() {
		_txtCoords2D.setValue("");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
//		_coordsStandard2D.setEnabled(enabled);
//		_coords2D.setEnabled(enabled);
//		_searchByGeoPosition2DBtn.setEnabled(enabled);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public Registration addSearchByGeoPosition2DBtnClickListener(final ComponentEventListener<ClickEvent<Button>> listener) {
		return _btnSearchByGeoPosition2D.addClickListener(listener);
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
}
