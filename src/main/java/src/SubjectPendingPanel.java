import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * SubjectPendingPanel - Panel for managing subject change requests in Receptionist Dashboard
 */
public class SubjectPendingPanel extends JPanel {
    private DataManager dataManager;
    private ReceptionistDashboard parentFrame;
    
    // Modern color scheme
    private final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private final Color SECONDARY_COLOR = new Color(243, 244, 246);
    private final Color ACCENT_COLOR = new Color(16, 185, 129);
    private final Color ERROR_COLOR = new Color(239, 68, 68);
    private final Color WARNING_COLOR = new Color(245, 158, 11);
    private final Color TEXT_COLOR = new Color(31, 41, 55);
    private final Color BACKGROUND_COLOR = new Color(249, 250, 251);
    
    // Components
    private JTable requestsTable;
    private DefaultTableModel tableModel;
    private JLabel statsLabel;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    
    // Data
    private List<SubjectChangeRequest> allRequests;
    private Map<String, String> studentNames;
    private Map<String, String> classSubjects;
    
    public SubjectPendingPanel(DataManager dataManager, ReceptionistDashboard parentFrame) {
        this.dataManager = dataManager;
        this.parentFrame = parentFrame;
        this.allRequests = new ArrayList<>();
        this.studentNames = new HashMap<>();
        this.classSubjects = new HashMap<>();
        
        initializeComponents();
        setupLayout();
        loadData();
        refreshTable();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Table setup
        String[] columnNames = {
            "Request ID", "Student ID", "Student Name", "Current Subject", 
            "New Subject", "Status", "Date", "Actions"
        };
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only actions column is editable
            }
        };
        
        requestsTable = new JTable(tableModel);
        requestsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        requestsTable.setRowHeight(40);
        requestsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        requestsTable.getTableHeader().setBackground(SECONDARY_COLOR);
        requestsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        requestsTable.setGridColor(new Color(229, 231, 235));
        
        // Custom cell renderer for better appearance
        requestsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if (column == 5) { // Status column
                        String status = (String) value;
                        if ("Pending".equals(status)) {
                            c.setBackground(new Color(254, 249, 195)); // Light yellow
                            setForeground(new Color(146, 64, 14)); // Dark yellow
                        } else if ("Approved".equals(status)) {
                            c.setBackground(new Color(220, 252, 231)); // Light green
                            setForeground(new Color(22, 101, 52)); // Dark green
                        } else if ("Rejected".equals(status)) {
                            c.setBackground(new Color(254, 226, 226)); // Light red
                            setForeground(new Color(153, 27, 27)); // Dark red
                        } else {
                            c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                            setForeground(TEXT_COLOR);
                        }
                    } else {
                        c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                        setForeground(TEXT_COLOR);
                    }
                }
                
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return c;
            }
        });
        
        // Custom button renderer for actions column
        requestsTable.getColumn("Actions").setCellRenderer(new ActionButtonRenderer());
        requestsTable.getColumn("Actions").setCellEditor(new ActionButtonEditor());
        
        // Column widths
        requestsTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Request ID
        requestsTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Student ID
        requestsTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Student Name
        requestsTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Current Subject
        requestsTable.getColumnModel().getColumn(4).setPreferredWidth(150); // New Subject
        requestsTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Status
        requestsTable.getColumnModel().getColumn(6).setPreferredWidth(120); // Date
        requestsTable.getColumnModel().getColumn(7).setPreferredWidth(120); // Actions
        
        // Search and filter components
        searchField = createStyledTextField();
        searchField.addActionListener(e -> filterTable());
        
        statusFilter = new JComboBox<>(new String[]{"All Requests", "Pending", "Approved", "Rejected"});
        statusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusFilter.addActionListener(e -> filterTable());
        
        // Stats label
        statsLabel = new JLabel();
        statsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statsLabel.setForeground(new Color(107, 114, 128));
    }
    
    private void setupLayout() {
        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel("Subject Change Requests Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(createQuickStatsPanel(), BorderLayout.EAST);
        
        // Control panel
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(BACKGROUND_COLOR);
        controlPanel.setBorder(new EmptyBorder(15, 0, 15, 0));
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(BACKGROUND_COLOR);
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Status:"));
        searchPanel.add(statusFilter);
        
        JButton refreshButton = createStyledButton("Refresh", PRIMARY_COLOR);
        refreshButton.addActionListener(e -> {
            loadData();
            refreshTable();
        });
        searchPanel.add(refreshButton);
        
        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setBackground(BACKGROUND_COLOR);
        
        JButton approveAllBtn = createStyledButton("Approve All Pending", ACCENT_COLOR);
        JButton exportBtn = createStyledButton("Export Report", SECONDARY_COLOR);
        
        approveAllBtn.addActionListener(e -> approveAllPendingRequests());
        exportBtn.addActionListener(e -> exportRequestsReport());
        
        actionPanel.add(approveAllBtn);
        actionPanel.add(exportBtn);
        
        controlPanel.add(searchPanel, BorderLayout.WEST);
        controlPanel.add(actionPanel, BorderLayout.EAST);
        
        // Table panel with scroll
        JScrollPane scrollPane = new JScrollPane(requestsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Status panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(BACKGROUND_COLOR);
        statusPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        statusPanel.add(statsLabel, BorderLayout.WEST);
        
        // Add components to main panel
        add(titlePanel, BorderLayout.NORTH);
        add(controlPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
        
        // Fix layout - create proper container
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setBackground(BACKGROUND_COLOR);
        centerContainer.add(controlPanel, BorderLayout.NORTH);
        centerContainer.add(scrollPane, BorderLayout.CENTER);
        
        removeAll();
        add(titlePanel, BorderLayout.NORTH);
        add(centerContainer, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createQuickStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setBackground(BACKGROUND_COLOR);
        
        // Count requests by status
        int pending = 0, approved = 0, rejected = 0;
        for (SubjectChangeRequest req : allRequests) {
            switch (req.getStatus().toLowerCase()) {
                case "pending": pending++; break;
                case "approved": approved++; break;
                case "rejected": rejected++; break;
            }
        }
        
        statsPanel.add(createMiniStatCard("Pending", String.valueOf(pending), WARNING_COLOR));
        statsPanel.add(createMiniStatCard("Approved", String.valueOf(approved), ACCENT_COLOR));
        statsPanel.add(createMiniStatCard("Rejected", String.valueOf(rejected), ERROR_COLOR));
        
        return statsPanel;
    }
    
    private JPanel createMiniStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        titleLabel.setForeground(new Color(107, 114, 128));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(valueLabel);
        card.add(titleLabel);
        
        return card;
    }
    
private void loadData() {
    System.out.println("🔍 DEBUG: Loading data for Subject Pending Panel...");
    
    // Load student names DIRECTLY from file (bypassing DataManager)
    loadStudentNamesDirectly();
    
    // Load class subjects
    classSubjects.clear();
    List<ClassInfo> classes = dataManager.getAllClasses();
    for (ClassInfo classInfo : classes) {
        classSubjects.put(classInfo.getClassId(), classInfo.getSubject());
    }
    System.out.println("✅ Loaded " + classSubjects.size() + " class subjects");
    
    // Load requests from file
    allRequests.clear();
    try (BufferedReader br = new BufferedReader(new FileReader("Subject_Change_Requests.txt"))) {
        String line;
        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            
            String[] parts = line.split(",");
            if (parts.length >= 5) {
                SubjectChangeRequest request = new SubjectChangeRequest(
                    parts[0].trim(), // Request ID
                    parts[1].trim(), // Student ID
                    parts[2].trim(), // Current Class ID
                    parts[3].trim(), // New Class ID
                    parts[4].trim()  // Status
                );
                allRequests.add(request);
            }
        }
        System.out.println("✅ Loaded " + allRequests.size() + " subject change requests");
    } catch (IOException e) {
        System.out.println("ℹ️ INFO: Subject change requests file not found - will be created when needed");
    }
}

    
    private void refreshTable() {
        filterTable();
        updateStats();
    }
    
    private void filterTable() {
    System.out.println("🔍 DEBUG: Filtering table with " + allRequests.size() + " total requests");
    
    tableModel.setRowCount(0);
    
    String searchText = searchField.getText().toLowerCase().trim();
    String statusFilter = (String) this.statusFilter.getSelectedItem();
    
    int displayedCount = 0;
    
    for (SubjectChangeRequest request : allRequests) {
        // Apply status filter
        if (!"All Requests".equals(statusFilter) && !request.getStatus().equals(statusFilter)) {
            continue;
        }
        
        // Apply search filter
        if (!searchText.isEmpty()) {
            String studentName = studentNames.getOrDefault(request.getStudentId(), "Unknown");
            boolean matches = request.getRequestId().toLowerCase().contains(searchText) ||
                            request.getStudentId().toLowerCase().contains(searchText) ||
                            studentName.toLowerCase().contains(searchText) ||
                            classSubjects.getOrDefault(request.getCurrentClassId(), "Unknown").toLowerCase().contains(searchText) ||
                            classSubjects.getOrDefault(request.getNewClassId(), "Unknown").toLowerCase().contains(searchText);
            
            if (!matches) continue;
        }
        
        // Get student name with fallback
        String studentName = studentNames.getOrDefault(request.getStudentId(), "Unknown Student");
        if ("Unknown Student".equals(studentName)) {
            System.err.println("⚠️ WARNING: No name found for student ID: " + request.getStudentId());
        }
        
        // Add to table
        Object[] row = {
            request.getRequestId(),
            request.getStudentId(),
            studentName, // This should now show the correct name
            classSubjects.getOrDefault(request.getCurrentClassId(), request.getCurrentClassId()),
            classSubjects.getOrDefault(request.getNewClassId(), request.getNewClassId()),
            request.getStatus(),
            getCurrentDateTime(),
            "Actions" // Placeholder for action buttons
        };
        tableModel.addRow(row);
        displayedCount++;
        
        System.out.println("🔍 DEBUG: Added table row - Request: " + request.getRequestId() + 
                         ", Student: " + studentName + " (" + request.getStudentId() + ")");
    }
    
    System.out.println("✅ Table filtered: showing " + displayedCount + " out of " + allRequests.size() + " requests");
}

private void loadStudentNamesDirectly() {
    System.out.println("🔧 DIRECT FIX: Loading student names from file...");
    studentNames.clear();
    
    try (BufferedReader br = new BufferedReader(new FileReader("students.txt"))) {
        String line;
        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            
            String[] parts = line.split(",");
            System.out.println("🔍 Raw line: " + line);
            System.out.println("🔍 Split into " + parts.length + " parts");
            
            // File format: StudentID,IC,Password,Name,Email,Phone,Address,Level,Month,Subjects
            if (parts.length >= 4) {
                String studentId = parts[0].trim();  // STU001
                String ic = parts[1].trim();         // 0538291933  
                String password = parts[2].trim();   // password123
                String name = parts[3].trim();       // Vince <- THIS IS WHAT WE WANT
                
                System.out.println("🔍 Parsed: ID=" + studentId + ", Name=" + name + ", Password=" + password);
                
                // Store the correct mapping
                studentNames.put(studentId, name);
                
                System.out.println("✅ Stored: " + studentId + " -> " + name);
            }
        }
    } catch (IOException e) {
        System.err.println("❌ Error reading students.txt: " + e.getMessage());
    }
    
    System.out.println("✅ Loaded " + studentNames.size() + " student names directly");
    
    // Print all loaded names for verification
    System.out.println("📋 All student names loaded:");
    for (Map.Entry<String, String> entry : studentNames.entrySet()) {
        System.out.println("  " + entry.getKey() + " -> " + entry.getValue());
    }
}

    
    private void updateStats() {
        int total = tableModel.getRowCount();
        int pending = 0;
        
        for (int i = 0; i < total; i++) {
            if ("Pending".equals(tableModel.getValueAt(i, 5))) {
                pending++;
            }
        }
        
        statsLabel.setText(String.format("Showing %d requests (%d pending)", total, pending));
    }
    
    private void approveRequest(int row) {
        if (row < 0 || row >= tableModel.getRowCount()) return;
        
        String requestId = (String) tableModel.getValueAt(row, 0);
        String studentId = (String) tableModel.getValueAt(row, 1);
        String studentName = (String) tableModel.getValueAt(row, 2);
        String currentSubject = (String) tableModel.getValueAt(row, 3);
        String newSubject = (String) tableModel.getValueAt(row, 4);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Approve subject change request?\n\n" +
            "Request ID: " + requestId + "\n" +
            "Student: " + studentName + " (" + studentId + ")\n" +
            "From: " + currentSubject + "\n" +
            "To: " + newSubject + "\n\n" +
            "This will update the student's enrollment.",
            "Confirm Approval",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (updateRequestStatus(requestId, "Approved")) {
                // Also update student's actual enrollment
                updateStudentEnrollment(requestId, studentId);
                
                JOptionPane.showMessageDialog(this,
                    "✅ Request approved successfully!\n\n" +
                    "Student " + studentName + "'s enrollment has been updated.",
                    "Request Approved",
                    JOptionPane.INFORMATION_MESSAGE);
                
                loadData();
                refreshTable();
                parentFrame.refreshData(); // Refresh parent dashboard
            } else {
                JOptionPane.showMessageDialog(this,
                    "❌ Error approving request. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void rejectRequest(int row) {
        if (row < 0 || row >= tableModel.getRowCount()) return;
        
        String requestId = (String) tableModel.getValueAt(row, 0);
        String studentName = (String) tableModel.getValueAt(row, 2);
        
        String reason = JOptionPane.showInputDialog(this,
            "Reject subject change request for " + studentName + "?\n\n" +
            "Please provide a reason (optional):",
            "Confirm Rejection",
            JOptionPane.QUESTION_MESSAGE);
        
        if (reason != null) { // User didn't cancel
            if (updateRequestStatus(requestId, "Rejected")) {
                JOptionPane.showMessageDialog(this,
                    "✅ Request rejected successfully!\n\n" +
                    "Student will be notified of the decision.",
                    "Request Rejected",
                    JOptionPane.INFORMATION_MESSAGE);
                
                loadData();
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this,
                    "❌ Error rejecting request. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean updateRequestStatus(String requestId, String newStatus) {
        try {
            List<String> lines = new ArrayList<>();
            
            // Read all lines
            try (BufferedReader br = new BufferedReader(new FileReader("Subject_Change_Requests.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    lines.add(line);
                }
            }
            
            // Update the specific request
            boolean updated = false;
            for (int i = 0; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if (parts.length >= 5 && parts[0].equals(requestId)) {
                    parts[4] = newStatus;
                    lines.set(i, String.join(",", parts));
                    updated = true;
                    break;
                }
            }
            
            if (updated) {
                // Write back to file
                try (PrintWriter pw = new PrintWriter(new FileWriter("Subject_Change_Requests.txt"))) {
                    for (String line : lines) {
                        pw.println(line);
                    }
                }
            }
            
            return updated;
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private void updateStudentEnrollment(String requestId, String studentId) {
        // Find the request details
        SubjectChangeRequest request = null;
        for (SubjectChangeRequest req : allRequests) {
            if (req.getRequestId().equals(requestId)) {
                request = req;
                break;
            }
        }
        
        if (request == null) return;
        
        // Get student and update enrollment
        Student student = dataManager.getStudentById(studentId);
        if (student != null) {
            List<String> currentSubjects = student.getSubjects();
            
            // Remove old class ID and add new one
            currentSubjects.remove(request.getCurrentClassId());
            currentSubjects.add(request.getNewClassId());
            
            // Update student enrollment
            dataManager.updateStudentSubjects(studentId, currentSubjects);
        }
    }
    
    private void approveAllPendingRequests() {
        int pendingCount = 0;
        for (SubjectChangeRequest request : allRequests) {
            if ("Pending".equals(request.getStatus())) {
                pendingCount++;
            }
        }
        
        if (pendingCount == 0) {
            JOptionPane.showMessageDialog(this,
                "No pending requests to approve.",
                "No Pending Requests",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Approve all " + pendingCount + " pending requests?\n\n" +
            "This will update all student enrollments automatically.",
            "Confirm Bulk Approval",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int approved = 0;
            
            for (SubjectChangeRequest request : allRequests) {
                if ("Pending".equals(request.getStatus())) {
                    if (updateRequestStatus(request.getRequestId(), "Approved")) {
                        updateStudentEnrollment(request.getRequestId(), request.getStudentId());
                        approved++;
                    }
                }
            }
            
            JOptionPane.showMessageDialog(this,
                "✅ Bulk approval completed!\n\n" +
                "Approved: " + approved + " requests\n" +
                "All student enrollments have been updated.",
                "Bulk Approval Complete",
                JOptionPane.INFORMATION_MESSAGE);
            
            loadData();
            refreshTable();
            parentFrame.refreshData();
        }
    }
    
    private void exportRequestsReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Requests Report");
        fileChooser.setSelectedFile(new File("subject_change_requests_report.csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                exportToCSV(file);
                JOptionPane.showMessageDialog(this,
                    "✅ Report exported successfully!\n\n" +
                    "File: " + file.getAbsolutePath(),
                    "Export Complete",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "❌ Error exporting report: " + e.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportToCSV(File file) throws IOException {
        try (PrintWriter writer = new PrintWriter(file)) {
            // Write header
            writer.println("Request ID,Student ID,Student Name,Current Subject,New Subject,Status,Date");
            
            // Write data
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                writer.printf("%s,%s,%s,%s,%s,%s,%s%n",
                    tableModel.getValueAt(i, 0),
                    tableModel.getValueAt(i, 1),
                    tableModel.getValueAt(i, 2),
                    tableModel.getValueAt(i, 3),
                    tableModel.getValueAt(i, 4),
                    tableModel.getValueAt(i, 5),
                    tableModel.getValueAt(i, 6)
                );
            }
        }
    }
    
    // Helper methods
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }
    
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(backgroundColor);
        button.setForeground(backgroundColor.equals(SECONDARY_COLOR) ? TEXT_COLOR : Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }
    
    // Action button renderer for table
    private class ActionButtonRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private JButton approveBtn;
        private JButton rejectBtn;
        
        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2));
            setOpaque(true);
            
            approveBtn = new JButton("✓");
            approveBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
            approveBtn.setBackground(ACCENT_COLOR);
            approveBtn.setForeground(Color.WHITE);
            approveBtn.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
            approveBtn.setFocusPainted(false);
            
            rejectBtn = new JButton("✗");
            rejectBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
            rejectBtn.setBackground(ERROR_COLOR);
            rejectBtn.setForeground(Color.WHITE);
            rejectBtn.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
            rejectBtn.setFocusPainted(false);
            
            add(approveBtn);
            add(rejectBtn);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            
            String status = (String) table.getValueAt(row, 5);
            boolean isPending = "Pending".equals(status);
            
            approveBtn.setEnabled(isPending);
            rejectBtn.setEnabled(isPending);
            
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
            }
            
            return this;
        }
    }
    
    // Action button editor for table
    private class ActionButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton approveBtn;
        private JButton rejectBtn;
        private int currentRow;
        
        public ActionButtonEditor() {
            super(new JCheckBox());
            
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
            
            approveBtn = new JButton("✓");
            approveBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
            approveBtn.setBackground(ACCENT_COLOR);
            approveBtn.setForeground(Color.WHITE);
            approveBtn.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
            approveBtn.setFocusPainted(false);
            approveBtn.addActionListener(e -> {
                approveRequest(currentRow);
                fireEditingStopped();
            });
            
            rejectBtn = new JButton("✗");
            rejectBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
            rejectBtn.setBackground(ERROR_COLOR);
            rejectBtn.setForeground(Color.WHITE);
            rejectBtn.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
            rejectBtn.setFocusPainted(false);
            rejectBtn.addActionListener(e -> {
                rejectRequest(currentRow);
                fireEditingStopped();
            });
            
            panel.add(approveBtn);
            panel.add(rejectBtn);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            
            String status = (String) table.getValueAt(row, 5);
            boolean isPending = "Pending".equals(status);
            
            approveBtn.setEnabled(isPending);
            rejectBtn.setEnabled(isPending);
            
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "Actions";
        }
    }
    
    // Data class for subject change requests
    private static class SubjectChangeRequest {
        private String requestId;
        private String studentId;
        private String currentClassId;
        private String newClassId;
        private String status;
        
        public SubjectChangeRequest(String requestId, String studentId, String currentClassId, 
                                  String newClassId, String status) {
            this.requestId = requestId;
            this.studentId = studentId;
            this.currentClassId = currentClassId;
            this.newClassId = newClassId;
            this.status = status;
        }
        
        // Getters
        public String getRequestId() { return requestId; }
        public String getStudentId() { return studentId; }
        public String getCurrentClassId() { return currentClassId; }
        public String getNewClassId() { return newClassId; }
        public String getStatus() { return status; }
        
        // Setters
        public void setStatus(String status) { this.status = status; }
    }
}