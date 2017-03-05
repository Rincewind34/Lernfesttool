package de.rincewind.sql.util;

import java.util.Map.Entry;

public class CustomMapEntry<K, V> implements Entry<K, V> {
	
	private K key;
	private V value;
	
	public CustomMapEntry(K key, V value) {
		this.key = key;
		this.value = value;
	}
	
	@Override
	public K getKey() {
		return this.key;
	}

	@Override
	public V getValue() {
		return this.value;
	}

	@Override
	public V setValue(V value) {
		return this.value;
	}

}
