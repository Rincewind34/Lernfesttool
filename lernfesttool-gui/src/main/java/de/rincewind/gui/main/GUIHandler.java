package de.rincewind.gui.main;

import java.nio.charset.Charset;

public class GUIHandler {
	
	private static GUISession session;
	
	public static final String PATH_LAYOUT = "fx/panes/";
	public static final String PATH_CSS = "fx/css/";

	public static final Charset CHARSET = Charset.forName("UTF-8");
	
	public static void initiliaze(GUISession session) {
		GUIHandler.session = session;
	}
	
	public static GUISession session() {
		return GUIHandler.session;
	}
	
}
