package de.rincewind.gui.windows;

import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import de.rincewind.api.util.ProjectType;

public class WindowProjectEditor extends Window<GridPane> {
	
	public WindowProjectEditor(int pID, ProjectType type, Runnable onClose) {
		super(null /* TODO */, "Lernfest - Project Editor");
//		((PaneProjectEditor) this.getFXMLPane()).setOnClose(onClose); TODO 
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
