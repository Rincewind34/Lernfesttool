package de.rincewind.gui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.rincewind.api.Project;
import de.rincewind.api.SchoolClass;
import de.rincewind.api.util.ProjectType;
import de.rincewind.gui.controller.abstracts.Controller;
import de.rincewind.gui.util.Cell;
import de.rincewind.gui.util.TabHandler;
import de.rincewind.gui.util.filler.Checker;
import de.rincewind.gui.util.filler.ListFiller;
import de.rincewind.gui.util.listeners.DoubleClickListener;
import de.rincewind.sql.SQLRequest;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;

public class ControllerStatsProject implements Controller {

	@FXML
	private Label labelCountEarly;

	@FXML
	private Label labelCountLate;

	@FXML
	private Label labelCountFull;

	@FXML
	private Label labelCountTotal;

	@FXML
	private Label labelStudentsEarly;

	@FXML
	private Label labelStudentsLate;

	@FXML
	private Label labelStudentsFull;

	@FXML
	private Label labelStudentsTotal;

	@FXML
	private Label labelStudents;

	@FXML
	private Label labelClasses;

	@FXML
	private Label labelTotalCountEarly;

	@FXML
	private Label labelTotalCountLate;

	@FXML
	private Label labelTotalCountFull;

	@FXML
	private Label labelTotalCountTotal;

	@FXML
	private Label labelTotalStudentsEarly;

	@FXML
	private Label labelTotalStudentsLate;

	@FXML
	private Label labelTotalStudentsFull;

	@FXML
	private Label labelTotalStudentsTotal;

	@FXML
	private Label labelTotalStudents;

	@FXML
	private Label labelTotalClasses;

	@FXML
	private Button btnReload;

	@FXML
	private Button btnSelectAllClasses;

	@FXML
	private ComboBox<Cell<Byte>> boxMinClassLevel;

	@FXML
	private ComboBox<Cell<Byte>> boxMaxClassLevel;

	@FXML
	private ListView<Cell<Project>> listProjects;

	@FXML
	private GridPane gridClasses;

	private ListFiller<Cell<Project>> fillerProjects;

	private List<Project> projects;
	private List<SchoolClass> classes;

	private Map<SchoolClass, Integer> classSizes;

	private TabHandler handler;

	public ControllerStatsProject(TabHandler handler) {
		this.handler = handler;
	}

	@Override
	public void init() {
		this.fetchData().sync();

		// === Building === //

		for (byte i = 5; i <= 12; i++) {
			this.boxMinClassLevel.getItems().add(new Cell<>(SchoolClass.convertLevel(i), i));
			this.boxMaxClassLevel.getItems().add(new Cell<>(SchoolClass.convertLevel(i), i));
		}

		this.setupFiller();

		// === Building === //
		// === Inserting === //

		this.setFinalValues();
		this.selectAllClasses();
		this.calculateLabels();

		this.fillerProjects.refresh();

		// === Inserting === //
		// === Listening === //

		this.btnReload.setOnAction((event) -> {
			this.btnReload.setDisable(true);

			this.fetchData().async((object) -> {
				Platform.runLater(() -> {
					this.setupFiller();
					this.fillerProjects.refresh();
					this.setFinalValues();
					this.calculateLabels();
					this.btnReload.setDisable(false);
				});
			}, (exception) -> {
				this.btnReload.setDisable(false);

				// TODO
			});
		});

		this.btnSelectAllClasses.setOnAction((event) -> {
			this.selectAllClasses();
		});

		this.boxMinClassLevel.getSelectionModel().selectedItemProperty().addListener((observeable, oldValue, newValue) -> {
			if (newValue != null) {
				this.calculateLabels();
				this.fillerProjects.refresh();
			}
		});

		this.boxMaxClassLevel.getSelectionModel().selectedItemProperty().addListener((observeable, oldValue, newValue) -> {
			if (newValue != null) {
				this.calculateLabels();
				this.fillerProjects.refresh();
			}
		});

		this.listProjects.setOnMouseClicked(new DoubleClickListener(this.handler, this.listProjects));

		// === Listening === //

	}

	private SQLRequest<Void> fetchData() {
		return () -> {
			this.projects = Project.getManager().getAllDatasets().sync();
			this.classes = SchoolClass.getManager().getAllDatasets().sync();
			
			this.classSizes = new HashMap<>();

			for (SchoolClass schoolClass : this.classes) {
				this.classSizes.put(schoolClass, schoolClass.getClassSize().sync());
			}

			return null;
		};
	}

	private void setupFiller() {
		this.listProjects.getItems().clear();

		List<Cell<Project>> projectElements = new ArrayList<>();

		for (Project project : this.projects) {
			projectElements.add(project.asCell());
		}

		Collections.sort(projectElements);

		this.fillerProjects = new ListFiller<>(this.listProjects, projectElements);
		this.fillerProjects.addChecker(new Checker<Cell<Project>>() {

			@Override
			public boolean check(Cell<Project> value) {
				int minLevel = ControllerStatsProject.this.boxMinClassLevel.getSelectionModel().getSelectedItem().getSavedObject();
				int maxLevel = ControllerStatsProject.this.boxMaxClassLevel.getSelectionModel().getSelectedItem().getSavedObject();

				return ControllerStatsProject.this.getTargetProjects(minLevel, maxLevel).contains(value.getSavedObject());
			}

		});
	}

