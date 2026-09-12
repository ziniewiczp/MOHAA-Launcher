package mohaa_launcher;

import javax.swing.*;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SettingsController.initialize();
                Parser.initParser();
                try {
                    Parser.MohaaResponse mohaaResponse = Parser.fetchServers("mohaa");
                    Parser.MohaaResponse mohaasResponse = Parser.fetchServers("mohaas");
                    Parser.buildServersArrays(List.of(mohaaResponse, mohaasResponse));

                } catch(Exception e) {
                    e.printStackTrace();
                }

                GUI.createAndShowGUI();
            }
        });
    }
}