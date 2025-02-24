package r01ui.base.components.images;

import com.vaadin.ui.Composite;
import com.vaadin.ui.Image;
import com.vaadin.ui.VerticalLayout;

import lombok.experimental.Accessors;
import r01f.ui.i18n.UII18NService;
import r01f.ui.vaadin.view.VaadinViewI18NMessagesCanBeUpdated;
import r01f.ui.vaadin.view.VaadinViews;
import r01ui.base.components.form.VaadinDetailForm;
import r01ui.base.components.form.VaadinFormEditsViewObject;

@Accessors(prefix="_")
public class R01UIImageForm
     extends Composite 
  implements VaadinDetailForm<R01UIViewObjForImage>,
  			 VaadinFormEditsViewObject<R01UIViewObjForImage>,
  			 VaadinViewI18NMessagesCanBeUpdated {

	private static final long serialVersionUID = -4453566840953548508L;
/////////////////////////////////////////////////////////////////////////////////////////
//	SERVICES
/////////////////////////////////////////////////////////////////////////////////////////
	protected final transient UII18NService _i18n;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  UI
/////////////////////////////////////////////////////////////////////////////////////////
	private Image _image;
	private final VerticalLayout _vly = new VerticalLayout();
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public R01UIImageForm(final UII18NService i18n) {
		_i18n = i18n;
		////////// UI

		// set ui labels
		VaadinViews.using(i18n)
				   .setI18NLabelsOf(this);
		
		this.setCompositionRoot(_vly);
		this.setWidthFull();
		
		_setBehaviour();
	}	
	
	private void _setBehaviour() {
		// Empty Block
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Binding
/////////////////////////////////////////////////////////////////////////////////////////
	////////// [viewObject] > [UI control] --------------
	@Override
	public void editViewObject(final R01UIViewObjForImage viewObj) {
		_image = viewObj.getWrappedModelObject();
		_vly.addComponent(_image);
	}
	////////// [UI control] > [viewObject] --------------
	@Override
	public void writeAsDraftEditedViewObjectTo(final R01UIViewObjForImage viewObj) {
		// Nothing to do
	}
	@Override
	public boolean writeIfValidEditedViewObjectTo(final R01UIViewObjForImage viewObj) {
		// Nothing to do
		return true;
	}
}
