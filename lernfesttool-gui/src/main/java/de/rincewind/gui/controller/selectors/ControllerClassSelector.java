package de.rincewind.gui.controller.selectors;

import java.util.List;
import java.util.stream.Collectors;

import de.rincewind.api.SchoolClass;
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
		this.boxClassLevels.getItems().add(new Cell<>("Egal", -1));
		
		for (int i = 5; i <= 12; i++) {
			this.boxClassLevels.getItems().add(new Cell<Integer>(i + "'te Klassenstufe", i));
		}
	}
	
	@Override
	public void setupList(List<SchoolClass> datasets) {
		List<Cell<SchoolClass>> elements = datasets.stream().map((schoolClass) -> {
			return schoolClass.asCell(SchoolClass.class);
		}).sorted().collect(Collectors.toList());
		
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
