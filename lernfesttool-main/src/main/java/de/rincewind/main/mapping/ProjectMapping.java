package de.rincewind.main.mapping;

import java.util.HashMap;
import java.util.Map;

import net.sf.javailp.Linear;
import net.sf.javailp.OptType;
import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import net.sf.javailp.Solver;
import net.sf.javailp.SolverFactory;
import net.sf.javailp.SolverFactoryLpSolve;
import net.sf.javailp.VarType;

public class ProjectMapping {
	
//	private Map<Integer, Object[]> students;
	private Map<Integer, Object[]> projects;
	
	private Map<Integer, ProjectSet[]> chooses;
//	private Map<Integer, ProjectSet> attandances;
	
	public ProjectMapping() {
		this.projects = new HashMap<>();
		this.chooses = new HashMap<>();
	}
	
//	public void fetchData() {
//		this.projects = Tables.projects().getData(ProjectDatas.NAME, ProjectDatas.TYPE, ProjectDatas.MAX_STUDENTS).sync();
////		this.students = Tables.students().getData(StudentDatas.FIRST_NAME, StudentDatas.LAST_NAME).sync();
//		
//		Map<Integer, List<Integer[]>> fetchedChooses = Tables.projectchoosing().getChooses().sync();
//		
////		for (int studentId : Tables.projectleading().getStudents().sync()) {
////			// TODO
////		}	
//		
//		for (int studentId : Tables.projectattandance().getStudents().sync()) {
//			fetchedChooses.remove(studentId);
//		}
//		
//		Map<Integer, ProjectSet[]> chooses = new HashMap<>();
//		
//		for (int studentId : fetchedChooses.keySet()) {
//			ProjectSet[] projectSets = new ProjectSet[] { new ProjectSet(), new ProjectSet(), new ProjectSet() };
//			List<Integer[]> choosenProjects = fetchedChooses.get(studentId);
//			
//			for (Integer[] choose : choosenProjects) {
//				int projectId = choose[1];
//				ProjectType projectType = (ProjectType) this.projects.get(projectId)[1];
//				projectSets[choose[0] - 1].setProject(projectType, projectId);
//			}
//		}
//		
//		this.chooses = chooses;
//	}
	
	public static void main(String[] args) {
		System.out.println("STARTING MAPPING");
		ProjectMapping mapping = new ProjectMapping();
		mapping.start();
		System.out.println("FINISHED");
	}
	
	public void start() {
		SolverFactory factory = new SolverFactoryLpSolve();
		factory.setParameter(Solver.VERBOSE, 0);
		factory.setParameter(Solver.TIMEOUT, Integer.MAX_VALUE);
		
		Problem problem = new Problem();
		Linear mainLinear = new Linear();
		
		Map<Integer, Linear> projectLinears = new HashMap<>();
		
		for (int studentId : this.chooses.keySet()) { // Iterate over all students
			ProjectSet[] projectSets = this.chooses.get(studentId);
			Linear studentLinear = new Linear(); // Generate the student linear
			
			for (int i = 0; i < projectSets.length; i++) { // Iterate over all three chooses
				ProjectSet set = projectSets[i];
				String var = Integer.toString(studentId) + "_" + (i + 1);
				
				if (set.getProjectFull() != -1) {
					mainLinear.add((i * 2) + 1, var + "chooseF");
					studentLinear.add(2, var + "chooseF");
					
					if (!projectLinears.containsKey(set.getProjectFull())) {
						projectLinears.put(set.getProjectFull(), new Linear());
					}
					
					projectLinears.get(set.getProjectFull()).add(1, var + "chooseF");
					problem.setVarType(var + "chooseF", VarType.BOOL);
				} else {
					mainLinear.add(i + 1, var + "chooseE");
					mainLinear.add(i + 1, var + "chooseL");
					studentLinear.add(1, var + "chooseE");
					studentLinear.add(1, var + "chooseL");
					
					if (!projectLinears.containsKey(set.getProjectEarly())) {
						projectLinears.put(set.getProjectEarly(), new Linear());
					}
					
					if (!projectLinears.containsKey(set.getProjectLate())) {
						projectLinears.put(set.getProjectLate(), new Linear());
					}
					
					projectLinears.get(set.getProjectEarly()).add(1, var + "chooseE");
					projectLinears.get(set.getProjectLate()).add(1, var + "chooseL");
					problem.setVarType(var + "chooseE", VarType.BOOL);
					problem.setVarType(var + "chooseL", VarType.BOOL);
				}
			}
			
			mainLinear.add(10, Integer.toString(studentId) + "_" + "exit");
			problem.add(studentLinear, "=", 2);
		}
		
		problem.setObjective(mainLinear, OptType.MIN);
		
		for (int projectId : projectLinears.keySet()) {
			problem.add(projectLinears.get(projectId), "<=", (int) this.projects.get(projectId)[2]);
		}
		
		Result result = factory.get().solve(problem);
		System.out.println(result.toString());
	}
	
}
