package r01f.ui.vaadin.flow.components.contact;

import java.util.Collection;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.binder.Binder.Binding;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import r01f.locale.I18NKey;
import r01f.patterns.Factory;
import r01f.types.contact.ContactInfoUsage;
import r01f.ui.i18n.UII18NService;
import r01f.ui.vaadin.flow.components.form.VaadinDetailEditForm;
import r01f.ui.vaadin.flow.components.form.VaadinViewTracksChanges;
import r01f.ui.vaadin.flow.components.grid.VaadinGridComponentBase;
import r01f.ui.vaadin.flow.databinding.VaadinListDataProviders;

/**
 * Creates a [contact mean] (phone, email...) manage component like:
 * <pre>
 * 		+++++++++++++++++++++++++++++++++++++++++++++++++++++
 * 		+ Contact Mean title  [ADD]                         +
 *      +												    +
 *      + +-----------------------------------------------+ +
 *      + | [mean]      | [usage] | [default] | [private] | +
 *      + +-------------|---------|-----------|-----------+ +
 *      + |             |         |           |           | +
 *      + |             |         |           |           | +
 *      + |             |         |           |           | +
 *      + +-----------------------------------------------+ +
 *      +++++++++++++++++++++++++++++++++++++++++++++++++++++
 * </pre>
 * @param <V>
 * @param <W>
 */
public abstract class VaadinContactMeanManageBase<V extends VaadinContactMeanObject,
												  W extends VaadinDetailEditForm<V>>
	 		  extends VaadinGridComponentBase<V,W> 
		   implements VaadinViewTracksChanges {

	private static final long serialVersionUID = 679801385310874400L;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinContactMeanManageBase(final UII18NService i18n,
									   final W winDetailEdit,
									   final Factory<V> viewObjFactory,
									   final I18NKey captionKey) {
		super(i18n,
			  winDetailEdit,
			  viewObjFactory,
			  captionKey);
	}
	@Override @SuppressWarnings({ "null","unused" })
	protected void _configureGridColumns(final UII18NService i18n) {
		////////// Grid controls
		// usage
		ComboBox<ContactInfoUsage> cmbUsage = new ComboBox<>();
		cmbUsage.setItems(ContactInfoUsage.values());
		cmbUsage.setItemLabelGenerator(usage -> usage.nameUsing(i18n));
		// default
		Checkbox chkDefault = new Checkbox();
		// private
		Checkbox chkPrivate = new Checkbox();

		////////// Grid binding
		Editor<V> gridEditor = _grid.getEditor();		
		gridEditor.setBuffered(false);		// inline editing!! no [save] / [cancel] buttons
		
		// usage
		Binding<V,ContactInfoUsage> usageBinding = _gridBinder.forField(cmbUsage)
															  .asRequired()
															  .bind(V::getUsage,V::setUsage);
		// default
		Binding<V,Boolean> defaultBindig = _gridBinder.forField(chkDefault)
													  .bind(V::isDefault,V::setDefault);
		// private
		Binding<V,Boolean> privateBindig = _gridBinder.forField(chkPrivate)
													  .bind(V::isPrivate,V::setPrivate);
		
		gridEditor.setBinder(_gridBinder);
		

		////////// Columns
		// usage
		_grid.addColumn(viewObj -> viewObj.getUsage() != null ? viewObj.getUsage().nameUsing(i18n) : null)
				.setHeader(i18n.getMessage("contact.mean.usage"))
				.setFlexGrow(0)
				.setResizable(false)
				.setId("usage");
		 // default
		_grid.addColumn(new ComponentRenderer<>(viewObj -> {
													VaadinIcon vIcon = viewObj.isDefault() ? VaadinIcon.CHECK : VaadinIcon.CLOSE;
													Icon icon = new Icon(vIcon);
													return icon;
					    						}))
			   	.setHeader(i18n.getMessage("contact.mean.default"))
			   	.setFlexGrow(0)
			   	.setResizable(false)
			   	.setId("default");
		 // private
		_grid.addColumn(viewObj -> new Icon(viewObj.isPrivate() ? VaadinIcon.CHECK : VaadinIcon.CLOSE))
				.setHeader(i18n.getMessage("contact.mean.private"))
				.setFlexGrow(0)
				.setResizable(false)
				.setId("private");

		////////// specific cols
		_configureMoreGridColumns(i18n);
		
		////////// Behavior
		// when the [default] Checkbox is checked/unchecked we must ensure
		// that there only exists a single checked item
		_gridBinder.addValueChangeListener(valChangeEvent -> {
												if (valChangeEvent.getHasValue() == chkDefault 
												 && (Boolean)valChangeEvent.getValue()) {
													// un-select the [default] Checkbox at other items
													_unCheckDefaults(_gridBinder.getBean());
												}
										   });
	}
	/**
	 * Adds the contact mean specific columns
	 * @param i18n
	 */
	protected abstract void _configureMoreGridColumns(final UII18NService i18n);
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	protected void _afterAddOrEdit(final V viewObj) {
		// Called just after an item is edited or added to the grid
		// ...
		_unCheckDefaults(viewObj);
	}
	private void _unCheckDefaults(final V viewObj) {
		if (!viewObj.isDefault()) return;
		
		// cycle all over the grid items and uncheck the [default] status
		Collection<V> gridItems = VaadinListDataProviders.collectionBackedOf(_grid)
			 									         .getUnderlyingItemsCollection();
		gridItems.stream()
		   .filter(viewMean -> viewMean != viewObj)
		   .forEach(otherViewMean -> {	
			   			// set & refresh
						otherViewMean.setDefault(false);
						VaadinListDataProviders.collectionBackedOf(_grid)
											   .refreshItem(otherViewMean);
				   });
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
