package r01fui.base.components.breadcrumbs;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Lists;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Resource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Composite;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.locale.I18NKey;
import r01f.ui.i18n.UII18NService;
import r01f.ui.vaadin.view.VaadinComponentHasCaption;
import r01f.ui.vaadin.view.VaadinComponentHasIcon;
import r01f.ui.vaadin.view.VaadinNavigator;
import r01f.ui.vaadin.view.VaadinViewI18NMessagesCanBeUpdated;
import r01f.ui.vaadin.view.VaadinViewID;
import r01f.util.types.collections.CollectionUtils;

public class VaadinBreadCrumbs 	 
	 extends Composite 
  implements VaadinViewI18NMessagesCanBeUpdated {

	private static final long serialVersionUID = 2809187788911721517L;
/////////////////////////////////////////////////////////////////////////////////////////
// FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final transient UII18NService _i18n;
	
	private final Collection<VaadinBreadCrumb> _crumbs = Lists.newArrayList();
/////////////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinBreadCrumbs(final UII18NService i18n) {
		super.setCompositionRoot(new HorizontalLayout());
		_i18n = i18n;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected HorizontalLayout getCompositionRoot() {
		return (HorizontalLayout)super.getCompositionRoot();
	}
/////////////////////////////////////////////////////////////////////////////////////////
// PUBLIC METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public void addUnLinkedCrumb(final VaadinViewID to) {
		_addCrumb(I18NKey.forId(to),
				  null,
				  null);		// no click listener
	}
	public void addLinkedCrumb(final VaadinViewID to) {
		this.addLinkedCrumb(to,
							(Collection<String>)null);	// no params	
	}
	public void addLinkedCrumb(final VaadinViewID to,
							   final Collection<String> navParams) {
		final String urlPathParam = VaadinNavigator.vaadinViewUrlPathParamOf(to,navParams); 
		this.addLinkedCrumb(I18NKey.forId(to),
							selCrumb -> UI.getCurrent().getNavigator()
													   .navigateTo(urlPathParam));
	}
	public void addLinkedCrumb(final VaadinViewID to,
							   final Map<String,String> navParams) {
		final String urlPathParam = VaadinNavigator.vaadinViewUrlPathFragmentOf(to,navParams); 
		this.addLinkedCrumb(I18NKey.forId(to),
							selCrumb -> UI.getCurrent().getNavigator()
													   .navigateTo(urlPathParam));	
	}
	public void addUnLinkedCrumb(final I18NKey key) {
		_addCrumb(key,
				  null,
				  null);		// no click listener
	}
	public void addLinkedCrumb(final I18NKey key,
							   final VaadinBreadCrumbsCommand cmd) {
		_addCrumb(key,
				  null,
				  cmd);	
	}

	public void addLinkedCrumb(final I18NKey keyCaption,
							   final I18NKey keyDescription,			
							   final VaadinBreadCrumbsCommand cmd) {
		_addCrumb(keyCaption,
				  keyDescription,
				  cmd);	
	}
	
	private VaadinBreadCrumb _addCrumb(final I18NKey key,
									  final I18NKey keyDescription,
							 		  final VaadinBreadCrumbsCommand cmd) {
		// add a separator if there already exists any crumb
		boolean hasBreadCrumbs = this.getCompositionRoot()
								   	 .getComponentCount() > 0;
		if (hasBreadCrumbs) {
			Component separator = _createSeparator();
			this.getCompositionRoot().addComponents(separator);
			this.getCompositionRoot().setComponentAlignment(separator,
															Alignment.MIDDLE_CENTER);
		}
		// store the crumb for later use
		VaadinBreadCrumb crumb = new VaadinBreadCrumb(key,
													keyDescription,
													cmd);
		_crumbs.add(crumb);
		
		// add to the layout
		this.getCompositionRoot().addComponents(crumb.getComponent());
		this.getCompositionRoot().setComponentAlignment(crumb.getComponent(),
														Alignment.MIDDLE_CENTER);
		
		// add the click listener
		return crumb;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	protected Component _createSeparator() {
		Label separator = new Label();
		separator.setIcon(VaadinIcons.CHEVRON_RIGHT);	
		return separator;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	I18N                                                                          
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public void updateI18NMessages(final UII18NService i18n) {
		if (CollectionUtils.isNullOrEmpty(_crumbs)) return;
		for (VaadinBreadCrumb crumb : _crumbs) {
			crumb.updateI18NMessages(i18n);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor
	public class VaadinBreadCrumb 
	  implements VaadinViewI18NMessagesCanBeUpdated,
	  			 VaadinComponentHasCaption,VaadinComponentHasIcon {
				private final I18NKey _i18nkeyDescription;
		@Getter private final Component _component;
		@Getter private final I18NKey _i18nKey;

		private VaadinBreadCrumb(final I18NKey keyCaption,
							    final I18NKey keyDescription,
								final VaadinBreadCrumbsCommand cmd) {
			_i18nKey = keyCaption;
			_i18nkeyDescription	= keyDescription;
			
			// create the crumb
			String caption = _i18n.getMessage(_i18nKey);
			String description = "";
			if (_i18nkeyDescription != null) {
				description = _i18n.getMessage(_i18nkeyDescription);
			}
			
			// create the component
			if (cmd != null) {
				Button btn = _createCrumbButton(caption, description);
				btn.addClickListener(e -> cmd.crumbSelected(this));
				_component = btn;
			}
			else {
				Label label = _createCrumbLabel(caption, description);
				_component = label;
			}
		}
		
		private Button _createCrumbButton(final String caption, final String description) {
			final Button crumb = new Button();
			crumb.setCaption(caption);
			crumb.setDescription(description);
			crumb.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
			return crumb;
		}
		
		private Label _createCrumbLabel(final String caption, final String description) {
			final Label crumb = new Label();
			crumb.setValue(caption);
			crumb.setDescription(description);
			crumb.addStyleNames(ValoTheme.LABEL_HUGE, ValoTheme.LABEL_BOLD);
			return crumb;
		}
		
		@Override
		public String getCaption() {
			return _component.getCaption();
		}
		@Override
		public void setCaption(final String caption) {
			_component.setCaption(caption);
		}
		@Override
		public Resource getIcon() {
			return _component.getIcon();
		}
		@Override
		public void setIcon(final Resource resource) {
			_component.setIcon(resource);
		}
		@Override
		public void updateI18NMessages(final UII18NService i18n) {
			String caption = i18n.getMessage(_i18nKey);
			if (_component instanceof Label) { //unlinked
				((Label) _component).setValue(caption);
			}
			else { //linked
				_component.setCaption(caption);
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	COMMAND                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
    @FunctionalInterface
    public interface VaadinBreadCrumbsCommand 
             extends Serializable {
        public void crumbSelected(VaadinBreadCrumb selCrumb);
    }

}
