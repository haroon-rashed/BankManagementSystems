package bank.base;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Abstract base class demonstrating ABSTRACTION and ENCAPSULATION.
 * All accounts must implement these abstract methods.
 */
public abstract class AbstractAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    // Encapsulated private fields
    private String accountNumber;
    private String ownerName;
    private double balance;
    private LocalDateTime createdAt;
    private boolean active;

    // Constructor
    public AbstractAccount(String accountNumber, String ownerName, double initialBalance) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = initialBalance;
        this.createdAt = LocalDateTime.now();
        this.active = true;
    }

    // Abstract methods - ABSTRACTION (subclasses must implement)
    public abstract String getAccountType();
    public abstract double calculateInterest();
    public abstract double getMinimumBalance();
    public abstract String getAccountDetails();

    // Concrete methods shared by all accounts
    public boolean deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit amount must be positive.");
        this.balance += amount;
        return true;
    }

    public boolean withdraw(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Withdrawal amount must be positive.");
        if (balance - amount < getMinimumBalance()) {
            throw new IllegalStateException("Insufficient balance. Minimum balance: " + getMinimumBalance());
        }
        this.balance -= amount;
        return true;
    }

    // Getters and Setters - ENCAPSULATION
    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) {
        if (ownerName == null || ownerName.trim().isEmpty())
            throw new IllegalArgumentException("Owner name cannot be empty.");
        this.ownerName = ownerName;
    }
    public double getBalance() { return balance; }
    protected void setBalance(double balance) { this.balance = balance; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return String.format("[%s] %s | Owner: %s | Balance: PKR %.2f | Active: %s",
                getAccountType(), accountNumber, ownerName, balance, active ? "Yes" : "No");
    }
}
