package de.rincewind.gui.panes;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.Arrays;
import java.util.List;

import de.rincewind.api.Room;
import de.rincewind.api.SchoolClass;
import de.rincewind.api.Student;
import de.rincewind.gui.controller.ControllerStudentActions;
import de.rincewind.gui.panes.abstracts.AdminTab;
import de.rincewind.gui.panes.abstracts.FXMLPane;
import de.rincewind.gui.util.Design;
import de.rincewind.gui.util.TabHandler;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class PaneStudentActions extends FXMLPane<HBox> implements AdminTab {

	public PaneStudentActions(TabHandler handler) {
		super("studentactions.fxml", Arrays.asList(), new ControllerStudentActions(handler));
	}

	@Override
	public void print() {
		Design.startPrint(this.new Canvas());
	}

	@Override
	public boolean save() {
		return false;
	}

	@Override
	public boolean delete() {
		return false;
	}

	@Override
	public boolean hasValueChanged() {
		return false;
	}

	@Override
	public boolean isSaveable() {
		return false;
	}

	@Override
	public boolean isPrintable() {
		return true;
	}

	@Override
	public boolean isDeleteable() {
		return false;
	}

	@Override
	public String getName() {
		return "Schüleraktionen";
	}

	@Override
	public Pane getContentPane() {
		return this.getPane();
	}

	private class Canvas implements Printable {

		private List<Student> students;

		public Canvas() {
			this.students = ((ControllerStudentActions) PaneStudentActions.this.getController()).getSelectedStudents();
		}

		@Override
		public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
			int index = 0;
			double currentY = 1.2;
			
			for (int i = 0; i < pageIndex; i++) {
				while (Design.centiToPoints(currentY + 1) < pageFormat.getImageableHeight()) {
					index = index + 1;
					currentY = currentY + 1;
				}
				
				currentY = 1.2;
			}
			
			if (index >= this.students.size()) {
				return Printable.NO_SUCH_PAGE;
			}
			
			Design.setupGraphics((Graphics2D) graphics, pageFormat);

			this.drawRow(graphics, pageFormat, 1, 0);
			this.fillRow(graphics, 0, "Klasse", "Raum", "Schüler");
			this.drawRow(graphics, pageFormat, 0.2, 1);
			
			for (; index < this.students.size(); index = index + 1) {
				if (Design.centiToPoints(currentY + 1) > pageFormat.getImageableHeight()) {
					break;
				}
				
				Student student = this.students.get(index);

				this.drawRow(graphics, pageFormat, 1, currentY);
				this.fillRow(graphics, currentY, student.getValue(Student.SCHOOL_CLASS).toString(),
						student.getValue(Student.SCHOOL_CLASS).getValue(SchoolClass.ROOM).getValue(Room.NAME),
						student.getValue(Student.LASTNAME) + ", " + student.getValue(Student.FIRSTNAME));
				
				currentY = currentY + 1;
			}
			
			graphics.drawLine(0, Design.centiToPoints(currentY), (int) pageFormat.getWidth(), Design.centiToPoints(currentY));
			return Printable.PAGE_EXISTS;
		}

		private void drawRow(Graphics graphics, PageFormat pageFormat, double height, double y) {
			graphics.drawLine(0, Design.centiToPoints(y), (int) pageFormat.getWidth(), Design.centiToPoints(y));
			graphics.drawLine(0, Design.centiToPoints(y), 0, Design.centiToPoints(y + height));
			graphics.drawLine(Design.centiToPoints(3), Design.centiToPoints(y), Design.centiToPoints(3), Design.centiToPoints(y + height));
			graphics.drawLine(Design.centiToPoints(6), Design.centiToPoints(y), Design.centiToPoints(6), Design.centiToPoints(y + height));
			graphics.drawLine((int) pageFormat.getImageableWidth(), Design.centiToPoints(y), (int) pageFormat.getImageableWidth(), Design.centiToPoints(y + height));
		}

		private void fillRow(Graphics graphics, double y, String col1, String col2, String col3) {
			graphics.drawString(col1, Design.centiToPoints(0.5), Design.centiToPoints(y + 0.75));
			graphics.drawString(col2, Design.centiToPoints(3.5), Design.centiToPoints(y + 0.75));
			graphics.drawString(col3, Design.centiToPoints(6.5), Design.centiToPoints(y + 0.75));
		}

	}

}
