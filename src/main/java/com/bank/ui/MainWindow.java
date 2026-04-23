package com.bank.ui;

import com.bank.config.BankConfig;
import com.bank.service.*;
import com.bank.ui.panels.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Main application window.
 * Sidebar navigation + CardLayout content area.
 */
public class MainWindow extends JFrame {

    // ── Theme colours ──────────────────────────────────────────────────────────
    public static final Color HEADER_BG      = new Color(0x0F172A);
    public static final Color SIDEBAR_BG     = new Color(0x1E293B);
    public static final Color SIDEBAR_TEXT   = new Color(0xE2E8F0);
    public static final Color SIDEBAR_ACTIVE = new Color(0x3B82F6);
    public static final Color SIDEBAR_HOVER  = new Color(0x334155);
    public static final Color CONTENT_BG     = new Color(0xF8FAFC);
    public static final Color ACCENT         = new Color(0x3B82F6);
    public static final Color SUCCESS        = new Color(0x22C55E);
    public static final Color DANGER         = new Color(0xEF4444);
    public static final Color TABLE_HEADER   = new Color(0x334155);
    public static final Color TABLE_ROW_ALT  = new Color(0xF1F5F9);
    public static final Font  FONT_HEADER    = new Font("SansSerif", Font.BOLD,   22);
    public static final Font  FONT_SLOGAN    = new Font("SansSerif", Font.ITALIC, 12);
    public static final Font  FONT_NAV       = new Font("SansSerif", Font.PLAIN,  14);
    public static final Font  FONT_LABEL     = new Font("SansSerif", Font.BOLD,   13);
    public static final Font  FONT_BODY      = new Font("SansSerif", Font.PLAIN,  13);

    private static final String[] NAV_ITEMS = {
            "Dashboard", "Customers", "Accounts", "Transactions", "Cards", "Loans", "Branches"
    };

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel     contentPanel = new JPanel(cardLayout);
    private final JLabel     statusLabel  = new JLabel("  Ready");
    private final ButtonGroup navGroup    = new ButtonGroup();

    // Services passed through to panels
    private final CustomerService    customerService;
    private final AccountService     accountService;
    private final TransactionService transactionService;
    private final CardService        cardService;
    private final LoanService        loanService;
    private final BranchService      branchService;

    public MainWindow(CustomerService cs, AccountService as, TransactionService ts,
                      CardService cds, LoanService ls, BranchService bs) {
        this.customerService    = cs;
        this.accountService     = as;
        this.transactionService = ts;
        this.cardService        = cds;
        this.loanService        = ls;
        this.branchService      = bs;

        setTitle(BankConfig.BANK_NAME + " — Banking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1024, 640));
        setLocationRelativeTo(null);

        buildUI();
        navigate("Dashboard");
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);
        add(buildContent(), BorderLayout.CENTER);
        add(buildStatus(),  BorderLayout.SOUTH);
    }

    // ── Header ─────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(new EmptyBorder(0, 20, 0, 20));

        JLabel nameLabel = new JLabel(BankConfig.BANK_NAME);
        nameLabel.setFont(FONT_HEADER);
        nameLabel.setForeground(Color.WHITE);

        JLabel sloganLabel = new JLabel("  " + BankConfig.BANK_SLOGAN);
        sloganLabel.setFont(FONT_SLOGAN);
        sloganLabel.setForeground(new Color(0x94A3B8));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(nameLabel);
        left.add(sloganLabel);

        JLabel version = new JLabel("v" + BankConfig.APP_VERSION + "  ");
        version.setFont(FONT_BODY);
        version.setForeground(new Color(0x64748B));

