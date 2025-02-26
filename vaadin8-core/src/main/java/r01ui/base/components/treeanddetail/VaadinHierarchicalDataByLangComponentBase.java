package r01ui.base.components.treeanddetail;

import java.util.Set;
import java.util.function.Function;

import com.vaadin.ui.CustomComponent;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.locale.Language;
import r01f.patterns.Transfer;
import r01f.ui.i18n.UII18NService;
import r01f.ui.vaadin.view.VaadinView;
import r01f.ui.vaadin.view.VaadinViewFactories.VaadinViewFactoryFrom;
import r01f.ui.vaadin.view.VaadinViewI18NMessagesCanBeUpdated;
import r01f.ui.viewobject.UIViewObjectByLanguage;
import r01f.ui.viewobject.UIViewObjectInLanguage;
import r01ui.base.components.form.VaadinFormEditsViewObject;
import r01ui.base.components.tree.VaadinTreeData;

/**
 * <pre>
 * 		+===================================================+
 * 		| |es| [eu] [en]									| 
 * 		| |  +--------------------------------------------+ |
 * 		| |											      | |
 * 		| |												  | |
 * 		| |	[WIL: in lang view   						  | |
 * 		| |												  | |
 * 		| |												  | |
 * 		| +-----------------------------------------------+ |
 * 		+===================================================+
 * </pre>
 * @param <VBL>
 * @param <IL>
 * @param <VIL>
 * @param <FIL>
 */
@Accessors(prefix="_")
public abstract class VaadinHierarchicalDataByLangComponentBase<// by lang view object & in-lang view object shown in the [detail] view 
														   		VBL extends UIViewObjectByLanguage<VIL>,	// by-lang view obj
														   		VIL extends UIViewObjectInLanguage,			// lang dependent (in a lang)
														   		// the [view object] binded at the [tree] + [detail] component
														   		// (this [view object] MIGHT BE the VIL [view object in language] BUT is easier if NOT)
														   		VO extends UIViewObjectInLanguage
														   				 & VaadinHierarchicalDataViewObj<VO>,	
														   		// the component used to edit [tree] + [detail] (form)
														   		FIL extends VaadinHierarchicalDataInLangComponentBase<VO,? extends VaadinHierarchicalDataInLangForm<VO>>>	
	 		  extends CustomComponent 
	 	   implements VaadinView,
  			 		  VaadinViewI18NMessagesCanBeUpdated,
  			 		  VaadinFormEditsViewObject<VBL> {

	private static final long serialVersionUID = -8652414236006812625L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FILEDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final VaadinHierarchicalDataByLangTabbedView<VO,FIL,
														 		 VBL,VIL> _langTabbedView;
	private final VaadinViewFactoryFrom<Language,FIL> _inLangViewFactory;			// the view inside each tab
	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinHierarchicalDataByLangComponentBase(final UII18NService i18n, 
													 final Set<Language> portalAvailableLangs,
													 // form factory
													 final VaadinViewFactoryFrom<Language,FIL> inLangFormFactory,
													 // transform from [view object] to [vaadin tree data]
													 final Function<VIL,VaadinTreeData<VO>> viewObjInLangToVaadinTreeData,
													 // transform from [vaadin tree data] to [view object]
													 final Transfer<VaadinTreeData<VO>,VIL> vaadinTreeDataToViewObjInLang) {
		_inLangViewFactory = inLangFormFactory;
		_langTabbedView = new VaadinHierarchicalDataByLangTabbedView<>(i18n, 
														 					  portalAvailableLangs,
														 					  _inLangViewFactory) {
									private static final long serialVersionUID = 3240348896255554839L;
									
									@Override
									public VaadinTreeData<VO> transformViewObjInLangToBindedObj(final VIL viewObjInLang) {
										// Although the usual case is that the view-binded object (of type D) is the [view object] of type VIL
										// (D == VIL), there are circumstances where the view-binded object is NO of type VIL (D != VIL)      
										// ... so when binding the [view object] to the [ui controls] the [view object] of type VIL           
										// MUST be transformed to D  
										return viewObjInLangToVaadinTreeData.apply(viewObjInLang);
									}
									@Override
									public void copyBindedObjDataToViewObjectInLang(final VaadinTreeData<VO> bindedViewObjInLang, final VIL viewObjInLang) {
										// Although the usual case is that the view-binded object (of type D) is the [view object] of type VIL
										// (D == VIL), there are circumstances where the view-binded object is NO of type VIL (D != VIL)      
										// ... so when binding the [view object] to the [ui controls] the [view object] of type VIL           
										// MUST be transformed to D
										vaadinTreeDataToViewObjInLang.transfer(bindedViewObjInLang,viewObjInLang);
									}
						     };
		// build the ui
		this.setCompositionRoot(_langTabbedView);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Binding
/////////////////////////////////////////////////////////////////////////////////////////
	////////// [viewObject] > [UI control] --------------
	@Override
	public void editViewObject(final VBL byLangViewObj) {
		_langTabbedView.editViewObject(byLangViewObj);		// tell the lang tabbed view to bind 
	}
	////////// [UI control] > [viewObject] --------------
	@Override
	public void writeAsDraftEditedViewObjectTo(final VBL byLangViewObj) {
		_langTabbedView.writeAsDraftEditedViewObjectTo(byLangViewObj);
	}
	@Override
	public boolean writeIfValidEditedViewObjectTo(final VBL viewObj) {
		return _langTabbedView.writeIfValidEditedViewObjectTo(viewObj);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void updateI18NMessages(final UII18NService i18n) {
		// TODO update i18n
	}
}
