package de.rincewind.api;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.rincewind.api.abstracts.Dataset;
import de.rincewind.api.abstracts.DatasetField;
import de.rincewind.api.abstracts.DatasetFieldAccessor;
import de.rincewind.api.abstracts.DatasetManager;
import de.rincewind.api.manager.ProjectManager;
import de.rincewind.api.manager.SchoolClassManager;
import de.rincewind.api.manager.StudentManager;
import de.rincewind.api.util.SaveResult;
import de.rincewind.sql.SQLRequest;
import de.rincewind.sql.abstracts.EntityTable.FieldMap;
import de.rincewind.sql.tables.entities.TableSchoolClasses;

public class SchoolClass extends Dataset implements Comparable<SchoolClass> {

	public static final DatasetFieldAccessor<Integer> CLASS_LEVEL = new DatasetFieldAccessor<>("classLevel", Integer.class);
	public static final DatasetFieldAccessor<String> CLASS_DATA = new DatasetFieldAccessor<>("classData", String.class);
	public static final DatasetFieldAccessor<Room> ROOM = new DatasetFieldAccessor<>("roomId", Room.class);
	public static final DatasetFieldAccessor<Teacher> TEACHER = new DatasetFieldAccessor<>("teacherId", Teacher.class);

	public static SchoolClassManager getManager() {
		return SchoolClassManager.instance();
	}

	public static int convertLevel(String input) {
		try {
			int level = Integer.parseInt(input);

			if (level < 10) {
				return level;
			} else {
				throw new RuntimeException("Invalid level '" + level + "'! Must be smaller than 10!");
			}
		} catch (NumberFormatException ex) {
			if (input.equals("E")) {
				return 10;
			} else if (input.equals("Q1")) {
				return 11;
			} else if (input.equals("Q2")) {
				return 12;
			} else {
				throw new RuntimeException("Invalid level '" + input + "'!");
			}
		}
	}

	public static String convertLevel(int input) {
		if (input < 10) {
			return Integer.toString(input);
		} else if (input == 10) {
			return "E";
		} else if (input == 11) {
			return "Q1";
		} else if (input == 12) {
			return "Q2";
		} else {
			throw new RuntimeException("Invalid level: " + input + "! 12 is the highest level!");
		}
	}

	public SchoolClass(int classId) {
		super(classId);
	}

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		return Printable.NO_SUCH_PAGE;
	}

	@Override
	public int compareTo(SchoolClass schoolClass) {
		int result = Integer.compare(this.getValue(SchoolClass.CLASS_LEVEL), schoolClass.getValue(SchoolClass.CLASS_LEVEL));

		if (result == 0) {
			return this.getValue(SchoolClass.CLASS_DATA).compareTo(schoolClass.getValue(SchoolClass.CLASS_DATA));
		} else {
			return result;
		}
	}

	@Override
	public DatasetManager getMatchingManager() {
		return SchoolClass.getManager();
	}

	@Override
	public SQLRequest<SaveResult> save() {
		return () -> {
			int teacherId = -1;

			if (this.isTeacherSelected()) {
				teacherId = this.getValue(SchoolClass.TEACHER).getId();
			}

			int roomId = -1;

			if (this.isRoomSelected()) {
				roomId = this.getValue(SchoolClass.ROOM).getId();
			}

			TableSchoolClasses table = (TableSchoolClasses) this.getMatchingManager().getTable();
			table.update(this.getId(), this.getValue(SchoolClass.CLASS_LEVEL), this.getValue(SchoolClass.CLASS_DATA), teacherId, roomId).sync();

			return null;
		};
	}

	@Override
	public String toString() {
		if (this.isValuePreset(SchoolClass.CLASS_LEVEL) && this.isValuePreset(SchoolClass.CLASS_DATA)) {
			return SchoolClass.convertLevel(this.getValue(SchoolClass.CLASS_LEVEL)) + this.getValue(SchoolClass.CLASS_DATA);
		} else {
			return "klasse-" + this.getId();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	protected <T> void setSQLValue(DatasetField<?> field, T value) {
		if (field.getAccessor().equals(SchoolClass.ROOM)) {
			Dataset.initDataset((DatasetField<Dataset>) field, (int) (Object) value, Room.getManager());
		} else if (field.getAccessor().equals(SchoolClass.TEACHER)) {
			Dataset.initDataset((DatasetField<Dataset>) field, (int) (Object) value, Teacher.getManager());
		} else {
			super.setSQLValue(field, value);
		}
	}

	public boolean isRoomSelected() {
		return Dataset.isDatasetSelected(SchoolClass.ROOM, this);
	}

	public boolean isTeacherSelected() {
		return Dataset.isDatasetSelected(SchoolClass.TEACHER, this);
	}

	public SQLRequest<Void> fetchRoom() {
		return Dataset.fetchDataset(SchoolClass.ROOM, this);
	}

	public SQLRequest<Void> fetchTeacher() {
		return Dataset.fetchDataset(SchoolClass.TEACHER, this);
	}

	public SQLRequest<Integer> getClassSize() {
		return () -> {
			return StudentManager.instance().getTable().getByClass(this.getId(), Student.getManager().getTableColumns()).sync().size();
		};
	}

	public SQLRequest<List<Student>> fetchStudents() {
		return () -> {
			Map<Integer, FieldMap> result = StudentManager.instance().getTable().getByClass(this.getId(), Student.getManager().getTableColumns()).sync();
			List<Student> students = new ArrayList<>();
			
			for (int studentId : result.keySet()) {
				students.add(Student.getManager().newObject(studentId, result.get(studentId)));
			}

			return students;
		};
	}

	public SQLRequest<List<Project>> fetchPossibleProjects() {
		return () -> {
			List<Integer> projectIds = ProjectManager.instance().getTable().getForClass(this.getValue(SchoolClass.CLASS_LEVEL)).sync();
			List<Project> projects = new ArrayList<>();

			for (int projectId : projectIds) {
				Project project = Project.getManager().newEmptyObject(projectId);
				project.fetchAll().sync();
				projects.add(project);
			}

			return projects;
		};
	}

}
