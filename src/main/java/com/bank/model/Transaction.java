package com.bank.model;

import com.bank.model.enums.TransactionStatus;
import com.bank.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a single financial movement on an account.
 */
public class Transaction {

    private final String transactionId;
    private final String fromAccountId;
    private final String toAccountId;
    private final BigDecimal amount;
    private final String currency;
    private final TransactionType type;
    private TransactionStatus status;
    private final String description;
    private final LocalDateTime timestamp;
    private final String referenceCode;

    public Transaction(String fromAccountId, String toAccountId,
                       BigDecimal amount, String currency,
                       TransactionType type, String description) {
        this.transactionId = UUID.randomUUID().toString();
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.currency = currency;
        this.type = type;
        this.status = TransactionStatus.COMPLETED;
        this.description = description;
        this.timestamp = LocalDateTime.now();
        this.referenceCode = "REF" + System.currentTimeMillis();
    }

    public String getTransactionId()    { return transactionId; }
    public String getFromAccountId()    { return fromAccountId; }
    public String getToAccountId()      { return toAccountId; }
    public BigDecimal getAmount()       { return amount; }
    public String getCurrency()         { return currency; }
    public TransactionType getType()    { return type; }
    public TransactionStatus getStatus() { return status; }
    public String getDescription()      { return description; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getReferenceCode()    { return referenceCode; }

    public void setStatus(TransactionStatus status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("Transaction{id='%s', type=%s, amount=%.2f %s, status=%s, desc='%s'}",
                transactionId, type, amount, currency, status, description);
    }
}
