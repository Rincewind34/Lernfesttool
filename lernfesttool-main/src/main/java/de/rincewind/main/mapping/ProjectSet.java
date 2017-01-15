package de.rincewind.main.mapping;

import de.rincewind.api.util.ProjectType;

public class ProjectSet {
	
	private int projectEarly;
	private int projectLate;
	private int projectFull;
	
	public ProjectSet() {
		this(-1, -1, -1);
	}
	
	public ProjectSet(int projectEarly, int projectLate, int projectFull) {
		this.projectEarly = projectEarly;
		this.projectLate = projectLate;
		this.projectFull = projectFull;
	}
	
	public void setProjectEarly(int projectEarly) {
		this.projectEarly = projectEarly;
	}
	
	public void setProjectFull(int projectFull) {
		this.projectFull = projectFull;
	}
	
	public void setProjectLate(int projectLate) {
		this.projectLate = projectLate;
	}
	
	public void setProject(ProjectType type, int projectId) {
		if (type == ProjectType.EARLY) {
			this.projectEarly = projectId;
		} else if (type == ProjectType.LATE) {
			this.projectLate = projectId;
		} else if (type == ProjectType.FULL) {
			this.projectFull = projectId;
		}
	}
	
	public int getProjectEarly() {
		return this.projectEarly;
	}
	
	public int getProjectFull() {
		return this.projectFull;
	}
	
	public int getProjectLate() {
		return this.projectLate;
	}
	
	public int getProject(ProjectType type) {
		if (type == ProjectType.EARLY) {
			return this.projectEarly;
		} else if (type == ProjectType.LATE) {
			return this.projectLate;
		} else if (type == ProjectType.FULL) {
			return this.projectFull;
		} else {
			return -1;
		}
	}
	
}
