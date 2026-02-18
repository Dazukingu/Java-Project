import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class ReceptionistDashboard extends JFrame {
    private User currentUser;
    private DataManager dataManager;
    private JTabbedPane tabbedPane;
    
    // Modern color scheme
    private final Color PRIMARY_COLOR = new Color(59, 130, 246);    // Blue
    private final Color SECONDARY_COLOR = new Color(243, 244, 246);  // Light gray
    private final Color ACCENT_COLOR = new Color(16, 185, 129);      // Green
    private final Color ERROR_COLOR = new Color(239, 68, 68);        // Red
    private final Color TEXT_COLOR = new Color(31, 41, 55);          // Dark gray
    private final Color BACKGROUND_COLOR = new Color(249, 250, 251); // Very light gray
    
    // Student table
    private JTable studentTable;
    private DefaultTableModel studentTableModel;
    
    // Thread safety
    private final Object fileLock = new Object();
    
    public ReceptionistDashboard(User user) {
        this.currentUser = user;
        this.dataManager = new DataManager();
        
        initializeComponents();
        setupLayout();
        loadStudentData();
        setupWindow();
    }
    
    private void initializeComponents() {
        // Create tabbed pane with enhanced styling
    tabbedPane = new JTabbedPane();
    tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    tabbedPane.setBackground(Color.WHITE);
    tabbedPane.setTabPlacement(JTabbedPane.TOP);
    
    // Add tabs with icons - UPDATED: Added Subject Requests tab
    tabbedPane.addTab("🏠 Dashboard", createDashboardPanel());
    tabbedPane.addTab("➕ Register Student", createRegisterStudentPanel());
    tabbedPane.addTab("👥 Manage Students", createManageStudentsPanel());
    tabbedPane.addTab("🔄 Subject Requests", createSubjectRequestsPanel()); // NEW TAB
    tabbedPane.addTab("💳 Payments", createPaymentsPanel());
    tabbedPane.addTab("👤 Profile", createEnhancedProfilePanel());
}

private JPanel createSubjectRequestsPanel() {
    // Create the SubjectPendingPanel with scroll support
    SubjectPendingPanel subjectPanel = new SubjectPendingPanel(dataManager, this);
    
    // Wrap in a main container for better layout
    JPanel containerPanel = new JPanel(new BorderLayout());
    containerPanel.setBackground(BACKGROUND_COLOR);
    containerPanel.setBorder(new EmptyBorder(0, 0, 0, 0)); // No extra padding
    
    // Add the subject panel directly
    containerPanel.add(subjectPanel, BorderLayout.CENTER);
    
    return containerPanel;
}
    
    // Enhanced Profile Panel with tabbed interface (from AdminDashboard pattern)
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
        
        // Work Info tab (Receptionist-specific)
        profileTabs.addTab("🏢 Work Details", createScrollableWorkInfoPanel());
        
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
        JLabel titleLabel = new JLabel("Receptionist Personal Information");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Profile form
        JPanel profileForm = createEditableReceptionistProfileForm();
        
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
        JPanel passwordPanel = createReceptionistPasswordChangeFormPanel();
        
        // Security info panel
        JPanel securityInfoPanel = createReceptionistSecurityInfoPanel();
        
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
    
    private JPanel createScrollableWorkInfoPanel() {
        // Main scrollable container
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(BACKGROUND_COLOR);
        
        // Content panel that will be scrolled
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Title
        JLabel titleLabel = new JLabel("Work Information");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Work details panel
        JPanel workPanel = createReceptionistWorkDetailsPanel();
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        contentPanel.add(workPanel);
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
    
    private JPanel createEditableReceptionistProfileForm() {
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
        
        JTextArea addressArea = createStyledTextArea(3, 25);
        // Try to get address from receptionist data or use default
        String address = getReceptionistAddress(currentUser.getUserId());
        addressArea.setText(address);
        
        // Layout form fields
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(createFieldLabel("User ID:"), gbc);
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
        
        updateButton.addActionListener(e -> updateReceptionistPersonalInfo(nameField, emailField, phoneField, addressArea));
        resetButton.addActionListener(e -> {
            nameField.setText(currentUser.getName());
            emailField.setText(currentUser.getEmail());
            phoneField.setText(currentUser.getPhone());
            addressArea.setText(getReceptionistAddress(currentUser.getUserId()));
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
    
    private JPanel createReceptionistPasswordChangeFormPanel() {
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
            changeReceptionistPassword(currentPasswordField, newPasswordField, confirmPasswordField));
        
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
    
    private JPanel createReceptionistSecurityInfoPanel() {
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
        JLabel statusLabel = new JLabel("Active Receptionist");
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
        JLabel accessLabel = new JLabel("Student Management Access");
        accessLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        accessLabel.setForeground(PRIMARY_COLOR);
        panel.add(accessLabel, gbc);
        
        // Account Type
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(createFieldLabel("Account Type:"), gbc);
        gbc.gridx = 1;
        JLabel typeLabel = new JLabel("Front Desk Receptionist");
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        typeLabel.setForeground(new Color(107, 114, 128));
        panel.add(typeLabel, gbc);
        
        // System Version
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(createFieldLabel("System Version:"), gbc);
        gbc.gridx = 1;
        JLabel versionLabel = new JLabel("ATC Management System v2.0");
        versionLabel.setForeground(new Color(107, 114, 128));
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(versionLabel, gbc);
        
        return panel;
    }
    
    private JPanel createReceptionistWorkDetailsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1), 
                "Work Information (Read-Only)", 0, 0, new Font("Segoe UI", Font.BOLD, 16), TEXT_COLOR),
            new EmptyBorder(25, 25, 25, 25)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Work info fields (read-only)
        JTextField userIdField = createStyledTextField();
        userIdField.setText(currentUser.getUserId());
        userIdField.setEditable(false);
        userIdField.setBackground(SECONDARY_COLOR);
        
        JTextField usernameField = createStyledTextField();
        usernameField.setText(currentUser.getUsername());
        usernameField.setEditable(false);
        usernameField.setBackground(SECONDARY_COLOR);
        
        JTextField positionField = createStyledTextField();
        positionField.setText("Front Desk Receptionist");
        positionField.setEditable(false);
        positionField.setBackground(SECONDARY_COLOR);
        
        JTextField departmentField = createStyledTextField();
        departmentField.setText("Student Services");
        departmentField.setEditable(false);
        departmentField.setBackground(SECONDARY_COLOR);
        
        // Work summary
        JTextArea workSummaryArea = createStyledTextArea(6, 25);
        workSummaryArea.setText(
            "Responsibilities:\n" +
            "• Student registration and enrollment\n" +
            "• Payment processing and records\n" +
            "• Student information management\n" +
            "• Customer service and inquiries\n" +
            "• Administrative support tasks\n" +
            "• Class scheduling coordination"
        );
        workSummaryArea.setEditable(false);
        workSummaryArea.setBackground(SECONDARY_COLOR);
        
        // Layout fields
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(createFieldLabel("User ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(userIdField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(createFieldLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(usernameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(createFieldLabel("Position:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(positionField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panel.add(createFieldLabel("Department:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(departmentField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(createFieldLabel("Work Summary:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        panel.add(new JScrollPane(workSummaryArea), gbc);
        
        return panel;
    }
    
    // Helper methods from AdminDashboard pattern
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

    private int getPendingSubjectRequests() {
    int count = 0;
    try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("Subject_Change_Requests.txt"))) {
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 5 && "Pending".equalsIgnoreCase(parts[4].trim())) {
                count++;
            }
        }
    } catch (java.io.IOException e) {
        // File might not exist
        count = 0;
    }
    return count;
}
    
    private int calculatePasswordStrength(String password) {
        if (password.length() == 0) return 0;
        
        int score = 0;
        
        // Length check
        if (password.length() >= 6) score += 25;
        if (password.length() >= 8) score += 15;
        
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
        
        JLabel titleLabel = new JLabel("Password Requirements:");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(TEXT_COLOR);
        
        String[] requirements = {
            "• At least 6 characters long (8+ recommended)",
            "• Contains uppercase and lowercase letters",
            "• Contains at least one number",
            "• Contains at least one special character (!@#$%^&*)",
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
    
    private void updateReceptionistPersonalInfo(JTextField nameField, JTextField emailField, 
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
            // Update receptionist profile in file
            boolean success = updateReceptionistInFile(currentUser.getUserId(), name, email, phone, address);
            
            if (success) {
                // Update current user object
                currentUser.setName(name);
                currentUser.setEmail(email);
                currentUser.setPhone(phone);
                
                JOptionPane.showMessageDialog(this, 
                    "✅ Profile updated successfully!\n\n" +
                    "Name: " + name + "\n" +
                    "Email: " + email + "\n" +
                    "Phone: " + phone, 
                    "Update Successful", JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh dashboard
                refreshData();
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
    
    private void changeReceptionistPassword(JPasswordField currentField, JPasswordField newField, 
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
        
        if (newPassword.length() < 6) {
            JOptionPane.showMessageDialog(this, 
                "Password must be at least 6 characters long!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (newPassword.equals(currentPassword)) {
            JOptionPane.showMessageDialog(this, 
                "New password must be different from current password!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Update password in file
            boolean success = updateReceptionistPasswordInFile(currentUser.getUserId(), newPassword);
            
            if (success) {
                // Update current user object
                currentUser.setPassword(newPassword);
                
                // Clear fields
                currentField.setText("");
                newField.setText("");
                confirmField.setText("");
                
                JOptionPane.showMessageDialog(this, 
                    "✅ Password changed successfully!\n\n" +
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
    
    private boolean updateReceptionistInFile(String userId, String name, String email, String phone, String address) {
        try {
            File file = new File("receptionist.txt");
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
            
            // Update the receptionist's line
            for (int i = 0; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if (parts.length >= 1 && parts[0].equals(userId)) {
                    // Reconstruct the line with updated information
                    // Format: userId,username,name,password,email,phone,address
                    String updatedLine = userId + "," + 
                                       (parts.length >= 2 ? parts[1] : currentUser.getUsername()) + "," + 
                                       name + "," + 
                                       (parts.length >= 4 ? parts[3] : currentUser.getPassword()) + "," + 
                                       email + "," + phone + "," + address;
                    lines.set(i, updatedLine);
                    found = true;
                    break;
                }
            }
            
            // If receptionist not found, add new line
            if (!found) {
                String newLine = userId + "," + currentUser.getUsername() + "," + name + "," + 
                               currentUser.getPassword() + "," + email + "," + phone + "," + address;
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
    
    private boolean updateReceptionistPasswordInFile(String userId, String newPassword) {
        try {
            File file = new File("receptionist.txt");
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
            
            // Update the receptionist's password
            for (int i = 0; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",", 7);
                if (parts.length >= 1 && parts[0].equals(userId)) {
                    // Reconstruct the line with updated password
                    StringBuilder updatedLine = new StringBuilder();
                    updatedLine.append(parts[0]).append(","); // user ID
                    updatedLine.append(parts.length >= 2 ? parts[1] : "").append(","); // username
                    updatedLine.append(parts.length >= 3 ? parts[2] : "").append(","); // name
                    updatedLine.append(newPassword).append(","); // new password
                    updatedLine.append(parts.length >= 5 ? parts[4] : "").append(","); // email
                    updatedLine.append(parts.length >= 6 ? parts[5] : "").append(","); // phone
                    updatedLine.append(parts.length >= 7 ? parts[6] : ""); // address
                    
                    lines.set(i, updatedLine.toString());
                    found = true;
                    break;
                }
            }
            
            // Write back to file
            if (found) {
                try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                    for (String line : lines) {
                        writer.println(line);
                    }
                }
            }
            
            return found;
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getCurrentDate() {
    java.time.LocalDate now = java.time.LocalDate.now();
    java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd");
    return now.format(formatter);
}

    
    private String getReceptionistAddress(String userId) {
        try (BufferedReader reader = new BufferedReader(new FileReader("receptionist.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 7 && parts[0].equals(userId)) {
                    return parts[6]; // address is the 7th field
                }
            }
        } catch (IOException e) {
            // File not found or error reading
        }
        return "Address not available";
    }
    
    private String getCurrentLoginTime() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return now.format(formatter) + " (Current Session)";
    }
    
    // Keep all existing methods from original ReceptionistDashboard
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
        
        JLabel roleLabel = new JLabel("Receptionist Dashboard - ATC Tuition Centre");
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
        
        // Fixed Layout - Proper container hierarchy
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.add(statsPanel, BorderLayout.CENTER);
        contentPanel.add(actionsPanel, BorderLayout.SOUTH);
        
        // Main layout
        panel.add(welcomePanel, BorderLayout.NORTH);
        panel.add(Box.createVerticalStrut(20), BorderLayout.CENTER);
        panel.add(contentPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
private JPanel createStatsPanel() {
    JPanel panel = new JPanel(new GridLayout(2, 3, 20, 20));
    panel.setBackground(BACKGROUND_COLOR);
    panel.setBorder(new EmptyBorder(20, 0, 20, 0));
    
    // Get statistics - Thread safe
    List<Student> students = getStudentsSafe();
    List<ClassInfo> classes = dataManager.getAllClasses();
    List<Tutor> tutors = dataManager.getAllTutors();
    
    // NEW: Get subject change request statistics
    int pendingRequests = getPendingSubjectRequests();
    
    // Calculate additional stats
    Map<String, Integer> levelCounts = new HashMap<>();
    for (Student student : students) {
        levelCounts.put(student.getLevel(), levelCounts.getOrDefault(student.getLevel(), 0) + 1);
    }
    
    // Create stat cards - UPDATED: Added pending requests card
    panel.add(createStatCard("👥", "Total Students", String.valueOf(students.size()), PRIMARY_COLOR));
    panel.add(createStatCard("📚", "Available Classes", String.valueOf(classes.size()), ACCENT_COLOR));
    panel.add(createStatCard("👩‍🏫", "Active Tutors", String.valueOf(tutors.size()), new Color(168, 85, 247)));
    panel.add(createStatCard("🔄", "Pending Requests", String.valueOf(pendingRequests), new Color(245, 158, 11)));
    panel.add(createStatCard("👤", "Logged in as", currentUser.getName(), new Color(99, 102, 241)));
    
    // Create a placeholder card for the 6th position
    panel.add(createStatCard("⏰", "Today", getCurrentDate(), new Color(59, 130, 246)));
    
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
    
    JButton registerBtn = createActionButton("Register New Student", "➕", e -> tabbedPane.setSelectedIndex(1));
    JButton paymentBtn = createActionButton("Process Payment", "💳", e -> tabbedPane.setSelectedIndex(4));
    JButton searchBtn = createActionButton("Search Students", "🔍", e -> tabbedPane.setSelectedIndex(2));
    JButton requestsBtn = createActionButton("Subject Requests", "🔄", e -> tabbedPane.setSelectedIndex(3)); // NEW BUTTON
    
    panel.add(registerBtn);
    panel.add(paymentBtn);
    panel.add(searchBtn);
    panel.add(requestsBtn); // Add the new button
    
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
    
    private JPanel createRegisterStudentPanel() {
        return new RegisterStudentPanel(dataManager, this);
    }
    
    private JPanel createManageStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel("Manage Students");
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
        
        JButton searchButton = createStyledButton("Search", PRIMARY_COLOR);
        JButton refreshButton = createStyledButton("Refresh", SECONDARY_COLOR);
        JButton exportButton = createStyledButton("Export", ACCENT_COLOR);
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);
        searchPanel.add(exportButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        // Students table - Fixed initialization
        String[] columnNames = {"ID", "Name", "IC/Passport", "Email", "Level", "Subjects", "Status"};
        studentTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        studentTable = new JTable(studentTableModel);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        studentTable.setRowHeight(30);
        studentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        studentTable.getTableHeader().setBackground(SECONDARY_COLOR);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Alternate row colors - Fixed renderer
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
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonsPanel.setBackground(BACKGROUND_COLOR);
        
        JButton updateButton = createStyledButton("Update Subjects", ACCENT_COLOR);
        JButton deleteButton = createStyledButton("Delete Student", ERROR_COLOR);
        JButton viewPaymentsButton = createStyledButton("View Payments", PRIMARY_COLOR);
        JButton editProfileButton = createStyledButton("Edit Profile", new Color(99, 102, 241));
        
        buttonsPanel.add(updateButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(viewPaymentsButton);
        buttonsPanel.add(editProfileButton);
        
        // Event listeners - Fixed with proper error handling
        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText().trim();
            performSearch(searchTerm);
        });
        
        refreshButton.addActionListener(e -> loadStudentData());
        exportButton.addActionListener(e -> exportStudentData());
        
        updateButton.addActionListener(e -> updateSelectedStudentSubjects());
        deleteButton.addActionListener(e -> deleteSelectedStudent());
        viewPaymentsButton.addActionListener(e -> viewSelectedStudentPayments());
        editProfileButton.addActionListener(e -> editSelectedStudentProfile());
        
        // Search field enter key
        searchField.addActionListener(e -> performSearch(searchField.getText().trim()));
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void updateSelectedStudentSubjects() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow >= 0) {
            String studentId = (String) studentTableModel.getValueAt(selectedRow, 0);
            showUpdateSubjectsDialog(studentId);
        } else {
            showWarningMessage("Please select a student to update.");
        }
    }
    
    private void deleteSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow < 0) {
            showWarningMessage("Please select a student to delete.");
            return;
        }
        
        try {
            String studentId = (String) studentTableModel.getValueAt(selectedRow, 0);
            String studentName = (String) studentTableModel.getValueAt(selectedRow, 1);
            
            // Enhanced confirmation dialog
            String[] options = {"Delete Student", "Cancel"};
            int choice = JOptionPane.showOptionDialog(this,
                "⚠️ PERMANENT DELETION WARNING ⚠️\n\n" +
                "Student: " + studentName + " (" + studentId + ")\n\n" +
                "This action will:\n" +
                "• Remove the student from the system\n" +
                "• Keep payment history for records\n" +
                "• Cannot be undone\n\n" +
                "Are you sure this student has completed their studies?",
                "Confirm Student Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[1]);
            
            if (choice == 0) { // Delete confirmed
                if (dataManager.deleteStudent(studentId)) {
                    showSuccessMessage(
                        "✅ Student deleted successfully!\n\n" +
                        "• Student record removed from system\n" +
                        "• Payment history preserved for records\n" +
                        "• Student: " + studentName + " (" + studentId + ")"
                    );
                    loadStudentData();
                } else {
                    showErrorMessage("❌ Error deleting student!\n\nPlease try again or contact system administrator.");
                }
            }
        } catch (Exception e) {
            showErrorMessage("Error during deletion: " + e.getMessage());
        }
    }
    
    private void viewSelectedStudentPayments() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow >= 0) {
            String studentId = (String) studentTableModel.getValueAt(selectedRow, 0);
            String studentName = (String) studentTableModel.getValueAt(selectedRow, 1);
            showPaymentHistoryDialog(studentId, studentName);
        } else {
            showWarningMessage("Please select a student to view payment history.");
        }
    }
    
    private void editSelectedStudentProfile() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow >= 0) {
            String studentId = (String) studentTableModel.getValueAt(selectedRow, 0);
            Student student = dataManager.getStudentById(studentId);
            
            if (student != null) {
                showEditStudentProfileDialog(student);
            } else {
                showErrorMessage("Student not found!");
            }
        } else {
            showWarningMessage("Please select a student to edit.");
        }
    }
    
    private void performSearch(String searchTerm) {
        if (!searchTerm.isEmpty()) {
            List<Student> results = dataManager.searchStudents(searchTerm);
            updateStudentTable(results);
        } else {
            loadStudentData();
        }
    }
    
    private void exportStudentData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Student Data");
        fileChooser.setSelectedFile(new java.io.File("students_export.csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fileChooser.getSelectedFile();
                exportToCSV(file);
                showSuccessMessage("Data exported successfully to:\n" + file.getAbsolutePath());
            } catch (Exception e) {
                showErrorMessage("Error exporting data: " + e.getMessage());
            }
        }
    }
    
    private void exportToCSV(java.io.File file) throws java.io.IOException {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
            // Write header
            writer.println("Student ID,Name,IC/Passport,Email,Phone,Address,Level,Enrollment Month,Subjects");
            
            // Write data
            List<Student> students = getStudentsSafe();
            for (Student student : students) {
                writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                    student.getStudentId(),
                    student.getName(),
                    student.getIc(),
                    student.getEmail(),
                    student.getPhone(),
                    student.getAddress(),
                    student.getLevel(),
                    student.getEnrollmentMonth(),
                    student.getSubjectsString()
                );
            }
        }
    }
    
    private JPanel createPaymentsPanel() {
        return new PaymentPanel(dataManager, this);
    }
    
    // Helper methods for UI components (from AdminDashboard pattern)
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
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
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
        
        JLabel logoLabel = new JLabel("🎓 ATC Tuition Centre");
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
        setTitle("ATC Tuition Centre - Receptionist Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Set application icon
        try {
            // setIconImage(new ImageIcon("icon.png").getImage());
        } catch (Exception e) {
            // Icon not found, continue without it
        }
    }
    
    // Thread-safe data access methods
    public void loadStudentData() {
        SwingUtilities.invokeLater(() -> {
            List<Student> students = getStudentsSafe();
            updateStudentTable(students);
        });
    }
    
    private List<Student> getStudentsSafe() {
        synchronized(fileLock) {
            return dataManager.getAllStudents();
        }
    }

    private List<Student> readStudentsDirectly() {
    List<Student> students = new ArrayList<>();
    
    System.out.println("🔧 DIRECT FIX: Reading students directly from file...");
    
    try (BufferedReader reader = new BufferedReader(new FileReader("students.txt"))) {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            
            String[] parts = line.split(",");
            System.out.println("🔍 Processing line: " + line);
            
            // File format: StudentID,IC,Password,Name,Email,Phone,Address,Level,Month,Subjects
            if (parts.length >= 9) {
                String studentId = parts[0].trim();
                String ic = parts[1].trim();
                String password = parts[2].trim();
                String name = parts[3].trim();        // <- CORRECT FIELD FOR NAME
                String email = parts[4].trim();
                String phone = parts[5].trim();
                String address = parts[6].trim();
                String level = parts[7].trim();
                String enrollmentMonth = parts[8].trim();
                
                System.out.println("🔍 Creating student: ID=" + studentId + ", Name=" + name);
                
                Student student = new Student(
                    studentId,      // userId
                    ic,            // ic
                    studentId,     // username  
                    password,      // password
                    name,          // name <- IMPORTANT: USE CORRECT FIELD
                    email,         // email
                    phone,         // phone
                    address,       // address
                    level,         // level
                    enrollmentMonth // enrollmentMonth
                );
                
                // Handle subjects
                if (parts.length > 9 && !parts[9].trim().isEmpty()) {
                    student.setSubjectsFromString(parts[9].trim());
                }
                
                students.add(student);
                
                System.out.println("✅ Added student: " + name + " (ID: " + studentId + ")");
            }
        }
    } catch (IOException e) {
        System.err.println("❌ Error reading students.txt: " + e.getMessage());
        return dataManager.getAllStudents(); // Fallback
    }
    
    System.out.println("✅ Direct read completed: " + students.size() + " students");
    return students;
}
    
    private void updateStudentTable(List<Student> students) {
        // Ensure this runs on EDT
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> updateStudentTable(students));
            return;
        }
        
        studentTableModel.setRowCount(0);
        
        // Get all classes for lookup
        List<ClassInfo> allClasses = dataManager.getAllClasses();
        Map<String, String> classIdToSubjectMap = new HashMap<>();
        for (ClassInfo classInfo : allClasses) {
            classIdToSubjectMap.put(classInfo.getClassId(), classInfo.getSubject());
        }
        
        for (Student student : students) {
            // Convert class IDs to subject names for display
            StringBuilder subjectsDisplay = new StringBuilder();
            List<String> classIds = student.getSubjects();
            for (String classId : classIds) {
                if (classId != null && !classId.trim().isEmpty()) {
                    if (subjectsDisplay.length() > 0) subjectsDisplay.append("; ");
                    String subjectName = classIdToSubjectMap.get(classId);
                    if (subjectName != null) {
                        subjectsDisplay.append(classId).append(": ").append(subjectName);
                    } else {
                        subjectsDisplay.append(classId);
                    }
                }
            }
            
            // Determine status
            String status = "Active";
            if (student.getSubjects().isEmpty()) {
                status = "No Subjects";
            } else if (student.getTotalBalance() < 0) {
                status = "Outstanding Payment";
            }
            
            Object[] row = {
                student.getStudentId(),
                student.getName(),
                student.getIc(),
                student.getEmail(),
                student.getLevel(),
                subjectsDisplay.toString(),
                status
            };
            studentTableModel.addRow(row);
        }
        
        // Update table appearance
        studentTable.revalidate();
        studentTable.repaint();
    }
    
    public void refreshData() {
    SwingUtilities.invokeLater(() -> {
        // Store current selection
        int selectedIndex = tabbedPane.getSelectedIndex();
        
        // Refresh data
        loadStudentData();
        
        tabbedPane.removeTabAt(0);
        tabbedPane.insertTab("🏠 Dashboard", null, createDashboardPanel(), null, 0);
        
        if (tabbedPane.getTabCount() > 3) {
            Component subjectRequestsComponent = tabbedPane.getComponentAt(3);
            if (subjectRequestsComponent instanceof JPanel) {
                JPanel containerPanel = (JPanel) subjectRequestsComponent;
                if (containerPanel.getComponentCount() > 0 && 
                    containerPanel.getComponent(0) instanceof SubjectPendingPanel) {
                    
                    // Recreate the subject requests panel
                    tabbedPane.removeTabAt(3);
                    tabbedPane.insertTab("🔄 Subject Requests", null, createSubjectRequestsPanel(), null, 3);
                }
            }
        }
        
        // Restore selection
        if (selectedIndex < tabbedPane.getTabCount()) {
            tabbedPane.setSelectedIndex(selectedIndex);
        }
    });
}
    
    // Dialog helper methods
    private void showUpdateSubjectsDialog(String studentId) {
        try {
            UpdateSubjectsDialog dialog = new UpdateSubjectsDialog(this, dataManager, studentId);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    refreshData(); // Refresh when dialog closes
                }
            });
            dialog.setVisible(true);
        } catch (Exception e) {
            showErrorMessage("Error opening update dialog: " + e.getMessage());
        }
    }
    
    private void showPaymentHistoryDialog(String studentId, String studentName) {
        try {
            PaymentHistoryDialog dialog = new PaymentHistoryDialog(this, dataManager, studentId, studentName);
            dialog.setVisible(true);
        } catch (Exception e) {
            showErrorMessage("Error opening payment history: " + e.getMessage());
        }
    }
    
    private void showEditStudentProfileDialog(Student student) {
        try {
            EditStudentProfileDialog dialog = new EditStudentProfileDialog(this, dataManager, student);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    refreshData(); // Refresh when dialog closes
                }
            });
            dialog.setVisible(true);
        } catch (Exception e) {
            showErrorMessage("Error opening edit profile dialog: " + e.getMessage());
        }
    }
    
    // Message helper methods for consistent UI
    private void showErrorMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        });
    }
    
    private void showSuccessMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
        });
    }
    
    private void showWarningMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
        });
    }
    
    private int showConfirmDialog(String message, String title) {
        return JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION);
    }
}



