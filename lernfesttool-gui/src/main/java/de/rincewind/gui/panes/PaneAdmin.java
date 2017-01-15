package de.rincewind.gui.panes;

import java.util.Arrays;

import javafx.scene.layout.VBox;
import de.rincewind.gui.controller.ControllerAdmin;
import de.rincewind.gui.panes.abstarcts.FXMLPane;

public class PaneAdmin extends FXMLPane<VBox> {

	public PaneAdmin() {
		super("admin.fxml", Arrays.asList(), new ControllerAdmin());
	}

}
