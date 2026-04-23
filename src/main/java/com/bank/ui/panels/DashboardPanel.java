package com.bank.ui.panels;

import com.bank.model.Account;
import com.bank.model.Transaction;
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

/**
 * Dashboard panel — shows summary statistics and recent transactions.
 */
public class DashboardPanel extends JPanel {

    private final CustomerService    customerService;
    private final AccountService     accountService;
    private final TransactionService transactionService;

    private JLabel totalCustomersValue;
    private JLabel totalAccountsValue;
    private JLabel totalBalanceValue;
    private DefaultTableModel recentModel;

    public DashboardPanel(CustomerService cs, AccountService as, TransactionService ts) {
        this.customerService    = cs;
        this.accountService     = as;
        this.transactionService = ts;
        setLayout(new BorderLayout());
        setBackground(MainWindow.CONTENT_BG);
        build();
        refresh();
    }

    private void build() {
        add(MainWindow.sectionTitle("Dashboard"), BorderLayout.NORTH);

        // ── Stat cards ───────────────────────────────────────────────────────────
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setBorder(new EmptyBorder(8, 20, 16, 20));

        totalCustomersValue = new JLabel("0");
        totalAccountsValue  = new JLabel("0");
        totalBalanceValue   = new JLabel("0");

        statsRow.add(statCard("Total Customers", totalCustomersValue, new Color(0x6366F1)));
        statsRow.add(statCard("Total Accounts",  totalAccountsValue,  new Color(0x0EA5E9)));
        statsRow.add(statCard("Total Accounts Balance (USD equiv.)", totalBalanceValue, new Color(0x10B981)));

        // ── Recent transactions table ─────────────────────────────────────────
        String[] cols = {"Date / Time", "Type", "Amount", "Currency", "Status", "Description"};
        recentModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(recentModel);
        MainWindow.styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(0, 20, 20, 20));

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        JLabel recentLabel = new JLabel("Recent Transactions");
        recentLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        recentLabel.setBorder(new EmptyBorder(0, 20, 8, 0));
        center.add(recentLabel, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);

        add(statsRow, BorderLayout.NORTH);
        add(center,   BorderLayout.CENTER);
    }

    private JPanel statCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE2E8F0)),
                new EmptyBorder(16, 20, 16, 20)));

        JLabel t = new JLabel(title);
        t.setFont(MainWindow.FONT_BODY);
        t.setForeground(new Color(0x64748B));

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        valueLabel.setForeground(accent);

        card.add(t,          BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    public void refresh() {
        int customers = customerService.getTotalCustomers();
        List<Account> accounts = accountService.getAllAccounts();
        BigDecimal total = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalCustomersValue.setText(String.valueOf(customers));
        totalAccountsValue.setText(String.valueOf(accounts.size()));
        totalBalanceValue.setText(String.format("%,.2f", total));

        recentModel.setRowCount(0);
        transactionService.getRecentTransactions(20).forEach(t -> recentModel.addRow(new Object[]{
                t.getTimestamp().toString().substring(0, 19),
                t.getType(), String.format("%.2f", t.getAmount()),
                t.getCurrency(), t.getStatus(),
                t.getDescription()
        }));
    }
}
