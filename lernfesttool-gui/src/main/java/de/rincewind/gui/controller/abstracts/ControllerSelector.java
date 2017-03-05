package de.rincewind.gui.controller.abstracts;

import java.util.List;

import de.rincewind.api.abstracts.Dataset;
import de.rincewind.gui.util.Cell;
import javafx.scene.control.ListView;

public abstract class ControllerSelector<T extends Dataset> implements Controller {
	
	public abstract void setupList(List<T> datasets);
	
	public abstract ListView<Cell<T>> getListValues();
	
}
