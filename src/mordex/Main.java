package mordex;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.managers.AccountManager;

public class Main {

    public static final boolean DEBUG = false;
    private static JDA jda;
    public static final List<String> admins = new ArrayList<>();
    public static final String version = "1.0.4";
    public static final String ornamus = "111570080105541632";

    public static void main(String[] args) {
        try {
            String token;
            if (DEBUG) {
                token = "MjI1Njk5NzQ5MTA4NTE0ODI3.Crs2yQ.WjDSHIK83U4BevgTDxsIjiemUIs";
                System.out.println("BOOTING IN DEVELOPMENT MODE");
            } else {
                token = "MjI0ODY4MjU0NDk2MTk0NTYw.Crgwvw.ktVQ4G5mAH_CPsKI4SMDYapia0o";
            }
            jda = new JDABuilder().setBotToken(token).addListener(new Listener()).buildBlocking();
            AccountManager acc = jda.getAccountManager();
            acc.reset();
            acc.update();
            if (DEBUG) {
                acc.setGame("Development Build");
            } else {
                acc.setGame("BMG Beta API");
            }
            acc.setIdle(false);
            acc.update();
            if (new File("users.txt").exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
                    String line = br.readLine();

                    while (line != null) {
                        String[] data = line.split("=");
                        Listener.DIDToBHID.put(data[0], Integer.parseInt(data[1]));
                        line = br.readLine();
                    }
                }
            }
            if (new File("challenges.txt").exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader("challenges.txt"))) {
                    String line = br.readLine();

                    while (line != null) {
                        String[] data = line.split("chal");
                        System.out.println(data[0] + " chal " + data[1]);
                        Challenge c = new Challenge(jda.getUserById(data[0]), jda.getUserById(data[1]));
                        Listener.challenges.add(c);
                        line = br.readLine();
                    }
                }
            }
            admins.add(ornamus);
            admins.add("131098385599037440"); //Aiden
            admins.add("126221145144950784"); //Yammah
            admins.add("110878384891936768"); //LegitPunisher

            Listener.challenges.add(new Challenge(jda.getUserById("111570080105541632"), jda.getUserById("111570080105541632")));
            //Listener.challenges.add(new Challenge(jda.getUserById("126221145144950784"), jda.getUserById("111570080105541632")));
            Listener.challenges.add(new Challenge(jda.getUserById(jda.getSelfInfo().getId()), jda.getUserById("111570080105541632")));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JDA getJDA() {
        return jda;
    }

    public static String getHTML(String s) {
        try {
            s = s.replace(" ", "%20");

            URLConnection uc = new URL(s).openConnection();

            uc.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");

            InputStream is = uc.getInputStream();

            int ptr = 0;
            StringBuffer buffer = new StringBuffer();
            while ((ptr = is.read()) != -1) {
                buffer.append((char) ptr);
            }
            return buffer.toString();
        } catch (IOException i) {
            System.out.println("ERROR GETTING HTML FOR \"" + s + "\"");
            i.printStackTrace();
            return "ERROR GETTING HTML";
        }
    }
}

