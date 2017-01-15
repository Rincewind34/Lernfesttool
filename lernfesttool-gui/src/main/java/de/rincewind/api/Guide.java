package de.rincewind.api;

import de.rincewind.api.abstracts.Dataset;
import de.rincewind.api.util.GuideType;
import de.rincewind.api.util.ProjectSet;
import de.rincewind.sql.SQLRequest;

public abstract class Guide extends Dataset {

	public Guide(int guideId) {
		super(guideId);
	}
	
	public abstract GuideType getType();
	
	public SQLRequest<ProjectSet> getLeadingProjects() {
		return () -> {
			ProjectSet projectSet = new ProjectSet();
			projectSet.fetch(this).sync();
			return projectSet;
		};
	}
}
