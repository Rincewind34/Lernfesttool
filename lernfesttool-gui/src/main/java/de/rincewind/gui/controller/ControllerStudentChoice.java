package de.rincewind.gui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import de.rincewind.api.Project;
import de.rincewind.api.SchoolClass;
import de.rincewind.api.util.ProjectCategory;
import de.rincewind.api.util.ProjectType;
import de.rincewind.gui.controller.abstracts.Controller;
import de.rincewind.gui.util.Cell;
import de.rincewind.gui.util.filler.ListFiller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ControllerStudentChoice implements Controller {
	
	@FXML
	private Label labelFirstEarly;
	
	@FXML
	private Label labelFirstLate;
	
	@FXML
	private Label labelFirstFull;
	
	@FXML
	private Label labelSecondEarly;
	
	@FXML
	private Label labelSecondLate;
	
	@FXML
	private Label labelSecondFull;
	
	@FXML
	private Label labelThirdEarly;
	
	@FXML
	private Label labelThirdLate;
	
	@FXML
	private Label labelThirdFull;
	
	@FXML
	private Label labelState;
	
	@FXML
	private Button btnSelectFirst;
	
	@FXML
	private Button btnSelectSecond;
	
	@FXML
	private Button btnSelectThird;
	
	@FXML
	private Button btnDoChoice;
	
	@FXML
	private CheckBox checkEarly;
	
	@FXML
	private CheckBox checkLate;
	
	@FXML
	private CheckBox checkFull;
	
	@FXML
	private TextField textSearch;
	
	@FXML
	private ListView<Cell<Integer>> listProjects;
	
	private ListFiller<Cell<Integer>> fillerProjects;
	
	private List<ProjectType> leadingProjects;
	private Map<Integer, Object[]> projects;
	
	private SchoolClass schoolClass;
	private int studentId;
	
	private int firstEarly;
	private int firstLate;
	private int firstFull;
	private int secondEarly;
	private int secondLate;
	private int secondFull;
	private int thirdEarly;
	private int thirdLate;
	private int thirdFull;
	
	private Runnable onClose;
	private Consumer<Integer> onOpenProject;
	
	public ControllerStudentChoice() {
//		this.studentId = ((StudentSession) Main.getSession()).getStudentId();
	}
	
	@Override
	public void init() {
		this.leadingProjects = new ArrayList<ProjectType>();
		
		this.firstEarly = -1;
		this.firstLate = -1;
		this.firstFull = -1;
		this.secondEarly = -1;
		this.secondLate = -1;
		this.secondFull = -1;
		this.thirdEarly = -1;
		this.thirdLate = -1;
		this.thirdFull = -1;
		
		
		// === Building === //
		
		// int classId = Tables.students().getData(this.studentId,
		// StudentDatas.CLASS).sync();
		//
		// if (classId != -1) {
		// this.schoolClass = Tables.classes().getSchoolClass(classId).sync();
		// this.projects =
		// Tables.projects().getDataWithClass(this.schoolClass.getLevel(),
		// ProjectDatas.TYPE, ProjectDatas.NAME, ProjectDatas.CATEGORY).sync();
		// } else {
		// this.projects = Tables.projects().getData(ProjectDatas.TYPE,
		// ProjectDatas.NAME, ProjectDatas.CATEGORY).sync();
		// }
		//
		// List<Cell<Integer>> projectElements = new ArrayList<>();
		//
		// for (int projectId : this.projects.keySet()) {
		// projectElements.add(new Cell<>(Project.format((ProjectType)
		// this.projects.get(projectId)[0], projectId,
		// this.projects.get(projectId)[1].toString()), projectId));
		// }
		//
		// Collections.sort(projectElements);
		//
		// this.fillerProjects = new ListFiller<>(this.listProjects,
		// projectElements);
		// this.fillerProjects.addChecker(new
		// SearchCheck<Cell<Integer>>(this.textSearch));
		// this.fillerProjects.addChecker(new CheckBoxCheck<>(this.checkEarly,
		// (checked, value) -> {
		// return checked ? true : (this.projects.get(value.getSavedObject())[0]
		// != ProjectType.EARLY);
		// }));
		// this.fillerProjects.addChecker(new CheckBoxCheck<>(this.checkLate,
		// (checked, value) -> {
		// return checked ? true :(this.projects.get(value.getSavedObject())[0]
		// != ProjectType.LATE);
		// }));
		// this.fillerProjects.addChecker(new CheckBoxCheck<>(this.checkFull,
		// (checked, value) -> {
		// return checked ? true :(this.projects.get(value.getSavedObject())[0]
		// != ProjectType.FULL);
		// }));
		//
		// for (int projectId :
		// Tables.projectleading().getProjects(this.studentId).sync()) {
		// this.leadingProjects.add(Tables.projects().getData(projectId,
		// ProjectDatas.TYPE).sync());
		// }
		//
		// // === Building === //
		// // === Inserting === //
		//
		// this.fillerProjects.refresh();
		// this.calculateDoneButton();
		// this.calculateChoiceButtons();
		// this.fillLabels();
		//
		// if (this.leadingProjects.size() == 0) {
		// this.labelState.setText("Du leitest kein Projekt");
		// } else if (this.leadingProjects.size() == 1) {
		// this.labelState.setText("Du leitest ein " +
		// this.leadingProjects.get(0).getAdjective() + " Projekt");
		// } else if (this.leadingProjects.size() == 2) {
		// this.labelState.setText("Du leitest ein " +
		// this.leadingProjects.get(0).getAdjective() + " und " +
		// this.leadingProjects.get(0).getAdjective() + " Projekt");
		// } else {
		// this.labelState.setText("Es liegt ein FEHLER in der Datenbank vor,
		// bitte melde diesen Vorfall!");
		// }
		//
		// // === Inserting === //
		// // === Listening === //
		//
		// this.listProjects.getSelectionModel().selectedItemProperty().addListener((observeable,
		// oldValue, newValue) -> {
		// this.calculateChoiceButtons();
		// });
		//
		// this.listProjects.setOnMouseClicked((event) -> {
		// if (event.getButton() == MouseButton.PRIMARY) {
		// if (event.getClickCount() == 2) {
		// if (this.onOpenProject != null) {
		// this.onOpenProject.accept(this.listProjects.getSelectionModel().getSelectedItem().getSavedObject());
		// }
		// }
		// }
		// });
		//
		// this.btnDoChoice.setOnAction((event) -> {
		// Tables.projectchoosing().clearStudent(this.studentId).async((object)
		// -> {
		// if (this.firstEarly != -1) {
		// Tables.projectchoosing().add(this.studentId, this.firstEarly, 1);
		// }
		//
		// if (this.firstLate != -1) {
		// Tables.projectchoosing().add(this.studentId, this.firstLate, 1);
		// }
		//
		// if (this.firstFull != -1) {
		// Tables.projectchoosing().add(this.studentId, this.firstFull, 1);
		// }
		//
		// if (this.secondEarly != -1) {
		// Tables.projectchoosing().add(this.studentId, this.secondEarly, 2);
		// }
		//
		// if (this.secondLate != -1) {
		// Tables.projectchoosing().add(this.studentId, this.secondLate, 2);
		// }
		//
		// if (this.secondFull != -1) {
		// Tables.projectchoosing().add(this.studentId, this.secondFull, 2);
		// }
		//
		// if (this.thirdEarly != -1) {
		// Tables.projectchoosing().add(this.studentId, this.thirdEarly, 3);
		// }
		//
		// if (this.thirdLate != -1) {
		// Tables.projectchoosing().add(this.studentId, this.thirdLate, 3);
		// }
		//
		// if (this.thirdFull != -1) {
		// Tables.projectchoosing().add(this.studentId, this.thirdFull, 3);
		// }
		// });
		//
		// if (this.onClose != null) {
		// this.onClose.run();
		// }
		// });
		//
		// this.btnSelectFirst.setOnAction((event) -> {
		// int projectId =
		// this.listProjects.getSelectionModel().getSelectedItem().getSavedObject();
		// ProjectType selectedType = (ProjectType)
		// this.projects.get(projectId)[0];
		//
		// this.clearFromChoice(projectId);
		//
		// if (selectedType.isHalfTime()) {
		// this.firstFull = -1;
		//
		// if (selectedType == ProjectType.EARLY) {
		// this.firstEarly = projectId;
		// } else {
		// this.firstLate = projectId;
		// }
		// } else {
		// this.firstEarly = -1;
		// this.firstLate = -1;
		// this.firstFull = projectId;
		// }
		//
		// this.fillLabels();
		// this.calculateDoneButton();
		// });
		//
		// this.btnSelectSecond.setOnAction((event) -> {
		// int projectId =
		// this.listProjects.getSelectionModel().getSelectedItem().getSavedObject();
		// ProjectType selectedType = (ProjectType)
		// this.projects.get(projectId)[0];
		//
		// this.clearFromChoice(projectId);
		//
		// if (selectedType.isHalfTime()) {
		// this.secondFull = -1;
		//
		// if (selectedType == ProjectType.EARLY) {
		// this.secondEarly = projectId;
		// } else {
		// this.secondLate = projectId;
		// }
		// } else {
		// this.secondEarly = -1;
		// this.secondLate = -1;
		// this.secondFull = projectId;
		// }
		//
		// this.fillLabels();
		// this.calculateDoneButton();
		// });
		//
		// this.btnSelectThird.setOnAction((event) -> {
		// int projectId =
		// this.listProjects.getSelectionModel().getSelectedItem().getSavedObject();
		// ProjectType selectedType = (ProjectType)
		// this.projects.get(projectId)[0];
		//
		// this.clearFromChoice(projectId);
		//
		// if (selectedType.isHalfTime()) {
		// this.thirdFull = -1;
		//
		// if (selectedType == ProjectType.EARLY) {
		// this.thirdEarly = projectId;
		// } else {
		// this.thirdLate = projectId;
		// }
		// } else {
		// this.thirdEarly = -1;
		// this.thirdLate = -1;
		// this.thirdFull = projectId;
		// }
		//
		// this.fillLabels();
		// this.calculateDoneButton();
		// });
		
		// === Listening === //
		
	}
	
	public void setOnClose(Runnable onClose) {
		this.onClose = onClose;
	}
	
	public void setOnOpenProject(Consumer<Integer> onOpenProject) {
		this.onOpenProject = onOpenProject;
	}
	
	private void calculateDoneButton() {
		boolean cancle = true;
		
		if (this.leadingProjects.contains(ProjectType.FULL) || (this.leadingProjects.contains(ProjectType.EARLY) && this.leadingProjects.contains(ProjectType.LATE))) {
			this.btnDoChoice.setDisable(false);
			this.btnDoChoice.setText("Wahl überspringen");
			return;
		}
		
		if (this.firstFull != -1) {
			cancle = false;
		} else if (this.firstEarly != -1 && this.leadingProjects.contains(ProjectType.LATE)) {
			cancle = false;
		} else if (this.firstLate != -1 && this.leadingProjects.contains(ProjectType.EARLY)) {
			cancle = false;
		} else if (this.firstEarly != -1 && this.firstLate != -1) {
			cancle = false;
		}
		
		if (cancle) {
			this.btnDoChoice.setDisable(true);
			return;
		} else {
			cancle = true;
		}
		
		if (this.secondFull != -1) {
			cancle = false;
		} else if (this.secondEarly != -1 && this.leadingProjects.contains(ProjectType.LATE)) {
			cancle = false;
		} else if (this.secondLate != -1 && this.leadingProjects.contains(ProjectType.EARLY)) {
			cancle = false;
		} else if (this.secondEarly != -1 && this.secondLate != -1) {
			cancle = false;
		}
		
		if (cancle) {
			this.btnDoChoice.setDisable(true);
			return;
		} else {
			cancle = true;
		}
		
		if (this.thirdFull != -1) {
			cancle = false;
		} else if (this.thirdEarly != -1 && this.leadingProjects.contains(ProjectType.LATE)) {
			cancle = false;
		} else if (this.thirdLate != -1 && this.leadingProjects.contains(ProjectType.EARLY)) {
			cancle = false;
		} else if (this.thirdEarly != -1 && this.thirdLate != -1) {
			cancle = false;
		}
		
		this.btnDoChoice.setDisable(cancle);
	}
	
	private void calculateChoiceButtons() {
		boolean disable = false;
		
		if (this.listProjects.getSelectionModel().getSelectedItem() == null) {
			disable = true;
		} else if (this.leadingProjects.contains(ProjectType.FULL)) {
			disable = true;
		} else {
			int projectId = this.listProjects.getSelectionModel().getSelectedItem().getSavedObject();
			ProjectType type = (ProjectType) this.projects.get(projectId)[0];
			
			if ((type == ProjectType.EARLY || type == ProjectType.FULL) && this.leadingProjects.contains(ProjectType.EARLY)) {
				disable = true;
			} else if ((type == ProjectType.LATE || type == ProjectType.FULL) && this.leadingProjects.contains(ProjectType.LATE)) {
				disable = true;
			}
			
			ProjectCategory category = (ProjectCategory) this.projects.get(projectId)[2];

			if (category.isSingleChoice()) {
				if (type == ProjectType.EARLY && (
						(this.firstLate != -1 && this.projects.get(this.firstLate)[2] == category) ||
						(this.secondLate != -1 && this.projects.get(this.secondLate)[2] == category) ||
						(this.thirdLate != -1 && this.projects.get(this.thirdLate)[2] == category))) {

					disable = true;
				} else if (type == ProjectType.LATE && (
						(this.firstEarly != -1 && this.projects.get(this.firstEarly)[2] == category) ||
						(this.secondEarly != -1 && this.projects.get(this.secondEarly)[2] == category) ||
						(this.thirdEarly != -1 && this.projects.get(this.thirdEarly)[2] == category))) {

					disable = true;
				}
			}
		}
		
		this.btnSelectFirst.setDisable(disable);
		this.btnSelectSecond.setDisable(disable);
		this.btnSelectThird.setDisable(disable);
	}
	
	private void fillLabels() {
		this.labelFirstEarly.setText(this.getText(this.firstEarly));
		this.labelFirstLate.setText(this.getText(this.firstLate));
		this.labelFirstFull.setText(this.getText(this.firstFull));
		this.labelSecondEarly.setText(this.getText(this.secondEarly));
		this.labelSecondLate.setText(this.getText(this.secondLate));
		this.labelSecondFull.setText(this.getText(this.secondFull));
		this.labelThirdEarly.setText(this.getText(this.thirdEarly));
		this.labelThirdLate.setText(this.getText(this.thirdLate));
		this.labelThirdFull.setText(this.getText(this.thirdFull));
	}
	
	private void clearFromChoice(int projectId) {
		if (this.firstEarly == projectId) {
			this.firstEarly = -1;
		} else if (this.firstLate == projectId) {
			this.firstLate = -1;
		} else if (this.firstFull == projectId) {
			this.firstFull = -1;
		} else if (this.firstEarly == projectId) {
			this.secondEarly = -1;
		} else if (this.secondLate == projectId) {
			this.secondLate = -1;
		} else if (this.secondFull == projectId) {
			this.secondFull = -1;
		} else if (this.thirdEarly == projectId) {
			this.thirdEarly = -1;
		} else if (this.thirdLate == projectId) {
			this.thirdLate = -1;
		} else if (this.thirdFull == projectId) {
			this.thirdFull = -1;
		}
	}
	
	private String getText(int projectId) {
		if (projectId == -1) {
			return "KEINE";
		} else {
			return Project.format(projectId, this.projects.get(projectId)[1].toString());
		}
	}

}
