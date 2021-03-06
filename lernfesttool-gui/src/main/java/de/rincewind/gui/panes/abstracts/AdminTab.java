package de.rincewind.gui.panes.abstracts;

import javafx.scene.layout.Pane;

public interface AdminTab {
	
	public abstract void print();
	
	public abstract boolean save();
	
	public abstract boolean delete();
	
	public abstract boolean hasValueChanged();
	
	public abstract boolean isSaveable();
	
	public abstract boolean isPrintable();
	
	public abstract boolean isDeleteable();
	
	public abstract String getName();
	
	public abstract Pane getContentPane();
	
}
