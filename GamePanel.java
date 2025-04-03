import java.awt.*;
import javax.swing.*;

/**
 * Klasa reprezentująca główne okno gry z planszą oraz panelem informacyjnym.
 * Odpowiada za wyświetlanie aktualnego stanu gry, tury oraz zbitych pionków.
 */
public class GamePanel extends JFrame {
    private Board board; // Obiekt planszy gry
    private JLabel playerLabel, turnLabel; // Etykiety informacyjne o graczu i turze
    private JLabel blackCapturedLabel;     // Licznik zbitych czarnych pionków
    private JLabel whiteCapturedLabel;  
    private JLabel warningLabel;
    private int capturedBlack = 0;         // Liczba zbitych czarnych pionków
    private int capturedWhite = 0;         // Liczba zbitych białych pionków

    private String playerColor;            // Kolor przypisany do gracza
    private String currentTurn = "";       // Kolor gracza, którego aktualnie jest tura
    private GameClient client;             // Obiekt klienta połączenia sieciowego

    /**
     * Konstruktor tworzy interfejs gry oraz inicjalizuje wszystkie komponenty GUI.
     * @param playerColor Kolor przypisany do gracza ("C" lub "B")
     * @param client Obiekt klienta obsługującego komunikację z serwerem
     */
    public GamePanel(String playerColor, GameClient client) {
        this.playerColor = playerColor;
        this.client = client;
        this.client.setGamePanel(this); // Przekazanie referencji do panelu gry

        setTitle("Warcaby Online - Grasz jako " + (playerColor.equals("C") ? "Czarne" : "Białe"));
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        board = new Board(this); // Utworzenie planszy
        add(board, BorderLayout.CENTER);

        // Utworzenie panelu bocznego z informacjami
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(200, 600));

        playerLabel = new JLabel("Gracz: " + (playerColor.equals("C") ? "Czarne" : "Białe"));
        playerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        playerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        turnLabel = new JLabel("Tura: ");
        turnLabel.setFont(new Font("Arial", Font.BOLD, 16));
        turnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        blackCapturedLabel = new JLabel("Zbite czarne: 0");
        blackCapturedLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        blackCapturedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        whiteCapturedLabel = new JLabel("Zbite białe: 0");
        whiteCapturedLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        whiteCapturedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        warningLabel = new JLabel("");
        warningLabel.setFont(new Font("Arial", Font.BOLD, 14));
        warningLabel.setForeground(Color.RED);
        warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidePanel.add(warningLabel);        
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(blackCapturedLabel);
        sidePanel.add(whiteCapturedLabel);
        sidePanel.add(Box.createVerticalGlue());
        sidePanel.add(playerLabel);
        sidePanel.add(turnLabel);
        sidePanel.add(Box.createVerticalGlue());

        add(sidePanel, BorderLayout.EAST);

        setVisible(true); // Wyświetlenie okna
    }

    /**
     * Ustawia etykietę tury i aktualizuje zmienną przechowującą bieżącą turę.
     * @param turn Kolor gracza, którego aktualnie jest tura
     */
    public void setCurrentTurn(String turn) {
        this.currentTurn = turn;
        turnLabel.setText("Tura: " + (turn.equals("C") ? "Czarne" : "Białe"));
    }

    /**
     * Sprawdza, czy aktualnie jest tura gracza.
     * @return true, jeśli gracz może wykonać ruch
     */
    public boolean isPlayerTurn() {
        return playerColor.equals(currentTurn);
    }

    /**
     * Zwraca kolor przypisany do bieżącego gracza.
     * @return "C" lub "B"
     */
    public String getPlayerColor() {
        return playerColor;
    }

    /**
     * Aktualizuje stan planszy oraz liczniki zbitych pionków.
     * @param boardState Stan planszy w formie ciągu znaków
     */
    public void updateBoard(String boardState) {
        board.updateBoard(boardState);
        board.getGameLogic().highlightVulnerableTiles(playerColor); // Podświetlenie możliwych bić
        updateCapturedCount(boardState); // Aktualizacja liczników
    }

    /**
     * Wysyła ruch gracza do serwera.
     * @param move Ruch w formacie "startRow-startCol-endRow-endCol"
     */
    public void sendMove(String move) {
        client.sendMove(move);
    }

    /**
     * Oblicza i aktualizuje liczbę zbitych pionków na podstawie stanu planszy.
     * @param boardState Reprezentacja stanu planszy
     */
    private void updateCapturedCount(String boardState) {
        String[] pieces = boardState.split(",");
        int blackCount = 0;
        int whiteCount = 0;

        for (String p : pieces) {
            if (p.equals("C") || p.equals("C_king")) blackCount++;
            if (p.equals("B") || p.equals("B_king")) whiteCount++;
        }

        capturedBlack = 12 - blackCount;
        capturedWhite = 12 - whiteCount;

        blackCapturedLabel.setText("Zbite czarne: " + capturedBlack);
        whiteCapturedLabel.setText("Zbite białe: " + capturedWhite);
    }
    public void showCaptureWarning() {
        warningLabel.setText("Musisz bić!");
        new Timer(2000, e -> warningLabel.setText("")).start();
    }
    
    
}
