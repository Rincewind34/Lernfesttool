package de.rincewind.gui.controller.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import de.rincewind.api.Room;
import de.rincewind.api.SchoolClass;
import de.rincewind.api.Student;
import de.rincewind.api.Teacher;
import de.rincewind.api.abstracts.Dataset;
import de.rincewind.api.util.StudentState;
import de.rincewind.gui.controller.abstracts.ControllerEditor;
import de.rincewind.gui.util.Cell;
import de.rincewind.gui.util.FeatureTask;
import de.rincewind.gui.util.TabHandler;
import de.rincewind.gui.util.filler.ListFiller;
import de.rincewind.gui.util.filler.SearchCheck;
import de.rincewind.gui.util.listeners.ActionListener;
import de.rincewind.gui.util.listeners.DoubleClickListener;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ControllerClassEditor extends ControllerEditor {

	@FXML
	private ListView<Cell<Student>> listStudents;

	@FXML
	private TextField textSearchStudents;

	@FXML
	private Button btnOpenStudents;

	@FXML
	private Button btnCloseStudents;

	@FXML
	private Button btnSelectTeacher;

	@FXML
	private Button btnRemoveTeacher;

	@FXML
	private Button btnOpenTeacher;

	@FXML
	private Button btnSelectRoom;

	@FXML
	private Button btnRemoveRoom;

	@FXML
	private Button btnOpenRoom;

	@FXML
	private Label labelTeacher;

	@FXML
	private Label labelRoom;

	private ListFiller<Cell<Student>> fillerStudents;

	private SchoolClass schoolClass;
	private Teacher teacher;
	private Room room;
	private List<Student> students;

	public ControllerClassEditor(TabHandler handler, int classId) {
		super(handler);

		this.schoolClass = SchoolClass.getManager().newEmptyObject(classId);
	}

	@Override
	public void init() {
		this.schoolClass.fetchAll().sync();

		if (this.schoolClass.isRoomSelected()) {
			this.schoolClass.fetchRoom().sync();
			this.room = this.schoolClass.getValue(SchoolClass.ROOM);
		} else {
			this.room = null;
		}

		if (this.schoolClass.isTeacherSelected()) {
			this.schoolClass.fetchTeacher().sync();
			this.teacher = this.schoolClass.getValue(SchoolClass.TEACHER);
		} else {
			this.teacher = null;
		}

		this.students = this.schoolClass.fetchStudents().sync();

		// === Building === //

		List<Cell<Student>> students = new ArrayList<>();

		for (Student student : this.students) {
			students.add(student.asCell());
		}

		Collections.sort(students);

		this.fillerStudents = new ListFiller<Cell<Student>>(this.listStudents, students);
		this.fillerStudents.addChecker(new SearchCheck<>(this.textSearchStudents));
		
		// === Building === //
		// === Inserting === //
		
		this.fillerStudents.refresh();
		
		this.updateRoomDisplay();
		this.updateTeacherDisplay();
		
		// === Inserting === //
		// === Listening === //

		this.btnOpenStudents.setOnAction((event) -> {
			this.btnOpenStudents.setDisable(true);
			this.btnCloseStudents.setDisable(true);

			FeatureTask task = new FeatureTask(this.students.size(), () -> {
				Platform.runLater(() -> {
					this.btnOpenStudents.setDisable(false);
					this.btnCloseStudents.setDisable(false);
				});
			});

			for (Student student : this.students) {
				student.setValue(Student.STATE, StudentState.VOTE_PROJECTS);
				student.save().async((result) -> {
					task.accept();
				}, (exception) -> {
					// TODO
				});
			}
		});

		this.btnCloseStudents.setOnAction((event) -> {
			this.btnOpenStudents.setDisable(true);
			this.btnCloseStudents.setDisable(true);

			FeatureTask task = new FeatureTask(this.students.size(), () -> {
				Platform.runLater(() -> {
					this.btnOpenStudents.setDisable(false);
					this.btnCloseStudents.setDisable(false);
				});
			});

			for (Student student : this.students) {
				student.setValue(Student.STATE, StudentState.LOOKUP_PROJECTS);
				student.save().async((result) -> {
					task.accept();
				}, (exception) -> {
					// TODO
				});
			}
		});

		this.listStudents.setOnMouseClicked(new DoubleClickListener(this, this.listStudents));

		this.btnSelectTeacher.setOnAction((event) -> {
			Optional<Dataset> result = Teacher.getManager().dialogSelector().showAndWait();

			if (result.isPresent()) {
				this.teacher = (Teacher) result.get();
				this.updateTeacherDisplay();
				this.getSaveHandler().valueChanged();
			}
		});
		
		this.btnSelectRoom.setOnAction((event) -> {
			Optional<Dataset> result = Room.getManager().dialogSelector().showAndWait();
			
			if (result.isPresent()) {
				this.room = (Room) result.get();
				this.updateRoomDisplay();
				this.getSaveHandler().valueChanged();
			}
		});
		
		this.btnRemoveTeacher.setOnAction((event) -> {
			if (this.teacher == null) {
				return;
			}
			
			this.teacher = null;
			this.updateTeacherDisplay();
			this.getSaveHandler().valueChanged();
		});
		
		this.btnRemoveRoom.setOnAction((event) -> {
			if (this.room == null) {
				return;
			}
			
			this.room = null;
			this.updateRoomDisplay();
			this.getSaveHandler().valueChanged();
		});

		this.btnOpenTeacher.setOnAction(new ActionListener(this, () -> {
			return this.teacher;
		}));

		this.btnOpenRoom.setOnAction(new ActionListener(this, () -> {
			return this.room;
		}));
		
		// === Listening === //

		this.getSaveHandler().reset();
	}

	@Override
	public void saveStages() {
		this.schoolClass.setValue(SchoolClass.ROOM, this.room);
		this.schoolClass.setValue(SchoolClass.TEACHER, this.teacher);
		
		this.schoolClass.save().async((result) -> {

		}, (exception) -> {
			// TODO
		});
	}

	@Override
	public SchoolClass getEditingObject() {
		return this.schoolClass;
	}

	private void updateRoomDisplay() {
		this.btnOpenRoom.setDisable(this.room == null);
		this.btnRemoveRoom.setDisable(this.room == null);

		if (this.room == null) {
			this.labelRoom.setText("KEINE");
		} else {
			this.labelRoom.setText(this.room.toString());
		}
	}

	private void updateTeacherDisplay() {
		this.btnOpenTeacher.setDisable(this.teacher == null);
		this.btnRemoveTeacher.setDisable(this.teacher == null);

		if (this.teacher == null) {
			this.labelTeacher.setText("KEINE");
		} else {
			this.labelTeacher.setText(this.teacher.toString());
		}
	}

}
