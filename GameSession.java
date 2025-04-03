import java.io.*;
import java.net.Socket;

/**
 * Klasa reprezentująca sesję gry pomiędzy dwoma graczami.
 * Obsługuje komunikację i logikę serwera w trakcie rozgrywki.
 */
public class GameSession extends Thread {
    private Socket player1, player2;
    private BufferedReader in1, in2;
    private PrintWriter out1, out2;
    private String[][] board = new String[8][8]; // Dwuwymiarowa tablica reprezentująca planszę gry
    private String currentTurn = "C"; // Bieżąca tura: "C" – czarne, "B" – białe

    /**
     * Konstruktor klasy sesji gry.
     * @param p1 Gniazdo pierwszego gracza (czarne)
     * @param p2 Gniazdo drugiego gracza (białe)
     */
    public GameSession(Socket p1, Socket p2) {
        this.player1 = p1;
        this.player2 = p2;
    }

    /**
     * Główna metoda wątku sesji.
     * Obsługuje wymianę informacji między graczami i przetwarza ich ruchy.
     */
    @Override
    public void run() {
        try {
            in1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));
            in2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));
            out1 = new PrintWriter(player1.getOutputStream(), true);
            out2 = new PrintWriter(player2.getOutputStream(), true);

            out1.println("CONNECTED C");
            out2.println("CONNECTED B");

            initializeBoard();
            sendGameState();

            while (true) {
                BufferedReader currentIn = currentTurn.equals("C") ? in1 : in2;
                String move = currentIn.readLine();

                if (applyMove(move)) {
                    currentTurn = currentTurn.equals("C") ? "B" : "C";
                    sendGameState();
                }
            }

        } catch (IOException e) {
            System.out.println("Gracz rozłączony.");
        }
    }

    /**
     * Inicjalizuje początkowy stan planszy, umieszczając pionki obu graczy.
     */
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

    /**
     * Weryfikuje i wykonuje ruch przesłany przez klienta.
     * Obsługuje reguły ruchu, bicie, promocję i weryfikację poprawności.
     * @param move Ruch w formacie "sr-sc-er-ec"
     * @return true, jeśli ruch jest poprawny i został wykonany
     */
    private boolean applyMove(String move) {
        String[] parts = move.split("-");
        int sr = Integer.parseInt(parts[0]);
        int sc = Integer.parseInt(parts[1]);
        int er = Integer.parseInt(parts[2]);
        int ec = Integer.parseInt(parts[3]);

        String piece = board[sr][sc];
        if (piece == null || piece.trim().isEmpty()) return false;
        if (!piece.startsWith(currentTurn)) return false;

        // Zapamiętanie koloru w celu promocji
        String colorOnly = piece.startsWith("B") ? "B" : "C";

        String destination = board[er][ec];
        if (destination != null && !destination.equals(" ")) {
            System.out.println("Pole docelowe [" + er + "," + ec + "] jest już zajęte!");
            return false;
        }

        // Blokada cofania (dotyczy tylko zwykłych pionków)
        if (!piece.endsWith("king") && Math.abs(sr - er) == 1) {
            if (piece.equals("C") && er <= sr) {
                System.out.println("Czarny pionek nie może się cofać!");
                return false;
            }
            if (piece.equals("B") && er >= sr) {
                System.out.println("Biały pionek nie może się cofać!");
                return false;
            }
        }

        System.out.println("Ruch z [" + sr + "," + sc + "] na [" + er + "," + ec + "]");
        System.out.println("Pionek: " + piece);

        // Obsługa ruchów damką
        if (piece.endsWith("king")) {
            int dr = er - sr;
            int dc = ec - sc;

            if (Math.abs(dr) != Math.abs(dc)) {
                System.out.println("Dama musi poruszać się po przekątnych!");
                return false;
            }

            int stepR = dr / Math.abs(dr);
            int stepC = dc / Math.abs(dc);

            int r = sr + stepR;
            int c = sc + stepC;
            int enemies = 0;
            int enemyRow = -1, enemyCol = -1;

            while (r != er && c != ec) {
                String cell = board[r][c];
                if (!cell.equals(" ")) {
                    if (cell.startsWith(currentTurn)) {
                        System.out.println("Dama nie może przeskakiwać własnych pionków!");
                        return false;
                    } else {
                        enemies++;
                        enemyRow = r;
                        enemyCol = c;
                    }
                }
                r += stepR;
                c += stepC;
            }

            if (enemies > 1) {
                System.out.println("Dama nie może przeskoczyć więcej niż jednego przeciwnika!");
                return false;
            }

            if (enemies == 1) {
                System.out.println("Dama bije pionek na [" + enemyRow + "," + enemyCol + "]");
                board[enemyRow][enemyCol] = " ";
            }

            board[sr][sc] = " ";
            board[er][ec] = piece;
            return true;
        }

        // Obsługa bicia zwykłym pionkiem
        if (Math.abs(sr - er) == 2 && Math.abs(sc - ec) == 2) {
            int middleRow = (sr + er) / 2;
            int middleCol = (sc + ec) / 2;
            String beaten = board[middleRow][middleCol];

            if (beaten != null && !beaten.equals(" ") && !beaten.startsWith(currentTurn)) {
                System.out.println("Zbijam pionek na [" + middleRow + "," + middleCol + "]");
                board[middleRow][middleCol] = " ";
            } else {
                System.out.println("Nie znaleziono pionka przeciwnika do zbicia!");
                return false;
            }
        }

        // Przeniesienie pionka
        board[sr][sc] = " ";

        // Promocja na damkę
        if (colorOnly.equals("C") && er == 7) {
            piece = "C_king";
            System.out.println("PROMOCJA NA C_king");
        }
        if (colorOnly.equals("B") && er == 0) {
            piece = "B_king";
            System.out.println("PROMOCJA NA B_king");
        }

        board[er][ec] = piece;
        System.out.println("UMIESZCZONO NA [" + er + "," + ec + "] pionek: " + piece);

        return true;
    }

    /**
     * Wysyła aktualny stan planszy do obu graczy wraz z informacją o bieżącej turze.
     */
    private void sendGameState() {
        StringBuilder state = new StringBuilder();
        state.append("TURA:").append(currentTurn);

        int liczbaPol = 0;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                state.append(",").append(board[row][col]);
                liczbaPol++;
            }
        }

        System.out.println("Serwer wysyła pól: " + liczbaPol);

        String finalState = state.toString();
        out1.println(finalState);
        out2.println(finalState);
    }
}
