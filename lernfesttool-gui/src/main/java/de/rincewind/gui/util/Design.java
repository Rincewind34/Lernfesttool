package de.rincewind.gui.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.function.Supplier;

import de.rincewind.gui.main.GUIHandler;

public class Design {

	public static void startPrint(Printable printable) {
		Design.startPrint(() -> {
			return printable;
		});
	}

	public static void startPrint(Supplier<Printable> printable) {
		GUIHandler.session().threadpool().execute(() -> {
			PrinterJob job = PrinterJob.getPrinterJob();

			if (job.printDialog()) {
				job.setJobName("Lernfest");
				job.setPrintable(printable.get());

				try {
					job.print();
				} catch (PrinterException exception) {
					exception.printStackTrace();
				}
			}
		});
	}

	public static int centiToPoints(double centis) {
		return (int) Math.round(centis * 0.01 / 0.000352777);
	}

	public static double pointsToCenti(int points) {
		return points * 0.000352777 * 100;
	}

	public static void setupGraphics(Graphics2D graphics, PageFormat pageFormat) {
		((Graphics2D) graphics).translate(pageFormat.getImageableX(), pageFormat.getImageableY());
		((Graphics2D) graphics).setStroke(new BasicStroke(0.25F));
		graphics.setColor(Color.BLACK);
		graphics.setFont(new Font(graphics.getFont().getFontName(), graphics.getFont().getStyle(), Design.centiToPoints(0.5)));
	}

	public static void drawStringMultiLine(Graphics2D graphics, String text, int lineWidth, int x, int y) {
		text = text.replace("\n\n", " PARAGRAPH ");
		text = text.replace("\n", " ");

		FontMetrics metrix = graphics.getFontMetrics();
		String[] words = text.split(" ");
		String currentLine = "";

		for (int i = 0; i < words.length; i++) {
			String word = words[i].trim();

			if (currentLine.isEmpty()) {
				currentLine = word;
				continue;
			}

			if (word.isEmpty()) {
				continue;
			}

			if (word.equals("PARAGRAPH")) {
				graphics.drawString(currentLine, x, y);
				y = y + metrix.getHeight() * 2;
				currentLine = "";
				continue;
			}

			if (metrix.stringWidth(currentLine + " " + word) < lineWidth) {
				currentLine = currentLine + " " + word;
			} else {
				graphics.drawString(currentLine, x, y);
				y = y + metrix.getHeight();
				currentLine = word;
			}
		}

		if (currentLine.trim().length() > 0) {
			graphics.drawString(currentLine, x, y);
		}
	}

}
