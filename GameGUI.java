import javax.swing.*;
import java.awt.*;

public class GameGUI {
    private JFrame frame;
    private JLabel statusLabel;

    public GameGUI() {
        frame = new JFrame("Warcaby Online");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(4, 1));

        JButton playButton = new JButton("Graj");
        JButton rankingButton = new JButton("Ranking");
        JButton rulesButton = new JButton("Zasady");

        statusLabel = new JLabel("", SwingConstants.CENTER);

        playButton.addActionListener(e -> startGame());
        rankingButton.addActionListener(e -> showRanking());
        rulesButton.addActionListener(e -> showRules());

        frame.add(playButton);
        frame.add(rankingButton);
        frame.add(rulesButton);
        frame.add(statusLabel);

        frame.setVisible(true);
    }

    private void startGame() {
        statusLabel.setText("Łączenie z serwerem...");
        new Thread(() -> {
            GameClient client = new GameClient();
            if (client.connectToServer()) {
                String playerColor = // Pobierz rzeczywisty kolor gracza od serwera
                SwingUtilities.invokeLater(() -> {
                    frame.dispose(); // Zamknięcie menu
                    new GamePanel(playerColor); // Ustaw poprawny kolor
                });
            } else {
                SwingUtilities.invokeLater(() -> statusLabel.setText("Błąd połączenia!"));
            }
        }).start();
    }
    

    private void showRanking() {
        JOptionPane.showMessageDialog(frame, "Ranking jeszcze nie działa!");
    }

    private void showRules() {
        JOptionPane.showMessageDialog(frame, "Warcaby to gra strategiczna...\n(Tutaj wstawisz zasady gry)");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameGUI::new);
    }
}
