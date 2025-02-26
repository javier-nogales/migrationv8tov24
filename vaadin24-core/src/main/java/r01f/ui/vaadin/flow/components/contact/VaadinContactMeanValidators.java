package r01f.ui.vaadin.flow.components.contact;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;
import r01f.ui.i18n.UII18NService;
import r01f.util.types.Strings;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class VaadinContactMeanValidators {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@RequiredArgsConstructor
	public static class VaadinPhoneValidator
	   	     implements Validator<Phone> {

		private static final long serialVersionUID = -6088142603048630416L;

		private final UII18NService _i18n;

		@Override
		public ValidationResult apply(final Phone phone,
									  final ValueContext context) {
			if (phone == null || Strings.isNullOrEmpty(phone.asString())) return ValidationResult.error(_i18n.getMessage("user.login.registry.validation.phone.mandatory"));

			// beware!! this is called for every keystroke on the text field
			return phone.isValid() ? ValidationResult.ok()
								   : ValidationResult.error(_i18n.getMessage("user.login.registry.validation.phone"));
		}
	}
	public static boolean isValidPhones(final String value) {
		Phone phone = Phone.of(value);
		boolean allValid = true;
		if (phone == null
		 || !phone.isValid()) {
			allValid = false;
		}
		return allValid;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EMAIL
/////////////////////////////////////////////////////////////////////////////////////////	
	@RequiredArgsConstructor
	public static class VaadinEMailValidator
	   	 	 implements Validator<EMail> {

		private static final long serialVersionUID = -6088142603048630416L;

		private final UII18NService _i18n;

		@Override
		public ValidationResult apply(final EMail email,
									  final ValueContext context) {
			if (email == null || Strings.isNullOrEmpty(email.asString())) return ValidationResult.error(_i18n.getMessage("user.login.registry.validation.email.mandatory"));

			// beware!! this is called for every keystroke on the text field
			return email.isValid() ? ValidationResult.ok()
								   : ValidationResult.error(_i18n.getMessage("user.login.registry.validation.email"));
		}
	}
	public static boolean isValidEmails(final String value) {
		EMail email = EMail.of(value);
		boolean allValid = true;
		if (email == null || !email.isValid()) {
			allValid = false;
		}
		return allValid;
	}
}
