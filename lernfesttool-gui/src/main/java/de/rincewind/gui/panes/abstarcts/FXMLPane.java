package de.rincewind.gui.panes.abstarcts;

import java.io.IOException;
import java.util.List;

import de.rincewind.gui.controller.abstracts.Controller;
import de.rincewind.gui.main.GUIHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

public class FXMLPane<T extends Pane> {
	
	public static <U extends FXMLPane<?>> U setup(U pane) {
		pane.create();
		pane.init();
		return pane;
	}
	
	
	private Controller controller;
	
	private String layout;
	private List<String> styleSheets;
	
	private T pane;
	
	public FXMLPane(String layout, List<String> styleSheets, Controller controller) {
		this.layout = layout;
		this.styleSheets = styleSheets;
		this.controller = controller;
	}
	
	public final void create() {
		FXMLLoader loader = new FXMLLoader(GUIHandler.CHARSET);
		
		try {
			loader.setController(this.controller);
			this.pane = loader.load(GUIHandler.class.getClassLoader().getResourceAsStream(GUIHandler.PATH_LAYOUT + this.layout));
			
			for (String element : this.styleSheets) {
				this.pane.getStylesheets().add(GUIHandler.PATH_CSS + element);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void init() {
		this.controller.init();
	}
	
	public String getLayout() {
		return this.layout;
	}
	
	public List<String> getStyleSheets() {
		return this.styleSheets;
	}
	
	public T getPane() {
		return this.pane;
	}
	
	public Controller getController() {
		return this.controller;
	}
	
}
