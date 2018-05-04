/*
 * Copyright 2000-2017 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.vaadin.artur.todomvc.events;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.shared.Registration;

/**
 * An event bus.
 */
public class EventBus implements Serializable {

	private static class EventData implements Serializable {
		private List<Listener<?>> listeners = new ArrayList<>(1);
	}

	// Package private to enable testing only
	HashMap<Class<?>, EventData> eventData = new HashMap<>();

	/**
	 * Creates an event bus.
	 */
	public EventBus() {
	}

	/**
	 * Adds a listener for the given event type.
	 *
	 * @param <T>
	 *            the event type
	 * @param eventType
	 *            the event type for which to call the listener
	 * @param listener
	 *            the listener to call when the event occurs
	 * @return an object which can be used to remove the event listener
	 */
	public synchronized <T> Registration addListener(Class<T> eventType, Listener<T> listener) {
		List<Listener<?>> listeners = eventData.computeIfAbsent(eventType, t -> new EventData()).listeners;
		listeners.add(listener);
		return () -> removeListener(eventType, listener);
	}

	/**
	 * Checks if there is at least one listener registered for the given event type.
	 *
	 * @param eventType
	 *            the component event type
	 * @return <code>true</code> if at least one listener is registered,
	 *         <code>false</code> otherwise
	 */
	public synchronized boolean hasListener(Class<?> eventType) {
		if (eventType == null) {
			throw new IllegalArgumentException("Event type cannot be null");
		}
		return eventData.containsKey(eventType);
	}

	/**
	 * Dispatches the event to all listeners registered for the event type.
	 *
	 * @param event
	 *            the event to fire
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public synchronized void fireEvent(Event event) {
		Class<?> eventType = event.getClass();
		if (!hasListener(eventType)) {
			return;
		}
		List<Listener> listeners = (List) eventData.get(event.getClass()).listeners;
		for (Listener l : new ArrayList<>(listeners)) {
			event.setRemoveCommand(() -> {
				removeListener(eventType, l);
			});
			l.onEvent(event);
			event.setRemoveCommand(null);
		}
	}

	public static abstract class Event {
		private Command removeCommand;

		public void setRemoveCommand(Command removeCommand) {
			this.removeCommand = removeCommand;
		}

		public void unregisterListener() {
			removeCommand.execute();
		}
	}

	/**
	 * Removes the given listener for the given event type.
	 * <p>
	 * Called through the {@link Registration} returned by
	 * {@link #addListener(Class, ComponentEventListener)}.
	 *
	 * @param eventType
	 *            the component event type
	 * @param listener
	 *            the listener to remove
	 */
	private <T> void removeListener(Class<T> eventType, Listener<T> listener) {
		assert eventType != null;
		assert listener != null;
		assert hasListener(eventType);

		List<Listener<?>> listeners = eventData.get(eventType).listeners;
		if (listeners == null) {
			throw new IllegalArgumentException("No listener of the given type is registered");
		}

		if (!listeners.remove(listener)) {
			throw new IllegalArgumentException("The given listener is not registered");
		}
		if (listeners.isEmpty()) {
			// No more listeners for this event type
			eventData.remove(eventType);
		}
	}

}
