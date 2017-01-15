package de.rincewind.gui.util.filler;

import javafx.scene.control.ComboBox;

public class BoxCheck<T, U> extends Checker<T> {
	
	private Matcher<T, U> matcher;
	private ComboBox<U> comboBox;
	
	public BoxCheck(ComboBox<U> comboBox, Matcher<T, U>  matcher) {
		this.matcher = matcher;
		this.comboBox = comboBox;
		this.comboBox.getSelectionModel().selectedItemProperty().addListener((observeable, oldValue, newValue) -> {
			if ((oldValue == null || newValue == null) || !oldValue.equals(newValue)) {
				this.valueChanged();
			}
		});
	}
	
	@Override
	public boolean check(T value) {
		if (this.comboBox.getSelectionModel().getSelectedItem() == null) {
			return true;
		} else {
			return this.matcher.test(value, this.comboBox.getSelectionModel().getSelectedItem());
		}
	}
	
}
