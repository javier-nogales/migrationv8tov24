package r01ui.base.components.taglist;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.types.tag.WeightedLangDependentTag;
import r01f.ui.i18n.UII18NService;
import r01f.ui.vaadin.VaadinUI;
import r01f.ui.vaadin.styles.VaadinValoTheme;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * A [tag] selector field like:
 * <pre>
 * 		Tags: [write the tags separated by a space...]
 * 		[x] Tag 1    [x] Tag 2    [x] Tag3
 * </pre>
 */
public class VaadinWeightedLangDependentTagField 
	 extends CustomField<Set<WeightedLangDependentTag>> {

	private static final long serialVersionUID = 9128885563484557454L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final String SPACE_ALL_REGEX_PATTERN = "\\s+";
/////////////////////////////////////////////////////////////////////////////////////////
//	SERVICES
/////////////////////////////////////////////////////////////////////////////////////////
	private final transient UII18NService _i18n;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final TextField _txtTags = new TextField();
	private final HorizontalLayout _selectedLabels = new HorizontalLayout();
	
	private Registration _txtTagsShortcutListenerRegistration;
/////////////////////////////////////////////////////////////////////////////////////////
//	STATE (avoid as much as possible)
/////////////////////////////////////////////////////////////////////////////////////////
	private Set<WeightedLangDependentTag> _values = new LinkedHashSet<>();
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR / BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinWeightedLangDependentTagField(final UII18NService i18n) {
		_i18n = i18n;
		_selectedLabels.setStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PUBLIC METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Set<WeightedLangDependentTag> getValue() {
		return CollectionUtils.hasData(_values) ? _values : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PROTECTED METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override 
	protected Component initContent() {
		_txtTags.setSizeFull();
		ShortcutListener enterShortcutListener = new ShortcutListener("Shortcut Name",
																	  ShortcutAction.KeyCode.ENTER,
																	  null) {
														private static final long serialVersionUID = -4048866411794719253L;

														@Override
														public void handleAction(final Object sender,final Object target) {
															String tagFieldValue = _txtTags.getValue();
															if (Strings.isNOTNullOrEmpty(tagFieldValue)) {
																if (_hasDuplicatedValues(tagFieldValue.split(SPACE_ALL_REGEX_PATTERN),
																						 true)) {			// show notification
																	return;
																} else if (_isAnyTagAlreadyStored(tagFieldValue.split(SPACE_ALL_REGEX_PATTERN),
																								  true)) {	// show notification
																	return;
																} else {
																	final Set<WeightedLangDependentTag> newValues = new LinkedHashSet<>();
																	for (String tag: tagFieldValue.split(SPACE_ALL_REGEX_PATTERN)) {
																		WeightedLangDependentTag newTag = new WeightedLangDependentTag(tag,
																																	   VaadinUI.getCurrentLanguage(),
																																	   tag.length(),
																																	   null);	// no url
																		newValues.add(newTag);
																	}
																	Set<WeightedLangDependentTag> storedTagValues = new LinkedHashSet<>(_values);
																	storedTagValues.addAll(newValues);
																	VaadinWeightedLangDependentTagField.this.setValue(storedTagValues);
																	_txtTags.setValue("");
																}
															}
														}
													};
		_txtTags.addFocusListener(event -> {
										_txtTagsShortcutListenerRegistration = _txtTags.addShortcutListener(enterShortcutListener);
								   });
		_txtTags.addBlurListener(event -> {
										// _txtTags.removeShortcutListener(enterShortcutListener);	// deprecated
										_txtTagsShortcutListenerRegistration.remove();
								  });
		VerticalLayout root = new VerticalLayout(_txtTags,
												 _selectedLabels);
		root.setMargin(false);
		root.addStyleName(VaadinValoTheme.TAG_FIELD);
		return root;
	}
	@Override
	protected void doSetValue(final Set<WeightedLangDependentTag> value) {
		_selectedLabels.removeAllComponents();
		if (CollectionUtils.hasData(value)) {
			_values = value;
			_values.stream()
			   .forEach(vTag -> {
							VaadinWeightedLangDependentTagItemComponent tagItemComponent = new VaadinWeightedLangDependentTagItemComponent(vTag);
							_selectedLabels.addComponent(tagItemComponent);
			   			});
			_selectedLabels.removeStyleName(VaadinValoTheme.HIDE);
		} else {
			_values = Sets.newHashSet();
			_selectedLabels.addStyleName(VaadinValoTheme.HIDE);
		}

	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PRIVATE METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	private boolean _tagIsAlreadyStored(final String tag,
										final boolean showNotification) {
		if (_values != null) {
			Iterator<WeightedLangDependentTag> iterator = _values.iterator();
			List<String> dups = new ArrayList<>();
			while (iterator.hasNext()) {
				String tempTagValue = iterator.next().getTag();
				if (tempTagValue.trim().equals(tag)) {
					dups.add(tag);
				}
			}
			if (CollectionUtils.hasData(dups)) {
				if (showNotification) {
					Notification.show(_i18n.getMessage("tagfield.notification.already-stored-value") + dups.toString());
				}
				return true;
			}
		}
		return false;
	}

	private boolean _isAnyTagAlreadyStored(final String[] newTags,
										   final boolean showNotification) {
		List<String> dups = new ArrayList<>();
		for (String newTag: newTags) {
			if (_tagIsAlreadyStored(newTag,false)) {
				dups.add(newTag);
			}
		}
		if (CollectionUtils.hasData(dups)) {
			if (showNotification) {
				Notification.show(_i18n.getMessage("tagfield.notification.already-stored-values") + dups.toString());
			}
			return true;
		}
		return false;
	}

	private boolean _hasDuplicatedValues(final String[] newTags,
										 final boolean showNotification) {
		Set<String> store = new HashSet<>();
		List<String> dups = new ArrayList<>();
		for (String tag : newTags) {
			if (store.add(tag) == false) {
				dups.add(tag);
			}
		}
		if (CollectionUtils.hasData(dups)) {
			if (showNotification) {
				Notification.show(_i18n.getMessage("tagfield.notification.duplicated-values") + dups.toString());
			}
			return true;
		}
		return false;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	private class VaadinWeightedLangDependentTagItemComponent
		  extends HorizontalLayout {
		private static final long serialVersionUID = 4593107003581739154L;
		@Getter private final WeightedLangDependentTag _value;

		public VaadinWeightedLangDependentTagItemComponent(final WeightedLangDependentTag value) {
			_value = value;
			setMargin(new MarginInfo(false, false, false, true));
			setStyleName(ValoTheme.LAYOUT_CARD);
			Label labelText = new Label(value.getTag());
			Button closeButton = new Button("x");
			closeButton.setData(value);
			closeButton.setStyleName(ValoTheme.BUTTON_BORDERLESS);
			closeButton.addClickListener(eventClick -> {
												final Set<WeightedLangDependentTag> newValue = new LinkedHashSet<>(_values);
												newValue.remove(value);
												_selectedLabels.removeComponent(this);
												setValue(newValue);
										  });
			addComponents(labelText,closeButton);
			setComponentAlignment(labelText, Alignment.MIDDLE_CENTER);
		}
	}
	public void setTextFieldPlaceholder(final String placeholder) {
		_txtTags.setPlaceholder(placeholder);
	}
}