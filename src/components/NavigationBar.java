package components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class NavigationBar extends JPanel {
    private static final String[] NAV_ITEMS = {"HOME", "DATE", "ADD", "ASSET", "MINE"};
    private static final Color NAV_BACKGROUND = new Color(245, 245, 255);
    private static final Color SELECTED_COLOR = new Color(235, 235, 255);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Map<String, String> ICONS = new HashMap<>();
    
    static {
        ICONS.put("HOME", "🏠");
        ICONS.put("DATE", "📅");
        ICONS.put("ADD", "✏️");
        ICONS.put("ASSET", "💰");
        ICONS.put("MINE", "👤");
    }
    
    public NavigationBar(ActionListener listener) {
        setLayout(new GridLayout(1, NAV_ITEMS.length));
        setBackground(NAV_BACKGROUND);
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));
        
        for (String item : NAV_ITEMS) {
            JButton btn = createNavButton(item);
            btn.addActionListener(listener);
            add(btn);
        }
    }
    
    private JButton createNavButton(String text) {
        JButton button = new JButton();
        button.setLayout(new BoxLayout(button, BoxLayout.Y_AXIS));
        
        // Icon label
        JLabel iconLabel = new JLabel(ICONS.get(text));
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setForeground(TEXT_COLOR);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Text label
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        textLabel.setForeground(TEXT_COLOR);
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        button.add(Box.createVerticalStrut(5));
        button.add(iconLabel);
        button.add(Box.createVerticalStrut(2));
        button.add(textLabel);
        button.add(Box.createVerticalStrut(5));
        
        button.setActionCommand(text);
        button.setBackground(NAV_BACKGROUND);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(SELECTED_COLOR);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(NAV_BACKGROUND);
            }
        });
        
        return button;
    }
} 