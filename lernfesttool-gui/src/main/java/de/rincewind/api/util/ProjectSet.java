package de.rincewind.api.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.rincewind.api.Guide;
import de.rincewind.api.Project;
import de.rincewind.api.Student;
import de.rincewind.api.abstracts.Dataset;
import de.rincewind.sql.SQLRequest;
import de.rincewind.sql.tables.relations.TableProjectAttandence;
import de.rincewind.sql.tables.relations.TableProjectHelping;
import de.rincewind.sql.tables.relations.TableProjectLeading;

public class ProjectSet implements Iterable<Entry<ProjectType, Project>> {
	
	private FetchType fetchType;
	
	private Map<ProjectType, Project> projects;
	private Map<ProjectType, Boolean> leads;
	
	public ProjectSet() {
		this.projects = new HashMap<>();
		this.leads = new HashMap<>();
	}
	
	@Override
	public Iterator<Entry<ProjectType, Project>> iterator() {
		return this.projects.entrySet().iterator();
	}
	
	public void setProject(ProjectType type, Project value) {
		this.projects.put(type, value);
		
		if (type == ProjectType.FULL) {
			this.clear(ProjectType.EARLY);
			this.clear(ProjectType.LATE);
		} else {
			this.clear(ProjectType.FULL);
		}
	}
	
	public void setLeading(ProjectType type, boolean value) {
		if (!this.projects.containsKey(type)) {
			return;
		}
		
		this.leads.put(type, value);
	}
	
	public void clear(ProjectType type) {
		this.projects.remove(type);
		this.leads.remove(type);
	}
	
	public boolean isSet(ProjectType type) {
		return this.projects.containsKey(type);
	}
	
	public boolean isLeading(ProjectType type) {
		return this.leads.get(type);
	}
	
	public Project getProject(ProjectType type) {
		return this.projects.get(type);
	}
	
	public Dataset getConcerningDataset() {
		return this.fetchType.getDataset();
	}
	
	public Map<ProjectType, Project> asMap() {
		return Collections.unmodifiableMap(this.projects);
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
				this.fetchType.createAddRequest(this.projects.get(type).getId(), this.leads.get(type)).sync();
			}
			
			return null;
		};
	}
	
	private SQLRequest<Void> fetch(FetchType fetchType) {
		return () -> {
			this.projects.clear();
			Map<Integer, Boolean> projectIds = fetchType.createRequestProjects().sync();
			
			for (Integer projectId : projectIds.keySet()) {
				Project project = Project.getManager().newEmptyObject(projectId);
				project.fetchAll().sync();
				
				if (this.projects.containsKey(project.getValue(Project.TYPE))) {
					System.out.println("Already inserted project while fetching project set!"); // TODO
				}
					
				this.projects.put(project.getValue(Project.TYPE), project);
				this.leads.put(project.getValue(Project.TYPE), projectIds.get(projectId));
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
					List<Integer> projectIds = TableProjectAttandence.instance().getProjects(this.dataset.getId()).sync();
					
					for (Integer projectId : projectIds) {
						result.put(projectId, false);
					}
					
					projectIds = TableProjectLeading.instance().getProjects(this.dataset.getId()).sync();
					
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
					TableProjectAttandence.instance().clearStudent(this.dataset.getId());
					TableProjectLeading.instance().clearStudent(this.dataset.getId());
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
				if (leading) {
					return TableProjectLeading.instance().add(projectId, this.dataset.getId());
				} else {
					return TableProjectAttandence.instance().add(projectId, this.dataset.getId());
				}
			} else {
				return null;
			}
		}
		
	}
	
}
