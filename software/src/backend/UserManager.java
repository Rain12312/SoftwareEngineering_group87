package backend;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class UserManager {
    private static final String USER_FILE = "users.csv";
    private static final String DELIMITER = ",";
    private static String currentUser = null;  // Track current logged in user
    
    public static class User {
        private String username;
        private String passwordHash;
        
        public User(String username, String passwordHash) {
            this.username = username;
            this.passwordHash = passwordHash;
        }
        
        public String getUsername() {
            return username;
        }
        
        public String getPasswordHash() {
            return passwordHash;
        }
    }
    
    public static boolean registerUser(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return false;
        }
        
        // Check if user already exists
        if (getUser(username) != null) {
            return false;
        }
        
        try {
            String passwordHash = hashPassword(password);
            String userData = username + DELIMITER + passwordHash + "\n";
            
            // Create file if it doesn't exist
            Path userFilePath = Paths.get(USER_FILE);
            if (!Files.exists(userFilePath)) {
                Files.createFile(userFilePath);
            }
            
            // Append user data to file
            Files.write(userFilePath, userData.getBytes(), StandardOpenOption.APPEND);
            
            // Create user-specific ledger files
            createUserLedgerFiles(username);
            
            return true;
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private static void createUserLedgerFiles(String username) throws IOException {
        // Create general ledger file
        String generalLedgerFile = username + "_general.csv";
        if (!Files.exists(Paths.get(generalLedgerFile))) {
            Files.createFile(Paths.get(generalLedgerFile));
            // Write header
            Files.write(Paths.get(generalLedgerFile), "Date,Type,Category,Amount\n".getBytes());
        }
        
        // Create personal ledger files
        String[] personalLedgers = {"daily", "love"};
        for (String ledger : personalLedgers) {
            String personalLedgerFile = username + "_" + ledger + ".csv";
            if (!Files.exists(Paths.get(personalLedgerFile))) {
                Files.createFile(Paths.get(personalLedgerFile));
                // Write header
                Files.write(Paths.get(personalLedgerFile), "Date,Type,Category,Amount\n".getBytes());
            }
        }
    }
    
    public static boolean validateUser(String username, String password) {
        User user = getUser(username);
        if (user == null) {
            return false;
        }
        
        try {
            String passwordHash = hashPassword(password);
            boolean isValid = user.getPasswordHash().equals(passwordHash);
            if (isValid) {
                currentUser = username;  // Set current user when login is successful
            }
            return isValid;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static String getCurrentUser() {
        return currentUser;
    }
    
    public static void logout() {
        currentUser = null;
    }
    
    private static User getUser(String username) {
        try {
            Path userFilePath = Paths.get(USER_FILE);
            if (!Files.exists(userFilePath)) {
                return null;
            }
            
            List<String> lines = Files.readAllLines(userFilePath);
            for (String line : lines) {
                String[] parts = line.split(DELIMITER);
                if (parts.length == 2 && parts[0].equals(username)) {
                    return new User(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
} 