package r01ui.base.components.images;


import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Image;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import r01f.ui.i18n.UII18NService;
import r01ui.base.components.button.VaadinAcceptCancelDeleteButtons.VaadinAcceptCancelDeleteButton;
import r01ui.base.components.form.VaadinDetailEditForm;
import r01ui.base.components.form.VaadinDetailEditFormWindowBase;


/**
 * A component to view the miniaturized image in a higher size window
 */
public class VaadinImagePopup
	 extends CustomComponent {
  
	private static final long serialVersionUID = 8232373798205675214L;
/////////////////////////////////////////////////////////////////////////////////////////
//	SERVICES
/////////////////////////////////////////////////////////////////////////////////////////
	private final UII18NService _i18n;
/////////////////////////////////////////////////////////////////////////////////////////
//	UI
/////////////////////////////////////////////////////////////////////////////////////////
	private Image _image;

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR / BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinImagePopup(final UII18NService i18n) {
		_i18n = i18n;

		////////// composition
		this.setCompositionRoot(_image);
	}
	
	public void openImage(Image image) {
		_image = image;
		_image.setWidth(100, Unit.PERCENTAGE);
		_image.setHeight(100, Unit.PERCENTAGE);
		
		// open a pop-up to create a new [warning]
		VaadinDetailEditForm<R01UIViewObjForImage> form = _createImageWindow(false);
		form.forCreating(shownImage -> {
						   		// Nothing to do
					   	});
		form.setButtonsVisibleStatus(false, VaadinAcceptCancelDeleteButton.ACCEPT, VaadinAcceptCancelDeleteButton.CANCEL);
		UI.getCurrent()
		  .addWindow((Window)form);
	}
	
	private VaadinDetailEditForm<R01UIViewObjForImage> _createImageWindow(final boolean editMode) {
		VaadinDetailEditFormWindowBase<R01UIViewObjForImage,
									   R01UIImageForm> outWin = null;
		outWin = new VaadinDetailEditFormWindowBase<R01UIViewObjForImage,
													R01UIImageForm>(_i18n,
																	// form
																	new R01UIImageForm(_i18n),
																	// view object factory
																	() -> {
																		return new R01UIViewObjForImage(_image);
																	},
																	true) {	// bottom sticky buttons
						private static final long serialVersionUID = -4455464449338753438L;
				};
		outWin.setWidth(35,Unit.PERCENTAGE);
		outWin.setHeight(75,Unit.PERCENTAGE);

		return outWin;
	}
}
