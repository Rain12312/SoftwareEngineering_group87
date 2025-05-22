package pages;

import backend.LedgerContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class PersonalLedgerManagerDialog extends JDialog {
    private Runnable onLedgerChanged;

    public PersonalLedgerManagerDialog(JFrame parent, Runnable onLedgerChanged) {
        super(parent, "Manage Personal Ledgers", true);
        this.onLedgerChanged = onLedgerChanged;
        setSize(320, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        add(createContentPanel(), BorderLayout.CENTER);
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 20));

        JLabel title = new JLabel("Personal Ledgers");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panel.add(title);
        panel.add(Box.createVerticalStrut(15));

        List<String[]> ledgers = LedgerContext.getAllLedgers();
        for (String[] ledger : ledgers) {
            if (!ledger[1].equals("personal")) continue;
            JPanel row = new JPanel(new BorderLayout());
            row.setBackground(new Color(245, 245, 255));
            row.setAlignmentX(Component.LEFT_ALIGNMENT);
            String ledgerName = ledger[0].substring(0,1).toUpperCase() + ledger[0].substring(1);
            JLabel nameLabel = new JLabel(ledgerName);
            nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
            row.add(nameLabel, BorderLayout.WEST);
            row.add(Box.createHorizontalStrut(400), BorderLayout.CENTER);
            JPanel btnPanel = new JPanel();
            btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
            btnPanel.setOpaque(false);
            JButton renameBtn = new JButton("Rename");
            renameBtn.setFont(new Font("Arial", Font.PLAIN, 12));
            renameBtn.setFocusPainted(false);
            renameBtn.setContentAreaFilled(true);
            renameBtn.setBackground(UIManager.getColor("Button.background"));
            renameBtn.setBorderPainted(true);
            renameBtn.addActionListener(e -> renameLedgerDialog(ledger[0]));
            JButton delBtn = new JButton("Delete");
            delBtn.setFont(new Font("Arial", Font.PLAIN, 12));
            delBtn.setFocusPainted(false);
            delBtn.setContentAreaFilled(true);
            delBtn.setBackground(UIManager.getColor("Button.background"));
            delBtn.setBorderPainted(true);
            delBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    delBtn.setBackground(new Color(240, 180, 180));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    delBtn.setBackground(UIManager.getColor("Button.background"));
                }
            });
            delBtn.addActionListener(e -> {
                int ok = JOptionPane.showConfirmDialog(this, "Delete ledger '"+ledger[0]+"'?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (ok == JOptionPane.YES_OPTION) {
                    LedgerContext.deleteLedger(ledger[0]);
                    if (onLedgerChanged != null) onLedgerChanged.run();
                    dispose();
                }
            });
            btnPanel.add(renameBtn);
            btnPanel.add(Box.createHorizontalStrut(8));
            btnPanel.add(delBtn);
            row.add(btnPanel, BorderLayout.EAST);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
            panel.add(row);
            panel.add(Box.createVerticalStrut(8));
            JSeparator sep = new JSeparator();
            sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            panel.add(sep);
            panel.add(Box.createVerticalStrut(4));
        }

        JButton addBtn = new JButton("+ Add Ledger");
        addBtn.setFont(new Font("Arial", Font.BOLD, 14));
        addBtn.setBackground(new Color(220, 230, 255));
        addBtn.setFocusPainted(false);
        addBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addBtn.setPreferredSize(new Dimension(200, 40));
        addBtn.setMaximumSize(new Dimension(200, 40));
        addBtn.addActionListener(e -> addLedgerDialog());
        panel.add(Box.createVerticalStrut(15));
        panel.add(addBtn);
        return panel;
    }

    private void addLedgerDialog() {
        String name = JOptionPane.showInputDialog(this, "Enter new ledger name:");
        if (name != null && !name.trim().isEmpty()) {
            LedgerContext.addLedger(name.trim(), "personal");
            if (onLedgerChanged != null) onLedgerChanged.run();
            dispose();
        }
    }

    private void renameLedgerDialog(String oldName) {
        String name = JOptionPane.showInputDialog(this, "Rename ledger:", oldName);
        if (name != null && !name.trim().isEmpty() && !name.equals(oldName)) {
            LedgerContext.renameLedger(oldName, name.trim());
            if (onLedgerChanged != null) onLedgerChanged.run();
            dispose();
        }
    }
} 