	private void setFinalValues() {
		int[] early = this.countProjects(ProjectType.EARLY, this.projects);
		int[] late = this.countProjects(ProjectType.LATE, this.projects);
		int[] full = this.countProjects(ProjectType.FULL, this.projects);

		int students = this.countStudents(this.classes);

		this.labelTotalCountEarly.setText(Integer.toString(early[0]));
		this.labelTotalCountLate.setText(Integer.toString(late[0]));
		this.labelTotalCountFull.setText(Integer.toString(full[0]));
		this.labelTotalCountTotal.setText(Integer.toString(full[0] + Math.min(late[0], early[0])));

		this.labelTotalStudentsEarly.setText(Integer.toString(early[1]));
		this.labelTotalStudentsLate.setText(Integer.toString(late[1]));
		this.labelTotalStudentsFull.setText(Integer.toString(full[1]));
		this.labelTotalStudentsTotal.setText(Integer.toString(full[1] + (early[0] < late[0] ? early[1] : late[1])));

		this.labelTotalStudents.setText(Integer.toString(students));
		this.labelTotalClasses.setText(Integer.toString(this.classes.size()));

		for (int i = 1; i <= 7; i++) {
			int classLevel = i + 4;
			int classStudents = this.countStudents(this.getTargetClasses(classLevel, classLevel));

			int[] classEarly = this.countProjects(ProjectType.EARLY, this.getTargetProjects(classLevel, classLevel));
			int[] classLate = this.countProjects(ProjectType.LATE, this.getTargetProjects(classLevel, classLevel));
			int[] classFull = this.countProjects(ProjectType.FULL, this.getTargetProjects(classLevel, classLevel));

			this.setGridText(1, i, Integer.toString(classStudents));
			this.setGridText(2, i, Integer.toString(classFull[0] + Math.min(classEarly[0], classLate[0])));
			this.setGridText(3, i, Integer.toString(classFull[1] + (classEarly[0] < classLate[0] ? classEarly[1] : classLate[1])));
		}
	}

	private void calculateLabels() {
		int minLevel = this.boxMinClassLevel.getSelectionModel().getSelectedItem().getSavedObject();
		int maxLevel = this.boxMaxClassLevel.getSelectionModel().getSelectedItem().getSavedObject();

		List<SchoolClass> classes = this.getTargetClasses(minLevel, maxLevel);
		List<Project> projects = this.getTargetProjects(minLevel, maxLevel);

		int[] early = this.countProjects(ProjectType.EARLY, projects);
		int[] late = this.countProjects(ProjectType.LATE, projects);
		int[] full = this.countProjects(ProjectType.FULL, projects);

		int students = this.countStudents(classes);

		this.labelCountEarly.setText(Integer.toString(early[0]));
		this.labelCountLate.setText(Integer.toString(late[0]));
		this.labelCountFull.setText(Integer.toString(full[0]));
		this.labelCountTotal.setText(Integer.toString(full[0] + Math.min(late[0], early[0])));

		this.labelStudentsEarly.setText(Integer.toString(early[1]));
		this.labelStudentsLate.setText(Integer.toString(late[1]));
		this.labelStudentsFull.setText(Integer.toString(full[1]));
		this.labelStudentsTotal.setText(Integer.toString(full[1] + (early[0] < late[0] ? early[1] : late[1])));

		this.labelStudents.setText(Integer.toString(students));
		this.labelClasses.setText(Integer.toString(classes.size()));
	}

	private void setGridText(int col, int row, String text) {
		for (Node node : this.gridClasses.getChildren()) {
			if (node == null) {
				continue;
			}

			if (GridPane.getColumnIndex(node) != null && GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) != null
					&& GridPane.getRowIndex(node) == row) {
				if (node instanceof Label) {
					((Label) node).setText(text);
					return;
				}
			}
		}
	}

	private int countStudents(List<SchoolClass> classes) {
		int students = 0;

		for (SchoolClass schoolClass : classes) {
			students = students + this.classSizes.get(schoolClass);
		}

		return students;
	}

	private int[] countProjects(ProjectType type, List<Project> projects) {
		int[] result = new int[] { 0, 0 };

		for (Project project : projects) {
			if (type == project.getValue(Project.TYPE)) {
				result[0] = result[0] + 1;
				result[1] = result[1] + project.getValue(Project.MAX_STUDENTS);
			}
		}

		return result;
	}

	private List<SchoolClass> getTargetClasses(int minLevel, int maxLevel) {
		List<SchoolClass> result = new ArrayList<>();

		for (SchoolClass schoolClass : this.classes) {
			if (minLevel <= schoolClass.getValue(SchoolClass.CLASS_LEVEL) && schoolClass.getValue(SchoolClass.CLASS_LEVEL) <= maxLevel) {
				result.add(schoolClass);
			}
		}

		return result;
	}

	private List<Project> getTargetProjects(int minLevel, int maxLevel) {
		List<Project> result = new ArrayList<>();

		for (Project project : this.projects) {
			if (project.getValue(Project.MIN_CLASS) <= minLevel && project.getValue(Project.MAX_CLASS) >= maxLevel) {
				result.add(project);
			}
		}

		return result;
	}

	private void selectAllClasses() {
		this.boxMinClassLevel.getSelectionModel().select(0);
		this.boxMaxClassLevel.getSelectionModel().select(this.boxMaxClassLevel.getItems().size() - 1);
	}

}
