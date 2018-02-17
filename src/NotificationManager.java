import javax.swing.*;

class NotificationManager {

    static void displayMessageDialog(String message) {
        JOptionPane.showMessageDialog(new JFrame(), message);
    }

    static boolean displayErrorYesNoOptionDialog(String message, String title) {
        int choice = JOptionPane.showOptionDialog(new JFrame(), message, title, JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE, null, null, JOptionPane.YES_OPTION);

        if(choice == JOptionPane.YES_OPTION)
            return true;
        else
            return false;
    }

}
