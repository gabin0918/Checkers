import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;


public class GameLogic implements InterfaceGameLogic {
    private Board board;
    private GamePanel panel;
    private List<Tile> forcedCaptureTiles = new ArrayList<>();

    public GameLogic(Board board, GamePanel panel) {
        this.board = board;
        this.panel = panel;
    }
    @Override
    public boolean makeMove(int startRow, int startCol, int endRow, int endCol) {
        if (!panel.isPlayerTurn()) return false;

        Tile startTile = board.getTile(startRow, startCol);
        Piece piece = startTile.getPiece();
        if (piece == null || !piece.getColor().equals(panel.getPlayerColor())) return false;

        checkForMandatoryCaptures(panel.getPlayerColor());
        boolean isCapture = isCaptureMove(startRow, startCol, endRow, endCol);

        if (isCaptureRequired()) {
            if (!isCapture || !forcedCaptureTiles.contains(startTile)) {
                panel.showCaptureWarning();
                return false;
            }
        }

        String move = startRow + "-" + startCol + "-" + endRow + "-" + endCol;
        panel.sendMove(move);

       
        String player = panel.getPlayerColor();
        String action = isCapture ? "bicie" : "ruch";
        String log = "Gracz " + player + ": " + action + " z (" + startRow + "," + startCol + ") do (" + endRow + "," + endCol + ")\n";

        try (FileWriter writer = new FileWriter("raport.txt", true)) {
            writer.write(log);
        } catch (IOException e) {
            e.printStackTrace(); 
        }

        return true;
    }

    @Override
    public boolean canSelectPiece(Piece piece, Tile tile) {
        checkForMandatoryCaptures(panel.getPlayerColor());
        return piece != null &&
               piece.getColor().equals(panel.getPlayerColor()) &&
               panel.isPlayerTurn() &&
               (!isCaptureRequired() || forcedCaptureTiles.contains(tile));
    }
    @Override
    public void checkForMandatoryCaptures(String playerColor) {
        forcedCaptureTiles.clear();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Tile tile = board.getTile(row, col);
                Piece piece = tile.getPiece();

                if (piece != null && piece.getColor().equals(playerColor)) {
                    boolean isKing = piece instanceof KingPiece;

                    if (isKing) {
                        // Damka: sprawdzamy każdy kierunek
                        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
                        for (int[] d : directions) {
                            int r = row + d[0];
                            int c = col + d[1];
                            boolean foundOpponent = false;
                            while (isInBounds(r, c)) {
                                Tile current = board.getTile(r, c);
                                if (!foundOpponent) {
                                    if (current.getPiece() == null) {
                                        r += d[0];
                                        c += d[1];
                                        continue;
                                    }
                                    if (!current.getPiece().getColor().equals(playerColor)) {
                                        foundOpponent = true;
                                        r += d[0];
                                        c += d[1];
                                    } else {
                                        break;
                                    }
                                } else {
                                    if (current.getPiece() == null) {
                                        forcedCaptureTiles.add(tile);
                                        // damka może stanąć na dowolnym pustym polu za bitym pionkiem
                                        r += d[0];
                                        c += d[1];
                                    } else {
                                        break;
                                    }
                                }
                            }
                        }
                    } else {
                        // Zwykły pionek: jak dotychczas
                        int[][] directions = {{-2, -2}, {-2, 2}, {2, -2}, {2, 2}};
                        for (int[] d : directions) {
                            int er = row + d[0];
                            int ec = col + d[1];
                            int mr = row + d[0] / 2;
                            int mc = col + d[1] / 2;

                            if (!isInBounds(er, ec) || !isInBounds(mr, mc)) continue;

                            Tile middle = board.getTile(mr, mc);
                            Tile end = board.getTile(er, ec);

                            if (middle.getPiece() != null &&
                                    !middle.getPiece().getColor().equals(playerColor) &&
                                    end.getPiece() == null) {

                                if (playerColor.equals("C") && d[0] < 0) continue;
                                if (playerColor.equals("B") && d[0] > 0) continue;

                                forcedCaptureTiles.add(tile);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean isCaptureRequired() {
        return !forcedCaptureTiles.isEmpty();
    }
    @Override
    public boolean isCaptureMove(int startRow, int startCol, int endRow, int endCol) {
        Tile startTile = board.getTile(startRow, startCol);
        Piece piece = startTile.getPiece();
        if (piece == null) return false;

        boolean isKing = piece instanceof KingPiece;

        // Sprawdź dla damki
        if (isKing) {
            int rowDir = Integer.signum(endRow - startRow);
            int colDir = Integer.signum(endCol - startCol);

            int r = startRow + rowDir;
            int c = startCol + colDir;
            boolean foundOpponent = false;

            while (r != endRow && c != endCol) {
                Tile current = board.getTile(r, c);
                if (current.getPiece() != null) {
                    if (!current.getPiece().getColor().equals(piece.getColor())) {
                        if (foundOpponent) return false; // Więcej niż jeden przeciwnik
                        foundOpponent = true;
                    } else {
                        return false; // Własny pionek na trasie
                    }
                }
                r += rowDir;
                c += colDir;
            }

            return foundOpponent; // Musi być dokładnie jeden przeciwnik
        }

        // Sprawdź dla zwykłego pionka
        return Math.abs(startRow - endRow) == 2 && Math.abs(startCol - endCol) == 2 &&
                board.getTile((startRow + endRow) / 2, (startCol + endCol) / 2).getPiece() != null &&
                !board.getTile((startRow + endRow) / 2, (startCol + endCol) / 2).getPiece().getColor().equals(piece.getColor());
    }

    private boolean isInBounds(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
    @Override
    public void resetAllHighlights() {
        for (int row = 0; row < 8; row++)
            for (int col = 0; col < 8; col++)
                board.getTile(row, col).resetHighlight();
    }
    @Override
    public void highlightVulnerableTiles(String playerColor) {
        resetAllHighlights();
        checkForMandatoryCaptures(playerColor);

        for (Tile t : forcedCaptureTiles) {
            t.highlightRed();
        }
    }
}
