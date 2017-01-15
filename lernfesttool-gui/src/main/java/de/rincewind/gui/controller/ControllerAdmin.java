package de.rincewind.gui.controller;

import java.util.Optional;

import de.rincewind.api.Helper;
import de.rincewind.api.Project;
import de.rincewind.api.Room;
import de.rincewind.api.SchoolClass;
import de.rincewind.api.Student;
import de.rincewind.api.Teacher;
import de.rincewind.api.abstracts.Dataset;
import de.rincewind.api.manager.DatasetManager;
import de.rincewind.gui.controller.abstracts.Controller;
import de.rincewind.gui.dialogs.DialogConfirmClose;
import de.rincewind.gui.main.GUIHandler;
import de.rincewind.gui.panes.PaneStatsProject;
import de.rincewind.gui.panes.abstarcts.AdminTab;
import de.rincewind.gui.panes.abstarcts.FXMLPane;
import de.rincewind.gui.util.TabHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class ControllerAdmin implements Controller, TabHandler {
	
	@FXML
	private MenuItem menuCreateProject;
	
	@FXML
	private MenuItem menuCreateStudent;
	
	@FXML
	private MenuItem menuCreateRoom;
	
	@FXML
	private MenuItem menuCreateHelper;
	
	@FXML
	private MenuItem menuCreateTeacher;
	
	@FXML
	private MenuItem menuCreateClass;
	
	@FXML
	private MenuItem menuOpenProject;
	
	@FXML
	private MenuItem menuOpenClass;
	
	@FXML
	private MenuItem menuOpenTeacher;
	
	@FXML
	private MenuItem menuOpenStudent;
	
	@FXML
	private MenuItem menuOpenRoom;
	
	@FXML
	private MenuItem menuOpenHelper;
	
	@FXML
	private MenuItem menuStatsProject;
	
	@FXML
	private MenuItem menuPrint;
	
	@FXML
	private MenuItem menuSave;
	
	@FXML
	private MenuItem menuLogout;
	
	@FXML
	private MenuItem menuTerminate;
	
	@FXML
	private TabPane paneTabs;
	
	@Override
	public void addTab(DatasetManager manager, int datasetId) {
		this.addTab(manager, datasetId, true);
	}
	
	@Override
	public void addTab(DatasetManager manager, int datasetId, boolean async) {
		if (async) {
			GUIHandler.session().threadpool().execute(() -> {
				this.addTab(manager, datasetId, false);
			});
		} else {
			FXMLPane<?> pane = manager.createEditorPane(this, datasetId);
			
			if (!(pane instanceof AdminTab)) {
				throw new RuntimeException("Can not add the pane '" + pane.getClass().getName() + "' as admintab!");
			}
			
			Platform.runLater(() -> {
				this.addTab((AdminTab) pane);
			});
		}
	}
	
	@Override
	public void addTab(AdminTab adminTab) {
		Tab tab = new Tab(adminTab.getName());
		tab.setUserData(adminTab);
		tab.setContent(adminTab.getContentPane());
		tab.setClosable(true);
		tab.setOnCloseRequest((closeEvent) -> {
			if (adminTab.isSaveable() && adminTab.hasValueChanged()) {
				if (new DialogConfirmClose().showAndWait().get()) {
					boolean saved = adminTab.save();
					
					if (!saved) {
						Alert error = new Alert(AlertType.ERROR);
						error.setTitle("Ein Fehler ist aufgetreten");
						error.setHeaderText("Der Datensatz konnte aus unbekannten Gründen nicht gespeichert werden!");
						error.show();
						
						closeEvent.consume(); // TODO create FeatureTast with check if the database save was successful
					}
				}
			}
		});
		
		System.out.println("=== Add tab ===");
		this.paneTabs.getTabs().add(tab);
	}
	
	@Override
	public void init() {
		this.menuPrint.setDisable(true);
		this.menuSave.setDisable(true);
		
		this.menuCreateProject.setOnAction((event) -> {
			this.createDataset(Project.getManager());
		});
		
		this.menuCreateStudent.setOnAction((event) -> {
			this.createDataset(Student.getManager());
		});
		
		this.menuCreateClass.setOnAction((event) -> {
			this.createDataset(SchoolClass.getManager());
		});
		
		this.menuCreateRoom.setOnAction((event) -> {
			this.createDataset(Room.getManager());
		});
		
		this.menuCreateTeacher.setOnAction((event) -> {
			this.createDataset(Teacher.getManager());
		});
		
		this.menuCreateHelper.setOnAction((event) -> {
			this.createDataset(Helper.getManager());
		});
		
		this.menuOpenProject.setOnAction((event) -> {
			this.openDataset(Project.getManager());
		});
		
		this.menuOpenClass.setOnAction((event) -> {
			this.openDataset(SchoolClass.getManager());
		});
		
		this.menuOpenStudent.setOnAction((event) -> {
			this.openDataset(Student.getManager());
		});
		
		this.menuOpenRoom.setOnAction((event) -> {
			this.openDataset(Room.getManager());
		});
		
		this.menuOpenTeacher.setOnAction((event) -> {
			this.openDataset(Teacher.getManager());
		});
		
		this.menuOpenHelper.setOnAction((event) -> {
			this.openDataset(Helper.getManager());
		});
		
		this.menuStatsProject.setOnAction((event) -> {
			GUIHandler.session().threadpool().execute(() -> {
				AdminTab tab = FXMLPane.setup(new PaneStatsProject(this));
				
				Platform.runLater(() -> {
					this.addTab(tab);
				});
			});
		});
		
		this.menuSave.setOnAction((event) -> {
			this.getCurrentTab().save();
		});
		
		this.menuPrint.setOnAction((event) -> {
			this.getCurrentTab().print();
		});
		
		this.menuLogout.setOnAction((event) -> {
			GUIHandler.session().logout();
		});
		
		this.menuTerminate.setOnAction((event) -> {
			GUIHandler.session().terminate();
		});
		
		this.paneTabs.getSelectionModel().selectedItemProperty().addListener((observeable, oldVale, newValue) -> {
			if (newValue == null) {
				this.menuPrint.setDisable(true);
				this.menuSave.setDisable(true);
			} else {
				AdminTab adminTab = (AdminTab) newValue.getUserData();
				
				this.menuPrint.setDisable(!adminTab.isPrintable());
				this.menuSave.setDisable(!adminTab.isSaveable());
			}
		});
	}
	
	private void openDataset(DatasetManager manager) {
		Optional<Dataset> selectedId = manager.dialogSelector().showAndWait();
		
		if (selectedId.isPresent()) {
			this.addTab(selectedId.get());
		}
	}
	
	private void createDataset(DatasetManager manager) {
		manager.createNewData().async((datasetId) -> {
			this.addTab(manager, datasetId, false);
		}, (exception) -> {
			// TODO
		});
	}
	
	private AdminTab getCurrentTab() {
		return (AdminTab) this.paneTabs.getSelectionModel().getSelectedItem().getUserData();
	}
	
}

