package mordex.commands.league;

import mordex.GDocs;
import mordex.commands.Command;
import mordex.searching.PlayerGetResult;
import mordex.searching.PlayerGetter;
import mordex.wrappers.LeaguePage;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class LeagueCommand extends Command {

    public LeagueCommand() {
        super("!yammah");
    }

    @Override
    public void run(String message, MessageReceivedEvent e) {
        if (message.length() > 0) {
            LeaguePage page = new LeaguePage(GDocs.getPage());
            String arg = message;
            /*
            if (arg.contains("title holder")) arg = arg.replace("title holder", "0");
            if (arg.contains("title")) arg = arg.replace("title", "0");
            if (arg.contains("holder")) arg = arg.replace("holder", "0");
            if (arg.contains("champion")) arg = arg.replace("champion", "0");
            if (arg.contains("champ")) arg = arg.replace("champ", "0");
            */
            //System.out.println("Arg: " + arg);
            int rank = -1;
            try {
                rank = Integer.parseInt(arg);
            } catch (NumberFormatException ex) {
            }
            //System.out.println("Rank: " + rank);
            if (rank > -1) {
                LeaguePage.PageEntry player = page.getByRank(rank);
                if (player != null) {
                    e.getChannel().sendMessage(player.getInfo());
                } else {
                    int highestRank = 0;
                    for (LeaguePage.PageEntry en : page.entries) {
                        if (en.rank > highestRank) {
                            highestRank = en.rank;
                        }
                    }
                    e.getChannel().sendMessage("There is no player with rank \"" + rank + "\"! Try a different rank. (Valid ranks are 1-" + highestRank + ")");
                }
            } else if (arg.equals("rankings")) {
                String response = "```PROLOG\nYammah's Invitational Tourneys Top 10\n\n\n";
                for (LeaguePage.PageEntry entry : page.entries) {
                    if (entry.rank <= 10) {
                        response += "\"" + entry.name + "\" (" + (entry.rank == 0 ? "Title Holder" : ("Rank " + entry.rank)) + ")\n\t" +
                               "Points: " + entry.points + "\n\n";
                    }
                }
                response += "```";
                e.getChannel().sendMessage(response);
            } else { //Search for player by name
                PlayerGetResult result = PlayerGetter.getPlayer(arg, page.getAllNames());
                if (result.result == PlayerGetResult.SUCCESS) {
                    LeaguePage.PageEntry player = page.getByName(result.name);
                    e.getChannel().sendMessage(player.getInfo());
                } else if (result.result == PlayerGetResult.TOO_MANY_PARTIAL_RESULTS) {
                    e.getChannel().sendMessage("There were too many results for a player who's name contains \"" + arg + "\". Try typing out their full name.");
                } else if (result.result == PlayerGetResult.TOO_MANY_PERFECT_RESULTS) {
                    e.getChannel().sendMessage("There are too many results for a player named \"" + arg + "\". Have an admin or someone fix the problem.");
                } else if (result.result == PlayerGetResult.NO_RESULTS) {
                    e.getChannel().sendMessage("There were no results for a player named \"" + arg + "\".");
                }
            }
        } else {
            e.getChannel().sendMessage("You need to specify a person or rank to look up. Here's the format:\n\n" +
                    "`!yammah PLAYER_NAME`  OR  `!yammah RANK`");
        }
    }
}
