package com.bank.ui.panels;

import com.bank.model.Customer;
import com.bank.model.Address;
import com.bank.model.enums.CustomerType;
import com.bank.service.CustomerService;
import com.bank.ui.MainWindow;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Customers panel — list, search, add, view, block, activate.
 */
public class CustomersPanel extends JPanel {

    private final CustomerService customerService;
    private final MainWindow      owner;
    private DefaultTableModel     model;
    private JTable                table;
    private JTextField            searchField;

    public CustomersPanel(CustomerService cs, MainWindow owner) {
        this.customerService = cs;
        this.owner = owner;
        setLayout(new BorderLayout());
        setBackground(MainWindow.CONTENT_BG);
        build();
    }

    private void build() {
        // ── Top bar ─────────────────────────────────────────────────────────────
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(MainWindow.sectionTitle("Customers"), BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 14));
        actions.setOpaque(false);
        searchField = new JTextField(16);
        searchField.setFont(MainWindow.FONT_BODY);
        searchField.setToolTipText("Search by name");

        JButton searchBtn = MainWindow.primaryButton("Search");
        JButton addBtn    = MainWindow.successButton("+ Add Customer");
        JButton viewBtn   = MainWindow.primaryButton("View Details");
        JButton blockBtn  = MainWindow.dangerButton("Block");
        JButton activeBtn = MainWindow.primaryButton("Activate");
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(MainWindow.FONT_BODY);

        actions.add(searchField);
        actions.add(searchBtn);
        actions.add(addBtn);
        actions.add(viewBtn);
        actions.add(blockBtn);
        actions.add(activeBtn);
        actions.add(refreshBtn);
        top.add(actions, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        // ── Table ────────────────────────────────────────────────────────────────
        String[] cols = {"ID", "Full Name", "Type", "Nationality", "Email", "Phone", "Status", "Member Since"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        MainWindow.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(0, 20, 20, 20));
        add(scroll, BorderLayout.CENTER);

        // ── Wire actions ─────────────────────────────────────────────────────────
        refreshBtn.addActionListener(e -> loadAll());
        searchBtn.addActionListener(e -> search());
        searchField.addActionListener(e -> search());
        addBtn.addActionListener(e -> showAddDialog());
        viewBtn.addActionListener(e -> viewSelected());
        blockBtn.addActionListener(e -> blockSelected());
        activeBtn.addActionListener(e -> activateSelected());

        loadAll();
    }

    private void loadAll() {
        model.setRowCount(0);
        customerService.getAllCustomers().forEach(this::addRow);
        owner.setStatus("Customers loaded: " + model.getRowCount());
    }

    private void search() {
        String q = searchField.getText().trim();
        model.setRowCount(0);
        if (q.isEmpty()) { loadAll(); return; }
        customerService.searchByName(q).forEach(this::addRow);
    }

    private void addRow(Customer c) {
        model.addRow(new Object[]{
                c.getCustomerId().substring(0, 8) + "...",
                c.getFullName(), c.getCustomerType(), c.getNationality(),
                c.getEmail(), c.getPhone(), c.getStatus(),
                c.getCreatedAt().toLocalDate()
        });
    }

    private Customer selectedCustomer() {
        int row = table.getSelectedRow();
        if (row < 0) { MainWindow.showError(this, "Please select a customer first."); return null; }
        String partial = model.getValueAt(row, 0).toString();
        List<Customer> all = customerService.getAllCustomers();
        return all.stream().filter(c -> c.getCustomerId().startsWith(partial.replace("...", ""))).findFirst().orElse(null);
    }

    private void viewSelected() {
        Customer c = selectedCustomer();
        if (c == null) return;
        String info = "<html><b>ID:</b> " + c.getCustomerId() + "<br>" +
                "<b>Name:</b> " + c.getFullName() + "<br>" +
                "<b>DOB:</b> " + c.getDateOfBirth() + "<br>" +
                "<b>Nationality:</b> " + c.getNationality() + "<br>" +
                "<b>Type:</b> " + c.getCustomerType() + "<br>" +
                "<b>Status:</b> " + c.getStatus() + "<br>" +
                "<b>Email:</b> " + c.getEmail() + "<br>" +
                "<b>Phone:</b> " + c.getPhone() + "<br>" +
                "<b>Address:</b> " + c.getAddress() + "<br>" +
                "<b>Member since:</b> " + c.getCreatedAt().toLocalDate() + "</html>";
        JOptionPane.showMessageDialog(this, info, "Customer Profile", JOptionPane.PLAIN_MESSAGE);
    }

    private void blockSelected() {
        Customer c = selectedCustomer();
        if (c == null) return;
        try { customerService.blockCustomer(c.getCustomerId()); loadAll(); MainWindow.showInfo(this, "Customer blocked."); }
        catch (Exception ex) { MainWindow.showError(this, ex.getMessage()); }
    }

    private void activateSelected() {
        Customer c = selectedCustomer();
        if (c == null) return;
        try { customerService.activateCustomer(c.getCustomerId()); loadAll(); MainWindow.showInfo(this, "Customer activated."); }
        catch (Exception ex) { MainWindow.showError(this, ex.getMessage()); }
    }

    private void showAddDialog() {
        JTextField first    = new JTextField(14);
        JTextField last     = new JTextField(14);
        JTextField dob      = new JTextField("1990-01-01", 14);
        JTextField email    = new JTextField(14);
        JTextField phone    = new JTextField(14);
        JTextField street   = new JTextField(14);
        JTextField city     = new JTextField(14);
        JTextField state    = new JTextField(14);
        JTextField postal   = new JTextField(14);
        JTextField country  = new JTextField(14);
        JTextField national = new JTextField(14);
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"INDIVIDUAL", "CORPORATE"});

        Object[][] rows = {
                {"First name:", first},   {"Last name:", last},
                {"Date of birth:", dob},  {"Email:", email},
                {"Phone:", phone},        {"Street:", street},
                {"City:", city},          {"State:", state},
                {"Postal code:", postal}, {"Country:", country},
                {"Nationality:", national}, {"Type:", typeCombo}
        };
        JPanel form = buildForm(rows);

        int res = JOptionPane.showConfirmDialog(this, form, "Register New Customer",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;
        try {
            customerService.registerCustomer(
                    first.getText(), last.getText(), LocalDate.parse(dob.getText()),
                    email.getText(), phone.getText(),
                    new Address(street.getText(), city.getText(), state.getText(), postal.getText(), country.getText()),
                    national.getText(), CustomerType.valueOf(typeCombo.getSelectedItem().toString()));
            loadAll();
            MainWindow.showInfo(this, "Customer registered successfully.");
        } catch (Exception ex) { MainWindow.showError(this, ex.getMessage()); }
    }

    static JPanel buildForm(Object[][] rows) {
        JPanel form = new JPanel(new GridLayout(rows.length, 2, 8, 6));
        form.setBorder(new EmptyBorder(10, 10, 10, 10));
        for (Object[] row : rows) {
            JLabel lbl = new JLabel(row[0].toString());
            lbl.setFont(MainWindow.FONT_BODY);
            form.add(lbl);
            if (row[1] instanceof JComponent) form.add((JComponent) row[1]);
        }
        return form;
    }
}
