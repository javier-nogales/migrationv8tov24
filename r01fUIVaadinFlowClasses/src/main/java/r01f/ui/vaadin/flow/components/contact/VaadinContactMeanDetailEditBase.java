package r01f.ui.vaadin.flow.components.contact;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.types.contact.ContactInfoUsage;
import r01f.ui.i18n.UII18NService;
import r01f.ui.vaadin.flow.annotations.VaadinViewComponentLabels;
import r01f.ui.vaadin.flow.annotations.VaadinViewField;
import r01f.ui.vaadin.flow.components.contact.email.VaadinViewContactEmail;
import r01f.ui.vaadin.flow.view.VaadinViews;

@Accessors(prefix="_")
public abstract class VaadinContactMeanDetailEditBase<V extends VaadinContactMeanObject>
	 		  extends VerticalLayout
           implements VaadinContactMeanDetailEdit<V> {

	private static final long serialVersionUID = -829148065680140801L;
/////////////////////////////////////////////////////////////////////////////////////////
//  SERVICES
/////////////////////////////////////////////////////////////////////////////////////////
	protected final UII18NService _i18n;
	protected final Class<V> _viewObjType;

/////////////////////////////////////////////////////////////////////////////////////////
//  UI
/////////////////////////////////////////////////////////////////////////////////////////
	@VaadinViewField(bindToViewObjectFieldNamed=VaadinViewContactEmail.USAGE_FIELD,
					 bindStringConverter=false,
					 required=true)
	@VaadinViewComponentLabels(captionI18NKey="contact.mean.usage",useCaptionI18NKeyAsPlaceHolderKey=true)
	protected final ComboBox<ContactInfoUsage> _cmbUsage = new ComboBox<ContactInfoUsage>();

	@VaadinViewField(bindToViewObjectFieldNamed=VaadinViewContactEmail.DEFAULT_FIELD,required=false)
	@VaadinViewComponentLabels(captionI18NKey="contact.mean.default")
	protected final Checkbox _chkDefault = new Checkbox();

	@VaadinViewField(bindToViewObjectFieldNamed=VaadinViewContactEmail.PRIVATE_FIELD,required=false)
	@VaadinViewComponentLabels(captionI18NKey="contact.mean.private")
	protected final Checkbox _chkPrivate = new Checkbox();
	
	@Getter protected final Binder<V> _vaadinUIBinder;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinContactMeanDetailEditBase(final Class<V> viewObjType,
										   final UII18NService i18n) {
		_i18n = i18n;
		_viewObjType = viewObjType;
		
		////////// style window
		this.setWidth(100,Unit.PERCENTAGE);
		this.setMargin(false);

		////////// Init the binder
		_vaadinUIBinder = new Binder<>(viewObjType);
		
		////////// form
		// usage
		_cmbUsage.setReadOnly(false);
		_cmbUsage.setItems(ContactInfoUsage.values());
		_cmbUsage.setItemLabelGenerator(usage -> usage.nameUsing(i18n));

		_cmbUsage.setValue(ContactInfoUsage.OTHER);		// other by default
	}
	/**
	 * Call this method just after all the UI is initialized (usually at the end of the constructor)
	 * BEWARE!	Ensure to init the form fields at the correct place:
	 * The problem arises when a form extends a base form:
	 *		public class MyEditorBase {
	 *			protected MyEditorBase(final UII18NService i18n,
	 *								   final final Class<V> viewObjType) {
	 *			VaadinViews.using(_binder,_i18n)
	 *					   .bindComponentsOf(this)
	 *					   .toViewObjectOfType(viewObjType);	<--- WRONG!!! this MUST be called at the
	 *			}													  		  concrete type constructor!!!
	 *		}
	 *		public class MyEditor
	 *			 extends MyEditorBase {
	 *
	 * 			@VaadinViewField(bindToViewObjectFieldNamed="myField",required=false) 
	 *			@LangIndependentVaadinViewField
	 *			@VaadinViewComponentLabels(captionI18NKey="myKey",useCaptionI18NKeyAsPlaceHolderKey=true)
	 *			protected final TextField _txtMyField = new TextField();
	 *
	 *			public MyEditor() {
	 *				super(i18n,
	 *					  MyViewObj.class);
	 *			}
	 *		}
	 *		Since the call to 
	 *				VaadinViews.using(_binder,_i18n)
	 *						   .bindComponentsOf(this)
	 *						   .toViewObjectOfType(viewObjType);
	 *		is done at the BASE type, the _txtMyField field IS NOT YET initialized!
	 *		... so it's NULL!!
	 *		ensure to call 
	 *				VaadinViews.using(_binder,_i18n)
	 *						   .bindComponentsOf(this)
	 *						   .toViewObjectOfType(viewObjType);
	 *		at the MyEditor constructor 
	 */
	protected void _initFormComponents() {
		// set ui labels
		VaadinViews.using(_i18n)
				   .setI18NLabelsOf(this);

		////////// Bind: automatic binding using using @VaadinViewField annotation of view fields
		VaadinViews.using(_vaadinUIBinder,_i18n)
				   .bindComponentsOf(this)
				   .toViewObjectOfType(_viewObjType);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Binding
/////////////////////////////////////////////////////////////////////////////////////////
	////////// [viewObject] > [UI control] --------------
	@Override
	public void editViewObject(final V viewObj) {
		_vaadinUIBinder.readBean(viewObj);
	}
	////////// [UI control] > [viewObject] --------------
	@Override
	public void writeAsDraftEditedViewObjectTo(final V viewObj) {
		_vaadinUIBinder.writeBeanAsDraft(viewObj);		
	}
	@Override
	public boolean writeIfValidEditedViewObjectTo(final V viewObj) {
		return _vaadinUIBinder.writeBeanIfValid(viewObj);
	}
}
