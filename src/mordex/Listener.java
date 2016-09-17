package mordex;

import mordex.searching.DiscordGetResult;
import mordex.searching.PlayerGetResult;
import mordex.searching.PlayerGetter;
import mordex.wrappers.LeaguePage;
import mordex.wrappers.LegendRanked;
import mordex.wrappers.PlayerRanked;
import mordex.wrappers.RankedPage;
import net.dv8tion.jda.entities.Channel;
import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import org.json.JSONObject;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Exchanger;

public class Listener extends ListenerAdapter {

    public static HashMap<String, Integer> DIDToBHID = new HashMap<>();
    public static List<Challenge> challenges = new ArrayList<>();

    private static final String tuaguild = "192497024422248448";

    //TODO: Help command.
    //TODO: Fix crash when using offline users(?)

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        //try {
        String message = e.getMessage().getContent();
        if (message.toLowerCase().startsWith("!rank")) { //TODO: search for Discord user -> BHID, if not do BH rank search
            e.getChannel().sendTyping();
            message = message.replace("!rank", "");
            if (message.length() > 1) {
                while (message.startsWith(" ")) message = message.replaceFirst(" ", "");
                String data = message;
                int id = -1;
                if (data.equalsIgnoreCase("me")) {
                    data = e.getAuthor().getUsername();
                }
                data = Utils.insertBHIDs(e.getGuild(), data);
                try {
                    id = Integer.parseInt(data);
                } catch (NumberFormatException exc) {}
                if (id != -1) {
                    PlayerRanked player = BHApi.getPlayerRanked(id);
                    if (player.init) {
                        LegendRanked bestLegend = null;
                        for (LegendRanked l : player.legends) {
                            if (bestLegend == null) bestLegend = l;
                            if (l.elo > bestLegend.elo) {
                                bestLegend = l;
                            }
                        }
                        String response = "```Markdown\n" +
                                "# Player Name: " + player.name + "\n\n" +
                                "Region: " + player.region + "\n" +
                                "ELO: " + player.elo + " (" + player.tier + ")\n" +
                                "Win/Loss: " + player.wins + "/" + player.losses + "\n" +
                                "Rank: " + player.region_rank + " (" + player.global_rank + " global)\n" +
                                "Best Legend: <" + bestLegend.name + "> " + bestLegend.elo + " ELO\n" +
                                "Brawlhalla ID: " + player.bhid + "\n" +
                                "```";
                        e.getChannel().sendMessage(response);
                    } else {
                        //TODO: If data is the name of a discord user, display a 'That user does not have a BHID on record" message
                        e.getChannel().sendMessage("Invalid BHID \"**" + data + "**\"! There is no ranked player with that BHID.");
                    }
                } else {
                    RankedPage page = BHApi.getRankedSearch(data);
                    if (page.getEntryCount() == 1) {
                        RankedPage.RankedEntry player = page.getEntry(0);
                        String response = "```Markdown\n" +
                                "# Player Name: " + player.name + "\n\n" +
                                "Region: " + player.region + "\n" +
                                "ELO: " + player.elo + " (" + player.tier + ")\n" +
                                "Win/Loss: " + player.wins + "/" + player.losses + "\n" +
                                "Global Rank: " + player.rank + "\n" +
                                "Brawlhalla ID: " + player.bhid + "\n\n" +
                                "<Notice> This search was not done using a BHID or registered name, so some information is missing. Use the BHID listed above " +
                                "to get the full data.\n" +
                                "```";
                        e.getChannel().sendMessage(response);
                    } else if (page.getEntryCount() > 1) {
                        String response = page.getEntryCount() + " results found for \"" + data + "\". Try being more specific, or use/register a BHID to search with instead.";
                        if (page.getEntryCount() <= 6) {
                            response += " Results Found (for narrowing your search):\n```\n";
                            for (RankedPage.RankedEntry r : page.getEntries()) {
                                response += r.name + " -- " + r.elo + " ELO -- BHID " + r.bhid + "\n";
                            }
                            response += "```";
                        } else {
                            response += " Too many results to display.";
                        }
                        e.getChannel().sendMessage(response);
                    } else {
                        e.getChannel().sendMessage("No results found for \"" + data + "\"! Try being less specific, double check your spelling, or use/register " +
                                "a BHID to search with instead.");
                    }
                }
            } else {
                e.getChannel().sendMessage("You need to specify a person or BHID to look up. Here's the format:\n\n" +
                        "`!rank PLAYER_NAME`  OR  `!rank BHID`");
            }
        } else if (message.toLowerCase().startsWith("!bhid") || message.toLowerCase().startsWith("!steamid")) {
            e.getChannel().sendTyping();
            boolean steam = message.startsWith("!steamid");

            if (steam) message = message.replace("!steamid", "");
            else message = message.replace("!bhid", "");

            if (message.length() > 1) {
                message = message.replaceFirst(" ", "");
                String[] data = message.split(" ");
                String idString = data[0];
                int id = -1;
                try {
                    if (steam) {
                        JSONObject o = BHApi.steamIDToBHID(idString);
                        System.out.println("Response: " + o.toString());
                        if (!o.isNull("brawlhalla_id")) {
                            id = o.getInt("brawlhalla_id");
                            System.out.println("Id set to " + id);
                        } else {
                            e.getChannel().sendMessage("Steam ID \"" + idString + "\" is not valid! Make sure it does not contain any letters.");
                            return;
                        }
                    } else {
                        id = Integer.parseInt(idString);
                    }
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
                    DIDToBHID.put(user.getId(), id);
                    //System.out.println("ID looks like: " + user.getId());
                    e.getChannel().sendMessage("BHID \"" + id + "\" is now connected with " + user.getAsMention() + "\'s Discord account.");
                } else {
                    e.getChannel().sendMessage("BHID \"" + idString + "\" is not valid! Make sure it is above 0** and does not contain any letters.");
                }
            } else {
                e.getChannel().sendMessage("You need to specify a BHID or SteamID to connect with your Discord account. Here's the format:\n\n" +
                        "`!bhid YOUR_BHID`  OR  `!steamid YOUR_STEAMID`");
            }
        } else if (message.toLowerCase().startsWith("!league challenge ")) {
            e.getChannel().sendTyping();
            message = message.replace("!league challenge ", ""); //TODO: 2v2 support :(
            if (message.length() > 0) {
                while (message.startsWith(" ")) message = message.replaceFirst(" ", "");
                LeaguePage page = new LeaguePage(GDocs.getPage());
                String name = message;

                PlayerGetResult result = PlayerGetter.getPlayer(name, page.getAllNames()); //Get opponent's league name
                if (result.result == PlayerGetResult.SUCCESS) {
                    PlayerGetResult thisPlayer = PlayerGetter.getPlayer(e.getAuthor().getUsername(), page.getAllNames()); //Get author's league name
                    if (thisPlayer.result == PlayerGetResult.SUCCESS || thisPlayer.result == PlayerGetResult.NO_RESULTS) {
                        LeaguePage.PageEntry player = page.getByName(thisPlayer.name); //TODO: probably have to change this so use a registration system like !rank does
                        //if (player != null) {
                        LeaguePage.PageEntry otherPlayer = page.getByName(result.name);
                        System.out.println(e.getAuthor().getUsername() + " challenging " + otherPlayer.name);
                        double rankDiff = 0;
                        if (player != null) rankDiff = player.rank - otherPlayer.rank;
                        if (player == null) System.out.println("Unranked player \"" + e.getAuthor().getUsername() + "\" is challenging anyone in the TUA league");
                        if (Math.abs(rankDiff) <= 14) {
                            DiscordGetResult discordResult = PlayerGetter.getDiscordUser(otherPlayer.name, Main.getJDA().getUsers());
                            /*
                            List<User> users = new ArrayList<>();
                            for (User u : Main.getJDA().getUsers()) {
                                if (!StringUtils.containsPhrase(u.getUsername(), otherPlayer.name).isEmpty()) {
                                    users.add(u);
                                }
                            }
                            */
                            System.out.println("Result: " + discordResult.result);
                            //if (users.size() == 1) {
                            if (discordResult.result == DiscordGetResult.SUCCESS) {
                                User otherUser = discordResult.user;
                                if (!Main.FAKE_CHALLENGING) otherUser.getPrivateChannel().sendMessage(e.getAuthor().getAsMention() + " has challenged you to a U.A. 1v1 League match! Here are your options:\n\n" +
                                        "`!league accept` -- This will accept the challenge and notify a U.A. Match Ref.\n\n" +
                                        "`!league deny` -- This will deny the challenge.");
                                e.getChannel().sendMessage("Your challenge has been sent to " + otherUser.getAsMention() + ", " + e.getAuthor().getAsMention() + ".");
                                challenges.add(new Challenge(e.getAuthor(), otherUser));
                            } else {
                                //TODO: deal w/ this correctly
                                System.out.println("uh oh spagettios");
                            }
                        } else {
                            e.getChannel().sendMessage(e.getAuthor().getAsMention() + ", you can't challenge someone more than 14 ranks above or below you! " + otherPlayer.name + " is " +
                                    "too " + (rankDiff > 0 ? "high" : "low") + "!");
                        }
                        //}
                    } else {
                        /*e.getChannel().sendMessage("I can't find you on the 1v1 League rankings, " + e.getAuthor().getAsMention() + "! Are you on it? " +
                                "Does your name on the rankings match your Discord name?");*/
                    }

                } else if (result.result == PlayerGetResult.TOO_MANY_PARTIAL_RESULTS) {
                    e.getChannel().sendMessage("There were too many results for a player who's name contains \"" + name + "\". Try typing out their full name.");
                } else if (result.result == PlayerGetResult.TOO_MANY_PERFECT_RESULTS) {
                    e.getChannel().sendMessage("There are too many results for a player named \"" + name + "\". Have an admin or someone fix the problem.");
                } else if (result.result == PlayerGetResult.NO_RESULTS) {
                    e.getChannel().sendMessage("There were no results for a player named \"" + name + "\"! Did you spell their name how it is spelled on the League Rankings page?");
                }
            } else {
                e.getChannel().sendMessage("You need to specify an opponent to challenge. Here's the format:\n\n" +
                        "`!league challenge OPPONENT_NAME");
            }
        } else if (message.toLowerCase().startsWith("!league accept")) {
            e.getChannel().sendTyping();
            message = message.replace("!league accept", "");
            while (message.startsWith(" ")) message = message.replaceFirst(" ", "");
            User opponent = null;
            List<Challenge> pendingChallenges = new ArrayList<>();
            for (Challenge c : challenges) {
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
                Role refs = Main.getJDA().getGuildById(tuaguild).getRoleById("217399692055674880");
                if (!Main.FAKE_CHALLENGING) Main.getJDA().getTextChannelById("217075161109757952").sendMessage(refs.getAsMention() + ", " + e.getAuthor().getAsMention() + " and " + opponent.getAsMention() +
                        " have agreed to a 1v1 League match!");
                for (Challenge c : pendingChallenges) {
                    if (c.isIn(opponent)) {
                        challenges.remove(c);
                    }
                }
            }
        } else if (message.toLowerCase().startsWith("!league deny")) {
            e.getChannel().sendTyping();
            message = message.replace("!league deny", "");
            while (message.startsWith(" ")) message = message.replaceFirst(" ", "");
            List<Challenge> pendingChallenges = new ArrayList<>();
            for (Challenge c : challenges) {
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
                        challenges.remove(c);
                    }
                }
            }
        } else if (message.toLowerCase().startsWith("!league challenges")) {
            e.getChannel().sendTyping();
            List<Challenge> outgoingChallenges = new ArrayList<>();
            List<Challenge> incomingChallenges = new ArrayList<>();
            for (Challenge c : challenges) {
                if (c.isIn(e.getAuthor())) {
                    if (c.isCreator(e.getAuthor())) outgoingChallenges.add(c);
                    else incomingChallenges.add(c);
                }
            }
            String response = "You have " + (incomingChallenges.size() + outgoingChallenges.size()) + " pending challenges. You can accept any incoming " +
                    "challenge with `!league accept PLAYER_NAME` and deny with `!league deny PLAYER_NAME`. " +
                    "Here is your list of pending challenges:\n```Markdown\n# Sent Challenges:\n\n";
            for (Challenge c : outgoingChallenges) {
                response += c.getOther(e.getAuthor()).getUsername() + "\n";
            }
            response += "\n# Incoming Challenges (you can accept or deny these):\n\n";
            for (Challenge c : incomingChallenges) {
                response += c.getOther(e.getAuthor()).getUsername() + "\n";
            }
            response += "```";
            e.getChannel().sendMessage(response);
            return;
        } else if (message.toLowerCase().startsWith("!league")) { //TODO 2v2 support
            e.getChannel().sendTyping();
            message = message.replace("!league", "");
            while (message.startsWith(" ")) message = message.replaceFirst(" ", "");
            if (message.length() > 0) {
                LeaguePage page = new LeaguePage(GDocs.getPage());
                String arg = message;
                if (arg.contains("title holder")) arg = arg.replace("title holder", "0");
                if (arg.contains("title")) arg = arg.replace("title", "0");
                if (arg.contains("holder")) arg = arg.replace("holder", "0");
                if (arg.contains("champion")) arg = arg.replace("champion", "0");
                if (arg.contains("champ")) arg = arg.replace("champ", "0");
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
                        e.getChannel().sendMessage("There is no player with rank \"" + rank + "\"! Try a different rank.");
                    }
                } else if (arg.equals("rankings")) {
                    String response = "```Markdown\n<Ultimate Alliance 1v1 League Top 10>\n\n";
                    for (LeaguePage.PageEntry entry : page.entries) {
                        if (entry.rank <= 10) {
                            response += "# " + entry.name + " (" + (entry.rank == 0 ? "Title Holder" : ("Rank " + entry.rank)) + ")\n\t" +
                                    entry.getWinLossTieDisplay() + ": " + entry.getWinLossTie() + "\n\tPoints: " + entry.points + "\n\n";
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
                        "`!league PLAYER_NAME`  OR  `!league RANK`");
            }
        } else if (message.toLowerCase().startsWith("!version")) {
            e.getChannel().sendTyping();
            e.getChannel().sendMessage("Current version: " + Main.version);
        } else if (message.toLowerCase().startsWith("!help")) {
            e.getChannel().sendTyping();
            //e.getChannel().sendMessage("Mordex documentation: " + "https://github.com/Ornamus/Mordex/blob/master/DOCUMENTATION");
            e.getChannel().sendMessage("Coming soon! In the mean time, ask Ornamus or another user for help!");
        } else if (message.toLowerCase().startsWith("!update") || message.toLowerCase().startsWith("!shutdown")) {
            if (e.getAuthor().getId().equals(Main.ornamus)) {
                try {
                    System.out.println("Saving users to file...");
                    PrintWriter writer = new PrintWriter("users.txt", "UTF-8");
                    for (String key : Listener.DIDToBHID.keySet()) {
                        writer.println(key + "=" + Listener.DIDToBHID.get(key));
                    }
                    writer.close();
                    writer = new PrintWriter("challenges.txt", "UTF-8");
                    for (Challenge c : challenges) {
                        writer.println(c.a.getId() + "chal" + c.b.getId());
                    }
                    writer.close();
                    System.out.println("Save complete.");
                    if (message.startsWith("!update")) {
                        Runtime.getRuntime().exec("java -jar update.jar");
                        System.out.println("Launching updater...");
                    }
                    System.exit(0);
                } catch (Exception exc) {
                    System.out.println("Shutdown file save exception!");
                    exc.printStackTrace();
                }
            }
        } else if (message.toLowerCase().startsWith("!chat ")) {
            if (e.getAuthor().getId().equals(Main.ornamus)) {
                message = message.replace("!chat ", "");
                String channelID = message.split(" ")[0];
                TextChannel chan = Main.getJDA().getTextChannelById(channelID);
                chan.sendTyping();
                message = message.replace(channelID, "");
                while (message.startsWith(" ")) message = message.replaceFirst(" ", "");
                chan.sendMessage(message);
            }
        } else if (message.startsWith("!music ")) {
            /*
            if (Utils.isAdmin(e.getGuild(), e.getAuthor())) {
                String[] args = message.replace("!music ", "").split(" ");
                if (args[0].equals("stop")) {
                    e.getJDA().getAudioManager().closeAudioConnection();
                    audioPlayer = null;
                    e.getChannel().sendMessage("Music stopped.");
                    Utils.setGame(null);
                } else {
                    String chanName = null;
                    boolean alreadyInChannel = false;
                    if (args.length > 1) {
                        chanName = args[1];
                    }
                    VoiceChannel curr;
                    if ((curr = e.getJDA().getAudioManager().getConnectedChannel()) != null) {
                        if (args.length == 1) chanName = curr.getName();
                        if (curr.getName().equalsIgnoreCase(chanName)) alreadyInChannel = true;
                    }
                    if (chanName != null) {
                        VoiceChannel chan = Utils.getVoiceChannel(e.getGuild(), chanName);
                        if (chan != null) {
                            try {
                                File music = new File("music/" + args[0] + ".mp3");
                                if (music.exists()) {
                                    if (!alreadyInChannel) e.getJDA().getAudioManager().openAudioConnection(chan);
                                    audioPlayer = new FilePlayer(music);
                                    e.getJDA().getAudioManager().setSendingHandler(audioPlayer);
                                    audioPlayer.play();
                                    e.getChannel().sendMessage("Now playing **" + music.getName() + "** in **" + chan.getName() + "**.");
                                    Utils.setGame(music.getName());
                                } else {
                                    e.getChannel().sendMessage("Song \"" + args[0] + "\" does not exist!");
                                }
                            } catch (Exception ex) {
                                System.out.println("Audio player exception!");
                                ex.printStackTrace();
                            }
                        } else {
                            e.getChannel().sendMessage("Voice channel \"" + chanName + "\" does not exist!");
                        }
                    } else {
                        e.getChannel().sendMessage("Please specify which Voice Channel this song will be played in. Example: **!music Recorder General**");
                    }
                }
            } else {
                e.getChannel().sendMessage("Only admins can use music commands.");
            }*/
        }
        /*
        } catch (Exception exception) {
            exception.printStackTrace();
            String error = exception.getMessage();
            List<String> parts = new ArrayList<>();
            parts.add(error);
            while (parts.get(parts.size() - 1).length() > 2000) {
                String lastPart = parts.get(parts.size() - 1);
                parts.add(parts.size() - 1, lastPart.substring(0, 2000));
                parts.add(lastPart.substring(2000, lastPart.length() - 1));
            }
            for (String s : parts) {
                Main.getJDA().getUserById(Main.ornamus).getPrivateChannel().sendMessage(s);
            }

        }
        */
    }
}
