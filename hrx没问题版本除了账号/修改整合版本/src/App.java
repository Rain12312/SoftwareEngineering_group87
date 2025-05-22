import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import pages.LoginPage;
import java.awt.Font;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // 设置全局字体为微软雅黑，支持中文显示
                setUIFont(new FontUIResource("Microsoft YaHei", Font.PLAIN, 12));
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new LoginPage().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // 设置全局字体
    private static void setUIFont(FontUIResource f) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, f);
            }
        }
    }
}
