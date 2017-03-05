package de.rincewind.gui.panes.selectors;

import java.util.Arrays;

import de.rincewind.api.Helper;
import de.rincewind.gui.controller.selectors.ControllerHelperSelector;
import javafx.scene.layout.VBox;

public class PaneHelperSelector extends PaneSelector<VBox, Helper> {
	
	public PaneHelperSelector() {
		super("helperselector.fxml", Arrays.asList(), new ControllerHelperSelector(), Helper.getManager(), Helper.class);
	}

}
