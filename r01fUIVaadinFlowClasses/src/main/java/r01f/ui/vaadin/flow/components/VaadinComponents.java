package r01f.ui.vaadin.flow.components;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;

import com.google.common.collect.Lists;
import com.google.common.reflect.Reflection;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasOrderedComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.combobox.ComboBoxBase;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.AbstractNumberField;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.reflection.ReflectionUtils;
import r01f.util.types.collections.CollectionUtils;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class VaadinComponents {
/////////////////////////////////////////////////////////////////////////////////////////
//	STYLING                                                                       
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Sets the CSS class names of this component. 
	 * This method overwrites any previous set class names
	 * @param styleName
	 * @param components
	 */
	public static void setStyleName(final String styleName,
						   			final HasStyle... components) {
		if (CollectionUtils.isNullOrEmpty(components)) return;
		for (HasStyle comp : components) {
			comp.setClassName(styleName);
		}
	}
	/**
	 * Sets or removes the given class name for this component.
	 * This method overwrites any previous set class names
	 * @param styleName
	 * @param add true add, false removes
	 * @param components
	 */
	public static void setStyleName(final String styleName,final boolean add,
						   			final HasStyle... components) {
		if (CollectionUtils.isNullOrEmpty(components)) return;
		for (HasStyle comp : components) {
			comp.setClassName(styleName,add);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Wrapping text in grid row
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Wraps a text into an {@link HorizontalLayout}
	 * @param value
	 * @return
	 */
	public static final HorizontalLayout wrapIntoHorizontalLayout(final String value) {
		Span label = new Span();
		label.setText(value);
		label.setSizeFull();

		HorizontalLayout outLy = new HorizontalLayout(label);
		outLy.setSizeFull();
		return outLy;
	}
	/**
	 * Wraps a text into a CSS layout
	 * @param value
	 * @return
	 */
	public static final Div wrapIntoCssLayout(final String value) {
		Span label = new Span();
		label.setText(value);
		label.setSizeFull();
		
		Div outLy = new Div(label);
		outLy.setSizeFull();
		return outLy;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	HasOrderedComponents
/////////////////////////////////////////////////////////////////////////////////////////
	public static Iterator<Component> getComponentIteratorOf(final HasOrderedComponents hasComponents) {
		return hasComponents.getChildren().iterator();
	}
	/**
	 * Finds a component matching a filter 
	 * @param hasComponents
	 * @param matcher
	 * @return
	 */
	public static final Collection<Integer> findIndexOfComponentsMatching(final HasOrderedComponents hasComponents,
														  				  final Predicate<Component> matcher) {
		Collection<Integer> outCompsIdxs = null;
		int index = 0;
		for (Iterator<Component> compsIt = hasComponents.getChildren().iterator(); compsIt.hasNext(); ) {
			Component comp = compsIt.next();
			boolean matches = matcher.test(comp);
			if (matches) {
				outCompsIdxs = Lists.newArrayList();
				outCompsIdxs.add(index);
			}
			index = index + 1;
		}
		return outCompsIdxs;
	}
	/**
	 * Finds the first component that matches the given filter
	 * @param hasComponents
	 * @param matcher
	 * @return
	 */
	public static final int findIndexOfFirstComponentMatching(final HasOrderedComponents hasComponents,
															  final Predicate<Component> matcher) {
		Collection<Integer> compsIdxs = VaadinComponents.findIndexOfComponentsMatching(hasComponents,
																					   matcher);
		return CollectionUtils.hasData(compsIdxs) ? compsIdxs.stream().findFirst().orElse(null) : -1;
	}
	/**
	 * Finds a component matching a filter 
	 * @param <C>
	 * @param hasComponents
	 * @param matcher
	 * @return
	 */
	public static final Collection<Component> findComponentsMatching(final HasOrderedComponents hasComponents,
																	 final Predicate<Component> matcher) {
		Collection<Component> outComps = null;
		for (Iterator<Component> compsIt = hasComponents.getChildren().iterator(); compsIt.hasNext(); ) {
			Component comp = compsIt.next();
			boolean matches = matcher.test(comp);
			if (matches) {
				if (outComps == null) outComps = Lists.newArrayList();
				outComps.add(comp);
			}
		}
		return outComps;
	}
	@SuppressWarnings("unchecked")
	public static final <C extends Component> C findFirstComponentMatchin(final HasOrderedComponents hasComponent,
																		  final Predicate<Component> matcher) {
		Collection<Component> comps = VaadinComponents.findComponentsMatching(hasComponent,
																			  matcher);
		return CollectionUtils.hasData(comps) ? (C)comps.stream().findFirst().orElse(null) : null;
 	}
	/**
	 * Removes a component that matches the filter
	 * @param hasComponents
	 * @param matcher
	 * @return
	 */
	public static final boolean removeComponentsMatching(final HasOrderedComponents hasComponents,
														 final Predicate<Component> matcher) {
		Collection<Component> compsMatching = VaadinComponents.findComponentsMatching(hasComponents,
																					  matcher);
		if (CollectionUtils.isNullOrEmpty(compsMatching)) return false;
		compsMatching.forEach(hasComponents::remove);
		return true;	
	}
	/**
	 * Removes the component at the given position
	 * @param hasComponents
	 * @param index
	 * @return
	 */
	public static final boolean removeComponentAt(final HasOrderedComponents hasComponents,
												  final int index) {
		Component comp = hasComponents.getComponentAt(index);
		if (comp == null) return false;
		hasComponents.remove(comp);
		return true;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Vaadin Flow does NOT define an interface for components having a placeholder
	 * So a dynamic proxy trick is used:
	 * <pre class='brush:java'>
	 * 		TextField myTextField = new TextField();
	 * 		VaadinComponentHasPlaceholder hasPlaceHolder = VaadinComponents.asHasPlaceholder(myTextField);
	 * 		hasPlaceHolder.setPlaceholder("enter text...");
	 * </pre>
	 * Obviously it the vaading component is known this is NOT needed to set the placeholder
	 * because it can be set directly:
	 * <pre class='brush:java'>
	 * 		TextField myTextField = new TextField();
	 * 		myTextField.setPlaceholder("enter text...");
	 * </pre>
	 * This utility is helpful when an unknown component type is received
	 * @param <C>
	 * @param component
	 * @return
	 */
	public static <C extends Component> VaadinComponentHasPlaceholder asHasPlaceholder(final C component) {
		VaadinHasPlaceholderInvocationHandler<C> handler = new VaadinHasPlaceholderInvocationHandler<>(component);
		return Reflection.newProxy(VaadinComponentHasPlaceholder.class,
								   handler);
	}
	public interface VaadinComponentHasPlaceholder {
		public void setPlaceholder(final String placeholder);
		public String getPlaceholder();
	}
	private static class VaadinHasPlaceholderInvocationHandler<C extends Component>
	   		  implements InvocationHandler {

		private final C _component;
		private final boolean _hasPlaceholder;
		
		private VaadinHasPlaceholderInvocationHandler(final C component) {
			if (!(component instanceof TextField)
			 || !(component instanceof EmailField)
			 || !(component instanceof PasswordField)
			 || !(component instanceof AbstractNumberField || !(component instanceof BigDecimalField))
			 || !(component instanceof DatePicker) || !(component instanceof DateTimePicker)
			 || !(component instanceof ComboBoxBase)) {
				_hasPlaceholder = true;
				// throw new IllegalArgumentException("cannot set [place holder] on " + component.getClass());
			} else {
				_hasPlaceholder = false;
			}
			_component = component;
		}
		@Override
		public Object invoke(final Object proxy,final Method method,final Object[] args) throws Throwable {
			if (!_hasPlaceholder) return null;
			
			Object out = null;
			if (method.getName().equals("setPlaceholder")) {
				out = ReflectionUtils.method(_component.getClass(),"setPlaceholder",new Class<?>[] { String.class })
									 .invoke(_component,args);
			} else if (method.getName().equals("getPlaceholder")) {
				out = ReflectionUtils.method(_component.getClass(),"getPlaceholder",new Class<?>[] { })
									 .invoke(_component,args);
			}
			return out;
		}
	}
}
