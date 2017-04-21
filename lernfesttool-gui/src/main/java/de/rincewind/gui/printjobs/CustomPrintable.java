package de.rincewind.gui.printjobs;

import java.awt.print.PageFormat;
import java.awt.print.Printable;

public interface CustomPrintable extends Printable {
	
	public abstract int getPageCount(PageFormat pageFormat);
	
}
