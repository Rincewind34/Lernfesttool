package de.rincewind.gui.util.listeners;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextInputControl;

public class NumberListener implements ChangeListener<String> {
	
	private TextInputControl control;
	
	public NumberListener(TextInputControl control) {
		this.control = control;
	}
	
	@Override
	public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		if (newValue.trim().isEmpty() || (oldValue != null && oldValue.equals(newValue))) {
			return;
		}
		
		try {
			int i = Integer.parseInt(newValue);
			
			if (i < 0) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException ex) {
			this.control.setText(oldValue != null ? oldValue : "");
		}
	}

}
