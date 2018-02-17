import java.io.File;
import java.io.IOException;

class Launcher {

    static void connectTo(String IP) {
        try {
            @SuppressWarnings("unused")
            Process process = Runtime.getRuntime().exec(Parser.path + "\\MOHAA.exe"
                    + " set cl_playintro 0 +connect " + IP,
                    null,
                    new File(Parser.path));

            System.exit(0);

        } catch(IOException e) {
            e.printStackTrace();

            NotificationManager.displayMessageDialog("MOHAA.exe not found. Please restart MOHAA Launcher " +
                    "and choose correct game directory.");

            FilesManager.initConfigFileWithPath("");
            System.exit(1);
        }
    }
    
    static void launchGame() {
        try {
            @SuppressWarnings("unused")
            Process process = Runtime.getRuntime().exec(Parser.path + "\\MOHAA.exe +set cl_playintro 0",
                    null, 
                    new File(Parser.path));

            System.exit(0);

        } catch(IOException e) {
            e.printStackTrace();

            NotificationManager.displayMessageDialog("MOHAA.exe not found. Please restart MOHAA Launcher " +
                    "and choose correct game directory.");

            FilesManager.initConfigFileWithPath("");
            System.exit(1);
        }
    }
}
