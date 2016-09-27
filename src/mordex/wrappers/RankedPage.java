package mordex.wrappers;

import mordex.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RankedPage {

    private final List<RankedEntry> entries = new ArrayList<>();

    public RankedPage(JSONArray array) {
        for (JSONObject o : Utils.getJSONObjects(array)) {
            entries.add(new RankedEntry(o));
        }
    }

    public int getEntryCount() {
        return entries.size();
    }

    public RankedEntry getEntry(int index) {
        return entries.get(index);
    }

    public List<RankedEntry> getEntries() {
        return new ArrayList<>(entries);
    }

    public class RankedEntry {
        public String name, region, tier, best_legend;
        public int rank, elo, peak_elo, bhid, games, wins, losses;

        public RankedEntry(JSONObject o) {
            //System.out.println(o.toString());
            name = Utils.fixEncoding(o.getString("name"));
            if (!o.isNull("region")) region = o.getString("region");
            else region = null;
            tier = o.getString("tier");
            rank = o.getInt("rank");
            elo = o.getInt("rating");
            peak_elo = o.getInt("peak_rating");
            bhid = o.getInt("brawlhalla_id");
            games = o.getInt("games");
            wins = o.getInt("wins");
            losses = games - wins;
        }
        /*/**
     * "rank": "58",
     "name": "Ornamus",
     "brawlhalla_id": 101175,
     "best_legend": 12,
     "best_legend_games": 273,
     "best_legend_wins": 196,
     "rating": 2429,
     "tier": "Diamond",
     "games": 273,
     "wins": 196,
     "region": "US-E",
     "peak_rating": 2454
     */
    }
}
