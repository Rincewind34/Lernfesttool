package de.rincewind.gui.windows;

import de.rincewind.api.Student;
import de.rincewind.gui.panes.PaneStudentChoice;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class WindowStudentChoice extends Window<GridPane> {

	public WindowStudentChoice(Student student) {
		super(new PaneStudentChoice(student), "Lernfest");
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
