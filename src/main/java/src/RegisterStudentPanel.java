import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * RegisterStudentPanel - Panel for registering new students with flexible subject selection (1-3 subjects)
 * Updated to support minimum 1 subject and maximum 3 subjects enrollment
 */
public class RegisterStudentPanel extends JPanel {
    private DataManager dataManager;
    private ReceptionistDashboard parentFrame;
    
    // Color scheme
    private final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private final Color SECONDARY_COLOR = new Color(243, 244, 246);
    private final Color ACCENT_COLOR = new Color(16, 185, 129);
    private final Color ERROR_COLOR = new Color(239, 68, 68);
    private final Color TEXT_COLOR = new Color(31, 41, 55);
    private final Color BACKGROUND_COLOR = new Color(249, 250, 251);
    
    // Form fields
    private JTextField nameField;
    private JTextField icPassportField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea addressArea;
    private JComboBox<String> levelComboBox;
    private JComboBox<String> monthComboBox;
    private JPanel subjectsPanel; // Panel for checkboxes
    private List<JCheckBox> subjectCheckBoxes; // List of checkboxes
    private List<ClassInfo> availableClasses; // Available classes for current level
    private JLabel totalFeeLabel;
    
    public RegisterStudentPanel(DataManager dataManager, ReceptionistDashboard parentFrame) {
        this.dataManager = dataManager;
        this.parentFrame = parentFrame;
        
        initializeComponents();
        setupLayout();
    }
    
    private void initializeComponents() {
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Initialize totalFeeLabel first
        totalFeeLabel = new JLabel("Total Fee: RM 0.00");
        totalFeeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalFeeLabel.setForeground(PRIMARY_COLOR);
        
        // Text fields
        nameField = createStyledTextField();
        icPassportField = createStyledTextField();
        emailField = createStyledTextField();
        phoneField = createStyledTextField();
        
        // Address area
        addressArea = new JTextArea(3, 30);
        addressArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addressArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        
        // Level combo box
        String[] levels = {"Form 1", "Form 2", "Form 3", "Form 4", "Form 5"};
        levelComboBox = new JComboBox<>(levels);
        levelComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        levelComboBox.setBackground(Color.WHITE);
        
        // Month combo box
        String[] months = {"January", "February", "March", "April", "May", "June",
                          "July", "August", "September", "October", "November", "December"};
        monthComboBox = new JComboBox<>(months);
        monthComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        monthComboBox.setBackground(Color.WHITE);
        
        // Subjects panel - uses checkboxes
        subjectsPanel = new JPanel();
        subjectsPanel.setLayout(new BoxLayout(subjectsPanel, BoxLayout.Y_AXIS));
        subjectsPanel.setBackground(Color.WHITE);
        subjectsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Initialize checkbox list
        subjectCheckBoxes = new ArrayList<>();
        availableClasses = new ArrayList<>();
        
        // Level change listener
        levelComboBox.addActionListener(e -> updateSubjectsList());
        
        // Load subjects based on level
        updateSubjectsList();
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(25);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }
    
