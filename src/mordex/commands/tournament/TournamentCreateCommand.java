package mordex.commands.tournament;

import mordex.Main;
import mordex.Utils;
import mordex.challonge.Challonge;
import mordex.commands.Command;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import org.json.JSONObject;

public class TournamentCreateCommand extends Command {

    public TournamentCreateCommand() {
        super("!tournament create");
    }

    @Override
    public void run(String message, MessageReceivedEvent e) {
        if (Main.admins.contains(e.getAuthor().getId())) {
            if (!Main.tournamentExists) {
                JSONObject result = Challonge.makeTournament("Ultimate Alliance 1v1 League", "ultimateallianceleagueonevone", Challonge.DOUBLE_ELIMINATION, "TODO", false, false);
                if (result.getBoolean("success")) {
                    Main.tournamentExists = true;
                    Main.tournamentID = result.getString("id");
                    Main.tournamentURL = result.getJSONObject("tournament").getString("full_challonge_url");
                    e.getChannel().sendMessage("Tournament created! " + Main.tournamentURL);
                } else {
                    String response = "The following error(s) occurred while creating the tournament:\n```\n";
                    for (Object err : Utils.getObjects(result.getJSONArray("errors"))) {
                        response += err.toString() + "\n";
                    }
                    response += "```";
                    e.getChannel().sendMessage(response);
                }
            } else {
                e.getChannel().sendMessage("There is already a tournament in progress!");
            }
        } else {
            e.getChannel().sendMessage("You are not authorized to create a tournament " + e.getAuthor().getAsMention() + ".");
        }
    }
}
