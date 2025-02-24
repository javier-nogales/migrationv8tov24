package r01f.ui.vaadin.flow.components.window;

import com.vaadin.flow.component.icon.VaadinIcon;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.locale.I18NKey;
import r01f.ui.i18n.UII18NService;
import r01f.ui.vaadin.flow.VaadinIcons;
import r01f.ui.vaadin.flow.components.window.VaadinProceedGateDialogWindow.VaadinProceedGateCancel;
import r01f.ui.vaadin.flow.components.window.VaadinProceedGateDialogWindow.VaadinProceedGateProceed;
import r01f.ui.vaadin.flow.components.window.VaadinProceedGateDialogWindow.VaadinProceedPuzzleCheck;
import r01f.util.types.Strings;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class VaadinProceedGateDialogWindows {
/////////////////////////////////////////////////////////////////////////////////////////
//	PROCEED WINDOW	
/////////////////////////////////////////////////////////////////////////////////////////
	public static void showProceedGateDialogWindow(final UII18NService i18n,
												   final VaadinProceedGateProceed proceed) {
		VaadinProceedGateDialogWindows.showProceedGateDialogWindow(i18n,
													  			   proceed,
													  			   null,		// cancel
													  			   null);		// puzzle
	}
	public static void showProceedGateDialogWindow(final UII18NService i18n,
												   final VaadinProceedGateProceed proceed,
												   final VaadinProceedGateCancel cancel,
												   final VaadinProceedPuzzleCheck puzzle,
												   final String... text) {

		VaadinProceedGateDialogWindow dialog = VaadinProceedGateDialogWindows.createProceedGateDialogWindow(i18n, 
				  												     			  							proceed,
				  												     			  							cancel, 
				  												     			  							puzzle, 
				  												     			  							text);
		dialog.show();
	}
	public static VaadinProceedGateDialogWindow createProceedGateDialogWindow(final UII18NService i18n,
																			  final VaadinProceedGateProceed proceed,
																			  final VaadinProceedGateCancel cancel,
																			  final VaadinProceedPuzzleCheck puzzle,
																			  final String... text) {
		VaadinProceedGateDialogWindow window = new VaadinProceedGateDialogWindow(i18n,
																				 I18NKey.forId((text != null && text.length > 0 && Strings.isNOTNullOrEmpty(text[0])) ? text[0] : "window.operation.confirm.title"),
																		 		 I18NKey.forId((text != null && text.length > 1 && Strings.isNOTNullOrEmpty(text[1])) ? text[1] : "window.operation.data.changes"),
																				 proceed,
																				 cancel,
																				 puzzle);
		window.setupProceedButtonWith(I18NKey.forId((text != null && text.length > 2 && Strings.isNOTNullOrEmpty(text[2]))
											? text[2]
											: "yes"),
									  VaadinIcons.createIconFor(VaadinIcon.CHECK));
		window.setupNOTProceedButtonWith(I18NKey.forId((text != null && text.length > 3 && Strings.isNOTNullOrEmpty(text[3]))
											? text[3] 
											: "no"),
										 VaadinIcons.createIconFor(VaadinIcon.CLOSE_SMALL));
		window.updateI18NMessages(i18n);	// force update messages
		return window;
	}
}