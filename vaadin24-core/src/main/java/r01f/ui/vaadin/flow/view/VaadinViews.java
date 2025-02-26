package r01f.ui.vaadin.flow.view;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.textfield.GeneratedVaadinTextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.converter.LocalDateToDateConverter;
import com.vaadin.flow.data.converter.StringToBigDecimalConverter;
import com.vaadin.flow.data.converter.StringToBigIntegerConverter;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToFloatConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.converter.StringToLongConverter;
import com.vaadin.flow.data.selection.MultiSelect;
import com.vaadin.flow.internal.BeanUtil;
import com.vaadin.flow.internal.ReflectTools;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.guids.OID;
import r01f.locale.Language;
import r01f.patterns.FactoryFrom;
import r01f.reflection.ReflectionUtils;
import r01f.ui.i18n.UII18NService;
import r01f.ui.vaadin.flow.annotations.VaadinViewComponentLabels;
import r01f.ui.vaadin.flow.annotations.VaadinViewField;
import r01f.ui.vaadin.flow.annotations.VaadinVoidViewFieldValidator;
import r01f.ui.vaadin.flow.components.VaadinComponent;
import r01f.ui.vaadin.flow.components.VaadinComponents;
import r01f.ui.vaadin.flow.components.form.VaadinViewTracksChanges;
import r01f.ui.viewobject.UIViewObject;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Utils for vaadin views
 *
 * Bind view components annotated with @PropertyId
 *
 */
