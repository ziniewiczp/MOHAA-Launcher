import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

class GUI {

    public static class MainFrame extends JPanel {
        private static final long serialVersionUID = 1L;
        private static int currentTab = 0;
        private JTable onlineServersTable;
        private JTable recentServersTable;
        private TableRowSorter<CustomTableModel> onlineSorter;
        private TableRowSorter<CustomTableModel> recentSorter;
        private JTextField filterText;
        private JLabel playersLabel;
        private JLabel imageLabel;
        private BufferedImage img;
        private Map<String, String> mapNames;
        private static JPanel loadingPanel;

        // row filter used to skip rows, which are empty
        private RowFilter<CustomTableModel, Integer> emptyFilter = new RowFilter<CustomTableModel, Integer>() {
            @Override
            public boolean include(RowFilter.Entry<? extends CustomTableModel, ? extends Integer> entry)
            {
                boolean included = true;
                Object cellValue = entry.getModel().getValueAt(entry.getIdentifier(), 0);
                if (cellValue == null || cellValue.toString().trim().isEmpty())
                    included = false;

                return included;
            }
        };

        // custom comparator to sort players count properly
        private Comparator<String> playersComparator = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if( o1.equals("") && o2.equals(""))
                    return 0;
                else if( o1.equals("") && !o2.equals(""))
                    return -1;
                else if( !o1.equals("") && o2.equals(""))
                    return 1;

                String firstNumber = o1.split("\\/")[0];
                String secondNumber = o2.split("\\/")[0];

