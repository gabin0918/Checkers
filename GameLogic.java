import java.util.ArrayList;
import java.util.List;

public class GameLogic {
    private Board board;
    private GamePanel panel;
    private List<Tile> forcedCaptureTiles = new ArrayList<>();

    public GameLogic(Board board, GamePanel panel) {
        this.board = board;
        this.panel = panel;
    }

    public boolean makeMove(int startRow, int startCol, int endRow, int endCol) {
        if (!panel.isPlayerTurn()) return false;

        Tile startTile = board.getTile(startRow, startCol);
        Piece piece = startTile.getPiece();
        if (piece == null || !piece.getColor().equals(panel.getPlayerColor())) return false;

        checkForMandatoryCaptures(panel.getPlayerColor());
        boolean isCapture = isCaptureMove(startRow, startCol, endRow, endCol);

        if (isCaptureRequired()) {
            // Ruch musi być biciem i wykonany jednym z pionków, które mogą bić
            if (!isCapture || !forcedCaptureTiles.contains(startTile)) {
                panel.showCaptureWarning();  // opcjonalnie: komunikat "Musisz bić!"
                return false;
            }
        }

        String move = startRow + "-" + startCol + "-" + endRow + "-" + endCol;
        panel.sendMove(move);
        return true;
    }

    public boolean canSelectPiece(Piece piece, Tile tile) {
        checkForMandatoryCaptures(panel.getPlayerColor());
        return piece != null &&
               piece.getColor().equals(panel.getPlayerColor()) &&
               panel.isPlayerTurn() &&
               (!isCaptureRequired() || forcedCaptureTiles.contains(tile));
    }

    public void checkForMandatoryCaptures(String playerColor) {
        forcedCaptureTiles.clear();
    
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Tile tile = board.getTile(row, col);
                Piece piece = tile.getPiece();
    
                if (piece != null && piece.getColor().equals(playerColor)) {
                    boolean isKing = piece instanceof KingPiece;
    
                    int[][] directions = {{-2, -2}, {-2, 2}, {2, -2}, {2, 2}};
                    for (int[] d : directions) {
                        int er = row + d[0];
                        int ec = col + d[1];
                        int mr = row + d[0] / 2;
                        int mc = col + d[1] / 2;
    
                        if (!isInBounds(er, ec) || !isInBounds(mr, mc)) continue;
    
                        Tile middle = board.getTile(mr, mc);
                        Tile end = board.getTile(er, ec);
    
                        // warunek: pionek przeciwnika po środku i wolne pole za nim
                        if (middle.getPiece() != null &&
                            !middle.getPiece().getColor().equals(playerColor) &&
                            end.getPiece() == null) {
    
                            // Sprawdź czy zwykły pionek porusza się w dozwolonym kierunku
                            if (!isKing) {
                                if (playerColor.equals("C") && d[0] < 0) continue; // czarny może tylko w dół
                                if (playerColor.equals("B") && d[0] > 0) continue; // biały może tylko w górę
                            }
    
                            forcedCaptureTiles.add(tile);
                            break;
                        }
                    }
                }
            }
        }
    }
    

    public boolean isCaptureRequired() {
        return !forcedCaptureTiles.isEmpty();
    }

    public boolean isCaptureMove(int startRow, int startCol, int endRow, int endCol) {
        return Math.abs(startRow - endRow) == 2 && Math.abs(startCol - endCol) == 2;
    }

    private boolean isInBounds(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    public void resetAllHighlights() {
        for (int row = 0; row < 8; row++)
            for (int col = 0; col < 8; col++)
                board.getTile(row, col).resetHighlight();
    }

    public void highlightVulnerableTiles(String playerColor) {
        resetAllHighlights();
        checkForMandatoryCaptures(playerColor);

        for (Tile t : forcedCaptureTiles) {
            t.highlightRed();
        }
    }
}
