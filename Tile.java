import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class Tile extends JPanel {
    private final int row, col;
    private Piece piece;
    private static Tile selectedTile = null;
    private Board board;

    private static final Color LIGHT_COLOR = new Color(240, 217, 181);
    private static final Color DARK_COLOR = new Color(181, 136, 99);

    public Tile(int row, int col, Board board) {
        this.row = row;
        this.col = col;
        this.board = board;

        setLayout(new BorderLayout());
        setBackground((row + col) % 2 == 0 ? LIGHT_COLOR : DARK_COLOR);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                GamePanel panel = board.getGamePanel();
                GameLogic logic = board.getGameLogic();

                if (!panel.isPlayerTurn()) return;

                if (selectedTile != null) {
                    int sr = selectedTile.row;
                    int sc = selectedTile.col;
                    int er = row;
                    int ec = col;

                    if (logic.makeMove(sr, sc, er, ec)) {
                        selectedTile.resetHighlight();
                        selectedTile = null;
                    }
                } else {
                    if (piece != null && logic.canSelectPiece(piece, Tile.this)) {
                        selectedTile = Tile.this;
                        setBackground(Color.YELLOW);
                    } else {
                        // Nie można zaznaczyć pionka — np. inny musi bić
                        // Brak reakcji
                    }
                }
            }
        });
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
        repaint();
    }

    public void highlightRed() {
        setBackground(Color.RED);
    }

    public void resetHighlight() {
        setBackground((row + col) % 2 == 0 ? LIGHT_COLOR : DARK_COLOR);
    }

    public Piece getPiece() {
        return piece;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (piece != null) {
            piece.draw(g, this);
        }
    }
}
