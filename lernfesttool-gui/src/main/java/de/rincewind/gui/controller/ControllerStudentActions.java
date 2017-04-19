package de.rincewind.gui.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.rincewind.api.Project;
import de.rincewind.api.Room;
import de.rincewind.api.SchoolClass;
import de.rincewind.api.Student;
import de.rincewind.api.abstracts.Dataset;
import de.rincewind.api.util.ProjectSet;
import de.rincewind.api.util.StudentState;
import de.rincewind.gui.controller.abstracts.Controller;
import de.rincewind.gui.util.Callback;
import de.rincewind.gui.util.Cell;
import de.rincewind.gui.util.TabHandler;
import de.rincewind.gui.util.filler.BoxCheck;
import de.rincewind.gui.util.filler.ListCheck;
import de.rincewind.gui.util.filler.ListFiller;
import de.rincewind.gui.util.listeners.DoubleClickListener;
import de.rincewind.sql.tables.relations.TableProjectAttandences;
import de.rincewind.sql.tables.relations.TableProjectChoosing;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

public class ControllerStudentActions implements Controller {

	@FXML
	private Button btnExecute;

	@FXML
	private Button btnExecuteAll;

	@FXML
	private ComboBox<Cell<Integer>> boxTarget;

	@FXML
	private ComboBox<Cell<Integer>> boxAction;

	@FXML
	private ListView<Cell<SchoolClass>> listClasses;

	@FXML
	private ListView<Cell<Student>> listStudents;

	private ListFiller<Cell<Student>> fillerStudents;

	private TabHandler handler;

	public ControllerStudentActions(TabHandler handler) {
		this.handler = handler;
	}

	@Override
	public void init() {
		Map<Integer, Project> projectMap = Dataset.convertList(Project.getManager().getAllDatasets().sync());
		List<TableProjectAttandences.Entry> attandences = TableProjectAttandences.instance().getEntries().sync();
		List<TableProjectChoosing.Entry> choosing = TableProjectChoosing.instance().getEntries().sync();

		List<SchoolClass> classes = SchoolClass.getManager().getAllDatasets(Dataset.convertList(Room.getManager().getAllDatasets().sync())).sync();
		List<Student> students = Student.getManager().getAllDatasets(Dataset.convertList(classes)).sync();
		List<Student> withoutChoice = Student.getManager().getWithoutChoice(Dataset.convertList(students), ProjectSet.convertChooses(choosing, projectMap),
				ProjectSet.convertLeadings(attandences, projectMap));
		List<Student> withoutProject = Student.getManager().getWithoutProject(Dataset.convertList(students),
				ProjectSet.convertAttandences(attandences, projectMap));

		Collections.sort(classes);

		// === Building === //

		this.boxTarget.getItems().add(new Cell<>("Schüler aus Klasse auswählen", 0));
		this.boxTarget.getItems().add(new Cell<>("Schüler ohne Wahl auswählen", 1));
		this.boxTarget.getItems().add(new Cell<>("Schüler ohne Projekt auswählen", 2));

		this.boxAction.getItems().add(new Cell<>("Schüler auf 'Projekte eingeben' setzten", 0));
		this.boxAction.getItems().add(new Cell<>("Schüler auf 'Projekte ansehen' setzten", 1));
		this.boxAction.getItems().add(new Cell<>("Schüler auf 'Projekte wählen' setzten", 2));
		this.boxAction.getItems().add(new Cell<>("Schüler auf 'Projekte nachwählen' setzten", 3));

		for (SchoolClass schoolClass : classes) {
			this.listClasses.getItems().add(schoolClass.asCell());
		}

		this.fillerStudents = new ListFiller<>(this.listStudents, students.stream().map((student) -> {
			return student.asCell(Student.class);
		}).sorted().collect(Collectors.toList()));
		this.fillerStudents.addChecker(new BoxCheck<>(this.boxTarget, (student, selected) -> {
			if (selected.getSavedObject() == 0) {
				if (!student.getSavedObject().isSchoolClassSelected()) {
					return false;
				}

				if (this.listClasses.getSelectionModel().isEmpty()) {
					return false;
				}

				return this.listClasses.getSelectionModel().getSelectedItem().getSavedObject().getId() == student.getSavedObject()
						.getValue(Student.SCHOOL_CLASS).getId();
			} else if (selected.getSavedObject() == 1) {
				return withoutChoice.contains(student.getSavedObject());
			} else if (selected.getSavedObject() == 2) {
				return withoutProject.contains(student.getSavedObject());
			} else {
				return false;
			}
		}));
		this.fillerStudents.addChecker(new ListCheck<>(this.listClasses, (student, selected) -> {
			if (this.boxTarget.getSelectionModel().getSelectedItem().getSavedObject() != 0) {
				return true;
			}

			if (selected == null) {
				return false;
			} else {
				if (!student.getSavedObject().isSchoolClassSelected()) {
					return false;
				}

				return student.getSavedObject().getValue(Student.SCHOOL_CLASS).getId() == selected.getSavedObject().getId();
			}
		}));

		// === Building === //
		// === Inserting === //

		this.boxTarget.getSelectionModel().select(0);
		this.boxAction.getSelectionModel().select(0);

		this.fillerStudents.refresh();

		// === Inserting === //
		// === Listening === //

		this.boxTarget.getSelectionModel().selectedItemProperty().addListener((observeable, oldValue, newValue) -> {
			if (newValue != null && newValue.getSavedObject() == 0) {
				this.listClasses.setDisable(false);
			} else {
				this.listClasses.setDisable(true);
				this.listClasses.getSelectionModel().clearSelection();
			}
		});

		this.btnExecute.setOnAction((event) -> {
			this.execute(this.listStudents.getItems().stream().map((cell) -> {
				return cell.getSavedObject();
			}).collect(Collectors.toList()), this.boxAction.getSelectionModel().getSelectedItem().getSavedObject());
		});

		this.btnExecuteAll.setOnAction((event) -> {
			this.execute(students, this.boxAction.getSelectionModel().getSelectedItem().getSavedObject());
		});

		this.listStudents.setOnMouseClicked(new DoubleClickListener(this.handler, this.listStudents));
		this.listClasses.setOnMouseClicked(new DoubleClickListener(this.handler, this.listClasses));

		// === Listening === //
	}
	
	public List<Student> getSelectedStudents() {
		return this.listStudents.getItems().stream().map((cell) -> {
			return cell.getSavedObject();
		}).collect(Collectors.toList());
	}
	
	private void setButtonsDisable(boolean value) {
		this.btnExecute.setDisable(value);
		this.btnExecuteAll.setDisable(value);
	}

	private void execute(List<Student> students, int action) {
		if (students.size() == 0) {
			return;
		}

		this.setButtonsDisable(true);

		Callback callback = new Callback(students.size(), () -> {
			this.setButtonsDisable(false);
		});

		for (Student student : students) {
			if (action == 0) {
				student.setValue(Student.STATE, StudentState.ENTER_PROJECTS);
			} else if (action == 1) {
				student.setValue(Student.STATE, StudentState.LOOKUP_PROJECTS);
			} else if (action == 2) {
				student.setValue(Student.STATE, StudentState.VOTE_PROJECTS);
			} else if (action == 3) {
				student.setValue(Student.STATE, StudentState.SEC_VOTE_PROJECTS);
			}

			student.save().async((result) -> {
				callback.accept();
			}, (exception) -> {
				// TODO
			});
		}
	}

}
