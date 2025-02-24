package r01ui.base.components.contact.nora;

import java.util.Collection;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.server.Resource;
import com.vaadin.shared.Registration;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import lombok.experimental.Accessors;
import r01f.types.geo.GeoLocationItem;
import r01f.types.geo.GeoOIDs.GeoID;
import r01f.ui.vaadin.view.VaadinComponent;
import r01f.util.types.Strings;
import r01ui.base.components.form.VaadinViewTracksChanges;

@Accessors(prefix="_")
public class VaadinNoraGeoLocationItemField<GID extends GeoID>
     extends CustomField<GeoLocationItem<GID>> 
  implements VaadinComponent,
  			 VaadinViewTracksChanges{
	private static final long serialVersionUID = 2243847227656872942L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private VaadinNoraGeoLocationItemFieldChangeListener _listener = null;
	
	private final ComboBox<GeoLocationItem<GID>> _cmb = new ComboBox<>();
	private final TextField _txtNotNormalized = new TextField();
	private Registration _txtNotNormalizedRegistration; 
	
	private boolean _blockEvents = false;
	private boolean _changed = false;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinNoraGeoLocationItemField () {
		super();
		this.setItemCaptionGenerator(GeoLocationItem::getName);
	}
	public VaadinNoraGeoLocationItemField(final boolean normalized) {
		super();
		this.setItemCaptionGenerator(GeoLocationItem::getName);
		this.normalized(normalized);
	}
	public static <GID extends GeoID> VaadinNoraGeoLocationItemField<GID> createNormalized() {
		return new VaadinNoraGeoLocationItemField<>(true);
	}
	public static <GID extends GeoID> VaadinNoraGeoLocationItemField<GID> create() {
		return new VaadinNoraGeoLocationItemField<>(false);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	protected Component initContent() {
		VerticalLayout vLayout = new VerticalLayout();
		vLayout.setWidth(100,Unit.PERCENTAGE);
		vLayout.setMargin(false);
		vLayout.setSpacing(false);
		vLayout.addComponent(_cmb);
		_cmb.setWidth(100,Unit.PERCENTAGE);
		vLayout.addComponent(_txtNotNormalized);
		_txtNotNormalized.setWidth(100,Unit.PERCENTAGE);
		_cmb.addValueChangeListener(event -> {
												if (_listener != null && !_blockEvents) {
													_listener.onValueChange(this);
												}
												setViewDataChanged(true);
								   });
		_txtNotNormalized.addValueChangeListener(event -> {
												if (_listener != null && ! _blockEvents) {
													_listener.onValueChange(this);
												}
												setViewDataChanged(true);
								   });
		//_normalized();
		return vLayout;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	STYLE & PRESENTATION	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void setCaption(final String caption) {
		_cmb.setCaption(caption);
		_txtNotNormalized.setCaption(caption);
	}
	public void setItemCaptionGenerator(final ItemCaptionGenerator<GeoLocationItem<GID>> itemCaptionGenerator) {
        _cmb.setItemCaptionGenerator(itemCaptionGenerator);
    }
	public void setPlaceholder(final String placeholder) {
		_cmb.setPlaceholder(placeholder);
	}
	public void  addComboStyleName(final String styleName) {
		_cmb.addStyleName(styleName);
	}
	public void setComboIcon(final Resource icon) {
		_cmb.setIcon(icon);
	}
	public void normalized(final boolean isNormalized) {
		if (isNormalized) {
			_normalized();
		} else {
			_notNormalized();
		}
	}
	private void _normalized() {
		_cmb.setVisible(true);
		_txtNotNormalized.setVisible(false);
		_blockEvents = true;
		_txtNotNormalized.setValue("");
		_txtNotNormalized.setPlaceholder(_txtNotNormalized.getCaption());
		_blockEvents = false;
	}
	private void _notNormalized() {
		this.setEnabled(true);
		_cmb.setVisible(false);
		_txtNotNormalized.setVisible(true);
		_blockEvents = true;
		_cmb.setValue(null);
		_cmb.setPlaceholder(_cmb.getCaption());
		_blockEvents = false;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	VALUE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public GeoLocationItem<GID> getValue() {
		GeoLocationItem<GID> returnValue = null;
		if (_cmb.isVisible() 
		 && _cmb.getValue() != null) {
			returnValue = GeoLocationItem.normalized(_cmb.getValue().getId());
		} else if (_txtNotNormalized.isVisible()
				&& Strings.isNOTNullOrEmpty(_txtNotNormalized.getValue())) {
			returnValue = GeoLocationItem.notNormalized(_txtNotNormalized.getValue());
		}
		return returnValue;
	}
	public GID getValueId() {
		if (_cmb.isVisible() && _cmb.getValue() != null) {
			return _cmb.getValue().getId();
		}
		return null;
	}
	@Override
	protected void doSetValue(final GeoLocationItem<GID> value) {
		if (value == null) return;
		
		_cmb.setVisible(value.isNormalized());
		_txtNotNormalized.setVisible(value.isNOTNormalized());
		if (value.isNormalized()) {
			_cmb.setValue(value);
		} else {
			_txtNotNormalized.setValue(value.getName());
		}
	}
	@Override
	public void clear() {
		_cmb.clear();
		_txtNotNormalized.clear();
		this.setEnabled(false);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DATA
/////////////////////////////////////////////////////////////////////////////////////////
	public void setItems(final Collection<GeoLocationItem<GID>> items) {
		_cmb.clear();
		_cmb.setPlaceholder(_cmb.getCaption());
		_cmb.setItems(items);
	}
	public void setDataProvider(final DataProvider<GeoLocationItem<GID>, String> dataProvider) {
		_cmb.setDataProvider(dataProvider);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EVENTS                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	public void setVaadinNoraGeoLocationItemFieldChangeListener(final VaadinNoraGeoLocationItemFieldChangeListener listener) {
		_listener = listener;
	}	
	
	public interface VaadinNoraGeoLocationItemFieldChangeListener {
		public <GID extends GeoID> void onValueChange(final VaadinNoraGeoLocationItemField<GID>	component );
	}	

	@Override
	public Registration addValueChangeListener(final ValueChangeListener<GeoLocationItem<GID>> listener) {
		_cmb.addValueChangeListener(listener);
		return super.addValueChangeListener(listener);
	}
	@Override
	public void setViewDataChanged(final boolean changed) {
		_changed = changed;
		
	}
	@Override
	public boolean hasViewDataChanged() {
		return _changed;
	}
	
	public void addTxtNotNormalizedChangeListener(final ValueChangeListener<String> listener) {
		// Add listener only first time, if not we will have an exponential list of listeners raising events
		if (_txtNotNormalizedRegistration == null) { 
			_txtNotNormalizedRegistration = _txtNotNormalized.addValueChangeListener(listener);
		}
	}
}
