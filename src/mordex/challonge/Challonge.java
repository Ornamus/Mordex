package mordex.challonge;

import mordex.Main;
import mordex.Utils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Challonge {

    private static final String api_key = "Jp2Tnaf2jnjzL6xVOBIAyz7S6JpUNsIHZKBDD2VM";

    public static final String SINGLE_ELIMINATION = "Single elimination";
    public static final String DOUBLE_ELIMINATION = "double elimination";
    public static final String ROUND_ROBIN = "round robin";
    public static final String SWISS = "swiss";

    public static JSONObject makeTournament(String name, String url, String type, String description, Boolean openSignup, Boolean isPrivate) {
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("api_key", api_key));
        pairs.add(new BasicNameValuePair("tournament[name]", name));
        pairs.add(new BasicNameValuePair("tournament[url]", url));
        if (type != null) pairs.add(new BasicNameValuePair("tournament[tournament_type]", type));
        if (description != null) pairs.add(new BasicNameValuePair("tournament[description]", description));
        if (openSignup != null) pairs.add(new BasicNameValuePair("tournament[open_signup]", openSignup + ""));
        if (isPrivate != null) pairs.add(new BasicNameValuePair("tournament[private]", isPrivate + ""));

        JSONObject o = new JSONObject(Main.postTo("https://api.challonge.com/v1/tournaments.json", pairs));
        //System.out.println(o.toString());
        JSONObject result = new JSONObject();
        if (!o.isNull("tournament")) {
            o = o.getJSONObject("tournament");
            if (!o.isNull("id")) {
                System.out.println("ID: " + o.getInt("id"));
                result.accumulate("success", true);
                result.accumulate("id", o.getInt("id") + "");
                result.accumulate("tournament", o);
            }
        }
        if (result.isNull("success")) {
            result.accumulate("success", false);
            result.accumulate("errors", o.get("errors"));
        }
        if (!result.isNull("errors")) {
            for (Object e : Utils.getObjects(result.getJSONArray("errors"))) {
                System.out.println("Challonge make bracket error: " + e.toString());
            }
        }
        //System.out.println("Result: " + result.toString());
        return result;
    }

    public static JSONObject addParticipant(String tourneyID, String username, String nickname, String email, Integer seed, String misc) {
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("api_key", api_key));
        if (email == null && username == null && nickname == null) System.out.println("[ERROR]: Email, username, and nickname are all null for a tournament participant!");
        if (username != null) pairs.add(new BasicNameValuePair("participant[challonge_username]", username));
        if (nickname != null) pairs.add(new BasicNameValuePair("participant[name]", nickname));
        if (email != null) pairs.add(new BasicNameValuePair("participant[email]", email));
        if (seed != null) pairs.add(new BasicNameValuePair("participant[seed]", seed + ""));
        if (misc != null) pairs.add(new BasicNameValuePair("participant[musc]", misc));
        JSONObject o = new JSONObject(Main.postTo("https://api.challonge.com/v1/tournaments/" + tourneyID + "/participants.json", pairs));
        System.out.println(o.toString());
        JSONObject response = new JSONObject();
        if (!o.isNull("participant")) {
            response.accumulate("success", true);
        } else {
            response.accumulate("success", false);
            response.accumulate("errors", o.get("errors"));
        }
        if (!response.isNull("errors")) {
            for (Object e : Utils.getObjects(response.getJSONArray("errors"))) {
                System.out.println("Challonge add participant: " + e.toString());
            }
        }
        return response;
    }
}
