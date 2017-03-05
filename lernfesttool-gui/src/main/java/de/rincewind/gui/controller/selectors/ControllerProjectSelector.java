package de.rincewind.gui.controller.selectors;

import java.util.List;
import java.util.stream.Collectors;

import de.rincewind.api.Project;
import de.rincewind.api.util.ProjectType;
import de.rincewind.gui.controller.abstracts.ControllerSelector;
import de.rincewind.gui.util.Cell;
import de.rincewind.gui.util.filler.BoxCheck;
import de.rincewind.gui.util.filler.ListFiller;
import de.rincewind.gui.util.filler.SearchCheck;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ControllerProjectSelector extends ControllerSelector<Project> {
	
	@FXML
	private ListView<Cell<Project>> listProjects;
	
	@FXML
	private TextField textSearch;
	
	@FXML
	private ComboBox<Cell<ProjectType>> boxProjectType;
	
	@FXML
	private HBox paneHBox;
	
	@FXML
	private VBox rootBox;
	
	private ListFiller<Cell<Project>> filler;
	
	@Override
	public void init() {
		this.boxProjectType.getItems().add(new Cell<>("Egal", null));
		
		for (ProjectType type : ProjectType.values()) {
			this.boxProjectType.getItems().add(new Cell<>(type.getName(), type));
		}
		
		this.boxProjectType.getSelectionModel().select(0);
	}
	
	@Override
	public void setupList(List<Project> datasets) {
		List<Cell<Project>> elements = datasets.stream().map((project) -> {
			return project.asCell(Project.class);
		}).sorted().collect(Collectors.toList());
		
		this.filler = new ListFiller<>(this.listProjects, elements);
		this.filler.addChecker(new SearchCheck<>(this.textSearch));
		this.filler.addChecker(new BoxCheck<>(this.boxProjectType, (value, boxEntry) -> {
			if (boxEntry.getSavedObject() == null) {
				return true;
			} else {
				return boxEntry.getSavedObject() == value.getSavedObject().getValue(Project.TYPE);
			}
		}));
		this.filler.refresh();
	}
	
	@Override
	public ListView<Cell<Project>> getListValues() {
		return this.listProjects;
	}
	
	public HBox getHbox() {
		return this.paneHBox;
	}
	
	public VBox getRootBox() {
		return this.rootBox;
	}
	
	public ComboBox<Cell<ProjectType>> getBoxProjectType() {
		return this.boxProjectType;
	}
	
	public TextField getTextSearch() {
		return this.textSearch;
	}
	
}
