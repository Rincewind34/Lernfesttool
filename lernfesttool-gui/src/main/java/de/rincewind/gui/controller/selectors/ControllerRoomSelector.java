package de.rincewind.gui.controller.selectors;

import java.util.List;
import java.util.stream.Collectors;

import de.rincewind.api.Room;
import de.rincewind.gui.controller.abstracts.ControllerSelector;
import de.rincewind.gui.util.Cell;
import de.rincewind.gui.util.filler.ListFiller;
import de.rincewind.gui.util.filler.SearchCheck;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ControllerRoomSelector extends ControllerSelector<Room> {

	@FXML
	private ListView<Cell<Room>> listRooms;

	@FXML
	private TextField textSearch;

	private ListFiller<Cell<Room>> filler;

	@Override
	public void init() {

	}

	@Override
	public void setupList(List<Room> datasets) {
		List<Cell<Room>> elements = datasets.stream().map((room) -> {
			return room.asCell(Room.class);
		}).sorted().collect(Collectors.toList());

		this.filler = new ListFiller<>(this.listRooms, elements);
		this.filler.addChecker(new SearchCheck<>(this.textSearch));
		this.filler.refresh();
	}

	@Override
	public ListView<Cell<Room>> getListValues() {
		return this.listRooms;
	}

}
