import java.awt.*;
import javax.swing.*;

public class GamePanel extends JFrame {
    private Board board;
    private JLabel opponentLabel, playerLabel, turnLabel, blackCapturedLabel, whiteCapturedLabel, warningLabel;
    private int capturedBlack = 0, capturedWhite = 0;
    private String playerColor, currentTurn = "", username, opponentUsername = "???";
    private String pendingOpponentUsername = null;
    private GameClient client;

    private String blackPlayer = "???", whitePlayer = "???"; // <-- nie static!

    public GamePanel(String playerColor, GameClient client, String username) {
        this.playerColor = playerColor;
        this.client = client;
        this.username = username;
        this.client.setGamePanel(this);

        if (playerColor.equals("C")) blackPlayer = username;
        else whitePlayer = username;

        setTitle("Warcaby Online - Grasz jako " + (playerColor.equals("C") ? "Czarne" : "Białe"));
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        board = new Board(this);
        add(board, BorderLayout.CENTER);

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(200, 600));

        opponentLabel = new JLabel("Przeciwnik: " + opponentUsername);
        opponentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (pendingOpponentUsername != null) {
            setOpponentUsername(pendingOpponentUsername);
            System.out.println("[PANEL] Zastosowano tymczasowego przeciwnika: " + pendingOpponentUsername);
        }

        playerLabel = new JLabel("Gracz: " + username + " (" + (playerColor.equals("C") ? "Czarne" : "Białe") + ")");
        playerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        playerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        turnLabel = new JLabel("Tura: ");
        turnLabel.setFont(new Font("Arial", Font.BOLD, 16));
        turnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        blackCapturedLabel = new JLabel("Zbite czarne: 0");
        blackCapturedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        whiteCapturedLabel = new JLabel("Zbite białe: 0");
        whiteCapturedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        warningLabel = new JLabel("");
        warningLabel.setForeground(Color.RED);
        warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidePanel.add(opponentLabel);
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(warningLabel);
        sidePanel.add(blackCapturedLabel);
        sidePanel.add(whiteCapturedLabel);
        sidePanel.add(Box.createVerticalGlue());
        sidePanel.add(playerLabel);
        sidePanel.add(turnLabel);
        sidePanel.add(Box.createVerticalGlue());

        add(sidePanel, BorderLayout.EAST);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void setOpponentUsername(String name) {
        this.opponentUsername = name;
        if (opponentLabel != null) {
            opponentLabel.setText("Przeciwnik: " + name);
            System.out.println("[PANEL] Ustawiono przeciwnika: " + name);
        } else {
            pendingOpponentUsername = name;
            System.out.println("[PANEL] Przechowano przeciwnika tymczasowo: " + name);
        }

        // Ustawienie przeciwnika logicznie – dla Board.infoLabel
        if (playerColor.equals("C")) {
            whitePlayer = name;
        } else {
            blackPlayer = name;
        }

        if (board != null) {
            board.updateInfoLabel(currentTurn); // odśwież widok z nazwami
        }
    }

    public void setCurrentTurn(String turn) {
        this.currentTurn = turn;
        System.out.println("[PANEL] Tura ustawiona na: " + turn);
        turnLabel.setText("Tura: " + (turn.equals("C") ? "Czarne" : "Białe"));
        board.updateInfoLabel(turn);
    }

    public boolean isPlayerTurn() {
        return playerColor.equals(currentTurn);
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public void updateBoard(String boardState) {
        System.out.println("[PANEL] Aktualizacja planszy:\n" + boardState);
        board.updateBoard(boardState);
        board.getGameLogic().highlightVulnerableTiles(playerColor);
        updateCapturedCount(boardState);
    
        if (!hasAnyMoves("C")) {
            showEndGameOverlay("Wygrały białe!");
        } else if (!hasAnyMoves("B")) {
            showEndGameOverlay("Wygrały czarne!");
        }
    }

    private void updateCapturedCount(String boardState) {
        String[] pieces = boardState.split(",");
        int blackCount = 0, whiteCount = 0;

        for (String p : pieces) {
            if (p.equals("C") || p.equals("C_king")) blackCount++;
            if (p.equals("B") || p.equals("B_king")) whiteCount++;
        }

        capturedBlack = 12 - blackCount;
        capturedWhite = 12 - whiteCount;
        blackCapturedLabel.setText("Zbite czarne: " + capturedBlack);
        whiteCapturedLabel.setText("Zbite białe: " + capturedWhite);
    }

    public void showCaptureWarning() {
        warningLabel.setText("Musisz bić!");
        new Timer(2000, e -> warningLabel.setText("")).start();
    }

    public String getBlackPlayer() {
        return blackPlayer;
    }

    public String getWhitePlayer() {
        return whitePlayer;
    }

    public void sendMove(String move) {
        client.sendMove(move);
    }
    public void showErrorMessage(String msg) {
        warningLabel.setText(msg);
        new Timer(2000, e -> warningLabel.setText("")).start();
    }
    public void showEndGameOverlay(String message) {
        JPanel overlay = new JPanel();
        overlay.setLayout(new BoxLayout(overlay, BoxLayout.Y_AXIS));
        overlay.setOpaque(false);
        overlay.setBackground(new Color(0, 0, 0, 100)); // półprzezroczyste tło
    
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 28));
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        JButton menuBtn = new JButton("Wróć do menu");
        JButton replayBtn = new JButton("Zagraj ponownie");
        JButton exitBtn = new JButton("Wyjdź");
    
        menuBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        replayBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        // Dodaj działania przycisków
        menuBtn.addActionListener(e -> {
            dispose();
            new LoginGUI(); // lub GameGUI jeśli był zalogowany
        });
    
        replayBtn.addActionListener(e -> {
            dispose();
            new GameGUI(username); // zakładam że masz to pole
        });
    
        exitBtn.addActionListener(e -> System.exit(0));
    
        overlay.add(Box.createVerticalGlue());
        overlay.add(label);
        overlay.add(Box.createVerticalStrut(20));
        overlay.add(menuBtn);
        overlay.add(Box.createVerticalStrut(10));
        overlay.add(replayBtn);
        overlay.add(Box.createVerticalStrut(10));
        overlay.add(exitBtn);
        overlay.add(Box.createVerticalGlue());
    
        overlay.setBounds(0, 0, getWidth(), getHeight());
    
        JLayeredPane layeredPane = getLayeredPane();
        layeredPane.add(overlay, JLayeredPane.POPUP_LAYER);
        layeredPane.revalidate();
        layeredPane.repaint();
    }
    public boolean hasAnyMoves(String playerColor) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Tile tile = board.getTile(row, col);
                Piece piece = tile.getPiece();
                if (piece != null && piece.getColor().equals(playerColor)) {
                    // możesz tu rozszerzyć na sprawdzenie możliwego ruchu
                    return true;
                }
            }
        }
        return false;
    }
    
}
