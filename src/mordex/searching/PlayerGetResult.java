package mordex.searching;

public class PlayerGetResult {

    public final String name;
    public final int result;

    public static final int SUCCESS = 0;
    public static final int TOO_MANY_PERFECT_RESULTS = 1;
    public static final int TOO_MANY_PARTIAL_RESULTS = 2;
    public static final int NO_RESULTS = 3;

    public PlayerGetResult(int r, String n) {
        name = n;
        result = r;
    }
}
