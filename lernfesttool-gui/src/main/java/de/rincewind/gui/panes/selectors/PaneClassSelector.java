package de.rincewind.gui.panes.selectors;

import java.util.Arrays;

import javafx.scene.layout.VBox;
import de.rincewind.api.SchoolClass;
import de.rincewind.gui.controller.selectors.ControllerClassSelector;

public class PaneClassSelector extends PaneSelector<VBox, SchoolClass> {
	
	public PaneClassSelector() {
		super("classselector.fxml", Arrays.asList(), new ControllerClassSelector(), SchoolClass.getManager(), SchoolClass.class);
	}
	
}
