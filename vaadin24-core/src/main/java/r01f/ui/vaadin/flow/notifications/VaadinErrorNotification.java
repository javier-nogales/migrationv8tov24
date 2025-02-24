package r01f.ui.vaadin.flow.notifications;

import com.google.common.base.Throwables;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import r01f.ui.vaadin.flow.i18n.VaadinI18NMessagesCanBeUpdated;
import r01f.util.types.Strings;

/**
 * A notification error component like:
 * <pre class='brush:java'>
 * 		+===============================+
 * 		| [icon] Header         [close] |
 * 		| Details (ie exception)        |
 * 		+===============================+
 * </pre>
 */
public class VaadinErrorNotification 
	 extends Notification 
  implements VaadinI18NMessagesCanBeUpdated {

	private static final long serialVersionUID = 465795953872732478L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
    private static final int DEFAULT_DURATION = 5000;
    private static final Position DEFAULT_POSITION = Position.BOTTOM_START;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final Label _lblHeaderTitle;
	private final Label _lblDetails;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinErrorNotification() {
		this("ERROR","",	// no title / no details
			 DEFAULT_DURATION,DEFAULT_POSITION);
	}
	public VaadinErrorNotification(final String title,final Throwable th) {
		this(title,Throwables.getStackTraceAsString(th),
			 DEFAULT_DURATION,DEFAULT_POSITION);
	}
	public VaadinErrorNotification(final String title,final Throwable th,
								   final int duration,final Position position) {
		this(title,Throwables.getStackTraceAsString(th),
			 duration,position);
	}
	public VaadinErrorNotification(final String title,final String details) {
		this(title,details,
			 DEFAULT_DURATION,DEFAULT_POSITION);
	}
	public VaadinErrorNotification(final String title,final String details,
								   final int duration,final Position position) {
		// [1] - Header
		// a) icon
		Icon icon = VaadinIcon.WARNING.create();
		// b) header
		_lblHeaderTitle = new Label(title);
		// c) close button
		Button btnClose = new Button(new Icon("lumo","cross"));
		btnClose.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		btnClose.getElement().setAttribute("aria-label","Close");
		
		btnClose.addClickListener(clickEvent -> {
		    							this.close();
								  });
		// d) header layout
		HorizontalLayout hlyHeader = new HorizontalLayout(icon,_lblHeaderTitle,btnClose);
		hlyHeader.setAlignItems(Alignment.CENTER);
		
		// [2] - Detail
		_lblDetails = new Label(details != null ? details : "");
		_lblDetails.setVisible(Strings.isNOTNullOrEmpty(details));
		
		// [3] - Layout
		VerticalLayout layout = new VerticalLayout(hlyHeader,
												   _lblDetails);
		this.add(layout);
		
		// [3] - style
		this.addThemeVariants(NotificationVariant.LUMO_ERROR);
		this.setDuration(duration);
		this.setPosition(position);
	}
	public static VaadinErrorNotification show(final String text,final Throwable th) { 
		return new VaadinErrorNotification(text,th);
	}
	public static VaadinErrorNotification show(final String text,final Throwable th,
											   final int duration,final Position position) { 
		return new VaadinErrorNotification(text,th,
										   duration,position);
	}
	public static VaadinErrorNotification show(final String text,final String details) { 
		return new VaadinErrorNotification(text,details);
	}
	public static VaadinErrorNotification show(final String text,final String details,
											   final int duration,final Position position) { 
		return new VaadinErrorNotification(text,details,
										   duration,position);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TITLE & DETAILS
/////////////////////////////////////////////////////////////////////////////////////////
	public String getTitle() {
		return _lblHeaderTitle.getText();
	}
	public void setTitle(final String text) {
		_lblHeaderTitle.setText(text);
	}
	public String getDetails() {
		return _lblDetails.getText();
	}
	public void setDetails(final String details) {
		_lblDetails.setText(details);
		_lblDetails.setVisible(Strings.isNOTNullOrEmpty(details));
	}
}
