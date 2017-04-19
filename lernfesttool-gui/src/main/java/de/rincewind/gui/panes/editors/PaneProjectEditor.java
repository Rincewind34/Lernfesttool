package de.rincewind.gui.panes.editors;

import java.util.Arrays;

import javafx.scene.layout.HBox;
import de.rincewind.api.Project;
import de.rincewind.gui.controller.editors.ControllerProjectEditor;
import de.rincewind.gui.panes.abstracts.PaneEditor;
import de.rincewind.gui.util.TabHandler;

public class PaneProjectEditor extends PaneEditor<HBox> {
	
	private ControllerProjectEditor controller;
	
	public PaneProjectEditor(TabHandler handler, int projectId) {
		super("projecteditor.fxml", Arrays.asList(), new ControllerProjectEditor(handler, projectId));
		
		this.controller = (ControllerProjectEditor) this.getController();
	}
	
	@Override
	public Project getEditingObject() {
		return this.controller.getEditingObject();
	}
	
}
