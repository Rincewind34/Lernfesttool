package de.rincewind.gui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.rincewind.api.SchoolClass;
import de.rincewind.api.util.ProjectType;
import de.rincewind.gui.controller.abstracts.Controller;
import de.rincewind.gui.util.Cell;
import de.rincewind.gui.util.TabHandler;
import de.rincewind.gui.util.filler.ListFiller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ControllerStatsProject implements Controller {
	
	@FXML
	private Label labelCountEarly;
	
	@FXML
	private Label labelCountLate;
	
	@FXML
	private Label labelCountFull;
	
	@FXML
	private Label labelCountTotal;
	
	@FXML
	private Label labelStudentsEarly;
	
	@FXML
	private Label labelStudentsLate;
	
	@FXML
	private Label labelStudentsFull;
	
	@FXML
	private Label labelStudentsTotal;
	
	@FXML
	private Label labelStudents;
	
	@FXML
	private Label labelClasses;
	
	@FXML
	private Label labelTotalCountEarly;
	
	@FXML
	private Label labelTotalCountLate;
	
	@FXML
	private Label labelTotalCountFull;
	
	@FXML
	private Label labelTotalCountTotal;
	
	@FXML
	private Label labelTotalStudentsEarly;
	
	@FXML
	private Label labelTotalStudentsLate;
	
	@FXML
	private Label labelTotalStudentsFull;
	
	@FXML
	private Label labelTotalStudentsTotal;
	
	@FXML
	private Label labelTotalStudents;
	
	@FXML
	private Label labelTotalClasses;
	
	@FXML
	private TextField textSearchProjects;
	
	@FXML
	private Button btnClose;
	
	@FXML
	private Button btnSelectAllClasses;
	
	@FXML
	private ComboBox<Cell<Byte>> boxMinClassLevel;
	
	@FXML
	private ComboBox<Cell<Byte>> boxMaxClassLevel;
	
	@FXML
	private ListView<Cell<SchoolClass>> listClasses;
	
	@FXML
	private ListView<Cell<Integer>> listProjects;
	
	
	private ListFiller<Cell<SchoolClass>> fillerClasses;
	private ListFiller<Cell<Integer>> fillerProjects;
	
	private Map<Integer, Object[]> projects;
	private Map<Integer, SchoolClass> classes;
	private Map<Integer, Integer> classSizes;
	
	
	private TabHandler handler;
	
	public ControllerStatsProject(TabHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public void init() {
//		this.classSizes = new HashMap<>();
//		
//		// === Building === //
//		
//		this.projects = Tables.projects().getData(ProjectDatas.TYPE, ProjectDatas.NAME, ProjectDatas.MIN_CLASS, ProjectDatas.MAX_CLASS, ProjectDatas.MAX_STUDENTS).sync();
//		this.classes = Tables.classes().getSchoolClassMap().sync();
//		
//		for (SchoolClass schoolClass : this.classes.values()) {
//			this.classSizes.put(schoolClass.getId(), Tables.classes().getClassSize(schoolClass.getId()).sync());
//		}
//		
//		for (byte i = 5; i <= 12; i++) {
//			this.boxMinClassLevel.getItems().add(new Cell<>(SchoolClass.convertLevel(i), i));
//			this.boxMaxClassLevel.getItems().add(new Cell<>(SchoolClass.convertLevel(i), i));
//		}
//		
//		List<Cell<SchoolClass>> classElements = new ArrayList<>();
//		List<Cell<Integer>> projectElements = new ArrayList<>();
//		
//		for (SchoolClass schoolClass : this.classes.values()) {
//			classElements.add(new Cell<SchoolClass>(schoolClass.getName(), schoolClass));
//		}
//		
//		for (int projectId : this.projects.keySet()) {
//			projectElements.add(new Cell<>(Project.format((ProjectType) this.projects.get(projectId)[0], projectId, this.projects.get(projectId)[1].toString()), projectId));
//		}
//		
//		Collections.sort(projectElements);
//		
//		this.fillerClasses = new ListFiller<>(this.listClasses, classElements);
//		this.fillerClasses.addChecker(new Checker<Cell<SchoolClass>>() {
//			
//			@Override
//			public boolean check(Cell<SchoolClass> value) {
//				return ControllerStatsProject.this.getTargetClasses().contains(value.getSavedObject());
//			}
//			
//		});
//		
//		this.fillerProjects = new ListFiller<>(this.listProjects, projectElements);
//		this.fillerProjects.addChecker(new SearchCheck<>(this.textSearchProjects));
//		this.fillerProjects.addChecker(new Checker<Cell<Integer>>() {
//			
//			@Override
//			public boolean check(Cell<Integer> value) {
//				return ControllerStatsProject.this.getTargetProjects().contains(value.getSavedObject());
//			}
//			
//		});
//		
//		// === Building === //
//		// === Inserting === //
//		
//		this.setFinalValues();
//		this.selectAllClasses();
//		this.calculateLabels();
//		
//		this.fillerClasses.refresh();
//		this.fillerProjects.refresh();
//		
//		// === Inserting === //
//		// === Listening === //
//		
//		this.btnSelectAllClasses.setOnAction((event) -> {
//			this.selectAllClasses();
//		});
//		
//		this.boxMinClassLevel.getSelectionModel().selectedItemProperty().addListener((observeable, oldValue, newValue) -> {
//			if (newValue != null) {
//				this.calculateLabels();
//				this.fillerClasses.refresh();
//				this.fillerProjects.refresh();
//			}
//		});
//		
//		this.boxMaxClassLevel.getSelectionModel().selectedItemProperty().addListener((observeable, oldValue, newValue) -> {
//			if (newValue != null) {
//				this.calculateLabels();
//				this.fillerClasses.refresh();
//				this.fillerProjects.refresh();
//			}
//		});
//		
//		this.listProjects.setOnMouseClicked(new DoubleClickListener(this.handler, Project.getManager(), this.listProjects));
//		this.listClasses.setOnMouseClicked(new DoubleClickListener(this.handler, SchoolClass.getManager(), () -> {
//			return this.listClasses.getSelectionModel().getSelectedItem().getSavedObject().getId();
//		}));
//		
		// === Listening === //
		
	}
	
	public Map<Integer, SchoolClass> getClasses() {
		return this.classes;
	}
	
	private void setFinalValues() {
		int countEarly = 0;
		int countLate = 0;
		int countFull = 0;
		
		int studentsEarly = 0;
		int studentsLate = 0;
		int studentsFull = 0;
		
		int students = 0;
		
		for (int projectId : this.projects.keySet()) {
			ProjectType type = (ProjectType) this.projects.get(projectId)[0];
			
			if (type == ProjectType.EARLY) {
				countEarly = countEarly + 1;
				studentsEarly = studentsEarly + (int) this.projects.get(projectId)[4];
			} else if (type == ProjectType.LATE) {
				countLate = countLate + 1;
				studentsLate = studentsLate + (int) this.projects.get(projectId)[4];
			} else if (type == ProjectType.FULL) {
				countFull = countFull + 1;
				studentsFull = studentsFull + (int) this.projects.get(projectId)[4];
			}
		}
		
		for (SchoolClass schoolClass : this.classes.values()) {
			students = students + this.classSizes.get(schoolClass.getId());
		}
		
		this.labelTotalCountEarly.setText(Integer.toString(countEarly));
		this.labelTotalCountLate.setText(Integer.toString(countLate));
		this.labelTotalCountFull.setText(Integer.toString(countFull));
		this.labelTotalCountTotal.setText(Integer.toString(countEarly + countLate + countFull));
		
		this.labelTotalStudentsEarly.setText(Integer.toString(studentsEarly));
		this.labelTotalStudentsLate.setText(Integer.toString(studentsLate));
		this.labelTotalStudentsFull.setText(Integer.toString(studentsFull));
		this.labelTotalStudentsTotal.setText(Integer.toString(studentsFull + Math.min(studentsEarly, studentsLate)));
		
		this.labelTotalStudents.setText(Integer.toString(students));
		this.labelTotalClasses.setText(Integer.toString(this.classes.size()));
	}
	
	private void calculateLabels() {
		int countEarly = 0;
		int countLate = 0;
		int countFull = 0;
		
		int studentsEarly = 0;
		int studentsLate = 0;
		int studentsFull = 0;
		
		int students = 0;
		
		for (int projectId : this.getTargetProjects()) {
			ProjectType type = (ProjectType) this.projects.get(projectId)[0];
			
			if (type == ProjectType.EARLY) {
				countEarly = countEarly + 1;
				studentsEarly = studentsEarly + (int) this.projects.get(projectId)[4];
			} else if (type == ProjectType.LATE) {
				countLate = countLate + 1;
				studentsLate = studentsLate + (int) this.projects.get(projectId)[4];
			} else if (type == ProjectType.FULL) {
				countFull = countFull + 1;
				studentsFull = studentsFull + (int) this.projects.get(projectId)[4];
			}
		}
		
		for (SchoolClass schoolClass : this.getTargetClasses()) {
			students = students + this.classSizes.get(schoolClass.getId());
		}
		
		this.labelCountEarly.setText(Integer.toString(countEarly));
		this.labelCountLate.setText(Integer.toString(countLate));
		this.labelCountFull.setText(Integer.toString(countFull));
		this.labelCountTotal.setText(Integer.toString(countEarly + countLate + countFull));
		
		this.labelStudentsEarly.setText(Integer.toString(studentsEarly));
		this.labelStudentsLate.setText(Integer.toString(studentsLate));
		this.labelStudentsFull.setText(Integer.toString(studentsFull));
		this.labelStudentsTotal.setText(Integer.toString(studentsFull + Math.min(studentsEarly, studentsLate)));
		
		this.labelStudents.setText(Integer.toString(students));
		this.labelClasses.setText(Integer.toString(this.getTargetClasses().size()));
	}
	
	private List<SchoolClass> getTargetClasses() {
		int minLevel = this.boxMinClassLevel.getSelectionModel().getSelectedItem().getSavedObject();
		int maxLevel = this.boxMaxClassLevel.getSelectionModel().getSelectedItem().getSavedObject();
		
		List<SchoolClass> result = new ArrayList<>();
		
//		for (SchoolClass schoolClass : this.classes.values()) {
//			if (minLevel <= schoolClass.getLevel() && schoolClass.getLevel() <= maxLevel) {
//				result.add(schoolClass);
//			}
//		}
		
		return result;
	}
	
	private List<Integer> getTargetProjects() {
		int minLevel = this.boxMinClassLevel.getSelectionModel().getSelectedItem().getSavedObject();
		int maxLevel = this.boxMaxClassLevel.getSelectionModel().getSelectedItem().getSavedObject();
		
		List<Integer> result = new ArrayList<>();
		
		for (Integer projectId : this.projects.keySet()) {
			int projectMinLevel = (int) this.projects.get(projectId)[2];
			int projectMaxLevel = (int) this.projects.get(projectId)[3];
			
			if (projectMinLevel <= minLevel && projectMaxLevel >= maxLevel) {
				result.add(projectId);
			}
		}
		
		return result;
	}
	
	private void selectAllClasses() {
		this.boxMinClassLevel.getSelectionModel().select(0);
		this.boxMaxClassLevel.getSelectionModel().select(this.boxMaxClassLevel.getItems().size() - 1);
	}
	
}
