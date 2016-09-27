package mordex.wrappers;

import mordex.Utils;
import org.json.JSONObject;

public class PlayerStats {

    public final boolean init;
    public final String name;
    public final int level, games, wins, losses, bhid;
    public final double xpPercent;
    //TODO: the rest of the stats
    //https://api.brawlhalla.com/player/101175/stats?api_key=B7Q4OLXXOVPQ3VGQ5XAYIMBXK9UQC

    public PlayerStats(JSONObject o) {
        if (!o.isNull("name")) {
            name = Utils.fixEncoding(o.getString("name"));
            xpPercent = o.getDouble("xp_percentage");
            level = o.getInt("level");
            games = o.getInt("games");
            wins = o.getInt("wins");
            losses = games - wins;
            bhid = o.getInt("brawlhalla_id");
            init = true;
        } else {
            name = "Unknown";
            xpPercent = 0;
            level = -1;
            games = 0;
            wins = 0;
            losses = 0;
            bhid = -1;
            init = false;
        }
    }
}
