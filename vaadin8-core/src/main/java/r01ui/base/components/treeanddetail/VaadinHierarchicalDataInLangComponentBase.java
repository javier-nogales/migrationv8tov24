package r01ui.base.components.treeanddetail;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import com.vaadin.data.ValueProvider;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.HorizontalSplitPanel;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.facets.HasLanguage;
import r01f.locale.Language;
import r01f.patterns.Factory;
import r01f.ui.i18n.UII18NService;
import r01f.ui.vaadin.view.VaadinView;
import r01f.ui.vaadin.view.VaadinViewI18NMessagesCanBeUpdated;
import r01f.ui.viewobject.UIViewObjectInLanguage;
import r01ui.base.components.form.VaadinFormEditsViewObject;
import r01ui.base.components.tree.VaadinTree.VaadinTreeChangedEventListener;
import r01ui.base.components.tree.VaadinTreeData;
import r01ui.base.components.treeanddetail.VaadinHierarchicalDataTree.RootNodeSelectedListener;
import r01ui.base.components.treeanddetail.VaadinHierarchicalDataTree.VaadinHiearchicalDataTreeOnDropItemListener;

/**
 * A component that wraps the [tree] and the [detail] and coordinates interactions between both
 * ie:
 * 		- when an item is clicked at the [tree], the [detail] view should show the [edit form]
 * 		- when an item is added at the [tree], the [detail] view should also show the [edit form]
 * 		- when the [edit form] contains invalid or incomplete data, this circumstance should be let to know to the [tree]
 * 		  so it can be disabled because the user should NOT leave the [edit form] (for example by clicking another item at the [tree])
 * <pre>
 * 				 +-- This component that wraps the [tree] and the [detail]
 * 				 |
 * 		++++++++\/++++++++++++++++++++++++++++++++++
 * 		|										   |
 * 		| +--------+ +---------------------------+ |
 * 		| | node   | |                           | |
 * 		| |  +node | | D: Detail               <------- this is the form: D extends VaadinHierarchicalDataInLangForm<VO>
 * 		| |  +node | |                           | |
 * 		| |        | |                           | |
 * 		| +--------+ +---------------------------+ |
 * 		| 										   |
 * 		++++++++++++++++++++++++++++++++++++++++++++
 * </pre>
 */
