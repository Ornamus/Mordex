package mordex.commands.league;

import mordex.Challenge;
import mordex.Listener;
import mordex.commands.Command;
import mordex.searching.DiscordGetResult;
import mordex.searching.PlayerGetter;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class LeagueWithdrawCommand extends Command {

    public LeagueWithdrawCommand() {
        super("!league withdraw");
    }

    @Override
    public void run(String message, MessageReceivedEvent e) {
        List<Challenge> ownedChallenges = new ArrayList<>();
        for (Challenge c : Listener.challenges) {
            if (c.isCreator(e.getAuthor())) {
                ownedChallenges.add(c);
            }
        }
        User withdrawnOpp = null;
        if (ownedChallenges.size() == 1) {
            withdrawnOpp = ownedChallenges.get(0).getOther(e.getAuthor());
        } else if (ownedChallenges.size() > 1) {
            if (message.length() > 0) {
                List<User> allOpponents = new ArrayList<>();
                for (Challenge c : ownedChallenges) {
                    allOpponents.add(c.getOther(e.getAuthor()));
                }
                DiscordGetResult search = PlayerGetter.getDiscordUser(message, allOpponents);
                if (search.result == DiscordGetResult.SUCCESS) {
                    withdrawnOpp = search.user;
                } else if (search.result == DiscordGetResult.NO_RESULTS) {
                    e.getChannel().sendMessage("You don't seem to have sent a challenge to a \"" + message + "\". Double check you spelled their name right " +
                            "and that you have sent them a challenge?");
                } else {
                    e.getChannel().sendMessage("Too many results for \"" + message + "\".");
                    System.out.println("[WTF] Listener !league withdraw too many opponents with similar names");
                }
            } else {
                String response = "You have " + ownedChallenges.size() + " outgoing challenges. To withdraw from one of them type `!league deny PLAYER_NAME`. " +
                        "Here is your list of outgoing challenges:\n```\n";
                for (Challenge c : ownedChallenges) {
                    response += c.getOther(e.getAuthor()).getUsername() + "\n";
                }
                response += "```";
                e.getChannel().sendMessage(response);
                return;
            }
        } else {
            e.getChannel().sendMessage("You do not have any outgoing challenges!");
            return;
        }
        if (withdrawnOpp != null) {
            e.getChannel().sendMessage("You have withdrawn the challenge you sent to " + withdrawnOpp.getAsMention() + ".");
            for (Challenge c : ownedChallenges) {
                if (c.isIn(withdrawnOpp)) {
                    Listener.challenges.remove(c);
                }
            }
        }
    }
}
