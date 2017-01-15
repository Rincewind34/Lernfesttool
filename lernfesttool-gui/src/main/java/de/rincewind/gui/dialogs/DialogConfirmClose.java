package de.rincewind.gui.dialogs;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

public class DialogConfirmClose extends Dialog<Boolean> {

	public DialogConfirmClose() {
		ButtonType yesType = new ButtonType("Ja", ButtonData.YES);
		ButtonType noType = new ButtonType("Nein", ButtonData.NO);
		
		this.setTitle("Bestätigen");
		this.setHeaderText("Sollen die veränderten Daten gespeichert werden?");
		this.getDialogPane().getButtonTypes().addAll(yesType, noType);
		
		this.setResultConverter((clickedType) -> {
			if (clickedType == yesType) {
				return true;
			} else {
				return false;
			}
		});
	}

}
