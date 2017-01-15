package de.rincewind.gui.controller;

import java.util.Collections;

import de.rincewind.api.SchoolClass;
import de.rincewind.api.Student;
import de.rincewind.api.abstracts.Dataset;
import de.rincewind.api.util.AccessLevel;
import de.rincewind.gui.controller.abstracts.Controller;
import de.rincewind.gui.main.GUIHandler;
import de.rincewind.gui.util.Cell;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class ControllerLogin implements Controller {
	
	@FXML
	private ProgressBar progressStudents;
	
	@FXML
	private Label labelStatus;
	
	@FXML
	private TextField textUsername;
	
	@FXML
	private PasswordField textPassword;
	
	@FXML
	private Button btnLogin;

	@FXML
	private ListView<Cell<SchoolClass>> listClasses;

	@FXML
	private ListView<Cell<Student>> listStudents;

	@Override
	public void init() {
		for (Dataset schoolClass : SchoolClass.getManager().initAllDatasets().sync()) {
			schoolClass.fetchAll().sync();
			this.listClasses.getItems().add(schoolClass.asCell(SchoolClass.class));
		}
		
		this.listStudents.setVisible(true);

		this.listClasses.getSelectionModel().selectedItemProperty()
				.addListener((ObservableValue<? extends Cell<SchoolClass>> observable, Cell<SchoolClass> oldValue, Cell<SchoolClass> newValue) -> {
					this.listClasses.setDisable(true);
					this.listStudents.setDisable(true);

					Timeline task = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(ControllerLogin.this.progressStudents.progressProperty(), 0)),
							new KeyFrame(Duration.seconds(1), new KeyValue(ControllerLogin.this.progressStudents.progressProperty(), 1)));
					task.playFromStart();

					new Thread(() -> {
						try {
							Thread.sleep(1000 * 1);
						} catch (Exception e) {
							e.printStackTrace();
						}

						Platform.runLater(() -> {
							ControllerLogin.this.listClasses.setDisable(false);
							ControllerLogin.this.listStudents.setDisable(false);
						});
					}).start();

					ObservableList<Cell<Student>> items = this.listStudents.getItems();
					items.clear();
					
					for (Student student : newValue.getSavedObject().fetchStudents().sync()) {
						items.add(student.asCell());
					}
					
					Collections.sort(items);
					this.listStudents.setItems(items);
					
					this.textUsername.setText("");
					this.textPassword.setText("");
					this.textPassword.setDisable(false);
				});

		this.listStudents.getSelectionModel().selectedItemProperty()
				.addListener((ObservableValue<? extends Cell<Student>> observable, Cell<Student> oldValue, Cell<Student> newValue) -> {
					if (newValue != null) {
						this.initLogin(newValue.getSavedObject());
					}
				});
		
		this.btnLogin.setOnAction((event) -> {
			String inputUsername = this.textUsername.getText();
			String inputPassword = this.textPassword.getText();

			if (inputUsername == null || inputUsername.isEmpty()) {
				this.labelStatus.setText("Bitte Namen auswählen!");
				return;
			}

			String result = GUIHandler.session().login(inputUsername, inputPassword);

			if (result == null) {
				this.labelStatus.setText("Bitte warten...");
				GUIHandler.session().getSession().start();
			} else {
				this.labelStatus.setText(result);
			}
		});
	}

	@FXML
	public void onAction() {
		String inputUsername = this.textUsername.getText();
		String inputPassword = this.textPassword.getText();

		if (inputUsername == null || inputUsername.isEmpty()) {
			this.labelStatus.setText("Bitte Namen auswählen!");
			return;
		}

		this.labelStatus.setText("Bitte warten...");

		String result = GUIHandler.session().login(inputUsername, inputPassword);

		if (result == null) {
			GUIHandler.session().getSession().start();
		} else {
			this.labelStatus.setText(result);
		}
	}
	
	private void initLogin(Student student) {
		this.textUsername.setText("schüler-" + student.getId());
		this.textPassword.setText("");
		
		if (student.getValue(Student.ACCESS_LEVEL) == AccessLevel.USER) {
			this.textPassword.setDisable(true);
			
			Platform.runLater(() -> {
				this.btnLogin.requestFocus();
			});
		} else {
			this.requestFocus(this.textPassword, 100);
		}
	}

	private void requestFocus(Node node, int delayMillis) {
		new Thread(() -> {
			try {
				Thread.sleep(delayMillis);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				Platform.runLater(() -> {
					node.requestFocus();
				});
			}
		}).start();
	}
}