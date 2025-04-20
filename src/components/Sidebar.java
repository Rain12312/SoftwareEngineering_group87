package components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class Sidebar extends JPanel {
    private static final Color BACKGROUND_COLOR = new Color(235, 235, 255);
    private static final Color CARD_COLOR = new Color(220, 230, 255);  // 淡蓝色
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color CHECK_COLOR = new Color(0, 51, 153);  // 深蓝色
    private static final Color DAILY_LEDGER_COLOR = new Color(210, 230, 255);
    private static final Color LOVE_LEDGER_COLOR = new Color(230, 220, 255);
    private static final Color DAILY_LEDGER_SELECTED_COLOR = new Color(180, 210, 255);
    private static final Color LOVE_LEDGER_SELECTED_COLOR = new Color(210, 190, 255);
    private static final int SIDEBAR_WIDTH = 200;
    
    private boolean isDailyLedgerSelected = false;
    private boolean isLoveLedgerSelected = false;
    private JPanel dailyLedgerPanel;
    private JPanel loveLedgerPanel;

    public Sidebar(int height, ActionListener actionListener) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(BACKGROUND_COLOR);
        setPreferredSize(new Dimension(SIDEBAR_WIDTH, height));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 添加统计信息面板
        add(createStatsPanel());
        add(Box.createVerticalStrut(10));  // 添加间距
        
        // 添加个人账本面板
        add(createPersonalLedgerPanel());
        add(Box.createVerticalStrut(10));  // 添加间距

        // 添加Daily Life Ledger面板
        dailyLedgerPanel = createDailyLifeLedgerPanel();
        add(dailyLedgerPanel);
        add(Box.createVerticalStrut(8));  // 添加间距

        // 添加Love Ledger面板
        loveLedgerPanel = createLoveLedgerPanel();
        add(loveLedgerPanel);

        // 添加底部填充
        add(Box.createVerticalGlue());
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 标题 - General Ledger
        JLabel titleLabel = new JLabel("General Ledger");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(8));

        // 数值面板
        JPanel valuesPanel = new JPanel(new GridLayout(1, 3, 5, 0));
        valuesPanel.setBackground(CARD_COLOR);
        valuesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // 添加三个数值
        String[] values = {"4", "500.00", "939.00"};
        for (String value : values) {
            JLabel valueLabel = new JLabel(value);
            valueLabel.setFont(new Font("Arial", Font.BOLD, 12));
            valueLabel.setForeground(TEXT_COLOR);
            valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
            valuesPanel.add(valueLabel);
        }
        panel.add(valuesPanel);
        panel.add(Box.createVerticalStrut(3));

        // 标签面板
        JPanel labelsPanel = new JPanel(new GridLayout(1, 3, 5, 0));
        labelsPanel.setBackground(CARD_COLOR);
        labelsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // 创建标签
        JPanel totalPanel = new JPanel();
        totalPanel.setLayout(new BoxLayout(totalPanel, BoxLayout.Y_AXIS));
        totalPanel.setBackground(CARD_COLOR);
        JLabel totalLabel1 = new JLabel("Total");
        JLabel totalLabel2 = new JLabel("Entries");
        totalLabel1.setFont(new Font("Arial", Font.PLAIN, 9));
        totalLabel2.setFont(new Font("Arial", Font.PLAIN, 9));
        totalLabel1.setForeground(TEXT_COLOR);
        totalLabel2.setForeground(TEXT_COLOR);
        totalLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
        totalLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        totalPanel.add(totalLabel1);
        totalPanel.add(totalLabel2);

        JLabel revenueLabel = new JLabel("Revenue");
        revenueLabel.setFont(new Font("Arial", Font.PLAIN, 9));
        revenueLabel.setForeground(TEXT_COLOR);
        revenueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel expensesLabel = new JLabel("EXP");
        expensesLabel.setFont(new Font("Arial", Font.PLAIN, 9));
        expensesLabel.setForeground(TEXT_COLOR);
        expensesLabel.setHorizontalAlignment(SwingConstants.CENTER);

        labelsPanel.add(totalPanel);
        labelsPanel.add(revenueLabel);
        labelsPanel.add(expensesLabel);
        
        panel.add(labelsPanel);

        return panel;
    }

    private JPanel createPersonalLedgerPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // 设置面板大小，与第一个面板一样宽
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 添加小人图标
        JLabel iconLabel = new JLabel("👤");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        iconLabel.setForeground(TEXT_COLOR);
        panel.add(iconLabel);
        panel.add(Box.createHorizontalStrut(8));

        // 添加文字
        JLabel textLabel = new JLabel("Personal Ledger");
        textLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        textLabel.setForeground(TEXT_COLOR);
        panel.add(textLabel);
        
        // 添加弹性空间
        panel.add(Box.createHorizontalGlue());

        // 添加数量
        JLabel countLabel = new JLabel("2");
        countLabel.setFont(new Font("Arial", Font.BOLD, 12));
        countLabel.setForeground(TEXT_COLOR);
        panel.add(countLabel);

        return panel;
    }

    private JPanel createDailyLifeLedgerPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // 绘制圆角矩形背景
                g2d.setColor(isDailyLedgerSelected ? DAILY_LEDGER_SELECTED_COLOR : DAILY_LEDGER_COLOR);
                RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), 15, 15);
                g2d.fill(roundedRectangle);
                
                // 添加左侧装饰条
                g2d.setColor(CHECK_COLOR);
                g2d.fillRect(0, 0, 4, getHeight());
                
                // 如果被选中，绘制对勾
                if (isDailyLedgerSelected) {
                    g2d.setColor(CHECK_COLOR);
                    g2d.setStroke(new BasicStroke(2));
                    int checkX = getWidth() - 20;
                    int checkY = getHeight() - 15;
                    g2d.drawLine(checkX - 8, checkY - 4, checkX - 4, checkY);
                    g2d.drawLine(checkX - 4, checkY, checkX + 2, checkY - 8);
                }
                
                g2d.dispose();
            }
        };
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        panel.setPreferredSize(new Dimension(SIDEBAR_WIDTH - 40, 45));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel("Daily Life Ledger");
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setForeground(TEXT_COLOR);
        panel.add(label, BorderLayout.WEST);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                isDailyLedgerSelected = !isDailyLedgerSelected;
                if (isDailyLedgerSelected) {
                    isLoveLedgerSelected = false;
                    loveLedgerPanel.repaint();
                }
                panel.repaint();
            }
        });

        return panel;
    }

    private JPanel createLoveLedgerPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // 绘制圆角矩形背景
                g2d.setColor(isLoveLedgerSelected ? LOVE_LEDGER_SELECTED_COLOR : LOVE_LEDGER_COLOR);
                RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), 15, 15);
                g2d.fill(roundedRectangle);
                
                // 添加左侧装饰条
                g2d.setColor(new Color(150, 130, 255));
                g2d.fillRect(0, 0, 4, getHeight());
                
                // 如果被选中，绘制对勾
                if (isLoveLedgerSelected) {
                    g2d.setColor(new Color(150, 130, 255));
                    g2d.setStroke(new BasicStroke(2));
                    int checkX = getWidth() - 20;
                    int checkY = getHeight() - 15;
                    g2d.drawLine(checkX - 8, checkY - 4, checkX - 4, checkY);
                    g2d.drawLine(checkX - 4, checkY, checkX + 2, checkY - 8);
                }
                
                g2d.dispose();
            }
        };
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        panel.setPreferredSize(new Dimension(SIDEBAR_WIDTH - 40, 45));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel("Love Ledger");
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setForeground(TEXT_COLOR);
        panel.add(label, BorderLayout.WEST);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                isLoveLedgerSelected = !isLoveLedgerSelected;
                if (isLoveLedgerSelected) {
                    isDailyLedgerSelected = false;
                    dailyLedgerPanel.repaint();
                }
                panel.repaint();
            }
        });

        return panel;
    }
} 