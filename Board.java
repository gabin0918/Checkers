import java.awt.*;
import javax.swing.*;

/**
 * Klasa reprezentująca planszę gry w warcaby.
 * Tworzy siatkę 8x8 pól typu Tile oraz zarządza stanem graficznym planszy.
 */
public class Board extends JPanel {
    private Tile[][] tiles = new Tile[8][8]; // Dwuwymiarowa tablica przechowująca pola planszy
    private GamePanel panel; // Referencja do panelu gry, zawierającego planszę
    private GameLogic gameLogic; // Obiekt odpowiedzialny za logikę gry

    /**
     * Konstruktor planszy. Inicjalizuje wszystkie pola oraz przypisuje logikę gry.
     * @param panel Referencja do nadrzędnego panelu gry.
     */
    public Board(GamePanel panel) {
        this.panel = panel;
        gameLogic = new GameLogic(this, panel);

        setLayout(new GridLayout(8, 8)); // Ustawienie układu planszy jako siatki 8x8

        for (int row = 0; row < 8; row++)
            for (int col = 0; col < 8; col++) {
                tiles[row][col] = new Tile(row, col, this); // Utworzenie każdego pola
                add(tiles[row][col]); // Dodanie pola do planszy
            }
    }

    /**
     * Aktualizuje stan planszy na podstawie danych w postaci tekstowej.
     * @param boardState Tekstowa reprezentacja stanu planszy przesłana z serwera.
     */
    public void updateBoard(String boardState) {
        String[] pieces = boardState.trim().split(",");
        if (pieces.length != 64) {
            System.out.println("BŁĄD: Liczba pól nie jest równa 64! Jest: " + pieces.length);
            return;
        }

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                String pieceCode = pieces[row * 8 + col].trim();
                Piece newPiece = null;

                // Tworzenie odpowiedniego obiektu pionka na podstawie kodu
                switch (pieceCode) {
                    case "C": newPiece = new NormalPiece("C"); break;
                    case "B": newPiece = new NormalPiece("B"); break;
                    case "C_king": newPiece = new KingPiece("C"); break;
                    case "B_king": newPiece = new KingPiece("B"); break;
                    default: newPiece = null; break;
                }

                tiles[row][col].setPiece(newPiece); // Ustawienie pionka na danym polu
            }
        }

        repaint(); // Odświeżenie planszy
    }

    /**
     * Zwraca referencję do panelu gry.
     * @return panel gry
     */
    public GamePanel getGamePanel() {
        return panel;
    }

    /**
     * Wysyła ruch w formacie tekstowym do klienta.
     * @param sr wiersz początkowy
     * @param sc kolumna początkowa
     * @param er wiersz końcowy
     * @param ec kolumna końcowa
     */
    public void sendMove(int sr, int sc, int er, int ec) {
        String move = sr + "-" + sc + "-" + er + "-" + ec;
        panel.sendMove(move);
    }

    /**
     * Zwraca obiekt pola o zadanych współrzędnych.
     * @param row numer wiersza
     * @param col numer kolumny
     * @return obiekt Tile
     */
    public Tile getTile(int row, int col) {
        return tiles[row][col];
    }

    /**
     * Zwraca obiekt logiki gry.
     * @return instancja GameLogic
     */
    public GameLogic getGameLogic() {
        return gameLogic;
    }
}
