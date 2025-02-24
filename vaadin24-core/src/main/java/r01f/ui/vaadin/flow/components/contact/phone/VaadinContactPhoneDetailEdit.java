package r01f.ui.vaadin.flow.components.contact.phone;

import java.time.Duration;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

import r01f.types.contact.ContactPhoneType;
import r01f.ui.i18n.UII18NService;
import r01f.ui.vaadin.flow.annotations.LangIndependentVaadinViewField;
import r01f.ui.vaadin.flow.annotations.VaadinViewComponentLabels;
import r01f.ui.vaadin.flow.annotations.VaadinViewField;
import r01f.ui.vaadin.flow.components.contact.VaadinContactMeanDetailEditBase;
import r01f.ui.vaadin.flow.components.datetime.VaadinDateTimeRangeField;

public class VaadinContactPhoneDetailEdit
	 extends VaadinContactMeanDetailEditBase<VaadinViewContactPhone> {

	private static final long serialVersionUID = -3069365158386075376L;
/////////////////////////////////////////////////////////////////////////////////////////
//  UI
/////////////////////////////////////////////////////////////////////////////////////////
	@VaadinViewField(bindToViewObjectFieldNamed=VaadinViewContactPhone.PHONE_NUMBER_FIELD,
					 required=true)
	@VaadinViewComponentLabels(captionI18NKey="contact.phone.number",useCaptionI18NKeyAsPlaceHolderKey=true)
	private final TextField _txtNumber = new TextField();

	@VaadinViewField(bindToViewObjectFieldNamed=VaadinViewContactPhone.PHONE_EXTENSION_FIELD,
					 required=false)
	@VaadinViewComponentLabels(captionI18NKey="contact.phone.extension",useCaptionI18NKeyAsPlaceHolderKey=true)
	private final TextField _txtExtension = new TextField();

	@VaadinViewField(bindToViewObjectFieldNamed=VaadinViewContactPhone.TYPE_FIELD,
					 bindStringConverter=false,
					 required = true)
	@VaadinViewComponentLabels(captionI18NKey="contact.phone.type",useCaptionI18NKeyAsPlaceHolderKey=true)
	private final ComboBox<ContactPhoneType> _cmbType = new ComboBox<>();

	@VaadinViewField(bindToViewObjectFieldNamed=VaadinViewContactPhone.AVAILABLE_RANGE,
					 bindStringConverter=false,
					 required=false)
	@LangIndependentVaadinViewField
	@VaadinViewComponentLabels(captionI18NKey="contact.phone.available",
							   useCaptionI18NKeyAsPlaceHolderKey=true)
	protected final VaadinDateTimeRangeField _dateTimeRange = new VaadinDateTimeRangeField();
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinContactPhoneDetailEdit(final UII18NService i18n) {
		super(VaadinViewContactPhone.class,
			  i18n);

		// phone number
		_txtNumber.setWidth(100,Unit.PERCENTAGE);
		_txtNumber.setReadOnly(false);

		// phone extension
		_txtExtension.setWidth(100,Unit.PERCENTAGE);
		_txtExtension.setReadOnly(false);

		// type
		_cmbType.setWidth(100,Unit.PERCENTAGE);
		_cmbType.setItems(ContactPhoneType.values());
		_cmbType.setReadOnly(false);

		// availability: from - to
		_dateTimeRange.setStep(Duration.ofHours(1));

		// layout: DO NOT FORGET!
		this.add(new HorizontalLayout(_cmbUsage,_txtNumber,_txtExtension,_cmbType),
				 _dateTimeRange,
				 new HorizontalLayout(_chkDefault,_chkPrivate));

		////////// Init the form components (DO NOT FORGET!!)
		_initFormComponents();
	}
}
