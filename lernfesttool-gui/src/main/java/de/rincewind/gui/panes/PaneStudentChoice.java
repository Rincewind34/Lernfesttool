package de.rincewind.gui.panes;

import java.util.Arrays;

import de.rincewind.gui.controller.ControllerStudentChoice;
import de.rincewind.gui.main.GUIHandler;
import de.rincewind.gui.panes.abstarcts.FXMLPane;
import de.rincewind.gui.windows.WindowLogin;
import javafx.scene.layout.GridPane;

public class PaneStudentChoice extends FXMLPane<GridPane> {
	
	private ControllerStudentChoice controller;
	
	public PaneStudentChoice() {
		super("studentchoice.fxml", Arrays.asList(), new ControllerStudentChoice());
	
		this.controller = (ControllerStudentChoice) this.getController();
		this.controller.setOnClose(() -> {
			GUIHandler.session().changeWindow(new WindowLogin());
		});
		
//		this.controller.setOnOpenProject((projectId) -> {
//			GUIHandler.session().changeWindow(new WindowProjectVisitor(projectId, () -> {
//				Main.changeWindow(new WindowStudentChoice());
//			}));
//		}); TODO
	}
	
}
