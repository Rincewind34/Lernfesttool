package de.rincewind.gui.util.filler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Filler<T, U> {
	
	private T object;
	
	private List<Checker<U>> checkerList;
	private List<U> elements;
	
	public Filler(T object, List<U> elements) {
		this.object = object;
		this.elements = elements;
		this.checkerList = new ArrayList<>();
	}
	
	public void refresh() {
		this.clearElements();
		
		iterateElements: for (U element : this.elements) {
			for (Checker<U> checker : this.checkerList) {
				if (!checker.check(element)) {
					continue iterateElements;
				}
			}
			
			this.addElement(element);
		}
	}
	
	public void addChecker(Checker<U> checker) {
		checker.setOnChange(() -> {
			this.refresh();
		});
		
		this.checkerList.add(checker);
	}
	
	public T getObject() {
		return this.object;
	}
	
	public List<U> getElements() {
		return Collections.unmodifiableList(this.elements);
	}
	
	public List<Checker<U>> getCheckerList() {
		return Collections.unmodifiableList(this.checkerList);
	}
	
	protected abstract void clearElements();
	
	protected abstract void addElement(U element);
	
}
