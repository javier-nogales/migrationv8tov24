package r01ui.base.components.tinyeditor;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

import org.json.JSONObject;

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
import r01f.ui.vaadin.tinymceeditor.TinyMCE7TextField;
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
 * 		| |												| |
 *	  | | CSSLaout									   | |
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
public class TinyMCE7TextFieldComponent
	 extends CustomField<String> {
	private static final long serialVersionUID = -1558485699636910812L;
		
	private static final String TINY_CONFIG = 			
			//+"auto_focus: id,"	
			//"selector: '#' + id,"			
			"height:'100%',"
			+"promotion: false,"
			+"plugins: 'lists r01advlist link r01image r01code r01templates axe_frame codesample autolink charmap hr r01linkanchor pagebreak searchreplace wordcount visualblocks visualchars fullscreen nonbreaking table directionality',"			
			+"menu : {"
				+"file : {items : ''},"
				+"edit : {title : 'Edit',items : 'undo redo | cut copy paste pastetext | selectall'},"
				+"insert : {title : 'Insert',items : 'unlink link r01linkanchor | r01image | charmap hr codesample date'},"
				+"view : {title : 'View',items : ' visualchars visualblocks visualaid'},"
				+"format : {title : 'Format',items : 'bold italic underline strikethrough superscript subscript | formats | removeformat'},"
				+"table : {title : 'Table',items : 'inserttable tableprops deletetable | cell row column'},"
				+"tools : {title : 'Tools',items : 'spellchecker r01code'}"
			+"},"
			+"menubar: 'file edit insert view format table tools',"
			+"removed_menuitems: 'Inline',"
			+"toolbar1 : 'insertfile undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | unlink link autolink r01linkanchor r01image',"
			+"toolbar2 : 'codeinfo codewarning codenotice codebutton codepost | codesample r01code axe_frame',"			  	  
			+"image_advtab: true,"
			+"table_advtab: false,"
			+"table_cell_advtab: false,"
			+"table_row_advtab: false,"
			+"table_appearance_options: false,"
			+"valid_elements:'*[*]',"
			+"extended_valid_elements:'script[language|type|src],',"
			+"valid_children:'-p[script]',"
			+"relative_urls: false,"
			+"verify_html: false,"
			+"allow_script_urls:true,"			
			+"remove_script_host : false,"				
			+"convert_urls : false,"	
			+"code_dialog_width: 1024,"
			+"code_dialog_height: 600,"			
			+"formats : {"			
				+"alignleft: ["
					+"{selector : 'p,h1,h2,h3,h4,h5,h6,td,th,tr,div,ul,ol,li,table', classes : 'r01AlignLeft'},"
					+"{selector : 'img', classes : 'r01AlignImgLeft', ceFalseOverride: true},"
					+"{selector: 'figure.image', collapsed: false, classes: 'align-left', ceFalseOverride: true},"
					+"{selector: 'figure,p,h1,h2,h3,h4,h5,h6,td,th,tr,div,ul,ol,li',"
						+"styles: {textAlign: 'left'},"
						+"inherit: false,"
						+"defaultBlock: 'div'"
					+"},"
					+"{selector: 'img,table', collapsed: false, styles: {'float': 'left'}}"
				+"],"		
				+"aligncenter: ["
					+"{selector : 'p,h1,h2,h3,h4,h5,h6,td,th,tr,div,ul,ol,li,table,img', classes : 'r01AlignCenter'},"							
					+"{selector: 'figure,p,h1,h2,h3,h4,h5,h6,td,th,tr,div,ul,ol,li',"
						+"styles: {textAlign: 'center'},"
						+"inherit: false,"
						+"defaultBlock: 'div'"
					+"},"
					+"{selector: 'figure.image', collapsed: false, classes: 'align-center', ceFalseOverride: true},"
					+"{selector: 'img', collapsed: false, styles: {display: 'block', marginLeft: 'auto', marginRight: 'auto'}},"
					+"{selector: 'table', collapsed: false, styles: {marginLeft: 'auto', marginRight: 'auto'}}"
				+"],"					
				+"alignright: ["
					+"{selector : 'p,h1,h2,h3,h4,h5,h6,td,th,tr,div,ul,ol,li,table', classes : 'r01AlignRight'},"
					+"{selector : 'img', classes : 'r01AlignImgRight', ceFalseOverride: true},"
					+"{selector: 'figure.image', collapsed: false, classes: 'align-right', ceFalseOverride: true},"
					+"{selector: 'figure,p,h1,h2,h3,h4,h5,h6,td,th,tr,div,ul,ol,li',"
						+"styles: {textAlign: 'right'},"
						+"inherit: false,"
						+"defaultBlock: 'div'"
					+"},"
					+"{selector: 'img,table', collapsed: false, styles: {'float': 'right'}}"
				+"],"						
				+"alignjustify: ["
					+"{selector : 'p,h1,h2,h3,h4,h5,h6,td,th,tr,div,ul,ol,li,table', classes : 'r01AlignJustify'},"
					+"{selector: 'figure,p,h1,h2,h3,h4,h5,h6,td,th,tr,div,ul,ol,li',"
						+"styles: {textAlign: 'justify'},"
						+"inherit: false,"
						+"defaultBlock: 'div'"
					+"}"
				+"],"
				+"valigntop: ["
					+"{selector: 'td,th', classes: 'r01VAlignTop'},"		
					+"{selector: 'td,th', styles: {'verticalAlign': 'top'}}"
				+"],"
				+"valignmiddle: ["
					+"{selector: 'td,th', classes: 'r01VAlignMiddle'},"		   
					+"{selector: 'td,th', styles: {'verticalAlign': 'middle'}}"
				+"],"
				+"valignbottom: ["
					+"{selector: 'td,th', classes: 'r01VAlignBottom'},"		   
					+"{selector: 'td,th', styles: {'verticalAlign': 'bottom'}}"
				+"],"			
				+"bold : ["
					+"{inline: 'strong', remove: 'all'},"
					+"{inline: 'span', classes: 'r01Bold'},"
					+"{inline: 'span', styles: {fontWeight: 'bold'}},"									
					+"{inline: 'b', remove: 'all'}"
				+"],"
				+"italic : ["
					+"{inline: 'em', remove: 'all'},"	
					+"{inline: 'span', classes : 'r01Italic'},"
					+"{inline: 'span', styles: {fontStyle: 'italic'}},"							
					+"{inline: 'i', remove: 'all'}"								
				+"],"
				+"underline : ["					
					+"{inline: 'span', classes : 'r01Underline'},"
					+"{inline: 'span', styles: {textDecoration: 'underline'}, exact: true},"
					+"{inline: 'u', remove: 'all'}"
				+"],"
				+"strikethrough: ["
					+"{inline: 'del', remove: 'all'},"
					+"{inline: 'span', styles: {textDecoration: 'line-through'}, exact: true},"
					+"{inline: 'strike', remove: 'all'}"
				+"]"			
			+"},"
			+"image_class_list: ["
				+"{title: '', value: ''},"
				+"{title: 'Align left', value: 'r01AlignImgLeft'},"
				+"{title: 'Align left (for list)', value: 'r01AlignImgLeftList'},"
				+"{title: 'Align right', value: 'r01AlignImgRight'}"
			+"],"																		
			+"content_css: ["
				+"'VAADIN/scripts/tinymce5/examples/css/r01gTinyContent.css',"
				+"'VAADIN/scripts/tinymce5/examples/templates/css/templates.css',"
				+"'VAADIN/scripts/tinymce7/frameworks/fonts/font-awesome.min.css',"
			+"]";	; 
/////////////////////////////////////////////////////////////////////////////////////////
//	SERVICES
/////////////////////////////////////////////////////////////////////////////////////////	
	private transient UII18NService _i18n;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	UI
/////////////////////////////////////////////////////////////////////////////////////////	
	private final Label _caption = new Label();
	private TinyMCE7TextField _tinyEditor = new TinyMCE7TextField();
	private final CssLayout _html = new CssLayout();
	private final TextField _hiddenField = new TextField();
/////////////////////////////////////////////////////////////////////////////////////////
//	STATE (avoid as much as possible)
/////////////////////////////////////////////////////////////////////////////////////////	
	private JSONObject _fileExplorerConfigJSON = null;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public TinyMCE7TextFieldComponent() {
		super();
		_html.setWidth(100,Unit.PERCENTAGE);
		_html.setHeight(300, Unit.PIXELS);
	}
	public TinyMCE7TextFieldComponent(final UII18NService i18n) {
		this(i18n, null, null);
	}
	public TinyMCE7TextFieldComponent(final UII18NService i18n, 
									  final JSONObject fileExplorerConfigJSON) {
		this(i18n, null, fileExplorerConfigJSON);
	}	
	public TinyMCE7TextFieldComponent(final UII18NService i18n, 
									  final String caption, 
									  final JSONObject fileExplorerConfigJSON) {
		_i18n = i18n;
		if (Strings.isNOTNullOrEmpty(caption)) {
			_caption.setValue(caption);
		}
		_fileExplorerConfigJSON = fileExplorerConfigJSON;
		_html.setWidth(100,Unit.PERCENTAGE);
		_html.setHeight(300, Unit.PIXELS);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
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
	@Override
	public String getValue() {
		return _hiddenField.getValue();
	}

	@Override
	protected void doSetValue(final String value) {
		_html.removeAllComponents();
		if (value != null) {
			_hiddenField.setValue(value);
			_html.addComponent(new Label(value,ContentMode.HTML));
		}

	}
	@Override
	public void setCaption(final String caption) {
		_caption.addStyleName("v-caption");
		_caption.setValue("<strong>"+ caption+"</strong>");
	}
	@Override
	public void setHeight(final float height,final Unit unit) {
		super.setHeight(height, unit);
		if (unit.equals(Unit.PIXELS)) {
			_html.setHeight(height-80, Unit.PIXELS);
		}

	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	private void _openEditor() {
		String caption = _caption.getValue();
		
		if (Strings.isNullOrEmpty(caption)) {
			caption = _i18n.getMessage("tinymce.default.caption");
		}
		
		Window w = new Window(caption);
		w.setId("tinyEditor");
		w.setCaptionAsHtml(true);
		w.setModal(true);
		_tinyEditor = new TinyMCE7TextField();
		
		StringBuffer tinyConfig = new StringBuffer();
		tinyConfig.append(Strings.customized("{language: '{}'",
												  _i18n.getCurrentLanguage().getIso639_1()))
				  .append(",").append(TINY_CONFIG);
		
		if (_fileExplorerConfigJSON != null) {		
			tinyConfig.append(Strings.customized(",fileExplorerConfigJSON: '{}'", _fileExplorerConfigJSON.toString()));
		}		
		tinyConfig.append("}");		
		
		System.out.println(tinyConfig.toString());
		
		_tinyEditor.setConfig(tinyConfig.toString());											  
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
										_hiddenField.setValue(_encodeHTML(_tinyEditor.getValue()));
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
	
	
/*
	 * Encode not ISO-8859-1 html characters
	 * @param htmlValue
	 */
	private static String _encodeHTML(final String htmlValue) {			
		if (Strings.isNullOrEmpty(htmlValue))  return htmlValue;		
		StringBuffer encodeHtmlBuffer = new StringBuffer();
						
		for (int i = 0;i<htmlValue.length();i++) {
			encodeHtmlBuffer.append(notISO8859CharsetEncode(htmlValue.charAt(i)));			
		}		
		return encodeHtmlBuffer.toString();		
	}
	/*
	 * Encode not ISO-8859-1 html char
	 * @param htmlChar
	 */
	private static String notISO8859CharsetEncode(final char htmlChar) {
		 // Define the charset
		Charset charset = Charset.forName("ISO-8859-1");		
		// Create a CharsetEncoder for the charset
		CharsetEncoder encoder = charset.newEncoder();		
		// Create a CharBuffer containing the Unicode characters you want to encode
		CharBuffer inputBuffer = CharBuffer.wrap(String.valueOf(htmlChar));						
		// Allocate a ByteBuffer to hold the encoded bytes
		ByteBuffer outputBuffer = ByteBuffer.allocate(2); // Adjust the capacity as needed		
		// Encode the characters
		CoderResult result = encoder.encode(inputBuffer, outputBuffer, true);		
		// Check if the encoding operation was successful
		if (result.isUnderflow()) {											 
			return String.valueOf(htmlChar);
		} else {			
			return "&#"+((int)htmlChar)+";";
		}
	}
}
