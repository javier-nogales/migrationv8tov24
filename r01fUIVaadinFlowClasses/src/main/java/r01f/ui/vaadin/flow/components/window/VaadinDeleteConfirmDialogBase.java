package r01f.ui.vaadin.flow.components.window;


import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import lombok.experimental.Accessors;
import r01f.guids.PersistableObjectOID;
import r01f.ui.i18n.UII18NService;
import r01f.ui.presenter.UIObjectDetailLanguageDependentPresenter;
import r01f.ui.presenter.UIPresenterSubscriber;
import r01f.ui.viewobject.UIViewObject;

@Accessors( prefix="_" )
public abstract class VaadinDeleteConfirmDialogBase<O extends PersistableObjectOID,V extends UIViewObject,
												    P extends UIObjectDetailLanguageDependentPresenter<O,V>>		// core mediator
	 		  extends Dialog {

	private static final long serialVersionUID = 67118991862242129L;
/////////////////////////////////////////////////////////////////////////////////////////
//  I18N
/////////////////////////////////////////////////////////////////////////////////////////
	private final transient UII18NService _i18n;
/////////////////////////////////////////////////////////////////////////////////////////
// 	PRESENTER
/////////////////////////////////////////////////////////////////////////////////////////
	private final P _presenter;

/////////////////////////////////////////////////////////////////////////////////////////
//  OUTSIDE WORLD SUBSCRIBERS & DATA
/////////////////////////////////////////////////////////////////////////////////////////
	private O _objToBeDeletedOid;
	private UIPresenterSubscriber<V> _subscriber;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinDeleteConfirmDialogBase(final UII18NService i18n,
										 final P presenter) {
		_i18n = i18n;
		_presenter = presenter;

		this.setModal(true);
		this.setWidth( 50,Unit.PERCENTAGE );
		this.setHeaderTitle(i18n.getMessage("confirm"));
		this.setResizable(false);

		// DELETE
		Button btnAccept = new Button();
		btnAccept.setText(i18n.getMessage("delete"));
		btnAccept.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		// CANCEL
		Button btnCancel = new Button();
		btnCancel.setText( i18n.getMessage("cancel"));

		// layout
		HorizontalLayout lyDelConfirm = new HorizontalLayout(btnCancel,btnAccept);

		// add to dialog
		this.add(lyDelConfirm);
		
		// Behavior
		_setBehavior(btnAccept,btnCancel);
	}
	private void _setBehavior(final Button btnAccept,final Button btnCancel) {
		btnAccept.addClickListener(clickEvent -> {
										_presenter.onDeleteRequested(_objToBeDeletedOid,
																	 _i18n.getCurrentLanguage(),
																	 _subscriber);			// the presenter will tell the subscriber...
										VaadinDeleteConfirmDialogBase.this.close();
								   });
		btnCancel.addClickListener(event -> VaadinDeleteConfirmDialogBase.this.close());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PUBLIC ENTRY POINT
/////////////////////////////////////////////////////////////////////////////////////////
	public void setObjToBeDeletedOid(final O oid,
									 final UIPresenterSubscriber<V> subscriber) {
		_objToBeDeletedOid = oid;
		_subscriber = subscriber;
	}

}