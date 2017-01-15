package de.rincewind.api.manager;

import java.util.Arrays;
import java.util.List;

import de.rincewind.api.Helper;
import de.rincewind.api.abstracts.DatasetFieldAccessor;
import de.rincewind.gui.panes.abstarcts.FXMLPane;
import de.rincewind.gui.panes.abstarcts.PaneSelector;
import de.rincewind.gui.panes.editors.PaneHelperEditor;
import de.rincewind.gui.panes.selectors.PaneHelperSelector;
import de.rincewind.gui.util.TabHandler;
import de.rincewind.sql.tables.entities.TableHelpers;

public class HelperManager extends DatasetManager {
	
	private static HelperManager instance;
	
	static {
		HelperManager.instance = new HelperManager();
	}
	
	public static HelperManager instance() {
		return HelperManager.instance;
	}
	
	private HelperManager() {
		
	}
	
	@Override
	public String getDataName() {
		return "Helfer";
	}
	
	@Override
	public Helper newEmptyObject(int datasetId) {
		return new Helper(datasetId);
	}
	
	@Override
	public TableHelpers getTable() {
		return TableHelpers.instance();
	}
	
	@Override
	public PaneSelector<?> createSelectorPane() {
		return FXMLPane.setup(new PaneHelperSelector());
	}
	
	@Override
	public PaneHelperEditor createEditorPane(TabHandler handler, int helperId) {
		return FXMLPane.setup(new PaneHelperEditor(handler, helperId));
	}
	
	@Override
	public List<DatasetFieldAccessor<?>> fieldAccessors() {
		return Arrays.asList(Helper.NAME, Helper.STUDENT);
	}
	
}
