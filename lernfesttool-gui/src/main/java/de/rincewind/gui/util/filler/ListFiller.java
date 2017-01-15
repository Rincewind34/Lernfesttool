package de.rincewind.gui.util.filler;

import java.util.List;

import javafx.scene.control.ListView;

public class ListFiller<T> extends Filler<ListView<T>, T> {
	
	public ListFiller(ListView<T> object, List<T> elements) {
		super(object, elements);
	}

	@Override
	protected void clearElements() {
		this.getObject().getItems().clear();
	}

	@Override
	protected void addElement(T element) {
		this.getObject().getItems().add(element);
	}

}
