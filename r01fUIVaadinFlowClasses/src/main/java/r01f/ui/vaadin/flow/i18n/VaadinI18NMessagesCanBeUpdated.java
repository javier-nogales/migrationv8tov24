package r01f.ui.vaadin.flow.i18n;

import r01f.ui.i18n.UII18NService;

public interface VaadinI18NMessagesCanBeUpdated {
	 default void updateI18NMessages(final UII18NService i18n) {
		 // just do nothing
	 }
}
