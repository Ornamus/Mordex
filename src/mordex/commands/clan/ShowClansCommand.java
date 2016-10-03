package mordex.commands.clan;

import mordex.Main;
import mordex.commands.Command;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class ShowClansCommand extends Command {

    public ShowClansCommand() {
        super("!allclans");
    }

    @Override
    public void run(String message, MessageReceivedEvent e) {
        if (Main.admins.contains(e.getAuthor().getId())) {
            String response = "```\n";

        }
    }
}
