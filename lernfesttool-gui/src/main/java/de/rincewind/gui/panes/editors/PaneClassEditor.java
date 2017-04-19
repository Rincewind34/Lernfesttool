package de.rincewind.gui.panes.editors;

import java.util.Arrays;

import javafx.scene.layout.HBox;
import de.rincewind.api.SchoolClass;
import de.rincewind.gui.controller.editors.ControllerClassEditor;
import de.rincewind.gui.panes.abstracts.PaneEditor;
import de.rincewind.gui.util.TabHandler;

public class PaneClassEditor extends PaneEditor<HBox> {
	
	private ControllerClassEditor controller;
	
	public PaneClassEditor(TabHandler handler, int classId) {
		super("classeditor.fxml", Arrays.asList(), new ControllerClassEditor(handler, classId));
		
		this.controller = (ControllerClassEditor) this.getController();
	}

	@Override
	public SchoolClass getEditingObject() {
		return this.controller.getEditingObject();
	}
	
}
