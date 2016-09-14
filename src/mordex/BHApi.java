package mordex;

import mordex.wrappers.PlayerRanked;
import mordex.wrappers.RankedPage;
import org.json.JSONArray;
import org.json.JSONObject;

public class BHApi {

    private static final String url = "https://api.brawlhalla.com/";
    private static final String api_key = "B7Q4OLXXOVPQ3VGQ5XAYIMBXK9UQC";

    public static String getPlayerStatsRaw(int bhID) {
        return Main.getHTML(url + "player/" + bhID + "/stats?api_key=" + api_key);
    }

    public static PlayerRanked getPlayerRanked(int bhID) {
        return new PlayerRanked(new JSONObject(Main.getHTML(url + "player/" + bhID + "/ranked?api_key=" + api_key)));
    }

    public static RankedPage getRankedSearch(String name) {
        String fullurl = url + "rankings/1v1/all/1?name=" + name + "&api_key=\"" + api_key;
        //System.out.println("Going to: " + fullurl);
        String result = Main.getHTML(fullurl);
        //System.out.println(result);
        return new RankedPage(new JSONArray(result));
    }

    public static JSONObject steamIDToBHID(String steamID) {
        String response = Main.getHTML(url + "search?steamid=" + steamID + "&api_key=" + api_key);
        if (response.equals("[]")) {
            response = "{}";
        }
        return new JSONObject(response);
    }
}