package de.rincewind.gui.panes;

import java.util.Arrays;

import javafx.scene.layout.BorderPane;
import de.rincewind.gui.controller.ControllerStudent;
import de.rincewind.gui.panes.abstarcts.FXMLPane;

public class PaneStudent extends FXMLPane<BorderPane> {

	public PaneStudent() {
		super("student.fxml", Arrays.asList(), new ControllerStudent());
	}

}
