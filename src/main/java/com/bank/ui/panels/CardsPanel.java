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

/**
 * Cards panel — auto-loads cards on customer select.
 */
public class CardsPanel extends JPanel {

    private final CardService     cardService;
    private final AccountService  accountService;
    private final CustomerService customerService;
    private final MainWindow      owner;

    private JComboBox<String> customerCombo;
    private List<Customer>    customerList;
    private List<com.bank.model.Card> cardList;
    private DefaultTableModel model;
    private JTable            table;

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
        customerCombo.setPreferredSize(new Dimension(240, 30));

        JButton issueBtn  = MainWindow.successButton("Issue Card");
        JButton blockBtn  = MainWindow.dangerButton("Block");
        JButton activeBtn = MainWindow.primaryButton("Activate");
        JButton cancelBtn = MainWindow.dangerButton("Cancel");

        actions.add(new JLabel("Customer:"));
        actions.add(customerCombo);
        actions.add(issueBtn);
        actions.add(blockBtn);
        actions.add(activeBtn);
        actions.add(cancelBtn);
        top.add(actions, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        String[] cols = {"Card Number", "Type", "Network", "Expiry", "Status", "Daily Limit", "Credit Limit", "Account No."};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        MainWindow.styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(0, 20, 20, 20));
        add(scroll, BorderLayout.CENTER);

        populateCustomerCombo();
        customerCombo.addActionListener(e -> loadCards());
        issueBtn.addActionListener(e -> issueCard());
        blockBtn.addActionListener(e -> actOnSelected("block"));
        activeBtn.addActionListener(e -> actOnSelected("activate"));
        cancelBtn.addActionListener(e -> actOnSelected("cancel"));
    }

    private void populateCustomerCombo() {
        customerList = customerService.getAllCustomers();
        customerCombo.removeAllItems();
        customerCombo.addItem("— Select customer —");
        customerList.forEach(c -> customerCombo.addItem(c.getFullName() + " [" + c.getCustomerId().substring(0, 8) + "]"));
        customerCombo.setSelectedIndex(0);
    }

    private void loadCards() {
        int idx = customerCombo.getSelectedIndex();
        if (idx <= 0 || customerList == null) { model.setRowCount(0); cardList = null; return; }
        Customer c = customerList.get(idx - 1);
        cardList = cardService.getCardsByCustomer(c.getCustomerId());
        model.setRowCount(0);
        cardList.forEach(card -> {
            Account acc = null;
            try { acc = accountService.getAccount(card.getAccountId()); } catch (Exception ignored) {}
            model.addRow(new Object[]{
                    card.getMaskedNumber(), card.getCardType(), card.getNetwork(),
                    card.getExpiryDate(), card.getStatus(),
                    String.format("%,.2f", card.getDailyLimit()),
                    String.format("%,.2f", card.getCreditLimit()),
                    acc != null ? acc.getAccountNumber() : card.getAccountId().substring(0, 8)
            });
        });
        owner.setStatus("Cards for " + c.getFullName() + ": " + model.getRowCount());
    }

    private void issueCard() {
        int idx = customerCombo.getSelectedIndex();
        if (idx <= 0) { MainWindow.showError(this, "Select a customer first."); return; }
        Customer c = customerList.get(idx - 1);
        List<Account> accounts = accountService.getAccountsByCustomer(c.getCustomerId());
        if (accounts.isEmpty()) { MainWindow.showError(this, "Customer has no accounts. Open an account first."); return; }

        String[] accItems = accounts.stream()
                .map(a -> a.getAccountNumber() + "  (" + a.getAccountType() + " · " + a.getCurrency()
                        + " · Balance: " + String.format("%,.2f", a.getBalance()) + ")")
                .toArray(String[]::new);
        JComboBox<String> accCombo  = new JComboBox<>(accItems);
        accCombo.setFont(MainWindow.FONT_BODY);
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"DEBIT", "CREDIT", "VIRTUAL"});
        JComboBox<String> netCombo  = new JComboBox<>(new String[]{"VISA", "MASTERCARD", "AMEX"});

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(new EmptyBorder(10, 10, 10, 10));
        addRow(form, "Customer:", c.getFullName());
        addRow(form, "Link to account:", accCombo);
        addRow(form, "Card type:", typeCombo);
        addRow(form, "Network:", netCombo);

        int res = JOptionPane.showConfirmDialog(this, form, "Issue New Card",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;
        try {
            Account acc = accounts.get(accCombo.getSelectedIndex());
            cardService.issueCard(acc.getAccountId(), c.getCustomerId(),
                    CardType.valueOf(typeCombo.getSelectedItem().toString()),
                    CardNetwork.valueOf(netCombo.getSelectedItem().toString()));
            loadCards();
            MainWindow.showInfo(this, "Card issued successfully.");
        } catch (Exception ex) { MainWindow.showError(this, ex.getMessage()); }
    }

    private void actOnSelected(String action) {
        int row = table.getSelectedRow();
        if (row < 0 || cardList == null || row >= cardList.size()) {
            MainWindow.showError(this, "Select a card from the table first."); return;
        }
        com.bank.model.Card card = cardList.get(row);
        try {
            switch (action) {
                case "block":    cardService.blockCard(card.getCardId());    break;
                case "activate": cardService.activateCard(card.getCardId()); break;
                case "cancel":   cardService.cancelCard(card.getCardId());   break;
            }
            loadCards();
            MainWindow.showInfo(this, "Card " + action + "d: " + card.getMaskedNumber());
        } catch (Exception ex) { MainWindow.showError(this, ex.getMessage()); }
    }

    private static void addRow(JPanel form, String label, String value) {
        JLabel lbl = new JLabel(label); lbl.setFont(MainWindow.FONT_BODY); form.add(lbl);
        JLabel val = new JLabel(value); val.setFont(new Font("SansSerif", Font.BOLD, 13)); form.add(val);
    }

    private static void addRow(JPanel form, String label, JComponent comp) {
        JLabel lbl = new JLabel(label); lbl.setFont(MainWindow.FONT_BODY); form.add(lbl);
        form.add(comp);
    }
}
