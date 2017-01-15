package de.rincewind.api;

import de.rincewind.api.abstracts.Dataset;
import de.rincewind.api.abstracts.DatasetField;
import de.rincewind.api.abstracts.DatasetFieldAccessor;
import de.rincewind.api.manager.DatasetManager;
import de.rincewind.api.manager.StudentManager;
import de.rincewind.api.util.AccessLevel;
import de.rincewind.api.util.ProjectSet;
import de.rincewind.api.util.SaveResult;
import de.rincewind.api.util.StudentState;
import de.rincewind.sql.SQLRequest;
import de.rincewind.sql.tables.entities.TableStudents;

public class Student extends Dataset {
	
	public static final DatasetFieldAccessor<String> LASTNAME = new DatasetFieldAccessor<>("lastname", String.class);
	public static final DatasetFieldAccessor<String> FIRSTNAME = new DatasetFieldAccessor<>("firstname", String.class);
	public static final DatasetFieldAccessor<SchoolClass> SCHOOL_CLASS = new DatasetFieldAccessor<>("classId", SchoolClass.class);
	public static final DatasetFieldAccessor<AccessLevel> ACCESS_LEVEL = new DatasetFieldAccessor<>("accessLevel", AccessLevel.class);
	public static final DatasetFieldAccessor<String> PASSWORD = new DatasetFieldAccessor<>("password", String.class);
	public static final DatasetFieldAccessor<StudentState> STATE = new DatasetFieldAccessor<>("state", StudentState.class);
	
	public static StudentManager getManager() {
		return StudentManager.instance();
	}
	

	public Student(int studentId) {
		super(studentId);
	}
	
	@Override
	public void print() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public DatasetManager getMatchingManager() {
		return Student.getManager();
	}
	
	@Override
	public SQLRequest<SaveResult> save() {
		return () -> {
			int classId = -1;
			
			if (this.isSchoolClassSelected()) {
				classId = this.getValue(Student.SCHOOL_CLASS).getId();
			}
			
			TableStudents table = (TableStudents) this.getMatchingManager().getTable();
			table.update(this.getId(), this.getValue(Student.LASTNAME), this.getValue(Student.FIRSTNAME), classId,
					this.getValue(Student.ACCESS_LEVEL).getId(),
					this.getValue(Student.STATE).getId(), this.getValue(Student.PASSWORD)).sync();
			
			return null;
		};
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected <T> void setSQLValue(DatasetField<?> field, T value) {
		if (field.getAccessor().equals(Student.SCHOOL_CLASS)) {
			((DatasetField<SchoolClass>) field).setValue(SchoolClass.getManager().newEmptyObject((int) (Object) value));
		} else if (field.getAccessor().equals(Student.ACCESS_LEVEL)) {
			((DatasetField<AccessLevel>) field).setValue(AccessLevel.get((int) (Object) value));
		} else if (field.getAccessor().equals(Student.STATE)) {
			((DatasetField<StudentState>) field).setValue(StudentState.get((int) (Object) value));
		} else {
			super.setSQLValue(field, value);
		}
	}
	
	@Override
	public String toString() {
		if (this.isValuePreset(Student.LASTNAME) && this.isValuePreset(Student.FIRSTNAME)) {
			return this.getValue(Student.LASTNAME) + ", " + this.getValue(Student.FIRSTNAME);
		} else {
			return "schüler-" + this.getId();
		}
	}
	
	public boolean isSchoolClassSelected() {
		return Dataset.isDatasetSelected(Student.SCHOOL_CLASS, this);
	}
	
	public SQLRequest<Void> fetchSchoolClass() {
		return Dataset.fetchDataset(Student.SCHOOL_CLASS, this);
	}
	
	public SQLRequest<ProjectSet> getLeadingProjects() {
		return () -> {
			ProjectSet projectSet = new ProjectSet();
			projectSet.fetch(this).sync();
			return projectSet;
		};
	}
	
}
