package r01f.ui.vaadin.flow;


import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.textfield.TextField;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.util.types.Numbers;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class VaadinEvents {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static <T,
				   C1 extends Component & HasValue<? extends ValueChangeEvent<T>,T>,
				   C2 extends Component & HasValue<? extends ValueChangeEvent<T>,T>> 
			ComponentValueChangeEvent<C1,T> wrapValueChangeEvent(final C1 comp,final C2 src,
													 			 final ValueChangeEvent<T> c1Event) {
		return new ComponentValueChangeEvent<C1,T>(comp,src,
											       c1Event.getOldValue(),
											       c1Event.isFromClient());	// from client?
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Creates a TextField's {@link ValueChangeListener} that just allows numbers
	 * @param txtField
	 * @return
	 */
	public static final HasValue.ValueChangeListener<? super ComponentValueChangeEvent<TextField,String>> createOnlyNumbersTextFieldValueChangeListener(final TextField txtField) {
		return valChangeEvent -> {
						// ensure only ints
						String val = valChangeEvent.getValue();
						if (val == null || !Numbers.isInteger(val)) {
							txtField.setValue("");
						}
			   };
	}
}
