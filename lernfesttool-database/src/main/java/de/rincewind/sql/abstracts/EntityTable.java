package de.rincewind.sql.abstracts;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.rincewind.sql.DatabaseConnection;
import de.rincewind.sql.SQLRequest;
import de.rincewind.sql.util.DatabaseUtils;
import de.rincewind.sql.util.SQLResult;

public abstract class EntityTable extends AbstractTable {
	
	private String primaryKey;
	
	public EntityTable(String name, String primaryKey) {
		super(name);
		
		this.primaryKey = primaryKey;
	}
	
	public abstract SQLRequest<Integer> insertEmptyDataset();
	
	public String getPrimaryKey() {
		return this.primaryKey;
	}
	
	public SQLRequest<Void> delete(int datasetId) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();
			
			PreparedStatement stmt = connection.prepare(this.format("DELETE FROM %table WHERE %key = ?"));
			DatabaseUtils.setInt(stmt, 1, datasetId);
			connection.update(stmt);
			
			return null;
		};
	}
	
	public SQLRequest<Boolean> contains(int datasetId) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();
			
			PreparedStatement stmt = connection.prepare(this.format("SELECT COUNT(*) AS total FROM %table WHERE %key = ?"));
			DatabaseUtils.setInt(stmt, 1, datasetId);
			SQLResult result = connection.query(stmt);
			
			boolean exists = false;
			
			if (result.next()) {
				if (result.current("total", int.class) > 0) {
					exists = true;
				}
			}
			
			result.close();
			return exists;
		};
	}
	
	
	public <T> SQLRequest<T> getValue(int datasetId, String column, Class<T> cls) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();
			
			PreparedStatement stmt = connection.prepare(this.format("SELECT %s FROM %table WHERE %key = ?", column));
			DatabaseUtils.setInt(stmt, 1, datasetId);
			SQLResult result = connection.query(stmt);
			
			T value = null;
			
			if (result.next()) {
				value = result.current(column);
			}
			
			result.close();
			return value;
		};
	}
	
	public <T> SQLRequest<List<T>> getColumn(String column) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();
			
			PreparedStatement stmt = connection.prepare(this.format("SELECT %s FROM %table", column));
			SQLResult result = connection.query(stmt);
			
			List<T> values = new ArrayList<>();
			
			while (result.next()) {
				values.addAll(result.current(column));
			}
			
			result.close();
			return values;
		};
	}
	
	public SQLRequest<FieldMap> getValues(int datasetId, String... columns) {
		if (columns.length == 0) {
			throw new RuntimeException("No column given!");
		}
		
		return () -> {
			String selection = columns[0];
			
			for (int i = 1; i < columns.length; i++) {
				selection = selection + ", " + columns[i];
			}
			
			DatabaseConnection connection = this.getDatabase().getConnection();
			
			PreparedStatement stmt = connection.prepare(this.format("SELECT %s FROM %table WHERE %key = ?", selection));
			DatabaseUtils.setInt(stmt, 1, datasetId);
			SQLResult result = connection.query(stmt);
			
			FieldMap values = new FieldMap();
			
			if (result.next()) {
				for (String column : columns) {
					values.set(column, result.current(column));
				}
			}
			
			result.close();
			return values;
		};
	}
	
	public SQLRequest<Map<Integer, FieldMap>> getValues(String... columns) {
		if (columns.length == 0) {
			throw new RuntimeException("No column given!");
		}
		
		return () -> {
			String selection = columns[0];
			
			for (int i = 1; i < columns.length; i++) {
				selection = selection + ", " + columns[i];
			}
			
			DatabaseConnection connection = this.getDatabase().getConnection();
			
			PreparedStatement stmt = connection.prepare(this.format(String.format("SELECT %key, %s FROM %table", selection)));
			SQLResult result = connection.query(stmt);
			
			Map<Integer, FieldMap> values = new HashMap<>();
			
			while (result.next()) {
				FieldMap fields = new FieldMap();
				
				for (String column : columns) {
					fields.set(column, result.current(column));
				}
				
				values.put(result.current(this.primaryKey), fields);
			}
			
			result.close();
			return values;
		};
	}
	
	public SQLRequest<List<Integer>> getEntries() {
		return () -> {
			SQLResult rs = this.getDatabase().getConnection().query(this.format("SELECT %key FROM %table"));
			
			List<Integer> ids = new ArrayList<>();
			
			while (rs.next()) {
				ids.add(rs.current(this.primaryKey, int.class));
			}
			
			rs.close();
			return ids;
		};
	}
	
	protected String format(String sql, Object... args) {
		return String.format(sql.replace("%table", this.getName()).replace("%key", this.primaryKey), args);
	}
	
	public static class FieldMap implements Iterable<Entry<String, Object>> {
		
		private Map<String, Object> values;
		
		public FieldMap() {
			this.values = new HashMap<>();
		}
		
		@Override
		public Iterator<Entry<String, Object>> iterator() {
			return this.values.entrySet().iterator();
		}
		
		public void set(String key, Object value) {
			this.values.put(key, value);
		}
		
		public Set<String> keySet() {
			return this.values.keySet();
		}
		
		public Collection<Object> values() {
			return this.values.values();
		}
		
		@SuppressWarnings("unchecked")
		public <T> T get(String key) {
			return (T) this.values.get(key);
		}

	}
	
}
