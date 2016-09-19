package mordex;

import mordex.challonge.QuickCommand;
import mordex.commands.*;
import mordex.commands.league.*;
import mordex.commands.tournament.TournamentCreateCommand;
import mordex.commands.tournament.TournamentGetCommand;
import mordex.commands.tournament.TournamentJoinCommand;
import mordex.searching.DiscordGetResult;
import mordex.searching.PlayerGetResult;
import mordex.searching.PlayerGetter;
import mordex.wrappers.LeaguePage;
import mordex.wrappers.LegendRanked;
import mordex.wrappers.PlayerRanked;
import mordex.wrappers.RankedPage;
import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.dv8tion.jda.utils.AvatarUtil;
import org.json.JSONObject;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Exchanger;

public class Listener extends ListenerAdapter {

    public static HashMap<String, Integer> DIDToBHID = new HashMap<>();
    public static List<Challenge> challenges = new ArrayList<>();
    public static List<Command> commands = new ArrayList<>();

    public static final String tuaguild = "192497024422248448";

    public Listener() {
        commands.add(new BHIDCommand());
        commands.add(new RankCommand());
        /*
        commands.add(new LeagueChallengesCommand());
        commands.add(new LeagueChallengeCommand());
        commands.add(new LeagueAcceptCommand());
        commands.add(new LeagueDenyCommand());
        commands.add(new LeagueWithdrawCommand());
        */
        commands.add(new LeagueCommand());
        /*
        //commands.add(new TournamentCreateCommand());
        //commands.add(new TournamentJoinCommand());
        //commands.add(new TournamentGetCommand());
        */
        commands.add(new QuickCommand("!version").setHandler((message, e) -> e.getChannel().sendMessage("Current version: " + Main.version)));
        commands.add(new QuickCommand("!help", "!documentation").setHandler((message, e) -> e.getChannel().sendMessage("Mordex documentation: " + "https://notehub.org/g1ciy")));
        commands.add(new QuickCommand("!grill", "!anime", "!waifu").setHandler((message, e) -> e.getChannel().sendMessage("http://i.imgur.com/XPX7YtD.png")));
        commands.add(new QuickCommand("!update", "!shutdown").setHandler((message, e) -> { //TODO: move to UpdateCommand class
            message = e.getMessage().getContent(); //Get the original, unaltered message
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
                    e.getChannel().sendMessage("Shutting down to update! Be right back!");
                    System.exit(0);
                } catch (Exception exc) {
                    System.out.println("Shutdown file save exception!");
                    exc.printStackTrace();
                }
            }
        }));
        commands.add(new QuickCommand("!avatar").setHandler((message, e) -> {
            if (e.getAuthor().getId().equals(Main.ornamus)) {
                if (message.length() > 0) {
                    try {
                        Utils.saveFile(new URL(message), "avatar.png");
                        Main.getJDA().getAccountManager().setAvatar(AvatarUtil.getAvatar(new File("avatar.png")));
                        Main.getJDA().getAccountManager().update();
                        e.getChannel().sendMessage("New avatar set!");
                    } catch (Exception ex) {
                        e.getChannel().sendMessage("Invalid URL!");
                    }
                } else {
                    e.getChannel().sendMessage("You need to supply a URL to an image!");
                }
            }
        }));
        commands.add(new QuickCommand("!chat").setHandler((message, e) -> {
            if (e.getAuthor().getId().equals(Main.ornamus)) {
                String channelID = message.split(" ")[0];
                TextChannel chan = Main.getJDA().getTextChannelById(channelID);
                chan.sendTyping();
                message = message.replace(channelID, "");
                while (message.startsWith(" ")) message = message.replaceFirst(" ", "");
                chan.sendMessage(message);
            }
        }));
        commands.add(new QuickCommand("( ͡° ͜ʖ ͡°)", "!lenny").setHandler((message, e) -> e.getChannel().sendMessage("( ͡° ͜ʖ ͡°)")).setTriggerOnSelf(false));
        //TODO: LeagueWithdrawCommand
    }

    //TODO: !league withdraw (Player Name) - Takes back a challenge you sent out
    //TODO: You can only do two league matches an hour

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        String message = e.getMessage().getContent();
        for (Command c : commands) {
            for (String s : c.starts) {
                if (message.toLowerCase().startsWith(s)) {
                    if (c.triggerOnSelf || (!e.getAuthor().getId().equals(Main.getJDA().getSelfInfo().getId()))) {
                        e.getChannel().sendTyping();
                        message = message.substring(s.length(), message.length());
                        //message = Utils.filterASCII(message);
                        while (message.startsWith(" ")) message = message.replaceFirst(" ", "");
                        c.run(message, e);
                        return;
                    }
                }
            }
        }
    }
}
