package mordex.searching;

import mordex.Main;
import mordex.stringutils.StringUtils;
import mordex.Utils;
import mordex.stringutils.UserSearchObject;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerGetter {

    //TODO: refactor to getName and NameGetResult
    public static PlayerGetResult getPlayer(String name, List<String> nameList) {
        name = Utils.filterASCII(name);
        HashMap<String, String> results = StringUtils.containsPhrase(name, Utils.toArray(nameList));
        if (!results.isEmpty()) {
            String fullName = null;
            if (results.size() == 1) {
                fullName = results.keySet().toArray()[0].toString();
            } else {
                int matches = 0;
                for (String s : results.keySet()) {
                    if (name.equalsIgnoreCase(s)) {
                        fullName = s;
                        matches++;
                    }
                }
                if (matches > 1) {
                    return new PlayerGetResult(1, null);
                } else if (matches == 0) {
                    return new PlayerGetResult(2, null);
                }
            }
            return new PlayerGetResult(0, fullName);
        } else {
            return new PlayerGetResult(3, null);
        }
    }

    public static DiscordGetResult getDiscordUser(String name, List<User> users) {
        name = Utils.filterASCII(name);
        //HashMap<User, List<String>> userNames = new HashMap<>();

        List<UserSearchObject> allUsers = new ArrayList<>();
        for (User u : users) {
            List<String> names = new ArrayList<>();
            names.add(u.getUsername());
            for (Guild g : Main.getJDA().getGuilds()) {
                if (g.isMember(u)) {
                    String nick = g.getNicknameForUser(u);
                    if (nick != null) names.add(nick);
                }
            }
            allUsers.add(new UserSearchObject(u, names));
        }
        List<UserSearchObject> results = StringUtils.containsPhrase(name, Utils.toArray(allUsers, UserSearchObject.class));
        if (results.size() > 0) {
            if (results.size() == 1) {
                return new DiscordGetResult(0, results.get(0).user);
            } else {
                return new DiscordGetResult(1, null);
            }
        } else {
            return new DiscordGetResult(2, null);
        }
    }

    /*
    public static DiscordGetResult getDiscordUser(String name, List<User> users) {
        name = Utils.filterASCII(name);
        List<String> nameList = new ArrayList<>();
        //HashMap<User, List<String>> userNames = new HashMap<>();
        for (User u : users) {
            nameList.add(u.getUsername());
        }
        User u = null;
        //TODO: StringUtils.containsPhrase that take a list of customizable objects thatat least have a string and an indentifier and returns a list of those objects.
        //TODO: This will allow for finding a user's name and being able to keep the name affiliated with the user instead of finding the user again.
        HashMap<String, String> results = StringUtils.containsPhrase(name, Utils.toArray(nameList));
        if (!results.isEmpty()) {
            String fullName = null;
            if (results.size() == 1) {
                for (String s : results.keySet()) {
                    for (User whichUser : users) {
                        if (whichUser.getUsername().equals(s)) {
                            u = whichUser;
                            break;
                        }
                    }
                }
            } else {
                int matches = 0;
                int index = 0;
                for (String s : results.keySet()) {
                    if (name.equalsIgnoreCase(s)) {
                        u = users.get(index);
                        matches++;
                    }
                    index++;
                }
                if (matches > 1) {
                    return new DiscordGetResult(1, null);
                } else if (matches == 0) {
                    return new DiscordGetResult(2, null);
                }
            }
            return new DiscordGetResult(0, u);
        } else {
            return new DiscordGetResult(3, null);
        }
    }*/
}
