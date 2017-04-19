package de.rincewind.gui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import de.rincewind.api.Guide;
import de.rincewind.api.Helper;
import de.rincewind.api.Project;
import de.rincewind.api.SchoolClass;
import de.rincewind.api.Student;
import de.rincewind.api.Teacher;
import de.rincewind.api.abstracts.Dataset;
import de.rincewind.api.util.GuideType;
import de.rincewind.api.util.ProjectCategory;
import de.rincewind.api.util.ProjectType;
import de.rincewind.gui.util.Cell;
import de.rincewind.gui.util.listeners.NumberListener;
import de.rincewind.sql.tables.relations.TableProjectHelping;
import de.rincewind.sql.tables.relations.TableProjectAttandences;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class WrapperProjectEditor {

	private TextField textName;

	private TextField textMinStudents;

	private TextField textMaxStudents;

	private TextField textCosts;

	private Button btnLeaderAdd;

	private Button btnLeaderRemove;

	private Button btnTeacherAdd;

	private Button btnHelperAdd;

	private Button btnGuideRemove;

	private CheckBox checkEBoard;

	private CheckBox checkSports;

	private CheckBox checkMusics;

	private CheckBox checkHardware;

	private TextArea textDescription;

	private TextArea textNotes;

	private ListView<Cell<Student>> listLeaders; // TODO use list filler

	private ListView<Cell<Guide>> listGuides; // TODO use list filler

	private ComboBox<Cell<ProjectCategory>> boxCategory;

	private ComboBox<Cell<Integer>> boxMinLevel;

	private ComboBox<Cell<Integer>> boxMaxLevel;

	private ComboBox<Cell<Integer>> boxCurrency;

	private Project project;

	private List<Guide> guides;
	private List<Student> leaders;

	private List<Integer> addedTeachers;
	private List<Integer> addedHelpers;
	private List<Integer> addedLeaders;

	public WrapperProjectEditor(Project project, TextField textName, TextField textMinStudents, TextField textMaxStudents, TextField textCosts,
			Button btnLeaderAdd, Button btnLeaderRemove, Button btnTeacherAdd, Button btnHelperAdd, Button btnGuideRemove, CheckBox checkEBoard,
			CheckBox checkSports, CheckBox checkMusics, CheckBox checkHardware, TextArea textDescription, TextArea textNotes,
			ListView<Cell<Student>> listLeaders, ListView<Cell<Guide>> listGuides, ComboBox<Cell<ProjectCategory>> boxCategory,
			ComboBox<Cell<Integer>> boxMinLevel, ComboBox<Cell<Integer>> boxMaxLevel, ComboBox<Cell<Integer>> boxCurrency) {

		this.project = project;
		this.textName = textName;
		this.textMinStudents = textMinStudents;
		this.textMaxStudents = textMaxStudents;
		this.textCosts = textCosts;
		this.btnLeaderAdd = btnLeaderAdd;
		this.btnLeaderRemove = btnLeaderRemove;
		this.btnTeacherAdd = btnTeacherAdd;
		this.btnHelperAdd = btnHelperAdd;
		this.btnGuideRemove = btnGuideRemove;
		this.checkEBoard = checkEBoard;
		this.checkSports = checkSports;
		this.checkMusics = checkMusics;
		this.checkHardware = checkHardware;
		this.textDescription = textDescription;
		this.textNotes = textNotes;
		this.listLeaders = listLeaders;
		this.listGuides = listGuides;
		this.boxCategory = boxCategory;
		this.boxMinLevel = boxMinLevel;
		this.boxMaxLevel = boxMaxLevel;
		this.boxCurrency = boxCurrency;

		this.addedHelpers = new ArrayList<>();
		this.addedLeaders = new ArrayList<>();
		this.addedTeachers = new ArrayList<>();
	}

	public void fetchData() {
		this.project.fetchAll().sync();

		this.leaders = this.project.fetchLeaders().sync();
		this.guides = this.project.fetchGuides().sync();
		
		for (Student student : this.leaders) {
			this.addedLeaders.add(student.getId());
		}

		for (Guide guide : this.guides) {
			if (guide.getType() == GuideType.TEACHER) {
				this.addedTeachers.add(guide.getId());
			} else {
				this.addedHelpers.add(guide.getId());
			}
		}
	}

	public void buildNodes() {
		ProjectCategory.iterateById((category) -> {
			this.boxCategory.getItems().add(new Cell<>(category.getName(), category));
		});

		this.boxCurrency.getItems().add(new Cell<>("€ (Euro)", 100));
		this.boxCurrency.getItems().add(new Cell<>("ct (Cent)", 1));

		for (int i = 5; i <= 12; i++) {
			this.boxMinLevel.getItems().add(new Cell<>(SchoolClass.convertLevel(i), i));
			this.boxMaxLevel.getItems().add(new Cell<>(SchoolClass.convertLevel(i), i));
		}
	}

	public void insertValues() {
		this.textName.setText(this.project.getValue(Project.NAME));
		this.textDescription.setText(this.project.getValue(Project.DESCRIPTION));
		this.textNotes.setText(this.project.getValue(Project.NOTES));
		this.boxCategory.getSelectionModel().select(this.project.getValue(Project.CATEGORY).getId());
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
		
		for (Student student : this.leaders) {
			this.listLeaders.getItems().add(student.asCell());
		}

		for (Guide guide : this.guides) {
			this.listGuides.getItems().add(guide.asCell());
		}
		
		Collections.sort(this.listGuides.getItems());
		Collections.sort(this.listLeaders.getItems());

		this.updateLeaderDisplay(true);
		this.updateGuideDisplay(true);
	}

	public void addListeners() {
		this.btnLeaderAdd.setOnAction((event) -> {
			Optional<Dataset> result = Student.getManager().dialogSelector().showAndWait();

			if (result.isPresent()) {
				if (this.addedLeaders.contains(result.get().getId())) {
					return;
				}

				this.addedLeaders.add(result.get().getId());
				this.listLeaders.getItems().add(result.get().asCell());

				this.updateLeaderDisplay(true);
				this.valueChanged();
			}
		});

		this.btnLeaderRemove.setOnAction((event) -> {
			Cell<Student> current = this.listLeaders.getSelectionModel().getSelectedItem();

			this.addedLeaders.remove((Object) current.getSavedObject().getId());
			this.listLeaders.getItems().remove(current);

			this.updateLeaderDisplay(true);
			this.valueChanged();
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
				this.valueChanged();
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
				this.valueChanged();
			}
		});

		this.btnGuideRemove.setOnAction((event) -> {
			Guide current = this.listGuides.getSelectionModel().getSelectedItem().getSavedObject();

			if (current.getType() == GuideType.TEACHER) {
				this.addedTeachers.remove((Object) current.getId());
			} else {
				this.addedHelpers.remove((Object) current.getId());
			}
			
			this.listGuides.getItems().remove(this.listGuides.getSelectionModel().getSelectedItem());

			this.updateGuideDisplay(true);
			this.valueChanged();
		});

		this.listLeaders.getSelectionModel().selectedItemProperty().addListener((observeable, oldValue, newValue) -> {
			this.updateLeaderDisplay(false);
		});

		this.listGuides.getSelectionModel().selectedItemProperty().addListener((observeable, oldValue, newValue) -> {
			this.updateGuideDisplay(false);
		});

		this.boxMinLevel.getSelectionModel().selectedItemProperty().addListener((observeable, oldValue, newValue) -> {
			if (this.boxMinLevel.getSelectionModel().getSelectedIndex() > this.boxMaxLevel.getSelectionModel().getSelectedIndex()) {
				this.boxMaxLevel.getSelectionModel().select(this.boxMinLevel.getSelectionModel().getSelectedIndex());
			}
		});

		this.boxMaxLevel.getSelectionModel().selectedItemProperty().addListener((observeable, oldValue, newValue) -> {
			if (this.boxMaxLevel.getSelectionModel().getSelectedIndex() < this.boxMinLevel.getSelectionModel().getSelectedIndex()) {
				this.boxMinLevel.getSelectionModel().select(this.boxMaxLevel.getSelectionModel().getSelectedIndex());
			}
		});

		this.textCosts.textProperty().addListener(new NumberListener(this.textCosts));
		this.textMinStudents.textProperty().addListener(new NumberListener(this.textMinStudents));
		this.textMaxStudents.textProperty().addListener(new NumberListener(this.textMaxStudents));
	}

	public void save(ProjectType type) {
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

		// TODO check if student leads already

		TableProjectAttandences.instance().clearProject(this.project.getId(), true).async((object) -> {
			for (int leaderId : this.addedLeaders) {
				TableProjectAttandences.instance().add(this.project.getId(), leaderId, true).sync();
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
		this.project.setValue(Project.MAX_CLASS, this.boxMaxLevel.getSelectionModel().getSelectedItem().getSavedObject());
		this.project.setValue(Project.MIN_STUDENTS, minStudents);
		this.project.setValue(Project.MAX_STUDENTS, maxStudents);
		this.project.setValue(Project.COSTS, costs * this.boxCurrency.getSelectionModel().getSelectedItem().getSavedObject());
		this.project.setValue(Project.DESCRIPTION, this.textDescription.getText());
		this.project.setValue(Project.NOTES, this.textNotes.getText());
		this.project.setValue(Project.TYPE, type);
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

	protected void valueChanged() {

	}

	protected boolean disableLeaderRemove() {
		return this.listLeaders.getSelectionModel().getSelectedItem() == null;
	}

	protected Student getStudentToRemove() {
		return this.listLeaders.getSelectionModel().getSelectedItem().getSavedObject();
	}

	private void updateLeaderDisplay(boolean sort) {
		this.btnLeaderRemove.setDisable(this.disableLeaderRemove());

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
