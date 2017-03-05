package de.rincewind.api;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.List;

import de.rincewind.api.abstracts.Dataset;
import de.rincewind.api.abstracts.DatasetField;
import de.rincewind.api.abstracts.DatasetFieldAccessor;
import de.rincewind.api.abstracts.DatasetManager;
import de.rincewind.api.manager.ProjectManager;
import de.rincewind.api.manager.StudentManager;
import de.rincewind.api.util.GuideType;
import de.rincewind.api.util.ProjectCategory;
import de.rincewind.api.util.ProjectType;
import de.rincewind.api.util.SaveResult;
import de.rincewind.sql.SQLRequest;
import de.rincewind.sql.tables.entities.TableProjects;
import de.rincewind.sql.tables.relations.TableProjectAttandences;
import de.rincewind.sql.tables.relations.TableProjectHelping;

public class Project extends Dataset {
	
	public static final String FORMAT = "(%s) %s";
	
	public static final DatasetFieldAccessor<Room> ROOM = new DatasetFieldAccessor<>("roomId", Room.class);
	public static final DatasetFieldAccessor<String> NAME = new DatasetFieldAccessor<>("name", String.class);
	public static final DatasetFieldAccessor<Integer> MIN_CLASS = new DatasetFieldAccessor<>("minClass", Integer.class);
	public static final DatasetFieldAccessor<Integer> MAX_CLASS = new DatasetFieldAccessor<>("maxClass", Integer.class);
	public static final DatasetFieldAccessor<Integer> MIN_STUDENTS = new DatasetFieldAccessor<>("minStudents", Integer.class);
	public static final DatasetFieldAccessor<Integer> MAX_STUDENTS = new DatasetFieldAccessor<>("maxStudents", Integer.class);
	public static final DatasetFieldAccessor<Integer> COSTS = new DatasetFieldAccessor<>("costs", Integer.class);
	public static final DatasetFieldAccessor<String> DESCRIPTION = new DatasetFieldAccessor<>("description", String.class);
	public static final DatasetFieldAccessor<String> NOTES = new DatasetFieldAccessor<>("notes", String.class);
	public static final DatasetFieldAccessor<Boolean> REQUEST_E_BOARD = new DatasetFieldAccessor<>("requestEBoard", Boolean.class);
	public static final DatasetFieldAccessor<Boolean> REQUEST_HARDWARE = new DatasetFieldAccessor<>("requestHardware", Boolean.class);
	public static final DatasetFieldAccessor<Boolean> REQUEST_MUSICS = new DatasetFieldAccessor<>("requestMusics", Boolean.class);
	public static final DatasetFieldAccessor<Boolean> REQUEST_SPORTS = new DatasetFieldAccessor<>("requestSports", Boolean.class);
	public static final DatasetFieldAccessor<ProjectType> TYPE = new DatasetFieldAccessor<>("type", ProjectType.class);
	public static final DatasetFieldAccessor<ProjectCategory> CATEGORY = new DatasetFieldAccessor<>("category", ProjectCategory.class);
	
	public static ProjectManager getManager() {
		return ProjectManager.instance();
	}
	
	public static String format(int id, String name) {
		StringBuilder builder = new StringBuilder();
		
		if (id < 100) {
			builder.append("0");
		}
		
		if (id < 10) {
			builder.append("0");
		}
		
		builder.append(Integer.toString(id));
		return String.format(Project.FORMAT, builder.toString(), name);
	}
	
	public static String format(ProjectType type, int id, String name) {
		return "[" + type.getName() + "] " + Project.format(id, name);
	}
	
	public Project(int projectId) {
		super(projectId);
	}
	
	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		if (pageIndex != 0) {
			return Printable.NO_SUCH_PAGE;
		}
		
