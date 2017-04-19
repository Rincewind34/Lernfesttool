package de.rincewind.gui.util.filler;

import java.util.function.BiFunction;

import javafx.scene.control.ListView;

public class ListCheck<T, U> extends Checker<T> {

	private BiFunction<T, U, Boolean> matcher;
	private ListView<U> list;

	public ListCheck(ListView<U> list, BiFunction<T, U, Boolean> matcher) {
		this.matcher = matcher;
		this.list = list;
		this.list.getSelectionModel().selectedItemProperty().addListener((observeable, oldValue, newValue) -> {
			if ((oldValue == null || newValue == null) || !oldValue.equals(newValue)) {
				this.valueChanged();
			}
		});
	}

	@Override
	public boolean check(T value) {
		return this.matcher.apply(value, this.list.getSelectionModel().getSelectedItem());
	}

}
