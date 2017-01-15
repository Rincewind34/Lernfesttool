package de.rincewind.gui.controller.selectors;

import java.util.ArrayList;
import java.util.List;

import de.rincewind.api.SchoolClass;
import de.rincewind.api.Student;
import de.rincewind.api.abstracts.Dataset;
import de.rincewind.gui.controller.abstracts.ControllerSelector;
import de.rincewind.gui.util.Cell;
import de.rincewind.gui.util.filler.BoxCheck;
import de.rincewind.gui.util.filler.ListFiller;
import de.rincewind.gui.util.filler.SearchCheck;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ControllerStudentSelector extends ControllerSelector<Student> {
	
	@FXML
	private ListView<Cell<Student>> listStudents;
	
	@FXML
	private ComboBox<Cell<SchoolClass>> boxClasses;
	
	@FXML
	private TextField textSearch;
	
	private ListFiller<Cell<Student>> filler;
	
	@Override
	public void init() {
		List<Cell<Student>> elements = new ArrayList<>();
		
		for (Dataset dataset : Student.getManager().initAllDatasets().sync()) {
			dataset.fetchAll().sync();
			elements.add(dataset.asCell());
		}
		
		this.boxClasses.getItems().add(new Cell<>("Alle", null));
		
		for (Dataset schoolClass : SchoolClass.getManager().initAllDatasets().sync()) {
			schoolClass.fetchAll().sync();
			this.boxClasses.getItems().add(schoolClass.asCell());
		}
		
		this.filler = new ListFiller<>(this.listStudents, elements);
		this.filler.addChecker(new SearchCheck<>(this.textSearch));
		this.filler.addChecker(new BoxCheck<>(this.boxClasses, (value, boxEntry) -> {
			if (boxEntry.getSavedObject() == null) {
				return true;
			} else {
				return boxEntry.getSavedObject().getId() == value.getSavedObject().getId();
			}
		}));
		this.filler.refresh();
	}
	
	@Override
	public ListView<Cell<Student>> getListValues() {
		return this.listStudents;
	}
	
}
