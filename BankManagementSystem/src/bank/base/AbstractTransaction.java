package bank.base;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Abstract Transaction - demonstrates ABSTRACTION with transaction types.
 */
public abstract class AbstractTransaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private String transactionId;
    private String accountNumber;
    private double amount;
    private LocalDateTime timestamp;
    private String description;

    public AbstractTransaction(String transactionId, String accountNumber, double amount, String description) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.description = description;
    }

    // Abstract methods
    public abstract String getTransactionType();
    public abstract String getFormattedEntry();

    // Getters
    public String getTransactionId() { return transactionId; }
    public String getAccountNumber() { return accountNumber; }
    public double getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getDescription() { return description; }
    public String getFormattedTime() {
        return timestamp.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }
}
