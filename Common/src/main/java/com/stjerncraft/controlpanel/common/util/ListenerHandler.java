package com.stjerncraft.controlpanel.common.util;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class ListenerHandler<T> {
	Set<T> listeners = new HashSet<>();
	
	public void add(T listener) {
		listeners.add(listener);
	}

	public boolean remove(T listener) {
		return listeners.remove(listener);
	}
	
	/**
	 * Remove all listeners
	 */
	public void clear() {
		listeners.clear();
	}
	
	/**
	 * Run the given function for all registered listeners,
	 * returning at once if any of them returns an error.
	 * @param toRun
	 * @return String containing error message. Send null if there was no error
	 */
	public String runTry(Function<T, String> toRun) {
		for(T listener : listeners) {
			String err = toRun.apply(listener);
			if(err != null)
				return err;
		}
		
		return null;
	}
	
	public void run(Consumer<T> toRun) {
		for(T listener : listeners)
			toRun.accept(listener);
	}
}
