import java.awt.*;  
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class Tile extends JPanel {
    private final int row, col;
    private Piece piece;
    private GameLogic gameLogic;
    private static Tile selectedTile = null;  // Przechowywanie wybranego kafelka

    private static final Color LIGHT_COLOR = new Color(240, 217, 181);
    private static final Color DARK_COLOR = new Color(181, 136, 99);

    public Tile(int row, int col, GameLogic gameLogic) {
        this.row = row;
        this.col = col;
        this.gameLogic = gameLogic;
        setLayout(new BorderLayout());
        setBackground((row + col) % 2 == 0 ? LIGHT_COLOR : DARK_COLOR);

        // Nasłuchujemy kliknięcie
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Jeśli jest już wybrany kafelek
                if (selectedTile != null) {
                    // Przesuwamy pionek na nowy kafelek
                    int startRow = selectedTile.row;
                    int startCol = selectedTile.col;
                    int endRow = row;
                    int endCol = col;
                    gameLogic.makeMove(startRow, startCol, endRow, endCol);
                    selectedTile.setBackground((selectedTile.row + selectedTile.col) % 2 == 0 ? LIGHT_COLOR : DARK_COLOR);  // Resetujemy kolor
                    selectedTile = null;  // Resetujemy zaznaczony kafelek
                } else {
                    // Wybieramy pionek na kafelku
                    if (piece != null && piece.getColor().equals(gameLogic.getCurrentPlayer())) {
                        selectedTile = Tile.this;
                        setBackground(Color.YELLOW);  // Zmieniamy kolor tła, aby zaznaczyć wybrany kafelek
                    }
                }
            }
        });
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
        repaint();
    }

    public Piece getPiece() {
        return piece;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (piece != null) {
            piece.draw(g, this);
        }
    }
}
