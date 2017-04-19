package de.rincewind.main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.print.PrintServiceLookup;

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
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

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
		
		System.out.println(PrintServiceLookup.lookupPrintServices(null, null).length);
		
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
	}

	@Override
	public void stop() throws Exception {
		super.stop();

		this.changeWindow(null);
	}

}