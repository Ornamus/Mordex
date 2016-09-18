package mordex.challonge;

import mordex.commands.Command;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class QuickCommand extends Command {

    private MessageHandler handler = null;

    public QuickCommand(String...starts) {
        super(starts);
    }

    public QuickCommand setHandler(MessageHandler h) {
        handler = h;
        return this;
    }

    @Override
    public void run(String message, MessageReceivedEvent e) {
        if (handler != null) handler.receive(message, e);
    }

    @FunctionalInterface
    public interface MessageHandler {

        void receive(String message, MessageReceivedEvent e);
    }

}
