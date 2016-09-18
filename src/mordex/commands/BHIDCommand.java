package mordex.commands;

import mordex.Listener;
import mordex.Main;
import mordex.searching.DiscordGetResult;
import mordex.searching.PlayerGetter;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class BHIDCommand extends Command{

    public BHIDCommand() {
        super("!bhid");
    }

    @Override
    public void run(String message, MessageReceivedEvent e) {
        if (message.length() > 0) {
            String[] data = message.split(" ");
            String idString = data[0];
            int id = -1;
            try {
                id = Integer.parseInt(idString);
            } catch (NumberFormatException ex) {}
            if (id > 0) {
                User user = e.getAuthor();
                if (data.length > 1 && Main.admins.contains(e.getAuthor().getId())) {
                    DiscordGetResult result = PlayerGetter.getDiscordUser(data[1], Main.getJDA().getUsers());
                    if (result.result == DiscordGetResult.SUCCESS) {
                        user = result.user;
                    } else if (result.result == DiscordGetResult.NO_RESULTS) {
                        e.getChannel().sendMessage("Could not register BHID \"" + id + "\" to user \"" + data[1] + "\" because there is " +
                                "no user with that name.");
                        return;
                    } else {
                        e.getChannel().sendMessage("Could not register BHID \"" + id + "\" to user \"" + data[1] + "\" because there is " +
                                "more than one user with that name.");
                        return;
                    }
                }
                Listener.DIDToBHID.put(user.getId(), id);
                e.getChannel().sendMessage("BHID \"" + id + "\" is now connected with " + user.getAsMention() + "\'s Discord account.");
            } else {
                e.getChannel().sendMessage("BHID \"" + idString + "\" is not valid! Make sure it is above 0 and does not contain any letters.");
            }
        } else {
            e.getChannel().sendMessage("You need to specify a BHID to connect with your Discord account. Here's the format:\n\n" +
                    "`!bhid YOUR_BRAWLHALLA_ID`");
        }
    }
}
