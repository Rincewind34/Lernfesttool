package de.rincewind.gui.controller.editors;

import java.util.ArrayList;
import java.util.List;

import de.rincewind.api.Helper;
import de.rincewind.api.SchoolClass;
import de.rincewind.api.Student;
import de.rincewind.gui.controller.abstracts.ControllerEditor;
import de.rincewind.gui.util.Cell;
import de.rincewind.gui.util.TabHandler;
import de.rincewind.gui.util.filler.BoxCheck;
import de.rincewind.gui.util.filler.ListFiller;
import de.rincewind.gui.util.filler.SearchCheck;
import de.rincewind.gui.util.listeners.ActionListener;
import de.rincewind.gui.util.listeners.DoubleClickListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ControllerHelperEditor extends ControllerEditor {
	
	@FXML
	private TextField textName;
	
	@FXML
	private TextField textSearch;
	
	@FXML
	private Button btnChooseStudent;
	
	@FXML
	private Button btnOpenStudent;
	
	@FXML
	private Button btnUnselect;
	
	@FXML
	private Label labelStudent;
	
	@FXML
	private ComboBox<Cell<SchoolClass>> boxClasses;
	
	@FXML
	private ListView<Cell<Student>> listStudents;
	
	private Helper helper;
	
	private ListFiller<Cell<Student>> filler;
	
	public ControllerHelperEditor(TabHandler handler, int helperId) {
		super(handler);
		
		this.helper = Helper.getManager().newEmptyObject(helperId);
	}
	
	@Override
	public void init() {
		this.helper.fetchAll().sync();
		
		// === Building === //
		
		this.boxClasses.getItems().add(new Cell<>("Alle", null));
		
		for (SchoolClass dataset : SchoolClass.getManager().getAllDatasets().sync()) {
			this.boxClasses.getItems().add(dataset.asCell());
		}
		
		List<Cell<Student>> studentElements = new ArrayList<>();
		
		for (Student dataset : Student.getManager().getAllDatasets().sync()) {
			studentElements.add(dataset.asCell());
		}
		
		this.filler = new ListFiller<>(this.listStudents, studentElements);
		this.filler.addChecker(new SearchCheck<Cell<Student>>(this.textSearch));
		this.filler.addChecker(new BoxCheck<>(this.boxClasses, (value, boxEntry) -> {
			if (boxEntry.getSavedObject() == null) {
				return true;
			}
			
			return boxEntry.getSavedObject().getId() == value.getSavedObject().getId();
		}));
		
		this.getSaveHandler().addValue(this.textName.textProperty());
		
		// === Building === //
		// === Inserting === //
		
		this.boxClasses.getSelectionModel().select(0);
		this.filler.refresh();
		
		this.textName.setText(this.helper.getValue(Helper.NAME));
		
		if (this.helper.isStudentSelected()) {
			this.setStudent(this.helper.getValue(Helper.STUDENT));
		} else {
			this.setStudent(null);
		}
		
		// === Inserting === //
		// === Listening === //
		
		this.btnChooseStudent.setOnAction((event) -> {
			this.setStudent(this.listStudents.getSelectionModel().getSelectedItem().getSavedObject());
		});
		
		this.btnUnselect.setOnAction((event) -> {
			this.setStudent(null);
		});
		
		this.listStudents.getSelectionModel().selectedItemProperty().addListener((observeable, oldValue, newValue) -> {
			this.updateButtons();
		});
		
		this.btnOpenStudent.setOnAction(new ActionListener(this.getTabHandler(), () -> {
			return this.helper.getValue(Helper.STUDENT);
		}));
		
		this.listStudents.setOnMouseClicked(new DoubleClickListener(this, this.listStudents));
		
		// === Listening === //
		
		this.getSaveHandler().reset();
	}
	
	@Override
	public void saveStages() {
		this.helper.setValue(Helper.NAME, this.textName.getText());
		this.helper.save().async((result) -> {
			
		}, (exception) -> {
			// TODO
		});
	}
	
	@Override
	public Helper getEditingObject() {
		return this.helper;
	}
	
	private void setStudent(Student student) {
		if (this.helper.isStudentSelected() && this.helper.getValue(Helper.STUDENT).getId() == student.getId()) {
			return;
		}
		
		this.helper.setValue(Helper.STUDENT, student);
		
		if (student != null) {
			this.labelStudent.setText(student.toString());
		} else {
			this.labelStudent.setText("Keinen");
		}
		
		this.updateButtons();
		this.getSaveHandler().valueChanged();
	}
	
	private void updateButtons() {
		this.btnChooseStudent.setDisable(this.listStudents.getSelectionModel().getSelectedIndex() == -1);
		this.btnOpenStudent.setDisable(!this.helper.isStudentSelected());
		this.btnUnselect.setDisable(!this.helper.isStudentSelected());
	}
	
	public TextField getTextName() {
		return this.textName;
	}
	
}
