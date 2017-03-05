package de.rincewind.api.manager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.rincewind.api.Project;
import de.rincewind.api.Student;
import de.rincewind.api.abstracts.DatasetFieldAccessor;
import de.rincewind.api.abstracts.DatasetManager;
import de.rincewind.api.util.ProjectSet;
import de.rincewind.gui.controller.selectors.ControllerStudentSelector;
import de.rincewind.gui.panes.abstarcts.FXMLPane;
import de.rincewind.gui.panes.editors.PaneStudentEditor;
import de.rincewind.gui.panes.selectors.PaneSelector;
import de.rincewind.gui.util.TabHandler;
import de.rincewind.sql.SQLRequest;
import de.rincewind.sql.tables.entities.TableStudents;
import de.rincewind.sql.tables.relations.TableProjectChoosing;
import javafx.scene.layout.VBox;

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
	public PaneSelector<VBox, Student> createSelectorPane() {
		return FXMLPane.setup(new PaneSelector<>("studentselector.fxml", Arrays.asList(), new ControllerStudentSelector(), this, Student.class));
	}
	
	@Override
	public PaneStudentEditor createEditorPane(TabHandler handler, int studentId) {
		return FXMLPane.setup(new PaneStudentEditor(handler, studentId));
	}
	
	@Override
	public List<DatasetFieldAccessor<?>> fieldAccessors() {
		return Arrays.asList(Student.ACCESS_LEVEL, Student.FIRSTNAME, Student.LASTNAME, Student.PASSWORD, Student.SCHOOL_CLASS, Student.STATE);
	}

	@Override
	@SuppressWarnings("unchecked")
	public SQLRequest<List<Student>> getAllDatasets() {
		return this.getAllDatasets(Student.class);
	}
	
	public SQLRequest<Map<Student, ProjectSet[]>> fetchChooses(Map<Integer, Student> students, Map<Integer, Project> projects) {
		return () -> {
			Map<Student, ProjectSet[]> result = new HashMap<>();
			
			for (TableProjectChoosing.Entry entry : TableProjectChoosing.instance().getEntries().sync()) {
				Student student = students.get(entry.studentId);
				Project project = projects.get(entry.projectId);
				
				if (student == null) {
					System.out.println("Found null student: " + entry.studentId);
					continue;
				}
				
				if (project == null) {
					System.out.println("Found null project: " + entry.projectId);
					continue;
				}
				
				if (!result.containsKey(student)) {
					result.put(student, new ProjectSet[] { new ProjectSet(), new ProjectSet(), new ProjectSet() });
				}
				
				result.get(student)[entry.chooseIndex - 1].setProject(project);
			}
			
			for (Student student : students.values()) {
				if (!result.containsKey(student)) {
					result.put(student, null);
				}
			}
			
			return result;
		};
	}
	
}
