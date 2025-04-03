import java.awt.Color;
import java.awt.Graphics;

public class KingPiece extends Piece {
    public KingPiece(String color) {
        super(color);
    }

    @Override
    public void draw(Graphics g, Tile tile) {
        // Kolor damki (czarny lub biały)
        g.setColor(color.startsWith("C") ? Color.BLACK : Color.WHITE);

        g.fillOval(10, 10, tile.getWidth() - 20, tile.getHeight() - 20);

        // Zewnętrzna żółta obwódka
        g.setColor(Color.YELLOW);
        g.drawOval(10, 10, tile.getWidth() - 20, tile.getHeight() - 20);

        // Korona jako małe żółte kółko w środku
        int inset = 25;
        g.setColor(Color.YELLOW);
        g.fillOval(inset, inset, tile.getWidth() - inset * 2, tile.getHeight() - inset * 2);

        // Debug
        System.out.println("Rysuję damkę koloru: " + color + " na polu [" + tile.getRow() + "," + tile.getCol() + "]");
    }
}
