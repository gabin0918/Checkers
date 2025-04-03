import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Klasa uruchamiająca serwer gry warcaby.
 * Odpowiada za akceptowanie połączeń od klientów i parowanie ich w sesje gry.
 */
public class GameServer {
    private static final int PORT = 5000; // Numer portu, na którym nasłuchuje serwer
    private static Queue<Socket> waitingPlayers = new LinkedList<>(); // Kolejka oczekujących graczy

    /**
     * Główna metoda uruchamiająca serwer.
     * Obsługuje nadchodzące połączenia i rozpoczyna nowe sesje gry dla sparowanych graczy.
     * @param args Argumenty wiersza poleceń (niewykorzystywane)
     */
    public static void main(String[] args) {
        System.out.println("Serwer warcabów uruchomiony...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket player = serverSocket.accept(); // Oczekiwanie na połączenie od gracza
                System.out.println("Nowy gracz: " + player);

                synchronized (waitingPlayers) {
                    waitingPlayers.add(player); // Dodanie gracza do kolejki oczekujących

                    // Jeżeli są co najmniej dwaj gracze, uruchamiana jest nowa sesja gry
                    if (waitingPlayers.size() >= 2) {
                        Socket p1 = waitingPlayers.poll();
                        Socket p2 = waitingPlayers.poll();

                        System.out.println("Gracze sparowani: " + p1 + " vs " + p2);

                        // Utworzenie i uruchomienie nowego wątku gry dla pary graczy
                        new GameSession(p1, p2).start();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Obsługa wyjątku związanego z połączeniem sieciowym
        }
    }
}
