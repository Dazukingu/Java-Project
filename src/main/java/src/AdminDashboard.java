import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Enhanced AdminDashboard with integrated profile management, user registration, and tutor assignment
 * Fixed quick actions navigation and added comprehensive user management
 */
public class AdminDashboard extends JFrame {
    private User currentAdmin;
    private DataManager dataManager;
    private JTabbedPane tabbedPane;
    
    // Color scheme matching other dashboards
    private final Color PRIMARY_COLOR = new Color(59, 130, 246);    // Blue
    private final Color SECONDARY_COLOR = new Color(243, 244, 246);  // Light gray
    private final Color ACCENT_COLOR = new Color(16, 185, 129);      // Green
    private final Color ERROR_COLOR = new Color(239, 68, 68);        // Red
    private final Color TEXT_COLOR = new Color(31, 41, 55);          // Dark gray
    private final Color BACKGROUND_COLOR = new Color(249, 250, 251); // Very light gray
    private final Color WARNING_COLOR = new Color(245, 158, 11);     // Orange
    
    public AdminDashboard(User admin) {
        this.currentAdmin = admin;
        this.dataManager = new DataManager();
        
        initializeComponents();
        setupLayout();
        setupWindow();
    }
    
    private void initializeComponents() {
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setBackground(Color.WHITE);
        
        // Add tabs with enhanced functionality
        tabbedPane.addTab("🏠 Dashboard", createDashboardPanel());
        tabbedPane.addTab("📊 System Stats", createSystemStatsPanel());
        tabbedPane.addTab("👥 User Management", createUserManagementPanel());
        tabbedPane.addTab("📈 Income Reports", createIncomeReportPanel());
        tabbedPane.addTab("➕ Register Users", createRegisterUsersPanel());
        tabbedPane.addTab("🎯 Assign Tutor", createAssignTutorPanel());
        tabbedPane.addTab("👤 My Profile", createEnhancedProfilePanel());
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Welcome section with enhanced admin info
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setBackground(Color.WHITE);
        welcomePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(30, 30, 30, 30)
        ));
        
        JLabel welcomeLabel = new JLabel("Welcome, " + currentAdmin.getName() + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(TEXT_COLOR);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel roleLabel = new JLabel("Administrator Portal - ATC Tuition Centre");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        roleLabel.setForeground(new Color(107, 114, 128));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel idLabel = new JLabel("Admin ID: " + currentAdmin.getUserId());
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        idLabel.setForeground(new Color(107, 114, 128));
        idLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add system health indicator
        JLabel systemHealthLabel = new JLabel("✅ All Systems Operational");
        systemHealthLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        systemHealthLabel.setForeground(ACCENT_COLOR);
        systemHealthLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        welcomePanel.add(welcomeLabel);
        welcomePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        welcomePanel.add(roleLabel);
        welcomePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        welcomePanel.add(idLabel);
        welcomePanel.add(Box.createRigidArea(new Dimension(0, 15)));
        welcomePanel.add(systemHealthLabel);
        
        // Enhanced quick stats with user management indicators
        JPanel statsPanel = createEnhancedStatsPanel();
        
        // Enhanced quick actions panel
        JPanel actionsPanel = createQuickActionsPanel();
        
        // Recent activity panel
        JPanel activityPanel = createRecentActivityPanel();
        
        JPanel mainContent = new JPanel(new BorderLayout(20, 20));
        mainContent.setBackground(BACKGROUND_COLOR);
        mainContent.add(statsPanel, BorderLayout.CENTER);
        mainContent.add(activityPanel, BorderLayout.EAST);
        
        panel.add(welcomePanel, BorderLayout.NORTH);
        panel.add(mainContent, BorderLayout.CENTER);
        panel.add(actionsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createRecentActivityPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                "Recent System Activity", 0, 0, new Font("Segoe UI", Font.BOLD, 16), TEXT_COLOR),
            new EmptyBorder(15, 15, 15, 15)
        ));
        panel.setPreferredSize(new Dimension(300, 200));
        
        JTextArea activityArea = new JTextArea(8, 25);
        activityArea.setEditable(false);
        activityArea.setFont(new Font("Courier New", Font.PLAIN, 11));
        activityArea.setBackground(SECONDARY_COLOR);
        activityArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Load recent activity
        StringBuilder activity = new StringBuilder();
        activity.append("Recent System Activity:\n\n");
        
        try {
            // Get recent logins
            java.io.File loginFile = new java.io.File("login_history.txt");
            if (loginFile.exists()) {
                java.util.List<String> lines = new java.util.ArrayList<>();
                try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(loginFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        lines.add(line);
                    }
                }
                
                // Show last 5 logins
                int start = Math.max(0, lines.size() - 5);
                for (int i = start; i < lines.size(); i++) {
                    String[] parts = lines.get(i).split(",");
                    if (parts.length >= 3) {
                        activity.append("• ").append(parts[0]).append(" (").append(parts[1]).append(")\n");
                        activity.append("  ").append(parts[2]).append("\n\n");
                    }
                }
            } else {
                activity.append("No recent activity logged.\n");
            }
            
            // Add system stats
            SystemStats stats = getSystemStats();
            activity.append("Current System Status:\n");
            activity.append("• Total Users: ").append(stats.getTotalStudents() + stats.getTotalTutors() + 5).append("\n");
            activity.append("• System Health: Operational\n");
            
        } catch (Exception e) {
            activity.append("Error loading activity data.\n");
        }
        
        activityArea.setText(activity.toString());
        
        JScrollPane activityScroll = new JScrollPane(activityArea);
        activityScroll.setBorder(null);
        activityScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        activityScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        panel.add(activityScroll, BorderLayout.CENTER);
        
        return panel;
    }
    
    // NEW: Register Users Panel
    private JPanel createRegisterUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Title
        JLabel titleLabel = new JLabel("Register New Users");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JLabel subtitleLabel = new JLabel("Create new accounts for Tutors, Receptionists, and Administrators");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(107, 114, 128));
        subtitleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(BACKGROUND_COLOR);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        titlePanel.add(subtitleLabel);
        
        // Registration options
        JPanel optionsPanel = new JPanel(new GridLayout(1, 3, 30, 0));
        optionsPanel.setBackground(BACKGROUND_COLOR);
        optionsPanel.setBorder(new EmptyBorder(50, 50, 50, 50));
        
        // Tutor Registration Card
        JPanel tutorCard = createRegistrationCard("👩‍🏫", "Register Tutor", 
            "Add new teaching staff to the system", "TUTOR");
        
        // Receptionist Registration Card
        JPanel receptionistCard = createRegistrationCard("📋", "Register Receptionist", 
            "Add new administrative staff", "RECEPTIONIST");
        
        // Admin Registration Card
        JPanel adminCard = createRegistrationCard("🔧", "Register Administrator", 
            "Add new system administrators", "ADMIN");
        
        optionsPanel.add(tutorCard);
        optionsPanel.add(receptionistCard);
        optionsPanel.add(adminCard);
        
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(optionsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    // NEW: Assign Tutor Panel
    private JPanel createAssignTutorPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Title
        JLabel titleLabel = new JLabel("Assign Tutor to Subject");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JLabel subtitleLabel = new JLabel("Create new class assignments for tutors");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(107, 114, 128));
        subtitleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(BACKGROUND_COLOR);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        titlePanel.add(subtitleLabel);
        
        // Assignment interface
        JPanel assignmentPanel = new JPanel(new BorderLayout());
        assignmentPanel.setBackground(Color.WHITE);
        assignmentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                "Quick Assign Tutor", 0, 0, new Font("Segoe UI", Font.BOLD, 16), TEXT_COLOR),
            new EmptyBorder(30, 30, 30, 30)
        ));
        
        JButton assignButton = createStyledButton("🎯 Open Assignment Dialog", PRIMARY_COLOR);
        assignButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        assignButton.setPreferredSize(new Dimension(300, 50));
        assignButton.addActionListener(e -> openAssignTutorDialog());
        
        JLabel infoLabel = new JLabel("<html><center>Click the button above to open the tutor assignment dialog.<br/>" +
            "You can assign tutors to specific subjects and levels.<br/>" +
            "New class entries will be created automatically.</center></html>");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoLabel.setForeground(new Color(107, 114, 128));
        infoLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(assignButton);
        
        assignmentPanel.add(buttonPanel, BorderLayout.CENTER);
        assignmentPanel.add(infoLabel, BorderLayout.SOUTH);
        
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(assignmentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createRegistrationCard(String icon, String title, String description, String userType) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(30, 25, 30, 25)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(new Color(107, 114, 128));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton registerButton = createStyledButton("Register " + userType, PRIMARY_COLOR);
        registerButton.addActionListener(e -> openRegisterDialog(userType));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(iconLabel);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(descLabel);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(registerButton);
        
        // Hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(SECONDARY_COLOR);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                    new EmptyBorder(29, 24, 29, 24)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
                    new EmptyBorder(30, 25, 30, 25)
                ));
            }
        });
        
        return card;
    }
    
    private void openRegisterDialog(String userType) {
        try {
            RegisterNewUser registerDialog = new RegisterNewUser(this, dataManager, userType);
            registerDialog.setVisible(true);
            
            if (registerDialog.isRegistrationSuccessful()) {
                // Refresh user management data
                refreshUserManagement();
                // Refresh dashboard stats
                tabbedPane.setComponentAt(0, createDashboardPanel());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error opening registration dialog: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void openAssignTutorDialog() {
        try {
            AssignTutor assignDialog = new AssignTutor(this, dataManager);
            assignDialog.setVisible(true);
            
            if (assignDialog.isAssignmentSuccessful()) {
                // Refresh data
                refreshUserManagement();
                // Refresh dashboard stats
                tabbedPane.setComponentAt(0, createDashboardPanel());
                
                JOptionPane.showMessageDialog(this, 
                    "Tutor assignment completed successfully!\nClass data has been updated.", 
                    "Assignment Complete", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error opening assignment dialog: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // Enhanced Profile Panel with tabbed interface (similar to StudentPortal)
    private JPanel createEnhancedProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create tabbed pane for profile sections
        JTabbedPane profileTabs = new JTabbedPane();
        profileTabs.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        profileTabs.setBackground(Color.WHITE);
        
        // Personal Information tab
        profileTabs.addTab("📋 Personal Info", createScrollablePersonalInfoPanel());
        
        // Password & Security tab
        profileTabs.addTab("🔒 Password & Security", createScrollablePasswordSecurityPanel());
        
        // Administrative Info tab
        profileTabs.addTab("🔧 Admin Details", createScrollableAdminInfoPanel());
        
        panel.add(profileTabs, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createUserManagementPanel() {
        // Create and return the enhanced UserManagement panel with EditUser integration
        UserManagement userManagementPanel = new UserManagement(dataManager, this);
        return userManagementPanel;
    } 
    
    // NEW: Enhanced Income Report Panel (from lab1.admin1)
    private JPanel createIncomeReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Title
        JLabel titleLabel = new JLabel("Monthly Income Report");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1), 
                "Select Report Parameters", 0, 0, new Font("Segoe UI", Font.BOLD, 16), TEXT_COLOR),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Year selection
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(createFieldLabel("Year:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> yearCombo = new JComboBox<>(new String[]{"2025", "2024", "2023", "2022", "2021"});
        yearCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(yearCombo, gbc);
        
        // Month selection
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(createFieldLabel("Month:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> monthCombo = new JComboBox<>(new String[]{
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        });
        monthCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(monthCombo, gbc);
        
        // Level selection
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(createFieldLabel("Level:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> levelCombo = new JComboBox<>(new String[]{
            "Form 1", "Form 2", "Form 3", "Form 4", "Form 5"
        });
        levelCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(levelCombo, gbc);
        
        // Subject selection
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(createFieldLabel("Subject:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> subjectCombo = new JComboBox<>(new String[]{
            "Mathematics", "English", "Malay", "Science", "History", "Geography"
        });
        subjectCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(subjectCombo, gbc);
        
        // Generate button
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton generateButton = createStyledButton("Generate Report", PRIMARY_COLOR);
        formPanel.add(generateButton, gbc);
        
        // Results area
        JTextArea resultsArea = new JTextArea(15, 50);
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        resultsArea.setBackground(SECONDARY_COLOR);
        resultsArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JScrollPane resultsScroll = new JScrollPane(resultsArea);
        resultsScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1), 
            "Report Results", 0, 0, new Font("Segoe UI", Font.BOLD, 16), TEXT_COLOR));
        
        // Generate report action
        generateButton.addActionListener(e -> {
            generateIncomeReport(yearCombo.getSelectedItem().toString(),
                               monthCombo.getSelectedItem().toString(),
                               levelCombo.getSelectedItem().toString(),
                               subjectCombo.getSelectedItem().toString(),
                               resultsArea);
        });
        
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(formPanel, BorderLayout.WEST);
        contentPanel.add(resultsScroll, BorderLayout.CENTER);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Income report generation method
    private void generateIncomeReport(String year, String month, String level, String subject, JTextArea resultsArea) {
        try {
            String monthNum = String.format("%02d", java.util.Arrays.asList(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
            ).indexOf(month) + 1);
            
            Set<String> matchingClassIDs = new HashSet<>();
            
            // Find matching classes
            try (BufferedReader reader = new BufferedReader(new FileReader("class.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] elements = line.split(",");
                    if (elements.length >= 6) {
                        String classID = elements[0].trim();
                        String className = elements[2].trim();
                        String dates = elements[4].trim();
                        
                        if (className.contains(subject) && className.contains(level)) {
                            String[] classDates = dates.split(";");
                            for (String dateStr : classDates) {
                                dateStr = dateStr.trim();
                                if (dateStr.startsWith(year + "-" + monthNum)) {
                                    matchingClassIDs.add(classID);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            
            double totalIncome = 0.0;
            int paymentCount = 0;
            
            // Calculate income from payments
            try (BufferedReader reader = new BufferedReader(new FileReader("payments.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] elements = line.split(",");
                    if (elements.length >= 6) {
                        String classList = elements[4].trim();
                        String amountStr = elements[5].trim();
                        try {
                            double totalPayment = Double.parseDouble(amountStr);
                            String[] paidClasses = classList.split(";");
                            int totalClasses = paidClasses.length;
                            
                            int matched = 0;
                            for (String classID : paidClasses) {
                                if (matchingClassIDs.contains(classID.trim())) {
                                    matched++;
                                }
                            }
                            
                            if (matched > 0) {
                                double valuePerClass = totalPayment / totalClasses;
                                totalIncome += valuePerClass * matched;
                                paymentCount++;
                            }
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
            
            // Format results
            StringBuilder report = new StringBuilder();
            report.append("=== MONTHLY INCOME REPORT ===\n\n");
            report.append("Report Parameters:\n");
            report.append("Year: ").append(year).append("\n");
            report.append("Month: ").append(month).append("\n");
            report.append("Level: ").append(level).append("\n");
            report.append("Subject: ").append(subject).append("\n\n");
            
            report.append("Results:\n");
            report.append("Matching Classes: ").append(matchingClassIDs.size()).append("\n");
            report.append("Payment Records: ").append(paymentCount).append("\n");
            report.append("Total Income: RM").append(String.format("%.2f", totalIncome)).append("\n\n");
            
            if (totalIncome > 0) {
                report.append("✅ Income data found for selected parameters");
            } else {
                report.append("❌ No income recorded for this selection");
            }
            
            resultsArea.setText(report.toString());
            
        } catch (IOException e) {
            resultsArea.setText("Error generating report: " + e.getMessage());
        }
    }
    
    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            "Quick Actions", 0, 0,
            new Font("Segoe UI", Font.BOLD, 16), TEXT_COLOR));
        
        // FIXED: Correct tab navigation
        JButton profileBtn = createActionButton("My Profile", "👤", e -> tabbedPane.setSelectedIndex(6)); // Tab 6 (My Profile)
        JButton statsBtn = createActionButton("System Stats", "📊", e -> tabbedPane.setSelectedIndex(1)); // Tab 1 (System Stats)
        JButton usersBtn = createActionButton("Manage Users", "👥", e -> tabbedPane.setSelectedIndex(2)); // Tab 2 (User Management)
        JButton registerBtn = createActionButton("Register Users", "➕", e -> tabbedPane.setSelectedIndex(4)); // Tab 4 (Register Users)
        JButton assignBtn = createActionButton("Assign Tutor", "🎯", e -> tabbedPane.setSelectedIndex(5)); // Tab 5 (Assign Tutor)
        JButton incomeBtn = createActionButton("Income Report", "📈", e -> tabbedPane.setSelectedIndex(3)); // Tab 3 (Income Reports)
        
        panel.add(profileBtn);
        panel.add(statsBtn);
        panel.add(usersBtn);
        panel.add(registerBtn);
        panel.add(assignBtn);
        panel.add(incomeBtn);
        
        return panel;
    }
    
    private JButton createActionButton(String text, String icon, java.awt.event.ActionListener action) {
        JButton button = new JButton("<html><center>" + icon + "<br>" + text + "</center></html>");
        button.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        button.setBackground(Color.WHITE);
        button.setForeground(TEXT_COLOR);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            new EmptyBorder(15, 20, 15, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 80));
        button.addActionListener(action);
        
        // Enhanced hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(SECONDARY_COLOR);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                    new EmptyBorder(14, 19, 14, 19)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                    new EmptyBorder(15, 20, 15, 20)
                ));
            }
        });
        
        return button;
    }
    
    // Create remaining panels (keeping existing functionality)
    private JPanel createEnhancedStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(30, 0, 30, 0));
        
        SystemStats stats = getSystemStats();
        
        // Create enhanced stat cards
        JPanel studentsCard = createStatCard("👥", "Total Students", String.valueOf(stats.getTotalStudents()));
        JPanel tutorsCard = createStatCard("👩‍🏫", "Total Tutors", String.valueOf(stats.getTotalTutors()));
        JPanel receptionistsCard = createStatCard("📋", "Receptionists", "3"); // From original system
        JPanel adminsCard = createStatCard("🔧", "Administrators", "2"); // From original system
        JPanel classesCard = createStatCard("📚", "Total Classes", String.valueOf(stats.getTotalClasses()));
        JPanel paymentsCard = createStatCard("💳", "Total Payments", String.valueOf(stats.getTotalPayments()));
        JPanel revenueCard = createStatCard("💰", "Total Revenue", "RM" + String.format("%.2f", stats.getTotalRevenue()));
        


        panel.add(studentsCard);
        panel.add(tutorsCard);
        panel.add(receptionistsCard);
        panel.add(adminsCard);
        panel.add(classesCard);
        panel.add(paymentsCard);
        panel.add(revenueCard);
        
        return panel;
    }
    
    // Keep existing profile methods from original AdminDashboard
    private JPanel createScrollablePersonalInfoPanel() {
        // Main scrollable container
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(BACKGROUND_COLOR);
        
        // Content panel that will be scrolled
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Title
        JLabel titleLabel = new JLabel("Administrator Personal Information");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Profile form
        JPanel profileForm = createEditableAdminProfileForm();
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        contentPanel.add(profileForm);
        
        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        mainContainer.add(scrollPane, BorderLayout.CENTER);
        return mainContainer;
    }
    
    private JPanel createScrollablePasswordSecurityPanel() {
        // Main scrollable container
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(BACKGROUND_COLOR);
        
        // Content panel that will be scrolled
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Title
        JLabel titleLabel = new JLabel("Password & Security Settings");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Change password panel
        JPanel passwordPanel = createAdminPasswordChangeFormPanel();
        
        // Security info panel
        JPanel securityInfoPanel = createAdminSecurityInfoPanel();
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        contentPanel.add(passwordPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        contentPanel.add(securityInfoPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 50))); // Extra space at bottom
        
        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        mainContainer.add(scrollPane, BorderLayout.CENTER);
        return mainContainer;
    }
    
    private JPanel createScrollableAdminInfoPanel() {
        // Main scrollable container
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(BACKGROUND_COLOR);
        
        // Content panel that will be scrolled
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Title
        JLabel titleLabel = new JLabel("Administrative Information");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Admin details panel
        JPanel adminPanel = createAdminDetailsPanel();
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        contentPanel.add(adminPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 50))); // Extra space at bottom
        
        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        mainContainer.add(scrollPane, BorderLayout.CENTER);
        return mainContainer;
    }
    
    private JPanel createEditableAdminProfileForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1), 
                "Edit Personal Information", 0, 0, new Font("Segoe UI", Font.BOLD, 16), TEXT_COLOR),
            new EmptyBorder(25, 25, 25, 25)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Create form fields
        JTextField adminIdField = createStyledTextField();
        adminIdField.setText(currentAdmin.getUserId());
        adminIdField.setEditable(false);
        adminIdField.setBackground(SECONDARY_COLOR);
        
        JTextField usernameField = createStyledTextField();
        usernameField.setText(currentAdmin.getUsername());
        usernameField.setEditable(false);
        usernameField.setBackground(SECONDARY_COLOR);
        
        JTextField nameField = createStyledTextField();
        nameField.setText(currentAdmin.getName());
        
        JTextField emailField = createStyledTextField();
        emailField.setText(currentAdmin.getEmail());
        
        JTextField phoneField = createStyledTextField();
        phoneField.setText(currentAdmin.getPhone());
        
        JTextArea addressArea = createStyledTextArea(3, 25);
        // Try to get address from admin data or use default
        String address = getAdminAddress(currentAdmin.getUserId());
        addressArea.setText(address);
        
        // Layout form fields
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(createFieldLabel("Admin ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(adminIdField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(createFieldLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(usernameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(createFieldLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panel.add(createFieldLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        panel.add(createFieldLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(phoneField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(createFieldLabel("Address:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        panel.add(new JScrollPane(addressArea), gbc);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonsPanel.setBackground(Color.WHITE);
        
        JButton updateButton = createStyledButton("Update Profile", ACCENT_COLOR);
        JButton resetButton = createStyledButton("Reset", SECONDARY_COLOR);
        
        updateButton.addActionListener(e -> updateAdminPersonalInfo(nameField, emailField, phoneField, addressArea));
        resetButton.addActionListener(e -> {
            nameField.setText(currentAdmin.getName());
            emailField.setText(currentAdmin.getEmail());
            phoneField.setText(currentAdmin.getPhone());
            addressArea.setText(getAdminAddress(currentAdmin.getUserId()));
        });
        
        buttonsPanel.add(updateButton);
        buttonsPanel.add(resetButton);
        
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(25, 15, 15, 15);
        panel.add(buttonsPanel, gbc);
        
        return panel;
    }
    
    private JPanel createAdminPasswordChangeFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1), 
                "Change Administrator Password", 0, 0, new Font("Segoe UI", Font.BOLD, 16), TEXT_COLOR),
            new EmptyBorder(25, 25, 25, 25)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Password fields with enhanced styling
        JPasswordField currentPasswordField = createStyledPasswordField();
        JPasswordField newPasswordField = createStyledPasswordField();
        JPasswordField confirmPasswordField = createStyledPasswordField();
        
        // Show/hide password toggles
        JCheckBox showCurrentPassword = new JCheckBox("Show");
        JCheckBox showNewPassword = new JCheckBox("Show");
        JCheckBox showConfirmPassword = new JCheckBox("Show");
        
        setupPasswordToggle(showCurrentPassword, currentPasswordField);
        setupPasswordToggle(showNewPassword, newPasswordField);
        setupPasswordToggle(showConfirmPassword, confirmPasswordField);
        
        // Password strength indicator
        JLabel strengthLabel = new JLabel("Password strength:");
        strengthLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JProgressBar strengthBar = new JProgressBar(0, 100);
        strengthBar.setStringPainted(true);
        strengthBar.setString("Enter new password");
        strengthBar.setPreferredSize(new Dimension(200, 20));
        
        // Add real-time password strength checking
        newPasswordField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePasswordStrength(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePasswordStrength(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePasswordStrength(); }
            
            private void updatePasswordStrength() {
                String password = new String(newPasswordField.getPassword());
                int strength = calculatePasswordStrength(password);
                strengthBar.setValue(strength);
                
                if (strength < 30) {
                    strengthBar.setString("Weak");
                    strengthBar.setForeground(ERROR_COLOR);
                } else if (strength < 70) {
                    strengthBar.setString("Medium");
                    strengthBar.setForeground(new Color(255, 193, 7));
                } else {
                    strengthBar.setString("Strong");
                    strengthBar.setForeground(ACCENT_COLOR);
                }
            }
        });
        
        // Layout components
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(createFieldLabel("Current Password:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(currentPasswordField, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(showCurrentPassword, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(createFieldLabel("New Password:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(newPasswordField, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(showNewPassword, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(createFieldLabel("Confirm Password:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(confirmPasswordField, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(showConfirmPassword, gbc);
        
        // Password strength row
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panel.add(strengthLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(strengthBar, gbc);
        
        // Password requirements
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3;
        gbc.insets = new Insets(20, 15, 15, 15);
        panel.add(createPasswordRequirementsPanel(), gbc);
        
        // Buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonsPanel.setBackground(Color.WHITE);
        
        JButton changePasswordButton = createStyledButton("Change Password", PRIMARY_COLOR);
        JButton clearButton = createStyledButton("Clear All", SECONDARY_COLOR);
        
        changePasswordButton.addActionListener(e -> 
            changeAdminPassword(currentPasswordField, newPasswordField, confirmPasswordField));
        
        clearButton.addActionListener(e -> {
            currentPasswordField.setText("");
            newPasswordField.setText("");
            confirmPasswordField.setText("");
            strengthBar.setValue(0);
            strengthBar.setString("Enter new password");
        });
        
        buttonsPanel.add(changePasswordButton);
        buttonsPanel.add(clearButton);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 3;
        gbc.insets = new Insets(25, 15, 15, 15);
        panel.add(buttonsPanel, gbc);
        
        return panel;
    }
    
    private JPanel createAdminSecurityInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1), 
                "Administrator Security Information", 0, 0, new Font("Segoe UI", Font.BOLD, 16), TEXT_COLOR),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Account Status
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createFieldLabel("Account Status:"), gbc);
        gbc.gridx = 1;
        JLabel statusLabel = new JLabel("Active Administrator");
        statusLabel.setForeground(ACCENT_COLOR);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(statusLabel, gbc);
        
        // Last Login
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(createFieldLabel("Last Login:"), gbc);
        gbc.gridx = 1;
        JLabel lastLoginLabel = new JLabel(getCurrentLoginTime());
        lastLoginLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lastLoginLabel.setForeground(new Color(107, 114, 128));
        panel.add(lastLoginLabel, gbc);
        
        // Access Level
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(createFieldLabel("Access Level:"), gbc);
        gbc.gridx = 1;
        JLabel accessLabel = new JLabel("Full Administrative Access");
        accessLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        accessLabel.setForeground(PRIMARY_COLOR);
        panel.add(accessLabel, gbc);
        
        // Account Type
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(createFieldLabel("Account Type:"), gbc);
        gbc.gridx = 1;
        JLabel typeLabel = new JLabel("System Administrator");
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        typeLabel.setForeground(new Color(107, 114, 128));
        panel.add(typeLabel, gbc);
        
        // Security Clearance
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(createFieldLabel("Security Clearance:"), gbc);
        gbc.gridx = 1;
        JLabel clearanceLabel = new JLabel("Level A - Full System Control");
        clearanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        clearanceLabel.setForeground(ERROR_COLOR);
        panel.add(clearanceLabel, gbc);
        
        // System Version
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(createFieldLabel("System Version:"), gbc);
        gbc.gridx = 1;
        JLabel versionLabel = new JLabel("ATC Management System v2.0");
        versionLabel.setForeground(new Color(107, 114, 128));
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(versionLabel, gbc);
        
        return panel;
    }
    
    private JPanel createAdminDetailsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1), 
                "Administrative Details (Read-Only)", 0, 0, new Font("Segoe UI", Font.BOLD, 16), TEXT_COLOR),
            new EmptyBorder(25, 25, 25, 25)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Admin info fields (read-only)
        JTextField adminIdField = createStyledTextField();
        adminIdField.setText(currentAdmin.getUserId());
        adminIdField.setEditable(false);
        adminIdField.setBackground(SECONDARY_COLOR);
        
        JTextField usernameField = createStyledTextField();
        usernameField.setText(currentAdmin.getUsername());
        usernameField.setEditable(false);
        usernameField.setBackground(SECONDARY_COLOR);
        
        JTextField permissionsField = createStyledTextField();
        permissionsField.setText("Full Administrative Permissions");
        permissionsField.setEditable(false);
        permissionsField.setBackground(SECONDARY_COLOR);
        
        JTextField createdDateField = createStyledTextField();
        createdDateField.setText("System Administrator Account");
        createdDateField.setEditable(false);
        createdDateField.setBackground(SECONDARY_COLOR);
        
        // System statistics
        SystemStats stats = getSystemStats();
        JTextArea systemOverviewArea = createStyledTextArea(6, 25);
        systemOverviewArea.setText(
            "System Overview:\n" +
            "• Total Students: " + stats.getTotalStudents() + "\n" +
            "• Total Tutors: " + stats.getTotalTutors() + "\n" +
            "• Total Classes: " + stats.getTotalClasses() + "\n" +
            "• Total Revenue: RM" + String.format("%.2f", stats.getTotalRevenue()) + "\n"
        );
        systemOverviewArea.setEditable(false);
        systemOverviewArea.setBackground(SECONDARY_COLOR);
        
        // Layout admin fields
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(createFieldLabel("Administrator ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(adminIdField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(createFieldLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(usernameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(createFieldLabel("Permissions:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(permissionsField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panel.add(createFieldLabel("Account Type:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(createdDateField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(createFieldLabel("System Overview:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        panel.add(new JScrollPane(systemOverviewArea), gbc);
        
        return panel;
    }
    
    // Keep all existing helper methods from original AdminDashboard
    private void setupPasswordToggle(JCheckBox toggle, JPasswordField passwordField) {
        toggle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        toggle.setBackground(Color.WHITE);
        toggle.addActionListener(e -> {
            if (toggle.isSelected()) {
                passwordField.setEchoChar((char) 0);
                toggle.setText("Hide");
            } else {
                passwordField.setEchoChar('•');
                toggle.setText("Show");
            }
        });
    }
    
    private int calculatePasswordStrength(String password) {
        if (password.length() == 0) return 0;
        
        int score = 0;
        
        // Length check
        if (password.length() >= 8) score += 25;
        if (password.length() >= 12) score += 15;
        
        // Character diversity
        if (password.matches(".*[a-z].*")) score += 15;
        if (password.matches(".*[A-Z].*")) score += 15;
        if (password.matches(".*[0-9].*")) score += 15;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) score += 15;
        
        return Math.min(100, score);
    }
    
    private JPanel createPasswordRequirementsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel("Administrator Password Requirements:");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(TEXT_COLOR);
        
        String[] requirements = {
            "• At least 8 characters long (12+ recommended for admin)",
            "• Contains uppercase and lowercase letters",
            "• Contains at least one number",
            "• Contains at least one special character (!@#$%^&*)",
            "• Different from current password",
            "• Strong password required for administrative access"
        };
        
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        
        for (String req : requirements) {
            JLabel reqLabel = new JLabel(req);
            reqLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            reqLabel.setForeground(new Color(107, 114, 128));
            panel.add(reqLabel);
        }
        
        return panel;
    }

    private void refreshUserManagement() {
        // Find the user management tab and refresh its data
        if (tabbedPane.getSelectedIndex() == 2) { // User Management tab index
            JPanel userManagementPanel = (JPanel) tabbedPane.getComponentAt(2);
            if (userManagementPanel instanceof UserManagement) {
                ((UserManagement) userManagementPanel).refreshData();
            }
        }
    }
    
    private void updateAdminPersonalInfo(JTextField nameField, JTextField emailField, 
                                        JTextField phoneField, JTextArea addressArea) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressArea.getText().trim();
        
        // Validation
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill in all required fields!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid email address!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Update admin profile in file
            boolean success = updateAdminInFile(currentAdmin.getUserId(), name, email, phone, address);
            
            if (success) {
                // Update current admin object
                currentAdmin.setName(name);
                currentAdmin.setEmail(email);
                currentAdmin.setPhone(phone);
                
                JOptionPane.showMessageDialog(this, 
                    "✅ Profile updated successfully!\n\n" +
                    "Name: " + name + "\n" +
                    "Email: " + email + "\n" +
                    "Phone: " + phone, 
                    "Update Successful", JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh dashboard
                tabbedPane.setComponentAt(0, createDashboardPanel());
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error updating profile. Please try again.", 
                    "Update Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Unexpected error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void changeAdminPassword(JPasswordField currentField, JPasswordField newField, 
                                    JPasswordField confirmField) {
        String currentPassword = new String(currentField.getPassword());
        String newPassword = new String(newField.getPassword());
        String confirmPassword = new String(confirmField.getPassword());
        
        // Validation
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill in all password fields!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!currentPassword.equals(currentAdmin.getPassword())) {
            JOptionPane.showMessageDialog(this, 
                "Current password is incorrect!", 
                "Authentication Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, 
                "New password and confirmation do not match!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (newPassword.length() < 8) {
            JOptionPane.showMessageDialog(this, 
                "Administrator password must be at least 8 characters long!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (newPassword.equals(currentPassword)) {
            JOptionPane.showMessageDialog(this, 
                "New password must be different from current password!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check password strength for admin (higher requirement)
        if (calculatePasswordStrength(newPassword) < 60) {
            int choice = JOptionPane.showConfirmDialog(this,
                "Password strength is insufficient for administrator account.\n" +
                "Admin accounts require strong passwords for security.\n" +
                "Do you want to continue anyway?",
                "Weak Password Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        try {
            // Update password in file using DataManager
            boolean success = dataManager.changeUserPassword(currentAdmin.getUserId(), "ADMIN", 
                                                           currentPassword, newPassword);
            
            if (success) {
                // Update current admin object
                currentAdmin.setPassword(newPassword);
                
                // Clear fields
                currentField.setText("");
                newField.setText("");
                confirmField.setText("");
                
                JOptionPane.showMessageDialog(this, 
                    "✅ Administrator password changed successfully!\n\n" +
                    "Your password has been updated securely.\n" +
                    "Please remember your new password for future logins.", 
                    "Password Changed", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error updating password. Please try again.", 
                    "Update Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Unexpected error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean updateAdminInFile(String adminId, String name, String email, String phone, String address) {
        try {
            File file = new File("admin.txt");
            List<String> lines = new ArrayList<>();
            boolean found = false;
            
            // Read all lines
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        lines.add(line);
                    }
                }
            }
            
            // Update the admin's line
            for (int i = 0; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if (parts.length >= 1 && parts[0].equals(adminId)) {
                    // Reconstruct the line with updated information
                    // Format: adminId,username,password,name,email,phone
                    String updatedLine = adminId + "," + 
                                       (parts.length >= 2 ? parts[1] : "admin") + "," + 
                                       (parts.length >= 3 ? parts[2] : "admin123") + "," + 
                                       name + "," + email + "," + phone;
                    lines.set(i, updatedLine);
                    found = true;
                    break;
                }
            }
            
            // If admin not found, add new line
            if (!found) {
                String newLine = adminId + ",admin," + currentAdmin.getPassword() + "," + 
                               name + "," + email + "," + phone;
                lines.add(newLine);
            }
            
            // Write back to file
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (String line : lines) {
                    writer.println(line);
                }
            }
            
            return true;
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private String getAdminAddress(String adminId) {
        // Since admin.txt doesn't store address, return a default
        return "ATC Tuition Centre Administrative Office";
    }
    
    private String getCurrentLoginTime() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return now.format(formatter) + " (Current Session)";
    }
    
    // System stats helper class and methods
    private static class SystemStats {
        private int totalStudents;
        private int totalTutors;
        private int totalClasses;
        private int totalPayments;
        private double totalRevenue;
        private int pendingRequests;
        
        public SystemStats() {
            this.totalStudents = 0;
            this.totalTutors = 0;
            this.totalClasses = 0;
            this.totalPayments = 0;
            this.totalRevenue = 0.0;
            this.pendingRequests = 0;
        }
        
        // Getters
        public int getTotalStudents() { return totalStudents; }
        public int getTotalTutors() { return totalTutors; }
        public int getTotalClasses() { return totalClasses; }
        public int getTotalPayments() { return totalPayments; }
        public double getTotalRevenue() { return totalRevenue; }
        public int getPendingRequests() { return pendingRequests; }
        
        // Setters
        public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }
        public void setTotalTutors(int totalTutors) { this.totalTutors = totalTutors; }
        public void setTotalClasses(int totalClasses) { this.totalClasses = totalClasses; }
        public void setTotalPayments(int totalPayments) { this.totalPayments = totalPayments; }
        public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
        public void setPendingRequests(int pendingRequests) { this.pendingRequests = pendingRequests; }
    }
    
    private SystemStats getSystemStats() {
        SystemStats stats = new SystemStats();
        
        try {
            // Get students count
            List<Student> students = dataManager.getAllStudents();
            stats.setTotalStudents(students != null ? students.size() : 0);
            
            // Get tutors count
            List<Tutor> tutors = dataManager.getAllTutors();
            stats.setTotalTutors(tutors != null ? tutors.size() : 0);
            
            // Get classes count
            List<ClassInfo> classes = dataManager.getAllClasses();
            stats.setTotalClasses(classes != null ? classes.size() : 0);
            
            // Calculate total revenue and payments count
            double totalRevenue = 0.0;
            int totalPayments = 0;
            
            if (students != null) {
                for (Student student : students) {
                    List<String> studentSubjects = student.getSubjects();
                    if (studentSubjects != null && !studentSubjects.isEmpty()) {
                        totalRevenue += dataManager.calculateTotalFee(studentSubjects);
                    }
                }
            }
            
            // Count payments from payment history file
            totalPayments = countPaymentsFromFile();
            
            stats.setTotalRevenue(totalRevenue);
            stats.setTotalPayments(totalPayments);
            

            
        } catch (Exception e) {
            System.err.println("Error getting system stats: " + e.getMessage());
            e.printStackTrace();
            // Return default stats if error occurs
        }
        
        return stats;
    }
    
    private int countPaymentsFromFile() {
        int count = 0;
        try {
            java.io.File file = new java.io.File("payment_history.txt");
            if (file.exists()) {
                try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file))) {
                    while (reader.readLine() != null) {
                        count++;
                    }
                }
            }
        } catch (java.io.IOException e) {
            System.err.println("Error reading payment history file: " + e.getMessage());
            // File doesn't exist or can't be read, return 0
        }
        return count;
    }
    
    private int countPendingRequests() {
        int count = 0;
        try {
            java.io.File file = new java.io.File("Subject_Change_Requests.txt");
            if (file.exists()) {
                try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().isEmpty()) continue;
                        String[] parts = line.split(",");
                        if (parts.length >= 5 && "Pending".equalsIgnoreCase(parts[4].trim())) {
                            count++;
                        }
                    }
                }
            }
        } catch (java.io.IOException e) {
            System.err.println("Error reading subject change requests file: " + e.getMessage());
            // File doesn't exist or can't be read, return 0
        }
        return count;
    }
    
    // Keep existing methods for other management panels (simplified versions)
    private JPanel createSystemStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("System Statistics");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        
        JTextArea statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Courier New", Font.PLAIN, 13));
        statsArea.setBackground(Color.WHITE);
        statsArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        updateSystemStats(statsArea);
        
        JScrollPane scrollPane = new JScrollPane(statsArea);
        scrollPane.setBorder(null);
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonsPanel.setBackground(BACKGROUND_COLOR);
        
        JButton refreshButton = createStyledButton("Refresh Statistics", PRIMARY_COLOR);
        refreshButton.addActionListener(e -> updateSystemStats(statsArea));
        buttonsPanel.add(refreshButton);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void updateSystemStats(JTextArea statsArea) {
        SystemStats stats = getSystemStats();
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== ATC TUITION CENTRE SYSTEM STATISTICS ===\n\n");
        
        sb.append("USER STATISTICS:\n");
        sb.append("Total Students: ").append(stats.getTotalStudents()).append("\n");
        sb.append("Total Tutors: ").append(stats.getTotalTutors()).append("\n");
        sb.append("Total Receptionists: 3\n");
        sb.append("Total Admins: 2\n");
        sb.append("Total Users: ").append(stats.getTotalStudents() + stats.getTotalTutors() + 5).append("\n\n");
        
        sb.append("ACADEMIC STATISTICS:\n");
        sb.append("Total Classes: ").append(stats.getTotalClasses()).append("\n");
        
        sb.append("\nFINANCIAL STATISTICS:\n");
        sb.append("Total Payments: ").append(stats.getTotalPayments()).append("\n");
        sb.append("Total Revenue: RM").append(String.format("%.2f", stats.getTotalRevenue())).append("\n");
        
        
        statsArea.setText(sb.toString());
    }
    
    // Helper methods for UI components
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(30);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        return field;
    }
    
    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(30);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setEchoChar('•');
        return field;
    }
    
    private JTextArea createStyledTextArea(int rows, int cols) {
        JTextArea area = new JTextArea(rows, cols);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }
    
    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_COLOR);
        label.setPreferredSize(new Dimension(160, 25));
        return label;
    }
    
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(backgroundColor);
        button.setForeground(backgroundColor.equals(SECONDARY_COLOR) ? TEXT_COLOR : Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private JPanel createStatCard(String icon, String title, String value) {
        return createStatCard(icon, title, value, PRIMARY_COLOR);
    }
    
    private JPanel createStatCard(String icon, String title, String value, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(107, 114, 128));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(accentColor);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(iconLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(valueLabel);
        
        return card;
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel logoLabel = new JLabel("🎓 ATC Tuition Centre - Administrator Portal");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logoLabel.setForeground(Color.WHITE);
        
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setBackground(PRIMARY_COLOR);
        
        JLabel userLabel = new JLabel("Welcome, " + currentAdmin.getName());
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);
        
        // Enhanced logout button with confirmation
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoutButton.setBackground(Color.WHITE);
        logoutButton.setForeground(PRIMARY_COLOR);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?\n" +
                "Any unsaved changes will be lost.",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Log the logout action
                try {
                    String timestamp = java.time.LocalDateTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    );
                    try (java.io.FileWriter fw = new java.io.FileWriter("admin_activity.log", true)) {
                        fw.write(currentAdmin.getUserId() + " logged out at " + timestamp + "\n");
                    }
                } catch (java.io.IOException ex) {
                    System.err.println("Error logging admin activity: " + ex.getMessage());
                }
                
                dispose();
                new LoginGUI().setVisible(true);
            }
        });
        
        userPanel.add(userLabel);
        userPanel.add(logoutButton);
        
        headerPanel.add(logoLabel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private void setupWindow() {
        setTitle("ATC Tuition Centre - Administrator Dashboard - " + currentAdmin.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
}