package mordex.stringutils;

import java.util.List;

public class SearchObject {

    public String id;
    public List<String> text;

    public SearchObject(String s, List<String> list) {
        id = s;
        text = list;
    }
}
