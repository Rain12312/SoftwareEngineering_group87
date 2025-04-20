package pages;

import components.NavigationBar;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;

public class AIAssistantPage extends BasePage {
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 255);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color DESCRIPTION_COLOR = new Color(120, 120, 120);
    private static final int WIDTH = 500;
    private static final int HEIGHT = 800;

    public AIAssistantPage() {
        super();
        setTitle("AI Assistant");
        createComponents();
    }

    private void createComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Logo and Title
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(BACKGROUND_COLOR);
        centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Icon container to ensure perfect centering
        JPanel iconContainer = new JPanel(new GridBagLayout());
        iconContainer.setBackground(BACKGROUND_COLOR);
        iconContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        JLabel iconLabel = new JLabel("✒️", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 62, 0, 0);
        iconContainer.add(iconLabel, gbc);

        JLabel titleLabel = new JLabel("Hello! I'm your AI Assistant", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descriptionLabel = new JLabel("<html><center>I can help you identify receipts,<br>categorize transactions, and<br>provide suggestions</center></html>", SwingConstants.CENTER);
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        descriptionLabel.setForeground(DESCRIPTION_COLOR);
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to center panel
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(iconContainer);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(descriptionLabel);
        centerPanel.add(Box.createVerticalGlue());

        // Add center panel to content panel
        contentPanel.add(Box.createVerticalGlue());
        contentPanel.add(centerPanel);
        contentPanel.add(Box.createVerticalGlue());

        // Add to scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Create a bottom container panel
        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setBackground(BACKGROUND_COLOR);

        // Message input area
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(Color.WHITE);
        messagePanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        messagePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JTextField messageField = new JTextField("Type your message here");
        messageField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        messageField.setForeground(Color.GRAY);

        // Add focus listener to handle placeholder text
        messageField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (messageField.getText().equals("Type your message here")) {
                    messageField.setText("");
                    messageField.setForeground(TEXT_COLOR);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (messageField.getText().isEmpty()) {
                    messageField.setText("Type your message here");
                    messageField.setForeground(Color.GRAY);
                }
            }
        });

        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Send button
        JButton sendButton = new JButton("▲");
        sendButton.setFont(new Font("Arial", Font.BOLD, 20));
        sendButton.setForeground(TEXT_COLOR);
        sendButton.setBorderPainted(false);
        sendButton.setContentAreaFilled(false);
        sendButton.setFocusPainted(false);

        // Add action listener for the send button
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText();
                if (!message.isEmpty() && !message.equals("Type your message here")) {
                    JOptionPane.showMessageDialog(AIAssistantPage.this, 
                        "Message sent successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    messageField.setText("");
                }
            }
        });

        // Upload button
        JButton uploadButton = new JButton("📷");
        uploadButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        uploadButton.setBorderPainted(false);
        uploadButton.setContentAreaFilled(false);
        uploadButton.setFocusPainted(false);
        
        // Add action listener for the upload button
        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif");
                fileChooser.setFileFilter(filter);
                
                int result = fileChooser.showOpenDialog(AIAssistantPage.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    // Here you can handle the selected image file
                    JOptionPane.showMessageDialog(AIAssistantPage.this, 
                        "Selected image: " + selectedFile.getName());
                }
            }
        });

        // Add buttons to the button panel
        buttonPanel.add(sendButton);
        buttonPanel.add(uploadButton);

        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(buttonPanel, BorderLayout.EAST);

        // Add message panel to bottom container with padding
        JPanel messageWrapper = new JPanel(new BorderLayout());
        messageWrapper.setBackground(BACKGROUND_COLOR);
        messageWrapper.add(messagePanel, BorderLayout.CENTER);
        messageWrapper.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20)); // Add padding
        bottomContainer.add(messageWrapper, BorderLayout.CENTER);

        // Add navigation bar to bottom container
        NavigationBar navigationBar = new NavigationBar(this::handleNavigation);
        bottomContainer.add(navigationBar, BorderLayout.SOUTH);

        // Add bottom container to main panel
        mainPanel.add(bottomContainer, BorderLayout.SOUTH);

        add(mainPanel);
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