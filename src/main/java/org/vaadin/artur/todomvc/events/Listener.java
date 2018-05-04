package org.vaadin.artur.todomvc.events;

import java.io.Serializable;
import java.util.EventListener;

/**
 * Generic listener for events.
 *
 * @param <T>
 *            component event type
 */
@FunctionalInterface
public interface Listener<T> extends EventListener, Serializable {

	/**
	 * Invoked when a component event has been fired.
	 *
	 * @param event
	 *            component event
	 */
	void onEvent(T event);
}
