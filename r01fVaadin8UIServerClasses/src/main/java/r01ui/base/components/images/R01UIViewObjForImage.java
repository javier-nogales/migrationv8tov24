package r01ui.base.components.images;

import com.vaadin.ui.Image;

import lombok.experimental.Accessors;
import r01f.ui.viewobject.UIViewObjectWrappedBase;

@Accessors(prefix = "_")
public class R01UIViewObjForImage
	 extends UIViewObjectWrappedBase<Image> {

	
	private static final long serialVersionUID = 5733984062740153386L;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	public R01UIViewObjForImage(final Image obj) {
		super(obj);
	}
	public static final R01UIViewObjForImage from(final Image obj) {
		R01UIViewObjForImage viewObj = new R01UIViewObjForImage(obj);
		return viewObj;
	}

}
