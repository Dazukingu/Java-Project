import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * DataManager class handles all business logic operations
 * Updated version with flexible enrollment (1-3 subjects) support
 */
public class DataManager {
    private FileHandler fileHandler;
    
    public DataManager() {
        this.fileHandler = new FileHandler();
    }
    
    /**
     * Registers a new student with flexible enrollment in subjects using class IDs (1-3 subjects)
     */
    public boolean registerStudent(String name, String icPassport, String email, String phone, 
                                 String address, String level, String enrollmentMonth, 
                                 List<String> selectedClassIds) {
        try {
            // Validate flexible enrollment (1-3 subjects)
            if (!validateClassEnrollment(selectedClassIds, level)) {
                throw new IllegalArgumentException("Invalid class enrollment. Students must enroll in 1-3 subjects for their level.");
            }
            
            // Generate new student ID
            String studentId = fileHandler.generateNextStudentId();
            
            // Create student object
            Student student = new Student(
                studentId,
                icPassport, // Use IC/Passport as IC field
                studentId,  // Use studentId as username
                "password123", // Default password
                name,
                email,
                phone,
                address,
                level,
                enrollmentMonth,
                selectedClassIds,
                0.0
            );
            
            // Read existing students
            List<Student> students = fileHandler.readStudents();
            
            // Check for duplicate IC/Passport
            for (Student existingStudent : students) {
                if (existingStudent.getIc().equals(icPassport)) {
                    throw new IllegalArgumentException("Student with IC/Passport " + icPassport + " already exists");
                }
            }
            
            // Add new student
            students.add(student);
            
            // Write back to file
            return fileHandler.writeStudents(students);
            
        } catch (Exception e) {
            System.err.println("Error registering student: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Updates student's class enrollment using class IDs with flexible validation (1-3 subjects)
     */
    public boolean updateStudentSubjects(String studentId, List<String> newClassIds) {
        try {
            // Get student to validate level
            Student student = getStudentById(studentId);
            if (student == null) {
                System.err.println("Student not found: " + studentId);
                return false;
            }
            
            // Validate flexible enrollment
            if (!validateClassEnrollment(newClassIds, student.getLevel())) {
                System.err.println("Invalid class enrollment for student " + studentId + 
                                 ": must have 1-3 subjects for level " + student.getLevel());
                return false;
            }
            
            List<Student> students = fileHandler.readStudents();
            
            for (Student s : students) {
                if (s.getUserId().equals(studentId)) {
                    // Update class IDs
                    s.setClassIds(newClassIds.toArray(new String[0]));
                    return fileHandler.writeStudents(students);
                }
            }
            
            return false; // Student not found
        } catch (Exception e) {
            System.err.println("Error updating student subjects: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Processes payment using class IDs and generates receipt with proper ID
     */
    public String processPayment(String studentId, List<String> classIds, double amount, String paymentMethod) {
        try {
            // Get student information
            Student student = getStudentById(studentId);
            if (student == null) {
                throw new IllegalArgumentException("Student not found with ID: " + studentId);
            }
            
            // Generate payment and receipt IDs
            String paymentId = fileHandler.generateNextPaymentId();
            String receiptId = fileHandler.generateNextReceiptId();
            
            // Create payment record with enhanced data
            String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            Payment payment = new Payment(
                paymentId,
                receiptId,
                studentId,
                student.getName(),
                classIds.toArray(new String[0]),
                amount,
                currentDate,
                paymentMethod
            );
            
            // Read existing payments
            List<Payment> payments = fileHandler.readPayments();
            payments.add(payment);
            
            // Write back to payments file
            boolean paymentSaved = fileHandler.writePayments(payments);
            
            // Also write to payment_history.txt for student portal compatibility
            try (java.io.FileWriter fw = new java.io.FileWriter("payment_history.txt", true)) {
                String record = String.join(",",
                    studentId,
                    student.getName(),
                    String.join(";", classIds),
                    String.format("%.2f", amount),
                    paymentMethod.toLowerCase().replace(" ", ""));
                fw.write(record + "\n");
            } catch (java.io.IOException e) {
                System.err.println("Error writing to payment history: " + e.getMessage());
            }
            
            if (paymentSaved) {
                return generateReceipt(payment, student);
            } else {
                return null;
            }
            
        } catch (Exception e) {
            System.err.println("Error processing payment: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Generates detailed receipt text with student and class information
     */
    private String generateReceipt(Payment payment, Student student) {
        StringBuilder receipt = new StringBuilder();
        
        // Header
        receipt.append("===============================\n");
        receipt.append("    ATC TUITION CENTRE\n");
        receipt.append("         RECEIPT\n");
        receipt.append("===============================\n");
        
        // Receipt and Payment Info
        receipt.append("Receipt ID: ").append(payment.getReceiptId()).append("\n");
        receipt.append("Payment ID: ").append(payment.getPaymentId()).append("\n");
        receipt.append("Date: ").append(payment.getPaymentDate()).append("\n");
        receipt.append("Payment Method: ").append(payment.getPaymentMethod()).append("\n");
        receipt.append("Status: ").append(payment.getStatus()).append("\n");
        
        receipt.append("-------------------------------\n");
        
        // Student Information
        receipt.append("STUDENT INFORMATION:\n");
        receipt.append("Student ID: ").append(student.getUserId()).append("\n");
        receipt.append("Name: ").append(student.getName()).append("\n");
        receipt.append("IC/Passport: ").append(student.getIc()).append("\n");
        receipt.append("Email: ").append(student.getEmail()).append("\n");
        receipt.append("Phone: ").append(student.getPhone()).append("\n");
        receipt.append("Level: ").append(student.getLevel()).append("\n");
        
        receipt.append("-------------------------------\n");
        
        // Class Details
        receipt.append("CLASSES PAID:\n");
        List<ClassInfo> allClasses = getAllClasses();
        Map<String, ClassInfo> classMap = new HashMap<>();
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
                    receipt.append("- ").append(classId).append(" (Details not found)\n");
                }
            }
        }
        
        receipt.append("-------------------------------\n");
        
        // Financial Summary
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
    
    /**
     * Deletes a student
     */
    public boolean deleteStudent(String studentId) {
        try {
            List<Student> students = fileHandler.readStudents();
            students.removeIf(student -> student.getUserId().equals(studentId));
            return fileHandler.writeStudents(students);
        } catch (Exception e) {
            System.err.println("Error deleting student: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Updates receptionist profile
     */
    public boolean updateReceptionistProfile(String userId, String name, String email, String phone) {
        try {
            List<Receptionist> receptionists = fileHandler.readReceptionists();
            
            for (Receptionist receptionist : receptionists) {
                if (receptionist.getUserId().equals(userId)) {
                    receptionist.setName(name);
                    receptionist.setEmail(email);
                    receptionist.setPhone(phone);
                    
                    return fileHandler.writeReceptionists(receptionists);
                }
            }
            
            return false; // Receptionist not found
        } catch (Exception e) {
            System.err.println("Error updating receptionist profile: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets all students
     */
    public List<Student> getAllStudents() {
        return fileHandler.readStudents();
    }
    
    /**
     * Gets student by ID
     */
    public Student getStudentById(String studentId) {
        List<Student> students = fileHandler.readStudents();
        for (Student student : students) {
            if (student.getUserId().equals(studentId)) {
                return student;
            }
        }
        return null;
    }
    
    /**
     * Gets all available classes/subjects
     */
    public List<ClassInfo> getAllClasses() {
        return fileHandler.readClasses();
    }
    
    /**
     * Gets subjects by level with debugging
     */
    public List<ClassInfo> getSubjectsByLevel(String level) {
        List<ClassInfo> allClasses = fileHandler.readClasses();
        List<ClassInfo> filteredClasses = new ArrayList<>();
        
        System.out.println("DEBUG: Total classes available: " + allClasses.size());
        System.out.println("DEBUG: Filtering for level: " + level);
        
        for (ClassInfo classInfo : allClasses) {
            System.out.println("DEBUG: Checking class " + classInfo.getClassId() + 
                             " - Subject: " + classInfo.getSubject() + 
                             " - Fee: " + classInfo.getFee());
            
            if (classInfo.getSubject().contains(level)) {
                filteredClasses.add(classInfo);
                System.out.println("DEBUG: ✓ Added to filtered list");
            } else {
                System.out.println("DEBUG: ✗ Does not match level " + level);
            }
        }
        
        System.out.println("DEBUG: Filtered classes for " + level + ": " + filteredClasses.size());
        return filteredClasses;
    }
    
    /**
     * Calculates total fee for selected class IDs
     */
    public double calculateTotalFee(List<String> classIds) {
        List<ClassInfo> allClasses = fileHandler.readClasses();
        double totalFee = 0.0;
        
        System.out.println("DEBUG: Calculating fees for " + classIds.size() + " classes");
        
        for (String classId : classIds) {
            System.out.println("DEBUG: Looking for class ID: " + classId);
            boolean found = false;
            
            for (ClassInfo classInfo : allClasses) {
                if (classInfo.getClassId().equals(classId)) {
                    totalFee += classInfo.getFee();
                    System.out.println("DEBUG: Found " + classId + " - Fee: " + classInfo.getFee() + " - Running total: " + totalFee);
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                System.out.println("DEBUG: Class ID " + classId + " not found in available classes!");
            }
        }
        
        System.out.println("DEBUG: Final total fee: " + totalFee);
        return totalFee;
    }
    
    /**
     * Alternative fee calculation method using ClassInfo objects directly
     */
    public double calculateTotalFeeFromClasses(List<ClassInfo> selectedClasses) {
        double totalFee = 0.0;
        
        for (ClassInfo classInfo : selectedClasses) {
            totalFee += classInfo.getFee();
        }
        
        return totalFee;
    }
    
    /**
     * Gets all payments for a student
     */
    public List<Payment> getStudentPayments(String studentId) {
        List<Payment> allPayments = fileHandler.readPayments();
        List<Payment> studentPayments = new ArrayList<>();
        
        for (Payment payment : allPayments) {
            if (payment.getStudentId().equals(studentId)) {
                studentPayments.add(payment);
            }
        }
        
        return studentPayments;
    }
    
    /**
     * Searches students by name or ID
     */
public List<Student> searchStudents(String searchTerm) {
    List<Student> allStudents = getAllStudents();
    return allStudents.stream()
        .filter(student -> 
            student.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
            student.getStudentId().toLowerCase().contains(searchTerm.toLowerCase()) ||
            student.getEmail().toLowerCase().contains(searchTerm.toLowerCase()) ||
            student.getIc().toLowerCase().contains(searchTerm.toLowerCase())
        )
        .collect(java.util.stream.Collectors.toList());
}

public List<Tutor> searchTutors(String searchTerm) {
    List<Tutor> allTutors = getAllTutors();
    return allTutors.stream()
        .filter(tutor -> 
            tutor.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
            tutor.getUserId().toLowerCase().contains(searchTerm.toLowerCase()) ||
            tutor.getEmail().toLowerCase().contains(searchTerm.toLowerCase())
        )
        .collect(java.util.stream.Collectors.toList());
}

public List<Receptionist> searchReceptionists(String searchTerm) {
    List<Receptionist> allReceptionists = getAllReceptionists();
    return allReceptionists.stream()
        .filter(receptionist -> 
            receptionist.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
            receptionist.getUserId().toLowerCase().contains(searchTerm.toLowerCase()) ||
            receptionist.getEmail().toLowerCase().contains(searchTerm.toLowerCase())
        )
        .collect(java.util.stream.Collectors.toList());
}

public List<Admin> searchAdmins(String searchTerm) {
    List<Admin> allAdmins = getAllAdmins();
    return allAdmins.stream()
        .filter(admin -> 
            admin.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
            admin.getUserId().toLowerCase().contains(searchTerm.toLowerCase()) ||
            admin.getEmail().toLowerCase().contains(searchTerm.toLowerCase())
        )
        .collect(java.util.stream.Collectors.toList());
}

public List<ClassInfo> getClassesByTutor(String tutorId) {
    List<ClassInfo> allClasses = getAllClasses();
    return allClasses.stream()
        .filter(classInfo -> tutorId.equals(classInfo.getTutorId()))
        .collect(java.util.stream.Collectors.toList());
}

public Tutor getTutorById(String tutorId) {
    List<Tutor> tutors = getAllTutors();
    return tutors.stream()
        .filter(tutor -> tutor.getUserId().equals(tutorId))
        .findFirst()
        .orElse(null);
}

public Receptionist getReceptionistById(String receptionistId) {
    List<Receptionist> receptionists = getAllReceptionists();
    return receptionists.stream()
        .filter(receptionist -> receptionist.getUserId().equals(receptionistId))
        .findFirst()
        .orElse(null);
}

public Admin getAdminById(String adminId) {
    List<Admin> admins = getAllAdmins();
    return admins.stream()
        .filter(admin -> admin.getUserId().equals(adminId))
        .findFirst()
        .orElse(null);
}

public boolean addStudent(Student student) {
    try {
        List<Student> students = fileHandler.readStudents();
        students.add(student);
        return fileHandler.writeStudents(students);
    } catch (Exception e) {
        System.err.println("Error adding student: " + e.getMessage());
        return false;
    }
}

public boolean deleteTutor(String tutorId) {
    try {
        java.io.File file = new java.io.File("tutor.txt");
        java.util.List<String> lines = new java.util.ArrayList<>();
        boolean found = false;
        
        if (file.exists()) {
            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length > 0 && !parts[0].equals(tutorId)) {
                        lines.add(line);
                    } else if (parts.length > 0 && parts[0].equals(tutorId)) {
                        found = true;
                    }
                }
            }
        }
        
        if (found) {
            try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(file))) {
                for (String line : lines) {
                    writer.println(line);
                }
            }
        }
        
        return found;
    } catch (java.io.IOException e) {
        e.printStackTrace();
        return false;
    }
}

public boolean deleteReceptionist(String receptionistId) {
    try {
        java.io.File file = new java.io.File("receptionist.txt");
        java.util.List<String> lines = new java.util.ArrayList<>();
        boolean found = false;
        
        if (file.exists()) {
            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length > 0 && !parts[0].equals(receptionistId)) {
                        lines.add(line);
                    } else if (parts.length > 0 && parts[0].equals(receptionistId)) {
                        found = true;
                    }
                }
            }
        }
        
        if (found) {
            try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(file))) {
                for (String line : lines) {
                    writer.println(line);
                }
            }
        }
        
        return found;
    } catch (java.io.IOException e) {
        e.printStackTrace();
        return false;
    }
}

public boolean deleteAdmin(String adminId) {
    try {
        java.io.File file = new java.io.File("admin.txt");
        java.util.List<String> lines = new java.util.ArrayList<>();
        boolean found = false;
        
        if (file.exists()) {
            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length > 0 && !parts[0].equals(adminId)) {
                        lines.add(line);
                    } else if (parts.length > 0 && parts[0].equals(adminId)) {
                        found = true;
                    }
                }
            }
        }
        
        if (found) {
            try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(file))) {
                for (String line : lines) {
                    writer.println(line);
                }
            }
        }
        
        return found;
    } catch (java.io.IOException e) {
        e.printStackTrace();
        return false;
    }
}

