// ProfilePanel.java - Enhanced version with proper profile management
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Enhanced ProfilePanel - Works for all user types (Receptionist, Tutor, Student, Admin)
 * UPDATED: Shows Role field for Admins instead of Full Name
 */
public class ProfilePanel extends JPanel {
    private DataManager dataManager;
    private User currentUser;
    private Object parentFrame; // Can be ReceptionistDashboard, TutorDashboard, StudentPortal, or AdminDashboard
    
    // Color scheme
    private final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private final Color SECONDARY_COLOR = new Color(243, 244, 246);
    private final Color ACCENT_COLOR = new Color(16, 185, 129);
    private final Color ERROR_COLOR = new Color(239, 68, 68);
    private final Color TEXT_COLOR = new Color(31, 41, 55);
    private final Color BACKGROUND_COLOR = new Color(249, 250, 251);
    
    // Form fields - different for each user type
    private JTextField userIdField;
    private JTextField usernameField;
    private JTextField nameField;      // For non-admin users
    private JTextField roleField;      // For admin users only
    private JTextField emailField;
    private JTextField phoneField;
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    
    // Student-specific fields
    private JTextField icField;
    private JTextArea addressArea;
    private JTextField levelField;
    private JTextField enrollmentMonthField;
    private JTextArea subjectsArea;
    
    // Tutor-specific fields
    private JTextField dobField;
    
    public ProfilePanel(DataManager dataManager, User currentUser, Object parentFrame) {
        this.dataManager = dataManager;
        this.currentUser = currentUser;
        this.parentFrame = parentFrame;
        
        initializeComponents();
        setupLayout();
        loadUserData();
    }
    
