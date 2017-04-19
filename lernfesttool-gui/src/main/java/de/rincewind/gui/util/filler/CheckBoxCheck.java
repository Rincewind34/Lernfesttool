package de.rincewind.gui.util.filler;

import java.util.function.BiFunction;

import javafx.scene.control.CheckBox;

public class CheckBoxCheck<T> extends Checker<T> {
	
	private BiFunction<Boolean, T, Boolean> matcher;
	private CheckBox checkBox;
	
	public CheckBoxCheck(CheckBox checkBox, BiFunction<Boolean, T, Boolean> matcher) {
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
		return this.matcher.apply(this.checkBox.isSelected(), value);
	}
	
}
