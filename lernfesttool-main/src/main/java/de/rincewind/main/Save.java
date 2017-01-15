package de.rincewind.main;

public class Save<T> {
	
	private T value;
	
	public Save() {
		this(null);
	}
	
	public Save(T value) {
		this.value = value;
	}
	
	public T get() {
		return this.value;
	}
	
	public void set(T value) {
		this.value = value;
	}
	
	public boolean notNull() {
		return this.value != null;
	}
	
}
