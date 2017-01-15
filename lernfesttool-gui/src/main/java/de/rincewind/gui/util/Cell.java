package de.rincewind.gui.util;

public class Cell<T> implements Comparable<Cell<T>> {
	
	private String display;
	
	private T savedObject;
	
	public Cell(String display, T savedObject) {
		this.display = display;
		this.savedObject = savedObject;
	}
	
	@Override
	public String toString() {
		return this.display;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Cell<?>) {
			Cell<?> cell = (Cell<?>) obj;
			
			return this.compareDisplay(cell) && this.compareSavedObject(cell);
		} else {
			return false;
		}
	}
	
	public String getDisplay() {
		return this.display;
	}
	
	public T getSavedObject() {
		return this.savedObject;
	}
	
	public boolean compareDisplay(Cell<?> cell) {
		return this.display.equals(cell.display);
	}
	
	public boolean compareSavedObject(Cell<?> cell) {
		if (cell.getSavedObject() == null && this.getSavedObject() == null) {
			return true;
		} else if (cell.getSavedObject() != null && this.getSavedObject() != null) {
			return this.savedObject.equals(cell.savedObject);
		} else {
			return false;
		}
	}
	
	@Override
	public int compareTo(Cell<T> cell) {
		return this.display.compareTo(cell.display);
	}
	
}
