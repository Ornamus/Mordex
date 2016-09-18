package mordex.commands.tournament;

import mordex.Main;
import mordex.commands.Command;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class TournamentGetCommand extends Command {

    public TournamentGetCommand() {
        super("!tournament", "!bracket");
    }

    @Override
    public void run(String message, MessageReceivedEvent e) {
        if (Main.tournamentExists) {
            e.getChannel().sendMessage(Main.tournamentURL);
        } else {
            e.getChannel().sendMessage("There is no tournament currently!");
        }
    }
}
