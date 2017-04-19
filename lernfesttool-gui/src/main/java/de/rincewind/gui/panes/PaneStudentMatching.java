package de.rincewind.gui.panes;

import java.util.Arrays;

import de.rincewind.gui.controller.ControllerStudentMatching;
import de.rincewind.gui.panes.abstracts.AdminTab;
import de.rincewind.gui.panes.abstracts.FXMLPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class PaneStudentMatching  extends FXMLPane<HBox> implements AdminTab {

	public PaneStudentMatching() {
		super("studentmatching.fxml", Arrays.asList(), new ControllerStudentMatching());
	}

	@Override
	public void print() {
		
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
	public boolean isPrintable() {
		return false;
	}

	@Override
	public boolean isDeleteable() {
		return false;
	}

	@Override
	public String getName() {
		return "Schülerzuteilung";
	}

	@Override
	public Pane getContentPane() {
		return this.getPane();
	}

}
