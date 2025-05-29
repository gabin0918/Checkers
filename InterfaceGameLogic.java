
public interface InterfaceGameLogic {
    boolean makeMove(int startRow, int startCol, int endRow, int endCol);

    boolean canSelectPiece(Piece piece, Tile tile);

    void checkForMandatoryCaptures(String playerColor);

    boolean isCaptureRequired();

    boolean isCaptureMove(int startRow, int startCol, int endRow, int endCol);

    void resetAllHighlights();

    void highlightVulnerableTiles(String playerColor);

}