package de.rincewind.gui.panes.selectors;

import java.util.Arrays;

import javafx.scene.layout.VBox;
import de.rincewind.gui.controller.selectors.ControllerClassSelector;
import de.rincewind.gui.panes.abstarcts.PaneSelector;

public class PaneClassSelector extends PaneSelector<VBox> {
	
	public PaneClassSelector() {
		super("classselector.fxml", Arrays.asList(), new ControllerClassSelector());
	}
	
}
