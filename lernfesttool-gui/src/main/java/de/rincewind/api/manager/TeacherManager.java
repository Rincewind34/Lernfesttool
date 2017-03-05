package de.rincewind.api.manager;

import java.util.Arrays;
import java.util.List;

import de.rincewind.api.Teacher;
import de.rincewind.api.abstracts.DatasetFieldAccessor;
import de.rincewind.api.abstracts.DatasetManager;
import de.rincewind.gui.controller.selectors.ControllerTeacherSelector;
import de.rincewind.gui.panes.abstarcts.FXMLPane;
import de.rincewind.gui.panes.editors.PaneTeacherEditor;
import de.rincewind.gui.panes.selectors.PaneSelector;
import de.rincewind.gui.util.TabHandler;
import de.rincewind.sql.SQLRequest;
import de.rincewind.sql.tables.entities.TableTeachers;
import javafx.scene.layout.VBox;

public class TeacherManager extends DatasetManager {
	
	private static TeacherManager instance;
	
	static {
		TeacherManager.instance = new TeacherManager();
	}
	
	public static TeacherManager instance() {
		return TeacherManager.instance;
	}
	
	private TeacherManager() {
		
	}
	
	@Override
	public String getDataName() {
		return "Lehrer";
	}
	
	@Override
	public Teacher newEmptyObject(int datasetId) {
		return new Teacher(datasetId);
	}
	
	@Override
	public TableTeachers getTable() {
		return TableTeachers.instance();
	}
	
	@Override
	public PaneSelector<VBox, Teacher> createSelectorPane() {
		return FXMLPane.setup(new PaneSelector<>("teacherselector.fxml", Arrays.asList(), new ControllerTeacherSelector(), this, Teacher.class));
	}
	
	@Override
	public PaneTeacherEditor createEditorPane(TabHandler handler, int teacherId) {
		return FXMLPane.setup(new PaneTeacherEditor(handler, teacherId));
	}
	
	@Override
	public List<DatasetFieldAccessor<?>> fieldAccessors() {
		return Arrays.asList(Teacher.NAME, Teacher.TOKEN);
	}

	@Override
	@SuppressWarnings("unchecked")
	public SQLRequest<List<Teacher>> getAllDatasets() {
		return this.getAllDatasets(Teacher.class);
	}
	
}
