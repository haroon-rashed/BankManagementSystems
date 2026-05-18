package bank.models;

import bank.base.AbstractAccount;

/**
 * CurrentAccount - INHERITANCE from AbstractAccount, POLYMORPHISM via overriding.
 */
public class CurrentAccount extends AbstractAccount {
    private static final long serialVersionUID = 1L;

    private static final double INTEREST_RATE = 0.02; // 2% annual
    private static final double MIN_BALANCE = 5000.0;
    private double overdraftLimit;
    private double transactionFee;

    public CurrentAccount(String accountNumber, String ownerName, double initialBalance) {
        super(accountNumber, ownerName, initialBalance);
        this.overdraftLimit = 10000.0;
        this.transactionFee = 25.0; // per transaction fee
    }

    @Override
    public String getAccountType() { return "Current Account"; }

    @Override
    public double calculateInterest() {
        return getBalance() * INTEREST_RATE / 12;
    }

    @Override
    public double getMinimumBalance() { return MIN_BALANCE; }

    @Override
    public String getAccountDetails() {
        return String.format(
            "Account No: %s\nType: %s\nOwner: %s\nBalance: PKR %.2f\nOverdraft Limit: PKR %.2f\nTransaction Fee: PKR %.2f\nStatus: %s",
            getAccountNumber(), getAccountType(), getOwnerName(),
            getBalance(), overdraftLimit, transactionFee,
            isActive() ? "Active" : "Inactive"
        );
    }

    // FUNCTION OVERRIDING - current accounts allow overdraft
    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
        double totalAmount = amount + transactionFee;
        if (getBalance() - totalAmount < -overdraftLimit) {
            throw new IllegalStateException(String.format(
                "Exceeds overdraft limit. Available: PKR %.2f (incl. PKR %.2f fee)",
                getBalance() + overdraftLimit - transactionFee, transactionFee));
        }
        setBalance(getBalance() - totalAmount);
        return true;
    }

    public double getOverdraftLimit() { return overdraftLimit; }
    public void setOverdraftLimit(double limit) { this.overdraftLimit = limit; }
    public double getTransactionFee() { return transactionFee; }
}
