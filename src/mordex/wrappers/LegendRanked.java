package mordex.wrappers;

import mordex.Utils;
import org.json.JSONObject;

public class LegendRanked {

    public final String name, tier;
    public final int id, elo, peak_elo, games, wins, losses;

    public LegendRanked(JSONObject o) {
        name = Utils.capitalize(o.getString("legend_name_key"));
        tier = o.getString("tier");
        id = o.getInt("legend_id");
        elo = o.getInt("rating");
        peak_elo = o.getInt("peak_rating");
        games = o.getInt("games");
        wins = o.getInt("wins");
        losses = games - wins;
    }
}
