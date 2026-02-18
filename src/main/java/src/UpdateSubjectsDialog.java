import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

/**
 * UpdateSubjectsDialog - Dialog for updating student subject enrollment (1-3 subjects)
 */
public class UpdateSubjectsDialog extends JDialog {
    private DataManager dataManager;
    private ReceptionistDashboard parentFrame;
    private String studentId;
    private Student student;
    
    // Color scheme
    private final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private final Color SECONDARY_COLOR = new Color(243, 244, 246);
    private final Color ACCENT_COLOR = new Color(16, 185, 129);
    private final Color ERROR_COLOR = new Color(239, 68, 68);
    private final Color TEXT_COLOR = new Color(31, 41, 55);
    private final Color BACKGROUND_COLOR = new Color(249, 250, 251);
    
    // Data lists
    private List<ClassInfo> availableClasses;
    private List<ClassInfo> enrolledClasses;
    
    // UI Components
    private JLabel studentInfoLabel;
    private JPanel availableSubjectsPanel;
    private JPanel enrolledSubjectsPanel;
    private List<JCheckBox> availableCheckBoxes;
    private List<JCheckBox> enrolledCheckBoxes;
    private JLabel totalFeeLabel;
    
    public UpdateSubjectsDialog(ReceptionistDashboard parent, DataManager dataManager, String studentId) {
        super(parent, "Update Subject Enrollment", true);
        this.parentFrame = parent;
        this.dataManager = dataManager;
        this.studentId = studentId;
        this.student = dataManager.getStudentById(studentId);
        
        this.availableClasses = new ArrayList<>();
        this.enrolledClasses = new ArrayList<>();
        this.availableCheckBoxes = new ArrayList<>();
        this.enrolledCheckBoxes = new ArrayList<>();
        
        initializeComponents();
        setupLayout();
        loadData();
        setupWindow();
    }
    
