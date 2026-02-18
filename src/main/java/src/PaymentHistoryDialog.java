import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * PaymentHistoryDialog - Dialog for viewing student payment history and receipt details
 */
public class PaymentHistoryDialog extends JDialog {
    private DataManager dataManager;
    private ReceptionistDashboard parentFrame;
    private String studentId;
    private String studentName;
    
    // Color scheme
    private final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private final Color SECONDARY_COLOR = new Color(243, 244, 246);
    private final Color ACCENT_COLOR = new Color(16, 185, 129);
    private final Color ERROR_COLOR = new Color(239, 68, 68);
    private final Color TEXT_COLOR = new Color(31, 41, 55);
    private final Color BACKGROUND_COLOR = new Color(249, 250, 251);
    
    // Components
    private JTable paymentsTable;
    private DefaultTableModel paymentsTableModel;
    private JTextArea receiptArea;
    private JLabel totalPaidLabel;
    
    public PaymentHistoryDialog(ReceptionistDashboard parent, DataManager dataManager, 
                               String studentId, String studentName) {
        super(parent, "Payment History - " + studentName, true);
        this.parentFrame = parent;
        this.dataManager = dataManager;
        this.studentId = studentId;
        this.studentName = studentName;
        
        initializeComponents();
        setupLayout();
        loadPaymentHistory();
        setupWindow();
    }
    
