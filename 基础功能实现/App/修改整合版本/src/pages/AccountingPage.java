package pages;

import components.NavigationBar;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import backend.Transaction;
import backend.TransactionService;
import java.time.LocalDate;
import backend.LedgerContext;
import pages.LedgerManagerDialog;

public class AccountingPage extends BasePage {
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 255);
    private static final Color BUTTON_COLOR = new Color(235, 235, 255);
    private static final Color SELECTED_COLOR = new Color(220, 220, 255);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color ACCENT_COLOR = new Color(100, 100, 255);
    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;

    private JLabel amountLabel;
    private String currentAmount = "0.00";
    private String selectedCategory = "";
    private String selectedType = "PAY";

    public AccountingPage() {
        super();
        setTitle("Add Transaction");
        createComponents();
    }

    private void createComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Header panel with sidebar toggle button, AI button, and ledger manager
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.add(createSidebarToggleButton(), BorderLayout.WEST);
        JButton aiButton = new JButton("🤖");
        aiButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        aiButton.setBorderPainted(false);
        aiButton.setContentAreaFilled(false);
        aiButton.setFocusPainted(false);
        aiButton.setToolTipText("AI助手");
        aiButton.addActionListener(e -> {
            AIAssistantPage aiPage = new AIAssistantPage();
            aiPage.setVisible(true);
        });
        headerPanel.add(aiButton, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JPanel typePanel = createTypeSelector();
        contentPanel.add(typePanel);
        contentPanel.add(Box.createVerticalStrut(20));
        JPanel categoryPanel = createCategoryPanel();
        contentPanel.add(categoryPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        JPanel amountPanel = createAmountPanel();
        contentPanel.add(amountPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        JPanel numberPadPanel = createNumberPad();
        contentPanel.add(numberPadPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        JButton saveButton = createSaveButton();
        contentPanel.add(saveButton);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        setupSidebar(mainPanel);
        NavigationBar navigationBar = new NavigationBar(this::handleNavigation);
        mainPanel.add(navigationBar, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private JPanel createTypeSelector() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 0));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        String[] types = {"PAY", "INC", "TRAN"};
        ButtonGroup group = new ButtonGroup();

        for (String type : types) {
            JToggleButton button = new JToggleButton(type);
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setForeground(TEXT_COLOR);
            button.setBackground(type.equals("PAY") ? SELECTED_COLOR : BUTTON_COLOR);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setContentAreaFilled(true);
            button.setOpaque(true);
            button.setSelected(type.equals("PAY"));

            button.addActionListener(e -> {
                selectedType = type;
                for (Component c : panel.getComponents()) {
                    if (c instanceof JToggleButton) {
                        JToggleButton b = (JToggleButton) c;
                        b.setBackground(b.getText().equals(type) ? SELECTED_COLOR : BUTTON_COLOR);
                    }
                }
            });

            group.add(button);
            panel.add(button);
        }

        return panel;
    }

    private JPanel createCategoryPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 4, 10, 10));
        panel.setBackground(BACKGROUND_COLOR);

        String[][] categories = {
            {"FOOD", "🍴"}, {"FRUIT", "🍎"}, {"LOVE", "💝"}, {"TRIP", "✈️"},
            {"EDU", "📚"}, {"CURE", "💊"}, {"GOTO", "🚗"}, {"MAKEUP", "💄"},
            {"SWEET", "🍬"}, {"OTHER", "📦"}
        };

        for (String[] category : categories) {
            JButton button = createCategoryButton(category[0], category[1]);
            panel.add(button);
        }

        // Fill remaining grid cells with empty panels
        for (int i = categories.length; i < 12; i++) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setBackground(BACKGROUND_COLOR);
            panel.add(emptyPanel);
        }

        return panel;
    }

    private JButton createCategoryButton(String text, String icon) {
        JButton button = new JButton();
        button.setLayout(new BoxLayout(button, BoxLayout.Y_AXIS));
        
        // Icon label
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Text label
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        button.add(Box.createVerticalStrut(10));
        button.add(iconLabel);
        button.add(Box.createVerticalStrut(5));
        button.add(textLabel);
        button.add(Box.createVerticalStrut(10));
        
        button.setForeground(TEXT_COLOR);
        button.setBackground(BUTTON_COLOR);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(90, 90));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(SELECTED_COLOR);
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(selectedCategory.equals(text) ? SELECTED_COLOR : BUTTON_COLOR);
            }
        });

        button.addActionListener(e -> {
            selectedCategory = text;
            updateCategoryButtons((JPanel)button.getParent());
        });

        return button;
    }

    private JPanel createAmountPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(BACKGROUND_COLOR);

        amountLabel = new JLabel("$0.00");
        amountLabel.setFont(new Font("Arial", Font.BOLD, 36));
        amountLabel.setForeground(TEXT_COLOR);
        panel.add(amountLabel);

        return panel;
    }

    private JPanel createNumberPad() {
        JPanel panel = new JPanel(new GridLayout(4, 3, 10, 10));
        panel.setBackground(BACKGROUND_COLOR);

        String[] buttons = {"1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "0", "⌫"};
        for (String label : buttons) {
            JButton button = new JButton(label);
            button.setFont(new Font("Arial", Font.BOLD, 18));
            button.setForeground(TEXT_COLOR);
            button.setBackground(BUTTON_COLOR);
            button.setBorderPainted(false);
            button.setFocusPainted(false);

            button.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(SELECTED_COLOR);
                }

                public void mouseExited(MouseEvent e) {
                    button.setBackground(BUTTON_COLOR);
                }
            });

            button.addActionListener(e -> handleNumberInput(label));
            panel.add(button);
        }

        return panel;
    }

    private JButton createSaveButton() {
        JButton button = new JButton("Save");
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(ACCENT_COLOR);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(ACCENT_COLOR.darker());
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(ACCENT_COLOR);
            }
        });

        button.addActionListener(e -> saveTransaction());
        return button;
    }

    private void handleNumberInput(String input) {
        if (input.equals("⌫")) {
            if (currentAmount.length() > 0) {
                currentAmount = currentAmount.substring(0, currentAmount.length() - 1);
                if (currentAmount.isEmpty() || currentAmount.equals("-")) {
                    currentAmount = "0.00";
                }
            }
        } else {
            if (currentAmount.equals("0.00")) {
                currentAmount = input;
            } else {
                currentAmount += input;
            }
        }
        updateAmountDisplay();
    }

    private void updateAmountDisplay() {
        try {
            double amount = Double.parseDouble(currentAmount);
            amountLabel.setText(String.format("$%.2f", amount));
        } catch (NumberFormatException e) {
            amountLabel.setText("$" + currentAmount);
        }
    }

    private void updateTypeButtons(JPanel panel) {
        for (Component c : panel.getComponents()) {
            if (c instanceof JToggleButton) {
                JToggleButton button = (JToggleButton) c;
                if (button.getText().equals(selectedType)) {
                    button.setSelected(true);
                    button.setBackground(SELECTED_COLOR);
                } else {
                    button.setSelected(false);
                    button.setBackground(BUTTON_COLOR);
                }
            }
        }
    }

    private void updateCategoryButtons(JPanel panel) {
        for (Component c : panel.getComponents()) {
            if (c instanceof JButton) {
                JButton button = (JButton) c;
                String buttonText = button.getText().replaceAll("<[^>]*>", "").split("\n")[1];
                button.setBackground(buttonText.equals(selectedCategory) ? SELECTED_COLOR : BUTTON_COLOR);
            }
        }
    }

    private void saveTransaction() {
        if (selectedCategory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a category", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double amount = Double.parseDouble(currentAmount);
            if (amount == 0) {
                JOptionPane.showMessageDialog(this, "Please enter an amount", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // 保存到CSV
            TransactionService service = new TransactionService();
            String date = LocalDate.now().toString();
            Transaction t = new Transaction(date, selectedType, selectedCategory, amount);
            service.addTransaction(t);
            JOptionPane.showMessageDialog(this,
                String.format("Transaction saved:\nType: %s\nCategory: %s\nAmount: $%.2f",
                selectedType, selectedCategory, amount),
                "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new MainPage().setVisible(true);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to save transaction: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleNavigation(ActionEvent e) {
        String command = e.getActionCommand();
        dispose();
        switch (command) {
            case "HOME":
                new MainPage().setVisible(true);
                break;
            case "DATE":
                new CalendarPage().setVisible(true);
                break;
            case "MINE":
                new MinePage().setVisible(true);
                break;
        }
    }
} 