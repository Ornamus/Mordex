package mordex.wrappers;

import java.util.ArrayList;
import java.util.List;

public class LeaguePage {

    public List<PageEntry> entries = new ArrayList<>();

    //TODO: Bot gives error message when leaguepage formatting gets broken
    public LeaguePage(String pageRaw) {
        String data = pageRaw;
        data = data.replace("The rankings and seeding\\n\\n", "");
        while (data.contains("\\n\\n")) data = data.replace("\\n\\n", "\\n");
        data = data.replace("\\n", "NEWENTRY");

        while (data.contains("\\t\\t")) data = data.replace("\\t\\t", "\\t");
        data = data.replace("\\t", "&");

        System.out.println("New data: " + data);
        String[] parts = data.split("NEWENTRY");
        int rank = 1;
        for (String s : parts) {
            String part = s;
            if (part.contains("Points")) {
                while (part.contains(" Points")) part = part.replace(" Points", "Points");
                String name = part.substring(0, part.indexOf("Points"));
                if (name.endsWith("&")) name = name.substring(0, name.length() - 1);
                while (name.endsWith(" ")) name = name.substring(0, name.length()-1);
                String pointsString = part.substring(part.indexOf("Points") + 7);
                if (pointsString.contains(" ")) pointsString = pointsString.replace(" ", "");
                //System.out.println("PointsString: " + pointsString);
                Integer points = null;
                try {
                    points = Integer.parseInt(pointsString);
                } catch (NumberFormatException ex) {
                    System.out.println("number format exception :(");
                }
                if (points != null) {
                    entries.add(new PageEntry(name, rank, points));
                }
            }
            rank++;
        }

        /*
        for (PageEntry e : entries) {
            System.out.println("=====NEW ENTRY====");
            System.out.println(e.rank + ": " + e.name + " - Points: " + e.points);
            //System.out.println(e.wins + "-" + e.losses + (e.ties > 0 ? ("-" + e.ties) : "") + " (" + e.points + " points)");
        }
        System.out.println("entries done");
        */

    }

    public List<String> getAllNames() {
        List<String> names = new ArrayList<>();
        for (PageEntry e : entries) {
            names.add(e.name);
        }
        return names;
    }

    public PageEntry getByRank(int rank) {
        for (PageEntry e : entries) {
            if (e.rank == rank) {
                return e;
            }
        }
        return null;
    }

    public PageEntry getByName(String name) {
        for (PageEntry e : entries) {
            if (e.name.equalsIgnoreCase(name)) {
                return e;
            }
        }
        return null;
    }

    public class PageEntry {

        public final String name;
        public final int rank;
        public final double points;

        public PageEntry(String name, int rank, double points) {
            this.name = name;
            this.rank = rank;
            this.points = points;
        }

        public String getInfo() {
            String response = "```Markdown\n" +
                    "# Player Name: " + name + "\n\n" +
                    "Region: " + "Yammah's Invitational Tourneys\n" +
                    "Rank: " + (rank > 0 ? rank : "Title Holder") + "\n" +
                    "Points: " + points + "\n" +
                    "```";
            return response;
        }
    }
}
