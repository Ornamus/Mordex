package mordex.stringutils;

import net.dv8tion.jda.entities.User;
import java.util.List;

public class UserSearchObject extends SearchObject {

    public final User user;

    public UserSearchObject(User u, List<String> list) {
        super(u.getId(), list);
        user = u;
    }
}
