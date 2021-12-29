import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TodoManager {

    Map<String, TodoList> todoListMap;

    public TodoManager() {
        todoListMap = new HashMap<>();
    }

    public TodoList extractTodoList(String id) {
        if (todoListMap.containsKey(id)) {
            return todoListMap.get(id);
        } else {
            todoListMap.put(id, new TodoList());
        }
        return todoListMap.get(id);
    }

    public void add(String key, TodoList todoList) {
        todoListMap.put(key, todoList);
    }

    public JSONObject mapToJson() {
        JSONObject jsonObject = new JSONObject();
        for (String next : todoListMap.keySet()) {
            jsonObject.put(next, todoListMap.get(next).intoArray());
        }
        return jsonObject;
    }
}
