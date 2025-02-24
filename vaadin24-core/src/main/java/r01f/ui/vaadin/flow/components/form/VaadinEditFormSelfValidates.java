package r01f.ui.vaadin.flow.components.form;

/**
 * An interface to be implemented at {@link VaadinDetailForm} instances to do any validation
 */
public interface VaadinEditFormSelfValidates {
	public default boolean validate() {
		return true;
	}
}
