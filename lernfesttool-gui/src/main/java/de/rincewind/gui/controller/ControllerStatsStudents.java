package de.rincewind.gui.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.rincewind.api.Project;
import de.rincewind.api.SchoolClass;
import de.rincewind.api.Student;
import de.rincewind.api.abstracts.Dataset;
import de.rincewind.api.util.ProjectSet;
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
	private Map<Integer, Project> projects;

	private TabHandler handler;

	public ControllerStatsStudents(TabHandler handler) {
		this.handler = handler;
	}

	@Override
	public void init() {
		this.classes = SchoolClass.getManager().getAllDatasets().sync();
		this.projects = Dataset.convertList(Project.getManager().getAllDatasets().sync());
		this.students = Student.getManager().getAllDatasets(Dataset.convertList(this.classes)).sync();

		Collections.sort(this.classes);

		List<TableProjectAttandences.Entry> entries = TableProjectAttandences.instance().getEntries().sync();

		Map<Integer, ProjectSet[]> chooses = ProjectSet.convertChooses(TableProjectChoosing.instance().getEntries().sync(), this.projects);
		Map<Integer, ProjectSet> attandences = ProjectSet.convertAttandences(entries, this.projects);
		Map<Integer, ProjectSet> leadings = ProjectSet.convertLeadings(entries, this.projects);

		Map<SchoolClass, List<Student>> withoutChoice = new HashMap<>();
		Map<SchoolClass, List<Student>> withoutProject = new HashMap<>();
		Map<SchoolClass, List<Student>> withoutLateProject = new HashMap<>();
		Map<SchoolClass, List<Student>> withoutEarlyProject = new HashMap<>();

		for (SchoolClass schoolClass : this.classes) {
			Map<Integer, Student> students = Dataset.convertList(schoolClass.getStudents(this.students));

			withoutChoice.put(schoolClass, Student.getManager().getWithoutChoice(students, chooses, leadings));
			withoutProject.put(schoolClass, Student.getManager().getWithoutProject(students, attandences));
			withoutEarlyProject.put(schoolClass, Student.getManager().getWithoutHalfProject(students, attandences, ProjectType.EARLY));
			withoutLateProject.put(schoolClass, Student.getManager().getWithoutHalfProject(students, attandences, ProjectType.LATE));
		}

		// === Building === //

		this.boxClassLevels.getItems().add(new Cell<>("Jeder Jahrgang", -1));

		for (int i = 5; i <= 12; i++) {
			this.boxClassLevels.getItems().add(new Cell<>(SchoolClass.convertLevel(i) + ". Jahrgang", i));
		}

		this.boxCategories.getItems().add(new Cell<>("Alles anzeigen", 0));
		this.boxCategories.getItems().add(new Cell<>("Schüler ohne Wahl", 1));
		this.boxCategories.getItems().add(new Cell<>("Schüler ohne Projekt", 2));
		this.boxCategories.getItems().add(new Cell<>("Schüler ohne Projekt (frühes Projekt fehlt)", 3));
		this.boxCategories.getItems().add(new Cell<>("Schüler ohne Projekt (spätes Projekt fehlt)", 4));
		this.boxCategories.getItems().add(new Cell<>("Schüler ohne Projekt (ganzes Projekt fehlt)", 5));

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

			return student.getSavedObject().getValue(Student.SCHOOL_CLASS).getValue(SchoolClass.CLASS_LEVEL) == selected.getSavedObject();
		}));
		this.fillerStudents.addChecker(new BoxCheck<>(this.boxCategories, (student, selected) -> {
			int index = selected.getSavedObject();

			if (index == 0) {
				return true;
			}

			if (index == 1) {
				for (List<Student> students : withoutChoice.values()) {
					if (students.contains(student.getSavedObject())) {
						return true;
					}
				}
			}

			if (index == 2 || index == 3) {
				for (List<Student> students : withoutEarlyProject.values()) {
					if (students.contains(student.getSavedObject())) {
						return true;
					}
				}
			}

			if (index == 2 || index == 4) {
				for (List<Student> students : withoutLateProject.values()) {
					if (students.contains(student.getSavedObject())) {
						return true;
					}
				}
			}

			if (index == 2 || index == 5) {
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
		int[] totalChooseFirst = new int[] { 0, 0, 0 };
		int[] totalChooseSecond = new int[] { 0, 0, 0 };
		int[] totalChooseThrid = new int[] { 0, 0, 0 };
		int[] totalLeading = new int[] { 0, 0, 0 };
		int[] totalWithoutProject = new int[] { 0, 0, 0 };
		int totalSize = 0;

		int index = 1;

		for (SchoolClass schoolClass : this.classes) {
			int levelWithoutChoice = withoutChoice.get(schoolClass).size();
			int[] levelChooseFirst = this.getChooseHitAmount(schoolClass, 1, attandences, chooses);
			int[] levelChooseSecond = this.getChooseHitAmount(schoolClass, 2, attandences, chooses);
			int[] levelChooseThrid = this.getChooseHitAmount(schoolClass, 3, attandences, chooses);
			int[] levelLeading = this.getLeading(schoolClass, leadings);
			int[] levelWithoutProject = new int[] { withoutProject.get(schoolClass).size(), withoutEarlyProject.get(schoolClass).size(),
					withoutLateProject.get(schoolClass).size() };
			int size = this.getClassSize(schoolClass);

			this.addGridText(0, index, schoolClass.toString());
			this.addGridText(1, index, Integer.toString(levelWithoutChoice));
			this.addGridText(2, index, levelChooseFirst[0] + " (" + levelChooseFirst[1] + "," + levelChooseFirst[2] + ")");
			this.addGridText(3, index, levelChooseSecond[0] + " (" + levelChooseSecond[1] + "," + levelChooseSecond[2] + ")");
			this.addGridText(4, index, levelChooseThrid[0] + " (" + levelChooseThrid[1] + "," + levelChooseThrid[2] + ")");
			this.addGridText(5, index, levelWithoutProject[0] + " (" + levelWithoutProject[1] + "," + levelWithoutProject[2] + ")");
			this.addGridText(6, index, levelLeading[0] + " (" + levelLeading[1] + "," + levelLeading[2] + ")");
			this.addGridText(7, index, Integer.toString(size));

			totalWithoutChoice = totalWithoutChoice + levelWithoutChoice;
			totalChooseFirst = this.addArray(totalChooseFirst, levelChooseFirst);
			totalChooseSecond = this.addArray(totalChooseSecond, levelChooseSecond);
			totalChooseThrid = this.addArray(totalChooseThrid, levelChooseThrid);
			totalLeading = this.addArray(totalLeading, levelLeading);
			totalWithoutProject = this.addArray(totalWithoutProject, levelWithoutProject);
			totalSize = totalSize + size;

			index = index + 1;
		}

		Label placeholder = new Label("");
		placeholder.setMinHeight(10.0D);
		this.gridStats.add(placeholder, 0, index);

		index = index + 1;

		this.addGridText(0, index, "Gesammt");
		this.addGridText(1, index, Integer.toString(totalWithoutChoice));
		this.addGridText(2, index, totalChooseFirst[0] + " (" + totalChooseFirst[1] + "," + totalChooseFirst[2] + ")");
		this.addGridText(3, index, totalChooseSecond[0] + " (" + totalChooseSecond[1] + "," + totalChooseSecond[2] + ")");
		this.addGridText(4, index, totalChooseThrid[0] + " (" + totalChooseThrid[1] + "," + totalChooseThrid[2] + ")");
		this.addGridText(5, index, totalWithoutProject[0] + " (" + totalWithoutProject[1] + "," + totalWithoutProject[2] + ")");
		this.addGridText(6, index, totalLeading[0] + " (" + totalLeading[1] + "," + totalLeading[2] + ")");
		this.addGridText(7, index, totalSize + " / " + this.getWithoutClass());

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

	private int[] getLeading(SchoolClass schoolClass, Map<Integer, ProjectSet> leadings) {
		int fullTime = 0;
		int early = 0;
		int late = 0;

		for (Student student : schoolClass.getStudents(this.students)) {
			ProjectSet projectSet = leadings.get(student.getId());

			if (projectSet == null || projectSet.leadingAmount() == 0) {
				continue;
			}

			if (projectSet.isComplete()) {
				fullTime = fullTime + 1;
			} else if (projectSet.isSet(ProjectType.EARLY)) {
				early = early + 1;
			} else {
				late = late + 1;
			}
		}

		return new int[] { fullTime, early, late };
	}

	private int[] getChooseHitAmount(SchoolClass schoolClass, int chooseIndex, Map<Integer, ProjectSet> attandences, Map<Integer, ProjectSet[]> chooses) {
		int fullTime = 0;
		int early = 0;
		int late = 0;

		for (Student student : schoolClass.getStudents(this.students)) {
			if (chooses.get(student.getId()) == null) {
				continue;
			}

			ProjectSet attandence = attandences.get(student.getId());
			ProjectSet choose = chooses.get(student.getId())[chooseIndex - 1];

			if (attandence == null || choose == null) {
				continue;
			}

			int equal = attandence.equalize(choose);

			if (equal == 3) {
				fullTime = fullTime + 1;
			} else if (equal == 2) {
				late = late + 1;
			} else if (equal == 1) {
				early = early + 1;
			}
		}

		return new int[] { fullTime, early, late };
	}

}
