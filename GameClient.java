import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class GameClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 12345;
    private static String playerSymbol;
    private static BufferedReader in;
    private static PrintWriter out;
    private static JButton[] buttons = new JButton[9];

    public static void main(String[] args) {
        JFrame frame = new JFrame("Kółko i Krzyżyk");
        frame.setSize(300, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(3, 3));

        for (int i = 0; i < 9; i++) {
            final int index = i;
            buttons[i] = new JButton(" ");
            buttons[i].setFont(new Font("Arial", Font.BOLD, 40));
            buttons[i].addActionListener(e -> sendMove(index));
            frame.add(buttons[i]);
        }

        frame.setVisible(true);

        try (Socket socket = new Socket(SERVER_ADDRESS, PORT)) {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String response = in.readLine();
            playerSymbol = response.split(" ")[1];
            System.out.println("Jesteś: " + playerSymbol);

            while (true) {
                String boardState = in.readLine();
                updateBoard(boardState);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendMove(int position) {
        out.println(position);
    }

    private static void updateBoard(String boardState) {
        String[] cells = boardState.split(",");
        for (int i = 0; i < 9; i++) {
            buttons[i].setText(cells[i]);
            buttons[i].setEnabled(cells[i].equals(" "));
        }
    }
}
