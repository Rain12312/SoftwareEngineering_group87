package pages;

import components.Sidebar;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class BasePage extends JFrame {
    protected static final Color BACKGROUND_COLOR = new Color(245, 245, 255);
    protected static final Color TEXT_COLOR = new Color(50, 50, 50);
    protected static final Color HOVER_COLOR = new Color(225, 225, 245);
    protected static final int WIDTH = 500;
    protected static final int HEIGHT = 800;

    protected JPanel sidebarPanel;
    protected boolean isSidebarVisible = false;

    public BasePage() {
        initializeFrame();
    }

    protected void initializeFrame() {
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(BACKGROUND_COLOR);
        setResizable(false);
    }

    protected JButton createSidebarToggleButton() {
        JButton sidebarToggleButton = new JButton();
        sidebarToggleButton.setPreferredSize(new Dimension(32, 32));
        sidebarToggleButton.setFont(new Font("Arial", Font.BOLD, 18));
        sidebarToggleButton.setText("≡");
        sidebarToggleButton.setForeground(TEXT_COLOR);
        sidebarToggleButton.setBackground(BACKGROUND_COLOR);
        sidebarToggleButton.setBorderPainted(false);
        sidebarToggleButton.setFocusPainted(false);
        sidebarToggleButton.setContentAreaFilled(true);
        sidebarToggleButton.setOpaque(true);
        
        // 添加鼠标悬停效果
        sidebarToggleButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                sidebarToggleButton.setBackground(HOVER_COLOR);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                sidebarToggleButton.setBackground(BACKGROUND_COLOR);
            }
        });
        
        sidebarToggleButton.addActionListener(e -> toggleSidebar());
        return sidebarToggleButton;
    }

    protected void setupSidebar(JPanel mainPanel) {
        // 创建侧边栏
        sidebarPanel = new Sidebar(HEIGHT, this::handleSidebarAction);
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        sidebarPanel.setVisible(false);
    }

    protected void toggleSidebar() {
        isSidebarVisible = !isSidebarVisible;
        sidebarPanel.setVisible(isSidebarVisible);
        revalidate();
        repaint();
    }

    protected void handleSidebarAction(ActionEvent e) {
        String action = e.getActionCommand();
        switch (action) {
            case "月度统计":
                // TODO: 实现月度统计功能
                break;
            case "预算设置":
                // TODO: 实现预算设置功能
                break;
            case "账单导出":
                // TODO: 实现账单导出功能
                break;
            case "设置":
                // TODO: 实现设置功能
                break;
        }
        JOptionPane.showMessageDialog(this, "选择了: " + action, "菜单操作", JOptionPane.INFORMATION_MESSAGE);
    }

} 