import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class GameServer {
    private static final int PORT = 5000;

    static class PlayerInfo {
        Socket socket;
        String username;

        PlayerInfo(Socket socket, String username) {
            this.socket = socket;
            this.username = username;
        }
    }

    private static Queue<PlayerInfo> waitingPlayers = new LinkedList<>();

    public static void main(String[] args) {
        System.out.println("Serwer warcabów uruchomiony...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket playerSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
                String msg = in.readLine();

                String username = msg.startsWith("USERNAME:") ? msg.substring(9) : "???";
                PlayerInfo info = new PlayerInfo(playerSocket, username);

                synchronized (waitingPlayers) {
                    waitingPlayers.add(info);

                    if (waitingPlayers.size() >= 2) {
                        PlayerInfo p1 = waitingPlayers.poll();
                        PlayerInfo p2 = waitingPlayers.poll();

                        new GameSession(p1.socket, p2.socket, p1.username, p2.username).start();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
