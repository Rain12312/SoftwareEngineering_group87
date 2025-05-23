package components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import pages.PersonalLedgerManagerDialog;
import backend.LedgerContext;
import backend.Transaction;
import backend.TransactionService;
import java.util.List;

public class Sidebar extends JPanel {
    private static final Color BACKGROUND_COLOR = new Color(235, 235, 255);
    private static final Color CARD_COLOR = new Color(220, 230, 255);  // 淡蓝色
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color CHECK_COLOR = new Color(0, 51, 153);  // 深蓝色
    private static final Color DAILY_LEDGER_COLOR = new Color(210, 230, 255);
    private static final Color LOVE_LEDGER_COLOR = new Color(230, 220, 255);
    private static final Color DAILY_LEDGER_SELECTED_COLOR = new Color(180, 210, 255);
    private static final Color LOVE_LEDGER_SELECTED_COLOR = new Color(210, 190, 255);
    private static final int SIDEBAR_WIDTH = 260;
    
    private boolean isDailyLedgerSelected = false;
    private boolean isLoveLedgerSelected = false;
    private JPanel dailyLedgerPanel;
    private JPanel loveLedgerPanel;

    public Sidebar(int height, ActionListener actionListener) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(BACKGROUND_COLOR);
        setPreferredSize(new Dimension(SIDEBAR_WIDTH, height));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // General Ledger统计区块
        add(createGeneralLedgerStatsPanel());
        add(Box.createVerticalStrut(10));

        // Personal Ledger分组区块
        add(createPersonalLedgerHeaderPanel());
        add(Box.createVerticalStrut(8));

        // 渲染所有账本列表
        renderLedgerList();

