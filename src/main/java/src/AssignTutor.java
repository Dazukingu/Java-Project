import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Enhanced AssignTutor - Dialog for assigning existing tutors to existing classes
 * or creating new classes with tutor assignment
 */
public class AssignTutor extends JDialog {
    private DataManager dataManager;
    private JFrame parentFrame;
    private boolean assignmentSuccessful = false;
    
    // UI Components
    private JComboBox<TutorItem> tutorCombo;
    private JComboBox<String> assignmentModeCombo;
    private JComboBox<ClassItem> existingClassCombo;
    private JComboBox<String> levelCombo, subjectCombo;
    private JTextField newClassIdField, descriptionField, scheduleField, feeField;
    private JLabel tutorInfoLabel, classInfoLabel;
    private JPanel newClassPanel, existingClassPanel;
    private CardLayout assignmentCardLayout;
    private JPanel assignmentContentPanel;
    
    // Modern color scheme
    private final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private final Color SECONDARY_COLOR = new Color(243, 244, 246);
    private final Color ACCENT_COLOR = new Color(16, 185, 129);
    private final Color ERROR_COLOR = new Color(239, 68, 68);
    private final Color TEXT_COLOR = new Color(31, 41, 55);
    private final Color BACKGROUND_COLOR = new Color(249, 250, 251);
    
    // Data containers
    private Map<String, Tutor> tutorMap;
    private Map<String, ClassInfo> classMap;
    private Map<String, String> subjectMap;
    
    // Helper classes for combo box items
    private static class TutorItem {
        private String id;
        private String name;
        private String email;
        
        public TutorItem(String id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }
        
        public String getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        
        @Override
        public String toString() {
            return id + " - " + name;
        }
    }
    
    private static class ClassItem {
        private String classId;
        private String subject;
        private String currentTutorId;
        private double fee;
        
        public ClassItem(String classId, String subject, String currentTutorId, double fee) {
            this.classId = classId;
            this.subject = subject;
            this.currentTutorId = currentTutorId;
            this.fee = fee;
        }
        
        public String getClassId() { return classId; }
        public String getSubject() { return subject; }
        public String getCurrentTutorId() { return currentTutorId; }
        public double getFee() { return fee; }
        
        @Override
        public String toString() {
            return classId + " - " + subject + " (Current: " + currentTutorId + ")";
        }
    }
    
    public AssignTutor(JFrame parent, DataManager dataManager) {
        super(parent, "Assign Tutor to Class", true);
        this.parentFrame = parent;
        this.dataManager = dataManager;
        
        loadData();
        initializeComponents();
        setupLayout();
        setupWindow();
    }
    
    private void loadData() {
        // Load tutors
        tutorMap = new HashMap<>();
        List<Tutor> tutors = dataManager.getAllTutors();
        for (Tutor tutor : tutors) {
            tutorMap.put(tutor.getUserId(), tutor);
        }
        
        // Load existing classes
        classMap = new HashMap<>();
        List<ClassInfo> classes = dataManager.getAllClasses();
        for (ClassInfo classInfo : classes) {
            classMap.put(classInfo.getClassId(), classInfo);
        }
        
        // Create subject mapping based on level
        subjectMap = new HashMap<>();
        subjectMap.put("Form 1", "Mathematics,English,Malay,Science,History,Geography,Pendidikan Seni,RBT");
        subjectMap.put("Form 2", "Mathematics,English,Malay,Science,History,Geography,Pendidikan Seni,RBT,Sejarah,Geografi");
        subjectMap.put("Form 3", "Mathematics,English,Bahasa Melayu,Science,Geography");
        subjectMap.put("Form 4", "Mathematics,English,Malay,Physics,Chemistry,Biology,History");
        subjectMap.put("Form 5", "Mathematics,English,Science,Physics,Chemistry,Add Maths");
    }
    
