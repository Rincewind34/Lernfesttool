package de.rincewind.gui.panes.editors;

import java.util.Arrays;

import javafx.scene.layout.HBox;
import de.rincewind.api.Student;
import de.rincewind.gui.controller.editors.ControllerStudentEditor;
import de.rincewind.gui.panes.abstarcts.PaneEditor;
import de.rincewind.gui.util.TabHandler;

public class PaneStudentEditor extends PaneEditor<HBox> {
	
	private ControllerStudentEditor controller;
	
	public PaneStudentEditor(TabHandler handler, int studentId) {
		super("studenteditor.fxml", Arrays.asList(), new ControllerStudentEditor(handler, studentId));
		
		this.controller = (ControllerStudentEditor) this.getController();
	}
	
	@Override
	public Student getEditingObject() {
		return this.controller.getEditingObject();
	}
	
}
