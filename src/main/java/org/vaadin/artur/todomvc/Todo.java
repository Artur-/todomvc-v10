package org.vaadin.artur.todomvc;

public class Todo {

	private String text;
	private boolean completed;
	private int id;

	public Todo() {

	}

	public Todo(String text) {
		this(text, false);
	}

	public Todo(String text, boolean completed) {
		setText(text);
		setCompleted(completed);
	}

	public Todo(String text, int id) {
		setText(text);
		setId(id);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getId();
		result = prime * result + ((getText() == null) ? 0 : getText().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Todo))
			return false;
		Todo other = (Todo) obj;
		if (getId() != other.getId())
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Todo [text=" + getText() + ", completed=" + isCompleted() + ", id=" + getId() + "]";
	}

}
