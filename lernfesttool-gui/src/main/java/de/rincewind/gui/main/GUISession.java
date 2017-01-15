package de.rincewind.gui.main;

import java.util.concurrent.ExecutorService;

import de.rincewind.api.session.Session;
import de.rincewind.gui.windows.Window;

public interface GUISession {
	
	public abstract void logout();
	
	public abstract void terminate();
	
	public abstract void changeWindow(Window<?> window);
	
	public abstract String login(String username, String password);
	
	public abstract Session getSession();
	
	public abstract ExecutorService threadpool();
	
}
