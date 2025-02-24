package r01f.ui.vaadin.flow.notifications;



import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import r01f.patterns.FactoryFrom;
import r01f.ui.i18n.UII18NService;
import r01f.ui.vaadin.flow.view.VaadinView;
import r01f.util.types.collections.CollectionUtils;

/**
 * A window that shows application [notifications]
 * 
 * Ensure to include the [r01uinotifications.scss] styles file:
 * [1] - Go to the UI type and check the @Theme annotation
 * 		 It should be something like:
 *		        @Theme("myAppStyles")	
 *		        public class MyAppVaadinUI
 *		        	 extends UI {
 *		        	...
 *		        }
 * [2] - There must exist this folder: [WebContent]/VAADIN/themes/myAppStyles  <-- the @Theme annotation value
 * 		 The folder's contents should be:
 * 			[WebContent]/VAADIN/themes/myAppStyles
 * 				+ styles.scss
 * 				+ addons.scss
 * 				+ favicon.ico
 * 				+ myAppStyles.scss  		<-- the @Theme annotation value
 * 				+ r01uinotifications.scss	<-- this file contains the header styles
 * [3] - The [styles.scss] file must contain:
 *			@import "addons.scss";
 *			@import "myAppStyles.scss";
 *			@import "r01uinotifications.scss";	
 *
 *			.myAppStyles {
 *				@include addons;
 *				@include myAppStyles;	<-- the @Theme annotation value
 *				@include r01uiheader;		
 *			} 
 */
public abstract class VaadinNotificationsWindow<N extends VaadinNotificationViewObject> 
	 		  extends Composite<Div>
	 	   implements VaadinView {

	private static final long serialVersionUID = 5257147074747528270L;
/////////////////////////////////////////////////////////////////////////////////////////
//	SERVICES																		  
/////////////////////////////////////////////////////////////////////////////////////////
	private final transient VaadinNotificationsWindowPresenter<N> _notifWinPresenter;
	private final transient FactoryFrom<N,Component> _notifViewComponentFactory;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	UI
/////////////////////////////////////////////////////////////////////////////////////////	
	private Dialog _notifsWin;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR														  
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinNotificationsWindow(final UII18NService i18n,
									 final VaadinNotificationsWindowPresenter<N> notifWinPresenter,
									 final FactoryFrom<N,Component> notifViewComponentFactory) {
		super();
		
		_notifWinPresenter = notifWinPresenter;
		_notifViewComponentFactory = notifViewComponentFactory;
		
		Button btn = _createNotificationsButton(i18n);
		this.getContent()
			.add(btn);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BUTTON																		  
/////////////////////////////////////////////////////////////////////////////////////////
	private Button _createNotificationsButton(final UII18NService i18n) {
		Button btn = new Button(new Icon(VaadinIcon.BELL));
		_notifWinPresenter.onNotificationsCountNeeded(count -> btn.setText(Integer.toString(count)));
		btn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btn.addClassNames("notifications",
						  "unread");				
		btn.addClickListener(event -> _openNotificationsWindow(i18n,
															   event.getClientX(),event.getClientX(),
															   event.getClientY(),event.getClientY()));
		return btn;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	NOTIFICATIONS WINDOW																		  
/////////////////////////////////////////////////////////////////////////////////////////
	private void _openNotificationsWindow(final UII18NService i18n,
										  final int cliX,final int relX,
										  final int cliY,final int relY) {
		if (_notifsWin == null) _notifsWin = _createNotificationsDialog(i18n);
		_notifsWin.open();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	NOTIFICATIONS CONTENT																		  
/////////////////////////////////////////////////////////////////////////////////////////
	private Dialog _createNotificationsDialog(final UII18NService i18n) {
		Dialog outDlg = new Dialog();
		outDlg.setWidth(300.0f,Unit.PIXELS);
		outDlg.setResizable(false);
		outDlg.setDraggable(false);
		
		Component notifs = _createNotificationsWindowContent(i18n);
		outDlg.add(notifs);
		
		return outDlg;
	}
	private Component _createNotificationsWindowContent(final UII18NService i18n) {
		VerticalLayout vLayoutWin = new VerticalLayout();
		
		// title
		//TODO: implements "VaadinViewI18NMessagesCanBeUpdated"
		H3 lblTitle = new H3(i18n.getMessage("notifications"));
		vLayoutWin.add(lblTitle);

		// notifications
		_notifWinPresenter.onNotificationsLoadNeeded(i18n.getCurrentLanguage(),
													 notifs -> {
														if (CollectionUtils.isNullOrEmpty(notifs)) {
															// TODO print a message
														}
														else {
															// Create view components for each notification view object
															Component[] compNotifs = notifs.stream()
																						   .map(notifViewObj -> {// crate a view component with the view object data
																									Component notifViewComp = _notifViewComponentFactory.from(notifViewObj);
																									return notifViewComp;
																				  				})
																						  .toArray(Component[]::new);
															// put all view components in a VerticalLayout
															VerticalLayout vlayoutNotifs = new VerticalLayout(compNotifs);
															
															// Add to the window layout
															vLayoutWin.add(vlayoutNotifs);
														} 
													});
		// footer
		HorizontalLayout hLayoutFooter = new HorizontalLayout();
		hLayoutFooter.setWidthFull();
		hLayoutFooter.setSpacing(false);
		
		Button btnShowAll = _buildShowAllButton(i18n);
		hLayoutFooter.add(btnShowAll);
		hLayoutFooter.setVerticalComponentAlignment(Alignment.CENTER,btnShowAll);
		
		// return 
		return vLayoutWin;
	}
	private static Button _buildShowAllButton(final UII18NService i18n) {
		Button btnShowAll = new Button(i18n.getMessage("notifs.seeAll"),
									   e -> Notification.show("NOT available (to be implemented)"));
		btnShowAll.addThemeVariants(ButtonVariant.LUMO_SMALL);
		return btnShowAll;
	}
}
