package de.rincewind.gui.controller.abstracts;

import de.rincewind.api.abstracts.Dataset;
import de.rincewind.gui.util.Cell;
import javafx.scene.control.ListView;

public abstract class ControllerSelector<T extends Dataset> implements Controller {
	
	public abstract ListView<Cell<T>> getListValues();
	
}
