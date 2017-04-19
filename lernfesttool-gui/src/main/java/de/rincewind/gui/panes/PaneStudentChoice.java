package de.rincewind.gui.panes;

import java.util.Arrays;

import de.rincewind.api.Student;
import de.rincewind.gui.controller.ControllerStudentChoice;
import de.rincewind.gui.main.GUIHandler;
import de.rincewind.gui.panes.abstracts.FXMLPane;
import de.rincewind.gui.windows.WindowLogin;
import de.rincewind.gui.windows.WindowStudentChoice;
import de.rincewind.gui.windows.WindowStudentProjectVisitor;
import javafx.scene.layout.GridPane;

public class PaneStudentChoice extends FXMLPane<GridPane> {
	
	private ControllerStudentChoice controller;
	
	public PaneStudentChoice(Student student) {
		super("studentchoice.fxml", Arrays.asList(), new ControllerStudentChoice(student));
	
		this.controller = (ControllerStudentChoice) this.getController();
		this.controller.setOnClose(() -> {
			GUIHandler.session().changeWindow(new WindowLogin());
		});
		
		this.controller.setOnOpenProject((project) -> {
			WindowStudentProjectVisitor window = new WindowStudentProjectVisitor(project);
			window.setOnClose(() -> {
				GUIHandler.session().changeWindow(new WindowStudentChoice(student));
			});
			
			GUIHandler.session().changeWindow(window);
		});
	}
	
}
