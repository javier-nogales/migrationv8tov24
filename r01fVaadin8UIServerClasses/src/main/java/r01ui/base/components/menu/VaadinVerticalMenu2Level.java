package r01ui.base.components.menu;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;

import com.google.common.collect.Lists;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Resource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.debug.Debuggable;
import r01f.locale.I18NKey;
import r01f.model.facets.view.IsSelectable;
import r01f.ui.component.UIComponentID;
import r01f.ui.i18n.UII18NService;
import r01f.ui.vaadin.styles.VaadinValoTheme;
import r01f.ui.vaadin.view.VaadinComponentHasCaption;
import r01f.ui.vaadin.view.VaadinComponentHasIcon;
import r01f.ui.vaadin.view.VaadinNavigator;
import r01f.ui.vaadin.view.VaadinViewI18NMessagesCanBeUpdated;
import r01f.ui.vaadin.view.VaadinViewID;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * A vertical menu with sections like:
 * <pre>
 *		+===========================================+
 *		|  +-------------------------------------+  |
 *		|  | [icon] container section         \/ |  |
 * 		|  +-------------------------------------+  |
 *		|  | item 1		         				 |  |
 *		|  | item 2						    	 |  |
 *	    |  | ...							     |  |
 * 		|  +-------------------------------------+  |
 *		|  +-------------------------------------+  |
 *		|  | [icon] clickable section            |  |
 * 		|  +-------------------------------------+  |
 *		|  +-------------------------------------+  |
 *		|  | [icon] container section         \/ |  |
 * 		|  +-------------------------------------+  |
 *		|  | item 1		         				 |  |
 *		|  | item 2						    	 |  |
 *	    |  | ...							     |  |
 * 		|  +-------------------------------------+  |
 *	    +===========================================+
 * </pre>
 * Usage:
 * <pre class='brush:java'>
 *    	VaadinVerticalMenu2Level menu = new VaadinVerticalMenu2Level(i18n);
 *
 *    	// add a clickable section that navigates to view
 *    	menu.addClickableSection(VaadinViewID.forId("myView"));
 *
 *    	// add a clickable section reacting to the selection event
 *    	menu.addClickableSection(I18NKey.named("menu.itemx"),
 *    							 itemSelected -> {
 *    								log.info("> selecting item {} (previously selected is {})",
 *    										 itemSelected.getSelectedItemId(),
 *    										 itemSelected.getPreviouslySelectedItemId());
 *    							 });
 *
 *    	// add a container section with two items
 *    	menu.addContainerSection(I18NKey.named("menu.container"))
 *    		// an item that navigates to a view
 *    		.addItem(VaadinViewID.forId("myView"))
 *    		// an item reacting to the selection event
 *    		.addItem(I18NKey.named("menu.itemy"),
 *    				 itemSelected -> {
 *    					log.info("> selecting item {} (previously selected is {})",
 *    							 itemSelected.getSelectedItemId(),
 *    							 itemSelected.getPreviouslySelectedItemId());
 *    				 });
 * </pre>
 */
