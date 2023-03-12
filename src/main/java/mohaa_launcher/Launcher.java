package mohaa_launcher;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

class Launcher {

    private static final HashMap<String, String> gameMapping = new HashMap<>();

    static {
        gameMapping.put("AA", SettingsController.getMohaaPath());
        gameMapping.put("SH", SettingsController.getSpearheadPath());
        gameMapping.put("BT", SettingsController.getBreakthroughPath());
    }

    static void playMultiplayer(String ip, String game) {
        launch(gameMapping.get(game) + " set cl_playintro 0 +connect " + ip);
    }

    static void launchVolute() { launch(SettingsController.getVolutePath()); }

    static void launch(String path) {
        try {
            String directory = Paths.get(path).getParent().toString();

            Runtime.getRuntime().exec(path, null, new File(directory));

        } catch(IOException e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(
                new JFrame(),
                "Executable file not found. Please review the Settings and try again.",
                "File not found",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
