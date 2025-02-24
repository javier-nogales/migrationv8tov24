package r01ui.base.components.tinyeditor;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ErrorMessage;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ErrorLevel;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import lombok.experimental.Accessors;
import r01f.types.Dimensions2D;
import r01f.ui.i18n.UII18NService;
import r01f.ui.vaadin.styles.VaadinValoTheme;
import r01f.ui.vaadin.tinymceeditor.TinyMCE7TextField;
import r01f.ui.vaadin.view.VaadinViewI18NMessagesCanBeUpdated;
import r01f.util.types.Strings;

/**
 * Component to use TinyMCETextField and solve addon implementation problems
 *
 * <pre>
 *
 * 		COMPONENT
 * 		+====================================================+
 * 		| Edit button Caption							 	 |
 * 		| +------------------------------------------------+ |
 * 		| |												| |
 *	  | | Panel									   | |
 *	  | |  											   | |
 * 		| |												| |
 *	  | +------------------------------------------------+ |
 *	  +====================================================+
 *
 *	  MODAL WINDOW
 *	  +====================================================+
 * 		| Caption (Same component caption)			 	 	 |
 * 		| +------------------------------------------------+ |
 * 		| |												| |
 *	  | | Tiny Editor									   | |
 *	  | |  											   | |
 * 		| |												| |
 *	  | +------------------------------------------------+ |
 *	  | Close									   Accept |
 *	  +====================================================+
 *
 *
 * </pre>
 */

