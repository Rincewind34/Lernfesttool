package de.rincewind.gui.panes.abstarcts;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.List;

import javafx.scene.layout.Pane;
import de.rincewind.api.abstracts.Dataset;
import de.rincewind.gui.controller.abstracts.ControllerEditor;

public abstract class PaneEditor<T extends Pane> extends FXMLPane<T> implements AdminTab {
	
	public PaneEditor(String layout, List<String> styleSheets, ControllerEditor controller) {
		super(layout, styleSheets, controller);
	}
	
	public abstract Dataset getEditingObject();
	
	@Override
	public void print() {
		PrinterJob job = PrinterJob.getPrinterJob();
		job.setJobName("Lernfest");
		job.setPrintable(this.getEditingObject());
		
		try {
			job.print();
		} catch (PrinterException exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public boolean save() {
		((ControllerEditor) this.getController()).saveStages();
		((ControllerEditor) this.getController()).getSaveHandler().reset();
		return true;
	}
	
	@Override
	public boolean delete() {
		this.getEditingObject().delete().async((nullObject) -> {
			
		}, (exception) -> {
			
		});
		
		return true;
	}

	@Override
	public boolean hasValueChanged() {
		return ((ControllerEditor) this.getController()).getSaveHandler().hasValueChanged();
	}

	@Override
	public boolean isSaveable() {
		return true;
	}
	
	@Override
	public boolean isPrintable() {
		return true;
	}
	
	@Override
	public boolean isDeleteable() {
		return true;
	}
	
	@Override
	public String getName() {
		return this.getEditingObject().getMatchingManager().getDataName() + " - " + this.getEditingObject().toString();
	}
	
	@Override
	public Pane getContentPane() {
		return this.getPane();
	}

}
