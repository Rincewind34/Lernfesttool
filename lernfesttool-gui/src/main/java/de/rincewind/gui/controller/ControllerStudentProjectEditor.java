package de.rincewind.gui.controller;

import de.rincewind.api.Guide;
import de.rincewind.api.Project;
import de.rincewind.api.Student;
import de.rincewind.api.util.ProjectCategory;
import de.rincewind.gui.controller.abstracts.Controller;
import de.rincewind.gui.main.GUIHandler;
import de.rincewind.gui.util.Cell;
import de.rincewind.gui.windows.WindowStudent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ControllerStudentProjectEditor implements Controller {

	@FXML
	private TextField textName;

	@FXML
	private TextField textMinStudents;

	@FXML
	private TextField textMaxStudents;

	@FXML
	private TextField textCosts;

	@FXML
	private TextField textType;

	@FXML
	private Button btnRoomOpen;

	@FXML
	private Button btnRoomSelect;

	@FXML
	private Button btnLeaderAdd;

	@FXML
	private Button btnLeaderRemove;

	@FXML
	private Button btnTeacherAdd;

	@FXML
	private Button btnHelperAdd;

	@FXML
	private Button btnGuideRemove;

	@FXML
	private Button btnSave;

	@FXML
	private Button btnClose;

	@FXML
	private CheckBox checkEBoard;

	@FXML
	private CheckBox checkSports;

	@FXML
	private CheckBox checkMusics;

	@FXML
	private CheckBox checkHardware;

	@FXML
	private TextArea textDescription;

	@FXML
	private TextArea textNotes;

	@FXML
	private ListView<Cell<Student>> listLeaders;

	@FXML
	private ListView<Cell<Guide>> listGuides;

	@FXML
	private ComboBox<Cell<ProjectCategory>> boxCategory;

	@FXML
	private ComboBox<Cell<Integer>> boxMinLevel;

	@FXML
	private ComboBox<Cell<Integer>> boxMaxLevel;

	@FXML
	private ComboBox<Cell<Integer>> boxCurrency;

	private Project project;
	private Student owner;

	private WrapperProjectEditor wrapper;

	public ControllerStudentProjectEditor(Project project, Student owner) {
		this.project = project;
		this.owner = owner;
	}

	@Override
	public void init() {
		this.wrapper = new WrapperProjectEditor(this.project, this.textName, this.textMinStudents, this.textMaxStudents, this.textCosts, this.btnLeaderAdd,
				this.btnLeaderRemove, this.btnTeacherAdd, this.btnHelperAdd, this.btnGuideRemove, this.checkEBoard, this.checkSports, this.checkMusics,
				this.checkHardware, this.textDescription, this.textNotes, this.listLeaders, this.listGuides, this.boxCategory, this.boxMinLevel,
				this.boxMaxLevel, this.boxCurrency) {
			
			@Override
			protected boolean disableLeaderRemove() {
				return super.disableLeaderRemove() || this.getStudentToRemove().getId() == ControllerStudentProjectEditor.this.owner.getId();
			}
			
		};
		
		this.wrapper.fetchData();

		// === Building === //

		this.wrapper.buildNodes();

		// === Building === //
		// === Inserting === //

		this.wrapper.insertValues();

		this.textType.setText(this.project.getValue(Project.TYPE).getName());

		// === Inserting === //
		// === Listening === //

		this.wrapper.addListeners();
		
		this.btnSave.setOnAction((event) -> {
			this.wrapper.save(this.project.getValue(Project.TYPE));
			
			GUIHandler.session().changeWindow(new WindowStudent(this.owner));
		});
		
		this.btnClose.setOnAction((event) -> {
			GUIHandler.session().changeWindow(new WindowStudent(this.owner));
		});

		// === Listening === //
	}

}
