package de.rincewind.gui.controller;

import java.util.List;
import java.util.stream.Collectors;

import de.rincewind.api.SchoolClass;
import de.rincewind.api.Student;
import de.rincewind.gui.controller.abstracts.Controller;
import de.rincewind.gui.util.Cell;
import de.rincewind.gui.util.filler.ListFiller;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class ControllerStatsStudents implements Controller {
	
	@FXML
	private GridPane gridStats;
	
	@FXML
	private TextField textSearch;
	
	@FXML
	private ComboBox<Cell<Integer>> boxClassLevels;
	
	@FXML
	private ComboBox<Cell<Integer>> boxCategories;
	
	@FXML
	private ListView<Cell<Student>> listStudents;
	
	private ListFiller<Cell<Student>> fillerStudents;
	
	@Override
	public void init() {
		// === Building === //
		
		this.boxClassLevels.getItems().add(new Cell<>(null, -1));
		
		for (int i = 5; i <= 12; i++) {
			this.boxClassLevels.getItems().add(new Cell<>(SchoolClass.convertLevel(i) + ". Jahrgang", i));
		}
		
		this.boxCategories.getItems().add(new Cell<>("Schüler ohne Wahl", 0));
		this.boxCategories.getItems().add(new Cell<>("Schüler ohne Projekt", 1));
		
		List<Student> students = Student.getManager().getAllDatasets().sync();
		
		for (Student student : students) {
			if (student.isSchoolClassSelected()) {
				
			}
		}
		
		this.fillerStudents = new ListFiller<>(this.listStudents, students.stream().map((student) -> {
			return student.asCell(Student.class);
		}).collect(Collectors.toList()));
		
		// === Building === //
		// === Inserting === //
		
		this.fillerStudents.refresh();

//		this.setFinalValues();
//		this.selectAllClasses();
//		this.calculateLabels();
//
//		this.fillerProjects.refresh();
//
//		// === Inserting === //
//		// === Listening === //
//
//		this.btnReload.setOnAction((event) -> {
//			this.btnReload.setDisable(true);
//
//			this.fetchData().async((object) -> {
//				Platform.runLater(() -> {
//					this.setupFiller();
//					this.fillerProjects.refresh();
//					this.setFinalValues();
//					this.calculateLabels();
//					this.btnReload.setDisable(false);
//				});
//			}, (exception) -> {
//				this.btnReload.setDisable(false);
//
//				// TODO
//			});
//		});
//
//		this.btnSelectAllClasses.setOnAction((event) -> {
//			this.selectAllClasses();
//		});
//
//		this.boxMinClassLevel.getSelectionModel().selectedItemProperty().addListener((observeable, oldValue, newValue) -> {
//			if (newValue != null) {
//				this.calculateLabels();
//				this.fillerProjects.refresh();
//			}
//		});
//
//		this.boxMaxClassLevel.getSelectionModel().selectedItemProperty().addListener((observeable, oldValue, newValue) -> {
//			if (newValue != null) {
//				this.calculateLabels();
//				this.fillerProjects.refresh();
//			}
//		});
//
//		this.listProjects.setOnMouseClicked(new DoubleClickListener(this.handler, this.listProjects));
//
//		// === Listening === //
//
	}

}
