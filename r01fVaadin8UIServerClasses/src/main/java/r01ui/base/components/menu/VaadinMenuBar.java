package r01ui.base.components.menu;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;

import com.google.common.collect.Lists;
import com.vaadin.server.Resource;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.locale.I18NKey;
import r01f.patterns.CommandOn;
import r01f.ui.i18n.UII18NService;
import r01f.ui.vaadin.styles.VaadinValoTheme;
import r01f.ui.vaadin.view.VaadinComponentHasCaption;
import r01f.ui.vaadin.view.VaadinComponentHasIcon;
import r01f.ui.vaadin.view.VaadinNavigator;
import r01f.ui.vaadin.view.VaadinViewI18NMessagesCanBeUpdated;
import r01f.ui.vaadin.view.VaadinViewID;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

public class VaadinMenuBar
	 extends MenuBar
  implements VaadinViewI18NMessagesCanBeUpdated {

	private static final long serialVersionUID = 3130637584876790267L;

/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	protected final Collection<VaadinMenuItem> _rootItems = Lists.newArrayList();

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinMenuBar() {
		// default no-args constructor
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ADD ROOT ITEMS
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinMenuItemBuilderIconStep addRootItemThatNavigatesTo(final VaadinViewID viewId) {
		return this.addRootItemThatNavigatesTo(viewId,(Map<String,String>)null);
	}
	public VaadinMenuItemBuilderIconStep addRootItemThatNavigatesTo(final VaadinViewID viewId,final Map<String,String> navParams) {
		return new VaadinMenuItemBuilderIconStep(VaadinMenuBar.this::addItem,
												 _rootItems,
												 _createVaadinNavigateCommand(viewId,navParams));
	}
	public VaadinMenuItemBuilderIconStep addRootItemForSubMenu() {
		return new VaadinMenuItemBuilderIconStep(VaadinMenuBar.this::addItem,
												 _rootItems,
												 null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public void executeInAllInHierarchy(final CommandOn<VaadinMenuItem> cmd) {
		if (CollectionUtils.isNullOrEmpty(_rootItems)) return;
		for (VaadinMenuItem rootItem : _rootItems) {
			rootItem.executeInAllInHierarchy(cmd);
		}
	}
	public VaadinMenuItem itemOf(final MenuItem menuItem) {
		return this.itemOf(item -> item.getMenuItem() == menuItem);
	}
	public VaadinMenuItem itemOf(final I18NKey key) {
		return this.itemOf(item -> item.getKey() != null
								&& item.getKey().is(key));
	}
	// Recursively finds an item verifying the given predicate
	public VaadinMenuItem itemOf(final Predicate<VaadinMenuItem> pred) {
		VaadinMenuItem outItem = null;
		for (VaadinMenuItem rootItem : _rootItems) {
			outItem = rootItem.itemOf(pred);
			if (outItem != null) break;
		}
		return outItem;
	}
	public boolean selectMenuFor(final I18NKey key) {
		if (_rootItems.isEmpty()) return false;

		// [1] - Unselect the selected menu item
		this.executeInAllInHierarchy(VaadinMenuItem::setNOTSelected);

		// [2] - Find an item with the given key
		VaadinMenuItem item = this.itemOf(key);

		// [3] - Select the item
		if (item != null) item.setSelected();

		// [4] - Return
		return item != null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	I18N
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void updateI18NMessages(final UII18NService i18n) {
		for (VaadinMenuItem item : _rootItems) {
			item.updateI18NMessages(i18n);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class VaadinMenuItem
	  implements VaadinComponentHasCaption,VaadinComponentHasIcon,
	  			 VaadinViewI18NMessagesCanBeUpdated,
	  			 Serializable {

		private static final long serialVersionUID = -7424723184476135645L;

		@Getter private final MenuItem _menuItem;
		@Getter private final I18NKey _key;			// used to update literals

		@Getter private boolean _selected;

		@Getter private final Collection<VaadinMenuItem> _subItems = Lists.newArrayList();

		public boolean hasSubItems() {
			return !_subItems.isEmpty();
		}
		public void executeInAllInHierarchy(final CommandOn<VaadinMenuItem> cmd) {
			cmd.executeOn(this);

			if (CollectionUtils.isNullOrEmpty(_subItems)) return;
			for (VaadinMenuItem item : _subItems) {
				cmd.executeOn(item);
				for (VaadinMenuItem subItem : item.getSubItems()) {
					subItem.executeInAllInHierarchy(cmd);	// BEWARE!! recursion
				}
			}
		}
		////////// Item access
		public VaadinMenuItem itemOf(final MenuItem menuItem) {
			return this.itemOf(item -> item.getMenuItem() == menuItem);
		}
		public VaadinMenuItem itemOf(final I18NKey key) {
			return this.itemOf(item -> item.getKey().is(key));
		}
		// Recursively finds an item verifying the given predicate
		public VaadinMenuItem itemOf(final Predicate<VaadinMenuItem> pred) {
			// check this
			if (pred.test(this)) return this;

			// check children
			if (CollectionUtils.isNullOrEmpty(_subItems)) return null;
			VaadinMenuItem outItem = null;
			for (VaadinMenuItem item : _subItems) {
				if (pred.test(item)) {
					return item;
				}
				for (VaadinMenuItem subItem : item.getSubItems()) {
					outItem = subItem.itemOf(pred);		// BEWARE!! recursion
					if (outItem != null) return outItem;
				}
			}
			return null;
		}
		////////// Get & Set
		public void setEnabled(final boolean enabled) {
			_menuItem.setEnabled(enabled);
		}
		public void setVisible(final boolean visible) {
			_menuItem.setVisible(visible);
		}
		public void setSelected() {
			_selected = true;
			_menuItem.setStyleName(VaadinValoTheme.MENU_ITEM_CHECKED);
		}
		public void setNOTSelected() {
			_selected = false;
			_menuItem.setStyleName(null);
		}
		@Override
		public String getCaption() {
			return _menuItem.getText();
		}
		@Override
		public void setCaption(final String caption) {
			_menuItem.setText(caption);
		}
		@Override
		public Resource getIcon() {
			return _menuItem.getIcon();
		}
		@Override
		public void setIcon(final Resource icon) {
			_menuItem.setIcon(icon);
		}
		////////// Target view id or command
		public VaadinMenuItemBuilderIconStep addItemThatNavigatesTo(final VaadinViewID viewId) {
			return this.addItemThatExecutes(_createVaadinNavigateCommand(viewId,null));
		}
		public VaadinMenuItemBuilderIconStep addItemThatNavigatesTo(final VaadinViewID viewId,final Map<String,String> navParams) {
			return this.addItemThatExecutes(_createVaadinNavigateCommand(viewId,navParams));
		}
		public VaadinMenuItemBuilderIconStep addItemThatExecutes(final MenuBar.Command command) {
			return new VaadinMenuItemBuilderIconStep((theCaption,theIcon,theCommand) -> _menuItem.addItem(theCaption,theIcon,
															 				  							  theCommand),
													 _subItems,
													 command);
		}
		public VaadinMenuItemBuilderIconStep addItemForSubMenu() {
			return new VaadinMenuItemBuilderIconStep((theCaption,theIcon,theCommand) -> _menuItem.addItem(theCaption,theIcon,
															 				  							  theCommand),
													 _subItems,
													 null);		// no command
		}
		////////// I18N
		@Override
		public void updateI18NMessages(final UII18NService i18n) {
			if (_key != null && Strings.isNOTNullOrEmpty(_key.getId())) _menuItem.setText(i18n.getMessage(_key));
			if (this.hasSubItems()) {
				for (VaadinMenuItem subItem : _subItems) {
					subItem.updateI18NMessages(i18n);
				}
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor
	public class VaadinMenuItemBuilderIconStep {
		private final VaadinHasMenuItems _hasMenuItems;
		private final Collection<VaadinMenuItem> _subItems;

		private final MenuBar.Command _command;

		public final VaadinMenuItemBuilderCaptionStep withIcon(final Resource icon) {
			return new VaadinMenuItemBuilderCaptionStep(_hasMenuItems,_subItems,
														_command,
														icon);
		}
		public final VaadinMenuItemBuilderCaptionStep withoutIcon() {
			return new VaadinMenuItemBuilderCaptionStep(_hasMenuItems,_subItems,
														_command,
														null);
		}
	}
	@RequiredArgsConstructor
	public class VaadinMenuItemBuilderCaptionStep {
		private final VaadinHasMenuItems _hasMenuItems;
		private final Collection<VaadinMenuItem> _subItems;

		private final MenuBar.Command _command;
		private final Resource _icon;

		public VaadinMenuItemBuilderI18NServiceStep withCaption(final I18NKey i18nKey) {
			return new VaadinMenuItemBuilderI18NServiceStep(_hasMenuItems,_subItems,
													  		_command,
													  		_icon,i18nKey);
		}
		public VaadinMenuItemBuilderBuildStep withCaption(final String caption) {
			return new VaadinMenuItemBuilderBuildStep(_hasMenuItems,_subItems,
													  _command,
													  _icon,caption,caption != null ? I18NKey.literal(caption) : null);
		}
		public VaadinMenuItemBuilderBuildStep withoutCaption() {
			return this.withCaption((String)null);	// no caption
		}
	}
	@RequiredArgsConstructor
	public class VaadinMenuItemBuilderI18NServiceStep {
		private final VaadinHasMenuItems _hasMenuItems;
		private final Collection<VaadinMenuItem> _subItems;

		private final MenuBar.Command _command;
		private final Resource _icon;
		private final I18NKey _i18nKey;

		public VaadinMenuItemBuilderBuildStep using(final UII18NService i18n) {
			return new VaadinMenuItemBuilderBuildStep(_hasMenuItems,_subItems,
													  _command,
													  _icon,_i18nKey != null ? i18n.getMessage(_i18nKey) : "",_i18nKey);
		}
	}
	@RequiredArgsConstructor
	public class VaadinMenuItemBuilderBuildStep {
		private final VaadinHasMenuItems _hasMenuItems;
		private final Collection<VaadinMenuItem> _subItems;

		private final MenuBar.Command _command;
		private final Resource _icon;
		private final String _caption;
		private final I18NKey _i18nKey;	// used to update literals

		public VaadinMenuItem build() {
			String theCaption = _caption != null ? _caption : "";
			MenuItem menuItem = _hasMenuItems.addItem(theCaption,_icon,
										  	  		  _command);
			VaadinMenuItem item = new VaadinMenuItem(menuItem,
													 _i18nKey);	// used to update literals
			_subItems.add(item);
			return item;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Command to execute when an item is clicked
/////////////////////////////////////////////////////////////////////////////////////////
	/** Navigate to a view by menu selection */
	private MenuBar.Command _createVaadinNavigateCommand(final VaadinViewID viewId,final Map<String,String> navParams) {
		return new MenuBar.Command() {
						private static final long serialVersionUID = 7920906555442357534L;

						@Override
						public void menuSelected(final MenuItem selectedItem) {
							// [1] - Unselect the selected menu item
					   		VaadinMenuBar.this.executeInAllInHierarchy(VaadinMenuItem::setNOTSelected);

							// [2] - Find an item with the given menu
							VaadinMenuItem item = VaadinMenuBar.this.itemOf(selectedItem);

							if (item == null) throw new IllegalStateException();
							item.setSelected();

							// [3] - use the key as vaadin id
							if (viewId != null) {
								String viewUrlPathParam = VaadinNavigator.vaadinViewUrlPathFragmentOf(viewId,
								                                                                      navParams);
								UI.getCurrent().getNavigator()
											   .navigateTo(viewUrlPathParam);
							} else {
								Notification.show("Vaadin view NOT available");
							}
						}
			   };
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@FunctionalInterface
	private interface VaadinHasMenuItems {
		public MenuItem addItem(final String caption,final Resource icon,
								final MenuBar.Command command);
	}
}
