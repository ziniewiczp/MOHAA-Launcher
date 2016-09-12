import java.io.File;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;


public class RecentServersManager
{
    public void reportConnecting(String givenIP, Parser parser) throws Exception
    {
        File f = new File("recentServers.txt");
        
        // case #1 - file doesn't exist or is empty.
        if( !f.isFile() || f.length() == 0 )
        {
            // creating (overwritting) file
            PrintWriter writer = new PrintWriter("recentServers.txt", "UTF-8");
            
            // simply adding given IP as most recent
            writer.println(givenIP + " 1");
            writer.close();
        }
        else
        {            
            // checking if given IP is in recentServersMap
            if( parser.recentServersMap.containsKey(givenIP))
            {
                // case #2 - given IP is most recent
                if( parser.recentServersMap.get(givenIP) == 1)
                {
                    // do nothing :)
                }
                
                // case #3 - given IP is one of the recent servers but not most recent
                else
                {     
                    Integer givenIPValue = parser.recentServersMap.get(givenIP);
                    
                    // creating (overwritting) file
                    PrintWriter writer = new PrintWriter("recentServers.txt", "UTF-8");
                    
                    // iterating over whole map
                    for(Iterator <Map.Entry<String, Integer>> iterator = parser.recentServersMap.entrySet().iterator();
                            iterator.hasNext();
                            )
                    {
                        Map.Entry<String, Integer> entry = iterator.next();
                        
                        // removing given IP from the map
                        if(entry.getKey().equals(givenIP))
                        {
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
            
            //case #4 - givenIP is not in recentServersMap
            else
            {
                // creating (overwritting) file
                PrintWriter writer = new PrintWriter("recentServers.txt", "UTF-8");
                
                // adding given IP as most recent
                writer.println(givenIP + " 1");
                
                // iterating over whole map
                for(Iterator <Map.Entry<String, Integer>> iterator = parser.recentServersMap.entrySet().iterator();
                        iterator.hasNext();
                        )
                {
                    Map.Entry<String, Integer> entry = iterator.next();
                    
                    // changing lower values than one of the given IP so they "move down" on the list
                    entry.setValue(entry.getValue() + 1);
                    
                    writer.println(entry.getKey() + " " + entry.getValue());
                  }
                
                writer.close();
            }
        }
    }
}
