package r01ui.base.components.layout;

import java.util.Set;

import com.vaadin.shared.Registration;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.VerticalLayout;

import r01f.facets.HasLanguage;
import r01f.locale.I18NKey;
import r01f.locale.Language;
import r01f.ui.i18n.UII18NService;
import r01f.ui.vaadin.view.VaadinComponent;
import r01f.ui.vaadin.view.VaadinViewFactories.VaadinViewFactoryFrom;
import r01f.ui.viewobject.UIViewObject;
import r01f.ui.viewobject.UIViewObjectByLanguage;
import r01f.ui.viewobject.UIViewObjectInLanguage;
import r01ui.base.components.form.VaadinFormEditsViewObject;

/**
 * lang-dependent HORIZONTAL tabbed views
 * <pre>
 * +===============================================================+
 * |                                                               |
 * |   +- [ es ] [ eu ] ---------------------------------------- + |
 * |   |                                                         | |
 * |   |                                                         | |
 * |   |   Each of the tabs is a [V]                             | | 
 * |   |                                                         | |
 * |   |                                                         | |
 * |   +---------------------------------------------------------+ |
 * +===============================================================+
 * </pre>
 * See the base type for usage instructions
 */
public class VaadinUILangHTabbedView<// the data being binded at the view; usually D=VIL but it does NOT have to be that way
									 D extends UIViewObject,
									 // the component used to edit / show the [lang-dependent] view object (VIL)
									 F extends Component & VaadinComponent & HasLanguage
									 		 & VaadinFormEditsViewObject<D>, 		// the view uses vaadin ui binder
									 // the [view obj] that contains [lang dependent view objs] (VIL)
									 VBL extends UIViewObjectByLanguage<VIL>,				// the view obj that contains lang dependent view objs
									 // the [lang dependent view obj]
									 VIL extends UIViewObjectInLanguage> 					// the lang dependent view obj	{
	 extends VaadinUILangTabbedViewBase<D,F,
	 									VBL,VIL> {

	private static final long serialVersionUID = 8242794789527302463L;
/////////////////////////////////////////////////////////////////////////////////////////
//	UI FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	protected final TabSheet _tabs = new TabSheet();
	protected final Label _lblTitle = new Label();
	
	protected VerticalLayout _vl = new VerticalLayout();
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinUILangHTabbedView(final UII18NService i18n,
								   final VaadinViewFactoryFrom<Language,F> inLangFormFactory) {
		super(i18n,
			  inLangFormFactory);
		_tabs.setResponsive(true);
		super.setCompositionRoot(_tabs);
	}
	
	public VaadinUILangHTabbedView(final UII18NService i18n,
								   final I18NKey titleKey,
								   final VaadinViewFactoryFrom<Language,F> inLangFormFactory) {
		super(i18n,
			  inLangFormFactory);
		_tabs.setResponsive(true);
		_lblTitle.setCaption(i18n.getMessage(titleKey).toUpperCase());
		_vl.addComponents(_lblTitle,_tabs);
		_vl.setSpacing(false);
		super.setCompositionRoot(_vl);
	}
	public VaadinUILangHTabbedView(final UII18NService i18n,
								   final Set<Language> langs,
								   final VaadinViewFactoryFrom<Language,F> inLangFormFactory) {
		this(i18n,
			 inLangFormFactory);
		this.addTabsFor(langs);
	}
	public VaadinUILangHTabbedView(final UII18NService i18n,
								   final Set<Language> langs,
								   final I18NKey titleKey,
								   final VaadinViewFactoryFrom<Language,F> inLangFormFactory) {
		this(i18n,
			 titleKey,
			 inLangFormFactory);
		this.addTabsFor(langs);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TABS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void _addLanguageTabComponent(final F view) {
		_tabs.addComponent(view);
	}
	@Override
	protected void _removeLanguageTabComponent(final F view) {
		_tabs.removeComponent(view);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SELECTION
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override @SuppressWarnings("unchecked")
	public F getSelectedTab() {
		return (F)_tabs.getSelectedTab();
	}
	@Override
	public void setSelectedTab(final F view) {
		_tabs.setSelectedTab(view);
	}
	@Override
	public void setSelectedTab(final int index) {
		_tabs.setSelectedTab(index);
	}
	@Override
	public Registration addSelectedTabChangeListener(final SelectedTabChangeListener listener) {
		return _tabs.addSelectedTabChangeListener(listener);
	}
}
