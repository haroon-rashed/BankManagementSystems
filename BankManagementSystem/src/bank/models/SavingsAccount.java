package bank.models;

import bank.base.AbstractAccount;

/**
 * SavingsAccount - demonstrates INHERITANCE from AbstractAccount.
 * Overrides abstract methods - FUNCTION OVERRIDING.
 */
public class SavingsAccount extends AbstractAccount {
    private static final long serialVersionUID = 1L;

    private static final double INTEREST_RATE = 0.06; // 6% annual
    private static final double MIN_BALANCE = 1000.0;
    private int withdrawalCount;
    private static final int MAX_WITHDRAWALS_PER_MONTH = 6;

    public SavingsAccount(String accountNumber, String ownerName, double initialBalance) {
        super(accountNumber, ownerName, initialBalance);
        this.withdrawalCount = 0;
    }

    // FUNCTION OVERRIDING - implementing abstract methods
    @Override
    public String getAccountType() {
        return "Savings Account";
    }

    @Override
    public double calculateInterest() {
        return getBalance() * INTEREST_RATE / 12; // monthly interest
    }

    @Override
    public double getMinimumBalance() {
        return MIN_BALANCE;
    }

    @Override
    public String getAccountDetails() {
        return String.format(
            "Account No: %s\nType: %s\nOwner: %s\nBalance: PKR %.2f\nInterest Rate: %.0f%%\nMonthly Interest: PKR %.2f\nWithdrawals This Month: %d/%d\nStatus: %s",
            getAccountNumber(), getAccountType(), getOwnerName(),
            getBalance(), INTEREST_RATE * 100, calculateInterest(),
            withdrawalCount, MAX_WITHDRAWALS_PER_MONTH,
            isActive() ? "Active" : "Inactive"
        );
    }

    // FUNCTION OVERRIDING - override withdraw with extra check
    @Override
    public boolean withdraw(double amount) {
        if (withdrawalCount >= MAX_WITHDRAWALS_PER_MONTH) {
            throw new IllegalStateException("Monthly withdrawal limit reached (" + MAX_WITHDRAWALS_PER_MONTH + " withdrawals).");
        }
        boolean result = super.withdraw(amount);
        if (result) withdrawalCount++;
        return result;
    }

    public void resetMonthlyWithdrawals() { this.withdrawalCount = 0; }
    public int getWithdrawalCount() { return withdrawalCount; }
    public double getInterestRate() { return INTEREST_RATE; }
}
