package de.rincewind.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

public class Design implements Printable {

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		if (pageIndex > 0) {
			return Printable.NO_SUCH_PAGE;
		}

		double width = Design.pointsToCenti((int) pageFormat.getImageableWidth());
		double height = Design.pointsToCenti((int) pageFormat.getImageableHeight());

		((Graphics2D) graphics).translate(pageFormat.getImageableX(), pageFormat.getImageableY());
		((Graphics2D) graphics).setStroke(new BasicStroke(0.25F));

		graphics.setColor(Color.BLACK);
		graphics.drawRect(0, 0, (int) pageFormat.getImageableWidth(), (int) pageFormat.getImageableHeight());

		graphics.drawLine(0, Design.centiToPoints(1), (int) pageFormat.getImageableWidth(), Design.centiToPoints(1));
		graphics.drawLine(0, Design.centiToPoints(2), (int) pageFormat.getImageableWidth(), Design.centiToPoints(2));
		graphics.drawLine(0, Design.centiToPoints(height - 1), (int) pageFormat.getImageableWidth(), Design.centiToPoints(height - 1));
		graphics.drawLine(Design.centiToPoints(2), 0, Design.centiToPoints(2), Design.centiToPoints(2));
		graphics.drawLine(Design.centiToPoints((width - 2) / 2.0D + 2), Design.centiToPoints(1), Design.centiToPoints((width - 2) / 2.0D + 2),
				Design.centiToPoints(2));
		graphics.drawLine(Design.centiToPoints(width / 2.0D), Design.centiToPoints(height - 1), Design.centiToPoints(width / 2.0D),
				(int) pageFormat.getImageableHeight());

		graphics.setFont(new Font(graphics.getFont().getFontName(), graphics.getFont().getStyle(), Design.centiToPoints(0.5)));
		graphics.drawString("001", Design.centiToPoints(0.5), Design.centiToPoints(0.75));
		graphics.drawString("3D-Drucke modelieren", Design.centiToPoints(2.5), Design.centiToPoints(0.75));
		graphics.drawString("O24", Design.centiToPoints(0.5), Design.centiToPoints(1.75));
		graphics.drawString("Klassenstufe: 5-Q1", Design.centiToPoints(2.5), Design.centiToPoints(1.75));
		graphics.drawString("Teilnhemerzahl: 5-10", Design.centiToPoints((width - 2) / 2.0D + 2.5), Design.centiToPoints(1.75));
		graphics.drawString("Kosten: Keine", Design.centiToPoints(0.5), Design.centiToPoints(height - 0.25));
		graphics.drawString("Kategory: Normal", Design.centiToPoints(width / 2.0D + 0.5), Design.centiToPoints(height - 0.25));

		Design.drawStringMultiLine((Graphics2D) graphics,
				"Mit der Software \"Blender\" können 3D Objekte erzeugt/modelliert werden. Diese\nkönnen dann mit dem 3D Drucker gedruckt werden. \n\nDas Projekt behandelt also die selbständige Einarbeit in die Software \"Blender\".\nAußerdem geht es darum sich mit dem 3D Drucker vertraut zu machen und auch\neigene Objekte auszudrucken.\n",
				Design.centiToPoints(width - 1), Design.centiToPoints(0.5), Design.centiToPoints(2.75));

		return Printable.PAGE_EXISTS;
	}

	public static int centiToPoints(double centis) {
		return (int) Math.round(centis * 0.01 / 0.000352777);
	}

	public static double pointsToCenti(int points) {
		return points * 0.000352777 * 100;
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