/**
 * EditStudentProfileDialog - Fixed version with proper error handling
 */
class EditStudentProfileDialog extends JDialog {
    private DataManager dataManager;
    private ReceptionistDashboard parentFrame;
    private Student student;
    
    // Form fields with enhanced styling
    private JTextField nameField;
    private JTextField icField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea addressArea;
    private JTextField levelField;
    private JTextField enrollmentMonthField;
    private JTextArea subjectsArea;
    private JTextField balanceField;
    
    // Modern color scheme
private final Color PRIMARY_COLOR = new Color(59, 130, 246);    // Blue
private final Color SECONDARY_COLOR = new Color(243, 244, 246);  // Light gray
private final Color ACCENT_COLOR = new Color(16, 185, 129);      // Green
private final Color ERROR_COLOR = new Color(239, 68, 68);        // Red
private final Color TEXT_COLOR = new Color(31, 41, 55);          // Dark gray
private final Color BACKGROUND_COLOR = new Color(249, 250, 251); // Very light gray
    
    public EditStudentProfileDialog(ReceptionistDashboard parent, DataManager dataManager, Student student) {
        super(parent, "Edit Student Profile", true);
        this.parentFrame = parent;
        this.dataManager = dataManager;
        this.student = student;
        
        initializeComponents();
        setupLayout();
        loadStudentData();
        setupWindow();
    }
    
