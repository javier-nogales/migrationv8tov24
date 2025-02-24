package r01f.ui.vaadin.flow.components.window;

import java.io.Serializable;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.theme.lumo.LumoIcon;

import r01f.locale.I18NKey;
import r01f.ui.i18n.UII18NService;
import r01f.ui.vaadin.flow.VaadinIcons;
import r01f.ui.vaadin.flow.i18n.VaadinI18NMessagesCanBeUpdated;
import r01f.util.types.Strings;

/**
 * A window used for ask user permission to proceed with something
 * The window is like:
 * <pre>
 *     +--------------------------------------------+
 *     + Caption                                    +
 *     +--------------------------------------------+
 *     | Message (ie: delete??)                     |
 *     |                                            |
 *     |                                            |
 *     |                             [Cancel] [OK]  |
 *     +--------------------------------------------+
 * </pre>
 * Optionally a puzzle can be provides so the user must have to resolve the puzzle
 * to proceed:
 * <pre>
 *     +--------------------------------------------+
 *     + Caption                                    +
 *     +--------------------------------------------+
 *     | Message (ie: delete??)                     |
 *     |                                            |
 *     |                                            |
 *     | [puzzle txt          ]      [Cancel] [OK]  |
 *     +--------------------------------------------+
 * </pre>
 * The [OK] button is NOT enabled until the user solves the puzzle
 */
