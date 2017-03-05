package de.rincewind.gui.panes.selectors;

import java.util.Arrays;

import javafx.scene.layout.VBox;
import de.rincewind.api.Student;
import de.rincewind.gui.controller.selectors.ControllerStudentSelector;

public class PaneStudentSelector extends PaneSelector<VBox, Student> {
	
	public PaneStudentSelector() {
		super("studentselector.fxml", Arrays.asList(), new ControllerStudentSelector(), Student.getManager(), Student.class);
	}
	
}
