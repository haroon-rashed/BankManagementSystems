package bank.utils;

import bank.base.AbstractAccount;
import bank.models.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * BankService - core business logic.
 * Demonstrates FUNCTION OVERLOADING (polymorphism) in deposit/withdraw/search methods.
 */
public class BankService {

    private Map<String, AbstractAccount> accounts;
    private Map<String, User> users;
    private List<Transaction> transactions;
    private AtomicLong txCounter;
    private AtomicLong accCounter;

    private static BankService instance;

    private BankService() {
        accounts = FileHandler.loadAccounts();
        users = FileHandler.loadUsers();
        transactions = FileHandler.loadTransactions();
        txCounter = new AtomicLong(transactions.size());
        accCounter = new AtomicLong(accounts.size());
        ensureAdminExists();
    }

    public static BankService getInstance() {
        if (instance == null) instance = new BankService();
        return instance;
    }

    private void ensureAdminExists() {
        if (!users.containsKey("admin")) {
            User admin = new User("admin",
                    PasswordUtil.hash("Admin123"),
                    "System Administrator", "admin@novabank.pk", "0300-0000000", "ADMIN");
            users.put("admin", admin);
            saveAll();
            FileHandler.log("Default admin account created.");
        }
    }

    // ==================== AUTH ====================

    public User login(String username, String password) {
        User user = users.get(username.toLowerCase());
        if (user == null) throw new IllegalArgumentException("User not found: " + username);
        if (!user.isActive()) throw new IllegalStateException("Account is deactivated.");
        if (!PasswordUtil.verify(password, user.getPasswordHash()))
            throw new IllegalArgumentException("Incorrect password.");
        user.setLastLogin(LocalDateTime.now());
        saveAll();
        FileHandler.log("User logged in: " + username);
        return user;
    }

    public User signup(String username, String password, String fullName, String email, String phone) {
        if (users.containsKey(username.toLowerCase()))
            throw new IllegalArgumentException("Username already taken.");
        if (!PasswordUtil.isStrongPassword(password))
            throw new IllegalArgumentException("Password must be 6+ chars with uppercase and digit.");
        if (!email.contains("@"))
            throw new IllegalArgumentException("Invalid email address.");
        User user = new User(username.toLowerCase(), PasswordUtil.hash(password), fullName, email, phone, "CUSTOMER");
        users.put(username.toLowerCase(), user);
        saveAll();
        FileHandler.log("New user registered: " + username);
        return user;
    }

    // ==================== ACCOUNT CREATION (OVERLOADED - FUNCTION OVERLOADING) ====================

    /** Create Savings Account */
    public SavingsAccount createAccount(String ownerName, double initialDeposit, String username) {
        String accNo = generateAccountNumber("SAV");
        SavingsAccount acc = new SavingsAccount(accNo, ownerName, initialDeposit);
        accounts.put(accNo, acc);
        linkAccountToUser(username, accNo);
        logTransaction(accNo, initialDeposit, Transaction.Type.DEPOSIT, "Initial deposit");
        saveAll();
        FileHandler.log("Savings account created: " + accNo + " for " + ownerName);
        return acc;
    }

    /** Create Current Account - OVERLOADED */
    public CurrentAccount createAccount(String ownerName, double initialDeposit, double overdraftLimit, String username) {
        String accNo = generateAccountNumber("CUR");
        CurrentAccount acc = new CurrentAccount(accNo, ownerName, initialDeposit);
        acc.setOverdraftLimit(overdraftLimit);
        accounts.put(accNo, acc);
        linkAccountToUser(username, accNo);
        logTransaction(accNo, initialDeposit, Transaction.Type.DEPOSIT, "Initial deposit");
        saveAll();
        FileHandler.log("Current account created: " + accNo + " for " + ownerName);
        return acc;
    }

    /** Create Fixed Deposit - OVERLOADED */
    public FixedDepositAccount createAccount(String ownerName, double depositAmount, int termMonths, boolean isFixed, String username) {
        String accNo = generateAccountNumber("FD");
        FixedDepositAccount acc = new FixedDepositAccount(accNo, ownerName, depositAmount, termMonths);
        accounts.put(accNo, acc);
        linkAccountToUser(username, accNo);
        logTransaction(accNo, depositAmount, Transaction.Type.DEPOSIT, "Fixed deposit opened - " + termMonths + " months");
        saveAll();
        FileHandler.log("Fixed deposit created: " + accNo + " for " + ownerName);
        return acc;
    }

    private void linkAccountToUser(String username, String accNo) {
        User user = users.get(username.toLowerCase());
        if (user != null && user.getLinkedAccountNumber() == null) {
            user.setLinkedAccountNumber(accNo);
        }
    }

    // ==================== OPERATIONS ====================

    public void deposit(String accountNumber, double amount) {
        AbstractAccount acc = getAccount(accountNumber);
        acc.deposit(amount);
        logTransaction(accountNumber, amount, Transaction.Type.DEPOSIT, "Cash deposit");
        saveAll();
        FileHandler.log("Deposit: " + accountNumber + " PKR " + amount);
    }

