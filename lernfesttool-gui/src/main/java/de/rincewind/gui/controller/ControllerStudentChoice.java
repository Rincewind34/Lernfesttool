package de.rincewind.gui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import de.rincewind.api.Project;
import de.rincewind.api.Student;
import de.rincewind.api.util.ProjectCategory;
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

public class ControllerStudentChoice implements Controller {

	@FXML
	private Label labelFirstEarly;

	@FXML
	private Label labelFirstLate;

	@FXML
	private Label labelFirstFull;

	@FXML
	private Label labelSecondEarly;

	@FXML
	private Label labelSecondLate;

	@FXML
	private Label labelSecondFull;

	@FXML
	private Label labelThirdEarly;

	@FXML
	private Label labelThirdLate;

	@FXML
	private Label labelThirdFull;

	@FXML
	private Label labelState;

	@FXML
	private Button btnSelectFirst;

	@FXML
	private Button btnSelectSecond;

	@FXML
	private Button btnSelectThird;

	@FXML
	private Button btnDoChoice;

	@FXML
	private CheckBox checkEarly;

	@FXML
	private CheckBox checkLate;

	@FXML
	private CheckBox checkFull;

	@FXML
	private TextField textSearch;

	@FXML
	private ListView<Cell<Project>> listProjects;

	private ListFiller<Cell<Project>> fillerProjects;

	private ProjectSet projectSet;
	private ProjectSet[] chooseSets;
	private List<Project> projects;

	private Runnable onClose;
	private Consumer<Project> onOpenProject;

	private Student student;

	public ControllerStudentChoice(Student student) {
		this.student = student;
		this.chooseSets = new ProjectSet[] { new ProjectSet(), new ProjectSet(), new ProjectSet() };
	}

	@Override
	public void init() {
		// === Building === //

		if (this.student.isSchoolClassSelected()) {
			this.student.fetchSchoolClass().sync();
			this.projects = this.student.getValue(Student.SCHOOL_CLASS).fetchPossibleProjects().sync();
		} else {
			this.projects = Project.getManager().getAllDatasets().sync();
		}

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
			this.student.choose(this.chooseSets).async((object) -> {
				
			}, (exception) -> {
				// TODO
			});
				
			if (this.onClose != null) {
				this.onClose.run();
			}
		});

		this.btnSelectFirst.setOnAction((event) -> {
			this.select(0);
		});

		this.btnSelectSecond.setOnAction((event) -> {
			this.select(1);
		});

		this.btnSelectThird.setOnAction((event) -> {
			this.select(2);
		});

		// === Listening === //

	}

	public void setOnClose(Runnable onClose) {
		this.onClose = onClose;
	}

	public void setOnOpenProject(Consumer<Project> onOpenProject) {
		this.onOpenProject = onOpenProject;
	}
	
	private void select(int index) {
		Project project = this.listProjects.getSelectionModel().getSelectedItem().getSavedObject();
		
		for (ProjectSet set : this.chooseSets) {
			set.clear(project);
		}
		
		this.chooseSets[index].setProject(project);
		
		this.fillLabels();
		this.calculateDoneButton();
	}

	private void calculateDoneButton() {
		if (this.projectSet.isLeading(ProjectType.FULL)
				|| (this.projectSet.isLeading(ProjectType.EARLY) && this.projectSet.isLeading(ProjectType.LATE))) {
			
			this.btnDoChoice.setDisable(false);
			this.btnDoChoice.setText("Wahl überspringen");
			return;
		}
		
		for (int i = 0; i < 3; i++) {
			if (this.chooseSets[i].isSet(ProjectType.FULL)) {
				continue;
			} else if (this.chooseSets[i].isSet(ProjectType.EARLY) && this.projectSet.isLeading(ProjectType.LATE)) {
				continue;
			} else if (this.chooseSets[i].isSet(ProjectType.LATE) && this.projectSet.isLeading(ProjectType.EARLY)) {
				continue;
			} else if (this.chooseSets[i].isSet(ProjectType.EARLY) && this.chooseSets[i].isSet(ProjectType.LATE)) {
				continue;
			}
			
			this.btnDoChoice.setDisable(true);
			return;
		}
		
		this.btnDoChoice.setDisable(false);
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

			ProjectCategory category = project.getValue(Project.CATEGORY);

			if (category.isSingleChoice() && type.isHalfTime()) {
				for (int i = 0; i < 3; i++) {
					if (this.chooseSets[i].isSet(type.invert()) && this.chooseSets[i].getProject(type.invert()).getValue(Project.CATEGORY) == category) {
						disable = true;
						break;
					}
				}
			}
		}

		this.btnSelectFirst.setDisable(disable);
		this.btnSelectSecond.setDisable(disable);
		this.btnSelectThird.setDisable(disable);
	}

	private void fillLabels() {
		this.labelFirstEarly.setText(this.getText(0, ProjectType.EARLY));
		this.labelFirstLate.setText(this.getText(0, ProjectType.LATE));
		this.labelFirstFull.setText(this.getText(0, ProjectType.FULL));
		this.labelSecondEarly.setText(this.getText(1, ProjectType.EARLY));
		this.labelSecondLate.setText(this.getText(1, ProjectType.LATE));
		this.labelSecondFull.setText(this.getText(1, ProjectType.FULL));
		this.labelThirdEarly.setText(this.getText(2, ProjectType.EARLY));
		this.labelThirdLate.setText(this.getText(2, ProjectType.LATE));
		this.labelThirdFull.setText(this.getText(2, ProjectType.FULL));
	}

	private String getText(int index, ProjectType type) {
		ProjectSet set = this.chooseSets[index];
		
		if (set.isSet(type)) {
			return set.getProject(type).getValue(Project.NAME);
		} else {
			return "KEINE";
		}
	}

}
