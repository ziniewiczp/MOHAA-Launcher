import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
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

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
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

public class GUI 
{
    public static class MainFrame extends JPanel
    {
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
        
        // row filter used to skip rows, which are empty
        private RowFilter<CustomTableModel, Integer> emptyFilter = new RowFilter<CustomTableModel, Integer>()
        {
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
         private Comparator<String> playersComparator = new Comparator<String>()
         {
             @Override
             public int compare(String o1, String o2) 
             {
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

        public MainFrame(Parser parser, String path)
        {
            super(new GridBagLayout());
                        
            FilesManager filesManager = new FilesManager();
            Launcher launcher = new Launcher();
            
            GridBagConstraints gbc = new GridBagConstraints();
            Border eBorder = BorderFactory.createEtchedBorder();
            
            this.setPreferredSize(new Dimension(1000, 600));
            
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
            
            CustomTableModel onlineModel = new CustomTableModel(parser.serverArray, 50);
            onlineServersTable = new JTable(onlineModel);
            onlineServersTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
            onlineServersTable.setFillsViewportHeight(true);
            
            JScrollPane serverList = new JScrollPane(onlineServersTable);
            tabbedPane.addTab("Server list", null, serverList, null);
            tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
            
            CustomTableModel recentModel = new CustomTableModel(parser.recentServerArray, parser.recentServerArray.length);
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
            tabbedPane.getModel().addChangeListener(new ChangeListener()
            {
                @Override
                public void stateChanged(ChangeEvent e)
                {
                   MainFrame.currentTab = tabbedPane.getSelectedIndex();
                }
             });
            
            // listener used to connecting to the server by double-clicking on its row
            MouseListener doubleClickListener = new MouseAdapter()
            {
                public void mousePressed(MouseEvent me)
                {
                    JTable table =(JTable) me.getSource();
                    Point p = me.getPoint();
                    int row = table.rowAtPoint(p);
                    if (me.getClickCount() == 2)
                    {
                        String IP;
                        if( MainFrame.currentTab == 0)
                            IP = parser.serverArray[row][3];
                        else
                            IP = parser.recentServerArray[row][3];
                        
                        try 
                        {
                            filesManager.reportConnecting(IP, parser);
                            launcher.connectTo(path, IP);
                            System.exit(0);
                        } 
                        catch (Exception e2) 
                        {
                            e2.printStackTrace();
                        }
                        
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
            
            try 
            {
                img = ImageIO.read(new File("images/default.jpg"));
                ImageIcon icon = new ImageIcon(img);
                imageLabel = new JLabel(" ", icon, JLabel.CENTER);
                imageLabel.setVerticalTextPosition(JLabel.BOTTOM);
                imageLabel.setHorizontalTextPosition(JLabel.CENTER);
                imageLabel.setOpaque(true);
                imageLabel.setFont(imageLabel.getFont().deriveFont(10f));
                imagePanel.add(imageLabel, BorderLayout.CENTER);
                imagePanel.setBorder(BorderFactory.createTitledBorder(eBorder));
            } 
            catch (IOException e2) 
            {
                e2.printStackTrace();
            }
            
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
            onlineServersTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
            {
                @Override
                public void valueChanged(ListSelectionEvent event)
                {
                    if (onlineServersTable.getSelectedRow() > -1)
                    {
                        setImage(onlineServersTable.getValueAt(onlineServersTable.getSelectedRow(), 4));
                        
                        try
                        {
                            playersLabel.setText(parser.parseServerInfo((String)onlineServersTable.getValueAt(onlineServersTable.getSelectedRow(), 3)));
                        } 
                        catch (Exception e) 
                        {
                            e.printStackTrace();
                        }
                    }
                }
            });
            
            recentServersTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
            {
                @Override
                public void valueChanged(ListSelectionEvent event)
                {
                    if (recentServersTable.getSelectedRow() > -1)
                    {
                        setImage(recentServersTable.getValueAt(recentServersTable.getSelectedRow(), 4));
                        
                        try
                        {
                            playersLabel.setText(parser.parseServerInfo((String)recentServersTable.getValueAt(recentServersTable.getSelectedRow(), 3)));    
                        } 
                        catch (Exception e) 
                        {
                            e.printStackTrace();
                        }
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
            filterText.getDocument().addDocumentListener(new DocumentListener() 
            {
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
            
            JButton refreshButton = new JButton("Refresh");
            
            JButton connectButton = new JButton("Connect");
            
            JLabel copyright = new JLabel("<html><center>Copyright \u00a9 2016 by Nevi<br/>Powered by GameTracker.com</center></html>", SwingConstants.CENTER);
            copyright.setFont(copyright.getFont().deriveFont(10f));
            
            // listener used to refresh current table
            refreshButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    CustomTableModel model;
                    
                    if( MainFrame.currentTab == 0)
                    {
                        model = (CustomTableModel) onlineServersTable.getModel();
                        model.refresh(parser, 0);
                    }
                    else
                    {
                        model = (CustomTableModel) recentServersTable.getModel();
                        model.refresh(parser, 1);
                    }
                        
                }
            });
            
            // listener used to connect to selected server
            connectButton.addActionListener(new ActionListener()
            { 
                public void actionPerformed(ActionEvent e)
                {
                    String IP;
                    
                    // checking if some row on current tab is selected
                    if( (onlineServersTable.getSelectedRow() != -1 && MainFrame.currentTab == 0) ||
                            (recentServersTable.getSelectedRow() != -1) && MainFrame.currentTab == 1)
                    {
                        if( MainFrame.currentTab == 0)
                            IP = parser.serverArray[onlineServersTable.getSelectedRow()][3];
                        else
                            IP = parser.recentServerArray[recentServersTable.getSelectedRow()][3];
                        
                        try 
                        {
                            filesManager.reportConnecting(IP, parser);
                            launcher.connectTo(path, IP);
                            System.exit(0);
                        } 
                        catch (Exception e1) 
                        {
                            e1.printStackTrace();
                        }
                    }
                } 
              } );
                        
            // additional panel, used to align both buttons to the EAST
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(refreshButton);
            buttonPanel.add(connectButton);
            
            bottomPanel.add(buttonPanel, BorderLayout.EAST);
            bottomPanel.add(copyright, BorderLayout.AFTER_LAST_LINE);
            
            add(bottomPanel, gbc);
        }
        
        // function used to filter list based on text entered in search box
        private void nameFilter()
        {
            RowFilter<CustomTableModel, Integer> rf = null;
             
            //If current expression doesn't parse, don't update.
            try
            {
                rf = RowFilter.regexFilter("(?i)" + filterText.getText(), 0);
            } catch (java.util.regex.PatternSyntaxException e)
            {
                return;
            }
            
            // if search box is empty set row filter back to emptyFilter
            if( filterText.getText().equals("") )
            {
                recentSorter.setRowFilter(this.emptyFilter);
                onlineSorter.setRowFilter(this.emptyFilter);
            }
            else
            {
                recentSorter.setRowFilter(rf);
                onlineSorter.setRowFilter(rf);
            } 
        }
        
        // function used to set image in the rightPanel according to the selected server
        private void setImage(Object map)
        {   
            String localMap = (String) map;
            
            try
            {
                switch( localMap.toLowerCase() )
                {
                    case "obj/obj_team1":
                        img = ImageIO.read(new File("images/thehunt.jpg"));
                        imageLabel.setText("<html><b>The Hunt</b></html>");
                        imageLabel.setIcon(new ImageIcon(img));
                        break;
                        
                    case "obj/obj_team2":
                        img = ImageIO.read(new File("images/v2.jpg"));
                        imageLabel.setText("<html><b>V2 Rocket Facility</b></html>");
                        imageLabel.setIcon(new ImageIcon(img));
                        break;
                        
                    case "obj/obj_team3":
                        img = ImageIO.read(new File("images/omaha.jpg"));
                        imageLabel.setText("<html><b>Omaha Beach</b></html>");
                        imageLabel.setIcon(new ImageIcon(img));
                        break;
                        
                    case "obj/obj_team4":
                        img = ImageIO.read(new File("images/thebridge.jpg"));
                        imageLabel.setText("<html><b>The Bridge</b></html>");
                        imageLabel.setIcon(new ImageIcon(img));
                        break;
                        
                    case "dm/mohdm1":
                        img = ImageIO.read(new File("images/france.jpg"));
                        imageLabel.setText("<html><b>Southern France</b></html>");
                        imageLabel.setIcon(new ImageIcon(img));
                        break;
                        
                    case "dm/mohdm2":
                        img = ImageIO.read(new File("images/destroyed.jpg"));
                        imageLabel.setText("<html><b>Destroyed Village</b></html>");
                        imageLabel.setIcon(new ImageIcon(img));
                        break
                        ;
                    case "dm/mohdm3":
                        img = ImageIO.read(new File("images/remagen.jpg"));
                        imageLabel.setText("<html><b>Remagen</b></html>");
                        imageLabel.setIcon(new ImageIcon(img));
                        break;
                        
                    case "dm/mohdm4":
                        img = ImageIO.read(new File("images/thecrossroads.jpg"));
                        imageLabel.setText("<html><b>The Crossroads</b></html>");
                        imageLabel.setIcon(new ImageIcon(img));
                        break;
                    
                    case "dm/mohdm5":
                        img = ImageIO.read(new File("images/snowy.jpg"));
                        imageLabel.setText("<html><b>Snowy Park</b></html>");
                        imageLabel.setIcon(new ImageIcon(img));
                        break;
                        
                    case "dm/mohdm6":
                        img = ImageIO.read(new File("images/stalingrad.jpg"));
                        imageLabel.setText("<html><b>Stalingrad</b></html>");
                        imageLabel.setIcon(new ImageIcon(img));
                        break;
                        
                    case "dm/mohdm7":
                        img = ImageIO.read(new File("images/algiers.jpg"));
                        imageLabel.setText("<html><b>Algiers</b></html>");
                        imageLabel.setIcon(new ImageIcon(img));
                        break;
                    
                    default:
                        img = ImageIO.read(new File("images/default.jpg"));
                        imageLabel.setText("<html><b>" + map + "</b></html>");
                        imageLabel.setIcon(new ImageIcon(img));
                        break;
                }
            } 
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    
    public static class CustomTableModel extends AbstractTableModel
    {
        private static final long serialVersionUID = 1L;
        private Object[][] serversArray;
        private int listLen;
        
        private String[] columnNames = {"Server Name",
                                        "Players",
                                        "Localization",
                                        "IP Address",
                                        "Map"};
       
        public CustomTableModel(Object[][] serversArray, int size)
        {
            this.listLen = size;
            this.serversArray = new Object[serversArray.length][5];
                        
            for(int i = 0; i < serversArray.length; i++)
            {
                for( int j = 0; j < 5; j++ )
                {
                    this.serversArray[i][j] = serversArray[i][j];
                }
            }
        }
        
        public String getColumnName(int col) {
            return columnNames[col];
        }

        public void refresh(Parser parser, int whichTable)
        { 
            try 
            {
                parser.parseOnlineServers();
                if( whichTable == 0)
                    this.serversArray = parser.serverArray;
                else
                    this.serversArray = parser.recentServerArray;
                fireTableDataChanged();
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
            
        }
        
        @Override
        public int getColumnCount() 
        {
            return 5;
        }
        
        @Override
        public int getRowCount() 
        {
            return listLen;
        }
        
        @Override
        public Object getValueAt(int arg0, int arg1)
        {
            return this.serversArray[arg0][arg1];
        }
        
        @Override
        public void setValueAt(Object value, int arg0, int arg1)
        {
            this.serversArray[arg0][arg1] = value;
            this.fireTableCellUpdated(arg0, arg1);
        }
        
        @Override
        public Class<?> getColumnClass(int column) 
        {
            if( column == 1)
                return Integer.class;
            else
                return getValueAt(0, column).getClass();
        }
    }
    
    
    public static void createAndShowGUI(Parser parser, String path) 
    {       
        //Create and set up the window.
        JFrame frame = new JFrame("Medal of Honor: Allied Assault Launcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        ImageIcon icon = new ImageIcon("images/icon.png");
        frame.setIconImage(icon.getImage());
        
        //Add content to the window.
        frame.add(new MainFrame(parser, path), BorderLayout.CENTER);
        
        //Display the window.
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
