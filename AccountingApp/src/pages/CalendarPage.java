package pages;

import components.NavigationBar;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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

    public CalendarPage() {
        super();
        setTitle("Calendar");
        createComponents();
    }

    private void createComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(BACKGROUND_COLOR);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        calendarPanel = new JPanel(new GridLayout(0, 7, 1, 1));
        calendarPanel.setBackground(CALENDAR_TABLE_BG_COLOR);
        calendarPanel.setBorder(BorderFactory.createLineBorder(CALENDAR_BORDER_COLOR));
        updateCalendar();
        calendarPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(calendarPanel);
        centerPanel.add(Box.createVerticalStrut(10));

        dailyDetailsPanel = new JPanel();
        dailyDetailsPanel.setLayout(new BoxLayout(dailyDetailsPanel, BoxLayout.Y_AXIS));
        dailyDetailsPanel.setBackground(BACKGROUND_COLOR);
        dailyDetailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(dailyDetailsPanel);

        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        NavigationBar navigationBar = new NavigationBar(this::handleNavigation);
        mainPanel.add(navigationBar, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        monthYearLabel = new JLabel("", SwingConstants.CENTER);
        monthYearLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        monthYearLabel.setForeground(TEXT_COLOR);
        updateMonthYearLabel();
        panel.add(monthYearLabel, BorderLayout.CENTER);

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
        panel.add(navPanel, BorderLayout.EAST);

        return panel;
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

        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBackground(new Color(230, 240, 255));
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 240)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        summaryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryPanel.setMaximumSize(new Dimension(WIDTH - 20, 60));

        JLabel balanceLabel = new JLabel("monthly balance:   0.00");
        JLabel averageLabel = new JLabel("average daily expense:   0.00");

        for (JLabel label : new JLabel[]{balanceLabel, averageLabel}) {
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            label.setForeground(TEXT_COLOR);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            summaryPanel.add(label);
        }

        dailyDetailsPanel.add(summaryPanel);
        dailyDetailsPanel.add(Box.createVerticalStrut(10));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        infoPanel.setMaximumSize(new Dimension(WIDTH - 20, 80));
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dateLabel = new JLabel(selectedDate.format(DateTimeFormatter.ofPattern("M.d EEEE", Locale.ENGLISH)));
        dateLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        dateLabel.setForeground(TEXT_COLOR);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel incomeLabel = new JLabel("Income:  500.00");
        incomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        incomeLabel.setForeground(new Color(0, 150, 0));
        incomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel expenseLabel = new JLabel("Expense: 316.30");
        expenseLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        expenseLabel.setForeground(new Color(200, 0, 0));
        expenseLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(dateLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(incomeLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(expenseLabel);

        dailyDetailsPanel.add(infoPanel);
        dailyDetailsPanel.add(Box.createVerticalStrut(10));

        dailyDetailsPanel.add(createTransactionCard(1, "WAGE ", "+500.00", new Color(0, 150, 0)));
        dailyDetailsPanel.add(Box.createVerticalStrut(6));
        dailyDetailsPanel.add(createTransactionCard(2, "SNACK", "-148.30", new Color(200, 0, 0)));
        dailyDetailsPanel.add(Box.createVerticalStrut(6));
        dailyDetailsPanel.add(createTransactionCard(3, "FRUIT  ", "-168.00", new Color(200, 0, 0)));

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
} 