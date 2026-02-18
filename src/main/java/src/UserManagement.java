import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Fixed Enhanced User Management Panel for ATC Tuition Centre
 * Fixed NullPointerException and improved initialization
 */
public class UserManagement extends JPanel {
    private DataManager dataManager;
    private JFrame parentFrame;
    private JTabbedPane mainTabbedPane;
    
    // Modern color scheme
    private final Color PRIMARY_COLOR = new Color(59, 130, 246);    // Blue
    private final Color SECONDARY_COLOR = new Color(243, 244, 246);  // Light gray
    private final Color ACCENT_COLOR = new Color(16, 185, 129);      // Green
    private final Color ERROR_COLOR = new Color(239, 68, 68);        // Red
    private final Color TEXT_COLOR = new Color(31, 41, 55);          // Dark gray
    private final Color BACKGROUND_COLOR = new Color(249, 250, 251); // Very light gray
    private final Color WARNING_COLOR = new Color(245, 158, 11);     // Orange
    
    // Tables for staff user types only (no student table)
    private JTable tutorTable, receptionistTable, adminTable;
    private DefaultTableModel tutorTableModel, receptionistTableModel, adminTableModel;
    
    // Search components
    private JTextField searchField;
    private JComboBox<String> searchTypeCombo;
    
    // Thread safety
    private final Object fileLock = new Object();
    
    public UserManagement(DataManager dataManager, JFrame parentFrame) {
        this.dataManager = dataManager;
        this.parentFrame = parentFrame;
        
        initializeComponents();
        setupLayout();
        loadAllData();
    }
    
    private void initializeComponents() {
        // Create main tabbed pane
        mainTabbedPane = new JTabbedPane();
        mainTabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainTabbedPane.setBackground(Color.WHITE);
        mainTabbedPane.setTabPlacement(JTabbedPane.TOP);
        
        // Search components
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        searchTypeCombo = new JComboBox<>(new String[]{"All Users", "Tutors", "Receptionists", "Admins"});
        searchTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Add search functionality
        searchField.addActionListener(e -> performSearch());
        searchTypeCombo.addActionListener(e -> performSearch());
        
        // Add tabs for staff user types only (no students)
        mainTabbedPane.addTab("👩‍🏫 Tutors", createTutorManagementPanel());
        mainTabbedPane.addTab("📋 Receptionists", createReceptionistManagementPanel());
        mainTabbedPane.addTab("🔧 Admins", createAdminManagementPanel());
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header with search
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        add(mainTabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel mainHeader = new JPanel(new BorderLayout());
        mainHeader.setBackground(BACKGROUND_COLOR);
        
        // Title section
        JPanel titleSection = new JPanel(new BorderLayout());
        titleSection.setBackground(BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel("User Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_COLOR);
        
        JLabel subtitleLabel = new JLabel("Manage all users in the ATC Tuition Centre system");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(107, 114, 128));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(BACKGROUND_COLOR);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);
        
        // Quick stats panel
        JPanel quickStatsPanel = createQuickStatsPanel();
        
        titleSection.add(titlePanel, BorderLayout.WEST);
        titleSection.add(quickStatsPanel, BorderLayout.EAST);
        
        // Search section
        JPanel searchSection = createSearchPanel();
        
        mainHeader.add(titleSection, BorderLayout.NORTH);
        mainHeader.add(searchSection, BorderLayout.SOUTH);
        
        return mainHeader;
    }
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                "Search Users", 0, 0, new Font("Segoe UI", Font.BOLD, 14), TEXT_COLOR),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel searchLabel = new JLabel("🔍 Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchLabel.setForeground(TEXT_COLOR);
        
        JButton searchButton = createStyledButton("Search", PRIMARY_COLOR);
        JButton clearButton = createStyledButton("Clear", SECONDARY_COLOR);
        
        searchButton.addActionListener(e -> performSearch());
        clearButton.addActionListener(e -> clearSearch());
        
        panel.add(searchLabel);
        panel.add(searchField);
        panel.add(new JLabel("in"));
        panel.add(searchTypeCombo);
        panel.add(searchButton);
        panel.add(clearButton);
        
        return panel;
    }
    
