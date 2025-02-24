package r01f.ui.vaadin.flow.components.window;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import lombok.experimental.Accessors;
import r01f.guids.PersistableObjectOID;
import r01f.ui.i18n.UII18NService;
import r01f.ui.presenter.UIObjectDetailLanguageDependentPresenter;
import r01f.ui.presenter.UIPresenterSubscriber;
import r01f.ui.viewobject.UIViewObject;

@Accessors( prefix="_" )
public abstract class VaadinDeleteConfirmDialogVerificationBase<O extends PersistableObjectOID,V extends UIViewObject,
												   				P extends UIObjectDetailLanguageDependentPresenter<O,V>>		// core mediator
	 		  extends Dialog {

	private static final long serialVersionUID = 7440334832528162438L;
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
	public VaadinDeleteConfirmDialogVerificationBase(final UII18NService i18n,
													final P presenter,
													final String name) {
		_i18n = i18n;
		_presenter = presenter;

		this.setModal(true);
		this.setResizable(false);
		this.setWidth(30,Unit.PERCENTAGE);
		this.setHeaderTitle(_i18n.getMessage("confirm"));

		//	DELETE CONFIRM DIALOG
		Label lblDelConfirm = new Label(_i18n.getMessage("delete.confirm.verification"));

		//	TEXT LABEL
		Label lblMsg = new Label(_i18n.getMessage("delete.message"));

		//	CONFIRM TEXTFIELD
		TextField txtConfirm = new TextField();

		// DELETE
		Button btnAccept = new Button(_i18n.getMessage("delete"));
		btnAccept.addThemeVariants(ButtonVariant.LUMO_ERROR);

		// CANCEL
		Button btnCancel = new Button(_i18n.getMessage("cancel"));

		// Layout
		HorizontalLayout ly = new HorizontalLayout(lblMsg,txtConfirm);

		HorizontalLayout lyDelConfirm = new HorizontalLayout(btnCancel,btnAccept);
		lyDelConfirm.setFlexGrow(1,btnAccept);

		VerticalLayout lyDelConfirmVerification = new VerticalLayout(lblDelConfirm,
										  			  				 ly,
										  			  				 lyDelConfirm);

		this.add(lyDelConfirmVerification);
		
		// behavior
		_setBehavior(txtConfirm,
					 btnAccept,btnCancel);
	}
	private void _setBehavior(final TextField txtConfirm,
							  final Button btnAccept,final Button btnCancel) {
		// if the user enters "Eliminar", enable the [accept] button
		txtConfirm.addValueChangeListener(valChange -> {
												btnAccept.setEnabled(txtConfirm.getValue().equals("Eliminar"));
										  });
		// accept button
		btnAccept.addClickListener(clickEvent -> {
										_presenter.onDeleteRequested(_objToBeDeletedOid,
																	 _i18n.getCurrentLanguage(),
																	 _subscriber);			// the presenter will tell the subscriber...
										VaadinDeleteConfirmDialogVerificationBase.this.close();
										txtConfirm.clear();
								   });
		// cancel button
		btnCancel.addClickListener(clickEvent -> {
										VaadinDeleteConfirmDialogVerificationBase.this.close();
								   });
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