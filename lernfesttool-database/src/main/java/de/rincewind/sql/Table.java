package de.rincewind.sql;

public interface Table {
	
	public abstract void create();
	
	public abstract boolean isCreated();
	
	public abstract String getName();
	
	public abstract SQLDatabase getDatabase();
	
}
