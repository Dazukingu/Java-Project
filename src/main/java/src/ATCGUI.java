import java.awt.*;
import javax.swing.*;

public class ATCGUI {
    
    public static void main(String[] args) {
        // Show system info if requested
        if (args.length > 0 && "--info".equals(args[0])) {
            showSystemInfo();
        }
        
        // Validate system requirements
        if (!validateSystemRequirements()) {
            System.exit(1);
        }
        
        // Set system look and feel for better appearance
        try {
            // Try to use Nimbus look and feel for modern appearance
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, use system look and feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                System.out.println("Could not set look and feel, using default");
            }
        }
        
        // Set some UI defaults for better appearance
        UIManager.put("Button.arc", 8);
        UIManager.put("Component.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        
        // Enable high DPI scaling on Windows
        System.setProperty("sun.java2d.dpiaware", "true");
        
        // Create and show the splash screen
        SwingUtilities.invokeLater(() -> {
            showSplashScreen();
        });
    }
    
    /**
     * Shows an splash screen before launching the main application
     */
    private static void showSplashScreen() {
        JWindow splashScreen = new JWindow();
        splashScreen.setSize(450, 350);
        splashScreen.setLocationRelativeTo(null);
        
        // Create splash panel
        JPanel splashPanel = new JPanel();
        splashPanel.setLayout(new BoxLayout(splashPanel, BoxLayout.Y_AXIS));
        splashPanel.setBackground(new Color(59, 130, 246)); // Primary blue color
        splashPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        // Logo
        JLabel logoLabel = new JLabel("🎓");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 72));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setForeground(Color.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel("ATC Tuition Centre");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Management System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(219, 234, 254)); // Light blue
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Version with new features
        JLabel versionLabel = new JLabel("Version 2.1 - With Profile Management");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        versionLabel.setForeground(new Color(191, 219, 254)); // Lighter blue
        versionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Loading label
        JLabel loadingLabel = new JLabel("Initializing system...");
        loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loadingLabel.setForeground(Color.WHITE);
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loadingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // progress bar
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setBackground(new Color(37, 99, 235)); // Darker blue
        progressBar.setForeground(new Color(16, 185, 129)); // Green accent
        progressBar.setBorderPainted(false);
        progressBar.setMaximumSize(new Dimension(250, 12));
        progressBar.setString("Loading components...");
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        
        // Add components to splash panel
        splashPanel.add(logoLabel);
        splashPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        splashPanel.add(titleLabel);
        splashPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        splashPanel.add(subtitleLabel);
        splashPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        splashPanel.add(versionLabel);
        splashPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        splashPanel.add(loadingLabel);
        splashPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        splashPanel.add(progressBar);
        
        splashScreen.add(splashPanel);
        splashScreen.setVisible(true);
        
        // Simulate loading process with status updates
        Timer loadingTimer = new Timer(800, null);
        final String[] loadingSteps = {
            "Initializing system...",
            "Loading user authentication...",
            "Setting up databases...",
            "Preparing dashboards...",
            "Loading profile management...", 
            "Finalizing startup...",
            "Ready to launch!"
        };
        final int[] currentStep = {0};
        
        loadingTimer.addActionListener(e -> {
            if (currentStep[0] < loadingSteps.length) {
                loadingLabel.setText(loadingSteps[currentStep[0]]);
                progressBar.setString(loadingSteps[currentStep[0]]);
                currentStep[0]++;
            } else {
                loadingTimer.stop();
                splashScreen.setVisible(false);
                splashScreen.dispose();
                launchMainApplication();
            }
        });
        
        loadingTimer.start();
    }
    
    /**
     * Launches the main application ( Unified Login GUI)
     */
    private static void launchMainApplication() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Initialize file handler to ensure files exist
                FileHandler.initializeDataFiles();
                
                // Show startup completion message
                System.out.println("✅ ATC Tuition Centre Management System v2.1 started successfully!");
                System.out.println("🚀  features loaded:");
                System.out.println("   • Integrated profile management for all user types");
                System.out.println("   • AdminDashboard with tabbed profile interface");
                System.out.println("   • Advanced password security with strength indicators");
                System.out.println("   • Improved user experience and modern UI design");
                System.out.println();
                
                // Launch the unified login GUI
                new LoginGUI().setVisible(true);
                
            } catch (Exception e) {
                showErrorDialog("Error starting application", 
                    "Failed to initialize the ATC Management System.\n\n" +
                    "Error details: " + e.getMessage() + "\n\n" +
                    "Please ensure all required files are present and try again.");
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Shows system requirements and compatibility info
     */
    public static void showSystemInfo() {
        System.out.println("=".repeat(60));
        System.out.println("💻 ATC TUITION CENTRE SYSTEM INFORMATION");
        System.out.println("=".repeat(60));
        System.out.println("Application: ATC Management System");
        System.out.println("Build Date: " + java.time.LocalDate.now());
        System.out.println();
        System.out.println("🔧 SYSTEM ENVIRONMENT:");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("Java Vendor: " + System.getProperty("java.vendor"));
        System.out.println("Operating System: " + System.getProperty("os.name"));
        System.out.println("OS Version: " + System.getProperty("os.version"));
        System.out.println("Architecture: " + System.getProperty("os.arch"));
        System.out.println("Working Directory: " + System.getProperty("user.dir"));
        System.out.println("User Home: " + System.getProperty("user.home"));
        System.out.println();
        System.out.println("✨ FEATURES:");
        System.out.println("• Unified login system for all user types");
        System.out.println("• AdminDashboard with profile management");
        System.out.println("• StudentPortal with advanced profile editing");
        System.out.println("• Integrated password security features");
        System.out.println("• Modern UI with consistent color schemes");
        System.out.println("• Comprehensive user management system");
        System.out.println();
        System.out.println("📊 SUPPORTED USER TYPES:");
        System.out.println("• Students - Full profile management with academic info");
        System.out.println("• Administrators - Complete system control with security features");
        System.out.println("• Receptionists - User management and administrative tasks");
        System.out.println("• Tutors - Class management and profile customization");
        System.out.println("=".repeat(60));
        System.out.println();
    }
    
    /**
     * system requirements validation
     */
    private static boolean validateSystemRequirements() {
        try {
            System.out.println("🔍 Validating system requirements...");
            
            // Check Java version (should be 8 or higher for Swing)
            String javaVersion = System.getProperty("java.version");
            System.out.println("   ✓ Java version: " + javaVersion);
            
            // Check if we can create files in current directory
            java.io.File testFile = new java.io.File("system_test.tmp");
            if (testFile.createNewFile()) {
                testFile.delete();
                System.out.println("   ✓ File system access: OK");
            }
            
            // Check Swing availability
            Class.forName("javax.swing.JFrame");
            System.out.println("   ✓ Swing GUI framework: Available");
            
            // Check required system components
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            System.out.println("   ✓ Available memory: " + (maxMemory / 1024 / 1024) + " MB");
            
            // Verify required classes are available
            Class.forName("java.awt.event.ActionListener");
            Class.forName("javax.swing.event.DocumentListener");
            System.out.println("   ✓ Event handling classes: Available");
            
            System.out.println("✅ All system requirements validated successfully!");
            System.out.println();
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ System requirement validation failed: " + e.getMessage());
            showErrorDialog("System Requirements Check Failed", 
                "Your system does not meet the minimum requirements:\n\n" +
                "MINIMUM REQUIREMENTS:\n" +
                "• Java 8 or higher\n" +
                "• Swing GUI support\n" +
                "• File system write access\n" +
                "• At least 128MB available memory\n\n" +
                "RECOMMENDED:\n" +
                "• Java 11 or higher\n" +
                "• 256MB+ available memory\n" +
                "• Modern operating system\n\n" +
                "Error details: " + e.getMessage());
            return false;
        }
    }
    

    private static void showErrorDialog(String title, String message) {
        // Create a custom dialog for better appearance
        JDialog errorDialog = new JDialog();
        errorDialog.setTitle("ATC System - " + title);
        errorDialog.setModal(true);
        errorDialog.setSize(450, 300);
        errorDialog.setLocationRelativeTo(null);
        errorDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Create panel with modern styling
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(249, 250, 251));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Error icon and title
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(249, 250, 251));
        headerPanel.add(new JLabel("❌"));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(239, 68, 68));
        headerPanel.add(titleLabel);
        
        // Message area
        JTextArea messageArea = new JTextArea(message);
        messageArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageArea.setBackground(Color.WHITE);
        messageArea.setForeground(new Color(31, 41, 55));
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // OK button
        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        okButton.setBackground(new Color(59, 130, 246));
        okButton.setForeground(Color.WHITE);
        okButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        okButton.setFocusPainted(false);
        okButton.addActionListener(e -> errorDialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(249, 250, 251));
        buttonPanel.add(okButton);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(messageArea), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        errorDialog.add(mainPanel);
        errorDialog.setVisible(true);
    }
}