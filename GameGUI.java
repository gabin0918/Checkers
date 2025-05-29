import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class GameGUI {
    private JFrame frame;
    private JLabel statusLabel;
    private String username;

    public GameGUI(String username) {
        this.username = username;

        frame = new JFrame("Warcaby Online");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(5, 1));

        JLabel userLabel = new JLabel("Gracz: " + username + getRankingSuffix(), SwingConstants.CENTER);

        JButton playButton = new JButton("Graj");
        JButton rankingButton = new JButton("Ranking");
        JButton rulesButton = new JButton("Zasady");

        statusLabel = new JLabel("", SwingConstants.CENTER);

        playButton.addActionListener(e -> startGame());
        rankingButton.addActionListener(e -> showRanking());
        rulesButton.addActionListener(e -> showRules());

        frame.add(userLabel);
        frame.add(playButton);
        frame.add(rankingButton);
        frame.add(rulesButton);
        frame.add(statusLabel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private String getRankingSuffix() {
        int ranking = UserDatabase.getUserRanking(username);
        return (ranking >= 0) ? " (ranking: " + ranking + ")" : "";
    }

    private void startGame() {
        statusLabel.setText("Łączenie z serwerem...");

        new Thread(() -> {
            GameClient client = new GameClient(username);
            String playerColor = client.connectToServer();

            if (playerColor != null) {
                SwingUtilities.invokeLater(() -> {
                    frame.dispose();
                    GamePanel panel = new GamePanel(playerColor, client, username);
                    client.setGamePanel(panel);
                    client.startListening();
                });
            } else {
                statusLabel.setText("Błąd połączenia!");
            }
        }).start();
    }

    private void showRanking() {
        StringBuilder rankingText = new StringBuilder("Ranking graczy:\n");

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/checkersdatabase", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT username, ranking FROM users ORDER BY ranking DESC")) {

            while (rs.next()) {
                rankingText.append(rs.getString("username"))
                           .append(": ")
                           .append(rs.getInt("ranking"))
                           .append("\n");
            }

        } catch (SQLException e) {
            rankingText.append("Błąd podczas pobierania rankingu.");
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(frame, rankingText.toString());
    }

    private void showRules() {
        JOptionPane.showMessageDialog(frame, "Warcaby to gra strategiczna...\n" +
                "Gracze mają po 12 pionków na planszy 8x8, grają po ciemnych polach.\n" +
                "\n" +
                "Zasady:\n" +
                "Pionki ruszają się na ukos do przodu.\n" +
                "Po dojściu do końca planszy pionek staje się damką – może chodzić i bić w obu kierunkach.\n" +
                "Bicie jest obowiązkowe.\n" +
                "Istnieje możliwość zbicia wielu pionków na raz.\n" +
                "Cel gry: zbić wszystkie pionki przeciwnika.");
    }
}
