package pages;

import components.NavigationBar;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MinePage extends BasePage {
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 255);
    private static final Color BUTTON_COLOR = new Color(235, 235, 255);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color HOVER_COLOR = new Color(225, 225, 245);
    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;

    public MinePage() {
        super();
        setTitle("Mine");
        createComponents();
    }

    private void createComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Header panel with sidebar toggle button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.add(createSidebarToggleButton(), BorderLayout.WEST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Profile section
        addSection(contentPanel, "Sign in / Sign up", new String[]{});

        // Personalization section
        addSection(contentPanel, "Personalization", new String[]{
            "Help Center"
        });

        // Data section
        addSection(contentPanel, "Data", new String[]{
            "Back up & Restore",
            "Import / Export Bills"
        });

        // Other section
        addSection(contentPanel, "Else", new String[]{
            "App Rating",
            "Sharing",
            "Regardings"
        });

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
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