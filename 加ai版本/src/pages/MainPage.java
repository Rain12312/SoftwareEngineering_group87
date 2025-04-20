package pages;

import components.NavigationBar;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainPage extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 255);
    private static final Color CARD_COLOR = new Color(235, 235, 255);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color POSITIVE_COLOR = new Color(0, 150, 0);
    private static final Color NEGATIVE_COLOR = new Color(200, 0, 0);
    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;

    public MainPage() {
        initializeFrame();
        createComponents();
    }

    private void initializeFrame() {
        setTitle("Home");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(BACKGROUND_COLOR);
        setResizable(false);
    }

    private void createComponents() {
        // Main container with BorderLayout
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBackground(BACKGROUND_COLOR);

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Overview card
        JPanel overviewCard = createOverviewCard();
        contentPanel.add(overviewCard);
        contentPanel.add(Box.createVerticalStrut(20));

        // Budget progress card
        JPanel budgetCard = createBudgetCard();
        contentPanel.add(budgetCard);
        contentPanel.add(Box.createVerticalStrut(20));

        // Recent transactions
        JPanel transactionsCard = createTransactionsCard();
        contentPanel.add(transactionsCard);
        contentPanel.add(Box.createVerticalStrut(20));

        // Wrap content in scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Set preferred size for scroll pane to account for navigation bar
        scrollPane.setPreferredSize(new Dimension(WIDTH, HEIGHT - 60));

        containerPanel.add(scrollPane, BorderLayout.CENTER);

        // Navigation bar
        NavigationBar navigationBar = new NavigationBar(this::handleNavigation);
        containerPanel.add(navigationBar, BorderLayout.SOUTH);

        add(containerPanel);
    }

    private JPanel createOverviewCard() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        // Center align all components
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_COLOR);
        contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel monthLabel = new JLabel("March Expense");
        monthLabel.setFont(new Font("Arial", Font.BOLD, 18));
        monthLabel.setForeground(TEXT_COLOR);
        monthLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel amountLabel = new JLabel("439.00");
        amountLabel.setFont(new Font("Arial", Font.BOLD, 32));
        amountLabel.setForeground(TEXT_COLOR);
        amountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        infoPanel.setBackground(CARD_COLOR);
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel incomeLabel = new JLabel("INCOME: 500.00");
        incomeLabel.setForeground(TEXT_COLOR);
        JLabel remainingLabel = new JLabel("REMAINING: 61.00");
        remainingLabel.setForeground(TEXT_COLOR);
        
        infoPanel.add(incomeLabel);
        infoPanel.add(remainingLabel);

        contentPanel.add(monthLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(amountLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(infoPanel);

        panel.add(contentPanel);
        return panel;
    }

    private JPanel createBudgetCard() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // Center align progress info
        JPanel progressInfo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        progressInfo.setBackground(CARD_COLOR);
        progressInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel percentLabel = new JLabel("43.9%");
        percentLabel.setFont(new Font("Arial", Font.BOLD, 16));
        percentLabel.setForeground(TEXT_COLOR);

        JLabel expenseLabel = new JLabel("439.00 EXPENSE");
        expenseLabel.setForeground(NEGATIVE_COLOR);
        
        JLabel remainingLabel = new JLabel("561.00 REMAINING");
        remainingLabel.setForeground(POSITIVE_COLOR);

        progressInfo.add(percentLabel);
        progressInfo.add(Box.createHorizontalStrut(20));
        progressInfo.add(expenseLabel);
        progressInfo.add(Box.createHorizontalStrut(20));
        progressInfo.add(remainingLabel);

        panel.add(progressInfo);
        return panel;
    }

    private JPanel createTransactionsCard() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("LAST SEVEN DAYS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(15));

        // Sample transactions
        String[][] transactions = {
            {"WAGE", "+500.00"},
            {"SNACK", "-148.30"},
            {"FRUIT", "-168.00"}
        };

        for (String[] transaction : transactions) {
            panel.add(createTransactionItem(transaction[0], transaction[1]));
            panel.add(Box.createVerticalStrut(10));
        }

        return panel;
    }

    private JPanel createTransactionItem(String category, String amount) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel categoryLabel = new JLabel(category);
        categoryLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        categoryLabel.setForeground(TEXT_COLOR);

        JLabel amountLabel = new JLabel(amount);
        amountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        amountLabel.setForeground(amount.startsWith("+") ? POSITIVE_COLOR : NEGATIVE_COLOR);

        panel.add(categoryLabel, BorderLayout.WEST);
        panel.add(amountLabel, BorderLayout.EAST);

        return panel;
    }

    private void handleNavigation(ActionEvent e) {
        String command = e.getActionCommand();
        dispose();
        switch (command) {
            case "DATE":
                new CalendarPage().setVisible(true);
                break;
            case "ADD":
                new AccountingPage().setVisible(true);
                break;
            case "MINE":
                new MinePage().setVisible(true);
                break;
            case "HOME":
                new MainPage().setVisible(true);
                break;
        }
    }
} 