package de.rincewind.api;

import de.rincewind.api.abstracts.Dataset;
import de.rincewind.api.abstracts.DatasetField;
import de.rincewind.api.abstracts.DatasetFieldAccessor;
import de.rincewind.api.abstracts.DatasetManager;
import de.rincewind.api.manager.StudentManager;
import de.rincewind.api.util.AccessLevel;
import de.rincewind.api.util.ProjectSet;
import de.rincewind.api.util.SaveResult;
import de.rincewind.api.util.StudentState;
import de.rincewind.sql.SQLRequest;
import de.rincewind.sql.tables.entities.TableStudents;
import de.rincewind.sql.tables.relations.TableProjectAttandences;
import de.rincewind.sql.tables.relations.TableProjectChoosing;

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
			table.update(this.getId(), this.getValue(Student.LASTNAME), this.getValue(Student.FIRSTNAME), classId, this.getValue(Student.ACCESS_LEVEL).getId(),
					this.getValue(Student.STATE).getId(), this.getValue(Student.PASSWORD)).sync();

			return null;
		};
	}

	@Override
	@SuppressWarnings("unchecked")
	protected <T> void setSQLValue(DatasetField<?> field, T value) {
		if (field.getAccessor().equals(Student.SCHOOL_CLASS)) {
			Dataset.initDataset((DatasetField<Dataset>) field, (int) (Object) value, SchoolClass.getManager());
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
			String result = "";

			if (this.isSchoolClassSelected() && this.isValuePreset(Student.SCHOOL_CLASS)) {
				SchoolClass schoolClass = this.getValue(Student.SCHOOL_CLASS);

				if (schoolClass.isValuePreset(SchoolClass.CLASS_LEVEL) && schoolClass.isValuePreset(SchoolClass.CLASS_DATA)) {
					result = "(" + schoolClass.toString() + ") ";
				}
			}

			return result + this.getValue(Student.LASTNAME) + ", " + this.getValue(Student.FIRSTNAME);
		} else {
			return "schüler-" + this.getId();
		}
	}

	@Override
	public SQLRequest<Void> delete() {
		return () -> {
			super.delete().sync();
			TableProjectAttandences.instance().clearStudent(this.getId(), true).sync();
			TableProjectAttandences.instance().clearStudent(this.getId(), false).sync();
			TableProjectChoosing.instance().clearStudent(this.getId()).sync();
			return null;
		};
	}

	public boolean isSchoolClassSelected() {
		return Dataset.isDatasetSelected(Student.SCHOOL_CLASS, this);
	}

	public SQLRequest<Void> fetchSchoolClass() {
		return Dataset.fetchDataset(Student.SCHOOL_CLASS, this);
	}

	public SQLRequest<Void> choose(ProjectSet[] sets) {
		return () -> {
			TableProjectChoosing.instance().clearStudent(this.getId()).sync();

			for (int i = 0; i < 3; i++) {
				for (Project project : sets[i].projects()) {
					TableProjectChoosing.instance().add(project.getId(), this.getId(), i).sync();
				}
			}

			return null;
		};
	}

	public SQLRequest<Void> chooseSecond(ProjectSet set) {
		return () -> {
			TableProjectAttandences.instance().clearStudent(this.getId(), false).sync();
			set.clearLeading();

			for (Project project : set.projects()) {
				TableProjectAttandences.instance().add(project.getId(), this.getId(), false).sync();
			}

			return null;
		};
	}

	public SQLRequest<ProjectSet> getLeadingProjects() {
		return () -> {
			ProjectSet projectSet = new ProjectSet();
			projectSet.fetch(this).sync();
			return projectSet;
		};
	}

}
