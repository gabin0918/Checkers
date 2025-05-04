import java.awt.*;
import javax.swing.*;

public class LoginGUI {
    private JFrame frame;
    private JLabel statusLabel;

    public LoginGUI() {
        frame = new JFrame("Warcaby Online");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(4, 1));

        JButton makeAccount = new JButton("Stwórz konto");
        JButton login = new JButton("Zaloguj się");
        JButton guest = new JButton("Graj jako gość");

        statusLabel = new JLabel("", SwingConstants.CENTER);

        // Dodanie komponentów
        frame.add(makeAccount);
        frame.add(login);
        frame.add(guest);
        frame.add(statusLabel);

        // Akcje przycisków
        makeAccount.addActionListener(e -> openSignUp());
        login.addActionListener(e -> openLogin());
        guest.addActionListener(e -> openAsGuest());

        frame.setVisible(true);
        frame.setLocationRelativeTo(null);

    }

    private void openSignUp() {
        frame.dispose();
        new SignUpPanel();
    }

    private void openLogin() {
        frame.dispose();
        new LoginPanel();
    }

    private void openAsGuest() {
        frame.dispose();
        String guestName = "Gość" + (int)(Math.random() * 10000);
        SwingUtilities.invokeLater(() -> new GameGUI(guestName));
    }
    
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginGUI::new);
    }
}
