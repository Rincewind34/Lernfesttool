package de.rincewind.gui.panes.selectors;

import java.util.Arrays;

import de.rincewind.api.Project;
import de.rincewind.api.util.ProjectType;
import de.rincewind.gui.controller.selectors.ControllerProjectSelector;
import de.rincewind.gui.util.Cell;
import javafx.scene.layout.VBox;

public class PaneProjectSelector extends PaneSelector<VBox, Project> {
	
	private ControllerProjectSelector controller;
	
	public PaneProjectSelector() {
		super("projectselector.fxml", Arrays.asList(), new ControllerProjectSelector(), Project.getManager(), Project.class);
		
		this.controller = (ControllerProjectSelector) this.getController();
	}
	
	public void lockType(ProjectType type) {
		for (Cell<ProjectType> cell : this.controller.getBoxProjectType().getItems()) {
			if (cell.getSavedObject() == type) {
				this.controller.getBoxProjectType().getSelectionModel().select(cell);
				break;
			}
		}
		
		this.controller.getBoxProjectType().setDisable(true);
	}
	
	public void unlock() {
		this.controller.getBoxProjectType().setDisable(false);
	}
	
}
