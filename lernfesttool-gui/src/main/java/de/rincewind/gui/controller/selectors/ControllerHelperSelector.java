package de.rincewind.gui.controller.selectors;

import java.util.List;
import java.util.stream.Collectors;

import de.rincewind.api.Helper;
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
		
	}
	
	@Override
	public void setupList(List<Helper> datasets) {
		List<Cell<Helper>> elements = datasets.stream().map((helper) -> {
			return helper.asCell(Helper.class);
		}).sorted().collect(Collectors.toList());
		
		this.filler = new ListFiller<>(this.listHelpers, elements);
		this.filler.addChecker(new SearchCheck<>(this.textSearch));
		this.filler.refresh();
	}
	
	@Override
	public ListView<Cell<Helper>> getListValues() {
		return this.listHelpers;
	}
	
}
