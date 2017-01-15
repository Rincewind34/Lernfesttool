package de.rincewind.api.manager;

import java.util.Arrays;
import java.util.List;

import de.rincewind.api.Student;
import de.rincewind.api.abstracts.DatasetFieldAccessor;
import de.rincewind.gui.panes.abstarcts.FXMLPane;
import de.rincewind.gui.panes.abstarcts.PaneSelector;
import de.rincewind.gui.panes.editors.PaneStudentEditor;
import de.rincewind.gui.panes.selectors.PaneStudentSelector;
import de.rincewind.gui.util.TabHandler;
import de.rincewind.sql.tables.entities.TableStudents;

public class StudentManager extends DatasetManager {
	
	private static StudentManager instance;
	
	static {
		StudentManager.instance = new StudentManager();
	}
	
	public static StudentManager instance() {
		return StudentManager.instance;
	}
	
	
	private StudentManager() {
		
	}
	
	@Override
	public String getDataName() {
		return "Schüler";
	}
	
	@Override
	public Student newEmptyObject(int datasetId) {
		return new Student(datasetId);
	}
	
	@Override
	public TableStudents getTable() {
		return TableStudents.instance();
	}
	
	@Override
	public PaneSelector<?> createSelectorPane() {
		return FXMLPane.setup(new PaneStudentSelector());
	}
	
	@Override
	public PaneStudentEditor createEditorPane(TabHandler handler, int studentId) {
		return FXMLPane.setup(new PaneStudentEditor(handler, studentId));
	}
	
	@Override
	public List<DatasetFieldAccessor<?>> fieldAccessors() {
		return Arrays.asList(Student.ACCESS_LEVEL, Student.FIRSTNAME, Student.LASTNAME, Student.PASSWORD, Student.SCHOOL_CLASS, Student.STATE);
	}
	
}
