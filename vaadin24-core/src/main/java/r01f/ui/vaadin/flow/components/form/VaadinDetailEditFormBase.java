package r01f.ui.vaadin.flow.components.form;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.model.facets.view.HasCaptionLanguageDependent;
import r01f.patterns.Factory;
import r01f.ui.i18n.UII18NService;
import r01f.ui.presenter.UIPresenterSubscriber;
import r01f.ui.vaadin.flow.components.button.VaadinAcceptCancelDeleteButtons;
import r01f.ui.vaadin.flow.components.button.VaadinAcceptCancelDeleteButtons.VaadinAcceptCancelDeleteButton;
import r01f.ui.viewobject.UIViewObject;
import r01f.util.types.Strings;

/**
 * Creates a detail edit form like:
 * <pre>
 *      +++++++++++++++++++++++++++++++++++++++++++++++
 *      + [Form]                                      +
 *      +                                             +
 *      +                                             +
 *      +                                             +
 *      +---------------------------------------------+
 *      + [Delete]                  [Cancel] [Accept] +
 *      +++++++++++++++++++++++++++++++++++++++++++++++
 * </pre>
 * BEWARE! DO NOT CONFUSE with it's pop-up window counterpart {@link VaadinDetailEditFormWindowBase}
 * @param <V>
 */
