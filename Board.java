import java.awt.*;
import javax.swing.*;

public class Board extends JPanel {
    private Tile[][] tiles = new Tile[8][8];
    private GamePanel panel;
    private GameLogic gameLogic;
    private JLabel infoLabel;

    public Board(GamePanel panel) {
        this.panel = panel;
        this.gameLogic = new GameLogic(this, panel);

        setLayout(new BorderLayout());

        infoLabel = new JLabel("", SwingConstants.CENTER);
        updateInfoLabel("C"); // startowo
        add(infoLabel, BorderLayout.NORTH);

        JPanel boardGrid = new JPanel(new GridLayout(8, 8));

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                tiles[row][col] = new Tile(row, col, this);
                boardGrid.add(tiles[row][col]);
            }
        }

        add(boardGrid, BorderLayout.CENTER);
    }

    public void updateBoard(String boardState) {
        String[] pieces = boardState.trim().split(",");
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                String pieceCode = pieces[row * 8 + col];
                Piece newPiece = switch (pieceCode) {
                    case "C" -> new NormalPiece("C");
                    case "B" -> new NormalPiece("B");
                    case "C_king" -> new KingPiece("C");
                    case "B_king" -> new KingPiece("B");
                    default -> null;
                };
                tiles[row][col].setPiece(newPiece);
                System.out.println("[BOARD] Ustawiono " + pieceCode + " na polu [" + row + "," + col + "]");
            }
        }
        repaint();
    }
    

    public void updateInfoLabel(String currentTurn) {
        String black = panel.getBlackPlayer();
        String white = panel.getWhitePlayer();
        String active = currentTurn.equals("C") ? black : white;
        String color = currentTurn.equals("C") ? "czarne" : "białe";
        infoLabel.setText(black + " (czarne) vs " + white + " (białe) | Tura: " + active + " (" + color + ")");
    }

    public Tile getTile(int row, int col) {
        return tiles[row][col];
    }

    public GamePanel getGamePanel() {
        return panel;
    }

    public GameLogic getGameLogic() {
        return gameLogic;
    }

    public void sendMove(int sr, int sc, int er, int ec) {
        panel.sendMove(sr + "-" + sc + "-" + er + "-" + ec);
    }
}
