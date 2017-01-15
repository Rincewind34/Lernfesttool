package de.rincewind.gui.util;

import javafx.beans.value.ObservableValue;

public class SaveHandler {
	
	private boolean valueChanged;
	
	public SaveHandler() {
		this.valueChanged = false;
	}
	
	public void reset() {
		this.valueChanged = false;
	}
	
	public void valueChanged() {
		this.valueChanged = true;
	}
	
	public void addValue(ObservableValue<?> value) {
		value.addListener((observeable, oldValue, newValue) -> {
			if (oldValue == null && newValue == null) {
				return;
			}
			
			if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
				return;
			}
			
			this.valueChanged();
		});
	}
	
	public boolean hasValueChanged() {
		return this.valueChanged;
	}
	
}
