package de.rincewind.gui.controller.selectors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.rincewind.api.Helper;
import de.rincewind.api.abstracts.Dataset;
import de.rincewind.gui.controller.abstracts.ControllerSelector;
import de.rincewind.gui.util.Cell;
import de.rincewind.gui.util.filler.ListFiller;
import de.rincewind.gui.util.filler.SearchCheck;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ControllerHelperSelector extends ControllerSelector<Helper> {
	
	@FXML
	private ListView<Cell<Helper>> listHelpers;
	
	@FXML
	private TextField textSearch;
	
	private ListFiller<Cell<Helper>> filler;
	
	@Override
	public void init() {
		List<Cell<Helper>> elements = new ArrayList<>();
		
		for (Dataset dataset : Helper.getManager().initAllDatasets().sync()) {
			dataset.fetchAll();
			elements.add(dataset.asCell());
		}
		
		Collections.sort(elements);
		
		this.filler = new ListFiller<>(this.listHelpers, elements);
		this.filler.addChecker(new SearchCheck<>(this.textSearch));
		this.filler.refresh();
	}
	
	@Override
	public ListView<Cell<Helper>> getListValues() {
		return this.listHelpers;
	}
	
}
