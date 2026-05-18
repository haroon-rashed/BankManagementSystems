package bank.models;

import bank.base.AbstractTransaction;

/**
 * Transaction - extends AbstractTransaction, demonstrates POLYMORPHISM.
 */
public class Transaction extends AbstractTransaction {
    private static final long serialVersionUID = 1L;

    public enum Type { DEPOSIT, WITHDRAWAL, TRANSFER, INTEREST, FEE }

    private Type type;
    private String relatedAccount; // for transfers

    public Transaction(String txId, String accountNumber, double amount, Type type, String description) {
        super(txId, accountNumber, amount, description);
        this.type = type;
    }

    public Transaction(String txId, String accountNumber, double amount, Type type, String description, String relatedAccount) {
        this(txId, accountNumber, amount, type, description);
        this.relatedAccount = relatedAccount;
    }

    @Override
    public String getTransactionType() { return type.name(); }

    @Override
    public String getFormattedEntry() {
        String sign = (type == Type.DEPOSIT || type == Type.INTEREST) ? "+" : "-";
        return String.format("%-20s %-15s %s PKR %-12.2f %s",
                getFormattedTime(), getTransactionType(), sign, getAmount(), getDescription());
    }

    public Type getType() { return type; }
    public String getRelatedAccount() { return relatedAccount; }
}