    /** OVERLOADED - deposit with description */
    public void deposit(String accountNumber, double amount, String description) {
        AbstractAccount acc = getAccount(accountNumber);
        acc.deposit(amount);
        logTransaction(accountNumber, amount, Transaction.Type.DEPOSIT, description);
        saveAll();
        FileHandler.log("Deposit: " + accountNumber + " PKR " + amount + " - " + description);
    }

    public void withdraw(String accountNumber, double amount) {
        AbstractAccount acc = getAccount(accountNumber);
        acc.withdraw(amount);
        logTransaction(accountNumber, amount, Transaction.Type.WITHDRAWAL, "Cash withdrawal");
        saveAll();
        FileHandler.log("Withdrawal: " + accountNumber + " PKR " + amount);
    }

    /** OVERLOADED - withdraw with description */
    public void withdraw(String accountNumber, double amount, String description) {
        AbstractAccount acc = getAccount(accountNumber);
        acc.withdraw(amount);
        logTransaction(accountNumber, amount, Transaction.Type.WITHDRAWAL, description);
        saveAll();
    }

    public void transfer(String fromAccount, String toAccount, double amount) {
        AbstractAccount from = getAccount(fromAccount);
        AbstractAccount to = getAccount(toAccount);
        from.withdraw(amount);
        to.deposit(amount);
        String txId = generateTxId();
        transactions.add(new Transaction(txId, fromAccount, amount, Transaction.Type.TRANSFER,
                "Transfer to " + toAccount, toAccount));
        transactions.add(new Transaction(generateTxId(), toAccount, amount, Transaction.Type.DEPOSIT,
                "Transfer from " + fromAccount, fromAccount));
        saveAll();
        FileHandler.log("Transfer: " + fromAccount + " -> " + toAccount + " PKR " + amount);
    }

    // ==================== SEARCH (OVERLOADED) ====================

    /** Search by account number */
    public AbstractAccount searchAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    /** Search by owner name - OVERLOADED */
    public List<AbstractAccount> searchAccount(String ownerName, boolean byName) {
        if (!byName) return Collections.emptyList();
        return accounts.values().stream()
                .filter(a -> a.getOwnerName().toLowerCase().contains(ownerName.toLowerCase()))
                .collect(Collectors.toList());
    }

    /** Search transactions by account */
    public List<Transaction> getTransactions(String accountNumber) {
        return transactions.stream()
                .filter(t -> t.getAccountNumber().equals(accountNumber))
                .sorted(Comparator.comparing(bank.base.AbstractTransaction::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    // ==================== UPDATE & DELETE ====================

    public void updateAccountOwner(String accountNumber, String newName) {
        getAccount(accountNumber).setOwnerName(newName);
        saveAll();
        FileHandler.log("Account updated: " + accountNumber);
    }

    public void updateUserInfo(String username, String fullName, String email, String phone) {
        User user = users.get(username.toLowerCase());
        if (user == null) throw new IllegalArgumentException("User not found.");
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        saveAll();
    }

    public void changePassword(String username, String oldPass, String newPass) {
        User user = users.get(username.toLowerCase());
        if (user == null) throw new IllegalArgumentException("User not found.");
        if (!PasswordUtil.verify(oldPass, user.getPasswordHash()))
            throw new IllegalArgumentException("Current password is incorrect.");
        if (!PasswordUtil.isStrongPassword(newPass))
            throw new IllegalArgumentException("New password too weak.");
        user.setPasswordHash(PasswordUtil.hash(newPass));
        saveAll();
    }

    public void deleteAccount(String accountNumber) {
        AbstractAccount acc = getAccount(accountNumber);
        if (acc.getBalance() > 0)
            throw new IllegalStateException("Cannot delete account with balance. Please withdraw PKR " + acc.getBalance() + " first.");
        acc.setActive(false);
        accounts.remove(accountNumber);
        saveAll();
        FileHandler.log("Account deleted: " + accountNumber);
    }

    // ==================== GETTERS ====================

    public AbstractAccount getAccount(String accountNumber) {
        AbstractAccount acc = accounts.get(accountNumber);
        if (acc == null) throw new IllegalArgumentException("Account not found: " + accountNumber);
        if (!acc.isActive()) throw new IllegalStateException("Account is inactive.");
        return acc;
    }

    public Map<String, AbstractAccount> getAllAccounts() { return Collections.unmodifiableMap(accounts); }
    public Map<String, User> getAllUsers() { return Collections.unmodifiableMap(users); }
    public List<Transaction> getAllTransactions() { return Collections.unmodifiableList(transactions); }

    public double getTotalDeposits() {
        return accounts.values().stream().mapToDouble(AbstractAccount::getBalance).sum();
    }

    // ==================== HELPERS ====================

    private String generateAccountNumber(String prefix) {
        return prefix + String.format("%07d", accCounter.incrementAndGet());
    }

    private String generateTxId() {
        return "TX" + String.format("%010d", txCounter.incrementAndGet());
    }

    private void logTransaction(String accNo, double amount, Transaction.Type type, String desc) {
        transactions.add(new Transaction(generateTxId(), accNo, amount, type, desc));
    }

    private void saveAll() {
        FileHandler.saveAccounts(accounts);
        FileHandler.saveUsers(users);
        FileHandler.saveTransactions(transactions);
    }
}
