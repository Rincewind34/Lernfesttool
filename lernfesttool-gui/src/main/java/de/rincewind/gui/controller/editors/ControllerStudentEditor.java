package de.rincewind.gui.controller.editors;

import java.util.Optional;

import de.rincewind.api.Project;
import de.rincewind.api.SchoolClass;
import de.rincewind.api.Student;
import de.rincewind.api.abstracts.Dataset;
import de.rincewind.api.util.AccessLevel;
import de.rincewind.api.util.ProjectSet;
import de.rincewind.api.util.ProjectType;
import de.rincewind.api.util.StudentState;
import de.rincewind.gui.controller.abstracts.ControllerEditor;
import de.rincewind.gui.util.Cell;
import de.rincewind.gui.util.TabHandler;
import de.rincewind.gui.util.listeners.ActionListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ControllerStudentEditor extends ControllerEditor {
	
	@FXML
	private TextField textFirstname;
	
	@FXML
	private TextField textLastname;
	
	@FXML
	private TextField textPassword;
	
	@FXML
	private Button btnResetPassword;
	
	@FXML
	private ComboBox<Cell<AccessLevel>> boxAccess;
	
	@FXML
	private ComboBox<Cell<StudentState>> boxState;
	
	@FXML
	private Label labelClass;
	
	@FXML
	private Button btnClassOpen;
	
	@FXML
	private Button btnClassRemove;
	
	@FXML
	private Button btnClassSelect;
	
	@FXML
	private Label labelFullProject;
	
	@FXML
	private CheckBox checkFullProject;
	
	@FXML
	private Button btnProjectFullOpen;
	
	@FXML
	private Button btnProjectFullRemove;
	
	@FXML
	private Button btnProjectFullSelect;
	
	@FXML
	private Label labelEarlyProject;
	
	@FXML
	private CheckBox checkEarlyProject;
	
	@FXML
	private Button btnProjectEarlyOpen;
	
	@FXML
	private Button btnProjectEarlyRemove;
	
	@FXML
	private Button btnProjectEarlySelect;
	
	@FXML
	private Label labelLateProject;
	
	@FXML
	private CheckBox checkLateProject;
	
	@FXML
	private Button btnProjectLateOpen;
	
	@FXML
	private Button btnProjectLateRemove;
	
	@FXML
	private Button btnProjectLateSelect;
	
	
	private Student student;
	private SchoolClass schoolClass;
	private ProjectSet projectSet;
	
	public ControllerStudentEditor(TabHandler handler, int studentId) {
		super(handler);
		
		this.student = Student.getManager().newEmptyObject(studentId);
	}
	
	@Override
	public void init() {
		this.student.fetchAll().sync();
		this.projectSet = this.student.getLeadingProjects().sync();
		
		// === Building === //
		
		for (AccessLevel accessLevel : AccessLevel.sortedValues()) {
			this.boxAccess.getItems().add(new Cell<>(accessLevel.getName(), accessLevel));
		}
		
		for (StudentState state : StudentState.sortedValues()) {
			this.boxState.getItems().add(new Cell<>(state.getName(), state));
		}
		
		this.getSaveHandler().addValue(this.textFirstname.textProperty());
		this.getSaveHandler().addValue(this.textLastname.textProperty());
		this.getSaveHandler().addValue(this.boxState.getSelectionModel().selectedItemProperty());
		this.getSaveHandler().addValue(this.boxAccess.getSelectionModel().selectedItemProperty());
		this.getSaveHandler().addValue(this.textPassword.textProperty());
		
		// === Building === //
		// === Inserting === //
		
		this.textFirstname.setText(this.student.getValue(Student.FIRSTNAME));
		this.textLastname.setText(this.student.getValue(Student.LASTNAME));
		this.boxState.getSelectionModel().select(this.student.getValue(Student.STATE).getId());
		this.boxAccess.getSelectionModel().select(this.student.getValue(Student.ACCESS_LEVEL).getId());
		this.textPassword.setText(this.student.getValue(Student.PASSWORD));
		
		if (this.student.isSchoolClassSelected()) {
			this.student.fetchSchoolClass().sync();
			this.schoolClass = this.student.getValue(Student.SCHOOL_CLASS);
		} else {
			this.schoolClass = null;
		}
		
		this.updateClassDisplay();
		this.updateProjectDisplay(ProjectType.EARLY);
		this.updateProjectDisplay(ProjectType.LATE);
		this.updateProjectDisplay(ProjectType.FULL);
		
		// === Inserting === //
		// === Listening === //
		
		this.btnResetPassword.setOnAction((event) -> {
			this.textPassword.setText("");
		});
		
		this.textPassword.textProperty().addListener((observeable, oldValue, newValue) -> {
			if (newValue != null) {
				if (newValue.isEmpty()) {
					this.btnResetPassword.setDisable(true);
				} else {
					this.btnResetPassword.setDisable(false);
				}
			}
		});
		
		if (this.textPassword.getText().isEmpty()) {
			this.btnResetPassword.setDisable(true);
		}
		
		this.btnClassSelect.setOnAction((event) -> {
			Optional<Dataset> result = SchoolClass.getManager().dialogSelector().showAndWait();
			
			if (result.isPresent()) {
				this.schoolClass = (SchoolClass) result.get();
				this.updateClassDisplay();
				this.getSaveHandler().valueChanged();
			}
		});
		
		this.btnProjectEarlySelect.setOnAction((event) -> {
			Optional<Dataset> result = Project.getManager().dialogSelector(ProjectType.EARLY).showAndWait();
			
			if (result.isPresent()) {
				this.projectSet.setProject((Project) result.get());
				this.projectSet.setLeading(ProjectType.EARLY, false);
				this.updateProjectDisplay(ProjectType.EARLY);
				this.getSaveHandler().valueChanged();
			}
		});
		
		this.btnProjectLateSelect.setOnAction((event) -> {
			Optional<Dataset> result = Project.getManager().dialogSelector(ProjectType.LATE).showAndWait();
			
			if (result.isPresent()) {
				this.projectSet.setProject((Project) result.get());
				this.projectSet.setLeading(ProjectType.LATE, false);
				this.updateProjectDisplay(ProjectType.LATE);
				this.getSaveHandler().valueChanged();
			}
		});
		
		this.btnProjectFullSelect.setOnAction((event) -> {
			Optional<Dataset> result = Project.getManager().dialogSelector(ProjectType.FULL).showAndWait();
			
			if (result.isPresent()) {
				this.projectSet.setProject((Project) result.get());
				this.projectSet.setLeading(ProjectType.FULL, false);
				this.updateProjectDisplay(ProjectType.FULL);
				this.getSaveHandler().valueChanged();
			}
		});
		
		this.btnClassSelect.setOnAction((event) -> {
			Optional<Dataset> result = SchoolClass.getManager().dialogSelector().showAndWait();
			
			if (result.isPresent()) {
				this.schoolClass = (SchoolClass) result.get();
				this.updateClassDisplay();
				this.getSaveHandler().valueChanged();
			}
		});
		
		this.btnClassRemove.setOnAction((event) -> {
			if (this.schoolClass == null) {
				return;
			}
			
			this.schoolClass = null;
			this.updateClassDisplay();
			this.getSaveHandler().valueChanged();
		});
		
		this.btnProjectEarlyRemove.setOnAction((event) -> {
			if (!this.projectSet.isSet(ProjectType.EARLY)) {
				return;
			}
			
			this.projectSet.clear(ProjectType.EARLY);
			this.updateProjectDisplay(ProjectType.EARLY);
			this.getSaveHandler().valueChanged();
		});
		
		this.btnProjectLateRemove.setOnAction((event) -> {
			if (!this.projectSet.isSet(ProjectType.LATE)) {
				return;
			}
			
			this.projectSet.clear(ProjectType.LATE);
			this.updateProjectDisplay(ProjectType.LATE);
			this.getSaveHandler().valueChanged();
		});
		
		this.btnProjectFullRemove.setOnAction((event) -> {
			if (!this.projectSet.isSet(ProjectType.FULL)) {
				return;
			}
			
			this.projectSet.clear(ProjectType.FULL);
			this.updateProjectDisplay(ProjectType.FULL);
			this.getSaveHandler().valueChanged();
		});
		
		this.checkEarlyProject.selectedProperty().addListener((observeable, oldValue, newValue) -> {
			this.projectSet.setLeading(ProjectType.EARLY, newValue);
			this.updateProjectDisplay(ProjectType.EARLY);
		});
		
		this.checkLateProject.selectedProperty().addListener((observeable, oldValue, newValue) -> {
			this.projectSet.setLeading(ProjectType.LATE, newValue);
			this.updateProjectDisplay(ProjectType.LATE);
		});
		
		this.checkFullProject.selectedProperty().addListener((observeable, oldValue, newValue) -> {
			this.projectSet.setLeading(ProjectType.FULL, newValue);
			this.updateProjectDisplay(ProjectType.FULL);
		});
		
		this.btnClassOpen.setOnAction(new ActionListener(this, () -> {
			return this.schoolClass;
		}));
		
		this.btnProjectEarlyOpen.setOnAction(new ActionListener(this, () -> {
			return this.projectSet.getProject(ProjectType.EARLY);
		}));
		
		this.btnProjectLateOpen.setOnAction(new ActionListener(this, () -> {
			return this.projectSet.getProject(ProjectType.LATE);
		}));
		
		this.btnProjectFullOpen.setOnAction(new ActionListener(this, () -> {
			return this.projectSet.getProject(ProjectType.FULL);
		}));
		
		this.getSaveHandler().addValue(this.checkEarlyProject.selectedProperty());
		this.getSaveHandler().addValue(this.checkLateProject.selectedProperty());
		this.getSaveHandler().addValue(this.checkFullProject.selectedProperty());
		
		// === Listening === //
		
		this.getSaveHandler().reset();
	}
	
	@Override
	public void saveStages() {
		this.student.setValue(Student.FIRSTNAME, this.textFirstname.getText());
		this.student.setValue(Student.LASTNAME, this.textLastname.getText());
		this.student.setValue(Student.PASSWORD, this.textPassword.getText());
		
		this.student.setValue(Student.ACCESS_LEVEL, this.boxAccess.getSelectionModel().getSelectedItem().getSavedObject());
		this.student.setValue(Student.STATE, this.boxState.getSelectionModel().getSelectedItem().getSavedObject());
		
		this.student.setValue(Student.SCHOOL_CLASS, this.schoolClass);
		
		this.student.save().async((object) -> {
			
		}, (exception) -> {
			// TODO
		});
		
		this.projectSet.save().async((object) -> {
			
		}, (exception) -> {
			// TODO
		});
	}
	
	@Override
	public Student getEditingObject() {
		return this.student;
	}
	
	private void updateClassDisplay() {
		this.btnClassOpen.setDisable(this.schoolClass == null);
		this.btnClassRemove.setDisable(this.schoolClass == null);
		
		if (this.schoolClass == null) {
			this.labelClass.setText("KEINE");
		} else {
			this.labelClass.setText(this.schoolClass.toString());
		}
	}
	
	private void updateProjectDisplay(ProjectType type) {
		if (type == ProjectType.FULL) {
			this.btnProjectFullOpen.setDisable(!this.projectSet.isSet(ProjectType.FULL));
			this.btnProjectFullRemove.setDisable(!this.projectSet.isSet(ProjectType.FULL));
			this.checkFullProject.setDisable(!this.projectSet.isSet(ProjectType.FULL));
			
			if (this.projectSet.isSet(ProjectType.FULL)) {
				this.checkFullProject.setSelected(this.projectSet.isLeading(ProjectType.FULL));
				this.labelFullProject.setText(this.projectSet.getProject(ProjectType.FULL).toString());
			} else {
				this.checkFullProject.setSelected(false);
				this.labelFullProject.setText("KEINES");
			}
		} else if (type == ProjectType.EARLY) {
			this.btnProjectEarlyOpen.setDisable(!this.projectSet.isSet(ProjectType.EARLY));
			this.btnProjectEarlyRemove.setDisable(!this.projectSet.isSet(ProjectType.EARLY));
			this.checkEarlyProject.setDisable(!this.projectSet.isSet(ProjectType.EARLY));
			
			if (this.projectSet.isSet(ProjectType.EARLY)) {
				this.checkEarlyProject.setSelected(this.projectSet.isLeading(ProjectType.EARLY));
				this.labelEarlyProject.setText(this.projectSet.getProject(ProjectType.EARLY).toString());
			} else {
				this.checkEarlyProject.setSelected(false);
				this.labelEarlyProject.setText("KEINES");
			}
		} else if (type == ProjectType.LATE) {
			this.btnProjectLateOpen.setDisable(!this.projectSet.isSet(ProjectType.LATE));
			this.btnProjectLateRemove.setDisable(!this.projectSet.isSet(ProjectType.LATE));
			this.checkLateProject.setDisable(!this.projectSet.isSet(ProjectType.LATE));
			
			if (this.projectSet.isSet(ProjectType.LATE)) {
				this.checkLateProject.setSelected(this.projectSet.isLeading(ProjectType.LATE));
				this.labelLateProject.setText(this.projectSet.getProject(ProjectType.LATE).toString());
			} else {
				this.checkLateProject.setSelected(false);
				this.labelLateProject.setText("KEINES");
			}
		}
	}
	
}
