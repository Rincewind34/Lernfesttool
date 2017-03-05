package de.rincewind.gui.panes;

import java.util.Arrays;

import de.rincewind.api.Student;
import de.rincewind.gui.controller.ControllerStudent;
import de.rincewind.gui.panes.abstarcts.FXMLPane;
import javafx.scene.layout.BorderPane;

public class PaneStudent extends FXMLPane<BorderPane> {

	public PaneStudent(Student student) {
		super("student.fxml", Arrays.asList(), new ControllerStudent(student));
	}

}
