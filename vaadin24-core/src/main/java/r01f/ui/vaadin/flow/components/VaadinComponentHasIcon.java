package r01f.ui.vaadin.flow.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.Icon;

public interface VaadinComponentHasIcon {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public Component getIcon();
	
	/**
	 * Beware!! check that the resource is a valid icon
	 * <pre class='brush:java'>
     * 		if (icon != null && icon.getElement().isTextNode()) throw new IllegalArgumentException("Text node can't be used as an icon.");
     * </pre>
	 * @param icon
	 */
	public void setIcon(final Icon icon);
}
