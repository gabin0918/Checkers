import javax.swing.*;
import java.awt.*;

public class GameBoardGUI {
    private JFrame frame;
    private JButton[][] buttons = new JButton[8][8];

    public GameBoardGUI() {
        frame = new JFrame("Plansza Warcabów");
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(8, 8));

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                buttons[row][col] = new JButton(" ");
                buttons[row][col].setFont(new Font("Arial", Font.BOLD, 24));
                buttons[row][col].setBackground((row + col) % 2 == 0 ? Color.LIGHT_GRAY : Color.DARK_GRAY);
                frame.add(buttons[row][col]);
            }
        }

        frame.setVisible(true);
    }
}
