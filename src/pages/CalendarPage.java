package pages;

import components.NavigationBar;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import backend.LedgerContext;
import backend.Transaction;
import backend.TransactionService;
import pages.PersonalLedgerManagerDialog;
import java.util.List;

public class CalendarPage extends BasePage {
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 255);
    private static final Color SELECTED_DAY_COLOR = new Color(255, 220, 100);
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color CALENDAR_TEXT_COLOR = Color.WHITE;
    private static final Color CARD_COLOR = new Color(235, 235, 255);
    private static final Color CALENDAR_TABLE_BG_COLOR = new Color(60, 90, 180);
    private static final Color CALENDAR_HEADER_BG_COLOR = new Color(20, 40, 140);
    private static final Color CALENDAR_BORDER_COLOR = new Color(255, 255, 255);

    private static final int WIDTH = 500;
    private static final int HEIGHT = 800;
    private static final String[] WEEKDAYS = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

    private YearMonth currentYearMonth = YearMonth.now();
    private JPanel calendarPanel;
    private JLabel monthYearLabel;
    private JPanel dailyDetailsPanel;
    private JButton selectedDayButton;
    private int initialSelectedDay = -1;
    private JButton initialSelectedButton = null;

    public CalendarPage() {
        super();
        setTitle("Calendar");
        createComponents();
    }

    private void createComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Header panel with sidebar toggle button and ledger manager
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.add(createSidebarToggleButton(), BorderLayout.WEST);

        // Calendar header
        JPanel centerHeaderPanel = new JPanel(new BorderLayout());
        centerHeaderPanel.setOpaque(false);
        monthYearLabel = new JLabel("", SwingConstants.CENTER);
        monthYearLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        monthYearLabel.setForeground(TEXT_COLOR);
        monthYearLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        updateMonthYearLabel();
        centerHeaderPanel.add(monthYearLabel, BorderLayout.CENTER);
        headerPanel.add(centerHeaderPanel, BorderLayout.CENTER);

        JPanel navPanel = new JPanel(new FlowLayout());
        navPanel.setBackground(BACKGROUND_COLOR);
        JButton prevButton = new JButton("←");
        JButton nextButton = new JButton("→");
        for (JButton button : new JButton[]{prevButton, nextButton}) {
            button.setFont(new Font("Segoe UI", Font.PLAIN, 24));
            button.setForeground(TEXT_COLOR);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
        }
        prevButton.addActionListener(e -> navigateMonth(-1));
        nextButton.addActionListener(e -> navigateMonth(1));
        navPanel.add(prevButton);
        navPanel.add(nextButton);
        headerPanel.add(navPanel, BorderLayout.SOUTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Calendar content
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBackground(BACKGROUND_COLOR);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 日历区（固定高度）
        calendarPanel = new JPanel(new GridLayout(0, 7, 1, 1));
        calendarPanel.setBackground(CALENDAR_TABLE_BG_COLOR);
        calendarPanel.setBorder(BorderFactory.createLineBorder(CALENDAR_BORDER_COLOR));
        calendarPanel.setPreferredSize(new Dimension(WIDTH - 40, 320));
        updateCalendar();
        calendarPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(calendarPanel, BorderLayout.NORTH);

        // 本月统计卡片
        JPanel monthSummaryPanel = createMonthSummaryPanel();
        centerPanel.add(monthSummaryPanel, BorderLayout.CENTER);

        // 详情区（自适应）
        dailyDetailsPanel = new JPanel();
        dailyDetailsPanel.setLayout(new BoxLayout(dailyDetailsPanel, BoxLayout.Y_AXIS));
        dailyDetailsPanel.setBackground(BACKGROUND_COLOR);
        dailyDetailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JScrollPane detailsScroll = new JScrollPane(dailyDetailsPanel);
        detailsScroll.setBorder(null);
        detailsScroll.setBackground(BACKGROUND_COLOR);
        detailsScroll.getViewport().setBackground(BACKGROUND_COLOR);
        detailsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        detailsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        detailsScroll.getVerticalScrollBar().setUnitIncrement(16);
        centerPanel.add(detailsScroll, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Setup sidebar
        setupSidebar(mainPanel);

        // Navigation bar
        NavigationBar navigationBar = new NavigationBar(this::handleNavigation);
        mainPanel.add(navigationBar, BorderLayout.SOUTH);

        add(mainPanel);
        // 自动选中当天
        LocalDate today = LocalDate.now();
        if (today.getYear() == currentYearMonth.getYear() && today.getMonthValue() == currentYearMonth.getMonthValue()) {
            showDailyDetails(today.getDayOfMonth());
            initialSelectedDay = today.getDayOfMonth();
        } else {
            showDailyDetails(1);
            initialSelectedDay = 1;
        }
    }

    private void updateCalendar() {
        calendarPanel.removeAll();

        for (String day : WEEKDAYS) {
            JLabel label = new JLabel(day, SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            label.setForeground(CALENDAR_TEXT_COLOR);
            label.setOpaque(true);
            label.setBackground(CALENDAR_HEADER_BG_COLOR);
            label.setBorder(BorderFactory.createLineBorder(CALENDAR_BORDER_COLOR));
            calendarPanel.add(label);
        }

        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int startDayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7;

        for (int i = 0; i < startDayOfWeek; i++) {
            JLabel emptyLabel = new JLabel();
            emptyLabel.setOpaque(true);
            emptyLabel.setBackground(CALENDAR_TABLE_BG_COLOR);
            emptyLabel.setBorder(BorderFactory.createLineBorder(CALENDAR_BORDER_COLOR));
            calendarPanel.add(emptyLabel);
        }

        initialSelectedButton = null;
        for (int day = 1; day <= currentYearMonth.lengthOfMonth(); day++) {
            final int currentDay = day;

            JButton dayButton = new JButton(String.valueOf(day));
            dayButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            dayButton.setForeground(Color.BLACK);
            dayButton.setFocusPainted(false);
            dayButton.setBorder(BorderFactory.createLineBorder(CALENDAR_BORDER_COLOR));
            dayButton.setBackground(CALENDAR_TABLE_BG_COLOR);
            dayButton.setOpaque(true);

            dayButton.addActionListener(e -> {
                if (selectedDayButton != null) {
                    selectedDayButton.setBackground(CALENDAR_TABLE_BG_COLOR);
                }
                dayButton.setBackground(new Color(255, 235, 150));
                selectedDayButton = dayButton;
                showDailyDetails(currentDay);
            });

            // 自动高亮当天
            if (currentDay == initialSelectedDay) {
                dayButton.setBackground(new Color(255, 235, 150));
                selectedDayButton = dayButton;
                initialSelectedButton = dayButton;
            }

            calendarPanel.add(dayButton);
        }

        int totalFilled = startDayOfWeek + currentYearMonth.lengthOfMonth();
        int remaining = (totalFilled <= 35) ? 35 - totalFilled : 42 - totalFilled;

        for (int i = 0; i < remaining; i++) {
            JLabel emptyLabel = new JLabel();
            emptyLabel.setOpaque(true);
            emptyLabel.setBackground(CALENDAR_TABLE_BG_COLOR);
            emptyLabel.setBorder(BorderFactory.createLineBorder(CALENDAR_BORDER_COLOR));
            calendarPanel.add(emptyLabel);
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    private void showDailyDetails(int day) {
        LocalDate selectedDate = currentYearMonth.atDay(day);
        dailyDetailsPanel.removeAll();
        double income = 0, expense = 0;
        List<Transaction> transactions = new java.util.ArrayList<>();
        try {
            TransactionService service = new TransactionService();
            for (Transaction t : service.getAllTransactions()) {
                if (t.getDate().equals(selectedDate.toString())) {
                    transactions.add(t);
                    if (t.getType().equals("INC")) income += t.getAmount();
                    if (t.getType().equals("PAY")) expense += t.getAmount();
                }
            }
        } catch (Exception e) {}
        // 白色卡片
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));
        // 大标题 日期+星期
        String weekDay = selectedDate.getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH);
        JLabel dateTitle = new JLabel(selectedDate.getMonthValue() + "." + selectedDate.getDayOfMonth() + " " + weekDay, SwingConstants.CENTER);
        dateTitle.setFont(new Font("Arial", Font.BOLD, 18));
        dateTitle.setForeground(Color.BLACK);
        dateTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(dateTitle);
        card.add(Box.createVerticalStrut(8));
        // Income/Expense
        JPanel iePanel = new JPanel();
        iePanel.setLayout(new BoxLayout(iePanel, BoxLayout.X_AXIS));
        iePanel.setBackground(Color.WHITE);
        JLabel incomeLabel = new JLabel("Income: ");
        incomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        incomeLabel.setForeground(new Color(0, 150, 0));
        JLabel incomeValue = new JLabel(String.format("%.2f", income));
        incomeValue.setFont(new Font("Arial", Font.BOLD, 16));
        incomeValue.setForeground(new Color(0, 150, 0));
        JLabel expenseLabel = new JLabel("  Expense: ");
        expenseLabel.setFont(new Font("Arial", Font.BOLD, 16));
        expenseLabel.setForeground(new Color(200, 0, 0));
        JLabel expenseValue = new JLabel(String.format("%.2f", expense));
        expenseValue.setFont(new Font("Arial", Font.BOLD, 16));
        expenseValue.setForeground(new Color(200, 0, 0));
        iePanel.add(incomeLabel);
        iePanel.add(incomeValue);
        iePanel.add(expenseLabel);
        iePanel.add(expenseValue);
        card.add(iePanel);
        card.add(Box.createVerticalStrut(10));
        // 流水明细
        int idx = 1;
        for (Transaction t : transactions) {
            String sign = t.getType().equals("INC") ? "+" : "-";
            Color color = t.getType().equals("INC") ? new Color(0, 150, 0) : new Color(200, 0, 0);
            JPanel row = new JPanel(new BorderLayout());
            row.setBackground(Color.WHITE);
            row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
            JLabel idxLabel = new JLabel(idx + "");
            idxLabel.setFont(new Font("Arial", Font.BOLD, 15));
            idxLabel.setForeground(Color.BLACK);
            JLabel nameLabel = new JLabel(t.getCategory());
            nameLabel.setFont(new Font("Arial", Font.BOLD, 15));
            nameLabel.setForeground(Color.BLACK);
            JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            left.setBackground(Color.WHITE);
            left.add(idxLabel);
            left.add(nameLabel);
            JLabel amountLabel = new JLabel(sign + String.format("%.2f", t.getAmount()));
            amountLabel.setFont(new Font("Arial", Font.BOLD, 15));
            amountLabel.setForeground(color);
            row.add(left, BorderLayout.WEST);
            row.add(amountLabel, BorderLayout.EAST);
            card.add(row);
            idx++;
        }
        dailyDetailsPanel.add(card);
        dailyDetailsPanel.revalidate();
        dailyDetailsPanel.repaint();
    }

    private ImageIcon loadIconForLabel(String label) {
        String filename = null;
        label = label.toLowerCase();

        if (label.contains("wage")) {
            filename = "/resources/icons/icon_wage.jpg";
        } else if (label.contains("snack")) {
            filename = "/resources/icons/icon_snack.jpg";
        } else if (label.contains("fruit")) {
            filename = "/resources/icons/icon_fruit.jpg";
        }

        if (filename != null) {
            try {
                ImageIcon raw = new ImageIcon(getClass().getResource(filename));
                Image scaled = raw.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            } catch (Exception e) {
                System.err.println("⚠️ 图标加载失败: " + filename);
            }
        }

        return null;
    }

    private JPanel createTransactionCard(int index, String label, String amount, Color amountColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.X_AXIS));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 255)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel indexLabel = new JLabel(index + "");
        indexLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        indexLabel.setForeground(TEXT_COLOR);

        JLabel nameLabel = new JLabel(label);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(TEXT_COLOR);

        JLabel iconLabel = new JLabel();
        ImageIcon icon = loadIconForLabel(label);
        if (icon != null) {
            iconLabel.setIcon(icon);
        }

        JLabel amountLabel = new JLabel(amount);
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        amountLabel.setForeground(amountColor);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setBackground(Color.WHITE);
        left.add(indexLabel);
        left.add(nameLabel);
        if (icon != null) {
            left.add(Box.createHorizontalStrut(5));
            left.add(iconLabel);
        }

        card.add(left);
        card.add(Box.createHorizontalGlue());
        card.add(amountLabel);

        return card;
    }

    private void navigateMonth(int months) {
        currentYearMonth = currentYearMonth.plusMonths(months);
        updateMonthYearLabel();
        updateCalendar();
    }

    private void updateMonthYearLabel() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/yyyy");
        monthYearLabel.setText(currentYearMonth.format(formatter));
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

    // 新增：本月统计卡片
    private JPanel createMonthSummaryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(220, 230, 250)); // 更浅蓝色
        panel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        double income = 0, expense = 0;
        int daysWithExpense = 0;
        int daysInMonth = currentYearMonth.lengthOfMonth();
        double[] dailyExpense = new double[daysInMonth + 1];
        try {
            TransactionService service = new TransactionService();
            java.time.YearMonth ym = currentYearMonth;
            for (Transaction t : service.getAllTransactions()) {
                java.time.LocalDate date = java.time.LocalDate.parse(t.getDate());
                if (date.getYear() == ym.getYear() && date.getMonthValue() == ym.getMonthValue()) {
                    if (t.getType().equals("INC")) income += t.getAmount();
                    if (t.getType().equals("PAY")) {
                        expense += t.getAmount();
                        int day = date.getDayOfMonth();
                        dailyExpense[day] += t.getAmount();
                    }
                }
            }
            for (int i = 1; i <= daysInMonth; i++) {
                if (dailyExpense[i] > 0) daysWithExpense++;
            }
        } catch (Exception e) {}
        double avgExpense = daysWithExpense > 0 ? expense / daysWithExpense : 0;
        JLabel balanceLabel = new JLabel("monthly balance:  " + String.format("%.2f", income - expense));
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        balanceLabel.setForeground(Color.BLACK);
        balanceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel avgLabel = new JLabel("average daily expense:  " + String.format("%.2f", avgExpense));
        avgLabel.setFont(new Font("Arial", Font.BOLD, 16));
        avgLabel.setForeground(Color.BLACK);
        avgLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(balanceLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(avgLabel);
        return panel;
    }
} 