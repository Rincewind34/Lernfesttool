package de.rincewind.gui.controller;

import java.util.List;

import de.rincewind.api.Guide;
import de.rincewind.api.Project;
import de.rincewind.api.Student;
import de.rincewind.gui.controller.abstracts.Controller;
import de.rincewind.gui.util.Cell;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ControllerStudentProjectVisitor implements Controller {
	
	@FXML
	private Button buttonClose;

	@FXML
	private TextArea textDescription;
	
	@FXML
	private TextField textName;

	@FXML
	private TextField textCategory;

	@FXML
	private TextField textType;

	@FXML
	private TextField textMinClass;

	@FXML
	private TextField textMaxClass;

	@FXML
	private TextField textMinStudents;

	@FXML
	private TextField textMaxStudents;

	@FXML
	private TextField textCosts;

	@FXML
	private TextField textRoom;

	@FXML
	private ListView<Cell<Guide>> listGuides;
	
	@FXML
	private ListView<Cell<Student>> listLeaders;
	
	
	private Project project;
	
	private Runnable onClose;
	
	public ControllerStudentProjectVisitor(Project project) {
		this.project = project;
	}
	
	@Override
	public void init() {
		this.project.fetchAll().sync();
		
		if (this.project.isRoomSelected()) {
			this.project.fetchRoom().sync();
		}
		
		List<Student> leaders = this.project.fetchLeaders().sync();
		List<Guide> guides = this.project.fetchGuides().sync();
		
		// === Inserting === //
		
		this.textName.setText(this.project.getValue(Project.NAME));
		this.textDescription.setText(this.project.getValue(Project.DESCRIPTION));
		this.textCategory.setText(this.project.getValue(Project.CATEGORY).getName());
		this.textType.setText(this.project.getValue(Project.TYPE).getName());
		this.textMinClass.setText(Integer.toString(this.project.getValue(Project.MIN_CLASS)));
		this.textMaxClass.setText(Integer.toString(this.project.getValue(Project.MAX_CLASS)));
		this.textMinStudents.setText(Integer.toString(this.project.getValue(Project.MIN_STUDENTS)));
		this.textMaxStudents.setText(Integer.toString(this.project.getValue(Project.MAX_STUDENTS)));
		this.textCosts.setText(this.project.getCostString());
		
		if (this.project.isRoomSelected()) {
			this.textRoom.setText(this.project.getValue(Project.ROOM).toString());
		} else {
			this.textRoom.setText("Nicht festgelegt");
		}
		
		leaders.sort((student1, student2) -> {
			return student1.toString().compareTo(student2.toString());
		});
		
		guides.sort((guide1, guide2) -> {
			return guide1.toString().compareTo(guide2.toString());
		});
		
		for (Student student : leaders) {
			this.listLeaders.getItems().add(student.asCell(Student.class));
		}
		
		for (Guide guide : guides) {
			this.listGuides.getItems().add(guide.asCell());
		}
		
		// === Inserting === //
		// === Listening === //
		
		this.buttonClose.setOnAction((event) -> {
			if (this.onClose != null) {
				this.onClose.run();
			}
		});
		
		// === Listening === //
		
	}
	
	public void setOnClose(Runnable onClose) {
		this.onClose = onClose;
	}
	
}
