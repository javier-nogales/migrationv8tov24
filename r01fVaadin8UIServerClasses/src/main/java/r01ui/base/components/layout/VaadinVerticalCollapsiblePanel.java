package r01ui.base.components.layout;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.stream.Stream;

import com.vaadin.event.SerializableEventListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.util.ReflectTools;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.ui.vaadin.styles.VaadinValoTheme;
import r01f.util.types.collections.CollectionUtils;

/**
 * A vertical collapsible panel like
 * <pre>
 * 		+=========================================+
 * 		| [ component 1                       \/] |
 * 		| |                                     | |
 * 		| |                                     | |
 * 		| |                                     | |
 * 		| |                                     | |
 * 		| +-------------------------------------+ |
 * 		+=========================================+
 * </pre>
 */
public class VaadinVerticalCollapsiblePanel 
	 extends CustomComponent {

	private static final long serialVersionUID = 7056581430786323694L;
	
/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final VaadinIcons DEFAULT_OPENED_ICON = VaadinIcons.CARET_DOWN;
	private static final VaadinIcons DEFAULT_CLOSED_ICON = VaadinIcons.CARET_RIGHT;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	protected final VerticalLayout _vlyMain = new VerticalLayout();
	protected final HorizontalLayout _hlyCaption = new HorizontalLayout();
	protected final HorizontalLayout _hlyCaptionExtras = new HorizontalLayout();
	protected final VerticalLayout _vlyContent = new VerticalLayout();
	protected final Label _lblButton = new Label();
	protected final Label _lblTitle	= new Label();
	protected final HorizontalLayout _hlyTitle = new HorizontalLayout();
	
/////////////////////////////////////////////////////////////////////////////////////////
//	STATE (avoid as much as possible)
/////////////////////////////////////////////////////////////////////////////////////////	
	private boolean _locked;
	
/////////////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinVerticalCollapsiblePanel() {
		this(null,	// no caption 
			 null);	// no components
	}
	public VaadinVerticalCollapsiblePanel(final String caption) {
		this(caption,
			 null);	// no components
	}
	public VaadinVerticalCollapsiblePanel(final Collection<Component> components) {
		this(null,	// no caption 
			 components);
	}
	public VaadinVerticalCollapsiblePanel(final String caption, 
								  		  final Collection<Component> components) {
		// margins
		_vlyMain.setMargin(false);
		_hlyCaption.setMargin(false);
		_vlyContent.setMargin(false);
		_hlyTitle.setMargin(false);
		_hlyCaptionExtras.setMargin(false);
		
		// open/close button
		_lblButton.setIcon(DEFAULT_OPENED_ICON);
		
		// title
		_hlyTitle.addComponents(_lblButton,_lblTitle);
		_hlyTitle.setComponentAlignment(_lblButton,Alignment.MIDDLE_LEFT);
		_hlyTitle.setComponentAlignment(_lblTitle,Alignment.MIDDLE_LEFT);
		_lblTitle.addStyleName(VaadinValoTheme.NON_SELECT);
		
		// title click event
		_hlyTitle.addLayoutClickListener(this::_showHideContent);
		
		// caption
		_hlyCaption.addComponents(_hlyTitle,_hlyCaptionExtras);
		_hlyCaption.setComponentAlignment(_hlyTitle,Alignment.MIDDLE_LEFT);
		_hlyCaption.setComponentAlignment(_hlyCaptionExtras,Alignment.MIDDLE_RIGHT);
		
		// add the components
		if (CollectionUtils.hasData(components)) components.forEach(c -> _vlyContent.addComponent(c));
		
		// create the main layout
		_vlyMain.addComponents(_hlyCaption,
							   _vlyContent);
		
		this.setCompositionRoot(_vlyMain);
		
		// set the title
		if (caption != null) this.setTitle(caption);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	APPEARANCE
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinVerticalCollapsiblePanel withBorder() {
		super.getCompositionRoot().addStyleName("r01ui-collapsable-border-layout");
		_vlyContent.addStyleName("r01ui-collapsable-border-layout");
		return this;
	}
	public void setExpandRatio(final Component c,final int ratio) {
		_vlyContent.setExpandRatio(c,ratio);
	}
	public void setMargin(final boolean enabled) {
		_vlyMain.setMargin(enabled);
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	ADD / REMOVE COMPONENTS
/////////////////////////////////////////////////////////////////////////////////////////
	public void clean() {
		_vlyContent.removeAllComponents();
	}
	public void addComponent(final Component c) {
		_vlyContent.addComponent(c);
	}
	public void addComponents(final Component... components) {
		if (CollectionUtils.isNullOrEmpty(components)) return;
		Stream.of(components)
			  .forEach(c -> _vlyContent.addComponent(c));
	}
	public void removeComponent(final Component c) {
		_vlyContent.removeComponent(c);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TITLE / ICON / CAPTION EXTRA COMPONENTS
/////////////////////////////////////////////////////////////////////////////////////////	
	public void setTitle(final String caption) {
		_lblTitle.setValue(caption);
	}
	public void setTitle(final String caption,final ContentMode contentMode) {
		_lblTitle.setValue(caption);
		_lblTitle.setContentMode(contentMode);
	}
	public void setIcon(final VaadinIcons icon) {
		_lblTitle.setIcon(icon);
	}
	public void setCaptionExtras(final Component c) {
		_hlyCaption.setSizeFull();
		_hlyCaptionExtras.addComponent(c);
	}
	public void toggleCaptionExtrasVisibility() {
		_hlyCaptionExtras.setVisible(!_hlyCaptionExtras.isVisible());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	OPEN / CLOSE (expand / collapse)
/////////////////////////////////////////////////////////////////////////////////////////	
	public void collapse() {
		_vlyContent.setVisible(false);
		_lblButton.setIcon(DEFAULT_CLOSED_ICON);
	}
	public void expand() {
		_vlyContent.setVisible(true);
		_lblButton.setIcon(DEFAULT_OPENED_ICON);
	}
	public boolean isExpanded() {
		return _vlyContent.isVisible();
	}
	public boolean isCollapsed() {
		return !_vlyContent.isVisible();
	}
	@Deprecated
	public void close() {
		this.collapse();
	}
	@Deprecated
	public void open() {
		this.expand();
	}
	public VaadinCollapsibleLayoutState getCollapseState()  {
		return this.isExpanded() ? VaadinCollapsibleLayoutState.EXPANDED : VaadinCollapsibleLayoutState.COLLAPSED;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LOCK / UNLOCK
/////////////////////////////////////////////////////////////////////////////////////////	
	public void lock() {
		_locked = true;
	}
	public void unlock() {
		_locked = false;
	}
	public void setLocked(final boolean locked) {
		_locked = locked;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	PRIVATE METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	private void _showHideContent(final Event event) {
		if (_locked) return;
		
		_vlyContent.setVisible(!_vlyContent.isVisible());
		_hlyCaptionExtras.setVisible(!_hlyCaptionExtras.isVisible());
		_lblButton.setIcon(_vlyContent.isVisible() ? DEFAULT_OPENED_ICON : DEFAULT_CLOSED_ICON);
		this.fireEvent(new VaadinCollapsibleLayoutStateChangeEvent(this,
																    this.getCollapseState()));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EVENTS
/////////////////////////////////////////////////////////////////////////////////////////
////////// Event Listener registration
	public Registration addStateChangeListener(final VaadinCollapsibleLayoutStateChangeEventListener listener) {
		return this.addListener(VaadinCollapsibleLayoutStateChangeEvent.class, 
						   		listener,VaadinCollapsibleLayoutStateChangeEventListener.STATE_CHANGED_METHOD);
	}
////////// The event
	@FunctionalInterface
	public interface VaadinCollapsibleLayoutStateChangeEventListener
			 extends SerializableEventListener {
		static final Method STATE_CHANGED_METHOD = ReflectTools.findMethod(VaadinCollapsibleLayoutStateChangeEventListener.class,
																		   "stateChanged",VaadinCollapsibleLayoutStateChangeEvent.class);
		public void stateChanged(VaadinCollapsibleLayoutStateChangeEvent event);
	}
	@Accessors(prefix="_")
	public class VaadinCollapsibleLayoutStateChangeEvent
		 extends Component.Event { 
		
		private static final long serialVersionUID = 6457466037950244684L;
		
		@Getter private final VaadinCollapsibleLayoutState _state;
		
		VaadinCollapsibleLayoutStateChangeEvent(final Component source,final VaadinCollapsibleLayoutState status) {
			super(source);
			_state = status;
		}
	}
	public static enum VaadinCollapsibleLayoutState {
		EXPANDED,
		COLLAPSED
	};
}
