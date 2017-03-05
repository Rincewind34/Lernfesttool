package de.rincewind.gui.panes.selectors;

import java.util.Arrays;

import de.rincewind.api.Room;
import de.rincewind.gui.controller.selectors.ControllerRoomSelector;
import javafx.scene.layout.VBox;

public class PaneRoomSelector extends PaneSelector<VBox, Room> {
	
	public PaneRoomSelector() {
		super("roomselector.fxml", Arrays.asList(), new ControllerRoomSelector(), Room.getManager(), Room.class);
	}
	
}
