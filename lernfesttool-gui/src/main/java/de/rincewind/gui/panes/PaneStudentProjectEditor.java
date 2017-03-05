package de.rincewind.gui.panes;

import java.util.Arrays;

import de.rincewind.api.Project;
import de.rincewind.api.Student;
import de.rincewind.gui.controller.ControllerStudentProjectEditor;
import de.rincewind.gui.panes.abstarcts.FXMLPane;
import javafx.scene.layout.HBox;

public class PaneStudentProjectEditor extends FXMLPane<HBox> {
	
	public PaneStudentProjectEditor(Project project, Student owner) {
		super("studentprojecteditor.fxml", Arrays.asList(), new ControllerStudentProjectEditor(project, owner));
	}
	
}
