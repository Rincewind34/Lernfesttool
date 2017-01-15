package de.rincewind.gui.dialogs;

import javafx.scene.control.Alert;

public class DialogPressCancle extends Alert {

	public DialogPressCancle() {
		super(AlertType.CONFIRMATION);
		
		this.setTitle("Bestätigen");
		this.setHeaderText("Es werden alle Änderungen verworfen");
	}

}
