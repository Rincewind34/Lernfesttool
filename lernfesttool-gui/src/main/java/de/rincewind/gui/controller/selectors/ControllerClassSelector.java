package de.rincewind.gui.controller.selectors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.rincewind.api.SchoolClass;
import de.rincewind.api.abstracts.Dataset;
import de.rincewind.gui.controller.abstracts.ControllerSelector;
import de.rincewind.gui.util.Cell;
import de.rincewind.gui.util.filler.BoxCheck;
import de.rincewind.gui.util.filler.ListFiller;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

public class ControllerClassSelector extends ControllerSelector<SchoolClass> {
	
	@FXML
	private ListView<Cell<SchoolClass>> listClasses;
	
	@FXML
	private ComboBox<Cell<Integer>> boxClassLevels;
	
	private ListFiller<Cell<SchoolClass>> filler;
	
	@Override
	public void init() {
		List<Cell<SchoolClass>> elements = new ArrayList<>();
		
		for (Dataset schoolClass : SchoolClass.getManager().initAllDatasets().sync()) {
			schoolClass.fetchAll().sync();
			elements.add(schoolClass.asCell());
		}
		
		Collections.sort(elements);
		
		this.boxClassLevels.getItems().add(new Cell<>("Egal", -1));
		
		for (int i = 5; i <= 12; i++) {
			this.boxClassLevels.getItems().add(new Cell<Integer>(i + "'te Klassenstufe", i));
		}
		
		this.filler = new ListFiller<>(this.listClasses, elements);
		this.filler.addChecker(new BoxCheck<>(this.boxClassLevels, (value, boxEntry) -> {
			if (boxEntry.getSavedObject() == -1) {
				return true;
			} else {
				return boxEntry.getSavedObject() == value.getSavedObject().getValue(SchoolClass.CLASS_LEVEL);
			}
		}));
		this.filler.refresh();
	}
	
	@Override
	public ListView<Cell<SchoolClass>> getListValues() {
		return this.listClasses;
	}
	
}
