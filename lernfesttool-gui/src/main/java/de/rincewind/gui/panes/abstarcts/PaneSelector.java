package de.rincewind.gui.panes.abstarcts;

import java.util.List;

import de.rincewind.api.abstracts.Dataset;
import de.rincewind.gui.controller.abstracts.ControllerSelector;
import de.rincewind.gui.util.Cell;
import de.rincewind.gui.util.ChangeListener;
import javafx.scene.layout.Pane;

public abstract class PaneSelector<T extends Pane> extends FXMLPane<T> {
	
	private ControllerSelector<?> controller;
	
	public PaneSelector(String layout, List<String> styleSheets, ControllerSelector<?> controller) {
		super(layout, styleSheets, controller);
		
		this.controller = controller;
	}

	public void addSelectListener(ChangeListener<Cell<? extends Dataset>> listener) {
		this.controller.getListValues().getSelectionModel().selectedItemProperty().addListener((observeable, oldValue, newValue) -> {
			if (oldValue == null && newValue == null) {
				return;
			}
			
			if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
				return;
			}
			
			listener.onChange(oldValue, newValue);
		});
	}
	
	public boolean isValueSelected() {
		return this.controller.getListValues().getSelectionModel().getSelectedItem() != null;
	}
	
	public Dataset getSelectedValue() {
		return this.controller.getListValues().getSelectionModel().getSelectedItem().getSavedObject();
	}
	
}
