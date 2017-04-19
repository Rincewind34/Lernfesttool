package de.rincewind.gui.panes;

import java.util.Arrays;

import javafx.scene.layout.GridPane;
import de.rincewind.gui.controller.ControllerLogin;
import de.rincewind.gui.panes.abstracts.FXMLPane;

public class PaneLogin extends FXMLPane<GridPane> {

	public PaneLogin() {
		super("login.fxml", Arrays.asList("listcells.css", "progress.css"), new ControllerLogin());
	}

}