@Slf4j
@Accessors(prefix="_")
@RequiredArgsConstructor
public class VaadinVerticalMenu2Level
	 extends CssLayout
  implements VaadinViewI18NMessagesCanBeUpdated,
  			 Debuggable {

	private static final long serialVersionUID = -1329991076161324687L;
/////////////////////////////////////////////////////////////////////////////////////////
//	SERVICES
/////////////////////////////////////////////////////////////////////////////////////////
	private final transient UII18NService _i18n;
/////////////////////////////////////////////////////////////////////////////////////////
//	UI FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	protected final Collection<VaadinVerticalMenuSectionBase> _menuSections = Lists.newArrayList();

/////////////////////////////////////////////////////////////////////////////////////////
//	ADD
/////////////////////////////////////////////////////////////////////////////////////////
////////// Clickable sections
	public VaadinVerticalMenuClickableSection addClickableSection(final VaadinViewID viewId) {
		return this.addClickableSection(viewId,(Map<String,String>)null);	// no navigation params
	}
	public VaadinVerticalMenuClickableSection addClickableSection(final VaadinViewID viewId,
										  		   			   	  final Resource icon) {
		return this.addClickableSection(viewId,null,	// no navigation params
					 				    icon);
	}
	public VaadinVerticalMenuClickableSection addClickableSection(final VaadinViewID viewId,final Map<String,String> navParams) {
		return this.addClickableSection(viewId,navParams,
					 				    null);		// no icon
	}
	public VaadinVerticalMenuClickableSection addClickableSection(final VaadinViewID viewId,final Map<String,String> navParams,
										  		   				  final Resource icon) {
		return this.addClickableSection(UIComponentID.from(viewId),	// componentId = viewId
										I18NKey.forId(viewId),icon,	// i18nKey = viewId
										// when the item is selected NAVIGATE to the given view
										itemSelected -> UI.getCurrent()
														  .getNavigator()
														  .navigateTo(VaadinNavigator.vaadinViewUrlPathFragmentOf(viewId,navParams)));
	}
	public VaadinVerticalMenuClickableSection addClickableSection(final I18NKey i18nKey,
										  		   				  final VaadinVerticalMenuItemSelectedListener selectedEventListener) {
		return this.addClickableSection(null,	// no component id (componentId = i18nKey)
										i18nKey,null,	// no icon
										selectedEventListener);
	}
	public VaadinVerticalMenuClickableSection addClickableSection(final I18NKey i18nKey,final Resource icon,
									      		   				  final VaadinVerticalMenuItemSelectedListener selectedEventListener) {
		return this.addClickableSection(null,	// no component id (componentId = i18nKey)
										i18nKey,icon,
										selectedEventListener);
	}
	public VaadinVerticalMenuClickableSection addClickableSection(final UIComponentID itemId,
										  		   				  final I18NKey i18nKey,
										  		   				  final VaadinVerticalMenuItemSelectedListener selectedEventListener) {
		return this.addClickableSection(itemId,
										i18nKey,null,	// no icon
										selectedEventListener);
	}
	public VaadinVerticalMenuClickableSection addClickableSection(final UIComponentID itemId,
										  		   				  final I18NKey i18nKey,final Resource icon,
										  		   				  final VaadinVerticalMenuItemSelectedListener selectedEventListener) {
		// Create the section
		VaadinVerticalMenuClickableSection section = new VaadinVerticalMenuClickableSection(itemId,
																 							i18nKey,icon,
																 							selectedEventListener);
		// add the item to the collection
		_menuSections.add(section);
		this.addComponent(section);
		return section;
	}
////////// Non clickable sections
	public VaadinVerticalMenuContainerSection addContainerSection(final I18NKey i18nKey) {
		return this.addContainerSection(null,		// no component id (componentId = i18nKey)
										i18nKey,null);	// no icon
	}
	public VaadinVerticalMenuContainerSection addContainerSection(final I18NKey i18nKey,final Resource icon) {
		return this.addContainerSection(null,		// no component id (componentId = i18nKey)
										i18nKey,icon);
	}
	public VaadinVerticalMenuContainerSection addContainerSection(final UIComponentID itemId,
																  final I18NKey i18nKey,final Resource icon) {
		// Create the section
		VaadinVerticalMenuContainerSection section = new VaadinVerticalMenuContainerSection(itemId,
																							i18nKey,icon);
		// add the item to the collection
		_menuSections.add(section);
		this.addComponent(section);
		return section;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	FIND
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinVerticalMenuItem findItemWithId(final UIComponentID itemId) {
		return this.findItemMatching(item -> item.getItemId() != null
										  && item.getItemId().is(itemId));
	}
	public VaadinVerticalMenuItem findSelectedItem() {
		return this.findItemMatching(item -> item.isSelectableItem()
										  && item.asSelectable()
										  		 .isSelected());
	}
	public VaadinVerticalMenuItem findItemMatching(final Predicate<VaadinVerticalMenuItem> pred) {
		if (CollectionUtils.isNullOrEmpty(_menuSections)) return null;

		VaadinVerticalMenuItem outItem = null;
		for (VaadinVerticalMenuSectionBase section : _menuSections) {
			if (pred.test(section)) {
				outItem = section;
				break;
			}
			if (section.isItemsContainer()) {
				VaadinVerticalMenuContainerSection container = section.as(VaadinVerticalMenuContainerSection.class);
				outItem = container.findItemMatching(pred);
				if (outItem != null) break;
			}
		}
		return outItem;
	}
	public VaadinVerticalMenuContainerSection findContainerSectionContainingItemWithId(final UIComponentID itemId) {
		return this.findContainerSectionContainingItemMatching(item -> item.getItemId() != null
																	&& item.getItemId().is(itemId));
	}
	public VaadinVerticalMenuContainerSection findContainerSectionContainingItem(final VaadinVerticalMenuItem item) {
		return this.findContainerSectionContainingItemMatching(theItem -> theItem == item);
	}
	public VaadinVerticalMenuContainerSection findContainerSectionContainingItemMatching(final Predicate<VaadinVerticalMenuItem> pred) {
		if (CollectionUtils.isNullOrEmpty(_menuSections)) return null;
		return _menuSections.stream()
							.filter(section -> section.isItemsContainer()
											&& section.as(VaadinVerticalMenuContainerSection.class)
													   .findItemMatching(pred) != null)
							.map(VaadinVerticalMenuContainerSection.class::cast)
							.findFirst()
							.orElse(null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SELECTION
/////////////////////////////////////////////////////////////////////////////////////////
	public void setSelected(final UIComponentID itemId,
							final boolean sel) {
		// set the selection status
		VaadinVerticalMenuItem item = this.findItemWithId(itemId);
		if (item != null && item.isSelectableItem()) item.asSelectable()
														 .setSelectionTo(sel);
		// if selected, ensure that the container section is expanded
		if (item != null && sel) {
			VaadinVerticalMenuContainerSection container = this.findContainerSectionContainingItem(item);
			if (container != null) container.expand();
		}
	}
	public void setSelected(final UIComponentID itemId) {
		this.setSelected(itemId,true);
	}
	public void setUnSelected(final UIComponentID itemId) {
		this.setSelected(itemId,false);
	}
	public boolean isSelected(final UIComponentID itemId) {
		VaadinVerticalMenuItem item = this.findItemWithId(itemId);
		return item != null
			&& item.isSelectableItem() ? item.asSelectable()
											 .isSelected()
									   : false;
	}
	public UIComponentID getSelectedItemId() {
		VaadinVerticalMenuItem selectedItem = this.findItemMatching(item -> item.isSelectableItem()
																		 && item.asSelectable()
																		 		.isSelected());
		return selectedItem != null ? selectedItem.getItemId() : null;
	}
	public void switchSelected(final UIComponentID from,final UIComponentID to) {
		VaadinVerticalMenuItem fromItem = this.findItemWithId(from);
		VaadinVerticalMenuItem toItem = this.findItemWithId(to);
		if (toItem == null) {
			log.error("Could NOT fina a menu item with componentId={}\n"+
					  "menu items:\n" +
					  "{}",
					  to,
					  this.debugInfo());
			throw new IllegalStateException("Could NOT find a menu item with componentId=" + to);
		}
		_switchSelected(fromItem,toItem);
	}
	private static void _switchSelected(final VaadinVerticalMenuItem from,final VaadinVerticalMenuItem to) {
		if (from != null && from.isSelectableItem()) from.asSelectable()
										 				 .setDeSelected();
		if (to.isSelectableItem()) to.asSelectable()
									 .setSelected();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EXPAND & COLLAPSE
/////////////////////////////////////////////////////////////////////////////////////////
	public void collapseAll() {
		this.setAllSectionExpanded(false);
	}
	public void expandAll() {
		this.setAllSectionExpanded(true);
	}
	public void setAllSectionExpanded(final boolean expanded) {
		if (_menuSections.isEmpty()) return;
		_menuSections.stream()
					 .filter(section -> section instanceof VaadinVerticalMenuContainerSection)
					 .forEach(section -> ((VaadinVerticalMenuContainerSection)section).setExpanded(expanded));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	I18N
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void updateI18NMessages(final UII18NService i18n) {
		if (CollectionUtils.hasData(_menuSections)) _menuSections.stream()
					 											 .forEach(section -> section.updateI18NMessages(i18n));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		if (CollectionUtils.isNullOrEmpty(_menuSections)) return "NO menu items";
		StringBuilder sb = new StringBuilder();
		for (VaadinVerticalMenuSectionBase section : _menuSections) {
			sb.append("- ").append(section.getItemId());
			if (section.isItemsContainer()) {
				VaadinVerticalMenuContainerSection container = section.as(VaadinVerticalMenuContainerSection.class);
				for (VaadinVerticalMenuClickableItem item : container.getItemsIterable()) {
					sb.append("\n" +
							  "\t- ").append(item.getItemId());
				}
			}
		}
		return sb;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EVENTS
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class VaadinMenuItemSelectedEvent {
		@Getter private final VaadinVerticalMenuItem _previouslySelectedItem;
		@Getter private final VaadinVerticalMenuItem _selectedItem;

		public UIComponentID getPreviouslySelectedItemId() {
			return _previouslySelectedItem != null ? _previouslySelectedItem.getItemId() : null;
		}
		public UIComponentID getSelectedItemId() {
			return _selectedItem != null ? _selectedItem.getItemId() : null;
		}
	}
	@FunctionalInterface
	public interface VaadinVerticalMenuItemSelectedListener
			 extends Serializable {
		public void onItemSelected(VaadinMenuItemSelectedEvent itemSelectedEvent);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	MENU ITEMS
/////////////////////////////////////////////////////////////////////////////////////////
	public interface VaadinVerticalMenuItem
			 extends VaadinViewI18NMessagesCanBeUpdated,
	  			     VaadinComponentHasCaption,VaadinComponentHasIcon {
		public UIComponentID getItemId();
		public I18NKey getI18nKey();

		public default boolean isItemsContainer() {
			return this instanceof VaadinVerticalMenuContainerSection;
		}
		public default boolean isClickableItem() {
			return this instanceof VaadinVerticalMenuClickableSection
				|| this instanceof VaadinVerticalMenuClickableItem;
		}
		public default boolean isSelectableItem() {
			return this.isClickableItem();
		}
		@SuppressWarnings({ "unchecked","unused" })
		public default <I extends VaadinVerticalMenuItem> I as(final Class<I> itemType) {
			return (I)this;
		}
		public default IsSelectable asSelectable() {
			return (IsSelectable)this;
		}
	}
	/**
	 * <pre>
	 *		+===========================================+
	 *		|  +-------------------------------------+  |<-- item (vertical layout)
	 *		|  | icon caption                     \/ |  |
	 * 		|  +-------------------------------------+  |
	 * 		| 											|
	 * 		|  optional sub-items (css layout)          |
	 *	    +===========================================+
	 * </pre>
	 */
	@Accessors(prefix="_")
	private abstract class VaadinVerticalMenuSectionBase
				   extends VerticalLayout
	  	        implements VaadinVerticalMenuItem {

		private static final long serialVersionUID = 7695193010089918636L;

		@Getter protected final UIComponentID _itemId;
		@Getter	protected final I18NKey _i18nKey;

				protected final Label _lblIcon;
				protected final Label _lblCaption;
				protected final Label _lblHandlerIcon;

		protected VaadinVerticalMenuSectionBase(final UIComponentID itemId,
												final I18NKey i18nKey,final Resource icon) {
			_itemId = itemId != null ? itemId
									 : i18nKey != null ? UIComponentID.from(i18nKey)	// if no itemId is provided, use the i18nKey
											 		   : null;
			_i18nKey = i18nKey;

			if (_itemId != null) this.setId(_itemId.asString());

			// [a] - Wrap the caption & icon in an horizontal layout
			_lblIcon = new Label();
			_lblIcon.setIcon(icon);
			if (icon == null) _lblIcon.setVisible(false); // hide label if there is no icon

			String caption = i18nKey != null ? _i18n.getMessage(_i18nKey) : "";
			_lblCaption = new Label(caption);

			_lblHandlerIcon = new Label();		// the label for the expand [\/] or collapse [/\] icon

			HorizontalLayout hlTitle = _createCaptionLayout(_lblIcon,_lblCaption,_lblHandlerIcon);

			// [b] create the VerticalLayout
			this.setMargin(false);
			this.setSpacing(false);

			// title
			this.addComponent(hlTitle);
			this.setExpandRatio(hlTitle,1);
		}
		private HorizontalLayout _createCaptionLayout(final Label icon,final Label caption,final Label iconHandler) {
			HorizontalLayout hlTitle = new HorizontalLayout();
			hlTitle.addStyleName(VaadinValoTheme.ROOT_MENU_TITLE);
			hlTitle.setSpacing(false);
			hlTitle.setWidth(100,Unit.PERCENTAGE);

			hlTitle.addComponents(icon,caption,iconHandler);
			hlTitle.setComponentAlignment(icon,Alignment.MIDDLE_LEFT);
			hlTitle.setComponentAlignment(iconHandler,Alignment.MIDDLE_RIGHT);
			return hlTitle;
		}
		protected HorizontalLayout _getTitleLayout() {
			return (HorizontalLayout)this.getComponent(0);	// the first component
		}
		@Override
		public String getCaption() {
			return _lblCaption.getCaption();
		}
		@Override
		public void setCaption(final String caption) {
			_lblCaption.setCaption(caption);
		}
		@Override
		public Resource getIcon() {
			return _lblIcon.getIcon();
		}
		@Override
		public void setIcon(final Resource icon) {
			_lblIcon.setIcon(icon);
		}
		////////// I18n
		@Override
		public void updateI18NMessages(final UII18NService i18n) {
			// Caption
			String caption = _i18nKey != null ? i18n.getMessage(_i18nKey) : "";
			_lblCaption.setValue(caption);
		}
	}
	/**
	 * A clickable section
	 * <pre>
	 *		+===========================================+
	 *		|  +-------------------------------------+  |
	 *		|  | [icon] label                        |  |
	 * 		|  +-------------------------------------+  |
	 *	    +===========================================+
	 * </pre>
	 */
	@Accessors(prefix="_")
	public class VaadinVerticalMenuClickableSection
		 extends VaadinVerticalMenuSectionBase
	  implements IsSelectable {

		private static final long serialVersionUID = 7359565469922925975L;

		private final VaadinVerticalMenuItemSelectedListener _selectedEventListener;

		protected VaadinVerticalMenuClickableSection(final UIComponentID itemId,
													 final I18NKey i18nKey,final Resource icon,
													 final VaadinVerticalMenuItemSelectedListener selectedEventListener) {
			super(itemId,
				  i18nKey,icon);

			// behavior
			_selectedEventListener = selectedEventListener;
			_setBehavior();
		}
		private void _setBehavior() {
			HorizontalLayout hlTitle = _getTitleLayout();
			hlTitle.addLayoutClickListener(clickEvent -> {
												if (_selectedEventListener != null) {
													VaadinVerticalMenuItem prevSelectedItem = VaadinVerticalMenu2Level.this.findSelectedItem();
													VaadinVerticalMenuItem nowSelectedItem = this;
													_selectedEventListener.onItemSelected(new VaadinMenuItemSelectedEvent(prevSelectedItem,nowSelectedItem));
												}
										   });
		}
		////////// IsSelectable
		@Override
		public boolean isSelected() {
			return this.getStyleName() != null
				&& this.getStyleName().contains(VaadinValoTheme.SELECTED);
		}
		@Override
		public void setSelectionTo(final boolean sel) {
			if (sel) {
				this.addStyleName(VaadinValoTheme.SELECTED);
			} else {
				this.removeStyleName(VaadinValoTheme.SELECTED);
			}
		}
	}
	/**
	 * A section that can contain items
	 * <pre>
	 *		+===========================================+
	 *		|  +-------------------------------------+  |
	 *		|  | [icon] label                     \/ |  |
	 * 		|  +-------------------------------------+  |
	 *		|  +-------------------------------------+  |
	 *		|  | items (css layout)			         |  |
	 *		|  |   button						     |  |
	 *	    |  |   button						     |  |
	 *	    |  |   ...							     |  |
	 * 		|  +-------------------------------------+  |
	 *	    +===========================================+
	 * </pre>
	 */
	@Accessors(prefix="_")
	public class VaadinVerticalMenuContainerSection
	     extends VaadinVerticalMenuSectionBase {

		private static final long serialVersionUID = 8941895340080807926L;

		private final Collection<VaadinVerticalMenuClickableItem> _items = Lists.newArrayList();

		protected VaadinVerticalMenuContainerSection(final UIComponentID itemId,
													 final I18NKey i18nKey,final Resource icon) {
			super(itemId,
				  i18nKey,icon);

			CssLayout csslSubItems = _creatItemsLayout();	// a layout that will contain the items

			this.addComponent(csslSubItems);
			this.setExpandRatio(csslSubItems,1);

			// behavior
			_setBehavior();
		}
		private CssLayout _creatItemsLayout() {
			CssLayout csslSubItems = new CssLayout();
			csslSubItems.setSizeFull();
			csslSubItems.setVisible(false);		// items not visible by default
			return csslSubItems;
		}
		protected CssLayout _getItemsContainerLayout() {
			return (CssLayout)this.getComponent(1);	// the css layout is the SECOND child of the parent VerticalLayout
		}
		private void _setBehavior() {
			HorizontalLayout hlTitle = _getTitleLayout();
			hlTitle.addLayoutClickListener(clickEvent -> {
												// when clicking the title...
												if (CollectionUtils.isNullOrEmpty(_items)) return;

												if (_items.size() == 1) {
													// if there exists a single item, just & expand or collapse this
													this.toggleExpanded();
												} else if (this.isExpanded()) {
													// if clicking on the the expanded section
													// ... just collapse this section (the other sections are supposed to be already collapsed)
													this.setExpanded(false);
												} else {
													// if clicking on a section other than the currently expanded one
													// ... close other already expanded sections & expand the clicked one
													VaadinVerticalMenu2Level.this.collapseAll();		// collapse other sub-menus
													this.setExpanded(true);								// now this is expanded
												}
										  });
		}
		Iterable<VaadinVerticalMenuClickableItem> getItemsIterable() {
			return _items;
		}
		public Resource getHandlerIcon() {
			return _lblHandlerIcon.getIcon();
		}
		public void setHandlerIcon(final Resource icon) {
			_lblHandlerIcon.setIcon(icon);
		}
		public void collapse() {
			this.setExpanded(false);
		}
		public void expand() {
			this.setExpanded(true);
		}
		public void toggleExpanded() {
			this.setExpanded(!this.isExpanded());
		}
		public boolean isExpanded() {
			CssLayout subItemsLayout = _getItemsContainerLayout();
			return subItemsLayout.isVisible();
		}
		public void setExpanded(final boolean expanded) {
			if (_items.size() <= 0) return;

			// set the icon
			Resource icon = expanded ? VaadinIcons.CARET_UP
								  	 : VaadinIcons.CARET_DOWN;
			_lblHandlerIcon.setIcon(icon);

			// hide or show the sub-menu layout
			CssLayout subItemsLayout = _getItemsContainerLayout();
			subItemsLayout.setVisible(expanded);
		}
		public VaadinVerticalMenuContainerSection addItem(final VaadinViewID viewId) {
			return this.addItem(viewId,(Map<String,String>)null);	// no navigation params
		}
		public VaadinVerticalMenuContainerSection addItem(final VaadinViewID viewId,
											  		   	  final Resource icon) {
			return this.addItem(viewId,null,		// no navigation params
						 		icon);
		}
		public VaadinVerticalMenuContainerSection addItem(final VaadinViewID viewId,final Map<String,String> navParams) {
			return this.addItem(viewId,navParams,
						 		null);		// no icon
		}
		public VaadinVerticalMenuContainerSection addItem(final VaadinViewID viewId,final Map<String,String> navParams,
											  		   	  final Resource icon) {
			return this.addItem(UIComponentID.from(viewId),	// componentId = viewId
								I18NKey.forId(viewId),icon,	// i18nKey = viewId
								subMenu -> UI.getCurrent()
											 .getNavigator()
											 .navigateTo(VaadinNavigator.vaadinViewUrlPathFragmentOf(viewId,navParams)));
		}
		public VaadinVerticalMenuContainerSection addItem(final I18NKey i18nKey,
											  		   	  final VaadinVerticalMenuItemSelectedListener selectedEventListener) {
			return this.addItem(UIComponentID.from(i18nKey),	// componentId = i18nKey
								i18nKey,null,	// no icon
						 		selectedEventListener);
		}
		public VaadinVerticalMenuContainerSection addItem(final I18NKey i18nKey,final Resource icon,
										      		   	  final VaadinVerticalMenuItemSelectedListener selectedEventListener) {
			return this.addItem(UIComponentID.from(i18nKey),	// componentId = i18nKey
								i18nKey,icon,
								selectedEventListener);
		}

		public VaadinVerticalMenuContainerSection addItem(final UIComponentID itemId,
											  		   	  final I18NKey i18nKey,final Resource icon,
											  		   	  final VaadinVerticalMenuItemSelectedListener selectedEventListener) {
			// Create the button
			VaadinVerticalMenuClickableItem item = new VaadinVerticalMenuClickableItem(itemId,
																	 				   i18nKey,icon,
																	 				   selectedEventListener);
			// add the item to the collection
			_items.add(item);
			if (_items.size() > 0) _lblHandlerIcon.setIcon(VaadinIcons.CARET_DOWN);	// now the child items can be expanded

			// Add the button to the items layout
			_getItemsContainerLayout().addComponent(item);

			// return fluent
			return this;
		}
		public void removeItemForViewWithId(final VaadinViewID viewId) {
			VaadinVerticalMenuClickableItem outSubMenuItem = this.findItemForViewWithId(viewId);
			_items.remove(outSubMenuItem);
			_getItemsContainerLayout().removeComponent(outSubMenuItem);
		}
		public void removeItemWithId(final UIComponentID viewId) {
			VaadinVerticalMenuClickableItem outSubMenuItem = this.findItemWithId(viewId);
			_items.remove(outSubMenuItem);
			_getItemsContainerLayout().removeComponent(outSubMenuItem);
		}
		public void clearItems() {
			_items.clear();
			_getItemsContainerLayout().removeAllComponents();
		}
		public VaadinVerticalMenuClickableItem findItemForViewWithId(final VaadinViewID viewId) {
			return this.findItemWithId(UIComponentID.from(viewId));	// componentId = viewId
		}
		public VaadinVerticalMenuClickableItem findItemWithId(final UIComponentID itemId) {
			return this.findItemMatching(item -> item.getItemId() != null
					 		 		  	  	  && item.getItemId().is(itemId));
		}
		public VaadinVerticalMenuClickableItem findItemMatching(final Predicate<VaadinVerticalMenuItem> pred) {
			if (_items.isEmpty()) return null;
			return _items.stream()
						 .filter(pred)
						 .findFirst()
						 .orElse(null);
		}
		@Override
		public String getCaption() {
			return _lblCaption.getCaption();
		}
		@Override
		public void setCaption(final String caption) {
			_lblCaption.setCaption(caption);
		}
		@Override
		public Resource getIcon() {
			return _lblHandlerIcon.getIcon();
		}
		@Override
		public void setIcon(final Resource icon) {
			_lblHandlerIcon.setIcon(icon);
		}
		////////// I18N
		@Override
		public void updateI18NMessages(final UII18NService i18n) {
			super.updateI18NMessages(i18n);
			if (CollectionUtils.hasData(_items)) _items.forEach(item -> item.updateI18NMessages(i18n));
		}
	}
	/**
	 * Just an item
	 */
	@Accessors(prefix="_")
	public class VaadinVerticalMenuClickableItem
		 extends Button
	  implements VaadinVerticalMenuItem,
	  			 IsSelectable {

		private static final long serialVersionUID = -6848667685051498424L;

		@Getter private final UIComponentID _itemId;
		@Getter private final I18NKey _i18nKey;

		private final VaadinVerticalMenuItemSelectedListener _selectedEventListener;

		public VaadinVerticalMenuClickableItem(final UIComponentID itemId,
									  		   final I18NKey i18nKey,final Resource icon,
									  		   final VaadinVerticalMenuItemSelectedListener selectedEventListener) {
			super(icon);

			_itemId = itemId != null ? itemId
									 : i18nKey != null ? UIComponentID.from(itemId)	// if itemId is null, use the i18nKey
											 		   : null;
			_i18nKey = i18nKey;

			if (_itemId != null) this.setId(_itemId.asString());
			this.setCaptionAsHtml(true);
			this.setPrimaryStyleName(ValoTheme.MENU_ITEM);

			// i18n
			this.updateI18NMessages(_i18n);

			// behavior
			_selectedEventListener = selectedEventListener;
			_setBehavior();
		}
		private void _setBehavior() {
			this.addClickListener(clickEvent -> {
										if (_selectedEventListener != null) {
											VaadinVerticalMenuItem prevSelectedItem = VaadinVerticalMenu2Level.this.findSelectedItem();
											VaadinVerticalMenuItem nowSelectedItem = this;
											_selectedEventListener.onItemSelected(new VaadinMenuItemSelectedEvent(prevSelectedItem,nowSelectedItem));
										}
								  });
		}
		////////// IsSelectable
		@Override
		public boolean isSelected() {
			return this.getStyleName() != null
				&& this.getStyleName().contains(VaadinValoTheme.SELECTED);
		}
		@Override
		public void setSelectionTo(final boolean sel) {
			if (sel) {
				this.addStyleName(VaadinValoTheme.SELECTED);
			} else {
				this.removeStyleName(VaadinValoTheme.SELECTED);
			}
		}
		////////// I18n
		@Override
		public void updateI18NMessages(final UII18NService i18n) {
			if (_i18nKey == null) return;

			I18NKey itemCaptionIK18NKey = _i18nKey != null ? I18NKey.forId(_i18nKey)
														   : null;

			I18NKey itemDescriptionI18NKey = _i18nKey != null && !itemCaptionIK18NKey.isLiteral() ? I18NKey.forId(Strings.customized("{}.description",
															   				  					 				  _i18nKey))
															  									   : null;
			String caption = itemCaptionIK18NKey != null ? i18n.getMessage(_i18nKey) : "";
			String description = itemDescriptionI18NKey != null ?  i18n.getMessage(itemDescriptionI18NKey) : "";

			this.setCaption(caption);
			this.setDescription(description);
		}
	}
}
