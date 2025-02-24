package r01f.ui.vaadin.flow.i18n;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation defines the message manager for a specific message bundle. 
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VaadinI18NMessageBundle {
	/**
	 * The bundle basename.
	 */
    String[] basename();

}
