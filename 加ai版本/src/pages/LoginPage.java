package pages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class LoginPage extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 255);
    private static final Color BUTTON_COLOR = new Color(235, 235, 255);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color BORDER_COLOR = new Color(200, 200, 220);
    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;

    public LoginPage() {
        initializeFrame();
        createComponents();
    }

    private void initializeFrame() {
        setTitle("Group87");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(BACKGROUND_COLOR);
        setResizable(false);
    }

    private void createComponents() {
        // Main panel with proper spacing
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(60, 30, 40, 30));

        // Logo and title with more space
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel logoLabel = new JLabel("Group87");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 36));
        logoLabel.setForeground(TEXT_COLOR);
        logoPanel.add(logoLabel);
        
        mainPanel.add(logoPanel);
        mainPanel.add(Box.createVerticalStrut(50));

        // Input fields with placeholder text
        JTextField userField = createInputField("User name/phone/email");
        mainPanel.add(userField);
        mainPanel.add(Box.createVerticalStrut(20));

        JPasswordField passwordField = createPasswordField("Input password...");
        mainPanel.add(passwordField);
        mainPanel.add(Box.createVerticalStrut(30));

        // Agreement checkbox with better alignment and wrapping
        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        checkboxPanel.setBackground(BACKGROUND_COLOR);
        checkboxPanel.setMaximumSize(new Dimension(340, 40));
        
        JCheckBox agreementCheckBox = new JCheckBox("I have read and agree to the");
        agreementCheckBox.setBackground(BACKGROUND_COLOR);
        agreementCheckBox.setForeground(TEXT_COLOR);
        agreementCheckBox.setFont(new Font("Arial", Font.PLAIN, 12));
        checkboxPanel.add(agreementCheckBox);
        
        JButton privacyButton = createLinkButton("privacy policy");
        JLabel andLabel = new JLabel(" and ");
        andLabel.setForeground(TEXT_COLOR);
        andLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        JButton agreementButton = createLinkButton("user agreement");
        
        checkboxPanel.add(privacyButton);
        checkboxPanel.add(andLabel);
        checkboxPanel.add(agreementButton);
        
        mainPanel.add(checkboxPanel);
        mainPanel.add(Box.createVerticalStrut(40));

        // Login button with better styling
        JButton loginButton = new JButton("Sign in / Sign up");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(340, 45));
        loginButton.setBackground(BUTTON_COLOR);
        loginButton.setForeground(TEXT_COLOR);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.addActionListener(this::handleLogin);
        
        mainPanel.add(loginButton);
        mainPanel.add(Box.createVerticalStrut(40));

        // Other login options with better spacing
        JLabel otherLoginLabel = new JLabel("Other Login Methods");
        otherLoginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        otherLoginLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        otherLoginLabel.setForeground(TEXT_COLOR);
        mainPanel.add(otherLoginLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Social login buttons with better styling
        JPanel socialPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        socialPanel.setBackground(BACKGROUND_COLOR);
        socialPanel.setMaximumSize(new Dimension(340, 40));
        
        String[] platforms = {"WeChat", "Weibo"};
        for (String platform : platforms) {
            JButton button = new JButton(platform);
            button.setPreferredSize(new Dimension(120, 35));
            button.setBackground(BUTTON_COLOR);
            button.setForeground(TEXT_COLOR);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setFont(new Font("Arial", Font.PLAIN, 14));
            socialPanel.add(button);
        }
        
        mainPanel.add(socialPanel);

        // Add everything to a scroll pane
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane);
    }

    private JTextField createInputField(String placeholder) {
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(340, 45));
        field.setPreferredSize(new Dimension(340, 45));
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_COLOR);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Add placeholder
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_COLOR);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
        
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        return field;
    }

    private JPasswordField createPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField();
        field.setMaximumSize(new Dimension(340, 45));
        field.setPreferredSize(new Dimension(340, 45));
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_COLOR);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Add placeholder
        field.setEchoChar((char)0);
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setEchoChar('•');
                    field.setForeground(TEXT_COLOR);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getPassword().length == 0) {
                    field.setEchoChar((char)0);
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
        
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        return field;
    }

    private JButton createLinkButton(String text) {
        JButton button = new JButton(text);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setForeground(new Color(51, 122, 183));
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void handleLogin(ActionEvent e) {
        dispose();
        new MainPage().setVisible(true);
    }
} 