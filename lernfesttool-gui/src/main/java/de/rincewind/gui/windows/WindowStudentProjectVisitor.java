package de.rincewind.gui.windows;

import de.rincewind.api.Project;
import de.rincewind.gui.panes.PaneStudentProjectVisitor;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class WindowStudentProjectVisitor extends Window<HBox> {

	public WindowStudentProjectVisitor(Project project) {
		super(new PaneStudentProjectVisitor(project), "Lernfest");
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
	
	public void setOnClose(Runnable onClose) {
		((PaneStudentProjectVisitor) this.getFXMLPane()).setOnClose(onClose);
	}

}
