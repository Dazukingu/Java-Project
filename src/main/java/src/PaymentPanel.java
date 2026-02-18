import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * PaymentPanel - Panel for processing payments and generating receipts
 */
public class PaymentPanel extends JPanel {
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
    private JTextField studentIdField;
    private JTextField studentNameField;
    private JList<ClassInfo> subjectsList; // Changed to ClassInfo
    private DefaultListModel<ClassInfo> subjectsListModel; // Changed to ClassInfo
    private JTextField amountField;
    private JComboBox<String> paymentMethodComboBox;
    private JTextArea receiptArea;
    private JLabel totalFeeLabel;
    
    public PaymentPanel(DataManager dataManager, ReceptionistDashboard parentFrame) {
        this.dataManager = dataManager;
        this.parentFrame = parentFrame;
        
        initializeComponents();
        setupLayout();
    }
    
    private void initializeComponents() {
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Student ID field
        studentIdField = createStyledTextField();
        studentNameField = createStyledTextField();
        studentNameField.setEditable(false);
        studentNameField.setBackground(SECONDARY_COLOR);
        
        // Subjects list - now uses ClassInfo objects
        subjectsListModel = new DefaultListModel<>();
        subjectsList = new JList<>(subjectsListModel);
        subjectsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        subjectsList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Custom cell renderer to display class info nicely
        subjectsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ClassInfo) {
                    ClassInfo classInfo = (ClassInfo) value;
                    setText(String.format("%s - %s (RM%.2f)", 
                        classInfo.getClassId(), 
                        classInfo.getSubject(), 
                        classInfo.getFee()));
                }
                return this;
            }
        });
        
        // Amount field
        amountField = createStyledTextField();
        amountField.setEditable(false);
        amountField.setBackground(SECONDARY_COLOR);
        
        // Payment method combo box
        String[] paymentMethods = {"Cash", "Credit Card", "Debit Card", "Bank Transfer", "Online Banking"};
        paymentMethodComboBox = new JComboBox<>(paymentMethods);
        paymentMethodComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        paymentMethodComboBox.setBackground(Color.WHITE);
        
        // Receipt area
        receiptArea = new JTextArea(15, 40);
        receiptArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        receiptArea.setEditable(false);
        receiptArea.setBackground(Color.WHITE);
        receiptArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Total fee label
        totalFeeLabel = new JLabel("Total: RM 0.00");
        totalFeeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalFeeLabel.setForeground(PRIMARY_COLOR);
        
        // Event listeners
        studentIdField.addActionListener(e -> loadStudentData());
        subjectsList.addListSelectionListener(e -> updateTotalAmount());
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
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(30, 30, 30, 30)
        ));
        
        // Title
        JLabel titleLabel = new JLabel("Process Payment");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Student ID row
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(createFieldLabel("Student ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(studentIdField, gbc);
        
        JButton loadButton = createStyledButton("Load Student", PRIMARY_COLOR);
        loadButton.addActionListener(e -> loadStudentData());
        gbc.gridx = 2;
        formPanel.add(loadButton, gbc);
        
        // Student name row
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(createFieldLabel("Student Name:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        formPanel.add(studentNameField, gbc);
        
        // Subjects row
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(createFieldLabel("Enrolled Subjects:"), gbc);
        
        JScrollPane subjectsScrollPane = new JScrollPane(subjectsList);
        subjectsScrollPane.setPreferredSize(new Dimension(300, 120));
        subjectsScrollPane.setBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1));
        gbc.gridx = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(subjectsScrollPane, gbc);
        
        // Amount row
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(createFieldLabel("Amount:"), gbc);
        gbc.gridx = 1;
        formPanel.add(amountField, gbc);
        gbc.gridx = 2;
        formPanel.add(totalFeeLabel, gbc);
        
        // Payment method row
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(createFieldLabel("Payment Method:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        formPanel.add(paymentMethodComboBox, gbc);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonsPanel.setBackground(Color.WHITE);
        
        JButton processButton = createStyledButton("Process Payment", ACCENT_COLOR);
        JButton clearButton = createStyledButton("Clear Form", SECONDARY_COLOR);
        JButton printButton = createStyledButton("Print Receipt", PRIMARY_COLOR);
        
        buttonsPanel.add(processButton);
        buttonsPanel.add(clearButton);
        buttonsPanel.add(printButton);
        
        // Event listeners
        processButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processPayment();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        
        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printReceipt();
            }
        });
        
        // Receipt panel
        JPanel receiptPanel = new JPanel(new BorderLayout());
        receiptPanel.setBackground(Color.WHITE);
        receiptPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1), 
                "Receipt Preview", 0, 0, new Font("Segoe UI", Font.BOLD, 14), TEXT_COLOR),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JScrollPane receiptScrollPane = new JScrollPane(receiptArea);
        receiptPanel.add(receiptScrollPane, BorderLayout.CENTER);
        
        // Layout arrangement
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.add(titleLabel, BorderLayout.NORTH);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)), BorderLayout.CENTER);
        
        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.setBackground(Color.WHITE);
        formContainer.add(formPanel, BorderLayout.NORTH);
        formContainer.add(buttonsPanel, BorderLayout.SOUTH);
        
        leftPanel.add(formContainer, BorderLayout.SOUTH);
        
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(receiptPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
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
    
    private void loadStudentData() {
        String studentId = studentIdField.getText().trim();
        if (studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Student ID!", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Student student = dataManager.getStudentById(studentId);
        if (student != null) {
            studentNameField.setText(student.getName());
            
            // Load student's enrolled classes
            subjectsListModel.clear();
            String[] classIds = student.getClassIds();
            
            // Get all classes for lookup
            List<ClassInfo> allClasses = dataManager.getAllClasses();
            Map<String, ClassInfo> classMap = new HashMap<>();
            for (ClassInfo classInfo : allClasses) {
                classMap.put(classInfo.getClassId(), classInfo);
            }
            
            // Add enrolled classes to the list
            for (String classId : classIds) {
                if (classId != null && !classId.trim().isEmpty()) {
                    ClassInfo classInfo = classMap.get(classId);
                    if (classInfo != null) {
                        subjectsListModel.addElement(classInfo);
                    }
                }
            }
            
            // Select all classes by default
            if (subjectsListModel.getSize() > 0) {
                subjectsList.setSelectionInterval(0, subjectsListModel.getSize() - 1);
            }
            updateTotalAmount();
        } else {
            JOptionPane.showMessageDialog(this, "Student not found with ID: " + studentId, "Student Not Found", JOptionPane.ERROR_MESSAGE);
            clearForm();
        }
    }
    
    private void updateTotalAmount() {
        List<ClassInfo> selectedClasses = subjectsList.getSelectedValuesList();
        double totalFee = 0.0;
        for (ClassInfo classInfo : selectedClasses) {
            totalFee += classInfo.getFee();
        }
        amountField.setText(String.format("%.2f", totalFee));
        totalFeeLabel.setText("Total: RM " + String.format("%.2f", totalFee));
    }
    
    private void processPayment() {
    // Validate input
    String studentId = studentIdField.getText().trim();
    String studentName = studentNameField.getText().trim();
    List<ClassInfo> selectedClasses = subjectsList.getSelectedValuesList();
    String amountText = amountField.getText().trim();
    String paymentMethod = (String) paymentMethodComboBox.getSelectedItem();
    
    if (studentId.isEmpty() || studentName.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please load a student first!", "Input Error", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    if (selectedClasses.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please select classes to pay for!", "Input Error", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    // NEW: Check if student already paid for this month
    String currentMonth = getCurrentMonth();
    if (hasStudentPaidThisMonth(studentId, currentMonth)) {
        int choice = JOptionPane.showConfirmDialog(this,
            "Student has already paid for " + formatMonth(currentMonth) + ".\n" +
            "Do you want to process an additional payment?",
            "Already Paid This Month", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
    }
    
    try {
        double amount = Double.parseDouble(amountText);
        
        // Convert selected classes to class IDs
        List<String> classIds = new ArrayList<>();
        for (ClassInfo classInfo : selectedClasses) {
            classIds.add(classInfo.getClassId());
        }
        
        // Process payment
        String receipt = dataManager.processPayment(studentId, classIds, amount, paymentMethod);
        
        if (receipt != null) {
            receiptArea.setText(receipt);
            
            // NEW: Update payment history with current month
            updatePaymentHistoryWithMonth(studentId, studentName, classIds, amount, paymentMethod, currentMonth);
            
            JOptionPane.showMessageDialog(this, 
                "✅ Payment processed successfully!\n" +
                "📅 Paid for: " + formatMonth(currentMonth) + "\n" +
                "💰 Amount: RM" + String.format("%.2f", amount), 
                "Payment Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Error processing payment. Please try again.", "Payment Error", JOptionPane.ERROR_MESSAGE);
        }
        
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Invalid amount format!", "Input Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
    private void clearForm() {
        studentIdField.setText("");
        studentNameField.setText("");
        subjectsListModel.clear();
        amountField.setText("");
        totalFeeLabel.setText("Total: RM 0.00");
        paymentMethodComboBox.setSelectedIndex(0);
        receiptArea.setText("");
    }
    
    private void printReceipt() {
        String receiptText = receiptArea.getText();
        if (receiptText.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No receipt to print!", "Print Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            receiptArea.print();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error printing receipt: " + e.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getCurrentMonth() {
    java.time.LocalDate now = java.time.LocalDate.now();
    return String.format("%04d-%02d", now.getYear(), now.getMonthValue());
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

private boolean hasStudentPaidThisMonth(String studentId, String currentMonth) {
    try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("payment_history.txt"))) {
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 6 && parts[0].trim().equals(studentId)) {
                // Check if payment month matches current month
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

private void updatePaymentHistoryWithMonth(String studentId, String studentName, 
                                         List<String> classIds, double amount, 
                                         String paymentMethod, String month) {
    try (java.io.FileWriter fw = new java.io.FileWriter("payment_history.txt", true)) {
        String classIdsStr = String.join(";", classIds);
        String methodStr = paymentMethod.toLowerCase().replace(" ", "");
        
        // NEW FORMAT: studentId,name,classes,amount,method,month
        String record = String.join(",",
            studentId,
            studentName,
            classIdsStr,
            String.format("%.2f", amount),
            methodStr,
            month
        );
        fw.write(record + "\n");
    } catch (java.io.IOException e) {
        System.err.println("Error writing to payment history: " + e.getMessage());
    }
}
}