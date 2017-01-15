package de.rincewind.api;

import de.rincewind.api.abstracts.Dataset;
import de.rincewind.api.abstracts.DatasetFieldAccessor;
import de.rincewind.api.manager.DatasetManager;
import de.rincewind.api.manager.RoomManager;
import de.rincewind.api.util.SaveResult;
import de.rincewind.sql.SQLRequest;
import de.rincewind.sql.tables.entities.TableRooms;

public class Room extends Dataset {
	
	public static final DatasetFieldAccessor<String> NAME = new DatasetFieldAccessor<>("name", String.class);
	public static final DatasetFieldAccessor<Boolean> E_BOARD = new DatasetFieldAccessor<>("eBoard", Boolean.class);
	public static final DatasetFieldAccessor<Boolean> HARDWARE = new DatasetFieldAccessor<>("hardware", Boolean.class);
	public static final DatasetFieldAccessor<Boolean> SPORTS = new DatasetFieldAccessor<>("sports", Boolean.class);
	public static final DatasetFieldAccessor<Boolean> MUSICS = new DatasetFieldAccessor<>("musics", Boolean.class);
	public static final DatasetFieldAccessor<Byte> SIZE = new DatasetFieldAccessor<>("size", Byte.class);
	
	public static RoomManager getManager() {
		return RoomManager.instance();
	}
	
	
	public Room(int roomId) {
		super(roomId);
	}
	
	@Override
	public void print() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public DatasetManager getMatchingManager() {
		return Room.getManager();
	}
	
	@Override
	public SQLRequest<SaveResult> save() {
		return () -> {
			TableRooms rooms = (TableRooms) this.getMatchingManager().getTable();
			rooms.update(this.getId(), this.getValue(Room.NAME), this.getValue(Room.E_BOARD), this.getValue(Room.HARDWARE), this.getValue(Room.SPORTS),
					this.getValue(Room.MUSICS), this.getValue(Room.SIZE)).sync();
			
			return null;
		};
	}
	
	@Override
	public String toString() {
		if (this.isValuePreset(Room.NAME)) {
			return this.getValue(Room.NAME);
		} else {
			return "raum-" + this.getId();
		}
	}
	
}
