import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * TutorDashboard - Enhanced with improved profile management and password change
 */
public class TutorDashboard extends JFrame {
    private User currentUser;
    private DataManager dataManager;
    private JTabbedPane tabbedPane;
    
    // Modern color scheme
    private final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private final Color SECONDARY_COLOR = new Color(243, 244, 246);
    private final Color ACCENT_COLOR = new Color(16, 185, 129);
    private final Color ERROR_COLOR = new Color(239, 68, 68);
    private final Color TEXT_COLOR = new Color(31, 41, 55);
    private final Color BACKGROUND_COLOR = new Color(249, 250, 251);
    
    public TutorDashboard(User user) {
        this.currentUser = user;
        this.dataManager = new DataManager();
        
        initializeComponents();
        setupLayout();
        setupWindow();
    }
    
    private void initializeComponents() {
        // Create tabbed pane with enhanced styling
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setTabPlacement(JTabbedPane.TOP);
        
        // Add tabs with icons
        tabbedPane.addTab("🏠 Dashboard", createDashboardPanel());
        tabbedPane.addTab("📚 My Classes", createClassManagementPanel());
        tabbedPane.addTab("👥 Students", createStudentViewPanel());
        tabbedPane.addTab("📅 Teaching Schedule", createTeachingSchedulePanel());
        tabbedPane.addTab("👤 Profile", createEnhancedProfilePanel());
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Welcome section
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setBackground(Color.WHITE);
        welcomePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(30, 30, 30, 30)
        ));
        
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getName() + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(TEXT_COLOR);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel roleLabel = new JLabel("Tutor Dashboard - ATC Tuition Centre");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        roleLabel.setForeground(new Color(107, 114, 128));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel timeLabel = new JLabel("Today: " + java.time.LocalDate.now().toString());
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        timeLabel.setForeground(new Color(107, 114, 128));
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        welcomePanel.add(welcomeLabel);
        welcomePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        welcomePanel.add(roleLabel);
        welcomePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        welcomePanel.add(timeLabel);
        
        // Quick stats
        JPanel statsPanel = createStatsPanel();
        
        // Quick actions
        JPanel actionsPanel = createQuickActionsPanel();
        
        // Layout
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(BACKGROUND_COLOR);
        topSection.add(welcomePanel, BorderLayout.CENTER);
        topSection.add(Box.createRigidArea(new Dimension(0, 20)), BorderLayout.SOUTH);
        
        panel.add(topSection, BorderLayout.NORTH);
        panel.add(statsPanel, BorderLayout.CENTER);
        panel.add(actionsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        // Get tutor statistics
        int totalClasses = getTutorClassCount();
        int totalStudents = getTutorStudentCount();
        String tutorLevel = getTutorLevel();
        
        // Calculate additional stats
        int activeClasses = getActiveClassCount();
        String joinDate = "2024"; // You can make this dynamic if you have the data
        
        panel.add(createStatCard("👨‍🏫", "Tutor ID", currentUser.getUserId(), PRIMARY_COLOR));
        panel.add(createStatCard("📚", "My Classes", String.valueOf(totalClasses), ACCENT_COLOR));
        panel.add(createStatCard("👥", "Total Students", String.valueOf(totalStudents), new Color(245, 158, 11)));
        panel.add(createStatCard("🎯", "Active Classes", String.valueOf(activeClasses), new Color(168, 85, 247)));
        panel.add(createStatCard("📊", "Teaching Level", tutorLevel, new Color(236, 72, 153)));
        panel.add(createStatCard("📅", "Since", joinDate, new Color(99, 102, 241)));
        
        return panel;
    }
    
    private JPanel createStatCard(String icon, String title, String value, Color color) {
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
        iconLabel.setForeground(color);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(107, 114, 128));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(TEXT_COLOR);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(iconLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(valueLabel);
        
        return card;
    }
    
    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            "Quick Actions", 0, 0,
            new Font("Segoe UI", Font.BOLD, 16), TEXT_COLOR));
        
        JButton addClassBtn = createActionButton("Add New Class", "➕", e -> showAddClassDialog());
        JButton viewClassesBtn = createActionButton("Manage Classes", "📚", e -> tabbedPane.setSelectedIndex(1));
        JButton viewStudentsBtn = createActionButton("View Students", "👥", e -> tabbedPane.setSelectedIndex(2));
        JButton scheduleBtn = createActionButton("View Schedule", "📅", e -> tabbedPane.setSelectedIndex(3));
        
        panel.add(addClassBtn);
        panel.add(viewClassesBtn);
        panel.add(viewStudentsBtn);
        panel.add(scheduleBtn);
        
        return panel;
    }
    
    private JButton createActionButton(String text, String icon, ActionListener action) {
        JButton button = new JButton("<html><center>" + icon + "<br>" + text + "</center></html>");
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setBackground(Color.WHITE);
        button.setForeground(TEXT_COLOR);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            new EmptyBorder(15, 20, 15, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 80));
        button.addActionListener(action);
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(SECONDARY_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
            }
        });
        
        return button;
    }
    
    // Enhanced Profile Panel with better password management
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
    
    // Account Settings tab
    profileTabs.addTab("⚙️ Account Settings", createScrollableAccountSettingsPanel());
    
    panel.add(profileTabs, BorderLayout.CENTER);
    return panel;
}

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
    JLabel titleLabel = new JLabel("Personal Information");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
    titleLabel.setForeground(PRIMARY_COLOR);
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    // Profile info panel
    JPanel profileInfoPanel = createProfileInfoFormPanel();
    
    contentPanel.add(titleLabel);
    contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
    contentPanel.add(profileInfoPanel);
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
    JLabel titleLabel = new JLabel("Password & Security");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
    titleLabel.setForeground(PRIMARY_COLOR);
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    // Change password panel
    JPanel passwordPanel = createPasswordChangeFormPanel();
    
    // Security info panel with enhanced information
    JPanel securityInfoPanel = createEnhancedSecurityInfoPanelTutor();
    
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

