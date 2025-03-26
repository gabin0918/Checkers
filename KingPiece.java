import java.awt.Color;
import java.awt.Graphics;

public class KingPiece extends Piece {
    public KingPiece(String color) {
        super(color);
    }

    @Override
    public void draw(Graphics g, Tile tile) {
        g.setColor(color.equals("B") ? Color.BLACK : Color.WHITE);
        g.fillOval(10, 10, tile.getWidth() - 20, tile.getHeight() - 20); // Rysowanie podstawowej damki
        g.setColor(Color.RED);  // Oznaczenie damki
        g.fillRect(tile.getWidth() / 4, tile.getHeight() / 4, tile.getWidth() / 2, tile.getHeight() / 2); // Oznaczenie damki
    }
}
