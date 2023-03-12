package mohaa_launcher;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

class FileChoosingManager {

    static String chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Executables (*.exe)", "exe");
        fileChooser.setFileFilter(filter);
        int returnValue = fileChooser.showOpenDialog(new JFrame());

        return (returnValue == JFileChooser.APPROVE_OPTION)
            ? fileChooser.getSelectedFile().getAbsolutePath()
            : null;
    }
}
