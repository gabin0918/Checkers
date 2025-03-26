import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GameClient {
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String playerColor;

    public String connectToServer() {
        try {
            socket = new Socket("localhost", 5000);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
    
            // Odczytaj najpierw potwierdzenie połączenia
            boolean gameStarted = input.readBoolean();
            if (!gameStarted) {
                System.out.println("Błąd: Serwer nie potwierdził startu gry.");
                return null;
            }
    
            // Teraz odczytaj kolor gracza
            playerColor = (String) input.readObject();
            System.out.println("DEBUG: Otrzymano kolor gracza = " + playerColor);
    
            return playerColor; // Zwróć kolor do GUI
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}