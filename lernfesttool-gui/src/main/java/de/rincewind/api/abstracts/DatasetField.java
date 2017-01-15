package de.rincewind.api.abstracts;

public class DatasetField<T> {
	
	private boolean isSet; // Allow 'value' to be null AND be set
	
	private DatasetFieldAccessor<T> accessor;
	
	protected T value;
	
	public DatasetField(DatasetFieldAccessor<T> accessor) {
		this.accessor = accessor;
		this.isSet = false;
		this.value = null;
	}
	
	@Override
	public String toString() {
		return this.accessor.getFieldName() + "{filled=" + this.isSet + "}";
	}
	
	public void clear() {
		this.isSet = false;
		this.value = null;
	}
	
	public void setValue(T value) {
		this.isSet = true;
		this.value = value;
	}
	
	public boolean isSet() {
		return this.isSet;
	}
	
	public DatasetFieldAccessor<T> getAccessor() {
		return this.accessor;
	}
	
	public T getValue() {
		if (!this.isSet) {
			throw new RuntimeException("This field is not set!");
		}
		
		return this.value;
	}
	
	protected void setAccessor(DatasetFieldAccessor<T> accessor) {
		this.accessor = accessor;
	}
	
}
