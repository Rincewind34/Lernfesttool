package de.rincewind.gui.controller.editors;

import java.util.Collections;
import java.util.Optional;

import de.rincewind.api.Guide;
import de.rincewind.api.Project;
import de.rincewind.api.Room;
import de.rincewind.api.Student;
import de.rincewind.api.abstracts.Dataset;
import de.rincewind.api.util.ProjectCategory;
import de.rincewind.api.util.ProjectType;
import de.rincewind.gui.controller.WrapperProjectEditor;
import de.rincewind.gui.controller.abstracts.ControllerEditor;
import de.rincewind.gui.util.Cell;
import de.rincewind.gui.util.TabHandler;
import de.rincewind.gui.util.listeners.ActionListener;
import de.rincewind.gui.util.listeners.DoubleClickListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ControllerProjectEditor extends ControllerEditor {

	@FXML
	private TextField textName;

	@FXML
	private TextField textMinStudents;

	@FXML
	private TextField textMaxStudents;

	@FXML
	private TextField textCosts;

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
	private Label labelRoom;

	@FXML
	private ListView<Cell<Student>> listStudents;

	@FXML
	private ListView<Cell<Student>> listLeaders;

	@FXML
	private ListView<Cell<Guide>> listGuides;

	@FXML
	private ComboBox<Cell<ProjectCategory>> boxCategory;

	@FXML
	private ComboBox<Cell<ProjectType>> boxType;

	@FXML
	private ComboBox<Cell<Integer>> boxMinLevel;

	@FXML
	private ComboBox<Cell<Integer>> boxMaxLevel;

	@FXML
	private ComboBox<Cell<Integer>> boxCurrency;

	private Project project;

	private Room room;

	private WrapperProjectEditor wrapper;

	public ControllerProjectEditor(TabHandler handler, int projectId) {
		super(handler);

		this.project = Project.getManager().newEmptyObject(projectId);
	}

	@Override
	public void init() {
		this.wrapper = new WrapperProjectEditor(this.project, this.textName, this.textMinStudents, this.textMaxStudents, this.textCosts, this.btnLeaderAdd,
				this.btnLeaderRemove, this.btnTeacherAdd, this.btnHelperAdd, this.btnGuideRemove, this.checkEBoard, this.checkSports, this.checkMusics,
				this.checkHardware, this.textDescription, this.textNotes, this.listLeaders, this.listGuides, this.boxCategory, this.boxMinLevel,
				this.boxMaxLevel, this.boxCurrency) {
			
			@Override
			protected void valueChanged() {
				ControllerProjectEditor.this.getSaveHandler().valueChanged();
			}
			
		};
		
		this.wrapper.fetchData();
		
		if (this.project.isRoomSelected()) {
			this.project.fetchRoom().sync();
			this.room = this.project.getValue(Project.ROOM);
		}

		// === Building === //

		this.wrapper.buildNodes();

		ProjectType.iterateById((type) -> {
			this.boxType.getItems().add(new Cell<>(type.getName(), type));
		});
		
		// === Building === //
		// === Inserting === //
		
		this.wrapper.insertValues();
		this.boxType.getSelectionModel().select(this.project.getValue(Project.TYPE).getId());
		
		this.updateRoomDisplay();

		for (Student student : this.project.fetchAttendences().sync()) {
			this.listStudents.getItems().add(student.asCell());
		}

		Collections.sort(this.listStudents.getItems());

		// === Inserting === //
		// === Listening === //

		this.btnRoomSelect.setOnAction((event) -> {
			Optional<Dataset> result = Room.getManager().dialogSelector().showAndWait();

			if (result.isPresent()) {
				this.room = (Room) result.get();
				this.updateRoomDisplay();
				this.getSaveHandler().valueChanged();
			}
		});

		this.btnRoomOpen.setOnAction(new ActionListener(this, () -> {
			return this.room;
		}));

		this.listStudents.setOnMouseClicked(new DoubleClickListener(this, this.listStudents));
		this.listLeaders.setOnMouseClicked(new DoubleClickListener(this, this.listLeaders));
		this.listGuides.setOnMouseClicked(new DoubleClickListener(this, this.listGuides));

		this.getSaveHandler().addValue(this.textName.textProperty());
		this.getSaveHandler().addValue(this.textDescription.textProperty());
		this.getSaveHandler().addValue(this.textNotes.textProperty());
		this.getSaveHandler().addValue(this.boxCategory.getSelectionModel().selectedItemProperty());
		this.getSaveHandler().addValue(this.boxType.getSelectionModel().selectedItemProperty());
		this.getSaveHandler().addValue(this.boxMinLevel.getSelectionModel().selectedItemProperty());
		this.getSaveHandler().addValue(this.boxMaxLevel.getSelectionModel().selectedItemProperty());
		this.getSaveHandler().addValue(this.textMinStudents.textProperty());
		this.getSaveHandler().addValue(this.textMaxStudents.textProperty());
		this.getSaveHandler().addValue(this.boxCurrency.getSelectionModel().selectedItemProperty());
		this.getSaveHandler().addValue(this.textCosts.textProperty());
		this.getSaveHandler().addValue(this.checkEBoard.selectedProperty());
		this.getSaveHandler().addValue(this.checkSports.selectedProperty());
		this.getSaveHandler().addValue(this.checkMusics.selectedProperty());
		this.getSaveHandler().addValue(this.checkHardware.selectedProperty());

		// === Listening === //

		this.getSaveHandler().reset();
	}

	@Override
	public void saveStages() {
		this.wrapper.save(this.boxType.getSelectionModel().getSelectedItem().getSavedObject());
	}

	@Override
	public Project getEditingObject() {
		return this.project;
	}

	private void updateRoomDisplay() {
		this.btnRoomOpen.setDisable(this.room == null);

		if (this.room == null) {
			this.labelRoom.setText("Keinen");
		} else {
			this.labelRoom.setText(this.room.toString());
		}
	}

}
