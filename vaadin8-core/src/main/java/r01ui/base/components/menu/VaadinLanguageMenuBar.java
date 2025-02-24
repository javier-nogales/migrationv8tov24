package r01ui.base.components.menu;

import java.util.Collection;
import java.util.stream.Collectors;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.MenuBar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.locale.Language;
import r01f.ui.i18n.UII18NService;
import r01f.ui.subscriber.UISubscriber;
import r01f.ui.vaadin.view.VaadinViewI18NMessagesCanBeUpdated;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.collections.Lists;
import r01f.util.types.locale.Languages;

@Accessors(prefix = "_")
public class VaadinLanguageMenuBar
	 extends MenuBar
  implements VaadinViewI18NMessagesCanBeUpdated {

	private static final long serialVersionUID = 2278398675813748703L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final Collection<Language> DEFAULT_AVAILABLE_LANGAGES = Language.valueSet()
																		   		   .stream()
																		   		   .filter(language -> (language.isNOT(Language.SPANISH)
																				  				  			&& language.isNOT(Language.BASQUE)
																				  				  			&& language.isNOT(Language.ANY)))
																		   		   .collect(Collectors.toList());
/////////////////////////////////////////////////////////////////////////////////////////
//	SERVICES
/////////////////////////////////////////////////////////////////////////////////////////
	protected final transient UII18NService _i18n;
//////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final Collection<Language> _availableLanguages;
/////////////////////////////////////////////////////////////////////////////////////////
//	UI
/////////////////////////////////////////////////////////////////////////////////////////	
	private final MenuItem _menuLangItemRoot;

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinLanguageMenuBar(final UII18NService i18n) {
		this(i18n,
			 DEFAULT_AVAILABLE_LANGAGES);
	}

	public VaadinLanguageMenuBar(final UII18NService i18n,
								 final Collection<Language> availableLanguages) {
		_i18n = i18n;
		_availableLanguages = availableLanguages;
		_menuLangItemRoot = addItem("", VaadinIcons.GLOBE, null);
		_menuLangItemRoot.setDescription(i18n.getMessage("add-language.menu-bar"));
		if (CollectionUtils.hasData(_menuLangItemRoot.getChildren())) {
			_menuLangItemRoot.getChildren()
							 .stream()
							 .forEach(langMenuItem -> {
									 		if (langMenuItem.getCommand() != null) {
									 			VaadinLanguageMenuBarCommand langCommand = (VaadinLanguageMenuBarCommand)langMenuItem.getCommand();
									 			langMenuItem.setText(Languages.nameUsing(i18n)
									 										  .of(langCommand.getLang()));
								 			}
							 		 });
		}
	}
//////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public void addAvailableLanguages(final Collection<Language> definedLanguages,
				   					  final UISubscriber<Collection<Language>> subscriber) {
		for (Language lang : _availableLanguages) {
			MenuItem subItem = _menuLangItemRoot.addItem(Languages.nameUsing(_i18n).of(lang),
														 new VaadinLanguageMenuBarCommand(lang) {
																private static final long serialVersionUID = 1280767598251538150L;
																@Override
																public void menuSelected(final MenuItem selectedItem) {
																	Collection<Language> languages = Lists.newArrayList(definedLanguages);
																	if (selectedItem.isChecked()) {
																		languages.add(lang);
																	}else {
																		languages.remove(lang);
																	}
																	subscriber.onSuccess(languages);
																}
														 });

			subItem.setCheckable(true);
			subItem.setChecked(definedLanguages.contains(lang));
		}
	}
	@Override
	public void updateI18NMessages(final UII18NService i18n) {
	    _menuLangItemRoot.setDescription(i18n.getMessage("add-language.menu-bar"));
	    _menuLangItemRoot.getChildren()
				     .stream()
				     .forEach(langMenuItem -> {
				    	 						if (langMenuItem.getCommand() != null) {
				    	 							VaadinLanguageMenuBarCommand langCommand = (VaadinLanguageMenuBarCommand)langMenuItem.getCommand();
				    	 							langMenuItem.setText(Languages.nameUsing(_i18n)
				    	 														  .of(langCommand.getLang()));
				    	 						}
				     						  });
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	COMMANDS
/////////////////////////////////////////////////////////////////////////////////////////
	@AllArgsConstructor
	@Accessors(prefix = "_")
	protected abstract class VaadinLanguageMenuBarCommand
				  implements Command{
		private static final long serialVersionUID = 7852383315934822029L;
		@Getter private final Language _lang;
	}
}
