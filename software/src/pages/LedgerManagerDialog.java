package pages;

import backend.LedgerContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class LedgerManagerDialog extends JDialog {
    private Runnable onLedgerChanged;

    public LedgerManagerDialog(JFrame parent, Runnable onLedgerChanged) {
        super(parent, "Ledger Manager", true);
        this.onLedgerChanged = onLedgerChanged;
        setSize(350, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        add(createContentPanel(), BorderLayout.CENTER);
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel generalLabel = new JLabel("General Ledger");
        generalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        generalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(generalLabel);
        panel.add(Box.createVerticalStrut(10));
        addLedgerButtons(panel, "general");
        panel.add(Box.createVerticalStrut(20));

        JLabel personalLabel = new JLabel("Personal Ledger");
        personalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        personalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(personalLabel);
        panel.add(Box.createVerticalStrut(10));
        addLedgerButtons(panel, "personal");
        JButton addBtn = new JButton("+ Add Ledger");
        addBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        addBtn.addActionListener(e -> addLedgerDialog());
        panel.add(addBtn);
        return panel;
    }

    private void addLedgerButtons(JPanel panel, String type) {
        List<String[]> ledgers = LedgerContext.getAllLedgers();
        String current = LedgerContext.getCurrentLedger();
        String currentType = LedgerContext.getCurrentLedgerType();
        for (String[] ledger : ledgers) {
            if (!ledger[1].equals(type)) continue;
            JPanel row = new JPanel(new BorderLayout());
            row.setBackground(new Color(245, 245, 255));
            JButton btn = new JButton(ledger[0]);
            btn.setFocusPainted(false);
            btn.setBackground(current.equals(ledger[0]) && currentType.equals(type) ? new Color(220, 230, 255) : Color.WHITE);
            btn.setFont(new Font("Arial", Font.PLAIN, 15));
            btn.setPreferredSize(new Dimension(160, 36));
            btn.addActionListener(e -> {
                LedgerContext.setCurrentLedger(ledger[0], ledger[1]);
                if (onLedgerChanged != null) onLedgerChanged.run();
                dispose();
            });
            row.add(btn, BorderLayout.CENTER);
            if (type.equals("personal")) {
                // 右侧加重命名、删除按钮
                JPanel ops = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 2));
                ops.setOpaque(false);
                JButton renameBtn = new JButton("Rename");
                renameBtn.setFont(new Font("Arial", Font.PLAIN, 12));
                renameBtn.addActionListener(e -> renameLedgerDialog(ledger[0]));
                JButton delBtn = new JButton("Delete");
                delBtn.setFont(new Font("Arial", Font.PLAIN, 12));
                delBtn.addActionListener(e -> {
                    int ok = JOptionPane.showConfirmDialog(this, "Delete ledger '"+ledger[0]+"'?", "Confirm", JOptionPane.YES_NO_OPTION);
                    if (ok == JOptionPane.YES_OPTION) {
                        LedgerContext.deleteLedger(ledger[0]);
                        if (onLedgerChanged != null) onLedgerChanged.run();
                        dispose();
                    }
                });
                ops.add(renameBtn);
                ops.add(delBtn);
                row.add(ops, BorderLayout.EAST);
            }
            panel.add(row);
            panel.add(Box.createVerticalStrut(6));
        }
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