package de.rincewind.sql.connections;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

import de.rincewind.sql.abstracts.AbstractConnection;

public class SQLiteConnection extends AbstractConnection {
	
	private File file;
	
	public SQLiteConnection(String path) {
		this(new File(path));
	}
	
	public SQLiteConnection(File file) {
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		
		if (file.exists() && !file.isFile()) {
			throw new RuntimeException("The file cannot be a directory!");
		}
		
		this.file = file;
	}
	
	@Override
	public void open() {
		if (this.isOpen()) {
			return;
		}
		
		String url = "jdbc:sqlite:" + this.file.getAbsolutePath();	
		
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException("No driver found!");
		}
		
		try {
			this.setConnection(DriverManager.getConnection(url));
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public File getFile() {
		return this.file;
	}
	
}
