package pages;

import components.NavigationBar;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;

public class AIAssistantPage extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 255);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;

    public AIAssistantPage() {
        initializeFrame();
        createComponents();
    }

    private void initializeFrame() {
        setTitle("AI Assistant");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(BACKGROUND_COLOR);
        setResizable(false);
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
        JLabel iconLabel = new JLabel("✒️", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Hello! I'm your AI Assistant", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descriptionLabel = new JLabel("<html><center>I can help you identify receipts,<br>categorize transactions, and<br>provide suggestions</center></html>", SwingConstants.CENTER);
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        descriptionLabel.setForeground(TEXT_COLOR);
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Message input area
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(Color.WHITE);
        messagePanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        messagePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JTextField messageField = new JTextField("Type your message here");
        messageField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        messageField.setForeground(Color.GRAY);

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

        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(uploadButton, BorderLayout.EAST);

        // Add components to content panel
        contentPanel.add(Box.createVerticalStrut(50));
        contentPanel.add(iconLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(descriptionLabel);
        contentPanel.add(Box.createVerticalStrut(50));
        contentPanel.add(messagePanel);

        // Add to scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
    }
} 