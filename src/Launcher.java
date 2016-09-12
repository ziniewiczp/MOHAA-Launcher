import java.io.File;
import java.io.IOException;

public class Launcher
{
    public void connectTo(String path, String IP)
    {
        try
        {
            @SuppressWarnings("unused")
            Process process = Runtime.getRuntime().exec(path + "\\MOHAA.exe"
                    + " set cl_playintro 0 +connect " + IP,
                    null,
                    new File(path));
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public static void launchGame(String path)
    {
        try
        {
            @SuppressWarnings("unused")
            Process process = Runtime.getRuntime().exec(path + "\\MOHAA.exe +set cl_playintro 0", 
                    null, 
                    new File(path));
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
