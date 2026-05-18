package bank.models;

import bank.base.AbstractAccount;
import java.time.LocalDateTime;

/**
 * FixedDepositAccount - demonstrates deep INHERITANCE and custom behavior.
 */
public class FixedDepositAccount extends AbstractAccount {
    private static final long serialVersionUID = 1L;

    private static final double INTEREST_RATE = 0.12; // 12% annual
    private int termMonths;
    private LocalDateTime maturityDate;
    private boolean matured;

    public FixedDepositAccount(String accountNumber, String ownerName, double depositAmount, int termMonths) {
        super(accountNumber, ownerName, depositAmount);
        this.termMonths = termMonths;
        this.maturityDate = LocalDateTime.now().plusMonths(termMonths);
        this.matured = false;
    }

    @Override
    public String getAccountType() { return "Fixed Deposit"; }

    @Override
    public double calculateInterest() {
        return getBalance() * INTEREST_RATE * termMonths / 12;
    }

    @Override
    public double getMinimumBalance() { return getBalance(); }

    @Override
    public String getAccountDetails() {
        return String.format(
            "Account No: %s\nType: %s\nOwner: %s\nDeposit: PKR %.2f\nTerm: %d months\nInterest Rate: %.0f%%\nMaturity Amount: PKR %.2f\nMaturity Date: %s\nStatus: %s",
            getAccountNumber(), getAccountType(), getOwnerName(),
            getBalance(), termMonths, INTEREST_RATE * 100,
            getBalance() + calculateInterest(),
            maturityDate.toLocalDate().toString(),
            isMatured() ? "Matured" : "Active"
        );
    }

    @Override
    public boolean withdraw(double amount) {
        if (!isMatured()) {
            throw new IllegalStateException("Fixed deposit has not matured yet. Maturity: " + maturityDate.toLocalDate());
        }
        return super.withdraw(amount);
    }

    public boolean isMatured() {
        return LocalDateTime.now().isAfter(maturityDate);
    }

    public LocalDateTime getMaturityDate() { return maturityDate; }
    public int getTermMonths() { return termMonths; }
    public double getMaturityAmount() { return getBalance() + calculateInterest(); }
}
