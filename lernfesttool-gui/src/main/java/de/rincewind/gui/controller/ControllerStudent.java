package de.rincewind.gui.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.rincewind.api.Project;
import de.rincewind.api.SchoolClass;
import de.rincewind.api.Student;
import de.rincewind.api.util.ProjectSet;
import de.rincewind.api.util.ProjectType;
import de.rincewind.api.util.StudentState;
import de.rincewind.gui.controller.abstracts.Controller;
import de.rincewind.gui.dialogs.DialogProjectCreator;
import de.rincewind.gui.main.GUIHandler;
import de.rincewind.gui.util.Cell;
import de.rincewind.gui.util.filler.CheckBoxCheck;
import de.rincewind.gui.util.filler.ListFiller;
import de.rincewind.gui.util.filler.SearchCheck;
import de.rincewind.gui.util.listeners.DoubleClickListener;
import de.rincewind.gui.windows.Window;
import de.rincewind.gui.windows.WindowStudentProjectVisitor;
import de.rincewind.gui.windows.WindowStudent;
import de.rincewind.gui.windows.WindowStudentProjectEditor;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ControllerStudent implements Controller {

	@FXML
	private CheckBox checkFullProjects;

	@FXML
	private CheckBox checkEarlyProjects;

	@FXML
	private CheckBox checkLateProjects;

	@FXML
	private CheckBox checkJoinableProjects;

	@FXML
	private TextField textSearch;

	@FXML
	private Button buttonManage;

	@FXML
	private Button buttonLogout;

	@FXML
	private ComboBox<Cell<Project>> boxProjects;

	@FXML
	private ListView<Cell<Project>> listProjects;

	private ListFiller<Cell<Project>> filler;

	private Student student;

	public ControllerStudent(Student student) {
		this.student = student;
	}

	@Override
	public void init() {
		this.student.fetchAll().sync();

		if (this.student.isSchoolClassSelected()) {
			this.student.fetchSchoolClass().sync();
		}

		List<Project> projects = Project.getManager().getAllDatasets().sync();
		ProjectSet leadings = this.student.getLeadingProjects().sync();

		// === Building === //

		this.filler = new ListFiller<Cell<Project>>(this.listProjects, projects.stream().map((project) -> {
			return project.asCell(Project.class);
		}).sorted().collect(Collectors.toList()));

		this.filler.addChecker(new SearchCheck<>(this.textSearch));
		this.filler.addChecker(new CheckBoxCheck<>(this.checkFullProjects, (value, cell) -> {
			return value ? true : cell.getSavedObject().getValue(Project.TYPE) != ProjectType.FULL;
		}));
		this.filler.addChecker(new CheckBoxCheck<>(this.checkEarlyProjects, (value, cell) -> {
			return value ? true : cell.getSavedObject().getValue(Project.TYPE) != ProjectType.EARLY;
		}));
		this.filler.addChecker(new CheckBoxCheck<>(this.checkLateProjects, (value, cell) -> {
			return value ? true : cell.getSavedObject().getValue(Project.TYPE) != ProjectType.LATE;
		}));
		this.filler.addChecker(new CheckBoxCheck<>(this.checkJoinableProjects, (value, cell) -> {
			if (!value || !this.student.isSchoolClassSelected()) {
				return true;
			}

			int classLevel = this.student.getValue(Student.SCHOOL_CLASS).getValue(SchoolClass.CLASS_LEVEL);
			return cell.getSavedObject().getValue(Project.MIN_CLASS) <= classLevel && classLevel <= cell.getSavedObject().getValue(Project.MAX_CLASS);
		}));

		// === Building === //
		// === Inserting === //

		this.filler.refresh();
		
		if (this.student.getValue(Student.STATE) == StudentState.ENTER_PROJECTS) {
			if ((leadings.leadingAmount() == 0 || !leadings.isLeading(ProjectType.FULL))) {
				this.boxProjects.getItems().add(new Cell<>("<Neues erstellen>", null));
			}
		}

		for (Project project : leadings.leadingProjects()) {
			this.boxProjects.getItems().add(project.asCell());
		}

		this.boxProjects.getSelectionModel().select(0);
		this.boxProjects.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			this.calculateButton();
		});

		this.calculateButton();
		
		if (this.student.getValue(Student.STATE) != StudentState.ENTER_PROJECTS && leadings.leadingAmount() == 0) {
			this.boxProjects.setDisable(true);
		}

		// === Inserting === //
		// === Listening === //

		this.listProjects.setOnMouseClicked(new DoubleClickListener(() -> {
			WindowStudentProjectVisitor window = new WindowStudentProjectVisitor(this.listProjects.getSelectionModel().getSelectedItem().getSavedObject());
			window.setOnClose(() -> {
				GUIHandler.session().changeWindow(new WindowStudent(this.student));
			});

			GUIHandler.session().changeWindow(window);
		}));

		this.buttonLogout.setOnAction((event) -> {
			GUIHandler.session().logout();
		});

		this.buttonManage.setOnAction((event) -> {
			Project project = this.boxProjects.getSelectionModel().getSelectedItem().getSavedObject();
			
			if (project == null && this.student.getValue(Student.STATE) == StudentState.ENTER_PROJECTS) {
				ProjectType type = null;

				if (leadings.leadingAmount() == 1) {
					type = leadings.getFirstLeadingOne().getValue(Project.TYPE).invert();
				}

				DialogProjectCreator creator = new DialogProjectCreator(this.student.getId(), type);
				Optional<Project> result = creator.showAndWait();

				if (result.isPresent()) {
					project = result.get();
				}
			}
			
			if (project == null) {
				return;
			}
			
			Window<?> window;
			
			if (this.student.getValue(Student.STATE) == StudentState.ENTER_PROJECTS) {
				window = new WindowStudentProjectEditor(project, this.student);
			} else {
				window = new WindowStudentProjectVisitor(project);
			}
			
			GUIHandler.session().changeWindow(window);
		});

		// === Listening === //
	}

	private void calculateButton() {
		this.buttonManage.setDisable(this.boxProjects.getSelectionModel().getSelectedIndex() == -1);
		
		if (this.student.getValue(Student.STATE) == StudentState.ENTER_PROJECTS) {
			if (this.boxProjects.getSelectionModel().getSelectedItem().getSavedObject() == null) {
				this.buttonManage.setText("Projekt erstellen");
			} else {
				this.buttonManage.setText("Projekt bearbeiten");
			}
		} else {
			this.buttonManage.setText("Projekt öffnen");
		}
	}

}
