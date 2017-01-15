package de.rincewind.api.abstracts;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DatasetFieldAccessor<T> {
	
	public static List<String> fieldNames(Collection<DatasetFieldAccessor<?>> accessors) {
		return accessors.stream().map((accessor) -> {
			return accessor.getFieldName();
		}).collect(Collectors.toList());
	}
	
	public static String[] fieldNames(DatasetFieldAccessor<?>... accessors) {
		String[] fieldNames = new String[accessors.length];
		
		for (int i = 0; i < accessors.length; i = i + 1) {
			fieldNames[i] = accessors[i].getFieldName();
		}
		
		return fieldNames;
	}
	
	
	private String fieldName;
	private Class<T> cls;
	
	public DatasetFieldAccessor(String fieldName, Class<T> cls) {
		this.fieldName = fieldName;
		this.cls = cls;
	}
	
	@Override
	public String toString() {
		return this.fieldName + "{type=" + this.cls.getSimpleName() + "}";
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	public Class<T> getFieldType() {
		return this.cls;
	}
	
}
