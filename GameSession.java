import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GameSession extends Thread {
    private Socket player1, player2;
    private ObjectOutputStream out1, out2;
    private ObjectInputStream in1, in2;

    public GameSession(Socket p1, Socket p2, ObjectOutputStream out1, ObjectOutputStream out2, 
                       ObjectInputStream in1, ObjectInputStream in2) {
        this.player1 = p1;
        this.player2 = p2;
        this.out1 = out1;
        this.out2 = out2;
        this.in1 = in1;
        this.in2 = in2;
    }

    @Override
    public void run() {
        try {
            // Przypisanie kolorów graczom
            out1.writeObject("C"); // Czarny dla pierwszego gracza
            out2.writeObject("B"); // Biały dla drugiego gracza
            out1.flush();
            out2.flush();
            
            System.out.println("Gracze połączeni: Czarne (Gracz 1), Białe (Gracz 2)");

            // Stan planszy z początkowymi pionkami
            String initialBoardState = generateInitialBoardState();  // Generujemy stan planszy

            // Przesyłamy stan planszy do obu graczy
            out1.writeObject(initialBoardState);
            out2.writeObject(initialBoardState);
            out1.flush();
            out2.flush();

            // Rozpoczynamy wymianę ruchów
            String currentBoardState = initialBoardState;
            boolean player1Turn = true;

            while (true) {
                if (player1Turn) {
                    // Czekamy na ruch od Gracza 1
                    String move1 = (String) in1.readObject();
                    System.out.println("Gracz 1 wykonał ruch: " + move1);

                    // Zaktualizuj stan planszy po ruchu
                    currentBoardState = updateBoardState(currentBoardState, move1);

                    // Wyślij zaktualizowaną planszę do obu graczy
                    out1.writeObject(currentBoardState);
                    out2.writeObject(currentBoardState);
                    out1.flush();
                    out2.flush();

                    // Wyślij komunikat o turze
                    out1.writeObject("Twoja tura");
                    out2.writeObject("Czekaj na swoją turę");
                } else {
                    // Czekamy na ruch od Gracza 2
                    String move2 = (String) in2.readObject();
                    System.out.println("Gracz 2 wykonał ruch: " + move2);

                    // Zaktualizuj stan planszy po ruchu
                    currentBoardState = updateBoardState(currentBoardState, move2);

                    // Wyślij zaktualizowaną planszę do obu graczy
                    out1.writeObject(currentBoardState);
                    out2.writeObject(currentBoardState);
                    out1.flush();
                    out2.flush();

                    // Wyślij komunikat o turze
                    out2.writeObject("Twoja tura");
                    out1.writeObject("Czekaj na swoją turę");
                }

                // Przełącz turę
                player1Turn = !player1Turn;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Generowanie początkowego stanu planszy z pionkami
    private String generateInitialBoardState() {
        StringBuilder sb = new StringBuilder();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ((row < 3) && ((row + col) % 2 != 0)) {
                    sb.append("C,");  // Czarny pionek
                } else if ((row > 4) && ((row + col) % 2 != 0)) {
                    sb.append("B,");  // Biały pionek
                } else {
                    sb.append(" ,");  // Puste pole
                }
            }
        }

        return sb.toString();  // Zwracamy stan planszy
    }

    private String updateBoardState(String currentState, String move) {
      
       return currentState;  // Zwracamy zaktualizowany stan planszy

    }
}