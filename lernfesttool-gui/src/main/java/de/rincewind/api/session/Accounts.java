package de.rincewind.api.session;

import java.util.function.Consumer;

import de.rincewind.api.util.AccessLevel;

public class Accounts {
	
	public static final Account ADMIN = new Account("leitung", "lernfest2017", AccessLevel.ADMIN);
	
	public static void iterate(Consumer<Account> action) {
		action.accept(Accounts.ADMIN);
	}
	
	
	public static class Account {
		
		private String username;
		private String password;
		
		private AccessLevel level;
		
		private Account(String username, String password, AccessLevel level) {
			this.username = username;
			this.password = password;
			this.level = level;
		}
		
		public String getUsername() {
			return this.username;
		}
		
		public AccessLevel getAccessLevel() {
			return this.level;
		}
		
		public boolean verify(String password) {
			return this.password.equals(password);
		}
		
	}
	
}
