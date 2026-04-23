package com.bank.service;

import com.bank.model.Card;
import com.bank.model.enums.CardNetwork;
import com.bank.model.enums.CardStatus;
import com.bank.model.enums.CardType;
import com.bank.repository.CardRepository;

import java.util.List;

/**
 * Business logic for card issuance and lifecycle management.
 */
public class CardService {

    private final CardRepository cardRepository;
    private final AccountService accountService;

    public CardService(CardRepository cardRepository, AccountService accountService) {
        this.cardRepository = cardRepository;
        this.accountService = accountService;
    }

    public Card issueCard(String accountId, String customerId, CardType type, CardNetwork network) {
        // Validate account exists
        accountService.getAccount(accountId);

        Card card = new Card(accountId, customerId, type, network);
        cardRepository.save(card);
        return card;
    }

    public Card getCard(String cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found: " + cardId));
    }

    public List<Card> getCardsByAccount(String accountId) {
        return cardRepository.findByAccountId(accountId);
    }

    public List<Card> getCardsByCustomer(String customerId) {
        return cardRepository.findByCustomerId(customerId);
    }

    public void blockCard(String cardId) {
        Card card = getCard(cardId);
        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
    }

    public void activateCard(String cardId) {
        Card card = getCard(cardId);
        if (card.getStatus() == CardStatus.CANCELLED || card.getStatus() == CardStatus.EXPIRED) {
            throw new IllegalStateException("Cannot activate a cancelled or expired card.");
        }
        card.setStatus(CardStatus.ACTIVE);
        cardRepository.save(card);
    }

    public void cancelCard(String cardId) {
        Card card = getCard(cardId);
        card.setStatus(CardStatus.CANCELLED);
        cardRepository.save(card);
    }
}