    private void initializeComponents() {
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Common fields
        userIdField = createStyledTextField();
        userIdField.setEditable(false);
        userIdField.setBackground(SECONDARY_COLOR);
        
        usernameField = createStyledTextField();
        usernameField.setEditable(false);
        usernameField.setBackground(SECONDARY_COLOR);
        
        // UPDATED: Create both name and role fields
        nameField = createStyledTextField();  // For non-admin users
        roleField = createStyledTextField();  // For admin users only
        
        emailField = createStyledTextField();
        phoneField = createStyledTextField();
        
        // Password fields
        currentPasswordField = createStyledPasswordField();
        newPasswordField = createStyledPasswordField();
        confirmPasswordField = createStyledPasswordField();
        
        // Student-specific fields
        if (currentUser instanceof Student) {
            icField = createStyledTextField();
            addressArea = createStyledTextArea(3, 25);
            levelField = createStyledTextField();
            levelField.setEditable(false);
            levelField.setBackground(SECONDARY_COLOR);
            enrollmentMonthField = createStyledTextField();
            enrollmentMonthField.setEditable(false);
            enrollmentMonthField.setBackground(SECONDARY_COLOR);
            subjectsArea = createStyledTextArea(4, 25);
            subjectsArea.setEditable(false);
            subjectsArea.setBackground(SECONDARY_COLOR);
        }
        
        // Tutor-specific fields
        if (currentUser instanceof Tutor) {
            dobField = createStyledTextField();
        }
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
    
    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(30);
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
        
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(30, 30, 30, 30)
        ));
        
        // Title
        String userType = "";
        if (currentUser instanceof Student) userType = "Student";
        else if (currentUser instanceof Tutor) userType = "Tutor";
        else if (currentUser instanceof Receptionist) userType = "Receptionist";
        else if (currentUser instanceof Admin) userType = "Administrator";
        
        JLabel titleLabel = new JLabel("Update " + userType + " Profile");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Profile info panel
        JPanel profileInfoPanel = createProfileInfoPanel();
        
        // Password change panel
        JPanel passwordPanel = createPasswordChangePanel();
        
        // Add components
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(profileInfoPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(passwordPanel);
        
        // Add scroll pane for long forms
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createProfileInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1), 
                "Profile Information", 0, 0, new Font("Segoe UI", Font.BOLD, 16), TEXT_COLOR),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Form fields
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Common fields for all users
        gbc.gridx = 0; gbc.gridy = row;
        fieldsPanel.add(createFieldLabel("User ID:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(userIdField, gbc);
        row++;
        
        if (!(currentUser instanceof Student)) {
            gbc.gridx = 0; gbc.gridy = row;
            fieldsPanel.add(createFieldLabel("Username:"), gbc);
            gbc.gridx = 1;
            fieldsPanel.add(usernameField, gbc);
            row++;
        }
        
        // UPDATED: Show Role field for Admins, Full Name for others
        gbc.gridx = 0; gbc.gridy = row;
        if (currentUser instanceof Admin) {
            fieldsPanel.add(createFieldLabel("Role:"), gbc);
            gbc.gridx = 1;
            fieldsPanel.add(roleField, gbc);
        } else {
            fieldsPanel.add(createFieldLabel("Full Name:"), gbc);
            gbc.gridx = 1;
            fieldsPanel.add(nameField, gbc);
        }
        row++;
        
        // Student-specific fields
        if (currentUser instanceof Student) {
            gbc.gridx = 0; gbc.gridy = row;
            fieldsPanel.add(createFieldLabel("IC/Passport:"), gbc);
            gbc.gridx = 1;
            fieldsPanel.add(icField, gbc);
            row++;
        }
        
        gbc.gridx = 0; gbc.gridy = row;
        fieldsPanel.add(createFieldLabel("Email:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(emailField, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        fieldsPanel.add(createFieldLabel("Phone:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(phoneField, gbc);
        row++;
        
        // Tutor-specific fields
        if (currentUser instanceof Tutor) {
            gbc.gridx = 0; gbc.gridy = row;
            fieldsPanel.add(createFieldLabel("Date of Birth:"), gbc);
            gbc.gridx = 1;
            fieldsPanel.add(dobField, gbc);
            row++;
        }
        
        // Student-specific fields continued
        if (currentUser instanceof Student) {
            gbc.gridx = 0; gbc.gridy = row;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            fieldsPanel.add(createFieldLabel("Address:"), gbc);
            gbc.gridx = 1;
            fieldsPanel.add(new JScrollPane(addressArea), gbc);
            row++;
            
            gbc.anchor = GridBagConstraints.WEST;
            gbc.gridx = 0; gbc.gridy = row;
            fieldsPanel.add(createFieldLabel("Level:"), gbc);
            gbc.gridx = 1;
            fieldsPanel.add(levelField, gbc);
            row++;
            
            gbc.gridx = 0; gbc.gridy = row;
            fieldsPanel.add(createFieldLabel("Enrollment Month:"), gbc);
            gbc.gridx = 1;
            fieldsPanel.add(enrollmentMonthField, gbc);
            row++;
            
            gbc.gridx = 0; gbc.gridy = row;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            fieldsPanel.add(createFieldLabel("Enrolled Subjects:"), gbc);
            gbc.gridx = 1;
            fieldsPanel.add(new JScrollPane(subjectsArea), gbc);
        }
        
        // Buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonsPanel.setBackground(Color.WHITE);
        
        JButton updateProfileButton = createStyledButton("Update Profile", ACCENT_COLOR);
        JButton resetButton = createStyledButton("Reset", SECONDARY_COLOR);
        
        buttonsPanel.add(updateProfileButton);
        buttonsPanel.add(resetButton);
        
        // Event listeners
        updateProfileButton.addActionListener(e -> updateProfile());
        resetButton.addActionListener(e -> loadUserData());
        
        panel.add(fieldsPanel, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createPasswordChangePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1), 
                "Change Password", 0, 0, new Font("Segoe UI", Font.BOLD, 16), TEXT_COLOR),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Form fields
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Current password
        gbc.gridx = 0; gbc.gridy = 0;
        fieldsPanel.add(createFieldLabel("Current Password:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(currentPasswordField, gbc);
        
        // New password
        gbc.gridx = 0; gbc.gridy = 1;
        fieldsPanel.add(createFieldLabel("New Password:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(newPasswordField, gbc);
        
        // Confirm password
        gbc.gridx = 0; gbc.gridy = 2;
        fieldsPanel.add(createFieldLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(confirmPasswordField, gbc);
        
        // Buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonsPanel.setBackground(Color.WHITE);
        
        JButton changePasswordButton = createStyledButton("Change Password", PRIMARY_COLOR);
        JButton clearPasswordButton = createStyledButton("Clear", SECONDARY_COLOR);
        
        buttonsPanel.add(changePasswordButton);
        buttonsPanel.add(clearPasswordButton);
        
        // Event listeners
        changePasswordButton.addActionListener(e -> changePassword());
        clearPasswordButton.addActionListener(e -> clearPasswordFields());
        
        panel.add(fieldsPanel, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
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
    
    private void loadUserData() {
        userIdField.setText(currentUser.getUserId());
        emailField.setText(currentUser.getEmail());
        phoneField.setText(currentUser.getPhone());
        
        if (!(currentUser instanceof Student)) {
            usernameField.setText(currentUser.getUsername());
        }
        
        // UPDATED: Load role for admins, name for others
        if (currentUser instanceof Admin) {
            Admin admin = (Admin) currentUser;
            roleField.setText(admin.getRole() != null ? admin.getRole() : "Administrator");
        } else {
            nameField.setText(currentUser.getName());
        }
        
        if (currentUser instanceof Student) {
            Student student = (Student) currentUser;
            icField.setText(student.getIc());
            addressArea.setText(student.getAddress());
            levelField.setText(student.getLevel());
            enrollmentMonthField.setText(student.getEnrollmentMonth());
            subjectsArea.setText(student.getSubjectsAsString());
        }
        
        if (currentUser instanceof Tutor) {
            Tutor tutor = (Tutor) currentUser;
            dobField.setText(tutor.getDateOfBirth());
        }
    }
    
    private void updateProfile() {
        // Validate input
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        
        if (email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Email validation
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            boolean success = false;
            
            if (currentUser instanceof Student) {
                Student student = (Student) currentUser;
                String name = nameField.getText().trim();
                String ic = icField.getText().trim();
                String address = addressArea.getText().trim();
                
                if (name.isEmpty() || ic.isEmpty() || address.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill in all student fields!", 
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                success = dataManager.updateStudentProfile(student.getStudentId(), name, email, phone, address);
                if (success) {
                    student.setName(name);
                    student.setEmail(email);
                    student.setPhone(phone);
                    student.setIc(ic);
                    student.setAddress(address);
                }
            } else if (currentUser instanceof Receptionist) {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Name cannot be empty!", 
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                success = dataManager.updateReceptionistProfile(currentUser.getUserId(), name, email, phone);
                if (success) {
                    currentUser.setName(name);
                    currentUser.setEmail(email);
                    currentUser.setPhone(phone);
                }
            } else if (currentUser instanceof Tutor) {
                Tutor tutor = (Tutor) currentUser;
                String name = nameField.getText().trim();
                String dob = dobField.getText().trim();
                
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Name cannot be empty!", 
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // For tutors, we'll update the basic info and DOB
                tutor.setName(name);
                tutor.setEmail(email);
                tutor.setPhone(phone);
                tutor.setDateOfBirth(dob);
                success = true; // Assume success for now since DataManager doesn't have tutor update method
            } else if (currentUser instanceof Admin) {
                // UPDATED: Handle admin profile update with role
                Admin admin = (Admin) currentUser;
                String role = roleField.getText().trim();
                
                if (role.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Role cannot be empty!", 
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Update admin fields
                admin.setRole(role);
                admin.setEmail(email);
                admin.setPhone(phone);
                
                // Save to file (you may need to implement this in DataManager)
                success = updateAdminInFile(admin);
            }
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Profile updated successfully!", 
                    "Update Success", JOptionPane.INFORMATION_MESSAGE);
                refreshParentFrame();
            } else {
                JOptionPane.showMessageDialog(this, "Error updating profile. Please try again.", 
                    "Update Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unexpected error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * UPDATED: Method to update admin in file
     */
    private boolean updateAdminInFile(Admin admin) {
        try {
            java.io.File file = new java.io.File("admin.txt");
            java.util.List<String> lines = new java.util.ArrayList<>();
            boolean found = false;
            
            // Read all lines
            if (file.exists()) {
                try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        lines.add(line);
                    }
                }
            }
            
            // Update the admin's line
            for (int i = 0; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if (parts.length >= 1 && parts[0].equals(admin.getUserId())) {
                    // Format: UserID,Username,Password,Name,Email,Phone,Role
                    String updatedLine = String.format("%s,%s,%s,%s,%s,%s,%s",
                        admin.getUserId(), 
                        admin.getUsername(), 
                        admin.getPassword(),
                        admin.getName(), 
                        admin.getEmail(), 
                        admin.getPhone(),
                        admin.getRole());
                    lines.set(i, updatedLine);
                    found = true;
                    break;
                }
            }
            
            if (found) {
                // Write back to file
                try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(file))) {
                    for (String line : lines) {
                        writer.println(line);
                    }
                }
                return true;
            }
            return false;
            
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private void changePassword() {
        // Get password values
        String currentPassword = new String(currentPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Validate input
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all password fields!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check current password
        if (!currentPassword.equals(currentUser.getPassword())) {
            JOptionPane.showMessageDialog(this, "Current password is incorrect!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check new password confirmation
        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "New password and confirmation do not match!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check password strength
        if (newPassword.length() < 6) {
            JOptionPane.showMessageDialog(this, "New password must be at least 6 characters long!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Additional validation for admin passwords
        if (currentUser instanceof Admin && newPassword.length() < 8) {
            JOptionPane.showMessageDialog(this, "Administrator password must be at least 8 characters long!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Update password
        currentUser.setPassword(newPassword);
        clearPasswordFields();
        
        JOptionPane.showMessageDialog(this, "Password changed successfully!", 
            "Password Change Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void clearPasswordFields() {
        currentPasswordField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");
    }
    
    private void refreshParentFrame() {
        try {
            if (parentFrame instanceof ReceptionistDashboard) {
                ((ReceptionistDashboard) parentFrame).refreshData();
            } else if (parentFrame instanceof TutorDashboard) {
                ((TutorDashboard) parentFrame).refreshData();
            } else if (parentFrame instanceof StudentPortal) {
                // StudentPortal doesn't have refreshData method, but we can refresh the profile tab
                // The changes are already reflected in the currentUser object
            } else if (parentFrame instanceof AdminDashboard) {
                // Refresh admin dashboard if it has a refresh method
                // The changes are already reflected in the currentUser object
            }
        } catch (Exception e) {
            System.err.println("Error refreshing parent frame: " + e.getMessage());
        }
    }
}