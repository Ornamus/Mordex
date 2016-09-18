package mordex.commands.tournament;

import mordex.Main;
import mordex.Utils;
import mordex.challonge.Challonge;
import mordex.commands.Command;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import org.json.JSONObject;

public class TournamentJoinCommand extends Command {

    public TournamentJoinCommand() {
        super("!tournament join");
    }

    @Override
    public void run(String message, MessageReceivedEvent e) {
        if (Main.tournamentExists) {
            if (message.length() > 0) {
                boolean email = message.contains("@");
                //TODO: seed properly
                JSONObject result = Challonge.addParticipant(Main.tournamentID, email ? null : message, e.getAuthorName(), email ? message : null, 1, e.getAuthor().getId());
                if (result.getBoolean("success")) {
                    e.getChannel().sendMessage("Your invitation to join has been sent. Check your email or Challonge account for the invite.");
                } else {
                    String response = "The following error(s) occurred while inviting you to the tournament:\n```\n";
                    for (Object err : Utils.getObjects(result.getJSONArray("errors"))) {
                        response += err.toString() + "\n";
                    }
                    response += "```";
                    e.getChannel().sendMessage(response);
                }
            } else {
                e.getChannel().sendMessage("You need to provide either a Challonge username or an email. The format is:\n\n" +
                        "`!tournament join CHALLONGE_NAME`  OR  `!tournament join MY@EMAIL.com`");
            }
        } else {
            e.getChannel().sendMessage("There is no tournament to join currently!");
        }
    }
}
