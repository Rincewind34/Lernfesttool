package de.rincewind.gui.util;

import de.rincewind.api.abstracts.Dataset;
import de.rincewind.api.manager.DatasetManager;
import de.rincewind.gui.panes.abstarcts.AdminTab;

public interface TabHandler {
	
	public abstract void addTab(DatasetManager manager, int datasetId);
	
	public abstract void addTab(DatasetManager manager, int datasetId, boolean async);
	
	public abstract void addTab(AdminTab adminTab);
	
	public default void addTab(Dataset dataset) {
		this.addTab(dataset.getMatchingManager(), dataset.getId());
	}
	
}
