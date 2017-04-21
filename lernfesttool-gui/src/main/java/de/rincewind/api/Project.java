package de.rincewind.api;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import de.rincewind.gui.printjobs.CustomPrintable;
import de.rincewind.gui.util.Design;
import de.rincewind.sql.SQLRequest;
import de.rincewind.sql.tables.entities.TableProjects;
import de.rincewind.sql.tables.relations.TableProjectAttandences;
import de.rincewind.sql.tables.relations.TableProjectHelping;

public class Project extends Dataset implements CustomPrintable {

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
				table.update(this.getId(), roomId, this.getValue(Project.NAME), this.getValue(Project.MIN_CLASS).byteValue(),
						this.getValue(Project.MAX_CLASS).byteValue(), this.getValue(Project.MIN_STUDENTS).byteValue(),
						this.getValue(Project.MAX_STUDENTS).byteValue(), this.getValue(Project.COSTS), this.getValue(Project.DESCRIPTION),
						this.getValue(Project.NOTES), this.getValue(Project.TYPE).getId(), this.getValue(Project.REQUEST_E_BOARD),
						this.getValue(Project.REQUEST_HARDWARE), this.getValue(Project.REQUEST_SPORTS), this.getValue(Project.REQUEST_MUSICS),
						this.getValue(Project.CATEGORY).getId()).sync();

