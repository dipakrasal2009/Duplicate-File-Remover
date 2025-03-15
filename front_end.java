import java.io.*;
import java.util.LinkedList;
import javax.swing.*;  // For JFrame, JPanel, JButton, etc.
import java.awt.*;    // For Color, Font, etc.
import java.awt.event.*;  // For ActionListener, ActionEvent
import javax.swing.border.BevelBorder;  // For BevelBorder
// ... existing code ...

public class front_end extends JFrame implements ActionListener {
    private DefaultListModel<String> model;
    private JList<String> l1;
    private JPanel panNorth, panCenter1, panCenter2, panCenter;
    private JLabel name, welcome;
    private JButton b1, b2;
    private JTextField jtext;
    private File f;
    private JMenuBar menubar;
    private JMenu menu1;
    private JMenuItem menu2;
    private JFrame splash;
    private TrayIcon trayIcon;
    private SystemTray tray;

    front_end() {
        if (SystemTray.isSupported()) {
            try {
                tray = SystemTray.getSystemTray();
                // Make sure to place your icon.png in the project directory
                Image image = new ImageIcon(getClass().getResource("/icon.png")).getImage();
                
                PopupMenu popup = new PopupMenu();
                MenuItem openItem = new MenuItem("Open");
                openItem.addActionListener(e -> showSplashAndMain());
                
                MenuItem exitItem = new MenuItem("Exit");
                exitItem.addActionListener(e -> {
                    tray.remove(trayIcon);
                    System.exit(0);
                });
                
                popup.add(openItem);
                popup.addSeparator();
                popup.add(exitItem);
                
                trayIcon = new TrayIcon(image, "Duplicate File Remover", popup);
                trayIcon.setImageAutoSize(true);
                trayIcon.addActionListener(e -> showSplashAndMain());
                
                tray.add(trayIcon);
                
                // Start minimized
                setVisible(false);
                
            } catch (AWTException e) {
                System.err.println("Tray icon could not be added: " + e.getMessage());
                // If tray icon fails, show window normally
                showSplashAndMain();
            }
        } else {
            System.err.println("System tray is not supported");
            showSplashAndMain();
        }

        initializeComponents();
    }

    private void initializeComponents() {
        splash = new JFrame("Welcome!");
        splash.setBounds(100, 100, 700, 250);

        welcome = new JLabel("Welcome to Duplicate File Remover!!");
        welcome.setFont(new Font("Courier New", Font.BOLD, 30));
        welcome.setForeground(new Color(100, 0, 0));
        splash.add(welcome);

        splash.setResizable(false);
        splash.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        splash.setVisible(true);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        splash.setVisible(false);
        splash.dispose();

        menubar = new JMenuBar();
        menubar.setBorder(new BevelBorder(BevelBorder.RAISED));
        menu1 = new JMenu("Help");
        menu2 = new JMenuItem("About");
        menubar.add(menu1);
        menubar.add(menu2);
        setJMenuBar(menubar);

        menu2.addActionListener(this);

        name = new JLabel("Duplicate File Remover");
        name.setFont(new Font("Courier New", Font.BOLD, 30));
        name.setForeground(new Color(100, 0, 0));

        panNorth = new JPanel();
        panNorth.setBackground(new Color(127, 112, 219));
        panNorth.add(name);

        b1 = new JButton("Choose Folder");
        b2 = new JButton("Delete Duplicate Files");
        jtext = new JTextField(20);

        b1.setBounds(200, 30, 150, 30);
        b2.setBounds(100, 80, 150, 40);
        panCenter1 = new JPanel();
        panCenter1.setLayout(null);
        panCenter1.add(jtext);
        panCenter1.add(b1);
        panCenter1.add(b2);

        model = new DefaultListModel<>();
        l1 = new JList<>(model);
        l1.setBackground(new Color(205, 201, 201));

        panCenter2 = new JPanel(new GridLayout());
        panCenter2.add(new JScrollPane(l1));

        panCenter = new JPanel(new GridLayout(1, 2));
        panCenter.setBackground(new Color(238, 127, 238));
        panCenter.add(panCenter1);
        panCenter.add(panCenter2);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(panCenter, "Center");
        this.add(panNorth, "North");
        setTitle("Duplicate File Remover");
        this.setBounds(100, 100, 800, 400);
        this.setResizable(false);

        setVisible(true);

        l1.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                // Handle item selection if needed
            }
        });

        b1.addActionListener(this);
        b2.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == b1) {
            JFileChooser filechooser = new JFileChooser();
            filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (filechooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                f = filechooser.getSelectedFile();
                jtext.setText(f.getAbsolutePath());
            } else {
                JOptionPane.showMessageDialog(this, "No folder selected.", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == b2) {
            try {
                Delete_Duplicate dobj = new Delete_Duplicate();
                dobj.list(f.getAbsolutePath());
                LinkedList<String> ll = dobj.getLlist();
                for (String item : ll) {
                    model.addElement(item);
                }
                model.addElement("Deleted All duplicate files!");
                JOptionPane.showMessageDialog(this, "Task Completed!");
            } catch (Exception eobj) {
                eobj.printStackTrace();
            }
        } else if (e.getSource() == menu2) {
            // About window code here (same as original)
        }
    }

    private void showSplashAndMain() {
        setVisible(true);
        initializeComponents();
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> new front_end());
    }
}