    private JPanel createQuickStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 0));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(10, 0, 20, 0));
        
        // Get stats (excluding students)
        List<Tutor> tutors = dataManager.getAllTutors();
        List<Receptionist> receptionists = dataManager.getAllReceptionists();
        List<Admin> admins = dataManager.getAllAdmins();
        
        panel.add(createMiniStatCard("👩‍🏫", String.valueOf(tutors.size()), "Tutors"));
        panel.add(createMiniStatCard("📋", String.valueOf(receptionists.size()), "Receptionists"));
        panel.add(createMiniStatCard("🔧", String.valueOf(admins.size()), "Admins"));
        panel.add(createMiniStatCard("👥", String.valueOf(tutors.size() + receptionists.size() + admins.size()), "Total Staff"));
        
        return panel;
    }
    
    private JPanel createMiniStatCard(String icon, String value, String label) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(PRIMARY_COLOR);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel labelLabel = new JLabel(label);
        labelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelLabel.setForeground(new Color(107, 114, 128));
        labelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(iconLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(valueLabel);
        card.add(labelLabel);
        
        return card;
    }
    
    // ==================== SEARCH FUNCTIONALITY ====================
    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        String searchType = (String) searchTypeCombo.getSelectedItem();
        
        if (searchTerm.isEmpty()) {
            loadAllData();
            return;
        }
        
        SwingUtilities.invokeLater(() -> {
            switch (searchType) {
                case "Tutors":
                    updateTutorTable(dataManager.searchTutors(searchTerm));
                    mainTabbedPane.setSelectedIndex(0);
                    break;
                case "Receptionists":
                    updateReceptionistTable(dataManager.searchReceptionists(searchTerm));
                    mainTabbedPane.setSelectedIndex(1);
                    break;
                case "Admins":
                    updateAdminTable(dataManager.searchAdmins(searchTerm));
                    mainTabbedPane.setSelectedIndex(2);
                    break;
                default: // All Users
                    updateTutorTable(dataManager.searchTutors(searchTerm));
                    updateReceptionistTable(dataManager.searchReceptionists(searchTerm));
                    updateAdminTable(dataManager.searchAdmins(searchTerm));
                    break;
            }
        });
    }
    
    private void clearSearch() {
        searchField.setText("");
        searchTypeCombo.setSelectedIndex(0);
        loadAllData();
    }
    
    // ==================== TUTOR MANAGEMENT PANEL ====================
    private JPanel createTutorManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Header
        JLabel titleLabel = new JLabel("Tutor Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_COLOR);
        
        // Table setup - FIXED: Initialize tutorTableModel properly
        String[] tutorColumns = {"ID", "Name", "Email", "Phone", "Date of Birth", "Classes", "Status"};
        tutorTableModel = new DefaultTableModel(tutorColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tutorTable = createStyledTable(tutorTableModel);
        JScrollPane scrollPane = new JScrollPane(tutorTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
        
        // Buttons panel
        JPanel buttonsPanel = createUserManagementButtons("TUTOR");
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // ==================== RECEPTIONIST MANAGEMENT PANEL ====================
    private JPanel createReceptionistManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Header
        JLabel titleLabel = new JLabel("Receptionist Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_COLOR);
        
        // Table setup - FIXED: Initialize receptionistTableModel properly
        String[] receptionistColumns = {"ID", "Username", "Name", "Email", "Phone", "Status"};
        receptionistTableModel = new DefaultTableModel(receptionistColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        receptionistTable = createStyledTable(receptionistTableModel);
        JScrollPane scrollPane = new JScrollPane(receptionistTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
        
        // Buttons panel
        JPanel buttonsPanel = createUserManagementButtons("RECEPTIONIST");
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // ==================== ADMIN MANAGEMENT PANEL ====================
   private JPanel createAdminManagementPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(BACKGROUND_COLOR);
    panel.setBorder(new EmptyBorder(15, 15, 15, 15));
    
    // Header
    JLabel titleLabel = new JLabel("Administrator Management");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
    titleLabel.setForeground(TEXT_COLOR);
    
    // Table setup - UPDATED: Changed "Username" to "Role" for admins
    String[] adminColumns = {"ID", "Name", "Role", "Email", "Phone", "Status"};
    adminTableModel = new DefaultTableModel(adminColumns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    
    adminTable = createStyledTable(adminTableModel);
    JScrollPane scrollPane = new JScrollPane(adminTable);
    scrollPane.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
    
    // Buttons panel
    JPanel buttonsPanel = createUserManagementButtons("ADMIN");
    
    panel.add(titleLabel, BorderLayout.NORTH);
    panel.add(scrollPane, BorderLayout.CENTER);
    panel.add(buttonsPanel, BorderLayout.SOUTH);
    
    return panel;
}
    
    // ==================== UNIVERSAL BUTTON PANEL ====================
    private JPanel createUserManagementButtons(String userType) {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonsPanel.setBackground(BACKGROUND_COLOR);
        
        JButton editButton = createStyledButton("✏️ Edit Selected", PRIMARY_COLOR);
        JButton deleteButton = createStyledButton("🗑️ Delete Selected", ERROR_COLOR);
        JButton refreshButton = createStyledButton("🔄 Refresh", SECONDARY_COLOR);
        JButton viewDetailsButton = createStyledButton("👁️ View Details", ACCENT_COLOR);
        
        // Event listeners based on user type
        switch (userType) {
            case "TUTOR":
                editButton.addActionListener(e -> editSelectedUser(tutorTable, tutorTableModel, "TUTOR"));
                deleteButton.addActionListener(e -> deleteSelectedTutor());
                refreshButton.addActionListener(e -> loadTutorData());
                viewDetailsButton.addActionListener(e -> viewUserDetails(tutorTable, tutorTableModel, "TUTOR"));
                break;
            case "RECEPTIONIST":
                editButton.addActionListener(e -> editSelectedUser(receptionistTable, receptionistTableModel, "RECEPTIONIST"));
                deleteButton.addActionListener(e -> deleteSelectedReceptionist());
                refreshButton.addActionListener(e -> loadReceptionistData());
                viewDetailsButton.addActionListener(e -> viewUserDetails(receptionistTable, receptionistTableModel, "RECEPTIONIST"));
                break;
            case "ADMIN":
                editButton.addActionListener(e -> editSelectedUser(adminTable, adminTableModel, "ADMIN"));
                deleteButton.addActionListener(e -> deleteSelectedAdmin());
                refreshButton.addActionListener(e -> loadAdminData());
                viewDetailsButton.addActionListener(e -> viewUserDetails(adminTable, adminTableModel, "ADMIN"));
                break;
        }
        
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(viewDetailsButton);
        buttonsPanel.add(refreshButton);
        
        return buttonsPanel;
    }
    
    // ==================== HELPER METHODS ====================
    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(SECONDARY_COLOR);
        table.getTableHeader().setForeground(TEXT_COLOR);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(true);
        table.setGridColor(new Color(229, 231, 235));
        
        // Alternate row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                }
                return c;
            }
        });
        
        return table;
    }
    
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setBackground(backgroundColor);
        button.setForeground(backgroundColor.equals(SECONDARY_COLOR) ? TEXT_COLOR : Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    // ==================== EDIT USER FUNCTIONALITY ====================
    private void editSelectedUser(JTable table, DefaultTableModel model, String userType) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            String userId = (String) model.getValueAt(selectedRow, 0);
            String userName = (String) model.getValueAt(selectedRow, 1); // Name is always column 1 for staff
            
            try {
                EditUser editDialog = new EditUser(parentFrame, dataManager, userType, userId);
                editDialog.setVisible(true);
                
                // Refresh data if edit was successful
                if (editDialog.isEditSuccessful()) {
                    refreshData();
                    showSuccessMessage(userType + " '" + userName + "' updated successfully!");
                }
                
            } catch (Exception e) {
                showErrorMessage("Error opening edit dialog: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showWarningMessage("Please select a " + userType.toLowerCase() + " to edit.");
        }
    }
    
    // ==================== VIEW USER DETAILS ====================
    private void viewUserDetails(JTable table, DefaultTableModel model, String userType) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            String userId = (String) model.getValueAt(selectedRow, 0);
            String userName = (String) model.getValueAt(selectedRow, 1); // Name is always column 1 for staff
            
            StringBuilder details = new StringBuilder();
            details.append("=== ").append(userType).append(" DETAILS ===\n\n");
            
            // Get full user details
            User user = null;
            switch (userType) {
                case "TUTOR":
                    user = dataManager.getTutorById(userId);
                    if (user != null) {
                        Tutor tutor = (Tutor) user;
                        details.append("Tutor ID: ").append(tutor.getUserId()).append("\n");
                        details.append("Username: ").append(tutor.getUsername()).append("\n");
                        details.append("Name: ").append(tutor.getName()).append("\n");
                        details.append("Email: ").append(tutor.getEmail()).append("\n");
                        details.append("Phone: ").append(tutor.getPhone()).append("\n");
                        details.append("Date of Birth: ").append(tutor.getDateOfBirth()).append("\n");
                        
                        // Get classes taught by this tutor
                        List<ClassInfo> tutorClasses = dataManager.getClassesByTutor(userId);
                        details.append("Classes Teaching: ");
                        if (tutorClasses.isEmpty()) {
                            details.append("None assigned\n");
                        } else {
                            details.append("\n");
                            for (ClassInfo classInfo : tutorClasses) {
                                details.append("  - ").append(classInfo.getSubject())
                                       .append(" (").append(classInfo.getClassId()).append(")\n");
                            }
                        }
                    }
                    break;
                case "RECEPTIONIST":
                    user = dataManager.getReceptionistById(userId);
                    if (user != null) {
                        Receptionist receptionist = (Receptionist) user;
                        details.append("Receptionist ID: ").append(receptionist.getUserId()).append("\n");
                        details.append("Username: ").append(receptionist.getUsername()).append("\n");
                        details.append("Name: ").append(receptionist.getName()).append("\n");
                        details.append("Email: ").append(receptionist.getEmail()).append("\n");
                        details.append("Phone: ").append(receptionist.getPhone()).append("\n");
                        details.append("Department: ").append(receptionist.getDepartment()).append("\n");
                    }
                    break;
                case "ADMIN":
                    user = dataManager.getAdminById(userId);
                    if (user != null) {
                        Admin admin = (Admin) user;
                        details.append("Admin ID: ").append(admin.getUserId()).append("\n");
                        details.append("Username: ").append(admin.getUsername()).append("\n");
                        details.append("Name: ").append(admin.getName()).append("\n");
                        details.append("Email: ").append(admin.getEmail()).append("\n");
                        details.append("Phone: ").append(admin.getPhone()).append("\n");
                        details.append("Role: ").append(admin.getRole()).append("\n");
                    }
                    break;
            }
            
            if (user != null) {
                // Create dialog to show details
                JDialog detailsDialog = new JDialog(parentFrame, userType + " Details", true);
                detailsDialog.setLayout(new BorderLayout());
                
                JTextArea textArea = new JTextArea(details.toString());
                textArea.setEditable(false);
                textArea.setFont(new Font("Courier New", Font.PLAIN, 12));
                textArea.setBackground(SECONDARY_COLOR);
                textArea.setBorder(new EmptyBorder(15, 15, 15, 15));
                
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(400, 300));
                
                JButton closeButton = createStyledButton("Close", PRIMARY_COLOR);
                closeButton.addActionListener(e -> detailsDialog.dispose());
                
                JPanel buttonPanel = new JPanel(new FlowLayout());
                buttonPanel.setBackground(BACKGROUND_COLOR);
                buttonPanel.add(closeButton);
                
                detailsDialog.add(scrollPane, BorderLayout.CENTER);
                detailsDialog.add(buttonPanel, BorderLayout.SOUTH);
                detailsDialog.setSize(450, 400);
                detailsDialog.setLocationRelativeTo(parentFrame);
                detailsDialog.setVisible(true);
            } else {
                showErrorMessage("User details not found!");
            }
        } else {
            showWarningMessage("Please select a " + userType.toLowerCase() + " to view details.");
        }
    }
    
    // ==================== DATA LOADING METHODS (FIXED) ====================
    public void loadAllData() {
        SwingUtilities.invokeLater(() -> {
            loadTutorData();
            loadReceptionistData();
            loadAdminData();
        });
    }
    
    private void loadTutorData() {
        synchronized(fileLock) {
            List<Tutor> tutors = dataManager.getAllTutors();
            updateTutorTable(tutors);
        }
    }
    
    private void loadReceptionistData() {
        synchronized(fileLock) {
            List<Receptionist> receptionists = dataManager.getAllReceptionists();
            updateReceptionistTable(receptionists);
        }
    }
    
    private void loadAdminData() {
        synchronized(fileLock) {
            List<Admin> admins = dataManager.getAllAdmins();
            updateAdminTable(admins);
        }
    }
    
    // ==================== TABLE UPDATE METHODS (FIXED) ====================

    
    private void updateTutorTable(List<Tutor> tutors) {
        // FIXED: Add null check
        if (tutorTableModel == null) {
            System.err.println("ERROR: tutorTableModel is null - initialization failed");
            return;
        }
        
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> updateTutorTable(tutors));
            return;
        }
        
        tutorTableModel.setRowCount(0);
        
        for (Tutor tutor : tutors) {
            // Get classes taught by this tutor
            List<ClassInfo> tutorClasses = dataManager.getClassesByTutor(tutor.getUserId());
            StringBuilder classesDisplay = new StringBuilder();
            for (int i = 0; i < tutorClasses.size(); i++) {
                if (i > 0) classesDisplay.append(", ");
                classesDisplay.append(tutorClasses.get(i).getSubject());
            }
            
            Object[] row = {
                tutor.getUserId(),
                tutor.getName(),
                tutor.getEmail(),
                tutor.getPhone(),
                tutor.getDateOfBirth(),
                classesDisplay.toString(),
                "Active"
            };
            tutorTableModel.addRow(row);
        }
    }
    
    private void updateReceptionistTable(List<Receptionist> receptionists) {
        // FIXED: Add null check
        if (receptionistTableModel == null) {
            System.err.println("ERROR: receptionistTableModel is null - initialization failed");
            return;
        }
        
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> updateReceptionistTable(receptionists));
            return;
        }
        
        receptionistTableModel.setRowCount(0);
        
        for (Receptionist receptionist : receptionists) {
            Object[] row = {
                receptionist.getUserId(),
                receptionist.getUsername(),
                receptionist.getName(),
                receptionist.getEmail(),
                receptionist.getPhone(),
                "Active"
            };
            receptionistTableModel.addRow(row);
        }
    }
    
    private void updateAdminTable(List<Admin> admins) {
    // FIXED: Add null check
    if (adminTableModel == null) {
        System.err.println("ERROR: adminTableModel is null - initialization failed");
        return;
    }
    
    if (!SwingUtilities.isEventDispatchThread()) {
        SwingUtilities.invokeLater(() -> updateAdminTable(admins));
        return;
    }
    
    adminTableModel.setRowCount(0);
    
    for (Admin admin : admins) {
        Object[] row = {
            admin.getUserId(),
            admin.getName(),                    
            admin.getRole() != null ? admin.getRole() : "Administrator", 
            admin.getEmail(),
            admin.getPhone(),
            "Active"
        };
        adminTableModel.addRow(row);
    }
}
    
    // ==================== DELETE METHODS (Staff Only) ====================
    
    private void deleteSelectedTutor() {
        int selectedRow = tutorTable.getSelectedRow();
        if (selectedRow >= 0) {
            String tutorId = (String) tutorTableModel.getValueAt(selectedRow, 0);
            String tutorName = (String) tutorTableModel.getValueAt(selectedRow, 1);
            
            // Check if tutor has assigned classes
            List<ClassInfo> tutorClasses = dataManager.getClassesByTutor(tutorId);
            String warningMessage = "Are you sure you want to delete tutor: " + tutorName + "?";
            if (!tutorClasses.isEmpty()) {
                warningMessage += "\n\nWARNING: This tutor is currently assigned to " + 
                                tutorClasses.size() + " class(es). Deleting will affect class assignments.";
            }
            warningMessage += "\n\nThis action cannot be undone.";
            
            int confirm = JOptionPane.showConfirmDialog(parentFrame, warningMessage,
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (dataManager.deleteTutor(tutorId)) {
                    showSuccessMessage("Tutor '" + tutorName + "' deleted successfully!");
                    loadTutorData();
                    refreshData(); // Update stats
                } else {
                    showErrorMessage("Failed to delete tutor. Please try again.");
                }
            }
        } else {
            showWarningMessage("Please select a tutor to delete.");
        }
    }
    
    private void deleteSelectedReceptionist() {
        int selectedRow = receptionistTable.getSelectedRow();
        if (selectedRow >= 0) {
            String receptionistId = (String) receptionistTableModel.getValueAt(selectedRow, 0);
            String receptionistName = (String) receptionistTableModel.getValueAt(selectedRow, 2);
            
            int confirm = JOptionPane.showConfirmDialog(parentFrame,
                "Are you sure you want to delete receptionist: " + receptionistName + "?\n" +
                "This will remove their access to the system.\n\n" +
                "This action cannot be undone.",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (dataManager.deleteReceptionist(receptionistId)) {
                    showSuccessMessage("Receptionist '" + receptionistName + "' deleted successfully!");
                    loadReceptionistData();
                    refreshData(); // Update stats
                } else {
                    showErrorMessage("Failed to delete receptionist. Please try again.");
                }
            }
        } else {
            showWarningMessage("Please select a receptionist to delete.");
        }
    }
    
    private void deleteSelectedAdmin() {
        int selectedRow = adminTable.getSelectedRow();
        if (selectedRow >= 0) {
            String adminId = (String) adminTableModel.getValueAt(selectedRow, 0);
            String adminName = (String) adminTableModel.getValueAt(selectedRow, 2);
            
            // Check if this is the last admin
            List<Admin> allAdmins = dataManager.getAllAdmins();
            if (allAdmins.size() <= 1) {
                showErrorMessage("Cannot delete the last administrator account!\n" +
                               "At least one admin account must remain in the system.");
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(parentFrame,
                "Are you sure you want to delete admin: " + adminName + "?\n" +
                "WARNING: This will remove administrative access!\n\n" +
                "This action cannot be undone.",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (dataManager.deleteAdmin(adminId)) {
                    showSuccessMessage("Admin '" + adminName + "' deleted successfully!");
                    loadAdminData();
                    refreshData(); // Update stats
                } else {
                    showErrorMessage("Failed to delete admin. Please try again.");
                }
            }
        } else {
            showWarningMessage("Please select an admin to delete.");
        }
    }
    
    // ==================== REFRESH AND UTILITY METHODS ====================
    public void refreshData() {
        SwingUtilities.invokeLater(() -> {
            loadAllData();
            
            // Update quick stats panel
            removeAll();
            setupLayout();
            revalidate();
            repaint();
        });
    }
    
    // ==================== MESSAGE HELPER METHODS ====================
    private void showErrorMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(parentFrame, message, "Error", JOptionPane.ERROR_MESSAGE);
        });
    }
    
    private void showSuccessMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(parentFrame, message, "Success", JOptionPane.INFORMATION_MESSAGE);
        });
    }
    
    private void showWarningMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(parentFrame, message, "Warning", JOptionPane.WARNING_MESSAGE);
        });
    }
    
    // ==================== BULK OPERATIONS ====================
    public void exportUserData() {
        try {
            StringBuilder export = new StringBuilder();
            export.append("=== ATC TUITION CENTRE USER EXPORT ===\n");
            export.append("Generated on: ").append(java.time.LocalDateTime.now()).append("\n\n");
            
            // Export tutors only (no students)
            export.append("TUTORS:\n");
            export.append("ID\tName\tEmail\tPhone\tDate of Birth\n");
            List<Tutor> tutors = dataManager.getAllTutors();
            for (Tutor tutor : tutors) {
                export.append(tutor.getUserId()).append("\t")
                      .append(tutor.getName()).append("\t")
                      .append(tutor.getEmail()).append("\t")
                      .append(tutor.getPhone()).append("\t")
                      .append(tutor.getDateOfBirth()).append("\n");
            }
            
            // Export receptionists
            export.append("\nRECEPTIONISTS:\n");
            export.append("ID\tUsername\tName\tEmail\tPhone\n");
            List<Receptionist> receptionists = dataManager.getAllReceptionists();
            for (Receptionist receptionist : receptionists) {
                export.append(receptionist.getUserId()).append("\t")
                      .append(receptionist.getUsername()).append("\t")
                      .append(receptionist.getName()).append("\t")
                      .append(receptionist.getEmail()).append("\t")
                      .append(receptionist.getPhone()).append("\n");
            }
            
            // Export admins
            export.append("\nADMINS:\n");
            export.append("ID\tUsername\tName\tEmail\tPhone\n");
            List<Admin> admins = dataManager.getAllAdmins();
            for (Admin admin : admins) {
                export.append(admin.getUserId()).append("\t")
                      .append(admin.getUsername()).append("\t")
                      .append(admin.getName()).append("\t")
                      .append(admin.getEmail()).append("\t")
                      .append(admin.getPhone()).append("\n");
            }
            
            // Show export data
            JDialog exportDialog = new JDialog(parentFrame, "Export User Data", true);
            exportDialog.setLayout(new BorderLayout());
            
            JTextArea exportArea = new JTextArea(export.toString());
            exportArea.setEditable(false);
            exportArea.setFont(new Font("Courier New", Font.PLAIN, 10));
            exportArea.setBackground(SECONDARY_COLOR);
            
            JScrollPane scrollPane = new JScrollPane(exportArea);
            scrollPane.setPreferredSize(new Dimension(600, 400));
            
            JButton closeButton = createStyledButton("Close", PRIMARY_COLOR);
            closeButton.addActionListener(e -> exportDialog.dispose());
            
            JPanel buttonPanel = new JPanel(new FlowLayout());
            buttonPanel.add(closeButton);
            
            exportDialog.add(scrollPane, BorderLayout.CENTER);
            exportDialog.add(buttonPanel, BorderLayout.SOUTH);
            exportDialog.setSize(650, 500);
            exportDialog.setLocationRelativeTo(parentFrame);
            exportDialog.setVisible(true);
            
        } catch (Exception e) {
            showErrorMessage("Error exporting user data: " + e.getMessage());
        }
    }
    
    // ==================== ACCESS METHODS FOR INTEGRATION ====================
    public JTabbedPane getMainTabbedPane() {
        return mainTabbedPane;
    }
    
    public void selectTutorsTab() {
        mainTabbedPane.setSelectedIndex(0);
    }
    
    public void selectReceptionistsTab() {
        mainTabbedPane.setSelectedIndex(1);
    }
    
    public void selectAdminsTab() {
        mainTabbedPane.setSelectedIndex(2);
    }
    
    // Remove all student-related methods and tables
    // Students are managed in ReceptionistDashboard instead
}