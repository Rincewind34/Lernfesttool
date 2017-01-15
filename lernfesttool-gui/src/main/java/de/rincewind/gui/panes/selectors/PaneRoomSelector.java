package de.rincewind.gui.panes.selectors;

import java.util.Arrays;

import javafx.scene.layout.VBox;
import de.rincewind.gui.controller.selectors.ControllerRoomSelector;
import de.rincewind.gui.panes.abstarcts.PaneSelector;

public class PaneRoomSelector extends PaneSelector<VBox> {
	
	public PaneRoomSelector() {
		super("roomselector.fxml", Arrays.asList(), new ControllerRoomSelector());
	}
	
}
