import java.io.*;
import java.util.*;

/**
 * FileHandler class manages all file operations for the ATC system
 * Unified version combining both codebases with enhanced functionality
 */
public class FileHandler {
    private static final String STUDENTS_FILE = "students.txt";
    private static final String TUTORS_FILE = "tutor.txt";
    private static final String RECEPTIONISTS_FILE = "receptionist.txt";
    private static final String ADMINS_FILE = "admin.txt";
    private static final String CLASSES_FILE = "class.txt";
    private static final String PAYMENTS_FILE = "payments.txt";
    private static final String PAYMENT_HISTORY_FILE = "payment_history.txt";
    private static final String SUBJECT_CHANGE_REQUESTS_FILE = "Subject_Change_Requests.txt";
    
    // Get the directory where the class files are located
    private String getFilePath(String filename) {
        try {
            String classPath = System.getProperty("user.dir");
            return classPath + File.separator + filename;
        } catch (Exception e) {
            return filename; // fallback to current directory
        }
    }
    
    /**
     * Initializes all data files with sample data if they don't exist
     */
    public static void initializeDataFiles() {
        FileHandler handler = new FileHandler();
        
        try {
            // Create students.txt with sample data
            File studentsFile = new File(handler.getFilePath(STUDENTS_FILE));
            if (!studentsFile.exists()) {
                try (PrintWriter writer = new PrintWriter(new FileWriter(studentsFile))) {
                    writer.println("STU001,0538291933,password123,Vince,vince@gmail.com,019-222-333,Kuala Lumpur,Form 5,January,CL007;CL008;CL015");
                    writer.println("STU002,39459933,password123,kim,kim@gmail.com,018-383-3848,Kuala Lumpur,Form 5,January,CL007;CL008;CL014");
                    writer.println("STU003,3242324424,password123,Sebastian,93920482@yahoo.com,018-239-3933,KualaLumpur,Form 1,January,CL001;CL005;CL006");
                    writer.println("STU004,038282845,password123,Max,maxium@gmail.com,016-999-9111,Pahang,Form 4,January,CL004;CL012;CL013");
                    System.out.println("Created students.txt with sample data.");
                }
            }
            
            // Create receptionist.txt with default accounts
            File receptFile = new File(handler.getFilePath(RECEPTIONISTS_FILE));
            if (!receptFile.exists()) {
                try (PrintWriter writer = new PrintWriter(new FileWriter(receptFile))) {
                    writer.println("RC001,sarah,sarah123,Sarah Johnson,sarah@atc.edu.my,0123456789");
                    writer.println("RC002,john,john456,John Smith,john@atc.edu.my,0123456788");
                    writer.println("RC003,mary,mary789,Mary Wilson,mary@atc.edu.my,0123456787");
                    System.out.println("Created receptionist.txt with default accounts.");
                }
            }
            
            // Create admin.txt with default accounts
            File adminFile = new File(handler.getFilePath(ADMINS_FILE));
            if (!adminFile.exists()) {
                try (PrintWriter writer = new PrintWriter(new FileWriter(adminFile))) {
                    writer.println("AD001,admin,admin123,Administrator,admin@atc.edu.my,0123456790");
                    writer.println("AD002,manager,manager456,System Manager,manager@atc.edu.my,0123456791");
                    System.out.println("Created admin.txt with default accounts.");
                }
            }
            
            // Create tutor.txt with sample data
            File tutorFile = new File(handler.getFilePath(TUTORS_FILE));
            if (!tutorFile.exists()) {
                try (PrintWriter writer = new PrintWriter(new FileWriter(tutorFile))) {
                    writer.println("TC001,Alice,pass123,alice@gmail.com,06-06-1998,0123552843");
                    writer.println("TC002,Mike,123pass,mike@gmail.com,30-02-1990,01160740843");
                    writer.println("TC003,Vincent,4321,vincent@gmail.com,05-07-1997,0183571182");
                    writer.println("TC004,Lisa,lisa321,lisa@gmail.com,15-03-1994,0124567890");
                    writer.println("TC005,Daniel,daniel123,daniel@gmail.com,22-09-1995,0172345678");
                    writer.println("TC006,Nina,nina456,nina@gmail.com,08-12-1992,0113456789");
                    writer.println("TC007,Ethan,ethan789,ethan@gmail.com,03-07-2000,0109876543");
                    writer.println("TC008,Grace,grace123,grace@gmail.com,19-01-1996,0188765432");
                    writer.println("TC009,Samuel,samuel456,samuel@gmail.com,27-11-1993,0197654321");
                    writer.println("TC010,Rachel,rachel789,rachel@gmail.com,11-05-2002,0161234567");
                    System.out.println("Created tutor.txt with sample data.");
                }
            }
            
            // Create class.txt with expanded course information
            File classFile = new File(handler.getFilePath(CLASSES_FILE));
            if (!classFile.exists()) {
                try (PrintWriter writer = new PrintWriter(new FileWriter(classFile))) {
                    // Form 1 classes
                    writer.println("CL001,TC001,Mathematics Form 1,Basic math for beginners,2025-07-01; 2025-07-02; 2025-07-03,50.0");
                    writer.println("CL005,TC005,History Form 1,Early civilizations and empires,2025-07-13; 2025-07-14; 2025-07-15,50.0");
                    writer.println("CL006,TC005,Geography Form 1,World map and physical features,2025-07-16; 2025-07-17; 2025-07-18,50.0");
                    writer.println("CL016,TC001,Pendidikan Seni Form 1,Introduction to art and creativity,2025-08-15; 2025-08-16; 2025-08-17,40.0");
                    writer.println("CL017,TC002,RBT Form 1,Design basics and hands-on projects,2025-08-18; 2025-08-19; 2025-08-20,45.0");
                    
                    // Form 2 classes
                    writer.println("CL002,TC002,English Form 2,Fundamental English skills,2025-07-04; 2025-07-05; 2025-07-06,45.0");
                    writer.println("CL009,TC007,Mathematics Form 2,Numbers fractions and ratios,2025-07-25; 2025-07-26; 2025-07-27,45.0");
                    writer.println("CL010,TC007,Malay Form 2,Story reading and comprehension,2025-07-28; 2025-07-29; 2025-07-30,45.0");
                    writer.println("CL018,TC003,Pendidikan Seni Form 2,Color theory and composition,2025-08-21; 2025-08-22; 2025-08-23,40.0");
                    writer.println("CL019,TC004,RBT Form 2,Model making and design software,2025-08-24; 2025-08-25; 2025-08-26,45.0");
                    writer.println("CL020,TC005,Sejarah Form 2,Colonial history and resistance,2025-08-27; 2025-08-28; 2025-08-29,45.0");
                    writer.println("CL021,TC006,Geografi Form 2,Physical and human geography,2025-08-30; 2025-08-31; 2025-09-01,45.0");
                    writer.println("CL022,TC007,Science Form 2,Energy and the environment,2025-09-02; 2025-09-03; 2025-09-04,50.0");
                    
                    // Form 3 classes
                    writer.println("CL003,TC003,Science Form 3,Introduction to science concepts,2025-07-07; 2025-07-08; 2025-07-09,55.0");
                    writer.println("CL011,TC008,Geography Form 3,Climate zones and countries,2025-07-31; 2025-08-01; 2025-08-02,50.0");
                    writer.println("CL023,TC008,Mathematics Form 3,Algebra and linear equations,2025-09-05; 2025-09-06; 2025-09-07,50.0");
                    writer.println("CL024,TC009,English Form 3,Grammar and creative writing,2025-09-08; 2025-09-09; 2025-09-10,50.0");
                    writer.println("CL025,TC010,Bahasa Melayu Form 3,Karangan and literature,2025-09-11; 2025-09-12; 2025-09-13,50.0");
                    
                    // Form 4 classes
                    writer.println("CL004,TC004,Malay Form 4,Bahasa Melayu advanced usage,2025-07-10; 2025-07-11; 2025-07-12,40.0");
                    writer.println("CL012,TC009,English Form 4,Presentation and writing skills,2025-08-03; 2025-08-04; 2025-08-05,55.0");
                    writer.println("CL013,TC009,History Form 4,Malaysia independence and politics,2025-08-06; 2025-08-07; 2025-08-08,55.0");
                    writer.println("CL026,TC001,Physics Form 4,Motion and dynamics,2025-09-14; 2025-09-15; 2025-09-16,60.0");
                    writer.println("CL027,TC002,Chemistry Form 4,Periodic table and compounds,2025-09-17; 2025-09-18; 2025-09-19,60.0");
                    writer.println("CL028,TC003,Biology Form 4,Nutrition and respiration,2025-09-20; 2025-09-21; 2025-09-22,60.0");
                    
                    // Form 5 classes
                    writer.println("CL007,TC006,English Form 5,Exam preparation and essay writing,2025-07-19; 2025-07-20; 2025-07-21,60.0");
                    writer.println("CL008,TC006,Science Form 5,SPM-level science discussions,2025-07-22; 2025-07-23; 2025-07-24,60.0");
                    writer.println("CL014,TC010,Science Form 5,Advanced SPM problem-solving,2025-08-09; 2025-08-10; 2025-08-11,60.0");
                    writer.println("CL015,TC010,Mathematics Form 5,Trigonometry and calculus intro,2025-08-12; 2025-08-13; 2025-08-14,60.0");
                    writer.println("CL029,TC004,Physics Form 5,Electricity and magnetism,2025-09-23; 2025-09-24; 2025-09-25,60.0");
                    writer.println("CL030,TC005,Add Maths Form 5,Functions calculus and graphs,2025-09-26; 2025-09-27; 2025-09-28,65.0");
                    
                    System.out.println("Created class.txt with comprehensive course data.");
                }
            }
            
            // Create payments.txt with sample payment data
            File paymentsFile = new File(handler.getFilePath(PAYMENTS_FILE));
            if (!paymentsFile.exists()) {
                try (PrintWriter writer = new PrintWriter(new FileWriter(paymentsFile))) {
                    writer.println("PAY001,RCP001,STU001,Vince,CL007;CL008;CL015,180.00,2025-07-24 09:26:14,Cash,PAID");
                    writer.println("PAY002,RCP002,STU002,kim,CL007;CL008;CL014,180.00,2025-07-24 09:28:00,Online Banking,PAID");
                    System.out.println("Created payments.txt with sample payment records.");
                }
            }
            
            // Create payment_history.txt
            File paymentHistoryFile = new File(handler.getFilePath(PAYMENT_HISTORY_FILE));
            if (!paymentHistoryFile.exists()) {
                try (PrintWriter writer = new PrintWriter(new FileWriter(paymentHistoryFile))) {
                    writer.println("STU001,Vince,CL007;CL008;CL015,180.00,cash");
                    writer.println("STU002,kim,CL007;CL008;CL014,180.00,onlinebanking");
                    writer.println("STU003,Sebastian,CL001;CL005;CL006,150.00,cash");
                    writer.println("STU004,Max,CL004;CL012;CL013,150.00,onlinebanking");
                    System.out.println("Created payment_history.txt with sample records.");
                }
            }
            
            // Create Subject_Change_Requests.txt
            File requestsFile = new File(handler.getFilePath(SUBJECT_CHANGE_REQUESTS_FILE));
            if (!requestsFile.exists()) {
                try (PrintWriter writer = new PrintWriter(new FileWriter(requestsFile))) {
                    writer.println("REQ001,STU001,CL008,CL015,Pending");
                    writer.println("REQ002,STU001,CL014,CL007,Approved");
                    writer.println("REQ003,STU002,CL015,CL008,Pending");
                    System.out.println("Created Subject_Change_Requests.txt with sample requests.");
                }
            }
            
            System.out.println("\n" + "=".repeat(50));
            System.out.println("🎓 ATC MANAGEMENT SYSTEM INITIALIZED 🎓");
            System.out.println("=".repeat(50));
            System.out.println("✅ All required data files created successfully!");
            System.out.println();
            System.out.println("🔐 LOGIN CREDENTIALS (Case-Insensitive UserIDs):");
            System.out.println("   💡 You can type: stu001, STU001, Stu001, etc.");
            System.out.println();
            System.out.println("📋 RECEPTIONIST LOGIN:");
            System.out.println("   Username: sarah  | Password: sarah123 | Name: Sarah Johnson");
            System.out.println("   Username: john   | Password: john456  | Name: John Smith");
            System.out.println("   Username: mary   | Password: mary789  | Name: Mary Wilson");
            System.out.println();
            System.out.println("🎓 STUDENT LOGIN:");
            System.out.println("   Username: STU001 | Password: password123 | Name: Vince");
            System.out.println("   Username: STU002 | Password: password123 | Name: kim");
            System.out.println("   Username: STU003 | Password: password123 | Name: Sebastian");
            System.out.println("   Username: STU004 | Password: password123 | Name: Max");
            System.out.println();
            System.out.println("🔧 ADMIN LOGIN:");
            System.out.println("   Username: admin    | Password: admin123   | Name: Administrator");
            System.out.println("   Username: manager  | Password: manager456 | Name: System Manager");
            System.out.println();
            System.out.println("👩‍🏫 TUTOR LOGIN:");
            System.out.println("   Username: Alice   | Password: pass123  | Name: Alice");
            System.out.println("   Username: Mike    | Password: 123pass  | Name: Mike");
            System.out.println("   Username: Vincent | Password: 4321     | Name: Vincent");
            System.out.println("=".repeat(50));
            System.out.println();
            
        } catch (Exception e) {
            System.err.println("❌ Error initializing data files: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Reads all students from file
     */
    public List<Student> readStudents() {
    List<Student> students = new ArrayList<>();
    String filepath = getFilePath(STUDENTS_FILE);
    
    System.out.println("🔍 DEBUG: Reading students from: " + filepath);
    
    try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
        String line;
        int lineNumber = 0;
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            if (line.trim().isEmpty()) continue;
            
            String[] parts = line.split(",");
            // Expected format: StudentID,IC,Password,Name,Email,Phone,Address,Level,Month,Subjects
            if (parts.length >= 9) {
                try {
                    Student student = new Student(
                        parts[0].trim(), // userId (STU001)
                        parts[1].trim(), // ic (0538291933)
                        parts[0].trim(), // username (using userId)
                        parts[2].trim(), // password (password123)
                        parts[3].trim(), // name (Vince) <- THIS IS THE IMPORTANT FIX
                        parts[4].trim(), // email (vince@gmail.com)
                        parts[5].trim(), // phone (019-222-333)
                        parts[6].trim(), // address (Kuala Lumpur)
                        parts[7].trim(), // level (Form 5)
                        parts[8].trim()  // enrollmentMonth (January)
                    );
                    
                    // Handle subjects if they exist
                    if (parts.length > 9 && !parts[9].trim().isEmpty()) {
                        student.setSubjectsFromString(parts[9].trim());
                    }
                    
                    students.add(student);
                    
                    // DEBUG: Print what we're reading
                    System.out.println("🔍 DEBUG: Read student - ID: " + parts[0].trim() + 
                                     ", Name: " + parts[3].trim() + 
                                     ", IC: " + parts[1].trim());
                    
                } catch (Exception e) {
                    System.err.println("❌ Error parsing line " + lineNumber + ": " + line);
                    System.err.println("❌ Error details: " + e.getMessage());
                }
            } else {
                System.err.println("⚠️ WARNING: Invalid line " + lineNumber + " (expected 9+ fields, got " + parts.length + "): " + line);
            }
        }
    } catch (IOException e) {
        System.err.println("❌ Error reading students file: " + e.getMessage());
    }
    
    System.out.println("✅ Successfully read " + students.size() + " students");
    return students;
}
    
    /**
     * Writes all students to file
     */
    public boolean writeStudents(List<Student> students) {
        String filepath = getFilePath(STUDENTS_FILE);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            for (Student student : students) {
                writer.println(student.toFileString());
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error writing students file: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Reads all tutors from file
     */
    public List<Tutor> readTutors() {
        List<Tutor> tutors = new ArrayList<>();
        String filepath = getFilePath(TUTORS_FILE);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    Tutor tutor = new Tutor(
                        parts[0].trim(), // userId
                        parts[1].trim(), // username
                        parts[2].trim(), // password
                        parts[1].trim(), // name (using username as placeholder)
                        parts[3].trim(), // email
                        parts[5].trim(), // phone
                        parts[4].trim()  // dateOfBirth
                    );
                    tutors.add(tutor);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading tutors file: " + e.getMessage());
        }
        
        return tutors;
    }
    
    /**
     * Reads all receptionists from file
     */
    public List<Receptionist> readReceptionists() {
        List<Receptionist> receptionists = new ArrayList<>();
        String filepath = getFilePath(RECEPTIONISTS_FILE);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    Receptionist receptionist = new Receptionist(
                        parts[0].trim(), // userId
                        parts[1].trim(), // username
                        parts[2].trim(), // password
                        parts[3].trim(), // name
                        parts[4].trim(), // email
                        parts[5].trim()  // phone
                    );
                    receptionists.add(receptionist);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading receptionists file: " + e.getMessage());
        }
        
        return receptionists;
    }
    
    /**
     * Writes all receptionists to file
     */
    public boolean writeReceptionists(List<Receptionist> receptionists) {
        String filepath = getFilePath(RECEPTIONISTS_FILE);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            for (Receptionist receptionist : receptionists) {
                writer.println(String.format("%s,%s,%s,%s,%s,%s",
                    receptionist.getUserId(),
                    receptionist.getUsername(),
                    receptionist.getPassword(),
                    receptionist.getName(),
                    receptionist.getEmail(),
                    receptionist.getPhone()
                ));
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error writing receptionists file: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Reads all admins from file
     */
    public List<Admin> readAdmins() {
        List<Admin> admins = new ArrayList<>();
        String filepath = getFilePath(ADMINS_FILE);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    Admin admin = new Admin(
                        parts[0].trim(), // userId
                        parts[1].trim(), // username
                        parts[2].trim(), // password
                        parts[3].trim(), // name
                        parts[4].trim(), // email
                        parts[5].trim()  // phone
                    );
                    admins.add(admin);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading admins file: " + e.getMessage());
        }
        
        return admins;
    }
    
    /**
     * Reads all classes from file
     */
    public List<ClassInfo> readClasses() {
        List<ClassInfo> classes = new ArrayList<>();
        String filepath = getFilePath(CLASSES_FILE);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                // Split by comma, but be careful with descriptions that might contain commas
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    try {
                        String classId = parts[0].trim();
                        String tutorId = parts[1].trim();
                        String subject = parts[2].trim();
                        
                        // Handle description that might span multiple parts due to commas
                        StringBuilder description = new StringBuilder();
                        int descStart = 3;
                        int descEnd = parts.length - 2; // Last two parts are schedule and fee
                        
                        for (int i = descStart; i < descEnd; i++) {
                            if (description.length() > 0) description.append(",");
                            description.append(parts[i].trim());
                        }
                        
                        String schedule = parts[parts.length - 2].trim();
                        double fee = Double.parseDouble(parts[parts.length - 1].trim());
                        
                        ClassInfo classInfo = new ClassInfo(
                            classId,
                            tutorId,
                            subject,
                            description.toString(),
                            schedule,
                            fee
                        );
                        classes.add(classInfo);
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing fee for line: " + line);
                        continue; // Skip this line and continue with next
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading classes file: " + e.getMessage());
        }
        
        return classes;
    }
    
    /**
     * Reads payments from file with enhanced format
     */
    public List<Payment> readPayments() {
        List<Payment> payments = new ArrayList<>();
        String filepath = getFilePath(PAYMENTS_FILE);
        File file = new File(filepath);
        
        if (!file.exists()) {
            // Create empty payments file if it doesn't exist
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating payments file: " + e.getMessage());
            }
            return payments;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 7) { // Enhanced format
                    try {
                        String paymentId = parts[0].trim();
                        String receiptId = parts[1].trim();
                        String studentId = parts[2].trim();
                        String studentName = parts[3].trim();
                        String[] classIds = parts[4].split(";");
                        double amount = Double.parseDouble(parts[5].trim());
                        String paymentDate = parts[6].trim();
                        String paymentMethod = parts.length > 7 ? parts[7].trim() : "Cash";
                        
                        Payment payment = new Payment(
                            paymentId, receiptId, studentId, studentName,
                            classIds, amount, paymentDate, paymentMethod
                        );
                        
                        if (parts.length > 8) {
                            payment.setStatus(parts[8].trim());
                        }
                        
                        payments.add(payment);
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing payment amount in line: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading payments file: " + e.getMessage());
        }
        
        return payments;
    }
    
    /**
     * Writes payments to file with enhanced format
     */
    public boolean writePayments(List<Payment> payments) {
        String filepath = getFilePath(PAYMENTS_FILE);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            for (Payment payment : payments) {
                // Enhanced format: PaymentID,ReceiptID,StudentID,StudentName,ClassIDs,Amount,Date,Method,Status
                writer.println(String.format("%s,%s,%s,%s,%s,%.2f,%s,%s,%s",
                    payment.getPaymentId(),
                    payment.getReceiptId(),
                    payment.getStudentId(),
                    payment.getStudentName(),
                    payment.getClassIdsString().replace(", ", ";"),
                    payment.getAmount(),
                    payment.getPaymentDate(),
                    payment.getPaymentMethod(),
                    payment.getStatus()
                ));
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error writing payments file: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Generates next receipt ID
     */
    public String generateNextReceiptId() {
        List<Payment> payments = readPayments();
        int maxId = 0;
        
        for (Payment payment : payments) {
            String id = payment.getReceiptId();
            if (id.startsWith("RCP")) {
                try {
                    int num = Integer.parseInt(id.substring(3));
                    maxId = Math.max(maxId, num);
                } catch (NumberFormatException e) {
                    // Ignore invalid IDs
                }
            }
        }
        
        return String.format("RCP%03d", maxId + 1);
    }
    
    /**
     * Generates next student ID
     */
    public String generateNextStudentId() {
        List<Student> students = readStudents();
        int maxId = 0;
        
        for (Student student : students) {
            String id = student.getUserId();
            if (id.startsWith("STU")) {
                try {
                    int num = Integer.parseInt(id.substring(3));
                    maxId = Math.max(maxId, num);
                } catch (NumberFormatException e) {
                    // Ignore invalid IDs
                }
            }
        }
        
        return String.format("STU%03d", maxId + 1);
    }
    
    /**
     * Generates next payment ID
     */
    public String generateNextPaymentId() {
        List<Payment> payments = readPayments();
        int maxId = 0;
        
        for (Payment payment : payments) {
            String id = payment.getPaymentId();
            if (id.startsWith("PAY")) {
                try {
                    int num = Integer.parseInt(id.substring(3));
                    maxId = Math.max(maxId, num);
                } catch (NumberFormatException e) {
                    // Ignore invalid IDs
                }
            }
        }
        
        return String.format("PAY%03d", maxId + 1);
    }
    
    /**
     * Subject Change Request utilities
     */
    public static class SubjectChangeUtil {
        private static final String FILE = "Subject_Change_Requests.txt";
        
        public static boolean deletePendingRequest(String studentId, String requestId) {
            try {
                File file = new File(FILE);
                if (!file.exists()) return false;
                
                List<String> lines = new ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        lines.add(line);
                    }
                }
                
                List<String> filtered = new ArrayList<>();
                boolean found = false;
                for (String line : lines) {
                    String[] p = line.split(",");
                    if (p.length >= 5 &&
                        p[0].trim().equalsIgnoreCase(requestId) &&
                        p[1].trim().equals(studentId) &&
                        p[4].trim().equalsIgnoreCase("Pending")) {
                        found = true; // Skip this line (delete it)
                    } else {
                        filtered.add(line);
                    }
                }
                
                if (found) {
                    try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                        for (String line : filtered) {
                            writer.println(line);
                        }
                    }
                    return true;
                }
                return false;
            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        
        public static List<String> getPendingRequestsForStudent(String studentId) {
            List<String> out = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] p = line.split(",");
                    if (p.length >= 5 &&
                            p[1].trim().equals(studentId) &&
                            p[4].trim().equalsIgnoreCase("Pending")) {
                        out.add(line);
                    }
                }
            } catch (IOException ignored) {}
            return out;
        }
        
        public static void writeSubjectChangeRequest(String requestId, String studentId, 
                                                   String currentClassId, String newClassId, String status) {
            try (FileWriter fw = new FileWriter(FILE, true)) {
                fw.write(requestId + "," + studentId + "," + currentClassId + "," + newClassId + "," + status + "\n");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}