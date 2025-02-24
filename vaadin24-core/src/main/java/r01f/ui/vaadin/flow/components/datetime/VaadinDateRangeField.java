package r01f.ui.vaadin.flow.components.datetime;

import java.time.LocalDate;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datepicker.DatePicker.DatePickerI18n;

/**
 * A vaadin date range selector like:
 * <pre>
 * 		+=====================================+
 * 		| [ lower date \/] - [ upper date \/] |
 * 		+=====================================+
 * </pre>
 */
public class VaadinDateRangeField 
	 extends VaadinTemporalRangeFieldBase<LocalDate,
	 									  DatePicker,
	 									  VaadinDateRangeField> {

	private static final long serialVersionUID = 3860939472466335166L;

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinDateRangeField() {
		super(DatePicker.class,
			  () -> new DatePicker());
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//	DATE PLACE HOLDER
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
//	DATE PICKER I18N
/////////////////////////////////////////////////////////////////////////////////////////
	public void setI18n(final DatePickerI18n i18n) {
		VaadinTemporalRangeFieldHasDatePickerI18N hasDatePickerI18N = _createProxyInvocationHandler(VaadinTemporalRangeFieldHasDatePickerI18N.class);
		hasDatePickerI18N.setI18n(i18n);
	}
	public DatePickerI18n getI18n() {
		VaadinTemporalRangeFieldHasDatePickerI18N hasDatePickerI18N = _createProxyInvocationHandler(VaadinTemporalRangeFieldHasDatePickerI18N.class);
		return hasDatePickerI18N.getI18n();
	}
	
	private interface VaadinTemporalRangeFieldHasDatePickerI18N {
		public DatePickerI18n getI18n();
		public void setI18n(DatePickerI18n i18n);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	INITIAL POSITION
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Date which should be visible when there is no value selected.
	 * @param initialPosition the LocalDate value to set
	 */
	public void setLowerInitialPosition(final LocalDate initialPosition) {
		if (initialPosition != null
		 && _temporalCompUpperBound.getInitialPosition() != null
		 && initialPosition.isAfter(_temporalCompUpperBound.getInitialPosition())) throw new IllegalArgumentException("Lower bound initial position MUST be BEFORE upper bound initial position");
		
		_temporalCompLowerBound.setInitialPosition(initialPosition);
	}
	/**
	 * Get the visible date when there is no value selected.
	 * This property is not synchronized automatically from the client side, so
	 * the returned value may not be the same as in client side.
	 * @return the {@code initialPosition} property from the datepicker
	 */
	public LocalDate getLowerInitialPosition() {
		return _temporalCompLowerBound.getInitialPosition();
	}
	/**
	 * Date which should be visible when there is no value selected.
	 * @param initialPosition the LocalDate value to set
	 */
	public void setUpperInitialPosition(final LocalDate initialPosition) {
		if (initialPosition != null
		 && _temporalCompLowerBound.getInitialPosition() != null
		 && initialPosition.isBefore(_temporalCompLowerBound.getInitialPosition())) throw new IllegalArgumentException("Upper bound initial position MUST be AFTER lower bound initial position");
		
		_temporalCompUpperBound.setInitialPosition(initialPosition);
	}
	/**
	 * Get the visible date when there is no value selected.
	 * This property is not synchronized automatically from the client side, so
	 * the returned value may not be the same as in client side.
	 * @return the {@code initialPosition} property from the datepicker
	 */
	public LocalDate getUpperInitialPosition() {
		return _temporalCompUpperBound.getInitialPosition();
	}
}
