package de.rincewind.gui.windows;

import de.rincewind.api.Project;
import de.rincewind.api.Student;
import de.rincewind.gui.panes.PaneStudentProjectEditor;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class WindowStudentProjectEditor extends Window<HBox> {

	public WindowStudentProjectEditor(Project project, Student owner) {
		super(new PaneStudentProjectEditor(project, owner), "Lernfest");
	}

	@Override
	public void onInit(Stage stage, HBox pane) {
		
	}

	@Override
	public void onShow(Stage stage, HBox pane) {
		
	}

	@Override
	public void onDispose(Stage stage) {
		
	}
	
}
