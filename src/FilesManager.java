import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;


public class FilesManager
{
    public String getPath() throws Exception
    {
       File file = new File("config.txt");
       
       if( !file.isFile() || file.length() == 0 )
           return null;
       else
       {
           Scanner scanner = new Scanner(Paths.get("config.txt"));
           
           String path = scanner.nextLine();
           scanner.close();
           
           return path;
       }       
    }
    
    public void initConfigFile(String path) throws FileNotFoundException, UnsupportedEncodingException
    {
        File file = new File("config.txt");
        
        if( !file.isFile() || file.length() == 0 )
        {
            // creating (overwritting) file
            PrintWriter writer = new PrintWriter("config.txt", "UTF-8");

            writer.println(path);
            writer.close();
        }
    }
}
