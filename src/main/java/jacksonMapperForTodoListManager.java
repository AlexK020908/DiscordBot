import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class jacksonMapperForTodoListManager {
    private String source;
    private TodoManager todoListManager;


    public jacksonMapperForTodoListManager(String source) {
        this.source = source;
        todoListManager = new TodoManager();
    }

    public TodoManager read() throws IOException {
        InputStream file = new FileInputStream(source);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, List<String>> todoListMangerNotCompleted = mapper.readValue(file,
                mapper.getTypeFactory().constructMapType(Map.class, String.class, List.class));

        parseIncompleteManager(todoListMangerNotCompleted, todoListManager);
        return todoListManager;
    }

    private void parseIncompleteManager(Map<String, List<String>> todoListMangerNotCompleted, TodoManager todoListManager) {

        for (String s : todoListMangerNotCompleted.keySet()) {
            List<String> todos = todoListMangerNotCompleted.get(s);
            TodoList todoList = new TodoList();
            for (String next : todos) {
                todoList.addTodo(next);
            }
            todoListManager.add(s, todoList);
        }
    }
}
