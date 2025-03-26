import javax.swing.*;
import java.awt.*;

public class Board extends JPanel {
    private Tile[][] tiles = new Tile[8][8];

    public Board() {
        setLayout(new GridLayout(8, 8));

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                tiles[row][col] = new Tile(row, col);
                add(tiles[row][col]);
            }
        }
    }

    public void updateBoard(String boardState) {
        String[] cells = boardState.split(",");
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                tiles[row][col].setPiece(cells[row * 8 + col]);
            }
        }
    }

    public void highlightTile(int row, int col) {
        tiles[row][col].highlight();
    }

    public void resetHighlights() {
        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                tile.resetColor();
            }
        }
    }
}
