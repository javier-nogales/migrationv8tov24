package r01f.ui.vaadin.flow.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;

/**
 * Wraps a vaadin {@link Component} with an icon as:
 * <pre>
 * 		[icon] [component]
 * </pre>
 */
public class VaadinComponentWithIconWrapper<C extends Component> 
	 extends Composite<Div> {

	private static final long serialVersionUID = -4455837964942734799L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private Icon _icon;
	private final C _component;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	private VaadinComponentWithIconWrapper(final C comp) {
		_component = comp;
		this.getContent()
			.add(_component);
	}
	private VaadinComponentWithIconWrapper(final C comp,final Icon icon) {
		_component = comp;
		this.getContent()
			.add(_icon,_component);
	}
	public static <C extends Component> VaadinComponentWithIconWrapper<C> wrap(final C comp) {
		return new VaadinComponentWithIconWrapper<>(comp);
	}
	public static <C extends Component> VaadinComponentWithIconWrapper<C> wrap(final C comp,final Icon icon) {
		return new VaadinComponentWithIconWrapper<>(comp,icon);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ICON
/////////////////////////////////////////////////////////////////////////////////////////
	public Icon getIcon() {
		if (this.getContent().getComponentCount() == 1) return null; 	// no icon
		
		Icon icon = null;
		if (this.getContent().getComponentCount() == 2) {
			// icon & component
			icon = (Icon)this.getContent().getComponentAt(0);
		} else if (this.getContent().getComponentCount() == 1) {
			// just component
			Component comp = this.getContent().getComponentAt(0);
			assert(comp != null);
		} else {
			throw new IllegalStateException("Component with icon is not valid!");
		}
		return icon;
	}
	public void setIcon(final Icon icon) {
		// a) remove existing icon if present
		Icon existingIcon = this.getIcon();
		if (icon != null && existingIcon != null) this.getContent().remove(existingIcon);
		
		// b) add new components
		if (icon != null) this.getContent().addComponentAsFirst(icon);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	COMPONENT
/////////////////////////////////////////////////////////////////////////////////////////	
	public C getComponent() {
		return _component;
	}
}
