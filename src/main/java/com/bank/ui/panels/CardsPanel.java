package com.bank.ui.panels;

import com.bank.model.Account;
import com.bank.model.Customer;
import com.bank.model.enums.CardNetwork;
import com.bank.model.enums.CardType;
import com.bank.service.AccountService;
import com.bank.service.CardService;
import com.bank.service.CustomerService;
import com.bank.ui.MainWindow;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CardsPanel extends JPanel {

    private final CardService     cardService;
    private final AccountService  accountService;
    private final CustomerService customerService;
    private final MainWindow      owner;

    private JComboBox<String> customerCombo;
    private List<Customer>    customerList;
    private DefaultTableModel model;

    public CardsPanel(CardService cs, AccountService as, CustomerService custS, MainWindow owner) {
        this.cardService     = cs;
        this.accountService  = as;
        this.customerService = custS;
        this.owner = owner;
        setLayout(new BorderLayout());
        setBackground(MainWindow.CONTENT_BG);
        build();
    }

    private void build() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(MainWindow.sectionTitle("Cards"), BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 14));
        actions.setOpaque(false);
        customerCombo = new JComboBox<>();
        customerCombo.setFont(MainWindow.FONT_BODY);
        customerCombo.setPreferredSize(new Dimension(220, 30));

        JButton loadBtn   = MainWindow.primaryButton("Load Cards");
        JButton issueBtn  = MainWindow.successButton("Issue Card");
        JButton blockBtn  = MainWindow.dangerButton("Block");
        JButton activeBtn = MainWindow.primaryButton("Activate");
        JButton cancelBtn = MainWindow.dangerButton("Cancel");

        actions.add(new JLabel("Customer:"));
        actions.add(customerCombo);
        actions.add(loadBtn);
        actions.add(issueBtn);
        actions.add(blockBtn);
        actions.add(activeBtn);
        actions.add(cancelBtn);
        top.add(actions, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        String[] cols = {"Card Number", "Type", "Network", "Expiry", "Status", "Daily Limit", "Credit Limit"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        MainWindow.styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(0, 20, 20, 20));
        add(scroll, BorderLayout.CENTER);

        populateCustomerCombo();
        loadBtn.addActionListener(e -> loadCards(table));
        issueBtn.addActionListener(e -> issueCard(table));
        blockBtn.addActionListener(e -> actOnSelected(table, "block"));
        activeBtn.addActionListener(e -> actOnSelected(table, "activate"));
        cancelBtn.addActionListener(e -> actOnSelected(table, "cancel"));
    }

    private void populateCustomerCombo() {
        customerList = customerService.getAllCustomers();
        customerCombo.removeAllItems();
        customerList.forEach(c -> customerCombo.addItem(c.getFullName() + " [" + c.getCustomerId().substring(0, 8) + "]"));
    }

    private void loadCards(JTable table) {
        int idx = customerCombo.getSelectedIndex();
        if (idx < 0) return;
        Customer c = customerList.get(idx);
        model.setRowCount(0);
        cardService.getCardsByCustomer(c.getCustomerId()).forEach(card -> model.addRow(new Object[]{
                card.getMaskedNumber(), card.getCardType(), card.getNetwork(),
                card.getExpiryDate(), card.getStatus(),
                String.format("%.2f", card.getDailyLimit()),
                String.format("%.2f", card.getCreditLimit())
        }));
        owner.setStatus("Cards for " + c.getFullName() + ": " + model.getRowCount());
    }

    private void issueCard(JTable table) {
        int idx = customerCombo.getSelectedIndex();
        if (idx < 0) { MainWindow.showError(this, "Select a customer first."); return; }
        Customer c = customerList.get(idx);
        List<Account> accounts = accountService.getAccountsByCustomer(c.getCustomerId());
        if (accounts.isEmpty()) { MainWindow.showError(this, "Customer has no accounts."); return; }

        String[] accItems = accounts.stream().map(a -> a.getAccountNumber() + " (" + a.getCurrency() + ")").toArray(String[]::new);
        JComboBox<String> accCombo  = new JComboBox<>(accItems);
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"DEBIT", "CREDIT", "VIRTUAL"});
        JComboBox<String> netCombo  = new JComboBox<>(new String[]{"VISA", "MASTERCARD", "AMEX"});

        Object[][] rows = {{"Account:", accCombo}, {"Card type:", typeCombo}, {"Network:", netCombo}};
        if (JOptionPane.showConfirmDialog(this, CustomersPanel.buildForm(rows), "Issue Card",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        try {
            Account acc = accounts.get(accCombo.getSelectedIndex());
            cardService.issueCard(acc.getAccountId(), c.getCustomerId(),
                    CardType.valueOf(typeCombo.getSelectedItem().toString()),
                    CardNetwork.valueOf(netCombo.getSelectedItem().toString()));
            loadCards(table);
            MainWindow.showInfo(this, "Card issued successfully.");
        } catch (Exception ex) { MainWindow.showError(this, ex.getMessage()); }
    }

    private void actOnSelected(JTable table, String action) {
        int row = table.getSelectedRow();
        if (row < 0) { MainWindow.showError(this, "Select a card first."); return; }
        // Reload cards with full objects to get cardId
        int idx = customerCombo.getSelectedIndex();
        if (idx < 0) return;
        Customer c = customerList.get(idx);
        List<com.bank.model.Card> cards = cardService.getCardsByCustomer(c.getCustomerId());
        if (row >= cards.size()) return;
        com.bank.model.Card card = cards.get(row);
        try {
            switch (action) {
                case "block":    cardService.blockCard(card.getCardId());    break;
                case "activate": cardService.activateCard(card.getCardId()); break;
                case "cancel":   cardService.cancelCard(card.getCardId());   break;
            }
            loadCards(table);
            MainWindow.showInfo(this, "Card " + action + "d.");
        } catch (Exception ex) { MainWindow.showError(this, ex.getMessage()); }
    }
}
