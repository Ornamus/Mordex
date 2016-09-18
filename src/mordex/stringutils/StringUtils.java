package mordex.stringutils;

import mordex.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StringUtils {

    public static List<String> getWords(String message) {
        List<String> words = new ArrayList<String>();
        for (String s : message.split(" ")) {
            words.add(s);
        }
        return words;
    }

    //Probation
    public static String getWord(String message, int word) {
        int currentWord = 0;
        for (String s : getWords(message)) {
            if (currentWord == word) {
                return s;
            } else {
                currentWord++;
            }
        }
        return null;
    }

    public static boolean contains(String message, String word) {
        message = Utils.filterASCII(message);
        word = Utils.filterASCII(word);
        for (String s : getWords(message)) {
            if (s.equalsIgnoreCase(word)) {
                return true;
            } else {
                if (s.replace("?", "").replace("!", "").replace(".", "").replace(",", "").equalsIgnoreCase(word)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<String> contains(String message, String... list) {
        message = Utils.filterASCII(message);
        List<String> objects = new ArrayList<String>();
        for (String s : list) {
            s = Utils.filterASCII(s);
            if (contains(message, s)) {
                objects.add(s);
            }
        }
        if (!objects.isEmpty()) {
            return objects;
        }
        return null;
    }

    public static <T extends SearchObject> List<T> containsPhrase(String message, T...searchObjects) {
        List<T> objects = new ArrayList<>();
        message = Utils.filterASCII(message);
        for (T t : searchObjects) {
            for (String s : t.text) {
                HashMap<String, String> result = containsPhrase(message, s);
                if (result.size() == 1) {
                    if (!objects.contains(t)) objects.add(t);
                }
            }
        }
        return objects;
    }

    public static HashMap<String, String> containsPhrase(String message, String... array) {
        message = Utils.filterASCII(message);
        HashMap<String, Integer> matches = new HashMap<>();
        HashMap<String, String> results = new HashMap<>();
        for (String o : array) {
            o = Utils.filterASCII(o);
            int partMatches = 0;
            List<String> phraseWords = getWords(o);
            for (String s : phraseWords) {
                if (contains(message, s)) {
                    if (results.get(o) == null) {
                        results.put(o, s);
                    } else {
                        results.put(o, results.get(o) + " " + s);
                    }
                    partMatches++;
                }
            }
            matches.put(o, partMatches);
        }
        HashMap<String, String> sortedResults = new HashMap<>();
        if (!results.isEmpty()) {
            while (!results.isEmpty()) {
                int highestMatches = 0;
                String highestS = null;
                for (String s : results.keySet()) {
                    int partMatches = matches.get(s);
                    if (partMatches > highestMatches) {
                        highestS = s;
                        highestMatches = partMatches;
                    }
                }
                sortedResults.put(highestS, results.get(highestS));
                results.remove(highestS);
            }
            return sortedResults;
        }
        return new HashMap<>();
    }
}
