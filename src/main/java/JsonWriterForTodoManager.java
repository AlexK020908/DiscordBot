import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class JsonWriterForTodoManager {
    private final int tab = 4;
    private PrintWriter writer;
    private String destination;

    //EFFECT: constructs a Json writer for Revenue list at the given destination
    public JsonWriterForTodoManager(String destination) {
        this.destination = destination;
    }

    //MODIFIES: this
    //EFFECT: opens the writer for revenue list at the given destination
    //        throws fileNotFoundException if file can not be opened
    public void open() throws FileNotFoundException {
        //initialize the writer to a file of destination
        writer = new PrintWriter(new File(destination));
    }


    //MODIFIES: this
    //EFFECT: write the Json representation of revenueList to the file at given location.
    public void write(TodoManager todoListManager) {
        //turn the revenue List into a json object
        //then write revenue list as to array on the destination
        JSONObject jsonObject = todoListManager.mapToJson();
        writer.print(jsonObject.toString(tab));

    }

    //MODIFIES: this
    //EFFECT: closes the writer
    public void close() {
        writer.close();
    }
}
