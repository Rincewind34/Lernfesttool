package de.rincewind.gui.panes;

import java.util.Arrays;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import de.rincewind.gui.controller.ControllerStatsProject;
import de.rincewind.gui.panes.abstarcts.AdminTab;
import de.rincewind.gui.panes.abstarcts.FXMLPane;
import de.rincewind.gui.util.TabHandler;

public class PaneStatsProject extends FXMLPane<HBox> implements AdminTab {
	
	public PaneStatsProject(TabHandler handler) {
		super("statsproject.fxml", Arrays.asList(), new ControllerStatsProject(handler));
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
		return "Statistik - Projekte";
	}

	@Override
	public Pane getContentPane() {
		return this.getPane();
	}

}
