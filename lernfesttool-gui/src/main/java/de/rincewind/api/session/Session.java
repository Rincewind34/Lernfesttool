package de.rincewind.api.session;

import de.rincewind.api.util.AccessLevel;

public interface Session {
	
	public abstract String getUsername();
	
	public abstract AccessLevel getAccessLevel();
	
	public abstract SessionType getType();
	
	public abstract void start();
	
}
