import java.awt.Color;
import java.awt.Graphics;

public class NormalPiece extends Piece {
    public NormalPiece(String color) {
        super(color);
    }

    @Override
    public void draw(Graphics g, Tile tile) {
        g.setColor(color.equals("C") ? Color.BLACK : Color.WHITE);
        g.fillOval(15, 15, 45, 45);  // Rysowanie pionka na kafelku
    }
}
