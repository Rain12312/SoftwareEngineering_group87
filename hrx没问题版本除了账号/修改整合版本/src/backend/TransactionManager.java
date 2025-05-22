package backend;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionManager {
    private static final String TRANSACTIONS_FILE = "transactions.csv";
    private static List<Transaction> transactions = new ArrayList<>();

    public static void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        saveTransactions();
    }

    public static List<Transaction> getTransactions() {
        loadTransactions();
        String currentUser = UserManager.getCurrentUser();
        List<Transaction> userTransactions = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getUsername().equals(currentUser)) {
                userTransactions.add(t);
            }
        }
        return userTransactions;
    }

    private static void saveTransactions() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(TRANSACTIONS_FILE))) {
            writer.println("Date,Type,Category,Amount,Username");
            for (Transaction t : transactions) {
                writer.println(t.getDate() + "," + t.getType() + "," + t.getCategory() + "," + t.getAmount() + "," + t.getUsername());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadTransactions() {
        transactions.clear();
        File file = new File(TRANSACTIONS_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    Transaction t = new Transaction(parts[0], parts[1], parts[2], Double.parseDouble(parts[3]));
                    transactions.add(t);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 