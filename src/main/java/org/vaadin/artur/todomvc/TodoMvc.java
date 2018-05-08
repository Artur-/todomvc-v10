package org.vaadin.artur.todomvc;

import java.util.List;
import java.util.Optional;

import org.vaadin.artur.todomvc.SharedTodos.Type;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UIDetachedException;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PageConfigurator;
import com.vaadin.flow.server.InitialPageSettings.Position;
import com.vaadin.flow.server.InitialPageSettings.WrapMode;
import com.vaadin.flow.templatemodel.TemplateModel;

@Tag("todo-mvc")
@HtmlImport("todo-mvc.html")
@Route("")
@Viewport("width=device-width, initial-scale=1.0")
@Push
public class TodoMvc extends PolymerTemplate<TodoMvc.TodoMvcModel> implements PageConfigurator {

	public interface TodoMvcModel extends TemplateModel {

		List<Todo> getTodos();

		void setTodos(List<Todo> todos);
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);
		SharedTodos.get().addListener(e -> {
			try {
				getUI().get().access(() -> {
					if (e.getType() == Type.ADD) {
						getModel().getTodos().add(e.getTodo());
					} else if (e.getType() == Type.UPDATE) {
						Optional<Todo> t = findTodo(e.getTodo());
						if (t.isPresent()) {
							t.get().setText(e.getTodo().getText());
							t.get().setCompleted(e.getTodo().isCompleted());
						}
					} else if (e.getType() == Type.REMOVE) {
						Optional<Todo> t = findTodo(e.getTodo());
						if (t.isPresent()) {
							getModel().getTodos().remove(t.get());
						}

					}
				});
			} catch (UIDetachedException ee) {
				e.unregisterListener();
			}
		});

		getModel().setTodos(SharedTodos.get().getTodos());
	}

	private Optional<Todo> findTodo(Todo clientTodo) {
		return getModel().getTodos().stream().filter(t -> t.equals(clientTodo)).findFirst();
	}

	@ClientCallable
	public void addTodo(String text) {
		SharedTodos.get().addTodo(text);
	}

	@ClientCallable
	public void updateTodoText(Todo clientTodo, String text) {
		SharedTodos.get().updateTodo(clientTodo, text);
	}

	@ClientCallable
	public void removeTodo(Todo clientTodo) {
		SharedTodos.get().removeTodo(clientTodo);
	}

	@ClientCallable
	public void markCompleted(Todo clientTodo, boolean completed) {
		SharedTodos.get().updateTodo(clientTodo, completed);
	}

	@ClientCallable
	public void resync() {
		// Needed because of https://github.com/vaadin/flow/issues/4080
		getModel().setTodos(getModel().getTodos());
	}

	@Override
	public void configurePage(InitialPageSettings settings) {
		settings.addLink(Position.PREPEND, "manifest", "todomvc.webmanifest");
		settings.addInlineWithContents("if ('serviceWorker' in navigator) navigator.serviceWorker.register('sw.js');",
				WrapMode.JAVASCRIPT);

	}

}
