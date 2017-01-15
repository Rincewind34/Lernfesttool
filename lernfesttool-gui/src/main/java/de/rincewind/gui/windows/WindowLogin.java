package de.rincewind.gui.windows;

import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import de.rincewind.gui.panes.PaneLogin;

public class WindowLogin extends Window<GridPane> {

	public WindowLogin() {
		super(new PaneLogin(), "Lernfest - Anmeldung");
	}

	@Override
	public void onInit(Stage stage, GridPane pane) {
		
	}

	@Override
	public void onShow(Stage stage, GridPane pane) {
		
	}

	@Override
	public void onDispose(Stage stage) {
		
	}

}
