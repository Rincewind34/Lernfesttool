package de.rincewind.gui.panes;

import java.util.Arrays;

import de.rincewind.api.Project;
import de.rincewind.gui.controller.ControllerStudentProjectVisitor;
import de.rincewind.gui.panes.abstracts.FXMLPane;
import javafx.scene.layout.HBox;

public class PaneStudentProjectVisitor extends FXMLPane<HBox> {
	
	public PaneStudentProjectVisitor(Project project) {
		super("studentprojectvisitor.fxml", Arrays.asList("disabled.css"), new ControllerStudentProjectVisitor(project));
	}
	
	public void setOnClose(Runnable onClose) {
		((ControllerStudentProjectVisitor) this.getController()).setOnClose(onClose);
	}
	
}
