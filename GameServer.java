import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.net.Socket;

public class GameServer {
    private static final int PORT = 5000;
    private static Queue<Socket> waitingPlayers = new LinkedList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serwer uruchomiony. Oczekiwanie na graczy...");

            while (true) {
                Socket playerSocket = serverSocket.accept();
                System.out.println("Gracz dołączył: " + playerSocket);

                synchronized (waitingPlayers) {
                    if (waitingPlayers.isEmpty()) {
                        waitingPlayers.add(playerSocket);
                        System.out.println("Gracz czeka na przeciwnika...");
                    } else {
                        Socket opponentSocket = waitingPlayers.poll();
                        System.out.println("Sparowano dwóch graczy! Gra się rozpoczyna.");

                        ObjectOutputStream out1 = new ObjectOutputStream(playerSocket.getOutputStream());
                        ObjectOutputStream out2 = new ObjectOutputStream(opponentSocket.getOutputStream());

                        out1.writeBoolean(true);
                        out2.writeBoolean(true);
                        out1.flush();
                        out2.flush();

                        new GameSession(playerSocket, opponentSocket).start();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
