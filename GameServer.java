import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class GameServer {
    private static final int PORT = 12345;
    private static BlockingQueue<Socket> waitingPlayers = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        System.out.println("Serwer uruchomiony. Oczekiwanie na graczy...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket player1 = serverSocket.accept();
                System.out.println("Gracz dołączył. Oczekiwanie na przeciwnika...");

                waitingPlayers.add(player1);
                
                if (waitingPlayers.size() >= 2) {
                    Socket player2 = waitingPlayers.poll();
                    Socket player3 = waitingPlayers.poll();
                    if (player2 != null && player3 != null) {
                        System.out.println("Nowa gra: Gracz X vs Gracz O");
                        new Thread(new GameHandler(player2, player3)).start();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class GameHandler implements Runnable {
    private Socket playerX, playerO;
    private PrintWriter outX, outO;
    private BufferedReader inX, inO;
    private String[] board = {" ", " ", " ", " ", " ", " ", " ", " ", " "};
    private int currentPlayer = 0; // 0 - X, 1 - O

    public GameHandler(Socket playerX, Socket playerO) {
        this.playerX = playerX;
        this.playerO = playerO;
    }

    @Override
    public void run() {
        try {
            outX = new PrintWriter(playerX.getOutputStream(), true);
            outO = new PrintWriter(playerO.getOutputStream(), true);
            inX = new BufferedReader(new InputStreamReader(playerX.getInputStream()));
            inO = new BufferedReader(new InputStreamReader(playerO.getInputStream()));

            outX.println("CONNECTED X");
            outO.println("CONNECTED O");
            sendBoardState();

            while (true) {
                int move = (currentPlayer == 0) ? Integer.parseInt(inX.readLine()) : Integer.parseInt(inO.readLine());
                if (makeMove(currentPlayer, move)) {
                    sendBoardState();
                }
            }
        } catch (IOException e) {
            System.out.println("Gracz opuścił grę.");
        }
    }

    private synchronized boolean makeMove(int player, int position) {
        if (player != currentPlayer || !board[position].equals(" ")) return false;
        board[position] = (player == 0) ? "X" : "O";
        currentPlayer = 1 - currentPlayer;
        return true;
    }

    private synchronized void sendBoardState() {
        String boardState = String.join(",", board);
        outX.println(boardState);
        outO.println(boardState);
    }
}
