package r01f.ui.vaadin.flow.components.menu;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.contextmenu.HasMenuItems;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utilities for {@link MenuBar} and {@link MenuItem}
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class VaadinMenus {
/////////////////////////////////////////////////////////////////////////////////////////
//	MENU ITEMS WITH ICON
/////////////////////////////////////////////////////////////////////////////////////////
	public static MenuItem createMenuItemWithIcon(final HasMenuItems menu, 
												  final Icon icon,
												  final String label,
												  final ComponentEventListener<ClickEvent<MenuItem>> clickEventListener) {
	    return VaadinMenus.createMenuItemWithIcon(menu,
	    										  icon,label,null,	// no aria-label 
	    										  clickEventListener,
	    										  false);	// is child
	}
	public static MenuItem createMenuItemWithIcon(final HasMenuItems menu, 
												  final Icon icon,
												  final String label,final String ariaLabel,
												  final ComponentEventListener<ClickEvent<MenuItem>> clickEventListener) {
	    return VaadinMenus.createMenuItemWithIcon(menu,
	    										  icon,label,ariaLabel, 
	    										  clickEventListener,
	    										  false);	// is child
	}
	public static MenuItem createMenuChildItemWithIcon(final HasMenuItems menu,
												  	   final Icon icon,
												  	   final String label,
												  	   final ComponentEventListener<ClickEvent<MenuItem>> clickEventListener) {
	    return VaadinMenus.createMenuItemWithIcon(menu,
	    										  icon,label,null,	// no aria-label 
	    										  clickEventListener,
	    										  true);	// is child		
	}
	public static MenuItem createMenuChildItemWithIcon(final HasMenuItems menu,
												  	   final Icon icon,
												  	   final String label,final String ariaLabel,
												  	   final ComponentEventListener<ClickEvent<MenuItem>> clickEventListener) {
	    return VaadinMenus.createMenuItemWithIcon(menu,
	    										  icon,label,ariaLabel, 
	    										  clickEventListener,
	    										  true);	// is child		
	}
	public static MenuItem createMenuItemWithIcon(final HasMenuItems menu,
												  final Icon icon,
												  final String label,final String ariaLabel,
												  final ComponentEventListener<ClickEvent<MenuItem>> clickEventListener,
												  final boolean isChild) {
	    if (isChild) {
	        icon.getStyle().set("width","var(--lumo-icon-size-s)");
	        icon.getStyle().set("height","var(--lumo-icon-size-s)");
	        icon.getStyle().set("marginRight","var(--lumo-space-s)");
	    }
	
	    MenuItem item = menu.addItem(icon, 
	    							 clickEventListener);
	    // label & areia label
	    if (label != null) item.add(new Label(label));
	    if (ariaLabel != null) item.getElement().setAttribute("aria-label",ariaLabel);	
	    return item;
	}
}
