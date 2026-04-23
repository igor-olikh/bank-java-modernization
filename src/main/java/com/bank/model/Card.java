package com.bank.model;

import com.bank.model.enums.CardNetwork;
import com.bank.model.enums.CardStatus;
import com.bank.model.enums.CardType;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

/**
 * Represents a payment card linked to an account.
 */
public class Card {

    private final String cardId;
    private final String accountId;
    private final String customerId;
    private final String maskedNumber;
    private final CardType cardType;
    private final CardNetwork network;
    private final YearMonth expiryDate;
    private CardStatus status;
    private BigDecimal creditLimit;
    private BigDecimal dailyLimit;

    public Card(String accountId, String customerId, CardType cardType, CardNetwork network) {
        this.cardId = UUID.randomUUID().toString();
        this.accountId = accountId;
        this.customerId = customerId;
        this.cardType = cardType;
        this.network = network;
        this.maskedNumber = "**** **** **** " + String.format("%04d", (int)(Math.random() * 10000));
        this.expiryDate = YearMonth.now().plusYears(3);
        this.status = CardStatus.ACTIVE;
        this.creditLimit = cardType == CardType.CREDIT ? new BigDecimal("5000.00") : BigDecimal.ZERO;
        this.dailyLimit = new BigDecimal("2000.00");
    }

    public String getCardId()           { return cardId; }
    public String getAccountId()        { return accountId; }
    public String getCustomerId()       { return customerId; }
    public String getMaskedNumber()     { return maskedNumber; }
    public CardType getCardType()       { return cardType; }
    public CardNetwork getNetwork()     { return network; }
    public YearMonth getExpiryDate()    { return expiryDate; }
    public CardStatus getStatus()       { return status; }
    public BigDecimal getCreditLimit()  { return creditLimit; }
    public BigDecimal getDailyLimit()   { return dailyLimit; }

    public void setStatus(CardStatus status)         { this.status = status; }
    public void setCreditLimit(BigDecimal limit)     { this.creditLimit = limit; }
    public void setDailyLimit(BigDecimal limit)      { this.dailyLimit = limit; }

    @Override
    public String toString() {
        return String.format("Card{number='%s', type=%s, network=%s, expiry=%s, status=%s}",
                maskedNumber, cardType, network, expiryDate, status);
    }
}
