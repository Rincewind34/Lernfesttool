package de.rincewind.gui.panes.editors;

import java.util.Arrays;

import javafx.scene.layout.VBox;
import de.rincewind.api.Helper;
import de.rincewind.gui.controller.editors.ControllerHelperEditor;
import de.rincewind.gui.panes.abstarcts.PaneEditor;
import de.rincewind.gui.util.TabHandler;

public class PaneHelperEditor extends PaneEditor<VBox> {
	
	private ControllerHelperEditor controller;
	
	public PaneHelperEditor(TabHandler handler, int helperId) {
		super("helpereditor.fxml", Arrays.asList(), new ControllerHelperEditor(handler, helperId));
		
		this.controller = (ControllerHelperEditor) this.getController();
	}

	@Override
	public Helper getEditingObject() {
		return this.controller.getEditingObject();
	}

}
