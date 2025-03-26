import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class WarcabyClient {
    private static boolean isBlack; // Czy ten klient to czarny gracz
    private static boolean myTurn = false; // Czy aktualnie moja tura
    private static DataOutputStream output;
    private static DataInputStream input;
    private static JFrame frame;
    private static JPanel boardPanel;
    private static Color[][] board = new Color[10][10]; // Plansza

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5000)) {
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            String startMessage = input.readUTF();
            isBlack = startMessage.equals("START_CZARNY");
            myTurn = isBlack; // Czarny zaczyna

            frame = new JFrame(isBlack ? "Gracz Czarny" : "Gracz Biały");
            frame.setSize(400, 400);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            boardPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    drawBoard(g);
                }
            };

            boardPanel.setPreferredSize(new Dimension(400, 400));
            boardPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (myTurn) {
                        int x = e.getX() / 40;
                        int y = e.getY() / 40;
                        if (board[x][y] == null) {
                            sendMove(x, y);
                        }
                    }
                }
            });

            frame.add(boardPanel);
            frame.setVisible(true);

            new Thread(() -> {
                try {
                    while (true) {
                        String move = input.readUTF();
                        if (move.equals("YOUR_TURN")) {
                            myTurn = true;
                        } else if (move.equals("WAIT")) {
                            myTurn = false;
                        } else {
                            String[] parts = move.split(",");
                            int x = Integer.parseInt(parts[0]);
                            int y = Integer.parseInt(parts[1]);
                            drawMove(x, y, !isBlack);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendMove(int x, int y) {
        try {
            output.writeUTF(x + "," + y);
            drawMove(x, y, isBlack);
            myTurn = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void drawMove(int x, int y, boolean isBlackMove) {
        board[x][y] = isBlackMove ? Color.BLUE : Color.RED;
        boardPanel.repaint();
    }

    private static void drawBoard(Graphics g) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                g.setColor((i + j) % 2 == 0 ? Color.LIGHT_GRAY : Color.DARK_GRAY);
                g.fillRect(i * 40, j * 40, 40, 40);
                if (board[i][j] != null) {
                    g.setColor(board[i][j]);
                    g.fillOval(i * 40 + 10, j * 40 + 10, 20, 20);
                }
            }
        }
    }
}
