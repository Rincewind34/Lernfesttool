package de.rincewind.api.manager;

import java.util.Arrays;
import java.util.List;

import de.rincewind.api.SchoolClass;
import de.rincewind.api.abstracts.DatasetFieldAccessor;
import de.rincewind.api.abstracts.DatasetManager;
import de.rincewind.gui.controller.selectors.ControllerClassSelector;
import de.rincewind.gui.panes.abstarcts.FXMLPane;
import de.rincewind.gui.panes.editors.PaneClassEditor;
import de.rincewind.gui.panes.selectors.PaneSelector;
import de.rincewind.gui.util.TabHandler;
import de.rincewind.sql.SQLRequest;
import de.rincewind.sql.tables.entities.TableSchoolClasses;
import javafx.scene.layout.VBox;

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
	public PaneSelector<VBox, SchoolClass> createSelectorPane() {
		return FXMLPane.setup(new PaneSelector<>("classselector.fxml", Arrays.asList(), new ControllerClassSelector(), this, SchoolClass.class));
	}
	
	@Override
	public PaneClassEditor createEditorPane(TabHandler handler, int classId) {
		return FXMLPane.setup(new PaneClassEditor(handler, classId));
	}
	
	@Override
	public List<DatasetFieldAccessor<?>> fieldAccessors() {
		return Arrays.asList(SchoolClass.CLASS_DATA, SchoolClass.CLASS_LEVEL, SchoolClass.ROOM, SchoolClass.TEACHER);
	}

	@Override
	@SuppressWarnings("unchecked")
	public SQLRequest<List<SchoolClass>> getAllDatasets() {
		return this.getAllDatasets(SchoolClass.class);
	}
	
}
