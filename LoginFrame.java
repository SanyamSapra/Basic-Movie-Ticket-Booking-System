import javax.swing.*;
import java.awt.event.*;
public class LoginFrame extends JFrame{
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    JLabel username, password;
    LoginFrame(){
        setTitle("Login");
        username = new JLabel("Username");
        username.setBounds(30, 30, 100, 30);
        password = new JLabel("Password");
        password.setBounds(30, 90, 100, 30);
        usernameField = new JTextField(20);
        usernameField.setBounds(160, 30, 100, 30);
        passwordField = new JPasswordField(20);
        passwordField.setBounds(160, 90, 100, 30);
        loginButton = new JButton("Login");
        loginButton.setBounds(80, 150, 100,30);
        add(username);
        add(usernameField);
        add(password);
        add(passwordField);
        add(loginButton);
        setSize(300,300);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (authenticate(username, password)) {
                    MainFrame mainFrame = new MainFrame(username);
                    mainFrame.setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
    private boolean authenticate(String username, String password) {
        // Simplified authentication for demo purposes
        return "sanyam".equals(username) && "1234".equals(password);
    }
}
