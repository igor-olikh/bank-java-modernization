package com.bank.exception;

public class AccountFrozenException extends BankException {
    public AccountFrozenException(String accountId) {
        super("Account is frozen and cannot process transactions: " + accountId);
    }
}
