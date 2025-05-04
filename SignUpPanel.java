import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SignUpPanel extends JFrame implements ActionListener {
    private JLabel userLabel, passwordLabel, confirmLabel, message;
    private JTextField userNameText;
    private JPasswordField passwordText, confirmPasswordText;
    private JButton submit, goToLoginButton;

    public SignUpPanel() {
        setTitle("Rejestracja");
        setSize(400, 300);
        setLayout(new GridLayout(6, 2));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        userLabel = new JLabel("Login:");
        userNameText = new JTextField();

        passwordLabel = new JLabel("Hasło:");
        passwordText = new JPasswordField();

        confirmLabel = new JLabel("Powtórz hasło:");
        confirmPasswordText = new JPasswordField();

        submit = new JButton("ZAREJESTRUJ");
        submit.addActionListener(this);

        message = new JLabel("");

        goToLoginButton = new JButton("Zaloguj się");
        goToLoginButton.addActionListener(e -> {
            dispose(); // zamyka okno rejestracji
            new LoginPanel(); // otwiera panel logowania
        });
        goToLoginButton.setVisible(false); // ukryty domyślnie

        add(userLabel);
        add(userNameText);
        add(passwordLabel);
        add(passwordText);
        add(confirmLabel);
        add(confirmPasswordText);
        add(new JLabel()); // puste pole dla układu
        add(submit);
        add(message);
        add(goToLoginButton);

        setLocationRelativeTo(null); // centrowanie okna
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String username = userNameText.getText();
        String password = String.valueOf(passwordText.getPassword());
        String confirm = String.valueOf(confirmPasswordText.getPassword());

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            message.setText("Uzupełnij wszystkie pola.");
        } else if (!password.equals(confirm)) {
            message.setText("Hasła się nie zgadzają.");
        } else {
            boolean success = UserDatabase.registerUser(username, password);
            if (success) {
                message.setText("Rejestracja zakończona!");
                submit.setEnabled(false); // dezaktywacja przycisku rejestracji
                goToLoginButton.setVisible(true); // pokazanie przycisku logowania
            } else {
                message.setText("Login już istnieje.");
            }
        }
    }
}
