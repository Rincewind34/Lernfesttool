package de.rincewind.gui.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import de.rincewind.api.Project;
import de.rincewind.api.Student;
import de.rincewind.api.abstracts.Dataset;
import de.rincewind.api.util.ProjectSet;
import de.rincewind.api.util.ProjectType;
import de.rincewind.sql.tables.relations.TableProjectAttandences;
import de.rincewind.sql.tables.relations.TableProjectChoosing;
import net.sf.javailp.Linear;
import net.sf.javailp.OptType;
import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import net.sf.javailp.Solver;
import net.sf.javailp.SolverFactory;
import net.sf.javailp.SolverFactoryLpSolve;
import net.sf.javailp.VarType;

/*
 * Var-Structure:	<studentId>_<chooseIndex>{F | E | L} == F, E, L stehen für den Projekttyp.
 * 					<studentId>_exit == Wird 1 wenn der Schüler kein Projekt bekommen hat.
 * 
 * Main Linear: MINIMAL = 2 * 1_1F + 4 * 1_2F + 3 * 1_3E + 3 * 1_3L + <exitweight> * 1_exit
 * => Der Faktor wird berechnet, in dem der 'chooseIndex' mit 2 multipliziert wird
 * 
 * Student: 	2 		= 2 * 1_1F + 2 * 1_2F + 1 * 1_3E + 1 * 1_3L + 2 * 1_exit
 * => Der Faktor ist bei einem ganzen Projekt 2 und bei einem halben 1
 * 
 * Project: 	<maxs> 	>= 1 * 1_2F + 1 * 2_3F + 1 * 3_1F
 * => Der Faktor ist immer 1, da jede Variable die Anwesenheit eines Schülers repräsentiert
 * 
 * Wenn der Schüler mehrere halbe Projekte gewählt hat, muss eine weitere Randbedingung garantieren, dass der Schüler in einem grühen und einem späten
 * und nicht in zwei späten landet.
 * 
 * LateType:	1		>= 1 * 1_1L + 1 * 1_2L + 1 * 1_3L
 * EarlyType:	1		>= 1 * 1_1E + 1 * 1_2E + 1 * 1_3E
 * 
 */
public class ProjectMapping {
	
	private int damageFirst;
	private int damageSecond;
	private int damageThird;
	private int damageNoProject;
	private int timeout;
	
	private Consumer<String> output;

	private Map<Integer, Project> projects;

	private Map<Student, ProjectSet[]> leadEarly;
	private Map<Student, ProjectSet[]> leadLate;
	private Map<Student, ProjectSet[]> chooses;
	
	private Map<Project, List<Student>> result;

	public ProjectMapping() {
		this.projects = new HashMap<>();
		this.chooses = new HashMap<>();
		this.leadEarly = new HashMap<>();
		this.leadLate = new HashMap<>();
		this.output = (message) -> {
			System.out.println(message);
		};
	}

