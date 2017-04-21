package de.rincewind.gui.printjobs;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import de.rincewind.api.Project;
import de.rincewind.api.SchoolClass;
import de.rincewind.api.Student;
import de.rincewind.api.util.ProjectSet;
import de.rincewind.api.util.ProjectType;
import de.rincewind.gui.util.Design;

public class ClassStudentList implements CustomPrintable {

	private SchoolClass schoolClass;
	private List<String[]> content;

	public ClassStudentList(SchoolClass schoolClass, Map<Integer, ProjectSet> attandences) {
		this.schoolClass = schoolClass;
		this.content = new ArrayList<>();

		List<Student> students = schoolClass.fetchStudents().sync();
		Collections.sort(students, new Comparator<Student>() {

			@Override
			public int compare(Student student1, Student student2) {
				return student1.toString().compareTo(student2.toString());
			}

		});

		for (Student student : students) {
			String name = student.getValue(Student.LASTNAME) + ", " + student.getValue(Student.FIRSTNAME);
			ProjectSet projectSet = attandences.get(student.getId());

			if (projectSet == null || projectSet.isEmpty()) {
				this.content.add(new String[] { name, "KEIN PROJEKT", "= / =" });
			} else {
				for (ProjectType type : ProjectType.values()) {
					if (projectSet.isSet(type)) {
						String room = "NaN";

						if (projectSet.getProject(type).isRoomSelected()) {
							room = projectSet.getProject(type).getValue(Project.ROOM).toString();
						}

						this.content.add(new String[] { name,
								(projectSet.isLeading(type) ? "LEITET: " : "") + projectSet.getProject(type).getValue(Project.NAME), room });
					}
				}
			}
		}
	}

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		int index = 0;
		double currentY = 1.7;

		for (int i = 0; i < pageIndex; i++) {
			while (Design.centiToPoints(currentY + 0.6) < pageFormat.getImageableHeight()) {
				index = index + 1;
				currentY = currentY + 1;
			}

			currentY = 1.7;
		}

		if (index >= this.content.size()) {
			return Printable.NO_SUCH_PAGE;
		}
		
		String teacher = "NaN";
		
		if (this.schoolClass.isTeacherSelected()) {
			teacher = this.schoolClass.getValue(SchoolClass.TEACHER).getName();
		}

		Design.setupGraphics((Graphics2D) graphics, pageFormat);
		graphics.drawString("Klasse: " + this.schoolClass.toString() + " (" + teacher + ")",
				Design.centiToPoints(0.5), Design.centiToPoints(0.75));
		
		graphics.setFont(new Font(graphics.getFont().getName(), graphics.getFont().getStyle(), Design.centiToPoints(0.3)));
		double width = Design.pointsToCenti((int) pageFormat.getImageableWidth());

		this.drawRow(graphics, width, 0.6, 1);
		this.fillRow(graphics, width, 1, "Schüler", "Projekt", "Raum");
		this.drawRow(graphics, width, 0.1, 1.6);

		for (; index < this.content.size(); index = index + 1) {
			if (Design.centiToPoints(currentY + 0.6) > pageFormat.getImageableHeight()) {
				break;
			}

			this.drawRow(graphics, width, 0.6, currentY);
			this.fillRow(graphics, width, currentY, this.content.get(index)[0], this.content.get(index)[1], this.content.get(index)[2]);

			currentY = currentY + 0.6;
		}

		graphics.drawLine(0, Design.centiToPoints(currentY), (int) pageFormat.getWidth(), Design.centiToPoints(currentY));
		return Printable.PAGE_EXISTS;
	}

	@Override
	public int getPageCount(PageFormat pageFormat) {
		int page = 0;
		double currentY;

		for (int i = 0; i < this.content.size(); i++) {
			page = page + 1;
			currentY = 1.7;

			while (Design.centiToPoints(currentY + 0.6) < pageFormat.getImageableHeight()) {
				currentY = currentY + 0.6;
				i = i + 1;
			}
		}

		return page;
	}

	private void drawRow(Graphics graphics, double width, double height, double y) {
		graphics.drawLine(0, Design.centiToPoints(y), Design.centiToPoints(width), Design.centiToPoints(y));
		graphics.drawLine(0, Design.centiToPoints(y), 0, Design.centiToPoints(y + height));
		graphics.drawLine(Design.centiToPoints(width / 2.0D - 2), Design.centiToPoints(y), Design.centiToPoints(width / 2.0D - 2),
				Design.centiToPoints(y + height));
		graphics.drawLine(Design.centiToPoints(width - 2), Design.centiToPoints(y), Design.centiToPoints(width - 2), Design.centiToPoints(y + height));
		graphics.drawLine(Design.centiToPoints(width), Design.centiToPoints(y), Design.centiToPoints(width), Design.centiToPoints(y + height));
	}

	private void fillRow(Graphics graphics, double width, double y, String col1, String col2, String col3) {
		graphics.drawString(col1, Design.centiToPoints(0.25), Design.centiToPoints(y + 0.45));
		graphics.drawString(col2, Design.centiToPoints(width / 2.0D - 1.75), Design.centiToPoints(y + 0.45));
		graphics.drawString(col3, Design.centiToPoints(width - 1.75), Design.centiToPoints(y + 0.45));
	}

}
