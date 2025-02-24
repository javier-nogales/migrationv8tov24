package r01f.ui.vaadin.flow.components.layout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import r01f.util.types.collections.CollectionUtils;

/**
 * A {@link HorizontalLayout} with a header
 * <pre class='brush:java'>
 * 		+====================================+
 * 		| [icon][text...]                    |
 * 		+------------------------------------+
 * 		| HorizontalLayout                   |
 * 		+====================================+
 * </pre>
 * @param <C>
 */
public class VaadinHorizontalLayoutWithCaption 
	 extends VaadinLayoutWithCaptionBase<HorizontalLayout> {

	private static final long serialVersionUID = -2202453738411141673L;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinHorizontalLayoutWithCaption(final Component... components) {
		super(new HorizontalLayout());
		if (CollectionUtils.hasData(components)) _lyContainer.add(components);
	}
	public VaadinHorizontalLayoutWithCaption(final String label,
											 final Component... components) {
		super(label,
			  new HorizontalLayout());
		if (CollectionUtils.hasData(components)) _lyContainer.add(components);
	}
	public VaadinHorizontalLayoutWithCaption(final Icon icon,final String label,
											 final Component... components) {
		super(icon,label,
			  new HorizontalLayout());
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
	public void setVerticalComponentAlignment(final Alignment alignment, final Component... componentsToAlign) {
		_lyContainer.setVerticalComponentAlignment(alignment, componentsToAlign);
	}
	public Alignment getVerticalComponentAlignment(final Component component) {
		return _lyContainer.getVerticalComponentAlignment(component);
	}
	public void setDefaultVerticalComponentAlignment(final Alignment alignment) {
		_lyContainer.setDefaultVerticalComponentAlignment(alignment);
	}
	public Alignment getDefaultVerticalComponentAlignment() {
		return _lyContainer.getDefaultVerticalComponentAlignment();
	}
}
