package r01f.ui.vaadin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;

import com.google.common.collect.Lists;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.util.types.collections.CollectionUtils;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class VaadinComponents {
/////////////////////////////////////////////////////////////////////////////////////////
//	STYLING                                                                       
/////////////////////////////////////////////////////////////////////////////////////////	
	public static void setStyleName(final String styleName,
						   			final AbstractComponent... components) {
		if (CollectionUtils.isNullOrEmpty(components)) return;
		for (AbstractComponent comp : components) {
			comp.setStyleName(styleName);
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
		HorizontalLayout outLy = new HorizontalLayout();
		outLy.setSizeFull();
		
		Label label = new Label();
		label.setContentMode(ContentMode.HTML);
		label.setValue(value);
		label.setSizeFull();
		label.setStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
		outLy.addComponent(label);
		return outLy;
	}
	/**
	 * Wraps a text into a CSS layout
	 * @param value
	 * @return
	 */
	public static final CssLayout wrapIntoCssLayout(final String value) {
		CssLayout outLy = new CssLayout();
		outLy.setSizeFull();
		
		Label label = new Label();
		label.setContentMode(ContentMode.HTML);
		label.setValue(value);
		label.setSizeFull();
		label.setStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
		outLy.addComponent(label);
		return outLy;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	HasComponent: find index
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds a component matching a filter 
	 * @param hasComponents
	 * @param matcher
	 * @return
	 */
	public static final Collection<Integer> findIndexOfComponentsMatching(final HasComponents hasComponents,
														  				  final Predicate<Component> matcher) {
		Collection<Integer> outCompsIdxs = null;
		int index = 0;
		for (Iterator<Component> compsIt = hasComponents.iterator(); compsIt.hasNext(); ) {
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
	public static final int findIndexOfFirstComponentMatching(final HasComponents hasComponents,
															  final Predicate<Component> matcher) {
		Collection<Integer> compsIdxs = VaadinComponents.findIndexOfComponentsMatching(hasComponents,
																					   matcher);
		return CollectionUtils.hasData(compsIdxs) ? compsIdxs.stream().findFirst().orElse(null) : -1;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	HasComponent: find
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Finds a component matching a filter 
	 * @param <C>
	 * @param hasComponents
	 * @param matcher
	 * @return
	 */
	public static final Collection<Component> findComponentsMatching(final HasComponents hasComponents,
																	 final Predicate<Component> matcher) {
		Collection<Component> outComps = null;
		for (Iterator<Component> compsIt = hasComponents.iterator(); compsIt.hasNext(); ) {
			Component comp = compsIt.next();
			boolean matches = matcher.test(comp);
			if (matches) {
				if (outComps == null) outComps = Lists.newArrayList();
				outComps.add(comp);
			}
		}
		return outComps;
	}
	/**
	 * Finds the FIRST component matching a filter 
	 * @param <C>
	 * @param hasComponent
	 * @param matcher
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static final <C extends Component> C findFirstComponentMatching(final HasComponents hasComponent,
																		   final Predicate<Component> matcher) {
		Component outComp = null;
		for (Iterator<Component> compsIt = hasComponent.iterator(); compsIt.hasNext(); ) {
			Component comp = compsIt.next();
			boolean matches = matcher.test(comp);
			if (matches) {
				outComp = comp;
				break;	// no more!
			}
		}
		return (C)outComp;
 	}
	/**
	 * Recursively finds the components matching a filter
	 * @param hasComponents
	 * @param matcher
	 * @return
	 */
	public static final Collection<Component> recurseFindComponentsMatching(final HasComponents hasComponents,
																			final Predicate<Component> matcher) {
		Collection<Component> accum = new ArrayList<>();
		_recurseAccumulateComponentsMatching(hasComponents,
											 matcher,
											 accum);
		return accum;
	}
	private static void _recurseAccumulateComponentsMatching(final HasComponents hasComponents,
													  		 final Predicate<Component> matcher,
													  		 final Collection<Component> accum) {
		for (Iterator<Component> compsIt = hasComponents.iterator(); compsIt.hasNext(); ) {
			Component comp = compsIt.next();
			boolean matches = matcher.test(comp);
			if (matches) {
				accum.add(comp);
			}
			if (comp instanceof HasComponents) {
				// BEWARE!! recursion
				_recurseAccumulateComponentsMatching((HasComponents)comp,
													 matcher,
													 accum);
			}
		}
	}
	/**
	 * Recursively finds the components matching a filter
	 * @param hasComponents
	 * @param matcher
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static final <C extends Component> C recurseFindFirstComponentMatching(final HasComponents hasComponents,
																	 			  final Predicate<Component> matcher) {
		Component outComp = null;
		for (Iterator<Component> compsIt = hasComponents.iterator(); compsIt.hasNext(); ) {
			Component comp = compsIt.next();
			boolean matches = matcher.test(comp);
			if (matches) {
				outComp = comp;
				break; // no more!
			}
			if (comp instanceof HasComponents) {
				// BEWARE!! recursion
				outComp = VaadinComponents.recurseFindFirstComponentMatching((HasComponents)comp,
																			 matcher);
				if (outComp != null) break;	// no more!
			}
		}
		return (C)outComp;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	HasComponent: remove
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Removes a component that matches the filter
	 * @param hasComponents
	 * @param matcher
	 * @return
	 */
	public static final boolean removeComponentsMatching(final ComponentContainer hasComponents,
														 final Predicate<Component> matcher) {
		Collection<Component> compsMatching = VaadinComponents.findComponentsMatching(hasComponents,
																					  matcher);
		if (CollectionUtils.isNullOrEmpty(compsMatching)) return false;
		compsMatching.forEach(hasComponents::removeComponent);
		return true;	
	}
	/**
	 * Removes the component at the given position
	 * @param hasComponents
	 * @param index
	 * @return
	 */
	public static final boolean removeComponentAt(final AbstractOrderedLayout hasComponents,
												  final int index) {
		Component comp = hasComponents.getComponent(index);
		if (comp == null) return false;
		hasComponents.removeComponent(comp);
		return true;
	}
}
