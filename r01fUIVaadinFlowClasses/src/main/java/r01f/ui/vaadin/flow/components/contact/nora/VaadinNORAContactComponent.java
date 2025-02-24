package r01f.ui.vaadin.flow.components.contact.nora;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import r01f.types.geo.GeoPosition2D;
import r01f.ui.i18n.UII18NService;
import r01f.ui.vaadin.flow.components.form.VaadinViewTracksChanges;
import r01f.util.types.Strings;

@Slf4j
public class VaadinNORAContactComponent
     extends CustomField<VaadinViewGeoPosition>
  implements VaadinViewTracksChanges {
	private static final long serialVersionUID = 5018317833169484242L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final VaadinNORAContactForm _contactForm;
	private final IFrame _iFrameMap;
	private VaadinViewGeoPosition _viewObject;
	
	private boolean _changed = false;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public VaadinNORAContactComponent(final UII18NService i18n,
									  final VaadinNORAContactFormPresenter presenter) {
		_contactForm = new VaadinNORAContactForm(i18n,
												 presenter);
		_iFrameMap = new IFrame("Mapa");
		_iFrameMap.setWidth("600px");
		_iFrameMap.setHeight("400px");

		////////// Layout
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSizeFull();
		hl.setMargin(true);
		hl.add(_contactForm,_iFrameMap);
		
		////////// Behavior
		_setBehavior();
	}
	private void _setBehavior() {
		_contactForm.setVaadinNORACoordsChangeListener(coordsChangeEvent -> {
															GeoPosition2D geo = _contactForm.getGeoPositionForETRS89Standard();
															if (geo == null) return;
															if (geo.getX() == 0 || geo.getY() == 0) {
																_iFrameMap.setSrc(null);
															} else {
																String resourceName = Strings.customized("components/geocoder/previewMap.html?x={}&y={}&zoom={}",
																										 geo.getX(),geo.getY(),_contactForm.getZoomLevel());
																log.debug("coords x,y={},{} (format={}) zoom={} > resource={}",
																		  geo.getStandard(),
																		  geo.getX(),geo.getY(),
																		  _contactForm.getZoomLevel(),
																		  resourceName);
																_iFrameMap.setSrc(resourceName);
															}
													  });
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected VaadinViewGeoPosition generateModelValue() {
		if (_viewObject != null) _contactForm.writeAsDraftEditedViewObjectTo(_viewObject);
		return _viewObject;
	}
	@Override
	protected void setPresentationValue(final VaadinViewGeoPosition value) {
		_viewObject = value;
		_contactForm.editViewObject(_viewObject);
	}
	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
		if (!enabled) _iFrameMap.setSrc(null);
		_contactForm.setEnabled(enabled);
	}
	public void hideMap (final boolean hide) {
		if (hide) {
			_iFrameMap.setSrc(null);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CHANGES
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public void setViewDataChanged(final boolean changed) {
		_changed = changed;
	}
	@Override
	public boolean hasViewDataChanged() {
		final boolean changed = _changed;
		_changed = false;
		return _contactForm.hasViewDataChanged() || changed;
	}
}
