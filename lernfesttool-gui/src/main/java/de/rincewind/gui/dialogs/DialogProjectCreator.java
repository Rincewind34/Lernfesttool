package de.rincewind.gui.dialogs;

import de.rincewind.api.Project;
import de.rincewind.api.util.ProjectType;
import de.rincewind.gui.panes.PaneStudentProjectCreator;
import de.rincewind.gui.panes.abstarcts.FXMLPane;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

public class DialogProjectCreator extends Dialog<Project> {
	
	public DialogProjectCreator(ProjectType type) {
		ButtonType selectType = new ButtonType("Öffnen", ButtonData.OK_DONE);
		ButtonType cancleType = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);
		
		this.setTitle("Projekt erstellen");
		this.setHeaderText("Wähle den Namen und den Typ aus.");
		this.getDialogPane().getButtonTypes().addAll(selectType, cancleType);
		
		Node btnSelect = this.getDialogPane().lookupButton(selectType);
		btnSelect.setDisable(true);
		
		PaneStudentProjectCreator pane = new PaneStudentProjectCreator();
		this.getDialogPane().setContent(FXMLPane.setup(pane).getPane());
		
		if (type != null) {
			pane.lockType(type);
		}
		
		pane.valueChanged(() -> {
			btnSelect.setDisable(pane.isNameSet() && pane.isTypeSelected());
		});
		
		this.setResultConverter((clickedType) -> {
			if (clickedType == selectType) {
				Project project = Project.getManager().newEmptyObject(Project.getManager().createNewData().sync());
				project.setValue(Project.NAME, pane.getSelectedName());
				project.setValue(Project.TYPE, pane.getSelectedType());
				return project;
			}
			
			return null;
		});
	}
	
}
