package com.bank.ui.panels;

import com.bank.model.Account;
import com.bank.model.Customer;
import com.bank.service.AccountService;
import com.bank.service.CustomerService;
import com.bank.service.TransactionService;
import com.bank.ui.MainWindow;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class TransactionsPanel extends JPanel {

    private final TransactionService transactionService;
    private final AccountService     accountService;
    private final CustomerService    customerService;
    private final MainWindow         owner;

    private JComboBox<String>  customerCombo;
    private JComboBox<String>  accountCombo;
    private List<Customer>     customerList;
    private List<Account>      accountList;
    private DefaultTableModel  model;

    public TransactionsPanel(TransactionService ts, AccountService as, CustomerService cs, MainWindow owner) {
        this.transactionService = ts;
        this.accountService     = as;
        this.customerService    = cs;
        this.owner = owner;
        setLayout(new BorderLayout());
        setBackground(MainWindow.CONTENT_BG);
        build();
    }

    private void build() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(MainWindow.sectionTitle("Transactions"), BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 14));
        actions.setOpaque(false);

        customerCombo = new JComboBox<>();
        customerCombo.setFont(MainWindow.FONT_BODY);
        customerCombo.setPreferredSize(new Dimension(190, 30));
        accountCombo  = new JComboBox<>();
        accountCombo.setFont(MainWindow.FONT_BODY);
        accountCombo.setPreferredSize(new Dimension(130, 30));

        JButton loadAccBtn  = MainWindow.primaryButton("Load Accounts");
        JButton historyBtn  = MainWindow.primaryButton("Show History");
        JButton depositBtn  = MainWindow.successButton("Deposit");
        JButton withdrawBtn = MainWindow.dangerButton("Withdraw");
        JButton transferBtn = MainWindow.primaryButton("Transfer");

        actions.add(new JLabel("Customer:"));
        actions.add(customerCombo);
        actions.add(loadAccBtn);
        actions.add(new JLabel("Account:"));
        actions.add(accountCombo);
        actions.add(historyBtn);
        actions.add(depositBtn);
        actions.add(withdrawBtn);
        actions.add(transferBtn);
        top.add(actions, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        String[] cols = {"Date / Time", "Type", "Amount", "Currency", "Status", "Description", "Ref"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        MainWindow.styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(0, 20, 20, 20));
        add(scroll, BorderLayout.CENTER);

        populateCustomerCombo();
        loadAccBtn.addActionListener(e -> loadAccountsForCustomer());
        historyBtn.addActionListener(e -> showHistory());
        depositBtn.addActionListener(e -> doDeposit());
        withdrawBtn.addActionListener(e -> doWithdraw());
        transferBtn.addActionListener(e -> doTransfer());
    }

    private void populateCustomerCombo() {
        customerList = customerService.getAllCustomers();
        customerCombo.removeAllItems();
        customerList.forEach(c -> customerCombo.addItem(c.getFullName() + " [" + c.getCustomerId().substring(0, 8) + "]"));
    }

    private void loadAccountsForCustomer() {
        int idx = customerCombo.getSelectedIndex();
        if (idx < 0) return;
        Customer c = customerList.get(idx);
        accountList = accountService.getAccountsByCustomer(c.getCustomerId());
        accountCombo.removeAllItems();
        accountList.forEach(a -> accountCombo.addItem(a.getAccountNumber() + " (" + a.getCurrency() + ")"));
    }

    private Account selectedAccount() {
        int idx = accountCombo.getSelectedIndex();
        if (idx < 0 || accountList == null || accountList.isEmpty()) {
            MainWindow.showError(this, "Load accounts for a customer first.");
            return null;
        }
        return accountList.get(idx);
    }

    private void showHistory() {
        Account acc = selectedAccount();
        if (acc == null) return;
        model.setRowCount(0);
        transactionService.getHistory(acc.getAccountId()).forEach(t -> model.addRow(new Object[]{
                t.getTimestamp().toString().substring(0, 19),
                t.getType(), String.format("%.2f", t.getAmount()),
                t.getCurrency(), t.getStatus(), t.getDescription(), t.getReferenceCode()
        }));
        owner.setStatus("Transactions for " + acc.getAccountNumber() + ": " + model.getRowCount());
    }

    private void doDeposit() {
        Account acc = selectedAccount();
        if (acc == null) return;
        JTextField amountField = new JTextField(12);
        JTextField descField   = new JTextField("Manual deposit", 20);
        Object[][] rows = {{"Amount (" + acc.getCurrency() + "):", amountField}, {"Description:", descField}};
        if (JOptionPane.showConfirmDialog(this, CustomersPanel.buildForm(rows), "Deposit",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        try {
            transactionService.deposit(acc.getAccountId(), new BigDecimal(amountField.getText().trim()), descField.getText());
            showHistory();
            MainWindow.showInfo(this, "Deposit successful. New balance: " +
                    String.format("%.2f %s", accountService.getBalance(acc.getAccountId()), acc.getCurrency()));
        } catch (Exception ex) { MainWindow.showError(this, ex.getMessage()); }
    }

    private void doWithdraw() {
        Account acc = selectedAccount();
        if (acc == null) return;
        JTextField amountField = new JTextField(12);
        JTextField descField   = new JTextField("Cash withdrawal", 20);
        Object[][] rows = {{"Amount (" + acc.getCurrency() + "):", amountField}, {"Description:", descField}};
        if (JOptionPane.showConfirmDialog(this, CustomersPanel.buildForm(rows), "Withdraw",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        try {
            transactionService.withdraw(acc.getAccountId(), new BigDecimal(amountField.getText().trim()), descField.getText());
            showHistory();
            MainWindow.showInfo(this, "Withdrawal successful. New balance: " +
                    String.format("%.2f %s", accountService.getBalance(acc.getAccountId()), acc.getCurrency()));
        } catch (Exception ex) { MainWindow.showError(this, ex.getMessage()); }
    }

    private void doTransfer() {
        Account from = selectedAccount();
        if (from == null) return;
        JTextField toAccField  = new JTextField(14);
        JTextField amountField = new JTextField(12);
        JTextField descField   = new JTextField("Internal transfer", 20);
        Object[][] rows = {{"To Account No.:", toAccField}, {"Amount:", amountField}, {"Description:", descField}};
        if (JOptionPane.showConfirmDialog(this, CustomersPanel.buildForm(rows), "Transfer",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        try {
            Account to = accountService.getAccountByNumber(toAccField.getText().trim());
            transactionService.transfer(from.getAccountId(), to.getAccountId(),
                    new BigDecimal(amountField.getText().trim()), descField.getText());
            showHistory();
            MainWindow.showInfo(this, "Transfer complete.");
        } catch (Exception ex) { MainWindow.showError(this, ex.getMessage()); }
    }
}
