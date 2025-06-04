import java.io.*;
import java.net.Socket;

public class GameClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String playerColor;
    private GamePanel gamePanel;
    private String username;
    private String pendingOpponent = null;

    public GameClient(String username) {
        this.username = username;
    }

    public String connectToServer() {
        try {
            socket = new Socket("localhost", 5000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("USERNAME:" + username);

            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("CONNECTED")) {
                    playerColor = line.split(" ")[1];
                    break;
                } else if (line.startsWith("OPPONENT:")) {
                    String opponent = line.substring("OPPONENT:".length());
                    System.out.println("[CLIENT] Otrzymano OPPONENT: " + opponent);
                    if (gamePanel != null) {
                        gamePanel.setOpponentUsername(opponent);
                        System.out.println("[CLIENT] Ustawiono przeciwnika w GamePanel: " + opponent);
                    } else {
                        System.out.println("[CLIENT] GamePanel = null! Przechowuję przeciwnika tymczasowo.");
                        pendingOpponent = opponent;
                    }
                }if (line.startsWith("GAME_OVER:WIN")) {
    gamePanel.showEndGameOverlay("Wygrałeś!");
} else if (line.startsWith("GAME_OVER:LOSE")) {
    gamePanel.showEndGameOverlay("Przegrałeś!");
}

            }

            return playerColor;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void startListening() {
        new Thread(() -> {
            try {
                while (true) {
                    String fullMsg = in.readLine();
                    if (fullMsg == null) continue;

                    if (fullMsg.startsWith("ERROR:")) {
                        String error = fullMsg.substring("ERROR:".length());
                        if (gamePanel != null) {
                            gamePanel.showErrorMessage(error);
                        }
                        continue;
                    }
                    
                    if (fullMsg.startsWith("OPPONENT:")) {
                        String opponent = fullMsg.substring("OPPONENT:".length());
                        System.out.println("[CLIENT] Otrzymano OPPONENT: " + opponent);
                        if (gamePanel != null) {
                            gamePanel.setOpponentUsername(opponent);
                            System.out.println("[CLIENT] Ustawiono przeciwnika w GamePanel: " + opponent);
                        } else {
                            System.out.println("[CLIENT] GamePanel = null! Przechowuję przeciwnika tymczasowo.");
                            pendingOpponent = opponent;
                        }
                        continue;
                    }

                    String[] tokens = fullMsg.split(",", -1);
                    String currentTurn = tokens[0].split(":")[1];

                    StringBuilder boardStateBuilder = new StringBuilder();
                    for (int i = 1; i < tokens.length; i++) {
                        boardStateBuilder.append(tokens[i]).append(",");
                    }

                    String boardState = boardStateBuilder.toString();

                    if (gamePanel != null) {
                        gamePanel.setCurrentTurn(currentTurn);
                        gamePanel.updateBoard(boardState);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendMove(String move) {
    System.out.println("[CLIENT] Wysyłam ruch: " + move);
    out.println(move);
    out.flush();
}


    public void setGamePanel(GamePanel panel) {
        this.gamePanel = panel;
        System.out.println("[CLIENT] GamePanel ustawiony.");
        if (pendingOpponent != null) {
            gamePanel.setOpponentUsername(pendingOpponent);
            System.out.println("[CLIENT] Przypisano wcześniej odebranego przeciwnika: " + pendingOpponent);
            pendingOpponent = null;
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
