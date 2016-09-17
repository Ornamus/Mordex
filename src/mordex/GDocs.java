package mordex;

public class GDocs {

    public static String getPage() {
        String result = Main.getHTML("https://docs.google.com/document/d/1vAG6O2Rrso_fWYCrnhMpSIgsItUYPfXRdfsWWB1hMY4/edit");
        String sub = result.substring(result.indexOf("\"s\":\"Title Holder:") + 5); //17
        //System.out.println("Origin size " + result.length() + ", new size " + sub.length());
        //System.out.println("First sub: " + sub);
        sub = sub.substring(0, sub.indexOf("Week Contender:"));
        //System.out.println("Origin size " + result.length() + ", new size " + sub.length());
        //System.out.println("Second sub: " + sub);
        //System.out.println("Substring: " + sub);
        return sub;
    }
}