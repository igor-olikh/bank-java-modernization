package com.bank.ui.panels;

import com.bank.service.BranchService;
import com.bank.ui.MainWindow;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class BranchesPanel extends JPanel {

    private final BranchService branchService;

    public BranchesPanel(BranchService bs) {
        this.branchService = bs;
        setLayout(new BorderLayout());
        setBackground(MainWindow.CONTENT_BG);
        build();
    }

    private void build() {
        add(MainWindow.sectionTitle("Branch Information"), BorderLayout.NORTH);

        String[] cols = {"Code", "Name", "Address", "Phone", "Manager", "Opening Hours"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        branchService.getAllBranches().forEach(b -> model.addRow(new Object[]{
                b.getBranchCode(), b.getName(), b.getAddress(),
                b.getPhone(), b.getManager(), b.getOpeningHours()
        }));

        JTable table = new JTable(model);
        MainWindow.styleTable(table);
        table.getColumnModel().getColumn(2).setPreferredWidth(260);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(0, 20, 20, 20));
        add(scroll, BorderLayout.CENTER);
    }
}
