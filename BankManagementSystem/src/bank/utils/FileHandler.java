package bank.utils;

import bank.base.AbstractAccount;
import bank.models.Transaction;
import bank.models.User;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * FileHandler - handles all file I/O operations.
 * Implements FILE HANDLING for persistent storage.
 */
public class FileHandler {

    private static final String DATA_DIR = "data/";
    private static final String ACCOUNTS_FILE = DATA_DIR + "accounts.dat";
    private static final String USERS_FILE = DATA_DIR + "users.dat";
    private static final String TRANSACTIONS_FILE = DATA_DIR + "transactions.dat";
    private static final String LOG_FILE = DATA_DIR + "system.log";

    static {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.err.println("Could not create data directory: " + e.getMessage());
        }
    }

    // ==================== ACCOUNT PERSISTENCE ====================

    @SuppressWarnings("unchecked")
    public static Map<String, AbstractAccount> loadAccounts() {
        return (Map<String, AbstractAccount>) loadObject(ACCOUNTS_FILE, new HashMap<>());
    }

    public static void saveAccounts(Map<String, AbstractAccount> accounts) {
        saveObject(ACCOUNTS_FILE, accounts);
    }

    // ==================== USER PERSISTENCE ====================

    @SuppressWarnings("unchecked")
    public static Map<String, User> loadUsers() {
        return (Map<String, User>) loadObject(USERS_FILE, new HashMap<>());
    }

    public static void saveUsers(Map<String, User> users) {
        saveObject(USERS_FILE, users);
    }

    // ==================== TRANSACTION PERSISTENCE ====================

    @SuppressWarnings("unchecked")
    public static List<Transaction> loadTransactions() {
        return (List<Transaction>) loadObject(TRANSACTIONS_FILE, new ArrayList<>());
    }

    public static void saveTransactions(List<Transaction> transactions) {
        saveObject(TRANSACTIONS_FILE, transactions);
    }

    // ==================== GENERIC OBJECT IO ====================

    private static Object loadObject(String filePath, Object defaultValue) {
        File file = new File(filePath);
        if (!file.exists()) return defaultValue;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return ois.readObject();
        } catch (Exception e) {
            logError("Failed to load " + filePath + ": " + e.getMessage());
            return defaultValue;
        }
    }

    private static void saveObject(String filePath, Object obj) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(obj);
        } catch (IOException e) {
            logError("Failed to save " + filePath + ": " + e.getMessage());
        }
    }

    // ==================== LOGGING ====================

    public static void log(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String entry = "[" + timestamp + "] INFO: " + message + "\n";
        appendToFile(LOG_FILE, entry);
    }

    public static void logError(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String entry = "[" + timestamp + "] ERROR: " + message + "\n";
        appendToFile(LOG_FILE, entry);
        System.err.println(entry);
    }

    private static void appendToFile(String filePath, String content) {
        try (FileWriter fw = new FileWriter(filePath, true)) {
            fw.write(content);
        } catch (IOException ignored) {}
    }

    // ==================== EXPORT TO TEXT ====================

    public static void exportAccountsReport(Map<String, AbstractAccount> accounts, String filename) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("==================== NOVA BANK - ACCOUNTS REPORT ====================\n");
        sb.append("Generated: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))).append("\n\n");
        for (AbstractAccount acc : accounts.values()) {
            sb.append(acc.getAccountDetails()).append("\n").append("-".repeat(60)).append("\n");
        }
        Files.write(Paths.get(filename), sb.toString().getBytes());
    }

    public static void exportTransactionHistory(List<Transaction> transactions, String accountNo, String filename) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("==================== NOVA BANK - TRANSACTION HISTORY ====================\n");
        sb.append("Account: ").append(accountNo).append("\n");
        sb.append("Generated: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))).append("\n\n");
        sb.append(String.format("%-20s %-15s %-3s %-15s %s\n", "DATE/TIME", "TYPE", "CR/DR", "AMOUNT", "DESCRIPTION"));
        sb.append("-".repeat(80)).append("\n");
        for (Transaction t : transactions) {
            if (accountNo.equals("ALL") || t.getAccountNumber().equals(accountNo)) {
                sb.append(t.getFormattedEntry()).append("\n");
            }
        }
        Files.write(Paths.get(filename), sb.toString().getBytes());
    }
}