                if(Integer.parseInt(firstNumber) > Integer.parseInt(secondNumber))
                    return 1;
                else if( Integer.parseInt(firstNumber) < Integer.parseInt(secondNumber) )
                    return -1;
                else
                    return 0;
            }
        };

        MainFrame() {
            super(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            Border eBorder = BorderFactory.createEtchedBorder();

            this.setPreferredSize(new Dimension(1000, 600));

            loadingPanel = new JPanel(new BorderLayout());
            ImageIcon loadingIcon = new ImageIcon("images/ajax-loader.gif");
            loadingPanel.add(new JLabel("Loading...", loadingIcon, JLabel.CENTER), BorderLayout.CENTER);
            loadingPanel.setPreferredSize(new Dimension(100, 50));
            add(loadingPanel);
            loadingPanel.setVisible(false);

            /**
             * Tabbed panel, displaying server lists
             * ----------------------------------------------------------------------------------------------
             */

            JTabbedPane tabbedPane = new JTabbedPane();

            tabbedPane.setBorder(BorderFactory.createTitledBorder(eBorder));

            // setting GridBagLayout constraints to properly allocate panel
            gbc.gridx = gbc.gridy = 0;
            gbc.gridwidth = gbc.gridheight = 1;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.NORTH;
            gbc.weightx = 80;
            gbc.weighty = 98;

            CustomTableModel onlineModel = new CustomTableModel(Parser.serversArray, 50);
            onlineServersTable = new JTable(onlineModel);
            onlineServersTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
            onlineServersTable.setFillsViewportHeight(true);

            JScrollPane serverList = new JScrollPane(onlineServersTable);
            tabbedPane.addTab("Server list", null, serverList, null);
            tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

            CustomTableModel recentModel = new CustomTableModel(Parser.recentServersArray, Parser.recentServersArray.length);
            recentServersTable = new JTable(recentModel);
            recentServersTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
            recentServersTable.setFillsViewportHeight(true);

            JScrollPane historyList = new JScrollPane(recentServersTable);
            tabbedPane.addTab("History", null, historyList, null);
            tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

            //The following line enables to use scrolling tabs.
            tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

            // setting table font
            onlineServersTable.setFont(onlineServersTable.getFont().deriveFont(10f));
            recentServersTable.setFont(recentServersTable.getFont().deriveFont(10f));

            // setting preferred columns width
            TableColumnModel tableColumnModel = onlineServersTable.getColumnModel();
            tableColumnModel.getColumn(0).setPreferredWidth(400);      // server name
            tableColumnModel.getColumn(1).setPreferredWidth(30);       // players
            tableColumnModel.getColumn(2).setPreferredWidth(25);       // localization
            tableColumnModel.getColumn(3).setPreferredWidth(100);      // IP
            tableColumnModel.getColumn(4).setPreferredWidth(100);      // map

            tableColumnModel = recentServersTable.getColumnModel();
            tableColumnModel.getColumn(0).setPreferredWidth(400);      // server name
            tableColumnModel.getColumn(1).setPreferredWidth(30);       // players
            tableColumnModel.getColumn(2).setPreferredWidth(25);       // localization
            tableColumnModel.getColumn(3).setPreferredWidth(100);      // IP
            tableColumnModel.getColumn(4).setPreferredWidth(100);      // map

            // centering content of Players and Localization columns
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment( JLabel.CENTER );
            onlineServersTable.getColumnModel().getColumn(1).setCellRenderer( centerRenderer );
            onlineServersTable.getColumnModel().getColumn(2).setCellRenderer( centerRenderer );

            recentServersTable.getColumnModel().getColumn(1).setCellRenderer( centerRenderer );
            recentServersTable.getColumnModel().getColumn(2).setCellRenderer( centerRenderer );

            // change listener to recognize which tab is selected            
            tabbedPane.getModel().addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e)
                {
                    MainFrame.currentTab = tabbedPane.getSelectedIndex();
                }
            });

            // listener used to connecting to the server by double-clicking on its row
            MouseListener doubleClickListener = new MouseAdapter() {
                public void mousePressed(MouseEvent me) {
                    JTable table =(JTable) me.getSource();
                    Point p = me.getPoint();
                    int row = table.rowAtPoint(p);
                    if (me.getClickCount() == 2) {
                        String IP;
                        if( MainFrame.currentTab == 0)
                            IP = Parser.serversArray[row][3];
                        else
                            IP = Parser.recentServersArray[row][3];

                        Parser.updateRecentServersList(IP);
                        Launcher.connectTo(IP);
                    }
                }
            };

            onlineServersTable.addMouseListener(doubleClickListener);
            recentServersTable.addMouseListener(doubleClickListener);

            onlineServersTable.setAutoCreateRowSorter(true);
            recentServersTable.setAutoCreateRowSorter(true);

            onlineSorter = new TableRowSorter<CustomTableModel>(onlineModel);
            recentSorter = new TableRowSorter<CustomTableModel>(recentModel);
            onlineServersTable.setRowSorter(onlineSorter);
            recentServersTable.setRowSorter(recentSorter);

            recentSorter.setRowFilter(emptyFilter);
            onlineSorter.setRowFilter(emptyFilter);

            onlineSorter.setComparator(1, playersComparator);
            recentSorter.setComparator(1, playersComparator);

            //Add the tabbed pane to this panel.
            add(tabbedPane, gbc);

            /**
             * Right panel with server information
             * ----------------------------------------------------------------------------------------------
             */

            JPanel rightPanel = new JPanel(new GridBagLayout());

            rightPanel.setBorder(BorderFactory.createTitledBorder(eBorder));

            JPanel imagePanel = new JPanel(new BorderLayout());

            initMapNames();

            try {
                img = ImageIO.read(new File("images/default.jpg"));

            } catch (IOException e2) {
                e2.printStackTrace();
                img = new BufferedImage(200, 148, TYPE_INT_ARGB);
            }

            ImageIcon icon = new ImageIcon(img);
            imageLabel = new JLabel(" ", icon, JLabel.CENTER);
            imageLabel.setVerticalTextPosition(JLabel.BOTTOM);
            imageLabel.setHorizontalTextPosition(JLabel.CENTER);
            imageLabel.setOpaque(true);
            imageLabel.setFont(imageLabel.getFont().deriveFont(10f));
            imagePanel.add(imageLabel, BorderLayout.CENTER);
            imagePanel.setBorder(BorderFactory.createTitledBorder(eBorder));

            // GridBagLayout constraints to imagePanel
            gbc.gridx = gbc.gridy = 0;
            gbc.gridwidth = gbc.gridheight = 1;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.NORTH;
            gbc.weightx = 100;
            gbc.weighty = 10;

            rightPanel.add(imagePanel, gbc);

            JPanel playersPanel = new JPanel(new BorderLayout());
            playersPanel.setBorder(BorderFactory.createTitledBorder(eBorder));
            playersLabel = new JLabel("<html><b>Players online:</b></html>");
            playersLabel.setFont(playersLabel.getFont().deriveFont(10f));
            playersPanel.add(playersLabel, BorderLayout.NORTH);

            // GridBagLayout constraints for playersPanel
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = gbc.gridheight = 1;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.SOUTH;
            gbc.weightx = 100;
            gbc.weighty = 90;

            JScrollPane playersScrollPanel = new JScrollPane(playersPanel);

            // set to avoid resizing playersScrollPanel
            playersScrollPanel.setPreferredSize(new Dimension(200,250));

            // "speeding up" scrolling inside playersScrollPanel
            playersScrollPanel.getVerticalScrollBar().setUnitIncrement(14);

            rightPanel.add(playersScrollPanel, gbc);

            // listener used to display selected server's information in the rightPanel
            onlineServersTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (onlineServersTable.getSelectedRow() > -1) {
                        setSelectedServerInfo(onlineServersTable.getValueAt(onlineServersTable.getSelectedRow(), 4));

                        playersLabel.setText(Parser.parseServerInfo((String)onlineServersTable.getValueAt(onlineServersTable.getSelectedRow(), 3)));

                    }
                }
            });

            recentServersTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (recentServersTable.getSelectedRow() > -1) {
                        setSelectedServerInfo(recentServersTable.getValueAt(recentServersTable.getSelectedRow(), 4));

                        playersLabel.setText(Parser.parseServerInfo((String)recentServersTable.getValueAt(recentServersTable.getSelectedRow(), 3)));
                    }
                }
            });

            // setting GridBagLayout constraints to properly allocate whole rightPanel
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.NORTHEAST;
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.weightx = 20;
            gbc.weighty = 98;

            add(rightPanel, gbc);

            /**
             * Bottom panel with buttons
             * ----------------------------------------------------------------------------------------------
             */

            JPanel bottomPanel = new JPanel(new BorderLayout());

            bottomPanel.setBorder(BorderFactory.createTitledBorder(eBorder));

            // setting GridBagLayout constraints to properly allocate panel
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weightx = 100;
            gbc.gridwidth = 2;
            gbc.weighty = 2;

            JPanel textPanel = new JPanel();
            JLabel textLabel = new JLabel("Search:");
            textPanel.add(textLabel);
            filterText = new JTextField(15);

            //Whenever filterText changes, invoke nameFilter.
            filterText.getDocument().addDocumentListener(new DocumentListener() {
                public void changedUpdate(DocumentEvent e)
                {
                    nameFilter();
                }
                public void insertUpdate(DocumentEvent e)
                {
                    nameFilter();
                }
                public void removeUpdate(DocumentEvent e)
                {
                    nameFilter();
                }
            });

            textLabel.setLabelFor(filterText);
            textPanel.add(filterText);
            bottomPanel.add(textPanel, BorderLayout.WEST);

            JButton launchGameButton = new JButton("Launch game");

            JButton refreshButton = new JButton("Refresh");

            JButton connectButton = new JButton("Connect");

            JLabel copyright = new JLabel("<html><center>Copyright \u00a9 2016 by Nevi<br/>Powered by GameTracker.com</center></html>", SwingConstants.CENTER);
            copyright.setFont(copyright.getFont().deriveFont(10f));

            launchGameButton.addActionListener((new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Launcher.launchGame();
                }
            }));

            // listener used to refresh current table
            refreshButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    loadingPanel.setVisible(true);

                    new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() {
                            CustomTableModel model;

                            if( MainFrame.currentTab == 0) {
                                model = (CustomTableModel) onlineServersTable.getModel();
                                model.refresh(0);
                            } else {
                                model = (CustomTableModel) recentServersTable.getModel();
                                model.refresh(1);
                            }

                            return null;
                        }

                        @Override
                        protected void done() {
                            loadingPanel.setVisible(false);
                        }
                    }.execute();
                }
            });

            // listener used to connect to selected server
            connectButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String IP;

                    // checking if some row on current tab is selected
                    if( (onlineServersTable.getSelectedRow() != -1 && MainFrame.currentTab == 0) ||
                            (recentServersTable.getSelectedRow() != -1) && MainFrame.currentTab == 1) {
                        if( MainFrame.currentTab == 0)
                            IP = Parser.serversArray[onlineServersTable.getSelectedRow()][3];
                        else
                            IP = Parser.recentServersArray[recentServersTable.getSelectedRow()][3];

                        Parser.updateRecentServersList(IP);
                        Launcher.connectTo(IP);
                    }
                }
            } );

            // additional panel, used to align both buttons to the EAST
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(launchGameButton);
            buttonPanel.add(refreshButton);
            buttonPanel.add(connectButton);

            bottomPanel.add(buttonPanel, BorderLayout.EAST);
            bottomPanel.add(copyright, BorderLayout.AFTER_LAST_LINE);

            add(bottomPanel, gbc);
        }

        // function used to filter list based on text entered in search box
        private void nameFilter() {
            RowFilter<CustomTableModel, Integer> rf = null;

            //If current expression doesn't parse, don't update.
            try {
                rf = RowFilter.regexFilter("(?i)" + filterText.getText(), 0);

            } catch (java.util.regex.PatternSyntaxException e) {
                return;
            }

            // if search box is empty set row filter back to emptyFilter
            if( filterText.getText().equals("") ) {
                recentSorter.setRowFilter(this.emptyFilter);
                onlineSorter.setRowFilter(this.emptyFilter);

            } else {
                recentSorter.setRowFilter(rf);
                onlineSorter.setRowFilter(rf);
            }
        }

        private void initMapNames() {
            mapNames = new HashMap<>();

            mapNames.put("obj/obj_team1", "The Hunt");
            mapNames.put("obj/obj_team2", "V2 Rocket Facility");
            mapNames.put("obj/obj_team3", "Omaha Beach");
            mapNames.put("obj/obj_team4", "The Bridge");
            mapNames.put("dm/mohdm1", "Southern France");
            mapNames.put("dm/mohdm2", "Destroyed Village");
            mapNames.put("dm/mohdm3", "Remagen");
            mapNames.put("dm/mohdm4", "The Crossroads");
            mapNames.put("dm/mohdm5", "Snowy Park");
            mapNames.put("dm/mohdm6", "Stalingrad");
            mapNames.put("dm/mohdm7", "Algiers");
        }

        private void setSelectedServerInfo(Object map) {
            String localMap = (String) map;

            if(mapNames.containsKey(localMap.toLowerCase())) {
                setMapImage(mapNames.get(localMap.toLowerCase()));
                setMapDescription(mapNames.get(localMap.toLowerCase()));
            } else {
                setMapImage("default");
                setMapDescription("");
            }
        }

        private void setMapImage(String mapName) {
            mapName = mapName.toLowerCase().replace(" ", "_");

            try {
                img = ImageIO.read(new File("images/" + mapName + ".jpg"));

            } catch (IOException e) {
                e.printStackTrace();
                img = new BufferedImage(200, 148, TYPE_INT_ARGB);
            }

            imageLabel.setIcon(new ImageIcon(img));
        }

        private void setMapDescription(String mapName) {
            imageLabel.setText("<html><b>" + mapName + "</b></html>");
        }
    }


    public static class CustomTableModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;
        private Object[][] serversArray;
        private int listLen;

        private String[] columnNames = {"Server Name",
                "Players",
                "Localization",
                "IP Address",
                "Map"};

        CustomTableModel(Object[][] serversArray, int size) {
            this.listLen = size;
            this.serversArray = new Object[serversArray.length][5];

            for(int i = 0; i < serversArray.length; i++) {
                for( int j = 0; j < 5; j++ ) {
                    this.serversArray[i][j] = serversArray[i][j];
                }
            }
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        void refresh(int whichTable) {
            try {
                Parser.parseOnlineServers();
                if( whichTable == 0)
                    this.serversArray = Parser.serversArray;
                else
                    this.serversArray = Parser.recentServersArray;
                fireTableDataChanged();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getColumnCount() {
            return 5;
        }

        @Override
        public int getRowCount() {
            return listLen;
        }

        @Override
        public Object getValueAt(int arg0, int arg1) {
            return this.serversArray[arg0][arg1];
        }

        @Override
        public void setValueAt(Object value, int arg0, int arg1) {
            this.serversArray[arg0][arg1] = value;
            this.fireTableCellUpdated(arg0, arg1);
        }

        @Override
        public Class<?> getColumnClass(int column) {
            if( column == 1)
                return Integer.class;
            else
                return getValueAt(0, column).getClass();
        }
    }


    static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Medal of Honor: Allied Assault Launcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImageIcon icon = new ImageIcon("images/icon.png");
        frame.setIconImage(icon.getImage());

        //Add content to the window.
        frame.add(new MainFrame(), BorderLayout.CENTER);

        //Display the window.
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
