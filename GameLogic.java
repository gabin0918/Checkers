
public class GameLogic {
    private Board board;
    private String currentPlayer;
    private GamePanel gamePanel;

    public GameLogic(Board board) {
        this.board = board;
        this.gamePanel = gamePanel;
        this.currentPlayer = "C";  // Czarny gracz zaczyna
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    // Funkcja wykonująca ruch
    public boolean makeMove(int startRow, int startCol, int endRow, int endCol) {
        Tile startTile = board.getTile(startRow, startCol);
        Tile endTile = board.getTile(endRow, endCol);
        Piece movingPiece = startTile.getPiece();

        // Sprawdzamy, czy pionek na początkowym kafelku należy do aktualnego gracza
        if (movingPiece == null || !movingPiece.getColor().equals(currentPlayer)) {
            return false;
        }

        // Sprawdzamy, czy ruch jest dozwolony
        if (!isValidMove(startRow, startCol, endRow, endCol, movingPiece)) {
            return false;
        }

        // Wykonanie ruchu
        endTile.setPiece(movingPiece);
        startTile.setPiece(null);

        // Zmiana pionka w damkę, jeśli dotarł na koniec planszy
        if (endRow == (currentPlayer.equals("C") ? 7 : 0)) {
            makeKing(endTile);
        }

        // Zmiana tury
        currentPlayer = currentPlayer.equals("C") ? "B" : "C";
        gamePanel.updateTurnDisplay(currentPlayer);

        return true;
    }

    private boolean isValidMove(int startRow, int startCol, int endRow, int endCol, Piece piece) {
        int rowDiff = Math.abs(endRow - startRow);
        int colDiff = Math.abs(endCol - startCol);
        Tile endTile = board.getTile(endRow, endCol);

        // Normalny pionek może poruszać się tylko o 1 pole do przodu po przekątnej
        if (piece instanceof NormalPiece) {
            return rowDiff == 1 && colDiff == 1 && endTile.getPiece() == null;
        }

        // Damka może poruszać się w każdą stronę po przekątnej
        if (piece instanceof KingPiece) {
            return rowDiff == colDiff && endTile.getPiece() == null;
        }

        return false;
    }

    private void makeKing(Tile tile) {
        Piece king = new KingPiece(currentPlayer);
        tile.setPiece(king);
    }
}
