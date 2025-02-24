package r01ui.base.components.tree;

import java.lang.reflect.Method;

import com.vaadin.event.SerializableEventListener;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;

public interface VaadinChangingTree {
/////////////////////////////////////////////////////////////////////////////////////////
//	EVENTS
/////////////////////////////////////////////////////////////////////////////////////////
	public class TreeChangeEvent
		 extends Component.Event {
		private static final long serialVersionUID = -2155625992746613086L;

		public TreeChangeEvent(final Component source) {
			super(source);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LISTENERS
/////////////////////////////////////////////////////////////////////////////////////////
	@FunctionalInterface
	public interface TreeChangeListener
			 extends SerializableEventListener {

		public static final Method TREE_CHANGE_METHOD = ReflectTools.findMethod(TreeChangeListener.class,
																				"treeChange",
																				TreeChangeEvent.class);

		public void treeChange(TreeChangeEvent event);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public Registration addTreeChangeListener(TreeChangeListener listener);
}
