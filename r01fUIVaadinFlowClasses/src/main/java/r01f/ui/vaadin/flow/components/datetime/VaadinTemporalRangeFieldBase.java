package r01f.ui.vaadin.flow.components.datetime;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.util.Locale;
import java.util.stream.Stream;

import com.vaadin.experimental.Feature;
import com.vaadin.experimental.FeatureFlags;
import com.vaadin.flow.component.AbstractCompositeField;
import com.vaadin.flow.component.AbstractSinglePropertyField;
import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.shared.HasClientValidation;
import com.vaadin.flow.data.binder.HasValidator;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValidationStatusChangeEvent;
import com.vaadin.flow.data.binder.ValidationStatusChangeListener;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.shared.Registration;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.patterns.Factory;
import r01f.reflection.ReflectionUtils;
import r01f.types.Range;
import r01f.ui.vaadin.flow.i18n.VaadinHasLocale;

/**
 * A vaadin date range selector like:
 * <pre>
 * 		+=====================================+
 * 		| [ lower temp \/] - [ upper temp \/] |
 * 		+=====================================+
 * </pre>
 */
abstract class VaadinTemporalRangeFieldBase<T extends Temporal & TemporalAdjuster & Serializable & Comparable<? super T>,
											TC extends AbstractSinglePropertyField<TC,T> & HasLabel & HasValidation,
											SELF_TYPE extends VaadinTemporalRangeFieldBase<T,TC,SELF_TYPE>> 
	   extends AbstractCompositeField<HorizontalLayout,
	 								  SELF_TYPE,
	 								  Range<T>> 
	implements HasLabel,
			   HasValidation,
			   HasValidator<Range<T>>,
			   HasClientValidation {

	private static final long serialVersionUID = -6829788420096612071L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final transient Class<TC> _compType;
	
	protected final TC _temporalCompLowerBound;
	protected final TC _temporalCompUpperBound;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	protected VaadinTemporalRangeFieldBase(final Class<TC> compType,
										   final Factory<TC> compFactory) {
		super(null);	// default value = null
		_compType = compType;
		
		////////// create components
		_temporalCompLowerBound = compFactory.create();
		_temporalCompUpperBound = compFactory.create();
		
		////////// layout
		this.getContent()
			.add(_temporalCompLowerBound,_temporalCompUpperBound);
		
		////////// Behavior
		_setBehavior();
	}
	@SuppressWarnings("unchecked")
	private void _setBehavior() {
 		VaadinTemporalComponentHasMinAndMax<T> lowerCompHasMinAndMax = _createTypeAdapterInvocationHandler(VaadinTemporalComponentHasMinAndMax.class,
 																										   _temporalCompLowerBound);	
 		VaadinTemporalComponentHasMinAndMax<T> upperCompHasMinAndMax = _createTypeAdapterInvocationHandler(VaadinTemporalComponentHasMinAndMax.class,
 																										   _temporalCompUpperBound);		
		_temporalCompLowerBound.addValueChangeListener(valChangeEvent -> {
															// when the lower bound is set, the upper bound MIN value is the lower bound value
															upperCompHasMinAndMax.setMin(valChangeEvent.getValue());
													   });
		_temporalCompUpperBound.addValueChangeListener(valChangeEvent -> { 
															// when the upper bound is set, the lower bound MAX value is the upper bound value
															lowerCompHasMinAndMax.setMax(valChangeEvent.getValue());
													   });
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PRESENTATION (view) & MODEL VALUES 
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	protected void setPresentationValue(final Range<T> newViewValue) {
		T viewLowerDate = newViewValue.getLowerBound();
		T viewUpperDate = newViewValue.getUpperBound();
		
		_temporalCompLowerBound.setValue(viewLowerDate);
		_temporalCompUpperBound.setValue(viewUpperDate);
	}
	@Override
	public Range<T> getValue() {
		T lower = _temporalCompLowerBound.getValue();
		T upper = _temporalCompUpperBound.getValue();
		
		return new Range<>(lower,upper);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CLEAR
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public void clear() {
		_temporalCompLowerBound.clear();
		_temporalCompUpperBound.clear();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	MIN & MAX
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public void setMin(final T min) {
		VaadinTemporalComponentHasMinAndMax<T> lowerCompHasMinAndMax = _createTypeAdapterInvocationHandler(VaadinTemporalComponentHasMinAndMax.class,
 																										   _temporalCompLowerBound);
 		lowerCompHasMinAndMax.setMin(min);
	}
	@SuppressWarnings("unchecked")
	public T getMin() {
 		VaadinTemporalComponentHasMinAndMax<T> lowerCompHasMinAndMax = _createTypeAdapterInvocationHandler(VaadinTemporalComponentHasMinAndMax.class,
 																										   _temporalCompLowerBound);
 		return lowerCompHasMinAndMax.getMin();
	}
	@SuppressWarnings("unchecked")
	public void setMax(final T max) {
 		VaadinTemporalComponentHasMinAndMax<T> upperCompHasMinAndMax = _createTypeAdapterInvocationHandler(VaadinTemporalComponentHasMinAndMax.class,
 																										   _temporalCompUpperBound);
 		upperCompHasMinAndMax.setMax(max);
	}
	@SuppressWarnings("unchecked")
	public T getMax() {
 		VaadinTemporalComponentHasMinAndMax<T> upperCompHasMinAndMax = _createTypeAdapterInvocationHandler(VaadinTemporalComponentHasMinAndMax.class,
 																										   _temporalCompUpperBound);
 		return upperCompHasMinAndMax.getMax();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	REQUIRED
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public void setRequiredIndicatorVisible(final boolean requiredIndicatorVisible) {
		_temporalCompLowerBound.setRequiredIndicatorVisible(true);
	}
	@Override
	public boolean isRequiredIndicatorVisible() {
		return super.isRequiredIndicatorVisible();	// if required just the lower bound is usually required
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	READONLY
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public void setReadOnly(final boolean readOnly) {
		_temporalCompLowerBound.setReadOnly(readOnly);
		_temporalCompUpperBound.setReadOnly(readOnly);
	}
	@Override
	public boolean isReadOnly() {
		return _temporalCompLowerBound.isReadOnly();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	HasValidation
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void setInvalid(final boolean invalid) {
		_temporalCompLowerBound.setInvalid(invalid);
		_temporalCompUpperBound.setInvalid(invalid);
	}
	@Override
	public boolean isInvalid() {
		return _temporalCompLowerBound.isInvalid();
	}
	@Override
	public void setErrorMessage(final String errorMessage) {
		_temporalCompLowerBound.setErrorMessage(errorMessage);
		_temporalCompUpperBound.setErrorMessage(errorMessage);
	}
	@Override
	public String getErrorMessage() {
		return _temporalCompLowerBound.getErrorMessage();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	HasValidator
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public Validator<Range<T>> getDefaultValidator() {
		return _isFeatureFlagEnabled(FeatureFlags.ENFORCE_FIELD_VALIDATION)
					? (value,context) -> _checkValidity(value)	// default validator
					: Validator.alwaysPass();
	}
	@Override
	public Registration addValidationStatusChangeListener(final ValidationStatusChangeListener<Range<T>> listener) {
		if (_isFeatureFlagEnabled(FeatureFlags.ENFORCE_FIELD_VALIDATION)) {
			return this.addClientValidatedEventListener(event -> listener.validationStatusChanged(new ValidationStatusChangeEvent<>(this,
																								  									!this.isInvalid())));
		}
		return null;
	}
	private ValidationResult _checkValidity(final Range<T> value) {
		if (_isFeatureFlagEnabled(FeatureFlags.ENFORCE_FIELD_VALIDATION)) {
			boolean hasNonParsableValue = value == this.getEmptyValue();
			if (hasNonParsableValue) return ValidationResult.error("");
		}

		ValidationResult greaterThanMax = _checkGreaterThanMax(value.getUpperBound(),this.getMax());
		if (greaterThanMax.isError()) return greaterThanMax;

		ValidationResult smallerThanMin = _checkSmallerThanMin(value.getLowerBound(),this.getMin());
		if (smallerThanMin.isError()) return smallerThanMin;

		return ValidationResult.ok();
	}
    private ValidationResult _checkGreaterThanMax(final T value, final T maxValue) {
        return value != null && maxValue != null
            && value.compareTo(maxValue) > 0 ? ValidationResult.error("")
        									 : ValidationResult.ok();
    }
    private ValidationResult _checkSmallerThanMin(final T value, final T minValue) {
       return value != null && minValue != null
           && value.compareTo(minValue) < 0 ? ValidationResult.error("")
        									: ValidationResult.ok();
    }
	/**
	 * Returns true if the given feature flag is enabled, false otherwise.
	 * The method requires the {@code VaadinService} instance to obtain the
	 * available feature flags, otherwise, the feature is considered disabled.
	 *
	 * @param feature the feature flag.
	 * @return whether the feature flag is enabled.
	 */
	protected boolean _isFeatureFlagEnabled(final Feature feature) {
		VaadinService service = VaadinService.getCurrent();
		if (service == null) return false;

		return FeatureFlags.get(service.getContext())
						   .isEnabled(feature);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LOCALE
/////////////////////////////////////////////////////////////////////////////////////////
	public void setLocale(final Locale locale) {
		VaadinHasLocale hasLocale = _createProxyInvocationHandler(VaadinHasLocale.class);
		hasLocale.setLocale(locale);
	}
	@Override
	public Locale getLocale() {
		VaadinHasLocale hasLocale = _createProxyInvocationHandler(VaadinHasLocale.class);
		return hasLocale.getLocale();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	WEEK NUMBERS
/////////////////////////////////////////////////////////////////////////////////////////	
	public void setWeekNumbersVisible(final boolean weekNumbersVisible) {
 		VaadinTemporalRangeFieldHasWeekNumbers hasWeekNums = _createProxyInvocationHandler(VaadinTemporalRangeFieldHasWeekNumbers.class);	
 		hasWeekNums.setWeekNumbersVisible(weekNumbersVisible);
	}
	public boolean isWeekNumbersVisible() {
 		VaadinTemporalRangeFieldHasWeekNumbers hasWeekNums = _createProxyInvocationHandler(VaadinTemporalRangeFieldHasWeekNumbers.class);
 		return hasWeekNums.isWeekNumbersVisible();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	AUTO OPEN
/////////////////////////////////////////////////////////////////////////////////////////
	public void setAutoOpen(final boolean autoOpen) {
		VaadinTemporalRangeFieldCanAutoOpen canAutoOpen = _createProxyInvocationHandler(VaadinTemporalRangeFieldCanAutoOpen.class);
		canAutoOpen.setAutoOpen(autoOpen);
	}
	public boolean isAutoOpen() {
		VaadinTemporalRangeFieldCanAutoOpen canAutoOpen = _createProxyInvocationHandler(VaadinTemporalRangeFieldCanAutoOpen.class);
		return canAutoOpen.isAutoOpen();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DYNAMIC PROXY
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Calls the SAME method on both components
	 * 
	 * Usage:
	 * <pre class='brush:java'>
	 * 		HasValidation hasValidation = (HasValidation)Proxy.newProxyInstance(VaadinTemporalRangeFieldBase.class.getClassLoader(),
	 *																			new Class[] { HasValidation.class },
	 *																			new ProxyInvocationHandler());	
	 * 		return hasValidation.getErrorMessage();	<-- this method gets called on both components
	 * </pre>
	 */
	@SuppressWarnings("unused")
	protected class ProxyInvocationHandler 
		 implements InvocationHandler {
		
		@Override
		public Object invoke(final Object proxy,
							 final Method method, final Object[] args)		{
			// [1] - Find the method with the same args at the [component]
			Method compMethod = ReflectionUtils.methodMatchingParamTypes(_compType,
													 				 	 method.getName(),args != null ? Stream.of(args)
															 							   				   	   .map(Object::getClass)	
															 							   				   	   .toArray(Class[]::new)
															 							   			   : new Class<?>[] {});
			// [2] - Invoke the method in both lower & upper component
			Object invocationReturnInLower = ReflectionUtils.invokeMethod(_temporalCompLowerBound,compMethod,args);
			Object invocationReturnInUpper = ReflectionUtils.invokeMethod(_temporalCompUpperBound,compMethod,args);
			
			// [3] - Return just the value on one of the components
			return invocationReturnInLower;
		}
	}
	@SuppressWarnings("unchecked")
	protected <I> I _createProxyInvocationHandler(final Class<I> interfaceType) {
		return (I)Proxy.newProxyInstance(VaadinTemporalRangeFieldBase.class.getClassLoader(),
				 						 new Class[] { interfaceType },
				 						 new ProxyInvocationHandler());
	}
	/**
	 * Makes a component implement an intferface that it does NOT implement
	 * 
	 * Usage:
	 * <pre class='brush:java'>
	 * 		VaadinTemporalComponentHasMinAndMax<T> hasMinAndMax = (VaadinTemporalComponentHasMinAndMax<T>)Proxy.newProxyInstance(VaadinTemporalRangeFieldBase.class.getClassLoader(),
	 *																															 new Class[] { VaadinTemporalComponentHasMinAndMax.class },
	 *																															 new AdapterInvocationHandler(component));	
	 * 		return hasMinAndMax.getMing();	<-- this method gets called in the given component
	 * </pre>
	 */
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	protected class AdapterInvocationHandler 
		 implements InvocationHandler {
		
		private final TC _component;
		
		@Override
		public Object invoke(final Object proxy,
							 final Method method, final Object[] args)		{
			// [1] - Find the method with the same args at the [component]
			Method compMethod = ReflectionUtils.methodMatchingParamTypes(_compType,
													 				 	 method.getName(),args != null ? Stream.of(args)
															 							   				   	   .map(Object::getClass)	
															 							   				   	   .toArray(Class[]::new)
															 							   			   : new Class<?>[] {});
			// [2] - Invoke the method in the given object
			Object invocationReturn = ReflectionUtils.invokeMethod(_component,compMethod,args);
			
			// [3] - Return the returned value
			return invocationReturn;
		}
	}
	@SuppressWarnings("unchecked")
	protected <I> I _createTypeAdapterInvocationHandler(final Class<I> interfaceType,
													  	final TC instance) {
		return (I)Proxy.newProxyInstance(VaadinTemporalRangeFieldBase.class.getClassLoader(),
				 						 new Class[] { interfaceType },
				 						 new AdapterInvocationHandler(instance));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private interface VaadinTemporalComponentHasMinAndMax<T> {
		public void setMin(T min);
		public T getMin();
		
		public void setMax(T max);
		public T getMax();
	}
	private interface VaadinTemporalRangeFieldHasWeekNumbers {
		public void setWeekNumbersVisible(boolean weekNumbersVisible);
		public boolean isWeekNumbersVisible();
	}
	private interface VaadinTemporalRangeFieldCanAutoOpen {
		public void setAutoOpen(boolean autoOpen);
		public boolean isAutoOpen();
	}
}
