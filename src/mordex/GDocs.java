package mordex;

public class GDocs {

    public static String getPage() {
        String result = Main.getHTML("https://docs.google.com/document/d/1I4wAYLIQhpUNTQxfu3Rp3A9tgueRYaBLr8f4AsfK21o/edit");
        result = result.replaceFirst("The rankings and seeding", "");
        String sub = result.substring(result.indexOf("The rankings and seeding")); //17
        //System.out.println("Origin size " + result.length() + ", new size " + sub.length());
        System.out.println("First sub: " + sub.length());
        sub = sub.substring(0, sub.indexOf("\""));
        System.out.println("Second sub: " + sub.length());
        System.out.println(sub);
        //System.out.println("Origin size " + result.length() + ", new size " + sub.length());
        //System.out.println("Substring: " + sub);
        return sub;
    }
}