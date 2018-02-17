import java.io.*;
import java.nio.file.Paths;
import java.util.*;


class FilesManager {

    static void initConfigFileWithPath(String path) {
        try(PrintWriter writer = new PrintWriter("config.txt", "UTF-8")) {
            writer.println(path);

        } catch (UnsupportedEncodingException | FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    static String getPathFromConfigFile() {
       try(Scanner scanner = new Scanner(Paths.get("config.txt"))) {
           String path = scanner.nextLine();
           return path;

       } catch (IOException ex) {
           return null;
       }
    }

    static void updateRecentServersFile() {
        try (PrintWriter writer = new PrintWriter("recentServers.txt", "UTF-8")) {
            for(String item : Parser.recentServersList) {
                writer.println(item);
            }
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }

    static List<String> createRecentServersListFromFile() {
        List<String> recentServersList = new ArrayList<>();

        try(Scanner scanner = new Scanner(Paths.get("recentServers.txt"))) {
            while( scanner.hasNext() )
                recentServersList.add(scanner.next());

        } catch (IOException ex) {
                System.out.println("File recentServers.txt not found.");
        }

        return recentServersList;
    }
}

