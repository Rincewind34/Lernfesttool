package de.rincewind.gui.dialogs;

import java.awt.print.Printable;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import de.rincewind.api.Project;
import de.rincewind.api.Room;
import de.rincewind.api.SchoolClass;
import de.rincewind.api.Student;
import de.rincewind.api.Teacher;
import de.rincewind.api.abstracts.Dataset;
import de.rincewind.api.util.ProjectSet;
import de.rincewind.api.util.ProjectType;
import de.rincewind.gui.dialogs.DialogPrintJob.SelectedPrintJob;
import de.rincewind.gui.printjobs.ClassStudentList;
import de.rincewind.gui.printjobs.PrintCombiner;
import de.rincewind.gui.printjobs.ProjectStudentList;
import de.rincewind.gui.printjobs.StudentList;
import de.rincewind.gui.util.Cell;
import de.rincewind.sql.tables.relations.TableProjectAttandences;
import de.rincewind.sql.tables.relations.TableProjectChoosing;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.layout.HBox;

public class DialogPrintJob extends Dialog<SelectedPrintJob> {

	public DialogPrintJob() {
		ButtonType sendType = new ButtonType("Drucken", ButtonData.YES);
		ButtonType cancelType = new ButtonType("Abbrechen", ButtonData.NO);

		this.setTitle("Drucken");
		this.setHeaderText("Welcher Druckauftrag soll abgeschickt werden?");
		this.getDialogPane().getButtonTypes().addAll(sendType, cancelType);

		Node button = this.getDialogPane().lookupButton(sendType);
		button.setDisable(true);

		ComboBox<Cell<SelectedPrintJob>> selectBox = new ComboBox<>();
		selectBox.setPromptText("Druckauftrag auswählen");
		selectBox.setMinSize(400, 30);
		selectBox.setMaxSize(400, 30);

		for (SelectedPrintJob job : SelectedPrintJob.values()) {
			selectBox.getItems().add(new Cell<>(job.getName(), job));
		}

		selectBox.getSelectionModel().selectedItemProperty().addListener((observeable, oldValue, newValue) -> {
			button.setDisable(newValue == null);
		});

		HBox pane = new HBox(selectBox);
		pane.setAlignment(Pos.CENTER);
		this.getDialogPane().setContent(pane);

		this.setResultConverter((clickedType) -> {
			if (clickedType == cancelType) {
				return null;
			} else {
				return selectBox.getSelectionModel().getSelectedItem().getSavedObject();
			}
		});
	}

	public static enum SelectedPrintJob {

		PROJECTS("Alle Projekte", () -> {
			return new PrintCombiner(Project.getManager().getAllDatasets().sync());
		}), LIST_STUDENTS_PROJECTS("Teilnehmerlisten (Projekt)", () -> {
			return new PrintCombiner(Project.getManager().getAllDatasets().sync().stream().map((project) -> {
				return new ProjectStudentList(project);
			}).collect(Collectors.toList()));
		}), LIST_STUDENTS_CLASSES("Teilnehmerlisten (Klassen)", () -> {
			Map<Integer, ProjectSet> attandences = ProjectSet.convertAttandences(TableProjectAttandences.instance().getEntries().sync(),
					Dataset.convertList(Project.getManager().getAllDatasets(Dataset.convertList(Room.getManager().getAllDatasets().sync())).sync()));

			return new PrintCombiner(SchoolClass.getManager().getAllDatasets(Dataset.convertList(Room.getManager().getAllDatasets().sync()),
					Dataset.convertList(Teacher.getManager().getAllDatasets().sync())).sync().stream().map((schoolClass) -> {
						return new ClassStudentList(schoolClass, attandences);
					}).collect(Collectors.toList()));
		}), STUDENTS_NO_CHOICE("Schüler ohne Wahl", () -> {
			Map<Integer, Project> projectMap = Dataset.convertList(Project.getManager().getAllDatasets().sync());

			return new StudentList(
					Student.getManager()
							.getWithoutChoice(
									Dataset.convertList(Student.getManager()
											.getAllDatasets(Dataset.convertList(SchoolClass.getManager()
													.getAllDatasets(Dataset.convertList(Room.getManager().getAllDatasets().sync())).sync()))
											.sync()),
									ProjectSet.convertChooses(TableProjectChoosing.instance().getEntries().sync(), projectMap),
									ProjectSet.convertLeadings(TableProjectAttandences.instance().getEntries().sync(), projectMap)));
		}), STUDENTS_NO_FULL_PROJECT("Schüler ohne Projekt (ganztägig)", () -> {
			Map<Integer, Project> projectMap = Dataset.convertList(Project.getManager().getAllDatasets().sync());

			return new StudentList(
					Student.getManager()
							.getWithoutProject(
									Dataset.convertList(Student.getManager()
											.getAllDatasets(Dataset.convertList(SchoolClass.getManager()
													.getAllDatasets(Dataset.convertList(Room.getManager().getAllDatasets().sync())).sync()))
											.sync()),
									ProjectSet.convertAttandences(TableProjectAttandences.instance().getEntries().sync(), projectMap)));
		}), STUDENTS_NO_EARLY_PROJECT("Schüler ohne Projekt (früh)", () -> {
			Map<Integer, Project> projectMap = Dataset.convertList(Project.getManager().getAllDatasets().sync());

			return new StudentList(
					Student.getManager()
							.getWithoutHalfProject(
									Dataset.convertList(Student.getManager()
											.getAllDatasets(Dataset.convertList(SchoolClass.getManager()
													.getAllDatasets(Dataset.convertList(Room.getManager().getAllDatasets().sync())).sync()))
											.sync()),
									ProjectSet.convertAttandences(TableProjectAttandences.instance().getEntries().sync(), projectMap), ProjectType.EARLY));
		}), STUDENTS_NO_LATE_PROJECT("Schüler ohne Projekt (spät)", () -> {
			Map<Integer, Project> projectMap = Dataset.convertList(Project.getManager().getAllDatasets().sync());

			return new StudentList(
					Student.getManager()
							.getWithoutHalfProject(
									Dataset.convertList(Student.getManager()
											.getAllDatasets(Dataset.convertList(SchoolClass.getManager()
													.getAllDatasets(Dataset.convertList(Room.getManager().getAllDatasets().sync())).sync()))
											.sync()),
									ProjectSet.convertAttandences(TableProjectAttandences.instance().getEntries().sync(), projectMap), ProjectType.LATE));
		});

		private String name;
		private Supplier<Printable> printable;

		private SelectedPrintJob(String name, Supplier<Printable> printable) {
			this.printable = printable;
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public Supplier<Printable> getPrintable() {
			return this.printable;
		}

	}

}