import bank.gui.LoginFrame;
import javax.swing.*;

/**
 * Main entry point for Nova Bank Management System.
 * Launches the Login GUI.
 */
public class Main {
    public static void main(String[] args) {
        // Run GUI on Event Dispatch Thread (Swing requirement)
        SwingUtilities.invokeLater(() -> {
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
            new LoginFrame();
        });
    }
}
