import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    Parser.initParser();

                    if(Parser.path == null) {
                        JOptionPane.showMessageDialog(new JFrame(),
                                "Medal of Honor: Allied Assault folder not found. Please choose directory "
                                        + "in which game is installed.");

                        JFileChooser fileChooser = new JFileChooser();

                        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        int returnValue = fileChooser.showOpenDialog(new JFrame());

                        if (returnValue == JFileChooser.APPROVE_OPTION) {
                            Parser.path = fileChooser.getSelectedFile().getAbsolutePath();

                            FilesManager.initConfigFileWithPath(Parser.path);

                            Parser.parseOnlineServers();
                            GUI.createAndShowGUI();
                        } else {
                            System.exit(0);
                        }
                    } else {
                        Parser.parseOnlineServers();
                        GUI.createAndShowGUI();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}