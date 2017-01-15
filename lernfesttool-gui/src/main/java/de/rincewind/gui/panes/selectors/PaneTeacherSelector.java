package de.rincewind.gui.panes.selectors;

import java.util.Arrays;

import javafx.scene.layout.VBox;
import de.rincewind.gui.controller.selectors.ControllerTeacherSelector;
import de.rincewind.gui.panes.abstarcts.PaneSelector;

public class PaneTeacherSelector extends PaneSelector<VBox> {
	
	public PaneTeacherSelector() {
		super("teacherselector.fxml", Arrays.asList(), new ControllerTeacherSelector());
	}
	
}
