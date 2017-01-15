package de.rincewind.gui.controller.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import de.rincewind.api.Guide;
import de.rincewind.api.Helper;
import de.rincewind.api.Project;
import de.rincewind.api.Room;
import de.rincewind.api.SchoolClass;
import de.rincewind.api.Student;
import de.rincewind.api.Teacher;
import de.rincewind.api.abstracts.Dataset;
import de.rincewind.api.util.GuideType;
import de.rincewind.api.util.ProjectCategory;
import de.rincewind.api.util.ProjectType;
import de.rincewind.gui.controller.abstracts.ControllerEditor;
import de.rincewind.gui.util.Cell;
import de.rincewind.gui.util.TabHandler;
import de.rincewind.gui.util.listeners.ActionListener;
import de.rincewind.gui.util.listeners.DoubleClickListener;
import de.rincewind.gui.util.listeners.NumberListener;
import de.rincewind.sql.tables.relations.TableProjectHelping;
import de.rincewind.sql.tables.relations.TableProjectLeading;
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

	private List<Integer> addedTeachers;
	private List<Integer> addedHelpers;
	private List<Integer> addedLeaders;

	public ControllerProjectEditor(TabHandler handler, int projectId) {
		super(handler);

		this.project = Project.getManager().newEmptyObject(projectId);
		this.addedTeachers = new ArrayList<>();
		this.addedHelpers = new ArrayList<>();
		this.addedLeaders = new ArrayList<>();
	}

	@Override
	public void init() {
		this.project.fetchAll().sync();

		if (this.project.isRoomSelected()) {
			this.project.fetchRoom().sync();
			this.room = this.project.getValue(Project.ROOM);
		}

		List<Student> leaders = this.project.fetchLeaders().sync();
		List<Guide> guides = this.project.fetchGuides().sync();

		for (Student student : leaders) {
			this.addedLeaders.add(student.getId());
		}

		for (Guide guide : guides) {
			if (guide.getType() == GuideType.TEACHER) {
				this.addedTeachers.add(guide.getId());
			} else {
				this.addedHelpers.add(guide.getId());
			}
		}

		// === Building === //

		ProjectCategory.iterateById((category) -> {
			this.boxCategory.getItems().add(new Cell<>(category.getName(), category));
		});

		ProjectType.iterateById((type) -> {
			this.boxType.getItems().add(new Cell<>(type.getName(), type));
		});

		this.boxCurrency.getItems().add(new Cell<>("€ (Euro)", 100));
		this.boxCurrency.getItems().add(new Cell<>("ct (Cent)", 1));

		for (int i = 5; i <= 12; i++) {
			this.boxMinLevel.getItems().add(new Cell<>(SchoolClass.convertLevel(i), i));
			this.boxMaxLevel.getItems().add(new Cell<>(SchoolClass.convertLevel(i), i));
		}

		// === Building === //
		// === Inserting === //

		this.textName.setText(this.project.getValue(Project.NAME));
		this.textDescription.setText(this.project.getValue(Project.DESCRIPTION));
		this.textNotes.setText(this.project.getValue(Project.NOTES));
		this.boxCategory.getSelectionModel().select(this.project.getValue(Project.CATEGORY).getId());
		this.boxType.getSelectionModel().select(this.project.getValue(Project.TYPE).getId());
		this.boxMinLevel.getSelectionModel().select(this.project.getValue(Project.MIN_CLASS) - 5);
		this.boxMaxLevel.getSelectionModel().select(this.project.getValue(Project.MAX_CLASS) - 5);
		this.textMinStudents.setText(Integer.toString(this.project.getValue(Project.MIN_STUDENTS)));
		this.textMaxStudents.setText(Integer.toString(this.project.getValue(Project.MAX_STUDENTS)));

		int costs = this.project.getValue(Project.COSTS);

		if (costs != 0) {
			if (costs % 100 == 0) {
				costs = costs / 100;

				this.boxCurrency.getSelectionModel().select(0);
			} else {
				this.boxCurrency.getSelectionModel().select(1);
			}
		} else {
			this.boxCurrency.getSelectionModel().select(0);
		}

		this.textCosts.setText(Integer.toString(costs));

		this.checkEBoard.setSelected(this.project.getValue(Project.REQUEST_E_BOARD));
		this.checkSports.setSelected(this.project.getValue(Project.REQUEST_SPORTS));
		this.checkMusics.setSelected(this.project.getValue(Project.REQUEST_MUSICS));
		this.checkHardware.setSelected(this.project.getValue(Project.REQUEST_HARDWARE));

		this.updateRoomDisplay();

		for (Student student : this.project.fetchAttendences().sync()) {
			this.listStudents.getItems().add(student.asCell());
		}

		for (Student student : leaders) {
			this.listLeaders.getItems().add(student.asCell());
		}

		for (Guide guide : guides) {
			this.listGuides.getItems().add(guide.asCell());
		}

		this.updateLeaderDisplay(true);
		this.updateGuideDisplay(true);

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

		this.btnLeaderAdd.setOnAction((event) -> {
			Optional<Dataset> result = Student.getManager().dialogSelector().showAndWait();

			if (result.isPresent()) {
				if (this.addedLeaders.contains(result.get().getId())) {
					return;
				}

				this.addedLeaders.add(result.get().getId());
				this.listLeaders.getItems().add(result.get().asCell());

				this.updateLeaderDisplay(true);
				this.getSaveHandler().valueChanged();
			}
		});

		this.btnLeaderRemove.setOnAction((event) -> {
			Student current = this.listLeaders.getSelectionModel().getSelectedItem().getSavedObject();

			this.addedLeaders.remove(current.getId());
			this.listLeaders.getItems().remove(current);

			this.updateLeaderDisplay(true);
			this.getSaveHandler().valueChanged();
		});

		this.btnTeacherAdd.setOnAction((event) -> {
			Optional<Dataset> result = Teacher.getManager().dialogSelector().showAndWait();

			if (result.isPresent()) {
				if (this.addedTeachers.contains(result.get().getId())) {
					return;
				}

				this.addedTeachers.add(result.get().getId());
				this.listGuides.getItems().add(result.get().asCell());

				this.updateGuideDisplay(true);
				this.getSaveHandler().valueChanged();
			}
		});

		this.btnHelperAdd.setOnAction((event) -> {
			Optional<Dataset> result = Helper.getManager().dialogSelector().showAndWait();

			if (result.isPresent()) {
				if (this.addedHelpers.contains(result.get().getId())) {
					return;
				}

				this.addedHelpers.add(result.get().getId());
				this.listGuides.getItems().add(result.get().asCell());

				this.updateGuideDisplay(true);
				this.getSaveHandler().valueChanged();
			}
		});

		this.btnGuideRemove.setOnAction((event) -> {
			Guide current = this.listGuides.getSelectionModel().getSelectedItem().getSavedObject();

			if (current.getType() == GuideType.TEACHER) {
				this.addedTeachers.remove(current.getId());
			} else {
				this.addedHelpers.remove(current.getId());
			}

			this.listGuides.getItems().remove(current);

			this.updateGuideDisplay(true);
			this.getSaveHandler().valueChanged();
		});
		
		this.btnRoomOpen.setOnAction(new ActionListener(this, () -> {
			return this.room;
		}));
		
		this.listLeaders.getSelectionModel().selectedItemProperty().addListener((observeable, oldValue, newValue) -> {
			this.updateLeaderDisplay(false);
		});

		this.listGuides.getSelectionModel().selectedItemProperty().addListener((observeable, oldValue, newValue) -> {
			this.updateGuideDisplay(false);
		});
		
		this.boxMinLevel.getSelectionModel().selectedItemProperty().addListener((observeable, oldValue, newValue) -> { // TODO
																														// test
			if (this.boxMinLevel.getSelectionModel().getSelectedIndex() > this.boxMaxLevel.getSelectionModel().getSelectedIndex()) {
				this.boxMaxLevel.getSelectionModel().select(this.boxMinLevel.getSelectionModel().getSelectedIndex());
			}
		});

		this.boxMaxLevel.getSelectionModel().selectedItemProperty().addListener((observeable, oldValue, newValue) -> { // TODO
																														// test
			if (this.boxMaxLevel.getSelectionModel().getSelectedIndex() < this.boxMinLevel.getSelectionModel().getSelectedIndex()) {
				this.boxMinLevel.getSelectionModel().select(this.boxMaxLevel.getSelectionModel().getSelectedIndex());
			}
		});

		this.textCosts.textProperty().addListener(new NumberListener(this.textCosts));
		this.textMinStudents.textProperty().addListener(new NumberListener(this.textMinStudents));
		this.textMaxStudents.textProperty().addListener(new NumberListener(this.textMaxStudents));
		
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
		int minStudents;
		int maxStudents;
		int costs;

		try {
			minStudents = Byte.parseByte(this.textMinStudents.getText());
		} catch (NumberFormatException ex) {
			minStudents = 1;
		}

		try {
			maxStudents = Byte.parseByte(this.textMaxStudents.getText());
		} catch (NumberFormatException ex) {
			maxStudents = 1;
		}

		try {
			costs = Integer.parseInt(this.textCosts.getText());
		} catch (NumberFormatException ex) {
			costs = 0;
		}

		if (minStudents <= 0) {
			minStudents = 1;
		}

		if (maxStudents <= 0) {
			maxStudents = 1;
		}

		if (costs < 0) {
			costs = 0;
		}

		if (minStudents > maxStudents) {
			minStudents = maxStudents;
		}

		// if
		// (this.boxMinLevel.getSelectionModel().getSelectedItem().getSavedObject()
		// >
		// this.boxMaxLevel.getSelectionModel().getSelectedItem().getSavedObject())
		// {
		// this.boxMinLevel.getSelectionModel().select(0);
		// } TODO could be removed, if the listeners working (look up)

		// TODO check if student leads already
		
		TableProjectLeading.instance().clearProject(this.project.getId()).async((object) -> {
			for (int leaderId : this.addedLeaders) {
				TableProjectLeading.instance().add(this.project.getId(), leaderId).sync();
			}
		}, (exception) -> {
			// TODO
		});
		
		TableProjectHelping.instance().clearProject(this.project.getId()).async((object) -> {
			for (int teacherId : this.addedTeachers) {
				TableProjectHelping.instance().add(this.project.getId(), teacherId, GuideType.TEACHER.getId()).sync();
			}
			
			for (int helperId : this.addedHelpers) {
				TableProjectHelping.instance().add(this.project.getId(), helperId, GuideType.TEACHER.getId()).sync();
			}
		}, (exception) -> {
			// TODO
		});
		
		this.project.setValue(Project.NAME, this.textName.getText());
		this.project.setValue(Project.MIN_CLASS, this.boxMinLevel.getSelectionModel().getSelectedItem().getSavedObject());
		this.project.setValue(Project.MIN_CLASS, this.boxMaxLevel.getSelectionModel().getSelectedItem().getSavedObject());
		this.project.setValue(Project.MIN_STUDENTS, minStudents);
		this.project.setValue(Project.MAX_STUDENTS, maxStudents);
		this.project.setValue(Project.COSTS, costs * this.boxCurrency.getSelectionModel().getSelectedItem().getSavedObject());
		this.project.setValue(Project.DESCRIPTION, this.textDescription.getText());
		this.project.setValue(Project.NOTES, this.textNotes.getText());
		this.project.setValue(Project.TYPE, this.boxType.getSelectionModel().getSelectedItem().getSavedObject());
		this.project.setValue(Project.REQUEST_E_BOARD, this.checkEBoard.isSelected());
		this.project.setValue(Project.REQUEST_HARDWARE, this.checkHardware.isSelected());
		this.project.setValue(Project.REQUEST_MUSICS, this.checkMusics.isSelected());
		this.project.setValue(Project.REQUEST_SPORTS, this.checkSports.isSelected());
		this.project.setValue(Project.CATEGORY, this.boxCategory.getSelectionModel().getSelectedItem().getSavedObject());
		
		this.project.save().async((result) -> {

		}, (exception) -> {
			// TODO
		});
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

	private void updateLeaderDisplay(boolean sort) {
		this.btnLeaderRemove.setDisable(this.listLeaders.getSelectionModel().getSelectedItem() == null);

		if (sort) {
			Collections.sort(this.listLeaders.getItems());
		}
	}

	private void updateGuideDisplay(boolean sort) {
		this.btnGuideRemove.setDisable(this.listGuides.getSelectionModel().getSelectedItem() == null);

		if (sort) {
			Collections.sort(this.listGuides.getItems());
		}
	}

}
