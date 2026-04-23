package com.bank.model;

import com.bank.model.enums.LoanStatus;
import com.bank.model.enums.LoanType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a loan product issued to a customer.
 */
public class Loan {

    private final String loanId;
    private final String customerId;
    private final String accountId;
    private final LoanType loanType;
    private final BigDecimal principalAmount;
    private BigDecimal outstandingBalance;
    private final BigDecimal interestRate;
    private final int termMonths;
    private final BigDecimal monthlyPayment;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private LoanStatus status;

    public Loan(String customerId, String accountId, LoanType loanType,
                BigDecimal principalAmount, BigDecimal interestRate, int termMonths) {
        this.loanId = UUID.randomUUID().toString();
        this.customerId = customerId;
        this.accountId = accountId;
        this.loanType = loanType;
        this.principalAmount = principalAmount;
        this.outstandingBalance = principalAmount;
        this.interestRate = interestRate;
        this.termMonths = termMonths;
        this.monthlyPayment = calculateMonthlyPayment(principalAmount, interestRate, termMonths);
        this.startDate = LocalDate.now();
        this.endDate = startDate.plusMonths(termMonths);
        this.status = LoanStatus.ACTIVE;
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal principal, BigDecimal annualRate, int months) {
        if (annualRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);
        }
        double r = annualRate.doubleValue() / 100.0 / 12.0;
        double payment = principal.doubleValue() * r / (1 - Math.pow(1 + r, -months));
        return BigDecimal.valueOf(payment).setScale(2, RoundingMode.HALF_UP);
    }

    public String getLoanId()               { return loanId; }
    public String getCustomerId()           { return customerId; }
    public String getAccountId()            { return accountId; }
    public LoanType getLoanType()           { return loanType; }
    public BigDecimal getPrincipalAmount()  { return principalAmount; }
    public BigDecimal getOutstandingBalance() { return outstandingBalance; }
    public BigDecimal getInterestRate()     { return interestRate; }
    public int getTermMonths()              { return termMonths; }
    public BigDecimal getMonthlyPayment()   { return monthlyPayment; }
    public LocalDate getStartDate()         { return startDate; }
    public LocalDate getEndDate()           { return endDate; }
    public LoanStatus getStatus()           { return status; }

    public void setOutstandingBalance(BigDecimal balance) { this.outstandingBalance = balance; }
    public void setStatus(LoanStatus status)              { this.status = status; }

    public void applyRepayment(BigDecimal amount) {
        this.outstandingBalance = this.outstandingBalance.subtract(amount);
        if (this.outstandingBalance.compareTo(BigDecimal.ZERO) <= 0) {
            this.outstandingBalance = BigDecimal.ZERO;
            this.status = LoanStatus.PAID_OFF;
        }
    }

    @Override
    public String toString() {
        return String.format("Loan{id='%s', type=%s, principal=%.2f, outstanding=%.2f, rate=%.2f%%, status=%s}",
                loanId, loanType, principalAmount, outstandingBalance, interestRate, status);
    }
}
