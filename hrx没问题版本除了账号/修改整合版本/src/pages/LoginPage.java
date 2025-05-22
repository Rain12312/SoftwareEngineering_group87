package pages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;
import backend.UserManager;

public class LoginPage extends JFrame {
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color BUTTON_COLOR = new Color(235, 235, 255);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color BORDER_COLOR = new Color(200, 200, 220);
    private static final int WIDTH = 500;
    private static final int HEIGHT = 800;

    private JTextField userField;
    private JPasswordField passwordField;
    private JCheckBox agreementCheckBox;
    private boolean isRegisterMode = false;
    private JLabel registerLabel;
    private JLabel forgotLabel;
    private CircleButton arrowBtn;

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
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // 顶部图标和标题
        mainPanel.add(Box.createVerticalStrut(100));
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel logoImage = new JLabel();
        logoImage.setPreferredSize(new Dimension(60, 60));
        logoImage.setIcon(new ImageIcon("resources/icons/logo.png")); // 替换为实际路径
        titlePanel.add(logoImage);
        titlePanel.add(Box.createHorizontalStrut(10));
        JLabel titleLabel = new JLabel("Group87");
        titleLabel.setFont(new Font("Arial Black", Font.BOLD, 40));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // 输入框
        userField = new RoundTextField("User name/ phone/ email");
        userField.setFont(new Font("Arial", Font.BOLD, 18));
        userField.setForeground(new Color(160, 160, 160));
        userField.setMaximumSize(new Dimension(300, 40));
        userField.setAlignmentX(Component.CENTER_ALIGNMENT);
        addPlaceholderListener(userField, "User name/ phone/ email");
        mainPanel.add(userField);
        mainPanel.add(Box.createVerticalStrut(15));
        passwordField = new RoundPasswordField("Input password...");
        passwordField.setFont(new Font("Arial", Font.BOLD, 18));
        passwordField.setForeground(new Color(160, 160, 160));
        passwordField.setMaximumSize(new Dimension(300, 40));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        addPasswordPlaceholderListener(passwordField, "Input password...");
        mainPanel.add(passwordField);
        mainPanel.add(Box.createVerticalStrut(15));

