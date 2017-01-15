package de.rincewind.gui.panes.selectors;

import java.util.Arrays;

import javafx.scene.layout.VBox;
import de.rincewind.gui.controller.selectors.ControllerHelperSelector;
import de.rincewind.gui.panes.abstarcts.PaneSelector;

public class PaneHelperSelector extends PaneSelector<VBox> {
	
	public PaneHelperSelector() {
		super("helperselector.fxml", Arrays.asList(), new ControllerHelperSelector());
	}

}
