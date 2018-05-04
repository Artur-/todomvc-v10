package org.vaadin.artur.todomvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.templatemodel.TemplateModel;

@Tag("todo-mvc")
@HtmlImport("todo-mvc.html")
@Route("")
@Viewport("width=device-width, initial-scale=1.0")
public class TodoMvc extends PolymerTemplate<TodoMvc.TodoMvcModel> {

	@Id("newTodoInput")
	private TextField newTodoInput;
	private int nextId = 0;

	public interface TodoMvcModel extends TemplateModel {

		List<Todo> getTodos();

		void setTodos(List<Todo> todos);
	}

	public TodoMvc() {
		ArrayList<Todo> items = new ArrayList<Todo>();
		items.add(new Todo("Something"));
		items.add(new Todo("Another thing"));
		items.add(new Todo("Done deal", true));
		getModel().setTodos(items);

		newTodoInput.getElement().addEventListener("keyup", e -> {
			newTodoInput.clear();
			String value = e.getEventData().getString("element.value");
			addTodo(value);
		}).addEventData("element.value").setFilter("event.keyCode == 13");
	}

	public void addTodo(String text) {
		getModel().getTodos().add(new Todo(text, nextId++));
	}

	@ClientCallable
	public void removeTodo(Todo clientTodo) {
		Optional<Todo> todo = getModel().getTodos().stream().filter(t -> t.equals(clientTodo)).findFirst();
		if (!todo.isPresent()) {
			return;
		}

		System.out.println("Todo " + todo + " removed");
		getModel().getTodos().remove(todo.get());
	}

	@ClientCallable
	public void markCompleted(Todo clientTodo, boolean completed) {
		Optional<Todo> todo = getModel().getTodos().stream().filter(t -> t.equals(clientTodo)).findFirst();
		if (!todo.isPresent()) {
			return;
		}

		System.out.println("Todo " + todo + " marked as complete=" + completed);
		todo.get().setCompleted(completed);
	}
}
