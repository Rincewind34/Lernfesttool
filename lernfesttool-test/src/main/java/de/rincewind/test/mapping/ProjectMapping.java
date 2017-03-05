package de.rincewind.test.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.rincewind.api.Project;
import de.rincewind.api.Student;
import de.rincewind.api.util.ProjectSet;
import de.rincewind.api.util.ProjectType;
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
 * Main Linear: MINIMAL = 2 * 1_1F + 4 * 1_2F + 3 * 1_3E + 3 * 1_3L + 10 * 1_exit
 * => Der Faktor wird berechnet, in dem der 'chooseIndex' mit 2 multipliziert wird, wenn es ein halbes Projekt ist
 * 
 * Student: 	2 		= 2 * 1_1F + 2 * 1_2F + 1 * 1_3E + 1 * 1_3L + 1 * 1_exitE + 1 * 1_exitL
 * => Der Faktor ist bei einem ganzen Projekt 2 und bei einem halben 1
 * 
 * Project: 	<maxs> 	=> 1 * 1_2F + 1 * 2_3F + 1 * 3_1F
 * => Der Faktor ist immer 1, da jede Variable die Anwesenheit eines Schülers repräsentiert
 * 
 */
public class ProjectMapping {

	private Map<Integer, Project> projects;

	private Map<Student, ProjectSet[]> chooses;

	public ProjectMapping() {
		this.projects = new HashMap<>();
		this.chooses = new HashMap<>();
	}

	public void fetchData() {
		Map<Integer, Student> students = new HashMap<>();
		
		for (Project project : Project.getManager().getAllDatasets().sync()) {
			this.projects.put(project.getId(), project);
		}
		
		for (Student student : Student.getManager().getAllDatasets().sync()) {
			students.put(student.getId(), student);
		}
		
		this.chooses = Student.getManager().fetchChooses(students, this.projects).sync();
		
		for (Student student : this.chooses.keySet().stream().collect(Collectors.toList())) {
			if (this.chooses.get(student) == null) {
				System.out.println("The student " + student.toString() + " failed to vote (skipping)");
				this.chooses.remove(student);
				continue;
			}
			
			for (ProjectSet set : this.chooses.get(student)) {
				if (!set.isComplete()) {
					System.out.println("The student " + student.toString() + " has an uncomplete vote (skipping)");
					this.chooses.remove(student);
					break;
				}
			}
		}
	}

	public Map<Project, List<Student>> execute() {
		System.out.println("\nStarting solve...");
		System.out.println("Students: " + this.chooses.size());
		System.out.println("Projects: " + this.projects.size());
		
		long timestampPreparing = System.currentTimeMillis();
		
		SolverFactory factory = new SolverFactoryLpSolve();
		factory.setParameter(Solver.VERBOSE, 0);
		factory.setParameter(Solver.TIMEOUT, 10);

		Problem problem = new Problem();
		Linear mainLinear = new Linear();
		
		Map<Integer, Linear> projectLinears = new HashMap<>();

		for (Student student : this.chooses.keySet()) {
			int studentId = student.getId();
			ProjectSet[] projectSets = this.chooses.get(student);
			Linear studentLinear = new Linear();

			for (int i = 0; i < projectSets.length; i++) {
				ProjectSet set = projectSets[i];
				String var = studentId + "_" + (i + 1);

				if (set.isSet(ProjectType.FULL)) {
					var = var + "F";

					mainLinear.add((i * 2) + 1, var);
					studentLinear.add(2, var);

					if (!projectLinears.containsKey(set.getProject(ProjectType.FULL).getId())) {
						projectLinears.put(set.getProject(ProjectType.FULL).getId(), new Linear());
					}

					projectLinears.get(set.getProject(ProjectType.FULL).getId()).add(1, var);
					problem.setVarType(var, VarType.BOOL);
				} else {
					mainLinear.add(i + 1, var + "E");
					mainLinear.add(i + 1, var + "L");
					studentLinear.add(1, var + "E");
					studentLinear.add(1, var + "L");

					if (!projectLinears.containsKey(set.getProject(ProjectType.EARLY).getId())) {
						projectLinears.put(set.getProject(ProjectType.EARLY).getId(), new Linear());
					}

					if (!projectLinears.containsKey(set.getProject(ProjectType.LATE).getId())) {
						projectLinears.put(set.getProject(ProjectType.LATE).getId(), new Linear());
					}

					projectLinears.get(set.getProject(ProjectType.EARLY).getId()).add(1, var + "E");
					projectLinears.get(set.getProject(ProjectType.LATE).getId()).add(1, var + "L");
					problem.setVarType(var + "E", VarType.BOOL);
					problem.setVarType(var + "L", VarType.BOOL);
				}
			}
			
			int exitWeight = 10;
			
			studentLinear.add(1, studentId + "_" + "exitE");
			studentLinear.add(1, studentId + "_" + "exitL");
			mainLinear.add((int) exitWeight / 2.0, studentId + "_" + "exitE");
			mainLinear.add((int) exitWeight / 2.0, studentId + "_" + "exitL");
			problem.setVarType(studentId + "_" + "exitE", VarType.BOOL);
			problem.setVarType(studentId + "_" + "exitL", VarType.BOOL);
			problem.add(studentLinear, "=", 2);
		}

		problem.setObjective(mainLinear, OptType.MIN);

		for (int projectId : projectLinears.keySet()) {
			problem.add(projectLinears.get(projectId), "<=", this.projects.get(projectId).getValue(Project.MAX_STUDENTS));
		}
		
		long timestampSolving = System.currentTimeMillis();

		Result result = factory.get().solve(problem);
		
		long finishedTimestamp = System.currentTimeMillis();
		
		System.out.println("\nCalculating stats:");
		
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
				for (ProjectType type : ProjectType.values()) {
					Number resultVar = result.get(student.getId() + "_" + i + type.name().charAt(0));
					
					if (resultVar != null && resultVar.equals(new Integer(1))) {
						Project project = this.chooses.get(student)[i - 1].getProject(type);
						attandences.get(project).add(student);
						
						int index = i + type.getId();
						chooseStats.put(index, chooseStats.get(index) + 1);
					}
				}
			}
			
			boolean exitEarly = result.get(student.getId() + "_exitE").equals(new Integer(1));
			boolean exitLate = result.get(student.getId() + "_exitL").equals(new Integer(1));
			
			if (exitEarly ^ exitLate) {
				chooseStats.put(10, chooseStats.get(10) + 1);
			}
			
			if (exitEarly && exitLate) {
				chooseStats.put(11, chooseStats.get(11) + 1);
			}
		}
		
		System.out.println("== Choosing");
		System.out.println("---- First choice:  E: " + chooseStats.get(1) + "   L: " + chooseStats.get(2) + "   F: " + chooseStats.get(3));
		System.out.println("---- Second choice: E: " + chooseStats.get(4) + "   L: " + chooseStats.get(5) + "   F: " + chooseStats.get(6));
		System.out.println("---- Third choice:  E: " + chooseStats.get(7) + "   L: " + chooseStats.get(8) + "   F: " + chooseStats.get(9));
		System.out.println("---- No project:    H: " + chooseStats.get(10) + "   F:" + chooseStats.get(11));
		System.out.println("== Timing");
		System.out.println("---- Preparing: " + (timestampSolving - timestampPreparing) / 1000.0D + " seconds");
		System.out.println("---- Solving: " + (finishedTimestamp - timestampSolving) / 1000.0D + " seconds");
		
		return attandences;
	}
	
}
