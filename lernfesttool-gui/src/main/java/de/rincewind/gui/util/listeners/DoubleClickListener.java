package de.rincewind.gui.util.listeners;

import java.util.function.Supplier;

import de.rincewind.api.abstracts.Dataset;
import de.rincewind.gui.controller.abstracts.ControllerEditor;
import de.rincewind.gui.util.Cell;
import de.rincewind.gui.util.TabHandler;
import javafx.event.EventHandler;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class DoubleClickListener implements EventHandler<MouseEvent> {
	
	private Runnable action;
	
	public DoubleClickListener(ControllerEditor controller, ListView<? extends Cell<? extends Dataset>> clickedList) {
		this(controller.getTabHandler(), clickedList);
	}
	
	public DoubleClickListener(TabHandler handler, ListView<? extends Cell<? extends Dataset>> clickedList) {
		this(handler, () -> {
			Cell<? extends Dataset> cell = clickedList.getSelectionModel().getSelectedItem();
			
			if (cell != null) {
				return cell.getSavedObject();
			} else {
				return null;
			}
		});
	}
	
	public DoubleClickListener(ControllerEditor controller, Supplier<? extends Dataset> supplier) {
		this(controller.getTabHandler(), supplier);
	}
	
	public DoubleClickListener(TabHandler handler, Supplier<? extends Dataset> supplier) {
		this(() -> {
			Dataset dataset = supplier.get();
			
			if (dataset != null) {
				handler.addTab(supplier.get());
			}
		});
	}
	
	public DoubleClickListener(Runnable action) {
		this.action = action;
	}
	
	@Override
	public void handle(MouseEvent event) {
		if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
			this.action.run();
		}
	}
	
}
