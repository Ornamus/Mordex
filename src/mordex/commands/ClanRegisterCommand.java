package mordex.commands;

import mordex.BHA;
import mordex.Listener;
import mordex.Main;
import mordex.searching.DiscordGetResult;
import mordex.searching.PlayerGetter;
import mordex.stringutils.StringUtils;
import mordex.wrappers.Clan;
import mordex.wrappers.PlayerStats;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class ClanRegisterCommand extends Command {

    public ClanRegisterCommand() {
        super("!clanreg");
    }

    @Override
    public void run(String message, MessageReceivedEvent e) {
        if (message.length() > 0) {
            String nick = null;
            if (message.contains("\"")) {
                String[] parts = message.split("\"");
                nick = parts[1];
                message = message.replace("\"" + nick + "\"", "");
                while (message.endsWith(" ")) message = message.substring(0, message.length() - 1);
                while (message.startsWith(" ")) message = message.replaceFirst(" ", "");
            }
            int id = -1;
            try {
                id = Integer.parseInt(message);
            } catch (Exception ex) {}
            if (id == -1) { //Try and pull a clan ID from a registered discord user's name, if there is one in the message
                String name = message;
                if (StringUtils.contains(message, "me")) {
                    name = e.getAuthorName();
                }
                List<User> users = new ArrayList<>();
                for (String s : Listener.DIDToBHID.keySet()) {
                    User u = Main.getJDA().getUserById(s);
                    if (u != null) users.add(u);
                }
                DiscordGetResult result = PlayerGetter.getDiscordUser(name, users);
                if (result.result == DiscordGetResult.SUCCESS) {
                    int bhid = Listener.DIDToBHID.get(result.user.getId());
                    PlayerStats stats = BHA.getPlayerStats(bhid);
                    if (stats.init) {
                        if (stats.hasClan) id = stats.clanID;
                    }
                }

            }
            if (id != -1) {
                Clan c = BHA.getClan(id);
                if (c.init) {
                    Listener.CLANNAMEtoID.put(c.name, id);
                    if (nick != null) Listener.CLANNAMEtoID.put(nick, id);
                    e.getChannel().sendMessage("Clan \"" + c.name + "\" has been registered and it's name can now be used " +
                            "with the `!clan` command." + (nick != null ? " It's nickname \"" + nick + "\" can also be used." : ""));
                } else {
                    e.getChannel().sendMessage("There is no clan with ID \"" + id + "\"!");
                }
            } else {
                e.getChannel().sendMessage("Invalid ID \"" + id + "\"!");
            }
        } else {
            e.getChannel().sendMessage("You must supply a clan ID. Example: `!clanreg 84420`");
        }
    }
}
