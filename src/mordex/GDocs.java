package mordex;

public class GDocs {

    public static String getPage() {
        String result = Main.getHTML("https://docs.google.com/document/d/1vAG6O2Rrso_fWYCrnhMpSIgsItUYPfXRdfsWWB1hMY4/edit"); //","ibi":1},{"ty":"is","s":"\u0003\n","ibi":1349}
        String sub = result.substring(result.indexOf("[{\"ty\":\"is\",\"s\":\"Title Holder:") + 17, result.indexOf("\",\"ibi\":1},{\"ty\":\"is\",\"s\":\"\\u0003\\n\",\"ibi\""));
        //System.out.println("Substring: " + sub);
        return sub;
    }
}