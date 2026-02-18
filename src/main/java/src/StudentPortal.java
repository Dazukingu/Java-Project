import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * StudentPortal - Enhanced with improved profile management and password change
 */
public class StudentPortal extends JFrame {

    // Modern color scheme
    private final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private final Color SECONDARY_COLOR = new Color(243, 244, 246);
    private final Color ACCENT_COLOR = new Color(16, 185, 129);
    private final Color ERROR_COLOR = new Color(239, 68, 68);
    private final Color TEXT_COLOR = new Color(31, 41, 55);
    private final Color BACKGROUND_COLOR = new Color(249, 250, 251);
    
    private static final Font FONT_MAIN = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 24);

    private final Student student;
    private JTabbedPane tabbedPane;
    private final Map<String, String> tutorMap = new HashMap<>();
    private DataManager dataManager;

    public StudentPortal(Student s) {
        super("ATC Tuition Centre - " + s.getName());
        this.student = s;
        this.dataManager = new DataManager();
        loadTutors();
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        buildUI();
    }

    private void loadTutors() {
        try (BufferedReader br = new BufferedReader(new FileReader("tutor.txt"))) {
            String l;
            while ((l = br.readLine()) != null) {
                String[] p = l.split(",");
                if (p.length >= 2) tutorMap.put(p[0], p[1]);
            }
        } catch (IOException ignored) {}
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Create tabbed pane with enhanced styling
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setTabPlacement(JTabbedPane.TOP);
        
        // Add tabs with icons
        tabbedPane.addTab("🏠 Dashboard", createDashboardPanel());
        tabbedPane.addTab("👤 My Profile", createEnhancedProfilePanel());
        tabbedPane.addTab("📅 Class Schedule", createSchedulePanel());
        tabbedPane.addTab("🔄 Subject Change", createChangePanel());
        tabbedPane.addTab("💳 Payment Status", createPaymentPanel());
        
        setupLayout();
    }
    
    private void setupLayout() {
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel logoLabel = new JLabel("🎓 ATC Tuition Centre - Student Portal");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logoLabel.setForeground(Color.WHITE);
        
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setBackground(PRIMARY_COLOR);
        
        JLabel userLabel = new JLabel("Welcome, " + student.getName());
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
            if (JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
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
        
        JLabel welcomeLabel = new JLabel("Welcome back, " + student.getName() + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(TEXT_COLOR);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel roleLabel = new JLabel("Student Dashboard - ATC Tuition Centre");
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
    
    // Get student statistics
    List<String> enrolledClasses = student.getSubjects();
    double totalFees = student.calculateTotalFees();
    double currentBalance = student.getTotalBalance();
    
    // NEW: Calculate outstanding based on monthly payment status
    String currentMonth = getCurrentMonth();
    boolean paidThisMonth = hasStudentPaidThisMonth(currentMonth);
    double outstandingAmount = paidThisMonth ? 0.0 : totalFees;
    
    // Count pending requests
    int pendingRequests = getPendingRequests();
    
    panel.add(createStatCard("🎓", "Student ID", student.getStudentId(), PRIMARY_COLOR));
    panel.add(createStatCard("📚", "Enrolled Subjects", String.valueOf(enrolledClasses.size()), ACCENT_COLOR));
    panel.add(createStatCard("💰", "Tuition Fees (Monthly)", String.format("RM %.2f", totalFees), new Color(245, 158, 11)));
    panel.add(createStatCard("💳", "Current Balance", String.format("RM %.2f", currentBalance), new Color(168, 85, 247)));
    
    // NEW: Dynamic outstanding card
    String outstandingTitle = paidThisMonth ? "Paid This Month" : "Outstanding";
    String outstandingIcon = paidThisMonth ? "✅" : "⚠️";
    Color outstandingColor = paidThisMonth ? ACCENT_COLOR : ERROR_COLOR;
    panel.add(createStatCard(outstandingIcon, outstandingTitle, String.format("RM %.2f", outstandingAmount), outstandingColor));
    
    panel.add(createStatCard("📋", "Pending Requests", String.valueOf(pendingRequests), new Color(99, 102, 241)));
    
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
        
        JButton profileBtn = createActionButton("Update Profile", "👤", e -> tabbedPane.setSelectedIndex(1));
        JButton scheduleBtn = createActionButton("View Schedule", "📅", e -> tabbedPane.setSelectedIndex(2));
        JButton changeBtn = createActionButton("Change Subject", "🔄", e -> tabbedPane.setSelectedIndex(3));
        JButton paymentBtn = createActionButton("Make Payment", "💳", e -> tabbedPane.setSelectedIndex(4));
        
        panel.add(profileBtn);
        panel.add(scheduleBtn);
        panel.add(changeBtn);
        panel.add(paymentBtn);
        
        return panel;
    }
    
    private JButton createActionButton(String text, String icon, java.awt.event.ActionListener action) {
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

    // Enhanced Profile Panel with tabbed interface
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
    
    // Academic Information tab
    profileTabs.addTab("🎓 Academic Info", createScrollableAcademicInfoPanel());
    
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
    
    // Profile form
    JPanel profileForm = createEditableProfileForm();
    
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
    JLabel titleLabel = new JLabel("Password & Security");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
    titleLabel.setForeground(PRIMARY_COLOR);
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    // Change password panel
    JPanel passwordPanel = createPasswordChangeFormPanel();
    
    // Security info panel with enhanced information
    JPanel securityInfoPanel = createEnhancedSecurityInfoPanel();
    
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

private JPanel createScrollableAcademicInfoPanel() {
    // Main scrollable container
    JPanel mainContainer = new JPanel(new BorderLayout());
    mainContainer.setBackground(BACKGROUND_COLOR);
    
    // Content panel that will be scrolled
    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    contentPanel.setBackground(Color.WHITE);
    contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
    
    // Title
    JLabel titleLabel = new JLabel("Academic Information");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
    titleLabel.setForeground(PRIMARY_COLOR);
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    // Academic details panel
    JPanel academicPanel = createAcademicDetailsPanel();
    
    contentPanel.add(titleLabel);
    contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
    contentPanel.add(academicPanel);
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

// Enhanced Security Info Panel for Students
private JPanel createEnhancedSecurityInfoPanel() {
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
    JLabel statusLabel = new JLabel("Active Student");
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
    JLabel typeLabel = new JLabel("Student Portal");
    typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    typeLabel.setForeground(new Color(107, 114, 128));
    panel.add(typeLabel, gbc);
    
    // Student Level
    gbc.gridx = 0; gbc.gridy = 4;
    panel.add(createFieldLabel("Student Level:"), gbc);
    gbc.gridx = 1;
    JLabel levelLabel = new JLabel(student.getLevel());
    levelLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
    levelLabel.setForeground(PRIMARY_COLOR);
    panel.add(levelLabel, gbc);
    
    // Enrollment Status
    gbc.gridx = 0; gbc.gridy = 5;
    panel.add(createFieldLabel("Enrollment Status:"), gbc);
    gbc.gridx = 1;
    JLabel enrollmentLabel = new JLabel();
    enrollmentLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
    int subjectCount = student.getSubjects().size();
    if (subjectCount >= 3) {
        enrollmentLabel.setForeground(ACCENT_COLOR);
        enrollmentLabel.setText("Full Enrollment (" + subjectCount + "/3 subjects)");
    } else if (subjectCount >= 1) {
        enrollmentLabel.setForeground(new Color(255, 193, 7));
        enrollmentLabel.setText("Partial Enrollment (" + subjectCount + "/3 subjects)");
    } else {
        enrollmentLabel.setForeground(ERROR_COLOR);
        enrollmentLabel.setText("No Enrollment (0/3 subjects)");
    }
    panel.add(enrollmentLabel, gbc);
    
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
    
    private JPanel createEditableProfileForm() {
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
        JTextField studentIdField = createStyledTextField();
        studentIdField.setText(student.getStudentId());
        studentIdField.setEditable(false);
        studentIdField.setBackground(SECONDARY_COLOR);
        
        JTextField nameField = createStyledTextField();
        nameField.setText(student.getName());
        
        JTextField icField = createStyledTextField();
        icField.setText(student.getIc());
        
        JTextField emailField = createStyledTextField();
        emailField.setText(student.getEmail());
        
        JTextField phoneField = createStyledTextField();
        phoneField.setText(student.getPhone());
        
        JTextArea addressArea = createStyledTextArea(3, 25);
        addressArea.setText(student.getAddress());
        
        // Layout form fields
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(createFieldLabel("Student ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(studentIdField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(createFieldLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(createFieldLabel("IC/Passport:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(icField, gbc);
        
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
        
        updateButton.addActionListener(e -> updatePersonalInfo(nameField, icField, emailField, phoneField, addressArea));
        resetButton.addActionListener(e -> {
            nameField.setText(student.getName());
            icField.setText(student.getIc());
            emailField.setText(student.getEmail());
            phoneField.setText(student.getPhone());
            addressArea.setText(student.getAddress());
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
            changePassword(currentPasswordField, newPasswordField, confirmPasswordField));
        
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
    JLabel statusLabel = new JLabel("Active Student");
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
    JLabel typeLabel = new JLabel("Student Portal");
    typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    typeLabel.setForeground(new Color(107, 114, 128));
    panel.add(typeLabel, gbc);
    
    // Student Level
    gbc.gridx = 0; gbc.gridy = 4;
    panel.add(createFieldLabel("Student Level:"), gbc);
    gbc.gridx = 1;
    JLabel levelLabel = new JLabel(student.getLevel());
    levelLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
    levelLabel.setForeground(PRIMARY_COLOR);
    panel.add(levelLabel, gbc);
    
    // Enrollment Status
    gbc.gridx = 0; gbc.gridy = 5;
    panel.add(createFieldLabel("Enrollment Status:"), gbc);
    gbc.gridx = 1;
    JLabel enrollmentLabel = new JLabel(getEnrollmentStatus());
    enrollmentLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
    if (student.getSubjects().size() >= 3) {
        enrollmentLabel.setForeground(ACCENT_COLOR);
        enrollmentLabel.setText("Full Enrollment (" + student.getSubjects().size() + "/3 subjects)");
    } else if (student.getSubjects().size() >= 1) {
        enrollmentLabel.setForeground(new Color(255, 193, 7));
        enrollmentLabel.setText("Partial Enrollment (" + student.getSubjects().size() + "/3 subjects)");
    } else {
        enrollmentLabel.setForeground(ERROR_COLOR);
        enrollmentLabel.setText("No Enrollment (0/3 subjects)");
    }
    panel.add(enrollmentLabel, gbc);
    
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
    
    private JPanel createAcademicInfoPanel() {
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
        JLabel titleLabel = new JLabel("Academic Information");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Academic details panel
        JPanel academicPanel = createAcademicDetailsPanel();
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        contentPanel.add(academicPanel);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createAcademicDetailsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1), 
                "Academic Details (Read-Only)", 0, 0, new Font("Segoe UI", Font.BOLD, 16), TEXT_COLOR),
            new EmptyBorder(25, 25, 25, 25)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Academic info fields (read-only)
        JTextField levelField = createStyledTextField();
        levelField.setText(student.getLevel());
        levelField.setEditable(false);
        levelField.setBackground(SECONDARY_COLOR);
        
        JTextField enrollmentField = createStyledTextField();
        enrollmentField.setText(student.getEnrollmentMonth());
        enrollmentField.setEditable(false);
        enrollmentField.setBackground(SECONDARY_COLOR);
        
        JTextArea subjectsArea = createStyledTextArea(4, 25);
        subjectsArea.setText(student.getSubjectsAsString());
        subjectsArea.setEditable(false);
        subjectsArea.setBackground(SECONDARY_COLOR);
        
        JTextField balanceField = createStyledTextField();
        balanceField.setText(String.format("RM %.2f", student.getTotalBalance()));
        balanceField.setEditable(false);
        balanceField.setBackground(SECONDARY_COLOR);
        
        // Layout academic fields
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(createFieldLabel("Academic Level:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(levelField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(createFieldLabel("Enrollment Month:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(enrollmentField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(createFieldLabel("Enrolled Subjects:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        panel.add(new JScrollPane(subjectsArea), gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(createFieldLabel("Account Balance:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(balanceField, gbc);
        
        return panel;
    }
    
    // Helper methods for enhanced profile functionality
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

    private boolean hasStudentPaidThisMonth(String currentMonth) {
    try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("payment_history.txt"))) {
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 4 && parts[0].trim().equals(student.getStudentId())) {
                // Check payment month (new format has month at index 5)
                String paymentMonth = parts.length > 5 ? parts[5] : getCurrentMonth();
                if (currentMonth.equals(paymentMonth)) {
                    return true;
                }
            }
        }
    } catch (java.io.IOException e) {
        System.err.println("Error checking payment history: " + e.getMessage());
    }
    return false;
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
    
    private void updatePersonalInfo(JTextField nameField, JTextField icField, JTextField emailField, 
                                   JTextField phoneField, JTextArea addressArea) {
        String name = nameField.getText().trim();
        String ic = icField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressArea.getText().trim();
        
        // Validation
        if (name.isEmpty() || ic.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
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
        
        // Update student profile
        student.updateProfile(name, email, phone, address);
        student.setIc(ic);
        
        JOptionPane.showMessageDialog(this, 
            "✅ Profile updated successfully!\n\n" +
            "Name: " + name + "\n" +
            "Email: " + email + "\n" +
            "Phone: " + phone, 
            "Update Successful", JOptionPane.INFORMATION_MESSAGE);
        
        // Refresh dashboard
        tabbedPane.setComponentAt(0, createDashboardPanel());
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
        
        if (!currentPassword.equals(student.getPassword())) {
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
        student.setPassword(newPassword);
        updateStudentInFile();
        
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
    
    private void updateStudentInFile() {
        try {
            File file = new File("students.txt");
            List<String> lines = new ArrayList<>();
            
            // Read all lines
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }
            
            // Update the student's line
            for (int i = 0; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if (parts.length >= 1 && parts[0].equals(student.getStudentId())) {
                    // Reconstruct the line with updated information
                    String updatedLine = String.join(",",
                        student.getStudentId(),
                        student.getIc(),
                        student.getPassword(),
                        student.getName(),
                        student.getEmail(),
                        student.getPhone(),
                        student.getAddress(),
                        student.getLevel(),
                        student.getEnrollmentMonth(),
                        student.getSubjectsString()
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

    // Create the remaining panels (simplified for this enhanced version)
    private JPanel createSchedulePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BACKGROUND_COLOR);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel title = createTitle("Class Schedule");
        p.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BACKGROUND_COLOR);
        content.add(Box.createVerticalStrut(20));

        List<ClassInfo> list = getEnrolledClasses();
        if (list.isEmpty()) {
            JPanel empty = createEmptyPanel("No classes found for your enrolled subjects.\n\nPlease contact the receptionist to enroll in classes.");
            content.add(empty);
        } else {
            for (ClassInfo c : list) {
                JPanel classCard = createClassCard(c);
                content.add(classCard);
                content.add(Box.createVerticalStrut(15));
            }
        }

        JScrollPane sp = new JScrollPane(content);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private JPanel createClassCard(ClassInfo classInfo) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel subjectTitle = new JLabel(classInfo.getSubject());
        subjectTitle.setFont(FONT_BOLD.deriveFont(18f));
        subjectTitle.setForeground(PRIMARY_COLOR);
        card.add(subjectTitle);
        card.add(Box.createVerticalStrut(10));

        card.add(createDetailRow("Class ID:", classInfo.getClassId()));
        card.add(createDetailRow("Tutor:", tutorMap.getOrDefault(classInfo.getTutorId(), "Unknown")));
        card.add(createDetailRow("Description:", classInfo.getDescription()));
        card.add(createDetailRow("Schedule:", formatDates(classInfo.getSchedule())));
        card.add(createDetailRow("Fee:", "RM" + String.format("%.2f", classInfo.getFee())));
        
        return card;
    }

    private JPanel createDetailRow(String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
        row.setOpaque(false);
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(FONT_MAIN.deriveFont(Font.BOLD));
        labelComp.setPreferredSize(new Dimension(100, 20));
        labelComp.setForeground(TEXT_COLOR);
        
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(FONT_MAIN);
        valueComp.setForeground(TEXT_COLOR);
        
        row.add(labelComp);
        row.add(Box.createHorizontalStrut(10));
        row.add(valueComp);
        return row;
    }

    private String formatDates(String dates) {
        return dates == null || dates.isEmpty() ? "Not scheduled" : dates.replace("; ", ", ");
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

/**
 * Gets enrollment status for students
 */
    private String getEnrollmentStatus() {
    int subjectCount = student.getSubjects().size();
    if (subjectCount >= 3) {
        return "Full Enrollment";
    } else if (subjectCount >= 1) {
        return "Partial Enrollment";
    } else {
        return "No Enrollment";
    }
}
    private JPanel createChangePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BACKGROUND_COLOR);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel title = createTitle("Request Subject Change");
        p.add(title, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(BACKGROUND_COLOR);
        mainContent.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(25, 25, 25, 25)));
        formPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10);
        g.fill = GridBagConstraints.HORIZONTAL;

        // Current enrolled subjects
        List<String> enrolledNames = student.getSubjects().stream()
                .map(this::subjectNameFromId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        JComboBox<String> curBox = new JComboBox<>(enrolledNames.toArray(new String[0]));
        curBox.setFont(FONT_MAIN);

        // Available non-enrolled subjects
        List<String> allNames = student.getAllSubjectsForForm();
        List<String> otherNames = allNames.stream()
                .filter(n -> !enrolledNames.contains(n))
                .collect(Collectors.toList());
        JComboBox<String> newBox = new JComboBox<>(otherNames.toArray(new String[0]));
        newBox.setFont(FONT_MAIN);
        if (otherNames.isEmpty()) newBox.setEnabled(false);

        JButton submitBtn = createStyledButton("Submit Request", ACCENT_COLOR);

        g.gridx = 0; g.gridy = 0;
        formPanel.add(createLabel("Current Subject:"), g);
        g.gridx = 1;
        formPanel.add(curBox, g);

        g.gridx = 0; g.gridy = 1;
        formPanel.add(createLabel("Change to:"), g);
        g.gridx = 1;
        formPanel.add(newBox, g);

        g.gridx = 0; g.gridy = 2; g.gridwidth = 2;
        g.insets = new Insets(20, 10, 10, 10);
        formPanel.add(submitBtn, g);

        // Existing requests panel
        JPanel requestsPanel = new JPanel();
        requestsPanel.setLayout(new BoxLayout(requestsPanel, BoxLayout.Y_AXIS));
        requestsPanel.setBackground(Color.WHITE);
        requestsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1), 
                "Pending Requests", 0, 0, new Font("Segoe UI", Font.BOLD, 14), TEXT_COLOR),
            new EmptyBorder(15, 15, 15, 15)));
        
        reloadRequestList(requestsPanel);

        JScrollPane scroll = new JScrollPane(requestsPanel);
        scroll.setPreferredSize(new Dimension(500, 200));
        scroll.setBorder(null);

        mainContent.add(formPanel, BorderLayout.NORTH);
        mainContent.add(Box.createVerticalStrut(20), BorderLayout.CENTER);
        mainContent.add(scroll, BorderLayout.SOUTH);

        p.add(mainContent, BorderLayout.CENTER);

        submitBtn.addActionListener(e -> {
            String curDisplay = (String) curBox.getSelectedItem();
            String newDisplay = (String) newBox.getSelectedItem();
            if (newDisplay == null) {
                JOptionPane.showMessageDialog(p, "Please select a new subject.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String newId = classIdFromSubjectName(newDisplay);
            if (newId == null) {
                JOptionPane.showMessageDialog(p, "Cannot resolve class ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String curId = classIdFromSubjectName(curDisplay);
            student.requestSubjectChange(curId, newId);
            reloadRequestList(requestsPanel);
        });

        return p;
    }

    private void reloadRequestList(JPanel listPanel) {
        listPanel.removeAll();
        List<String> requests = FileHandler.SubjectChangeUtil.getPendingRequestsForStudent(student.getStudentId());
    
    if (requests.isEmpty()) {
        JLabel noRequests = createLabel("No pending requests found.");
        noRequests.setForeground(new Color(107, 114, 128));
        listPanel.add(noRequests);
    } else {
        for (String req : requests) {
            String[] p = req.split(",");
            if (p.length >= 5) {
                String reqId = p[0];
                String currentClassId = p[2];
                String newClassId = p[3];
                String status = p[4];
                
                JPanel row = new JPanel(new BorderLayout());
                row.setBackground(Color.WHITE);
                row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
                    new EmptyBorder(10, 15, 10, 15)));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
                
                // Enhanced request display with more details
                JPanel infoPanel = new JPanel();
                infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
                infoPanel.setBackground(Color.WHITE);
                
                JLabel requestLabel = new JLabel("Request " + reqId + " - " + status);
                requestLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                requestLabel.setForeground(TEXT_COLOR);
                
                JLabel detailLabel = new JLabel("From: " + getSubjectNameFromId(currentClassId) + 
                                               " → To: " + getSubjectNameFromId(newClassId));
                detailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                detailLabel.setForeground(new Color(107, 114, 128));
                
                infoPanel.add(requestLabel);
                infoPanel.add(detailLabel);
                
                row.add(infoPanel, BorderLayout.CENTER);
                
                if ("Pending".equalsIgnoreCase(status)) {
                    JButton del = createStyledButton("Delete", ERROR_COLOR);
                    del.setPreferredSize(new Dimension(80, 30));
                    del.addActionListener(e -> {
                        int confirm = JOptionPane.showConfirmDialog(this, 
                            "Are you sure you want to delete this request?\n\n" +
                            "Request: " + reqId + "\n" +
                            "From: " + getSubjectNameFromId(currentClassId) + "\n" +
                            "To: " + getSubjectNameFromId(newClassId), 
                            "Confirm Delete", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            // FIXED: Use FileHandler.SubjectChangeUtil
                            boolean ok = FileHandler.SubjectChangeUtil
                                    .deletePendingRequest(student.getStudentId(), reqId);
                            if (ok) {
                                reloadRequestList(listPanel);
                                JOptionPane.showMessageDialog(this, 
                                    "✅ Request deleted successfully!\n\nRequest " + reqId + " has been removed.", 
                                    "Success", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(this,
                                    "❌ Delete failed!\n\nRequest not found or already processed.", 
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    });
                    row.add(del, BorderLayout.EAST);
                } else {
                    // Show status label for non-pending requests
                    JLabel statusLabel = new JLabel(status);
                    statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    if ("Approved".equalsIgnoreCase(status)) {
                        statusLabel.setForeground(ACCENT_COLOR);
                    } else if ("Rejected".equalsIgnoreCase(status)) {
                        statusLabel.setForeground(ERROR_COLOR);
                    }
                    row.add(statusLabel, BorderLayout.EAST);
                }
                
                listPanel.add(row);
                listPanel.add(Box.createVerticalStrut(5));
            }
        }
    }
    listPanel.revalidate();
    listPanel.repaint();
}

    private JPanel createPaymentPanel() {
    JPanel p = new JPanel(new BorderLayout());
    p.setBackground(BACKGROUND_COLOR);
    p.setBorder(new EmptyBorder(20, 20, 20, 20));

    JLabel title = createTitle("Payment Status");
    p.add(title, BorderLayout.NORTH);

    JPanel mainContent = new JPanel(new BorderLayout());
    mainContent.setBackground(BACKGROUND_COLOR);
    mainContent.setBorder(new EmptyBorder(20, 0, 0, 0));

    // NEW: Enhanced payment status with monthly tracking
    JTextArea ta = new JTextArea(getEnhancedPaymentStatus());
    ta.setEditable(false);
    ta.setFont(new Font("Monospaced", Font.PLAIN, 13));
    ta.setBackground(Color.WHITE);
    ta.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
        new EmptyBorder(15, 15, 15, 15)));

    JScrollPane scrollPane = new JScrollPane(ta);
    scrollPane.setBorder(null);

    JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    bottom.setBackground(BACKGROUND_COLOR);
    
    // NEW: Dynamic button text based on payment status
    String currentMonth = getCurrentMonth();
    boolean paidThisMonth = hasStudentPaidThisMonth(currentMonth);
    
    JButton paymentBtn = createStyledButton(
        paidThisMonth ? "Additional Payment" : "Make Payment", 
        paidThisMonth ? SECONDARY_COLOR : ACCENT_COLOR
    );
    paymentBtn.addActionListener(e -> handleTopUp());
    bottom.add(paymentBtn);

    mainContent.add(scrollPane, BorderLayout.CENTER);
    mainContent.add(bottom, BorderLayout.SOUTH);

    p.add(mainContent, BorderLayout.CENTER);
    return p;
}

    private void handleTopUp() {
    String currentMonth = getCurrentMonth();
    boolean alreadyPaid = hasStudentPaidThisMonth(currentMonth);
    double monthlyFees = student.calculateTotalFees();
    
    String dialogTitle = alreadyPaid ? "Additional Payment" : "Monthly Payment";
    String dialogMessage = alreadyPaid ? 
        "You have already paid for " + formatMonth(currentMonth) + ".\nProcess additional payment?" :
        "Pay monthly fees for " + formatMonth(currentMonth);
    
    if (alreadyPaid) {
        int confirm = JOptionPane.showConfirmDialog(this, dialogMessage, dialogTitle, JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
    }

    JTextField txtAmount = new JTextField(String.format("%.2f", monthlyFees), 10);
    JComboBox<String> cmbMethod = new JComboBox<>(new String[]{"Cash", "Online Banking", "Credit Card", "Debit Card"});

    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBackground(Color.WHITE);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.anchor = GridBagConstraints.WEST;

    gbc.gridx = 0; gbc.gridy = 0;
    panel.add(createLabel("Payment Month:"), gbc);
    gbc.gridx = 1;
    panel.add(createLabel(formatMonth(currentMonth)), gbc);

    gbc.gridx = 0; gbc.gridy = 1;
    panel.add(createLabel("Monthly Fees:"), gbc);
    gbc.gridx = 1;
    panel.add(createLabel("RM " + String.format("%.2f", monthlyFees)), gbc);

    gbc.gridx = 0; gbc.gridy = 2;
    panel.add(createLabel("Payment Amount (RM):"), gbc);
    gbc.gridx = 1;
    panel.add(txtAmount, gbc);

    gbc.gridx = 0; gbc.gridy = 3;
    panel.add(createLabel("Payment Method:"), gbc);
    gbc.gridx = 1;
    panel.add(cmbMethod, gbc);

    int ok = JOptionPane.showConfirmDialog(this, panel, dialogTitle, JOptionPane.OK_CANCEL_OPTION);
    if (ok != JOptionPane.OK_OPTION) return;

    try {
        double amount = Double.parseDouble(txtAmount.getText().trim());
        if (amount <= 0) {
            JOptionPane.showMessageDialog(this, "Please enter a positive amount.", "Invalid Amount", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String classes = String.join(";", student.getSubjects());
        String method = ((String) cmbMethod.getSelectedItem()).toLowerCase().replace(" ", "");

        // NEW: Record payment with month tracking
        String record = String.join(",",
                student.getStudentId(),
                student.getName(),
                classes,
                String.format("%.2f", amount),
                method,
                currentMonth  // NEW: Add current month
        );

        try (java.io.FileWriter fw = new java.io.FileWriter("payment_history.txt", true)) {
            fw.write(record + "\n");
        }

        // Update balance
        double newBalance = student.getTotalBalance() + amount;
        student.topUpBalance(newBalance);

        // Show confirmation
        String message = "✅ Payment recorded successfully!\n\n";
        message += "📅 Paid for: " + formatMonth(currentMonth) + "\n";
        message += "💰 Amount: RM" + String.format("%.2f", amount) + "\n";
        
        if (!alreadyPaid) {
            message += "🎉 Your monthly fees are now up to date!";
        } else {
            message += "💳 Additional payment processed.";
        }
        
        JOptionPane.showMessageDialog(this, message, "Payment Successful", JOptionPane.INFORMATION_MESSAGE);

        // Refresh the payment panel and dashboard
        tabbedPane.setComponentAt(4, createPaymentPanel());
        tabbedPane.setComponentAt(0, createDashboardPanel());

    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
    } catch (java.io.IOException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error recording payment: " + ex.getMessage(), "Payment Error", JOptionPane.ERROR_MESSAGE);
    }
}

    // Helper methods
    private List<ClassInfo> getEnrolledClasses() {
        List<ClassInfo> out = new ArrayList<>();
        Set<String> enrolledIds = new HashSet<>(student.getSubjects());
        
        List<ClassInfo> allClasses = dataManager.getAllClasses();
        for (ClassInfo classInfo : allClasses) {
            if (enrolledIds.contains(classInfo.getClassId())) {
                out.add(classInfo);
            }
        }
        
        return out;
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
        label.setPreferredSize(new Dimension(140, 25));
        return label;
    }

    private JLabel createTitle(String text) {
        JLabel title = new JLabel(text);
        title.setFont(FONT_HEADER);
        title.setForeground(TEXT_COLOR);
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        return title;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_MAIN);
        label.setForeground(TEXT_COLOR);
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
    
    private JPanel createEmptyPanel(String message) {
        JPanel empty = new JPanel();
        empty.setBackground(Color.WHITE);
        empty.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
                new EmptyBorder(40, 40, 40, 40)));
        empty.setLayout(new BoxLayout(empty, BoxLayout.Y_AXIS));
        
        JTextArea messageArea = new JTextArea(message);
        messageArea.setFont(FONT_MAIN);
        messageArea.setForeground(new Color(107, 114, 128));
        messageArea.setBackground(Color.WHITE);
        messageArea.setEditable(false);
        messageArea.setOpaque(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        empty.add(Box.createVerticalGlue());
        empty.add(messageArea);
        empty.add(Box.createVerticalGlue());
        return empty;
    }

    private String getCurrentMonth() {
    java.time.LocalDate now = java.time.LocalDate.now();
    return String.format("%04d-%02d", now.getYear(), now.getMonthValue());
}

private String getSubjectNameFromId(String classId) {
    if (classId == null || classId.isEmpty()) return "Unknown";
    
    try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("class.txt"))) {
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 3 && parts[0].equals(classId)) {
                return parts[2]; // Return subject name
            }
        }
    } catch (java.io.IOException ignored) {}
    return classId; // Return class ID if subject name not found
}

private String generateNextRequestId() {
    int max = 0;
    try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("Subject_Change_Requests.txt"))) {
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length > 0 && parts[0].startsWith("REQ")) {
                try {
                    int num = Integer.parseInt(parts[0].substring(3));
                    max = Math.max(max, num);
                } catch (NumberFormatException ignored) {}
            }
        }
    } catch (java.io.IOException ignored) {}
    return String.format("REQ%03d", max + 1);
}

    private String formatMonth(String month) {
    try {
        String[] parts = month.split("-");
        int year = Integer.parseInt(parts[0]);
        int monthNum = Integer.parseInt(parts[1]);
        
        String[] monthNames = {"", "January", "February", "March", "April", "May", "June",
                             "July", "August", "September", "October", "November", "December"};
        
        return monthNames[monthNum] + " " + year;
    } catch (Exception e) {
        return month;
    }
}

    private String subjectNameFromId(String id) {
        List<ClassInfo> allClasses = dataManager.getAllClasses();
        for (ClassInfo classInfo : allClasses) {
            if (classInfo.getClassId().equals(id)) {
                return classInfo.getSubject();
            }
        }
        return id; // Return ID if name not found
    }

private String classIdFromSubjectName(String subjectName) {
    if (subjectName == null || subjectName.isEmpty()) return null;
    
    try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("class.txt"))) {
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 3 && parts[2].trim().equalsIgnoreCase(subjectName.trim())) {
                return parts[0]; // Return class ID
            }
        }
    } catch (java.io.IOException ignored) {}
    return null;
}

    private String getEnhancedPaymentStatus() {
    StringBuilder sb = new StringBuilder("=== MONTHLY PAYMENT STATUS ===\n\n");
    
    String currentMonth = getCurrentMonth();
    double monthlyFees = student.calculateTotalFees();
    boolean paidThisMonth = hasStudentPaidThisMonth(currentMonth);
    
    // Current month status
    sb.append("📅 Current Month: ").append(formatMonth(currentMonth)).append("\n");
    sb.append("💰 Monthly Fees: RM").append(String.format("%.2f", monthlyFees)).append("\n");
    
    if (paidThisMonth) {
        sb.append("✅ Status: PAID - No payment required this month\n");
        sb.append("🎉 Your fees are up to date!\n\n");
    } else {
        sb.append("⚠️ Status: OUTSTANDING\n");
        sb.append("💳 Amount Due: RM").append(String.format("%.2f", monthlyFees)).append("\n\n");
    }
    
    // Payment history
    sb.append("📋 PAYMENT HISTORY:\n");
    sb.append("=".repeat(50)).append("\n");
    
    boolean foundPayments = false;
    try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("payment_history.txt"))) {
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 4 && parts[0].trim().equals(student.getStudentId())) {
                foundPayments = true;
                String paymentMonth = parts.length > 5 ? parts[5] : "Unknown";
                
                sb.append("📄 Payment Record\n");
                sb.append("   Month: ").append(formatMonth(paymentMonth)).append("\n");
                sb.append("   Student: ").append(parts[1]).append("\n");
                sb.append("   Classes: ").append(parts[2].replace(";", ", ")).append("\n");
                sb.append("   Amount: RM").append(parts[3]).append("\n");
                sb.append("   Method: ").append(parts.length > 4 ? parts[4] : "Cash").append("\n");
                sb.append("   Status: Completed\n");
                sb.append("-".repeat(40)).append("\n");
            }
        }
    } catch (java.io.IOException ex) {
        ex.printStackTrace();
    }
    
    if (!foundPayments) {
        sb.append("No payment records found.\n");
    }
    
    return sb.toString();
}
    
    private int getPendingRequests() {
        return UserHandler.SubjectChangeUtil.getPendingRequestsForStudent(student.getStudentId()).size();
    }
    
}

