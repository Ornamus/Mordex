package mordex.commands.clan;

import mordex.BHA;
import mordex.Listener;
import mordex.Main;
import mordex.Utils;
import mordex.commands.Command;
import mordex.searching.DiscordGetResult;
import mordex.searching.PlayerGetResult;
import mordex.searching.PlayerGetter;
import mordex.stringutils.StringUtils;
import mordex.wrappers.Clan;
import mordex.wrappers.PlayerStats;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClanCommand extends Command {

    public ClanCommand() {
        super("!clan");
    }

    @Override
    public void run(String message, MessageReceivedEvent e) { //Look for ID -> Look for registered clan name -> //TODO: check for registered BH user and get their clan
        if (message.length() > 0) {
            int id = -1;
            try {
                id = Integer.parseInt(message);
            } catch (Exception exc){}
            if (id == -1) { //Try and find a registered clan name

                List<String> names = new ArrayList<>();
                for (String s : Listener.CLANNAMEtoID.keySet()) {
                    names.add(s);
                }
                PlayerGetResult result = PlayerGetter.getPlayer(message, names);
                if (result.result == DiscordGetResult.SUCCESS) {
                    id = Listener.CLANNAMEtoID.get(result.name);
                }

            }
            if (id == -1) { //TODO: check for registered BH user and get their clan

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
                    String response = "```PROLOG\n\"" + c.name + "\" (Founded " + c.createDate + ")\n\n\n";
                    String[] ranks = {"Leader", "Officer", "Member", "Recruit"};
                    for (String s : ranks) {
                        List<Clan.ClanEntry> membersOfRank = new ArrayList<>();
                        for (Clan.ClanEntry m : c.members) {
                            if (m.rank.equalsIgnoreCase(s)) {
                                membersOfRank.add(m);
                            }
                        }
                        if (membersOfRank.size() > 0) {
                            if (membersOfRank.size() > 1) {
                                response += s + "s:\n\n\t";
                                int index = 0;
                                for (Clan.ClanEntry m : membersOfRank) {
                                    response += "\"" + m.name + "\"" + ((index + 1) == membersOfRank.size() ? "" : ", ");
                                    index++;
                                }
                            } else {
                                response += s + ": \"" + membersOfRank.get(0).name + "\"";
                            }
                            response += "\n\n";
                        }
                    }
                    //response += "\n\n";
                    Clan.ClanEntry newest = c.members.get(c.members.size() - 1);
                    Clan.ClanEntry mostXP = null;
                    for (Clan.ClanEntry m : c.members) {
                        if (mostXP == null) mostXP = m;
                        if (m.xp > mostXP.xp) {
                            mostXP = m;
                        }
                    }
                    response += "\n" +
                            "Clan XP: " + NumberFormat.getNumberInstance(Locale.US).format(c.xp) + "\n" +
                            "Best XP Collector: \"" + mostXP.name + "\" (\"" + Utils.getPercent(c.xp, mostXP.xp).intValue() + "% of clan XP\")\n" +
                            "Newest Member: \"" + newest.name + "\" (Joined " + newest.joinDate + ")\n" +
                            "```";
                    e.getChannel().sendMessage(response);
                } else {
                    e.getChannel().sendMessage("Clan \"" + id + "\" does not exist!");
                }
            } else {
                e.getChannel().sendMessage("Invalid clan \"" + message + "\"! Clan IDs can be their in-game ID, name (if registered with `!clanreg`), or the name of " +
                        "one of their members (if that member has registered with `!bhid`.");
            }
        } else {
            e.getChannel().sendMessage("You need to supply a clan ID or registered clan name! Example:\n\n`!clan 84420`  OR  `!clan SOAP`");
        }
    }
}
