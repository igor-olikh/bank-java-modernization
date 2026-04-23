package com.bank.ui;

import com.bank.model.Card;
import com.bank.model.enums.CardNetwork;
import com.bank.model.enums.CardType;
import com.bank.service.AccountService;
import com.bank.service.CardService;
import com.bank.service.CustomerService;

import java.util.List;
import java.util.Scanner;

public class CardMenu {

    private static final String DIV = "--------------------------------------------------------------------------------";
    private final Scanner scanner;
    private final CardService cardService;
    private final AccountService accountService;
    private final CustomerService customerService;

    public CardMenu(Scanner scanner, CardService cardService, AccountService accountService, CustomerService customerService) {
        this.scanner = scanner;
        this.cardService = cardService;
        this.accountService = accountService;
        this.customerService = customerService;
    }

    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println("\n" + DIV);
            System.out.println("  CARDS");
            System.out.println(DIV);
            System.out.println("  [1]  List cards by customer");
            System.out.println("  [2]  Issue new card");
            System.out.println("  [3]  Block card");
            System.out.println("  [4]  Activate card");
            System.out.println("  [5]  Cancel card");
            System.out.println("  [0]  Back");
            System.out.println(DIV);
            System.out.print("  Select: ");
            String input = scanner.nextLine().trim();
            switch (input) {
                case "1": listCards();    break;
                case "2": issueCard();   break;
                case "3": blockCard();   break;
                case "4": activateCard(); break;
                case "5": cancelCard();  break;
                case "0": back = true;   break;
                default:  System.out.println("  [!] Invalid option.");
            }
        }
    }

    private void listCards() {
        System.out.print("  Customer ID: ");
        String customerId = scanner.nextLine().trim();
        try {
            String name = customerService.getCustomerById(customerId).getFullName();
            List<Card> cards = cardService.getCardsByCustomer(customerId);
            System.out.println("\n  Cards for: " + name);
            System.out.println("  " + DIV);
            System.out.printf("  %-20s %-8s %-12s %-10s %-8s %-12s%n",
                    "Card Number", "Type", "Network", "Expiry", "Status", "Daily Limit");
            System.out.println("  " + DIV);
            if (cards.isEmpty()) { System.out.println("  No cards found."); return; }
            cards.forEach(c -> System.out.printf("  %-20s %-8s %-12s %-10s %-8s %-12.2f%n",
                    c.getMaskedNumber(), c.getCardType(), c.getNetwork(),
                    c.getExpiryDate(), c.getStatus(), c.getDailyLimit()));
        } catch (Exception e) { System.out.println("  [!] " + e.getMessage()); }
    }

    private void issueCard() {
        try {
            System.out.print("  Account ID: ");
            String accountId = scanner.nextLine().trim();
            System.out.print("  Customer ID: ");
            String customerId = scanner.nextLine().trim();
            System.out.print("  Card type (DEBIT/CREDIT/VIRTUAL): ");
            CardType type = CardType.valueOf(scanner.nextLine().trim().toUpperCase());
            System.out.print("  Network (VISA/MASTERCARD/AMEX): ");
            CardNetwork network = CardNetwork.valueOf(scanner.nextLine().trim().toUpperCase());
            Card card = cardService.issueCard(accountId, customerId, type, network);
            System.out.println("  [OK] Card issued: " + card.getMaskedNumber() + " — " + card.getNetwork());
        } catch (Exception e) { System.out.println("  [!] " + e.getMessage()); }
    }

    private void blockCard() {
        System.out.print("  Card ID: ");
        String id = scanner.nextLine().trim();
        try { cardService.blockCard(id); System.out.println("  [OK] Card blocked."); }
        catch (Exception e) { System.out.println("  [!] " + e.getMessage()); }
    }

    private void activateCard() {
        System.out.print("  Card ID: ");
        String id = scanner.nextLine().trim();
        try { cardService.activateCard(id); System.out.println("  [OK] Card activated."); }
        catch (Exception e) { System.out.println("  [!] " + e.getMessage()); }
    }

    private void cancelCard() {
        System.out.print("  Card ID: ");
        String id = scanner.nextLine().trim();
        try { cardService.cancelCard(id); System.out.println("  [OK] Card cancelled."); }
        catch (Exception e) { System.out.println("  [!] " + e.getMessage()); }
    }
}
