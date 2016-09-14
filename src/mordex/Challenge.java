package mordex;

import net.dv8tion.jda.entities.User;

public class Challenge {

    public User a, b;
    public User creator;

    public Challenge(User creator, User b) {
        this.creator = creator;
        a = creator;
        this.b = b;
    }

    public boolean isIn(User u) {
        return a.getId().equals(u.getId()) || b.getId().equals(u.getId());
    }

    public boolean isCreator(User u) {
        return creator.getId().equals(u.getId());
    }

    public User getOther(User u) {
        if (a.getId().equals(u.getId())) return b;
        else return a;
    }
}