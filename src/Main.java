import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                DatabaseConnection.ensureMySqlDriverLoaded();
                try (Connection connection = DatabaseConnection.getConnection()) {
                    connection.getMetaData();
                    BankSystem bankSystem = new BankSystem();
                    BankSwingUI ui = new BankSwingUI(bankSystem);
                    ui.setVisible(true);
                }
            } catch (IllegalStateException | SQLException ex) {
                JOptionPane.showMessageDialog(
                    null,
                    "Cannot connect to XAMPP MySQL.\n" + ex.getMessage(),
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}