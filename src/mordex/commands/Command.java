package mordex.commands;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class Command {

    public final List<String> starts = new ArrayList<>();

    public Command(String s) {
        starts.add(s);
    }

    public Command(String...strings) {
        for (String s : strings) {
            starts.add(s);
        }
    }

    public abstract void run(String message, MessageReceivedEvent e);
}