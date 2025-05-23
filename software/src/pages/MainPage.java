package pages;

import components.NavigationBar;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import backend.Transaction;
import backend.TransactionService;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import backend.LedgerContext;
import pages.LedgerManagerDialog;

public class MainPage extends BasePage {
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 255);
    private static final Color CARD_COLOR = new Color(235, 235, 255);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color POSITIVE_COLOR = new Color(0, 150, 0);
    private static final Color NEGATIVE_COLOR = new Color(200, 0, 0);
    private static final int WIDTH = 500;
    private static final int HEIGHT = 800;

    private double totalExpense = 0;
    private double totalIncome = 0;
    private double remaining = 0;
    private List<Transaction> lastSevenDaysTransactions;

    private void loadData() {
        totalExpense = 0;
        totalIncome = 0;
        remaining = 0;
        lastSevenDaysTransactions = new java.util.ArrayList<>();
        try {
            TransactionService service = new TransactionService();
            List<Transaction> all = service.getAllTransactions();
            LocalDate now = LocalDate.now();
            for (Transaction t : all) {
                LocalDate date = LocalDate.parse(t.getDate());
                // 本月
                if (date.getYear() == now.getYear() && date.getMonthValue() == now.getMonthValue()) {
                    if (t.getType().equals("PAY")) totalExpense += t.getAmount();
                    if (t.getType().equals("INC")) totalIncome += t.getAmount();
                }
                // 近七天
                if (ChronoUnit.DAYS.between(date, now) >= 0 && ChronoUnit.DAYS.between(date, now) < 7) {
                    lastSevenDaysTransactions.add(t);
                }
            }
            remaining = totalIncome - totalExpense;
        } catch (Exception e) {
            // ignore, 保持为0
        }
    }

    public MainPage() {
        super();
        setTitle("Home");
        loadData();
        createComponents();
    }

    private void createComponents() {
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBackground(BACKGROUND_COLOR);

        // Header panel with sidebar toggle button and ledger manager
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.add(createSidebarToggleButton(), BorderLayout.WEST);

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel overviewCard = createOverviewCard();
        contentPanel.add(overviewCard);
        contentPanel.add(Box.createVerticalStrut(15));
        JPanel budgetCard = createBudgetCard();
        contentPanel.add(budgetCard);
        contentPanel.add(Box.createVerticalStrut(15));
        JPanel transactionsCard = createTransactionsCard();
        contentPanel.add(transactionsCard);
        containerPanel.add(headerPanel, BorderLayout.NORTH);
        containerPanel.add(contentPanel, BorderLayout.CENTER);
        setupSidebar(containerPanel);
        NavigationBar navigationBar = new NavigationBar(this::handleNavigation);
        containerPanel.add(navigationBar, BorderLayout.SOUTH);
        add(containerPanel);
    }

    private JPanel createOverviewCard() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_COLOR);
        contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Get current month name
        String monthName = LocalDate.now().getMonth().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH);
        JLabel monthLabel = new JLabel(monthName + " Expense");
        monthLabel.setFont(new Font("Arial", Font.BOLD, 24));
        monthLabel.setForeground(TEXT_COLOR);
        monthLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel amountLabel = new JLabel(String.format("%.2f", totalExpense));
        amountLabel.setFont(new Font("Arial", Font.BOLD, 32));
        amountLabel.setForeground(TEXT_COLOR);
        amountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(monthLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(amountLabel);
        panel.add(contentPanel);
        return panel;
    }

    private JPanel createBudgetCard() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        JPanel progressInfo = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        progressInfo.setBackground(CARD_COLOR);
        progressInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel expenseLabel = new JLabel(String.format("%.2f EXPENSE", totalExpense));
        expenseLabel.setFont(new Font("Arial", Font.BOLD, 18));
        expenseLabel.setForeground(NEGATIVE_COLOR);
        JLabel remainingLabel = new JLabel(String.format("%.2f REMAINING", remaining));
        remainingLabel.setFont(new Font("Arial", Font.BOLD, 18));
        remainingLabel.setForeground(POSITIVE_COLOR);
        progressInfo.add(expenseLabel);
        progressInfo.add(remainingLabel);
        panel.add(progressInfo);
        return panel;
    }

    private JPanel createTransactionsCard() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.add(Box.createVerticalStrut(5));
        JLabel titleLabel = new JLabel("LAST SEVEN DAYS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(15));
        for (Transaction t : lastSevenDaysTransactions) {
            String sign = t.getType().equals("INC") ? "+" : "-";
            String amount = sign + String.format("%.2f", t.getAmount());
            panel.add(createTransactionItem(t.getCategory(), amount));
        }
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JPanel createTransactionItem(String category, String amount) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel categoryLabel = new JLabel(category);
        categoryLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        categoryLabel.setForeground(TEXT_COLOR);

        JLabel amountLabel = new JLabel(amount);
        amountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        amountLabel.setForeground(amount.startsWith("+") ? POSITIVE_COLOR : NEGATIVE_COLOR);

        panel.add(categoryLabel, BorderLayout.WEST);
        panel.add(amountLabel, BorderLayout.EAST);

        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(16, 0, 16, 0)
        ));

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