import java.awt.GridLayout;
import javax.swing.JPanel;


public class Board extends JPanel {
    private Tile[][] tiles = new Tile[8][8];
    private GameLogic gameLogic;

    public Board() {
        setLayout(new GridLayout(8, 8));
        gameLogic = new GameLogic(this);  // Inicjalizujemy logikę gry

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                tiles[row][col] = new Tile(row, col, gameLogic); // Przekazujemy gameLogic
                add(tiles[row][col]);
            }
        }
        

        // Ustawiamy pionki na początek gry
        initializePieces();
    }

    private void initializePieces() {
        // Ustawienie pionków czarnych
        for (int row = 0; row < 3; row++) {
            for (int col = (row % 2 == 0) ? 1 : 0; col < 8; col += 2) {
                tiles[row][col].setPiece(new NormalPiece("C"));  // Czarne pionki
            }
        }

        // Ustawienie pionków białych
        for (int row = 5; row < 8; row++) {
            for (int col = (row % 2 == 0) ? 1 : 0; col < 8; col += 2) {
                tiles[row][col].setPiece(new NormalPiece("B"));  // Białe pionki
            }
        }
    }

    public void updateBoard(String boardState) {
        String[] cells = boardState.split(",");
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                String piece = cells[row * 8 + col];
                Piece newPiece = null;

                if (piece.equals("B")) {
                    newPiece = new NormalPiece("B");  // Biały pionek
                } else if (piece.equals("C")) {
                    newPiece = new NormalPiece("C");  // Czarny pionek
                } else if (piece.equals("B_king")) {
                    newPiece = new KingPiece("B");  // Biała damka
                } else if (piece.equals("C_king")) {
                    newPiece = new KingPiece("C");  // Czarna damka
                }

                tiles[row][col].setPiece(newPiece);  // Ustawiamy odpowiednią figurę
            }
        }
    }

    public Tile getTile(int row, int col) {
        return tiles[row][col];
    }
}