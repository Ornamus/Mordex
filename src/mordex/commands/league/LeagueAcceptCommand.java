package mordex.commands.league;

import mordex.Challenge;
import mordex.Listener;
import mordex.Main;
import mordex.commands.Command;
import mordex.searching.DiscordGetResult;
import mordex.searching.PlayerGetter;
import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class LeagueAcceptCommand extends Command {

    public LeagueAcceptCommand() {
        super("!league accept");
    }

    @Override
    public void run(String message, MessageReceivedEvent e) {
        User opponent = null;
        List<Challenge> pendingChallenges = new ArrayList<>();
        for (Challenge c : Listener.challenges) {
            if (c.isIn(e.getAuthor()) && !c.isCreator(e.getAuthor())) {
                pendingChallenges.add(c);
            }
        }
        if (pendingChallenges.size() == 1) {
            opponent = pendingChallenges.get(0).getOther(e.getAuthor());
        } else if (pendingChallenges.size() > 1) {
            if (message.length() > 0) {
                List<User> allOpponents = new ArrayList<>();
                for (Challenge c : pendingChallenges) {
                    allOpponents.add(c.getOther(e.getAuthor()));
                    System.out.println("Possible Opponent: " + c.getOther(e.getAuthor()).getUsername());
                }
                DiscordGetResult search = PlayerGetter.getDiscordUser(message, allOpponents);
                if (search.result == DiscordGetResult.SUCCESS) {
                    opponent = search.user;
                    System.out.println("Found Opponent: " + opponent.getUsername());
                } else if (search.result == DiscordGetResult.NO_RESULTS) {
                    e.getChannel().sendMessage("You don't seem to have a pending challenge from a \"" + message + "\". Double check you spelled their name right " +
                            "and that you have a pending challenge from them?");
                } else {
                    e.getChannel().sendMessage("Too many results for \"" + message + "\".");
                    System.out.println("[WTF] Listener !league deny too many opponents with similar names");
                }
            } else {
                String response = "You have " + pendingChallenges.size() + " pending challenges. To accept one of them, type `!league accept PLAYER_NAME`. " +
                        "Here is your list of pending challenges:\n```\n";
                for (Challenge c : pendingChallenges) {
                    response += c.getOther(e.getAuthor()).getUsername() + "\n";
                }
                response += "```";
                e.getChannel().sendMessage(response);
                return;
            }
        }
        if (opponent != null) {
            e.getChannel().sendMessage("Challenge accepted! " + opponent.getAsMention() + " and the Ultimate Alliance Match Refs has been notified. Happy battling!");
            if (!Main.FAKE_CHALLENGING) opponent.getPrivateChannel().sendMessage(e.getAuthor().getAsMention() + " has accepted your challenge! The Ultimate Alliance Match Refs has been notified. " +
                    "Good luck!");
            Role refs = Main.getJDA().getGuildById(Listener.tuaguild).getRoleById("217399692055674880");
            if (!Main.FAKE_CHALLENGING) Main.getJDA().getTextChannelById("217075161109757952").sendMessage(refs.getAsMention() + ", " + e.getAuthor().getAsMention() + " and " + opponent.getAsMention() +
                    " have agreed to a 1v1 League match!");
            for (Challenge c : pendingChallenges) {
                if (c.isIn(opponent)) {
                    Listener.challenges.remove(c);
                }
            }
        }
    }
}
