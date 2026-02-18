import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * LoginGUI - Enhanced unified login interface for ATC Tuition Centre
 * Supports all user types: Students, Receptionists, Admins, Tutors
 * Integrated with enhanced AdminDashboard profile management
 */
public class LoginGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel;
    private JButton loginButton, resetButton;
    private UserHandler userHandler;
    private JLabel attemptsLabel;

    // Modern color scheme
    private final Color PRIMARY_COLOR   = new Color(59, 130, 246);    // Blue
    private final Color SECONDARY_COLOR = new Color(243, 244, 246);   // Light gray
    private final Color ACCENT_COLOR    = new Color(16, 185, 129);    // Green
    private final Color ERROR_COLOR     = new Color(239, 68, 68);     // Red
    private final Color TEXT_COLOR      = new Color(31, 41, 55);      // Dark gray
    private final Color BACKGROUND      = new Color(249, 250, 251);   // Very light gray

    public LoginGUI() {
        userHandler = new UserHandler();
        initComponents();
        buildLayout();
        bindEvents();
        setupWindow();
    }

    private void initComponents() {
        // Username field
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                new EmptyBorder(12, 16, 12, 16)));

        // Password field
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(usernameField.getBorder());

        // Login button
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(PRIMARY_COLOR);
        loginButton.setForeground(Color.WHITE);
        loginButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Reset button
        resetButton = new JButton("Reset");
        resetButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resetButton.setBackground(SECONDARY_COLOR);
        resetButton.setForeground(TEXT_COLOR);
        resetButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        resetButton.setFocusPainted(false);
        resetButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Error label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(ERROR_COLOR);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Attempts label
        attemptsLabel = new JLabel(" ");
        attemptsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        attemptsLabel.setForeground(new Color(107, 114, 128));
        attemptsLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void buildLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND);

        // Main panel with adjusted padding
        JPanel main = new JPanel(new GridBagLayout());
        main.setBorder(new EmptyBorder(30, 30, 30, 30)); // Reduced padding
        main.setBackground(BACKGROUND);

        // Login form panel
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
                new EmptyBorder(30, 30, 30, 30))); // Reduced padding

        // Header section
        JPanel headerPanel = createHeaderPanel();
        
        // Form fields section
        JPanel fieldsPanel = createFieldsPanel();
        
        // Buttons section
        JPanel buttonsPanel = createButtonsPanel();
        
        // Info section
        JPanel infoPanel = createInfoPanel();

        // Add all sections to form with adjusted spacing
        form.add(headerPanel);
        form.add(Box.createVerticalStrut(25)); // Reduced spacing
        form.add(fieldsPanel);
        form.add(Box.createVerticalStrut(15)); // Reduced spacing
        form.add(buttonsPanel);
        form.add(Box.createVerticalStrut(10)); // Reduced spacing
        form.add(errorLabel);
        form.add(attemptsLabel);
        form.add(Box.createVerticalStrut(15)); // Reduced spacing
        form.add(infoPanel);

        // Set maximum width for form panel
        form.setMaximumSize(new Dimension(450, form.getPreferredSize().height));

        main.add(form);
        add(main, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Logo/Icon - reduced size
        JLabel logoLabel = new JLabel("🎓");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40)); // Reduced from 48
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title - reduced size
        JLabel titleLabel = new JLabel("ATC Tuition Centre");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22)); // Reduced from 24
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle - reduced size
        JLabel subtitleLabel = new JLabel("Management System Login");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Reduced from 16
        subtitleLabel.setForeground(new Color(107, 114, 128));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(logoLabel);
        headerPanel.add(Box.createVerticalStrut(8)); // Reduced spacing
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(4)); // Reduced spacing
        headerPanel.add(subtitleLabel);
        
        return headerPanel;
    }
    
    private JPanel createFieldsPanel() {
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBackground(Color.WHITE);
        fieldsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username field
        addLabeledField(fieldsPanel, "ID", usernameField);
        
        // Password field
        addLabeledField(fieldsPanel, "Password", passwordField);
        
        return fieldsPanel;
    }
    
    private JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.add(loginButton);
        buttonsPanel.add(resetButton);
        return buttonsPanel;
    }
    
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        
        // Create info text with more compact content
        JTextArea infoText = new JTextArea(
            "Login Instructions:\n" +
            "• Students: Use your Student ID (e.g., STU001)\n" +
            "• Other: Use your ID (e.g., AD001, RC001)\n" +
            "• Default password for students: password123\n" +
            "• System supports: Students, Receptionists, Admins, Tutors"
        );
        infoText.setFont(new Font("Segoe UI", Font.PLAIN, 10)); // Reduced font size
        infoText.setForeground(new Color(107, 114, 128));
        infoText.setBackground(Color.WHITE);
        infoText.setEditable(false);
        infoText.setOpaque(false);
        infoText.setLineWrap(true);
        infoText.setWrapStyleWord(true);
        infoText.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(8, 8, 8, 8) // Reduced padding
        ));
        
        infoPanel.add(infoText);
        return infoPanel;
    }

    private void addLabeledField(JPanel container, String label, JTextField field) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        l.setForeground(TEXT_COLOR);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, field.getPreferredSize().height));

        container.add(l);
        container.add(Box.createVerticalStrut(5));
        container.add(field);
        container.add(Box.createVerticalStrut(15)); // Reduced spacing
    }

    private void bindEvents() {
        // Login button action
        loginButton.addActionListener(e -> handleLogin());
        
        // Reset button action
        resetButton.addActionListener(e -> resetForm());

        // Enter key listeners
        usernameField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    passwordField.requestFocus();
                }
            }
        });

        passwordField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        });
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // Basic validation
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both ID and password");
            return;
        }

        System.out.println("DEBUG: Login attempt - Username: " + username);

        // Attempt login
        LoginResult result = userHandler.authenticate(username, password);
        
        if (result.isSuccess()) {
            String userType = result.getUserType();
            User user = result.getUser();
            
            showSuccess("Login successful! Welcome, " + user.getName());
            System.out.println("DEBUG: Successful login - UserType: " + userType + 
                             ", UserID: " + user.getUserId());
            
            // Route to appropriate dashboard based on user type
            openDashboard(user, userType);
        } else {
            showError(result.getMessage());
            updateAttemptsLabel(username);
        }
    }

    private void resetForm() {
        usernameField.setText("");
        passwordField.setText("");
        errorLabel.setText(" ");
        attemptsLabel.setText(" ");
        usernameField.requestFocus();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setForeground(ERROR_COLOR);
    }

    private void showSuccess(String message) {
        errorLabel.setText(message);
        errorLabel.setForeground(ACCENT_COLOR);
    }

    private void updateAttemptsLabel(String username) {
        if (!userHandler.isAccountLocked(username)) {
            int remaining = userHandler.getRemainingAttempts(username);
            attemptsLabel.setText("Attempts remaining: " + remaining);
        } else {
            attemptsLabel.setText("Account locked - Contact administrator");
        }
    }

    private void openDashboard(User user, String userType) {
        // Hide login window and open appropriate dashboard
        this.setVisible(false);
        
        SwingUtilities.invokeLater(() -> {
            switch (userType) {
                case "STUDENT":
                    openStudentDashboard(user);
                    break;
                    
                case "RECEPTIONIST":
                    openReceptionistDashboard(user);
                    break;
                    
                case "ADMIN":
                    openAdminDashboard(user);
                    break;
                    
                case "TUTOR":
                    openTutorDashboard(user);
                    break;
                    
                default:
                    showErrorDialog("Unknown user type: " + userType);
                    this.setVisible(true);
            }
        });
    }
    
    private void openStudentDashboard(User user) {
        try {
            // Load complete student data
            Student student = Student.loadStudentsFromFile("students.txt")
                    .stream()
                    .filter(s -> s.getStudentId().equals(user.getUserId()))
                    .findFirst()
                    .orElseGet(() -> {
                        // Create student from user data if not found in detailed format
                        Student s = (Student) user;
                        return new Student(s.getUserId(), s.getIc(), s.getUsername(), 
                                         s.getPassword(), s.getName(), s.getEmail(), 
                                         s.getPhone(), s.getAddress(), s.getLevel(), 
                                         s.getEnrollmentMonth(), s.getSubjects(), s.getTotalBalance());
                    });
            
            new StudentPortal(student).setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Error opening student dashboard: " + e.getMessage());
            this.setVisible(true);
        }
    }
    
    private void openReceptionistDashboard(User user) {
        try {
            new ReceptionistDashboard(user).setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Error opening receptionist dashboard: " + e.getMessage());
            this.setVisible(true);
        }
    }
    
    private void openAdminDashboard(User user) {
        try {
            // Open the enhanced AdminDashboard directly
            new AdminDashboard(user).setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Error opening admin dashboard: " + e.getMessage());
            this.setVisible(true);
        }
    }
    
    private void openTutorDashboard(User user) {
        try {
            new TutorDashboard(user).setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Error opening tutor dashboard: " + e.getMessage());
            this.setVisible(true);
        }
    }
    
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void setupWindow() {
        setTitle("ATC Tuition Centre - Login Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // FIXED: Increased frame height significantly to show full content
        setSize(600, 800); // Changed from 650 to 800
        
        setLocationRelativeTo(null);
        setResizable(true); // Allow resizing so users can adjust if needed
        
        // Set minimum size to prevent content from being cut off
        setMinimumSize(new Dimension(500, 700));
        
        // Set application icon (if available)
        try {
            // setIconImage(new ImageIcon("icon.png").getImage());
        } catch (Exception e) {
            // Icon not found, continue without it
        }
        
        // Focus on username field when window opens
        SwingUtilities.invokeLater(() -> usernameField.requestFocus());
    }

    public static void main(String[] args) {
        // Set system look and feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Use default look and feel
            System.out.println("Could not set Nimbus look and feel, using default");
        }
        
        SwingUtilities.invokeLater(() -> {
            new LoginGUI().setVisible(true);
        });
    }
}