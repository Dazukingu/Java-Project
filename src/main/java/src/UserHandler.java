import java.io.*;
import java.util.*;

/**
 * UserHandler class manages user authentication and user data operations
 * Unified version combining both codebases with enhanced functionality
 */
public class UserHandler {
    private static final String STUDENTS_FILE = "students.txt";
    private static final String TUTORS_FILE = "tutor.txt";
    private static final String RECEPTIONISTS_FILE = "receptionist.txt";
    private static final String ADMINS_FILE = "admin.txt";
    
    // Get the directory where the class files are located
    private String getFilePath(String filename) {
        try {
            String classPath = System.getProperty("user.dir");
            return classPath + File.separator + filename;
        } catch (Exception e) {
            return filename; // fallback to current directory
        }
    }
    
    private final Map<String, Integer> loginAttempts = new HashMap<>();
    private final Set<String> lockedAccounts = new HashSet<>();
    
    /**
     * Authenticates user login credentials (supports all user types)
     * @param username The username to authenticate (UserID for students, username for others)
     * @param password The password to authenticate (case-sensitive)
     * @return LoginResult object containing success status, user role, and user data
     */
    public LoginResult authenticate(String username, String password) {
        // Check if account is locked
        if (lockedAccounts.contains(username)) {
            return new LoginResult(false, null, null, "Account locked. Contact administrator.");
        }
        
        // Check current attempts
        int attempts = loginAttempts.getOrDefault(username, 0);
        if (attempts >= 3) {
            lockedAccounts.add(username);
            return new LoginResult(false, null, null, "Maximum login attempts exceeded. Account locked.");
        }
        
        System.out.println("DEBUG: Authenticating user: " + username);
        
        // Try to authenticate in each user type
        User user = null;
        String userType = null;
        
        // Determine user type from UserID prefix (case-insensitive) or try all
        String upperUsername = username.toUpperCase();
        
        if (upperUsername.startsWith("AD")) {
            // Check Admin first
            user = findUserInFile(getFilePath(ADMINS_FILE), username, password, "ADMIN");
            if (user != null) {
                userType = "ADMIN";
                System.out.println("DEBUG: Admin login successful");
            }
        } else if (upperUsername.startsWith("RC")) {
            // Check Receptionist
            user = findUserInFile(getFilePath(RECEPTIONISTS_FILE), username, password, "RECEPTIONIST");
            if (user != null) {
                userType = "RECEPTIONIST";
                System.out.println("DEBUG: Receptionist login successful");
            }
        } else if (upperUsername.startsWith("TC")) {
            // Check Tutor
            user = findUserInFile(getFilePath(TUTORS_FILE), username, password, "TUTOR");
            if (user != null) {
                userType = "TUTOR";
                System.out.println("DEBUG: Tutor login successful");
            }
        } else if (upperUsername.startsWith("STU")) {
            // Check Student
            user = findUserInFile(getFilePath(STUDENTS_FILE), username, password, "STUDENT");
            if (user != null) {
                userType = "STUDENT";
                System.out.println("DEBUG: Student login successful");
            }
        } else {
            // If no prefix matches, try all files (fallback for legacy usernames)
            System.out.println("DEBUG: No prefix match, trying all user types...");
            
            // Try Admin
            user = findUserInFile(getFilePath(ADMINS_FILE), username, password, "ADMIN");
            if (user != null) {
                userType = "ADMIN";
            }
            
            // Try Receptionist
            if (user == null) {
                user = findUserInFile(getFilePath(RECEPTIONISTS_FILE), username, password, "RECEPTIONIST");
                if (user != null) {
                    userType = "RECEPTIONIST";
                }
            }
            
            // Try Tutor
            if (user == null) {
                user = findUserInFile(getFilePath(TUTORS_FILE), username, password, "TUTOR");
                if (user != null) {
                    userType = "TUTOR";
                }
            }
            
            // Try Student
            if (user == null) {
                user = findUserInFile(getFilePath(STUDENTS_FILE), username, password, "STUDENT");
                if (user != null) {
                    userType = "STUDENT";
                }
            }
        }
        
        if (user != null && userType != null) {
            // Reset attempts on successful login
            loginAttempts.remove(username);
            System.out.println("DEBUG: Authentication successful - UserType: " + userType + ", UserID: " + user.getUserId());
            return new LoginResult(true, userType, user, "Login successful");
        } else {
            // Increment attempts on failed login
            loginAttempts.put(username, attempts + 1);
            int remainingAttempts = 3 - (attempts + 1);
            System.out.println("DEBUG: Authentication failed for: " + username);
            return new LoginResult(false, null, null, 
                remainingAttempts > 0 
                    ? "Invalid credentials. " + remainingAttempts + " attempts remaining."
                    : "Invalid credentials. Account will be locked.");
        }
    }
    
