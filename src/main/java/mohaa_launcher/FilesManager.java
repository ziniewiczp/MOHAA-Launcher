package mohaa_launcher;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

class FilesManager {

    static void initConfigFileWithPath(String path) {
        try(PrintWriter writer = new PrintWriter("src\\main\\resources\\config.txt", "UTF-8")) {
            writer.println(path);

        } catch (UnsupportedEncodingException | FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    static String getPathFromConfigFile() {
       try(Scanner scanner = new Scanner(Paths.get("src\\main\\resources\\config.txt"))) {
           String path = scanner.nextLine();

           return (path.equals(""))
               ? null
               : path;

       } catch (IOException ex) {
           return null;
       }
    }

    static void updateRecentServersFile(List<String> recentServersList) {
        try (PrintWriter writer = new PrintWriter("src\\main\\resources\\recentServers.txt", "UTF-8")) {
            for(String item : recentServersList) {
                writer.println(item);
            }

        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }

    static List<String> createRecentServersListFromFile() {
        List<String> recentServersList = new ArrayList<>();

        try(Scanner scanner = new Scanner(Paths.get("src\\main\\resources\\recentServers.txt"))) {
            while( scanner.hasNext() ) {
                recentServersList.add(scanner.next());
            }

        } catch (IOException ex) {
                System.out.println("File recentServers.txt not found.");
        }

        return recentServersList;
    }
}

