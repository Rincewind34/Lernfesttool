package de.rincewind.api.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import de.rincewind.api.Guide;
import de.rincewind.api.Project;
import de.rincewind.api.Student;
import de.rincewind.api.abstracts.Dataset;
import de.rincewind.sql.SQLRequest;
import de.rincewind.sql.tables.relations.TableProjectAttandences;
import de.rincewind.sql.tables.relations.TableProjectHelping;

public class ProjectSet implements Iterable<Entry<ProjectType, Project>> {
	
	private FetchType fetchType;
	
	private Map<ProjectType, Project> projects;
	private List<ProjectType> leads;
	
	public ProjectSet() {
		this.projects = new HashMap<>();
		this.leads = new ArrayList<>();
	}
	
	@Override
	public Iterator<Entry<ProjectType, Project>> iterator() {
		return this.projects.entrySet().iterator();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		
		for (ProjectType type : ProjectType.values()) {
			if (this.isSet(type)) {
				builder.append(this.isLeading(type) ? type.name().charAt(0) : Character.toString(type.name().charAt(0)).toLowerCase());
				builder.append(":");
				builder.append(this.getProject(ProjectType.FULL).getId());
				builder.append(";");
			}
		}
		
		if (builder.length() > 1) {
			builder.setLength(builder.length() - 1);
		}
		
		builder.append("]");
		return builder.toString();
	}
	
	public void setProject(Project value) {
		this.projects.put(value.getValue(Project.TYPE), value);
		
		if (value.getValue(Project.TYPE) == ProjectType.FULL) {
			this.clear(ProjectType.EARLY);
			this.clear(ProjectType.LATE);
		} else {
			this.clear(ProjectType.FULL);
		}
	}
	
	public void setLeading(ProjectType type, boolean value) {
		if (value) {
			this.leads.add(type);
		} else {
			this.leads.remove(type);
		}
	}
	
	public void clear(ProjectType type) {
		this.projects.remove(type);
		this.leads.remove(type);
	}
	
	public void clear(Project project) {
		for (ProjectType type : ProjectType.values()) {
			if (this.isSet(type) && this.getProject(type).getId() == project.getId()) {
				this.clear(type);
			}
		}
	}
	
	public boolean isSet(ProjectType type) {
		return this.projects.containsKey(type);
	}
	
	public boolean isLeading(ProjectType type) {
		return this.leads.contains(type);
	}
	
	public boolean isComplete() {
		return this.isSet(ProjectType.FULL) || (this.isSet(ProjectType.EARLY) && this.isSet(ProjectType.LATE));
	}
	
	
	public int leadingAmount() {
		return this.leads.size();
	}
	
	public Project getFirstLeadingOne() {
		for (ProjectType type : ProjectType.values()) {
			if (this.isLeading(type)) {
				return this.getProject(type);
			}
		}
		
		return null;
	}
	
	public Project getProject(ProjectType type) {
		return this.projects.get(type);
	}
	
	public Dataset getConcerningDataset() {
		return this.fetchType.getDataset();
	}
	
	public List<Project> projects() {
		return Collections.unmodifiableList(this.projects.values().stream().collect(Collectors.toList()));
	}
	
	public List<Project> leadingProjects() {
		return Collections.unmodifiableList(this.projects.values().stream().filter((project) -> {
			return this.isLeading(project.getValue(Project.TYPE));
		}).collect(Collectors.toList()));
	}
	
	public SQLRequest<Void> fetch(Student student) {
		return this.fetch(new FetchType(student));
	}
	
	public SQLRequest<Void> fetch(Guide guide) {
		return this.fetch(new FetchType(guide));
	}
	
	public SQLRequest<Void> save() {
		return () -> {
			this.fetchType.createClearRequest().sync();
			
			for (ProjectType type : this.projects.keySet()) {
				this.fetchType.createAddRequest(this.projects.get(type).getId(), this.isLeading(type)).sync();
			}
			
			return null;
		};
	}
	
	private SQLRequest<Void> fetch(FetchType fetchType) {
		this.fetchType = fetchType;
		
		return () -> {
			this.projects.clear();
			Map<Integer, Boolean> projectIds = this.fetchType.createRequestProjects().sync();
			
			for (Integer projectId : projectIds.keySet()) {
				Project project = Project.getManager().newEmptyObject(projectId);
				project.fetchAll().sync();
				
				if (this.projects.containsKey(project.getValue(Project.TYPE))) {
					System.out.println("Already inserted project while fetching project set!"); // TODO
				}
					
				this.projects.put(project.getValue(Project.TYPE), project);
				this.setLeading(project.getValue(Project.TYPE), projectIds.get(projectId));
			}
			
			return null;
		};
	}
	
	private static class FetchType {
		
		private Dataset dataset;
		
		public FetchType(Dataset dataset) {
			this.dataset = dataset;
		}
		
		public Dataset getDataset() {
			return this.dataset;
		}
		
		public SQLRequest<Map<Integer, Boolean>> createRequestProjects() {
			if (this.dataset instanceof Guide) {
				return () -> {
					List<Integer> projectIds = TableProjectHelping.instance().getProjects(this.dataset.getId(), ((Guide) this.dataset).getType().getId()).sync();
					Map<Integer, Boolean> result = new HashMap<>();
					
					for (Integer projectId : projectIds) {
						result.put(projectId, true);
					}
					
					return result;
				};
			} else if (this.dataset instanceof Student) {
				return () -> {
					Map<Integer, Boolean> result = new HashMap<>();
					List<Integer> projectIds = TableProjectAttandences.instance().getProjects(this.dataset.getId(), false).sync();
					
					for (Integer projectId : projectIds) {
						result.put(projectId, false);
					}
					
					projectIds = TableProjectAttandences.instance().getProjects(this.dataset.getId(), true).sync();
					
					for (Integer projectId : projectIds) {
						result.put(projectId, true);
					}
					
					return result;
				};
			} else {
				return null;
			}
		}
		
		public SQLRequest<Void> createClearRequest() {
			if (this.dataset instanceof Guide) {
				return TableProjectHelping.instance().clearGuide(this.dataset.getId(), ((Guide) this.dataset).getType().getId());
			} else if (this.dataset instanceof Student) {
				return () -> {
					TableProjectAttandences.instance().clearStudent(this.dataset.getId(), true);
					TableProjectAttandences.instance().clearStudent(this.dataset.getId(), false);
					return null;
				};
			} else {
				return null;
			}
		}
		
		public SQLRequest<Void> createAddRequest(int projectId, boolean leading) {
			if (this.dataset instanceof Guide) {
				return TableProjectHelping.instance().add(projectId, this.dataset.getId(), ((Guide) this.dataset).getType().getId());
			} else if (this.dataset instanceof Student) {
				return TableProjectAttandences.instance().add(projectId, this.dataset.getId(), leading);
			} else {
				return null;
			}
		}
		
	}
	
}
