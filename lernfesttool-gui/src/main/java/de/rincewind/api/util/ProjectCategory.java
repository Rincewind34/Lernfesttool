package de.rincewind.api.util;

import java.util.function.Consumer;

public enum ProjectCategory {
	
	NORMAL(0x00, "Normal", false),
	SPORT(0x01, "Sport", true);
	
	
	private boolean singleChoice;
	
	private byte id;
	
	private String name;
	
	private ProjectCategory(int id, String name, boolean singleChoice) {
		this.id = (byte) id;
		this.name = name;
		this.singleChoice = singleChoice;
	}
	
	public boolean isSingleChoice() {
		return this.singleChoice;
	}
	
	public byte getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static ProjectCategory get(byte id) {
		for (ProjectCategory type : ProjectCategory.values()) {
			if (type.getId() == id) {
				return type;
			}
		}
		
		return null;
	}
	
	public static ProjectCategory get(int id) {
		return ProjectCategory.get((byte) id);
	}
	
	public static void iterateById(Consumer<ProjectCategory> action) {
		for (int i = 0; i < 2; i++) {
			action.accept(ProjectCategory.get(i));
		}
	}

	
}
