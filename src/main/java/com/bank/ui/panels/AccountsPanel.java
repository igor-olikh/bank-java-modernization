package com.bank.ui.panels;

import com.bank.model.Account;
import com.bank.model.Customer;
import com.bank.model.enums.AccountType;
import com.bank.service.AccountService;
import com.bank.service.CustomerService;
import com.bank.ui.MainWindow;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Accounts panel — auto-loads accounts on customer select.
 */
public class AccountsPanel extends JPanel {

    private final AccountService  accountService;
    private final CustomerService customerService;
    private final MainWindow      owner;
    private DefaultTableModel     model;
    private JComboBox<String>     customerCombo;
    private List<Customer>        customerList;
    private JTable                table;

    public AccountsPanel(AccountService as, CustomerService cs, MainWindow owner) {
        this.accountService  = as;
        this.customerService = cs;
        this.owner = owner;
        setLayout(new BorderLayout());
        setBackground(MainWindow.CONTENT_BG);
        build();
    }

    private void build() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(MainWindow.sectionTitle("Accounts"), BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 14));
        actions.setOpaque(false);
        customerCombo = new JComboBox<>();
        customerCombo.setFont(MainWindow.FONT_BODY);
        customerCombo.setPreferredSize(new Dimension(240, 30));

        JButton openBtn     = MainWindow.successButton("+ Open Account");
        JButton freezeBtn   = MainWindow.dangerButton("Freeze");
        JButton unfreezeBtn = MainWindow.primaryButton("Unfreeze");
        JButton closeBtn    = MainWindow.dangerButton("Close");

        actions.add(new JLabel("Customer:"));
        actions.add(customerCombo);
        actions.add(openBtn);
        actions.add(freezeBtn);
        actions.add(unfreezeBtn);
        actions.add(closeBtn);
        top.add(actions, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        String[] cols = {"Account No.", "Type", "Currency", "Balance", "Available", "Interest %", "Status", "Opened"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        MainWindow.styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(0, 20, 20, 20));
        add(scroll, BorderLayout.CENTER);

        populateCustomerCombo();

        // Auto-load when customer is selected
        customerCombo.addActionListener(e -> loadAccounts());

        openBtn.addActionListener(e -> openAccount());
        freezeBtn.addActionListener(e -> actOnSelected("freeze"));
        unfreezeBtn.addActionListener(e -> actOnSelected("unfreeze"));
        closeBtn.addActionListener(e -> actOnSelected("close"));
    }

    private void populateCustomerCombo() {
        customerList = customerService.getAllCustomers();
        customerCombo.removeAllItems();
        customerList.forEach(c -> customerCombo.addItem(c.getFullName() + " [" + c.getCustomerId().substring(0, 8) + "]"));
    }

    private void loadAccounts() {
        int idx = customerCombo.getSelectedIndex();
        if (idx < 0 || customerList == null) return;
        Customer c = customerList.get(idx);
        model.setRowCount(0);
        accountService.getAccountsByCustomer(c.getCustomerId()).forEach(a -> model.addRow(new Object[]{
                a.getAccountNumber(), a.getAccountType(), a.getCurrency(),
                String.format("%,.2f", a.getBalance()),
                String.format("%,.2f", a.getAvailableBalance()),
                a.getInterestRate(), a.getStatus(), a.getOpenedAt().toLocalDate()
        }));
        owner.setStatus("Accounts for " + c.getFullName() + ": " + model.getRowCount());
    }

    private void openAccount() {
        int idx = customerCombo.getSelectedIndex();
        if (idx < 0) { MainWindow.showError(this, "Select a customer first."); return; }
        Customer c = customerList.get(idx);
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"CHECKING", "SAVINGS", "BUSINESS", "INVESTMENT"});
        JTextField currencyField = new JTextField("USD", 8);
        Object[][] rows = {{"Account type:", typeBox}, {"Currency (e.g. USD, EUR):", currencyField}};
        JPanel form = CustomersPanel.buildForm(rows);
        int res = JOptionPane.showConfirmDialog(this, form, "Open New Account for " + c.getFullName(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;
        try {
            Account acc = accountService.openAccount(c.getCustomerId(),
                    AccountType.valueOf(typeBox.getSelectedItem().toString()),
                    currencyField.getText().trim().toUpperCase());
            loadAccounts();
            MainWindow.showInfo(this, "Account opened: " + acc.getAccountNumber());
        } catch (Exception ex) { MainWindow.showError(this, ex.getMessage()); }
    }

    private void actOnSelected(String action) {
        int row = table.getSelectedRow();
        if (row < 0) { MainWindow.showError(this, "Select an account from the table first."); return; }
        String accNum = model.getValueAt(row, 0).toString();
        try {
            Account acc = accountService.getAccountByNumber(accNum);
            switch (action) {
                case "freeze":   accountService.freezeAccount(acc.getAccountId());   break;
                case "unfreeze": accountService.unfreezeAccount(acc.getAccountId()); break;
                case "close":    accountService.closeAccount(acc.getAccountId());    break;
            }
            loadAccounts();
            MainWindow.showInfo(this, "Account " + accNum + " " + action + "d.");
        } catch (Exception ex) { MainWindow.showError(this, ex.getMessage()); }
    }
}
