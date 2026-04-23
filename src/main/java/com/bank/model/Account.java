package com.bank.model;

import com.bank.model.enums.AccountStatus;
import com.bank.model.enums.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Represents a bank account belonging to a customer.
 */
public class Account {

    private static final AtomicLong SEQUENCE = new AtomicLong(100000);

    private final String accountId;
    private final String accountNumber;
    private final String customerId;
    private AccountType accountType;
    private String currency;
    private BigDecimal balance;
    private BigDecimal availableBalance;
    private AccountStatus status;
    private final LocalDateTime openedAt;
    private BigDecimal interestRate;

    public Account(String customerId, AccountType accountType, String currency) {
        this.accountId = UUID.randomUUID().toString();
        this.accountNumber = "BNK" + SEQUENCE.getAndIncrement();
        this.customerId = customerId;
        this.accountType = accountType;
        this.currency = currency;
        this.balance = BigDecimal.ZERO;
        this.availableBalance = BigDecimal.ZERO;
        this.status = AccountStatus.ACTIVE;
        this.openedAt = LocalDateTime.now();
        this.interestRate = accountType == AccountType.SAVINGS ? new BigDecimal("2.50") : BigDecimal.ZERO;
    }

    public String getAccountId()         { return accountId; }
    public String getAccountNumber()     { return accountNumber; }
    public String getCustomerId()        { return customerId; }
    public AccountType getAccountType()  { return accountType; }
    public String getCurrency()          { return currency; }
    public BigDecimal getBalance()       { return balance; }
    public BigDecimal getAvailableBalance() { return availableBalance; }
    public AccountStatus getStatus()     { return status; }
    public LocalDateTime getOpenedAt()   { return openedAt; }
    public BigDecimal getInterestRate()  { return interestRate; }

    public void setAccountType(AccountType t)       { this.accountType = t; }
    public void setCurrency(String currency)        { this.currency = currency; }
    public void setBalance(BigDecimal balance)      { this.balance = balance; this.availableBalance = balance; }
    public void setAvailableBalance(BigDecimal b)   { this.availableBalance = b; }
    public void setStatus(AccountStatus status)     { this.status = status; }
    public void setInterestRate(BigDecimal rate)    { this.interestRate = rate; }

    public void credit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
        this.availableBalance = this.availableBalance.add(amount);
    }

    public void debit(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
        this.availableBalance = this.availableBalance.subtract(amount);
    }

    @Override
    public String toString() {
        return String.format("Account{number='%s', type=%s, balance=%.2f %s, status=%s}",
                accountNumber, accountType, balance, currency, status);
    }
}
