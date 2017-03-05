package de.rincewind.main;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.rincewind.api.Student;
import de.rincewind.api.session.Accounts;
import de.rincewind.api.session.HardcodedSession;
import de.rincewind.api.session.Session;
import de.rincewind.api.session.StudentSession;
import de.rincewind.api.util.AccessLevel;
import de.rincewind.gui.main.GUIHandler;
import de.rincewind.gui.main.GUISession;
import de.rincewind.gui.windows.Window;
import de.rincewind.gui.windows.WindowLogin;
import de.rincewind.sql.Database;
import de.rincewind.sql.tables.entities.TableHelpers;
import de.rincewind.sql.tables.entities.TableProjects;
import de.rincewind.sql.tables.entities.TableRooms;
import de.rincewind.sql.tables.entities.TableSchoolClasses;
import de.rincewind.sql.tables.entities.TableStudents;
import de.rincewind.sql.tables.entities.TableTeachers;
import de.rincewind.sql.tables.relations.TableProjectAttandences;
import de.rincewind.sql.tables.relations.TableProjectChoosing;
import de.rincewind.sql.tables.relations.TableProjectHelping;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application implements GUISession {

	private static ExecutorService threadpool;
	
	private static Stage stage;
	
	private Window<?> currentWindow;

	private Session session;

	public static void main(String[] args) {
		if (args.length != 5) {
			System.out.println("Invalid argument length!");
			return;
		}
		
		Main.threadpool = Executors.newCachedThreadPool();
		
		Database.initialize(Main.threadpool, args[0], Integer.parseInt(args[1]), args[2], args[3], args[4]);
		Database.instance().getConnection().open();

		if (Database.instance().getConnection().isOpen()) {
			Database.instance().registerTable(TableHelpers.class);
			Database.instance().registerTable(TableProjects.class);
			Database.instance().registerTable(TableRooms.class);
			Database.instance().registerTable(TableSchoolClasses.class);
			Database.instance().registerTable(TableStudents.class);
			Database.instance().registerTable(TableTeachers.class);
			Database.instance().registerTable(TableProjectHelping.class);
			Database.instance().registerTable(TableProjectAttandences.class);
			Database.instance().registerTable(TableProjectChoosing.class);
			Database.instance().setup().sync();

			GUIHandler.initiliaze(new Main());
			Application.launch(args);
		}

		Main.threadpool.shutdown();

		if (Database.instance().getConnection().isOpen()) {
			Database.instance().getConnection().close();
		}
	}

	@Override
	public ExecutorService threadpool() {
		return Main.threadpool;
	}

	public Stage getStage() {
		return Main.stage;
	}

	@Override
	public void changeWindow(Window<?> newWindow) {
		if (this.currentWindow != null) {
			this.currentWindow.dispose(Main.stage);
		}

		System.out.println("=== Switch Window ===");
		System.out.println(Main.stage);

		if (newWindow != null) {
			this.currentWindow = newWindow;
			this.currentWindow.create();
			this.currentWindow.init(Main.stage);
		}
	}

	@Override
	public Session getSession() {
		return this.session;
	}

	@Override
	public String login(String username, String password) {
		Save<String> result = new Save<>();

		Accounts.iterate((account) -> {
			if (account.getUsername().equals(username)) {
				if (account.verify(password)) {
					this.session = new HardcodedSession(username, account.getAccessLevel());
				} else {
					result.set("Das Passwort ist falsch!");
				}
			}
		});

		if (result.notNull()) {
			return result.get();
		}

		if (this.session != null) {
			return null;
		}

		Student student;

		try {
			student = Student.getManager().newEmptyObject(Integer.parseInt(username.split("\\-")[1]));
		} catch (Exception exception) {
			return "Ungültiger Username!";
		}

		student.fetchAll().sync();

		if (student.getValue(Student.ACCESS_LEVEL) == AccessLevel.USER) {
			this.session = new StudentSession(student);
		} else {
			if (password.equals(student.getValue(Student.PASSWORD))) {
				this.session = new StudentSession(student);
			} else {
				return "Das Passwort ist falsch!";
			}
		}

		return null;
	}

	@Override
	public void logout() {
		this.session = null;
		this.changeWindow(new WindowLogin());
	}

	@Override
	public void terminate() {
		Platform.exit();
	}

	@Override
	public void start(Stage stage) throws Exception {
		Main.stage = stage;
		this.changeWindow(new WindowLogin());

		//
		// Rectangle2D primaryScreenBounds =
		// Screen.getPrimary().getVisualBounds();
		//
		// final FadePopup popup = new FadePopup();
		// popup.setHideOnEscape(true);
		//
		// String css = "-fx-background-color: #eee;\n"
		// + "-fx-border-color: #ccc;\n"
		// + "-fx-border-width: 2;\n"
		// + "-fx-border-style: solid;\n";
		//
		// HBox content = new HBox();
		// content.setStyle(css);
		// content.setMinWidth(300);
		// content.setMinWidth(30);
		// content.setPadding(new Insets(10.0, 10.0, 10.0, 10.0));
		// content.getChildren().add(new Label("Hey, das ist ein Test"));
		//
		// popup.getContent().addAll(content);
		//
		// popup.setX(primaryScreenBounds.getWidth() - 130);
		// popup.setY(30);
		//
		// Button show = new Button("Show");
		// show.setOnAction(new EventHandler<ActionEvent>() {
		//
		// @Override
		// public void handle(ActionEvent event) {
		// popup.show(stage);
		// stage.requestFocus();
		//
		// new AnimationTimer() {
		//
		// private double originalX = popup.getX();
		// private double x = this.originalX;
		//
		// @Override
		// public void handle(long now) {
		// if (!popup.isShowing() || this.x == this.originalX - 100) {
		// this.stop();
		// }
		//
		// this.x = this.x - 1;
		// popup.setX(this.x);
		// }
		//
		// }.start();
		// }
		//
		// });
		//
		// Button hide = new Button("Hide");
		// hide.disableProperty().bind(popup.hidingProperty());
		// hide.setOnAction(new EventHandler<ActionEvent>() {
		//
		// @Override
		// public void handle(ActionEvent event) {
		// popup.hide();
		// }
		//
		// });
		//
		// HBox layout = new HBox(10);
		//
		// layout.setStyle("-fx-background-color: cornsilk; -fx-padding: 10;");
		// layout.getChildren().addAll(show, hide);
		//
		// stage.setScene(new Scene(layout));
		// stage.show();
	}

	@Override
	public void stop() throws Exception {
		super.stop();

		this.changeWindow(null);
	}

	public class FadePopup extends Popup {

		private final BooleanProperty hiding;

		public FadePopup() {
			this.hiding = new SimpleBooleanProperty(this, "hiding", false);
			this.setEventHandler(MouseEvent.MOUSE_PRESSED, (event) -> {
				this.hide();
			});
		}

		public ReadOnlyBooleanProperty hidingProperty() {
			return this.hiding;
		}

		public boolean isHiding() {
			return this.hiding.get();
		}

		@Override
		public void hide() {
			if (!this.hiding.get()) {
				this.hiding.set(true);

				final ObservableList<Node> nodes = this.getContent();
				final Map<Node, Double> opacities = new HashMap<>();
				KeyValue[] keyValues = new KeyValue[nodes.size()];

				for (int i = 0; i < keyValues.length; i++) {
					Node node = nodes.get(i);
					opacities.put(node, node.getOpacity());
					keyValues[i] = new KeyValue(nodes.get(i).opacityProperty(), 0);
				}

				KeyFrame frame = new KeyFrame(Duration.seconds(1), keyValues);
				Timeline timeline = new Timeline(frame);

				timeline.setOnFinished(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						FadePopup.this.hiding.set(false);
						FadePopup.super.hide();
						for (Node node : nodes) {
							node.setOpacity(opacities.get(node));
						}
					}

				});

				timeline.play();
			}
		}
	}
}