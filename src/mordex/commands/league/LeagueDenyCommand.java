package mordex.commands.league;

import mordex.Challenge;
import mordex.Listener;
import mordex.Main;
import mordex.commands.Command;
import mordex.searching.DiscordGetResult;
import mordex.searching.PlayerGetter;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class LeagueDenyCommand extends Command {

    public LeagueDenyCommand() {
        super("!league deny");
    }

    @Override
    public void run(String message, MessageReceivedEvent e) {
        List<Challenge> pendingChallenges = new ArrayList<>();
        for (Challenge c : Listener.challenges) {
            if (c.isIn(e.getAuthor()) && !c.isCreator(e.getAuthor())) {
                pendingChallenges.add(c);
            }
        }
        User opponent = null;
        if (pendingChallenges.size() == 1) {
            opponent = pendingChallenges.get(0).getOther(e.getAuthor());
        } else if (pendingChallenges.size() > 1) {
            if (message.length() > 0) {
                List<User> allOpponents = new ArrayList<>();
                for (Challenge c : pendingChallenges) {
                    allOpponents.add(c.getOther(e.getAuthor()));
                    //System.out.println("Possible Opponent: " + c.getOther(e.getAuthor()).getUsername());
                }
                DiscordGetResult search = PlayerGetter.getDiscordUser(message, allOpponents);
                if (search.result == DiscordGetResult.SUCCESS) {
                    opponent = search.user;
                    //System.out.println("Found Opponent: " + opponent.getUsername());
                } else if (search.result == DiscordGetResult.NO_RESULTS) {
                    e.getChannel().sendMessage("You don't seem to have a pending challenge from a \"" + message + "\". Double check you spelled their name right " +
                            "and that you have a pending challenge from them?");
                } else {
                    e.getChannel().sendMessage("Too many results for \"" + message + "\".");
                    System.out.println("[WTF] Listener !league deny too many opponents with similar names");
                }
            } else {
                String response = "You have " + pendingChallenges.size() + " pending challenges. To deny one of them, type `!league deny PLAYER_NAME`. " +
                        "Here is your list of pending challenges:\n```\n";
                for (Challenge c : pendingChallenges) {
                    response += c.getOther(e.getAuthor()).getUsername() + "\n";
                }
                response += "```";
                e.getChannel().sendMessage(response);
                return;
            }
        } else {
            e.getChannel().sendMessage("You don't have any pending challenges right now!");
            return;
        }
        if (opponent != null) {
            e.getChannel().sendMessage("You have denied " + opponent.getAsMention() + "'s challenge.");
            if (!Main.FAKE_CHALLENGING) opponent.getPrivateChannel().sendMessage(e.getAuthor().getAsMention() + " has denied your challenge.");
            for (Challenge c : pendingChallenges) {
                if (c.isIn(opponent)) {
                    Listener.challenges.remove(c);
                    System.out.println("REMOVED A CHALLENGE???");
                }
            }
        }
    }
}
