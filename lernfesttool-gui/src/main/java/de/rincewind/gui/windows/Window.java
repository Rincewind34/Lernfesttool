package de.rincewind.gui.windows;

import de.rincewind.gui.panes.abstracts.FXMLPane;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public abstract class Window<T extends Pane> {
	
	private String title;
	
	private FXMLPane<T> pane;
	
	public Window(FXMLPane<T> pane, String title) {
		this.title = title;
		this.pane = pane;
	}
	
	public final void create() {
		if (this.pane.getPane() == null) {
			this.pane.create();
		}
	}
	
	public final void init(Stage stage) {
		this.pane.init();
		
		stage.setScene(new Scene(this.pane.getPane()));
		stage.setFullScreen(true);
		stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		stage.setFullScreenExitHint(null);
		stage.setTitle(this.title);
		
		this.onInit(stage, this.pane.getPane());
		
		stage.show();
		
		this.onShow(stage, this.pane.getPane());
	}
	
	public final void dispose(Stage stage) {
		this.onDispose(stage);
		stage.hide();
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public FXMLPane<T> getFXMLPane() {
		return this.pane;
	}
	
	public abstract void onInit(Stage stage, T pane);
	
	public abstract void onShow(Stage stage, T pane);
	
	public abstract void onDispose(Stage stage);
	
}
