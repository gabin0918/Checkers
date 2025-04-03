import java.io.*;
import java.net.Socket;

/**
 * Klasa klienta gry. Odpowiada za nawiązanie połączenia z serwerem,
 * odbieranie wiadomości oraz wysyłanie ruchów.
 */
public class GameClient {
    private Socket socket; // Połączenie z serwerem
    private BufferedReader in; // Strumień wejściowy do odbioru danych z serwera
    private PrintWriter out; // Strumień wyjściowy do wysyłania danych do serwera
    private String playerColor; // Kolor przypisany do gracza ("C" lub "B")
    private GamePanel gamePanel; // Referencja do obiektu GUI reprezentującego planszę gry

    /**
     * Nawiązuje połączenie z serwerem gry i odbiera przypisany kolor gracza.
     * @return Kolor gracza ("C" lub "B") lub null w przypadku błędu.
     */
    public String connectToServer() {
        try {
            socket = new Socket("localhost", 5000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String welcomeMsg = in.readLine(); // Oczekiwanie na wiadomość powitalną z kolorem
            playerColor = welcomeMsg.split(" ")[1];

            return playerColor;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Uruchamia wątek nasłuchujący wiadomości z serwera i aktualizujący stan gry.
     */
    public void startListening() {
        new Thread(() -> {
            try {
                while (true) {
                    String fullMsg = in.readLine();
                    System.out.println("ODEBRANO Z SERWERA: " + fullMsg);

                    if (fullMsg == null || fullMsg.isEmpty()) continue;

                    // Podział wiadomości na informacje o turze i stanie planszy
                    String[] tokens = fullMsg.split(",", -1); // Utrzymuje puste pola na końcu wiadomości
                    System.out.println("LICZBA TOKENS = " + tokens.length); // Oczekiwana długość: 65

                    String turnInfo = tokens[0]; // Pierwszy token zawiera informację o turze (np. "TURA:C")
                    String currentTurn = turnInfo.split(":")[1];

                    // Składanie pozostałych danych jako reprezentacja stanu planszy
                    StringBuilder boardStateBuilder = new StringBuilder();
                    for (int i = 1; i < tokens.length; i++) {
                        boardStateBuilder.append(tokens[i]).append(",");
                    }
                    String boardState = boardStateBuilder.toString();

                    // Aktualizacja stanu GUI (planszy) jeśli obiekt panelu został wcześniej ustawiony
                    if (gamePanel != null) {
                        System.out.println("TEST GameClient: currentTurn = [" + currentTurn + "]");
                        System.out.println("TEST GameClient: boardState = [" + boardState + "]");

                        gamePanel.setCurrentTurn(currentTurn);
                        gamePanel.updateBoard(boardState);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Wysyła ruch gracza do serwera.
     * @param move Ruch w formacie "startRow-startCol-endRow-endCol"
     */
    public void sendMove(String move) {
        out.println(move);
        out.flush();
    }

    /**
     * Ustawia referencję do obiektu GamePanel.
     * @param panel Obiekt GUI zawierający planszę gry.
     */
    public void setGamePanel(GamePanel panel) {
        this.gamePanel = panel;
    }
}
