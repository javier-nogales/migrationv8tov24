package r01ui.base.components.layout;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.vaadin.data.Binder;
import com.vaadin.data.HasValue;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Component;
import com.vaadin.ui.Composite;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.facets.HasLanguage;
import r01f.locale.Language;
import r01f.reflection.ReflectionUtils;
import r01f.reflection.ReflectionUtils.FieldAnnotated;
import r01f.ui.i18n.UII18NService;
import r01f.ui.vaadin.annotations.LangIndependentVaadinViewField;
import r01f.ui.vaadin.view.VaadinComponent;
import r01f.ui.vaadin.view.VaadinViewFactories.VaadinViewFactoryFrom;
import r01f.ui.vaadin.view.VaadinViewI18NMessagesCanBeUpdated;
import r01f.ui.viewobject.UIViewObject;
import r01f.ui.viewobject.UIViewObjectByLanguage;
import r01f.ui.viewobject.UIViewObjectInLanguage;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.locale.Languages;
import r01ui.base.components.form.VaadinFormEditsViewObject;
import r01ui.base.components.form.VaadinViewTracksChanges;

/**
 * Base type for lang-dependent tabbed views
 * <pre>
 * +===============================================================+    +===============================================================+
 * |                                                               |    |     ________________________________________________________  |
 * |   +- [ es ] [ eu ] ---------------------------------------- + |    | [es]                                                        | |
 * |   |                                                         | |    | [eu]                                                        | |
 * |   |                                                         | |    |    |    Each of the tabs is a [V]                           | |
 * |   |   Each of the tabs is a [V]                             | |    |    |                                                        | |
 * |   |                                                         | |    |    +________________________________________________________+ |
 * |   |                                                         | |    +===============================================================+
 * |   +---------------------------------------------------------+ |
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 * </pre>
 *
 * Language IN-dependent fields:
 * 		Any component of [V] annotated with @R01UILangIndependentViewField
 * 		will be updated in all languages when changed in any of them
 * 		ie:
 * 			If a view component declared as:
 * 				@R01UILangIndependentViewField
 *				private final TextField _txtName = new TextField();
 *			when this component is updated in the [es] tab, the new value
 *			is automatically copied to the corresponding component at the [eu] tab
 *
 * Usage:
 * <pre class='brush:java'>
 * 	// Given a [model object]:
 *	@Accessors(prefix="_")
 *	public class MyTestObj
 *	  implements HasLangDependentNamedFacet {
 *
 *		@Getter @Setter private AppCode _appCode;					// this field is lang-INDEPENDENT
 *		@Getter @Setter private LanguageTexts _nameByLanguage;		// this field is lang-DEPENDENT
 *
 *		@Getter private final transient LanguageTextsWrapper<MyTestObj> _name = LanguageTextsWrapper.atHasLangDependentNamedFacet(this);
 *
 *		@Override
 *		public LangDependentNamed asLangDependentNamed() {
 *			return new LangDependentNamedDelegate<>(this);
 *		}
 *	}
 *
 * 	// ... and the [view object] counterpart of the [model object] that just wraps the [model object]
 *  // The #getViewObjectFor(lang) method returns a [view object] for the given [language]
 *	public class MyTestViewObjByLang
 *		 extends UIViewObjectWrappedBase<MyTestObj>
 *	  implements UIViewObjectByLanguage<MyTestViewObjInLang> {
 *
 *		public MyTestViewObjByLang(final MyTestObj wrappedModelObject) {
 *			super(wrappedModelObject);
 *		}
 *		@Override
 *		public MyTestViewObjInLang getViewObjectFor(final Language lang) {
 *			return new MyTestViewObjInLang(_wrappedModelObject,
 *										   lang);
 *		}
 *	}
 *
 *	// The [language]-dependent [view object]
 *  // ... just wraps the "original" [model object] in a way that every field-accessor
 *  // returns the data in the required [language]
 *	public class MyTestViewObjInLang
 *		 extends UIViewObjectWrappedBase<MyTestObj>
 *	  implements UIViewObjectInLanguage {
 *
 *		private final Language _lang;
 *
 *		public MyTestViewObjInLang(final MyTestObj wrappedModelObj,
 *									  final Language lang) {
 *			super(wrappedModelObj);
 *			_lang = lang;
 *		}
 *		////////// appCode
 *		public static final String APP_CODE_FIELD = "appCode";
 *		public AppCode getAppCode() {
 *			return _wrappedModelObject.getAppCode();
 *		}
 *		public void setAppCode(final AppCode appCode) {
 *			_wrappedModelObject.setAppCode(appCode);
 *		}
 *		////////// name
 *		public static final String NAME_FIELD = "name";
 *		public String getName() {
 *			return _wrappedModelObject.getName()
 *									  .getInOrNull(_lang);
 *		}
 *		public void setName(final String name) {
 *			_wrappedModelObject.getName()
 *							   .setIn(_lang,name);
 *		}
 *	}
 *
 * 	// The [language]-dependent [form]
 *	@Accessors(prefix="_")
 *	public class MyTestViewObjInLangForm
 *		 extends Composite
 *	  implements VaadinView,
 * 			 	 VaadinFormEditsViewObject<MyTestViewObjInLang>,
 * 			 	 HasLanguage {
 *
 *		@Getter private final Language _language;
 *
 * 		@VaadinViewField(bindToViewObjectFieldNamed=R01UITestViewObjInLang.APP_CODE_FIELD,
 *						 required=true)
 *		@VaadinViewComponentLabels(captionI18NKey="app.code",
 *								   useCaptionI18NKeyAsPlaceHolderKey=true)
 * 		@LangIndependentVaadinViewField		// changes in one lang are automagically reflected on any other lang
 *		private final TextField _txtAppCode = new TextField();
 *
 *		@VaadinViewField(bindToViewObjectFieldNamed=MyTestViewObjInLang.NAME_FIELD,
 *						 required=true)
 *		@VaadinViewComponentLabels(captionI18NKey="name",
 *								   useCaptionI18NKeyAsPlaceHolderKey=true)
 *		private final TextField _txtName = new TextField();
 *
 *		// Binder
 *		@Getter private final Binder<MyTestViewObjInLang> _vaadinUIBinder = new Binder<>(MyTestViewObjInLang.class);
 *
 *		public MyTestViewObjInLangForm(final UII18NService i18n,
 *									   final Language lang) {
 *			_language = lang;
 *
 *			// set ui labels
 *			VaadinViews.using(i18n)
 *					   .setI18NLabelsOf(this);
 *
 *			// Bind: automatic binding using using @VaadinViewField annotation of view fields
 *			VaadinViews.using(_vaadinUIBinder,i18n)
 *						.bindComponentsOf(this)
 *						.toViewObjectOfType(MyTestViewObjInLang.class);
 *			// Layout
 *			VerticalLayout vly = new VerticalLayout(_txtAppCode,
 *													_txtName);
 *			this.setCompositionRoot(vly);
 *		}
 *
 *		@Override
 *		public void editViewObject(final MyTestViewObjInLang viewObj) {
 *			_vaadinUIBinder.readBean(viewObj);
 *		}
 *		@Override
 *		public void writeAsDraftEditedViewObjectTo(final MyTestViewObjInLang viewObject) {
 *			_vaadinUIBinder.writeAsDraft(viewObject);
 *		}
 *		@Override
 *		public void setLanguage(final Language lang) {
 *			throw new UnsupportedOperationException();
 *		}
 *	}
 *
 *	// ... and finally the [tabs] > Vertical
 *	public class MyTestVTabsForm
 *		 extends VaadinUILangVTabbedView<MyTestViewObjInLang,
 *		 								 MyTestViewObjInLangForm,
 *		 							 	 MyTestViewObjByLang,MyTestViewObjInLang> {
 *
 *		public MyTestVTabsForm(final UII18NService i18n) {
 *			super(i18n,
 *  				  // The lang-tabs
 *				  Lists.newArrayList(Language.SPANISH,Language.BASQUE),
 *				  // the language-dependent form factory: VaadinViewFactoryFrom<Language,MyTestViewObjInLangForm>
 *				  MyTestViewObjInLangForm::new);
 *		}
 *	}
 *	// ... and Horizontal
 *	public class MyTestHTabsForm
 *		 extends VaadinUILangHTabbedView<MyTestViewObjInLang,
 *		 								 MyTestViewObjInLangForm,
 *		 							 	 MyTestViewObjByLang,MyTestViewObjInLang> {
 *
 *		private static final long serialVersionUID = 3683193830925796973L;
 *
 *		public MyTestHTabsForm(final UII18NService i18n) {
 *			super(i18n,
 *  			  // The lang-tabs
 *				  Lists.newArrayList(Language.SPANISH,Language.BASQUE),
 *				  // the language-dependent form factory: VaadinViewFactoryFrom<Language,MyTestViewObjInLangForm>
 *				  MyTestViewObjInLangForm::new);
 *		}
 *	}
 *
 *  // Now test > check that [app code] is replicated in every lang
 *	public class MyTabsView
 *		 extends Composite
 *	  implements View {
 *		@Inject
 *		public MyTestTabsView(final UII18NService i18n) {
 *			MyTestVTabsForm vTabs = new MyTestVTabsForm(i18n);
 *			MyTestHTabsForm hTabs = new MyTestHTabsForm(i18n);
 *
 *			VerticalLayout vly = new VerticalLayout(vTabs,
 *													hTabs);
 *			this.setCompositionRoot(vly);
 *
 *			MyTestObj modelObj = new MyTestObj();
 *			MyTestViewObjByLang viewObj = new MyTestViewObjByLang(modelObj);
 *			vTabs.bindUIControlsTo(viewObj);
 *			hTabs.bindUIControlsTo(viewObj);
 *		}
 * </pre>
 *
 * @param <V>
 * @param <VBL>
 * @param <VIL>
 */
