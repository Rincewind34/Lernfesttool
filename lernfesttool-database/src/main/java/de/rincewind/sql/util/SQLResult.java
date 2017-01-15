package de.rincewind.sql.util;

import java.io.Closeable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

public class SQLResult implements Closeable {
	
	private ResultSet rs;
	private PreparedStatement stmt;
	
	public SQLResult(ResultSet rs, PreparedStatement stmt) {
		this.rs = rs;
		this.stmt = stmt;
	}
	
	@Override
	public void close() {
		try {
			this.rs.close();
			this.stmt.close();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public void iterate(Consumer<SQLResult> action) {
		while (this.next()) {
			action.accept(this);
		}
	}
	
	public boolean next() {
		try {
			return this.rs.next();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public boolean first() {
		try {
			return this.rs.first();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public boolean isLast() {
		try {
			return this.rs.isLast();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public Object getObject(String column) {
		try {
			return this.rs.getObject(column);
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public ResultSet getResultSet() {
		return this.rs;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T current(String column) {
		return (T) this.getObject(column);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T current(String column, Class<T> object) {
		return (T) this.getObject(column);
	}
	
}
