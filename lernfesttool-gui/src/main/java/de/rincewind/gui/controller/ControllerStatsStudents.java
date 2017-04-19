package de.rincewind.gui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.rincewind.api.Project;
import de.rincewind.api.SchoolClass;
import de.rincewind.api.Student;
import de.rincewind.api.abstracts.Dataset;
import de.rincewind.api.util.ProjectType;
import de.rincewind.gui.controller.abstracts.Controller;
import de.rincewind.gui.util.Cell;
import de.rincewind.gui.util.TabHandler;
import de.rincewind.gui.util.filler.BoxCheck;
import de.rincewind.gui.util.filler.ListFiller;
import de.rincewind.gui.util.filler.SearchCheck;
import de.rincewind.gui.util.listeners.DoubleClickListener;
import de.rincewind.sql.tables.relations.TableProjectAttandences;
import de.rincewind.sql.tables.relations.TableProjectChoosing;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class ControllerStatsStudents implements Controller {

	@FXML
	private GridPane gridStats;

	@FXML
	private TextField textSearch;

	@FXML
	private ComboBox<Cell<Integer>> boxClassLevels;

	@FXML
	private ComboBox<Cell<Integer>> boxCategories;

	@FXML
	private ListView<Cell<Student>> listStudents;

	private ListFiller<Cell<Student>> fillerStudents;

	private List<Student> students;
	private List<SchoolClass> classes;
	private List<TableProjectChoosing.Entry> choosing;
	private List<TableProjectAttandences.Entry> attandances;
	private Map<Integer, Project> projects;

	private TabHandler handler;

	public ControllerStatsStudents(TabHandler handler) {
		this.handler = handler;
	}

	@Override
	public void init() {
		this.choosing = TableProjectChoosing.instance().getEntries().sync();
		this.attandances = TableProjectAttandences.instance().getEntries().sync();
		this.students = Student.getManager().getAllDatasets().sync();
		this.classes = SchoolClass.getManager().getAllDatasets().sync();
		this.projects = Dataset.convertList(Project.getManager().getAllDatasets().sync());

		this.students = Student.getManager().fetchSchoolClasses(this.students).sync();
		Collections.sort(this.classes);

		Map<SchoolClass, List<Student>> withoutChoice = new HashMap<>();
		Map<SchoolClass, List<Student>> withoutProject = new HashMap<>();
		Map<SchoolClass, List<Student>> withoutHalfProject = new HashMap<>();

		for (SchoolClass schoolClass : this.classes) {
			withoutChoice.put(schoolClass, this.getWithoutChoice(schoolClass));
			withoutProject.put(schoolClass, this.getWithoutProject(schoolClass));
			withoutHalfProject.put(schoolClass, this.getWithoutHalfProject(schoolClass));
		}

		// === Building === //

		this.boxClassLevels.getItems().add(new Cell<>("Jeder Jahrgang", -1));

		for (int i = 5; i <= 12; i++) {
			this.boxClassLevels.getItems().add(new Cell<>(SchoolClass.convertLevel(i) + ". Jahrgang", i));
		}

		this.boxCategories.getItems().add(new Cell<>("Alles anzeigen", 0));
		this.boxCategories.getItems().add(new Cell<>("Schüler ohne Wahl", 1));
		this.boxCategories.getItems().add(new Cell<>("Schüler ohne Projekt", 2));
		this.boxCategories.getItems().add(new Cell<>("Schüler ohne Projekt (Halbes Projekt fehlt)", 3));
		this.boxCategories.getItems().add(new Cell<>("Schüler ohne Projekt (Ganzes Projekt fehlt)", 4));

		this.fillerStudents = new ListFiller<>(this.listStudents, students.stream().map((student) -> {
			return student.asCell(Student.class);
		}).sorted().collect(Collectors.toList()));
		this.fillerStudents.addChecker(new SearchCheck<>(this.textSearch));
		this.fillerStudents.addChecker(new BoxCheck<>(this.boxClassLevels, (student, selected) -> {
			if (selected.getSavedObject() == -1) {
				return true;
			}

			if (!student.getSavedObject().isSchoolClassSelected()) {
				return false;
			}

			return student.getSavedObject().getValue(Student.SCHOOL_CLASS).getId() == selected.getSavedObject();
		}));
		this.fillerStudents.addChecker(new BoxCheck<>(this.boxCategories, (student, selected) -> {
			int index = selected.getSavedObject();

			if (index == 0) {
				return true;
			} else if (index == 1) {
				for (List<Student> students : withoutChoice.values()) {
					if (students.contains(student.getSavedObject())) {
						return true;
					}
				}
			} else if (index == 2 || index == 3) {
				for (List<Student> students : withoutHalfProject.values()) {
					if (students.contains(student.getSavedObject())) {
						return true;
					}
				}
			} else if (index == 2 || index == 4) {
				for (List<Student> students : withoutProject.values()) {
					if (students.contains(student.getSavedObject())) {
						return true;
					}
				}
			}

			return false;
		}));

		// === Building === //
		// === Inserting === //

		this.fillerStudents.refresh();

		this.boxClassLevels.getSelectionModel().select(0);
		this.boxCategories.getSelectionModel().select(1);

		int totalWithoutChoice = 0;
		int[] totalChooseFirst = new int[] { 0, 0 };
		int[] totalChooseSecond = new int[] { 0, 0 };
		int[] totalChooseThrid = new int[] { 0, 0 };
		int[] totalLeading = new int[] { 0, 0 };
		int totalWithoutProject = 0;
		int totalWithoutHalfProject = 0;
		int totalSize = 0;

		int index = 1;

		for (SchoolClass schoolClass : this.classes) {
			int levelWithoutChoice = withoutChoice.get(schoolClass).size();
			int[] levelChooseFirst = this.getChooseHitAmount(schoolClass, 1);
			int[] levelChooseSecond = this.getChooseHitAmount(schoolClass, 2);
			int[] levelChooseThrid = this.getChooseHitAmount(schoolClass, 3);
			int[] levelLeading = this.getLeading(schoolClass);
			int levelWithoutProject = withoutProject.get(schoolClass).size();
			int levelWithoutHalfProject = withoutHalfProject.get(schoolClass).size();
			int size = this.getClassSize(schoolClass);

			this.addGridText(0, index, schoolClass.toString());
			this.addGridText(1, index, Integer.toString(levelWithoutChoice));
			this.addGridText(2, index, levelChooseFirst[0] + " (" + levelChooseFirst[1] + ")");
			this.addGridText(3, index, levelChooseSecond[0] + " (" + levelChooseSecond[1] + ")");
			this.addGridText(4, index, levelChooseThrid[0] + " (" + levelChooseThrid[1] + ")");
			this.addGridText(5, index, levelWithoutProject + " (" + levelWithoutHalfProject + ")");
			this.addGridText(6, index, levelLeading[0] + " (" + levelLeading[1] + ")");
			this.addGridText(7, index, Integer.toString(size));

			totalWithoutChoice = totalWithoutChoice + levelWithoutChoice;
			totalChooseFirst = this.addArray(totalChooseFirst, levelChooseFirst);
			totalChooseSecond = this.addArray(totalChooseSecond, levelChooseSecond);
			totalChooseThrid = this.addArray(totalChooseThrid, levelChooseThrid);
			totalLeading = this.addArray(totalLeading, levelLeading);
			totalWithoutProject = totalWithoutProject + levelWithoutProject;
			totalWithoutHalfProject = totalWithoutHalfProject + levelWithoutHalfProject;
			totalSize = totalSize + size;

			index = index + 1;
		}

		Label placeholder = new Label("");
		placeholder.setMinHeight(10.0D);
		this.gridStats.add(placeholder, 0, index);

		index = index + 1;

		this.addGridText(0, index, "Gesammt");
		this.addGridText(1, index, Integer.toString(totalWithoutChoice));
		this.addGridText(2, index, totalChooseFirst[0] + " (" + totalChooseFirst[1] + ")");
		this.addGridText(3, index, totalChooseSecond[0] + " (" + totalChooseSecond[1] + ")");
		this.addGridText(4, index, totalChooseThrid[0] + " (" + totalChooseThrid[1] + ")");
		this.addGridText(5, index, totalWithoutProject + " (" + totalWithoutHalfProject + ")");
		this.addGridText(6, index, totalLeading[0] + " (" + totalLeading[1] + ")");
		this.addGridText(7, index, totalSize + " (" + this.getWithoutClass() + ")");

		// === Inserting === //
		// === Listening === //

		this.listStudents.setOnMouseClicked(new DoubleClickListener(this.handler, this.listStudents));

		// === Listening === //
	}

	private void addGridText(int col, int row, String text) {
		Label label = new Label(text);
		label.setMinHeight(30.0D);
		this.gridStats.add(label, col, row);
	}

	private int getClassSize(SchoolClass schoolClass) {
		int result = 0;

		for (Student student : this.students) {
			if (student.isSchoolClassSelected() && student.getValue(Student.SCHOOL_CLASS).getId() == schoolClass.getId()) {
				result = result + 1;
			}
		}

		return result;
	}

	private int getWithoutClass() {
		int result = 0;

		for (Student student : this.students) {
			if (!student.isSchoolClassSelected()) {
				result = result + 1;
			}
		}

		return result;
	}

	private int[] addArray(int[] array1, int[] array2) {
		if (array1.length != array2.length) {
			throw new ArrayIndexOutOfBoundsException();
		}

		int[] result = new int[array1.length];

		for (int i = 0; i < result.length; i++) {
			result[i] = array1[i] + array2[i];
		}

		return result;
	}

	private int[] getLeading(SchoolClass schoolClass) {
		List<Student> halfTime = new ArrayList<>();
		int result = 0;

		iterateStudents: for (Student student : this.students) {
			if (!student.isSchoolClassSelected() || student.getValue(Student.SCHOOL_CLASS).getId() != schoolClass.getId()) {
				continue iterateStudents;
			}

			for (TableProjectAttandences.Entry entryAttandance : this.attandances) {
				if (entryAttandance.studentId == student.getId() && entryAttandance.leading) {
					ProjectType type = this.projects.get(entryAttandance.projectId).getValue(Project.TYPE);

					if (type.isHalfTime()) {
						if (halfTime.contains(student)) {
							halfTime.remove(student);
							result = result + 1;
							continue iterateStudents;
						} else {
							halfTime.add(student);
						}
					} else {
						result = result + 1;
						continue iterateStudents;
					}
				}
			}
		}

		return new int[] { result, halfTime.size() };
	}

	private int[] getChooseHitAmount(SchoolClass schoolClass, int chooseIndex) {
		List<Student> halfTime = new ArrayList<>();
		int result = 0;

		iterateStudents: for (Student student : this.students) {
			if (!student.isSchoolClassSelected() || student.getValue(Student.SCHOOL_CLASS).getId() != schoolClass.getId()) {
				continue iterateStudents;
			}

			for (TableProjectChoosing.Entry entry : this.choosing) {
				if (entry.studentId == student.getId() && entry.chooseIndex == chooseIndex) {
					for (TableProjectAttandences.Entry entryAttandance : this.attandances) {
						if (entryAttandance.studentId == student.getId() && entryAttandance.projectId == entry.projectId) {
							ProjectType type = this.projects.get(entry.projectId).getValue(Project.TYPE);

							if (type.isHalfTime()) {
								if (halfTime.contains(student)) {
									halfTime.remove(student);
									result = result + 1;
									continue iterateStudents;
								} else {
									halfTime.add(student);
								}
							} else {
								result = result + 1;
								continue iterateStudents;
							}
						}
					}
				}
			}
		}

		return new int[] { result, halfTime.size() };
	}

	private List<Student> getWithoutProject(SchoolClass schoolClass) {
		List<Student> result = new ArrayList<>();

		iterateStudents: for (Student student : this.students) {
			if (!student.isSchoolClassSelected() || student.getValue(Student.SCHOOL_CLASS).getId() != schoolClass.getId()) {
				continue iterateStudents;
			}

			for (TableProjectAttandences.Entry entry : this.attandances) {
				if (entry.studentId == student.getId()) {
					continue iterateStudents;
				}
			}

			result.add(student);
		}

		return result;
	}

	private List<Student> getWithoutHalfProject(SchoolClass schoolClass) {
		List<Student> result = new ArrayList<>();

		iterateStudents: for (Student student : this.students) {
			if (!student.isSchoolClassSelected() || student.getValue(Student.SCHOOL_CLASS).getId() != schoolClass.getId()) {
				continue iterateStudents;
			}

			for (TableProjectAttandences.Entry entry : this.attandances) {
				if (entry.studentId == student.getId()) {
					ProjectType type = this.projects.get(entry.projectId).getValue(Project.TYPE);

					if (!type.isHalfTime()) {
						continue iterateStudents;
					} else {
						if (result.contains(student)) {
							result.remove(student);
							continue iterateStudents;
						} else {
							result.add(student);
						}
					}
				}
			}
		}

		return result;
	}

	private List<Student> getWithoutChoice(SchoolClass schoolClass) {
		Map<Integer, Student> studentMap = Dataset.convertList(this.students);
		Map<Student, Integer> leadings = new HashMap<>();

		for (TableProjectAttandences.Entry entry : this.attandances) {
			Student student = studentMap.get(entry.studentId);

			if (student == null) {
				new RuntimeException("Found null student: " + entry.studentId).printStackTrace();
			}

			if (!leadings.containsKey(student)) {
				leadings.put(student, 0);
			}
			
			Project project = this.projects.get(entry.projectId);

			if (student == null) {
				new RuntimeException("Found null project: " + entry.projectId).printStackTrace();
			}
			
			if (project.getValue(Project.TYPE).isHalfTime()) {
				leadings.put(student, leadings.get(student) + (project.getValue(Project.TYPE) == ProjectType.EARLY ? 1 : 2));
			} else {
				leadings.put(student, 3);
			}
		}

		List<Student> result = new ArrayList<>();

		iterateStudents: for (Student student : this.students) {
			if (!student.isSchoolClassSelected() || student.getValue(Student.SCHOOL_CLASS).getId() != schoolClass.getId()) {
				continue iterateStudents;
			}
			
			for (TableProjectChoosing.Entry entry : this.choosing) {
				if (entry.studentId == student.getId()) {
					continue iterateStudents;
				}
			}

			if (leadings.containsKey(student) && leadings.get(student) < 0) {
				continue iterateStudents;
			}
			
			// TODO better checks
			
			result.add(student);
		}

		return result;
	}

}
