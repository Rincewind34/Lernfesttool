package de.rincewind.sql;

import java.util.function.Consumer;

public interface SQLRequest<T> {
	
	public abstract T sync();
	
	public default void async() {
		this.async((value) -> {});
	}
	
	public default void async(Consumer<T> action) {
		this.async(action, (exception) -> {});
	}
	
	public default void async(Consumer<T> action, Consumer<Exception> oncatch) {
		Database.threadpool().execute(() -> {
			try {
				action.accept(this.sync());
			} catch (Exception ex) {
				oncatch.accept(ex);
			}
		});
	}
	
}
