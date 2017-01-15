package de.rincewind.api.util;

import java.util.function.Consumer;

public enum StudentState {
	
	ENTER_PROJECTS(0x00, "Projekte eingeben"),
	LOOKUP_PROJECTS(0x01, "Projekte einsehen"),
	VOTE_PROJECTS(0x02, "Projekte wählen"),
	SEC_VOTE_PROJECTS(0x03, "Projekte nachwählen");
	
	private byte id;
	
	private String name;
	
	private StudentState(int id, String name) {
		this.id = (byte) id;
		this.name = name;
	}
	
	public byte getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	
	public static boolean containsId(byte id) {
		return StudentState.get(id) != null;
	}
	
	public static StudentState get(byte id) {
		for (StudentState type : StudentState.values()) {
			if (type.getId() == id) {
				return type;
			}
		}
		
		return null;
	}
	
	public static StudentState get(int id) {
		return StudentState.get((byte) id);
	}
	
	public static void iterateById(Consumer<StudentState> action) {
		for (int i = 0; i < 4; i++) {
			action.accept(StudentState.get(i));
		}
	}
	
	public static StudentState[] sortedValues() {
		StudentState[] result = new StudentState[StudentState.values().length];
		
		byte i = 0;
		
		while (StudentState.containsId(i)) {
			result[i] = StudentState.get(i);
			i = (byte) (i + 1);
		}
		
		return result;
	}
	
	
}
