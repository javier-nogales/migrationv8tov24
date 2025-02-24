package r01f.ui.vaadin.flow.components.window;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.theme.lumo.LumoIcon;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.ui.vaadin.flow.VaadinIcons;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class VaadinDialogWindows {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public static void addCloseButtonTo(final Dialog dialog) {
		// create the button
		Button btnClose = new Button(VaadinIcons.createIconFor(LumoIcon.CROSS),
		        					 clickEvent -> dialog.close());
		btnClose.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		
		// add to the header
		dialog.getHeader()
			  .add(btnClose);
	}
}
