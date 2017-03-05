package de.rincewind.api.abstracts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.rincewind.gui.dialogs.DialogSelector;
import de.rincewind.gui.panes.abstarcts.PaneEditor;
import de.rincewind.gui.panes.selectors.PaneSelector;
import de.rincewind.gui.util.TabHandler;
import de.rincewind.sql.SQLRequest;
import de.rincewind.sql.abstracts.EntityTable;
import de.rincewind.sql.abstracts.EntityTable.FieldMap;

public abstract class DatasetManager {
	
	public abstract String getDataName();
	
	public abstract Dataset newEmptyObject(int datasetId);
	
	public abstract EntityTable getTable();
	
	public abstract PaneSelector<?, ?> createSelectorPane();
	
	public abstract PaneEditor<?> createEditorPane(TabHandler tabHandler, int datasetId);
	
	public abstract List<DatasetFieldAccessor<?>> fieldAccessors();
	
	public abstract <T extends Dataset> SQLRequest<List<T>> getAllDatasets();
	
	public String[] getTableColumns() {
		List<String> fieldNames = DatasetFieldAccessor.fieldNames(this.fieldAccessors());
		return fieldNames.toArray(new String[fieldNames.size()]);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Dataset> T newObject(int datasetId, FieldMap fieldMap) {
		T dataset = (T) this.newEmptyObject(datasetId);
		dataset.insertValues(fieldMap);
		return dataset;
	}
	
	public <T extends Dataset> T newObject(int datasetId, FieldMap fieldMap, Class<T> cls) {
		T dataset = cls.cast(this.newEmptyObject(datasetId));
		dataset.insertValues(fieldMap);
		return dataset;
	}
	
	public DialogSelector dialogSelector() {
		return new DialogSelector(this);
	}
	
	public DatasetFieldAccessor<?> getFieldAccessor(String name) {
		for (DatasetFieldAccessor<?> accessor : this.fieldAccessors()) {
			if (accessor.getFieldName().equals(name)) {
				return accessor;
			}
		}
		
		return null;
	}
	
	public SQLRequest<Void> deleteDataset(int datasetId) {
		return this.getTable().delete(datasetId);
	}
	
	public SQLRequest<Integer> createNewData() {
		return this.getTable().insertEmptyDataset();
	}
	
	public SQLRequest<List<Dataset>> initAllDatasets() {
		return () -> {
			return this.getTable().getEntries().sync().stream().map((schoolClassId) -> {
				return this.newEmptyObject(schoolClassId);
			}).collect(Collectors.toList());
		};
	}
	
	public <T extends Dataset> SQLRequest<List<T>> getAllDatasets(Class<T> cls) {
		return () -> {
			Map<Integer, FieldMap> sqlResult = this.getTable().getValues(this.getTableColumns()).sync();
			List<T> result = new ArrayList<>();
			
			for (int datasetId : sqlResult.keySet()) {
				T dataset = cls.cast(this.newEmptyObject(datasetId));
				dataset.insertValues(sqlResult.get(datasetId));
				result.add(dataset);
			}
			
			return result;
		};
	}
	
}
