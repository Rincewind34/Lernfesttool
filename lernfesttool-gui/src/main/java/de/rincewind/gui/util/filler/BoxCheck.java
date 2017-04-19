package de.rincewind.gui.util.filler;

import java.util.function.BiFunction;

import javafx.scene.control.ComboBox;

public class BoxCheck<T, U> extends Checker<T> {
	
	private BiFunction<T, U, Boolean> matcher;
	private ComboBox<U> comboBox;
	
	public BoxCheck(ComboBox<U> comboBox, BiFunction<T, U, Boolean>  matcher) {
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
			return this.matcher.apply(value, this.comboBox.getSelectionModel().getSelectedItem());
		}
	}
	
}
