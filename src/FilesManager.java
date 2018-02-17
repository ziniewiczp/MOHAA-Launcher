import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;


class FilesManager {

    void initConfigFile(String path) throws FileNotFoundException, UnsupportedEncodingException {
        File file = new File("config.txt");

        if( !file.isFile() || file.length() == 0 ) {
            // creating (overwritting) file
            PrintWriter writer = new PrintWriter("config.txt", "UTF-8");

            writer.println(path);
            writer.close();
        }
    }

    String getPathFromConfigFile() throws Exception {
       File file = new File("config.txt");
       
       if( !file.isFile() || file.length() == 0 )
           return null;
       else {
           Scanner scanner = new Scanner(Paths.get("config.txt"));
           
           String path = scanner.nextLine();
           scanner.close();
           
           return path;
       }       
    }

    void reportConnecting(String givenIP, Parser parser) throws Exception {
        File file = new File("recentServers.txt");

        // case #1 - file doesn't exist or is empty.
        if( !file.isFile() || file.length() == 0 ) {
            // creating (overwritting) file
            PrintWriter writer = new PrintWriter("recentServers.txt", "UTF-8");

            // simply adding given IP as most recent
            writer.println(givenIP + " 1");
            writer.close();

        } else {
            // checking if given IP is in recentServersMap
            if( parser.recentServersMap.containsKey(givenIP)) {

                // case #2 - given IP is one of the recent servers but not most recent. If it is most recent
                // we don't need to make any changes in recentServers.txt
                if( parser.recentServersMap.get(givenIP) != 1) {
                    Integer givenIPValue = parser.recentServersMap.get(givenIP);

                    reorganizeRecentServersAndAddGivenIPAsMostRecent(givenIP, parser, givenIPValue);
                }
            }

            // case #3 - givenIP is not in recentServersMap
            else {
                // passing 0 as givenIPValue to reorganize all entries
                reorganizeRecentServersAndAddGivenIPAsMostRecent(givenIP, parser, Integer.MAX_VALUE);
            }
        }
    }

    static Map<String, Integer> createRecentServersMap() throws Exception {
        Map<String, Integer> recentServersMap = new HashMap<>();
        File file = new File("recentServers.txt");

        if( file.isFile() && file.length() != 0 ) {
            Scanner scanner = new Scanner(Paths.get("recentServers.txt"));

            while( scanner.hasNext() ) {
                recentServersMap.put(scanner.next(), Integer.parseInt(scanner.next()));
            }

            scanner.close();
        }

        return recentServersMap;
    }

    private void reorganizeRecentServersAndAddGivenIPAsMostRecent(String givenIP, Parser parser, Integer givenIPValue) throws Exception {
        PrintWriter writer = new PrintWriter("recentServers.txt", "UTF-8");

        // iterating over whole recentServersMap
        for(Iterator <Map.Entry<String, Integer>> iterator = parser.recentServersMap.entrySet().iterator();
            iterator.hasNext();
                ) {
            Map.Entry<String, Integer> entry = iterator.next();

            // removing given IP from the map
            if(entry.getKey().equals(givenIP)) {
                iterator.remove();
                continue;
            }

            // changing lower values than one of the given IP so they "move down" on the list
            if(entry.getValue() < givenIPValue)
                entry.setValue(entry.getValue() + 1);

            writer.println(entry.getKey() + " " + entry.getValue());
        }

        // adding given IP as most recent
        writer.println(givenIP + " 1");

        writer.close();
    }
}

