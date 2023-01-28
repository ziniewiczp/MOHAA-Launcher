import java.io.File;
import java.io.IOException;
import java.util.HashMap;

class Launcher {

    private static final HashMap<String, String> gameMapping = new HashMap<>();

    static {
        gameMapping.put("Medal of Honor: Allied Assault", "\\MOHAA.exe");
        gameMapping.put("Medal of Honor: Allied Assault Spearhead", "\\moh_spearhead.exe");
        gameMapping.put("Medal of Honor: Allied Assault Breakthrough", "\\moh_breakthrough");
    }

    static void playMultiplayer(String ip) {
        launchGame(" set cl_playintro 0 +connect " + ip);
    }

    static void playSingleplayer() {
        launchGame(" +set cl_playintro 0");
    }

    private static void launchGame(String parameters) {
        try {
            Runtime.getRuntime().exec(Parser.path
                + gameMapping.get(GUI.getCurrentlySelectedGame())
                + parameters,
                null,
                new File(Parser.path));

            System.exit(0);

        } catch(IOException e) {
            e.printStackTrace();

            NotificationManager.displayMessageDialog("Game executable not found. Please restart MOHAA Launcher " +
                    "and choose the correct game directory.");

            FilesManager.initConfigFileWithPath("");
            System.exit(1);
        }
    }
}
