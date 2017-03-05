package de.rincewind.gui.windows;

import de.rincewind.api.Student;
import de.rincewind.gui.panes.PaneStudent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class WindowStudent extends Window<BorderPane> {

	public WindowStudent(Student student) {
		super(new PaneStudent(student), "Lernfest");
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