    private void initializeComponents() {
        // Payments table
        String[] columnNames = {"Receipt ID", "Payment ID", "Date", "Amount", "Method", "Classes", "Status"};
        paymentsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        paymentsTable = new JTable(paymentsTableModel);
        paymentsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        paymentsTable.setRowHeight(25);
        paymentsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        paymentsTable.getTableHeader().setBackground(SECONDARY_COLOR);
        paymentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Receipt area
        receiptArea = new JTextArea(20, 50);
        receiptArea.setFont(new Font("Courier New", Font.PLAIN, 11));
        receiptArea.setEditable(false);
        receiptArea.setBackground(Color.WHITE);
        receiptArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Total paid label
        totalPaidLabel = new JLabel("Total Paid: RM 0.00");
        totalPaidLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalPaidLabel.setForeground(ACCENT_COLOR);
        
        // Table selection listener
        paymentsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedReceipt();
            }
        });
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
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Payment History for " + studentName + " (" + studentId + ")");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_COLOR);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(totalPaidLabel, BorderLayout.EAST);
        
        // Content panel with split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBackground(Color.WHITE);
        splitPane.setDividerLocation(500);
        splitPane.setResizeWeight(0.6);
        
        // Left panel - Payments table
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1), 
            "Payment Records", 0, 0, 
            new Font("Segoe UI", Font.BOLD, 14), TEXT_COLOR));
        
        JScrollPane tableScrollPane = new JScrollPane(paymentsTable);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
        leftPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Right panel - Receipt display
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1), 
            "Receipt Details", 0, 0, 
            new Font("Segoe UI", Font.BOLD, 14), TEXT_COLOR));
        
        JScrollPane receiptScrollPane = new JScrollPane(receiptArea);
        receiptScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        receiptScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        rightPanel.add(receiptScrollPane, BorderLayout.CENTER);
        
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonsPanel.setBackground(Color.WHITE);
        
        JButton printReceiptButton = createStyledButton("Print Receipt", PRIMARY_COLOR);
        JButton refreshButton = createStyledButton("Refresh", SECONDARY_COLOR);
        JButton closeButton = createStyledButton("Close", SECONDARY_COLOR);
        
        buttonsPanel.add(printReceiptButton);
        buttonsPanel.add(refreshButton);
        buttonsPanel.add(closeButton);
        
        // Event listeners
        printReceiptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printSelectedReceipt();
            }
        });
        
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPaymentHistory();
            }
        });
        
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
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
        setSize(1000, 700);
        setLocationRelativeTo(parentFrame);
        setResizable(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }
    
    private void loadPaymentHistory() {
        List<Payment> payments = dataManager.getStudentPayments(studentId);
        paymentsTableModel.setRowCount(0);
        
        double totalPaid = 0.0;
        
        for (Payment payment : payments) {
            Object[] row = {
                payment.getReceiptId(),
                payment.getPaymentId(),
                payment.getPaymentDate(),
                String.format("RM %.2f", payment.getAmount()),
                payment.getPaymentMethod(),
                payment.getClassIdsString(),
                payment.getStatus()
            };
            paymentsTableModel.addRow(row);
            totalPaid += payment.getAmount();
        }
        
        totalPaidLabel.setText(String.format("Total Paid: RM %.2f (%d payments)", totalPaid, payments.size()));
        
        if (payments.isEmpty()) {
            receiptArea.setText("No payment records found for this student.\n\n" +
                              "When payments are made, receipt details will appear here.");
        } else {
            receiptArea.setText("Select a payment record from the table to view receipt details.");
        }
    }
    
    private void showSelectedReceipt() {
        int selectedRow = paymentsTable.getSelectedRow();
        if (selectedRow >= 0) {
            String receiptId = (String) paymentsTableModel.getValueAt(selectedRow, 0);
            String paymentId = (String) paymentsTableModel.getValueAt(selectedRow, 1);
            
            // Find the payment record
            List<Payment> payments = dataManager.getStudentPayments(studentId);
            Payment selectedPayment = null;
            
            for (Payment payment : payments) {
                if (payment.getReceiptId().equals(receiptId) && payment.getPaymentId().equals(paymentId)) {
                    selectedPayment = payment;
                    break;
                }
            }
            
            if (selectedPayment != null) {
                // Generate and display the receipt
                String receipt = generateDetailedReceipt(selectedPayment);
                receiptArea.setText(receipt);
                receiptArea.setCaretPosition(0); // Scroll to top
            }
        }
    }
    
    private String generateDetailedReceipt(Payment payment) {
        StringBuilder receipt = new StringBuilder();
        
        // Get student information
        Student student = dataManager.getStudentById(studentId);
        
        // Header
        receipt.append("===============================\n");
        receipt.append("    ATC TUITION CENTRE\n");
        receipt.append("         RECEIPT\n");
        receipt.append("===============================\n");
        
        // Receipt Information
        receipt.append("Receipt ID: ").append(payment.getReceiptId()).append("\n");
        receipt.append("Payment ID: ").append(payment.getPaymentId()).append("\n");
        receipt.append("Date: ").append(payment.getPaymentDate()).append("\n");
        receipt.append("Payment Method: ").append(payment.getPaymentMethod()).append("\n");
        receipt.append("Status: ").append(payment.getStatus()).append("\n");
        
        receipt.append("-------------------------------\n");
        
        // Student Information
        if (student != null) {
            receipt.append("STUDENT INFORMATION:\n");
            receipt.append("Student ID: ").append(student.getUserId()).append("\n");
            receipt.append("Name: ").append(student.getName()).append("\n");
            receipt.append("IC/Passport: ").append(student.getIcPassport()).append("\n");
            receipt.append("Email: ").append(student.getEmail()).append("\n");
            receipt.append("Phone: ").append(student.getPhone()).append("\n");
            receipt.append("Level: ").append(student.getLevel()).append("\n");
        } else {
            receipt.append("STUDENT INFORMATION:\n");
            receipt.append("Student ID: ").append(payment.getStudentId()).append("\n");
            receipt.append("Name: ").append(payment.getStudentName()).append("\n");
            receipt.append("(Student record no longer in system)\n");
        }
        
        receipt.append("-------------------------------\n");
        
        // Class Details
        receipt.append("CLASSES PAID:\n");
        List<ClassInfo> allClasses = dataManager.getAllClasses();
        java.util.Map<String, ClassInfo> classMap = new java.util.HashMap<>();
        for (ClassInfo classInfo : allClasses) {
            classMap.put(classInfo.getClassId(), classInfo);
        }
        
        double totalCalculated = 0.0;
        for (String classId : payment.getClassIds()) {
            if (classId != null && !classId.trim().isEmpty()) {
                ClassInfo classInfo = classMap.get(classId);
                if (classInfo != null) {
                    receipt.append("- ").append(classId).append(": ")
                           .append(classInfo.getSubject())
                           .append("\n  Fee: RM").append(String.format("%.2f", classInfo.getFee()))
                           .append(" | Tutor: ").append(classInfo.getTutorId()).append("\n");
                    totalCalculated += classInfo.getFee();
                } else {
                    receipt.append("- ").append(classId).append(" (Details not available)\n");
                }
            }
        }
        
        receipt.append("-------------------------------\n");
        
        // Payment Summary
        receipt.append("PAYMENT SUMMARY:\n");
        receipt.append("Subtotal: RM").append(String.format("%.2f", totalCalculated)).append("\n");
        if (Math.abs(payment.getAmount() - totalCalculated) > 0.01) {
            receipt.append("Adjustment: RM").append(String.format("%.2f", payment.getAmount() - totalCalculated)).append("\n");
        }
        receipt.append("TOTAL PAID: RM").append(String.format("%.2f", payment.getAmount())).append("\n");
        
        receipt.append("===============================\n");
        receipt.append("Thank you for your payment!\n");
        receipt.append("Keep this receipt for your records.\n");
        receipt.append("===============================\n");
        
        return receipt.toString();
    }
    
    private void printSelectedReceipt() {
        String receiptText = receiptArea.getText();
        if (receiptText.trim().isEmpty() || receiptText.contains("Select a payment record")) {
            JOptionPane.showMessageDialog(this, 
                "Please select a payment record first!", 
                "Print Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            receiptArea.print();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error printing receipt: " + e.getMessage(), 
                "Print Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}