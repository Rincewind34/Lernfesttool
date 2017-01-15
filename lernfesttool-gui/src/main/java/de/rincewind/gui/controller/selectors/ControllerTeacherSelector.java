package de.rincewind.gui.controller.selectors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.rincewind.api.Teacher;
import de.rincewind.api.abstracts.Dataset;
import de.rincewind.gui.controller.abstracts.ControllerSelector;
import de.rincewind.gui.util.Cell;
import de.rincewind.gui.util.filler.ListFiller;
import de.rincewind.gui.util.filler.SearchCheck;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ControllerTeacherSelector extends ControllerSelector<Teacher> {
	
	@FXML
	private ListView<Cell<Teacher>> listTeachers;
	
	@FXML
	private TextField textSearch;
	
	private ListFiller<Cell<Teacher>> filler;
	
	@Override
	public void init() {
		List<Cell<Teacher>> elements = new ArrayList<>();
		
		for (Dataset schoolClass : Teacher.getManager().initAllDatasets().sync()) {
			schoolClass.fetchAll().sync();
			elements.add(schoolClass.asCell());
		}
		
		Collections.sort(elements);
		
		this.filler = new ListFiller<>(this.listTeachers, elements);
		this.filler.addChecker(new SearchCheck<>(this.textSearch));
		this.filler.refresh();
	}
	
	@Override
	public ListView<Cell<Teacher>> getListValues() {
		return this.listTeachers;
	}
	
}
