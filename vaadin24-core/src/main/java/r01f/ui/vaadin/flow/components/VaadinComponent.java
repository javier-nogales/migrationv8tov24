package r01f.ui.vaadin.flow.components;

import com.vaadin.flow.component.Component;

public interface VaadinComponent {
	/**
	 * As component
	 * @param <C>
	 * @param component
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public default <C extends Component> C asVaadinComponent(final Class<C> component) {
		return (C)this;
	}
}
