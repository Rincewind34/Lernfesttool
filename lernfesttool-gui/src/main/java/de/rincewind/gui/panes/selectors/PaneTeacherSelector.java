package de.rincewind.gui.panes.selectors;

import java.util.Arrays;

import de.rincewind.api.Teacher;
import de.rincewind.gui.controller.selectors.ControllerTeacherSelector;
import javafx.scene.layout.VBox;

public class PaneTeacherSelector extends PaneSelector<VBox, Teacher> {
	
	public PaneTeacherSelector() {
		super("teacherselector.fxml", Arrays.asList(), new ControllerTeacherSelector(), Teacher.getManager(), Teacher.class);
	}
	
}
