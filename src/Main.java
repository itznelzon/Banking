import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BankSystem bankSystem = new BankSystem();
            BankSwingUI ui = new BankSwingUI(bankSystem);
            ui.setVisible(true);
        });
    }
}