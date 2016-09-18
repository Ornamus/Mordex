package mordex.commands.league;

import mordex.Challenge;
import mordex.GDocs;
import mordex.Listener;
import mordex.Main;
import mordex.commands.Command;
import mordex.searching.DiscordGetResult;
import mordex.searching.PlayerGetResult;
import mordex.searching.PlayerGetter;
import mordex.wrappers.LeaguePage;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class LeagueChallengeCommand extends Command {

    //TODO: 2v2 support if it doesn't get scrapped by Yammah
    //TODO: Rewrite to be better

    public LeagueChallengeCommand() {
        super("!league challenge");
    }

    @Override
    public void run(String message, MessageReceivedEvent e) {
        if (message.length() > 0) {
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
                    if (player == null) System.out.println("Unranked player \"" + e.getAuthor().getUsername() + "\" is challenging anyone in the U.A. league");
                    if (Math.abs(rankDiff) <= 14) {
                        DiscordGetResult discordResult = PlayerGetter.getDiscordUser(otherPlayer.name, Main.getJDA().getUsers());

                        System.out.println("Result: " + discordResult.result);
                        //if (users.size() == 1) {
                        if (discordResult.result == DiscordGetResult.SUCCESS) {
                            User otherUser = discordResult.user;
                            if (!Main.FAKE_CHALLENGING) otherUser.getPrivateChannel().sendMessage(e.getAuthor().getAsMention() + " has challenged you to a U.A. 1v1 League match! Here are your options:\n\n" +
                                    "`!league accept` -- This will accept the challenge and notify a U.A. Match Ref.\n\n" +
                                    "`!league deny` -- This will deny the challenge.");
                            e.getChannel().sendMessage("Your challenge has been sent to " + otherUser.getAsMention() + ", " + e.getAuthor().getAsMention() + ".");
                            Listener.challenges.add(new Challenge(e.getAuthor(), otherUser));
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
    }
}
