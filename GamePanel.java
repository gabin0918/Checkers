import javax.swing.*;
import java.awt.*;

public class GamePanel extends JFrame {
    private Board board;
    private JLabel playerLabel, turnLabel;

    public GamePanel(String playerColor) {
        setTitle("Warcaby Online");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        board = new Board();
        add(board, BorderLayout.CENTER);

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(200, 600));

        playerLabel = new JLabel("Grasz jako: " + (playerColor.equals("C") ? "Czarne" : "Białe"));
        playerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        playerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        turnLabel = new JLabel("Tura: Oczekiwanie...");
        turnLabel.setFont(new Font("Arial", Font.BOLD, 16));
        turnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidePanel.add(Box.createVerticalGlue());
        sidePanel.add(playerLabel);
        sidePanel.add(turnLabel);
        sidePanel.add(Box.createVerticalGlue());

        add(sidePanel, BorderLayout.EAST);

        setVisible(true);
    }

    public void updateBoard(String boardState) {
        board.updateBoard(boardState);
    }

    public void setTurn(String text) {
        turnLabel.setText("Tura: " + text);
    }
    public String getPlayerColor(){
        return playerLabel.getText();
    }
}