public boolean changeUserPassword(String userId, String userType, String currentPassword, String newPassword) {
    try {
        String filename;
        switch (userType.toLowerCase()) {
            case "student":
                filename = "students.txt";
                break;
            case "tutor":
                filename = "tutor.txt";
                break;
            case "receptionist":
                filename = "receptionist.txt";
                break;
            case "admin":
                filename = "admin.txt";
                break;
            default:
                return false;
        }
        
        java.io.File file = new java.io.File(filename);
        java.util.List<String> lines = new java.util.ArrayList<>();
        boolean found = false;
        
        if (file.exists()) {
            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }
        }
        
        for (int i = 0; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",");
            if (parts.length >= 3 && parts[0].equals(userId)) {
                // Verify current password
                if (parts[2].equals(currentPassword)) {
                    parts[2] = newPassword;
                    lines.set(i, String.join(",", parts));
                    found = true;
                }
                break;
            }
        }
        
        if (found) {
            try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(file))) {
                for (String line : lines) {
                    writer.println(line);
                }
            }
        }
        
        return found;
    } catch (java.io.IOException e) {
        e.printStackTrace();
        return false;
    }
}
    /**
     * Gets all tutors
     */
    public List<Tutor> getAllTutors() {
        return fileHandler.readTutors();
    }
    
    /**
     * Gets all receptionists
     */
    public List<Receptionist> getAllReceptionists() {
        return fileHandler.readReceptionists();
    }
    
    /**
     * Gets all admins
     */
    public List<Admin> getAllAdmins() {
        return fileHandler.readAdmins();
    }
    
    /**
     * Enhanced student management methods
     */
    public boolean updateStudentProfile(String studentId, String name, String email, 
                                       String phone, String address) {
        try {
            List<Student> students = fileHandler.readStudents();
            
            for (Student student : students) {
                if (student.getUserId().equals(studentId)) {
                    student.setName(name);
                    student.setEmail(email);
                    student.setPhone(phone);
                    student.setAddress(address);
                    
                    return fileHandler.writeStudents(students);
                }
            }
            
            return false; // Student not found
        } catch (Exception e) {
            System.err.println("Error updating student profile: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Subject change request management
     */
    public List<String> getPendingSubjectChangeRequests(String studentId) {
        return FileHandler.SubjectChangeUtil.getPendingRequestsForStudent(studentId);
    }
    
    public boolean deleteSubjectChangeRequest(String studentId, String requestId) {
        return FileHandler.SubjectChangeUtil.deletePendingRequest(studentId, requestId);
    }
    
    public void submitSubjectChangeRequest(String studentId, String currentClassId, String newClassId) {
        // Generate request ID
        String requestId = generateNextRequestId();
        FileHandler.SubjectChangeUtil.writeSubjectChangeRequest(requestId, studentId, currentClassId, newClassId, "Pending");
    }
    
    private String generateNextRequestId() {
        int max = 0;
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("Subject_Change_Requests.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length > 0 && p[0].startsWith("REQ")) {
                    try {
                        max = Math.max(max, Integer.parseInt(p[0].substring(3)));
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (java.io.IOException ignored) {}
        return String.format("REQ%03d", max + 1);
    }
    
    /**
     * Payment history management for student portal
     */
    public String getStudentPaymentHistory(String studentId) {
        StringBuilder sb = new StringBuilder("=== Payment History ===\n");
        
        boolean foundInFile = false;
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("payment_history.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length >= 4 && p[0].trim().equals(studentId)) {
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
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
        
        if (!foundInFile) {
            sb.setLength(0);
            sb.append("No payment record found.\n");
        }
        
        return sb.toString();
    }
    
    /**
     * UPDATED: Flexible class enrollment validation (1-3 subjects)
     */
    public boolean validateClassEnrollment(List<String> classIds, String studentLevel) {
        // Check if classIds is null or empty
        if (classIds == null || classIds.isEmpty()) {
            System.out.println("DEBUG: Validation failed - no classes selected");
            return false; // Minimum 1 subject required
        }
        
        // Check if more than 3 subjects selected
        if (classIds.size() > 3) {
            System.out.println("DEBUG: Validation failed - too many classes: " + classIds.size() + " (max 3)");
            return false; // Maximum 3 subjects allowed
        }
        
        System.out.println("DEBUG: Validating " + classIds.size() + " classes for level: " + studentLevel);
        
        // Check if all classes are for the correct level
        List<ClassInfo> allClasses = getAllClasses();
        Map<String, ClassInfo> classMap = new HashMap<>();
        for (ClassInfo classInfo : allClasses) {
            classMap.put(classInfo.getClassId(), classInfo);
        }
        
        for (String classId : classIds) {
            ClassInfo classInfo = classMap.get(classId);
            if (classInfo == null) {
                System.out.println("DEBUG: Validation failed - class not found: " + classId);
                return false; // Class not found
            }
            
            if (!classInfo.getSubject().contains(studentLevel)) {
                System.out.println("DEBUG: Validation failed - class " + classId + 
                                 " subject '" + classInfo.getSubject() + 
                                 "' does not match level '" + studentLevel + "'");
                return false; // Class not for student's level
            }
            
            System.out.println("DEBUG: ✓ Class " + classId + " validated for level " + studentLevel);
        }
        
        System.out.println("DEBUG: ✅ All " + classIds.size() + " classes validated successfully");
        return true; // 1-3 subjects, all valid for student's level
    }
    
    /**
     * ENHANCED: Validates student enrollment with detailed feedback
     */
    public EnrollmentValidationResult validateStudentEnrollment(String studentId, List<String> classIds) {
        Student student = getStudentById(studentId);
        if (student == null) {
            return new EnrollmentValidationResult(false, "Student not found");
        }
        
        if (classIds == null || classIds.isEmpty()) {
            return new EnrollmentValidationResult(false, "Minimum 1 subject required");
        }
        
        if (classIds.size() > 3) {
            return new EnrollmentValidationResult(false, 
                "Maximum 3 subjects allowed. Currently selected: " + classIds.size());
        }
        
        // Check class validity
        List<ClassInfo> allClasses = getAllClasses();
        Map<String, ClassInfo> classMap = new HashMap<>();
        for (ClassInfo classInfo : allClasses) {
            classMap.put(classInfo.getClassId(), classInfo);
        }
        
        for (String classId : classIds) {
            ClassInfo classInfo = classMap.get(classId);
            if (classInfo == null) {
                return new EnrollmentValidationResult(false, "Class not found: " + classId);
            }
            
            if (!classInfo.getSubject().contains(student.getLevel())) {
                return new EnrollmentValidationResult(false, 
                    "Class " + classId + " is not available for " + student.getLevel());
            }
        }
        
        return new EnrollmentValidationResult(true, 
            "Enrollment valid: " + classIds.size() + " subject(s) for " + student.getLevel());
    }
    
    /**
     * Helper class for detailed enrollment validation results
     */
    public static class EnrollmentValidationResult {
        private final boolean valid;
        private final String message;
        
        public EnrollmentValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
    }
    
    /**
     * ENHANCED: Get enrollment statistics for analytics
     */
    public EnrollmentStatistics getEnrollmentStatistics() {
        List<Student> students = getAllStudents();
        int oneSubject = 0, twoSubjects = 0, threeSubjects = 0, noSubjects = 0;
        double totalRevenue = 0.0;
        
        for (Student student : students) {
            int subjectCount = student.getSubjects().size();
            switch (subjectCount) {
                case 0: noSubjects++; break;
                case 1: oneSubject++; break;
                case 2: twoSubjects++; break;
                case 3: threeSubjects++; break;
            }
            totalRevenue += calculateTotalFee(student.getSubjects());
        }
        
        return new EnrollmentStatistics(students.size(), oneSubject, twoSubjects, 
                                       threeSubjects, noSubjects, totalRevenue);
    }
    
    /**
     * Helper class for enrollment statistics
     */
    public static class EnrollmentStatistics {
        private final int totalStudents;
        private final int oneSubjectStudents;
        private final int twoSubjectStudents;
        private final int threeSubjectStudents;
        private final int noSubjectStudents;
        private final double totalRevenue;
        
        public EnrollmentStatistics(int totalStudents, int oneSubject, int twoSubjects, 
                                  int threeSubjects, int noSubjects, double totalRevenue) {
            this.totalStudents = totalStudents;
            this.oneSubjectStudents = oneSubject;
            this.twoSubjectStudents = twoSubjects;
            this.threeSubjectStudents = threeSubjects;
            this.noSubjectStudents = noSubjects;
            this.totalRevenue = totalRevenue;
        }
        
        // Getters
        public int getTotalStudents() { return totalStudents; }
        public int getOneSubjectStudents() { return oneSubjectStudents; }
        public int getTwoSubjectStudents() { return twoSubjectStudents; }
        public int getThreeSubjectStudents() { return threeSubjectStudents; }
        public int getNoSubjectStudents() { return noSubjectStudents; }
        public double getTotalRevenue() { return totalRevenue; }
        
        public double getAverageSubjectsPerStudent() {
            if (totalStudents == 0) return 0;
            return (double)(oneSubjectStudents + (twoSubjectStudents * 2) + (threeSubjectStudents * 3)) / totalStudents;
        }
        
        public double getEnrollmentRate() {
            if (totalStudents == 0) return 0;
            return (double)(totalStudents - noSubjectStudents) / totalStudents * 100;
        }
    }
}