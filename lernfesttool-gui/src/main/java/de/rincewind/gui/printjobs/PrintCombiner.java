package de.rincewind.gui.printjobs;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.List;

public class PrintCombiner implements Printable {

	private List<? extends CustomPrintable> printables;

	public PrintCombiner(List<? extends CustomPrintable> printables) {
		this.printables = printables;
	}

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		int index = 0;

		for (CustomPrintable printable : this.printables) {
			int pageCount = printable.getPageCount(pageFormat);
			
			for (int i = 0; i < pageCount; i++) {
				if (index == pageIndex) {
					printable.print(graphics, pageFormat, i);
					return Printable.PAGE_EXISTS;
				}

				index = index + 1;
			}
		}

		return Printable.NO_SUCH_PAGE;
	}

}