@Accessors(prefix="_")
public class TinyMCE7TextFieldClickableComponent
	 extends CustomField<String>
  implements VaadinViewI18NMessagesCanBeUpdated {

	private static final long serialVersionUID = -3374854203176307354L;
/////////////////////////////////////////////////////////////////////////////////////////
//	SERVICES
/////////////////////////////////////////////////////////////////////////////////////////
	private final UII18NService _i18n;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private Dimensions2D _winDimensions = new Dimensions2D(800, 625); // default width and height
	private Unit _winDimensionsUnit = Unit.PIXELS; // default dimension units
	private float _tinyHeightInPixels = 380; // default tiny height (tiny does not accept height in percentage, only in pixels)

	private final Button _btnEdit = new Button();
	private final Label _lblCaption = new Label("",ContentMode.HTML);
	private final Label _lblRequired = new Label();

	private final Panel _panelHTML = new Panel();
	private final VerticalLayout _layoutHTML = new VerticalLayout();
	private final Label _lblHTML = new Label();

	private final TextField _hiddenField = new TextField();
	private final VerticalLayout _componentRoot = new VerticalLayout();

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private float heightLimit = 150;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public TinyMCE7TextFieldClickableComponent(final UII18NService i18n) {
		this(i18n, null);
	}
	public TinyMCE7TextFieldClickableComponent(final UII18NService i18n,
									   final String caption) {
		_i18n = i18n;

		if (Strings.isNOTNullOrEmpty(caption)) {
			_lblCaption.setValue(caption);
		}
		// styles
		_lblHTML.addStyleName("r01ui-html-box");
		_lblHTML.setContentMode(ContentMode.HTML);
		_lblHTML.setWidthFull();
		_panelHTML.setWidthFull();
		_panelHTML.addStyleName("tiny-editor");
		_componentRoot.setMargin(false);

		// panel with a label containing tiny value
		_layoutHTML.setMargin(new MarginInfo(false, true));
		_layoutHTML.addComponent(_lblHTML);
		_panelHTML.setContent(_layoutHTML);

		this.setHeight(heightLimit, Unit.PIXELS);
		this.updateI18NMessages(_i18n);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected Component initContent() {
		HorizontalLayout captionLayout = new HorizontalLayout();
		captionLayout.addComponents(_lblCaption, _lblRequired);
		captionLayout.setComponentAlignment(_lblCaption, Alignment.MIDDLE_CENTER);
		captionLayout.setComponentAlignment(_lblRequired, Alignment.MIDDLE_CENTER);

		HorizontalLayout headerLayout = new HorizontalLayout();
		headerLayout.addComponents(_btnEdit, captionLayout);

		_componentRoot.addComponents(headerLayout,
				   					 _panelHTML);

		// behaviour
		_btnEdit.addClickListener(event -> _openEditor());
		_layoutHTML.addLayoutClickListener(event -> _openEditor());

		// styles
		_btnEdit.setIcon(VaadinIcons.EDIT);
		_btnEdit.addStyleName(VaadinValoTheme.BUTTON_SMALL);

		return _componentRoot;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String getValue() {
		return Strings.isNOTNullOrEmpty(_hiddenField.getValue())
						? _hiddenField.getValue()
						: null;
	}
	@Override
	protected void doSetValue(final String value) {
		if (Strings.isNOTNullOrEmpty(value)) {
			_hiddenField.setValue(value);
		} else {
			_hiddenField.setValue("");
		}
		_lblHTML.setValue(_hiddenField.getValue());
		_calculateHeight(_hiddenField.getValue());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void setCaption(final String caption) {
		_lblCaption.addStyleName("v-caption");
		_lblCaption.setValue(caption);
	}
	@Override
	public void setHeight(final String height) {
		this.setHeight(height);
	}
	@Override
	public void setHeight(final float height,
						  final Unit unit) {
		if (unit.equals(Unit.PIXELS)) {
			heightLimit = height;
			_panelHTML.setHeight(height, Unit.PIXELS);
			_layoutHTML.setHeight(height - 10, Unit.PIXELS);
		} else {
			throw new IllegalArgumentException(Strings.customized("{} is not a valid unit. Height must be set only with Unit.PIXELS ", unit));
		}
	}
	@Override
	public void setComponentError(final ErrorMessage componentError) {
		super.setComponentError(componentError);
		if (componentError != null && componentError.getErrorLevel().equals(ErrorLevel.ERROR)) {
			_panelHTML.addStyleName("v-textarea-error");
		} else {
			_panelHTML.removeStyleName("v-textarea-error");
		}
	}
	@Override
	public void setRequiredIndicatorVisible(final boolean visible) {
		_hiddenField.setRequiredIndicatorVisible(visible);
   		_lblRequired.addStyleName("v-required-field-indicator");
		_lblRequired.setValue("*");
	}
	@Override
	public boolean isRequiredIndicatorVisible() {
		return _hiddenField.isRequiredIndicatorVisible();
	}
	public void setWinDimensions(final Dimensions2D winDimensions,
								 final Unit winDimensionsUnit,
								 final float tinyHeightInPixels) {
		_winDimensions = winDimensions;
		_winDimensionsUnit = winDimensionsUnit;
		_tinyHeightInPixels = tinyHeightInPixels;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PRIVATE METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	private void _openEditor() {
		TinyMCETextFieldWindow tinyMCEWindow = new TinyMCETextFieldWindow(_lblCaption.getValue(),
																		 _winDimensions,
																		 _winDimensionsUnit,
																		 _tinyHeightInPixels);
		getUI().addWindow(tinyMCEWindow);
	}
	private void _calculateHeight(final String htmlValue) {
		int n = htmlValue.split("(?=</p)").length-1;
		int contentSize = htmlValue.length();
		// every 40 pixeles
		if (contentSize > 450 || n > (heightLimit / 50)) {
			_layoutHTML.setHeightUndefined(); // force scroll to panel
		} else{
			_layoutHTML.setSizeFull();
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	I18N
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void updateI18NMessages(final UII18NService i18n) {
		_btnEdit.setDescription(_i18n.getMessage("tiny.edit.placeHolder"));
		_btnEdit.setCaption(_i18n.getMessage("edit"));
		_panelHTML.setDescription(_i18n.getMessage("tiny.edit.click"));
	}
/////////////////////////////////////////////////////////////////////////////////////////
// INNER CLASS
/////////////////////////////////////////////////////////////////////////////////////////
	public class TinyMCETextFieldWindow
		 extends Window {

		private static final long serialVersionUID = -5701450740388661241L;
		////////// FIELDS
		private final TinyMCE7TextField _tinyEditor = new TinyMCE7TextField();

		////////// CONSTRUCTOR
		public TinyMCETextFieldWindow() {
			this(null,
				 _winDimensions,
				 _winDimensionsUnit,
				 _tinyHeightInPixels);
		}
		public TinyMCETextFieldWindow(final String caption,
									  final Dimensions2D winDimensions,
									  final Unit winDimensionsUnit,
									  final float tinyHeightInPixels) {
			super(caption);
			this.setModal(true);
			this.setId("tinyEditor");
			this.setCaptionAsHtml(true);
			_setWindowDimensions(winDimensions,
								 winDimensionsUnit,
								 tinyHeightInPixels);

			// init tiny with current language and default config
			StringBuffer tinyConfig = new StringBuffer();
			tinyConfig.append(Strings.customized("{language: '{}'",
												 _i18n.getCurrentLanguage().getIso639_1()))
					  .append(",").append(TinyMCE7TextField.DEFAULT_TINY_CONFIG);
			tinyConfig.append("}");

			_tinyEditor.setConfig(tinyConfig.toString());
			_tinyEditor.setValue(_hiddenField.getValue());
			_tinyEditor.setSizeFull();

			VerticalLayout _windowLayout = new VerticalLayout();
			VerticalLayout _tinyLayout = new VerticalLayout();
			HorizontalLayout _buttonsLayout = new HorizontalLayout();
			Button _close = new Button(_i18n.getMessage("close"));
			Button _accept = new Button(_i18n.getMessage("accept"));

			_tinyLayout.setHeight("100%");
			_tinyLayout.setMargin(false);
			_tinyLayout.addComponent(_tinyEditor);
			_buttonsLayout.addComponents(_close, _accept);

			_windowLayout.setSizeFull();
			_windowLayout.addComponents(_tinyLayout, _buttonsLayout);
			_windowLayout.setComponentAlignment(_buttonsLayout, Alignment.BOTTOM_RIGHT);
			_windowLayout.setExpandRatio(_tinyLayout, 1);

			// behaviour
			_close.addClickListener(evt -> this.close());
			_accept.addClickListener(evt -> {
											_acceptClickListenerEvent();
											this.close();
									 });
			_accept.addStyleName(ValoTheme.BUTTON_PRIMARY);

			this.setContent(_windowLayout);
		}
		/////////////////////////////////////////////////////////////////////////////////////////
		//	PRIVATE METHODS
		/////////////////////////////////////////////////////////////////////////////////////////
		private void _setWindowDimensions(final Dimensions2D winDimensions,
									  	  final Unit winDimensionsUnit,
									  	  final float tinyHeightInPixels) {
			this.setWidth(winDimensions.getWidth(), winDimensionsUnit);
			this.setHeight(winDimensions.getHeight(), winDimensionsUnit);
			_tinyEditor.setHeight(tinyHeightInPixels, Unit.PIXELS);
		}
		//	Set value to hidden field and set value to show in panel
		private void _acceptClickListenerEvent() {
			String oldValue = _hiddenField.getValue();
			_hiddenField.setValue(_tinyEditor.getValue());
			_lblHTML.setValue(_tinyEditor.getValue());
			_calculateHeight(_tinyEditor.getValue());
			if (!oldValue.equals(_hiddenField.getValue())) {
				TinyMCE7TextFieldClickableComponent.this.fireEvent(new ValueChangeEvent<>(TinyMCE7TextFieldClickableComponent.this,
																						  oldValue,
																						  true));
			}
		}
	}
}
