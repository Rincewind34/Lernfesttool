package de.rincewind.api;

import de.rincewind.api.abstracts.Dataset;
import de.rincewind.api.abstracts.DatasetField;
import de.rincewind.api.abstracts.DatasetFieldAccessor;
import de.rincewind.api.manager.DatasetManager;
import de.rincewind.api.manager.HelperManager;
import de.rincewind.api.util.GuideType;
import de.rincewind.api.util.SaveResult;
import de.rincewind.sql.SQLRequest;
import de.rincewind.sql.tables.entities.TableHelpers;

public class Helper extends Guide {
	
	public static final DatasetFieldAccessor<String> NAME = new DatasetFieldAccessor<>("name", String.class);
	public static final DatasetFieldAccessor<Student> STUDENT = new DatasetFieldAccessor<>("studentId", Student.class);
	
	public static HelperManager getManager() {
		return HelperManager.instance();
	}
	
	
	public Helper(int helperId) {
		super(helperId);
	}
	
	@Override
	public void print() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public GuideType getType() {
		return GuideType.HELPER;
	}
	
	@Override
	public DatasetManager getMatchingManager() {
		return Helper.getManager();
	}
	
	@Override
	public SQLRequest<SaveResult> save() {
		return () -> {
			SaveResult result;
			
			try {
				int studentId = -1;
				
				if (this.isStudentSelected()) {
					studentId = this.getValue(Helper.STUDENT).getId();
				}
				
				TableHelpers table = (TableHelpers) this.getMatchingManager().getTable();
				table.update(this.getId(), this.getValue(Helper.NAME), studentId).sync();
				
				result = SaveResult.success();
			} catch (Exception ex) {
				result = SaveResult.error(ex);
			}
			
			return result;
		};
	}
	
	@Override
	public String toString() {
		if (this.isValuePreset(Helper.NAME)) {
			return "(H) " + this.getValue(Helper.NAME);
		} else {
			return "helfer-" + this.getId();
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected <T> void setSQLValue(DatasetField<?> field, T value) {
		if (field.getAccessor().equals(Helper.STUDENT)) {
			Dataset.initDataset((DatasetField<Dataset>) field, (int) (Object) value, Student.getManager());
		} else {
			super.setSQLValue(field, value);
		}
	}
	
	public boolean isStudentSelected() {
		if (this.isValuePreset(Helper.STUDENT)) {
			return false;
		}
		
		return this.getValue(Helper.STUDENT) != null;
	}
	
	public SQLRequest<Void> fetchStudent() {
		Student student = this.getValue(Helper.STUDENT);
		
		if (student == null) {
			throw new RuntimeException("The student is not selected!");
		}
		
		return student.fetchAll();
	}
	
}
