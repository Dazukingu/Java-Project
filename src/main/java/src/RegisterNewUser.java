import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * RegisterNewUser - Dialog for registering new Tutors, Receptionists, and Admins
 * Follows the exact data structure from the text files
 */
public class RegisterNewUser extends JDialog {
    private DataManager dataManager;
    private JFrame parentFrame;
    private String userType;
    private boolean registrationSuccessful = false;
    
    // UI Components
    private JTextField tutorIdField, nameField, passwordField, emailField, phoneField, dobField;
    private JTextField usernameField, roleField;
    
    // Modern color scheme
    private final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private final Color SECONDARY_COLOR = new Color(243, 244, 246);
    private final Color ACCENT_COLOR = new Color(16, 185, 129);
    private final Color ERROR_COLOR = new Color(239, 68, 68);
    private final Color TEXT_COLOR = new Color(31, 41, 55);
    private final Color BACKGROUND_COLOR = new Color(249, 250, 251);
    
    public RegisterNewUser(JFrame parent, DataManager dataManager, String userType) {
        super(parent, "Register New " + userType, true);
        this.parentFrame = parent;
        this.dataManager = dataManager;
        this.userType = userType;
        
        initializeComponents();
        setupLayout();
        setupWindow();
    }
    
    private void initializeComponents() {
        // Common fields for all user types
        tutorIdField = createStyledTextField();
        tutorIdField.setText(generateNextId());
        tutorIdField.setEditable(false);
        tutorIdField.setBackground(SECONDARY_COLOR);
        
        nameField = createStyledTextField();
        passwordField = createStyledTextField();
        emailField = createStyledTextField();
        phoneField = createStyledTextField();
        
        // User type specific fields
        if (userType.equals("TUTOR")) {
            dobField = createStyledTextField();
            dobField.setToolTipText("Format: DD-MM-YYYY");
        } else {
            usernameField = createStyledTextField();
        }
        
        if (userType.equals("ADMIN")) {
            roleField = createStyledTextField();
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main form - wrapped in scroll pane
        JPanel mainPanel = createMainPanel();
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));
        
        JLabel titleLabel = new JLabel("Register New " + userType);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Enter " + userType.toLowerCase() + " information");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(220, 230, 255));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);
        
