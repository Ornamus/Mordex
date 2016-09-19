package mordex;

import mordex.stringutils.StringUtils;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.VoiceChannel;
import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class Utils {

    public static String filterChars(String s) {
        return s.replaceAll("[^A-Za-z0-9()\\[\\]|. ]", "");
    }

    /**
     * Sets the game the bot is playing.
     * @param game The game.
     */
    public static void setGame(String game) {
        Main.getJDA().getAccountManager().setGame(game);
    }

    /**
     * Gets a VoiceChannel by name from a specific Guild.
     * @param g The Guild the VoiceChannel is in.
     * @param channelName The name of the VoiceChannel.
     * @return The VoiceChannel, or null if it does not exist.
     */
    public static VoiceChannel getVoiceChannel(Guild g, String channelName) {
        for (VoiceChannel v : g.getVoiceChannels()) {
            if (v.getName().equalsIgnoreCase(channelName)) return v;
        }
        return null;
    }

    /**
     * Gets the roles of a User in a Guild.
     * @param g The Guild.
     * @param u The User.
     * @return The User's roles.
     */
    public static List<Role> getRoles(Guild g, User u) {
        return g.getRolesForUser(u);
    }

    /**
     * Checks if a User is an admin role.
     * @param g The Guild the User is in.
     * @param u The User.
     * @return If the User is an admin.
     */
    public static boolean isAdmin(Guild g, User u) {
        return isRole(g, u, "admin") || isRole(g, u, "leader");
    }

    /**
     * Checks if a User is a specific Guild role.
     * @param g The Guild.
     * @param u The User.
     * @param role The role.
     * @return If a user is a specific Guild role.
     */
    public static boolean isRole(Guild g, User u, String role) {
        for (Role r : Utils.getRoles(g, u)) {
            if (r.getName().equalsIgnoreCase(role)) {
                return true;
            }
        }
        return false;
    }

    public static String capitalize(String name) {
        return WordUtils.capitalize(name);
    }

    public static List<String> getAllPlayerNames(Guild g) {
        List<String> usernames = new ArrayList<>();
        for (User u : g.getUsers()) {
            usernames.add(u.getUsername());
            String nickname = g.getNicknameForUser(u);
            if (nickname != null) usernames.add(nickname);
        }
        return usernames;
    }

    /**
     * Gets all the Users in a Guild who's name or nickname matches the string.
     *
     * @param g The Guild.
     * @param name The name.
     * @return All the Users in a Guild who's name or nickname matches the string.
     */
    public static List<User> getUsersMatching(Guild g, String name) {
        List<User> users = new ArrayList<>();
        for (User u : g.getUsers()) {
            if (u.getUsername().equalsIgnoreCase(name)) users.add(u);
        }
        for (User u : g.getUsers()) {
            String nick = g.getNicknameForUser(u);
            if (nick != null && nick.equalsIgnoreCase(name)) {
                if (!users.contains(u)) users.add(u);
            }
        }
        return users;
    }

    /*
    public static String filterASCII(String fil) {
        return fil.replaceAll("[^\\x00-\\x7F]", "");
    }*/

    public static String[] toArray(List<String> list) {
        String[] array = new String[list.size()];
        array = list.toArray(array);
        return array;
    }

    public static <T> T[] toArray(List<T> list, Class tClass) {
        T[] array = (T[]) Array.newInstance(tClass, list.size());
        array = list.toArray(array);
        return array;
    }

    public static List<JSONObject> getJSONObjects(JSONArray a) {
        List<JSONObject> objects = new ArrayList<>();
        for (int i = 0; i < a.length(); i++) {
            JSONObject o = a.getJSONObject(i);
            objects.add(o);
        }
        return objects;
    }

    public static List<Object> getObjects(JSONArray a) {
        List<Object> objects = new ArrayList<>();
        for (int i = 0; i < a.length(); i++) {
            Object o = a.get(i);
            objects.add(o);
        }
        return objects;
    }

    public static void saveFile(URL url, String destinationFile) {
        try {
            InputStream is = url.openStream();
            OutputStream os = new FileOutputStream(destinationFile);

            byte[] b = new byte[2048];
            int length;

            while ((length = is.read(b)) != -1) {
                os.write(b, 0, length);
            }

            is.close();
            os.close();
        } catch (Exception e) {}
    }

    private static Random random = new Random();

    public static int randomInt(int min, int max) {
        int randomNum = random.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}
