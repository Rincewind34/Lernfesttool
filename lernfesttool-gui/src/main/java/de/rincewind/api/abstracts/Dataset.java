package de.rincewind.api.abstracts;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.rincewind.api.manager.DatasetManager;
import de.rincewind.api.util.SaveResult;
import de.rincewind.gui.util.Cell;
import de.rincewind.sql.SQLRequest;
import de.rincewind.sql.abstracts.EntityTable.FieldMap;

public abstract class Dataset {
	
	public static final Comparator<Dataset> COMPERATOR_ID = (dataset1, dataset2) -> {
		return Integer.compare(dataset1.datasetId, dataset2.datasetId);
	};
	
	protected static void initDataset(DatasetField<Dataset> field, int datasetId, DatasetManager manager) {
		if (datasetId <= 0) {
			field.setValue(null);
		} else {
			field.setValue(manager.newEmptyObject(datasetId));
		}
	}
	
	protected static boolean isDatasetSelected(DatasetFieldAccessor<?> accessor, Dataset root) {
		if (!root.isValuePreset(accessor)) {
			return false;
		}
		
		return root.getValue(accessor) != null;
	}
	
	protected static SQLRequest<Void> fetchDataset(DatasetFieldAccessor<? extends Dataset> accessor, Dataset root) {
		Dataset dataset = root.getValue(accessor);
		
		if (dataset == null) {
			throw new RuntimeException("The dataset is not selected!");
		}
		
		return dataset.fetchAll();
	}
	
	
	private int datasetId;
	
	private Map<DatasetFieldAccessor<?>, DatasetField<?>> fields;
	
	public Dataset(int datasetId) {
		if (datasetId <= 0) {
			throw new RuntimeException("The id is out of range!");
		}
		
		this.datasetId = datasetId;
		this.fields = new HashMap<>();
		
		for (DatasetFieldAccessor<?> accessor : this.getMatchingManager().fieldAccessors()) {
			this.fields.put(accessor, new DatasetField<>(accessor));
		}
	}
	
	public abstract void print();
	
	public abstract DatasetManager getMatchingManager();
	
	public abstract SQLRequest<SaveResult> save();
	
	public <T> void clearValue(DatasetFieldAccessor<?> accessor) {
		if (!this.containsField(accessor)) {
			throw new RuntimeException("Unknown field!");
		}
		
		this.fields.get(accessor).clear();
	}
	
	@SuppressWarnings("unchecked")
	public <T> void setValue(DatasetFieldAccessor<T> accessor, T value) {
		if (!this.containsField(accessor)) {
			throw new RuntimeException("Unknown field!");
		}
		
		DatasetField<T> field = (DatasetField<T>) this.fields.get(accessor);
		field.setValue(value);
	}
	
	public boolean containsField(DatasetFieldAccessor<?> accessor) {
		return this.fields.containsKey(accessor);
	}
	
	public boolean isValuePreset(DatasetFieldAccessor<?> accessor) {
		if (!this.containsField(accessor)) {
			throw new RuntimeException("Unknown field!");
		}
		
		return this.fields.get(accessor).isSet();
	}
	
	public int getId() {
		return this.datasetId;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Dataset> Cell<T> asCell() {
		return new Cell<>(this.toString(), (T) this);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Dataset> Cell<T> asCell(Class<T> datasetCls) {
		return new Cell<T>(this.toString(), (T) this);
	}
	
	public SQLRequest<Void> fetchValue(DatasetFieldAccessor<?> accessor) {
		if (!this.containsField(accessor)) {
			throw new RuntimeException("Unknown field!");
		}
		
		return () -> {
			DatasetField<?> field = this.fields.get(accessor);
			Object obj = this.getMatchingManager().getTable().getValue(this.datasetId, accessor.getFieldName(), Object.class).sync();
			this.setSQLValue(field, obj);
			
			return null;
		};
	}
	
	public SQLRequest<Void> fetchValues(DatasetFieldAccessor<?>... accessors) {
		String[] fieldNames = new String[accessors.length];
		
		for (int i = 0; i < accessors.length; i = i + 1) {
			DatasetFieldAccessor<?> accessor = accessors[i];
			
			if (!this.containsField(accessor)) {
				throw new RuntimeException("Unknown field '" + accessor.getFieldName() + "'!");
			} else {
				fieldNames[i] = accessor.getFieldName();
			}
		}
		
		return () -> {
			FieldMap map = this.getMatchingManager().getTable().getValues(this.datasetId, fieldNames).sync();
			
			for (Entry<String, Object> entry : map) {
				DatasetField<?> field = this.fields.get(entry.getKey());
				this.setSQLValue(field, entry.getValue());
			}
			
			return null;
		};
	}
	
	public SQLRequest<Void> fetchAll() {
		return () -> {
			List<String> fieldNames = DatasetFieldAccessor.fieldNames(this.fields.keySet());
			FieldMap map = this.getMatchingManager().getTable().getValues(this.datasetId, fieldNames.toArray(new String[fieldNames.size()])).sync();
			
			for (Entry<String, Object> entry : map) {
				DatasetField<?> field = this.fields.get(this.getMatchingManager().getFieldAccessor(entry.getKey()));
				this.setSQLValue(field, entry.getValue());
			}
			
			return null;
		};
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getValue(DatasetFieldAccessor<T> accessor) {
		if (!this.containsField(accessor)) {
			throw new RuntimeException("Unknown field!");
		}
		
		if (!this.isValuePreset(accessor)) {
			throw new RuntimeException("This field is empty!");
		}
		
		DatasetField<T> field = (DatasetField<T>) this.fields.get(accessor);
		return field.getValue();
	}
	
	@SuppressWarnings("unchecked")
	protected <T> void setSQLValue(DatasetField<?> field, T value) {
		((DatasetField<T>) field).setValue(value);
	}
	
}
