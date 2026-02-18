import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Base User class
 */
public class User {
    protected String userId;
    protected String username;
    protected String password;
    protected String name;
    protected String email;
    protected String phone;
    protected int loginAttempts;
    protected boolean lockedOut;
    
    public User(String userId, String username, String password, String name, String email, String phone) {
        this(userId, username, password, name, email, phone, 0, false);
    }
    
    public User(String userId, String username, String password, String name, String email, 
               String phone, int loginAttempts, boolean lockedOut) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.loginAttempts = loginAttempts;
        this.lockedOut = lockedOut;
    }
    
    // Getters
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public int getLoginAttempts() { return loginAttempts; }
    public boolean isLockedOut() { return lockedOut; }
    
    // Setters
    public void setUserId(String userId) { this.userId = userId; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setLoginAttempts(int loginAttempts) { this.loginAttempts = loginAttempts; }
    public void setLockedOut(boolean lockedOut) { this.lockedOut = lockedOut; }
    
    /**
     * Static method to save users to file
     */
    public static void saveUsersToFile(List<?> users, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Object u : users) {
                if (u instanceof Student) {
                    writer.write(((Student) u).toFileString() + "\n");
                } else if (u instanceof Tutor) {
                    Tutor t = (Tutor) u;
                    writer.write(String.join(",", t.getUserId(), t.getUsername(), t.getPassword(),
                            t.getName(), t.getEmail(), t.getPhone(), t.getDateOfBirth()) + "\n");
                } else if (u instanceof Receptionist) {
                    Receptionist r = (Receptionist) u;
                    writer.write(String.join(",", r.getUserId(), r.getUsername(), r.getPassword(),
                            r.getName(), r.getEmail(), r.getPhone()) + "\n");
                } else if (u instanceof Admin) {
                    Admin a = (Admin) u;
                    writer.write(String.join(",", a.getUserId(), a.getUsername(), a.getPassword(),
                            a.getName(), a.getEmail(), a.getPhone()) + "\n");
                } else if (u instanceof User) {
                    User user = (User) u;
                    writer.write(String.join(",", user.getUserId(), user.getUsername(), user.getPassword(),
                            user.getName(), user.getEmail(), user.getPhone()) + "\n");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}

/**
 * Student class
 */
class Student extends User {
    private String ic;
    private String address;
    private String level;
    private String enrollmentMonth;
    private List<String> classIds; // Store class IDs instead of subject names
    private double totalBalance;
    
    public Student(String studentId, String ic, String username, String password,
                   String name, String email, String contact, String address,
                   String level, String enrollmentMonth, List<String> subjects,
                   double totalBalance, int loginAttempts, boolean lockedOut) {
        super(studentId, username, password, name, email, contact, loginAttempts, lockedOut);
        this.ic = ic;
        this.address = address;
        this.level = level;
        this.enrollmentMonth = enrollmentMonth;
        this.classIds = subjects != null ? new ArrayList<>(subjects) : new ArrayList<>();
        this.totalBalance = totalBalance;
    }
    
    public Student(String studentId, String ic, String username, String password,
                   String name, String email, String contact, String address,
                   String level, String enrollmentMonth, List<String> subjects, double totalBalance) {
        this(studentId, ic, username, password, name, email, contact,
                address, level, enrollmentMonth, subjects, totalBalance, 0, false);
    }
    
    // Legacy constructor for compatibility with original codebase
    public Student(String userId, String username, String password, String name, String email, 
                  String phone, String icPassport, String address, String level, String enrollmentMonth) {
        super(userId, username, password, name, email, phone);
        this.ic = icPassport;
        this.address = address;
        this.level = level;
        this.enrollmentMonth = enrollmentMonth;
        this.classIds = new ArrayList<>();
        this.totalBalance = 0.0;
    }
    
    // Getters
    public String getStudentId() { return userId; }
    public String getIc() { return ic; }
    public String getIcPassport() { return ic; } // Legacy compatibility
    public String getContactNumber() { return phone; }
    public String getAddress() { return address; }
    public String getLevel() { return level; }
    public String getEnrollmentMonth() { return enrollmentMonth; }
    public List<String> getSubjects() { return new ArrayList<>(classIds); }
    public String[] getClassIds() { 
        return classIds.toArray(new String[0]); 
    }
    public double getTotalBalance() { return totalBalance; }
    
    // Setters
    public void setIc(String ic) { this.ic = ic; }
    public void setIcPassport(String icPassport) { this.ic = icPassport; }
    public void setAddress(String address) { this.address = address; }
    public void setLevel(String level) { this.level = level; }
    public void setEnrollmentMonth(String enrollmentMonth) { this.enrollmentMonth = enrollmentMonth; }
    public void setClassIds(String[] classIds) { 
        this.classIds = new ArrayList<>();
        if (classIds != null) {
            for (String classId : classIds) {
                if (classId != null && !classId.trim().isEmpty()) {
                    this.classIds.add(classId.trim());
                }
            }
        }
    }
    public void setSubjects(String[] subjects) { setClassIds(subjects); }
    public void setTotalBalance(double totalBalance) { this.totalBalance = totalBalance; }
    
    /**
     * File operations
     */
    public String toFileString() {
        String subs = classIds != null ? String.join(";", classIds) : "";
        return String.join(",", userId, ic, password, name,
                email, phone, address, level, enrollmentMonth, subs);
    }
    
    public void setSubjectsFromString(String subjectsStr) {
        classIds = new ArrayList<>();
        if (subjectsStr != null && !subjectsStr.trim().isEmpty()) {
            String[] parts = subjectsStr.split(";");
            for (String part : parts) {
                if (part != null && !part.trim().isEmpty()) {
                    classIds.add(part.trim());
                }
            }
        }
    }
    
    public String getSubjectsString() {
        return classIds != null ? String.join(";", classIds) : "";
    }
    
    public String getClassIdsString() {
        return getSubjectsString();
    }
    
    /**
     * Enhanced functionality from new codebase
     */
    public String getSubjectsAsString() {
        if (classIds == null || classIds.isEmpty()) return "No subjects enrolled";
        return classIds.stream()
                .map(this::getSubjectNameFromClassId)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", "));
    }
    
    private String getSubjectNameFromClassId(String classId) {
        try (BufferedReader br = new BufferedReader(new FileReader("class.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3 && parts[0].equals(classId)) {
                    return parts[2];
                }
            }
        } catch (IOException ignored) {}
        return classId;
    }
    
    /**
     * Static method to load students from file
     */
    public static List<Student> loadStudentsFromFile(String filename) {
        List<Student> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length < 10) continue;
                String id = p[0].trim();
                String ic = p[1].trim();
                String pwd = p[2].trim();
                String name = p[3].trim();
                String email = p[4].trim();
                String contact = p[5].trim();
                String address = p[6].trim();
                String level = p[7].trim();
                String month = p[8].trim();
                List<String> subs = p[9].trim().isEmpty()
                        ? new ArrayList<>()
                        : Arrays.asList(p[9].split(";"));
                list.add(new Student(id, ic, id, pwd, name, email,
                        contact, address, level, month, subs, 0.0));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    /**
     * Profile update functionality
     */
    public void updateProfile(String newName, String newEmail,
                              String newContact, String newAddress) {
        this.name = newName;
        this.email = newEmail;
        this.phone = newContact;
        this.address = newAddress;
        saveSingleStudent();
    }
    
    /**
     * Payment-related functionality
     */
    public String viewPaymentStatus() {
        StringBuilder sb = new StringBuilder("=== Payment History ===\n");
        
        boolean foundInFile = false;
        try (BufferedReader br = new BufferedReader(new FileReader("payment_history.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length >= 4 && p[0].trim().equals(userId)) {
                    foundInFile = true;
                    sb.append("Recorded Payment\n")
                            .append("Student ID : ").append(p[0]).append("\n")
                            .append("Name       : ").append(p[1]).append("\n")
                            .append("Classes    : ").append(p[2]).append("\n")
                            .append("Amount     : RM").append(p[3]).append("\n")
                            .append("Method     : ").append(p.length > 4 ? p[4] : "-").append("\n")
                            .append("----------------------------\n");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        double dueNow = calculateTotalFees() - getTotalBalance();
        if (!foundInFile && dueNow <= 0) {
            sb.setLength(0);
            sb.append("You have credit RM").append(String.format("%.2f", -dueNow))
                    .append(" for next payment.\n");
        } else if (!foundInFile) {
            sb.setLength(0);
            sb.append("No payment record found.\n")
                    .append("Outstanding amount: RM")
                    .append(String.format("%.2f", dueNow))
                    .append("\n");
        }
        return sb.toString();
    }
    
    public double calculateTotalFees() {
        double total = 0.0;
        Set<String> enrolledSet = new HashSet<>(getSubjects());
        try (BufferedReader br = new BufferedReader(new FileReader("class.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6 && enrolledSet.contains(parts[0])) {
                    try { 
                        total += Double.parseDouble(parts[5]); 
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (IOException ignored) {}
        return total;
    }
    
    public void topUpBalance(double amount) {
        if (amount >= 0) {
            totalBalance = amount;
            saveSingleStudent();
        }
    }
    
    /**
     * Subject change functionality
     */
    public void requestSubjectChange(String current, String newSub) {
        try {
            String reqId = nextRequestId();
            try (FileWriter fw = new FileWriter("Subject_Change_Requests.txt", true)) {
                fw.write(reqId + "," + userId + "," + current + "," +
                        newSub + ",Pending\n");
            }
            javax.swing.JOptionPane.showMessageDialog(null, "Subject change request submitted successfully!");
        } catch (IOException ex) {
            ex.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "Error submitting request: " + ex.getMessage());
        }
    }
    
    private String nextRequestId() {
        int max = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("Subject_Change_Requests.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length > 0 && p[0].startsWith("REQ")) {
                    max = Math.max(max, Integer.parseInt(p[0].substring(3)));
                }
            }
        } catch (IOException | NumberFormatException ignored) {}
        return String.format("REQ%03d", max + 1);
    }
    
    public List<String> getAllSubjectsForForm() {
        List<String> out = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("class.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String classLevel = parts[2].toLowerCase();
                    if (classLevel.contains(level.toLowerCase())) {
                        out.add(parts[2]);
                    }
                }
            }
        } catch (IOException ignored) {}
        return out;
    }
    
    private void saveSingleStudent() {
        List<Student> all = loadStudentsFromFile("students.txt");
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getStudentId().equals(userId)) {
                all.set(i, this);
                break;
            }
        }
        User.saveUsersToFile(all, "students.txt");
    }
    
    // Class management methods
    public void addClassId(String classId) {
        if (classId != null && !classId.trim().isEmpty() && !classIds.contains(classId)) {
            classIds.add(classId);
        }
    }
    
    public void removeClassId(String classId) {
        classIds.remove(classId);
    }
    
    @Override
    public String toString() {
        return "Student{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", ic='" + ic + '\'' +
                ", email='" + email + '\'' +
                ", level='" + level + '\'' +
                ", subjects='" + getSubjectsString() + '\'' +
                '}';
    }
}

