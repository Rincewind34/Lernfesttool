package de.rincewind.api;

import de.rincewind.api.abstracts.DatasetFieldAccessor;
import de.rincewind.api.manager.DatasetManager;
import de.rincewind.api.manager.TeacherManager;
import de.rincewind.api.util.GuideType;
import de.rincewind.api.util.SaveResult;
import de.rincewind.sql.SQLRequest;
import de.rincewind.sql.tables.entities.TableTeachers;

public class Teacher extends Guide {
	
	public static final DatasetFieldAccessor<String> NAME = new DatasetFieldAccessor<>("name", String.class);
	public static final DatasetFieldAccessor<String> TOKEN = new DatasetFieldAccessor<>("token", String.class);
	
	public static TeacherManager getManager() {
		return TeacherManager.instance();
	}
	
	
	public Teacher(int teacherId) {
		super(teacherId);
	}
	
	@Override
	public void print() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public GuideType getType() {
		return GuideType.TEACHER;
	}
	
	@Override
	public DatasetManager getMatchingManager() {
		return Teacher.getManager();
	}
	
	@Override
	public SQLRequest<SaveResult> save() {
		return () -> {
			TableTeachers table = (TableTeachers) this.getMatchingManager().getTable();
			table.update(this.getId(), this.getValue(Teacher.NAME), this.getValue(Teacher.TOKEN)).sync();
			return null;
		};
	}
	
	@Override
	public String toString() {
		if (this.isValuePreset(Teacher.NAME)) {
			StringBuilder builder = new StringBuilder();
			builder.append("(L) ");
			builder.append(this.getValue(Teacher.NAME));
			
			if (this.isValuePreset(Teacher.TOKEN)) {
				builder.append(" (");
				builder.append(this.getValue(Teacher.TOKEN));
				builder.append(")");
			}
			
			return builder.toString();
		} else {
			return "lehrer-" + this.getId();
		}
	}
	
}
