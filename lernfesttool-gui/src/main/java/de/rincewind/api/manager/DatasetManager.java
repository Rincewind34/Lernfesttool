package de.rincewind.api.manager;

import java.util.List;
import java.util.stream.Collectors;

import de.rincewind.api.abstracts.Dataset;
import de.rincewind.api.abstracts.DatasetFieldAccessor;
import de.rincewind.gui.dialogs.DialogSelector;
import de.rincewind.gui.panes.abstarcts.PaneEditor;
import de.rincewind.gui.panes.abstarcts.PaneSelector;
import de.rincewind.gui.util.TabHandler;
import de.rincewind.sql.SQLRequest;
import de.rincewind.sql.abstracts.EntityTable;

public abstract class DatasetManager {
	
	public abstract String getDataName();
	
	public abstract Dataset newEmptyObject(int datasetId);
	
	public abstract EntityTable getTable();
	
	public abstract PaneSelector<?> createSelectorPane();
	
	public abstract PaneEditor<?> createEditorPane(TabHandler tabHandler, int datasetId);
	
	public abstract List<DatasetFieldAccessor<?>> fieldAccessors();
	
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
	
}
