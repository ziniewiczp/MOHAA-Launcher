import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Parser
{
    boolean recentServersFound;
    public String[][] serverArray;
    public String[][] recentServerArray;
    Map<String, Integer> recentServersMap;
    
    public Parser() throws Exception
    {
        createRecentServersMap();
    }
    
    public void parseOnlineServers() throws Exception
    {
        this.serverArray = new String[50][5];
        
        // checking if there are recently visited servers found.
        if( this.recentServersMap.size() > 0 )
        {
            recentServersFound = true;
            
            this.recentServerArray = new String[this.recentServersMap.size()][5];
            
            for( int i = 0; i < this.recentServersMap.size(); i++)
            {
                for( int j = 0; j < 5; j++)
                {
                    this.recentServerArray[i][j] = "";
                }
            }
        }
        else
        {
            recentServersFound = false;
            this.recentServerArray = new String[1][5];

            for( int i = 0; i < 5; i++)
            {
                this.recentServerArray[0][i] = "";
            }
        }
        
        // creating JSoup document
        Document doc = Jsoup.connect("http://www.gametracker.com/search/mohaa/?sort=3&order=DESC&searchipp=50#search")
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                .get();
        
        // used to fill serverArray
        int i = 0;
        
        for (Element table : doc.select("table[class=table_lst table_lst_srs]"))
        {
            for (Element row : table.select("tr"))
            {
                Elements tds = row.select("td");
                
                // ignoring headers
                if( tds.get(2).text().equals("Server Name")) continue;
                
                serverArray[i][0] = tds.get(2).text();  // server name
                serverArray[i][1] = tds.get(3).text();  // players count
                
                // substring used to cut out "GB" from "/search/mohaa/GB/"
                serverArray[i][2] = tds.get(5).select("a[href]").attr("href").substring(14,  16);   // localization
                serverArray[i][3] = tds.get(6).text();  // IP address
                serverArray[i][4] = tds.get(7).text();  // map
                
                if( recentServersFound && recentServersMap.containsKey(tds.get(6).text()) )
                {
                    Integer currentServerNumber = recentServersMap.get(tds.get(6).text());
                    
                    recentServerArray[currentServerNumber - 1][0] = tds.get(2).text();  // server name
                    recentServerArray[currentServerNumber - 1][1] = tds.get(3).text();  // players count
                
                    // substring used to cut out "GB" from "/search/mohaa/GB/"
                    recentServerArray[currentServerNumber - 1][2] = tds.get(5).select("a[href]").attr("href").substring(14,  16);   // localization
                    recentServerArray[currentServerNumber - 1][3] = tds.get(6).text();  // IP address
                    recentServerArray[currentServerNumber - 1][4] = tds.get(7).text();  // map
                }
                  
                i++;
            }
        }
    }
    
    public String parseServerInfo(String IP) throws Exception
    {
        String serverInfo = "<html><b>Players online:</b><br/><ol>";
        
        // creating JSoup document
        Document doc = Jsoup.connect("http://www.gametracker.com/server_info/" + IP + "/")
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                .get();
        
        for (Element table : doc.select("table[class=table_lst table_lst_stp]"))
        {
            for (Element row : table.select("tr"))
            {
                Elements tds = row.select("td");
                
                // ignoring headers
                if( tds.get(1).text().equals("Name")) continue;
                
                serverInfo = serverInfo + "<li>" + tds.get(1).text() + "</li>";  
            }
        }
        
        serverInfo = serverInfo + "</ol></html>";
        
        return serverInfo;
    }
    
    public void createRecentServersMap() throws Exception
    {
        this.recentServersMap = new HashMap<String, Integer>();
        
        // checking if file with recent IP's exists and if it isn't empty
        File f = new File("recentServers.txt");
        if( f.isFile() && f.length() != 0 )
        {
            Scanner file = new Scanner(Paths.get("recentServers.txt"));
            
            while( file.hasNext() )
            {
                this.recentServersMap.put(file.next(), Integer.parseInt(file.next()));
            }
            
            file.close();
        }
    }
}
