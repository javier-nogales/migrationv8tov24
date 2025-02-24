package r01f.ui.vaadin.flow.components.datetime;

import java.time.Duration;
import java.time.LocalDateTime;

import com.vaadin.flow.component.datepicker.DatePicker.DatePickerI18n;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;

/**
 * A vaadin date range selector like:
 * <pre>
 * 		+===============================================+
 * 		| [ lower date time \/] - [ upper date time \/] |
 * 		+===============================================+
 * </pre>
 */
public class VaadinDateTimeRangeField 
	 extends VaadinTemporalRangeFieldBase<LocalDateTime,
	 									  DateTimePicker,
	 									  VaadinDateTimeRangeField> {

	private static final long serialVersionUID = 7895477458066116996L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinDateTimeRangeField() {
		super(DateTimePicker.class,
			  () -> new DateTimePicker());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DATE & TIME PLACE HOLDER
/////////////////////////////////////////////////////////////////////////////////////////
////////// Date
	public void setDatePlaceholder(final String placeholder) {
		VaadinTemporalRangeFieldHasDateAndTimePlaceholder hasDatePAndTimePlaceholder = _createProxyInvocationHandler(VaadinTemporalRangeFieldHasDateAndTimePlaceholder.class);
		hasDatePAndTimePlaceholder.setDatePlaceholder(placeholder);
	}
	public String getDatePlaceholder() {
		VaadinTemporalRangeFieldHasDateAndTimePlaceholder hasDatePAndTimePlaceholder = _createProxyInvocationHandler(VaadinTemporalRangeFieldHasDateAndTimePlaceholder.class);
		return hasDatePAndTimePlaceholder.getDatePlaceholder();
	}
////////// Time
	public void setTimePlaceholder(final String placeholder) {
		VaadinTemporalRangeFieldHasDateAndTimePlaceholder hasDatePAndTimePlaceholder = _createProxyInvocationHandler(VaadinTemporalRangeFieldHasDateAndTimePlaceholder.class);
		hasDatePAndTimePlaceholder.setDatePlaceholder(placeholder);
	}
	public String getTimePlaceholder() {
		VaadinTemporalRangeFieldHasDateAndTimePlaceholder hasDatePAndTimePlaceholder = _createProxyInvocationHandler(VaadinTemporalRangeFieldHasDateAndTimePlaceholder.class);
		return hasDatePAndTimePlaceholder.getTimePlaceholder();
	}
	
	private interface VaadinTemporalRangeFieldHasDateAndTimePlaceholder {
		public void setDatePlaceholder(final String placeholder);
		public String getDatePlaceholder() ;
		
		public void setTimePlaceholder(final String placeholder);
		public String getTimePlaceholder();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TIME PICKER STEP
/////////////////////////////////////////////////////////////////////////////////////////
	public void setStep(final Duration step) {
		_temporalCompLowerBound.setStep(step);
		_temporalCompUpperBound.setStep(step);
	}
	public Duration getStep() {
		return _temporalCompLowerBound.getStep();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DATE PICKER I18N
/////////////////////////////////////////////////////////////////////////////////////////
	public void setDatePickerI18n(final DatePickerI18n i18n) {
		VaadinTemporalRangeFieldHasDatePickerI18N hasDatePickerI18N = _createProxyInvocationHandler(VaadinTemporalRangeFieldHasDatePickerI18N.class);
		hasDatePickerI18N.setDatePickerI18n(i18n);
	}
	public DatePickerI18n getDatePickerI18n() {
		VaadinTemporalRangeFieldHasDatePickerI18N hasDatePickerI18N = _createProxyInvocationHandler(VaadinTemporalRangeFieldHasDatePickerI18N.class);
		return hasDatePickerI18N.getDatePickerI18n();
	}
	
	private interface VaadinTemporalRangeFieldHasDatePickerI18N {
		public DatePickerI18n getDatePickerI18n();
		public void setDatePickerI18n(DatePickerI18n i18n);
	}
}
