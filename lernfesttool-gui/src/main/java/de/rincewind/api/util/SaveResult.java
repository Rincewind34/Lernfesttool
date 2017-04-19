package de.rincewind.api.util;

public abstract class SaveResult {
	
	public static SaveResultSuccess success() {
		return new SaveResultSuccess();
	}
	
	public static SaveResultError error(Exception ex) {
		ex.printStackTrace();
		return new SaveResultError(ex);
	}
	
	
	public abstract boolean successful();
	
	
	public static class SaveResultSuccess extends SaveResult {
		
		private SaveResultSuccess() {
			
		}
		
		@Override
		public boolean successful() {
			return true;
		}
		
	}
	
	public static class SaveResultError extends SaveResult {
		
		private Exception ex;
		
		private SaveResultError(Exception ex) {
			this.ex = ex;
		}
		
		@Override
		public boolean successful() {
			return false;
		}
		
		public Exception getException() {
			return this.ex;
		}
		
	}
	
}
