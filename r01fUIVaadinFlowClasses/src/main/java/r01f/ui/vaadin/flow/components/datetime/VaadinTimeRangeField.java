package r01f.ui.vaadin.flow.components.datetime;

import java.time.Duration;
import java.time.LocalTime;

import com.vaadin.flow.component.datepicker.DatePicker.DatePickerI18n;
import com.vaadin.flow.component.timepicker.TimePicker;

/**
 * A vaadin date range selector like:
 * <pre>
 * 		+=====================================+
 * 		| [ lower time \/] - [ upper time \/] |
 * 		+=====================================+
 * </pre>
 */
public class VaadinTimeRangeField 
	 extends VaadinTemporalRangeFieldBase<LocalTime,
	 									  TimePicker,
	 									  VaadinTimeRangeField> {

	private static final long serialVersionUID = 4640980227330069190L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinTimeRangeField() {
		super(TimePicker.class,
			  () -> new TimePicker());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TIME PLACE HOLDER
/////////////////////////////////////////////////////////////////////////////////////////
	public void setPlaceholder(final String placeholder) {
		VaadinTemporalRangeFieldHasPlaceHolder hasPlaceholder = _createProxyInvocationHandler(VaadinTemporalRangeFieldHasPlaceHolder.class);
		hasPlaceholder.setPlaceholder(placeholder);
	}
	public String getPlaceholder() {
		VaadinTemporalRangeFieldHasPlaceHolder hasPlaceholder = _createProxyInvocationHandler(VaadinTemporalRangeFieldHasPlaceHolder.class);
		return hasPlaceholder.getPlaceholder();
	}
	
	private interface VaadinTemporalRangeFieldHasPlaceHolder {
		public void setPlaceholder(final String placeholder);
		public String getPlaceholder();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TIME PICKER STEP
/////////////////////////////////////////////////////////////////////////////////////////
	public void setTimePickerStep(final Duration step) {
		_temporalCompLowerBound.setStep(step);
		_temporalCompUpperBound.setStep(step);
	}
	public Duration getTimePickerStep() {
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