    private void initializeComponents() {
        // Assignment mode selection
        assignmentModeCombo = new JComboBox<>(new String[]{
            "Assign to Existing Class", "Create New Class"
        });
        assignmentModeCombo.addActionListener(e -> switchAssignmentMode());
        
        // Tutor selection
        tutorCombo = new JComboBox<>();
        tutorCombo.addItem(new TutorItem("", "Select Tutor...", ""));
        for (String tutorId : tutorMap.keySet()) {
            Tutor tutor = tutorMap.get(tutorId);
            tutorCombo.addItem(new TutorItem(tutorId, tutor.getName(), tutor.getEmail()));
        }
        tutorCombo.addActionListener(e -> updateTutorInfo());
        
        // Existing class selection
        existingClassCombo = new JComboBox<>();
        existingClassCombo.addItem(new ClassItem("", "Select Existing Class...", "", 0.0));
        for (String classId : classMap.keySet()) {
            ClassInfo classInfo = classMap.get(classId);
            existingClassCombo.addItem(new ClassItem(
                classId, 
                classInfo.getSubject(), 
                classInfo.getTutorId(), 
                classInfo.getFee()
            ));
        }
        existingClassCombo.addActionListener(e -> updateExistingClassInfo());
        
        // New class components
        levelCombo = new JComboBox<>(new String[]{
            "Select Level...", "Form 1", "Form 2", "Form 3", "Form 4", "Form 5"
        });
        levelCombo.addActionListener(e -> updateSubjectOptions());
        
        subjectCombo = new JComboBox<>();
        subjectCombo.addItem("Select Subject...");
        subjectCombo.addActionListener(e -> updateNewClassInfo());
        
        newClassIdField = createStyledTextField();
        newClassIdField.setText(generateNextClassId());
        newClassIdField.setEditable(false);
        newClassIdField.setBackground(SECONDARY_COLOR);
        
        descriptionField = createStyledTextField();
        scheduleField = createStyledTextField();
        scheduleField.setToolTipText("Format: YYYY-MM-DD; YYYY-MM-DD; YYYY-MM-DD");
        
        feeField = createStyledTextField();
        feeField.setText("50.0");
        
        // Info labels
        tutorInfoLabel = new JLabel("Select a tutor to view information");
        tutorInfoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        tutorInfoLabel.setForeground(new Color(107, 114, 128));
        
        classInfoLabel = new JLabel("Select assignment mode and class");
        classInfoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        classInfoLabel.setForeground(new Color(107, 114, 128));
        
        // Create card layout for assignment content
        assignmentCardLayout = new CardLayout();
        assignmentContentPanel = new JPanel(assignmentCardLayout);
        assignmentContentPanel.setBackground(BACKGROUND_COLOR);
        
        // Create panels for different assignment modes
        createExistingClassPanel();
        createNewClassPanel();
        
        assignmentContentPanel.add(existingClassPanel, "existing");
        assignmentContentPanel.add(newClassPanel, "new");
    }
    