private JPanel createScrollableAccountSettingsPanel() {
    // Main scrollable container
    JPanel mainContainer = new JPanel(new BorderLayout());
    mainContainer.setBackground(BACKGROUND_COLOR);
    
    // Content panel that will be scrolled
    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    contentPanel.setBackground(Color.WHITE);
    contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
    
    // Title
    JLabel titleLabel = new JLabel("Account Settings");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
    titleLabel.setForeground(PRIMARY_COLOR);
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    // Settings options
    JPanel settingsPanel = createAccountSettingsFormPanel();
    
    contentPanel.add(titleLabel);
    contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
    contentPanel.add(settingsPanel);
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

// Enhanced Security Info Panel for Tutors
    private JPanel createEnhancedSecurityInfoPanelTutor() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBackground(Color.WHITE);
    panel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1), 
            "Security Information", 0, 0, new Font("Segoe UI", Font.BOLD, 16), TEXT_COLOR),
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
    JLabel statusLabel = new JLabel("Active Tutor");
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
    
    // Last Recent Online
    gbc.gridx = 0; gbc.gridy = 2;
    panel.add(createFieldLabel("Last Recent Online:"), gbc);
    gbc.gridx = 1;
    JLabel lastOnlineLabel = new JLabel(getLastOnlineTime());
    lastOnlineLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    lastOnlineLabel.setForeground(new Color(107, 114, 128));
    panel.add(lastOnlineLabel, gbc);
    
    // Account Type
    gbc.gridx = 0; gbc.gridy = 3;
    panel.add(createFieldLabel("Account Type:"), gbc);
    gbc.gridx = 1;
    JLabel typeLabel = new JLabel("Tutor Portal");
    typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    typeLabel.setForeground(new Color(107, 114, 128));
    panel.add(typeLabel, gbc);
    
    // Teaching Status
    gbc.gridx = 0; gbc.gridy = 4;
    panel.add(createFieldLabel("Teaching Status:"), gbc);
    gbc.gridx = 1;
    JLabel teachingLabel = new JLabel();
    teachingLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
    int classCount = getTutorClassCount();
    if (classCount >= 3) {
        teachingLabel.setForeground(ACCENT_COLOR);
        teachingLabel.setText("Active Teacher (" + classCount + " classes)");
    } else if (classCount >= 1) {
        teachingLabel.setForeground(new Color(255, 193, 7));
        teachingLabel.setText("Part-time Teacher (" + classCount + " classes)");
    } else {
        teachingLabel.setForeground(ERROR_COLOR);
        teachingLabel.setText("No Active Classes");
    }
    panel.add(teachingLabel, gbc);
    
    // Two-Factor Auth
    gbc.gridx = 0; gbc.gridy = 5;
    panel.add(createFieldLabel("Two-Factor Auth:"), gbc);
    gbc.gridx = 1;
    JLabel twoFALabel = new JLabel("Not Enabled");
    twoFALabel.setForeground(new Color(107, 114, 128));
    twoFALabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    panel.add(twoFALabel, gbc);
    
    // Data Privacy
    gbc.gridx = 0; gbc.gridy = 6;
    panel.add(createFieldLabel("Data Privacy:"), gbc);
    gbc.gridx = 1;
    JLabel privacyLabel = new JLabel("Protected by ATC Security");
    privacyLabel.setForeground(new Color(107, 114, 128));
    privacyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    panel.add(privacyLabel, gbc);
    
    return panel;
    }
    
    private JPanel createPersonalInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(30, 30, 30, 30)
        ));
        
        // Title
        JLabel titleLabel = new JLabel("Personal Information");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Profile info panel
        JPanel profileInfoPanel = createProfileInfoFormPanel();
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        contentPanel.add(profileInfoPanel);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createProfileInfoFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1), 
                "Edit Profile Information", 0, 0, new Font("Segoe UI", Font.BOLD, 16), TEXT_COLOR),
            new EmptyBorder(25, 25, 25, 25)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Create form fields
        JTextField userIdField = createStyledTextField();
        userIdField.setText(currentUser.getUserId());
        userIdField.setEditable(false);
        userIdField.setBackground(SECONDARY_COLOR);
        
        JTextField usernameField = createStyledTextField();
        usernameField.setText(currentUser.getUsername());
        usernameField.setEditable(false);
        usernameField.setBackground(SECONDARY_COLOR);
        
        JTextField nameField = createStyledTextField();
        nameField.setText(currentUser.getName());
        
        JTextField emailField = createStyledTextField();
        emailField.setText(currentUser.getEmail());
        
        JTextField phoneField = createStyledTextField();
        phoneField.setText(currentUser.getPhone());
        
        JTextField dobField = createStyledTextField();
        if (currentUser instanceof Tutor) {
            dobField.setText(((Tutor) currentUser).getDateOfBirth());
        }
        
        // Layout form fields
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(createFieldLabel("Tutor ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(userIdField, gbc);
        
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
        panel.add(createFieldLabel("Date of Birth:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(dobField, gbc);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonsPanel.setBackground(Color.WHITE);
        
        JButton updateButton = createStyledButton("Update Profile", ACCENT_COLOR);
        JButton resetButton = createStyledButton("Reset", SECONDARY_COLOR);
        
        updateButton.addActionListener(e -> updatePersonalInfo(nameField, emailField, phoneField, dobField));
        resetButton.addActionListener(e -> {
            nameField.setText(currentUser.getName());
            emailField.setText(currentUser.getEmail());
            phoneField.setText(currentUser.getPhone());
            if (currentUser instanceof Tutor) {
                dobField.setText(((Tutor) currentUser).getDateOfBirth());
            }
        });
        
        buttonsPanel.add(updateButton);
        buttonsPanel.add(resetButton);
        
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 15, 15, 15);
        panel.add(buttonsPanel, gbc);
        
        return panel;
    }
    
    private JPanel createPasswordSecurityPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(30, 30, 30, 30)
        ));
        
        // Title
        JLabel titleLabel = new JLabel("Password & Security");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Change password panel
        JPanel passwordPanel = createPasswordChangeFormPanel();
        
        // Security info panel
        JPanel securityInfoPanel = createSecurityInfoPanel();
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        contentPanel.add(passwordPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        contentPanel.add(securityInfoPanel);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createPasswordChangeFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1), 
                "Change Password", 0, 0, new Font("Segoe UI", Font.BOLD, 16), TEXT_COLOR),
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
        JCheckBox showCurrentPassword = new JCheckBox("Show password");
        JCheckBox showNewPassword = new JCheckBox("Show password");
        JCheckBox showConfirmPassword = new JCheckBox("Show password");
        
        setupPasswordToggle(showCurrentPassword, currentPasswordField);
        setupPasswordToggle(showNewPassword, newPasswordField);
        setupPasswordToggle(showConfirmPassword, confirmPasswordField);
        
        // Password strength indicator
        JLabel strengthLabel = new JLabel("Password strength: ");
        strengthLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JProgressBar strengthBar = new JProgressBar(0, 100);
        strengthBar.setStringPainted(true);
        strengthBar.setString("Enter password");
        
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
            changePassword(currentPasswordField, newPasswordField, confirmPasswordField));
        
        clearButton.addActionListener(e -> {
            currentPasswordField.setText("");
            newPasswordField.setText("");
            confirmPasswordField.setText("");
            strengthBar.setValue(0);
            strengthBar.setString("Enter password");
        });
        
        buttonsPanel.add(changePasswordButton);
        buttonsPanel.add(clearButton);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 3;
        gbc.insets = new Insets(25, 15, 15, 15);
        panel.add(buttonsPanel, gbc);
        
        return panel;
    }
    
    private JPanel createPasswordRequirementsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel("Password Requirements:");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(TEXT_COLOR);
        
        String[] requirements = {
            "• At least 8 characters long",
            "• Contains uppercase and lowercase letters",
            "• Contains at least one number",
            "• Contains at least one special character",
            "• Different from current password"
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
    
    private JPanel createSecurityInfoPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBackground(Color.WHITE);
    panel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1), 
            "Security Information", 0, 0, new Font("Segoe UI", Font.BOLD, 16), TEXT_COLOR),
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
    JLabel statusLabel = new JLabel("Active Tutor");
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
    
    // Last Recent Online
    gbc.gridx = 0; gbc.gridy = 2;
    panel.add(createFieldLabel("Last Recent Online:"), gbc);
    gbc.gridx = 1;
    JLabel lastOnlineLabel = new JLabel(getLastOnlineTime());
    lastOnlineLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    lastOnlineLabel.setForeground(new Color(107, 114, 128));
    panel.add(lastOnlineLabel, gbc);
    
    // Account Type
    gbc.gridx = 0; gbc.gridy = 3;
    panel.add(createFieldLabel("Account Type:"), gbc);
    gbc.gridx = 1;
    JLabel typeLabel = new JLabel("Tutor ");
    typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    typeLabel.setForeground(new Color(107, 114, 128));
    panel.add(typeLabel, gbc);
    
    // Data Privacy
    gbc.gridx = 0; gbc.gridy = 6;
    panel.add(createFieldLabel("Data Privacy:"), gbc);
    gbc.gridx = 1;
    JLabel privacyLabel = new JLabel("Protected by ATC Security");
    privacyLabel.setForeground(new Color(107, 114, 128));
    privacyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    panel.add(privacyLabel, gbc);
    
    return panel;
}
    
    private JPanel createAccountSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(30, 30, 30, 30)
        ));
        
        // Title
        JLabel titleLabel = new JLabel("Account Settings");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Settings options
        JPanel settingsPanel = createAccountSettingsFormPanel();
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        contentPanel.add(settingsPanel);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createAccountSettingsFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        
        // Notification preferences
        JPanel notificationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        notificationPanel.setBackground(Color.WHITE);
        notificationPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            "Notification Preferences", 0, 0,
            new Font("Segoe UI", Font.BOLD, 14), TEXT_COLOR));
        
        JCheckBox emailNotifications = new JCheckBox("Email notifications");
        JCheckBox smsNotifications = new JCheckBox("SMS notifications");
        JCheckBox systemAlerts = new JCheckBox("System alerts");
        
        emailNotifications.setSelected(true);
        systemAlerts.setSelected(true);
        
        notificationPanel.add(emailNotifications);
        notificationPanel.add(smsNotifications);
        notificationPanel.add(systemAlerts);
        
        // Privacy settings
        JPanel privacyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        privacyPanel.setBackground(Color.WHITE);
        privacyPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            "Privacy Settings", 0, 0,
            new Font("Segoe UI", Font.BOLD, 14), TEXT_COLOR));
        
        JCheckBox profileVisible = new JCheckBox("Profile visible to students");
        JCheckBox contactVisible = new JCheckBox("Contact info visible");
        
        profileVisible.setSelected(true);
        contactVisible.setSelected(true);
        
        privacyPanel.add(profileVisible);
        privacyPanel.add(contactVisible);
        
        // Account actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        actionsPanel.setBackground(Color.WHITE);
        actionsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            "Account Actions", 0, 0,
            new Font("Segoe UI", Font.BOLD, 14), TEXT_COLOR));
        
        JButton exportDataButton = createStyledButton("Export My Data", SECONDARY_COLOR);
        JButton requestSupportButton = createStyledButton("Request Support", PRIMARY_COLOR);
        
        exportDataButton.addActionListener(e -> exportTutorData());
        requestSupportButton.addActionListener(e -> requestSupport());
        
        actionsPanel.add(exportDataButton);
        actionsPanel.add(requestSupportButton);
        
        panel.add(notificationPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(privacyPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(actionsPanel);
        
        return panel;
    }
    
    // Helper methods for enhanced profile functionality
    private void setupPasswordToggle(JCheckBox toggle, JPasswordField passwordField) {
        toggle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        toggle.setBackground(Color.WHITE);
        toggle.addActionListener(e -> {
            if (toggle.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
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
    
    private void updatePersonalInfo(JTextField nameField, JTextField emailField, 
                                   JTextField phoneField, JTextField dobField) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String dob = dobField.getText().trim();
        
        // Validation
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
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
        
        // Update user object
        currentUser.setName(name);
        currentUser.setEmail(email);
        currentUser.setPhone(phone);
        
        if (currentUser instanceof Tutor) {
            ((Tutor) currentUser).setDateOfBirth(dob);
        }
        
        // Save to file (you might want to implement this in DataManager)
        updateTutorInFile();
        
        JOptionPane.showMessageDialog(this, 
            "✅ Profile updated successfully!\n\n" +
            "Name: " + name + "\n" +
            "Email: " + email + "\n" +
            "Phone: " + phone, 
            "Update Successful", JOptionPane.INFORMATION_MESSAGE);
        
        refreshData();
    }
    
    private void changePassword(JPasswordField currentField, JPasswordField newField, 
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
        
        if (!currentPassword.equals(currentUser.getPassword())) {
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
                "Password must be at least 8 characters long!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (newPassword.equals(currentPassword)) {
            JOptionPane.showMessageDialog(this, 
                "New password must be different from current password!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check password strength
        if (calculatePasswordStrength(newPassword) < 50) {
            int choice = JOptionPane.showConfirmDialog(this,
                "Password strength is low. Do you want to continue?",
                "Weak Password", JOptionPane.YES_NO_OPTION);
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        // Update password
        currentUser.setPassword(newPassword);
        updateTutorInFile();
        
        // Clear fields
        currentField.setText("");
        newField.setText("");
        confirmField.setText("");
        
        JOptionPane.showMessageDialog(this, 
            "✅ Password changed successfully!\n\n" +
            "Your password has been updated securely.\n" +
            "Please remember your new password.", 
            "Password Changed", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void updateTutorInFile() {
        try {
            File file = new File("tutor.txt");
            List<String> lines = new ArrayList<>();
            
            // Read all lines
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }
            
            // Update the tutor's line
            for (int i = 0; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if (parts.length >= 1 && parts[0].equals(currentUser.getUserId())) {
                    String updatedLine = String.join(",",
                        currentUser.getUserId(),
                        currentUser.getUsername(),
                        currentUser.getPassword(),
                        currentUser.getEmail(),
                        currentUser instanceof Tutor ? ((Tutor) currentUser).getDateOfBirth() : "",
                        currentUser.getPhone()
                    );
                    lines.set(i, updatedLine);
                    break;
                }
            }
            
            // Write back to file
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (String line : lines) {
                    writer.println(line);
                }
            }
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error saving profile changes: " + e.getMessage(), 
                "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportTutorData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Tutor Data");
        fileChooser.setSelectedFile(new java.io.File("tutor_data_" + currentUser.getUserId() + ".txt"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fileChooser.getSelectedFile();
                
                try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                    writer.println("=== TUTOR DATA EXPORT ===");
                    writer.println("Export Date: " + java.time.LocalDateTime.now());
                    writer.println();
                    writer.println("PERSONAL INFORMATION:");
                    writer.println("Tutor ID: " + currentUser.getUserId());
                    writer.println("Name: " + currentUser.getName());
                    writer.println("Email: " + currentUser.getEmail());
                    writer.println("Phone: " + currentUser.getPhone());
                    if (currentUser instanceof Tutor) {
                        writer.println("Date of Birth: " + ((Tutor) currentUser).getDateOfBirth());
                    }
                    writer.println();
                    
                    writer.println("STATISTICS:");
                    writer.println("Total Classes: " + getTutorClassCount());
                    writer.println("Total Students: " + getTutorStudentCount());
                    writer.println();
                    
                    writer.println("CLASSES:");
                    // Export class details
                    try (BufferedReader reader = new BufferedReader(new FileReader("class.txt"))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            String[] parts = line.split(",");
                            if (parts.length >= 6 && parts[1].equals(currentUser.getUserId())) {
                                writer.println("- " + parts[0] + ": " + parts[2] + " (RM" + parts[5] + ")");
                            }
                        }
                    }
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Data exported successfully to:\n" + file.getAbsolutePath(),
                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error exporting data: " + e.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void requestSupport() {
        JTextArea requestArea = new JTextArea(8, 40);
        requestArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        requestArea.setLineWrap(true);
        requestArea.setWrapStyleWord(true);
        requestArea.setText("Please describe your issue or request...");
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Support Request:"), BorderLayout.NORTH);
        panel.add(new JScrollPane(requestArea), BorderLayout.CENTER);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Request Support", JOptionPane.OK_CANCEL_OPTION);
            
        if (result == JOptionPane.OK_OPTION) {
            String request = requestArea.getText().trim();
            if (!request.isEmpty() && !request.equals("Please describe your issue or request...")) {
                // Save support request to file
                try (FileWriter fw = new FileWriter("support_requests.txt", true)) {
                    fw.write(java.time.LocalDateTime.now() + "," + 
                            currentUser.getUserId() + "," + 
                            currentUser.getName() + "," + 
                            request.replace(",", ";") + "\n");
                    
                    JOptionPane.showMessageDialog(this,
                        "Support request submitted successfully!\n" +
                        "We will get back to you within 24 hours.",
                        "Request Submitted", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this,
                        "Error submitting request: " + e.getMessage(),
                        "Submission Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private JPanel createClassManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel("Class Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(BACKGROUND_COLOR);
        
        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        JButton addButton = createStyledButton("Add Class", ACCENT_COLOR);
        JButton refreshButton = createStyledButton("Refresh", SECONDARY_COLOR);
        JButton exportButton = createStyledButton("Export", PRIMARY_COLOR);
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(addButton);
        searchPanel.add(refreshButton);
        searchPanel.add(exportButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        // Classes table
        String[] columnNames = {"Class ID", "Course Name", "Description", "Schedule", "Fee (RM)", "Students"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable classTable = new JTable(tableModel);
        classTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        classTable.setRowHeight(30);
        classTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        classTable.getTableHeader().setBackground(SECONDARY_COLOR);
        classTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Alternate row colors
        classTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(classTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
        
        // Action buttons
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionsPanel.setBackground(BACKGROUND_COLOR);
        
        JButton viewButton = createStyledButton("View Details", PRIMARY_COLOR);
        JButton editButton = createStyledButton("Edit Class", new Color(255, 193, 7));
        JButton deleteButton = createStyledButton("Delete Class", ERROR_COLOR);
        
        actionsPanel.add(viewButton);
        actionsPanel.add(editButton);
        actionsPanel.add(deleteButton);
        
        // Load tutor's classes
        loadTutorClasses(tableModel);
        
        // Event listeners
        addButton.addActionListener(e -> showAddClassDialog());
        refreshButton.addActionListener(e -> loadTutorClasses(tableModel));
        exportButton.addActionListener(e -> exportClassData());
        
        viewButton.addActionListener(e -> {
            int selectedRow = classTable.getSelectedRow();
            if (selectedRow >= 0) {
                String classId = (String) tableModel.getValueAt(selectedRow, 0);
                showClassDetails(classId);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a class to view.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        editButton.addActionListener(e -> {
            int selectedRow = classTable.getSelectedRow();
            if (selectedRow >= 0) {
                String classId = (String) tableModel.getValueAt(selectedRow, 0);
                showEditClassDialog(classId);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a class to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        deleteButton.addActionListener(e -> {
            int selectedRow = classTable.getSelectedRow();
            if (selectedRow >= 0) {
                String classId = (String) tableModel.getValueAt(selectedRow, 0);
                String className = (String) tableModel.getValueAt(selectedRow, 1);
                deleteClass(classId, className, tableModel);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a class to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        // Search functionality
        searchField.addActionListener(e -> {
            String searchTerm = searchField.getText().trim();
            searchClasses(searchTerm, tableModel);
        });
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(actionsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createStudentViewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel("My Students");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        
        // Class selector
        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectorPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel classLabel = new JLabel("Select Class:");
        classLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JComboBox<String> classSelector = new JComboBox<>();
        classSelector.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        classSelector.setPreferredSize(new Dimension(400, 30));
        
        JButton exportStudentsButton = createStyledButton("Export Student List", ACCENT_COLOR);
        
        selectorPanel.add(classLabel);
        selectorPanel.add(Box.createHorizontalStrut(10));
        selectorPanel.add(classSelector);
        selectorPanel.add(Box.createHorizontalStrut(20));
        selectorPanel.add(exportStudentsButton);
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(selectorPanel, BorderLayout.SOUTH);
        
        // Students table
        String[] columnNames = {"No.", "Student ID", "Name", "Email", "Phone", "Level", "Enrollment Month"};
        DefaultTableModel studentTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable studentTable = new JTable(studentTableModel);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        studentTable.setRowHeight(25);
        studentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        studentTable.getTableHeader().setBackground(SECONDARY_COLOR);
        
        // Alternate row colors
        studentTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
        
        // Load tutor's classes in selector
        loadClassSelector(classSelector);
        
        // Load students for selected class
        classSelector.addActionListener(e -> loadStudentsForClass(classSelector, studentTableModel));
        
        // Export functionality
        exportStudentsButton.addActionListener(e -> exportStudentList(classSelector, studentTableModel));
        
        // Load initial students if classes exist
        if (classSelector.getItemCount() > 0) {
            loadStudentsForClass(classSelector, studentTableModel);
        }
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTeachingSchedulePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel("Teaching Schedule");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setBackground(BACKGROUND_COLOR);
        
        JComboBox<String> viewSelector = new JComboBox<>(new String[]{"This Week", "This Month", "All Classes"});
        viewSelector.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        viewSelector.setBackground(Color.WHITE);
        
        JButton refreshButton = createStyledButton("Refresh", SECONDARY_COLOR);
        JButton exportButton = createStyledButton("Export Schedule", PRIMARY_COLOR);
        
        filterPanel.add(new JLabel("View:"));
        filterPanel.add(viewSelector);
        filterPanel.add(refreshButton);
        filterPanel.add(exportButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(filterPanel, BorderLayout.EAST);
        
        // Main content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);
        
        // Quick stats
        JPanel statsPanel = createScheduleStatsPanel();
        contentPanel.add(statsPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Schedule content
        JPanel scheduleContent = createScheduleContentPanel();
        
        JScrollPane scrollPane = new JScrollPane(scheduleContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Event listeners
        refreshButton.addActionListener(e -> refreshSchedule(scheduleContent));
        exportButton.addActionListener(e -> exportTeachingSchedule());
        viewSelector.addActionListener(e -> refreshSchedule(scheduleContent));
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createScheduleStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Calculate schedule stats
        int totalClasses = getTutorClassCount();
        int classesWithSchedule = getClassesWithScheduleCount();
        int totalSessions = getTotalScheduledSessions();
        String nextClass = getNextScheduledClass();
        
        panel.add(createMiniStatCard("📚", "Total Classes", String.valueOf(totalClasses)));
        panel.add(createMiniStatCard("📅", "Scheduled", String.valueOf(classesWithSchedule)));
        panel.add(createMiniStatCard("🕐", "Total Sessions", String.valueOf(totalSessions)));
        panel.add(createMiniStatCard("⏰", "Next Class", nextClass));
        
        return panel;
    }
    
    private JPanel createMiniStatCard(String icon, String title, String value) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        titleLabel.setForeground(new Color(107, 114, 128));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valueLabel.setForeground(TEXT_COLOR);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(iconLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 3)));
        card.add(valueLabel);
        
        return card;
    }
    
    private JPanel createScheduleContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        
        // Load tutor's classes with schedules
        loadTeachingSchedule(panel);
        
        return panel;
    }
    
    private void loadTeachingSchedule(JPanel schedulePanel) {
        schedulePanel.removeAll();
        
        try (BufferedReader reader = new BufferedReader(new FileReader("class.txt"))) {
            String line;
            boolean hasClasses = false;
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6 && parts[1].equals(currentUser.getUserId())) {
                    hasClasses = true;
                    JPanel classScheduleCard = createClassScheduleCard(parts);
                    schedulePanel.add(classScheduleCard);
                    schedulePanel.add(Box.createRigidArea(new Dimension(0, 15)));
                }
            }
            
            if (!hasClasses) {
                JPanel emptyPanel = createEmptySchedulePanel();
                schedulePanel.add(emptyPanel);
            }
            
        } catch (IOException e) {
            JLabel errorLabel = new JLabel("Error loading schedule: " + e.getMessage());
            errorLabel.setForeground(ERROR_COLOR);
            errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            schedulePanel.add(errorLabel);
        }
        
        schedulePanel.revalidate();
        schedulePanel.repaint();
    }
    
    private JPanel createClassScheduleCard(String[] classData) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Class info
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Color.WHITE);
        
        JLabel classTitle = new JLabel(classData[2]); // Course name
        classTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        classTitle.setForeground(PRIMARY_COLOR);
        
        JLabel classId = new JLabel("Class ID: " + classData[0]);
        classId.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        classId.setForeground(new Color(107, 114, 128));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(classTitle);
        titlePanel.add(classId);
        
        infoPanel.add(titlePanel, BorderLayout.WEST);
        
        // Student count
        int studentCount = getStudentCountForClass(classData[0]);
        JLabel studentsLabel = new JLabel(studentCount + " students");
        studentsLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        studentsLabel.setForeground(ACCENT_COLOR);
        infoPanel.add(studentsLabel, BorderLayout.EAST);
        
        // Schedule details
        JPanel schedulePanel = new JPanel();
        schedulePanel.setBackground(Color.WHITE);
        schedulePanel.setLayout(new BoxLayout(schedulePanel, BoxLayout.Y_AXIS));
        
        String schedule = classData[4]; // Schedule data
        if (schedule != null && !schedule.trim().isEmpty()) {
            String[] scheduleDates = schedule.split(";");
            
            JLabel scheduleTitle = new JLabel("📅 Scheduled Sessions:");
            scheduleTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
            scheduleTitle.setForeground(TEXT_COLOR);
            schedulePanel.add(scheduleTitle);
            schedulePanel.add(Box.createRigidArea(new Dimension(0, 8)));
            
            for (String date : scheduleDates) {
                if (date != null && !date.trim().isEmpty()) {
                    JLabel dateLabel = new JLabel("• " + date.trim());
                    dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    dateLabel.setForeground(new Color(107, 114, 128));
                    schedulePanel.add(dateLabel);
                }
            }
        } else {
            JLabel noSchedule = new JLabel("📅 No schedule set");
            noSchedule.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            noSchedule.setForeground(new Color(107, 114, 128));
            schedulePanel.add(noSchedule);
        }
        
        // Fee info
        JLabel feeLabel = new JLabel("💰 RM " + classData[5] + " per session");
        feeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        feeLabel.setForeground(new Color(107, 114, 128));
        schedulePanel.add(Box.createRigidArea(new Dimension(0, 8)));
        schedulePanel.add(feeLabel);
        
        card.add(infoPanel, BorderLayout.NORTH);
        card.add(schedulePanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createEmptySchedulePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(40, 40, 40, 40)
        ));
        
        JLabel iconLabel = new JLabel("📅");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel messageLabel = new JLabel("No classes scheduled yet");
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        messageLabel.setForeground(TEXT_COLOR);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel instructionLabel = new JLabel("Add classes to see your teaching schedule here");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        instructionLabel.setForeground(new Color(107, 114, 128));
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(iconLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(messageLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(instructionLabel);
        
        return panel;
    }
    
    // Helper methods for schedule statistics
    private int getClassesWithScheduleCount() {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("class.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[1].equals(currentUser.getUserId())) {
                    String schedule = parts[4];
                    if (schedule != null && !schedule.trim().isEmpty() && !schedule.equals("Not scheduled")) {
                        count++;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading class file: " + e.getMessage());
        }
        return count;
    }
    
    private int getTotalScheduledSessions() {
        int totalSessions = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("class.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[1].equals(currentUser.getUserId())) {
                    String schedule = parts[4];
                    if (schedule != null && !schedule.trim().isEmpty()) {
                        String[] sessions = schedule.split(";");
                        totalSessions += sessions.length;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading class file: " + e.getMessage());
        }
        return totalSessions;
    }
    
    private String getNextScheduledClass() {
        // For simplicity, return the first class found with a schedule
        try (BufferedReader reader = new BufferedReader(new FileReader("class.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[1].equals(currentUser.getUserId())) {
                    String schedule = parts[4];
                    if (schedule != null && !schedule.trim().isEmpty() && !schedule.equals("Not scheduled")) {
                        return parts[0]; // Return class ID
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading class file: " + e.getMessage());
        }
        return "None";
    }

    private String getCurrentLoginTime() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return now.format(formatter) + " (Current Session)";
}

/**
 * Gets last online time (simulated - you can enhance this with actual tracking)
 */
    private String getLastOnlineTime() {
    // This simulates last online time - you can enhance this by storing actual login history
        java.time.LocalDateTime lastOnline = java.time.LocalDateTime.now().minusHours(2);
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return lastOnline.format(formatter);
}

    private String getTeachingStatus() {
    int classCount = getTutorClassCount();
        if (classCount >= 3) {
        return "Active Teacher";
        } else if (classCount >= 1) {
        return "Part-time Teacher";
        } else {
        return "No Active Classes";
    }
}
    
    private void refreshSchedule(JPanel scheduleContent) {
        loadTeachingSchedule(scheduleContent);
    }
    
    private void exportTeachingSchedule() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Teaching Schedule");
        fileChooser.setSelectedFile(new java.io.File("teaching_schedule_" + currentUser.getUserId() + ".txt"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fileChooser.getSelectedFile();
                
                try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(file))) {
                    writer.println("=== TEACHING SCHEDULE ===");
                    writer.println("Tutor: " + currentUser.getName() + " (" + currentUser.getUserId() + ")");
                    writer.println("Export Date: " + java.time.LocalDateTime.now());
                    writer.println();
                    
                    try (BufferedReader reader = new BufferedReader(new FileReader("class.txt"))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            String[] parts = line.split(",");
                            if (parts.length >= 6 && parts[1].equals(currentUser.getUserId())) {
                                writer.println("CLASS: " + parts[2]);
                                writer.println("ID: " + parts[0]);
                                writer.println("Students: " + getStudentCountForClass(parts[0]));
                                writer.println("Fee: RM" + parts[5]);
                                writer.println("Schedule: " + (parts[4].isEmpty() ? "Not scheduled" : parts[4]));
                                writer.println("Description: " + parts[3]);
                                writer.println("-".repeat(40));
                            }
                        }
                    }
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Teaching schedule exported successfully to:\n" + file.getAbsolutePath(),
                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error exporting schedule: " + e.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Class management methods implementation
    private void loadTutorClasses(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        
        try (BufferedReader reader = new BufferedReader(new FileReader("class.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6 && parts[1].equals(currentUser.getUserId())) {
                    String classId = parts[0];
                    String courseName = parts[2];
                    String description = parts[3];
                    String schedule = parts[4].replace("; ", ", ");
                    String fee = parts[5];
                    
                    int studentCount = getStudentCountForClass(classId);
                    
                    Object[] row = {classId, courseName, description, schedule, fee, studentCount};
                    tableModel.addRow(row);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading classes: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchClasses(String searchTerm, DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        
        try (BufferedReader reader = new BufferedReader(new FileReader("class.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6 && parts[1].equals(currentUser.getUserId())) {
                    String classId = parts[0];
                    String courseName = parts[2];
                    String description = parts[3];
                    
                    // Check if search term matches any field
                    if (searchTerm.isEmpty() || 
                        classId.toLowerCase().contains(searchTerm.toLowerCase()) ||
                        courseName.toLowerCase().contains(searchTerm.toLowerCase()) ||
                        description.toLowerCase().contains(searchTerm.toLowerCase())) {
                        
                        String schedule = parts[4].replace("; ", ", ");
                        String fee = parts[5];
                        int studentCount = getStudentCountForClass(classId);
                        
                        Object[] row = {classId, courseName, description, schedule, fee, studentCount};
                        tableModel.addRow(row);
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error searching classes: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadClassSelector(JComboBox<String> classSelector) {
        classSelector.removeAllItems();
        
        try (BufferedReader reader = new BufferedReader(new FileReader("class.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3 && parts[1].equals(currentUser.getUserId())) {
                    classSelector.addItem(parts[0] + " - " + parts[2]);
                }
            }
            
            if (classSelector.getItemCount() == 0) {
                classSelector.addItem("No classes found");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading classes: " + e.getMessage());
        }
    }
    
    private void loadStudentsForClass(JComboBox<String> classSelector, DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        
        String selected = (String) classSelector.getSelectedItem();
        if (selected == null || selected.equals("No classes found")) {
            return;
        }
        
        String selectedClassId = selected.split(" - ")[0];
        int count = 1;
        
        try (BufferedReader reader = new BufferedReader(new FileReader("students.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 10) {
                    String studentId = parts[0];
                    String name = parts[3];
                    String email = parts[4];
                    String phone = parts[5];
                    String level = parts[7];
                    String enrollMonth = parts[8];
                    String[] enrolledClasses = parts[9].split(";");
                    
                    for (String classId : enrolledClasses) {
                        if (classId.trim().equals(selectedClassId)) {
                            Object[] row = {count, studentId, name, email, phone, level, enrollMonth};
                            tableModel.addRow(row);
                            count++;
                            break;
                        }
                    }
                }
            }
            
            if (count == 1) {
                Object[] row = {"No students enrolled in this class", "", "", "", "", "", ""};
                tableModel.addRow(row);
            }
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage());
        }
    }
    
    private void exportClassData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Class Data");
        fileChooser.setSelectedFile(new java.io.File("tutor_classes_export.csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fileChooser.getSelectedFile();
                try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
                    writer.println("Class ID,Course Name,Description,Schedule,Fee,Students");
                    
                    try (BufferedReader reader = new BufferedReader(new FileReader("class.txt"))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            String[] parts = line.split(",");
                            if (parts.length >= 6 && parts[1].equals(currentUser.getUserId())) {
                                int studentCount = getStudentCountForClass(parts[0]);
                                writer.printf("%s,%s,%s,%s,%s,%d%n",
                                    parts[0], parts[2], parts[3], parts[4], parts[5], studentCount);
                            }
                        }
                    }
                }
                JOptionPane.showMessageDialog(this, "Class data exported successfully to:\n" + file.getAbsolutePath(),
                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error exporting data: " + e.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportStudentList(JComboBox<String> classSelector, DefaultTableModel tableModel) {
        String selected = (String) classSelector.getSelectedItem();
        if (selected == null || selected.equals("No classes found")) {
            JOptionPane.showMessageDialog(this, "Please select a class first!", "No Class Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Student List");
        String className = selected.replace(" - ", "_").replaceAll("[^a-zA-Z0-9_-]", "");
        fileChooser.setSelectedFile(new java.io.File("students_" + className + ".csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fileChooser.getSelectedFile();
                try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
                    writer.println("No,Student ID,Name,Email,Phone,Level,Enrollment Month");
                    
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        writer.printf("%s,%s,%s,%s,%s,%s,%s%n",
                            tableModel.getValueAt(i, 0),
                            tableModel.getValueAt(i, 1),
                            tableModel.getValueAt(i, 2),
                            tableModel.getValueAt(i, 3),
                            tableModel.getValueAt(i, 4),
                            tableModel.getValueAt(i, 5),
                            tableModel.getValueAt(i, 6));
                    }
                }
                JOptionPane.showMessageDialog(this, "Student list exported successfully to:\n" + file.getAbsolutePath(),
                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error exporting student list: " + e.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Dialog methods using your existing dialogs
    private void showAddClassDialog() {
        AddClassDialog dialog = new AddClassDialog(this, currentUser.getUserId());
        dialog.setVisible(true);
    }
    
    private void showClassDetails(String classId) {
        ClassDetailsDialog dialog = new ClassDetailsDialog(this, classId);
        dialog.setVisible(true);
    }
    
    private void showEditClassDialog(String classId) {
        EditClassDialog dialog = new EditClassDialog(this, classId, currentUser.getUserId());
        dialog.setVisible(true);
    }
    
    private void deleteClass(String classId, String className, DefaultTableModel tableModel) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "⚠️ DELETE CLASS WARNING ⚠️\n\n" +
            "Class: " + className + " (" + classId + ")\n\n" +
            "This action will:\n" +
            "• Remove the class permanently\n" +
            "• Affect enrolled students\n" +
            "• Cannot be undone\n\n" +
            "Are you sure you want to delete this class?",
            "Confirm Class Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (deleteClassFromFile(classId)) {
                JOptionPane.showMessageDialog(this, 
                    "✅ Class deleted successfully!\n\n" +
                    "Class: " + className + " (" + classId + ")", 
                    "Deletion Successful", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadTutorClasses(tableModel);
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "❌ Error deleting class!\n\nPlease try again or contact system administrator.", 
                    "Deletion Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean deleteClassFromFile(String classId) {
        File originalFile = new File("class.txt");
        File tempFile = new File("class_temp.txt");
        boolean deleted = false;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(originalFile));
             PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].equals(classId)) {
                    deleted = true;
                } else {
                    writer.println(line);
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        
        if (deleted && originalFile.delete() && tempFile.renameTo(originalFile)) {
            return true;
        }
        
        tempFile.delete();
        return false;
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(25);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        return field;
    }
    
    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(25);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setEchoChar('•');
        return field;
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
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel logoLabel = new JLabel("🎓 ATC Tuition Centre - Tutor Portal");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logoLabel.setForeground(Color.WHITE);
        
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setBackground(PRIMARY_COLOR);
        
        JLabel userLabel = new JLabel("Welcome, " + currentUser.getName());
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoutButton.setBackground(Color.WHITE);
        logoutButton.setForeground(PRIMARY_COLOR);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
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
        setTitle("ATC Tuition Centre - Tutor Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
    // Helper methods for statistics
    private int getTutorClassCount() {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("class.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[1].equals(currentUser.getUserId())) {
                    count++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }
    
    private int getTutorStudentCount() {
        Set<String> uniqueStudents = new HashSet<>();
        String tutorId = currentUser.getUserId();
        
        try (BufferedReader reader = new BufferedReader(new FileReader("students.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 10 && !parts[9].trim().isEmpty()) {
                    String[] studentClasses = parts[9].split(";");
                    
                    // Check if any of the student's classes belong to this tutor
                    for (String classId : studentClasses) {
                        if (classId != null && !classId.trim().isEmpty()) {
                            if (isClassBelongsToTutor(classId.trim(), tutorId)) {
                                uniqueStudents.add(parts[0]); // Add student ID
                                break; // Student found, no need to check other classes
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading students file: " + e.getMessage());
        }
        return uniqueStudents.size();
    }
    
    private boolean isClassBelongsToTutor(String classId, String tutorId) {
        try (BufferedReader reader = new BufferedReader(new FileReader("class.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].equals(classId) && parts[1].equals(tutorId)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading class file: " + e.getMessage());
        }
        return false;
    }
    
    private String getTutorLevel() {
        // Determine tutor level based on classes taught
        Set<String> levels = new HashSet<>();
        String tutorId = currentUser.getUserId();
        
        try (BufferedReader reader = new BufferedReader(new FileReader("class.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3 && parts[1].equals(tutorId)) {
                    String subject = parts[2].toLowerCase();
                    if (subject.contains("form 1")) levels.add("Form 1");
                    else if (subject.contains("form 2")) levels.add("Form 2");
                    else if (subject.contains("form 3")) levels.add("Form 3");
                    else if (subject.contains("form 4")) levels.add("Form 4");
                    else if (subject.contains("form 5")) levels.add("Form 5");
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading class file: " + e.getMessage());
        }
        
        if (levels.isEmpty()) {
            return "Not Set";
        } else if (levels.size() == 1) {
            return levels.iterator().next();
        } else {
            return "Multi-Level";
        }
    }
    
    private int getActiveClassCount() {
        // For now, return the same as total classes
        // You can enhance this to check if classes have students enrolled
        int activeCount = 0;
        String tutorId = currentUser.getUserId();
        
        try (BufferedReader reader = new BufferedReader(new FileReader("class.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[1].equals(tutorId)) {
                    // Check if class has any students
                    String classId = parts[0];
                    if (getStudentCountForClass(classId) > 0) {
                        activeCount++;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading class file: " + e.getMessage());
        }
        
        return activeCount;
    }
    
    private List<String> getTutorClassIds() {
        List<String> classIds = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("class.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[1].equals(currentUser.getUserId())) {
                    classIds.add(parts[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classIds;
    }
    
    private int getStudentCountForClass(String classId) {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("students.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 10) {
                    String[] studentClasses = parts[9].split(";");
                    for (String studentClass : studentClasses) {
                        if (studentClass.trim().equals(classId)) {
                            count++;
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }
    
    public void refreshData() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        tabbedPane.setComponentAt(0, createDashboardPanel());
        tabbedPane.setComponentAt(1, createClassManagementPanel());
        tabbedPane.setComponentAt(2, createStudentViewPanel());
        tabbedPane.setSelectedIndex(selectedIndex);
    }
} 