    /**
     * Finds user in specific file with username and password
     * @param filepath The full file path to search in
 * @param username The UserID to find (changed from username to UserID)
 * @param password The password to verify
 * @param userType The type of user being searched for
 * @return User object if found, null otherwise
 */
private User findUserInFile(String filepath, String username, String password, String userType) {
    File file = new File(filepath);
    if (!file.exists()) {
        System.err.println("File not found: " + filepath);
        return null;
    }
    
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) continue; // Skip empty lines
            
            String[] parts = line.split(",");
            if (parts.length >= 3) {
                String fileUserId = parts[0].trim();
                String filePassword;
                
                // Get password from correct position based on file format
                switch (userType) {
                    case "STUDENT":
                        // Students: UserID,IC/Passport,Password,Name,Email,Phone,Address,Form/Level,Intake,Classes
                        filePassword = parts.length > 2 ? parts[2].trim() : "";
                        break;
                    case "TUTOR":
                        // Tutors: UserID,Name,Password,Email,Birthdate,Phone
                        filePassword = parts.length > 2 ? parts[2].trim() : "";
                        break;
                    case "RECEPTIONIST":
                        // Receptionists: UserID,Username,Password,FullName,Email,Phone
                        filePassword = parts.length > 2 ? parts[2].trim() : "";
                        break;
                    case "ADMIN":
                        // Admins: UserID,Username,Password,Role,Email,Phone
                        filePassword = parts.length > 2 ? parts[2].trim() : "";
                        break;
                    default:
                        filePassword = parts.length > 2 ? parts[2].trim() : "";
                }
                
                // MAIN CHANGE: Use UserID (first column) for ALL user types instead of username
                boolean loginMatch = fileUserId.equalsIgnoreCase(username) && filePassword.equals(password);
                
                if (loginMatch) {
                    System.out.println("DEBUG: Login successful for UserID " + username + 
                                     " (matched with " + fileUserId + ")");
                    return createUserFromData(parts, userType);
                }
            }
        }
    } catch (IOException e) {
        System.err.println("Error reading file: " + filepath + " - " + e.getMessage());
    }
    return null;
}
    
    /**
     * Creates User object from file data
     * @param parts The data parts from file
     * @param userType The type of user to create
     * @return User object
     */
    private User createUserFromData(String[] parts, String userType) {
        String userId = parts[0].trim();
        String username = parts[1].trim();
        String password = parts[2].trim();
        
        switch (userType) {
            case "STUDENT":
                // STU001,0538291933,password123,Vince,vince@gmail.com,019-222-333,Kuala Lumpur,Form 5,January,CL007;CL008;CL015
                String ic = parts[1].trim();
                String name = parts.length > 3 ? parts[3].trim() : "";
                String email = parts.length > 4 ? parts[4].trim() : "";
                String phone = parts.length > 5 ? parts[5].trim() : "";
                String address = parts.length > 6 ? parts[6].trim() : "";
                String level = parts.length > 7 ? parts[7].trim() : "";
                String enrollmentMonth = parts.length > 8 ? parts[8].trim() : "";
                
                List<String> subjects = new ArrayList<>();
                if (parts.length > 9 && !parts[9].trim().isEmpty()) {
                    subjects = Arrays.asList(parts[9].split(";"));
                }
                
                return new Student(userId, ic, userId, password, name, email, phone,
                                 address, level, enrollmentMonth, subjects, 0.0);
                
            case "TUTOR":
                // TC001,Alice,pass123,alice@gmail.com,06-06-1998,0123552843
                String tutorName = username; // Use username as name
                String tutorEmail = parts.length > 3 ? parts[3].trim() : "";
                String dob = parts.length > 4 ? parts[4].trim() : "";
                String tutorPhone = parts.length > 5 ? parts[5].trim() : "";
                
                return new Tutor(userId, username, password, tutorName, tutorEmail, tutorPhone, dob);
                
            case "RECEPTIONIST":
                // RC001,sarah,sarah123,Sarah Johnson,sarah@atc.edu.my,0123456789
                String receptName = parts.length > 3 ? parts[3].trim() : "";
                String receptEmail = parts.length > 4 ? parts[4].trim() : "";
                String receptPhone = parts.length > 5 ? parts[5].trim() : "";
                
                return new Receptionist(userId, username, password, receptName, receptEmail, receptPhone);
                
            case "ADMIN":
                // AD001,admin,admin123,Administrator,admin@atc.edu.my,0123456790
                String adminName = parts.length > 3 ? parts[3].trim() : "";
                String adminEmail = parts.length > 4 ? parts[4].trim() : "";
                String adminPhone = parts.length > 5 ? parts[5].trim() : "";
                
                return new Admin(userId, username, password, adminName, adminEmail, adminPhone);
                
            default:
                return null;
        }
    }
    
    /**
     * Resets login attempts for a user
     * @param username The username to reset attempts for
     */
    public void resetLoginAttempts(String username) {
        loginAttempts.remove(username);
        lockedAccounts.remove(username);
    }

    public void recordLoginTime(String userId, String userType) {
    try {
        String timestamp = java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );
        
        try (java.io.FileWriter fw = new java.io.FileWriter("login_history.txt", true)) {
            fw.write(userId + "," + userType + "," + timestamp + "\n");
        }
    } catch (java.io.IOException e) {
        System.err.println("Error recording login time: " + e.getMessage());
    }
}

    
    
    /**
     * Gets remaining login attempts for a user
     * @param username The username to check
     * @return Number of remaining attempts
     */
    public int getRemainingAttempts(String username) {
        return Math.max(0, 3 - loginAttempts.getOrDefault(username, 0));
    }
    
    /**
     * Checks if an account is locked
     * @param username The username to check
     * @return true if account is locked
     */
    public boolean isAccountLocked(String username) {
        return lockedAccounts.contains(username);
    }
    
    /**
     * Subject Change Request utilities - Enhanced version
     */
    public static final class SubjectChangeUtil {
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
        
        public static List<String> getAllRequests() {
            List<String> out = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
                String line;
                while ((line = br.readLine()) != null) {
                    out.add(line);
                }
            } catch (IOException ignored) {}
            return out;
        }
        
        public static boolean approveRequest(String requestId) {
            return updateRequestStatus(requestId, "Approved");
        }
        
        public static boolean rejectRequest(String requestId) {
            return updateRequestStatus(requestId, "Rejected");
        }
        
        private static boolean updateRequestStatus(String requestId, String newStatus) {
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
                
                boolean found = false;
                for (int i = 0; i < lines.size(); i++) {
                    String[] p = lines.get(i).split(",");
                    if (p.length >= 5 && p[0].trim().equals(requestId)) {
                        p[4] = newStatus;
                        lines.set(i, String.join(",", p));
                        found = true;
                        break;
                    }
                }
                
                if (found) {
                    try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                        for (String line : lines) {
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
        
        public static void writeSubjectChangeRequest(String requestId, String studentId, 
                                                   String currentClassId, String newClassId, String status) {
            try (FileWriter fw = new FileWriter(FILE, true)) {
                fw.write(requestId + "," + studentId + "," + currentClassId + "," + newClassId + "," + status + "\n");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * Password management utilities
     */
    public boolean changeUserPassword(String userId, String userType, String newPassword) {
        try {
            String filename;
            switch (userType.toUpperCase()) {
                case "STUDENT":
                    filename = STUDENTS_FILE;
                    break;
                case "RECEPTIONIST":
                    filename = RECEPTIONISTS_FILE;
                    break;
                case "TUTOR":
                    filename = TUTORS_FILE;
                    break;
                case "ADMIN":
                    filename = ADMINS_FILE;
                    break;
                default:
                    return false;
            }
            
            File file = new File(getFilePath(filename));
            if (!file.exists()) return false;
            
            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }
            
            boolean found = false;
            for (int i = 0; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if (parts.length >= 3 && parts[0].trim().equals(userId)) {
                    parts[2] = newPassword; // Update password
                    lines.set(i, String.join(",", parts));
                    found = true;
                    break;
                }
            }
            
            if (found) {
                try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                    for (String line : lines) {
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
    
    /**
     * Account management utilities
     */
    public boolean unlockAccount(String username) {
        lockedAccounts.remove(username);
        loginAttempts.remove(username);
        return true;
    }
    
    public Set<String> getLockedAccounts() {
        return new HashSet<>(lockedAccounts);
    }
    
    public Map<String, Integer> getLoginAttempts() {
        return new HashMap<>(loginAttempts);
    }
}

/**
 * LoginResult class to encapsulate login attempt results - Enhanced version
 */
class LoginResult {
    private final boolean success;
    private final String userType;
    private final User user;
    private final String message;
    
    public LoginResult(boolean success, String userType, User user, String message) {
        this.success = success;
        this.userType = userType;
        this.user = user;
        this.message = message;
    }
    
    // Getters
    public boolean isSuccess() { return success; }
    public String getUserType() { return userType; }
    public User getUser() { return user; }
    public String getMessage() { return message; }
    public String getLastLoginTime(String userId) {
    String lastLogin = "First time login";
    try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("login_history.txt"))) {
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 3 && parts[0].equals(userId)) {
                lastLogin = parts[2]; // Keep updating to get the latest
            }
        }
    } catch (java.io.IOException e) {
        // File doesn't exist or error reading - return default
    }
    return lastLogin;
}
    
    @Override
    public String toString() {
        return "LoginResult{" +
                "success=" + success +
                ", userType='" + userType + '\'' +
                ", user=" + (user != null ? user.getUserId() : "null") +
                ", message='" + message + '\'' +
                '}';
    }
}