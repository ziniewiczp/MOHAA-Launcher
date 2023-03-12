package mohaa_launcher;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;

class SettingsFrame extends JFrame {
    private JTextField mohaaLocationTextField;
    private JTextField mohaashLocationTextField;
    private JTextField mohaabtLocationTextField;
    private JTextField voluteLocationTextField;

    public SettingsFrame() {
        this.setTitle("Settings");
        ImageIcon icon = new ImageIcon("src\\main\\resources\\images\\mohaa.png");
        this.setIconImage(icon.getImage());
        this.setVisible(true);
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        JPanel locationsPanel = new JPanel();
        locationsPanel.setLayout(new BoxLayout(locationsPanel, BoxLayout.PAGE_AXIS));
        locationsPanel.setBorder(BorderFactory.createTitledBorder("Games locations"));

        JPanel mohaaLocationHeadingPanel = new JPanel();
        mohaaLocationHeadingPanel.setLayout(new BorderLayout());
        mohaaLocationHeadingPanel.setBorder( new EmptyBorder(5,5,0,0));

        JLabel mohaaLocationHeading = new JLabel("Medal of Honor: Allied Assault");
        mohaaLocationHeadingPanel.add(mohaaLocationHeading, BorderLayout.LINE_START);
        locationsPanel.add(mohaaLocationHeadingPanel);

        JPanel mohaaLocationPanel = new JPanel();
        mohaaLocationPanel.setLayout(new FlowLayout());

        mohaaLocationTextField = new JTextField(25);
        mohaaLocationTextField.setText(SettingsController.getMohaaPath());
        mohaaLocationPanel.add(mohaaLocationTextField);

        JButton mohaaLocationButton = new JButton("Browse");
        mohaaLocationButton.addActionListener(e -> browse(mohaaLocationTextField));
        mohaaLocationPanel.add(mohaaLocationButton);

        locationsPanel.add(mohaaLocationPanel);

        JPanel mohaashLocationHeadingPanel = new JPanel();
        mohaashLocationHeadingPanel.setLayout(new BorderLayout());
        mohaashLocationHeadingPanel.setBorder( new EmptyBorder(0,5,0,0));

        JLabel mohaashLocationHeading = new JLabel("Medal of Honor: Allied Assault Spearhead");
        mohaashLocationHeadingPanel.add(mohaashLocationHeading, BorderLayout.LINE_START);
        locationsPanel.add(mohaashLocationHeadingPanel);

        JPanel mohaashLocationPanel = new JPanel();
        mohaashLocationPanel.setLayout(new FlowLayout());

        mohaashLocationTextField = new JTextField(25);
        mohaashLocationTextField.setText(SettingsController.getSpearheadPath());
        mohaashLocationPanel.add(mohaashLocationTextField);

        JButton mohaashLocationButton = new JButton("Browse");
        mohaashLocationButton.addActionListener(e -> browse(mohaashLocationTextField));
        mohaashLocationPanel.add(mohaashLocationButton);

        locationsPanel.add(mohaashLocationPanel);

        JPanel mohaabtLocationHeadingPanel = new JPanel();
        mohaabtLocationHeadingPanel.setLayout(new BorderLayout());
        mohaabtLocationHeadingPanel.setBorder( new EmptyBorder(0,5,0,0));

        JLabel mohaabtLocationHeading = new JLabel("Medal of Honor: Allied Assault Breakthrough");
        mohaabtLocationHeadingPanel.add(mohaabtLocationHeading, BorderLayout.LINE_START);
        locationsPanel.add(mohaabtLocationHeadingPanel);

        JPanel mohaabtLocationPanel = new JPanel();
        mohaabtLocationPanel.setLayout(new FlowLayout());

        mohaabtLocationTextField = new JTextField(25);
        mohaabtLocationTextField.setText(SettingsController.getBreakthroughPath());
        mohaabtLocationPanel.add(mohaabtLocationTextField);

        JButton mohaabtLocationButton = new JButton("Browse");
        mohaabtLocationButton.addActionListener(e -> browse(mohaabtLocationTextField));
        mohaabtLocationPanel.add(mohaabtLocationButton);

        locationsPanel.add(mohaabtLocationPanel);

        panel.add(locationsPanel);

        JPanel otherSettingsPanel = new JPanel();
        otherSettingsPanel.setLayout(new BoxLayout(otherSettingsPanel, BoxLayout.PAGE_AXIS));
        otherSettingsPanel.setBorder(BorderFactory.createTitledBorder("Other settings"));

        JPanel voluteLocationHeadingPanel = new JPanel();
        voluteLocationHeadingPanel.setLayout(new BorderLayout());
        voluteLocationHeadingPanel.setBorder( new EmptyBorder(5,5,0,0));

        JLabel voluteLocationHeading = new JLabel("Volute location");
        voluteLocationHeadingPanel.add(voluteLocationHeading, BorderLayout.LINE_START);
        otherSettingsPanel.add(voluteLocationHeadingPanel);

        JPanel voluteLocationPanel = new JPanel();
        voluteLocationPanel.setLayout(new FlowLayout());

        voluteLocationTextField = new JTextField(25);
        voluteLocationTextField.setText(SettingsController.getVolutePath());
        voluteLocationPanel.add(voluteLocationTextField);

        JButton voluteLocationButton = new JButton("Browse");
        voluteLocationButton.addActionListener(e -> browse(voluteLocationTextField));
        voluteLocationPanel.add(voluteLocationButton);

        otherSettingsPanel.add(voluteLocationPanel);

        panel.add(otherSettingsPanel);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> this.dispose());
        buttonsPanel.add(cancelButton);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> save());
        buttonsPanel.add(saveButton);

        panel.add(buttonsPanel);

        this.getContentPane().add(BorderLayout.CENTER, panel);
        this.pack();
        this.setLocationRelativeTo(null);
    }

    private void browse(JTextField selectedTextField) {
        String selectedPath = FileChoosingManager.chooseFile();

        if(!selectedPath.isBlank()) {
            selectedTextField.setText(selectedPath);
        }
    }

    private void save() {
        HashMap<String, String> settings = new HashMap<>();
        settings.put("mohaaPath",   mohaaLocationTextField.getText());
        settings.put("mohaashPath", mohaashLocationTextField.getText());
        settings.put("mohaabtPath", mohaabtLocationTextField.getText());
        settings.put("volutePath",  voluteLocationTextField.getText());

        SettingsController.update(settings);
        this.dispose();
    }
}
