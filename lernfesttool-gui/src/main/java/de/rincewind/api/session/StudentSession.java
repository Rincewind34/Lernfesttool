package de.rincewind.api.session;

import de.rincewind.api.Student;
import de.rincewind.api.util.AccessLevel;
import de.rincewind.api.util.StudentState;
import de.rincewind.gui.main.GUIHandler;
import de.rincewind.gui.windows.WindowAdmin;
import de.rincewind.gui.windows.WindowStudent;
import de.rincewind.gui.windows.WindowStudentChoice;

public class StudentSession implements Session {

	private Student student;
	
	public StudentSession(Student student) {
		this.student = student;
	}
	
	@Override
	public void start() {
		if (this.student.getValue(Student.ACCESS_LEVEL) != AccessLevel.USER) {
			GUIHandler.session().changeWindow(new WindowAdmin());
		} else {
			StudentState state = this.student.getValue(Student.STATE);
			
			if (state == StudentState.ENTER_PROJECTS) {
				GUIHandler.session().changeWindow(new WindowStudent(this.student));
			} else if (state == StudentState.LOOKUP_PROJECTS) {
				GUIHandler.session().changeWindow(new WindowStudent(this.student));
			} else if (state == StudentState.VOTE_PROJECTS) {
				GUIHandler.session().changeWindow(new WindowStudentChoice(this.student));
			}
		}
	}
	
	@Override
	public AccessLevel getAccessLevel() {
		return this.student.getValue(Student.ACCESS_LEVEL);
	}

	@Override
	public String getUsername() {
		return "schüler-" + this.student.getId();
	}

	@Override
	public SessionType getType() {
		return SessionType.STUDENT;
	}
	
	public Student getStudent() {
		return this.student;
	}
	
}
