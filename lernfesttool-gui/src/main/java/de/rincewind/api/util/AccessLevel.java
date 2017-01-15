package de.rincewind.api.util;


public enum AccessLevel {

	USER(0x00, "Schüler"),
	ADMIN(0x01, "Administrator");
	
	private byte id;
	
	private String name;
	
	
	private AccessLevel(int id, String name) {
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
		return AccessLevel.get(id) != null;
	}
	
	public static AccessLevel get(byte id) {
		for (AccessLevel gender : AccessLevel.values()) {
			if (gender.getId() == id) {
				return gender;
			}
		}
		
		return null;
	}
	
	public static AccessLevel get(int id) {
		return AccessLevel.get((byte) id);
	}
	
	public static AccessLevel[] sortedValues() {
		AccessLevel[] result = new AccessLevel[AccessLevel.values().length];
		
		byte i = 0;
		
		while (AccessLevel.containsId(i)) {
			result[i] = AccessLevel.get(i);
			i = (byte) (i + 1);
		}
		
		return result;
	}
	
}
