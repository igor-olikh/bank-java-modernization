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

public class LoansPanel extends JPanel {

    private final LoanService     loanService;
    private final AccountService  accountService;
    private final CustomerService customerService;
    private final MainWindow      owner;

    private JComboBox<String> customerCombo;
    private List<Customer>    customerList;
    private List<Loan>        loanList;
    private DefaultTableModel model;

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
        customerCombo.setPreferredSize(new Dimension(220, 30));

        JButton loadBtn  = MainWindow.primaryButton("Load Loans");
        JButton applyBtn = MainWindow.successButton("Apply for Loan");
        JButton repayBtn = MainWindow.primaryButton("Make Repayment");

        actions.add(new JLabel("Customer:"));
        actions.add(customerCombo);
        actions.add(loadBtn);
        actions.add(applyBtn);
        actions.add(repayBtn);
        top.add(actions, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        String[] cols = {"Type", "Principal", "Outstanding", "Monthly Pay", "Rate %", "Term", "End Date", "Status"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        MainWindow.styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(0, 20, 20, 20));
        add(scroll, BorderLayout.CENTER);

        populateCustomerCombo();
        loadBtn.addActionListener(e -> loadLoans(table));
        applyBtn.addActionListener(e -> applyLoan(table));
        repayBtn.addActionListener(e -> makeRepayment(table));
    }

    private void populateCustomerCombo() {
        customerList = customerService.getAllCustomers();
        customerCombo.removeAllItems();
        customerList.forEach(c -> customerCombo.addItem(c.getFullName() + " [" + c.getCustomerId().substring(0, 8) + "]"));
    }

    private void loadLoans(JTable table) {
        int idx = customerCombo.getSelectedIndex();
        if (idx < 0) return;
        Customer c = customerList.get(idx);
        loanList = loanService.getLoansByCustomer(c.getCustomerId());
        model.setRowCount(0);
        loanList.forEach(l -> model.addRow(new Object[]{
                l.getLoanType(),
                String.format("%.2f", l.getPrincipalAmount()),
                String.format("%.2f", l.getOutstandingBalance()),
                String.format("%.2f", l.getMonthlyPayment()),
                l.getInterestRate(), l.getTermMonths() + " mo",
                l.getEndDate(), l.getStatus()
        }));
        owner.setStatus("Loans for " + c.getFullName() + ": " + model.getRowCount());
    }

    private void applyLoan(JTable table) {
        int idx = customerCombo.getSelectedIndex();
        if (idx < 0) { MainWindow.showError(this, "Select a customer first."); return; }
        Customer c = customerList.get(idx);
        List<Account> accounts = accountService.getAccountsByCustomer(c.getCustomerId());
        if (accounts.isEmpty()) { MainWindow.showError(this, "Customer has no accounts."); return; }

        String[] accItems = accounts.stream().map(a -> a.getAccountNumber() + " (" + a.getCurrency() + ")").toArray(String[]::new);
        JComboBox<String> accCombo  = new JComboBox<>(accItems);
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"PERSONAL", "MORTGAGE", "AUTO", "BUSINESS"});
        JTextField amountField = new JTextField("10000.00", 12);
        JTextField rateField   = new JTextField("8.00", 8);
        JTextField termField   = new JTextField("36", 6);

        Object[][] rows = {{"Account:", accCombo}, {"Loan type:", typeCombo},
                {"Amount:", amountField}, {"Annual rate (%):", rateField}, {"Term (months):", termField}};
        if (JOptionPane.showConfirmDialog(this, CustomersPanel.buildForm(rows), "Apply for Loan",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        try {
            Account acc = accounts.get(accCombo.getSelectedIndex());
            Loan loan = loanService.applyForLoan(c.getCustomerId(), acc.getAccountId(),
                    LoanType.valueOf(typeCombo.getSelectedItem().toString()),
                    new BigDecimal(amountField.getText().trim()),
                    new BigDecimal(rateField.getText().trim()),
                    Integer.parseInt(termField.getText().trim()));
            loadLoans(table);
            MainWindow.showInfo(this, "Loan approved! Monthly payment: " +
                    String.format("%.2f", loan.getMonthlyPayment()) + "  End: " + loan.getEndDate());
        } catch (Exception ex) { MainWindow.showError(this, ex.getMessage()); }
    }

    private void makeRepayment(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0 || loanList == null || row >= loanList.size()) {
            MainWindow.showError(this, "Select a loan first."); return;
        }
        Loan loan = loanList.get(row);
        int idx = customerCombo.getSelectedIndex();
        Customer c = customerList.get(idx);
        List<Account> accounts = accountService.getAccountsByCustomer(c.getCustomerId());
        String[] accItems = accounts.stream().map(a -> a.getAccountNumber() + " (" + a.getCurrency() + ")").toArray(String[]::new);
        JComboBox<String> accCombo = new JComboBox<>(accItems);
        JTextField amountField = new JTextField(String.format("%.2f", loan.getMonthlyPayment()), 12);

        Object[][] rows = {{"Debit account:", accCombo}, {"Amount:", amountField}};
        if (JOptionPane.showConfirmDialog(this, CustomersPanel.buildForm(rows), "Make Repayment",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        try {
            Account acc = accounts.get(accCombo.getSelectedIndex());
            Loan updated = loanService.makeRepayment(loan.getLoanId(), acc.getAccountId(),
                    new BigDecimal(amountField.getText().trim()));
            loadLoans(table);
            MainWindow.showInfo(this, "Repayment recorded. Outstanding: " +
                    String.format("%.2f", updated.getOutstandingBalance()) + "  Status: " + updated.getStatus());
        } catch (Exception ex) { MainWindow.showError(this, ex.getMessage()); }
    }
}
