package de.rincewind.gui.windows;

import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import de.rincewind.gui.panes.PaneStudent;

public class WindowStudent extends Window<BorderPane> {

	public WindowStudent() {
		super(new PaneStudent(), "Lernfest - Projekt Management");
	}

	@Override
	public void onInit(Stage stage, BorderPane pane) {
		
	}

	@Override
	public void onShow(Stage stage, BorderPane pane) {
		
	}

	@Override
	public void onDispose(Stage stage) {
		
	}

}
