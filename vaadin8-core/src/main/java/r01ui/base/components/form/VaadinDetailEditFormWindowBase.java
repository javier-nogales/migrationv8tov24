package r01ui.base.components.form;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.patterns.Factory;
import r01f.ui.i18n.UII18NService;
import r01f.ui.presenter.UIPresenterSubscriber;
import r01f.ui.viewobject.UIViewObject;
import r01ui.base.components.button.VaadinAcceptCancelDeleteButtons.VaadinAcceptCancelDeleteButton;

/**
 * Wraps a {@link VaadinDetailEditFormBase} and creates a detail edit popup-window like:
 * <pre>
 * 		+---------------------------------------------+
 * 	    + Caption                                     +
 *      +---------------------------------------------+
 *      | [Form]                                      |
 *      |                                             |
 *      |                                             |
 *      |                                             |
 *      +---------------------------------------------+
 *      + [Delete]                  [Cancel] [Accept] +
 *      +---------------------------------------------+
 * </pre>
 * This window just wraps a {@link VaadinDetailEditFormBase}
 * @param <V>
 */
@Accessors (prefix = "_")
public abstract class VaadinDetailEditFormWindowBase<V extends UIViewObject,
												 	 F extends VaadinDetailForm<V>
															 & VaadinFormEditsViewObject<V>>
     		  extends Window
     	   implements VaadinDetailEditForm<V> {

	private static final long serialVersionUID = 7719084020409366076L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	protected final Factory<V> _viewObjFactory;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	STATUS
/////////////////////////////////////////////////////////////////////////////////////////
	// Status parameter to avoid cancel event after closing window on save button click
	protected boolean _savedForm = false;
/////////////////////////////////////////////////////////////////////////////////////////
//	UI
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The edit form layer 
	 */
	@Getter protected final VaadinDetailEditFormBase<V,F> _editForm;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinDetailEditFormWindowBase(final UII18NService i18n,
									  	  final F form,
									  	  final Factory<V> viewObjFactory) {
		this(i18n,
			 form,viewObjFactory,
			 false);	// not bottom sticky buttons
	}
	public VaadinDetailEditFormWindowBase(final UII18NService i18n,
									  	  final F form,
									  	  final Factory<V> viewObjFactory,
									  	  final boolean bottomStickyButtons) {
		_viewObjFactory = viewObjFactory;

		// ui
		_editForm = new VaadinDetailEditFormBase<>(i18n,
												      form,
												      viewObjFactory) {
							private static final long serialVersionUID = 7219012021409356071L;
					};
		_editForm.addCancelButtonClickListner(clickEvent -> this.close());
		if (bottomStickyButtons) {
			VerticalLayout vl = new VerticalLayout();
			vl.setSizeFull();
			vl.setSpacing(false);
			
			Panel p = new Panel(_editForm);
			p.setSizeFull();
			p.addStyleName(ValoTheme.PANEL_BORDERLESS);
			vl.addComponent(p);
			vl.addComponent(new CssLayout(_editForm._btnAcepCancDelete));
			vl.setComponentAlignment(vl.getComponent(vl.getComponentCount()-1),Alignment.MIDDLE_RIGHT);
			
			vl.setExpandRatio(p,1); 
			
			this.setContent(vl);		// wrap into a VerticalLayout to get a bit of margin
		} else {
			this.setContent(new VerticalLayout(_editForm));
		}
		// style window
		this.center();
		this.setModal(true);
		this.setCaption("");
		this.setResizable(false);
		
		// when clicking the pop-up [X] window simulate the [cancel] button click
		this.addCloseListener(closeEvent -> {
									// tell the edit form to cancel > this will "simulate" the [cancel] button click
									// this listener was executed on all close events (save,cancel,x) not only for [X], with the parameter is not executed on close after saving
									if (!_savedForm) {
										_editForm.cancel();
									}
							  });
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public F getForm() {
		return _editForm != null ? _editForm.getForm()
								 : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ENTRY POINT
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void forCreating(final UIPresenterSubscriber<V> saveSubscriber) {
		// just delegate
		_editForm.forCreating(createdViewObj -> {
								  if (saveSubscriber != null) saveSubscriber.onSuccess(createdViewObj);
								  this.close();
							  });
	}
	@Override
	public void forEditing(final V viewObj,
						   final UIPresenterSubscriber<V> saveSubscriber,
						   final UIPresenterSubscriber<V> deleteSubscriber) {
		// just delegate
		_editForm.forEditing(viewObj,
							 savedViewObj -> {
								  if (saveSubscriber != null) saveSubscriber.onSuccess(savedViewObj);
								  _savedForm = true;
								  this.close();
							 },
							 deleteSubscriber != null ? deletedViewObj -> {
									   						deleteSubscriber.onSuccess(deletedViewObj);
									   						this.close();
								   					    }
								 					  : null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void addCancelButtonClickListner(final ClickListener clickListener) {
		_editForm.addCancelButtonClickListner(clickListener);
	}
	@Override
	public void addAcceptButtonClickListner(final ClickListener clickListener) {
		_editForm.addAcceptButtonClickListner(clickListener);
	}
	@Override
	public void addDeleteButtonClickListner(final ClickListener clickListener) {
		_editForm.addDeleteButtonClickListner(clickListener);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void setButtonsVisibleStatus(final boolean visible,
								  		final VaadinAcceptCancelDeleteButton... btns) {
		// just delegate
		_editForm.setButtonsVisibleStatus(visible,
										  btns);
	}
	@Override
	public void setButtonsEnableStatus(final boolean enabled,
								  	   final VaadinAcceptCancelDeleteButton... btns) {
		// just delegate
		_editForm.setButtonsEnableStatus(enabled, 
										 btns);
	}
}
