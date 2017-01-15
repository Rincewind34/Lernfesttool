package de.rincewind.api.manager;

import java.util.Arrays;
import java.util.List;

import de.rincewind.api.Teacher;
import de.rincewind.api.abstracts.DatasetFieldAccessor;
import de.rincewind.gui.panes.abstarcts.FXMLPane;
import de.rincewind.gui.panes.abstarcts.PaneSelector;
import de.rincewind.gui.panes.editors.PaneTeacherEditor;
import de.rincewind.gui.panes.selectors.PaneTeacherSelector;
import de.rincewind.gui.util.TabHandler;
import de.rincewind.sql.tables.entities.TableTeachers;

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
	public PaneSelector<?> createSelectorPane() {
		return FXMLPane.setup(new PaneTeacherSelector());
	}
	
	@Override
	public PaneTeacherEditor createEditorPane(TabHandler handler, int teacherId) {
		return FXMLPane.setup(new PaneTeacherEditor(handler, teacherId));
	}
	
	@Override
	public List<DatasetFieldAccessor<?>> fieldAccessors() {
		return Arrays.asList(Teacher.NAME, Teacher.TOKEN);
	}
	
}
