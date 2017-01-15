package de.rincewind.gui.dialogs;

import javafx.scene.control.Alert;

public class DialogPressDelete extends Alert {

	public DialogPressDelete() {
		super(AlertType.CONFIRMATION);
		
		this.setTitle("Bestätigen");
		this.setHeaderText("Es werden sämmtliche Daten gelöscht");
	}

}
