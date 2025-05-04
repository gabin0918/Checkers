import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class LoginPanel extends JFrame implements ActionListener {
    private JLabel userLabel, passwordLabel, message;
    private JTextField userNameText;
    private JPasswordField passwordText;
    private JButton submit;
    private JButton backButton;


    public LoginPanel() {
        setTitle("Zaloguj się");
        setSize(400, 300);
        setLayout(new GridLayout(4, 2));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
addWindowListener(new WindowAdapter() {
    public void windowClosing(WindowEvent e) {
        new LoginGUI(); // zamiast wyjścia z programu
    }
});


        userLabel = new JLabel("Login:");
        userNameText = new JTextField();

        passwordLabel = new JLabel("Hasło:");
        passwordText = new JPasswordField();

        submit = new JButton("ZALOGUJ");
        submit.addActionListener(this);

        backButton = new JButton("Powrót");
backButton.addActionListener(e -> {
    dispose();
    new LoginGUI();
});



        message = new JLabel("");

        add(userLabel);
        add(userNameText);
        add(passwordLabel);
        add(passwordText);
        add(new JLabel()); // Pusta komórka
        add(submit);
        add(message);
        add(backButton);


        setVisible(true); 
        setLocationRelativeTo(null);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String username = userNameText.getText();
        String password = String.valueOf(passwordText.getPassword());
    
        if (username.isEmpty() || password.isEmpty()) {
            message.setText("Uzupełnij login i hasło.");
        } else if (UserDatabase.loginUser(username, password)) {
            message.setText("Zalogowano pomyślnie!");
            dispose(); // zamyka LoginPanel
            new GameGUI(username); // otwiera grę
        } else {
            message.setText("Błędny login lub hasło.");
        }
    }
    

}
