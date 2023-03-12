package mohaa_launcher;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;

class Parser {
    static String[][] serversArray;
    static String[][] recentServersArray;
    static List<String> recentServersList;
    static HashMap<String, String> serverInfo = new HashMap<>();

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36";

    private static final int COLUMNS = 6;

    static void initParser() {
        recentServersList = FilesManager.createRecentServersListFromFile();
    }
    
    static void parseOnlineServers() {
        recentServersArray = (recentServersList.size() > 0)
            ? new String[recentServersList.size()][COLUMNS]
            : new String[1][COLUMNS];

        for(String[] row : recentServersArray) {
            Arrays.fill(row, "");
        }

        Document document;

        try {
            // creating JSoup document
             document = Jsoup.connect("https://www.mohaaservers.tk/")
                .userAgent(USER_AGENT)
                .get();

        } catch (IOException ex) {
            ex.printStackTrace();

            serversArray = new String[1][COLUMNS];
            for(String[] row : serversArray) {
                Arrays.fill(row, "");
            }

            JOptionPane.showMessageDialog(
                new JFrame(),
                "Server responded with " + ex.getMessage(),
                "Connection error",
                JOptionPane.ERROR_MESSAGE);

            return;
        }

        serversArray = new String[countOnlineServers(document)][COLUMNS];

        int currentRow = 0;

        for (Element table : document.select("table[class=sortresults]")) {
            for (Element row : table.select("tr")) {

                Elements td = row.select("td");

                // ignore headers and offline servers
                if (td.size() == 0 || td.get(0).text().contains("Offline")) {
                    continue;
                }

                populateRow(serversArray, currentRow, td);

                if (recentServersList.contains(td.get(4).text())) {
                    populateRow(recentServersArray, recentServersList.indexOf(td.get(4).text()), td);
                }

                currentRow++;
            }
        }
    }

    private static int countOnlineServers(Document doc) {
        int counter = 0;

        for (Element table : doc.select("table[class=sortresults]")) {
            for (Element row : table.select("tr")) {
                Elements td = row.select("td");

                if ((td.size() > 0) && td.get(0).text().contains("Online")) {
                    counter += 1;
                }
            }
        }

        return counter;
    }

    static void updateRecentServersList(String givenIP) {
        recentServersList.remove(givenIP);
        recentServersList.add(0, givenIP);

        FilesManager.updateRecentServersFile(recentServersList);
    }

    private static void populateRow(String[][] array, int rowNumber, Elements td) {
        String game = td.get(1).select("img").get(0).attr("alt");
        array[rowNumber][0] = game.substring(game.length() - 2); // game
        array[rowNumber][1] = td.get(3).text();  // server name
        array[rowNumber][2] = td.get(7).text();  // players count
        array[rowNumber][3] = (td.get(2).select("img").get(0).attr("alt")).toUpperCase();  // localization
        array[rowNumber][4] = td.get(4).text();  // IP address
        array[rowNumber][5] = td.get(6).text();  // map

        String serverInfoString = "<html><b>Players online:</b><br/><ol>";

        for(Element player : td.get(8).select("li")) {
            serverInfoString += "<li>" + player.text() + "</li>";
        }

        serverInfoString += "</ol></html>";

        serverInfo.put(array[rowNumber][4], serverInfoString);
    }
}
