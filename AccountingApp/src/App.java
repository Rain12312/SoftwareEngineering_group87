import javax.swing.*;
import pages.LoginPage;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new LoginPage().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
