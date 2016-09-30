package mordex.wrappers;

import mordex.Utils;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Clan {

    public final String name;
    public final int id, xp;
    public final Date createDate;
    public final boolean init;

    public final List<ClanEntry> members = new ArrayList<>();

    public Clan(JSONObject o) {
        if (!o.isNull("clan_id")) {
            name = o.getString("clan_name");
            id = o.getInt("clan_id");
            xp = o.getInt("clan_xp");
            createDate = Utils.toDate(o.getInt("clan_create_date"));
            for (JSONObject mem : Utils.getJSONObjects(o.getJSONArray("clan"))) {
                members.add(new ClanEntry(mem));
            }
            init = true;
        } else {
            name = "Unknown";
            id = -1;
            xp = 0;
            createDate = new Date();
            init = false;
        }
    }

    public class ClanEntry {

        public final String name, rank;
        public final int bhid, xp;
        public final Date joinDate;

        public ClanEntry(JSONObject o) {
            name = Utils.fixEncoding(o.getString("name"));
            rank = o.getString("rank");
            bhid = o.getInt("brawlhalla_id");
            xp = o.getInt("xp");
            joinDate = Utils.toDate(o.getInt("join_date"));
        }
    }
}
