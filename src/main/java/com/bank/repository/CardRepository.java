package com.bank.repository;

import com.bank.model.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory repository for Card entities.
 */
public class CardRepository {

    private final ConcurrentHashMap<String, Card> store = new ConcurrentHashMap<>();

    public synchronized void save(Card card) {
        store.put(card.getCardId(), card);
    }

    public Optional<Card> findById(String cardId) {
        return Optional.ofNullable(store.get(cardId));
    }

    public List<Card> findByAccountId(String accountId) {
        return store.values().stream()
                .filter(c -> c.getAccountId().equals(accountId))
                .collect(Collectors.toList());
    }

    public List<Card> findByCustomerId(String customerId) {
        return store.values().stream()
                .filter(c -> c.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    public List<Card> findAll() {
        return new ArrayList<>(store.values());
    }
}
