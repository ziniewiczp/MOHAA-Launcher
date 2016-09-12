import javax.swing.SwingUtilities;
import javax.swing.UIManager;


public class Main
{
    public static void main(String[] args) throws Exception
    {
        // path format e.g.: "D:\\Games\\MOHAA"
        String path = args[0];
                
        Parser parser = new Parser();
        
        parser.parseOnlineServers();

        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                //Turn off metal's use of bold fonts
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                GUI.createAndShowGUI(parser, path);
            }
        });
    }
}