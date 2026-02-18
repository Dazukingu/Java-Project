import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

class ClassDetailsDialog extends JDialog {
    private TutorDashboard parentFrame;
    private String classId;
    

    private final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private final Color SECONDARY_COLOR = new Color(243, 244, 246);
    private final Color ACCENT_COLOR = new Color(16, 185, 129);
    private final Color TEXT_COLOR = new Color(31, 41, 55);
    private final Color BACKGROUND_COLOR = new Color(249, 250, 251);
    
    public ClassDetailsDialog(TutorDashboard parent, String classId) {
        super(parent, "Class Details", true);
        this.parentFrame = parent;
        this.classId = classId;
        
        initializeComponents();
        setupWindow();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        String[] classData = loadClassData();
        if (classData == null) {
            JOptionPane.showMessageDialog(this, "Class not found!", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(30, 30, 30, 30)
        ));
        
        JLabel titleLabel = new JLabel("Class Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel classInfoPanel = createClassInfoPanel(classData);
        
        JPanel enrollmentPanel = createEnrollmentPanel();
        
        JButton closeButton = createStyledButton("Close", SECONDARY_COLOR);
        closeButton.addActionListener(e -> dispose());
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        mainPanel.add(classInfoPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(enrollmentPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        mainPanel.add(closeButton);
        
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createClassInfoPanel(String[] classData) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                "Class Information", 0, 0,
                new Font("Segoe UI", Font.BOLD, 16), TEXT_COLOR),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        addDetailRow(panel, "Class ID:", classData[0]);
        addDetailRow(panel, "Course Name:", classData[2]);
        addDetailRow(panel, "Description:", classData[3]);
        addDetailRow(panel, "Schedule:", formatSchedule(classData[4]));
        addDetailRow(panel, "Fee per Class:", "RM " + classData[5]);
        addDetailRow(panel, "Tutor ID:", classData[1]);
        
        return panel;
    }
    
    private JPanel createEnrollmentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                "Student Enrollment", 0, 0,
                new Font("Segoe UI", Font.BOLD, 16), TEXT_COLOR),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        int studentCount = getStudentCount();
        
        JLabel enrollmentLabel = new JLabel("Enrolled Students: " + studentCount);
        enrollmentLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        enrollmentLabel.setForeground(ACCENT_COLOR);
        
        JTextArea studentListArea = new JTextArea(8, 40);
        studentListArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        studentListArea.setEditable(false);
        studentListArea.setBackground(Color.WHITE);
        studentListArea.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
        
        String studentList = getStudentList();
        if (studentList.isEmpty()) {
            studentListArea.setText("No students enrolled in this class yet.");
        } else {
            studentListArea.setText(studentList);
        }
        
        JScrollPane scrollPane = new JScrollPane(studentListArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
        
        panel.add(enrollmentLabel, BorderLayout.NORTH);
        panel.add(Box.createRigidArea(new Dimension(0, 10)), BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void addDetailRow(JPanel parent, String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setBorder(new EmptyBorder(8, 0, 8, 0));
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelComp.setForeground(TEXT_COLOR);
        labelComp.setPreferredSize(new Dimension(150, 25));
        
        JTextArea valueComp = new JTextArea(value);
        valueComp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueComp.setForeground(TEXT_COLOR);
        valueComp.setEditable(false);
        valueComp.setOpaque(false);
        valueComp.setLineWrap(true);
        valueComp.setWrapStyleWord(true);
        
        row.add(labelComp, BorderLayout.WEST);
        row.add(valueComp, BorderLayout.CENTER);
        
        parent.add(row);
    }
    
    private String[] loadClassData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("class.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 6);
                if (parts.length == 6 && parts[0].equals(classId)) {
                    return parts;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private String formatSchedule(String schedule) {
        return schedule.replace("; ", "\n");
    }
    
    private int getStudentCount() {
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
    
    private String getStudentList() {
        StringBuilder studentList = new StringBuilder();
        int count = 1;
        
        try (BufferedReader reader = new BufferedReader(new FileReader("students.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 10) {
                    String studentId = parts[0];
                    String studentName = parts[3];
                    String email = parts[4];
                    String[] studentClasses = parts[9].split(";");
                    
                    for (String studentClass : studentClasses) {
                        if (studentClass.trim().equals(classId)) {
                            studentList.append(count).append(". ")
                                     .append(studentName).append(" (").append(studentId).append(")")
                                     .append("\n   Email: ").append(email)
                                     .append("\n\n");
                            count++;
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return studentList.toString();
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
    
    private void setupWindow() {
        setSize(600, 500);
        setLocationRelativeTo(parentFrame);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(true);
    }
}

class EditClassDialog extends JDialog {
    private TutorDashboard parentFrame;
    private String classId;
    private String tutorId;
    
    // Colors
    private final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private final Color SECONDARY_COLOR = new Color(243, 244, 246);
    private final Color ACCENT_COLOR = new Color(16, 185, 129);
    private final Color ERROR_COLOR = new Color(239, 68, 68);
    private final Color TEXT_COLOR = new Color(31, 41, 55);
    
    private JTextField classIdField, courseNameField, chargeField;
    private JTextArea descriptionArea;
    private JTextArea scheduleArea;
    
    public EditClassDialog(TutorDashboard parent, String classId, String tutorId) {
        super(parent, "Edit Class", true);
        this.parentFrame = parent;
        this.classId = classId;
        this.tutorId = tutorId;
        
        initializeComponents();
        setupLayout();
        loadClassData();
        setupWindow();
    }
    
    private void initializeComponents() {
        classIdField = createStyledTextField();
        classIdField.setEditable(false);
        classIdField.setBackground(SECONDARY_COLOR);
        
        courseNameField = createStyledTextField();
        chargeField = createStyledTextField();
        
        descriptionArea = createStyledTextArea(4, 30);
        scheduleArea = createStyledTextArea(6, 30);
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
        
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        // Title
        JLabel titleLabel = new JLabel("Edit Class");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Form panel
        JPanel formPanel = createFormPanel();
        
        // Buttons panel
        JPanel buttonsPanel = createButtonsPanel();
        
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        mainPanel.add(formPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        mainPanel.add(buttonsPanel);
        
        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                "Class Information", 0, 0,
                new Font("Segoe UI", Font.BOLD, 16), TEXT_COLOR),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 15, 12, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Class ID
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createFieldLabel("Class ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(classIdField, gbc);
        
        // Course Name
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(createFieldLabel("Course Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(courseNameField, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(createFieldLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.3;
        panel.add(new JScrollPane(descriptionArea), gbc);
        
        // Schedule
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        panel.add(createFieldLabel("Schedule:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.4;
        panel.add(new JScrollPane(scheduleArea), gbc);
        
        // Charge
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        panel.add(createFieldLabel("Charge (RM):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(chargeField, gbc);
        
        return panel;
    }
    
    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panel.setBackground(Color.WHITE);
        
        JButton updateButton = createStyledButton("Update Class", ACCENT_COLOR);
        JButton deleteButton = createStyledButton("Delete Class", ERROR_COLOR);
        JButton cancelButton = createStyledButton("Cancel", SECONDARY_COLOR);
        
        // Event listeners
        updateButton.addActionListener(e -> updateClass());
        deleteButton.addActionListener(e -> deleteClass());
        cancelButton.addActionListener(e -> dispose());
        
        panel.add(updateButton);
        panel.add(deleteButton);
        panel.add(cancelButton);
        
        return panel;
    }
    
    private void loadClassData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("class.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 6);
                if (parts.length == 6 && parts[0].equals(classId)) {
                    classIdField.setText(parts[0]);
                    courseNameField.setText(parts[2]);
                    descriptionArea.setText(parts[3]);
                    scheduleArea.setText(parts[4].replace("; ", "\n"));
                    chargeField.setText(parts[5]);
                    break;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading class data: " + e.getMessage());
        }
    }
    
    private void updateClass() {
        String courseName = courseNameField.getText().trim();
        String description = descriptionArea.getText().trim();
        String schedule = scheduleArea.getText().trim().replace("\n", "; ");
        String chargeText = chargeField.getText().trim();
        
        // Validation
        if (courseName.isEmpty() || description.isEmpty() || schedule.isEmpty() || chargeText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        double charge;
        try {
            charge = Double.parseDouble(chargeText);
            if (charge <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Charge must be a valid positive number.", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String newRecord = classId + "," + tutorId + "," + courseName + "," + 
                          description + "," + schedule + "," + charge;
        
        // Update file
        if (updateClassInFile(newRecord)) {
            JOptionPane.showMessageDialog(this, "✅ Class updated successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            parentFrame.refreshData();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Error updating class!", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteClass() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "⚠️ Are you sure you want to delete this class?\n\n" +
            "This action cannot be undone and may affect enrolled students.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (deleteClassFromFile()) {
                JOptionPane.showMessageDialog(this, "✅ Class deleted successfully.", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                parentFrame.refreshData();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Error deleting class!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean updateClassInFile(String newRecord) {
        java.io.File inputFile = new java.io.File("class.txt");
        java.io.File tempFile = new java.io.File("class_temp.txt");
        boolean updated = false;
        
        try (BufferedReader reader = new BufferedReader(new java.io.FileReader(inputFile));
             java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(tempFile))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(classId + ",")) {
                    writer.println(newRecord);
                    updated = true;
                } else {
                    writer.println(line);
                }
            }
            
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        
        return inputFile.delete() && tempFile.renameTo(inputFile) && updated;
    }
    
    private boolean deleteClassFromFile() {
        java.io.File originalFile = new java.io.File("class.txt");
        java.io.File tempFile = new java.io.File("class_temp.txt");
        boolean deleted = false;
        
        try (BufferedReader reader = new BufferedReader(new java.io.FileReader(originalFile));
             java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(tempFile))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith(classId + ",")) {
                    writer.println(line);
                } else {
                    deleted = true;
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        
        return originalFile.delete() && tempFile.renameTo(originalFile) && deleted;
    }
    
    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_COLOR);
        label.setPreferredSize(new Dimension(120, 25));
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
    
    private void setupWindow() {
        setSize(650, 600);
        setLocationRelativeTo(parentFrame);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(true);
    }
}