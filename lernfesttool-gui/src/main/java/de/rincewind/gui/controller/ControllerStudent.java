package de.rincewind.gui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.rincewind.api.Project;
import de.rincewind.api.util.ProjectType;
import de.rincewind.gui.controller.abstracts.Controller;
import de.rincewind.gui.util.Cell;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;

public class ControllerStudent implements Controller {

	@FXML
	private ListView<Cell<Integer>> listProjects;

	@FXML
	private CheckBox checkFullProjects;

	@FXML
	private CheckBox checkEarlyProjects;

	@FXML
	private CheckBox checkLateProjects;

	@FXML
	private CheckBox checkJoinableProjects;

	@FXML
	private TextField textSearch;

	@FXML
	private Button btnManage;

	@FXML
	private Button btnLogout;

	@FXML
	private ComboBox<Cell<Integer>> boxProjects;

	private Map<Integer, Object[]> projectsFull;
	private Map<Integer, Object[]> projectsEarly;
	private Map<Integer, Object[]> projectsLate;

	private int classLevel;

	@Override
	public void init() {
		// int classId = Tables.students().getData(((StudentSession)
		// Main.getSession()).getStudentId(), StudentDatas.CLASS).sync();
		// this.classLevel = Tables.classes().getData(classId,
		// ClassDatas.CLASS_LEVEL).sync();

		// this.refreshProjects();
		this.refillListView();

		this.checkFullProjects.selectedProperty().addListener(new ChangeHandler(this.checkFullProjects));
		this.checkEarlyProjects.selectedProperty().addListener(new ChangeHandler(this.checkEarlyProjects));
		this.checkLateProjects.selectedProperty().addListener(new ChangeHandler(this.checkLateProjects));
		this.checkJoinableProjects.selectedProperty().addListener(new ChangeHandler(this.checkJoinableProjects));

		this.textSearch.textProperty().addListener((observable, oldValue, newValue) -> {
			this.refillListView();
		});

		Map<Integer, Object[]> projects = new HashMap<>();

		// for (int pID : Tables.projectleading().getProjects(((StudentSession)
		// Main.getSession()).getStudentId()).sync()) {
		// projects.put(pID, Tables.projects().getData(pID, ProjectDatas.NAME,
		// ProjectDatas.TYPE).sync());
		// }

		if ((projects.isEmpty() || !projects.values().iterator().next()[1].equals(ProjectType.FULL)) && projects.size() < 2) {
			this.boxProjects.getItems().add(new Cell<>("<Neues erstellen>", -1));
		}

		for (Entry<Integer, Object[]> entry : projects.entrySet()) {
			this.boxProjects.getItems().add(new Cell<>(entry.getValue()[0].toString(), entry.getKey()));
		}

		this.boxProjects.getSelectionModel().select(0);
		this.boxProjects.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue != null && oldValue.equals(newValue)) {
				return;
			}

			this.calculateButton();
		});

		this.listProjects.setOnMouseClicked((event) -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				if (event.getClickCount() == 2) {
					// GUIHandler.session().changeWindow(new
					// WindowProjectVisitor(this.listProjects.getSelectionModel().getSelectedItem().getSavedObject(),
					// () -> {
					// Main.changeWindow(new WindowStudent());
					// }));
				}
			}
		});

		this.calculateButton();
	}

	// @FXML
	// public void onManage() {
	// int id =
	// this.boxProjects.getSelectionModel().getSelectedItem().getSavedObject();
	//
	// if (id == -1) {
	// if (this.boxProjects.getItems().size() == 1) {
	// Main.changeWindow(new WindowProjectEditor(id, null, () -> {
	// Main.changeWindow(new WindowStudent());
	// }));
	// } else {
	// Main.changeWindow(new WindowProjectEditor(id,
	// Tables.projects().getData(this.boxProjects.getItems().get(1).getSavedObject(),
	// ProjectDatas.TYPE).sync() == ProjectType.EARLY
	// ? ProjectType.LATE : ProjectType.EARLY,
	// () -> {
	// Main.changeWindow(new WindowStudent());
	// }));
	// }
	// } else {
	// Main.changeWindow(new WindowProjectEditor(id,
	// Tables.projects().getData(id, ProjectDatas.TYPE).sync(), () -> {
	// Main.changeWindow(new WindowStudent());
	// }));
	// }
	// }
	//
	// @FXML
	// public void onLogout() {
	// Main.logout();
	// }
	//
	// private void refreshProjects() {
	// this.projectsFull = Tables.projects()
	// .getData(new Equalizer<>(ProjectDatas.TYPE, ProjectType.FULL),
	// ProjectDatas.NAME, ProjectDatas.MIN_CLASS,
	// ProjectDatas.MAX_CLASS).sync();
	// this.projectsEarly = Tables.projects()
	// .getData(new Equalizer<>(ProjectDatas.TYPE, ProjectType.EARLY),
	// ProjectDatas.NAME, ProjectDatas.MIN_CLASS,
	// ProjectDatas.MAX_CLASS).sync();
	// this.projectsLate = Tables.projects()
	// .getData(new Equalizer<>(ProjectDatas.TYPE, ProjectType.LATE),
	// ProjectDatas.NAME, ProjectDatas.MIN_CLASS,
	// ProjectDatas.MAX_CLASS).sync();
	// }

	private void refillListView() {
		this.listProjects.getItems().clear();

		List<Cell<Integer>> entryList = new ArrayList<>();

		if (this.checkFullProjects.isSelected()) {
			entryList.addAll(this.elementsFromMap(this.projectsFull, this.classLevel));
		}

		if (this.checkEarlyProjects.isSelected()) {
			entryList.addAll(this.elementsFromMap(this.projectsEarly, this.classLevel));
		}

		if (this.checkLateProjects.isSelected()) {
			entryList.addAll(this.elementsFromMap(this.projectsLate, this.classLevel));
		}

		Collections.sort(entryList);
		this.listProjects.getItems().addAll(entryList);
	}

	private List<Cell<Integer>> elementsFromMap(Map<Integer, Object[]> map, int classLevel) {
		List<Cell<Integer>> result = new ArrayList<>();

		for (int id : map.keySet()) {
			String name = (String) map.get(id)[0];
			int minCl = (int) map.get(id)[1];
			int maxCl = (int) map.get(id)[2];

			if (this.checkJoinableProjects.isSelected()) {
				if (maxCl < classLevel || classLevel < minCl) {
					continue;
				}
			}

			if (name.toLowerCase().contains(this.textSearch.getText().toLowerCase())) {
				result.add(new Cell<>(Project.format(id, name), id));
			}
		}

		return result;
	}

	private void calculateButton() {
		if (this.boxProjects.getSelectionModel().getSelectedItem().getSavedObject() == -1) {
			this.btnManage.setText("Neues Projekt erstellen");
		} else {
			this.btnManage.setText("Projekt anpassen");
		}
	}

	private class ChangeHandler implements ChangeListener<Boolean> {

		private CheckBox handler;

		public ChangeHandler(CheckBox handler) {
			this.handler = handler;
		}

		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			this.handler.setSelected(newValue);
			ControllerStudent.this.refillListView();
		}

	}

}