@Slf4j
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class VaadinViews {
/////////////////////////////////////////////////////////////////////////////////////////
//	BINDING
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * View components can be binded to view object's fields using one of
	 * these two methods:
	 *		a) use VaadinViews util that "scans" view's @VaadinViewField-annotated components fields
	 *		   and "automatically" binds each component to a view's field
	 *		   ie:
	 *		        a view component field declared like:
	 *					@VaadinViewField(bindToViewObjectFieldNamed="name")
	 *					private final TextField _txtName = new TextField();
	 *				will be binded to a view object field named "name"
	 *					@Getter @Setter private String _name;
	 *
	 *	   	   The binder can be instructed to "require" a value in the component
	 *		   if the view component is annotated like:
	 *					@VaadinViewField(bindToViewObjectFieldNamed="name",required=true)
	 *					private final TextField _txtName = new TextField();
	 *
	 *		   To use this annotation-based method, just:
	 *				VaadinViews.using(_binder,_i18n)
	 *						   .bindComponentsOf({theView instance})
	 *						   .toViewObjectOfType({the view object type});
	 *
	 *		b) Manually bind EVERY property like:
	 *				_binder.forField(_txtName)
	 *			  			.asRequired(_i18n.getMessage("required"))
	 *			  			.bind( "name" );
	 *
	 * Invoke automatic binding based upon @PropertyId-annotated fields
	 * <pre class='brush:java'>
	 * 		UIVaadinViews.using(binder,i18n)
	 * 						.bindComponentsOf(view)
	 * 						.toViewObjectOfType(viewObjType);
	 * </pre>
	 * @param binder
	 * @param i18n
	 * @return
	 */
	public static <M extends UIViewObject> UIVaadinViewBinderBuilderViewStep<M> using(final Binder<M> binder,
																					  final UII18NService i18n) {
		return new VaadinViews() { /* nothing */ }
						.new UIVaadinViewBinderBuilderViewStep<>(binder,i18n);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class UIVaadinViewBinderBuilderViewStep<M extends UIViewObject> {
		private final Binder<M> _binder;
		private final UII18NService _i18n;

		public <V extends VaadinComponent> UIVaadinViewBinderBuilderViewObjStep<V,M> bindComponentsOf(final V view) {
			return new UIVaadinViewBinderBuilderViewObjStep<>(view,
															  _binder,_i18n);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class UIVaadinViewBinderBuilderViewObjStep<V extends VaadinComponent,M extends UIViewObject> {
		private final V _view;
		private final Binder<M> _binder;
		private final UII18NService _i18n;

		public void toViewObjectOfType(final Class<M> viewObjectType) {
			_bind(_view,viewObjectType,
				  _binder,
				  _i18n);
		}
	}
	private static <V extends VaadinComponent,
				    M extends UIViewObject> void _bind(final V view,final Class<M> viewObjectType,
													   final Binder<M> binder,
													   final UII18NService i18n)  {
		Class<?> viewType = view.getClass();
		Field[] viewFields = ReflectionUtils.allFields(viewType);
		for (Field viewCompField : viewFields) {
			try {
				if (!ReflectionUtils.isImplementing(viewCompField.getType(),
													Component.class)) continue;		// do NOT process fields that are NOT vaadin component

				// [0] - Get the @VaadinViewField annotation which contains the viewObject's field data
				VaadinViewField viewFieldAnnotation = viewCompField.getAnnotation(VaadinViewField.class);
				if (viewFieldAnnotation == null) continue;

				boolean canBeBinded = _canBeBinded(viewCompField);

				// [1] - Get the view object field name from the @VaadinViewField annotation
				String viewObjFieldName = viewFieldAnnotation.bindToViewObjectFieldNamed();

				Class<?> viewObjFieldType = BeanUtil.getPropertyType(viewObjectType,		// BEWARE!! there MUST exist a GETTER for the [view obj] field
																	 viewObjFieldName);
				if (viewObjFieldType == null) {
					log.error("Could NOT find type of bean property {}.{} > ensure there exists the getter & setter methods!",
							  viewObjectType,viewObjFieldName);
					log.error("{} properties:",
							  viewObjectType);
					List<PropertyDescriptor> propertyDescriptors = BeanUtil.getBeanPropertyDescriptors(viewObjectType);
					for (PropertyDescriptor desc : propertyDescriptors) {
						log.error("\t\t-{} ({})",
								  desc.getName(),desc.getPropertyType());
					}
					throw new IllegalStateException("Could NOT find type of bean property" + viewObjectType + "." + viewObjFieldName + " > ensure there exists the getter & setter methods!");
				}

				if (!canBeBinded) log.warn("{} view's component field {} of type {} cannot be 'automatically' binded to {}'s {} property of type {} since the component does NOT implement {}: it should be binded MANUALLY",
										   view.getClass(),viewCompField.getName(),viewCompField.getType(),
										   viewObjectType,viewObjFieldName,viewObjFieldType,
										   HasValue.class);

				// [1] - Get the view component field and the corresponding view obj type
				HasValue<?,?> hasValueComp = (HasValue<?,?>)ReflectTools.getJavaFieldValue(view,
																					   	   viewCompField,HasValue.class);
				if (hasValueComp == null) continue;

				// BEWARE!	If the component is null maybe the call to
				// 						VaadinViews.using(_i18n)
				//								   .setI18NLabelsOf(this);
				// 			is done in the WRONG place:
				//	The problem arises when a form extends a base form:
				//	public class MyEditorBase {
				//		protected MyEditorBase(final UII18NService i18n,
				//							   final final Class<V> viewObjType) {
				//		VaadinViews.using(_binder,_i18n)
				//				   .bindComponentsOf(this)
				//				   .toViewObjectOfType(viewObjType);	<--- WRONG!!! this MUST be called at the
				//		}													  		  concrete type constructor!!!
				//	}
				//	public class MyEditor
				//		 extends MyEditorBase {
				//
				// 		@VaadinViewField(bindToViewObjectFieldNamed="myField",required=false)
				//		@LangIndependentVaadinViewField
				//		@VaadinViewComponentLabels(captionI18NKey="myKey",useCaptionI18NKeyAsPlaceHolderKey=true)
				//		protected final TextField _txtMyField = new TextField();
				//
				//		public MyEditor() {
				//			super(i18n,
				//				  MyViewObj.class);
				//		}
				//	}
				//	Since the call to
				//			VaadinViews.using(_binder,_i18n)
				//					   .bindComponentsOf(this)
				//					   .toViewObjectOfType(viewObjType);
				//	is done at the BASE type, the _txtMyField field IS NOT YET initialized!
				//	... so it's NULL!!
				//	ensure to call
				//			VaadinViews.using(_binder,_i18n)
				//					   .bindComponentsOf(this)
				//					   .toViewObjectOfType(viewObjType);
				//	at the MyEditor constructor

				// [2] - Get the binding builder
				Binder.BindingBuilder<M,?> bindingBuilder = binder.forField(hasValueComp);

				// [3] - Bind
				// BEWARE!!! Order matters
				// [a] - Get the @UIViewComponentValueCannotBeNull annotation: cannot be null
				_bindRequiredFor(bindingBuilder,
								 hasValueComp,
								 viewFieldAnnotation,
								 i18n);
				// [b] - Null representation
				_bindNullRepresentationFor(bindingBuilder,
										   hasValueComp);
				// [c] - String converter
				//		 BEWARE! Vaadin fields are instances of HasValue<?>
				//				 convert ONLY HasValue<String> fields like TextFields
				//				 (ie: do NOT convert on combos)
				if (viewFieldAnnotation.bindStringConverter()) {
					_bindStringConverterFor(bindingBuilder,
									  		viewObjFieldType);
				}
				// [d] - Validator
				_bindValidator(bindingBuilder,
							   viewFieldAnnotation,
							   i18n);
				// [4] - Bind to the view object property
				bindingBuilder.bind(viewObjFieldName);
			} catch (Throwable th) {
				log.error("Could NOT bind one of the view components to {} view object: {}",
						  view.getClass(),viewObjectType,
						  th.getMessage(),th);
			}
		} // for

		// attach an event handler to the binder so when the binder fires a ValueChangeEvent,
		// the view is "marked" as changed
		if (view instanceof VaadinViewTracksChanges) {
			final VaadinViewTracksChanges viewTrackChanges = (VaadinViewTracksChanges)view;
			binder.addValueChangeListener(e -> viewTrackChanges.setViewDataChanged(true));
		}
	}
	private static boolean _canBeBinded(final Field viewCompField) {
		if (!ReflectionUtils.isImplementing(viewCompField.getType(),
											HasValue.class)) return false;
		return true;
	}
	@SuppressWarnings({ "unchecked","rawtypes" })
	private static <M extends UIViewObject,T> void _bindStringConverterFor(final Binder.BindingBuilder<M,?> bindingBuilder,
											  					     	   final Class<T> viewObjPropertyType) {	// view object property
		if (viewObjPropertyType == null) return;

		// Converters
		Binder.BindingBuilder<M,String> strBindingBuilder = (Binder.BindingBuilder<M,String>)bindingBuilder;

		if (String.class.isAssignableFrom(viewObjPropertyType)) {
			// do nothing
		}
		else if (Integer.class.isAssignableFrom(viewObjPropertyType)
		      || int.class.isAssignableFrom(viewObjPropertyType)) {
			strBindingBuilder.withConverter(new StringToIntegerConverter(null,
														 		  	  	 "Must be a number"));
		}
		else if (Float.class.isAssignableFrom(viewObjPropertyType)
			  || float.class.isAssignableFrom(viewObjPropertyType)) {
			strBindingBuilder.withConverter(new StringToFloatConverter(null,
																	   "Must be a number"));
		}
		else if (Double.class.isAssignableFrom(viewObjPropertyType)
			  || double.class.isAssignableFrom(viewObjPropertyType)) {
			strBindingBuilder.withConverter(new StringToDoubleConverter(null,
																		"Must be a number"));
		}
		else if (Long.class.isAssignableFrom(viewObjPropertyType)
			  || long.class.isAssignableFrom(viewObjPropertyType)) {
			strBindingBuilder.withConverter(new StringToLongConverter(null,
																	  "Must be a number"));
		}
		else if (BigDecimal.class.isAssignableFrom(viewObjPropertyType)) {
			strBindingBuilder.withConverter(new StringToBigDecimalConverter(null,
																		    "Must be a number"));
		}
		else if (BigInteger.class.isAssignableFrom(viewObjPropertyType)) {
			strBindingBuilder.withConverter(new StringToBigIntegerConverter(null,
																			"Must be a number"));
		}
		else if (Date.class.isAssignableFrom(viewObjPropertyType)) {
			Binder.BindingBuilder<M,LocalDate> dateBindingBuilder = (Binder.BindingBuilder<M,LocalDate>)bindingBuilder;

			dateBindingBuilder.withConverter(new LocalDateToDateConverter());

		}
		else if (OID.class.isAssignableFrom(viewObjPropertyType)) {
			FactoryFrom<String,T> oidFactory = oidAsStr -> ReflectionUtils.invokeStaticMethod(viewObjPropertyType,
													  										  "forId",new Class<?>[] {String.class},new String[] {oidAsStr});
			Converter<String,T> converter = new VaadinOIDConverter(oidFactory);
			strBindingBuilder.withConverter(converter);

		}
		else if (ReflectionUtils.canBeCreatedFromString(viewObjPropertyType)) {
			// try to see if the [view obj] property type has a valueOf or a constructor from String
			Converter<String,T> converter = new Converter<String,T>() {
													private static final long serialVersionUID = -2471867717063606318L;

													@Override
													public Result<T> convertToModel(final String value,final ValueContext context) {
														T instance = value != null ? ReflectionUtils.createInstanceFromString(viewObjPropertyType,
																											  				  value)
																				   : null;
														return Result.ok(instance);
													}
													@Override
													public String convertToPresentation(final T value,final ValueContext context) {
														String outStr = value != null ? value.toString() : null;
														return outStr;
													}
											};
			strBindingBuilder.withConverter(converter);
		} else {
			log.debug("Could NOT bind a string converter for a [view obj]'s property of type {}",
					  viewObjPropertyType);
		}
	}
	private static <M extends UIViewObject> void _bindRequiredFor(final Binder.BindingBuilder<M,?> bindingBuilder,
													  			  final HasValue<?,?> viewComp,
													  			  final VaadinViewField viewFieldAnnot,
													  			  final UII18NService i18n) {
		if (viewFieldAnnot != null
		 && viewFieldAnnot.required()) {
			if (GeneratedVaadinTextField.class.isAssignableFrom(viewComp.getClass())) {
//			 || RichTextEditor.class.isAssignableFrom(viewComp.getClass())) {
				bindingBuilder.asRequired((value,context) -> {
												String strVal = value != null ? value.toString() : null;
												if (Strings.isNOTNullOrEmpty(strVal)) return ValidationResult.ok();
												return ValidationResult.error(i18n.getMessage(viewFieldAnnot.i18nKeyForRequiredMessage()));
										  });
			} else if (MultiSelect.class.isAssignableFrom(viewComp.getClass())) {
				bindingBuilder.asRequired((value,context) -> {
												Collection<?> v = (Collection<?>) value;
												if (value != null && CollectionUtils.hasData(v)) return ValidationResult.ok();
												return ValidationResult.error(i18n.getMessage(viewFieldAnnot.i18nKeyForRequiredMessage()));
										  });
			} else {
				bindingBuilder.asRequired((value,context) -> {
												if (value != null && value instanceof String) { // fields with string value (ej: tiny)
													String strVal = value.toString();
													if (Strings.isNOTNullOrEmpty(strVal)) return ValidationResult.ok();
													return ValidationResult.error(i18n.getMessage(viewFieldAnnot.i18nKeyForRequiredMessage()));
												} 
												// fields with collection value
												if (value != null && !(value instanceof Collection)) return ValidationResult.ok();
												if (value != null && value instanceof Collection && CollectionUtils.hasData((Collection<?>)value)) { 
													return ValidationResult.ok();
												}
												return ValidationResult.error(i18n.getMessage(viewFieldAnnot.i18nKeyForRequiredMessage()));
										 });
			}
		}
		if (DatePicker.class.isAssignableFrom(viewComp.getClass())) {
			DatePicker datePickr = (DatePicker)viewComp;
			datePickr.setErrorMessage(i18n.getMessage("validation.datefield.parseError"));
			// datePickr.setDateOutOfRangeMessage(i18n.getMessage("validation.datefield.outOfRange"));
			
			// format
			datePickr.setAllowedCharPattern("yyyy/MM/dd");
			if (i18n.getCurrentLanguage().isIn(Language.SPANISH)) {
				datePickr.setAllowedCharPattern("dd/MM/yyyy");
			}
		} else if (DateTimePicker.class.isAssignableFrom(viewComp.getClass())) {
			DateTimePicker dateTimePickr = (DateTimePicker)viewComp;
			dateTimePickr.setErrorMessage(i18n.getMessage("validation.datefield.parseError"));
			// datePickr.setDateOutOfRangeMessage(i18n.getMessage("validation.datefield.outOfRange"));
			
			// format
			dateTimePickr.setTimePlaceholder("HH:mm");
			dateTimePickr.setDatePlaceholder("yyyy/MM/dd");
			if (i18n.getCurrentLanguage().isIn(Language.SPANISH)) {
				dateTimePickr.setDatePlaceholder("dd/MM/yyyy");
			}
		}
	}
	@SuppressWarnings({ "unchecked" })
	private static <M extends UIViewObject> void _bindNullRepresentationFor(final Binder.BindingBuilder<M,?> bindingBuilder,
																			final HasValue<?,?> viewComp) {
		// TextField: null representation
		if (GeneratedVaadinTextField.class.isAssignableFrom(viewComp.getClass())) {
			((Binder.BindingBuilder<M,String>)bindingBuilder).withNullRepresentation("");
		}
	}
	@SuppressWarnings({ "rawtypes","unchecked" })
	private static void _bindValidator(final Binder.BindingBuilder<?,?> bindingBuilder,
									   final VaadinViewField viewFieldAnnot,
									   final UII18NService i18n) {
		if (viewFieldAnnot != null
		 && viewFieldAnnot.useValidatorType() != VaadinVoidViewFieldValidator.class) {
			Class<? extends Validator> validatorType = viewFieldAnnot.useValidatorType();
			try {
				// create a validator instance
				Validator validator = ReflectionUtils.createInstanceOf(validatorType,
																	   new Class<?>[] { UII18NService.class },new Object[] { i18n });
				// if the field values is NOT required, the validator should NOT be run
				// ... but sometimes the validator checks that the field value is NOT null
				//	   so wrap the validator and return valid=true if field value is null
				if (viewFieldAnnot.required() == false) {
					Validator wrappingValidator = new VaadinNotRequiredWrappingValidator(validator);
					bindingBuilder.withValidator(wrappingValidator);
				} else {
					bindingBuilder.withValidator(validator);
				}
			} catch (Throwable th) {
				log.error("Could NOT create a view field validator of type {}; do the validator type has a {}-based constructor?: {}",
						  validatorType,
						  UII18NService.class,
						  th.getMessage(),th);
			}
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	private static class VaadinNotRequiredWrappingValidator<T>
	   	      implements Validator<T> {
		
		private static final long serialVersionUID = 6507813177664382465L;

		private final Validator<T> _wrappedValidator;
		
		@Override
		public ValidationResult apply(final T value,final ValueContext context) {
			if (value == null || Strings.isNullOrEmpty(value.toString())) return ValidationResult.ok();
			return _wrappedValidator.apply(value,context);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CAPTION & PLACE-HOLDER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * view component's [caption] and [place holder] can be set using one of
	 * these two methods:
	 *		a) use UIVaadinViews util that "scans" view's @R01UIViewComponentLabels components fields
	 *		   and "automatically" sets the caption and place-holder of the component
	 *		   ie:
	 *		        a view component field declared like:
	 *					@R01UIViewComponentLabels(captionI18NKey="object.name",useCaptionI18NKeyAsPlaceHolderKey=true)
	 *					private final TextField _txtName = new TextField();
	 *				will look after the i18n key [object.name] and set the caption
	 *				... since useCaptionI18NKeyAsPlaceHolderKey=true, the same text will be used as placeholder
	 *				alternatively, a different i18n key can be set for the place-holder
	 *
	 *		   To use this annotation-based method, just:
	 *				UIVaadinViews.using(_i18n)
	 *								.setI18NLabelsOf({theView instance})
	 *
	 *		b) Manually set the caption and place-holder:
	 *				_txtName.setCaption(_i18n.getMessage("object.name"));
	 *				_txtName.setPlaceholder(_i18n.getMessage("object.name"));
	 * <pre class='brush:java'>
	 * 		UIVaadinViews.using(i18n)
	 * 						.setI18NLabelsOf(view);
	 * </pre>
	 * @param binder
	 * @param i18n
	 * @return
	 */
	public static UIVaadinViewI18NLabelsStep using(final UII18NService i18n) {
		return new VaadinViews() { /* nothing */ }
						.new UIVaadinViewI18NLabelsStep(i18n);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class UIVaadinViewI18NLabelsStep {
		private final UII18NService _i18n;

		public <V extends VaadinComponent> void setI18NLabelsOf(final V view) {
			_setLabels(view,
					   _i18n);
		}
	}

	private static <V extends VaadinComponent> void _setLabels(final V view,
														  	   final UII18NService i18n)  {
		final Class<?> viewType = view.getClass();
		final Field[] viewFields = ReflectionUtils.allFields(viewType);
		for (final Field viewCompField : viewFields) {
			try {
				if (!ReflectionUtils.isImplementing(viewCompField.getType(),Component.class)) continue;

				// [1] - Get the component field and the corresponding view obj type
				Component vaadinComponent = ReflectionUtils.fieldValue(view,viewCompField,
																	   false);		// do not use Accessors

				// BEWARE!	If the component is null maybe the call to
				// 						VaadinViews.using(_i18n)
				//								   .setI18NLabelsOf(this);
				// 			is done in the WRONG place:
				//	The problem arises when a form extends a base form:
				//	public class MyEditorBase {
				//		protected MyEditorBase(final UII18NService i18n,
				//							   final final Class<V> viewObjType) {
				//			VaadinViews.using(_i18n)
				//					   .setI18NLabelsOf(this);	<--- WRONG!!! this MUST be called at the
				//		}													  concrete type constructor!!!
				//	}
				//	public class MyEditor
				//		 extends MyEditorBase {
				//
				// 		@VaadinViewField(bindToViewObjectFieldNamed="myField",required=false)
				//		@LangIndependentVaadinViewField
				//		@VaadinViewComponentLabels(captionI18NKey="myKey",useCaptionI18NKeyAsPlaceHolderKey=true)
				//		protected final TextField _txtMyField = new TextField();
				//
				//		public MyEditor() {
				//			super(i18n,
				//				  MyViewObj.class);
				//		}
				//	}
				//	Since the call to
				// 				VaadinViews.using(_i18n)
				//						   .setI18NLabelsOf(this);
				//	is done at the BASE type, the _txtMyField field IS NOT YET initialized!
				//	... so it's NULL!!
				//	ensure to call
				// 				VaadinViews.using(_i18n)
				//						   .setI18NLabelsOf(this);
				//	at the MyEditor constructor
				if (vaadinComponent == null) continue;		// parent field on AbstractComponent

				// [2] - Get the @R01UIViewComponentCaption annotation which contains the view component's caption (label)
				//		 and place holder values
				VaadinViewComponentLabels labelsAnnot = viewCompField.getAnnotation(VaadinViewComponentLabels.class);
				if (labelsAnnot != null) {
					String viewCompCaption =  i18n.getMessage(labelsAnnot.captionI18NKey());
					String viewCompPlaceHolder = labelsAnnot.useCaptionI18NKeyAsPlaceHolderKey()
																? viewCompCaption
																: Strings.isNOTNullOrEmpty(labelsAnnot.placeholderI18NKey()) ? labelsAnnot.placeholderI18NKey()
																															 : null;
					_setViewComponentCaption(vaadinComponent,
								   			 viewCompCaption,labelsAnnot.captionI18NKey());	// caption & default value
					_setViewComponentPlaceHolder(vaadinComponent,
												 viewCompPlaceHolder);
				}
			} catch (Throwable th) {
				log.error("Could NOT set caption or placeholder of one of the view components of {}",
						  view.getClass(),
						  th.getMessage(),th);
			}
		} // for
	}
	private static void _setViewComponentCaption(final Component vaadinComponent,
								  				 final String viewCompCaption,final String viewCompCaptionDefaultValue) {
		if (!(vaadinComponent instanceof HasLabel)) return;
		
		String caption = Strings.isNOTNullOrEmpty(viewCompCaption) ? viewCompCaption
																   : viewCompCaptionDefaultValue;
		((HasLabel)vaadinComponent).setLabel(caption);
	}
	private static void _setViewComponentPlaceHolder(final Component vaadinComponent,
													 final String viewCompPlaceHolder) {
		if (Strings.isNullOrEmpty(viewCompPlaceHolder)) return;
		VaadinComponents.asHasPlaceholder(vaadinComponent)
						.setPlaceholder(viewCompPlaceHolder);
	}
}

