package de.rincewind.api.util;

import java.util.Random;
import java.util.function.Consumer;

public enum ProjectType {
	
	EARLY(0x00, "Früh", "frühes"),
	LATE(0x01, "Spät", "spätes"),
	FULL(0x02, "Ganz", "ganzes");
	
	private byte id;
	
	private String name;
	private String adjective;
	
	private ProjectType(int id, String name, String adjective) {
		this.id = (byte) id;
		this.name = name;
		this.adjective = adjective;
	}
	
	public boolean isHalfTime() {
		return this != ProjectType.FULL;
	}
	
	public byte getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getAdjective() {
		return this.adjective;
	}
	
	public ProjectType invert() {
		if (this == ProjectType.EARLY) {
			return ProjectType.LATE;
		} else if (this == ProjectType.LATE) {
			return ProjectType.EARLY;
		} else {
			return ProjectType.FULL;
		}
	}
	
	
	public static ProjectType get(byte id) {
		for (ProjectType type : ProjectType.values()) {
			if (type.getId() == id) {
				return type;
			}
		}
		
		return null;
	}
	
	public static ProjectType get(int id) {
		return ProjectType.get((byte) id);
	}
	
	public static ProjectType random() {
		return ProjectType.random(new Random());
	}
	
	public static ProjectType random(Random random) {
		return ProjectType.get(random.nextInt(ProjectType.values().length));
	}
	
	public static void iterateById(Consumer<ProjectType> action) {
		for (int i = 0; i < 3; i++) {
			action.accept(ProjectType.get(i));
		}
	}
	
}
