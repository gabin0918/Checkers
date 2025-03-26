import java.net.Socket;


public class GameSession extends Thread {
    private Socket player1, player2;

    public GameSession(Socket p1, Socket p2) {
        this.player1 = p1;
        this.player2 = p2;
    }

    @Override
    public void run() {
        // Obsługa wymiany ruchów (do zrobienia)
    }
}
