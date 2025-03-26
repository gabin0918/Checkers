import javax.swing.*;
import java.awt.*;

public class Tile extends JPanel {
    private int row, col;
    private JLabel pieceLabel; // Etykieta do wyświetlania pionków
    private static final Color LIGHT_COLOR = new Color(240, 217, 181);
    private static final Color DARK_COLOR = new Color(181, 136, 99);

    public Tile(int row, int col) {
        this.row = row;
        this.col = col;
        setLayout(new BorderLayout());
        setBackground((row + col) % 2 == 0 ? LIGHT_COLOR : DARK_COLOR);

        pieceLabel = new JLabel();
        pieceLabel.setHorizontalAlignment(JLabel.CENTER);
        add(pieceLabel, BorderLayout.CENTER);
    }

    public void setPiece(String piece) {
        if (piece.equals("B")) {
            pieceLabel.setIcon(new ImageIcon("white_piece.png"));
        } else if (piece.equals("C")) {
            pieceLabel.setIcon(new ImageIcon("black_piece.png"));
        } else {
            pieceLabel.setIcon(null);
        }
    }

    public void highlight() {
        setBackground(Color.YELLOW);
    }

    public void resetColor() {
        setBackground((row + col) % 2 == 0 ? LIGHT_COLOR : DARK_COLOR);
    }
}
