package com.bank.ui.panels;

import com.bank.model.Account;
import com.bank.model.Customer;
import com.bank.model.Loan;
import com.bank.model.enums.LoanType;
import com.bank.service.AccountService;
import com.bank.service.CustomerService;
import com.bank.service.LoanService;
import com.bank.ui.MainWindow;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Loans panel — auto-loads on customer select.
 */
public class LoansPanel extends JPanel {

    private final LoanService     loanService;
    private final AccountService  accountService;
    private final CustomerService customerService;
    private final MainWindow      owner;

    private JComboBox<String> customerCombo;
    private List<Customer>    customerList;
    private List<Loan>        loanList;
    private DefaultTableModel model;
    private JTable            table;

    public LoansPanel(LoanService ls, AccountService as, CustomerService cs, MainWindow owner) {
        this.loanService     = ls;
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
        top.add(MainWindow.sectionTitle("Loans"), BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 14));
        actions.setOpaque(false);
        customerCombo = new JComboBox<>();
        customerCombo.setFont(MainWindow.FONT_BODY);
        customerCombo.setPreferredSize(new Dimension(240, 30));

        JButton applyBtn = MainWindow.successButton("Apply for Loan");
        JButton repayBtn = MainWindow.primaryButton("Make Repayment");

        actions.add(new JLabel("Customer:"));
        actions.add(customerCombo);
        actions.add(applyBtn);
        actions.add(repayBtn);
        top.add(actions, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        String[] cols = {"Type", "Principal", "Outstanding", "Monthly Pay", "Rate %", "Term", "End Date", "Status"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        MainWindow.styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(0, 20, 20, 20));
        add(scroll, BorderLayout.CENTER);

        populateCustomerCombo();
        customerCombo.addActionListener(e -> loadLoans());
        applyBtn.addActionListener(e -> applyLoan());
        repayBtn.addActionListener(e -> makeRepayment());
    }

    private void populateCustomerCombo() {
        customerList = customerService.getAllCustomers();
        customerCombo.removeAllItems();
        customerCombo.addItem("— Select customer —");
        customerList.forEach(c -> customerCombo.addItem(c.getFullName() + " [" + c.getCustomerId().substring(0, 8) + "]"));
        customerCombo.setSelectedIndex(0);
    }

    private void loadLoans() {
        int idx = customerCombo.getSelectedIndex();
        if (idx <= 0 || customerList == null) { model.setRowCount(0); loanList = null; return; }
        Customer c = customerList.get(idx - 1);
        loanList = loanService.getLoansByCustomer(c.getCustomerId());
        model.setRowCount(0);
        loanList.forEach(l -> model.addRow(new Object[]{
                l.getLoanType(),
                String.format("%,.2f", l.getPrincipalAmount()),
                String.format("%,.2f", l.getOutstandingBalance()),
                String.format("%,.2f", l.getMonthlyPayment()),
                l.getInterestRate(), l.getTermMonths() + " mo",
                l.getEndDate(), l.getStatus()
        }));
        owner.setStatus("Loans for " + c.getFullName() + ": " + model.getRowCount());
    }

    private void applyLoan() {
        int idx = customerCombo.getSelectedIndex();
        if (idx <= 0) { MainWindow.showError(this, "Select a customer first."); return; }
        Customer c = customerList.get(idx - 1);
        List<Account> accounts = accountService.getAccountsByCustomer(c.getCustomerId());
        if (accounts.isEmpty()) { MainWindow.showError(this, "Customer has no accounts. Open an account first."); return; }

        String[] accItems = accounts.stream()
                .map(a -> a.getAccountNumber() + "  (" + a.getCurrency() + " · Balance: " + String.format("%,.2f", a.getBalance()) + ")")
                .toArray(String[]::new);
        JComboBox<String> accCombo  = new JComboBox<>(accItems);
        accCombo.setFont(MainWindow.FONT_BODY);
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"PERSONAL", "MORTGAGE", "AUTO", "BUSINESS"});
        JTextField amountField = new JTextField("10000.00", 14);
        JTextField rateField   = new JTextField("8.00", 8);
        JTextField termField   = new JTextField("36", 6);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(new EmptyBorder(10, 10, 10, 10));
        addRow(form, "Customer:", c.getFullName());
        addRow(form, "Disbursement account:", accCombo);
        addRow(form, "Loan type:", typeCombo);
        addRow(form, "Principal amount:", amountField);
        addRow(form, "Annual interest rate (%):", rateField);
        addRow(form, "Term (months):", termField);

        int res = JOptionPane.showConfirmDialog(this, form, "Apply for Loan",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;
        try {
            Account acc = accounts.get(accCombo.getSelectedIndex());
            Loan loan = loanService.applyForLoan(c.getCustomerId(), acc.getAccountId(),
                    LoanType.valueOf(typeCombo.getSelectedItem().toString()),
                    new BigDecimal(amountField.getText().trim()),
                    new BigDecimal(rateField.getText().trim()),
                    Integer.parseInt(termField.getText().trim()));
            loadLoans();
            MainWindow.showInfo(this, String.format(
                    "Loan approved!\n\nMonthly payment: %,.2f\nEnd date: %s\nAccount credited: %s",
                    loan.getMonthlyPayment(), loan.getEndDate(), acc.getAccountNumber()));
        } catch (Exception ex) { MainWindow.showError(this, ex.getMessage()); }
    }

    private void makeRepayment() {
        int row = table.getSelectedRow();
        if (row < 0 || loanList == null || row >= loanList.size()) {
            MainWindow.showError(this, "Select a loan from the table first."); return;
        }
        Loan loan = loanList.get(row);
        int idx = customerCombo.getSelectedIndex();
        Customer c = customerList.get(idx - 1);
        List<Account> accounts = accountService.getAccountsByCustomer(c.getCustomerId());
        if (accounts.isEmpty()) { MainWindow.showError(this, "No accounts to debit."); return; }

        String[] accItems = accounts.stream()
                .map(a -> a.getAccountNumber() + "  (Balance: " + String.format("%,.2f", a.getBalance()) + " " + a.getCurrency() + ")")
                .toArray(String[]::new);
        JComboBox<String> accCombo = new JComboBox<>(accItems);
        accCombo.setFont(MainWindow.FONT_BODY);
        JTextField amountField = new JTextField(String.format("%.2f", loan.getMonthlyPayment()), 14);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(new EmptyBorder(10, 10, 10, 10));
        addRow(form, "Loan type:", loan.getLoanType().toString());
        addRow(form, "Outstanding balance:", String.format("%,.2f", loan.getOutstandingBalance()));
        addRow(form, "Monthly payment:", String.format("%,.2f", loan.getMonthlyPayment()));
        addRow(form, "Debit account:", accCombo);
        addRow(form, "Repayment amount:", amountField);

        int res = JOptionPane.showConfirmDialog(this, form, "Make Loan Repayment",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;
        try {
            Account acc = accounts.get(accCombo.getSelectedIndex());
            Loan updated = loanService.makeRepayment(loan.getLoanId(), acc.getAccountId(),
                    new BigDecimal(amountField.getText().trim()));
            loadLoans();
            MainWindow.showInfo(this, String.format(
                    "Repayment recorded.\nRemaining balance: %,.2f\nLoan status: %s",
                    updated.getOutstandingBalance(), updated.getStatus()));
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
