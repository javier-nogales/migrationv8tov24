package r01f.ui.vaadin.flow.components.label;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;

/**
 * A label with an optional icon like:
 * <pre>
 * 		[Icon] [Label]
 * </pre>
 */
public class VaadinInfo 
	 extends Composite<Div> 
  implements HasSize,
  			 HasStyle {

	private static final long serialVersionUID = 7837032567095828099L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinInfo() {
		// default no-args constructor
	}
	public <C extends Component> VaadinInfo(final C label,final Icon icon) {
		Div div = this.getContent();
		if (label != null && icon != null) {
			div.add(icon,label);
		} else if (label != null) {
			div.add(label);
		} else if (icon != null) {
			div.add(icon);
		}
	}
	public static VaadinInfo create(final String labelStr,final Icon icon) {
		return new VaadinInfo(labelStr != null ? new Span(labelStr) : null,
							  icon);
	}
	public static VaadinInfo create(final String labelStr) {
		return VaadinInfo.create(labelStr,
								 null);	// no icon
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GET & SET 
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public <C extends Component> C getInfoComponent() {
		C outComponent = null;
		
		Div div = this.getContent();
		if (div.getComponentCount() == 2) {
			// [icon] and [label]
			outComponent = (C)div.getComponentAt(1);
		} else if (div.getComponentCount() == 1) {
			// [label] or [icon]
			Component comp = div.getComponentAt(0);
			outComponent = comp instanceof Span ? (C)comp
										   : null;		// the component is an icon
		} else {
			// the div does not contain any component
		}
		return outComponent;
	}
	public <C extends Component> void setInfoComponent(final C newComp) {
		Div div = this.getContent();
		C comp = this.getInfoComponent();
		if (comp != null) div.remove(comp);
		if (newComp != null) div.add(newComp);
	}
	public Icon getIcon() {
		Icon outIcon = null;
		
		Div div = this.getContent();
		if (div.getComponentCount() == 2) {
			// [icon] and [label]
			outIcon = (Icon)div.getComponentAt(0);
		} else if (div.getComponentCount() == 1) {
			// [label] or [icon]
			Component comp = div.getComponentAt(0);
			outIcon = comp instanceof Icon ? (Icon)comp
										   : null;		// the component is an span
		} else {
			// the div does not contain any component
		}
		return outIcon;
	}
	public void setIcon(final Icon newIcon) {
		Div div = this.getContent();
		Icon icon = this.getIcon();
		if (icon != null) div.remove(icon);
		if (newIcon != null) div.addComponentAtIndex(0,newIcon);
	}
}
