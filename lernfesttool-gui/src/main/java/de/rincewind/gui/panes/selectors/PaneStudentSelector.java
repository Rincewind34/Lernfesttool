package de.rincewind.gui.panes.selectors;

import java.util.Arrays;

import javafx.scene.layout.VBox;
import de.rincewind.gui.controller.selectors.ControllerStudentSelector;
import de.rincewind.gui.panes.abstarcts.PaneSelector;

public class PaneStudentSelector extends PaneSelector<VBox> {
	
	public PaneStudentSelector() {
		super("studentselector.fxml", Arrays.asList(), new ControllerStudentSelector());
	}
	
}
