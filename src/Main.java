import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


public class Main
{
    public static void main(String[] args) throws Exception
    {    
        Parser parser = new Parser();
        FilesManager configManager = new FilesManager();
        JFileChooser fileChooser = new JFileChooser();
        
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                try
                {
                    String path = configManager.getPath();
                    
                    // checking if server path was found in config file
                    if(  path == null )
                    {
                        JOptionPane.showMessageDialog(new JFrame(),
                                "Medal of Honor: Allied Assault folder not found. Please choose directory "
                                + "in which game is installed.");
                        
                        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        int returnValue = fileChooser.showOpenDialog(new JFrame());
                        
                        if( returnValue == JFileChooser.APPROVE_OPTION )
                        {
                            path = fileChooser.getSelectedFile().getAbsolutePath();
                            
                            configManager.initConfigFile(path);
                            
                            parser.parseOnlineServers();
                            GUI.createAndShowGUI(parser, path);
                        }
                        else
                            System.exit(0);
                    }
                    else
                    {
                        parser.parseOnlineServers();
                        GUI.createAndShowGUI(parser, path);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
}