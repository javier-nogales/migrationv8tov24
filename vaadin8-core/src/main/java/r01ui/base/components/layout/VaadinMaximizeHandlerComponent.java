package r01ui.base.components.layout;

import java.lang.reflect.Method;

import com.vaadin.event.SerializableEventListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.util.ReflectTools;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.ui.i18n.UII18NService;
import r01f.ui.vaadin.styles.VaadinValoTheme;

/**
 * A component that handles the expansion (maximize) of ANOTHER component
 * <pre>
 * 		+-----------------------------------------------+
 * 		|			       [maximize size/restore size] |
 * 		+-----------------------------------------------+
 * 		(usually the [subject component] is here)	<-- when the [maximize] button is clicked, the [subject component]
 * 														EXPANDS its size
 * </pre>
 */
public class VaadinMaximizeHandlerComponent<C extends Component> 
     extends CustomComponent {

	private static final long serialVersionUID = 7217202019508583752L;
/////////////////////////////////////////////////////////////////////////////////////////
//	UI
/////////////////////////////////////////////////////////////////////////////////////////	
	private final C _subjectComponent;
	
	private final Button _btnMaximizeSize;
	private final Button _btnRestoreSize;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	COMMAND TO PERFORM WHEN MAXIMIZE / RESTORE SIZE
/////////////////////////////////////////////////////////////////////////////////////////	
	private final R01UIMaximizeSizeCmd<C> _maximizeSizeCmd;
	private final R01UIRestoreSizeCmd<C> _restoreSizeCmd;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public VaadinMaximizeHandlerComponent(final UII18NService i18n,
						   		 	 	  final C subjectComponent,
						   		 	 	  final R01UIMaximizeSizeCmd<C> maximizeSizeCmd,final R01UIRestoreSizeCmd<C> restoreSizeCmd) {
		_subjectComponent = subjectComponent;
		
		_maximizeSizeCmd = maximizeSizeCmd;
		_restoreSizeCmd = restoreSizeCmd;
		
		_btnMaximizeSize = new Button(VaadinIcons.EXPAND);
		_btnMaximizeSize.setDescription(i18n.getMessage("maximize.size"));
		_btnRestoreSize = new Button(VaadinIcons.COMPRESS);
		_btnRestoreSize.setDescription(i18n.getMessage("restore.size"));

		_btnMaximizeSize.addStyleName(VaadinValoTheme.BUTTON_BORDERLESS_GREY);
		_btnRestoreSize.addStyleName(VaadinValoTheme.BUTTON_BORDERLESS_GREY);
		_btnRestoreSize.setVisible(false);
		
		HorizontalLayout hly = new HorizontalLayout();
		hly.addComponents(_btnMaximizeSize,_btnRestoreSize);
		
		super.setCompositionRoot(hly);
		
		// events
		_btnMaximizeSize.addClickListener(this::_maximizeSize);
		_btnRestoreSize.addClickListener(this::_restoreSize);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	private void _maximizeSize(final ClickEvent event) {
		// adjust buttons
		_btnMaximizeSize.setVisible(false);
		_btnRestoreSize.setVisible(true);
		
		// do the maximization
		_maximizeSizeCmd.maximizeSize(_subjectComponent);	// ie: subjectComponent.addStyleName("expanded-code-editor");
		
		// event
		this.fireEvent(new VaadinMaximizeHandlerChangeEvent(this,VaadinMaximizeHandlerStatus.MAXIMIZED));
	}
	private void _restoreSize(final ClickEvent event) {
		// adjust buttons
		_btnMaximizeSize.setVisible(true); 
		_btnRestoreSize.setVisible(false);
		
		// set the parent style
		_restoreSizeCmd.restoreSize(_subjectComponent);		// ie: _subjectComponent.removeStyleName("expanded-code-editor");
		
		// event
		this.fireEvent(new VaadinMaximizeHandlerChangeEvent(this,VaadinMaximizeHandlerStatus.NORMAL));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	MAXIMIZE / RESTORE ACTION
/////////////////////////////////////////////////////////////////////////////////////////	
	@FunctionalInterface 
	public interface R01UIMaximizeSizeCmd<C extends Component> {
		public void maximizeSize(final C component);
	}
	@FunctionalInterface 
	public interface R01UIRestoreSizeCmd<C extends Component> {
		public void restoreSize(final C component);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
////////// Event Listener registration
	public Registration addStateChangeListener(final VaadinMaximizeHandlerChangeEventListener listener) {
		return this.addListener(VaadinMaximizeHandlerChangeEvent.class, 
						   		listener,VaadinMaximizeHandlerChangeEventListener.STATE_CHANGED_METHOD);
	}
////////// The event
	@FunctionalInterface
	public interface VaadinMaximizeHandlerChangeEventListener
			 extends SerializableEventListener {
		
		static final Method STATE_CHANGED_METHOD = ReflectTools.findMethod(VaadinMaximizeHandlerChangeEventListener.class,
																		   "stateChanged",VaadinMaximizeHandlerChangeEvent.class);
		public void stateChanged(VaadinMaximizeHandlerChangeEvent event);		
	}
	@Accessors(prefix="_")
	public static class VaadinMaximizeHandlerChangeEvent
		 	    extends Component.Event {
		
		private static final long serialVersionUID = -8347791686133408539L;
		
		@Getter private final VaadinMaximizeHandlerStatus _status;

		VaadinMaximizeHandlerChangeEvent(final Component source,final VaadinMaximizeHandlerStatus status) {
			super(source);
			_status = status;
		}
		public boolean isMaximized() {
			return _status == VaadinMaximizeHandlerStatus.MAXIMIZED;
		}
		public boolean isRestored() {
			return _status == VaadinMaximizeHandlerStatus.NORMAL;
		}
	}
	public static enum VaadinMaximizeHandlerStatus {
		MAXIMIZED,
		NORMAL
	};
}
