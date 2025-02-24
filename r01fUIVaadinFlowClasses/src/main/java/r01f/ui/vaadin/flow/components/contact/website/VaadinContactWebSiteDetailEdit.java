package r01f.ui.vaadin.flow.components.contact.website;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

import r01f.ui.i18n.UII18NService;
import r01f.ui.vaadin.flow.annotations.VaadinViewComponentLabels;
import r01f.ui.vaadin.flow.annotations.VaadinViewField;
import r01f.ui.vaadin.flow.components.contact.VaadinContactMeanDetailEditBase;

public class VaadinContactWebSiteDetailEdit
	 extends VaadinContactMeanDetailEditBase<VaadinViewDirectoryContactWebSite> {

	private static final long serialVersionUID = -3069365158386075376L;
/////////////////////////////////////////////////////////////////////////////////////////
//  UI
/////////////////////////////////////////////////////////////////////////////////////////
	@VaadinViewField(bindToViewObjectFieldNamed=VaadinViewDirectoryContactWebSite.URL_FIELD,required=true)
	@VaadinViewComponentLabels(captionI18NKey="contact.web.url",useCaptionI18NKeyAsPlaceHolderKey=true)
	private final TextField _txtUrl = new TextField();

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinContactWebSiteDetailEdit(final UII18NService i18n) {
		super(VaadinViewDirectoryContactWebSite.class,
			  i18n);

		// url
		_txtUrl.setWidth(100,Unit.PERCENTAGE);
		_txtUrl.setReadOnly(false);
		
		// layout: DO NOT FORGET! 
		super.add(new HorizontalLayout(_cmbUsage),
				  new HorizontalLayout(_txtUrl),new HorizontalLayout(_chkDefault,_chkPrivate));

		////////// Init the form components (DO NOT FORGET!!)
		_initFormComponents();
	}
}
