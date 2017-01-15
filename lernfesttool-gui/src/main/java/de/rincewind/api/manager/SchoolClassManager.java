package de.rincewind.api.manager;

import java.util.Arrays;
import java.util.List;

import de.rincewind.api.SchoolClass;
import de.rincewind.api.abstracts.DatasetFieldAccessor;
import de.rincewind.gui.panes.abstarcts.FXMLPane;
import de.rincewind.gui.panes.abstarcts.PaneSelector;
import de.rincewind.gui.panes.editors.PaneClassEditor;
import de.rincewind.gui.panes.selectors.PaneClassSelector;
import de.rincewind.gui.util.TabHandler;
import de.rincewind.sql.tables.entities.TableSchoolClasses;

public class SchoolClassManager extends DatasetManager {
	
	private static SchoolClassManager instance;
	
	static {
		SchoolClassManager.instance = new SchoolClassManager();
	}
	
	public static SchoolClassManager instance() {
		return SchoolClassManager.instance;
	}
	
	private SchoolClassManager() {
		
	}
	
	@Override
	public String getDataName() {
		return "Klasse";
	}
	
	@Override
	public SchoolClass newEmptyObject(int datasetId) {
		return new SchoolClass(datasetId);
	}
	
	@Override
	public TableSchoolClasses getTable() {
		return TableSchoolClasses.instance();
	}
	
	@Override
	public PaneSelector<?> createSelectorPane() {
		return FXMLPane.setup(new PaneClassSelector());
	}
	
	@Override
	public PaneClassEditor createEditorPane(TabHandler handler, int classId) {
		return FXMLPane.setup(new PaneClassEditor(handler, classId));
	}
	
	@Override
	public List<DatasetFieldAccessor<?>> fieldAccessors() {
		return Arrays.asList(SchoolClass.CLASS_DATA, SchoolClass.CLASS_LEVEL, SchoolClass.ROOM, SchoolClass.TEACHER);
	}
	
}
