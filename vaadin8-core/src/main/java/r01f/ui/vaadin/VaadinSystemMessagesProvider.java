package r01f.ui.vaadin;

import com.vaadin.server.CustomizedSystemMessages;
import com.vaadin.server.SystemMessages;
import com.vaadin.server.SystemMessagesInfo;
import com.vaadin.server.SystemMessagesProvider;

import r01f.types.url.Url;
import r01f.ui.i18n.UII18NService;
import r01f.util.types.Strings;

public class VaadinSystemMessagesProvider 
  implements SystemMessagesProvider {

	private static final long serialVersionUID = 7431471725897619522L;
	
	private final UII18NService _i18n;
	private final Url _loginUrl;
	private final Url _unauthorizedUrl;
	//private final Url _cookiesDisabledUrl;
	private final Url _internalErrorUrl;
	
	public VaadinSystemMessagesProvider(final UII18NService i18n, 
										final Url loginUrl, 
										final Url unauthorizedUrl, 
										//final Url _cookiesDisabledUrl,
										final Url internalErrorUrl) {
		_i18n = i18n;
		_loginUrl = loginUrl;
		_unauthorizedUrl = unauthorizedUrl;
		//_cookiesDisabledUrl = cookiesDisabledUrl;
		_internalErrorUrl = internalErrorUrl;
	}

	@Override
	public SystemMessages getSystemMessages(final SystemMessagesInfo systemMessagesInfo) {
		String errorMessage = _i18n.getMessage("error.message.instructions", 
				Strings.customized("<u>{}</u>", _i18n.getMessage("error.message.link")));

		CustomizedSystemMessages messages = new CustomizedSystemMessages();
		
		// authentication
		messages.setAuthenticationErrorCaption(_i18n.getMessage("error.authentication.caption"));
		messages.setAuthenticationErrorMessage(errorMessage);
		if (_unauthorizedUrl != null) {
			messages.setAuthenticationErrorURL(_unauthorizedUrl.asString());
		}
		// comunication
		messages.setCommunicationErrorCaption(_i18n.getMessage("error.communication.caption"));
		messages.setCommunicationErrorMessage(errorMessage);
		if (_loginUrl != null) {
			messages.setCommunicationErrorURL(_loginUrl.asString());
		}
		
		// sesion expired
		messages.setSessionExpiredCaption(_i18n.getMessage("error.session.caption"));
		messages.setSessionExpiredMessage(errorMessage);
		messages.setSessionExpiredURL(_loginUrl.asString());
		
		// disabled cookies
		messages.setCookiesDisabledCaption(_i18n.getMessage("error.cookies.caption"));
		String cookiesMessage = _i18n.getMessage("error.cookies.message") + "<br/>" +
								_i18n.getMessage("error.cookies.message.instructions", 
												  Strings.customized("<u>{}</u>", _i18n.getMessage("error.message.link")));
		
		messages.setCookiesDisabledMessage(cookiesMessage);
//		if (_cookiesDisabledUrl != null) {
//			messages.setCookiesDisabledURL(_cookiesDisabledUrl);
//		}
		
		// internal error
		messages.setInternalErrorCaption(_i18n.getMessage("error.internal.caption"));
		String internalErrorMessage = _i18n.getMessage("error.internal.message") + "<br/>" +
								_i18n.getMessage("error.message.instructions", 
												  Strings.customized("<u>{}</u>", _i18n.getMessage("error.message.link")));
		
		messages.setInternalErrorMessage(internalErrorMessage);
		if (_internalErrorUrl != null) {
			messages.setInternalErrorURL(_internalErrorUrl.asString());
		}
		return messages;
	}

}
