package r01ui.base.components.tinyeditor;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import lombok.experimental.Accessors;
import r01f.ui.i18n.UII18NService;
import r01f.ui.vaadin.tinymceeditor.TinyMCETextField;
import r01f.util.types.Strings;

/**
 * Component to use TinyMCETextField and solve addon implementation problems
 * 
 * <pre>
 * 
 * 		COMPONENT
 * 		+====================================================+ 
 * 		| Caption							 	Edit button	 |
 * 		| +------------------------------------------------+ |
 * 		| |                                                | |
 *      | | CSSLaout		                               | |
 *      | |  											   | |
 * 		| |                                                | |
 *      | +------------------------------------------------+ |
 *      +====================================================+ 
 *      
 *      MODAL WINDOW
 *      +====================================================+
 * 		| Caption (Same component caption)			 	 	 |
 * 		| +------------------------------------------------+ |
 * 		| |                                                | |
 *      | | Tiny Editor		                               | |
 *      | |  											   | |
 * 		| |                                                | |
 *      | +------------------------------------------------+ |
 *      | Close                                       Accept |
 *      +====================================================+
 *      
 *      
 * </pre>
 */

@Accessors(prefix="_")
public class TinyMCETextFieldComponent 
     extends CustomField<String> {
	private static final long serialVersionUID = -1558485699636910812L;
/////////////////////////////////////////////////////////////////////////////////////////
//	SERVICES
/////////////////////////////////////////////////////////////////////////////////////////	
	private transient UII18NService _i18n;
/////////////////////////////////////////////////////////////////////////////////////////
//	UI
/////////////////////////////////////////////////////////////////////////////////////////	
	private final Label _caption = new Label();
	private TinyMCETextField _tinyEditor = new TinyMCETextField();
	private final CssLayout _html = new CssLayout();
	private final TextField _hiddenField = new TextField();
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public TinyMCETextFieldComponent() {
		super();
		_html.setWidth(100,Unit.PERCENTAGE);
		_html.setHeight(300, Unit.PIXELS);
	}
	public TinyMCETextFieldComponent(final UII18NService i18n) {
		this(i18n, null);
	}
	public TinyMCETextFieldComponent(final UII18NService i18n, 
									 final String caption) {
		_i18n = i18n;
		if (Strings.isNOTNullOrEmpty(caption)) {
			_caption.setValue(caption);
		}
		_html.setWidth(100,Unit.PERCENTAGE);
		_html.setHeight(300, Unit.PIXELS);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	INIT
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	protected Component initContent() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(false);
		layout.setMargin(false);
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSizeFull();
		_caption.setContentMode(ContentMode.HTML);
		hl.addComponent(_caption);
		Button btn = new Button(_i18n.getMessage("edit"));
		btn.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btn.addClickListener(event -> _openEditor());
		hl.addComponent(btn);
		hl.setComponentAlignment(btn, Alignment.BOTTOM_RIGHT);
		hl.setComponentAlignment(_caption, Alignment.BOTTOM_LEFT);
		layout.addComponent(hl);
		_html.addStyleName("r01ui-html-box");
		layout.addComponent(_html);
		return layout;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	VALUE
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public String getValue() {
		return _hiddenField.getValue();
	}
	@Override
	protected void doSetValue(final String value) {
		_html.removeAllComponents();
		if (value != null) {
			_hiddenField.setValue(value);
			_html.addComponent(new Label(value, ContentMode.HTML));
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public void setCaption(final String caption) {
		_caption.addStyleName("v-caption");
		_caption.setValue("<strong>"+ caption+"</strong>");
	}	
	@Override
	public void setHeight(final float height, final Unit unit) {
		super.setHeight(height, unit);
		if (unit.equals(Unit.PIXELS)) {
			_html.setHeight(height-80, Unit.PIXELS);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	private void _openEditor() {
		Window w = new Window(_caption.getValue());
		w.setId("tinyEditor");
		w.setCaptionAsHtml(true);
		_tinyEditor = new TinyMCETextField();
		_tinyEditor.setValue(_hiddenField.getValue());
		w.setResizable(true);
		w.center();
		w.setWidth(800, Unit.PIXELS);
		w.setHeight(600, Unit.PIXELS);
		VerticalLayout vl = new VerticalLayout();
		vl.setSizeFull();
		w.setContent(vl);
		vl.addComponent(_tinyEditor);
		Button close = new Button(_i18n.getMessage("close"));
		close.addClickListener(evt -> w.close());
		Button accept = new Button(_i18n.getMessage("accept"));
		accept.addStyleName(ValoTheme.BUTTON_PRIMARY);
		accept.addClickListener(evt -> {
										String oldValue = _hiddenField.getValue();
										_hiddenField.setValue(_tinyEditor.getValue());
										if (!oldValue.equals(_hiddenField.getValue())) {
											this.fireEvent(new ValueChangeEvent<>(this,
																				    	oldValue,
																				    	true));
										}
										_html.removeAllComponents();
										_html.addComponent(new Label(_tinyEditor.getValue(), ContentMode.HTML));
										w.close();
									  });
		HorizontalLayout hl = new HorizontalLayout();
		hl.addComponents(close, accept);
		hl.setWidth(100, Unit.PERCENTAGE);
		hl.setExpandRatio(close, 1);
		hl.setComponentAlignment(accept, Alignment.BOTTOM_RIGHT);
		hl.setComponentAlignment(close, Alignment.BOTTOM_RIGHT);
		vl.addComponent(hl);
		vl.setExpandRatio(_tinyEditor, 1);
		getUI().addWindow(w);
	}
}
