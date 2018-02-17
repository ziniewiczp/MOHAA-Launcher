import javax.swing.*;

class FileChoosingManager {

    static boolean chooseGameDirectory() {
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = fileChooser.showOpenDialog(new JFrame());

        if(returnValue == JFileChooser.APPROVE_OPTION) {
            Parser.path = fileChooser.getSelectedFile().getAbsolutePath();
            FilesManager.initConfigFileWithPath(Parser.path);

            return true;

        } else {
            return false;
        }
    }
}
