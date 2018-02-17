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

        } catch(IOException e) {
            e.printStackTrace();

            // TODO: display information that MOHAA.exe was not found and delete path from config file
        }
    }
    
    static void launchGame() {
        try {
            @SuppressWarnings("unused")
            Process process = Runtime.getRuntime().exec(Parser.path + "\\MOHAA.exe +set cl_playintro 0",
                    null, 
                    new File(Parser.path));
        } catch(IOException e) {
            e.printStackTrace();

            // TODO: display information that MOHAA.exe was not found and delete path from config file
        }
    }
}
