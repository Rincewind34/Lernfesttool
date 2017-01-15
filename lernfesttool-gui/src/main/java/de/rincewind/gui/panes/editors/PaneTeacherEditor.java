package de.rincewind.gui.panes.editors;

import java.util.Arrays;

import javafx.scene.layout.VBox;
import de.rincewind.api.Teacher;
import de.rincewind.gui.controller.editors.ControllerTeacherEditor;
import de.rincewind.gui.panes.abstarcts.PaneEditor;
import de.rincewind.gui.util.TabHandler;

public class PaneTeacherEditor extends PaneEditor<VBox> {
	
	private ControllerTeacherEditor controller;
	
	public PaneTeacherEditor(TabHandler handler, int teacherId) {
		super("teachereditor.fxml", Arrays.asList(), new ControllerTeacherEditor(handler, teacherId));
		
		this.controller = (ControllerTeacherEditor) this.getController();
	}

	@Override
	public Teacher getEditingObject() {
		return this.controller.getEditingObject();
	}

}
