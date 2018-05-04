package org.vaadin.artur.todomvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.vaadin.artur.todomvc.events.EventBus;
import org.vaadin.artur.todomvc.events.EventBus.Event;
import org.vaadin.artur.todomvc.events.Listener;

import com.vaadin.flow.shared.Registration;

public class SharedTodos {

	private static SharedTodos instance = new SharedTodos();

	private List<Todo> todos = new ArrayList<>();
	private int nextId = 0;
	private EventBus eventBus = new EventBus();

	private SharedTodos() {
		addTodo("Something");
		addTodo("Another thing");
		Todo todo = addTodo("Done deal");
		updateTodo(todo, true);
	}

	public static SharedTodos get() {
		return instance;
	}

	public List<Todo> getTodos() {
		return Collections.unmodifiableList(todos);
	}

	public enum Type {
		ADD, UPDATE, REMOVE;
	}

	public static class AddUpdateRemoveEvent extends Event {
		private Todo todo;
		private Type type;

		public AddUpdateRemoveEvent(Type type, Todo todo) {
			this.type = type;
			this.todo = todo;
		}

		public Todo getTodo() {
			return todo;
		}

		public Type getType() {
			return type;
		}

	}

	public synchronized Todo addTodo(String text) {
		int id = nextId++;
		Todo todo = new Todo(text, id);
		todos.add(todo);
		eventBus.fireEvent(new AddUpdateRemoveEvent(Type.ADD, todo));
		return todo;
	}

	public Registration addListener(Listener<AddUpdateRemoveEvent> listener) {
		return eventBus.addListener(AddUpdateRemoveEvent.class, listener);
	}

	public synchronized void updateTodo(Todo clientTodo, String text) {
		Optional<Todo> todo = findTodo(clientTodo);
		if (!todo.isPresent()) {
			return;
		}

		System.out.println("Todo " + todo.get() + " text updated to " + text);
		todo.get().setText(text);
		eventBus.fireEvent(new AddUpdateRemoveEvent(Type.UPDATE, todo.get()));
	}

	public synchronized void updateTodo(Todo clientTodo, boolean completed) {
		Optional<Todo> todo = findTodo(clientTodo);
		if (!todo.isPresent()) {
			return;
		}

		System.out.println("Todo " + todo.get() + " completed updated to " + completed);
		todo.get().setCompleted(completed);
		eventBus.fireEvent(new AddUpdateRemoveEvent(Type.UPDATE, todo.get()));
	}

	private Optional<Todo> findTodo(Todo clientTodo) {
		for (Todo todo : todos) {
			if (todo.equals(clientTodo))
				return Optional.of(todo);
		}
		return Optional.empty();
	}

	public synchronized void removeTodo(Todo clientTodo) {
		Optional<Todo> todo = findTodo(clientTodo);
		if (!todo.isPresent()) {
			return;
		}

		System.out.println("Todo " + todo + " removed");
		todos.remove(todo.get());
		eventBus.fireEvent(new AddUpdateRemoveEvent(Type.REMOVE, todo.get()));
	}
}
