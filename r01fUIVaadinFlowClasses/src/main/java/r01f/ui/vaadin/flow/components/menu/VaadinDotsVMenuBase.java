package r01f.ui.vaadin.flow.components.menu;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;

/**
 * A dots [vertical menu] like:
 * <pre>
 * 		[:]
 * 		  +++++++++++++
 *        + Menu Item +
 *        + Menu Item +
 *        + Menu Item +
 *        +++++++++++++
 * </pre>
 */
public abstract class VaadinDotsVMenuBase<SELF_TYPE extends VaadinDotsVMenuBase<SELF_TYPE>>
			  extends MenuBar {

	private static final long serialVersionUID = 1987426127671369830L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final MenuItem _mainMenuItem;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinDotsVMenuBase() {
		super();

		// create the root [ellipsis] menu
		MenuItem mnuItemRoot = VaadinMenus.createMenuItemWithIcon(this,
																  new Icon(VaadinIcon.ELLIPSIS_DOTS_V),
																  null,		// no label
																  null);	// no command
		_mainMenuItem = mnuItemRoot;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public MenuItem addItem(final String caption) {
		return _mainMenuItem.getSubMenu()
							.addItem(caption);
	}
	@Override
	public MenuItem addItem(final String caption,
							final ComponentEventListener<ClickEvent<MenuItem>> clickEventListener) {
		return _mainMenuItem.getSubMenu()
							.addItem(caption,
									 clickEventListener);
	}
	public MenuItem addItem(final String caption,final Icon icon,
							final ComponentEventListener<ClickEvent<MenuItem>> clickEventListener) {
		return _mainMenuItem.getSubMenu()
							.addItem(VaadinMenus.createMenuChildItemWithIcon(_mainMenuItem.getSubMenu(),
																			 icon,caption,
																			 clickEventListener));
	}
	public void addComponentAtIndex(final int index,final Component comp) {
		_mainMenuItem.getSubMenu()
					 .addComponentAtIndex(0,comp);
	}
}
