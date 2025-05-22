package pages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import backend.UserManager;

public class LoginPage extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 255);
    private static final Color BUTTON_COLOR = new Color(235, 235, 255);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color BORDER_COLOR = new Color(200, 200, 220);
    private static final int WIDTH = 500;
    private static final int HEIGHT = 800;

    private JTextField userField;
    private JPasswordField passwordField;
    private JCheckBox agreementCheckBox;
    private boolean isRegisterMode = false;

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
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Center panel for content
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(BACKGROUND_COLOR);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));

        // Logo and title
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel logoLabel = new JLabel("Group87");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 36));
        logoLabel.setForeground(TEXT_COLOR);
        logoPanel.add(logoLabel);
        
        centerPanel.add(Box.createVerticalStrut(100));
        centerPanel.add(logoPanel);
        centerPanel.add(Box.createVerticalStrut(5));

        // 第一部分：登录表单
        JPanel loginFormPanel = new JPanel();
        loginFormPanel.setLayout(new BoxLayout(loginFormPanel, BoxLayout.Y_AXIS));
        loginFormPanel.setBackground(BACKGROUND_COLOR);
        loginFormPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Input fields with placeholder text
        userField = createInputField("Username");
        userField.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginFormPanel.add(userField);
        loginFormPanel.add(Box.createVerticalStrut(15));

        passwordField = createPasswordField("Password");
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginFormPanel.add(passwordField);
        loginFormPanel.add(Box.createVerticalStrut(15));

        // Agreement checkbox
        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.X_AXIS));
        checkboxPanel.setBackground(BACKGROUND_COLOR);
        checkboxPanel.setMaximumSize(new Dimension(440, 40));
        checkboxPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        agreementCheckBox = new JCheckBox("I have read and agree to the");
        agreementCheckBox.setBackground(BACKGROUND_COLOR);
        agreementCheckBox.setForeground(TEXT_COLOR);
        agreementCheckBox.setFont(new Font("Arial", Font.PLAIN, 12));
        agreementCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        checkboxPanel.add(agreementCheckBox);
        
        JButton privacyButton = createLinkButton("privacy policy");
        privacyButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        checkboxPanel.add(privacyButton);

        JLabel andLabel = new JLabel(" and ");
        andLabel.setForeground(TEXT_COLOR);
        andLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        andLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        checkboxPanel.add(andLabel);
        
        JButton agreementButton = createLinkButton("user agreement");
        agreementButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        checkboxPanel.add(agreementButton);
        
        checkboxPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginFormPanel.add(checkboxPanel);

        // 第二部分：登录按钮和其他选项
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Login/Register button
        JButton actionButton = new JButton("Sign in");
        actionButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        actionButton.setMaximumSize(new Dimension(440, 45));
        actionButton.setBackground(BUTTON_COLOR);
        actionButton.setForeground(TEXT_COLOR);
        actionButton.setBorderPainted(false);
        actionButton.setFocusPainted(false);
        actionButton.setFont(new Font("Arial", Font.BOLD, 16));
        actionButton.addActionListener(this::handleAction);
        
        bottomPanel.add(actionButton);
        bottomPanel.add(Box.createVerticalStrut(15));

        // Toggle mode button
        JButton toggleButton = new JButton("Don't have an account? Sign up");
        toggleButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toggleButton.setBorderPainted(false);
        toggleButton.setContentAreaFilled(false);
        toggleButton.setFocusPainted(false);
        toggleButton.setForeground(new Color(51, 122, 183));
        toggleButton.setFont(new Font("Arial", Font.PLAIN, 14));
        toggleButton.addActionListener(e -> toggleMode(actionButton, toggleButton));
        bottomPanel.add(toggleButton);
        bottomPanel.add(Box.createVerticalStrut(15));

        // Other login options
        JLabel otherLoginLabel = new JLabel("Other Login Methods");
        otherLoginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        otherLoginLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        otherLoginLabel.setForeground(TEXT_COLOR);
        bottomPanel.add(otherLoginLabel);
        bottomPanel.add(Box.createVerticalStrut(10));

        // Social login buttons
        JPanel socialPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        socialPanel.setBackground(BACKGROUND_COLOR);
        socialPanel.setMaximumSize(new Dimension(440, 40));
        
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
        
        socialPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottomPanel.add(socialPanel);

        // 将两个主要部分添加到centerPanel
        centerPanel.add(loginFormPanel);
        centerPanel.add(Box.createVerticalStrut(100));
        centerPanel.add(bottomPanel);
        centerPanel.add(Box.createVerticalGlue());

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JTextField createInputField(String placeholder) {
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(440, 45));
        field.setPreferredSize(new Dimension(440, 45));
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
        field.setMaximumSize(new Dimension(440, 45));
        field.setPreferredSize(new Dimension(440, 45));
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
        button.setOpaque(false);
        button.setVisible(true);
        return button;
    }

    private void toggleMode(JButton actionButton, JButton toggleButton) {
        isRegisterMode = !isRegisterMode;
        actionButton.setText(isRegisterMode ? "Sign up" : "Sign in");
        toggleButton.setText(isRegisterMode ? "Already have an account? Sign in" : "Don't have an account? Sign up");
    }

    private void handleAction(ActionEvent e) {
        if (!agreementCheckBox.isSelected()) {
            JOptionPane.showMessageDialog(this,
                "Please agree to the terms and conditions first.",
                "Agreement Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = userField.getText();
        String password = new String(passwordField.getPassword());

        if (username.equals("Username") || password.equals("Password")) {
            JOptionPane.showMessageDialog(this,
                "Please enter both username and password.",
                "Input Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success;
        if (isRegisterMode) {
            success = UserManager.registerUser(username, password);
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Registration successful! Please sign in.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                toggleMode((JButton)e.getSource(), null);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Registration failed. Username might already exist.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            success = UserManager.validateUser(username, password);
            if (success) {
                dispose();
                new MainPage().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Invalid username or password.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 