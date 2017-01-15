package de.rincewind.gui.panes.editors;

import java.util.Arrays;

import javafx.scene.layout.VBox;
import de.rincewind.api.Room;
import de.rincewind.gui.controller.editors.ControllerRoomEditor;
import de.rincewind.gui.panes.abstarcts.PaneEditor;
import de.rincewind.gui.util.TabHandler;

public class PaneRoomEditor extends PaneEditor<VBox> {
	
	private ControllerRoomEditor controller;
	
	public PaneRoomEditor(TabHandler handler, int roomId) {
		super("roomeditor.fxml", Arrays.asList("disabled.css"), new ControllerRoomEditor(handler, roomId));
		
		this.controller = (ControllerRoomEditor) this.getController();
	}
	
	@Override
	public Room getEditingObject() {
		return this.controller.getEditingObject();
	}

}
