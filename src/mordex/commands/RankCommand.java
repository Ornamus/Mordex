package mordex.commands;

import mordex.BHApi;
import mordex.Listener;
import mordex.Main;
import mordex.Utils;
import mordex.searching.DiscordGetResult;
import mordex.searching.PlayerGetter;
import mordex.wrappers.LegendRanked;
import mordex.wrappers.PlayerRanked;
import mordex.wrappers.PlayerStats;
import mordex.wrappers.RankedPage;
import net.dv8tion.jda.audio.player.Player;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class RankCommand extends Command {

    public RankCommand() {
        super("!rank");
    }

    //Searches for BHID -> Discord user with BHID -> Does ranked search
    public void run(String name, MessageReceivedEvent e) {
        if (name.length() > 0) {
            if (name.equalsIgnoreCase("me")) name = e.getAuthorName();
            int bhid = -1;
            try {
                bhid = Integer.parseInt(name);
            } catch (NumberFormatException exc) {}
            if (bhid == -1) {
                List<User> users = new ArrayList<>();
                for (String s : Listener.DIDToBHID.keySet()) {
                    User u = Main.getJDA().getUserById(s);
                    if (u != null) users.add(u);
                }
                DiscordGetResult result = PlayerGetter.getDiscordUser(name, users);
                if (result.result == DiscordGetResult.SUCCESS) {
                    bhid = Listener.DIDToBHID.get(result.user.getId());
                }
            }
            if (bhid != -1) {
                PlayerRanked player = BHApi.getPlayerRanked(bhid);
                if (player.init) {
                    LegendRanked bestLegend = null;
                    for (LegendRanked l : player.legends) {
                        if (bestLegend == null) bestLegend = l;
                        if (l.elo > bestLegend.elo) {
                            bestLegend = l;
                        }
                    }
                    Double percentDouble = player.wins / (player.games * 1.0);
                    percentDouble = Utils.roundToPlace(percentDouble * 100, 0);

                    String response = "```Markdown\n" +
                            "# Player Name: " + player.name + "\n\n" +
                            "Region: " + player.region + "\n" +
                            "ELO: " + player.elo + " (" + player.tier + ")\n" +
                            "Win/Loss: " + player.wins + "/" + player.losses + " (" + percentDouble.intValue() + "% winrate)\n" +
                            "Rank: " + player.region_rank + " (" + player.global_rank + " global)\n" +
                            "Best Legend: <" + bestLegend.name + "> " + bestLegend.elo + " ELO\n" +
                            "Brawlhalla ID: " + player.bhid + "\n" +
                            "```";
                    e.getChannel().sendMessage(response);
                } else {
                    PlayerStats stats = BHApi.getPlayerStats(bhid);
                    if (stats.init) {
                        e.getChannel().sendMessage("\"" + stats.name + "\" has not played ranked yet. There is no ranked data on them.");
                    } else {
                        e.getChannel().sendMessage("Invalid BHID \"" + bhid + "\"! There is no player (ranked or unranked) with that BHID.");
                    }
                }
            } else {
                RankedPage page = BHApi.getRankedSearch(name);
                if (page.getEntryCount() == 1) {
                    System.out.println("searching for " + name);
                    RankedPage.RankedEntry player = page.getEntry(0);

                    Double percentDouble = player.wins / (player.games * 1.0);
                    percentDouble = Utils.roundToPlace(percentDouble * 100, 0);

                    String response = "```Markdown\n" +
                            "# Player Name: " + player.name + "\n\n" +
                            "Region: " + player.region + "\n" +
                            "ELO: " + player.elo + " (" + player.tier + ")\n" +
                            "Win/Loss: " + player.wins + "/" + player.losses + " (" + percentDouble.intValue() + "% winrate)\n" +
                            "Global Rank: " + player.rank + "\n" +
                            "Brawlhalla ID: " + player.bhid + "\n\n" +
                            "<Notice> This search was not done using a BHID or registered name, so some information is missing. Use the BHID listed above " +
                            "to get the full data.\n" +
                            "```";
                    e.getChannel().sendMessage(response);
                } else if (page.getEntryCount() > 1) {
                    String response = page.getEntryCount() + " results found for \"" + name + "\". Try being more specific, or use/register a BHID to search with instead.";
                    //if (page.getEntryCount() <= 6) {
                        response += (page.getEntryCount() > 6 ? " Top 6" : "") + " Results Found (for narrowing your search):\n```\n";
                        int num = 0;
                        for (RankedPage.RankedEntry r : page.getEntries()) {
                            num++;
                            if (num > 6) break;
                            response += r.name + " -- " + r.elo + " ELO -- BHID " + r.bhid + " -- " + r.region + "\n";;
                        }
                        response += "```";
                    /*} else {
                        response += " Too many results to display.";
                    }*/
                    e.getChannel().sendMessage(response);
                } else {
                    e.getChannel().sendMessage("No results found for a player named \"" + name + "\"! Try being less specific, double check your spelling, or use/register " +
                            "a BHID to search with instead.");
                }
            }

            /*else if (result.result == DiscordGetResult.TOO_MANY_RESULTS) {
                e.getChannel().sendMessage("There were too many results for a player named \"" + name + "\"! Try being more specific.");
            } else if (result.result == DiscordGetResult.NO_RESULTS) {
                e.getChannel().sendMessage("There were no results for a player named \"" + name + "\"! Double check your spelling?");
            }*/
        } else {
            e.getChannel().sendMessage("You need to specify a person or BHID to look up. Here's the format:\n\n" +
                    "`!rank PLAYER_NAME`  OR  `!rank BHID`");
        }
    }
}
