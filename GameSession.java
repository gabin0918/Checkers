import java.io.*;
import java.net.Socket;

public class GameSession extends Thread {
    private Socket player1, player2;
    private BufferedReader in1, in2;
    private PrintWriter out1, out2;
    private String[][] board = new String[8][8];
    private String currentTurn = "C";
    private final String username1;
    private final String username2;

    private int continuingRow = -1;
    private int continuingCol = -1;

    public GameSession(Socket p1, Socket p2, String user1, String user2) {
        this.player1 = p1;
        this.player2 = p2;
        this.username1 = user1;
        this.username2 = user2;
    }

    @Override
    public void run() {
        try {
            in1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));
            in2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));
            out1 = new PrintWriter(player1.getOutputStream(), true);
            out2 = new PrintWriter(player2.getOutputStream(), true);

            out1.println("OPPONENT:" + username2);
            out2.println("OPPONENT:" + username1);
            out1.println("CONNECTED C");
            out2.println("CONNECTED B");

            initializeBoard();
            sendGameState();

            while (true) {
                // Reset kontynuacji bicia jeśli pionek zniknął lub zmienił się gracz
                if (continuingRow != -1 && continuingCol != -1) {
                    String piece = board[continuingRow][continuingCol];
                    if (piece == null || piece.equals(" ") || !piece.startsWith(currentTurn)) {
                        continuingRow = -1;
                        continuingCol = -1;
                        System.out.println("[SERVER] Reset continuingMove – pionek nie istnieje lub zmiana gracza");
                    }
                }

                BufferedReader currentIn = currentTurn.equals("C") ? in1 : in2;
                String move = currentIn.readLine();
                System.out.println("[SERVER] Otrzymano ruch: " + move);

                boolean validMove = applyMove(move);
                sendGameState();
            }

        } catch (IOException e) {
            System.out.println("Gracz rozłączony.");
        }
    }

    private void initializeBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (row < 3 && (row + col) % 2 != 0) {
                    board[row][col] = "C";
                } else if (row > 4 && (row + col) % 2 != 0) {
                    board[row][col] = "B";
                } else {
                    board[row][col] = " ";
                }
            }
        }
    }

    private boolean applyMove(String move) {
        try {
            String[] parts = move.split("-");
            int sr = Integer.parseInt(parts[0]);
            int sc = Integer.parseInt(parts[1]);
            int er = Integer.parseInt(parts[2]);
            int ec = Integer.parseInt(parts[3]);

            String piece = board[sr][sc];
            if (piece == null || piece.trim().isEmpty() || piece.equals(" ")) {
                sendErrorMessage("Brak pionka na wybranym polu.");
                return false;
            }
            if (!piece.startsWith(currentTurn)) {
                sendErrorMessage("To nie twój pionek.");
                return false;
            }
            if (!board[er][ec].equals(" ")) {
                sendErrorMessage("Pole docelowe nie jest puste.");
                return false;
            }

            String colorOnly = piece.startsWith("B") ? "B" : "C";
            boolean wasCapture = false;

            // Zwykły pionek
            if (!piece.endsWith("king")) {
                if (Math.abs(sr - er) != Math.abs(sc - ec)) {
                    sendErrorMessage("Ruch musi być po przekątnej.");
                    return false;
                }
                if ((colorOnly.equals("C") && er <= sr) || (colorOnly.equals("B") && er >= sr)) {
                    sendErrorMessage("Nie możesz cofać pionkiem.");
                    return false;
                }
                if (Math.abs(sr - er) == 2 && Math.abs(sc - ec) == 2) {
                    int midRow = (sr + er) / 2;
                    int midCol = (sc + ec) / 2;
                    String mid = board[midRow][midCol];
                    if (mid != null && !mid.equals(" ") && !mid.startsWith(currentTurn)) {
                        board[midRow][midCol] = " ";
                        wasCapture = true;
                    } else {
                        sendErrorMessage("Brak pionka przeciwnika do zbicia.");
                        return false;
                    }
                } else if (Math.abs(sr - er) != 1) {
                    sendErrorMessage("Pionek przesuwa się tylko o 1 lub bije o 2.");
                    return false;
                }
            }
            // Dama
            else {
                if (Math.abs(sr - er) != Math.abs(sc - ec)) {
                    sendErrorMessage("Dama może iść tylko po przekątnych.");
                    return false;
                }

                int stepR = (er - sr) / Math.abs(er - sr);
                int stepC = (ec - sc) / Math.abs(ec - sc);
                int r = sr + stepR;
                int c = sc + stepC;

                int enemyCount = 0;
                int enemyRow = -1, enemyCol = -1;

                while (r != er && c != ec) {
                    String cell = board[r][c];
                    if (!cell.equals(" ")) {
                        if (cell.startsWith(currentTurn)) {
                            sendErrorMessage("Nie możesz przeskakiwać własnych pionków.");
                            return false;
                        }
                        enemyCount++;
                        enemyRow = r;
                        enemyCol = c;
                    }
                    r += stepR;
                    c += stepC;
                }

                if (enemyCount > 1) {
                    sendErrorMessage("Dama może przeskoczyć tylko jednego przeciwnika.");
                    return false;
                }

                if (enemyCount == 1) {
                    board[enemyRow][enemyCol] = " ";
                    wasCapture = true;
                }
            }

            // Promocja
            if (colorOnly.equals("C") && er == 7) piece = "C_king";
            if (colorOnly.equals("B") && er == 0) piece = "B_king";

            board[sr][sc] = " ";
            board[er][ec] = piece;

            if (wasCapture && canContinueCapturing(er, ec)) {
                continuingRow = er;
                continuingCol = ec;
                System.out.println("[SERVER] Możliwe kolejne bicie dla " + piece + " z pozycji [" + er + "," + ec + "]");
            } else {
                continuingRow = -1;
                continuingCol = -1;
                currentTurn = currentTurn.equals("C") ? "B" : "C";
                System.out.println("[SERVER] Zmiana tury na: " + currentTurn);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorMessage("Błąd przetwarzania ruchu.");
            return false;
        }
    }

    private boolean canContinueCapturing(int row, int col) {
        String piece = board[row][col];
        if (piece == null || piece.equals(" ")) return false;

        String color = piece.startsWith("C") ? "C" : "B";
        boolean isKing = piece.endsWith("king");

        int[][] directions = {{-2, -2}, {-2, 2}, {2, -2}, {2, 2}};

        for (int[] d : directions) {
            int er = row + d[0];
            int ec = col + d[1];
            int mr = row + d[0] / 2 + row;
            int mc = col + d[1] / 2;

            if (!isInBounds(er, ec) || !isInBounds(mr, mc)) continue;

            String mid = board[mr][mc];
            String end = board[er][ec];

            if (mid != null && !mid.equals(" ") && !mid.startsWith(color) && end.equals(" ")) {
                if (isKing || (color.equals("C") && er > row) || (color.equals("B") && er < row)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isInBounds(int r, int c) {
        return r >= 0 && r < 8 && c >= 0 && c < 8;
    }

    private void sendGameState() {
        StringBuilder state = new StringBuilder("TURA:" + currentTurn);
        for (int row = 0; row < 8; row++)
            for (int col = 0; col < 8; col++)
                state.append(",").append(board[row][col]);
        out1.println(state);
        out2.println(state);
    }

    private void sendErrorMessage(String message) {
        PrintWriter currentOut = currentTurn.equals("C") ? out1 : out2;
        currentOut.println("ERROR:" + message);
        System.out.println("[SERVER] ERROR -> " + message);
    }
}
