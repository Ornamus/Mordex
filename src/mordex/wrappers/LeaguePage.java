package mordex.wrappers;

import java.util.ArrayList;
import java.util.List;

public class LeaguePage {

    public List<PageEntry> entries = new ArrayList<>();

    public LeaguePage(String data) {
        data = data.replace("\\t", "&");
        data = data.replace("\\n\\n\\n", "NEWENTRY");
        data = data.replace("\\n", "NEWENTRY");
        while (data.contains("&&")) {
            data = data.replace("&&", "&");
        }
        //System.out.println("New data: " + data);
        String[] parts = data.split("NEWENTRY");
        for (String s : parts) {
            String part = s;
            int rank = -1;
            if (part.startsWith("Title Holder:")) {
                part = part.replace("Title Holder:", "0.");
            }
            if (part.contains(".")) {
                rank = Integer.parseInt(part.substring(0, part.indexOf(".")));
            }
            if (rank != -1) {
                String[] args = part.split("&");
                String name = args[0].substring(args[0].indexOf(" "));
                while (name.startsWith(" ")) name = name.replaceFirst(" ", "");
                while (name.endsWith(" ")) name = name.substring(0, name.length() - 1);
                String[] matchData = args[1].split("/");
                int wins = Integer.parseInt(matchData[0]);
                int losses = Integer.parseInt(matchData[1]);
                int ties = Integer.parseInt(matchData[2]);
                double points = Double.parseDouble(args[2]);
                entries.add(new PageEntry(rank, name, wins, losses, ties, points));
            }
        }

        /*
        for (PageEntry e : entries) {
            System.out.println("=====NEW ENTRY====");
            System.out.println(e.rank + ": " + e.name);
            System.out.println(e.wins + "-" + e.losses + (e.ties > 0 ? ("-" + e.ties) : "") + " (" + e.points + " points)");
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

        public final int rank, wins, losses, ties;
        public final String name;
        public final double points;
        public final boolean hasTies;

        public PageEntry(int rank, String name, int wins, int losses, int ties, double points) {
            this.rank = rank;
            this.name = name;
            this.wins = wins;
            this.losses = losses;
            this.ties = ties;
            this.points = points;
            hasTies = ties > 0;
        }

        public String getInfo() {
            String response = "```Markdown\n" +
                    "# Player Name: " + name + "\n\n" +
                    "Region: " + "Ultimate Alliance 1v1 League\n" +
                    "Rank: " + (rank > 0 ? rank : "Title Holder") + "\n" +
                    getWinLossTieDisplay() + ": " + getWinLossTie() + "\n" +
                    "Points: " + points + "\n" +
                    "```";
            return response;
        }

        public String getWinLossTieDisplay() {
            return "Win/Loss" + (hasTies ? "/Tie" : "");
        }

        public String getWinLossTie() {
            return wins + "/" + losses + (hasTies ? ("/" + ties) : "");
        }
    }
}
