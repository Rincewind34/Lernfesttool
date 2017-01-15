package de.rincewind.sql.connections;

import java.sql.DriverManager;
import java.sql.SQLException;

import de.rincewind.sql.abstracts.AbstractConnection;

public class MySQLConnection extends AbstractConnection {

	private String host;
	private String database;
	private String username;
	private String password;
	
	private int port;
	
	public MySQLConnection(String host, int port, String database, String username, String password) {
		this.host = host;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
	}
	
	@Override
	public void open() {
		if (this.isOpen()) {
			return;
		}
		
		String url = "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException("Could not find mysql-driver (com.mysql.jdbc.Driver)");
		}
		
		try {
			this.setConnection(DriverManager.getConnection(url, this.username, this.password));
		} catch (SQLException ex) {
			this.setConnection(null); // Make sure, that the connection is null. Otherwise, the DatabaseConnection#isOpen() could return wrong values.
			throw new RuntimeException(ex);
		}
	}

}
