package mordex.wrappers;

import mordex.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class PlayerRanked {

    public final String name, tier, region;
    public final int elo, peak_elo, games, wins, losses, bhid;
    public final int region_rank, global_rank;
    public final List<LegendRanked> legends = new ArrayList<>();
    public final boolean init;

    public PlayerRanked(JSONObject o) {
        if (!o.isNull("name")) {
            Utils.fixEncoding(name = o.getString("name"));
            elo = o.getInt("rating");
            peak_elo = o.getInt("peak_rating");
            tier = o.getString("tier");
            region = o.getString("region");
            games = o.getInt("games");
            wins = o.getInt("wins");
            bhid = o.getInt("brawlhalla_id");
            losses = games - wins;
            region_rank = o.getInt("region_rank");
            global_rank = o.getInt("global_rank");
            JSONArray legendsJson = o.getJSONArray("legends");
            for (int i = 0; i < legendsJson.length(); i++) {
                JSONObject legend = legendsJson.getJSONObject(i);
                legends.add(new LegendRanked(legend));
            }
            init = true;
        } else {
            name = "Unknown";
            elo = -1;
            peak_elo = -1;
            tier = "Unknown 0";
            region = "Unknown";
            games = 0;
            wins = 0;
            bhid = 0;
            losses = 0;
            region_rank = 0;
            global_rank = 0;
            init = false;
        }
    }
}
