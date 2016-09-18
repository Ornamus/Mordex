package mordex.searching;

import net.dv8tion.jda.entities.User;

public class DiscordGetResult {

    public final User user;
    public final int result;

    public static final int SUCCESS = 0;
    public static final int TOO_MANY_RESULTS = 1;
    public static final int NO_RESULTS = 2;

    public DiscordGetResult(int r, User u) {
        user = u;
        result = r;
    }
}

