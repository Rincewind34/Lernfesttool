package de.rincewind.gui.controller.editors;

import de.rincewind.api.Room;
import de.rincewind.gui.controller.abstracts.ControllerEditor;
import de.rincewind.gui.util.TabHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public class ControllerRoomEditor extends ControllerEditor {
	
	@FXML
	private TextField textName;
	
	@FXML
	private TextField textSize; // this text field is disabled by default
	
	@FXML
	private CheckBox checkEBoard;
	
	@FXML
	private CheckBox checkHardware;
	
	@FXML
	private CheckBox checkSports;
	
	@FXML
	private CheckBox checkMusics;
	
	@FXML
	private Slider sliderSize;
	
	private Room room;
	
	public ControllerRoomEditor(TabHandler handler, int roomId) {
		super(handler);
		
		this.room = Room.getManager().newEmptyObject(roomId);
	}
	
	@Override
	public void init() {
		this.room.fetchAll().sync();
		
		// === Building === //
		
		this.getSaveHandler().addValue(this.textName.textProperty());
		this.getSaveHandler().addValue(this.checkEBoard.selectedProperty());
		this.getSaveHandler().addValue(this.checkSports.selectedProperty());
		this.getSaveHandler().addValue(this.checkMusics.selectedProperty());
		this.getSaveHandler().addValue(this.checkHardware.selectedProperty());
		this.getSaveHandler().addValue(this.sliderSize.valueProperty());
		
		// === Building === //
		// === Inserting === //
		
		this.textName.setText(this.room.getValue(Room.NAME));
		
		this.checkEBoard.setSelected(this.room.getValue(Room.E_BOARD));
		this.checkHardware.setSelected(this.room.getValue(Room.HARDWARE));
		this.checkSports.setSelected(this.room.getValue(Room.SPORTS));
		this.checkMusics.setSelected(this.room.getValue(Room.MUSICS));
		
		int size = this.room.getValue(Room.SIZE);
		
		this.textSize.setText(Integer.toString(size));
		this.sliderSize.setValue(size);
		
		// === Inserting === //
		// === Listening === //
		
		this.sliderSize.valueProperty().addListener((observeable, oldValue, newValue) -> {
			this.textSize.setText(Integer.toString(newValue.intValue()));
		});
		
		// === Listening === //
		
		this.getSaveHandler().reset();
	}
	
	@Override
	public void saveStages() {
		this.room.setValue(Room.NAME, this.textName.getText());
		this.room.setValue(Room.E_BOARD, this.checkEBoard.isSelected());
		this.room.setValue(Room.HARDWARE, this.checkHardware.isSelected());
		this.room.setValue(Room.SPORTS, this.checkSports.isSelected());
		this.room.setValue(Room.MUSICS, this.checkMusics.isSelected());
		this.room.setValue(Room.SIZE, (byte) this.sliderSize.getValue());
		this.room.save().async((result) -> {
			
		}, (exception) -> {
			// TODO
		});
	}
	
	@Override
	public Room getEditingObject() {
		return this.room;
	}
	
	public TextField getTextName() {
		return this.textName;
	}

}
