package mohaa_launcher;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
class SettingsController {

    private static final String CONFIG_PATH = "src\\main\\resources\\config.cfg";
    private static Map<String,String> settingsMap;

    public static String getMohaaPath() {
        return settingsMap.get("mohaaPath");
    }

    public static String getSpearheadPath() {
        return settingsMap.get("mohaashPath");
    }

    public static String getBreakthroughPath() {
        return settingsMap.get("mohaabtPath");
    }

    public static String getVolutePath() {
        return settingsMap.get("volutePath");
    }

    public static void initialize() {
        try {
            byte[] settingsData = Files.readAllBytes(Paths.get(CONFIG_PATH));
            ObjectMapper objectMapper = new ObjectMapper();
            settingsMap = objectMapper.readValue(settingsData, HashMap.class);

        } catch(Exception e) {
            settingsMap = new HashMap<>();
        }
    }

    public static void update(HashMap<String, String> updatedSettings) {
        settingsMap = updatedSettings;

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(Paths.get(CONFIG_PATH).toFile(), settingsMap);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