@Accessors(prefix="_")
public abstract class VaadinHierarchicalDataInLangComponentBase<// the [view object] binded at the [tree] + [detail] component
														   		// (this [view object] MIGHT BE the VIL [view object in language] BUT is easier if NOT)
														   		VO extends UIViewObjectInLanguage			// a [view obj] (in a language)
														   				 & VaadinHierarchicalDataViewObj<VO>,
														   		// the detail view where the view obj is edited
														   		F extends VaadinHierarchicalDataInLangForm<VO>>
	 		  extends CustomField<VaadinTreeData<VO>> 	// BEWARE! TreeData<VIL>
  		   implements HasLanguage,
  			 		  VaadinView,
  			 		  VaadinFormEditsViewObject<VaadinTreeData<VO>>,	// BEWARE!! this is binding a TreeData<VIL>
  			 		  VaadinViewI18NMessagesCanBeUpdated {

	private static final long serialVersionUID = -741879526211369538L;

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter private Language _language;

/////////////////////////////////////////////////////////////////////////////////////////
//	UI COMPONENTS
/////////////////////////////////////////////////////////////////////////////////////////
	// the tree
	@Getter private final VaadinHierarchicalDataTree<VO> _treeGrid;

	// the detail form
	@Getter private final F _form;

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinHierarchicalDataInLangComponentBase(final UII18NService i18n,
													 final VaadinHierarchicalDataEditConfig settings,
													 final Language lang,
													 final Factory<VO> viewObjInLangFactory,
													 final F form) {
		this(i18n,
			 settings,
			 lang,
			 viewObjInLangFactory,
			 form,
			 null);	// no tree change event listener
	}
	@Deprecated
	public VaadinHierarchicalDataInLangComponentBase(final UII18NService i18n,
													 final VaadinHierarchicalDataEditConfig settings,
													 final Language lang,final Collection<Language> availableLangs,
													 final Factory<VO> viewObjInLangFactory,
													 final F form) {
		this(i18n,
			 settings,
			 lang,
			 viewObjInLangFactory,
			 form,
			 null);	// no tree change event listener
	}
	@Deprecated
	public VaadinHierarchicalDataInLangComponentBase(final UII18NService i18n,
													 final VaadinHierarchicalDataEditConfig settings,
													 final Language lang,final Collection<Language> availableLangs,
													 final Factory<VO> viewObjInLangFactory,
													 final F form,
													 final VaadinTreeChangedEventListener treeChangeEventListener) {
		this(i18n,
			 settings,
			 lang,
			 viewObjInLangFactory,
			 form,
			 treeChangeEventListener);
	}
	public VaadinHierarchicalDataInLangComponentBase(final UII18NService i18n,
													 final VaadinHierarchicalDataEditConfig settings,
													 final Language lang,
													 final Factory<VO> viewObjInLangFactory,
													 final F form,
													 final VaadinTreeChangedEventListener treeChangeEventListener) {
		_language = lang;

		////////// UI
		// the tree grid
		_treeGrid = new VaadinHierarchicalDataTree<>(i18n,
													 settings,
													 // [view obj] factory
													 viewObjInLangFactory);
		_form = form;
		_form.setVisible(false);		// not visible until a tree item is clicked

		////////// Events
		// what happens when an item is edited at the detail form
		_form.setOnValueChangeEventListener(// Enable / disable the [tree] depending on the item validation status
										    valChangeEvent -> {
											   		// get the changed obj
											   		VO changedViewObj = valChangeEvent.getViewObject();

											   		// get the corresponding item at the grid
											   		VO viewObjAtGrid = _treeGrid.getTreeData()
											   									.recurseFindItemFirstMatch(viewObj -> viewObj.getId().is(changedViewObj.getId()));
											   		if (viewObjAtGrid == null) throw new IllegalStateException("[tree grid]: could NOT find an item with id=" + changedViewObj.getId());

											   		// transfer the data from the edit form
											   		viewObjAtGrid.copyDataFrom(changedViewObj);

											   		// enable the tree
											   		_treeGrid.setEnabled(true);
										    });
		// what happens when an item is selected (or created) at the tree
		_treeGrid.setOnTreeRootNodeSelectedEventListener(() -> {
															// hide the [detail component]
															_form.setVisible(false);
														});
		_treeGrid.setOnItemEditEventListener(itemEditReqEvt -> {
													// the currently edited viewObj (this forces the binded object to be saved from the UI controls)
													VO currEditedViewObj = viewObjInLangFactory.create();
													_form.writeAsDraftEditedViewObjectTo(currEditedViewObj);

													// tell the [detail component] to edit the item
													VO viewObj = itemEditReqEvt.getItemToBeEdited();
													_form.editViewObject(viewObj);
													_form.setVisible(true);
											 });
		// what happens when an item is deleted at the tree
		_treeGrid.setOnItemDeletedEventListener(itemDeletedEvt -> {
													// hide the [detail component]
													_form.setVisible(false);
												});
		// what happens when the tree is update (ie by drag & drop elements or by removing an item)
		if (treeChangeEventListener != null) this.setOnTreeChangedEventListener(treeChangeEventListener);
	}
	@Override
	protected Component initContent() {
		////////// text & description split-panel layout
		HorizontalSplitPanel hsplitPanel = new HorizontalSplitPanel();
		hsplitPanel.setFirstComponent(_treeGrid);
		hsplitPanel.setSecondComponent(_form);
		hsplitPanel.setSplitPosition(30);
		hsplitPanel.getFirstComponent().setWidth(98, Unit.PERCENTAGE);
		hsplitPanel.setMinSplitPosition(17, Unit.PERCENTAGE);

		return hsplitPanel;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GET & SET
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public VaadinTreeData<VO> getValue() {
		return _treeGrid.getValue();
	}
	@Override
	protected void doSetValue(final VaadinTreeData<VO> value) {
		_treeGrid.setValue(value);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Binding
/////////////////////////////////////////////////////////////////////////////////////////
	////////// [viewObject] > [UI control] --------------
	@Override
	public void editViewObject(final VaadinTreeData<VO> viewObj) {
		_treeGrid.setValue(viewObj);
		_treeGrid.init();
	}
	////////// [UI control] > [viewObject] --------------
	@Override
	public void writeAsDraftEditedViewObjectTo(final VaadinTreeData<VO> viewObj) {
		// in order to keep the received [view object] the "internal" state of the [view object]
		// is REPLACED with the [tree data]
		viewObj.clear();
		VaadinTreeData<VO> currData = this.getValue();
		viewObj.importTree(currData);
	}
	@Override
	public boolean writeIfValidEditedViewObjectTo(final VaadinTreeData<VO> viewObj) {
		this.writeAsDraftEditedViewObjectTo(viewObj);
		return true;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EVENTS
/////////////////////////////////////////////////////////////////////////////////////////
	public void setOnTreeChangedEventListener(final VaadinTreeChangedEventListener listener) {
		_treeGrid.setOnTreeChangedEventListener(listener);
	}
	public void addCustomComponent(Component comp) {
		_treeGrid.addCustomActionComponent(comp);
	}
	public void setOnSelectItemEventListener(BiConsumer<VO, List<VO>> behaviour) {
		_treeGrid.setOnSelectItemEventListener(behaviour);
	}
	public void setOnTreeRootNodeSelectedEventListener(final RootNodeSelectedListener listener) {
		_treeGrid.setOnTreeRootNodeSelectedEventListener(listener);
	}
	public void setOnChildAddedBehavior(BiConsumer<VO,VO> behaviour) {
		_treeGrid.setOnChildAddedListener(behaviour);
	}
	public void setOnChildRemovedListener(BiConsumer<VO,VO> behaviour) {
		_treeGrid.setOnChildRemovedBehaviorListener(behaviour);
	}
	public void setOnDropItemListener(VaadinHiearchicalDataTreeOnDropItemListener<VO,VO,VO> listenr) {
		_treeGrid.setOnDropItemListener(listenr);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CUSTOM COLUMNS
/////////////////////////////////////////////////////////////////////////////////////////
	public Column<VO, Component> addComponentColumnValueProvider(ValueProvider<VO, Component> valueProvider) {
		return _treeGrid.addComponenColumnValueProvider(valueProvider);
	}
	public Column<VO, String> getMainColumn() {
		return _treeGrid.getMainColumn();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//CUSTOMIZE (ENABLE/DISABLE) ADD BUTTON RULE
/////////////////////////////////////////////////////////////////////////////////////////
public void setCustomRuleToHavingChildren(Predicate<VO> rule) {
		_treeGrid.setCustomRuleToHavingChildren(rule);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	I18N
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void updateI18NMessages(final UII18NService i18n) {
		_treeGrid.updateI18NMessages(i18n);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	REFRESH TREE
/////////////////////////////////////////////////////////////////////////////////////////
	public void refresh() {
		_treeGrid.refreshAll();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EXPAND / COLLAPSE ALL
/////////////////////////////////////////////////////////////////////////////////////////
	public void enableExpandAndCollapseButton(boolean enable) {
		_treeGrid.enableExpandCollapseButton(enable);
	}
	public void collapseAll() {
		_treeGrid.collapseAll();
	}
	public void expandAll() {
		_treeGrid.collapseAll();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CHILDREN
/////////////////////////////////////////////////////////////////////////////////////////
	public List<VO> getChildren(VO item) {
		return _treeGrid.getChildren(item);
	}
	public boolean hasChildren(VO item) {
		return this.getChildren(item) != null
				&& !this.getChildren(item).isEmpty();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LEVEL
/////////////////////////////////////////////////////////////////////////////////////////
	public int getItemDepth(VO item) {
		return _treeGrid.getTreeData().getItemDepth(item);
	}
}
