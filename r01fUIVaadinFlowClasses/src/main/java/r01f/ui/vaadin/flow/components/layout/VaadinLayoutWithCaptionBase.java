package r01f.ui.vaadin.flow.components.layout;

import java.util.Collection;
import java.util.stream.Stream;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.HasOrderedComponents;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.ShortcutRegistration;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.BoxSizing;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.ThemableLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.ClassList;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.shared.Registration;

/**
 * A layout ({@link VerticalLayout}, {@link HorizontalLayout}...) with a header
 * <pre class='brush:java'>
 * 		+====================================+
 * 		| [icon][text...]                    |
 * 		+------------------------------------+
 * 		| Layout (vertical / horizontal)     |
 * 		+====================================+
 * </pre>
 * @param <C>
 */
abstract class VaadinLayoutWithCaptionBase<C extends Component
										 		   & HasOrderedComponents
										 		   & ThemableLayout 	
										 		   & FlexComponent 
										 		   & ClickNotifier<C>>
	   extends Composite<C> 
    implements ThemableLayout,
   			   FlexComponent,
   			   ClickNotifier<C> {

	private static final long serialVersionUID = -4014937641394053894L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	protected final C _lyContainer;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	protected VaadinLayoutWithCaptionBase(final C ly) {
		// create components
		_lyContainer = ly;
		
		// style
		_styleLayoutContainer(_lyContainer);
		_style();
		
		// layout
		this.add(_lyContainer);
	}
	protected VaadinLayoutWithCaptionBase(final String label,
										  final C ly) {
		// create components
		Div divHeader = _createHeaderContainerWith(null,
												   new Label(label));
		_lyContainer = ly;
		
		_styleLayoutContainer(_lyContainer);
		_style();

		// layout
		this.add(divHeader,_lyContainer);
	}
	protected VaadinLayoutWithCaptionBase(final Icon icon,final String label,
										  final C ly) {
		// create components
		Div divHeader = _createHeaderContainerWith(icon,
												   new Label(label));
		_lyContainer = ly;
		
		_styleLayoutContainer(_lyContainer);
		_style();

		// layout
		this.add(divHeader,_lyContainer);
	}
	private void _styleLayoutContainer(final C ly) {
		ly.setSizeFull();
	}
	private void _style() {
		this.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
		this.setSpacing(false);
		this.setPadding(false);
		this.setMargin(false);
		
		this.setFlexGrow(0,_getHeaderContainer());	// see https://www.joshwcomeau.com/css/interactive-guide-to-flexbox/
		this.setFlexGrow(1,_lyContainer);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	HEADER
/////////////////////////////////////////////////////////////////////////////////////////
	private Div _createHeaderContainerWith(final Icon icon,final Label lbl) {
		Div divHeader = null;
		if (this.getComponentCount() == 2) {
			// existing header
			divHeader = (Div)_getHeaderContainer();
		} else if (this.getComponentCount() == 1) {
			// create header
			divHeader = new Div(icon,lbl);
		} else {
			throw new IllegalStateException();
		}
		// add components
		_addOrReplaceHeaderComponents(divHeader,
							 		  icon,lbl);
		return divHeader;
	}
	private void _addOrReplaceHeaderComponents(final HasOrderedComponents header,
									  		   final Icon icon,final Label lbl) {
		// a) remove existing components if present
		Icon existingIcon = _getHeaderIcon();
		Label existingLbl = _getHeaderLabel();
		if (icon != null && existingIcon != null) header.remove(existingIcon);
		if (lbl != null && existingLbl != null) header.remove(existingLbl);
		
		// b) add new components
		if (icon != null) header.addComponentAsFirst(icon);
		if (lbl != null) header.add(lbl);
	}
	private HasOrderedComponents _getHeaderContainer() {
		if (this.getComponentCount() != 2) throw new IllegalStateException("NO header present!");
		HasOrderedComponents spanHeader = (HasOrderedComponents)this.getComponentAt(0);
		return spanHeader;
	}
	private Label _getHeaderLabel() {
		if (this.getComponentCount() == 1) return null; 	// no header
		
		HasOrderedComponents headerContainer = _getHeaderContainer();
		Label outLbl = null;
		if (headerContainer.getComponentCount() == 2) {
			// icon & text
			outLbl = (Label)headerContainer.getComponentAt(1);
		} else if (headerContainer.getComponentCount() == 1) {
			// just icon or text
			Component comp = headerContainer.getComponentAt(0);
			if (comp instanceof Label) outLbl = (Label)comp;
		} else {
			throw new IllegalStateException("Header is not valid!");
		}
		return outLbl;
	}
	private Icon _getHeaderIcon() {
		if (this.getComponentCount() == 1) return null; 	// no header
		
		HasOrderedComponents headerContainer = _getHeaderContainer();
		Icon icon = null;
		if (headerContainer.getComponentCount() == 2) {
			// icon & text
			icon = (Icon)headerContainer.getComponentAt(0);
		} else if (headerContainer.getComponentCount() == 1) {
			// just icon or text
			Component comp = headerContainer.getComponentAt(0);
			if (comp instanceof Icon) icon = (Icon)comp;
		} else {
			throw new IllegalStateException("Header is not valid!");
		}
		return icon;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public String getLabel() {
		Label lbl = _getHeaderLabel();
		return lbl != null ? lbl.getText() : null;
	}
	public void setLabel(final String lbl) {
		Label lblHeader = _getHeaderLabel();
		if (lblHeader != null) {
			// existing header text
			lblHeader.setText(lbl);
		} else {
			// new header text
			lblHeader = new Label(lbl);
			
			HasOrderedComponents header = _getHeaderContainer();
			if (header != null) {
				_addOrReplaceHeaderComponents(header,
									 		  null,lblHeader);	// do not change icon if exists
			} else {
				header = _createHeaderContainerWith(null,lblHeader);
				this.addComponentAsFirst((Component)header);
			}
		}
	}
	public Icon getIcon() {
		Icon icon = _getHeaderIcon();
		return icon;
	}
	public void setIcon(final Icon icon) {
		HasOrderedComponents header = _getHeaderContainer();
		if (header != null) {
			_addOrReplaceHeaderComponents(header,
								 		  icon,null);	// do not change text if exists
		} else {
			header = _createHeaderContainerWith(icon,null);	// no text
			this.addComponentAsFirst((Component)header);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	HAS COMPONENTS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int indexOf(final Component component) {
		return _lyContainer.indexOf(component);
	}
	@Override
	public int getComponentCount() {
		return _lyContainer.getComponentCount();
	}
	@Override
	public Component getComponentAt(final int index) {
		return _lyContainer.getComponentAt(index);
	}
	@Override
	public void add(final Component... components) {
		_lyContainer.add(components);
	}
	@Override
	public void add(final Collection<Component> components) {
		_lyContainer.add(components);
	}
	@Override
	public void add(final String text) {
		_lyContainer.add(text);
	}
	@Override
	public void addComponentAtIndex(final int index, final Component component) {
		_lyContainer.addComponentAtIndex(index, component);
	}
	@Override
	public void addComponentAsFirst(final Component component) {
		_lyContainer.addComponentAsFirst(component);
	}
	@Override
	public void remove(final Component... components) {
		_lyContainer.remove(components);
	}
	@Override
	public void remove(final Collection<Component> components) {
		_lyContainer.remove(components);
	}
	@Override
	public void removeAll() {
		_lyContainer.removeAll();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	VERTICAL LAYOUT OVERRIDE
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public void setSpacing(final boolean spacing) {
		_lyContainer.setSpacing(spacing);
	}
	@Override
	public void setPadding(final boolean padding) {
		_lyContainer.setPadding(padding);
	}
	@Override
	public void setAlignItems(final Alignment alignment) {
		_lyContainer.setAlignItems(alignment);
	}
	@Override
	public Alignment getAlignItems() {
		return _lyContainer.getAlignItems();
	}
	@Override
	public void setAlignSelf(final Alignment alignment,final HasElement... elementContainers) {
		_lyContainer.setAlignSelf(alignment,elementContainers);
	}
	@Override
	public Alignment getAlignSelf(final HasElement container) {
		return _lyContainer.getAlignSelf(container);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	FLEX COMPONENT
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void setFlexGrow(final double flexGrow, final HasElement... elementContainers) {
		_lyContainer.setFlexGrow(flexGrow, elementContainers);
	}
	@Override
	public double getFlexGrow(final HasElement elementContainer) {
		return _lyContainer.getFlexGrow(elementContainer);
	}
	@Override
	public void setJustifyContentMode(final JustifyContentMode justifyContentMode) {
		_lyContainer.setJustifyContentMode(justifyContentMode);
	}
	@Override
	public JustifyContentMode getJustifyContentMode() {
		return _lyContainer.getJustifyContentMode();
	}
	@Override
	public void expand(final Component... componentsToExpand) {
		_lyContainer.expand(componentsToExpand);
	}
	@Override
	public void replace(final Component oldComponent, final Component newComponent) {
		_lyContainer.replace(oldComponent, newComponent);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	THEMABLE LAYOUT
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public void setMargin(final boolean margin) {
		_lyContainer.setMargin(margin);
	}
	@Override
	public boolean isMargin() {
		return _lyContainer.isMargin();
	}
	@Override
	public boolean isPadding() {
		return _lyContainer.isPadding();
	}
	@Override
	public boolean isSpacing() {
		return _lyContainer.isSpacing();
	}
	@Override
	public ThemeList getThemeList() {
		return _lyContainer.getThemeList();
	}
	@Override
	public void setBoxSizing(final BoxSizing boxSizing) {
		_lyContainer.setBoxSizing(boxSizing);
	}
	@Override
	public BoxSizing getBoxSizing() {
		return _lyContainer.getBoxSizing();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ENABLE / DISABLE
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public void setEnabled(final boolean enabled) {
		_lyContainer.setEnabled(enabled);
	}
	@Override
	public boolean isEnabled() {
		return _lyContainer.isEnabled();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ATTACH / DETACH NOTIFIER
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Registration addDetachListener(final ComponentEventListener<DetachEvent> listener) {
		return _lyContainer.addDetachListener(listener);
	}
	@Override
	public Registration addAttachListener(final ComponentEventListener<AttachEvent> listener) {
		return _lyContainer.addAttachListener(listener);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CLICK NOTIFIER
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public Registration addClickListener(final ComponentEventListener<ClickEvent<C>> listener) {
		return _lyContainer.addClickListener(listener);
	}	
	@Override
	public ShortcutRegistration addClickShortcut(final Key key, final KeyModifier... keyModifiers) {
		return _lyContainer.addClickShortcut(key, keyModifiers);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	HAS STYLE
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public void addClassName(final String className) {
		_lyContainer.addClassName(className);
	}
	@Override
	public boolean removeClassName(final String className) {
		return _lyContainer.removeClassName(className);
	}
	@Override
	public void setClassName(final String className) {
		_lyContainer.setClassName(className);
	}
	@Override
	public String getClassName() {
		return _lyContainer.getClassName();
	}
	@Override
	public ClassList getClassNames() {
		return _lyContainer.getClassNames();
	}
	@Override
	public void setClassName(final String className, final boolean set) {
		_lyContainer.setClassName(className, set);
	}
	@Override
	public boolean hasClassName(final String className) {
		return _lyContainer.hasClassName(className);
	}
	@Override
	public Style getStyle() {
		return _lyContainer.getStyle();
	}
	@Override
	public void addClassNames(final String... classNames) {
		_lyContainer.addClassNames(classNames);
	}
	@Override
	public void removeClassNames(final String... classNames) {
		_lyContainer.removeClassNames(classNames);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	COMPONENT
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public Element getElement() {
		return _lyContainer.getElement();
	}
	@Override
	public Stream<Component> getChildren() {
		return _lyContainer.getChildren();
	}
}