@Accessors(prefix="_")
public abstract class VaadinDetailEditFormBase<V extends UIViewObject,
											   F extends VaadinDetailForm<V>
												       & VaadinFormEditsViewObject<V>>
     		  extends Composite<Div>
     	   implements VaadinDetailEditForm<V> {

	private static final long serialVersionUID = 9150166467660862183L;

/////////////////////////////////////////////////////////////////////////////////////////
//  SERVICES
/////////////////////////////////////////////////////////////////////////////////////////
	protected final transient UII18NService _i18n;

/////////////////////////////////////////////////////////////////////////////////////////
//	UI
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter protected final F _form;
			protected final Factory<V> _viewObjFactory;
			protected final VaadinAcceptCancelDeleteButtons _btnAcepCancDelete;

	// OUTSIDE WORLD SUBSCRIBERS
	protected UIPresenterSubscriber<V> _saveSubscriber;
	protected UIPresenterSubscriber<V> _deleteSubscriber;
	
	// the view object being managed
	private V _viewObj;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinDetailEditFormBase(final UII18NService i18n,
									final F form,
									final Factory<V> viewObjFactory) {
		_i18n = i18n;
		
		// Form & view obj factory
		_form = form;
		_viewObjFactory = viewObjFactory;

		// OK | CANCEL | DELETE
		_btnAcepCancDelete = new VaadinAcceptCancelDeleteButtons(i18n);

		// Layout
		this.getContent()
			.add(new Label(),			// title
				 (Component)_form,		// form must be a component
				 _btnAcepCancDelete);	// buttons
		
		// Behavior
		_setAcceptCancelDeleteButtonsBehavior();
	}
	private void _setAcceptCancelDeleteButtonsBehavior() {
		// - CANCEL
		_btnAcepCancDelete.addCancelButtonClickListner(clickEvent -> VaadinDetailEditFormBase.this.close());
		// - OK
		_btnAcepCancDelete.addAcceptButtonClickListner(clickEvent -> {
															// do any validation
															boolean valid = true;
															
															if (_form instanceof VaadinEditFormSelfValidates) {
																VaadinEditFormSelfValidates validates = (VaadinEditFormSelfValidates)_form;
																valid = validates.validate();
															}
															if (!valid)  {
																//Notification.show("Hay campos sin rellenar");
																return;
															}	
															
															// is valid or maybe not if not implementing VaadinEditFormSelfValidates														
															_form.writeAsDraftEditedViewObjectTo(_viewObj);
															if (_saveSubscriber != null) _saveSubscriber.onSuccess(_viewObj);
															this.close();
											  		  });
		// - DELETE
		_btnAcepCancDelete.addDeleteButtonClickListner(event -> {
															if (_deleteSubscriber != null) {
																// copy the edited object to a new [view object]
																V deletedViewObj = _viewObjFactory.create();
																_form.writeAsDraftEditedViewObjectTo(deletedViewObj);
																// tell the subscriber
																_deleteSubscriber.onSuccess(deletedViewObj);
															}
													   });
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CAPTION
/////////////////////////////////////////////////////////////////////////////////////////
	public Label getHeaderLabel() {
		return (Label)this.getContent()
				   		  .getComponentAt(0);
	}
	public void setHeaderText(final String text) {
		this.getHeaderLabel()
			.setText(text);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Performs any validation of the data: override if needed
	 * @param viewObj
	 * @return
	 */
	protected boolean _doValidateFormData(final V viewObj) {
		return true;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ENTRY POINT
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void forCreating(final UIPresenterSubscriber<V> saveSubscriber) {		
		// what to do after saving
		_saveSubscriber = saveSubscriber;
		_deleteSubscriber = null;				// cannot delete from a create window
		
		// create the view object
		_viewObj = _viewObjFactory.create();	// set _viewObj BEFORE binding!

		// bind the view object to the view
		_form.editViewObject(_viewObj);
		
		// set window caption
		this.setHeaderText(_i18n.getMessage(this.getNewItemCaptionI18NKey()));
		
		// set the buttons status
		_btnAcepCancDelete.setCreatingNewRecordStatus();
		this.setVisible(true);
	}
	@Override
	public void forEditing(final V viewObj,
						   final UIPresenterSubscriber<V> saveSubscriber,
						   final UIPresenterSubscriber<V> deleteSubscriber) {
		if (viewObj == null) throw new IllegalArgumentException("view object MUST NOT be null!");

		// store the subscribers
		_saveSubscriber = saveSubscriber;
		_deleteSubscriber = deleteSubscriber;
		
		// store the view object
		_viewObj = viewObj;						// set _viewObj BEFORE binding!
		
		// bind the view object to the view
		_form.editViewObject(_viewObj);		// BEWARE!!! the given [view object] will NOT be modified when the UI controls are updated!

		// sets window caption
		if (viewObj instanceof HasCaptionLanguageDependent) {
			HasCaptionLanguageDependent hasCaption = (HasCaptionLanguageDependent)viewObj;
			this.setHeaderText(Strings.customized("{}: {}",
											   	  _i18n.getMessage(this.getEditItemCaptionI18NKey()),
											   	  hasCaption.getCaption(_i18n.getCurrentLanguage())));
		} else {
			this.setHeaderText(_i18n.getMessage(this.getEditItemCaptionI18NKey()));
		}
		
		// set the buttons status
		_btnAcepCancDelete.setEditingExistingRecordStatus();
		
		
		// if no delete subscriber is handed, do NOT show the delete button
		if (_deleteSubscriber == null) _btnAcepCancDelete.setDeleteButtonVisible(false);
		this.setVisible(true);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void cancel() {
		// simulate the [cancel] button click
		_btnAcepCancDelete.cancel();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void addCancelButtonClickListner(final ComponentEventListener<ClickEvent<Button>> clickListener) {
		_btnAcepCancDelete.addCancelButtonClickListner(clickListener);
	}
	@Override
	public void addAcceptButtonClickListner(final ComponentEventListener<ClickEvent<Button>> clickListener) {
		_btnAcepCancDelete.addAcceptButtonClickListner(clickListener);
	}
	@Override
	public void addDeleteButtonClickListner(final ComponentEventListener<ClickEvent<Button>> clickListener) {
		_btnAcepCancDelete.addDeleteButtonClickListner(clickListener);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void setButtonsVisibleStatus(final boolean visible,
								  		final VaadinAcceptCancelDeleteButton... btns) {
		_btnAcepCancDelete.setButtonsVisibleStatus(visible, 
												   btns);
	}
	@Override
	public void setButtonsEnableStatus(final boolean enabled,
								  	   final VaadinAcceptCancelDeleteButton... btns) {
		_btnAcepCancDelete.setButtonsEnableStatus(enabled, 
												   btns);
	}
}
