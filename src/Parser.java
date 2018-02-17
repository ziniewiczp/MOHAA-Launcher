import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class Parser {
    static String path;
    static String[][] serversArray;
    static String[][] recentServersArray;
    static List<String> recentServersList;
    
    static void initParser() {
        recentServersList = FilesManager.createRecentServersListFromFile();
        path = FilesManager.getPathFromConfigFile();
    }
    
    static void parseOnlineServers() {
        boolean recentServersFound;
        serversArray = new String[50][5];

        if( recentServersList.size() > 0 ) {
            recentServersFound = true;
            recentServersArray = new String[recentServersList.size()][5];
        } else {
            recentServersFound = false;
            recentServersArray = new String[1][5];
        }

        for( String[] row : recentServersArray)
            Arrays.fill(row, "");

        try {
            // creating JSoup document
            Document doc = Jsoup.connect("http://www.gametracker.com/search/mohaa/?sort=3&order=DESC&searchipp=50#search")
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                    .get();

            int rowCounter = 0;

            for (Element table : doc.select("table[class=table_lst table_lst_srs]")) {
                for (Element row : table.select("tr")) {

                    Elements td = row.select("td");

                    // ignore headers
                    if( td.get(2).text().equals("Server Name"))
                        continue;

                    populateRow(serversArray, rowCounter, td);

                    if( recentServersFound && recentServersList.contains(td.get(6).text()) ) {
                        int currentServerNumber = recentServersList.indexOf(td.get(6).text());

                        populateRow(recentServersArray, currentServerNumber, td);
                    }

                    rowCounter++;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();

            boolean choice = NotificationManager.displayErrorYesNoOptionDialog("GameTracker.com is not responding, " +
                    "MOHAA Launcher will now close. Do you want to launch the game?", "Connection error");

            if(choice) {
                Launcher.launchGame();
                System.exit(0);
            } else {
                System.exit(0);
            }
        }
    }
    
    static String parseServerInfo(String IP) {
        String serverInfo = "<html><b>Players online:</b><br/><ol>";

        try {
            // creating JSoup document
            Document doc = Jsoup.connect("http://www.gametracker.com/server_info/" + IP + "/")
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                    .get();

            for (Element table : doc.select("table[class=table_lst table_lst_stp]")) {
                for (Element row : table.select("tr")) {

                    Elements td = row.select("td");

                    // ignore headers
                    if (td.get(1).text().equals("Name"))
                        continue;

                    serverInfo = serverInfo + "<li>" + td.get(1).text() + "</li>";
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();

            boolean choice = NotificationManager.displayErrorYesNoOptionDialog("GameTracker.com is not responding, " +
                    "MOHAA Launcher will now close. Do you want to launch the game?", "Connection error");

            if(choice) {
                Launcher.launchGame();
                System.exit(0);
            } else {
                System.exit(0);
            }
        }
        
        return serverInfo + "</ol></html>";
    }

    static void updateRecentServersList(String givenIP) {
        if(!recentServersList.isEmpty()) {
            if(recentServersList.contains(givenIP)) {
                recentServersList.remove(givenIP);
            }
        }

        recentServersList.add(0, givenIP);

        FilesManager.updateRecentServersFile();
    }

    private static void populateRow(String[][] array, int rowNumber, Elements td) {
        array[rowNumber][0] = td.get(2).text();  // server name
        array[rowNumber][1] = td.get(3).text();  // players count
        // substring used to cut out "GB" from "/search/mohaa/GB/"
        array[rowNumber][2] = td.get(5).select("a[href]").attr("href").substring(14,  16);   // localization
        array[rowNumber][3] = td.get(6).text();  // IP address
        array[rowNumber][4] = td.get(7).text();  // map
    }
}
