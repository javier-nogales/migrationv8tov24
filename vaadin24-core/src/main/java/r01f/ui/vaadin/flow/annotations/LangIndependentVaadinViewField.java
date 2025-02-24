package r01f.ui.vaadin.flow.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.vaadin.flow.component.HasValue;




/**
 * Sets a ui view field implementing {@link HasValue} as a language independent field
 * This annotation is used at language dependent views (see {@link VaadinUILangTabbedViewBase})
 * so when one of these fields is changed in any of the languages, the other languages 
 * are updated accordingly
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface LangIndependentVaadinViewField {
	// just a marker annotation
}
