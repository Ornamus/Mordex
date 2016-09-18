package mordex.commands;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class Command {

    public final List<String> starts = new ArrayList<>();
    public boolean triggerOnSelf = true;

    public Command(String s) {
        starts.add(s);
    }

    public Command(String...strings) {
        for (String s : strings) {
            starts.add(s);
        }
    }

    public Command setTriggerOnSelf(boolean b) {
        triggerOnSelf = b;
        return this;
    }

    public abstract void run(String message, MessageReceivedEvent e);
}