		return Printable.PAGE_EXISTS;
	}
	
	@Override
	public DatasetManager getMatchingManager() {
		return Project.getManager();
	}
	
	@Override
	public SQLRequest<SaveResult> save() {
		return () -> {
			SaveResult result;
			
			try {
				int roomId = -1;
				
				if (this.isRoomSelected()) {
					roomId = this.getValue(Project.ROOM).getId();
				}
				
				TableProjects table = (TableProjects) this.getMatchingManager().getTable();
				table.update(this.getId(), roomId,
						this.getValue(Project.NAME),
						this.getValue(Project.MIN_CLASS).byteValue(),
						this.getValue(Project.MAX_CLASS).byteValue(),
						this.getValue(Project.MIN_STUDENTS).byteValue(),
						this.getValue(Project.MAX_STUDENTS).byteValue(),
						this.getValue(Project.COSTS),
						this.getValue(Project.DESCRIPTION),
						this.getValue(Project.NOTES),
						this.getValue(Project.TYPE).getId(),
						this.getValue(Project.REQUEST_E_BOARD),
						this.getValue(Project.REQUEST_HARDWARE),
						this.getValue(Project.REQUEST_SPORTS),
						this.getValue(Project.REQUEST_MUSICS),
						this.getValue(Project.CATEGORY).getId()).sync();
				
				result = SaveResult.success();
			} catch (Exception ex) {
				result = SaveResult.error(ex);
			}
			
			return result;
		};
	}
	
	@Override
	public String toString() {
		if (this.isValuePreset(Project.NAME)) {
			StringBuilder builder = new StringBuilder();
			
			if (this.isValuePreset(Project.TYPE)) {
				builder.append("[");
				builder.append(this.getValue(Project.TYPE).getName());
				builder.append("] ");
			}
			
			builder.append("(");
			
			if (this.getId() < 100) {
				builder.append("0");
			}
			
			if (this.getId() < 10) {
				builder.append("0");
			}
			
			builder.append(this.getId());
			builder.append(") ");
			builder.append(this.getValue(Project.NAME));
			return builder.toString();
		} else {
			return "projekt-" + this.getId();
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected <T> void setSQLValue(DatasetField<?> field, T value) {
		if (field.getAccessor().equals(Project.TYPE)) {
			((DatasetField<ProjectType>) field).setValue(ProjectType.get((int) (Object) value));
		} else if (field.getAccessor().equals(Project.CATEGORY)) {
			((DatasetField<ProjectCategory>) field).setValue(ProjectCategory.get((int) (Object) value));
		} else if (field.getAccessor().equals(Project.ROOM)) {
			Dataset.initDataset((DatasetField<Dataset>) field, (int) (Object) value, Room.getManager());
		} else {
			super.setSQLValue(field, value);
		}
	}
	
	public boolean isRoomSelected() {
		return Dataset.isDatasetSelected(Project.ROOM, this);
	}
	
	public SQLRequest<Void> fetchRoom() {
		return Dataset.fetchDataset(Project.ROOM, this);
	}
	
	public SQLRequest<List<Student>> fetchAttendences() {
		return () -> {
			List<Integer> studentIds = TableProjectAttandences.instance().getStudents(this.getId(), false).sync();
			List<Student> students = new ArrayList<>();
			
			for (int studentId : studentIds) {
				Student student = StudentManager.instance().newEmptyObject(studentId);
				student.fetchAll().sync();
				students.add(student);
			}
			
			return students;
		};
	}
	
	public SQLRequest<List<Student>> fetchLeaders() {
		return () -> {
			List<Integer> studentIds = TableProjectAttandences.instance().getStudents(this.getId(), true).sync();
			List<Student> students = new ArrayList<>();
			
			for (int studentId : studentIds) {
				Student student = StudentManager.instance().newEmptyObject(studentId);
				student.fetchAll().sync();
				students.add(student);
			}
			
			return students;
		};
	}
	
	public SQLRequest<List<Guide>> fetchGuides() {
		return () -> {
			List<Integer> teacherIds = TableProjectHelping.instance().getGuides(this.getId(), GuideType.TEACHER.getId()).sync();
			List<Integer> helperIds = TableProjectHelping.instance().getGuides(this.getId(), GuideType.HELPER.getId()).sync();
			List<Guide> guides = new ArrayList<>();
			
			for (int teacherId : teacherIds) {
				Guide guide = Teacher.getManager().newEmptyObject(teacherId);
				guide.fetchAll().sync();
				guides.add(guide);
			}
			
			for (int helperId : helperIds) {
				Guide guide = Helper.getManager().newEmptyObject(helperId);
				guide.fetchAll().sync();
				guides.add(guide);
			}
			
			return guides;
		};
	}
	
}
