package mohaa_launcher;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

class Launcher {

    private static final HashMap<String, String> gameMapping = new HashMap<>();

    static {
        gameMapping.put("AA", "\\MOHAA.exe");
        gameMapping.put("SH", "\\moh_spearhead.exe");
        gameMapping.put("BT", "\\moh_breakthrough");
    }

    static void playMultiplayer(String ip, String game) {
        launchGame(" set cl_playintro 0 +connect " + ip, game);
    }

    static void playSingleplayer() {
        launchGame(" +set cl_playintro 0", "AA");
    }

    private static void launchGame(String parameters, String game) {
        try {
            Runtime.getRuntime().exec(Parser.path
                + gameMapping.get(game)
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
