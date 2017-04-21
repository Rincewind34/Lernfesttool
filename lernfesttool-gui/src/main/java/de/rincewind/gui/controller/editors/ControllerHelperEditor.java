package de.rincewind.gui.controller.editors;

import java.util.Optional;

import de.rincewind.api.Helper;
import de.rincewind.api.Project;
import de.rincewind.api.Student;
import de.rincewind.api.abstracts.Dataset;
import de.rincewind.api.util.ProjectSet;
import de.rincewind.api.util.ProjectType;
import de.rincewind.gui.controller.abstracts.ControllerEditor;
import de.rincewind.gui.util.TabHandler;
import de.rincewind.gui.util.listeners.ActionListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ControllerHelperEditor extends ControllerEditor {

	@FXML
	private TextField textName;

	@FXML
	private Button btnSelectStudent;

	@FXML
	private Button btnRemoveStudent;

	@FXML
	private Button btnOpenStudent;

	@FXML
	private Label labelStudent;
	
	@FXML
	private Label labelEarly;
	
	@FXML
	private Label labelLate;
	
	@FXML
	private Label labelFull;
	
	@FXML
	private Button btnEarly;
	
	@FXML
	private Button btnLate;
	
	@FXML
	private Button btnFull;

	private Helper helper;
	private Student student;
	private ProjectSet projectSet;

	public ControllerHelperEditor(TabHandler handler, int helperId) {
		super(handler);

		this.helper = Helper.getManager().newEmptyObject(helperId);
	}

	@Override
	public void init() {
		this.helper.fetchAll().sync();
		
		if (this.helper.isStudentSelected()) {
			this.helper.fetchStudent().sync();
			this.student = this.helper.getValue(Helper.STUDENT);
		} else {
			this.student = null;
		}
		
		this.projectSet = this.helper.getLeadingProjects().sync();

		// === Building === //

		this.getSaveHandler().addValue(this.textName.textProperty());

		// === Building === //
		// === Inserting === //
		
		for (Project project : this.projectSet.projects()) {
			if (project.getValue(Project.TYPE) == ProjectType.EARLY) {
				this.labelEarly.setText(project.toString());
				this.btnEarly.setDisable(false);
			} else if (project.getValue(Project.TYPE) == ProjectType.LATE) {
				this.labelLate.setText(project.toString());
				this.btnLate.setDisable(false);
			} else if (project.getValue(Project.TYPE) == ProjectType.FULL) {
				this.labelFull.setText(project.toString());
				this.btnFull.setDisable(false);
			} 
		}
		
		this.textName.setText(this.helper.getValue(Helper.NAME));
		this.updateStudentDisplay();

		// === Inserting === //
		// === Listening === //

		this.btnSelectStudent.setOnAction((event) -> {
			Optional<Dataset> result = Student.getManager().dialogSelector().showAndWait();

			if (result.isPresent()) {
				this.student = (Student) result.get();
				this.updateStudentDisplay();
				this.getSaveHandler().valueChanged();
			}
		});

		this.btnRemoveStudent.setOnAction((event) -> {
			if (this.student == null) {
				return;
			}

			this.student = null;
			this.updateStudentDisplay();
			this.getSaveHandler().valueChanged();
		});

		this.btnOpenStudent.setOnAction(new ActionListener(this, () -> {
			return this.student;
		}));

		// === Listening === //

		this.getSaveHandler().reset();
	}

	@Override
	public void saveStages() {
		this.helper.setValue(Helper.NAME, this.textName.getText());
		this.helper.setValue(Helper.STUDENT, this.student);
		this.helper.save().async((result) -> {

		}, (exception) -> {
			// TODO
		});
	}

	@Override
	public Helper getEditingObject() {
		return this.helper;
	}

	private void updateStudentDisplay() {
		this.btnOpenStudent.setDisable(this.student == null);
		this.btnRemoveStudent.setDisable(this.student == null);

		if (this.student == null) {
			this.labelStudent.setText("KEINE");
		} else {
			this.labelStudent.setText(this.student.toString());
		}
	}

	public TextField getTextName() {
		return this.textName;
	}

}
