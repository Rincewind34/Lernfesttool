package de.rincewind.api.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.rincewind.api.Project;
import de.rincewind.api.Room;
import de.rincewind.api.Student;
import de.rincewind.api.abstracts.DatasetFieldAccessor;
import de.rincewind.api.abstracts.DatasetManager;
import de.rincewind.api.util.ProjectType;
import de.rincewind.gui.dialogs.DialogSelector;
import de.rincewind.gui.panes.abstracts.FXMLPane;
import de.rincewind.gui.panes.editors.PaneProjectEditor;
import de.rincewind.gui.panes.selectors.PaneProjectSelector;
import de.rincewind.gui.util.TabHandler;
import de.rincewind.sql.SQLRequest;
import de.rincewind.sql.tables.entities.TableProjects;
import de.rincewind.sql.tables.relations.TableProjectAttandences;

public class ProjectManager extends DatasetManager {

	private static ProjectManager instance;

	static {
		ProjectManager.instance = new ProjectManager();
	}

	public static ProjectManager instance() {
		return ProjectManager.instance;
	}

	private ProjectManager() {

	}

	@Override
	public String getDataName() {
		return "Projekt";
	}

	@Override
	public Project newEmptyObject(int datasetId) {
		return new Project(datasetId);
	}

	@Override
	public TableProjects getTable() {
		return TableProjects.instance();
	}

	@Override
	public PaneProjectSelector createSelectorPane() {
		return FXMLPane.setup(new PaneProjectSelector());
	}
	
	@Override
	public PaneProjectEditor createEditorPane(TabHandler handler, int projectId) {
		return FXMLPane.setup(new PaneProjectEditor(handler, projectId));
	}

	@Override
	public List<DatasetFieldAccessor<?>> fieldAccessors() {
		return Arrays.asList(Project.CATEGORY, Project.COSTS, Project.DESCRIPTION, Project.MAX_CLASS, Project.MAX_STUDENTS, Project.MIN_CLASS,
				Project.MIN_STUDENTS, Project.NAME, Project.NOTES, Project.REQUEST_E_BOARD, Project.REQUEST_HARDWARE, Project.REQUEST_MUSICS,
				Project.REQUEST_SPORTS, Project.ROOM, Project.TYPE);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public SQLRequest<List<Project>> getAllDatasets() {
		return this.getAllDatasets(Project.class);
	}
	
	public SQLRequest<List<Project>> getAllDatasets(Map<Integer, Room> rooms) {
		return () -> {
			List<Project> projects = this.getAllDatasets().sync();

			for (Project project : projects) {
				if (project.isRoomSelected()) {
					project.getValue(Project.ROOM).loadFrom(rooms.get(project.getValue(Project.ROOM).getId()));
				}
			}

			return projects;
		};
	}
	
	public PaneProjectSelector createSelectorPane(ProjectType locked) {
		PaneProjectSelector pane = this.createSelectorPane();
		pane.lockType(locked);
		return pane;
	}
	
	public DialogSelector dialogSelector(ProjectType type) {
		PaneProjectSelector fxmlPane = this.createSelectorPane();
		fxmlPane.lockType(type);
		return new DialogSelector(this, fxmlPane);
	}
	
	public SQLRequest<Void> clearAttandences() {
		return TableProjectAttandences.instance().clearAll(false);
	}
	
	public SQLRequest<Void> addAttandences(Map<Project, List<Student>> matching) {
		return () -> {
			List<TableProjectAttandences.Entry> entries = new ArrayList<>();
			
			for (Project project : matching.keySet()) {
				for (Student student : matching.get(project)) {
					entries.add(new TableProjectAttandences.Entry(project.getId(), student.getId(), false));
				}
			}
			
			TableProjectAttandences.instance().addAll(entries).sync();
			return null;
		};
	}

}
