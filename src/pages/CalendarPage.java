package pages;

import components.NavigationBar;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class CalendarPage extends BasePage {
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 255); // 更浅的紫色背景
    private static final Color SELECTED_DAY_COLOR = new Color(200, 220, 255); // 选中日期的蓝色
    private static final Color CURRENT_DAY_COLOR = new Color(200, 220, 255); // 当前日期的蓝色
    private static final Color TEXT_COLOR = new Color(50, 50, 50); // 深灰色文字
    private static final Color CARD_COLOR = new Color(235, 235, 255);
    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;
    private static final String[] WEEKDAYS = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    
    private YearMonth currentYearMonth;
    private JPanel calendarPanel;
    private JLabel monthYearLabel;
    private JPanel dailyDetailsPanel;
    private JButton selectedDayButton;
    private JPanel sidebarPanel;
    private boolean isSidebarVisible = false;

    public CalendarPage() {
        super();
        setTitle("Calendar");
        currentYearMonth = YearMonth.now();
        createComponents();
    }

    private void createComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Calendar header with separator
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Add separator after header
        JSeparator headerSeparator = new JSeparator(JSeparator.HORIZONTAL);
        headerSeparator.setForeground(new Color(200, 200, 220));
        mainPanel.add(headerSeparator, BorderLayout.CENTER);

        // Center panel containing calendar and details
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(BACKGROUND_COLOR);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Calendar grid
        calendarPanel = new JPanel(new GridLayout(0, 7, 5, 5));
        calendarPanel.setBackground(BACKGROUND_COLOR);
        calendarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        updateCalendar();
        centerPanel.add(calendarPanel);

        // Add separator between calendar and details
        JSeparator calendarSeparator = new JSeparator(JSeparator.HORIZONTAL);
        calendarSeparator.setForeground(new Color(200, 200, 220));
        calendarSeparator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(calendarSeparator);
        centerPanel.add(Box.createVerticalStrut(10));

        // Daily details panel
        dailyDetailsPanel = createDailyDetailsPanel();
        centerPanel.add(dailyDetailsPanel);

        // Wrap center panel in scroll pane
        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Setup sidebar
        setupSidebar(mainPanel);

        // Navigation bar
        NavigationBar navigationBar = new NavigationBar(this::handleNavigation);
        mainPanel.add(navigationBar, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add sidebar toggle button
        panel.add(createSidebarToggleButton(), BorderLayout.WEST);

        // Month/Year label
        monthYearLabel = new JLabel("", SwingConstants.CENTER);
        monthYearLabel.setFont(new Font("Arial", Font.BOLD, 24));
        monthYearLabel.setForeground(TEXT_COLOR);
        updateMonthYearLabel();
        panel.add(monthYearLabel, BorderLayout.CENTER);

        // Navigation buttons
        JPanel navPanel = new JPanel(new FlowLayout());
        navPanel.setBackground(BACKGROUND_COLOR);
        
        JButton prevButton = new JButton("←");
        prevButton.setFont(new Font("Arial", Font.PLAIN, 24));
        prevButton.setForeground(TEXT_COLOR);
        prevButton.setFocusPainted(false);
        prevButton.setBorderPainted(false);
        prevButton.setContentAreaFilled(false);
        prevButton.addActionListener(e -> navigateMonth(-1));
        
        JButton nextButton = new JButton("→");
        nextButton.setFont(new Font("Arial", Font.PLAIN, 24));
        nextButton.setForeground(TEXT_COLOR);
        nextButton.setFocusPainted(false);
        nextButton.setBorderPainted(false);
        nextButton.setContentAreaFilled(false);
        nextButton.addActionListener(e -> navigateMonth(1));
        
        navPanel.add(prevButton);
        navPanel.add(nextButton);
        panel.add(navPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(CARD_COLOR);
        sidebar.setPreferredSize(new Dimension(200, HEIGHT));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add sidebar content
        JLabel titleLabel = new JLabel("菜单");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(titleLabel);
        sidebar.add(Box.createVerticalStrut(20));

        // Add menu items
        String[] menuItems = {"月度统计", "预算设置", "账单导出", "设置"};
        for (String item : menuItems) {
            JButton menuButton = new JButton(item);
            menuButton.setFont(new Font("Arial", Font.PLAIN, 16));
            menuButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            menuButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            menuButton.setBackground(CARD_COLOR);
            menuButton.setBorderPainted(false);
            menuButton.setFocusPainted(false);
            menuButton.addActionListener(e -> {
                ActionEvent actionEvent = new ActionEvent(menuButton, ActionEvent.ACTION_PERFORMED, item);
                handleSidebarAction(actionEvent);
            });
            sidebar.add(menuButton);
            sidebar.add(Box.createVerticalStrut(10));
        }

        return sidebar;
    }

    @Override
    public void toggleSidebar() {
        isSidebarVisible = !isSidebarVisible;
        sidebarPanel.setVisible(isSidebarVisible);
        revalidate();
        repaint();
    }

    @Override
    protected void handleSidebarAction(ActionEvent e) {
        super.handleSidebarAction(e);
    }

    public void handleSidebarAction(String item) {
        // 处理菜单项点击
        System.out.println("Selected menu item: " + item);
    }

    private void updateCalendar() {
        calendarPanel.removeAll();
        
        // Add weekday headers
        for (String day : WEEKDAYS) {
            JLabel label = new JLabel(day, SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 14));
            label.setForeground(TEXT_COLOR);
            calendarPanel.add(label);
        }
        
        // Get the first day of the month
        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7;
        
        // Add empty spaces for days before the first of the month
        for (int i = 0; i < dayOfWeek; i++) {
            JLabel emptyLabel = new JLabel("");
            emptyLabel.setBackground(BACKGROUND_COLOR);
            calendarPanel.add(emptyLabel);
        }
        
        // Add buttons for each day
        for (int i = 1; i <= currentYearMonth.lengthOfMonth(); i++) {
            JButton dayButton = new JButton(String.valueOf(i));
            dayButton.setFont(new Font("Arial", Font.PLAIN, 14));
            dayButton.setForeground(TEXT_COLOR);
            dayButton.setFocusPainted(false);
            dayButton.setBorderPainted(false);
            dayButton.setBackground(BACKGROUND_COLOR);
            dayButton.setOpaque(true);
            
            final int day = i;
            dayButton.addActionListener(e -> {
                if (selectedDayButton != null) {
                    selectedDayButton.setBackground(BACKGROUND_COLOR);
                }
                dayButton.setBackground(SELECTED_DAY_COLOR);
                selectedDayButton = dayButton;
                showDailyDetails(day);
            });
            
            calendarPanel.add(dayButton);
        }

        // Add empty spaces for remaining days to maintain grid
        int totalDays = dayOfWeek + currentYearMonth.lengthOfMonth();
        int remainingSpaces = (totalDays <= 35) ? 35 - totalDays : 42 - totalDays;
        for (int i = 0; i < remainingSpaces; i++) {
            JLabel emptyLabel = new JLabel("");
            emptyLabel.setBackground(BACKGROUND_COLOR);
            calendarPanel.add(emptyLabel);
        }
        
        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    private JPanel createDailyDetailsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Monthly statistics
        JPanel statsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        statsPanel.setBackground(BACKGROUND_COLOR);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel balanceLabel = new JLabel("Monthly balance: 0.00");
        balanceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        balanceLabel.setForeground(TEXT_COLOR);

        JLabel averageLabel = new JLabel("Average daily expense: 0.00");
        averageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        averageLabel.setForeground(TEXT_COLOR);

        statsPanel.add(balanceLabel);
        statsPanel.add(averageLabel);
        panel.add(statsPanel);

        // Daily transactions panel
        JPanel transactionsPanel = new JPanel();
        transactionsPanel.setLayout(new BoxLayout(transactionsPanel, BoxLayout.Y_AXIS));
        transactionsPanel.setBackground(BACKGROUND_COLOR);
        transactionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Add a label for the date
        JLabel dateLabel = new JLabel("Select a date to view details");
        dateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        dateLabel.setForeground(TEXT_COLOR);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        transactionsPanel.add(dateLabel);
        
        panel.add(transactionsPanel);
        return panel;
    }

    private void showDailyDetails(int day) {
        LocalDate selectedDate = currentYearMonth.atDay(day);
        
        dailyDetailsPanel.removeAll();
        
        // Date header
        JLabel dateLabel = new JLabel(selectedDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
        dateLabel.setFont(new Font("Arial", Font.BOLD, 16));
        dateLabel.setForeground(TEXT_COLOR);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Daily summary
        JPanel summaryPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        summaryPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel incomeLabel = new JLabel("Income: $500.00");
        incomeLabel.setForeground(new Color(0, 150, 0));
        JLabel expenseLabel = new JLabel("Expense: $168.30");
        expenseLabel.setForeground(new Color(200, 0, 0));
        
        summaryPanel.add(incomeLabel);
        summaryPanel.add(expenseLabel);
        
        dailyDetailsPanel.add(dateLabel);
        dailyDetailsPanel.add(Box.createVerticalStrut(10));
        dailyDetailsPanel.add(summaryPanel);
        
        dailyDetailsPanel.revalidate();
        dailyDetailsPanel.repaint();
    }

    private void navigateMonth(int months) {
        currentYearMonth = currentYearMonth.plusMonths(months);
        updateMonthYearLabel();
        updateCalendar();
    }

    private void updateMonthYearLabel() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM yyyy");
        monthYearLabel.setText(currentYearMonth.format(formatter));
    }

    private void handleNavigation(ActionEvent e) {
        String command = e.getActionCommand();
        dispose();
        switch (command) {
            case "HOME":
                new MainPage().setVisible(true);
                break;
            case "ADD":
                new AccountingPage().setVisible(true);
                break;
            case "MINE":
                new MinePage().setVisible(true);
                break;
        }
    }

    private void createBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Summary section
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBackground(CARD_COLOR);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        summaryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // Monthly balance with icon
        JPanel balancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        balancePanel.setBackground(CARD_COLOR);
        
        JLabel balanceIcon = new JLabel("💰");
        JLabel balanceLabel = new JLabel("Monthly balance: ");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel balanceAmount = new JLabel("0.00");
        balanceAmount.setFont(new Font("Arial", Font.PLAIN, 14));
        
        balancePanel.add(balanceIcon);
        balancePanel.add(balanceLabel);
        balancePanel.add(balanceAmount);
        balancePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Average expense with icon
        JPanel averagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        averagePanel.setBackground(CARD_COLOR);
        
        JLabel avgIcon = new JLabel("📊");
        JLabel avgLabel = new JLabel("Average daily expense: ");
        avgLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel avgAmount = new JLabel("0.00");
        avgAmount.setFont(new Font("Arial", Font.PLAIN, 14));
        
        averagePanel.add(avgIcon);
        averagePanel.add(avgLabel);
        averagePanel.add(avgAmount);
        averagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        summaryPanel.add(balancePanel);
        summaryPanel.add(Box.createVerticalStrut(10));
        summaryPanel.add(averagePanel);

        // Details section
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(CARD_COLOR);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel detailsTitle = new JLabel("Daily Details");
        detailsTitle.setFont(new Font("Arial", Font.BOLD, 16));
        detailsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel selectPrompt = new JLabel("Select a date to view details");
        selectPrompt.setFont(new Font("Arial", Font.ITALIC, 14));
        selectPrompt.setForeground(new Color(100, 100, 100));
        selectPrompt.setAlignmentX(Component.LEFT_ALIGNMENT);

        detailsPanel.add(detailsTitle);
        detailsPanel.add(Box.createVerticalStrut(10));
        detailsPanel.add(selectPrompt);

        bottomPanel.add(summaryPanel);
        bottomPanel.add(Box.createVerticalStrut(15));
        bottomPanel.add(detailsPanel);

        add(bottomPanel, BorderLayout.SOUTH);
    }
} 