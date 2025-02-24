package r01f.ui.vaadin.flow.components.contact.phone;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import r01f.locale.I18NKey;
import r01f.ui.i18n.UII18NService;
import r01f.ui.vaadin.flow.components.contact.VaadinContactMeanManageBase;

public class VaadinContactPhoneManage
	 extends VaadinContactMeanManageBase<VaadinViewContactPhone,
	 									 VaadinContactPhoneDetailEditWindow> {

	private static final long serialVersionUID = 793707311914095044L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinContactPhoneManage(final UII18NService i18n) {
		super(i18n,
			  new VaadinContactPhoneDetailEditWindow(i18n),
			  VaadinViewContactPhone::new,			// factory
			  I18NKey.named("contact.phone.list.title"));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void _configureMoreGridColumns(final UII18NService i18n) {
		////////// Columns
	

		// phone
		_grid.addColumn(VaadinViewContactPhone::getNumber)
			 .setHeader(i18n.getMessage("contact.phone.number"))
			 .setFlexGrow(0)
			 .setResizable(false)
			 .setId("number");
		// type
		_grid.addColumn(VaadinViewContactPhone::getType)
			 .setHeader(i18n.getMessage("contact.phone.type"))
			 .setFlexGrow(0)
			 .setResizable(false)
			 .setId("type");
		// availability range
		_grid.addColumn(new ComponentRenderer<>(phone -> new Span(phone.getAvailableRangeAsString())))
			 .setHeader(i18n.getMessage("contact.phone.available"))
			 .setFlexGrow(1)
			 .setResizable(false)
			 .setId("availability");
	}
}
