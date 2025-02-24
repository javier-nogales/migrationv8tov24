package r01f.ui.vaadin.flow;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.theme.lumo.LumoIcon;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class VaadinIcons {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static Icon createIconFor(final VaadinIcon vaadinIcon) {
		return vaadinIcon.create();
	}
	public static Icon createIconFor(final LumoIcon lumoIcon) {
		return lumoIcon.create();
	}
}