public class VaadinProceedGateDialogWindow 
	 extends Dialog 
  implements VaadinI18NMessagesCanBeUpdated {  
	
	private static final long serialVersionUID = 2953083925723451730L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	private final I18NKey _i18nKeyForCaption; 
	private final I18NKey _i18nKeyForMessage;
	private final Component _component;
	private final Object[] _paramsForMessage;
	private I18NKey _i18nKeyForBtnProceed = I18NKey.forId("yes");
	private I18NKey _i18nKeyForBtnNOTProceed = I18NKey.forId("no");
	
	private final Button _btnNOTProceed = new Button();
	private final Button _btnProceed = new Button();
	private final Label _lblWindowMessage = new Label();
	private final TextField _txtPuzzle = new TextField();
	private VaadinProceedPuzzleCheck _puzzleCheck;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinProceedGateDialogWindow(final UII18NService i18n,
									     final I18NKey i18nKeyForCaption,final I18NKey i18nKeyForMessage, 
									     // what happens when the user solves the puzzle
									     final VaadinProceedGateProceed proceed) {
		this(i18n,
			 i18nKeyForCaption,i18nKeyForMessage,
			 proceed,
			 null,		// opened listener
			 null);		// no puzzle
	}
	public VaadinProceedGateDialogWindow(final UII18NService i18n,
									     final I18NKey i18nKeyForCaption,final I18NKey i18nKeyForMessage, 
									     final VaadinProceedGateProceed proceed,
									     final VaadinProceedGateCancel cancel) {
		this(i18n,
			 i18nKeyForCaption,i18nKeyForMessage,
			 proceed,
			 cancel,
			 null);		// no puzzle
	}
	public VaadinProceedGateDialogWindow(final UII18NService i18n,
									     final I18NKey i18nKeyForCaption,final I18NKey i18nKeyForMessage, 
									     final VaadinProceedGateProceed proceed,
									     final VaadinProceedPuzzleCheck puzzleCheck) {
		this(i18n,
			 i18nKeyForCaption,i18nKeyForMessage, 
			 proceed,
			 null,		// closed listener
			 puzzleCheck);
	}	
	public VaadinProceedGateDialogWindow(final UII18NService i18n,
									     final I18NKey i18nKeyForCaption,final I18NKey i18nKeyForMessage, 
									     final VaadinProceedGateProceed proceed,
									     final VaadinProceedGateCancel cancel,
									     final VaadinProceedPuzzleCheck puzzleCheck) {
		this(i18n,
			i18nKeyForCaption,i18nKeyForMessage,null,
			proceed,
			cancel,
			puzzleCheck,
			(Object[])null);
	}
	public VaadinProceedGateDialogWindow(final UII18NService i18n,
									     final I18NKey i18nKeyForCaption,final I18NKey i18nKeyForMessage,final Component component,
									     final VaadinProceedGateProceed proceed,
									     final VaadinProceedGateCancel cancel,
									     final VaadinProceedPuzzleCheck puzzleCheck,
									     final Object... paramsForMessage) {
		this.setModal(true);
		this.setResizable(false);
		VaadinDialogWindows.addCloseButtonTo(this);
		
		_i18nKeyForCaption = i18nKeyForCaption;
		_i18nKeyForMessage = i18nKeyForMessage;
		_component = component;
		_paramsForMessage = paramsForMessage;
		
		_puzzleCheck = puzzleCheck;
		_initLayout(i18n);
		_initBehavior(proceed,
					  cancel);
	}
	public static void showProceedGateDialogWindow(final UII18NService i18n,
												   final VaadinProceedGateProceed proceed,
												   final VaadinProceedGateCancel cancel,
												   final VaadinProceedPuzzleCheck puzzle,
												   final String... text) {
		VaadinProceedGateDialogWindow dialog = (createProceedGateDialogWindow(i18n, 
				  															  proceed, 
				  															  cancel, 
				  															  puzzle, 
				  															  text));
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
		window.setupProceedButtonWith(I18NKey.forId((text != null && text.length > 2 
								   && Strings.isNOTNullOrEmpty(text[2]))
											? text[2]
											: "yes"),
									  VaadinIcons.createIconFor(VaadinIcon.CHECK));
		window.setupNOTProceedButtonWith(I18NKey.forId((text != null && text.length > 3 
									  && Strings.isNOTNullOrEmpty(text[3]))
											? text[3] 
											: "no"),
										 VaadinIcons.createIconFor(VaadinIcon.CLOSE_SMALL));
		window.updateI18NMessages(i18n);	// force update messages
		return window;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Creates the window structure
	 * @param message
	 */
	private void _initLayout(final UII18NService i18n) {
		// Header -----------------------
		this.setHeaderTitle(i18n.getMessage(_i18nKeyForCaption));
		
		// Message ----------------------
		// label
		_lblWindowMessage.setText(_paramsForMessage == null
										 ? i18n.getMessage(_i18nKeyForMessage)
										 : i18n.getMessage(_i18nKeyForMessage,_paramsForMessage));
		_lblWindowMessage.setWidthFull();
		
		// Component
		this.add(_lblWindowMessage);
		if (_component != null) this.add(_component);

		// Footer -----------------------
		// buttons
		_btnNOTProceed.setIcon(VaadinIcons.createIconFor(VaadinIcon.CLOSE_SMALL));
		_btnNOTProceed.setText(i18n.getMessage(_i18nKeyForBtnNOTProceed));
		
		_btnProceed.setIcon(VaadinIcons.createIconFor(LumoIcon.CHECKMARK));
		_btnProceed.setText(i18n.getMessage(_i18nKeyForBtnProceed));
		_btnProceed.addThemeVariants(ButtonVariant.LUMO_ERROR);
		
		// Puzzle
		_txtPuzzle.setValueChangeMode(ValueChangeMode.TIMEOUT);
		_txtPuzzle.setWidth(90, Unit.PERCENTAGE); 
		_txtPuzzle.setVisible(false);
		if (_puzzleCheck != null) {
			_btnProceed.setEnabled(false);	// the button is NOT enabled by default
			
			_txtPuzzle.setVisible(true);
			_txtPuzzle.focus();
			// only activate the operation button if the puzzle is solved
			_txtPuzzle.addValueChangeListener(valChangeEvent -> {
													String txt = valChangeEvent.getValue();
													boolean solved = _puzzleCheck.check(txt);
													_btnProceed.setEnabled(solved);
											  });
		}
		
		// Layout
		HorizontalLayout hLyButtons = new HorizontalLayout(_btnNOTProceed,_btnProceed);
		this.getFooter()
			.add(hLyButtons);
	}
	private void _initBehavior(final VaadinProceedGateProceed proceed,
							   final VaadinProceedGateCancel cancel) {
		_btnNOTProceed.addClickListener(clickEvent -> {
											if (cancel != null) cancel.cancel();
											this.close();
									    });
		_btnProceed.addClickListener(clickEvent -> {	// pass the event & close
										if (proceed != null) proceed.proceed();
										this.close();	
									});
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//	SHOW
/////////////////////////////////////////////////////////////////////////////////////////
	public void show() {
		this.open();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BUTTONS & MESSAGES 
/////////////////////////////////////////////////////////////////////////////////////////	
	public void setMessage(final String message) {
		_lblWindowMessage.setText(message);
	}
	public void setupProceedButtonWith(final I18NKey proceedBtnI18NKey) {
		this.setupProceedButtonWith(proceedBtnI18NKey,
									null);		// no icon
	}
	public void setupProceedButtonWith(final I18NKey proceedBtnI18NKey,
									   final Icon proceedBtnIcon) {
		_i18nKeyForBtnProceed = proceedBtnI18NKey;
		_btnProceed.setIcon(proceedBtnIcon);
	}
	public void setupNOTProceedButtonWith(final I18NKey notProceedBtnI18NKey) {
		this.setupNOTProceedButtonWith(notProceedBtnI18NKey,
									   null);	// no icon
	}
	public void setupNOTProceedButtonWith(final I18NKey notProceedBtnI18NKey,
										  final Icon notProceedBtnIcon) {
		_i18nKeyForBtnNOTProceed = notProceedBtnI18NKey;
		_btnNOTProceed.setIcon(notProceedBtnIcon);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public void updateI18NMessages(final UII18NService i18n) {
		this.setHeaderTitle(i18n.getMessage(_i18nKeyForCaption));
		_lblWindowMessage.setText(i18n.getMessage(_i18nKeyForMessage));
		_btnProceed.setText(i18n.getMessage(_i18nKeyForBtnProceed));
		_btnNOTProceed.setText(i18n.getMessage(_i18nKeyForBtnNOTProceed));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EVENT                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@FunctionalInterface
	public interface VaadinProceedGateProceed 
	  		 extends Serializable {
		void proceed();
	}
	@FunctionalInterface
	public interface VaadinProceedGateCancel 
	  		 extends Serializable {
		void cancel();
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//	PUZZLE CHECK
/////////////////////////////////////////////////////////////////////////////////////////
	@FunctionalInterface
	public interface VaadinProceedPuzzleCheck {
		public boolean check(final String text);
	}
	public void setProceedPuzzleCheck(final VaadinProceedPuzzleCheck check) {
		_puzzleCheck = check;
		if (_puzzleCheck != null) {
			_btnProceed.setEnabled(false);	// the button is NOT enabled by default
			
			_txtPuzzle.clear();
			_txtPuzzle.setVisible(true);
			_txtPuzzle.focus();
			// only activate the operation button if the puzzle is solved
			_txtPuzzle.addValueChangeListener(valChangeEvent -> {
													String txt = valChangeEvent.getValue();
													boolean solved = _puzzleCheck.check(txt);
													_btnProceed.setEnabled(solved);
											  });
		}
		else {
			_btnProceed.setEnabled(false);
			_txtPuzzle.setVisible(false);
		}
	}
}