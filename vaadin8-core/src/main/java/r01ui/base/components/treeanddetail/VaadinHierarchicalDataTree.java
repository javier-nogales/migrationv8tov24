package r01ui.base.components.treeanddetail;

import java.util.Collection;
import java.util.Comparator;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import com.vaadin.data.TreeData;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.grid.DropLocation;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Composite;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.dnd.DropTargetExtension;
import com.vaadin.ui.themes.ValoTheme;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.facets.LangInDependentNamed.HasLangInDependentNamedFacet;
import r01f.locale.I18NKey;
import r01f.locale.Language;
import r01f.patterns.Factory;
import r01f.ui.i18n.UII18NService;
import r01f.ui.vaadin.view.VaadinViewI18NMessagesCanBeUpdated;
import r01f.ui.viewobject.UIViewObjectInLanguage;
import r01f.util.types.collections.CollectionUtils;
import r01ui.base.components.tree.VaadinTree;
import r01ui.base.components.tree.VaadinTree.VaadinTreeChangedEventListener;
import r01ui.base.components.tree.VaadinTreeData;
import r01ui.base.components.window.VaadinProceedGateDialogWindow;

/**
 * A tree like:
 * <pre>
 * 		+--------------------------------------------+
 * 		| [e/c] [add] [remove] [customizable layout] |
 * 		+--------------------------------------------+
 * 		| node                                       |
 * 		|   + node                                   |
 * 		|		+ node                               |
 * 		|		+ node                               |
 * 		|	+ node                                   |
 * 		|		+ node                               |
 * 		+--------------------------------------------+
 * </pre>
 * @param <VO>
 */
