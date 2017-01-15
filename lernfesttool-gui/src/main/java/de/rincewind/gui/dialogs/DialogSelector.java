package de.rincewind.gui.dialogs;

import de.rincewind.api.abstracts.Dataset;
import de.rincewind.api.manager.DatasetManager;
import de.rincewind.gui.panes.abstarcts.PaneSelector;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

public class DialogSelector extends Dialog<Dataset> {
	
	public DialogSelector(DatasetManager manager) {
		this(manager, manager.createSelectorPane());
	}
	
	public DialogSelector(DatasetManager manager, PaneSelector<?> fxmlPane) {
		ButtonType selectType = new ButtonType("Öffnen", ButtonData.OK_DONE);
		ButtonType cancleType = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);
		
		this.setTitle(manager.getDataName() + " öffnen");
		this.setHeaderText("Bitte wähle einen Eintrag aus" /* TODO */);
		this.getDialogPane().getButtonTypes().addAll(selectType, cancleType);
		
		Node btnSelect = this.getDialogPane().lookupButton(selectType);
		btnSelect.setDisable(true);
		
		this.getDialogPane().setContent(fxmlPane.getPane());
		
		fxmlPane.addSelectListener((oldValue, newValue) -> {
			if (newValue == null) {
				btnSelect.setDisable(true);
			} else {
				btnSelect.setDisable(false);
			}
		});
		
		this.setResultConverter((clickedType) -> {
			if (clickedType == selectType) {
				if (fxmlPane.isValueSelected()) {
					return fxmlPane.getSelectedValue();
				}
			}
			
			return null;
		});
	}
	
}