	public void fetchData() {
		this.projects = Dataset.convertList(Project.getManager().getAllDatasets().sync());
		Map<Integer, Student> students = Dataset.convertList(Student.getManager().getAllDatasets().sync());
		List<TableProjectAttandences.Entry> attandences = TableProjectAttandences.instance().getEntries().sync();
		List<TableProjectChoosing.Entry> choosing = TableProjectChoosing.instance().getEntries().sync();

		Map<Integer, ProjectSet> leadings = ProjectSet.convertAttandences(attandences, this.projects);
		Map<Integer, ProjectSet[]> chooses = ProjectSet.convertChooses(choosing, this.projects);
		List<Student> withoutChoice = Student.getManager().getWithoutChoice(students, chooses, leadings);
		List<Student> withoutProject = Student.getManager().getWithoutProject(students, ProjectSet.convertAttandences(attandences, this.projects));

		for (int studentId : leadings.keySet()) {
			if (!leadings.get(studentId).isComplete()) {
				if (leadings.get(studentId).isSet(ProjectType.EARLY)) {
					if (chooses.containsKey(studentId) && ProjectSet.checkAll(chooses.get(studentId), ProjectType.LATE)) {
						this.leadEarly.put(students.get(studentId), chooses.get(studentId));
						this.output.accept("INFO: Student " + students.get(studentId).toString() + ": Leading early => half choosed");
					} else {
						this.output.accept("WARNING: Student " + students.get(studentId).toString() + ": Leading early but no choice");
					}
				} else if (leadings.get(studentId).isSet(ProjectType.LATE)) {
					if (chooses.containsKey(studentId) && ProjectSet.checkAll(chooses.get(studentId), ProjectType.EARLY)) {
						this.leadLate.put(students.get(studentId), chooses.get(studentId));
						this.output.accept("INFO: Student " + students.get(studentId).toString() + ": Leading late => half choosed");
					} else {
						this.output.accept("WARNING: Student " + students.get(studentId).toString() + ": Leading late but no choice");
					}
				} else {
					this.output.accept("CRITICAL: Student " + students.get(studentId).toString() + ": Leading nothing but is in list");
				}
			} else {
				this.output.accept("INFO: Student " + students.get(studentId).toString() + ": Leading whole day => skipping");
			}

			students.remove(studentId);
		}

		for (int studentId : students.keySet()) {
			Student student = students.get(studentId);

			if (withoutChoice.contains(student)) {
				this.output.accept("WARNING: Student " + students.get(studentId).toString() + ": No choice");
				continue;
			}

			if (!withoutProject.contains(student)) {
				this.output.accept("WARNING: Student " + students.get(studentId).toString() + ": Already in project");
				continue;
			}

			this.chooses.put(students.get(studentId), chooses.get(studentId));
		}
	}

