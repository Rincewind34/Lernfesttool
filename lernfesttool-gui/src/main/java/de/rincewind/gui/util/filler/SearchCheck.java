package de.rincewind.gui.util.filler;

import javafx.scene.control.TextField;

public class SearchCheck<T> extends Checker<T> {
	
	private TextField textSearch;
	
	public SearchCheck(TextField textSearch) {
		this.textSearch = textSearch;
		this.textSearch.textProperty().addListener((observable, oldValue, newValue) -> {
			this.valueChanged();
		});
	}
	
	@Override
	public boolean check(T value) {
		return value.toString().contains(this.textSearch.getText());
	}

}