@Slf4j
public abstract class VaadinUILangTabbedViewBase<// the data being binded at the view; usually D=VIL but it does NOT have to be that way
											 	 D extends UIViewObject,
											 	 // the component used to edit / show the [lang-dependent] view object (VIL)
											 	 F extends Component & VaadinComponent & HasLanguage
										 	             & VaadinFormEditsViewObject<D>, 		// the view uses vaadin ui binder
										 	     // the [view obj] that contains [lang dependent view objs] (VIL)
										 	     VBL extends UIViewObjectByLanguage<VIL>,				// the view obj that contains lang dependent view objs
										 	     // the [lang dependent view obj]
										 	     VIL extends UIViewObjectInLanguage> 					// the lang dependent view obj
	          extends Composite
           implements TabSheet.SelectedTabChangeListener,
  			 		  VaadinViewTracksChanges,
  			 		  VaadinViewI18NMessagesCanBeUpdated,
  			 		  VaadinFormEditsViewObject<VBL> {

	private static final long serialVersionUID = -4245277897023751719L;

/////////////////////////////////////////////////////////////////////////////////////////
//  SERVICES
/////////////////////////////////////////////////////////////////////////////////////////
	protected final UII18NService _i18n;

/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	protected final VaadinViewFactoryFrom<Language,F> _inLangFormFactory;
	protected final FormIterable _langForms;

	protected VBL _viewObject;
	protected boolean _syncLangForms;	// stops the [lang form] lang-independent data sync-ing

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR / BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinUILangTabbedViewBase(final UII18NService i18n,
						       	  	  final VaadinViewFactoryFrom<Language,F> inLangFormFactory) {
		_i18n = i18n;
		_inLangFormFactory = inLangFormFactory;
		_langForms = new FormIterable(Lists.newArrayList());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GET
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the list of languages
	 * @return
	 */
	public Set<Language> getLanguages() {
		return _langForms.getLanguages();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	REMOVE
/////////////////////////////////////////////////////////////////////////////////////////
	public void removeTabsFor(final Set<Language> langs) {
		if (CollectionUtils.isNullOrEmpty(langs)) return;	// nothing to do
		
		langs.stream()
			 .forEach(this::removeTabFor);
	}
	/**
	 * Removes tab for the given lang
	 * @param lang
	 */
	public void removeTabFor(final Language lang) {
		if (lang == null) return;		// nothing to do
		
		VaadinTabbedLangForm langTab = _langForms.tabFormFor(lang)
												 .orNull();
		if (langTab == null) return;	// nothing to do
		F frmInLang = langTab.getForm();
		_removeLanguageTabComponent(frmInLang);
		_langForms.removeTabFor(lang);
	}
	/**
	 * Removes all tabs
	 */
	public void removeAllTabs() {
		this.getLanguages()
			.stream()
			.forEach(this::removeTabFor);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ADD
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings({ "rawtypes","unchecked" })
	public void addTabsFor(final Set<Language> langs) {
		if (CollectionUtils.isNullOrEmpty(langs)) return; 	// nothing to do
		
		// store the language forms ... using the form factory, create a form for each language
		Collection<VaadinTabbedLangForm> tabs = langs.stream()
													 // do NOT add a NEW tab if it already exists!
													 .filter(lang -> !_langForms.getLanguages().contains(lang))
													 // create a NEW form for the given language
													 .map(lang -> {
																// create the tab
																F langForm = _inLangFormFactory.from(_i18n,
																									 lang);
																// set the tab caption
																if (Strings.isNullOrEmpty(langForm.getCaption())) {
																	String captionKey = Strings.customized("tab.caption.{}",
																										   Languages.languageLowerCase(lang));
																	langForm.setCaption(_i18n.getMessage(captionKey));
																}
																
																// If the [form] is tracking changes (implements VaadinViewTracksChanges), update the view 
																// so when any @VaadinViewField annotated [form field] is updated the [form] CHANGED status
																// is also updated
																_addValueChangeListenerToEveryFormHasValueImplementingField(langForm);
																
																// There exists [form fields] that has the same value no matter the language 
																// (fields annotated with @LangIndependentVaadinViewField)
																// When any of these fields is updated in ANY of the language forms,
																// the value of the SAME field at ANY OTHER language forms must be updated
																_addValueChangeListenerToEveryLangIndependentVaadinViewAnnotatedFormFields(langForm);
																
																return new VaadinTabbedLangForm(lang,langForm);
														})
													 .collect(Collectors.toList());
		_langForms.addAll(tabs);

		// Handle tab changes
		this.addSelectedTabChangeListener(this);

		// Set the first tab the one for the current language
		// ... the other language tabs are set in the received order
		VaadinTabbedLangForm firstForm = _langForms.tabFormFor(_i18n.getCurrentLanguage())
												   .or(_langForms.tabFormFor(Language.DEFAULT))
												   .or(_langForms.tabFormFor(Iterables.get(langs, 0))
												   .orNull());
		if (firstForm == null) throw new IllegalStateException("Could NOT find a form for " + _i18n.getCurrentLanguage() + " nor for " + Language.DEFAULT);

		firstForm.setCurrent(true);
		_addLanguageTabComponent(firstForm.getForm());		// first tab: current language

		Collection<VaadinTabbedLangForm> remainingTabs = _langForms.allTabsExcept(firstForm);
		for (final VaadinTabbedLangForm otherForm : remainingTabs) _addLanguageTabComponent(otherForm.getForm());

		// select the first tab
		this.setSelectedTab(firstForm.getForm());
	}

	protected abstract void _addLanguageTabComponent(final F form);
	protected abstract void _removeLanguageTabComponent(final F form);

/////////////////////////////////////////////////////////////////////////////////////////
//	GET / SET SELECTED TAB
/////////////////////////////////////////////////////////////////////////////////////////	
	public abstract F getSelectedTab();
	
	public abstract void setSelectedTab(final F form);
	public abstract void setSelectedTab(final int index);
	public void setSelectedTab(final Language lang) {
		for (F form : this.langFormIterable()) {
			if (form.getLanguage() == lang) {
				this.setSelectedTab(form);
				break;
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EVENTS
/////////////////////////////////////////////////////////////////////////////////////////	
	public abstract Registration addSelectedTabChangeListener(final SelectedTabChangeListener listener);
	
	/**
	 * There exists [form fields] that has the same value no matter the language 
	 * (fields annotated with @LangIndependentVaadinViewField)
	 * When any of these fields is updated in ANY of the language forms,
	 * the value of the SAME field at ANY OTHER language forms must be updated
	 * This is usually the case of DATES, COMBO values, CHECKBOXES or RADIO BUTTONS (but not only)
	 * that have language-independent values
	 */
	private void _addValueChangeListenerToEveryLangIndependentVaadinViewAnnotatedFormFields(final F form) {
		FieldAnnotated<LangIndependentVaadinViewField>[] annLangIndFields = ReflectionUtils.fieldsAnnotated(form.getClass(),
																										    LangIndependentVaadinViewField.class);
		if (CollectionUtils.isNullOrEmpty(annLangIndFields)) return;	// nothing to do
		
		for (FieldAnnotated<LangIndependentVaadinViewField> annLangIndField : annLangIndFields) {
			Field langIndField = annLangIndField.getField();		// a field annotated with @LangIndependentVaadinViewField

			if (!ReflectionUtils.isImplementing(langIndField.getType(),HasValue.class)) {
				log.error("Error while registering [value change listener] for lang-INdependent form field {} of {}: it's is annotated with @{} BUT it's NOT an instance of {}: IGNORED",
						  langIndField.getName(),form,
						  LangIndependentVaadinViewField.class.getSimpleName(),
						  HasValue.class.getSimpleName());
				continue;
			}

			HasValue<?> langIndHasValue = (HasValue<?>)ReflectionUtils.fieldValue(form,langIndField,false);	// ... the field value on the form

			// add a value change listener so when the field gets updated, the corresponding
			// fields at the other language dependent forms are also updated with the same value
			langIndHasValue.addValueChangeListener(valChangeEvent -> {
														if (!_syncLangForms) return;

														_syncLangForms = false;	// BEWARE!|

														// Get the updated value
														Object updatedVal = valChangeEvent.getValue();

														// ... and set it to the corresponding field at the other language forms
														for (F otherForm : _langForms.langFormIterable()) {
															@SuppressWarnings("unchecked")
															HasValue<Object> otherLangIndHasValue = (HasValue<Object>)ReflectionUtils.fieldValue(otherForm,
																																 langIndField,
																																 false);
															if (valChangeEvent.getSource() == otherLangIndHasValue) continue;	// do NOT sync the source
															otherLangIndHasValue.setValue(updatedVal);		// update

															// the "other" form has change... so if if it's tracking changes update the state
															if (otherForm instanceof VaadinViewTracksChanges tracksChanges) {
																tracksChanges.setViewDataChanged(true);
															}
														}
														_syncLangForms = true;	// BEWARE!!
												   });
		}
	}
	/**
	 * If the [form] is tracking changes (implements VaadinViewTracksChanges), update the view 
	 * so when any @VaadinViewField annotated [form field] is updated the [form] CHANGED status
	 * is also updated
	 * To do so, add a [value change listener] to every @VaadinViewField annotated [form field] 
	 * so when it gets updated the [form] [tracking status] is updated
	 * 
	 * TODO review if this is really necessary 
	 * 		usually the [form] uses a [Vaadin Binder] that ALSO has a [value change listener]
	 * 		that updates the [form]'s [tracking status] (see VaadinViews utility)
	 * 
	 * @param form
	 */
	private void _addValueChangeListenerToEveryFormHasValueImplementingField(final F form) {
		if (!(form instanceof VaadinViewTracksChanges)) return;

		// TODO select implementation: add a ValueChangeListener to every @VaadinViewField annotated field or to every HasValue field?
		
//		// add a ValueChangeListener to every @VaadinViewField annotated field that updates the CHANGE status
//		// of the form
//		FieldAnnotated<VaadinViewField>[] annFormFields = ReflectionUtils.fieldsAnnotated(form.getClass(),
//																						  VaadinViewField.class);		
//		if (CollectionUtils.isNullOrEmpty(annFormFields)) return;
//		
//		// 1) get [form field]s implementing HasValue
//		Field[] hasValueFormFields = Stream.of(annFormFields)
//										   .map(FieldAnnotated::getField)	// a field annotated with @VaadinViewField
//										   .filter(formField -> {
//														boolean fieldImplementsHasValue = ReflectionUtils.isImplementing(formField.getType(),HasValue.class);
//														if (!fieldImplementsHasValue) log.error("Error while registering [value change listener] for lang-INdependent form field {} of {}: it's is annotated with @{} BUT it's NOT an instance of {}: IGNORED",
//																	  							formField.getName(),form,
//																	  							VaadinViewField.class.getSimpleName(),
//																	  							HasValue.class.getSimpleName());
//														return fieldImplementsHasValue;
//										  		   })
//										   .toArray(Field[]::new);
		
		// add the ValueChangeListener to every field implementing HasValue
		// this solution might be more general
		Field[] hasValueFormFields = ReflectionUtils.fieldsMatching(form.getClass(),
									   								formField -> ReflectionUtils.isImplementing(formField.getType(),HasValue.class));
		
		// 2) add the ValueChangeListener to every [form field]
		VaadinViewTracksChanges tracksChanges = (VaadinViewTracksChanges)form;
		
		Stream.of(hasValueFormFields)
			  .forEach(formField -> {
							// add the event
							HasValue<?> formFieldHasValue = (HasValue<?>)ReflectionUtils.fieldValue(form,formField,false);	// ... the field value on the form
							formFieldHasValue.addValueChangeListener(valChangeEvent -> {
																		Object oldVal = valChangeEvent.getOldValue();
																		Object updatedVal = valChangeEvent.getValue(); 	// or formFieldHasValue.getValue();
																		boolean changed = oldVal != null 
																					   && updatedVal != null ? oldVal.equals(updatedVal)
																													: oldVal != null 
																												   && updatedVal == null ? true
																																	   	 : oldVal == null 
																																	   	&& updatedVal != null ? true
																																	   						  : false;	// both null
																		// if the field value changed, update the [form]'s CHANGE status
																		if (changed) tracksChanges.setViewDataChanged(changed);
																	});
			  		   });
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TAB CHANGE EVENT HANDLER
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void selectedTabChange(final SelectedTabChangeEvent event) {
		final VaadinTabbedLangForm currTabForm = _langForms.currentTab();
		if (currTabForm != null
		 && this.getSelectedTab() == currTabForm.getForm()) return;	// no changed tab

		// change the form
		final F newCurrForm = this.getSelectedTab();
		_langForms.changeCurrentTabTo(newCurrForm);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	UIVaadinViewTracksChanges
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void setViewDataChanged(final boolean changed) {
		_langForms.langFormIterable()
		  		  .forEach(form -> {
		  			  			if (form instanceof VaadinViewTracksChanges tracksChanges) {
			  			  			tracksChanges.setViewDataChanged(changed);
		  			  			}
		  		  		   });
	}
	@Override
	public boolean hasViewDataChanged() {
		boolean outChanges = false;
		for (F form : _langForms.langFormIterable()) {
			if (form instanceof VaadinViewTracksChanges tracksChanges) {
				if (tracksChanges.hasViewDataChanged()) return true;
			}
		}
		return outChanges;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Although the usual case is that the view-binded object (of type D) is the [view object] of type VIL
	 * (D == VIL), there are circumstances where the view-binded object is NO of type VIL (D != VIL)
	 * ... so when binding the [view object] to the [ui controls] the [view object] of type VIL
	 * MUST be transformed to D
	 */
	@SuppressWarnings("unchecked")
	public D transformViewObjInLangToBindedObj(final VIL viewObjInLang) {
		// Override this method if D != VIL
		return (D)viewObjInLang;	// if D == VIL there's nothing to be transformed
	}
	/**
	 * Although the usual case is that the view-binded object (of type D) is the [view object] of type VIL
	 * (D == VIL), there are circumstances where the view-binded object is NO of type VIL (D != VIL)
	 * ... so when getting back the [view object] from the [ui controls] the binded object of type D
	 * MUST be copied into an instance of the [view object] of type VIL
	 */
	public void copyBindedObjDataToViewObjectInLang(final D bindedViewObjInLang,final VIL viewObjInLang) {
		// override this method if D != VIL
		return;	// if D == VIL just do nothing
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Binding
/////////////////////////////////////////////////////////////////////////////////////////
	////////// [viewObject] > [UI control] --------------
	@Override
	public void editViewObject(final VBL viewObject) {
		_viewObject = viewObject;

		// Set the [ui control] values from [view object]'s properties
		// (the [ui controls] are NOT binded to [view object]'s properties
		//  so when an [ui control] changes, the change is NOT reflected
		//  at the [view object property]

		// just "tell" all language-dependent view to bind to the corresponding
		// language-dependent view object
		_syncLangForms = false;	// BEWARE!	while setting the [view object] into the [lang view]
								//			STOP the [lang independent] components value sync-ing
		for (F langView : this.langFormIterable()) {
			Language lang = langView.getLanguage();
			VIL viewObjInLang = viewObject.getViewObjectFor(lang);

			if (viewObjInLang == null) {
				log.warn("NO [view object] for lang={}: cannot create the corresponding LangTabbedView!",lang);
				continue;
			}

			// maybe it has to be transformed to be binded: VIL -> D
			D bindedViewObjInLang = this.transformViewObjInLangToBindedObj(viewObjInLang);

			// ... and read the data
			langView.editViewObject(bindedViewObjInLang);
		}
		_syncLangForms = true;	// the [lang form] can now be sync-ed
	}
	////////// [UI control] > [viewObject] --------------
	@Override
	public void writeAsDraftEditedViewObjectTo(final VBL viewObj) {
		// just "tell" all language-dependent view to write the corresponding
		// language-dependent [view obj]
		for (F langView : this.langFormIterable()) {
			if (langView instanceof VaadinViewTracksChanges tracksChanges) {
				if (!tracksChanges.hasViewDataChanged()) continue;	// no changes
			}

			// get the [view obj] for the lang
			Language lang = langView.getLanguage();
			VIL viewObjInLang = viewObj.getViewObjectFor(lang);

			// maybe the binded object has another type: VIL -> D
			D bindedViewObjInLang = this.transformViewObjInLangToBindedObj(viewObjInLang);

			// UI -> object
			langView.writeAsDraftEditedViewObjectTo(bindedViewObjInLang);

			// and now back from D to VIL
			this.copyBindedObjDataToViewObjectInLang(bindedViewObjInLang,viewObjInLang);
		}
	}
	@Override
	public boolean writeIfValidEditedViewObjectTo(final VBL viewObj) {
		return _writeIfValidEditedViewObjectTo(viewObj,
											   false);	// do NOT ignore change tracking
	}
	public boolean writeIfValidAndIgnoringChangeTrackingEditedViewObjectTo(final VBL viewObj) {
		return _writeIfValidEditedViewObjectTo(viewObj,
											   true);	// ignore change tracking
	}
	private boolean _writeIfValidEditedViewObjectTo(final VBL viewObj,
													final boolean ignoreChangeTracking) {
		boolean allTabsValid = true;

		// just "tell" all language-dependent form to write the corresponding
		// language-dependent [view obj]
		for (F langView : this.langFormIterable()) {
			if (!ignoreChangeTracking 	// if ignoring change tracking the form is not written to the model obj
			 && (langView instanceof VaadinViewTracksChanges tracksChanges)) {
				if (!tracksChanges.hasViewDataChanged()) continue;	// no changes
			}
			// get the [view obj] for the lang
			Language lang = langView.getLanguage();
			VIL viewObjInLang = viewObj.getViewObjectFor(lang);

			// maybe the binded object has another type: VIL -> D
			D bindedViewObjInLang = this.transformViewObjInLangToBindedObj(viewObjInLang);

			// UI -> object
			boolean tabValid = langView.writeIfValidEditedViewObjectTo(bindedViewObjInLang);
			this.copyBindedObjDataToViewObjectInLang(bindedViewObjInLang,viewObjInLang);
			
			// if the tab is NOT valid, select it
			if (!tabValid) {
				allTabsValid = false;
				this.setSelectedTab(langView);
			}
		}
		return allTabsValid;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Binder<VBL> getVaadinUIBinder() {
		throw new UnsupportedOperationException();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isValid() {
		boolean allValid = true;
		for (F langForm : this.langFormIterable()) {
			Binder<D> vaadinUIBinder = langForm.getVaadinUIBinder();
			if (vaadinUIBinder != null) {
				boolean langFormValid = vaadinUIBinder.isValid();
				if (!langFormValid) {
					allValid = false;
					break;
				}
			}
		}
		return allValid;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	UPDATE I18N MESSAGES
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void updateI18NMessages(final UII18NService i18n) {
		for (final VaadinTabbedLangForm form : _langForms) {
			// update the tab caption
			String newCaptionKey = Strings.customized("tab.caption.{}",
													  Languages.languageLowerCase(form.getLang()));
			form.getForm()
				.setCaption(_i18n.getMessage(newCaptionKey));

			// update the tab form i18n messages
			if (form.getForm() instanceof VaadinViewI18NMessagesCanBeUpdated) {
				VaadinViewI18NMessagesCanBeUpdated i18nUpdatable = (VaadinViewI18NMessagesCanBeUpdated)form.getForm();
				i18nUpdatable.updateI18NMessages(i18n);
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TAB ACCESS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return true if there's any lang form
	 */
	public boolean hasLangForms() {
		return this.getLangFormCount() > 0;
	}
	/**
	 * @return the number of lang forms
	 */
	public int getLangFormCount() {
		return _langForms.size();
	}
	/**
	 * @return an {@link Iterable} over the lang forms
	 */
	public Iterable<F> langFormIterable() {
		return _langForms.langFormIterable();
	}
	/**
	 * @return a {@link Stream} over the lang forms
	 */
	public Stream<F> langFormStream() {
		Spliterator<VaadinTabbedLangForm> spliterator = _langForms != null ? _langForms.spliterator()
																		   : null;
		Stream<VaadinTabbedLangForm> stream = spliterator != null ? StreamSupport.stream(spliterator,false)
																  : Stream.empty();
		return stream.map(VaadinTabbedLangForm::getForm);
	}
	public F getLangFormFor(final Language lang) {
		return _langForms.tabFormFor(lang)
						 .orNull()
						 .getForm();
	}
	public F getLangViewFor(final Language lang) {
		return this.getLangFormFor(lang);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor
	protected class VaadinTabbedLangForm {
		@Getter private final Language _lang;
		@Getter private final F _form;
		@Getter @Setter private boolean _current;
	}
	@RequiredArgsConstructor
	protected class FormIterable
	     implements Iterable<VaadinTabbedLangForm> {

		private final Collection<VaadinTabbedLangForm> _theLangForms;

		////////// remove
		public boolean remove(final VaadinTabbedLangForm form) {
			return _theLangForms.remove(form);
		}
		public boolean removeTabFor(final Language lang) {
			VaadinTabbedLangForm toBeRemoved = this.tabFormFor(lang)
												   .orNull();
			return toBeRemoved != null ? this.remove(toBeRemoved)
									   : false;
		}

		////////// Add
		public void add(final VaadinTabbedLangForm form) {
			_theLangForms.add(form);
		}
		public void addAll(final Collection<VaadinTabbedLangForm> forms) {
			_theLangForms.addAll(forms);
		}

		////////// get
		public Set<Language> getLanguages() {
			return _theLangForms.stream()
								.map(VaadinTabbedLangForm::getLang)
								.collect(Collectors.toSet());
		}
		////////// size
		public int size() {
			return _theLangForms.size();
		}

		@Override
		public Iterator<VaadinTabbedLangForm> iterator() {
			return _theLangForms.iterator();
		}
		public Iterable<F> langFormIterable() {
			Collection<F> forms = _theLangForms.stream()
											   .map(VaadinTabbedLangForm::getForm)
											   .collect(Collectors.toList());
			return forms;
		}
		public Collection<VaadinTabbedLangForm> allTabsExcept(final VaadinTabbedLangForm form) {
			return _theLangForms.stream()
								.filter(otherForm -> form == otherForm ? false : true)	// not the given form
								.toList();
		}
		public Optional<VaadinTabbedLangForm> tabFormFor(final Language lang) {
			return Iterables.tryFind(_theLangForms,
								 	 f -> f.getLang().is(lang));
		}
		public Optional<VaadinTabbedLangForm> tabFormFor(final F form) {
			return Iterables.tryFind(_theLangForms,
									 f -> f.getForm() == form);
		}
		public VaadinTabbedLangForm currentTab() {
			VaadinTabbedLangForm langForm = Iterables.tryFind(_theLangForms,
															  VaadinTabbedLangForm::isCurrent)
													 .orNull();
			if (langForm == null) log.warn("NOT currently selected tab form!");
			return langForm;
		}
		private VaadinTabbedLangForm changeCurrentTabTo(final F form) {
			VaadinTabbedLangForm langForm = Iterables.tryFind(_theLangForms,
															  f -> f.getForm() == form)
														  .orNull();
			if (langForm == null) throw new IllegalStateException("Could NOT find required tab form!");
			return langForm;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unused")
	private Iterator<F> _languageViewIterator() {
		// AbstractComponent implements ComponentContainer which in turn implements HasComponents
		// ... HasComponent implements Iterable<Component>
		// so since this object extends TabSheet (which in turn extends AbstractComponent),
		// all TabSheet components can be iterated over
		// ... and since we're sure that the ONLY component added to the TabSheet are the
		// 	   language-dependent forms, we're sure that all components are of type F
		final Iterator<Component> componentIt = this.iterator();
		return Iterators.transform(componentIt,
								   comp -> (F)comp);
	}
}
