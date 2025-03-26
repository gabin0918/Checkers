import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GameClient {
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public boolean connectToServer() {
        try {
            socket = new Socket("localhost", 5000);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());

            return input.readBoolean(); // Oczekujemy na start gry od serwera
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