        header.add(left,    BorderLayout.CENTER);
        header.add(version, BorderLayout.EAST);
        return header;
    }

    // ── Sidebar ────────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(190, 0));
        sidebar.setBorder(new EmptyBorder(16, 0, 16, 0));

        for (String item : NAV_ITEMS) {
            JToggleButton btn = createNavButton(item);
            navGroup.add(btn);
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(2));
        }
        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private JToggleButton createNavButton(String label) {
        JToggleButton btn = new JToggleButton(label);
        btn.setFont(FONT_NAV);
        btn.setForeground(SIDEBAR_TEXT);
        btn.setBackground(SIDEBAR_BG);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(10, 20, 10, 10));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(190, 44));
        btn.setPreferredSize(new Dimension(190, 44));

        btn.addActionListener(e -> navigate(label));

        btn.addChangeListener(e -> {
            if (btn.isSelected()) {
                btn.setBackground(SIDEBAR_ACTIVE);
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(SIDEBAR_BG);
                btn.setForeground(SIDEBAR_TEXT);
            }
        });
        return btn;
    }

    // ── Content ────────────────────────────────────────────────────────────────
    private JPanel buildContent() {
        contentPanel.setBackground(CONTENT_BG);
        contentPanel.add(new DashboardPanel(customerService, accountService, transactionService), "Dashboard");
        contentPanel.add(new CustomersPanel(customerService, this),                               "Customers");
        contentPanel.add(new AccountsPanel(accountService, customerService, this),               "Accounts");
        contentPanel.add(new TransactionsPanel(transactionService, accountService, customerService, this), "Transactions");
        contentPanel.add(new CardsPanel(cardService, accountService, customerService, this),     "Cards");
        contentPanel.add(new LoansPanel(loanService, accountService, customerService, this),     "Loans");
        contentPanel.add(new BranchesPanel(branchService),                                       "Branches");
        return contentPanel;
    }

    // ── Status bar ─────────────────────────────────────────────────────────────
    private JPanel buildStatus() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(0xE2E8F0));
        bar.setBorder(new EmptyBorder(3, 10, 3, 10));
        bar.setPreferredSize(new Dimension(0, 26));
        statusLabel.setFont(FONT_BODY);
        statusLabel.setForeground(new Color(0x475569));
        bar.add(statusLabel, BorderLayout.WEST);

        JLabel support = new JLabel(BankConfig.SUPPORT_EMAIL + "  |  " + BankConfig.SUPPORT_PHONE + "  ");
        support.setFont(FONT_BODY);
        support.setForeground(new Color(0x64748B));
        bar.add(support, BorderLayout.EAST);
        return bar;
    }

    // ── Navigation ─────────────────────────────────────────────────────────────
    public void navigate(String panel) {
        cardLayout.show(contentPanel, panel);
        setStatus("Section: " + panel);
        // Refresh dashboard stats when returning to it
        if ("Dashboard".equals(panel)) {
            Component c = contentPanel.getComponent(0);
            if (c instanceof DashboardPanel) ((DashboardPanel) c).refresh();
        }
    }

    public void setStatus(String message) {
        statusLabel.setText("  " + message);
    }

    // ── Utility helpers (shared across panels) ─────────────────────────────────

    /** Styled action button (blue). */
    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY);
        btn.setBackground(ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(6, 16, 6, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /** Styled danger button (red). */
    public static JButton dangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY);
        btn.setBackground(DANGER);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(6, 16, 6, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /** Styled success button (green). */
    public static JButton successButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY);
        btn.setBackground(SUCCESS);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(6, 16, 6, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /** Styled section title label. */
    public static JLabel sectionTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        lbl.setForeground(new Color(0x1E293B));
        lbl.setBorder(new EmptyBorder(16, 20, 8, 20));
        return lbl;
    }

    /** Applies standard table styling. */
    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(30);
        table.setGridColor(new Color(0xE2E8F0));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.getTableHeader().setFont(FONT_LABEL);
        table.getTableHeader().setBackground(TABLE_HEADER);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(0xBFDBFE));
        table.setSelectionForeground(new Color(0x1E293B));
        table.setDefaultEditor(Object.class, null); // read-only
    }

    /** Standard combo-box populated from a string array. */
    public static JComboBox<String> styledCombo(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(FONT_BODY);
        return combo;
    }

    /** Shows an error dialog. */
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Shows an info dialog. */
    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Prompts for a string value. Returns null if cancelled. */
    public static String prompt(Component parent, String message) {
        return JOptionPane.showInputDialog(parent, message);
    }
}