    private void createExistingClassPanel() {
        existingClassPanel = new JPanel(new GridBagLayout());
        existingClassPanel.setBackground(Color.WHITE);
        existingClassPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                "Select Existing Class", 0, 0, new Font("Segoe UI", Font.BOLD, 14), TEXT_COLOR),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Existing class selection
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        existingClassPanel.add(createFieldLabel("Select Class:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        existingClassPanel.add(existingClassCombo, gbc);
        
        // Class details info
        JTextArea classDetailsArea = new JTextArea(4, 30);
        classDetailsArea.setEditable(false);
        classDetailsArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        classDetailsArea.setBackground(SECONDARY_COLOR);
        classDetailsArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        classDetailsArea.setText("Select a class to view details...");
        
        JScrollPane detailsScroll = new JScrollPane(classDetailsArea);
        detailsScroll.setBorder(BorderFactory.createTitledBorder("Class Details"));
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        existingClassPanel.add(detailsScroll, gbc);
        
        // Store reference for updates
        existingClassPanel.putClientProperty("detailsArea", classDetailsArea);
    }
    
    private void createNewClassPanel() {
        newClassPanel = new JPanel(new GridBagLayout());
        newClassPanel.setBackground(Color.WHITE);
        newClassPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                "Create New Class", 0, 0, new Font("Segoe UI", Font.BOLD, 14), TEXT_COLOR),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Class ID
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        newClassPanel.add(createFieldLabel("Class ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        newClassPanel.add(newClassIdField, gbc);
        row++;
        
        // Level selection
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        newClassPanel.add(createFieldLabel("Level:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        newClassPanel.add(levelCombo, gbc);
        row++;
        
        // Subject selection
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        newClassPanel.add(createFieldLabel("Subject:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        newClassPanel.add(subjectCombo, gbc);
        row++;
        
        // Description
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        newClassPanel.add(createFieldLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        newClassPanel.add(descriptionField, gbc);
        row++;
        
        // Schedule
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        newClassPanel.add(createFieldLabel("Schedule:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        newClassPanel.add(scheduleField, gbc);
        row++;
        
        // Fee
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        newClassPanel.add(createFieldLabel("Fee (RM):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        newClassPanel.add(feeField, gbc);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main form
        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));
        
        JLabel titleLabel = new JLabel("Assign Tutor to Class");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Assign tutors to existing classes or create new class assignments");
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
        // ENHANCED: Wrap main panel in scroll pane
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        
        // Tutor Selection Section
        JPanel tutorSection = createTutorSelectionSection();
        mainPanel.add(tutorSection);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Assignment Mode Section
        JPanel modeSection = createAssignmentModeSection();
        mainPanel.add(modeSection);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Assignment Content Section (CardLayout)
        mainPanel.add(assignmentContentPanel);
        
        // ENHANCED: Create scrollable wrapper
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Create wrapper panel to return
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(BACKGROUND_COLOR);
        wrapperPanel.add(scrollPane, BorderLayout.CENTER);
        
        return wrapperPanel;
    }
    
    private JPanel createTutorSelectionSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                "Tutor Selection", 0, 0, new Font("Segoe UI", Font.BOLD, 14), TEXT_COLOR),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Tutor selection
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(createFieldLabel("Select Tutor:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(tutorCombo, gbc);
        
        // Tutor info
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(tutorInfoLabel, gbc);
        
        return panel;
    }
    
    private JPanel createAssignmentModeSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                "Assignment Mode", 0, 0, new Font("Segoe UI", Font.BOLD, 14), TEXT_COLOR),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Assignment mode selection
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(createFieldLabel("Assignment Mode:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(assignmentModeCombo, gbc);
        
        // Class info
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(classInfoLabel, gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panel.setBackground(BACKGROUND_COLOR);
        
        JButton assignButton = createStyledButton("✅ Assign Tutor", ACCENT_COLOR);
        JButton cancelButton = createStyledButton("❌ Cancel", SECONDARY_COLOR);
        
        assignButton.addActionListener(e -> performAssignment());
        cancelButton.addActionListener(e -> dispose());
        
        panel.add(assignButton);
        panel.add(cancelButton);
        
        return panel;
    }
    
    private void switchAssignmentMode() {
        String mode = (String) assignmentModeCombo.getSelectedItem();
        if ("Assign to Existing Class".equals(mode)) {
            assignmentCardLayout.show(assignmentContentPanel, "existing");
            classInfoLabel.setText("Select an existing class to reassign tutor");
        } else {
            assignmentCardLayout.show(assignmentContentPanel, "new");
            classInfoLabel.setText("Create a new class and assign tutor");
        }
    }
    
    private void updateTutorInfo() {
        TutorItem selected = (TutorItem) tutorCombo.getSelectedItem();
        if (selected == null || selected.getId().isEmpty()) {
            tutorInfoLabel.setText("Select a tutor to view information");
            return;
        }
        
        Tutor tutor = tutorMap.get(selected.getId());
        if (tutor != null) {
            tutorInfoLabel.setText(String.format("📧 %s | 📞 %s | 🎂 %s", 
                tutor.getEmail(), tutor.getPhone(), tutor.getDateOfBirth()));
        }
    }
    
    private void updateExistingClassInfo() {
        ClassItem selected = (ClassItem) existingClassCombo.getSelectedItem();
        if (selected == null || selected.getClassId().isEmpty()) {
            JTextArea detailsArea = (JTextArea) existingClassPanel.getClientProperty("detailsArea");
            if (detailsArea != null) {
                detailsArea.setText("Select a class to view details...");
            }
            return;
        }
        
        ClassInfo classInfo = classMap.get(selected.getClassId());
        if (classInfo != null) {
            Tutor currentTutor = tutorMap.get(classInfo.getTutorId());
            String currentTutorName = currentTutor != null ? currentTutor.getName() : "Unknown";
            
            StringBuilder details = new StringBuilder();
            details.append("Class ID: ").append(classInfo.getClassId()).append("\n");
            details.append("Subject: ").append(classInfo.getSubject()).append("\n");
            details.append("Description: ").append(classInfo.getDescription()).append("\n");
            details.append("Current Tutor: ").append(currentTutorName).append(" (").append(classInfo.getTutorId()).append(")\n");
            details.append("Schedule: ").append(classInfo.getSchedule()).append("\n");
            details.append("Fee: RM").append(String.format("%.2f", classInfo.getFee()));
            
            JTextArea detailsArea = (JTextArea) existingClassPanel.getClientProperty("detailsArea");
            if (detailsArea != null) {
                detailsArea.setText(details.toString());
            }
        }
    }
    
    private void updateSubjectOptions() {
        String selectedLevel = (String) levelCombo.getSelectedItem();
        subjectCombo.removeAllItems();
        subjectCombo.addItem("Select Subject...");
        
        if (selectedLevel == null || selectedLevel.equals("Select Level...")) {
            return;
        }
        
        String subjects = subjectMap.get(selectedLevel);
        if (subjects != null) {
            for (String subject : subjects.split(",")) {
                subjectCombo.addItem(subject.trim());
            }
        }
        
        updateNewClassInfo();
    }
    
    private void updateNewClassInfo() {
        String level = (String) levelCombo.getSelectedItem();
        String subject = (String) subjectCombo.getSelectedItem();
        
        if (level != null && !level.equals("Select Level...") && 
            subject != null && !subject.equals("Select Subject...")) {
            
            String className = subject + " " + level;
            classInfoLabel.setText("New Class: " + className);
            
            // Auto-generate description if empty
            if (descriptionField.getText().trim().isEmpty()) {
                descriptionField.setText(generateDescription(subject, level));
            }
            
            // Set default fee based on level
            if (feeField.getText().trim().equals("50.0") || feeField.getText().trim().isEmpty()) {
                feeField.setText(getDefaultFee(level));
            }
        } else {
            classInfoLabel.setText("Select level and subject to create new class");
        }
    }
    
    private void performAssignment() {
        try {
            // Validation
            if (!validateAssignment()) {
                return;
            }
            
            TutorItem selectedTutor = (TutorItem) tutorCombo.getSelectedItem();
            String tutorId = selectedTutor.getId();
            String mode = (String) assignmentModeCombo.getSelectedItem();
            
            boolean success = false;
            String message = "";
            
            if ("Assign to Existing Class".equals(mode)) {
                success = assignToExistingClass(tutorId);
                message = "Tutor successfully assigned to existing class!";
            } else {
                success = createNewClassWithTutor(tutorId);
                message = "New class created and tutor assigned successfully!";
            }
            
            if (success) {
                assignmentSuccessful = true;
                JOptionPane.showMessageDialog(this, message, 
                    "Assignment Successful", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to complete assignment. Please try again.", 
                    "Assignment Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error during assignment: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private boolean assignToExistingClass(String tutorId) {
        ClassItem selectedClass = (ClassItem) existingClassCombo.getSelectedItem();
        String classId = selectedClass.getClassId();
        
        try {
            // Update the class file with new tutor assignment
            return updateClassTutorInFile(classId, tutorId);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean createNewClassWithTutor(String tutorId) {
        String level = (String) levelCombo.getSelectedItem();
        String subject = (String) subjectCombo.getSelectedItem();
        String classId = newClassIdField.getText().trim();
        String description = descriptionField.getText().trim();
        String schedule = scheduleField.getText().trim();
        String feeStr = feeField.getText().trim();
        
        try {
            double fee = Double.parseDouble(feeStr);
            String className = subject + " " + level;
            
            // Add new class to file
            return addClassToFile(classId, tutorId, className, description, schedule, fee);
        } catch (NumberFormatException e) {
            showValidationError("Please enter a valid fee amount!");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean updateClassTutorInFile(String classId, String newTutorId) throws IOException {
        File file = new File("class.txt");
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
        
        // Update the specific class line
        for (int i = 0; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",");
            if (parts.length >= 6 && parts[0].trim().equals(classId)) {
                // Update tutor ID (second column)
                parts[1] = newTutorId;
                lines.set(i, String.join(",", parts));
                found = true;
                break;
            }
        }
        
        if (found) {
            // Write back to file
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (String line : lines) {
                    writer.println(line);
                }
            }
        }
        
        return found;
    }
    
    private boolean addClassToFile(String classId, String tutorId, String className, String description, String schedule, double fee) throws IOException {
        String newLine = String.format("%s,%s,%s,%s,%s,%.1f",
            classId, tutorId, className, description, schedule, fee);
        
        // Append to class.txt
        try (FileWriter fw = new FileWriter("class.txt", true)) {
            fw.write(newLine + "\n");
            return true;
        }
    }
    
    private boolean validateAssignment() {
        // Check tutor selection
        TutorItem selectedTutor = (TutorItem) tutorCombo.getSelectedItem();
        if (selectedTutor == null || selectedTutor.getId().isEmpty()) {
            showValidationError("Please select a tutor!");
            return false;
        }
        
        String mode = (String) assignmentModeCombo.getSelectedItem();
        
        if ("Assign to Existing Class".equals(mode)) {
            // Validate existing class selection
            ClassItem selectedClass = (ClassItem) existingClassCombo.getSelectedItem();
            if (selectedClass == null || selectedClass.getClassId().isEmpty()) {
                showValidationError("Please select an existing class!");
                return false;
            }
        } else {
            // Validate new class creation
            String level = (String) levelCombo.getSelectedItem();
            if (level == null || level.equals("Select Level...")) {
                showValidationError("Please select a level!");
                return false;
            }
            
            String subject = (String) subjectCombo.getSelectedItem();
            if (subject == null || subject.equals("Select Subject...")) {
                showValidationError("Please select a subject!");
                return false;
            }
            
            if (descriptionField.getText().trim().isEmpty()) {
                showValidationError("Please enter a description!");
                descriptionField.requestFocus();
                return false;
            }
            
            if (scheduleField.getText().trim().isEmpty()) {
                showValidationError("Please enter a schedule!");
                scheduleField.requestFocus();
                return false;
            }
            
            try {
                double fee = Double.parseDouble(feeField.getText().trim());
                if (fee <= 0) {
                    showValidationError("Fee must be greater than 0!");
                    feeField.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                showValidationError("Please enter a valid fee amount!");
                feeField.requestFocus();
                return false;
            }
            
            // Check if class ID already exists
            if (isClassIdExists(newClassIdField.getText().trim())) {
                newClassIdField.setText(generateNextClassId());
                showValidationError("Class ID already exists! A new ID has been generated.");
                return false;
            }
        }
        
        return true;
    }
    
    private boolean isClassIdExists(String classId) {
        return classMap.containsKey(classId);
    }
    
    private String generateNextClassId() {
        int maxId = 0;
        for (String classId : classMap.keySet()) {
            if (classId.startsWith("CL")) {
                try {
                    int num = Integer.parseInt(classId.substring(2));
                    maxId = Math.max(maxId, num);
                } catch (NumberFormatException ignored) {}
            }
        }
        return String.format("CL%03d", maxId + 1);
    }
    
    private String generateDescription(String subject, String level) {
        Map<String, String> descriptions = new HashMap<>();
        descriptions.put("Mathematics", "Advanced mathematical concepts and problem solving");
        descriptions.put("English", "Language skills development and literature");
        descriptions.put("Malay", "Bahasa Melayu language and communication skills");
        descriptions.put("Science", "Scientific concepts and practical applications");
        descriptions.put("History", "Historical events and cultural understanding");
        descriptions.put("Geography", "Physical and human geography studies");
        descriptions.put("Pendidikan Seni", "Art education and creative expression");
        descriptions.put("RBT", "Design and technology skills");
        descriptions.put("Physics", "Physics principles and applications");
        descriptions.put("Chemistry", "Chemical concepts and laboratory work");
        descriptions.put("Biology", "Biological systems and life sciences");
        descriptions.put("Add Maths", "Additional mathematics for advanced students");
        descriptions.put("Sejarah", "Malaysian and world history");
        descriptions.put("Geografi", "Geographic systems and mapping");
        descriptions.put("Bahasa Melayu", "Advanced Malay language studies");
        
        String baseDescription = descriptions.getOrDefault(subject, "Comprehensive " + subject.toLowerCase() + " studies");
        return baseDescription + " for " + level + " students";
    }
    
    private String getDefaultFee(String level) {
        switch (level) {
            case "Form 1":
            case "Form 2":
                return "45.0";
            case "Form 3":
                return "50.0";
            case "Form 4":
                return "55.0";
            case "Form 5":
                return "60.0";
            default:
                return "50.0";
        }
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
    
    private void setupWindow() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(700, 650);
        setLocationRelativeTo(parentFrame);
        setResizable(true);
        setMinimumSize(new Dimension(650, 600));
    }
    
    public boolean isAssignmentSuccessful() {
        return assignmentSuccessful;
    }
}