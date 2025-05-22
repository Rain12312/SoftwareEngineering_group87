package pages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import components.NavigationBar;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;
import backend.Transaction;
import backend.TransactionService;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Vision extends BasePage {
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 255);
    private static final Color BUTTON_COLOR = new Color(235, 235, 255);
    private static final Color SELECTED_COLOR = new Color(220, 220, 255);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    
    private String currentTimeRange = "Week"; // 当前选中的时间范围
    private YearMonth currentYearMonth = YearMonth.now();
    private LocalDate currentDate = LocalDate.now();
    private JLabel timeRangeLabel;
    private JPanel timeRangePanel;
    private JPanel dateNavPanel;
    private LineChartPanel lineChartPanel;
    private JPanel typeRangePanel;
    private String currentTypeRange = "Inc";
    private PieChartPanel pieChartPanel; // 新增饼状图面板
    private String lastPieType = "Inc"; // 记录上次饼状图类型

    public Vision() {
        super();
        setTitle("Asset Visualization");
        createComponents();
        // 默认选择"Week"和"Inc"
        currentTimeRange = "Week";
        currentTypeRange = "Inc";
        if (timeRangePanel instanceof TimeRangeBar) {
            ((TimeRangeBar) timeRangePanel).updateTimeRangeButtons();
        }
        if (typeRangePanel instanceof TypeRangeBar) {
            ((TypeRangeBar) typeRangePanel).updateTypeRangeButtons();
        }
        if (lineChartPanel != null) lineChartPanel.repaint();
    }

    private void createComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Header panel with sidebar toggle button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.add(createSidebarToggleButton(), BorderLayout.WEST);
        
        // Time range navigation panel（新设计，居中且变窄）
        JPanel timeRangeBarWrapper = new JPanel();
        timeRangeBarWrapper.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        timeRangeBarWrapper.setOpaque(false);
        timeRangeBarWrapper.setBorder(BorderFactory.createEmptyBorder(0, 46, 0, 0)); // 左边距
        timeRangePanel = new TimeRangeBar();
        timeRangePanel.setMaximumSize(new Dimension(320, 40)); // 限制最大宽度
        timeRangePanel.setPreferredSize(new Dimension(320, 40));
        timeRangeBarWrapper.add(timeRangePanel);
        headerPanel.add(timeRangeBarWrapper, BorderLayout.CENTER);
        
        // Date navigation panel
        dateNavPanel = createDateNavPanel();
        headerPanel.add(dateNavPanel, BorderLayout.SOUTH);
        
        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setPreferredSize(new Dimension(500, 700));
        
        // 折线图面板
        lineChartPanel = new LineChartPanel();
        lineChartPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(lineChartPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        
        // 饼状图面板
        pieChartPanel = new PieChartPanel();
        pieChartPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(pieChartPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        
        // 类型切换栏
        JPanel typeRangeBarWrapper = new JPanel();
        typeRangeBarWrapper.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        typeRangeBarWrapper.setOpaque(false);
        typeRangePanel = new TypeRangeBar();
        typeRangePanel.setMaximumSize(new Dimension(320, 40));
        typeRangePanel.setPreferredSize(new Dimension(320, 40));
        typeRangeBarWrapper.add(typeRangePanel);
        typeRangeBarWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(typeRangeBarWrapper);
        
        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Setup sidebar
        setupSidebar(mainPanel);
        
        // Navigation bar
        NavigationBar navigationBar = new NavigationBar(this::handleNavigation);
        mainPanel.add(navigationBar, BorderLayout.SOUTH);
        
        add(mainPanel);
    }

    private JPanel createTimeRangePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        panel.setBackground(BACKGROUND_COLOR);
        
        String[] timeRanges = {"Year", "Month", "Week"};
        ButtonGroup group = new ButtonGroup();
        
        for (String range : timeRanges) {
            JToggleButton button = new JToggleButton(range);
            button.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
            button.setForeground(TEXT_COLOR);
            button.setBackground(range.equals(currentTimeRange) ? SELECTED_COLOR : BUTTON_COLOR);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(60, 30));
            
            button.addActionListener(e -> {
                currentTimeRange = range;
                updateTimeRangeButtons();
                updateDateNavPanel();
            });
            
            group.add(button);
            panel.add(button);
        }
        
        return panel;
    }

    private JPanel createDateNavPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JButton prevButton = new JButton("←");
        JButton nextButton = new JButton("→");
        for (JButton button : new JButton[]{prevButton, nextButton}) {
            button.setFont(new Font("Microsoft YaHei", Font.BOLD, 26));
            button.setForeground(TEXT_COLOR);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
        }
        prevButton.addActionListener(e -> navigateDate(-1));
        nextButton.addActionListener(e -> navigateDate(1));

        timeRangeLabel = new JLabel("", SwingConstants.CENTER);
        timeRangeLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 18));
        timeRangeLabel.setForeground(TEXT_COLOR);
        timeRangeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createHorizontalGlue());
        panel.add(prevButton);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(timeRangeLabel);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(nextButton);
        panel.add(Box.createHorizontalGlue());

        updateDateNavPanel();
        return panel;
    }

    private void updateTimeRangeButtons() {
        if (timeRangePanel instanceof TimeRangeBar) {
            ((TimeRangeBar) timeRangePanel).updateTimeRangeButtons();
        }
    }

    private void updateDateNavPanel() {
        String label = "";
        switch (currentTimeRange) {
            case "Year":
                label = String.format("%d Year", currentYearMonth.getYear());
                break;
            case "Month":
                label = String.format("%d/%d", currentYearMonth.getYear(), currentYearMonth.getMonthValue());
                break;
            case "Week":
                WeekFields weekFields = WeekFields.of(Locale.getDefault());
                int weekNumber = currentDate.get(weekFields.weekOfWeekBasedYear());
                label = String.format("Week %d, %d", weekNumber, currentDate.getYear());
                break;
        }
        timeRangeLabel.setText(label);
        // 折线图刷新
        if (lineChartPanel != null) lineChartPanel.repaint();
        // 饼状图刷新（Rem时不刷新）
        if (pieChartPanel != null && !"Rem".equals(currentTypeRange)) {
            lastPieType = currentTypeRange;
            pieChartPanel.repaint();
        }
    }

    private void navigateDate(int direction) {
        switch (currentTimeRange) {
            case "Year":
                currentYearMonth = currentYearMonth.plusYears(direction);
                currentDate = currentDate.plusYears(direction);
                break;
            case "Month":
                currentYearMonth = currentYearMonth.plusMonths(direction);
                currentDate = currentDate.plusMonths(direction);
                break;
            case "Week":
                currentDate = currentDate.plusWeeks(direction);
                currentYearMonth = YearMonth.from(currentDate);
                break;
        }
        updateDateNavPanel();
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
            case "ASSET":
                new Vision().setVisible(true);
                break;
        }
    }

    // 顶部时间范围导航条组件
    private class TimeRangeBar extends JPanel {
        private final String[] RANGES = {"Year", "Month", "Week"};
        private JButton[] buttons = new JButton[RANGES.length];
        public TimeRangeBar() {
            setLayout(new GridLayout(1, RANGES.length, 0, 0));
            setBackground(BACKGROUND_COLOR);
            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            for (int i = 0; i < RANGES.length; i++) {
                JButton btn = new JButton(RANGES[i]);
                btn.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
                btn.setForeground(TEXT_COLOR);
                btn.setBackground(BUTTON_COLOR);
                btn.setBorderPainted(false);
                btn.setFocusPainted(false);
                btn.setActionCommand(RANGES[i]);
                btn.addActionListener(e -> {
                    currentTimeRange = btn.getText();
                    updateTimeRangeButtons();
                    updateDateNavPanel();
                });
                btn.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        if (!btn.getText().equals(currentTimeRange))
                            btn.setBackground(SELECTED_COLOR);
                    }
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        if (!btn.getText().equals(currentTimeRange))
                            btn.setBackground(BUTTON_COLOR);
                    }
                });
                buttons[i] = btn;
                add(btn);
            }
            updateTimeRangeButtons();
        }
        public void updateTimeRangeButtons() {
            for (JButton btn : buttons) {
                if (btn.getText().equals(currentTimeRange)) {
                    btn.setBackground(SELECTED_COLOR);
                } else {
                    btn.setBackground(BUTTON_COLOR);
                }
            }
        }
    }

    // 折线图面板
    private class LineChartPanel extends JPanel {
        public LineChartPanel() {
            setPreferredSize(new Dimension(500, 220));
            setBackground(BACKGROUND_COLOR);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if ("Total".equals(currentTimeRange)) return;
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int margin = 50;
            int chartH = h - 60;
            int chartY = -10;
            List<Double> values = new ArrayList<>();
            List<String> labels = new ArrayList<>();
            String type = currentTypeRange;
            // 取数据
            try {
                TransactionService service = new TransactionService();
                List<Transaction> all = service.getAllTransactions();
                if ("Year".equals(currentTimeRange)) {
                    int year = currentYearMonth.getYear();
                    double[] monthInc = new double[12];
                    double[] monthExp = new double[12];
                    String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                    for (Transaction t : all) {
                        LocalDate d = LocalDate.parse(t.getDate());
                        if (d.getYear() == year) {
                            if ("INC".equals(t.getType())) monthInc[d.getMonthValue() - 1] += t.getAmount();
                            if ("PAY".equals(t.getType())) monthExp[d.getMonthValue() - 1] += t.getAmount();
                        }
                    }
                    for (int i = 0; i < 12; i++) {
                        if ("Inc".equals(type)) values.add(monthInc[i]);
                        else if ("Exp".equals(type)) values.add(monthExp[i]);
                        else if ("Rem".equals(type)) values.add(monthInc[i] - monthExp[i]);
                        labels.add(monthNames[i]);
                    }
                } else if ("Month".equals(currentTimeRange)) {
                    int year = currentYearMonth.getYear();
                    int month = currentYearMonth.getMonthValue();
                    int[] days = {1, 5, 9, 13, 17, 21, 25, 29};
                    double[] dayInc = new double[days.length];
                    double[] dayExp = new double[days.length];
                    for (Transaction t : all) {
                        LocalDate d = LocalDate.parse(t.getDate());
                        if (d.getYear() == year && d.getMonthValue() == month) {
                            for (int i = 0; i < days.length; i++) {
                                int start = days[i];
                                int end = (i == days.length - 1) ? 31 : days[i + 1];
                                if (d.getDayOfMonth() >= start && d.getDayOfMonth() < end) {
                                    if ("INC".equals(t.getType())) dayInc[i] += t.getAmount();
                                    if ("PAY".equals(t.getType())) dayExp[i] += t.getAmount();
                                    break;
                                }
                            }
                        }
                    }
                    for (int i = 0; i < days.length; i++) {
                        if ("Inc".equals(type)) values.add(dayInc[i]);
                        else if ("Exp".equals(type)) values.add(dayExp[i]);
                        else if ("Rem".equals(type)) values.add(dayInc[i] - dayExp[i]);
                        int day = days[i];
                        String suffix = "th";
                        if (day % 10 == 1 && day != 11) suffix = "st";
                        else if (day % 10 == 2 && day != 12) suffix = "nd";
                        else if (day % 10 == 3 && day != 13) suffix = "rd";
                        labels.add(day + suffix);
                    }
                } else if ("Week".equals(currentTimeRange)) {
                    LocalDate monday = currentDate.with(java.time.DayOfWeek.MONDAY);
                    double[] weekInc = new double[7];
                    double[] weekExp = new double[7];
                    for (Transaction t : all) {
                        LocalDate d = LocalDate.parse(t.getDate());
                        if (!d.isBefore(monday) && !d.isAfter(monday.plusDays(6))) {
                            int idx = d.getDayOfWeek().getValue() - 1;
                            if ("INC".equals(t.getType())) weekInc[idx] += t.getAmount();
                            if ("PAY".equals(t.getType())) weekExp[idx] += t.getAmount();
                        }
                    }
                    String[] weekDays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
                    for (int i = 0; i < 7; i++) {
                        if ("Inc".equals(type)) values.add(weekInc[i]);
                        else if ("Exp".equals(type)) values.add(weekExp[i]);
                        else if ("Rem".equals(type)) values.add(weekInc[i] - weekExp[i]);
                        labels.add(weekDays[i]);
                    }
                }
            } catch (Exception e) {}
            if (values.isEmpty()) return;
            // 计算最大值、均值
            double max = values.stream().mapToDouble(Double::doubleValue).max().orElse(0);
            double avg = values.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            double min = values.stream().mapToDouble(Double::doubleValue).min().orElse(0);
            double yMax = max * 1.2;
            double yMin = 0;
            if ("Rem".equals(type)) {
                yMin = min * 1.7;
                if (yMin > 0) yMin = 0;
            }
            if (yMax == yMin) yMax = yMin + 1;
            // 坐标点
            int n = values.size();
            int x0 = margin;
            int x1 = w - margin;
            int y0 = chartY;
            int y1 = chartY + chartH;
            int[] xs = new int[n];
            int[] ys = new int[n];
            for (int i = 0; i < n; i++) {
                xs[i] = x0 + (x1 - x0) * i / (n - 1);
                double v = values.get(i);
                double percent = (v - yMin) / (yMax - yMin);
                ys[i] = y1 - (int) (percent * chartH * 0.8);
            }
            // 背景虚线网格
            g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4, 4}, 0));
            g2.setColor(new Color(180, 200, 255));
            int yGridNum = 5;
            for (int i = 0; i <= yGridNum; i++) {
                int y = y1 - (int) (chartH * 0.8 * i / yGridNum);
                g2.drawLine(x0, y, x1, y);
            }
            for (int i = 0; i < n; i++) {
                g2.drawLine(xs[i], y1, xs[i], y0 + 10);
            }
            g2.setStroke(new BasicStroke(2.2f));
            // 折线
            g2.setColor(new Color(100, 120, 220));
            for (int i = 0; i < n - 1; i++) {
                g2.drawLine(xs[i], ys[i], xs[i + 1], ys[i + 1]);
            }
            // 点
            g2.setColor(new Color(80, 80, 180));
            for (int i = 0; i < n; i++) {
                g2.fillOval(xs[i] - 4, ys[i] - 4, 8, 8);
            }
            // 横坐标标签
            g2.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
            g2.setColor(TEXT_COLOR);
            for (int i = 0; i < n; i++) {
                int strW = g2.getFontMetrics().stringWidth(labels.get(i));
                g2.drawString(labels.get(i), xs[i] - strW / 2, y1 + 35);
            }
            // 纵坐标刻度和数值
            g2.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
            g2.setColor(new Color(100, 120, 220));
            for (int i = 0; i <= yGridNum; i++) {
                double v = yMin + (yMax - yMin) * i / yGridNum;
                int y = y1 - (int) (chartH * 0.8 * i / yGridNum);
                String vStr = String.format("%.0f", v);
                int strW = g2.getFontMetrics().stringWidth(vStr);
                g2.drawString(vStr, x0 - strW - 8, y + 5);
            }
            // 最大值点标注
            int maxIdx = 0;
            for (int i = 1; i < n; i++) if (values.get(i) > values.get(maxIdx)) maxIdx = i;
            g2.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
            g2.setColor(new Color(60, 100, 220));
            String maxStr = String.format("%.2f", values.get(maxIdx));
            int maxStrW = g2.getFontMetrics().stringWidth(maxStr);
            g2.drawString(maxStr, xs[maxIdx] - maxStrW / 2, ys[maxIdx] - 10);

            // Rem类型且存在负值时，最小值点标注
            if ("Rem".equals(type)) {
                double minVal = values.get(0);
                int minIdx = 0;
                for (int i = 1; i < n; i++) if (values.get(i) < minVal) { minVal = values.get(i); minIdx = i; }
                if (minVal < 0) {
                    String minStr = String.format("%.2f", minVal);
                    int minStrW = g2.getFontMetrics().stringWidth(minStr);
                    g2.drawString(minStr, xs[minIdx] - minStrW / 2, ys[minIdx] + 20);
                }
            }
        }
    }

    // 类型切换栏组件
    private class TypeRangeBar extends JPanel {
        private final String[] TYPES = {"Inc", "Exp", "Rem"};
        private JButton[] buttons = new JButton[TYPES.length];
        public TypeRangeBar() {
            setLayout(new GridLayout(1, TYPES.length, 0, 0));
            setBackground(BACKGROUND_COLOR);
            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            for (int i = 0; i < TYPES.length; i++) {
                JButton btn = new JButton(TYPES[i]);
                btn.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
                btn.setForeground(TEXT_COLOR);
                btn.setBackground(BUTTON_COLOR);
                btn.setBorderPainted(false);
                btn.setFocusPainted(false);
                btn.setActionCommand(TYPES[i]);
                btn.addActionListener(e -> {
                    currentTypeRange = btn.getText();
                    updateTypeRangeButtons();
                    if (lineChartPanel != null) lineChartPanel.repaint();
                    // 饼状图联动（Rem时不刷新）
                    if (pieChartPanel != null && !"Rem".equals(currentTypeRange)) {
                        lastPieType = currentTypeRange;
                        pieChartPanel.repaint();
                    }
                });
                btn.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        if (!btn.getText().equals(currentTypeRange))
                            btn.setBackground(SELECTED_COLOR);
                    }
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        if (!btn.getText().equals(currentTypeRange))
                            btn.setBackground(BUTTON_COLOR);
                    }
                });
                buttons[i] = btn;
                add(btn);
            }
            updateTypeRangeButtons();
        }
        public void updateTypeRangeButtons() {
            for (JButton btn : buttons) {
                if (btn.getText().equals(currentTypeRange)) {
                    btn.setBackground(SELECTED_COLOR);
                } else {
                    btn.setBackground(BUTTON_COLOR);
                }
            }
        }
    }

    // 饼状图面板
    private class PieChartPanel extends JPanel {
        public PieChartPanel() {
            setPreferredSize(new Dimension(400, 220));
            setMaximumSize(new Dimension(500, 240));
            setMinimumSize(new Dimension(300, 120));
            setBackground(BACKGROUND_COLOR);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            String type = lastPieType; // Rem时保持上次Inc/Exp
            if ("Rem".equals(currentTypeRange)) type = lastPieType;
            else type = currentTypeRange;
            Map<String, Double> categoryMap = new HashMap<>();
            try {
                TransactionService service = new TransactionService();
                List<Transaction> all = service.getAllTransactions();
                if ("Year".equals(currentTimeRange)) {
                    int year = currentYearMonth.getYear();
                    for (Transaction t : all) {
                        LocalDate d = LocalDate.parse(t.getDate());
                        if (d.getYear() == year) {
                            if (("Inc".equals(type) && "INC".equals(t.getType())) || ("Exp".equals(type) && "PAY".equals(t.getType()))) {
                                categoryMap.put(t.getCategory(), categoryMap.getOrDefault(t.getCategory(), 0.0) + t.getAmount());
                            }
                        }
                    }
                } else if ("Month".equals(currentTimeRange)) {
                    int year = currentYearMonth.getYear();
                    int month = currentYearMonth.getMonthValue();
                    for (Transaction t : all) {
                        LocalDate d = LocalDate.parse(t.getDate());
                        if (d.getYear() == year && d.getMonthValue() == month) {
                            if (("Inc".equals(type) && "INC".equals(t.getType())) || ("Exp".equals(type) && "PAY".equals(t.getType()))) {
                                categoryMap.put(t.getCategory(), categoryMap.getOrDefault(t.getCategory(), 0.0) + t.getAmount());
                            }
                        }
                    }
                } else if ("Week".equals(currentTimeRange)) {
                    LocalDate monday = currentDate.with(java.time.DayOfWeek.MONDAY);
                    for (Transaction t : all) {
                        LocalDate d = LocalDate.parse(t.getDate());
                        if (!d.isBefore(monday) && !d.isAfter(monday.plusDays(6))) {
                            if (("Inc".equals(type) && "INC".equals(t.getType())) || ("Exp".equals(type) && "PAY".equals(t.getType()))) {
                                categoryMap.put(t.getCategory(), categoryMap.getOrDefault(t.getCategory(), 0.0) + t.getAmount());
                            }
                        }
                    }
                }
            } catch (Exception e) {}
            if (categoryMap.isEmpty()) return;
            double total = categoryMap.values().stream().mapToDouble(Double::doubleValue).sum();
            if (total == 0) return;
            // 画饼状图
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int pieSize = Math.min(w, h) - 45;
            int cx = w / 3 - 18; // 再向左移动20像素
            int cy = h / 2 + 10;
            int r = pieSize / 2;
            int x = cx - r;
            int y = cy - r - 28;
            // 颜色列表
            Color[] colors = {new Color(100,120,220), new Color(80,180,180), new Color(220,120,120), new Color(180,180,80), new Color(120,80,180), new Color(80,120,80), new Color(200,160,80), new Color(120,120,220)};
            int colorIdx = 0;
            double curAngle = 0;
            int i = 0;
            for (Map.Entry<String, Double> entry : categoryMap.entrySet()) {
                double value = entry.getValue();
                double angle = value / total * 360.0;
                g2.setColor(colors[i % colors.length]);
                g2.fillArc(x, y, pieSize, pieSize, (int) curAngle, (int) Math.round(angle));
                curAngle += angle;
                i++;
            }
            // 图例
            int legendX = cx + r + 60; // 从+20改为+50，图例右移
            int legendY = cy - r - 37;
            i = 0;
            for (Map.Entry<String, Double> entry : categoryMap.entrySet()) {
                g2.setColor(colors[i % colors.length]);
                g2.fillRect(legendX, legendY + i * 24, 18, 18);
                g2.setColor(TEXT_COLOR);
                String label = entry.getKey() + " " + String.format("%.2f", entry.getValue());
                g2.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
                g2.drawString(label, legendX + 26, legendY + i * 24 + 15);
                i++;
            }
        }
    }
} 