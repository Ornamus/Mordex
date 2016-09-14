package mordex.searching;

import net.dv8tion.jda.entities.User;

public class DiscordGetResult {

    public final User user;
    public final int result;

    public static final int SUCCESS = 0;
    public static final int TOO_MANY_PERFECT_RESULTS = 1;
    public static final int TOO_MANY_PARTIAL_RESULTS = 2;
    public static final int NO_RESULTS = 3;

    public DiscordGetResult(int r, User u) {
        user = u;
        result = r;
    }
}

