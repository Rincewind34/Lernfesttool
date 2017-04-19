package de.rincewind.api.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.rincewind.api.SchoolClass;
import de.rincewind.api.Student;
import de.rincewind.api.abstracts.Dataset;
import de.rincewind.api.abstracts.DatasetFieldAccessor;
import de.rincewind.api.abstracts.DatasetManager;
import de.rincewind.api.util.ProjectSet;
import de.rincewind.api.util.ProjectType;
import de.rincewind.gui.controller.selectors.ControllerStudentSelector;
import de.rincewind.gui.panes.abstracts.FXMLPane;
import de.rincewind.gui.panes.editors.PaneStudentEditor;
import de.rincewind.gui.panes.selectors.PaneSelector;
import de.rincewind.gui.util.TabHandler;
import de.rincewind.sql.SQLRequest;
import de.rincewind.sql.tables.entities.TableStudents;
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
	
	public SQLRequest<List<Student>> getAllDatasets(Map<Integer, SchoolClass> classes) {
		return () -> {
			List<Student> students = this.getAllDatasets().sync();
			
			for (Student student : students) {
				if (student.isSchoolClassSelected()) {
					student.getValue(Student.SCHOOL_CLASS).loadFrom(classes.get(student.getValue(Student.SCHOOL_CLASS).getId()));
				}
			}
			
			return students;
		};
	}

	public List<Student> getWithoutChoice(Map<Integer, Student> studentMap, Map<Integer, ProjectSet[]> chooses, Map<Integer, ProjectSet> leadingSets) {
		List<Student> result = new ArrayList<>();

		for (Integer studentId : studentMap.keySet()) {
			Student student = studentMap.get(studentId);
			
			if (leadingSets.containsKey(studentId)) {
				if (leadingSets.get(studentId).isComplete()) {
					continue;
				}
				
				if (chooses.containsKey(studentId)) {
					if (leadingSets.get(studentId).isSet(ProjectType.EARLY) && ProjectSet.checkAll(chooses.get(studentId), ProjectType.LATE)) {
						continue;
					} else if (leadingSets.get(studentId).isSet(ProjectType.LATE) && ProjectSet.checkAll(chooses.get(studentId), ProjectType.EARLY)) {
						continue;
					}
				}
			} else if (chooses.containsKey(studentId) && ProjectSet.checkAll(chooses.get(studentId))) {
				continue;
			}
			
			result.add(student);
		}

		return result;
	}

	public List<Student> getWithoutProject(Map<Integer, Student> studentMap, Map<Integer, ProjectSet> attandencesSets) {
		List<Student> result = new ArrayList<>();

		for (Integer studentId : studentMap.keySet()) {
			Student student = studentMap.get(studentId);

			if (attandencesSets.containsKey(studentId) && attandencesSets.get(studentId).isComplete()) {
				continue;
			} else {
				result.add(student);
			}
		}

		return result;
	}

	public SQLRequest<List<Student>> fetchSchoolClasses(List<Student> students) {
		return () -> {
			Map<Integer, SchoolClass> classes = Dataset.convertList(SchoolClass.getManager().getAllDatasets().sync());

			for (Student student : students) {
				if (student.isSchoolClassSelected()) {
					SchoolClass schoolClass = student.getValue(Student.SCHOOL_CLASS);
					schoolClass.loadFrom(classes.get(schoolClass.getId()));
				}
			}

			return students;
		};
	}

}
