// Enhanced AddClassDialog.java with improved schedule input
import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Enhanced AddClassDialog - Dialog for adding new classes with improved schedule input
 */
class AddClassDialog extends JDialog {
    private TutorDashboard parentFrame;
    private String tutorId;
    
    // Colors
    private final Color PRIMARY_COLOR = new Color(100, 149, 237);
    private final Color SECONDARY_COLOR = new Color(243, 244, 246);
    private final Color ACCENT_COLOR = new Color(76, 175, 80);
    private final Color ERROR_COLOR = new Color(244, 67, 54);
    private final Color TEXT_COLOR = new Color(31, 41, 55);
    
    // Components
    private JTextField classIdField, courseNameField, chargeField;
    private JTextArea descriptionArea; // Changed to JTextArea for better input
    private JPanel calendarPanel;
    private JLabel monthLabel, selectedDateLabel;
    private int currentMonth, currentYear;
    private List<LocalDate> selectedDates = new ArrayList<>();
    
    // Schedule input options
    private JTabbedPane scheduleTabPane;
    private JTextArea manualScheduleArea;
    
    public AddClassDialog(TutorDashboard parent, String tutorId) {
        super(parent, "Add New Class", true);
        this.parentFrame = parent;
        this.tutorId = tutorId;
        
        LocalDate today = LocalDate.now();
        currentMonth = today.getMonthValue();
        currentYear = today.getYear();
        
        initializeComponents();
        setupLayout();
        setupWindow();
    }
    
