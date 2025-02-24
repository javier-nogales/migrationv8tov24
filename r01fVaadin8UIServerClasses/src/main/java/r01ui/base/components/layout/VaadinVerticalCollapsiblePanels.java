package r01ui.base.components.layout;

import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;


/**
 * A vertical collapsible panel like
 * <pre>
 * 		+=========================================+
 * 		| [ component 1                       \/] |
 * 		| --------------------------------------- |
 * 		| [ component 2                       \/] |
 * 		| --------------------------------------- |
 * 		| [ component 2                       \/] |
 * 		+=========================================+
 * </pre>
 */
public class VaadinVerticalCollapsiblePanels 
	 extends CssLayout {

	private static final long serialVersionUID = -3529682141851791117L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	private final HorizontalLayout _header;
	
	public VaadinVerticalCollapsiblePanels(final Label lblTitle,
									  	   final Component... components) {
		this(lblTitle,
			 true,
			 components);
	}
	public VaadinVerticalCollapsiblePanels(final Label lblTitle,
									  	   final boolean collapsable,
									  	   final Component... components) {
		this.addStyleName(ValoTheme.LAYOUT_CARD);		
		this.setWidthFull();
		
		Label lblForOpenCloseIcon = new Label();
		if (collapsable) {
			lblForOpenCloseIcon.setIcon(VaadinIcons.CARET_DOWN);
			// initialize visible to false
			for (Component component : components) {
				component.setVisible(false);
			}		
		}
		lblTitle.addStyleNames(ValoTheme.LABEL_H4,
							   ValoTheme.LABEL_COLORED, 
							   ValoTheme.LABEL_NO_MARGIN);		
		
		_header = new HorizontalLayout(lblTitle,lblForOpenCloseIcon);
		_header.setExpandRatio(lblTitle, 1);
		_header.setMargin(true);		
		_header.setWidthFull();		
		_header.setComponentAlignment(lblForOpenCloseIcon,Alignment.MIDDLE_RIGHT);
		
		if (collapsable) {
			_header.addLayoutClickListener(clickEvent -> {
												for (Component component : components) {
													component.setVisible(!component.isVisible());
												}
												lblForOpenCloseIcon.setIcon(lblForOpenCloseIcon.getIcon().equals(VaadinIcons.CARET_DOWN) 
																				  ? VaadinIcons.CARET_UP  
																				  : VaadinIcons.CARET_DOWN);
										  });	// toggle form visibility		
		}
		this.addComponent(_header);
		this.addComponents(components);
	}
	public void setHeaderLayoutClickListener(final LayoutClickListener listener) {
		_header.addLayoutClickListener(listener);
	}
}
