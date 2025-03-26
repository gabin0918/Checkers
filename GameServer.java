import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class GameServer {
    private static final int PORT = 5000;
    private static Queue<Socket> waitingPlayers = new LinkedList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serwer uruchomiony. Oczekiwanie na graczy...");

            while (true) {
                // Akceptowanie połączenia gracza
                Socket playerSocket = serverSocket.accept();
                System.out.println("Gracz dołączył: " + playerSocket);

                synchronized (waitingPlayers) {
                    if (waitingPlayers.isEmpty()) {
                        // Pierwszy gracz czeka na przeciwnika
                        waitingPlayers.add(playerSocket);
                        System.out.println("Gracz czeka na przeciwnika...");
                    } else {
                        // Dopasowanie dwóch graczy
                        Socket opponentSocket = waitingPlayers.poll();
                        System.out.println("Sparowano dwóch graczy! Gra się rozpoczyna.");

                        // Tworzenie strumieni do komunikacji
                        ObjectOutputStream out1 = new ObjectOutputStream(playerSocket.getOutputStream());
                        ObjectOutputStream out2 = new ObjectOutputStream(opponentSocket.getOutputStream());

                        ObjectInputStream in1 = new ObjectInputStream(playerSocket.getInputStream());
                        ObjectInputStream in2 = new ObjectInputStream(opponentSocket.getInputStream());

                        // Wysłanie potwierdzenia dla obu graczy, że gra się rozpoczęła
                        out1.writeBoolean(true);
                        out2.writeBoolean(true);
                        out1.flush();
                        out2.flush();

                        // Uruchomienie sesji gry
                        new GameSession(playerSocket, opponentSocket, out1, out2, in1, in2).start();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}