package de.rincewind.gui.panes.abstarcts;

import javafx.scene.layout.Pane;

public interface AdminTab {
	
	public abstract void print();
	
	public abstract boolean save();
	
	public abstract boolean hasValueChanged();
	
	public abstract boolean isSaveable();
	
	public abstract boolean isPrintable();
	
	public abstract String getName();
	
	public abstract Pane getContentPane();
	
}
