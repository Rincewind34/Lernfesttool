package de.rincewind.gui.windows;

import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import de.rincewind.gui.panes.PaneStudentChoice;

public class WindowStudentChoice extends Window<GridPane> {

	public WindowStudentChoice() {
		super(new PaneStudentChoice(), "Lernfest");
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
