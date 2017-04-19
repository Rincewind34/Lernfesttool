package de.rincewind.api;

import de.rincewind.api.abstracts.Dataset;
import de.rincewind.api.util.GuideType;
import de.rincewind.api.util.ProjectSet;
import de.rincewind.sql.SQLRequest;
import de.rincewind.sql.tables.relations.TableProjectHelping;

public abstract class Guide extends Dataset {

	public Guide(int guideId) {
		super(guideId);
	}
	
	public abstract String getName();
	
	public abstract GuideType getType();
	
	@Override
	public SQLRequest<Void> delete() {
		return () -> {
			super.delete().sync();
			TableProjectHelping.instance().clearGuide(this.getId(), this.getType().getId());
			return null;
		};
	}
	
	public SQLRequest<ProjectSet> getLeadingProjects() {
		return () -> {
			ProjectSet projectSet = new ProjectSet();
			projectSet.fetch(this).sync();
			return projectSet;
		};
	}
}