        panel.add(titlePanel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                userType + " Information", 0, 0, new Font("Segoe UI", Font.BOLD, 14), TEXT_COLOR),
            new EmptyBorder(30, 30, 30, 30) // Increased padding
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12); // Increased spacing
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // ID field (auto-generated) - bigger
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(createFieldLabel(userType + " ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        tutorIdField.setPreferredSize(new Dimension(300, 35)); // Bigger text field
        panel.add(tutorIdField, gbc);
        row++;
        
        // Username field (for Receptionist and Admin only) - bigger
        if (!userType.equals("TUTOR")) {
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
            panel.add(createFieldLabel("Username:"), gbc);
            gbc.gridx = 1; gbc.weightx = 1.0;
            usernameField.setPreferredSize(new Dimension(300, 35));
            panel.add(usernameField, gbc);
            row++;
        }
        
        // Password field - bigger
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(createFieldLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        passwordField.setPreferredSize(new Dimension(300, 35));
        panel.add(passwordField, gbc);
        row++;
        
        // Name field - bigger
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(createFieldLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        nameField.setPreferredSize(new Dimension(300, 35));
        panel.add(nameField, gbc);
        row++;
        
        // Email field - bigger
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(createFieldLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        emailField.setPreferredSize(new Dimension(300, 35));
        panel.add(emailField, gbc);
        row++;
        
        // Date of Birth (for Tutor) OR Phone (for others) - bigger
        if (userType.equals("TUTOR")) {
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
            panel.add(createFieldLabel("Date of Birth:"), gbc);
            gbc.gridx = 1; gbc.weightx = 1.0;
            dobField.setPreferredSize(new Dimension(300, 35));
            panel.add(dobField, gbc);
            row++;
        }
        
        // Phone field - bigger
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(createFieldLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        phoneField.setPreferredSize(new Dimension(300, 35));
        panel.add(phoneField, gbc);
        row++;
        
        // role field (for Admin only) - bigger
        if (userType.equals("ADMIN")) {
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
            panel.add(createFieldLabel("Role:"), gbc);
            gbc.gridx = 1; gbc.weightx = 1.0;
            roleField.setPreferredSize(new Dimension(300, 35));
            panel.add(roleField, gbc);
            row++;
        }
        
        // Add some extra space at the bottom
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panel.setBackground(BACKGROUND_COLOR);
        
        JButton registerButton = createStyledButton("✅ Register " + userType, ACCENT_COLOR);
        JButton cancelButton = createStyledButton("❌ Cancel", SECONDARY_COLOR);
        
        registerButton.addActionListener(e -> registerUser());
        cancelButton.addActionListener(e -> dispose());
        
        panel.add(registerButton);
        panel.add(cancelButton);
        
        return panel;
    }
    
    private void registerUser() {
        try {
            // Validation
            if (!validateFields()) {
                return;
            }
            
            boolean success = false;
            String userId = tutorIdField.getText().trim();
            String name = nameField.getText().trim();
            String password = passwordField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            
            switch (userType) {
                case "TUTOR":
                    success = registerTutor(userId, name, password, email, phone, dobField.getText().trim());
                    break;
                case "RECEPTIONIST":
                    success = registerReceptionist(userId, usernameField.getText().trim(), password, name, email, phone);
                    break;
                case "ADMIN":
                    success = registerAdmin(userId, usernameField.getText().trim(), password, name, email, phone, roleField.getText().trim());
                    break;
            }
            
            if (success) {
                registrationSuccessful = true;
                JOptionPane.showMessageDialog(this, 
                    userType + " '" + name + "' registered successfully!", 
                    "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to register " + userType.toLowerCase() + ". Please try again.", 
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error during registration: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private boolean registerTutor(String tutorId, String name, String password, String email, String phone, String dob) {
        try {
            // Format: TC001,Alice,pass123,alice@gmail.com,06-06-1998,0123552843
            String newLine = String.format("%s,%s,%s,%s,%s,%s",
                tutorId, name, password, email, dob, phone);
            
            // Append to tutor.txt
            try (FileWriter fw = new FileWriter("tutor.txt", true)) {
                fw.write(newLine + "\n");
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean registerReceptionist(String receptionistId, String username, String password, String name, String email, String phone) {
        try {
            // Format: RC001,sarah,sarah123,Sarah Johnson,sarah@atc.edu.my,0123456789
            String newLine = String.format("%s,%s,%s,%s,%s,%s",
                receptionistId, username, password, name, email, phone);
            
            // Append to receptionist.txt
            try (FileWriter fw = new FileWriter("receptionist.txt", true)) {
                fw.write(newLine + "\n");
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean registerAdmin(String adminId, String username, String password, String name, String email, String phone, String role) {
        try {
            // Format: AD001,admin,admin123,Administrator,admin@atc.edu.my,0123456790
            String newLine = String.format("%s,%s,%s,%s,%s,%s",
                adminId, username, password, name, email, phone);
            
            // Append to admin.txt
            try (FileWriter fw = new FileWriter("admin.txt", true)) {
                fw.write(newLine + "\n");
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean validateFields() {
        // Basic validation
        if (nameField.getText().trim().isEmpty()) {
            showValidationError("Name cannot be empty!");
            nameField.requestFocus();
            return false;
        }
        
        if (passwordField.getText().trim().isEmpty()) {
            showValidationError("Password cannot be empty!");
            passwordField.requestFocus();
            return false;
        }
        
        if (passwordField.getText().trim().length() < 4) {
            showValidationError("Password must be at least 4 characters long!");
            passwordField.requestFocus();
            return false;
        }
        
        if (emailField.getText().trim().isEmpty() || !emailField.getText().contains("@")) {
            showValidationError("Please enter a valid email role!");
            emailField.requestFocus();
            return false;
        }
        
        if (phoneField.getText().trim().isEmpty()) {
            showValidationError("Phone number cannot be empty!");
            phoneField.requestFocus();
            return false;
        }
        
        // Username validation for non-tutors
        if (!userType.equals("TUTOR")) {
            if (usernameField.getText().trim().isEmpty()) {
                showValidationError("Username cannot be empty!");
                usernameField.requestFocus();
                return false;
            }
            
            // Check if username already exists
            if (isUsernameExists(usernameField.getText().trim())) {
                showValidationError("Username already exists! Please choose a different username.");
                usernameField.requestFocus();
                return false;
            }
        }
        
        // Date of birth validation for tutors
        if (userType.equals("TUTOR")) {
            if (dobField.getText().trim().isEmpty()) {
                showValidationError("Date of birth cannot be empty!");
                dobField.requestFocus();
                return false;
            }
            
            // Basic date format validation (DD-MM-YYYY)
            String dob = dobField.getText().trim();
            if (!dob.matches("\\d{2}-\\d{2}-\\d{4}")) {
                showValidationError("Date of birth must be in DD-MM-YYYY format!");
                dobField.requestFocus();
                return false;
            }
        }
        
        // Check if ID already exists
        if (isIdExists(tutorIdField.getText().trim())) {
            showValidationError("ID already exists! Please refresh and try again.");
            return false;
        }
        
        return true;
    }
    
    private boolean isUsernameExists(String username) {
        String filename = userType.equals("RECEPTIONIST") ? "receptionist.txt" : "admin.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[1].trim().equalsIgnoreCase(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            // File doesn't exist, username is available
        }
        return false;
    }
    
    private boolean isIdExists(String id) {
        String filename;
        switch (userType) {
            case "TUTOR":
                filename = "tutor.txt";
                break;
            case "RECEPTIONIST":
                filename = "receptionist.txt";
                break;
            case "ADMIN":
                filename = "admin.txt";
                break;
            default:
                return false;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].trim().equals(id)) {
                    return true;
                }
            }
        } catch (IOException e) {
            // File doesn't exist, ID is available
        }
        return false;
    }
    
    private String generateNextId() {
        String prefix;
        String filename;
        
        switch (userType) {
            case "TUTOR":
                prefix = "TC";
                filename = "tutor.txt";
                break;
            case "RECEPTIONIST":
                prefix = "RC";
                filename = "receptionist.txt";
                break;
            case "ADMIN":
                prefix = "AD";
                filename = "admin.txt";
                break;
            default:
                return "ERR001";
        }
        
        int maxId = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].startsWith(prefix)) {
                    try {
                        int num = Integer.parseInt(parts[0].substring(2));
                        maxId = Math.max(maxId, num);
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (IOException e) {
            // File doesn't exist, start from 0
        }
        
        return String.format("%s%03d", prefix, maxId + 1);
    }
    
    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
    
    // Helper methods for creating UI components
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(25); // Increased width
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Bigger font
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15) // More padding
        ));
        field.setPreferredSize(new Dimension(300, 35)); // Set preferred size
        return field;
    }
    
    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Bigger font
        label.setForeground(TEXT_COLOR);
        label.setPreferredSize(new Dimension(140, 35)); // Bigger label size
        return label;
    }
    
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setBackground(backgroundColor);
        button.setForeground(backgroundColor.equals(SECONDARY_COLOR) ? TEXT_COLOR : Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private void setupWindow() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Make size bigger and responsive based on user type
        if (userType.equals("TUTOR")) {
            setSize(650, 550); // Bigger for tutor (has DOB field)
        } else if (userType.equals("ADMIN")) {
            setSize(650, 600); // Biggest for admin (has role field)
        } else {
            setSize(650, 500); // Standard size for receptionist
        }
        
        setLocationRelativeTo(parentFrame);
        setResizable(true); // Allow resizing
        setMinimumSize(new Dimension(550, 400)); // Set minimum size
    }
    
    public boolean isRegistrationSuccessful() {
        return registrationSuccessful;
    }
}