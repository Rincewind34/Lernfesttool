package de.rincewind.gui.printjobs;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.rincewind.api.Project;
import de.rincewind.api.Room;
import de.rincewind.api.SchoolClass;
import de.rincewind.api.Student;
import de.rincewind.gui.util.Design;

public class ProjectStudentList implements CustomPrintable {
	
	private Project project;
	private List<Student> students;
	
	public ProjectStudentList(Project project) {
		this.project = project;
		this.project.fetchMissing().sync();
		this.students = this.project.fetchAttendences().sync();
		this.students = Student.getManager().fetchSchoolClasses(this.students).sync();
		
		Collections.sort(this.students, new Comparator<Student>() {

			@Override
			public int compare(Student student1, Student student2) {
				if (!student1.isSchoolClassSelected() && !student2.isSchoolClassSelected()) {
					return 0;
				}
				
				if (!student1.isSchoolClassSelected()) {
					return -1;
				}
				
				if (!student2.isSchoolClassSelected()) {
					return 1;
				}
				
				int result = student1.getValue(Student.SCHOOL_CLASS).toString().compareTo(student2.getValue(Student.SCHOOL_CLASS).toString());
				
				if (result == 0) {
					return student1.toString().compareTo(student2.toString());
				} else {
					return result;
				}
			}
			
		});
	}
	
	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		int index = 0;
		double currentY = 2.2;
		
		for (int i = 0; i < pageIndex; i++) {
			while (Design.centiToPoints(currentY + 1) < pageFormat.getImageableHeight()) {
				index = index + 1;
				currentY = currentY + 1;
			}
			
			currentY = 2.2;
		}
		
		Design.setupGraphics((Graphics2D) graphics, pageFormat);
		graphics.drawString("Projekt: " + this.project.getValue(Project.NAME), Design.centiToPoints(0.5), Design.centiToPoints(0.75));
		
		this.drawRow(graphics, pageFormat, 1, 1);
		this.fillRow(graphics, 1, "Klasse", "Raum", "Schüler");
		this.drawRow(graphics, pageFormat, 0.2, 2);
		
		if (index >= this.students.size()) {
			return Printable.NO_SUCH_PAGE;
		}
		
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
	
	@Override
	public int getPageCount(PageFormat pageFormat) {
		int page = 0;
		double currentY;
		
		for (int i = 0; i < this.students.size(); i++) {
			currentY = 2.2;
			page = page + 1;
			
			while (Design.centiToPoints(currentY + 1) < pageFormat.getImageableHeight()) {
				currentY = currentY + 1;
				i = i + 1;
			}
		}
		
		return page;
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
