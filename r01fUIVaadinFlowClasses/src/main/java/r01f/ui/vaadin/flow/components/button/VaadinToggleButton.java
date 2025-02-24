package r01f.ui.vaadin.flow.components.button;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import r01f.ui.vaadin.flow.view.VaadinViews;

/**
 * A {@link HasValue} button wrapper like a toggle. It can be use as field and can be bind with {@link VaadinViews}
 * <pre>
 * 		+=======================+
 * 		+ Captino [on [x]  off] +
 * 		+=======================+
 * </pre>
 */
public class VaadinToggleButton 
	 extends CustomField<Boolean> {

	private static final long serialVersionUID = 2192600562645846577L;
/////////////////////////////////////////////////////////////////////////////////////////
//	UI FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	private final Icon _toggleOn = new Icon("img/toggle-on.png");
	private final Icon _toggleOff = new Icon("img/toggle-off.png");
	private final Label _lblCaption = new Label();
	private final Button _btnToggle = new Button();
/////////////////////////////////////////////////////////////////////////////////////////
//	STATE (avoid as much as possible)
/////////////////////////////////////////////////////////////////////////////////////////	
	private boolean _value = false;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR / BUILDER
/////////////////////////////////////////////////////////////////////////////////////////	
	public VaadinToggleButton() {
		_btnToggle.setIcon(_toggleOff);
		HorizontalLayout hly = new HorizontalLayout(_lblCaption,_btnToggle);
		hly.setAlignItems(Alignment.END);
		hly.setMargin(false);
		this.add(hly);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EVENTS
/////////////////////////////////////////////////////////////////////////////////////////	
	public void addClickListener(final ComponentEventListener<ClickEvent<Button>> listener) {
		_btnToggle.addClickListener(clickEvent -> {
										this.setValue(!_value);
										if (listener != null)
											listener.onComponentEvent(clickEvent);
									});
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CAPTION
/////////////////////////////////////////////////////////////////////////////////////////
	public void setCaption(final String caption) {
		_lblCaption.setText(caption);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	VALUE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Boolean getValue() {
		return _value;
	}
	@Override
	protected Boolean generateModelValue() {
		return _value;
	}
	@Override
	protected void setPresentationValue(final Boolean value) {
		this.toggle(value);
	}	
	public void toggle(final boolean value) {
		_value = value;
		if (_value) {
			_btnToggle.setIcon(_toggleOn);
		} else {
			_btnToggle.setIcon(_toggleOff);
		}
	}
	public void toggle() {
		this.toggle(!_value);
	}
}