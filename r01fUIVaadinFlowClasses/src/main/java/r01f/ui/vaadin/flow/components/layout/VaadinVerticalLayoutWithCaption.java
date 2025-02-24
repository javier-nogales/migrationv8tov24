package r01f.ui.vaadin.flow.components.layout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import r01f.util.types.collections.CollectionUtils;

/**
 * A {@link VerticalLayout} with a header
 * <pre class='brush:java'>
 * 		+====================================+
 * 		| [icon][text...]                    |
 * 		+------------------------------------+
 * 		| VerticalLayout                     |
 * 		+====================================+
 * </pre>
 * @param <C>
 */
public class VaadinVerticalLayoutWithCaption 
	 extends VaadinLayoutWithCaptionBase<VerticalLayout> {


	private static final long serialVersionUID = 2027872545636721290L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinVerticalLayoutWithCaption(final Component... components) {
		super(new VerticalLayout());
		if (CollectionUtils.hasData(components)) _lyContainer.add(components);
	}
	public VaadinVerticalLayoutWithCaption(final String label,
										   final Component... components) {
		super(label,
			  new VerticalLayout());
		if (CollectionUtils.hasData(components)) _lyContainer.add(components);
	}
	public VaadinVerticalLayoutWithCaption(final Icon icon,final String label,
										   final Component... components) {
		super(icon,label,
			  new VerticalLayout());
		if (CollectionUtils.hasData(components)) _lyContainer.add(components);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ADD
/////////////////////////////////////////////////////////////////////////////////////////
	public void addAndExpand(final Component... components) {
		_lyContainer.addAndExpand(components);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ALIGNMENT
/////////////////////////////////////////////////////////////////////////////////////////
	public void setHorizontalComponentAlignment(final Alignment alignment, final Component... componentsToAlign) {
		_lyContainer.setHorizontalComponentAlignment(alignment,componentsToAlign);
	}
	public Alignment getHorizontalComponentAlignment(final Component component) {
		return _lyContainer.getHorizontalComponentAlignment(component);
	}
	public void setDefaultHorizontalComponentAlignment(final Alignment alignment) {
		_lyContainer.setDefaultHorizontalComponentAlignment(alignment);
	}
	public Alignment getDefaultHorizontalComponentAlignment() {
		return _lyContainer.getDefaultHorizontalComponentAlignment();
	}
}
