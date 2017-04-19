package de.rincewind.gui.panes;

import java.util.Arrays;

import de.rincewind.api.util.ProjectType;
import de.rincewind.gui.controller.ControllerStudentProjectCreator;
import de.rincewind.gui.panes.abstracts.FXMLPane;
import de.rincewind.gui.util.Cell;
import javafx.scene.layout.VBox;

public class PaneStudentProjectCreator extends FXMLPane<VBox> {

	private ControllerStudentProjectCreator controller;
	
	public PaneStudentProjectCreator() {
		super("studentprojectcreator.fxml", Arrays.asList(), new ControllerStudentProjectCreator());
		
		this.controller = (ControllerStudentProjectCreator) this.getController();
	}
	
	public void lockType(ProjectType type) {
		for (Cell<ProjectType> cell : this.controller.getBoxType().getItems()) {
			if (cell.getSavedObject() == type) {
				this.controller.getBoxType().getSelectionModel().select(cell);
				break;
			}
		}
		
		this.controller.getBoxType().setDisable(true);
	}
	
	public void unlock() {
		this.controller.getBoxType().setDisable(false);
	}
	
	public void valueChanged(Runnable action) {
		this.controller.getTextName().textProperty().addListener((observeable, oldValue, newValue) -> {
			action.run();
		});
		
		this.controller.getBoxType().getSelectionModel().selectedItemProperty().addListener((observeable, oldValue, newValue) -> {
			action.run();
		});
	}
	
	public boolean isNameSet() {
		return this.controller.getTextName().getText() != null && !this.controller.getTextName().getText().trim().isEmpty();
	}
	
	public boolean isTypeSelected() {
		return this.controller.getBoxType().getSelectionModel().getSelectedIndex() != -1;
	}
	
	public String getSelectedName() {
		return this.controller.getTextName().getText();
	}
	
	public ProjectType getSelectedType() {
		return this.controller.getBoxType().getSelectionModel().getSelectedItem().getSavedObject();
	}
	
}