public class VaadinHierarchicalDataTree<VO extends UIViewObjectInLanguage
												 & HasLangInDependentNamedFacet // the name is needed to "paint" something at the tree
												 & VaadinHierarchicalDataViewObj<VO>>
	 extends Composite
  implements VaadinViewI18NMessagesCanBeUpdated {

	private static final long serialVersionUID = 4193358827131783715L;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private final Factory<VO> _viewObjFactory;
	private final VaadinHierarchicalDataEditConfig _settings;
/////////////////////////////////////////////////////////////////////////////////////////
//	UI
/////////////////////////////////////////////////////////////////////////////////////////
	private final Button _btnRootNode = new Button();
	
	private final HorizontalLayout _hlyHeader = new HorizontalLayout();
	private final HorizontalLayout _hlyHeaderCustomComponents = new HorizontalLayout();
	private final VaadinHierarchicalDataTreeImpl _treeGrid;

	private final HorizontalLayout _hlyExpandAndCollapse = new HorizontalLayout();
	private final Button _btnCollapse = new Button();
	private final Button _btnExpand = new Button();
	private final Button _btnAdd = new Button();
	private final Button _btnRemove = new Button();
	private final HorizontalLayout _hlyCustomizableLayout = new HorizontalLayout();

	private boolean _isExpandAndCollapseEnabled;
/////////////////////////////////////////////////////////////////////////////////////////
//	LISTENERS
/////////////////////////////////////////////////////////////////////////////////////////
	private VaadinHiearchicalDataTreeOnItemEditEventListener<VO> _itemEditEventListener;
	private VaadinHiearchicalDataTreeOnItemDeletedEventListener<VO> _itemDeletedEventListener;
	private VaadinHiearchicalDataTreeOnDropItemListener<VO, VO, VO> _dropItemListener;
	private RootNodeSelectedListener _rootNodeSelectedListener;
	private BiConsumer<VO, List<VO>> _selectItemListener;
	private BiConsumer<VO, VO> _childAddedListener;
	private BiConsumer<VO, VO> _childRemovedListener;
	private boolean _readOnly;
//	private Predicate<VO> _customRuleToHavingChildren = item -> true; // true by default
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Deprecated @SuppressWarnings("unused")
	public VaadinHierarchicalDataTree(final UII18NService i18n,
									  final VaadinHierarchicalDataEditConfig settings,
									  final Language lang,final Collection<Language> availableLangs,
									  final Factory<VO> viewObjFactory) {
		this(i18n,
			 settings,
			 viewObjFactory);
	}
	public VaadinHierarchicalDataTree(final UII18NService i18n,
									  final VaadinHierarchicalDataEditConfig settings,
									  final Factory<VO> viewObjFactory) {
		_viewObjFactory = viewObjFactory;
		_settings = settings;

		////////// UI
		_treeGrid = new VaadinHierarchicalDataTreeImpl(i18n,
													   settings);
		_treeGrid.removeHeaderRow(0);
		_treeGrid.setSizeFull();

		// buttons
		HorizontalLayout hlyButtons = _configureButtonsLayout(settings);

		_btnRootNode.setIcon(VaadinIcons.FILE_TREE);
		_btnRootNode.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		_btnRootNode.setVisible(settings.isCollection());
		_btnRootNode.setDescription(i18n.getMessage("hierachical.data.tree.selectroot"));
		
		if (!settings.isCollection()) _treeGrid.setHeightByRows(1);

		// header
		_hlyHeaderCustomComponents.setSizeFull();	
		_hlyHeader.addComponents(_btnRootNode,_hlyHeaderCustomComponents);
		_hlyHeader.setStyleName(ValoTheme.LAYOUT_CARD);
		_hlyHeader.setSizeFull();
		_hlyHeader.setComponentAlignment(_btnRootNode,Alignment.MIDDLE_LEFT);
		_hlyHeader.setComponentAlignment(_hlyHeaderCustomComponents,Alignment.MIDDLE_RIGHT);
			
		
		VerticalLayout vly = new VerticalLayout(hlyButtons,
												_hlyHeader,
												_treeGrid);
		vly.setWidthFull();
		vly.setSpacing(false);
		vly.setMargin(false);
		this.setCompositionRoot(vly);

		// configure events
		_setBehavior(i18n,
					 settings);

		// init i18n
		this.updateI18NMessages(i18n);
		
		// set the focus at the root 
		_btnRootNode.focus();
	}
	private HorizontalLayout _configureButtonsLayout(final VaadinHierarchicalDataEditConfig vaTypeSettings) {
		// add button
		HorizontalLayout hlButtons = new HorizontalLayout();
		// expand and collapse
		_configureExpandAndCollapseBtns();
		hlButtons.addComponent(_hlyExpandAndCollapse);

		if (vaTypeSettings.isCollection() ||
		   (vaTypeSettings.isNOTCollection() && CollectionUtils.isNullOrEmpty(_treeGrid.getRootItems()))) {
			// allow add a single item
			_btnAdd.setIcon(VaadinIcons.PLUS);
			_btnAdd.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
			hlButtons.addComponent(_btnAdd);
		}

		// remove button
		_btnRemove.setIcon(VaadinIcons.TRASH);
		_btnRemove.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);

		hlButtons.addComponents(_btnRemove,_hlyCustomizableLayout);

		return hlButtons;
	}
	private void _configureExpandAndCollapseBtns() {
		_btnExpand.setIcon(VaadinIcons.ANGLE_DOUBLE_DOWN);
		_btnCollapse.setIcon(VaadinIcons.ANGLE_DOUBLE_UP);
		_btnExpand.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		_btnCollapse.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		_btnExpand.setVisible(true);
		_btnCollapse.setVisible(false);
		_btnExpand.addClickListener(event -> {
										this.expandAll();
										_btnExpand.setVisible(false);
										_btnCollapse.setVisible(true)
							;		});
		_btnCollapse.addClickListener(event -> {
											this.collapseAll();
											_btnCollapse.setVisible(false);
											_btnExpand.setVisible(true);
									  });
		_hlyExpandAndCollapse.addComponents(_btnCollapse,_btnExpand);
		_hlyExpandAndCollapse.setVisible(false);
	}
	public void enableExpandCollapseButton(final boolean enable) {
		_isExpandAndCollapseEnabled = enable;
		_refreshVisibilityOfExpandCollapseBtn();

	}
	private void _refreshVisibilityOfExpandCollapseBtn() {
		boolean hasSubTrees = _treeGrid.getRootItems()
									   .stream()
									   .filter(item -> CollectionUtils.hasData(_treeGrid.getChildren(item)))
									   .count() > 0;

		boolean isVisible = _isExpandAndCollapseEnabled && hasSubTrees;
		_hlyExpandAndCollapse.setVisible(isVisible);

	}
	@SuppressWarnings("unchecked")
	private void _setBehavior(final UII18NService i18n,
							  final VaadinHierarchicalDataEditConfig config) {
		_btnRootNode.addFocusListener(event -> _btnRootNode.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED));
		ClickListener btnRootNodelickListener = event -> {
													_selectRootNode();
													_rootNodeSelectedListener.onRootNodeSelected();
												};
		_btnRootNode.addClickListener(btnRootNodelickListener);
		_hlyHeader.addLayoutClickListener(event -> {
												_btnRootNode.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
												btnRootNodelickListener.buttonClick(new ClickEvent(_btnRootNode));
									  	  });
		_treeGrid.addTreeChangeListener(event -> _refreshVisibilityOfExpandCollapseBtn());
		////////// when an item is selected
		_treeGrid.addSelectionListener(selEvent -> {
											// if selecting an item at the max depth level, the [add button]
											// should be disabled
											VO treeSelectedItemViewObj = _treeGrid.getUniqueSelectedItem();
											int treeSelectedItemDepth = _treeGrid.getTreeData()
																				 .getItemDepth(treeSelectedItemViewObj);
											boolean addEnabled = _settings.isCollection() && treeSelectedItemDepth + 1 < _settings.getMaxDepth();
											_btnAdd.setEnabled(addEnabled && !_readOnly && _treeGrid._customRuleToHavingChildren.test(treeSelectedItemViewObj));

											if (treeSelectedItemViewObj == null) return;	// no selected item (should NOT happen)

											// tell the detail view to enter editing the new view object
											VaadinHiearchicalDataTreeOnItemEditEvent<VO> event = new VaadinHiearchicalDataTreeOnItemEditEvent<>(this,
																																				treeSelectedItemViewObj);
											// trigger selectItemEvent
											if (_selectItemListener != null) {
												List<VO> childList = _treeGrid.getChildren(treeSelectedItemViewObj);
												_selectItemListener.accept(treeSelectedItemViewObj, childList);
											}

											// trigger itemEditEvent
											if (_itemEditEventListener != null) _itemEditEventListener.onItemEditRequested(event);

											_btnRootNode.setStyleName(ValoTheme.BUTTON_BORDERLESS);
									   });
		////////// add
		if (config.isCollection()
			||
		   (config.isNOTCollection() && CollectionUtils.isNullOrEmpty(_treeGrid.getRootItems()))) {
			// allow add a single item
			_btnAdd.addClickListener(click -> {
										// create a new view obj
										VO newViewObj = _viewObjFactory.create();

										// put it into the tree
										VO treeSelectedItem = _treeGrid.getUniqueSelectedItem();
										boolean treeSelectedItemCanContainChildren = _treeGrid.canContainChildItems(treeSelectedItem);
										VO newViewObjParent = treeSelectedItemCanContainChildren ? treeSelectedItem
											  												     : null;

										_treeGrid.setVisible(true);
										_treeGrid.addItem(newViewObjParent,
											  			  newViewObj);
										_treeGrid.expand(newViewObjParent);
										_treeGrid.refreshAll();

										// tell the detail view to enter editing the new view object
										VaadinHiearchicalDataTreeOnItemEditEvent<VO> event = new VaadinHiearchicalDataTreeOnItemEditEvent<>(this,		// source component
											  																			   				    newViewObj);
										if (_itemEditEventListener != null) _itemEditEventListener.onItemEditRequested(event);

										_treeGrid.movePositionTo(newViewObj);

										// while adding a new item, do not allow clicking in another item
										// neither click to the add button
										this.setEnabled(false);
										_btnRemove.setEnabled(true && !_readOnly);

										// trigger add child event
										if (_childAddedListener != null) _childAddedListener.accept(newViewObj,newViewObjParent);

										_btnRootNode.setStyleName(ValoTheme.BUTTON_BORDERLESS);

										_refreshVisibilityOfExpandCollapseBtn();
									});
		}
		////////// Trash button
		// trash click
		_btnRemove.addClickListener(clickEvent -> {
										VO treeSelectedItem = _treeGrid.getUniqueSelectedItem();
										VO parentItem = _treeGrid.getParent(treeSelectedItem);
										if (treeSelectedItem != null) _deleteTreeItem(i18n,
																					  treeSelectedItem,
																					  parentItem);
										_refreshVisibilityOfExpandCollapseBtn();
									});
		// add the drag & drop behavior to the [trash] button
		final DropTargetExtension<Button> removeDrop = new DropTargetExtension<>(_btnRemove);
		removeDrop.setDropEffect(DropEffect.MOVE);
		removeDrop.addDropListener(dropEvent -> {
										VO draggedItem = _treeGrid.getDraggedItem();
										VO parentItem = _treeGrid.getParent(draggedItem);
										_deleteTreeItem(i18n,
														draggedItem,
														parentItem);
								  });

	}
	private void _selectRootNode() {
		_treeGrid.deselectAll();
	    _treeGrid.refreshAll();

	   // If NOT a collection the [add button] must be
	   // enabled or disabled depending on whether there's an item or not
	   boolean addEnabled = _settings.isCollection()
			   		     || (_settings.isNOTCollection()
			   			     && CollectionUtils.isNullOrEmpty(_treeGrid.getRootItems()));	// no root items = no items
	   this.setEnabled(addEnabled);
	   _btnAdd.setEnabled(addEnabled && !_readOnly);
	   _btnAdd.setVisible(true);
	}
	private class TreeProceedGateDialogWindow
		  extends VaadinProceedGateDialogWindow {

		private static final long serialVersionUID = -7534910999703683273L;

		TreeProceedGateDialogWindow(final UII18NService i18n,
									final VaadinProceedGateProceed proceedGateOpenedListener) {
			super(i18n,
		  		  I18NKey.forId("delete.confirm.title"),
		  		  I18NKey.forId("delete.confirm.proceed.gate.message"),
		  		  proceedGateOpenedListener
			);
		}
	}
	private void _deleteTreeItem(final UII18NService i18n,
								 final VO item,final VO parentItem) {
		TreeProceedGateDialogWindow proceedGateDialog = null;
		proceedGateDialog = new TreeProceedGateDialogWindow(i18n,
													  	    // when the user solves the puzzle just remove the items
													  	    () -> {
																// the the container view that an item (or items) were deleted
																if (_itemDeletedEventListener != null) {
																	TreeData<VO> deletedTree = _treeGrid.getSubTreeOf(item);
																	VaadinHiearchicalDataTreeOnItemDeletedEvent<VO> event = new VaadinHiearchicalDataTreeOnItemDeletedEvent<>(this,
																	   			  																								  deletedTree);
																	_itemDeletedEventListener.onItemsDeleted(event);
																}
																// remove the item from the tree
																_treeGrid.removeItem(item);
																_treeGrid.deselectAll();
																_treeGrid.refreshAll();
																// trigger child remove event
																if (_childRemovedListener != null) _childRemovedListener.accept(item,parentItem);
	
																_treeGrid.setVisible(CollectionUtils.isNullOrEmpty(_treeGrid.getRootItems()));
	
																// If NOT a collection the [add button] must be
																// enabled or disabled depending on whether there's an item or not
																boolean addEnabled = _settings.isCollection()
																		   		   || (_settings.isNOTCollection()
																		   			   && 
																		   			   CollectionUtils.isNullOrEmpty(_treeGrid.getRootItems()));	// no root items = no items
																this.setEnabled(addEnabled);
														  		_btnAdd.setEnabled(addEnabled);
														  		_btnAdd.setVisible(true);
														  		_treeGrid.setVisible(!CollectionUtils.isNullOrEmpty(_treeGrid.getRootItems()));
	
														  		_btnRootNode.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
													  	    });
		UI.getCurrent()
		  .addWindow(proceedGateDialog);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GET VALUE
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinTreeData<VO> getValue() {
		return this.getTreeData();
	}
	public void setValue(final VaadinTreeData<VO> treeData) {
		this.setTreeData(treeData);
	}
	public VaadinTreeData<VO> getTreeData() {
		return _treeGrid.getTreeData();
	}
	public void setTreeData(final VaadinTreeData<VO> treeData) {
		_treeGrid.replaceDataWith(treeData);
		_treeGrid.deselectAll();
		_treeGrid.refreshAll();
		_treeGrid.setVisible(!CollectionUtils.isNullOrEmpty(_treeGrid.getRootItems()));
		boolean addEnabled = _settings.isCollection()
				   		   || (_settings.isNOTCollection()
				   			   && 
				   			   CollectionUtils.isNullOrEmpty(_treeGrid.getRootItems()));	// no root items = no items
		_btnAdd.setEnabled(addEnabled && !_readOnly);
		_refreshVisibilityOfExpandCollapseBtn();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CHILDREN
/////////////////////////////////////////////////////////////////////////////////////////
	public List<VO> getChildren(final VO item) {
		return _treeGrid.getChildren(item);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	REFRESH ALL
/////////////////////////////////////////////////////////////////////////////////////////
	public void refreshAll() {
		_treeGrid.refreshAll();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BUTTONS STYLES
/////////////////////////////////////////////////////////////////////////////////////////
	public void addButtonSetPrimaryStyleName(final String styleName) {
		_btnAdd.setPrimaryStyleName(styleName);
	}
	public void removeButtonSetPrimaryStyleName(final String styleName) {
		_btnRemove.setPrimaryStyleName(styleName);
	}
	public void rootButtonSetPrimaryStyleName(final String styleName) {
		_btnRootNode.setPrimaryStyleName(styleName);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	INIT
/////////////////////////////////////////////////////////////////////////////////////////
	public void init() {
		if (!_settings.isCollection()) {
			this.selectFirstChildIfExists();
		} else {
			_btnRootNode.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		}
	}
	public void selectFirstChildIfExists() {
		if (_treeGrid.isNotEmpty()) {
			VO selectedItem = _treeGrid.selectFirstChild();
			// tell the detail view to enter editing the new view object
			VaadinHiearchicalDataTreeOnItemEditEvent<VO> event = new VaadinHiearchicalDataTreeOnItemEditEvent<>(this,
																												selectedItem);
			if (_itemEditEventListener != null) _itemEditEventListener.onItemEditRequested(event);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CUSTOMIZE (ENABLE/DISABLE) ADD BUTTON RULE
/////////////////////////////////////////////////////////////////////////////////////////
	public void setCustomRuleToHavingChildren(final Predicate<VO> rule) {
		_treeGrid.setCustomRuleToHavingChildren(rule);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CUSTOMIZABLE ACTIONS LAYOUT
/////////////////////////////////////////////////////////////////////////////////////////
	public void addCustomActionComponent(final Component comp) {
		_hlyCustomizableLayout.addComponent(comp);
	}
	public void setOnSelectItemEventListener(final BiConsumer<VO, List<VO>> behaviour) {
		_selectItemListener = behaviour;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CUSTOMIZE ROOT CAPTION AND EXTRA COMPONENTS
/////////////////////////////////////////////////////////////////////////////////////////
	public void setGridCaption(final String str) {
		_btnRootNode.setCaption(str);
	}
	public void addComponentAsRootInfo(final Component component) {
		_hlyHeaderCustomComponents.addComponent(component);
		_hlyHeaderCustomComponents.setComponentAlignment(component,Alignment.MIDDLE_RIGHT);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CUSTOMIZE COLUMNS
/////////////////////////////////////////////////////////////////////////////////////////
	public Column<VO, Component> addComponenColumnValueProvider(final ValueProvider<VO, Component> valueProvider) {
		Column<VO, Component> col = _treeGrid.addComponentColumn(valueProvider);
		return col;
	}
	public Column<VO, String> getMainColumn() {
		return _treeGrid._colMain;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EXPAND / COLLAPSE ALL
/////////////////////////////////////////////////////////////////////////////////////////
	public void collapseAll() {
		_treeGrid.collapseRecursively(_treeGrid.getRootItems(), 0);	}
	public void expandAll() {
		_treeGrid.expandRecursively(_treeGrid.getRootItems(), 0);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ENABLED / DISABLED / READONLY
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void setEnabled(final boolean enabled) {
		_treeGrid.setEnabled(enabled);
		VO treeSelectedItemViewObj = _treeGrid.getUniqueSelectedItem();
		int treeSelectedItemDepth = _treeGrid.getTreeData()
											 .getItemDepth(treeSelectedItemViewObj);
		_btnAdd.setEnabled(_settings.isCollection() && treeSelectedItemDepth +1 < _settings.getMaxDepth());
		boolean addEnabled = (_settings.isCollection() && treeSelectedItemDepth +1 < _settings.getMaxDepth())
				   		   || (_settings.isNOTCollection() && CollectionUtils.isNullOrEmpty(_treeGrid.getRootItems()));
		_btnAdd.setEnabled(addEnabled && !_readOnly && _treeGrid._customRuleToHavingChildren.test(treeSelectedItemViewObj));
		_btnRemove.setEnabled(enabled && !_readOnly);
	}

	public void setNotEditable(final boolean readOnly) {
		_readOnly = readOnly;
		_btnAdd.setEnabled(!readOnly);
		_btnRemove.setEnabled(!readOnly);
		_btnRootNode.setVisible(_settings.isCollection() && !readOnly);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EVENT  LISTENERS
/////////////////////////////////////////////////////////////////////////////////////////
	////////// Tree updated
	public void setOnTreeChangedEventListener(final VaadinTreeChangedEventListener listener) {
		// just pass the tree the event listener
		_treeGrid.setTreeChangedEventListener(listener);
	}
	////////// Item edit
	public void setOnItemEditEventListener(final VaadinHiearchicalDataTreeOnItemEditEventListener<VO> listener) {
		_itemEditEventListener = listener;
	}
	@Accessors(prefix="_")
	public static class VaadinHiearchicalDataTreeOnItemEditEvent<VIL extends UIViewObjectInLanguage>
		 		extends Component.Event {
		private static final long serialVersionUID = -3843906493580325214L;

		@Getter private final VIL _itemToBeEdited;

		public VaadinHiearchicalDataTreeOnItemEditEvent(final Component srcComponent,
														final VIL itemToBeEdited) {
			super(srcComponent);
			_itemToBeEdited = itemToBeEdited;
		}
	}
	@FunctionalInterface
	public interface VaadinHiearchicalDataTreeOnItemEditEventListener<VIL extends UIViewObjectInLanguage>
	         extends EventListener {

        public abstract void onItemEditRequested(final VaadinHiearchicalDataTreeOnItemEditEvent<VIL> event);
	}
	////////// Item delete
	public void setOnItemDeletedEventListener(final VaadinHiearchicalDataTreeOnItemDeletedEventListener<VO> listener) {
		_itemDeletedEventListener = listener;
	}
	@Accessors(prefix="_")
	public static class VaadinHiearchicalDataTreeOnItemDeletedEvent<VIL extends UIViewObjectInLanguage>
		 		extends EventObject {

		private static final long serialVersionUID = 7381227122007331462L;

		@Getter private final TreeData<VIL> _treeToBeDeleted;

		public VaadinHiearchicalDataTreeOnItemDeletedEvent(final Component srcComponent,
												   		   final TreeData<VIL> treeToBeDeleted) {
			super(srcComponent);
			_treeToBeDeleted = treeToBeDeleted;
		}
	}
	@FunctionalInterface
	public interface VaadinHiearchicalDataTreeOnItemDeletedEventListener<VIL extends UIViewObjectInLanguage>
	         extends EventListener {

        public abstract void onItemsDeleted(final VaadinHiearchicalDataTreeOnItemDeletedEvent<VIL> event);
	}
	////////// Tree root node selected
	public void setOnTreeRootNodeSelectedEventListener(final RootNodeSelectedListener listener) {
		_rootNodeSelectedListener = listener;
	}
	@FunctionalInterface
	public interface RootNodeSelectedListener {
		public void onRootNodeSelected();
	}
	////////// On Child added
	public void setOnChildAddedListener(final BiConsumer<VO,VO> listener) {
		_childAddedListener = listener;
	}
	////////// On Child Removed
	public void setOnChildRemovedBehaviorListener(final BiConsumer<VO,VO> behaviour) {
		_childRemovedListener = behaviour;
	}
	////////// On Drop Item
	@FunctionalInterface
	public interface VaadinHiearchicalDataTreeOnDropItemListener<VOitem,VOparent,VOtarget> {
		void onDrop(VOitem draggedItem, VOparent parentItem, VOtarget targetItem);
	}
	public void setOnDropItemListener(final VaadinHiearchicalDataTreeOnDropItemListener<VO, VO, VO> listener) {
		_dropItemListener = listener;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	I18N
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void updateI18NMessages(final UII18NService i18n) {
		_btnAdd.setCaption(i18n.getMessage("add"));
		_btnRemove.setCaption(i18n.getMessage("remove"));
		_btnRootNode.setDescription(i18n.getMessage("hierachical.data.tree.selectroot"));
		_btnCollapse.setDescription(i18n.getMessage("collapseall"));
		_btnExpand.setDescription(i18n.getMessage("expandall"));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unused")
	private class VaadinHierarchicalDataTreeImpl
		  extends VaadinTree<VO> {

		private static final long serialVersionUID = -2845849590863734274L;

		private final UII18NService _i18n;
		private final VaadinHierarchicalDataEditConfig _vaTypeSettings;

		private final TreeDataProvider<VO> _treeDataProvider;
		private final VaadinTreeData<VO> _treeData;

		private Grid.Column<VO,String> _colMain;

		private VaadinHiearchicalDataTreeOnDropItemListener<VO,VO,VO> _onDropItemLsitener;
		protected Predicate<VO> _customRuleToHavingChildren = item -> true; // true by default

		VaadinHierarchicalDataTreeImpl(final UII18NService i18n,
									   final VaadinHierarchicalDataEditConfig vaTypeSettings) {
			super();
			_i18n = i18n;
			_vaTypeSettings = vaTypeSettings;

			_treeData = new VaadinTreeData<>();
			_treeDataProvider = new TreeDataProvider<>(_treeData);
			this.setDataProvider(_treeDataProvider);

			_configureTree(i18n);
		}
		public void setCustomRuleToHavingChildren(final Predicate<VO> rule) {
			_customRuleToHavingChildren = rule;
		}
		public List<VO> getChildren(final VO item) {
			return _treeData.getChildren(item);
		}
		private void _configureTree(final UII18NService i18n) {
			Grid.Column<VO,String> col = this.addColumn(vo -> vo.getName()) 
											 .setId("caption-column") 
											 .setSortable(false);
			_colMain = col;
			this.setHierarchyColumn(col);
			this.setSelectionMode(SelectionMode.SINGLE);
		}
		public void refreshAll() {
			_treeDataProvider.refreshAll();
		}
		@Override
		public VaadinTreeData<VO> getTreeData() {
			return _treeData;
		}
		public void replaceDataWith(final VaadinTreeData<VO> data) {
			_treeData.replaceWith(data);
		}
		public void addItem(final VO parent,final VO item) {
			_treeData.addItem(parent,item);
		}
		public void removeItem(final VO item) {
			// remove from parent(viewObj)
		    VO parent = _treeData.getParent(item);
		   	if (parent != null) {
		   		parent.removeChild(item);
		   	}
		   	// remove item
			_treeData.removeItem(item);
			_treeDataUpdated();
		}
		public Collection<VO> getRootItems() {
			return _treeData.getRootItems();
		}
		public TreeData<VO> getSubTreeOf(final VO item) {
			return _treeData.getSubTreeOf(item);
		}
		public VO getUniqueSelectedItem() {
			VO outSelItem = null;
			if (CollectionUtils.hasData(this.getSelectedItems())
			 && this.getSelectedItems().size() == 1) {
				outSelItem = this.getSelectedItems().stream().findFirst().orElse(null);
			}
			return outSelItem;
		}
		public VO getParent(final VO childItem) {
			return _treeData.getParent(childItem);
		}
		public boolean canSelectedItemContainChildItems() {
			VO treeSelItem = this.getUniqueSelectedItem();
			return this.canContainChildItems(treeSelItem);
		}
		public boolean canContainChildItems(final VO item) {
			int itemDepth = item != null ? _treeData.getItemDepth(item)
		  						  		 : -1;
			return itemDepth < 0
				|| _vaTypeSettings.getMaxDepth() == itemDepth ? false
															  : true;
		}
		public VO selectFirstChild() {
			Collection<VO> rootItems = this.getRootItems();
			VO firstItem = rootItems.iterator().next();
			this.select(firstItem);
			return firstItem;
		}
		public boolean isNotEmpty() {
			return !CollectionUtils.isNullOrEmpty(this.getRootItems());
		}
		@Override
		protected String _itemCaption(final VO item) {
			return item.getName();
		}
		@Override
		protected StringBuilder _treeDebugInfo(final TreeData<VO> treeData) {
			return null;
		}
		@Override
		protected boolean _customIsValid(final VO dropTargetItem,
										 final VO draggedItem,
										 final DropLocation dropLocation) {
			int dropTargetItemDepth = _treeData().getItemDepth(dropTargetItem);
			int draggedItemSubtreeDepth = _recurseBranchMaxDepthStartingAt(draggedItem, 0);

			boolean droppingIntoNodeAboveAllowedDepth = dropLocation == DropLocation.ON_TOP
													  && (dropTargetItemDepth + draggedItemSubtreeDepth) < (_vaTypeSettings.getMaxDepth() - 1);
			boolean droppingToLocationAboveAllowedDepth = dropLocation != DropLocation.ON_TOP
														&& (dropTargetItemDepth + draggedItemSubtreeDepth) < (_vaTypeSettings.getMaxDepth());
			boolean isDropTargetItemAChildOfDraggedItem = _isFirstItemAChildOfSecond(dropTargetItem, draggedItem);

			boolean canDrop = (droppingToLocationAboveAllowedDepth || droppingIntoNodeAboveAllowedDepth)
							  && !isDropTargetItemAChildOfDraggedItem
							  && _customRuleToHavingChildren.test(dropTargetItem);

			if (!canDrop) {
				Notification.show(_i18n.getMessage("hierachical.data.tree.not.allowed"),
								  Type.ERROR_MESSAGE);
			}
			return canDrop;
		}
		@Override
		protected void _triggerCustomActionsOnInvolvedItemsAtDragAndDrop(final VO draggedItem,
															final VO parentItem,
															final VO dropTargetItem) {
			if (_dropItemListener != null)
				_dropItemListener.onDrop(draggedItem,
										   parentItem,
										   dropTargetItem);
		}
		/**
		 * The branch max depth
		 * <pre>
		 * 		node				<-- _branchMaxDepthStartingAt(node) = 3
		 * 			+ node			<-- _branchMaxDepthStartingAt(node) = 2
		 * 				+ node		<-- _branchMaxDepthStartingAt(node) = 1
		 * 					+ node	<-- _branchMaxDepthStartingAt(node) = 0
		 *	</pre>
		 * @param viewObj
		 * @return
		 */
		private int _recurseBranchMaxDepthStartingAt(final VO viewObj,
													 final int startingDepth) {
			if (CollectionUtils.isNullOrEmpty(_treeData().getChildren(viewObj))) return startingDepth;
			return _treeData.getChildren(viewObj)
							.stream()
							.map(childViewObj -> _recurseBranchMaxDepthStartingAt(childViewObj,
																		  		  startingDepth + 1))
							.max(Comparator.naturalOrder())
							.get();
		}
		void movePositionTo(final VO viewObj) {
			this.asSingleSelect()
				.setSelectedItem(viewObj);
			_treeDataProvider.refreshAll();
		}
		private boolean _isFirstItemAChildOfSecond(final VO firstNode, final VO secondNode) {
			return _treeData().getAllItemsBelow(secondNode).contains(firstNode);
		}
	}
}
