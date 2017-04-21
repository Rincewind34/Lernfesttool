package de.rincewind.gui.windows;

import de.rincewind.api.Student;
import de.rincewind.gui.panes.PaneStudentSecondChoice;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class WindowStudentSecondChoice extends Window<GridPane> {

	public WindowStudentSecondChoice(Student student) {
		super(new PaneStudentSecondChoice(student), "Lernfest");
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
