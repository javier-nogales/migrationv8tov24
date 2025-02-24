package r01f.ui.vaadin.flow.components.form;


import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.patterns.Factory;
import r01f.ui.i18n.UII18NService;
import r01f.ui.presenter.UIPresenterSubscriber;
import r01f.ui.vaadin.flow.components.button.VaadinAcceptCancelDeleteButtons.VaadinAcceptCancelDeleteButton;
import r01f.ui.viewobject.UIViewObject;

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
     		  extends Dialog
     	   implements VaadinDetailEditForm<V> {

	private static final long serialVersionUID = 7719084020409366076L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	protected final Factory<V> _viewObjFactory;
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
		_viewObjFactory = viewObjFactory;

		// ui
		_editForm = new VaadinDetailEditFormBase<V,F>(i18n,
												      form,
												      viewObjFactory) {
							private static final long serialVersionUID = 7219012021409356071L;
				    };

		this.add(_editForm);
		
		// style window
		this.setModal(true);
		this.setResizable(false);
		
		// behavior
		_setBehavior();
	}
	private void _setBehavior() {
		// when canceling the edition, close the dialog
		_editForm.addCancelButtonClickListner(clickEvent -> this.close());
		
		// when clicking the pop-up [X] window simulate the [cancel] button click
		this.addDialogCloseActionListener(closeEvent -> {
												// tell the edit form to cancel > this will "simulate" the [cancel] button click
												_editForm.cancel();
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
								  this.close();
							 },
							 (deleteSubscriber != null) 
								 ? deletedViewObj -> {
									   deleteSubscriber.onSuccess(deletedViewObj);
									   this.close();
								   }
								 : null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void addCancelButtonClickListner(final ComponentEventListener<ClickEvent<Button>> clickListener) {
		_editForm.addCancelButtonClickListner(clickListener);
	}
	@Override
	public void addAcceptButtonClickListner(final ComponentEventListener<ClickEvent<Button>> clickListener) {
		_editForm.addAcceptButtonClickListner(clickListener);
	}
	@Override
	public void addDeleteButtonClickListner(final ComponentEventListener<ClickEvent<Button>> clickListener) {
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