    private void initializeComponents() {
        // Enhanced text fields with better styling
        nameField = createStyledTextField();
        icField = createStyledTextField();
        emailField = createStyledTextField();
        phoneField = createStyledTextField();
        
        // Large address field for better input
        addressArea = createStyledTextArea(4, 30);
        
        // Read-only academic fields
        levelField = createStyledTextField();
        levelField.setEditable(false);
        levelField.setBackground(SECONDARY_COLOR);
        
        enrollmentMonthField = createStyledTextField();
        enrollmentMonthField.setEditable(false);
        enrollmentMonthField.setBackground(SECONDARY_COLOR);
        
        subjectsArea = createStyledTextArea(5, 30);
        subjectsArea.setEditable(false);
        subjectsArea.setBackground(SECONDARY_COLOR);
        
        balanceField = createStyledTextField();
        balanceField.setEditable(false);
        balanceField.setBackground(SECONDARY_COLOR);
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(30);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
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
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);
        
        // Main container with scroll support
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        // Title
        JLabel titleLabel = new JLabel("Edit Student Profile");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel studentInfo = new JLabel("Student: " + student.getName() + " (" + student.getStudentId() + ")");
        studentInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        studentInfo.setForeground(new Color(107, 114, 128));
        studentInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Form sections
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(studentInfo);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        mainPanel.add(createPersonalInfoPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(createAcademicInfoPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        mainPanel.add(createButtonsPanel());
        
        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createPersonalInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            "Personal Information", 0, 0,
            new Font("Segoe UI", Font.BOLD, 14), TEXT_COLOR));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(nameField, gbc);
        