/**
 * Tutor class
 */
class Tutor extends User {
    private String dateOfBirth;
    
    public Tutor(String userId, String username, String password, String name, String email, 
                String phone, String dateOfBirth) {
        super(userId, username, password, name, email, phone);
        this.dateOfBirth = dateOfBirth;
    }
    
    // Getters
    public String getDateOfBirth() { return dateOfBirth; }
    
    // Setters
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
}

/**
 * Receptionist class
 */
class Receptionist extends User {
    private String department;
    
    public Receptionist(String userId, String username, String password, String name, 
                       String email, String phone, String department) {
        super(userId, username, password, name, email, phone);
        this.department = department;
    }
    
    public Receptionist(String userId, String username, String password, String name, 
                       String email, String phone) {
        this(userId, username, password, name, email, phone, "Reception");
    }
    
    // Getters
    public String getDepartment() { return department; }

    public String getAddress() {
    // Return a default address or add an address field to the Receptionist class
    return "Address not specified";
}
    
    // Setters
    public void setDepartment(String department) { this.department = department; }
}

/**
 * Admin class 
 */
class Admin extends User {
    private String role;
    
    public Admin(String userId, String username, String password, String name, 
                String email, String phone, String role) {
        super(userId, username, password, name, email, phone);
        this.role = role;
    }
    
