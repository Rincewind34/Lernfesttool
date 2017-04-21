package de.rincewind.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.rincewind.api.abstracts.Dataset;
import de.rincewind.api.abstracts.DatasetFieldAccessor;
import de.rincewind.api.abstracts.DatasetManager;
import de.rincewind.api.manager.RoomManager;
import de.rincewind.api.util.SaveResult;
import de.rincewind.sql.SQLRequest;
import de.rincewind.sql.abstracts.EntityTable.FieldMap;
import de.rincewind.sql.tables.entities.TableProjects;
import de.rincewind.sql.tables.entities.TableRooms;
import de.rincewind.sql.tables.entities.TableSchoolClasses;

public class Room extends Dataset {

	public static final DatasetFieldAccessor<String> NAME = new DatasetFieldAccessor<>("name", String.class);
	public static final DatasetFieldAccessor<Boolean> E_BOARD = new DatasetFieldAccessor<>("eBoard", Boolean.class);
	public static final DatasetFieldAccessor<Boolean> HARDWARE = new DatasetFieldAccessor<>("hardware", Boolean.class);
	public static final DatasetFieldAccessor<Boolean> SPORTS = new DatasetFieldAccessor<>("sports", Boolean.class);
	public static final DatasetFieldAccessor<Boolean> MUSICS = new DatasetFieldAccessor<>("musics", Boolean.class);
	public static final DatasetFieldAccessor<Integer> SIZE = new DatasetFieldAccessor<>("size", Integer.class);

	public static RoomManager getManager() {
		return RoomManager.instance();
	}

	public Room(int roomId) {
		super(roomId);
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
					this.getValue(Room.MUSICS), this.getValue(Room.SIZE).byteValue()).sync();

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

	public SQLRequest<SchoolClass> getOwningClass() {
		return () -> {
			Entry<Integer, FieldMap> entry = TableSchoolClasses.instance().getByRoomId(this.getId(), SchoolClass.getManager().getTableColumns()).sync();

			if (entry == null) {
				return null;
			} else {
				return SchoolClass.getManager().newObject(entry.getKey(), entry.getValue());
			}
		};
	}

	public SQLRequest<List<Project>> getUsingProjects() {
		return () -> {
			Map<Integer, FieldMap> result = TableProjects.instance().getByRoom(this.getId(), Project.getManager().getTableColumns()).sync();
			List<Project> projects = new ArrayList<>();
			
			for (int projectId : result.keySet()) {
				projects.add(Project.getManager().newObject(projectId, result.get(projectId)));
			}
			
			return projects;
		};
	}

}
