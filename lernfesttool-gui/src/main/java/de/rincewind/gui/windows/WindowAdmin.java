package de.rincewind.gui.windows;

import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import de.rincewind.gui.panes.PaneAdmin;

public class WindowAdmin extends Window<VBox> {

	public WindowAdmin() {
		super(new PaneAdmin(), "Lernfest - Administration");
	}

	@Override
	public void onInit(Stage stage, VBox pane) {
		
	}

	@Override
	public void onShow(Stage stage, VBox pane) {
		
	}

	@Override
	public void onDispose(Stage stage) {
		
	}

}