        // IC
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(createLabel("IC/Passport:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(icField, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(createLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(emailField, gbc);
        
        // Phone
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panel.add(createLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(phoneField, gbc);
        
        // Address
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(createLabel("Address:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        panel.add(new JScrollPane(addressArea), gbc);
        
        return panel;
    }
    
    private JPanel createAcademicInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            "Academic Information (Read-Only)", 0, 0,
            new Font("Segoe UI", Font.BOLD, 14), TEXT_COLOR));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Level
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createLabel("Level:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(levelField, gbc);
        
        // Enrollment Month
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(createLabel("Enrollment Month:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(enrollmentMonthField, gbc);
        
        // Balance
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(createLabel("Balance:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(balanceField, gbc);
        
        // Subjects
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(createLabel("Subjects:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        panel.add(new JScrollPane(subjectsArea), gbc);
        
        return panel;
    }
    
    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setBackground(Color.WHITE);
        
        JButton saveButton = createButton("Save Changes", ACCENT_COLOR);
        JButton cancelButton = createButton("Cancel", SECONDARY_COLOR);
        
        saveButton.addActionListener(e -> saveChanges());
        cancelButton.addActionListener(e -> dispose());
        
        panel.add(saveButton);
        panel.add(cancelButton);
        
        return panel;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(TEXT_COLOR);
        label.setPreferredSize(new Dimension(120, 25));
        return label;
    }
    
    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(color.equals(SECONDARY_COLOR) ? TEXT_COLOR : Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private void loadStudentData() {
        nameField.setText(student.getName());
        icField.setText(student.getIc());
        emailField.setText(student.getEmail());
        phoneField.setText(student.getPhone());
        addressArea.setText(student.getAddress());
        levelField.setText(student.getLevel());
        enrollmentMonthField.setText(student.getEnrollmentMonth());
        balanceField.setText(String.format("RM %.2f", student.getTotalBalance()));
        
        // Load subjects with details
        StringBuilder subjectsText = new StringBuilder();
        List<String> classIds = student.getSubjects();
        if (classIds.isEmpty()) {
            subjectsText.append("No subjects enrolled");
        } else {
            subjectsText.append("Enrolled subjects (").append(classIds.size()).append("):\n\n");
            List<ClassInfo> allClasses = dataManager.getAllClasses();
            java.util.Map<String, ClassInfo> classMap = new java.util.HashMap<>();
            for (ClassInfo c : allClasses) {
                classMap.put(c.getClassId(), c);
            }
            
            for (String classId : classIds) {
                ClassInfo info = classMap.get(classId);
                if (info != null) {
                    subjectsText.append("• ").append(info.getSubject())
                               .append(" (").append(classId).append(")")
                               .append(" - RM").append(String.format("%.2f", info.getFee()))
                               .append("\n");
                } else {
                    subjectsText.append("• ").append(classId).append(" (Details not found)\n");
                }
            }
        }
        subjectsArea.setText(subjectsText.toString());
    }
    
    private void saveChanges() {
        // Get form data
        String name = nameField.getText().trim();
        String ic = icField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressArea.getText().trim();
        
        // Validate
        if (name.isEmpty() || ic.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            boolean success = dataManager.updateStudentProfile(student.getStudentId(), name, email, phone, address);
            
            if (success) {
                // Update student object
                student.setName(name);
                student.setIc(ic);
                student.setEmail(email);
                student.setPhone(phone);
                student.setAddress(address);
                
                JOptionPane.showMessageDialog(this, 
                    "✅ Student profile updated successfully!\n\n" +
                    "Student: " + name + "\n" +
                    "ID: " + student.getStudentId(), 
                    "Update Successful", JOptionPane.INFORMATION_MESSAGE);
                
                parentFrame.refreshData();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "❌ Error updating profile. Please try again.", 
                    "Update Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + ex.getMessage(), 
                "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void setupWindow() {
        setSize(650, 600);
        setLocationRelativeTo(parentFrame);
        setResizable(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(550, 500));
    }
}