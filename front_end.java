import java.io.*;
import java.util.LinkedList;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

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
        // Improved splash screen
        splash = new JFrame("Welcome!");
        splash.setUndecorated(true); // Remove window decorations for a cleaner look
        splash.setBackground(new Color(0, 0, 0, 0)); // Transparent background
        splash.setBounds(100, 100, 700, 250);
        
        JPanel splashPanel = new JPanel(new BorderLayout());
        splashPanel.setBackground(new Color(33, 33, 33));
        splashPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)));
        
        welcome = new JLabel("Welcome to Duplicate File Remover", JLabel.CENTER);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcome.setForeground(new Color(240, 240, 240));
        
        JLabel subtext = new JLabel("Scanning and cleaning your files...", JLabel.CENTER);
        subtext.setFont(new Font("Segoe UI Light", Font.PLAIN, 16));
        subtext.setForeground(new Color(200, 200, 200));
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setForeground(new Color(70, 130, 180));
        progressBar.setBackground(new Color(60, 60, 60));
        progressBar.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(new Color(33, 33, 33));
        textPanel.add(welcome, BorderLayout.NORTH);
        textPanel.add(subtext, BorderLayout.CENTER);
        textPanel.add(progressBar, BorderLayout.SOUTH);
        textPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        splashPanel.add(textPanel, BorderLayout.CENTER);
        splash.add(splashPanel);
        
        splash.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        splash.setResizable(false);
        splash.setVisible(true);
        
        // Center splash screen
        splash.setLocationRelativeTo(null);
        
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        splash.setVisible(false);
        splash.dispose();

        // Improved menu bar
        menubar = new JMenuBar();
        menubar.setBorder(BorderFactory.createEmptyBorder());
        menubar.setBackground(new Color(33, 33, 33));
        
        menu1 = new JMenu("Help");
        menu1.setForeground(Color.WHITE);
        menu1.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        menu2 = new JMenuItem("About");
        menu2.setBackground(new Color(33, 33, 33));
        menu2.setForeground(Color.WHITE);
        
        menubar.add(menu1);
        menubar.add(menu2);
        setJMenuBar(menubar);

        menu2.addActionListener(this);

        // Main UI setup with improved colors and layout
        setLayout(new BorderLayout(0, 0));
        
        // Header panel
        panNorth = new JPanel();
        panNorth.setLayout(new BorderLayout());
        panNorth.setBackground(new Color(70, 130, 180)); // Steel blue
        panNorth.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        name = new JLabel("Duplicate File Remover");
        name.setFont(new Font("Segoe UI", Font.BOLD, 24));
        name.setForeground(Color.WHITE);
        
        JLabel subtitle = new JLabel("Clean your storage efficiently");
        subtitle.setFont(new Font("Segoe UI Light", Font.PLAIN, 14));
        subtitle.setForeground(new Color(230, 230, 230));
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(70, 130, 180));
        titlePanel.add(name, BorderLayout.NORTH);
        titlePanel.add(subtitle, BorderLayout.SOUTH);
        
        panNorth.add(titlePanel, BorderLayout.WEST);

        // Input panel (left side)
        panCenter1 = new JPanel();
        panCenter1.setLayout(new BoxLayout(panCenter1, BoxLayout.Y_AXIS));
        panCenter1.setBackground(new Color(240, 240, 240));
        panCenter1.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 15));
        
        JLabel folderLabel = new JLabel("Select Folder to Scan");
        folderLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        folderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        folderLabel.setForeground(new Color(60, 60, 60));
        
        JPanel textPanel2 = new JPanel(new BorderLayout(10, 0));
        textPanel2.setBackground(new Color(240, 240, 240));
        textPanel2.setAlignmentX(Component.LEFT_ALIGNMENT);
        textPanel2.setMaximumSize(new Dimension(2000, 35));
        
        jtext = new JTextField(20);
        jtext.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jtext.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        
        b1 = new JButton("Browse");
        b1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b1.setBackground(new Color(100, 160, 200));
        b1.setForeground(Color.WHITE);
        b1.setFocusPainted(false);
        b1.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
        textPanel2.add(jtext, BorderLayout.CENTER);
        textPanel2.add(b1, BorderLayout.EAST);
        
        b2 = new JButton("SCAN & REMOVE DUPLICATES");
        b2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b2.setBackground(new Color(70, 130, 180));
        b2.setForeground(Color.WHITE);
        b2.setFocusPainted(false);
        b2.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        b2.setAlignmentX(Component.LEFT_ALIGNMENT);
        b2.setMaximumSize(new Dimension(2000, 45));
        
        // Add some space between components
        panCenter1.add(folderLabel);
        panCenter1.add(Box.createRigidArea(new Dimension(0, 10)));
        panCenter1.add(textPanel2);
        panCenter1.add(Box.createRigidArea(new Dimension(0, 25)));
        panCenter1.add(b2);
        panCenter1.add(Box.createVerticalGlue());
        
        // Results panel (right side)
        JPanel resultsPanel = new JPanel(new BorderLayout(0, 10));
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 25, 25));
        resultsPanel.setBackground(new Color(250, 250, 250));
        
        JLabel resultsLabel = new JLabel("Results");
        resultsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        resultsLabel.setForeground(new Color(60, 60, 60));
        
        model = new DefaultListModel<>();
        l1 = new JList<>(model);
        l1.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        l1.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(l1);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        resultsPanel.add(resultsLabel, BorderLayout.NORTH);
        resultsPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Main content panel
        panCenter = new JPanel(new GridLayout(1, 2));
        panCenter.add(panCenter1);
        panCenter.add(resultsPanel);
        
        // Add everything to the frame
        add(panNorth, BorderLayout.NORTH);
        add(panCenter, BorderLayout.CENTER);
        
        // Setup window properties
        setTitle("Duplicate File Remover");
        setSize(900, 500);
        setMinimumSize(new Dimension(800, 400));
        setLocationRelativeTo(null); // Center on screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Register event listeners
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
            if (jtext.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a folder first.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Show progress dialog
            JDialog progressDialog = new JDialog(this, "Processing", true);
            progressDialog.setLayout(new BorderLayout());
            
            JPanel progressPanel = new JPanel(new BorderLayout(10, 10));
            progressPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            JLabel statusLabel = new JLabel("Scanning for duplicate files...");
            statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            
            JProgressBar progressBar = new JProgressBar();
            progressBar.setIndeterminate(true);
            
            progressPanel.add(statusLabel, BorderLayout.NORTH);
            progressPanel.add(progressBar, BorderLayout.CENTER);
            
            progressDialog.add(progressPanel);
            progressDialog.setSize(300, 100);
            progressDialog.setLocationRelativeTo(this);
            
            // Use SwingWorker to run the process in background
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        model.clear(); // Clear previous results
                        Delete_Duplicate dobj = new Delete_Duplicate();
                        dobj.list(f.getAbsolutePath());
                        LinkedList<String> ll = dobj.getLlist();
                        
                        for (String item : ll) {
                            model.addElement(item);
                        }
                        
                        if (ll.isEmpty()) {
                            model.addElement("No duplicate files found!");
                        } else {
                            model.addElement("All duplicate files have been removed!");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return null;
                }
                
                @Override
                protected void done() {
                    progressDialog.dispose();
                    JOptionPane.showMessageDialog(front_end.this, "Task completed successfully!", "Complete", JOptionPane.INFORMATION_MESSAGE);
                }
            };
            
            worker.execute();
            progressDialog.setVisible(true);
        } else if (e.getSource() == menu2) {
            // About dialog with improved UI
            JDialog aboutDialog = new JDialog(this, "About", true);
            aboutDialog.setLayout(new BorderLayout());
            
            JPanel aboutPanel = new JPanel(new BorderLayout(15, 15));
            aboutPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            aboutPanel.setBackground(Color.WHITE);
            
            JLabel titleLabel = new JLabel("Duplicate File Remover");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            
            JLabel versionLabel = new JLabel("Version 1.0");
            versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            JTextArea descArea = new JTextArea(
                "This application helps you identify and remove duplicate files from your system, " +
                "freeing up valuable disk space.\n\n" +
                "• Select a folder to scan\n" +
                "• The application will find files with identical content\n" +
                "• Duplicates will be automatically removed\n" +
                "• You'll see a list of all removed files"
            );
            descArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            descArea.setLineWrap(true);
            descArea.setWrapStyleWord(true);
            descArea.setEditable(false);
            descArea.setBackground(Color.WHITE);
            
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(Color.WHITE);
            headerPanel.add(titleLabel, BorderLayout.NORTH);
            headerPanel.add(versionLabel, BorderLayout.SOUTH);
            
            JButton closeButton = new JButton("Close");
            closeButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
            closeButton.addActionListener(event -> aboutDialog.dispose());
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(Color.WHITE);
            buttonPanel.add(closeButton);
            
            aboutPanel.add(headerPanel, BorderLayout.NORTH);
            aboutPanel.add(descArea, BorderLayout.CENTER);
            aboutPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            aboutDialog.add(aboutPanel);
            aboutDialog.setSize(400, 300);
            aboutDialog.setLocationRelativeTo(this);
            aboutDialog.setVisible(true);
        }
    }

    private void showSplashAndMain() {
        setVisible(true);
        initializeComponents();
    }

    public static void main(String args[]) {
        try {
            // Set system look and feel for better appearance
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new front_end());
    }
}
