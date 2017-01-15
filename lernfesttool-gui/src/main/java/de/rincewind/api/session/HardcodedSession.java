package de.rincewind.api.session;

import de.rincewind.api.util.AccessLevel;
import de.rincewind.gui.main.GUIHandler;
import de.rincewind.gui.windows.WindowAdmin;

public class HardcodedSession implements Session {
	
	private AccessLevel access;
	
	private String username;
	
	public HardcodedSession(String username, AccessLevel access) {
		this.access = access;
		this.username = username;
	}
	
	@Override
	public void start() {
		GUIHandler.session().changeWindow(new WindowAdmin());
	}
	
	@Override
	public AccessLevel getAccessLevel() {
		return this.access;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public SessionType getType() {
		return SessionType.HARDCODET;
	}

}