    private void initializeComponents() {
        // Text fields
        classIdField = createStyledTextField();
        classIdField.setText(generateNextClassId());
        classIdField.setEditable(false);
        classIdField.setBackground(SECONDARY_COLOR);
        
        courseNameField = createStyledTextField();
        chargeField = createStyledTextField();
        
        // Description area - Enhanced for better input
        descriptionArea = new JTextArea(4, 30);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        
        // Calendar components
        calendarPanel = new JPanel(new GridLayout(0, 7, 5, 5));
        calendarPanel.setBackground(Color.WHITE);
        
        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        selectedDateLabel = new JLabel("Selected Dates: ");
        selectedDateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Manual schedule input area - Enhanced
        manualScheduleArea = new JTextArea(6, 40);
        manualScheduleArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        manualScheduleArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        manualScheduleArea.setLineWrap(true);
        manualScheduleArea.setWrapStyleWord(true);
        
        // Set placeholder text for manual schedule
        manualScheduleArea.setText("Enter class schedule here...\n\nExamples:\n" +
            "• Monday 2:00 PM - 3:30 PM\n" +
            "• Tuesday & Thursday 10:00 AM - 11:30 AM\n" +
            "• Weekends 9:00 AM - 12:00 PM\n" +
            "• 2025-08-01, 2025-08-08, 2025-08-15");
        manualScheduleArea.setForeground(Color.GRAY);
        
        // Add focus listener to clear placeholder text
        manualScheduleArea.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (manualScheduleArea.getForeground() == Color.GRAY) {
                    manualScheduleArea.setText("");
                    manualScheduleArea.setForeground(TEXT_COLOR);
                }
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (manualScheduleArea.getText().trim().isEmpty()) {
                    manualScheduleArea.setText("Enter class schedule here...\n\nExamples:\n" +
                        "• Monday 2:00 PM - 3:30 PM\n" +
                        "• Tuesday & Thursday 10:00 AM - 11:30 AM\n" +
                        "• Weekends 9:00 AM - 12:00 PM\n" +
                        "• 2025-08-01, 2025-08-08, 2025-08-15");
                    manualScheduleArea.setForeground(Color.GRAY);
                }
            }
        });
        
        // Schedule input tabs
        scheduleTabPane = new JTabbedPane();
        scheduleTabPane.setFont(new Font("Segoe UI", Font.PLAIN, 12));
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
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Add New Class");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Content panel using vertical layout
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        
        // Basic info panel
        JPanel basicInfoPanel = createBasicInfoPanel();
        contentPanel.add(basicInfoPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Schedule panel with tabs
        JPanel schedulePanel = createSchedulePanel();
        contentPanel.add(schedulePanel);
        
        // Buttons panel
        JPanel buttonsPanel = createButtonsPanel();
        
        // Add components to main panel
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        // Wrap in scroll pane for better handling of long content
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scrollPane, BorderLayout.CENTER);
        
        updateCalendar();
    }
    
    private JPanel createBasicInfoPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            "Class Details", 0, 0,
            new Font("Segoe UI", Font.BOLD, 14), TEXT_COLOR));
        panel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Class ID
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createFieldLabel("Class ID (Auto):"), gbc);
        gbc.gridx = 1;
        panel.add(classIdField, gbc);
        
        // Course Name
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(createFieldLabel("Course Name:"), gbc);
        gbc.gridx = 1;
        panel.add(courseNameField, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(createFieldLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 0.3;
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setPreferredSize(new Dimension(400, 120));
        panel.add(descScrollPane, gbc);
        
        // Charge per Class
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0; gbc.weighty = 0;
        panel.add(createFieldLabel("Charge per Class (RM):"), gbc);
        gbc.gridx = 1;
        panel.add(chargeField, gbc);
        
        // Tutor ID
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(createFieldLabel("Tutor ID:"), gbc);
        gbc.gridx = 1;
        JTextField tutorIdField = createStyledTextField();
        tutorIdField.setText(tutorId);
        tutorIdField.setEditable(false);
        tutorIdField.setBackground(SECONDARY_COLOR);
        panel.add(tutorIdField, gbc);
        
        return panel;
    }
    
    private JPanel createSchedulePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            "Class Schedule", 0, 0,
            new Font("Segoe UI", Font.BOLD, 14), TEXT_COLOR));
        
        // Create calendar panel
        JPanel calendarPanelContainer = createCalendarPanel();
        
        // Create manual input panel
        JPanel manualInputPanel = createManualSchedulePanel();
        
        // Add tabs
        scheduleTabPane.addTab("📅 Calendar Selection", calendarPanelContainer);
        scheduleTabPane.addTab("✏️ Manual Input", manualInputPanel);
        
        // Add instruction label
        JLabel instructionLabel = new JLabel(
            "<html><div style='text-align: center; padding: 10px;'>" +
            "<b>Choose how to set your class schedule:</b><br>" +
            "Use Calendar Selection for specific dates, or Manual Input for recurring schedules" +
            "</div></html>");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        instructionLabel.setForeground(new Color(107, 114, 128));
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        panel.add(instructionLabel, BorderLayout.NORTH);
        panel.add(scheduleTabPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createCalendarPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Calendar navigation
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBackground(Color.WHITE);
        
        JButton prevButton = createStyledButton("<", PRIMARY_COLOR);
        JButton nextButton = createStyledButton(">", PRIMARY_COLOR);
        
        prevButton.setPreferredSize(new Dimension(50, 35));
        nextButton.setPreferredSize(new Dimension(50, 35));
        
        navPanel.add(prevButton, BorderLayout.WEST);
        navPanel.add(monthLabel, BorderLayout.CENTER);
        navPanel.add(nextButton, BorderLayout.EAST);
        
        // Calendar grid
        JPanel calendarContainer = new JPanel(new BorderLayout());
        calendarContainer.setBackground(Color.WHITE);
        calendarContainer.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
        calendarContainer.add(calendarPanel, BorderLayout.CENTER);
        
        // Selected dates info
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        infoPanel.add(selectedDateLabel, BorderLayout.CENTER);
        
        JButton clearDatesButton = createStyledButton("Clear All Dates", ERROR_COLOR);
        clearDatesButton.addActionListener(e -> {
            selectedDates.clear();
            selectedDateLabel.setText("Selected Dates: ");
            updateCalendar();
        });
        infoPanel.add(clearDatesButton, BorderLayout.EAST);
        
        // Navigation button listeners
        prevButton.addActionListener(e -> {
            if (currentMonth == 1) {
                currentMonth = 12;
                currentYear--;
            } else {
                currentMonth--;
            }
            updateCalendar();
        });
        
        nextButton.addActionListener(e -> {
            if (currentMonth == 12) {
                currentMonth = 1;
                currentYear++;
            } else {
                currentMonth++;
            }
            updateCalendar();
        });
        
        panel.add(navPanel, BorderLayout.NORTH);
        panel.add(calendarContainer, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createManualSchedulePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Instruction label
        JLabel instructionLabel = new JLabel(
            "<html><div style='padding: 10px;'>" +
            "<b>Enter your class schedule in the text area below:</b><br><br>" +
            "You can enter:<br>" +
            "• Recurring schedules (e.g., 'Every Monday 2:00 PM - 3:30 PM')<br>" +
            "• Specific dates (e.g., '2025-08-01, 2025-08-08, 2025-08-15')<br>" +
            "• Mixed formats as needed for your class structure" +
            "</div></html>");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        instructionLabel.setForeground(TEXT_COLOR);
        
        // Schedule input area with scroll
        JScrollPane scheduleScrollPane = new JScrollPane(manualScheduleArea);
        scheduleScrollPane.setPreferredSize(new Dimension(500, 200));
        scheduleScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            "Class Schedule Details", 0, 0,
            new Font("Segoe UI", Font.BOLD, 12), TEXT_COLOR));
        
        // Quick templates panel
        JPanel templatesPanel = createScheduleTemplatesPanel();
        
        panel.add(instructionLabel, BorderLayout.NORTH);
        panel.add(scheduleScrollPane, BorderLayout.CENTER);
        panel.add(templatesPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createScheduleTemplatesPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            "Quick Templates", 0, 0,
            new Font("Segoe UI", Font.BOLD, 12), TEXT_COLOR));
        
        String[] templates = {
            "Monday 2:00 PM - 3:30 PM",
            "Tuesday & Thursday 10:00 AM - 11:30 AM",
            "Weekends 9:00 AM - 12:00 PM",
            "Every Wednesday 4:00 PM - 5:30 PM",
            "Mon/Wed/Fri 3:00 PM - 4:00 PM"
        };
        
        for (String template : templates) {
            JButton templateButton = createStyledButton(template, SECONDARY_COLOR);
            templateButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            templateButton.addActionListener(e -> {
                if (manualScheduleArea.getForeground() == Color.GRAY) {
                    manualScheduleArea.setText("");
                    manualScheduleArea.setForeground(TEXT_COLOR);
                }
                
                String currentText = manualScheduleArea.getText().trim();
                if (!currentText.isEmpty() && !currentText.contains("Enter class schedule")) {
                    manualScheduleArea.setText(currentText + "\n" + template);
                } else {
                    manualScheduleArea.setText(template);
                    manualScheduleArea.setForeground(TEXT_COLOR);
                }
            });
            panel.add(templateButton);
        }
        
        return panel;
    }
    
    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panel.setBackground(Color.WHITE);
        
        JButton saveButton = createStyledButton("Save Class", ACCENT_COLOR);
        JButton cancelButton = createStyledButton("Cancel", SECONDARY_COLOR);
        JButton previewButton = createStyledButton("Preview Schedule", PRIMARY_COLOR);
        
        // Event listeners
        saveButton.addActionListener(e -> saveClass());
        cancelButton.addActionListener(e -> dispose());
        previewButton.addActionListener(e -> previewSchedule());
        
        panel.add(previewButton);
        panel.add(saveButton);
        panel.add(cancelButton);
        
        return panel;
    }
    
    private void updateCalendar() {
        calendarPanel.removeAll();
        
        YearMonth yearMonth = YearMonth.of(currentYear, currentMonth);
        LocalDate firstDay = yearMonth.atDay(1);
        int daysInMonth = yearMonth.lengthOfMonth();
        int startDay = firstDay.getDayOfWeek().getValue();
        
        String monthName = firstDay.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        monthLabel.setText(monthName + " " + currentYear);
        
        // Day headers
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String day : days) {
            JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
            dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            dayLabel.setForeground(new Color(107, 114, 128));
            dayLabel.setBackground(SECONDARY_COLOR);
            dayLabel.setOpaque(true);
            dayLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
            calendarPanel.add(dayLabel);
        }
        
        // Empty cells for days before month starts
        for (int i = 1; i < startDay; i++) {
            JLabel emptyLabel = new JLabel("");
            emptyLabel.setBorder(BorderFactory.createLineBorder(new Color(240, 240, 240), 1));
            calendarPanel.add(emptyLabel);
        }
        
        // Day buttons
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate thisDate = LocalDate.of(currentYear, currentMonth, day);
            JButton dayButton = new JButton(String.valueOf(day));
            dayButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            dayButton.setFocusPainted(false);
            dayButton.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
            
            if (selectedDates.contains(thisDate)) {
                dayButton.setBackground(ACCENT_COLOR);
                dayButton.setForeground(Color.WHITE);
            } else {
                dayButton.setBackground(Color.WHITE);
                dayButton.setForeground(TEXT_COLOR);
            }
            
            // Highlight today
            if (thisDate.equals(LocalDate.now())) {
                dayButton.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2));
            }
            
            dayButton.addActionListener(e -> {
                if (selectedDates.contains(thisDate)) {
                    selectedDates.remove(thisDate);
                } else {
                    selectedDates.add(thisDate);
                }
                updateSelectedDatesLabel();
                updateCalendar();
            });
            
            calendarPanel.add(dayButton);
        }
        
        calendarPanel.revalidate();
        calendarPanel.repaint();
    }
    
    private void updateSelectedDatesLabel() {
        if (selectedDates.isEmpty()) {
            selectedDateLabel.setText("Selected Dates: ");
        } else {
            String datesText = selectedDates.stream()
                .sorted()
                .map(LocalDate::toString)
                .collect(Collectors.joining(", "));
            selectedDateLabel.setText("Selected Dates (" + selectedDates.size() + "): " + datesText);
        }
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
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = button.getBackground();
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (backgroundColor.equals(SECONDARY_COLOR)) {
                    button.setBackground(backgroundColor.darker());
                } else {
                    button.setBackground(backgroundColor.brighter());
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(originalColor);
            }
        });
        
        return button;
    }
    
    private void previewSchedule() {
        String schedule = getScheduleString();
        if (schedule.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No schedule information entered!\n\nPlease either:\n" +
                "• Select dates from the calendar, or\n" +
                "• Enter schedule details in the manual input tab", 
                "Schedule Preview", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JTextArea previewArea = new JTextArea(10, 40);
        previewArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        previewArea.setEditable(false);
        previewArea.setText("CLASS SCHEDULE PREVIEW\n" +
                           "======================\n\n" +
                           "Course: " + courseNameField.getText().trim() + "\n" +
                           "Tutor: " + tutorId + "\n\n" +
                           "Schedule:\n" + schedule);
        
        JScrollPane scrollPane = new JScrollPane(previewArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Schedule Preview", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private String getScheduleString() {
        // Check which tab is selected
        int selectedTab = scheduleTabPane.getSelectedIndex();
        
        if (selectedTab == 0) {
            // Calendar selection
            if (selectedDates.isEmpty()) {
                return "";
            }
            return selectedDates.stream()
                    .sorted()
                    .map(LocalDate::toString)
                    .collect(Collectors.joining("; "));
        } else {
            // Manual input
            String manualText = manualScheduleArea.getText().trim();
            if (manualScheduleArea.getForeground() == Color.GRAY || 
                manualText.contains("Enter class schedule here")) {
                return "";
            }
            return manualText;
        }
    }
    
    private void saveClass() {
        String classId = classIdField.getText().trim();
        String courseName = courseNameField.getText().trim();
        String description = descriptionArea.getText().trim();
        String chargeText = chargeField.getText().trim();
        String schedule = getScheduleString();
        
        // Validation
        if (courseName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a course name.", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            courseNameField.requestFocus();
            return;
        }
        
        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a course description.", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            descriptionArea.requestFocus();
            return;
        }
        
        if (chargeText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the charge per class.", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            chargeField.requestFocus();
            return;
        }
        
        if (schedule.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please set up the class schedule!\n\n" +
                "You can either:\n" +
                "• Select specific dates from the calendar tab, or\n" +
                "• Enter schedule information in the manual input tab", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        double chargePerClass;
        try {
            chargePerClass = Double.parseDouble(chargeText);
            if (chargePerClass <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Charge must be a valid positive number.", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            chargeField.requestFocus();
            return;
        }
        
        String newRecord = classId + "," + tutorId + "," + courseName + "," +
                          description + "," + schedule + "," + chargePerClass;
        
        // Save to file
        try (FileWriter writer = new FileWriter("class.txt", true)) {
            writer.write(newRecord + "\n");
            
            JOptionPane.showMessageDialog(this, 
                "✅ Class added successfully!\n\n" +
                "Class ID: " + classId + "\n" +
                "Course: " + courseName + "\n" +
                "Charge: RM " + String.format("%.2f", chargePerClass) + "\n" +
                "Schedule: " + (schedule.length() > 50 ? schedule.substring(0, 50) + "..." : schedule), 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            parentFrame.refreshData();
            dispose();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to save class: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String generateNextClassId() {
        int maxId = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("class.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("CL")) {
                    try {
                        int id = Integer.parseInt(line.substring(2, 5));
                        if (id > maxId) maxId = id;
                    } catch (NumberFormatException | StringIndexOutOfBoundsException ignored) {}
                }
            }
        } catch (IOException ignored) {}
        return String.format("CL%03d", maxId + 1);
    }
    
    private void setupWindow() {
        setSize(900, 700);
        setLocationRelativeTo(parentFrame);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(true);
    }
}