package de.rincewind.gui.util.filler;

public interface Matcher<T, U> {
	
	public abstract boolean test(T value1, U value2);
	
}
