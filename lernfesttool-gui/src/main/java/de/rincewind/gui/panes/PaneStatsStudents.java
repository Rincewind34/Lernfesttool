package de.rincewind.gui.panes;

import java.util.Arrays;

import de.rincewind.gui.controller.ControllerStatsStudents;
import de.rincewind.gui.panes.abstracts.AdminTab;
import de.rincewind.gui.panes.abstracts.FXMLPane;
import de.rincewind.gui.util.TabHandler;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class PaneStatsStudents extends FXMLPane<HBox> implements AdminTab {
	
	public PaneStatsStudents(TabHandler handler) {
		super("statsstudents.fxml", Arrays.asList(), new ControllerStatsStudents(handler));
	}

	@Override
	public void print() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean save() {
		return false;
	}
	
	@Override
	public boolean delete() {
		return false;
	}
	
	@Override
	public boolean hasValueChanged() {
		return false;
	}
	
	@Override
	public boolean isSaveable() {
		return false;
	}
	
	@Override
	public boolean isDeleteable() {
		return false;
	}

	@Override
	public boolean isPrintable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		return "Statistik - Schüler";
	}

	@Override
	public Pane getContentPane() {
		return this.getPane();
	}

}
