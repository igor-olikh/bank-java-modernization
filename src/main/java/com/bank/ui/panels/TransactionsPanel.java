package com.bank.ui.panels;

import com.bank.model.Account;
import com.bank.model.Customer;
import com.bank.service.AccountService;
import com.bank.service.CustomerService;
import com.bank.service.TransactionService;
import com.bank.ui.MainWindow;

import com.bank.util.DateUtil;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Transactions panel.
 * Customer → auto-loads accounts → select account → history auto-loads.
 * Deposit / Withdraw show current balance.
 * Transfer uses a dropdown of all accounts in the system.
 */
public class TransactionsPanel extends JPanel {

    private final TransactionService transactionService;
    private final AccountService     accountService;
    private final CustomerService    customerService;
    private final MainWindow         owner;

    private JComboBox<String> customerCombo;
    private JComboBox<String> accountCombo;
    private List<Customer>    customerList;
    private List<Account>     accountList;
    private DefaultTableModel model;
    private JTable            table;

    public TransactionsPanel(TransactionService ts, AccountService as, CustomerService cs, MainWindow owner) {
        this.transactionService = ts;
        this.accountService     = as;
        this.customerService    = cs;
        this.owner = owner;
        setLayout(new BorderLayout());
        setBackground(MainWindow.CONTENT_BG);
        build();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // UI Construction
    // ──────────────────────────────────────────────────────────────────────────

    private void build() {
        // ── Toolbar ──────────────────────────────────────────────────────────
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(MainWindow.sectionTitle("Transactions"), BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 14));
        actions.setOpaque(false);

        customerCombo = new JComboBox<>();
        customerCombo.setFont(MainWindow.FONT_BODY);
        customerCombo.setPreferredSize(new Dimension(210, 30));

        accountCombo = new JComboBox<>();
        accountCombo.setFont(MainWindow.FONT_BODY);
        accountCombo.setPreferredSize(new Dimension(160, 30));

        JButton depositBtn  = MainWindow.successButton("Deposit");
        JButton withdrawBtn = MainWindow.dangerButton("Withdraw");
        JButton transferBtn = MainWindow.primaryButton("Transfer");
        JButton refreshBtn  = new JButton("↺ Refresh");
        refreshBtn.setFont(MainWindow.FONT_BODY);

        actions.add(new JLabel("Customer:"));
        actions.add(customerCombo);
        actions.add(Box.createHorizontalStrut(4));
        actions.add(new JLabel("Account:"));
        actions.add(accountCombo);
        actions.add(refreshBtn);
        actions.add(Box.createHorizontalStrut(8));
        actions.add(depositBtn);
        actions.add(withdrawBtn);
        actions.add(transferBtn);
        top.add(actions, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        // ── Balance info bar ─────────────────────────────────────────────────
        JPanel infoBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 6));
        infoBar.setBackground(new Color(0xEFF6FF));
        infoBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xBFDBFE)));

        JLabel balLabel = new JLabel("Balance: —");
        balLabel.setFont(MainWindow.FONT_LABEL);
        balLabel.setForeground(new Color(0x1D4ED8));

        JLabel availLabel = new JLabel("Available: —");
        availLabel.setFont(MainWindow.FONT_BODY);
        availLabel.setForeground(new Color(0x3B82F6));

        JLabel currLabel = new JLabel("");
        currLabel.setFont(MainWindow.FONT_BODY);
        currLabel.setForeground(new Color(0x64748B));

        infoBar.add(balLabel);
        infoBar.add(new JSeparator(SwingConstants.VERTICAL));
        infoBar.add(availLabel);
        infoBar.add(new JSeparator(SwingConstants.VERTICAL));
        infoBar.add(currLabel);
        add(infoBar, BorderLayout.CENTER);

        // ── Table ────────────────────────────────────────────────────────────
        String[] cols = {"Date / Time", "Type", "Amount", "Currency", "Status", "Description", "Reference"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        MainWindow.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(160);
        table.getColumnModel().getColumn(5).setPreferredWidth(200);
        table.getColumnModel().getColumn(6).setPreferredWidth(140);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(0, 20, 20, 20));

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(infoBar, BorderLayout.NORTH);
        center.add(scroll,  BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // ── Wire events ──────────────────────────────────────────────────────
        populateCustomerCombo();

        // Auto-load accounts when customer changes
        customerCombo.addActionListener(e -> {
            loadAccountsForCustomer();
            loadHistory(balLabel, availLabel, currLabel);
        });

        // Auto-load history when account changes
        accountCombo.addActionListener(e -> loadHistory(balLabel, availLabel, currLabel));

        refreshBtn.addActionListener(e -> {
            loadHistory(balLabel, availLabel, currLabel);
            owner.setStatus("History refreshed.");
        });

        depositBtn.addActionListener(e -> doDeposit(balLabel, availLabel, currLabel));
        withdrawBtn.addActionListener(e -> doWithdraw(balLabel, availLabel, currLabel));
        transferBtn.addActionListener(e -> doTransfer(balLabel, availLabel, currLabel));
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Data helpers
    // ──────────────────────────────────────────────────────────────────────────

    private void populateCustomerCombo() {
        customerList = customerService.getAllCustomers();
        customerCombo.removeAllItems();
        customerList.forEach(c -> customerCombo.addItem(c.getFullName() + " [" + c.getCustomerId().substring(0, 8) + "]"));
    }

    private void loadAccountsForCustomer() {
        int idx = customerCombo.getSelectedIndex();
        accountCombo.removeAllItems();
        accountList = null;
        if (idx < 0 || customerList == null) return;
        Customer c = customerList.get(idx);
        accountList = accountService.getAccountsByCustomer(c.getCustomerId());
        accountList.forEach(a -> accountCombo.addItem(
                a.getAccountNumber() + " (" + a.getAccountType() + " · " + a.getCurrency() + ")"));
    }

    /** Returns the currently selected Account, or shows an error and returns null. */
    private Account selectedAccount() {
        int idx = accountCombo.getSelectedIndex();
        if (idx < 0 || accountList == null || accountList.isEmpty()) {
            MainWindow.showError(this, "Select a customer and account first.");
            return null;
        }
        return accountList.get(idx);
    }

    private void loadHistory(JLabel balLabel, JLabel availLabel, JLabel currLabel) {
        model.setRowCount(0);
        Account acc = (accountCombo.getSelectedIndex() >= 0 && accountList != null && !accountList.isEmpty())
                ? accountList.get(accountCombo.getSelectedIndex()) : null;
        if (acc == null) { updateInfoBar(null, balLabel, availLabel, currLabel); return; }

        // Reload account to get fresh balance
        Account fresh = accountService.getAccount(acc.getAccountId());
        updateInfoBar(fresh, balLabel, availLabel, currLabel);

        transactionService.getHistory(fresh.getAccountId()).forEach(t -> model.addRow(new Object[]{
                DateUtil.formatDateTime(t.getTimestamp()),
                t.getType(),
                String.format("%.2f", t.getAmount()),
                t.getCurrency(),
                t.getStatus(),
                t.getDescription(),
                t.getReferenceCode()
        }));
        owner.setStatus("Transactions for " + fresh.getAccountNumber() + ": " + model.getRowCount());
    }

    private void updateInfoBar(Account acc, JLabel balLabel, JLabel availLabel, JLabel currLabel) {
        if (acc == null) {
            balLabel.setText("Balance: —");
            availLabel.setText("Available: —");
            currLabel.setText("");
        } else {
            balLabel.setText(String.format("Balance: %,.2f", acc.getBalance()));
            availLabel.setText(String.format("Available: %,.2f", acc.getAvailableBalance()));
            currLabel.setText(acc.getCurrency() + "  ·  " + acc.getStatus());
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Actions
    // ──────────────────────────────────────────────────────────────────────────

    private void doDeposit(JLabel balLabel, JLabel availLabel, JLabel currLabel) {
        Account acc = selectedAccount();
        if (acc == null) return;

        JTextField amountField = new JTextField("", 14);
        JTextField descField   = new JTextField("Cash deposit", 22);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(new EmptyBorder(10, 10, 10, 10));
        addFormRow(form, "Current balance:", String.format("%,.2f %s", acc.getBalance(), acc.getCurrency()));
        addFormRow(form, "Deposit amount:", amountField);
        addFormRow(form, "Description:", descField);

        int res = JOptionPane.showConfirmDialog(this, form,
                "Deposit — " + acc.getAccountNumber(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;
        try {
            transactionService.deposit(acc.getAccountId(),
                    new BigDecimal(amountField.getText().trim()), descField.getText().trim());
            loadHistory(balLabel, availLabel, currLabel);
            Account updated = accountService.getAccount(acc.getAccountId());
            MainWindow.showInfo(this, String.format("Deposit successful.\nNew balance: %,.2f %s",
                    updated.getBalance(), updated.getCurrency()));
        } catch (Exception ex) { MainWindow.showError(this, ex.getMessage()); }
    }

    private void doWithdraw(JLabel balLabel, JLabel availLabel, JLabel currLabel) {
        Account acc = selectedAccount();
        if (acc == null) return;

        JTextField amountField = new JTextField("", 14);
        JTextField descField   = new JTextField("Cash withdrawal", 22);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(new EmptyBorder(10, 10, 10, 10));
        addFormRow(form, "Current balance:", String.format("%,.2f %s", acc.getBalance(), acc.getCurrency()));
        addFormRow(form, "Available:", String.format("%,.2f %s", acc.getAvailableBalance(), acc.getCurrency()));
        addFormRow(form, "Withdrawal amount:", amountField);
        addFormRow(form, "Description:", descField);

        int res = JOptionPane.showConfirmDialog(this, form,
                "Withdraw — " + acc.getAccountNumber(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;
        try {
            transactionService.withdraw(acc.getAccountId(),
                    new BigDecimal(amountField.getText().trim()), descField.getText().trim());
            loadHistory(balLabel, availLabel, currLabel);
            Account updated = accountService.getAccount(acc.getAccountId());
            MainWindow.showInfo(this, String.format("Withdrawal successful.\nNew balance: %,.2f %s",
                    updated.getBalance(), updated.getCurrency()));
        } catch (Exception ex) { MainWindow.showError(this, ex.getMessage()); }
    }

    private void doTransfer(JLabel balLabel, JLabel availLabel, JLabel currLabel) {
        Account from = selectedAccount();
        if (from == null) return;

        // Build list of all accounts except the source
        List<Account> allAccounts = accountService.getAllAccounts().stream()
                .filter(a -> !a.getAccountId().equals(from.getAccountId()))
                .collect(Collectors.toList());

        if (allAccounts.isEmpty()) {
            MainWindow.showError(this, "No other accounts available for transfer.");
            return;
        }

        // Build display items: "BNK100001 (James Anderson · USD · CHECKING)"
        String[] toItems = allAccounts.stream().map(a -> {
            Customer owner = customerList.stream()
                    .filter(c -> c.getCustomerId().equals(a.getCustomerId()))
                    .findFirst().orElse(null);
            String ownerName = owner != null ? owner.getFullName() : "Unknown";
            return a.getAccountNumber() + "  —  " + ownerName + "  (" + a.getCurrency() + " · " + a.getAccountType() + ")";
        }).toArray(String[]::new);

        JComboBox<String> toCombo = new JComboBox<>(toItems);
        toCombo.setFont(MainWindow.FONT_BODY);
        toCombo.setPreferredSize(new Dimension(360, 28));

        JTextField amountField = new JTextField("", 14);
        JTextField descField   = new JTextField("Internal transfer", 22);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(new EmptyBorder(10, 10, 10, 10));
        addFormRow(form, "From account:", from.getAccountNumber() + " (" + from.getCurrency() + ")");
        addFormRow(form, "Current balance:", String.format("%,.2f %s", from.getBalance(), from.getCurrency()));
        addFormRow(form, "To account:", toCombo);
        addFormRow(form, "Amount (" + from.getCurrency() + "):", amountField);
        addFormRow(form, "Description:", descField);

        int res = JOptionPane.showConfirmDialog(this, form,
                "Transfer — " + from.getAccountNumber(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;
        try {
            Account to = allAccounts.get(toCombo.getSelectedIndex());
            transactionService.transfer(from.getAccountId(), to.getAccountId(),
                    new BigDecimal(amountField.getText().trim()), descField.getText().trim());
            loadHistory(balLabel, availLabel, currLabel);
            MainWindow.showInfo(this, String.format(
                    "Transfer complete.\nFrom: %s → To: %s\nAmount: %s %s",
                    from.getAccountNumber(), to.getAccountNumber(),
                    amountField.getText().trim(), from.getCurrency()));
        } catch (Exception ex) { MainWindow.showError(this, ex.getMessage()); }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Form helpers
    // ──────────────────────────────────────────────────────────────────────────

    /** Adds a label + read-only value row. */
    private static void addFormRow(JPanel form, String label, String value) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(MainWindow.FONT_BODY);
        JLabel val = new JLabel(value);
        val.setFont(new Font("SansSerif", Font.BOLD, 13));
        val.setForeground(new Color(0x1E293B));
        form.add(lbl);
        form.add(val);
    }

    /** Adds a label + editable component row. */
    private static void addFormRow(JPanel form, String label, JComponent field) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(MainWindow.FONT_BODY);
        form.add(lbl);
        form.add(field);
    }
}