    public Admin(String userId, String username, String password, String name, 
                String email, String phone) {
        this(userId, username, password, name, email, phone, "Administrator");
    }
    
    // Getters
    public String getRole() { return role; }
    
    // Setters
    public void setRole(String role) { this.role = role; }
}

/**
 * Class/Subject information - Enhanced version
 */
class ClassInfo {
    private String classId;
    private String tutorId;
    private String subject;
    private String description;
    private String schedule; // dates separated by semicolon
    private double fee;
    
    public ClassInfo(String classId, String tutorId, String subject, String description, 
                    String schedule, double fee) {
        this.classId = classId;
        this.tutorId = tutorId;
        this.subject = subject;
        this.description = description;
        this.schedule = schedule;
        this.fee = fee;
    }
    
    // Getters
    public String getClassId() { return classId; }
    public String getTutorId() { return tutorId; }
    public String getSubject() { return subject; }
    public String getDescription() { return description; }
    public String getSchedule() { return schedule; }
    public double getFee() { return fee; }
    
    // Setters
    public void setClassId(String classId) { this.classId = classId; }
    public void setTutorId(String tutorId) { this.tutorId = tutorId; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setDescription(String description) { this.description = description; }
    public void setSchedule(String schedule) { this.schedule = schedule; }
    public void setFee(double fee) { this.fee = fee; }
    
    @Override
    public String toString() {
        return subject + " (" + classId + ") - RM" + String.format("%.2f", fee);
    }
}

/**
 * Payment record with receipt functionality - Enhanced version
 */
class Payment {
    private String paymentId;
    private String receiptId;
    private String studentId;
    private String studentName;
    private String[] classIds;
    private double amount;
    private String paymentDate;
    private String paymentMethod;
    private String status; // PAID, PENDING, CANCELLED
    
