package de.rincewind.api.util;

import java.util.function.Consumer;

public enum GuideType {
	
	TEACHER(0, "Lehrer"),
	HELPER(1, "Betreuer");
	
	private byte id;
	
	private String name;
	
	private GuideType(int id, String name) {
		this.id = (byte) id;
		this.name = name;
	}
	
	public byte getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static GuideType get(byte id) {
		for (GuideType type : GuideType.values()) {
			if (type.getId() == id) {
				return type;
			}
		}
		
		return null;
	}
	
	public static GuideType get(int id) {
		return GuideType.get((byte) id);
	}
	
	public static void iterateById(Consumer<GuideType> action) {
		for (int i = 0; i < 2; i++) {
			action.accept(GuideType.get(i));
		}
	}

	
}