        add(Box.createVerticalGlue());
    }

    private JPanel createGeneralLedgerStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(230, 240, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel titleLabel = new JLabel("<html><div style='text-align:center;'>General Ledger</div></html>");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(8));
        JPanel valuesPanel = new JPanel(new GridLayout(2, 3, 5, 0));
        valuesPanel.setBackground(new Color(230, 240, 255));
        valuesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // 统计所有账本数据
        int totalCount = 0;
        double totalIncome = 0, totalExpense = 0;
        try {
            List<String[]> ledgers = LedgerContext.getAllLedgers();
            totalCount = ledgers.size();
            for (String[] ledger : ledgers) {
                if (!"personal".equals(ledger[1])) continue;
                String ledgerFile = "ledger_" + ledger[0] + ".csv";
                java.io.File f = new java.io.File(ledgerFile);
                if (!f.exists()) continue;
                java.util.List<String[]> rows = backend.CsvUtil.read(ledgerFile);
                for (String[] row : rows) {
                    if (row.length < 4) continue;
                    try {
                        String type = row[1];
                        double amount = Double.parseDouble(row[3]);
                        if (type.equals("INC")) totalIncome += amount;
                        if (type.equals("PAY")) totalExpense += amount;
                    } catch (Exception ignored) {}
                }
            }
        } catch (Exception e) {}
        JLabel countLabel = new JLabel(String.valueOf(totalCount));
        countLabel.setFont(new Font("Arial", Font.BOLD, 16));
        countLabel.setForeground(TEXT_COLOR);
        JLabel incomeLabel = new JLabel(String.format("%.2f", totalIncome));
        incomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        incomeLabel.setForeground(TEXT_COLOR);
        JLabel expenseLabel = new JLabel(String.format("%.2f", totalExpense));
        expenseLabel.setFont(new Font("Arial", Font.BOLD, 16));
        expenseLabel.setForeground(TEXT_COLOR);
        valuesPanel.add(countLabel);
        valuesPanel.add(incomeLabel);
        valuesPanel.add(expenseLabel);
        // 第二行空白填充
        valuesPanel.add(new JLabel(""));
        valuesPanel.add(new JLabel(""));
        valuesPanel.add(new JLabel(""));
        panel.add(valuesPanel);
        panel.add(Box.createVerticalStrut(3));
        JPanel labelsPanel = new JPanel(new GridLayout(1, 3, 5, 0));
        labelsPanel.setBackground(new Color(230, 240, 255));
        labelsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel totalLabel = new JLabel("Total\nEntries");
        totalLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        totalLabel.setForeground(TEXT_COLOR);
        JLabel revenueLabel = new JLabel("Revenue");
        revenueLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        revenueLabel.setForeground(TEXT_COLOR);
        JLabel expLabel = new JLabel("EXP");
        expLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        expLabel.setForeground(TEXT_COLOR);
        labelsPanel.add(totalLabel);
        labelsPanel.add(revenueLabel);
        labelsPanel.add(expLabel);
        panel.add(labelsPanel);
        return panel;
    }

    private JPanel createPersonalLedgerHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(new Color(220, 230, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel iconLabel = new JLabel("\uD83D\uDC64");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        iconLabel.setForeground(TEXT_COLOR);
        panel.add(iconLabel);
        panel.add(Box.createHorizontalStrut(8));
        JLabel textLabel = new JLabel("Personal Ledger");
        textLabel.setFont(new Font("Arial", Font.BOLD, 14));
        textLabel.setForeground(TEXT_COLOR);
        panel.add(textLabel);
        panel.add(Box.createHorizontalGlue());
        int count = 0;
        for (String[] ledger : LedgerContext.getAllLedgers()) {
            if (ledger[1].equals("personal")) count++;
        }
        JLabel countLabel = new JLabel(String.valueOf(count));
        countLabel.setFont(new Font("Arial", Font.BOLD, 14));
        countLabel.setForeground(TEXT_COLOR);
        panel.add(countLabel);
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(panel);
                new pages.PersonalLedgerManagerDialog(topFrame, () -> {
                    topFrame.dispose();
                    try {
                        topFrame.getClass().getConstructor().newInstance().setVisible(true);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }).setVisible(true);
            }
        });
        return panel;
    }

    private void renderLedgerList() {
        // 移除旧账本面板
        for (Component c : getComponents()) {
            if (c instanceof JPanel && ((JPanel) c).getName() != null && ((JPanel) c).getName().startsWith("ledger_")) {
                remove(c);
            }
        }
        // 渲染 general 账本
        add(createLedgerItemPanel("general", "general"));
        add(Box.createVerticalStrut(8));
        // 只渲染personal账本
        for (String[] ledger : LedgerContext.getAllLedgers()) {
            if (ledger[1].equals("personal")) {
                add(createLedgerItemPanel(ledger[0], "personal"));
                add(Box.createVerticalStrut(8));
            }
        }
    }

    private JPanel createLedgerItemPanel(String name, String type) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bgColor = type.equals("general") ? new Color(220, 230, 255) : new Color(230, 220, 255);
                Color barColor = type.equals("general") ? new Color(0, 51, 153) : new Color(150, 130, 255);
                Color selectedColor = type.equals("general") ? new Color(180, 210, 255) : new Color(210, 190, 255);
                boolean selected = name.equals(LedgerContext.getCurrentLedger());
                g2d.setColor(selected ? selectedColor : bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.setColor(barColor);
                g2d.fillRect(0, 0, 4, getHeight());
                g2d.dispose();
            }
        };
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        panel.setPreferredSize(new Dimension(SIDEBAR_WIDTH - 40, 45));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setName("ledger_" + name);
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JLabel label = new JLabel(name + (name.equals(LedgerContext.getCurrentLedger()) ? "  ✔" : ""));
        label.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 14));
        label.setForeground(TEXT_COLOR);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setPreferredSize(new Dimension(90, 24));
        panel.add(label, BorderLayout.WEST);
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                LedgerContext.setCurrentLedger(name, type);
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(panel);
                topFrame.dispose();
                try {
                    topFrame.getClass().getConstructor().newInstance().setVisible(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        return panel;
    }
} 