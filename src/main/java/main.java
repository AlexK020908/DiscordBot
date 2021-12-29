
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;



import javax.security.auth.login.LoginException;
import java.io.*;

import java.util.concurrent.TimeUnit;

public class main extends ListenerAdapter {

    private static JDABuilder bot;
    private static JDA jda;
    private static TodoManager todoListManager;
    private static TodoList todoList;
    private static JsonWriterForTodoManager jsonWriterForTodoManager;
    private static jacksonMapperForTodoListManager reader;

    private static EventWaiter eventWaiter; //single instance needed

    public static void main(String[] args) {
        jsonWriterForTodoManager = new JsonWriterForTodoManager("./data/todoListManager.json");
        reader = new jacksonMapperForTodoListManager("./data/todoListManager.json");
        bot = JDABuilder.createDefault("input key here");
        bot.setActivity(Activity.playing("todo manager"));
        bot.setStatus(OnlineStatus.DO_NOT_DISTURB);
        bot.addEventListeners(new main());
        todoListManager = new TodoManager();
        eventWaiter = new EventWaiter();
        bot.addEventListeners(eventWaiter);


        try {
            todoListManager = reader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            jda = bot.build();
        } catch (LoginException e) {
            e.printStackTrace();
        }


        Runtime.getRuntime().addShutdownHook(
                new Thread("app-shutdown-hook") {
                    @Override
                    public void run() {
                        System.out.println("bye bye!");
                        try {
                            jsonWriterForTodoManager.open();
                            jsonWriterForTodoManager.write(todoListManager);
                            jsonWriterForTodoManager.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }


    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {

        String message = event.getMessage().getContentRaw();

        switch (message) {
            case "-pm":
                event.getMember().getUser().openPrivateChannel().queue(
                        privateChannel -> {
                            privateChannel.sendMessage("Hi, here is your help pannel").queue();
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setTitle("your todo list");
                            embed.setColor(0xE6E6FA);
                            embed.setThumbnail(event.getMember().getUser().getAvatarUrl());
                            embed.setDescription("\n **-todo** to show todo" +
                                    "\n **+todo <todo>** to add a new todo" +
                                    "\n **-clear** to clear all todos" +
                                    "\n **-sf** so you can select an finished item");
                            privateChannel.sendMessage(embed.build()).queue();
                        }
                );
                break;

            case "-shutdown":
                if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                    event.getChannel().sendMessage("now shutting down and saving progress").queue();
                    System.exit(0);
                } else {
                    event.getChannel().sendMessage("you do not have permission to manage the bot");
                }
                break;
        }
    }


    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {


        String message = event.getMessage().getContentRaw();
        Long userId = event.getAuthor().getIdLong();
        String[] arguments = event.getMessage().getContentRaw().split(" ");
        String todo = todoListManager.extractTodoList(userId.toString()).printAllTodos();

        StringBuilder s = new StringBuilder();
        for (String r : arguments) {
            s.append(r + " ");
        }

        if (arguments[0].equals("+todo") && !arguments[1].isEmpty()) {
            String todoItem = s.substring(5);
            TodoList userTodoList = todoListManager.extractTodoList(event.getAuthor().getId());
            userTodoList.addTodo(todoItem);
            event.getChannel().sendMessage("a new todo for you" +
                    " have been created" + " please do **-todo** to view your todos").queue();
        }

        switch (message) {

            case "-sf" :
                event.getAuthor().openPrivateChannel().queue(

                        privateChannel -> {
                            privateChannel.sendMessage("which one of the following todos have you finished?").queue();

                            TodoList todoList = todoListManager.extractTodoList(userId.toString());
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 1; i <= todoList.getSize(); i++) {
                                stringBuilder.append("\n" + i + ". " + todoList.get(i-1));
                            }
                            privateChannel.sendMessage(stringBuilder.toString()).queue(
                                    message1 ->  {
                                        eventWaiter.waitForEvent(PrivateMessageReceivedEvent.class, e -> !e.getAuthor().isBot() ,
                                                e -> {
                                                    String message2 = e.getMessage().getContentRaw();
                                                    Integer position = Integer.parseInt(message2);
                                                    if (position > todoList.getSize()) {
                                                        privateChannel.sendMessage("there are no such todo positioned at the requested selection").queue();
                                                    } else {
                                                        todoList.remove(position - 1);
                                                        privateChannel.sendMessage("your requested todo has been finished, congrats!" +
                                                                "\n here is your updated version of todos.").queue();

                                                        EmbedBuilder embed = new EmbedBuilder();
                                                        embed.setTitle("your todo list");
                                                        embed.setColor(0xE6E6FA);
                                                        embed.setThumbnail(event.getAuthor().getAvatarUrl());
                                                        embed.setDescription("\n your list have been loaded" +
                                                                todoList.printAllTodos());

                                                        privateChannel.sendMessage(
                                                                embed.build()
                                                        ).queue();


                                                    }


                                                },
                                                1, TimeUnit.MINUTES,
                                                () -> privateChannel.sendMessage("you took too long to respond").queue()
                                        );
                                    }
                            );

                        }




                );
                break;
            case "-clear":
                event.getAuthor().openPrivateChannel().queue(
                        privateChannel -> {
                            if (todo.isEmpty()) {
                                privateChannel.sendMessage("your todo list is empty, can not clear an empty todo list").queue();
                            } else {
                                privateChannel.sendMessage("you have finished your todo, congrats! ").queue();
                                TodoList userTodoList = todoListManager.extractTodoList(event.getAuthor().getId());
                                userTodoList.removeAll();
                            }
                        }
                );
                break;

            case "-todo":

                event.getAuthor().openPrivateChannel().queue(
                        privateChannel -> {
                            if (!(todo.isEmpty())) {
                                EmbedBuilder embed = new EmbedBuilder();
                                embed.setTitle("your todo list");
                                embed.setColor(0xE6E6FA);
                                embed.setThumbnail(event.getAuthor().getAvatarUrl());
                                embed.setDescription("\n your list have been loaded" +
                                        todo);

                                privateChannel.sendMessage(
                                        embed.build()
                                ).queue();
                            } else {
                                privateChannel.sendMessage(
                                        "your todo list is empty, please do **+todo <todo name>** to add a new todo"

                                ).queue();

                            }

                        }


                );


        }


    }
}
