package de.rincewind.gui.panes;

import java.util.Arrays;

import de.rincewind.gui.controller.ControllerStudentActions;
import de.rincewind.gui.panes.abstracts.AdminTab;
import de.rincewind.gui.panes.abstracts.FXMLPane;
import de.rincewind.gui.printjobs.StudentList;
import de.rincewind.gui.util.Design;
import de.rincewind.gui.util.TabHandler;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class PaneStudentActions extends FXMLPane<HBox> implements AdminTab {

	public PaneStudentActions(TabHandler handler) {
		super("studentactions.fxml", Arrays.asList(), new ControllerStudentActions(handler));
	}

	@Override
	public void print() {
		Design.startPrint(new StudentList(((ControllerStudentActions) this.getController()).getSelectedStudents()));
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
		return true;
	}

	@Override
	public boolean isDeleteable() {
		return false;
	}

	@Override
	public String getName() {
		return "Schüleraktionen";
	}

	@Override
	public Pane getContentPane() {
		return this.getPane();
	}

}
