package de.rincewind.gui.controller.editors;

import de.rincewind.api.Project;
import de.rincewind.api.Teacher;
import de.rincewind.api.util.ProjectSet;
import de.rincewind.api.util.ProjectType;
import de.rincewind.gui.controller.abstracts.ControllerEditor;
import de.rincewind.gui.util.TabHandler;
import de.rincewind.gui.util.listeners.ActionListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ControllerTeacherEditor extends ControllerEditor {
	
	@FXML
	private TextField textName;
	
	@FXML
	private TextField textToken;
	
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
	
	private Teacher teacher;
	private ProjectSet projectSet;
	
	public ControllerTeacherEditor(TabHandler handler, int teacherId) {
		super(handler);
		
		this.teacher = Teacher.getManager().newEmptyObject(teacherId);
	}
	
	@Override
	public void init() {
		this.teacher.fetchAll().sync();
		this.projectSet = this.teacher.getLeadingProjects().sync();
		
		// === Building === //
		
		this.getSaveHandler().addValue(this.textName.textProperty());
		this.getSaveHandler().addValue(this.textToken.textProperty());
		
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
		
		this.textName.setText(this.teacher.getValue(Teacher.NAME));
		this.textToken.setText(this.teacher.getValue(Teacher.TOKEN));
		
		// === Inserting === //
		// === Listening === //
		
		this.btnEarly.setOnAction(new ActionListener(this.getTabHandler(), () -> {
			return this.projectSet.getProject(ProjectType.EARLY);
		}));
		
		this.btnLate.setOnAction(new ActionListener(this.getTabHandler(), () -> {
			return this.projectSet.getProject(ProjectType.LATE);
		}));
		
		this.btnFull.setOnAction(new ActionListener(this.getTabHandler(), () -> {
			return this.projectSet.getProject(ProjectType.FULL);
		}));
		
		// === Listening === //
		
		this.getSaveHandler().reset();
	}
	
	@Override
	public void saveStages() {
		this.teacher.setValue(Teacher.NAME, this.textName.getText());
		this.teacher.setValue(Teacher.TOKEN, this.textToken.getText());
		this.teacher.save().async((result) -> {
			
		}, (exception) -> {
			// TODO
		});
	}
	
	@Override
	public Teacher getEditingObject() {
		return this.teacher;
	}
	
	public TextField getTextName() {
		return this.textName;
	}
	
}
