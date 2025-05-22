package pages;

import components.NavigationBar;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
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
    private JTextField notesField;
    private JPanel categoryPanel;

    public AccountingPage() {
        super();
        setTitle("Add Transaction");
        createComponents();
    }

    public void setPreFilledData(double amount, String category, String merchant) {
        currentAmount = String.format("%.2f", amount);
        amountLabel.setText(currentAmount);

        selectedCategory = category;
        if (categoryPanel != null) {
            for (Component comp : categoryPanel.getComponents()) {
                if (comp instanceof JToggleButton) {
                    JToggleButton button = (JToggleButton) comp;
                    if (button.getText().equals(category)) {
                        button.setSelected(true);
                        button.setBackground(SELECTED_COLOR);
                    } else {
                        button.setSelected(false);
                        button.setBackground(BUTTON_COLOR);
                    }
                }
            }
        }
    }

    private void createComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Panel for header elements and type selector
        JPanel headerAndTypePanel = new JPanel(new BorderLayout());
        headerAndTypePanel.setBackground(BACKGROUND_COLOR);
        headerAndTypePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Sidebar toggle button
        JButton sidebarButton = createSidebarToggleButton();
        headerAndTypePanel.add(sidebarButton, BorderLayout.WEST);

        // AI button
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
        headerAndTypePanel.add(aiButton, BorderLayout.EAST);

        // Type selector panel
        JPanel typePanel = createTypeSelector();
        JPanel centerTypePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerTypePanel.setBackground(BACKGROUND_COLOR);
        centerTypePanel.add(typePanel);
        headerAndTypePanel.add(centerTypePanel, BorderLayout.CENTER);
        mainPanel.add(headerAndTypePanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel categoryPanel = createCategoryPanel();
        contentPanel.add(categoryPanel);
        contentPanel.add(Box.createVerticalStrut(7));

        // Add combined amount and notes panel
        JPanel amountAndNotesPanel = createAmountAndNotesPanel();
        contentPanel.add(amountAndNotesPanel);
        contentPanel.add(Box.createVerticalStrut(7));

        JPanel numberPadPanel = createNumberPad();
        contentPanel.add(numberPadPanel);
        contentPanel.add(Box.createVerticalStrut(7));
        JButton saveButton = createSaveButton();
        contentPanel.add(saveButton);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        setupSidebar(mainPanel);
        NavigationBar navigationBar = new NavigationBar(this::handleNavigation);
        mainPanel.add(navigationBar, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private JPanel createTypeSelector() {
        RoundedPanel panel = new RoundedPanel(20);
        panel.setLayout(new GridLayout(1, 3, 10, 0));
        panel.setBackground(BUTTON_COLOR);
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        String[] types = { "PAY", "INC", "TRAN" };
        ButtonGroup group = new ButtonGroup();

        for (String type : types) {
            RoundedToggleButton button = new RoundedToggleButton(type);
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setForeground(TEXT_COLOR);
            button.setBackground(type.equals("PAY") ? ACCENT_COLOR : new Color(0, 0, 0, 0));
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setContentAreaFilled(false);
            button.setOpaque(false);
            button.setSelected(type.equals("PAY"));
            button.setPreferredSize(new Dimension(70, 30));

            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!button.getText().equals(selectedType)) {
                        button.setBackground(SELECTED_COLOR);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!button.getText().equals(selectedType)) {
                        button.setBackground(new Color(0, 0, 0, 0));
                    }
                }
            });

            button.addActionListener(e -> {
                selectedType = type;
                for (Component c : panel.getComponents()) {
                    if (c instanceof RoundedToggleButton) {
                        RoundedToggleButton b = (RoundedToggleButton) c;
                        b.setBackground(b.getText().equals(selectedType) ? ACCENT_COLOR : new Color(0, 0, 0, 0));
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
                { "FOOD", "resources/icons/food.png" },
                { "FRUIT", "resources/icons/fruit.png" },
                { "LOVE", "resources/icons/love.png" },
                { "TRIP", "resources/icons/trip.png" },
                { "EDU", "resources/icons/edu.png" },
                { "CURE", "resources/icons/cure.png" },
                { "MAKEUP", "resources/icons/beauty.png" },
                { "SWEET", "resources/icons/sweet.png" },
                { "OTHER", "resources/icons/other.png" }
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

    private JButton createCategoryButton(String text, String imagePath) {
        JButton button = new JButton();
        button.setLayout(new BoxLayout(button, BoxLayout.Y_AXIS));

        try {
            java.net.URL imageUrl = getClass().getResource("/" + imagePath);
            if (imageUrl == null) {
                System.err.println("Failed to load image: " + imagePath);
                ImageIcon icon = new ImageIcon();
                JLabel iconLabel = new JLabel(icon);
                iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                button.add(Box.createVerticalStrut(5));
                button.add(iconLabel);
            } else {
                ImageIcon icon = new ImageIcon(imageUrl);
                Image image = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                JLabel iconLabel = new JLabel(new ImageIcon(image));
                iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                button.add(Box.createVerticalStrut(5));
                button.add(iconLabel);
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + imagePath);
            e.printStackTrace();
            ImageIcon icon = new ImageIcon();
            JLabel iconLabel = new JLabel(icon);
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.add(Box.createVerticalStrut(5));
            button.add(iconLabel);
        }

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        button.add(Box.createVerticalStrut(2));
        button.add(textLabel);
        button.add(Box.createVerticalStrut(5));

        button.setForeground(TEXT_COLOR);
        button.setBackground(BACKGROUND_COLOR);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(80, 80));
        button.setMaximumSize(new Dimension(80, 80));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(SELECTED_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(selectedCategory.equals(text) ? SELECTED_COLOR : BACKGROUND_COLOR);
            }
        });

        button.addActionListener(e -> {
            selectedCategory = text;
            updateCategoryButtons(button);
        });

        return button;
    }

    private JPanel createAmountAndNotesPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // Camera Icon Button
        JButton cameraButton = new JButton();
        try {
            ImageIcon cameraIcon = new ImageIcon("resources/icons/ai.jpg");
            if (cameraIcon.getImageLoadStatus() == java.awt.MediaTracker.COMPLETE) {
                Image img = cameraIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
                cameraButton.setIcon(new ImageIcon(img));
            }
        } catch (Exception e) {
            System.err.println("Error loading camera icon.");
            e.printStackTrace();
        }
        cameraButton.setToolTipText("上传照片");
        cameraButton.setBackground(BACKGROUND_COLOR);
        cameraButton.setBorderPainted(false);
        cameraButton.setFocusPainted(false);
        cameraButton.setContentAreaFilled(true);
        cameraButton.setOpaque(true);
        cameraButton.setPreferredSize(new Dimension(40, 40));

        cameraButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int option = fileChooser.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileChooser.getSelectedFile();
                if (file != null) {
                    notesField.setText(file.getAbsolutePath());
                    notesField.setForeground(TEXT_COLOR);
                }
            }
        });

        // Notes Field
        notesField = new JTextField("add some notes....", 12);
        notesField.setFont(new Font("Arial", Font.PLAIN, 14));
        notesField.setForeground(Color.GRAY);
        notesField.setBackground(Color.WHITE);
        notesField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BUTTON_COLOR, 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        notesField.setPreferredSize(new Dimension(120, 35));

        notesField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (notesField.getText().equals("add some notes....")) {
                    notesField.setText("");
                    notesField.setForeground(TEXT_COLOR);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (notesField.getText().isEmpty()) {
                    notesField.setForeground(Color.GRAY);
                    notesField.setText("add some notes....");
                }
            }
        });

        // Amount Label
        amountLabel = new JLabel("$0.00");
        amountLabel.setFont(new Font("Arial", Font.BOLD, 28));
        amountLabel.setForeground(TEXT_COLOR);

        panel.add(cameraButton);
        panel.add(notesField);
        panel.add(amountLabel);

        return panel;
    }

    private JPanel createNumberPad() {
        JPanel panel = new JPanel(new GridLayout(4, 3, 10, 10));
        panel.setBackground(BACKGROUND_COLOR);

        String[] buttons = { "1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "0", "delete" };
        for (String label : buttons) {
            JButton button = new JButton(label);
            button.setFont(new Font("Arial", Font.BOLD, 18));
            button.setForeground(TEXT_COLOR);
            button.setBackground(BUTTON_COLOR);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(60, 60));

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
        if (input.equals("delete")) {
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
            if (c instanceof RoundedToggleButton) {
                RoundedToggleButton button = (RoundedToggleButton) c;
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

    private void updateCategoryButtons(JButton clickedButton) {
        JPanel panel = (JPanel) clickedButton.getParent();
        if (panel == null)
            return;

        for (Component c : panel.getComponents()) {
            if (c instanceof JButton) {
                JButton button = (JButton) c;
                String buttonText = "";
                if (button.getComponentCount() > 3 && button.getComponent(3) instanceof JLabel) {
                    buttonText = ((JLabel) button.getComponent(3)).getText();
                }

                button.setBackground(
                        buttonText != null && buttonText.equals(selectedCategory) ? SELECTED_COLOR : BACKGROUND_COLOR);
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
            String notes = notesField.getText();
            if (notes.equals("add some notes....")) {
                notes = "";
            }

            // 保存到CSV
            TransactionService service = new TransactionService();
            String date = LocalDate.now().toString();
            Transaction t = new Transaction(date, selectedType, selectedCategory, amount, notes);
            service.addTransaction(t);
            JOptionPane.showMessageDialog(this,
                    String.format("Transaction saved:\nType: %s\nCategory: %s\nAmount: $%.2f\nNotes: %s",
                            selectedType, selectedCategory, amount, notes),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new MainPage().setVisible(true);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to save transaction: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
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
            case "ADD":
                new AccountingPage().setVisible(true);
                break;
            case "ASSET":
                new Vision().setVisible(true);
                break;
        }
    }

    private class RoundedPanel extends JPanel {
        private int cornerRadius = 15;

        public RoundedPanel(int radius) {
            this.cornerRadius = radius;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius));

            g2.dispose();
        }

        @Override
        public void updateUI() {
            super.updateUI();
            setOpaque(false);
        }
    }

    private class RoundedToggleButton extends JToggleButton {
        private Color backgroundColor;

        public RoundedToggleButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setOpaque(false);
        }

        @Override
        public void setBackground(Color bg) {
            this.backgroundColor = bg;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (backgroundColor != null) {
                g2.setColor(backgroundColor);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 20, 20));
            }

            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            // Border is handled by the parent panel
        }
    }
}