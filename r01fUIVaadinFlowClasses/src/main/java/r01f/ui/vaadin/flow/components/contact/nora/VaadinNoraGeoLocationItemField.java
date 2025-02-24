package r01f.ui.vaadin.flow.components.contact.nora;

import java.util.Collection;

import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.shared.Registration;

import lombok.experimental.Accessors;
import r01f.types.geo.GeoLocationItem;
import r01f.types.geo.GeoOIDs.GeoID;
import r01f.ui.vaadin.flow.VaadinEvents;
import r01f.ui.vaadin.flow.components.VaadinComponent;
import r01f.ui.vaadin.flow.components.VaadinComponentWithIconWrapper;
import r01f.ui.vaadin.flow.components.form.VaadinViewTracksChanges;
import r01f.util.types.Strings;

@Accessors(prefix="_")
public class VaadinNoraGeoLocationItemField<GID extends GeoID>
	 extends CustomField<GeoLocationItem<GID>> 
  implements VaadinComponent,
  			 VaadinViewTracksChanges {
	private static final long serialVersionUID = 2243847227656872942L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	private VaadinNoraGeoLocationItemFieldChangeListener _listener = null;
	
	private ComboBox<GeoLocationItem<GID>> _cmb;
	private TextField _txtNotNormalized;
	
	private final VaadinComponentWithIconWrapper<ComboBox<GeoLocationItem<GID>>> _cmbWithIconWrap;
	
	private boolean _blockEvents = false;
	private boolean _changed = false;
	
	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinNoraGeoLocationItemField () {
		super();
		this.setComboItemLabelGenerator(GeoLocationItem::getName);
		
		////////// Layout
		_cmb = new ComboBox<>();
		_cmbWithIconWrap = VaadinComponentWithIconWrapper.wrap(_cmb);
		
		_txtNotNormalized = new TextField();
		
		VerticalLayout vLayout = new VerticalLayout();
		vLayout.setWidthFull();
		vLayout.setMargin(false);
		vLayout.setSpacing(false);
		vLayout.add(_cmbWithIconWrap,		// combo with an icon
					_txtNotNormalized);
		
		_cmb.setWidthFull();
		_txtNotNormalized.setWidthFull();
		
		this.add(vLayout);

		////////// Behavior
		_setBehavior();
	}
	public VaadinNoraGeoLocationItemField(final boolean normalized) {
		this();
		this.normalized(normalized);
	}
	public static <GID extends GeoID> VaadinNoraGeoLocationItemField<GID> createNormalized() {
		return new VaadinNoraGeoLocationItemField<>(true);
	}
	public static <GID extends GeoID> VaadinNoraGeoLocationItemField<GID> create() {
		return new VaadinNoraGeoLocationItemField<>(false);
	}
	
	private void _setBehavior() {
		_cmb.addValueChangeListener(event -> {
										if (_listener != null 
										 && !_blockEvents) {
											_listener.onValueChange(this);
										}
										this.setViewDataChanged(true);
									});
		_txtNotNormalized.addValueChangeListener(event -> {
													if (_listener != null 
													 && ! _blockEvents) {
														_listener.onValueChange(this);
													}
													this.setViewDataChanged(true);
								   				});
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	STYLE & PRESENTATION	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void setLabel(final String caption) {
		_cmb.setLabel(caption);
		_txtNotNormalized.setLabel(caption);
	}
	@Override
	public String getLabel() {
		return _cmb.getLabel();
	}
	public void setComboItemLabelGenerator(final ItemLabelGenerator<GeoLocationItem<GID>> itemCaptionGenerator) {
		_cmb.setItemLabelGenerator(itemCaptionGenerator);
	}
	public void setComboPlaceholder(final String placeholder) {
		_cmb.setPlaceholder(placeholder);
	}
	public void setComboIcon(final Icon icon) {
		_cmbWithIconWrap.setIcon(icon);
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
		
		// change without raising events
		_blockEvents = true;
		_txtNotNormalized.setValue("");
		_txtNotNormalized.setPlaceholder(_txtNotNormalized.getLabel());
		_blockEvents = false;
	}
	private void _notNormalized() {
		this.setEnabled(true);
		_cmb.setVisible(false);
		_txtNotNormalized.setVisible(true);
		
		// change without raising events
		_blockEvents = true;
		_cmb.setValue(null);
		_cmb.setPlaceholder(_cmb.getLabel());
		_blockEvents = false;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	VALUE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void setPresentationValue(final GeoLocationItem<GID> value) {
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
	protected GeoLocationItem<GID> generateModelValue() {
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
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
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
		_cmb.setItems(items);
		
		_cmb.setPlaceholder(_cmb.getLabel());
	}
	public void setDataProvider(final DataProvider<GeoLocationItem<GID>, String> dataProvider) {
		_cmb.setItems(dataProvider);
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
	public Registration addValueChangeListener(final ValueChangeListener<? super ComponentValueChangeEvent<CustomField<GeoLocationItem<GID>>,GeoLocationItem<GID>>> listener) {
		_cmb.addValueChangeListener(cmbValChangeEvent -> {			
										ComponentValueChangeEvent<CustomField<GeoLocationItem<GID>>,
																  GeoLocationItem<GID>> event = VaadinEvents.wrapValueChangeEvent(this,_cmb,
																		  														  cmbValChangeEvent);
										listener.valueChanged(event);
									});
		return super.addValueChangeListener(listener);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CHANGED
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public void setViewDataChanged(final boolean changed) {
		_changed = changed;
	}
	@Override
	public boolean hasViewDataChanged() {
		boolean prevChanged = _changed;
		_changed = false;
		return prevChanged;
	}
}
