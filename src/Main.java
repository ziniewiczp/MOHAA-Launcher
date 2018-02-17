import javax.swing.SwingUtilities;


public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                Parser.initParser();

                if(Parser.path == null) {
                    NotificationManager.displayMessageDialog("Medal of Honor: Allied Assault folder not found." +
                            "Please choose directory in which game is installed.");

                    if (!FileChoosingManager.chooseGameDirectory()) {
                        System.exit(0);
                    }
                }

                Parser.parseOnlineServers();
                GUI.createAndShowGUI();
            }
        });
    }
}