    public Payment(String paymentId, String receiptId, String studentId, String studentName,
                  String[] classIds, double amount, String paymentDate, String paymentMethod) {
        this.paymentId = paymentId;
        this.receiptId = receiptId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.classIds = classIds;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.status = "PAID";
    }
    
    // Legacy constructor for compatibility
    public Payment(String paymentId, String studentId, String[] subjects, double amount, 
                  String paymentDate, String paymentMethod) {
        this(paymentId, "RCP" + paymentId.substring(3), studentId, "", subjects, amount, paymentDate, paymentMethod);
    }
    
    // Getters
    public String getPaymentId() { return paymentId; }
    public String getReceiptId() { return receiptId; }
    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String[] getSubjects() { return classIds; } // Keep method name for compatibility
    public String[] getClassIds() { return classIds; }
    public double getAmount() { return amount; }
    public String getPaymentDate() { return paymentDate; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getStatus() { return status; }
    
    // Setters
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public void setReceiptId(String receiptId) { this.receiptId = receiptId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public void setSubjects(String[] subjects) { this.classIds = subjects; }
    public void setClassIds(String[] classIds) { this.classIds = classIds; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setStatus(String status) { this.status = status; }
    
    public String getSubjectsString() {
        StringBuilder sb = new StringBuilder();
        for (String classId : classIds) {
            if (classId != null && !classId.trim().isEmpty()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(classId);
            }
        }
        return sb.toString();
    }
    
    public String getClassIdsString() {
        return getSubjectsString();
    }
    
    @Override
    public String toString() {
        return String.format("Payment{receiptId='%s', studentId='%s', amount=%.2f, date='%s', method='%s'}", 
            receiptId, studentId, amount, paymentDate, paymentMethod);
    }
}