    private void initializeComponents() {
        // Student info label
        studentInfoLabel = new JLabel();
        studentInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        studentInfoLabel.setForeground(TEXT_COLOR);
        
        // Available subjects panel
        availableSubjectsPanel = new JPanel();
        availableSubjectsPanel.setLayout(new BoxLayout(availableSubjectsPanel, BoxLayout.Y_AXIS));
        availableSubjectsPanel.setBackground(Color.WHITE);
        availableSubjectsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Enrolled subjects panel
        enrolledSubjectsPanel = new JPanel();
        enrolledSubjectsPanel.setLayout(new BoxLayout(enrolledSubjectsPanel, BoxLayout.Y_AXIS));
        enrolledSubjectsPanel.setBackground(Color.WHITE);
        enrolledSubjectsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Total fee label
        totalFeeLabel = new JLabel("Total Fee: RM 0.00");
        totalFeeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalFeeLabel.setForeground(ACCENT_COLOR);
        totalFeeLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Title
        JLabel titleLabel = new JLabel("Update Subject Enrollment");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Student info panel
        JPanel studentInfoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        studentInfoPanel.setBackground(Color.WHITE);
        studentInfoPanel.add(studentInfoLabel);
        
        // Subjects panel with horizontal layout
        JPanel subjectsPanel = new JPanel(new BorderLayout());
        subjectsPanel.setBackground(Color.WHITE);
        subjectsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Available panel (left)
        JPanel availablePanel = new JPanel(new BorderLayout());
        availablePanel.setBackground(Color.WHITE);
        availablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1), 
            "Available Subjects", 0, 0, 
            new Font("Segoe UI", Font.BOLD, 14), TEXT_COLOR));
        
        JScrollPane availableScrollPane = new JScrollPane(availableSubjectsPanel);
        availableScrollPane.setPreferredSize(new Dimension(250, 300));
        availableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
        availablePanel.add(availableScrollPane, BorderLayout.CENTER);
        
        // Control panel (center)
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));
        
        JButton addButton = createStyledButton("Add Selected →", ACCENT_COLOR);
        JButton removeButton = createStyledButton("← Remove Selected", ERROR_COLOR);
        
        controlPanel.add(Box.createVerticalGlue());
        controlPanel.add(addButton);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        controlPanel.add(removeButton);
        controlPanel.add(Box.createVerticalGlue());
        
        // Enrolled panel (right)
        JPanel enrolledPanel = new JPanel(new BorderLayout());
        enrolledPanel.setBackground(Color.WHITE);
        enrolledPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1), 
            "Currently Enrolled", 0, 0, 
            new Font("Segoe UI", Font.BOLD, 14), TEXT_COLOR));
        
        JScrollPane enrolledScrollPane = new JScrollPane(enrolledSubjectsPanel);
        enrolledScrollPane.setPreferredSize(new Dimension(250, 300));
        enrolledScrollPane.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
        enrolledPanel.add(enrolledScrollPane, BorderLayout.CENTER);
        
        // Add panels to subjects panel
        subjectsPanel.add(availablePanel, BorderLayout.WEST);
        subjectsPanel.add(controlPanel, BorderLayout.CENTER);
        subjectsPanel.add(enrolledPanel, BorderLayout.EAST);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonsPanel.setBackground(Color.WHITE);
        
        JButton updateButton = createStyledButton("Update Enrollment", PRIMARY_COLOR);
        JButton cancelButton = createStyledButton("Cancel", SECONDARY_COLOR);
        
        buttonsPanel.add(updateButton);
        buttonsPanel.add(cancelButton);
        
        // Event listeners
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addSubjects();
            }
        });
        
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeSubjects();
            }
        });
        
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateEnrollment();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        // Add components to main panel
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(studentInfoPanel, BorderLayout.NORTH);
        mainPanel.add(subjectsPanel, BorderLayout.CENTER);
        mainPanel.add(totalFeeLabel, BorderLayout.SOUTH);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
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
    
    private void setupWindow() {
        setSize(800, 600);
        setLocationRelativeTo(parentFrame);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }
    
    private void loadData() {
        // Set student info
        studentInfoLabel.setText(String.format("Student: %s (%s) - %s", 
            student.getName(), student.getUserId(), student.getLevel()));
        
        // Load available classes for the student's level
        List<ClassInfo> allClasses = dataManager.getSubjectsByLevel(student.getLevel());
        
        // Get currently enrolled class IDs
        String[] enrolledClassIds = student.getClassIds();
        List<String> enrolledIdsList = new ArrayList<>();
        for (String classId : enrolledClassIds) {
            if (classId != null && !classId.trim().isEmpty()) {
                enrolledIdsList.add(classId);
            }
        }
        
        // Separate available and enrolled classes
        availableClasses.clear();
        enrolledClasses.clear();
        
        for (ClassInfo classInfo : allClasses) {
            if (enrolledIdsList.contains(classInfo.getClassId())) {
                enrolledClasses.add(classInfo);
            } else {
                availableClasses.add(classInfo);
            }
        }
        
        // Create checkboxes for available classes
        createAvailableCheckBoxes();
        
        // Create checkboxes for enrolled classes
        createEnrolledCheckBoxes();
        
        updateTotalFee();
    }
    
    private void createAvailableCheckBoxes() {
        availableSubjectsPanel.removeAll();
        availableCheckBoxes.clear();
        
        if (availableClasses.isEmpty()) {
            JLabel noClassesLabel = new JLabel("No available classes");
            noClassesLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            noClassesLabel.setForeground(new Color(107, 114, 128));
            availableSubjectsPanel.add(noClassesLabel);
        } else {
            for (ClassInfo classInfo : availableClasses) {
                JCheckBox checkBox = new JCheckBox();
                checkBox.setText(String.format("%s - %s (RM%.2f)", 
                    classInfo.getClassId(), 
                    classInfo.getSubject(), 
                    classInfo.getFee()));
                checkBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                checkBox.setBackground(Color.WHITE);
                checkBox.setAlignmentX(Component.LEFT_ALIGNMENT);
                
                availableCheckBoxes.add(checkBox);
                availableSubjectsPanel.add(checkBox);
                availableSubjectsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }
        
        availableSubjectsPanel.revalidate();
        availableSubjectsPanel.repaint();
    }
    
    private void createEnrolledCheckBoxes() {
        enrolledSubjectsPanel.removeAll();
        enrolledCheckBoxes.clear();
        
        if (enrolledClasses.isEmpty()) {
            JLabel noClassesLabel = new JLabel("No enrolled classes");
            noClassesLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            noClassesLabel.setForeground(new Color(107, 114, 128));
            enrolledSubjectsPanel.add(noClassesLabel);
        } else {
            for (ClassInfo classInfo : enrolledClasses) {
                JCheckBox checkBox = new JCheckBox();
                checkBox.setText(String.format("%s - %s (RM%.2f)", 
                    classInfo.getClassId(), 
                    classInfo.getSubject(), 
                    classInfo.getFee()));
                checkBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                checkBox.setBackground(Color.WHITE);
                checkBox.setAlignmentX(Component.LEFT_ALIGNMENT);
                
                enrolledCheckBoxes.add(checkBox);
                enrolledSubjectsPanel.add(checkBox);
                enrolledSubjectsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }
        
        enrolledSubjectsPanel.revalidate();
        enrolledSubjectsPanel.repaint();
    }
    
    private void addSubjects() {
        List<ClassInfo> selectedClasses = new ArrayList<>();
        
        // Get selected available classes
        for (int i = 0; i < availableCheckBoxes.size(); i++) {
            if (availableCheckBoxes.get(i).isSelected()) {
                selectedClasses.add(availableClasses.get(i));
            }
        }
        
        if (selectedClasses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select classes to add!", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Check if adding would exceed the limit (3 subjects max)
        if (enrolledClasses.size() + selectedClasses.size() > 3) {
            JOptionPane.showMessageDialog(this, 
                "Cannot enroll in more than 3 classes!\n" +
                "Currently enrolled: " + enrolledClasses.size() + "\n" +
                "Trying to add: " + selectedClasses.size() + "\n" +
                "Maximum allowed: 3", 
                "Enrollment Limit", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Move classes from available to enrolled
        for (ClassInfo classInfo : selectedClasses) {
            availableClasses.remove(classInfo);
            enrolledClasses.add(classInfo);
        }
        
        // Recreate checkboxes
        createAvailableCheckBoxes();
        createEnrolledCheckBoxes();
        updateTotalFee();
    }
    
    private void removeSubjects() {
        List<ClassInfo> selectedClasses = new ArrayList<>();
        
        // Get selected enrolled classes
        for (int i = 0; i < enrolledCheckBoxes.size(); i++) {
            if (enrolledCheckBoxes.get(i).isSelected()) {
                selectedClasses.add(enrolledClasses.get(i));
            }
        }
        
        if (selectedClasses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select classes to remove!", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Check if removing would go below minimum (1 subject)
        int currentEnrolled = enrolledClasses.size();
        int toRemove = selectedClasses.size();
        int afterRemoval = currentEnrolled - toRemove;
        
        if (afterRemoval < 1) {
            JOptionPane.showMessageDialog(this, 
                "Cannot remove selected classes! Student must be enrolled in at least 1 subject.\n" +
                "Currently enrolled: " + currentEnrolled + "\n" +
                "Trying to remove: " + toRemove + "\n" +
                "Would result in: " + afterRemoval + " subjects", 
                "Minimum Enrollment Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Move classes from enrolled to available
        for (ClassInfo classInfo : selectedClasses) {
            enrolledClasses.remove(classInfo);
            availableClasses.add(classInfo);
        }
        
        // Recreate checkboxes
        createAvailableCheckBoxes();
        createEnrolledCheckBoxes();
        updateTotalFee();
    }
    
    private void updateTotalFee() {
        double totalFee = 0.0;
        int enrolledCount = enrolledClasses.size();
        
        for (ClassInfo classInfo : enrolledClasses) {
            totalFee += classInfo.getFee();
        }
        
        // Update display with requirement status (1-3 subjects allowed)
        String feeText = String.format("Total Fee: RM %.2f (%d/3 subjects)", totalFee, enrolledCount);
        
        if (enrolledCount == 0) {
            feeText += " - MINIMUM 1 REQUIRED";
            totalFeeLabel.setForeground(ERROR_COLOR);
        } else if (enrolledCount >= 1 && enrolledCount <= 3) {
            if (enrolledCount == 3) {
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
    
    private void updateEnrollment() {
        // Check minimum requirement (at least 1 subject)
        if (enrolledClasses.size() < 1) {
            JOptionPane.showMessageDialog(this, 
                "Student must be enrolled in at least 1 subject!\n" +
                "Currently enrolled: " + enrolledClasses.size() + "\n" +
                "Please add at least 1 subject.", 
                "Minimum Enrollment Required", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check maximum limit (3 subjects max)
        if (enrolledClasses.size() > 3) {
            JOptionPane.showMessageDialog(this, 
                "Student cannot be enrolled in more than 3 subjects!\n" +
                "Currently enrolled: " + enrolledClasses.size() + "\n" +
                "Please remove " + (enrolledClasses.size() - 3) + " subject(s).", 
                "Maximum Enrollment Exceeded", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get new class ID list
        List<String> newClassIds = new ArrayList<>();
        double totalFee = 0.0;
        
        for (ClassInfo classInfo : enrolledClasses) {
            newClassIds.add(classInfo.getClassId());
            totalFee += classInfo.getFee();
        }
        
        // Show confirmation dialog
        String subjectWord = enrolledClasses.size() == 1 ? "subject" : "subjects";
        int confirm = JOptionPane.showConfirmDialog(this,
            "Update enrollment to " + enrolledClasses.size() + " " + subjectWord + "?\n" +
            "Total Fee: RM" + String.format("%.2f", totalFee),
            "Confirm Enrollment Update",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return; // User cancelled
        }
        
        // Update student enrollment
        boolean success = dataManager.updateStudentSubjects(studentId, newClassIds);
        
        if (success) {
            JOptionPane.showMessageDialog(this, 
                "Enrollment updated successfully!\n" +
                "Student: " + student.getName() + "\n" +
                "Subjects: " + enrolledClasses.size() + "\n" +
                "Total Fee: RM" + String.format("%.2f", totalFee), 
                "Update Successful", 
                JOptionPane.INFORMATION_MESSAGE);
            
            parentFrame.loadStudentData(); // Refresh the student table
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Error updating enrollment. Please try again.", 
                "Update Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}