				result = SaveResult.success();
			} catch (Exception ex) {
				result = SaveResult.error(ex);
			}

			return result;
		};
	}

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		if (pageIndex > 0) {
			return Printable.NO_SUCH_PAGE;
		}
		
		try {
			this.fetchMissing().sync();
			List<Student> fetchedLeaders = this.fetchLeaders().sync();
			List<Guide> fetchedGuides = this.fetchGuides().sync();
			
			String room = "NaN";
			
			if (this.isRoomSelected()) {
				this.getValue(Project.ROOM).fetchMissing().sync();
				room = this.getValue(Project.ROOM).getValue(Room.NAME);
			}
			
			String leaders = "";
			
			for (Student student : fetchedLeaders) {
				leaders = leaders + ", " + student.getValue(Student.FIRSTNAME) + " " + student.getValue(Student.LASTNAME);
			}
			
			for (Guide guide : fetchedGuides) {
				leaders = leaders + ", " + guide.getName();
			}
			
			if (!leaders.isEmpty()) {
				leaders = leaders.substring(2);
			}
			
			String classes = SchoolClass.convertLevel(this.getValue(Project.MIN_CLASS)) + "-" + SchoolClass.convertLevel(this.getValue(Project.MAX_CLASS));
			String students = this.getValue(Project.MIN_STUDENTS) + "-" + this.getValue(Project.MAX_STUDENTS);
	
			double width = Design.pointsToCenti((int) pageFormat.getImageableWidth());
			double height = Design.pointsToCenti((int) pageFormat.getImageableHeight());
			
			Design.setupGraphics((Graphics2D) graphics, pageFormat);
			
			graphics.drawRect(0, 0, (int) pageFormat.getImageableWidth(), (int) pageFormat.getImageableHeight());
	
			graphics.drawLine(0, Design.centiToPoints(1), (int) pageFormat.getImageableWidth(), Design.centiToPoints(1));
			graphics.drawLine(0, Design.centiToPoints(2), (int) pageFormat.getImageableWidth(), Design.centiToPoints(2));
			graphics.drawLine(0, Design.centiToPoints(3), (int) pageFormat.getImageableWidth(), Design.centiToPoints(3));
			graphics.drawLine(0, Design.centiToPoints(height - 1), (int) pageFormat.getImageableWidth(), Design.centiToPoints(height - 1));
			graphics.drawLine(Design.centiToPoints(2), 0, Design.centiToPoints(2), Design.centiToPoints(2));
			graphics.drawLine(Design.centiToPoints(width - 2), 0, Design.centiToPoints(width - 2), Design.centiToPoints(1));
			graphics.drawLine(Design.centiToPoints((width - 2) / 2.0D + 2), Design.centiToPoints(1), Design.centiToPoints((width - 2) / 2.0D + 2),
					Design.centiToPoints(2));
			graphics.drawLine(Design.centiToPoints(width / 2.0D), Design.centiToPoints(height - 1), Design.centiToPoints(width / 2.0D),
					(int) pageFormat.getImageableHeight());
	
			graphics.drawString(this.getProjectIdString(), Design.centiToPoints(0.5), Design.centiToPoints(0.75));
			graphics.drawString(this.getValue(Project.NAME), Design.centiToPoints(2.5), Design.centiToPoints(0.75));
			graphics.drawString(this.getValue(Project.TYPE).getName(), Design.centiToPoints(width - 1.5), Design.centiToPoints(0.75));
			graphics.drawString(room, Design.centiToPoints(0.5), Design.centiToPoints(1.75));
			graphics.drawString("Klassenstufe: " + classes, Design.centiToPoints(2.5), Design.centiToPoints(1.75));
			graphics.drawString("Teilnhemerzahl: " + students, Design.centiToPoints((width - 2) / 2.0D + 2.5), Design.centiToPoints(1.75));
			graphics.drawString("Leiter: " + leaders, Design.centiToPoints(0.5), Design.centiToPoints(2.75));
			graphics.drawString("Kosten: " + this.getCostString(), Design.centiToPoints(0.5), Design.centiToPoints(height - 0.25));
			graphics.drawString("Kategory: " + this.getValue(Project.CATEGORY).getName(), Design.centiToPoints(width / 2.0D + 0.5),
					Design.centiToPoints(height - 0.25));
	
			Design.drawStringMultiLine((Graphics2D) graphics, this.getValue(Project.DESCRIPTION), Design.centiToPoints(width - 1), Design.centiToPoints(0.5),
					Design.centiToPoints(3.75));
	
			return Printable.PAGE_EXISTS;
		} catch (Exception exception) {
			System.out.println("ERROR while printing project " + this.getId() + ":");
			exception.printStackTrace();
			return Printable.NO_SUCH_PAGE;
		}
	}
	
	@Override
	public int getPageCount(PageFormat pageFormat) {
		return 1;
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
			builder.append(this.getProjectIdString());
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

	@Override
	public SQLRequest<Void> delete() {
		return () -> {
			super.delete().sync();
			TableProjectAttandences.instance().clearProject(this.getId(), true).sync();
			TableProjectAttandences.instance().clearProject(this.getId(), false).sync();
			TableProjectHelping.instance().clearProject(this.getId()).sync();
			return null;
		};
	}

	public void fillDefaults() {
		this.setValue(Project.NAME, "");
		this.setValue(Project.MIN_CLASS, 5);
		this.setValue(Project.MAX_CLASS, 11);
		this.setValue(Project.MIN_STUDENTS, 5);
		this.setValue(Project.MAX_STUDENTS, 10);
		this.setValue(Project.COSTS, 0);
		this.setValue(Project.DESCRIPTION, "");
		this.setValue(Project.NOTES, "");
		this.setValue(Project.TYPE, ProjectType.FULL);
		this.setValue(Project.REQUEST_E_BOARD, false);
		this.setValue(Project.REQUEST_HARDWARE, false);
		this.setValue(Project.REQUEST_SPORTS, false);
		this.setValue(Project.REQUEST_MUSICS, false);
		this.setValue(Project.CATEGORY, ProjectCategory.NORMAL);
	}

	public boolean isRoomSelected() {
		return Dataset.isDatasetSelected(Project.ROOM, this);
	}

	public String getProjectIdString() {
		StringBuilder builder = new StringBuilder();

		if (this.getId() < 100) {
			builder.append("0");
		}

		if (this.getId() < 10) {
			builder.append("0");
		}

		builder.append(this.getId());
		return builder.toString();
	}

	public String getCostString() {
		int costs = this.getValue(Project.COSTS);

		if (costs != 0) {
			if (costs % 100 == 0) {
				costs = costs / 100;

				return costs + " Euro";
			} else {
				return costs + " Cent";
			}
		} else {
			return "Keine";
		}
	}

	public SQLRequest<Void> addLeader(int studentId) {
		return TableProjectAttandences.instance().add(this.getId(), studentId, true);
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

	public SQLRequest<List<Student>> fetchSimpleAttendences() {
		return () -> {
			return TableProjectAttandences.instance().getStudents(this.getId(), false).sync().stream().map((studentId) -> {
				return Student.getManager().newEmptyObject(studentId);
			}).collect(Collectors.toList());
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