    private void updateSubjectsList() {
        String selectedLevel = (String) levelComboBox.getSelectedItem();
        availableClasses = dataManager.getSubjectsByLevel(selectedLevel);
        
        System.out.println("DEBUG: Loading subjects for level: " + selectedLevel);
        System.out.println("DEBUG: Found " + availableClasses.size() + " available classes");
        
        // Clear existing checkboxes
        subjectsPanel.removeAll();
        subjectCheckBoxes.clear();
        
        // Add instruction label with flexible requirements
        JLabel instructionLabel = new JLabel("Select 1-3 subjects (minimum 1 required, maximum 3 allowed):");
        instructionLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        instructionLabel.setForeground(TEXT_COLOR);
        instructionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subjectsPanel.add(instructionLabel);
        subjectsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Add flexibility note
        JLabel flexibilityNote = new JLabel("💡 Students can start with 1 subject and add more later through 'Manage Students'");
        flexibilityNote.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        flexibilityNote.setForeground(new Color(107, 114, 128));
        flexibilityNote.setAlignmentX(Component.LEFT_ALIGNMENT);
        subjectsPanel.add(flexibilityNote);
        subjectsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Create checkboxes for each available class
        for (ClassInfo classInfo : availableClasses) {
            System.out.println("DEBUG: Creating checkbox for " + classInfo.getClassId() + 
                             " - " + classInfo.getSubject() + " - Fee: " + classInfo.getFee());
            
            JCheckBox checkBox = new JCheckBox();
            checkBox.setText(String.format("%s - %s (RM%.2f)", 
                classInfo.getClassId(), 
                classInfo.getSubject(), 
                classInfo.getFee()));
            checkBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            checkBox.setBackground(Color.WHITE);
            checkBox.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            // Add action listener to enforce subject limit and update fee
            checkBox.addActionListener(e -> {
                enforceSubjectLimit();
                updateTotalFee();
            });
            
            subjectCheckBoxes.add(checkBox);
            subjectsPanel.add(checkBox);
            subjectsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        
        // Refresh the panel
        subjectsPanel.revalidate();
        subjectsPanel.repaint();
        
        updateTotalFee();
    }
    
    private void enforceSubjectLimit() {
        int selectedCount = 0;
        
        // Count selected checkboxes
        for (JCheckBox checkBox : subjectCheckBoxes) {
            if (checkBox.isSelected()) {
                selectedCount++;
            }
        }
        
        // If more than 3 are selected, disable unchecked boxes
        if (selectedCount >= 3) {
            for (JCheckBox checkBox : subjectCheckBoxes) {
                if (!checkBox.isSelected()) {
                    checkBox.setEnabled(false);
                }
            }
        } else {
            // Re-enable all checkboxes if less than 3 selected
            for (JCheckBox checkBox : subjectCheckBoxes) {
                checkBox.setEnabled(true);
            }
        }
    }
    
    private void updateTotalFee() {
        if (totalFeeLabel == null) {
            return; // Safety check - exit if label not initialized yet
        }
        
        List<ClassInfo> selectedClasses = getSelectedClasses();
        
        // Calculate total fee directly from selected classes (more reliable)
        double totalFee = 0.0;
        for (ClassInfo classInfo : selectedClasses) {
            totalFee += classInfo.getFee();
            System.out.println("DEBUG Registration: Adding " + classInfo.getClassId() + 
                             " - " + classInfo.getSubject() + " - Fee: " + classInfo.getFee());
        }
        
        System.out.println("DEBUG Registration: Total calculated fee: " + totalFee);
        
        // Update display with selection count and flexible requirement
        String feeText = String.format("Total Fee: RM %.2f (%d/3 subjects selected)", 
            totalFee, selectedClasses.size());
        
        if (selectedClasses.size() == 0) {
            feeText += " - MINIMUM 1 REQUIRED";
            totalFeeLabel.setForeground(ERROR_COLOR);
        } else if (selectedClasses.size() >= 1 && selectedClasses.size() <= 3) {
            if (selectedClasses.size() == 3) {
                feeText += " - MAXIMUM REACHED";
            } else {
                feeText += " - VALID SELECTION";
            }
            totalFeeLabel.setForeground(ACCENT_COLOR);
        } else {
            totalFeeLabel.setForeground(PRIMARY_COLOR);
        }
        
        totalFeeLabel.setText(feeText);
    }
    
    private List<ClassInfo> getSelectedClasses() {
        List<ClassInfo> selectedClasses = new ArrayList<>();
        
        for (int i = 0; i < subjectCheckBoxes.size(); i++) {
            if (subjectCheckBoxes.get(i).isSelected()) {
                selectedClasses.add(availableClasses.get(i));
            }
        }
        
        return selectedClasses;
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Main form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(30, 30, 30, 30)
        ));
        
