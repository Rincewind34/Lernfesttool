package de.rincewind.gui.util.filler;

import javafx.scene.control.CheckBox;

public class CheckBoxCheck<T> extends Checker<T> {
	
	private Matcher<Boolean, T> matcher;
	private CheckBox checkBox;
	
	public CheckBoxCheck(CheckBox checkBox, Matcher<Boolean, T> matcher) {
		this.checkBox = checkBox;
		this.matcher = matcher;
		this.checkBox.selectedProperty().addListener((observeable, oldValue, newValue) -> {
			if ((oldValue == null || newValue == null) || !oldValue.equals(newValue)) {
				this.valueChanged();
			}
		});
	}
	
	@Override
	public boolean check(T value) {
		return this.matcher.test(this.checkBox.isSelected(), value);
	}
	
}
