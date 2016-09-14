package mordex;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Updater {

    public static void main(String[] args) {
        try {
            URL gtDownload = new URL("Direct Download Link");
            saveFile(gtDownload, "bot.jar");
            System.out.println("[MordexUpdate] Downloaded bot.");
            File libFile = new File("lib");
            if (!libFile.exists()) {
                libFile.mkdirs();
            }
            Runtime.getRuntime().exec("java -jar bot.jar");
            System.out.println("[MordexUpdate] Launched bot");
            System.exit(-1); //Is this the correct exit coe?
        } catch (IOException ex) {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void saveFile(URL url, String destinationFile) throws IOException {
        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(destinationFile);

        byte[] b = new byte[2048];
        int length;

        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }

        is.close();
        os.close();
    }
}