package de.rincewind.gui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import de.rincewind.api.Project;
import de.rincewind.api.Student;
import de.rincewind.api.util.ProjectSet;
import de.rincewind.api.util.ProjectType;
import de.rincewind.gui.controller.abstracts.Controller;
import de.rincewind.gui.util.Cell;
import de.rincewind.gui.util.filler.CheckBoxCheck;
import de.rincewind.gui.util.filler.ListFiller;
import de.rincewind.gui.util.filler.SearchCheck;
import de.rincewind.gui.util.listeners.DoubleClickListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ControllerStudentSecondChoice implements Controller {

	@FXML
	private Label labelEarlyChoice;

	@FXML
	private Label labelLateChoice;

	@FXML
	private Label labelFullChoice;

	@FXML
	private Label labelState;

	@FXML
	private CheckBox checkEarly;

	@FXML
	private CheckBox checkLate;

	@FXML
	private CheckBox checkFull;

	@FXML
	private Button btnDoChoice;

	@FXML
	private Button btnSelect;

	@FXML
	private TextField textSearch;

	@FXML
	private ListView<Cell<Project>> listProjects;

	private Student student;
	private List<Project> projects;
	private ProjectSet projectSet;

	private Runnable onClose;
	private Consumer<Project> onOpenProject;

	private ListFiller<Cell<Project>> fillerProjects;

	public ControllerStudentSecondChoice(Student student) {
		this.student = student;
		this.projectSet = new ProjectSet();
	}

	@Override
	public void init() {
		if (this.student.isSchoolClassSelected()) {
			this.student.fetchSchoolClass().sync();
			this.projects = this.student.getValue(Student.SCHOOL_CLASS).fetchPossibleProjects().sync();
		} else {
			this.projects = Project.getManager().getAllDatasets().sync();
		}
		
		this.projects = Project.getManager().filterFullProjects(this.projects).sync();

		// === Building === //

		List<Cell<Project>> projectElements = new ArrayList<>();

		for (Project project : this.projects) {
			projectElements.add(project.asCell());
		}

		Collections.sort(projectElements);

		this.fillerProjects = new ListFiller<>(this.listProjects, projectElements);
		this.fillerProjects.addChecker(new SearchCheck<Cell<Project>>(this.textSearch));
		this.fillerProjects.addChecker(new CheckBoxCheck<>(this.checkEarly, (checked, value) -> {
			return checked ? true : (value.getSavedObject().getValue(Project.TYPE) != ProjectType.EARLY);
		}));
		this.fillerProjects.addChecker(new CheckBoxCheck<>(this.checkLate, (checked, value) -> {
			return checked ? true : (value.getSavedObject().getValue(Project.TYPE) != ProjectType.LATE);
		}));
		this.fillerProjects.addChecker(new CheckBoxCheck<>(this.checkFull, (checked, value) -> {
			return checked ? true : (value.getSavedObject().getValue(Project.TYPE) != ProjectType.FULL);
		}));

		this.projectSet = this.student.getLeadingProjects().sync();

		// === Building === //
		// === Inserting === //

		this.fillerProjects.refresh();
		this.calculateDoneButton();
		this.calculateChoiceButtons();
		this.fillLabels();

		if (this.projectSet.leadingAmount() == 0) {
			this.labelState.setText("Du leitest kein Projekt");
		} else if (this.projectSet.leadingAmount() == 1) {
			this.labelState.setText("Du leitest ein " + this.projectSet.getFirstLeadingOne().getValue(Project.TYPE).getAdjective() + " Projekt");
		} else if (this.projectSet.leadingAmount() == 2) {
			this.labelState.setText("Du leitest ein frühes und spätes Projekt");
		} else {
			this.labelState.setText("Es liegt ein FEHLER in der Datenbank vor,bitte melde diesen Vorfall!");
		}

		// === Inserting === //
		// === Listening === //

		this.listProjects.getSelectionModel().selectedItemProperty().addListener((observeable, oldValue, newValue) -> {
			this.calculateChoiceButtons();
		});

		this.listProjects.setOnMouseClicked(new DoubleClickListener(() -> {
			if (this.onOpenProject != null) {
				this.onOpenProject.accept(this.listProjects.getSelectionModel().getSelectedItem().getSavedObject());
			}
		}));

		this.btnDoChoice.setOnAction((event) -> {
			this.student.chooseSecond(this.projectSet).async((object) -> {

			}, (exception) -> {
				// TODO
			});

			if (this.onClose != null) {
				this.onClose.run();
			}
		});
		
		this.btnSelect.setOnAction((event) -> {
			this.projectSet.setProject(this.listProjects.getSelectionModel().getSelectedItem().getSavedObject());

			this.fillLabels();
			this.calculateDoneButton();
		});
		
		// === Listening === //
	}

	public void setOnClose(Runnable onClose) {
		this.onClose = onClose;
	}

	public void setOnOpenProject(Consumer<Project> onOpenProject) {
		this.onOpenProject = onOpenProject;
	}

	private void calculateDoneButton() {
		if (this.projectSet.isLeading(ProjectType.FULL) || (this.projectSet.isLeading(ProjectType.EARLY) && this.projectSet.isLeading(ProjectType.LATE))) {

			this.btnDoChoice.setDisable(false);
			this.btnDoChoice.setText("Wahl überspringen");
			return;
		}

		this.btnDoChoice.setDisable(!this.projectSet.isComplete());
	}

	private void calculateChoiceButtons() {
		boolean disable = false;

		if (this.listProjects.getSelectionModel().getSelectedItem() == null || this.projectSet.isLeading(ProjectType.FULL)) {
			disable = true;
		} else {
			Project project = this.listProjects.getSelectionModel().getSelectedItem().getSavedObject();
			ProjectType type = project.getValue(Project.TYPE);

			if ((type == ProjectType.EARLY || type == ProjectType.FULL) && this.projectSet.isLeading(ProjectType.EARLY)) {
				disable = true;
			} else if ((type == ProjectType.LATE || type == ProjectType.FULL) && this.projectSet.isLeading(ProjectType.LATE)) {
				disable = true;
			}
		}

		this.btnSelect.setDisable(disable);
	}

	private void fillLabels() {
		this.labelEarlyChoice.setText(this.getText(ProjectType.EARLY));
		this.labelLateChoice.setText(this.getText(ProjectType.LATE));
		this.labelFullChoice.setText(this.getText(ProjectType.FULL));
	}

	private String getText(ProjectType type) {
		if (this.projectSet.isLeading(type)) {
			return "LEITUNG";
		} else if ((type == ProjectType.FULL && (this.projectSet.isLeading(ProjectType.EARLY) || this.projectSet.isLeading(ProjectType.LATE)))
				|| (type.isHalfTime() && this.projectSet.isLeading(ProjectType.FULL))) {
			return "= / =";
		} else {
			return this.projectSet.isSet(type) ? this.projectSet.getProject(type).getValue(Project.NAME) : "KEINE";
		}
	}

}
