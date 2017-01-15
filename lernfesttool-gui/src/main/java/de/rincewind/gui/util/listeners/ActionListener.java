package de.rincewind.gui.util.listeners;

import java.util.function.Supplier;

import de.rincewind.api.abstracts.Dataset;
import de.rincewind.gui.controller.abstracts.ControllerEditor;
import de.rincewind.gui.util.Cell;
import de.rincewind.gui.util.TabHandler;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ListView;

public class ActionListener implements EventHandler<ActionEvent> {
	
	private Runnable action;
	
	public ActionListener(ControllerEditor controller, ListView<? extends Cell<? extends Dataset>> clickedList) {
		this(controller.getTabHandler(), clickedList);
	}
	
	public ActionListener(TabHandler handler, ListView<? extends Cell<? extends Dataset>> clickedList) {
		this(handler, () -> {
			Cell<? extends Dataset> cell = clickedList.getSelectionModel().getSelectedItem();
			
			if (cell != null) {
				return cell.getSavedObject();
			} else {
				return null;
			}
		});
	}
	
	public ActionListener(ControllerEditor controller, Supplier<? extends Dataset> supplier) {
		this(controller.getTabHandler(), supplier);
	}
	
	public ActionListener(TabHandler handler, Supplier<? extends Dataset> supplier) {
		this(() -> {
			Dataset dataset = supplier.get();
			
			if (dataset != null) {
				handler.addTab(supplier.get());
			}
		});
	}
	
	public ActionListener(Runnable action) {
		this.action = action;
	}
	
	@Override
	public void handle(ActionEvent event) {
		this.action.run();
	}
	
}