        // Title
        JLabel titleLabel = new JLabel("Register New Student");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtitle with flexible enrollment info
        JLabel subtitleLabel = new JLabel("Flexible Enrollment: 1-3 Subjects Allowed");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 114, 128));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Form fields panel
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Row 1: Name and IC/Passport
        gbc.gridx = 0; gbc.gridy = 0;
        fieldsPanel.add(createFieldLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(nameField, gbc);
        
        gbc.gridx = 2;
        fieldsPanel.add(createFieldLabel("IC/Passport:"), gbc);
        gbc.gridx = 3;
        fieldsPanel.add(icPassportField, gbc);
        
        // Row 2: Email and Phone
        gbc.gridx = 0; gbc.gridy = 1;
        fieldsPanel.add(createFieldLabel("Email:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(emailField, gbc);
        
        gbc.gridx = 2;
        fieldsPanel.add(createFieldLabel("Phone:"), gbc);
        gbc.gridx = 3;
        fieldsPanel.add(phoneField, gbc);
        
        // Row 3: Address
        gbc.gridx = 0; gbc.gridy = 2;
        fieldsPanel.add(createFieldLabel("Address:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        fieldsPanel.add(new JScrollPane(addressArea), gbc);
        
        // Row 4: Level and Enrollment Month
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        fieldsPanel.add(createFieldLabel("Level:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(levelComboBox, gbc);
        
        gbc.gridx = 2;
        fieldsPanel.add(createFieldLabel("Enrollment Month:"), gbc);
        gbc.gridx = 3;
        fieldsPanel.add(monthComboBox, gbc);
        
        // Row 5: Subjects - Updated label
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        fieldsPanel.add(createFieldLabel("Subjects (1-3 Required):"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        
        JScrollPane subjectsScrollPane = new JScrollPane(subjectsPanel);
        subjectsScrollPane.setPreferredSize(new Dimension(400, 200));
        subjectsScrollPane.setBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1));
        subjectsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        subjectsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        fieldsPanel.add(subjectsScrollPane, gbc);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonsPanel.setBackground(Color.WHITE);
        
        JButton registerButton = createStyledButton("Register Student", PRIMARY_COLOR);
        JButton clearButton = createStyledButton("Clear Form", SECONDARY_COLOR);
        
        buttonsPanel.add(registerButton);
        buttonsPanel.add(clearButton);
        
        // Event listeners
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegistration();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        
        // Add components to form panel
        formPanel.add(titleLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(subtitleLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        formPanel.add(fieldsPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(totalFeeLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(buttonsPanel);
        
        // Add form panel to main panel
        add(formPanel, BorderLayout.CENTER);
    }
    
    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT_COLOR);
        return label;
    }
    
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(backgroundColor.equals(SECONDARY_COLOR) ? TEXT_COLOR : Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private void handleRegistration() {
        // Validate input
        String name = nameField.getText().trim();
        String icPassport = icPassportField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressArea.getText().trim();
        String level = (String) levelComboBox.getSelectedItem();
        String month = (String) monthComboBox.getSelectedItem();
        List<ClassInfo> selectedClasses = getSelectedClasses();
        
        // Basic validation
        if (name.isEmpty() || icPassport.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Updated subject validation - flexible 1-3 subjects
        if (selectedClasses.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please select at least 1 subject!\n\n" +
                "💡 Minimum requirement: 1 subject\n" +
                "📚 Maximum allowed: 3 subjects\n" +
                "🔄 Students can add more subjects later through 'Manage Students'", 
                "Subject Selection Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (selectedClasses.size() > 3) {
            JOptionPane.showMessageDialog(this, 
                "Maximum 3 subjects allowed!\n\n" +
                "Currently selected: " + selectedClasses.size() + " subjects\n" +
                "Please deselect " + (selectedClasses.size() - 3) + " subject(s).", 
                "Too Many Subjects", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Email validation
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Convert selected classes to class IDs
        List<String> selectedClassIds = new ArrayList<>();
        for (ClassInfo classInfo : selectedClasses) {
            selectedClassIds.add(classInfo.getClassId());
        }
        
        // Register student with class IDs
        try {
            boolean success = dataManager.registerStudent(name, icPassport, email, phone, address, level, month, selectedClassIds);
            
            if (success) {
                // Calculate total fee for display (use the same method as the fee label)
                double totalFee = dataManager.calculateTotalFeeFromClasses(selectedClasses);
                
                // Create enrollment summary
                String enrollmentSummary = createEnrollmentSummary(selectedClasses, totalFee);
                
                // Enhanced success message with flexible enrollment info
                String successMessage = "✅ STUDENT REGISTRATION SUCCESSFUL!\n\n" +
                    "📋 Registration Details:\n" +
                    "• Student ID: Will be auto-generated\n" +
                    "• Name: " + name + "\n" +
                    "• Level: " + level + "\n" +
                    "• Enrollment Month: " + month + "\n" +
                    "• Selected Subjects: " + selectedClasses.size() + "\n" +
                    "• Total Fee: RM" + String.format("%.2f", totalFee) + "\n" +
                    "• Default Password: password123\n\n" +
                    enrollmentSummary;
                
                // Add flexibility information based on current enrollment
                if (selectedClasses.size() == 1) {
                    successMessage += "\n📚 ENROLLMENT FLEXIBILITY:\n" +
                        "• Current: " + selectedClasses.size() + " subject enrolled\n" +
                        "• Can add more: " + (3 - selectedClasses.size()) + " subjects available\n" +
                        "• Update anytime: Through 'Manage Students' section\n" +
                        "• 💡 Perfect for students who want to start with 1 subject!";
                } else if (selectedClasses.size() == 2) {
                    successMessage += "\n📚 ENROLLMENT FLEXIBILITY:\n" +
                        "• Current: " + selectedClasses.size() + " subjects enrolled\n" +
                        "• Can add more: " + (3 - selectedClasses.size()) + " subject available\n" +
                        "• Update anytime: Through 'Manage Students' section\n" +
                        "• ⚡ Good balance for most students!";
                } else if (selectedClasses.size() == 3) {
                    successMessage += "\n📚 ENROLLMENT STATUS:\n" +
                        "• Current: " + selectedClasses.size() + " subjects enrolled (MAXIMUM)\n" +
                        "• Full enrollment achieved!\n" +
                        "• Can modify subjects: Through 'Manage Students' section\n" +
                        "• 🎯 Maximum learning potential unlocked!";
                }
                
                JOptionPane.showMessageDialog(this, successMessage, "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
                
                clearForm();
                parentFrame.refreshData(); // Refresh the dashboard data
            } else {
                JOptionPane.showMessageDialog(this, "❌ Error registering student. Please try again.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "⚠️ Registration Error:\n\n" + ex.getMessage(), "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String createEnrollmentSummary(List<ClassInfo> classes, double totalFee) {
        StringBuilder summary = new StringBuilder();
        summary.append("🎓 ENROLLED SUBJECTS:\n");
        
        int count = 1;
        for (ClassInfo classInfo : classes) {
            summary.append(count).append(". ").append(classInfo.getClassId())
                   .append(" - ").append(classInfo.getSubject())
                   .append(" (RM").append(String.format("%.2f", classInfo.getFee())).append(")\n");
            count++;
        }
        
        return summary.toString();
    }
    
    private String getClassesSummary(List<ClassInfo> classes) {
        StringBuilder summary = new StringBuilder();
        for (ClassInfo classInfo : classes) {
            summary.append("• ").append(classInfo.getClassId())
                   .append(" - ").append(classInfo.getSubject())
                   .append(" (RM").append(String.format("%.2f", classInfo.getFee())).append(")\n");
        }
        return summary.toString();
    }
    
    private void clearForm() {
        nameField.setText("");
        icPassportField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressArea.setText("");
        levelComboBox.setSelectedIndex(0);
        monthComboBox.setSelectedIndex(0);
        
        // Clear all checkboxes
        for (JCheckBox checkBox : subjectCheckBoxes) {
            checkBox.setSelected(false);
            checkBox.setEnabled(true);
        }
        
        updateTotalFee();
    }
}