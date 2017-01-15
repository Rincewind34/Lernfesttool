package de.rincewind.api.manager;

import java.util.Arrays;
import java.util.List;

import de.rincewind.api.Room;
import de.rincewind.api.abstracts.DatasetFieldAccessor;
import de.rincewind.gui.panes.abstarcts.FXMLPane;
import de.rincewind.gui.panes.abstarcts.PaneSelector;
import de.rincewind.gui.panes.editors.PaneRoomEditor;
import de.rincewind.gui.panes.selectors.PaneRoomSelector;
import de.rincewind.gui.util.TabHandler;
import de.rincewind.sql.tables.entities.TableRooms;

public class RoomManager extends DatasetManager {
	
	private static RoomManager instance;
	
	static {
		RoomManager.instance = new RoomManager();
	}
	
	public static RoomManager instance() {
		return RoomManager.instance;
	}
	
	private RoomManager() {
		
	}
	
	@Override
	public String getDataName() {
		return "Raum";
	}
	
	@Override
	public Room newEmptyObject(int datasetId) {
		return new Room(datasetId);
	}
	
	@Override
	public TableRooms getTable() {
		return TableRooms.instance();
	}
	
	@Override
	public PaneSelector<?> createSelectorPane() {
		return FXMLPane.setup(new PaneRoomSelector());
	}
	
	@Override
	public PaneRoomEditor createEditorPane(TabHandler handler, int roomId) {
		return FXMLPane.setup(new PaneRoomEditor(handler, roomId));
	}
	
	@Override
	public List<DatasetFieldAccessor<?>> fieldAccessors() {
		return Arrays.asList(Room.E_BOARD, Room.HARDWARE, Room.MUSICS, Room.NAME, Room.SIZE, Room.SPORTS);
	}
	
}