        // 忘记密码和注册
        JPanel linkPanel = new JPanel();
        linkPanel.setOpaque(false);
        linkPanel.setLayout(new BoxLayout(linkPanel, BoxLayout.X_AXIS));
        forgotLabel = createLinkLabel("Forgot password");
        registerLabel = createLinkLabel("Register");
        registerLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (isRegisterMode) {
                    isRegisterMode = false;
                    updateMode();
                } else {
                    isRegisterMode = true;
                    updateMode();
                }
            }
        });
        forgotLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (!isRegisterMode) {
                    JOptionPane.showMessageDialog(LoginPage.this, "Please contact the administrator to reset the password!", "Reset the Password", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        linkPanel.add(forgotLabel);
        linkPanel.add(Box.createHorizontalGlue());
        linkPanel.add(registerLabel);
        linkPanel.setMaximumSize(new Dimension(260, 30));
        mainPanel.add(linkPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        // 协议勾选和声明
        JPanel agreementContainer = new JPanel();
        agreementContainer.setOpaque(false);
        agreementContainer.setLayout(new BoxLayout(agreementContainer, BoxLayout.X_AXIS));
        agreementContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        agreementContainer.setMaximumSize(new Dimension(330, 40));
        agreementContainer.setPreferredSize(new Dimension(330, 40));

        agreementCheckBox = new JCheckBox();
        agreementCheckBox.setBackground(BACKGROUND_COLOR);
        agreementContainer.add(agreementCheckBox);
        agreementContainer.add(Box.createHorizontalStrut(5));

        String html = "<html><span style='font-family:Arial; font-size:11px; white-space:nowrap;'>"
                + "I have read and agree to the "
                + "<span style='color:#337ab7; text-decoration:underline; font-weight:bold; cursor:pointer;'>privacy policy</span>"
                + "</span></html>";
        JLabel agreeLabel = new JLabel(html);
        agreeLabel.setVerticalAlignment(JLabel.CENTER);
        agreeLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                JOptionPane.showMessageDialog(LoginPage.this, "This is privacy policy.", "Privacy Policy",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        agreementContainer.add(agreeLabel);
        mainPanel.add(agreementContainer);
        mainPanel.add(Box.createVerticalStrut(100));

        // 大圆形按钮
        arrowBtn = new CircleButton(null);
        arrowBtn.setPreferredSize(new Dimension(60, 60));
        arrowBtn.setMaximumSize(new Dimension(60, 60));
        arrowBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        arrowBtn.addActionListener(this::handleAction);
        mainPanel.add(arrowBtn);
        mainPanel.add(Box.createVerticalStrut(40));

        // 底部"Other Login Methods"及图标
        JPanel otherPanel = new JPanel();
        otherPanel.setOpaque(false);
        otherPanel.setLayout(new BoxLayout(otherPanel, BoxLayout.Y_AXIS));
        JLabel otherLabel = new JLabel("Other Login Methods");
        otherLabel.setFont(new Font("Arial Black", Font.BOLD, 14));
        otherLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        otherPanel.add(Box.createVerticalStrut(10));
        otherPanel.add(otherLabel);
        otherPanel.add(Box.createVerticalStrut(8));
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        iconPanel.setOpaque(false);
        String[] iconPaths = { "resources/icons/phone.png", "resources/icons/mail.png", "resources/icons/wechat.png",
                "resources/icons/bell.png" };
        for (String path : iconPaths) {
            JLabel icon = new JLabel();
            icon.setIcon(new ImageIcon(path));
            icon.setPreferredSize(new Dimension(36, 36));
            icon.setOpaque(false);
            iconPanel.add(icon);
        }
        otherPanel.add(iconPanel);
        mainPanel.add(otherPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        setContentPane(mainPanel);
        updateMode();
    }

    private void updateMode() {
        if (isRegisterMode) {
            arrowBtn.setToolTipText("注册");
            registerLabel.setText("Log in");
            forgotLabel.setText("");
        } else {
            arrowBtn.setToolTipText("登录");
            registerLabel.setText("Register");
            forgotLabel.setText("Forgot password");
        }
    }

    private JLabel createLinkLabel(String text) {
        return createLinkLabel(text, null, 14);
    }

    private JLabel createLinkLabel(String text, String message) {
        return createLinkLabel(text, message, 14);
    }

    private JLabel createLinkLabel(String text, String message, int fontSize) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(51, 122, 183));
        label.setFont(new Font("Arial", Font.BOLD, fontSize));
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        if (message != null) {
            label.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    JOptionPane.showMessageDialog(LoginPage.this, message, text, JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
        return label;
    }

    private void addPlaceholderListener(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(new Color(160, 160, 160));
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(160, 160, 160));
                }
            }
        });
    }

    private void addPasswordPlaceholderListener(JPasswordField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(new Color(160, 160, 160));
        field.setEchoChar((char) 0);
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setEchoChar('•');
                    field.setForeground(Color.BLACK);
                }
            }
            
            public void focusLost(FocusEvent e) {
                if (field.getPassword().length == 0) {
                    field.setEchoChar((char) 0);
                    field.setText(placeholder);
                    field.setForeground(new Color(160, 160, 160));
                }
            }
        });
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
        if (username.equals("User name/ phone/ email") || password.equals("Input password...")) {
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
                isRegisterMode = false;
                updateMode();
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

class RoundTextField extends JTextField {
    public RoundTextField(String placeholder) {
        super(placeholder);
        setOpaque(false);
        setFont(new Font("Arial", Font.PLAIN, 16));
        setForeground(Color.GRAY);
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(new Color(230, 230, 235));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
        super.paintComponent(g);
        g2.dispose();
    }
}

class CircleButton extends JComponent {
    private boolean hovered = false;
    private float scale = 1.0f;
    private Timer animTimer;
    private final float SCALE_HOVER = 1.18f;
    private final float SCALE_NORMAL = 1.0f;
    private List<ActionListener> listeners = new ArrayList<>();

    public CircleButton(Icon icon) {
        setPreferredSize(new Dimension(60, 60));
        setMaximumSize(new Dimension(60, 60));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setOpaque(false);
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                hovered = true;
                startScaleAnim(SCALE_HOVER);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                hovered = false;
                startScaleAnim(SCALE_NORMAL);
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                fireActionEvent();
            }
        });
    }

    public void addActionListener(ActionListener l) {
        listeners.add(l);
    }

    private void fireActionEvent() {
        ActionEvent evt = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "clicked");
        for (ActionListener l : listeners)
            l.actionPerformed(evt);
    }

    private void startScaleAnim(float target) {
        if (animTimer != null && animTimer.isRunning())
            animTimer.stop();
        animTimer = new Timer(7, null);
        animTimer.addActionListener(e -> {
            if (Math.abs(scale - target) < 0.01f) {
                scale = target;
                animTimer.stop();
                repaint();
            } else {
                scale += (target - scale) * 0.18f;
                repaint();
            }
        });
        animTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth(), h = getHeight();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.translate(w / 2, h / 2);
        g2.scale(scale, scale);
        g2.translate(-w / 2, -h / 2);
        // 阴影
        g2.setColor(new Color(80, 80, 80, 60));
        g2.fillOval(6, 8, w - 12, h - 8);
        // 圆形底色
        g2.setColor(new Color(120, 160, 255));
        g2.fillOval(0, 0, w, h);
        // 画粗箭头
        g2.setColor(Color.WHITE);
        int arrowW = w / 2, arrowH = h / 6;
        int[] xPoints = { w / 2 - arrowW / 2, w / 2 - arrowW / 2, w / 2 + arrowW / 4, w / 2 + arrowW / 4,
                w / 2 + arrowW / 2, w / 2 + arrowW / 4, w / 2 + arrowW / 4, w / 2 - arrowW / 2 };
        int[] yPoints = { h / 2 - arrowH, h / 2 + arrowH, h / 2 + arrowH, h / 2 + arrowH * 2, h / 2, h / 2 - arrowH * 2,
                h / 2 - arrowH, h / 2 - arrowH };
        g2.fillPolygon(xPoints, yPoints, xPoints.length);
        g2.dispose();
    }

    @Override
    public boolean contains(int x, int y) {
        int r = Math.min(getWidth(), getHeight()) / 2 + 50;
        int cx = getWidth() / 2, cy = getHeight() / 2;
        return (x - cx) * (x - cx) + (y - cy) * (y - cy) <= r * r;
    }
}

class RoundPasswordField extends JPasswordField {
    private String placeholder;

    public RoundPasswordField(String placeholder) {
        super(placeholder);
        this.placeholder = placeholder;
        setOpaque(false);
        setFont(new Font("Arial", Font.BOLD, 18));
        setForeground(new Color(160, 160, 160));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        setEchoChar((char) 0);
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(getPassword()).equals(placeholder)) {
                    setText("");
                    setEchoChar('•');
                    setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getPassword().length == 0) {
                    setEchoChar((char) 0);
                    setText(placeholder);
                    setForeground(new Color(160, 160, 160));
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(new Color(230, 230, 235));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
        super.paintComponent(g);
        g2.dispose();
    }
} 