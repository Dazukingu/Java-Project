import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * FIXED EditUser - Now with proper scrollable content for all sections
 * This fixes the issue where you couldn't scroll to see all form fields
 */
public class EditUser extends JDialog {
    private DataManager dataManager;
    private JFrame parentFrame;
    private String userType;
    private String userId;
    private User currentUser;
    private boolean editSuccessful = false;
    
    // UI Components
    private JTextField nameField, emailField, phoneField, usernameField, roleField;
    private JTextField icField, addressField, levelField, enrollmentMonthField;
    private JTextField dobField;
    private JPasswordField passwordField, confirmPasswordField;
    private JList<String> availableClassesList, selectedClassesList;
    private DefaultListModel<String> availableClassesModel, selectedClassesModel;
    private JCheckBox showPasswordCheckBox;
    
    // Modern color scheme
    private final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private final Color SECONDARY_COLOR = new Color(243, 244, 246);
    private final Color ACCENT_COLOR = new Color(16, 185, 129);
    private final Color ERROR_COLOR = new Color(239, 68, 68);
    private final Color TEXT_COLOR = new Color(31, 41, 55);
    private final Color BACKGROUND_COLOR = new Color(249, 250, 251);
    
    public EditUser(JFrame parent, DataManager dataManager, String userType, String userId) {
        super(parent, "Edit " + userType, true);
        this.parentFrame = parent;
        this.dataManager = dataManager;
        this.userType = userType;
        this.userId = userId;
        
        // Load user data
        loadUserData();
        
        if (currentUser == null) {
            JOptionPane.showMessageDialog(parent, "User not found!", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        
        initializeComponents();
        setupScrollableLayout();
        populateFields();
        setupWindow();
    }
    
    private void loadUserData() {
        switch (userType.toUpperCase()) {
            case "STUDENT":
                currentUser = dataManager.getStudentById(userId);
                break;
            case "TUTOR":
                currentUser = dataManager.getTutorById(userId);
                break;
            case "RECEPTIONIST":
                currentUser = dataManager.getReceptionistById(userId);
                break;
            case "ADMIN":
                currentUser = dataManager.getAdminById(userId);
                break;
            default:
                currentUser = null;
        }
    }
    
    private void initializeComponents() {
        // Common fields
        nameField = createStyledTextField();
        emailField = createStyledTextField();
        phoneField = createStyledTextField();
        usernameField = createStyledTextField();
        roleField = createStyledTextField(); // New role field for admins
        passwordField = createStyledPasswordField();
        confirmPasswordField = createStyledPasswordField();
        showPasswordCheckBox = new JCheckBox("Show Passwords");
        
        // Student-specific fields
        if (userType.equalsIgnoreCase("STUDENT")) {
            icField = createStyledTextField();
            addressField = createStyledTextField();
            levelField = createStyledTextField();
            enrollmentMonthField = createStyledTextField();
            
            // Class selection components
            availableClassesModel = new DefaultListModel<>();
            selectedClassesModel = new DefaultListModel<>();
            availableClassesList = new JList<>(availableClassesModel);
            selectedClassesList = new JList<>(selectedClassesModel);
            
            availableClassesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            selectedClassesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            
            setupClassSelectionData();
        }
        
        // Tutor-specific fields
        if (userType.equalsIgnoreCase("TUTOR")) {
            dobField = createStyledTextField();
        }
        
        // Password toggle functionality
        setupPasswordToggle();
    }
    
    private void setupClassSelectionData() {
        if (!userType.equalsIgnoreCase("STUDENT")) return;
        
        Student student = (Student) currentUser;
        List<ClassInfo> allClasses = dataManager.getAllClasses();
        List<String> studentClasses = student.getSubjects();
        
        // Filter classes by student's level
        for (ClassInfo classInfo : allClasses) {
            String classDisplay = classInfo.getClassId() + " - " + classInfo.getSubject() + " (RM" + 
                                String.format("%.2f", classInfo.getFee()) + ")";
            
            if (classInfo.getSubject().contains(student.getLevel())) {
                if (studentClasses.contains(classInfo.getClassId())) {
                    selectedClassesModel.addElement(classDisplay);
                } else {
                    availableClassesModel.addElement(classDisplay);
                }
            }
        }
    }
    
    private void setupPasswordToggle() {
        showPasswordCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPasswordCheckBox.setBackground(Color.WHITE);
        showPasswordCheckBox.addActionListener(e -> {
            if (showPasswordCheckBox.isSelected()) {
                passwordField.setEchoChar((char) 0);
                confirmPasswordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
                confirmPasswordField.setEchoChar('•');
            }
        });
    }
    
    /**
     * FIXED: Completely rewritten layout method with proper scrolling
     */
    private void setupScrollableLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Header (non-scrollable)
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // MAIN SCROLLABLE CONTENT
        JPanel scrollableContent = createScrollableContentPanel();
        
        // Create scroll pane with proper settings
        JScrollPane scrollPane = new JScrollPane(scrollableContent);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Improve scroll performance
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setBlockIncrement(50);
        
        // Ensure the scroll pane takes up the center space
        add(scrollPane, BorderLayout.CENTER);
        
        // Buttons (non-scrollable, always visible)
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Create the main scrollable content panel with all form sections
     */
    private JPanel createScrollableContentPanel() {
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(BACKGROUND_COLOR);
        mainContent.setBorder(new EmptyBorder(20, 25, 30, 25)); // Extra bottom padding
        
        // Add sections with spacing
        mainContent.add(createBasicInfoSection());
        mainContent.add(Box.createRigidArea(new Dimension(0, 20)));
        
        if (userType.equalsIgnoreCase("STUDENT")) {
            mainContent.add(createStudentSpecificSection());
            mainContent.add(Box.createRigidArea(new Dimension(0, 20)));
            mainContent.add(createClassSelectionSection());
            mainContent.add(Box.createRigidArea(new Dimension(0, 20)));
        } else if (userType.equalsIgnoreCase("TUTOR")) {
            mainContent.add(createTutorSpecificSection());
            mainContent.add(Box.createRigidArea(new Dimension(0, 20)));
        }
        
        mainContent.add(createSecuritySection());
        
        // Add extra space at the bottom to ensure all content is accessible
        mainContent.add(Box.createRigidArea(new Dimension(0, 50)));
        
        return mainContent;
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));
        
        JLabel titleLabel = new JLabel("Edit " + userType + " - " + currentUser.getName());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("User ID: " + userId + " | Scroll down to see all sections");
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
    
    private JPanel createBasicInfoSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                "Basic Information", 0, 0, new Font("Segoe UI", Font.BOLD, 14), TEXT_COLOR),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Name field
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(createFieldLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(nameField, gbc);
        
        // Email field
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(createFieldLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(emailField, gbc);
        
        // Phone field
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(createFieldLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(phoneField, gbc);
        
        // Username field for tutors and receptionists, Role field for admins
        if (!userType.equalsIgnoreCase("STUDENT")) {
            gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
            if (userType.equalsIgnoreCase("ADMIN")) {
                panel.add(createFieldLabel("Role:"), gbc);
                gbc.gridx = 1; gbc.weightx = 1.0;
                panel.add(roleField, gbc);
            } else {
                panel.add(createFieldLabel("Username:"), gbc);
                gbc.gridx = 1; gbc.weightx = 1.0;
                panel.add(usernameField, gbc);
            }
        }
        
        return panel;
    }
    
    private JPanel createStudentSpecificSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                "Student Information", 0, 0, new Font("Segoe UI", Font.BOLD, 14), TEXT_COLOR),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // IC/Passport field
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(createFieldLabel("IC/Passport:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(icField, gbc);
        
        // Level field
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(createFieldLabel("Level:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(levelField, gbc);
        
        // Enrollment Month field
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(createFieldLabel("Enrollment Month:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(enrollmentMonthField, gbc);
        
        // Address field
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panel.add(createFieldLabel("Address:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(addressField, gbc);
        
        return panel;
    }
    
    private JPanel createTutorSpecificSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                "Tutor Information", 0, 0, new Font("Segoe UI", Font.BOLD, 14), TEXT_COLOR),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Date of Birth field
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(createFieldLabel("Date of Birth:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(dobField, gbc);
        
        return panel;
    }
    
    private JPanel createClassSelectionSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                "Class Enrollment (Select 1-3 Classes)", 0, 0, new Font("Segoe UI", Font.BOLD, 14), TEXT_COLOR),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Available classes panel
        JPanel availablePanel = new JPanel(new BorderLayout());
        availablePanel.setBorder(BorderFactory.createTitledBorder("Available Classes"));
        JScrollPane availableScroll = new JScrollPane(availableClassesList);
        availableScroll.setPreferredSize(new Dimension(280, 120));
        availablePanel.add(availableScroll, BorderLayout.CENTER);
        
        // Selected classes panel
        JPanel selectedPanel = new JPanel(new BorderLayout());
        selectedPanel.setBorder(BorderFactory.createTitledBorder("Selected Classes"));
        JScrollPane selectedScroll = new JScrollPane(selectedClassesList);
        selectedScroll.setPreferredSize(new Dimension(280, 120));
        selectedPanel.add(selectedScroll, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBorder(new EmptyBorder(20, 10, 20, 10));
        
        JButton addButton = createStyledButton("→ Add", ACCENT_COLOR);
        JButton removeButton = createStyledButton("← Remove", ERROR_COLOR);
        
        addButton.addActionListener(e -> moveSelectedClasses(availableClassesList, availableClassesModel, selectedClassesModel));
        removeButton.addActionListener(e -> moveSelectedClasses(selectedClassesList, selectedClassesModel, availableClassesModel));
        
        buttonsPanel.add(Box.createVerticalGlue());
        buttonsPanel.add(addButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonsPanel.add(removeButton);
        buttonsPanel.add(Box.createVerticalGlue());
        
        // Layout
        panel.add(availablePanel, BorderLayout.WEST);
        panel.add(buttonsPanel, BorderLayout.CENTER);
        panel.add(selectedPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createSecuritySection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                "Security Settings (Optional)", 0, 0, new Font("Segoe UI", Font.BOLD, 14), TEXT_COLOR),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // New Password field
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(createFieldLabel("New Password:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(passwordField, gbc);
        
        // Confirm Password field
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(createFieldLabel("Confirm Password:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(confirmPasswordField, gbc);
        
        // Show passwords checkbox
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        panel.add(showPasswordCheckBox, gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panel.setBackground(BACKGROUND_COLOR);
        
        JButton saveButton = createStyledButton("💾 Save Changes", ACCENT_COLOR);
        JButton cancelButton = createStyledButton("❌ Cancel", SECONDARY_COLOR);
        
        saveButton.addActionListener(e -> saveUser());
        cancelButton.addActionListener(e -> dispose());
        
        panel.add(saveButton);
        panel.add(cancelButton);
        
        return panel;
    }
    
    private void populateFields() {
        // Basic fields
        nameField.setText(currentUser.getName());
        emailField.setText(currentUser.getEmail());
        phoneField.setText(currentUser.getPhone());
        
        // Username for non-students (except admins), Role for admins
        if (!userType.equalsIgnoreCase("STUDENT")) {
            if (userType.equalsIgnoreCase("ADMIN")) {
                Admin admin = (Admin) currentUser;
                roleField.setText(admin.getRole() != null ? admin.getRole() : "Administrator");
            } else {
                usernameField.setText(currentUser.getUsername());
            }
        }
        
        // Type-specific fields
        if (userType.equalsIgnoreCase("STUDENT")) {
            Student student = (Student) currentUser;
            icField.setText(student.getIc());
            addressField.setText(student.getAddress());
            levelField.setText(student.getLevel());
            enrollmentMonthField.setText(student.getEnrollmentMonth());
        } else if (userType.equalsIgnoreCase("TUTOR")) {
            Tutor tutor = (Tutor) currentUser;
            dobField.setText(tutor.getDateOfBirth());
        }
    }
    
    private void moveSelectedClasses(JList<String> sourceList, DefaultListModel<String> sourceModel, DefaultListModel<String> targetModel) {
        int[] selectedIndices = sourceList.getSelectedIndices();
        if (selectedIndices.length == 0) return;
        
        // Check if target model would exceed 3 items for selected classes
        if (targetModel == selectedClassesModel && selectedClassesModel.getSize() + selectedIndices.length > 3) {
            JOptionPane.showMessageDialog(this, "Students can only enroll in a maximum of 3 classes!", 
                "Enrollment Limit", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Move selected items
        for (int i = selectedIndices.length - 1; i >= 0; i--) {
            String item = sourceModel.getElementAt(selectedIndices[i]);
            sourceModel.removeElementAt(selectedIndices[i]);
            targetModel.addElement(item);
        }
    }
    
    private void saveUser() {
        try {
            // Validation
            if (!validateFields()) {
                return;
            }
            
            // Update common fields
            currentUser.setName(nameField.getText().trim());
            currentUser.setEmail(emailField.getText().trim());
            currentUser.setPhone(phoneField.getText().trim());
            
            // Update username for non-students (except admins), role for admins
            if (!userType.equalsIgnoreCase("STUDENT")) {
                if (userType.equalsIgnoreCase("ADMIN")) {
                    Admin admin = (Admin) currentUser;
                    admin.setRole(roleField.getText().trim());
                } else {
                    currentUser.setUsername(usernameField.getText().trim());
                }
            }
            
            // Update password if provided
            String newPassword = new String(passwordField.getPassword());
            if (!newPassword.isEmpty()) {
                currentUser.setPassword(newPassword);
            }
            
            // Type-specific updates
            boolean success = false;
            
            switch (userType.toUpperCase()) {
                case "STUDENT":
                    success = updateStudent();
                    break;
                case "TUTOR":
                    success = updateTutor();
                    break;
                case "RECEPTIONIST":
                    success = updateReceptionist();
                    break;
                case "ADMIN":
                    success = updateAdmin();
                    break;
            }
            
            if (success) {
                editSuccessful = true;
                JOptionPane.showMessageDialog(this, "User updated successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update user. Please try again.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating user: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private boolean validateFields() {
        // Basic validation
        if (nameField.getText().trim().isEmpty()) {
            showValidationError("Name cannot be empty!");
            nameField.requestFocus();
            return false;
        }
        
        if (emailField.getText().trim().isEmpty() || !emailField.getText().contains("@")) {
            showValidationError("Please enter a valid email address!");
            emailField.requestFocus();
            return false;
        }
        
        if (phoneField.getText().trim().isEmpty()) {
            showValidationError("Phone number cannot be empty!");
            phoneField.requestFocus();
            return false;
        }
        
        // Validate username for non-students (except admins), role for admins
        if (!userType.equalsIgnoreCase("STUDENT")) {
            if (userType.equalsIgnoreCase("ADMIN")) {
                if (roleField.getText().trim().isEmpty()) {
                    showValidationError("Role cannot be empty!");
                    roleField.requestFocus();
                    return false;
                }
            } else {
                if (usernameField.getText().trim().isEmpty()) {
                    showValidationError("Username cannot be empty!");
                    usernameField.requestFocus();
                    return false;
                }
                
                if (usernameField.getText().trim().length() < 3) {
                    showValidationError("Username must be at least 3 characters long!");
                    usernameField.requestFocus();
                    return false;
                }
            }
        }
        
        // Password validation
        String newPassword = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        if (!newPassword.isEmpty()) {
            if (newPassword.length() < 6) {
                showValidationError("Password must be at least 6 characters long!");
                passwordField.requestFocus();
                return false;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                showValidationError("Passwords do not match!");
                confirmPasswordField.requestFocus();
                return false;
            }
        }
        
        // Student-specific validation
        if (userType.equalsIgnoreCase("STUDENT")) {
            if (icField.getText().trim().isEmpty()) {
                showValidationError("IC/Passport cannot be empty!");
                icField.requestFocus();
                return false;
            }
            
            if (levelField.getText().trim().isEmpty()) {
                showValidationError("Level cannot be empty!");
                levelField.requestFocus();
                return false;
            }
            
            if (selectedClassesModel.getSize() == 0) {
                showValidationError("Student must be enrolled in at least 1 class!");
                return false;
            }
            
            if (selectedClassesModel.getSize() > 3) {
                showValidationError("Student can enroll in maximum 3 classes!");
                return false;
            }
        }
        
        return true;
    }
    
    private boolean updateStudent() {
        try {
            Student student = (Student) currentUser;
            
            // Update student-specific fields
            student.setIc(icField.getText().trim());
            student.setAddress(addressField.getText().trim());
            student.setLevel(levelField.getText().trim());
            student.setEnrollmentMonth(enrollmentMonthField.getText().trim());
            
            // Update class enrollment
            java.util.List<String> newClassIds = new ArrayList<>();
            for (int i = 0; i < selectedClassesModel.getSize(); i++) {
                String classDisplay = selectedClassesModel.getElementAt(i);
                String classId = classDisplay.split(" - ")[0]; // Extract class ID
                newClassIds.add(classId);
            }
            student.setClassIds(newClassIds.toArray(new String[0]));
            
            // Update in file
            return updateUserInFile("students.txt", student);
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean updateTutor() {
        try {
            Tutor tutor = (Tutor) currentUser;
            tutor.setDateOfBirth(dobField.getText().trim());
            return updateUserInFile("tutor.txt", tutor);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean updateReceptionist() {
        try {
            return updateUserInFile("receptionist.txt", currentUser);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean updateAdmin() {
        try {
            return updateUserInFile("admin.txt", currentUser);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean updateUserInFile(String filename, User user) {
        try {
            File file = new File(filename);
            java.util.List<String> lines = new ArrayList<>();
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
            
            // Update the user's line
            for (int i = 0; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if (parts.length > 0 && parts[0].equals(user.getUserId())) {
                    String updatedLine = createUpdatedLine(user);
                    lines.set(i, updatedLine);
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                return false;
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
    
    private String createUpdatedLine(User user) {
        if (user instanceof Student) {
            Student s = (Student) user;
            return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                s.getUserId(), s.getIc(), s.getPassword(), s.getName(),
                s.getEmail(), s.getPhone(), s.getAddress(), s.getLevel(),
                s.getEnrollmentMonth(), s.getClassIdsString());
        } else if (user instanceof Tutor) {
            Tutor t = (Tutor) user;
            return String.format("%s,%s,%s,%s,%s,%s,%s",
                t.getUserId(), t.getName(), t.getPassword(),
                t.getEmail(), t.getDateOfBirth(), t.getPhone(), t.getUsername());
        } else if (user instanceof Receptionist) {
            Receptionist r = (Receptionist) user;
            return String.format("%s,%s,%s,%s,%s,%s",
                r.getUserId(), r.getUsername(), r.getPassword(),
                r.getName(), r.getEmail(), r.getPhone());
        } else if (user instanceof Admin) {
            Admin a = (Admin) user;
            // Format: UserID,Username,Password,Name,Email,Phone,Role
            return String.format("%s,%s,%s,%s,%s,%s,%s",
                a.getUserId(), a.getUsername(), a.getPassword(),
                a.getName(), a.getEmail(), a.getPhone(), 
                a.getRole() != null ? a.getRole() : "Administrator");
        }
        return "";
    }
    
    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
    
    // Helper methods for creating UI components
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }
    
    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setEchoChar('•');
        return field;
    }
    
    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(TEXT_COLOR);
        label.setPreferredSize(new Dimension(120, 25));
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
    
    /**
     * FIXED: Window setup with proper sizing for scrollable content
     */
    private void setupWindow() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Set appropriate size based on user type but ensure it fits on screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int maxHeight = (int) (screenSize.height * 0.85); // Use 85% of screen height
        int maxWidth = (int) (screenSize.width * 0.6);    // Use 60% of screen width
        
        if (userType.equalsIgnoreCase("STUDENT")) {
            // Students have more sections, so need more height
            setSize(Math.min(850, maxWidth), Math.min(750, maxHeight));
        } else {
            // Other user types have fewer sections
            setSize(Math.min(700, maxWidth), Math.min(600, maxHeight));
        }
        
        setLocationRelativeTo(parentFrame);
        setResizable(true);
        
        // Set minimum size to ensure usability
        setMinimumSize(new Dimension(600, 400));
        
        // Ensure the dialog is properly sized and positioned
        validate();
    }
    
    public boolean isEditSuccessful() {
        return editSuccessful;
    }
}