	public Map<Integer, Integer> execute() {
		this.output.accept("\nStarting solve...");
		this.output.accept("Students (Full): " + this.chooses.size());
		this.output.accept("Students (Frühe-Leitung): " + this.leadEarly.size());
		this.output.accept("Students (Späte-Leitung): " + this.leadLate.size());
		this.output.accept("Projects: " + this.projects.size());

		long timestampPreparing = System.currentTimeMillis();

		SolverFactory factory = new SolverFactoryLpSolve();
		factory.setParameter(Solver.VERBOSE, 0);
		factory.setParameter(Solver.TIMEOUT, this.timeout);

		Problem problem = new Problem();
		Linear mainLinear = new Linear();

		Map<Integer, Linear> projectLinears = new HashMap<>();

		for (Student student : this.chooses.keySet()) {
			int studentId = student.getId();
			ProjectSet[] projectSets = this.chooses.get(student);
			Linear studentLinear = new Linear();

			List<String> earlyVars = new ArrayList<>();
			List<String> lateVars = new ArrayList<>();

			for (int i = 0; i < projectSets.length; i++) {
				ProjectSet set = projectSets[i];
				String var = studentId + "_" + (i + 1);

				if (set.isSet(ProjectType.FULL)) {
					var = var + "F";

					mainLinear.add(this.getDamage(i) * 2, var);
					studentLinear.add(2, var);

					if (!projectLinears.containsKey(set.getProject(ProjectType.FULL).getId())) {
						projectLinears.put(set.getProject(ProjectType.FULL).getId(), new Linear());
					}

					projectLinears.get(set.getProject(ProjectType.FULL).getId()).add(1, var);
					problem.setVarType(var, VarType.BOOL);
				} else {
					String earlyVar = var + "E";
					String lateVar = var + "L";

					mainLinear.add(this.getDamage(i), earlyVar);
					mainLinear.add(this.getDamage(i), lateVar);
					studentLinear.add(1, earlyVar);
					studentLinear.add(1, lateVar);

					if (!projectLinears.containsKey(set.getProject(ProjectType.EARLY).getId())) {
						projectLinears.put(set.getProject(ProjectType.EARLY).getId(), new Linear());
					}

					if (!projectLinears.containsKey(set.getProject(ProjectType.LATE).getId())) {
						projectLinears.put(set.getProject(ProjectType.LATE).getId(), new Linear());
					}

					projectLinears.get(set.getProject(ProjectType.EARLY).getId()).add(1, earlyVar);
					projectLinears.get(set.getProject(ProjectType.LATE).getId()).add(1, lateVar);
					problem.setVarType(earlyVar, VarType.BOOL);
					problem.setVarType(lateVar, VarType.BOOL);
					
					earlyVars.add(earlyVar);
					lateVars.add(lateVar);
				}
			}

			if (earlyVars.size() > 1) {
				Linear earlyLinear = new Linear();

				for (String var : earlyVars) {
					earlyLinear.add(1, var);
				}

				problem.add(earlyLinear, "<=", 1);
			}

			if (lateVars.size() > 1) {
				Linear lateLinear = new Linear();

				for (String var : lateVars) {
					lateLinear.add(1, var);
				}

				problem.add(lateLinear, "<=", 1);
			}

			studentLinear.add(2, studentId + "_" + "exit");
			mainLinear.add(this.damageNoProject * 2, studentId + "_" + "exit");
			
			problem.setVarType(studentId + "_" + "exit", VarType.BOOL);
			problem.add(studentLinear, "=", 2);
		}

		for (Student student : this.leadEarly.keySet()) {
			int studentId = student.getId();
			ProjectSet[] projectSets = this.leadEarly.get(student);
			Linear studentLinear = new Linear();

			for (int i = 0; i < projectSets.length; i++) {
				ProjectSet set = projectSets[i];
				String var = studentId + "_" + (i + 1) + "L";

				mainLinear.add(this.getDamage(i), var);
				studentLinear.add(1, var);

				if (!projectLinears.containsKey(set.getProject(ProjectType.LATE).getId())) {
					projectLinears.put(set.getProject(ProjectType.LATE).getId(), new Linear());
				}

				projectLinears.get(set.getProject(ProjectType.LATE).getId()).add(1, var);
				problem.setVarType(var, VarType.BOOL);
			}

			studentLinear.add(1, studentId + "_" + "exit");
			mainLinear.add(this.damageNoProject, studentId + "_" + "exit");
			problem.setVarType(studentId + "_" + "exit", VarType.BOOL);
			problem.add(studentLinear, "=", 1);
		}

		for (Student student : this.leadLate.keySet()) {
			int studentId = student.getId();
			ProjectSet[] projectSets = this.leadLate.get(student);
			Linear studentLinear = new Linear();

			for (int i = 0; i < projectSets.length; i++) {
				ProjectSet set = projectSets[i];
				String var = studentId + "_" + (i + 1) + "E";

				mainLinear.add(this.getDamage(i), var);
				studentLinear.add(1, var);

				if (!projectLinears.containsKey(set.getProject(ProjectType.EARLY).getId())) {
					projectLinears.put(set.getProject(ProjectType.EARLY).getId(), new Linear());
				}

				projectLinears.get(set.getProject(ProjectType.EARLY).getId()).add(1, var);
				problem.setVarType(var, VarType.BOOL);
			}

			studentLinear.add(1, studentId + "_" + "exit");
			mainLinear.add(this.damageNoProject, studentId + "_" + "exit");
			problem.setVarType(studentId + "_" + "exit", VarType.BOOL);
			problem.add(studentLinear, "=", 1);
		}

		problem.setObjective(mainLinear, OptType.MIN);

		for (int projectId : projectLinears.keySet()) {
			problem.add(projectLinears.get(projectId), "<=", this.projects.get(projectId).getValue(Project.MAX_STUDENTS));
		}

		long timestampSolving = System.currentTimeMillis();

		Result result = factory.get().solve(problem);

		long finishedTimestamp = System.currentTimeMillis();

		this.output.accept("\nCalculating stats:");

		Map<Integer, Integer> chooseStats = new HashMap<>();
		Map<Project, List<Student>> attandences = new HashMap<>();

		for (Project project : this.projects.values()) {
			attandences.put(project, new ArrayList<>());
		}

		for (int i = 1; i <= 11; i++) {
			chooseStats.put(i, 0);
		}

		for (Student student : this.chooses.keySet()) {
			for (int i = 1; i <= 9; i = i + 3) {
				int rawIndex = (i + 2) / 3;

				for (ProjectType type : ProjectType.values()) {
					Number resultVar = result.get(student.getId() + "_" + rawIndex + type.name().charAt(0));

					if (resultVar != null && resultVar.equals(new Integer(1))) {
						Project project = this.chooses.get(student)[rawIndex - 1].getProject(type);
						attandences.get(project).add(student);

						int index = i + type.getId();
						chooseStats.put(index, chooseStats.get(index) + 1);
					}
				}
			}

			boolean exit = result.get(student.getId() + "_exit").equals(new Integer(1));

			if (exit) {
				chooseStats.put(11, chooseStats.get(11) + 1);
			}
		}
		
		for (Student student : this.leadEarly.keySet()) {
			for (int i = 0; i < 3; i++) {
				Number resultVar = result.get(student.getId() + "_" + (i + 1) + ProjectType.LATE.name().charAt(0));

				if (resultVar != null && resultVar.equals(new Integer(1))) {
					Project project = this.leadEarly.get(student)[i].getProject(ProjectType.LATE);
					attandences.get(project).add(student);

					chooseStats.put(i * 3 + 2, chooseStats.get(i * 3 + 2) + 1);
				}
			}
		}
		
		for (Student student : this.leadLate.keySet()) {
			for (int i = 0; i < 3; i++) {
				Number resultVar = result.get(student.getId() + "_" + (i + 1) + ProjectType.EARLY.name().charAt(0));

				if (resultVar != null && resultVar.equals(new Integer(1))) {
					Project project = this.leadLate.get(student)[i].getProject(ProjectType.EARLY);
					attandences.get(project).add(student);

					chooseStats.put(i * 3 + 1, chooseStats.get(i * 3 + 1) + 1);
				}
			}
		}

		this.output.accept("== Choosing");
		this.output.accept("---- First choice:  E: " + chooseStats.get(1) + "   L: " + chooseStats.get(2) + "   F: " + chooseStats.get(3));
		this.output.accept("---- Second choice: E: " + chooseStats.get(4) + "   L: " + chooseStats.get(5) + "   F: " + chooseStats.get(6));
		this.output.accept("---- Third choice:  E: " + chooseStats.get(7) + "   L: " + chooseStats.get(8) + "   F: " + chooseStats.get(9));
		this.output.accept("---- No project:    H: " + chooseStats.get(10) + "   F:" + chooseStats.get(11));
		this.output.accept("== Timing");
		this.output.accept("---- Preparing: " + (timestampSolving - timestampPreparing) / 1000.0D + " seconds");
		this.output.accept("---- Solving: " + (finishedTimestamp - timestampSolving) / 1000.0D + " seconds");

		this.result = attandences;
		return chooseStats;
	}
	
	public void setOutput(Consumer<String> output) {
		this.output = output;
	}
	
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	public void setDamageFirst(int damageFirst) {
		this.damageFirst = damageFirst;
	}
	
	public void setDamageSecond(int damageSecond) {
		this.damageSecond = damageSecond;
	}
	
	public void setDamageThird(int damageThird) {
		this.damageThird = damageThird;
	}
	
	public void setDamageNoProject(int damageNoProject) {
		this.damageNoProject = damageNoProject;
	}

	public Map<Student, ProjectSet[]> getChooses() {
		return this.chooses;
	}

	public Map<Student, ProjectSet[]> getLeadEarly() {
		return this.leadEarly;
	}

	public Map<Student, ProjectSet[]> getLeadLate() {
		return this.leadLate;
	}

	public Map<Integer, Project> getProjects() {
		return this.projects;
	}
	
	public Map<Project, List<Student>> getResult() {
		return this.result;
	}
	
	private int getDamage(int index) {
		if (index == 0) {
			return this.damageFirst;
		} else if (index == 1) {
			return this.damageSecond;
		} else if (index == 2) {
			return this.damageThird;
		} else {
			return 0;
		}
	}

}
