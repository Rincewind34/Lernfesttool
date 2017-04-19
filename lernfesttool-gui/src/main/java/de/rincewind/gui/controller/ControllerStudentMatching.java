package de.rincewind.gui.controller;

import java.util.Map;

import de.rincewind.api.Project;
import de.rincewind.gui.controller.abstracts.Controller;
import de.rincewind.gui.main.GUIHandler;
import de.rincewind.gui.main.ProjectMapping;
import de.rincewind.gui.util.listeners.NumberListener;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ControllerStudentMatching implements Controller {
	
	@FXML
	private Label labelStudents;
	
	@FXML
	private Label labelStudentsEarly;
	
	@FXML
	private Label labelStudentsLate;
	
	@FXML
	private Label labelProjects;

	@FXML
	private Label labelStatsFirstEarly;
	
	@FXML
	private Label labelStatsFirstLate;
	
	@FXML
	private Label labelStatsFirstFull;
	
	@FXML
	private Label labelStatsSecondEarly;
	
	@FXML
	private Label labelStatsSecondLate;
	
	@FXML
	private Label labelStatsSecondFull;
	
	@FXML
	private Label labelStatsThirdEarly;
	
	@FXML
	private Label labelStatsThirdLate;
	
	@FXML
	private Label labelStatsThirdFull;
	
	@FXML
	private Label labelStatsWithout;
	
	@FXML
	private TextField textDamageFirst;
	
	@FXML
	private TextField textDamageSecond;
	
	@FXML
	private TextField textDamageThird;
	
	@FXML
	private TextField textDamageWithout;
	
	@FXML
	private TextField textTimeout;
	
	@FXML
	private Button btnStart;
	
	@FXML
	private Button btnSave;
	
	@FXML
	private Button btnDelete;
	
	@FXML
	private Button btnDeleteConsole;
	
	@FXML
	private CheckBox checkBoxConsole;
	
	@FXML
	private ListView<String> listConsole;
	
	private ProjectMapping mapping;
	
	public ControllerStudentMatching() {
		this.mapping = new ProjectMapping();
	}
	
	@Override
	public void init() {
		// === Building === //
		
		this.mapping.setOutput((message) -> {
			if (!this.checkBoxConsole.isSelected()) {
				this.listConsole.getItems().add(message);
				
				Platform.runLater(() -> {
					this.listConsole.scrollTo(this.listConsole.getItems().size() - 1);
				});
			}
		});
		
		this.mapping.fetchData();
		
		// === Building === //
		// === Inserting === //
		
		this.btnSave.setDisable(true);
		
		this.labelStudents.setText(Integer.toString(this.mapping.getChooses().size()));
		this.labelStudentsEarly.setText(Integer.toString(this.mapping.getLeadLate().size()));
		this.labelStudentsLate.setText(Integer.toString(this.mapping.getLeadEarly().size()));
		this.labelProjects.setText(Integer.toString(this.mapping.getProjects().size()));
		
		this.textDamageFirst.setText("1");
		this.textDamageSecond.setText("2");
		this.textDamageThird.setText("3");
		this.textDamageWithout.setText("10");
		this.textTimeout.setText("10");
		
		// === Inserting === //
		// === Listening === //
		
		this.textDamageFirst.textProperty().addListener(new NumberListener(this.textDamageFirst));
		this.textDamageSecond.textProperty().addListener(new NumberListener(this.textDamageSecond));
		this.textDamageThird.textProperty().addListener(new NumberListener(this.textDamageThird));
		this.textDamageWithout.textProperty().addListener(new NumberListener(this.textDamageWithout));
		
		this.btnStart.setOnAction((event) -> {
			this.btnStart.setDisable(true);
			
			this.mapping.setDamageFirst(Integer.valueOf(this.textDamageFirst.getText()));
			this.mapping.setDamageSecond(Integer.valueOf(this.textDamageSecond.getText()));
			this.mapping.setDamageThird(Integer.valueOf(this.textDamageThird.getText()));
			this.mapping.setDamageNoProject(Integer.valueOf(this.textDamageWithout.getText()));
			this.mapping.setTimeout(Integer.valueOf(this.textTimeout.getText()));
			
			GUIHandler.session().threadpool().execute(() -> {
				Map<Integer, Integer> stats = this.mapping.execute();
				this.btnStart.setDisable(false);
				this.btnSave.setDisable(false);
				
				Platform.runLater(() -> {
					this.labelStatsFirstEarly.setText(Integer.toString(stats.get(1)));
					this.labelStatsFirstLate.setText(Integer.toString(stats.get(2)));
					this.labelStatsFirstFull.setText(Integer.toString(stats.get(3)));
					this.labelStatsSecondEarly.setText(Integer.toString(stats.get(4)));
					this.labelStatsSecondLate.setText(Integer.toString(stats.get(5)));
					this.labelStatsSecondFull.setText(Integer.toString(stats.get(6)));
					this.labelStatsThirdEarly.setText(Integer.toString(stats.get(7)));
					this.labelStatsThirdLate.setText(Integer.toString(stats.get(8)));
					this.labelStatsThirdFull.setText(Integer.toString(stats.get(9)));
					this.labelStatsWithout.setText(Integer.toString(stats.get(11)));
				});
			});
		});
		
		this.btnSave.setOnAction((event) -> {
			this.btnSave.setDisable(true);
			this.btnDelete.setDisable(true);
			
			Project.getManager().addAttandences(this.mapping.getResult()).async((voidObject) -> {
				this.btnSave.setDisable(false);
				this.btnDelete.setDisable(false);
			}, (exception) -> {
				// TODO
			});
		});
		
		this.btnDelete.setOnAction((event) -> {
			this.btnSave.setDisable(true);
			this.btnDelete.setDisable(true);
			
			Project.getManager().clearAttandences().async((voidObject) -> {
				this.btnSave.setDisable(false);
				this.btnDelete.setDisable(false);
			}, (exception) -> {
				// TODO
			});
		});
		
		this.btnDeleteConsole.setOnAction((event) -> {
			this.listConsole.getItems().clear();
		});
		
		// === Listening === //
	}

}
