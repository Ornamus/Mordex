package mordex.commands.league;

import mordex.Challenge;
import mordex.Listener;
import mordex.commands.Command;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import java.util.ArrayList;
import java.util.List;

public class LeagueChallengesCommand extends Command {

    public LeagueChallengesCommand() {
        super("!league challenges");
    }

    @Override
    public void run(String message, MessageReceivedEvent e) {
        List<Challenge> outgoingChallenges = new ArrayList<>();
        List<Challenge> incomingChallenges = new ArrayList<>();
        for (Challenge c : Listener.challenges) {
            if (c.isIn(e.getAuthor())) {
                if (c.isCreator(e.getAuthor())) outgoingChallenges.add(c);
                else incomingChallenges.add(c);
            }
        }
        if (incomingChallenges.size() != 0 || outgoingChallenges.size() != 0) {
            String response = "You have " + (incomingChallenges.size() + outgoingChallenges.size()) + " pending challenges. You can accept any incoming " +
                    "challenge with `!league accept PLAYER_NAME` and deny with `!league deny PLAYER_NAME`, and you can withdraw one of the challenges you've sent with " +
                    "`!league withdraw PLAYER_NAME`. " +
                    "Here is your list of pending challenges:\n```Markdown\n# Sent Challenges (you can withdraw from these):\n\n";
            for (Challenge c : outgoingChallenges) {
                response += c.getOther(e.getAuthor()).getUsername() + "\n";
            }
            response += "\n# Incoming Challenges (you can accept or deny these):\n\n";
            for (Challenge c : incomingChallenges) {
                response += c.getOther(e.getAuthor()).getUsername() + "\n";
            }
            response += "```";
            e.getChannel().sendMessage(response);
        } else {
            e.getChannel().sendMessage("You do not have any incoming or outgoing challenges!");
        }
        return;
    }
}
