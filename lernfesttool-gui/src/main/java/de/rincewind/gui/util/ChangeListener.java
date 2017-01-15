package de.rincewind.gui.util;

public interface ChangeListener<T> {
	
	public abstract void onChange(T oldValue, T newValue);
	
}
