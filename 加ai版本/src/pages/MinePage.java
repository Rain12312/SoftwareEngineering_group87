package pages;

import components.NavigationBar;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MinePage extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 255);
    private static final Color BUTTON_COLOR = new Color(235, 235, 255);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color HOVER_COLOR = new Color(225, 225, 255);
    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;

    public MinePage() {
        initializeFrame();
        createComponents();
    }

    private void initializeFrame() {
        setTitle("Mine");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(BACKGROUND_COLOR);
    }

    private void createComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Profile section
        addSection(mainPanel, "Sign in / Sign up", new String[]{});

        // Personalization section
        addSection(mainPanel, "Personalization", new String[]{
            "Help Center"
        });

        // Data section
        addSection(mainPanel, "Data", new String[]{
            "Back up & Restore",
            "Import / Export Bills"
        });

        // Other section
        addSection(mainPanel, "Else", new String[]{
            "App Rating",
            "Sharing",
            "Regardings"
        });

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Main container with BorderLayout
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBackground(BACKGROUND_COLOR);
        containerPanel.add(scrollPane, BorderLayout.CENTER);

        // Navigation bar
        NavigationBar navigationBar = new NavigationBar(this::handleNavigation);
        containerPanel.add(navigationBar, BorderLayout.SOUTH);

        add(containerPanel);
    }

    private void addSection(JPanel parent, String title, String[] items) {
        // Section container
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBackground(BACKGROUND_COLOR);
        sectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sectionPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Section title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sectionPanel.add(titleLabel);
        sectionPanel.add(Box.createVerticalStrut(10));

        // If this is the Sign in section, add a special button
        if (title.equals("Sign in / Sign up")) {
            JButton signInButton = createStyledButton("Sign in / Sign up");
            signInButton.setBackground(BUTTON_COLOR);
            sectionPanel.add(signInButton);
        }

        // Section items
        for (String item : items) {
            JButton button = createStyledButton(item);
            sectionPanel.add(button);
            sectionPanel.add(Box.createVerticalStrut(1)); // Small gap between buttons
        }

        parent.add(sectionPanel);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, 50));
        button.setBackground(BUTTON_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setMargin(new Insets(0, 20, 0, 20));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(HOVER_COLOR);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_COLOR);
            }
        });

        return button;
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
            case "ADD":
                new AccountingPage().setVisible(true);
                break;
        }
    }
} 