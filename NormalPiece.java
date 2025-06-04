import java.awt.Color;
import java.awt.Graphics;

public class NormalPiece extends Piece {
    public NormalPiece(String color) {
        super(color);
    }

    @Override
    public boolean isKing() {
        return false;
    }

    @Override
    public void draw(Graphics g, Tile tile) {
        System.out.println("Rysuję pionek [" + color + "] na polu [" + tile.getRow() + "," + tile.getCol() + "]");
        g.setColor(color.equals("C") ? Color.BLACK : Color.WHITE);
        g.fillOval(10, 10, tile.getWidth() - 20, tile.getHeight() - 20);
    }
    
    
}
