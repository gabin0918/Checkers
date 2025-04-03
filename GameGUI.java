import java.awt.*;
import javax.swing.*;

/**
 * Klasa odpowiedzialna za uruchomienie graficznego interfejsu użytkownika
 * z ekranem startowym gry. Umożliwia rozpoczęcie gry, wyświetlenie zasad oraz rankingu.
 */
public class GameGUI {
    private JFrame frame; // Główne okno aplikacji
    private JLabel statusLabel; // Etykieta informująca o stanie połączenia lub błędach

    /**
     * Konstruktor tworzący interfejs głównego menu gry.
     * Inicjalizuje przyciski: Graj, Ranking, Zasady oraz etykietę statusu.
     */
    public GameGUI() {
        frame = new JFrame("Warcaby Online");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(4, 1)); // Układ pionowy 4-elementowy

        JButton playButton = new JButton("Graj");
        JButton rankingButton = new JButton("Ranking");
        JButton rulesButton = new JButton("Zasady");

        statusLabel = new JLabel("", SwingConstants.CENTER);

        // Przypisanie akcji do przycisków
        playButton.addActionListener(e -> startGame());
        rankingButton.addActionListener(e -> showRanking());
        rulesButton.addActionListener(e -> showRules());

        // Dodanie komponentów do głównego okna
        frame.add(playButton);
        frame.add(rankingButton);
        frame.add(rulesButton);
        frame.add(statusLabel);

        frame.setVisible(true); // Wyświetlenie okna
    }

    /**
     * Metoda uruchamiana po naciśnięciu przycisku „Graj”.
     * Nawiązuje połączenie z serwerem i inicjuje grę.
     */
    private void startGame() {
        statusLabel.setText("Łączenie z serwerem...");

        new Thread(() -> {
            GameClient client = new GameClient();
            String playerColor = client.connectToServer(); // Połączenie z serwerem i odbiór koloru gracza

            if (playerColor != null) {
                SwingUtilities.invokeLater(() -> {
                    frame.dispose(); // Zamknięcie menu startowego
                    GamePanel panel = new GamePanel(playerColor, client); // Utworzenie panelu gry
                    client.setGamePanel(panel); // Przekazanie panelu do klienta
                    System.out.println("GameGUI: GamePanel ustawiony w GameClient!");

                    client.startListening(); // Uruchomienie nasłuchiwania wiadomości z serwera
                });
            } else {
                statusLabel.setText("Błąd połączenia!");
            }
        }).start(); // Nowy wątek, aby nie blokować GUI
    }

    /**
     * Wyświetla komunikat informujący o niedostępności funkcji rankingu.
     */
    private void showRanking() {
        JOptionPane.showMessageDialog(frame, "Ranking jeszcze nie działa!");
    }

    /**
     * Wyświetla skrócone zasady gry w warcaby.
     */
    private void showRules() {
        JOptionPane.showMessageDialog(frame, "Warcaby to gra strategiczna...\n(Tutaj wstawisz zasady gry)");
    }

    /**
     * Metoda główna uruchamiająca aplikację.
     * Tworzy instancję klasy GameGUI w wątku GUI.
     * @param args parametry wejściowe
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameGUI::new);
    }
}
