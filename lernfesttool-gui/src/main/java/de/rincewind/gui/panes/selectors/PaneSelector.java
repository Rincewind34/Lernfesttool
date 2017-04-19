package de.rincewind.gui.panes.selectors;

import java.util.List;
import java.util.stream.Collectors;

import de.rincewind.api.abstracts.Dataset;
import de.rincewind.api.abstracts.DatasetManager;
import de.rincewind.gui.controller.abstracts.ControllerSelector;
import de.rincewind.gui.panes.abstracts.FXMLPane;
import de.rincewind.gui.util.Cell;
import de.rincewind.gui.util.ChangeListener;
import javafx.application.Platform;
import javafx.scene.layout.Pane;

public class PaneSelector<T extends Pane, U extends Dataset> extends FXMLPane<T> {
	
	private DatasetManager manager;
	
	private Class<U> datasetCls;
	
	private ControllerSelector<U> controller;
	
	public PaneSelector(String layout, List<String> styleSheets, ControllerSelector<U> controller, DatasetManager manager, Class<U> datasetCls) {
		super(layout, styleSheets, controller);
		
		this.controller = controller;
		this.manager = manager;
		this.datasetCls = datasetCls;
	}
	
	@Override
	public void init() {
		super.init();
		
		this.manager.getAllDatasets().async((datasets) -> {
			List<U> casted = datasets.stream().map((dataset) -> {
				return this.datasetCls.cast(dataset);
			}).collect(Collectors.toList());
			
			Platform.runLater(() -> {
				this.controller.setupList(casted);
			});
		}, (exception) -> {
			// TODO
		});
	}

	public void addSelectListener(ChangeListener<Cell<? extends Dataset>> listener) {
		this.controller.getListValues().getSelectionModel().selectedItemProperty().addListener((observeable, oldValue, newValue) -> {
			if (oldValue == null && newValue == null) {
				return;
			}
			
			if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
				return;
			}
			
			listener.onChange(oldValue, newValue);
		});
	}
	
	public boolean isValueSelected() {
		return this.controller.getListValues().getSelectionModel().getSelectedItem() != null;
	}
	
	public Dataset getSelectedValue() {
		return this.controller.getListValues().getSelectionModel().getSelectedItem().getSavedObject();
	}
	
}
