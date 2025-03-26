import java.io.*;
import java.net.*;

public class WarcabyServer {
    private static Socket player1, player2;
    private static DataInputStream input1, input2;
    private static DataOutputStream output1, output2;
    private static boolean turnBlack = true; // Czarny zaczyna

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Serwer oczekuje na graczy...");
            
            player1 = serverSocket.accept();
            System.out.println("Gracz czarny połączony!");
            player2 = serverSocket.accept();
            System.out.println("Gracz biały połączony!");

            input1 = new DataInputStream(player1.getInputStream());
            output1 = new DataOutputStream(player1.getOutputStream());
            input2 = new DataInputStream(player2.getInputStream());
            output2 = new DataOutputStream(player2.getOutputStream());

            output1.writeUTF("START_CZARNY");
            output2.writeUTF("START_BIALY");

            new Thread(() -> handlePlayer(input1, output2, output1, true)).start();
            new Thread(() -> handlePlayer(input2, output1, output2, false)).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handlePlayer(DataInputStream input, DataOutputStream opponentOutput, 
                                     DataOutputStream playerOutput, boolean isBlack) {
        try {
            while (true) {
                String move = input.readUTF();
                System.out.println((isBlack ? "Czarny" : "Biały") + " wykonał ruch: " + move);

                opponentOutput.writeUTF(move);
                turnBlack = !turnBlack;

                opponentOutput.writeUTF("YOUR_TURN");
                playerOutput.writeUTF("WAIT");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
