package de.rincewind.gui.controller;

import de.rincewind.api.util.ProjectType;
import de.rincewind.gui.controller.abstracts.Controller;
import de.rincewind.gui.util.Cell;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class ControllerStudentProjectCreator implements Controller {

	@FXML
	private TextField textName;

	@FXML
	private ComboBox<Cell<ProjectType>> boxType;

	@Override
	public void init() {
		for (ProjectType type : ProjectType.values()) {
			this.boxType.getItems().add(new Cell<>(type.getName(), type));
		}
	}

	public TextField getTextName() {
		return this.textName;
	}

	public ComboBox<Cell<ProjectType>> getBoxType() {
		return this.boxType;
	}

}
