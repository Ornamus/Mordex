package mordex.commands;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

public abstract class Command {

    public final String start;
    private Runnable code = null;

    public Command(String s) {
        start = s;
    }

    public Command(String s, Runnable r) {
        start = s;
        code = r;
    }

    public void run(MessageReceivedEvent e) {

    }
}
