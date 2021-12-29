
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jetbrains.annotations.NotNull;


import java.util.LinkedList;
import java.util.List;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TodoList implements Iterable<String> {
    List<String> todos;


    public TodoList() {
        todos = new LinkedList<>();
    }


    public int getSize() {
        return todos.size();
    }

    public String get(int i) {
        return todos.get(i);
    }

    public void addTodo(String todo) {
        todos.add(todo);
    }

    public String printAllTodos() {
        StringBuilder stringBuilder = new StringBuilder();
        for(String e: todos) {
            stringBuilder.append("\n - " + e );
        }
        return stringBuilder.toString();
    }


    public void removeAll() {
        todos.clear();
    }




    public Object[] intoArray() {
        return todos.toArray();

    }

    @NotNull
    @Override
    public Iterator<String> iterator() {
        return this.iterator();
    }

    public void remove(int parseInt) {
        todos.remove(parseInt);
    }


}