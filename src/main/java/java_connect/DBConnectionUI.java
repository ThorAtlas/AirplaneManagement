package java_connect;

import home.LoginPage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

public class DBConnectionUI extends JFrame{
    private JPanel connectionPanel;
    private JButton connectButton;
    private JTextField usernameTextField;
    private JTextField dbNameTextField;
    private JTextField passwordTextField;
    Connection conn = null;
    String username;
    String password;
    String dbName;
    JavaMySql javaMySql = new JavaMySql();
    public DBConnectionUI() {
        setContentPane(connectionPanel);
        setTitle("Connection");
        setSize(300,300);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //setVisible(true);


        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                username = usernameTextField.getText();
                password = passwordTextField.getText();
                dbName = dbNameTextField.getText();

                javaMySql.setDbName(dbName);
                javaMySql.setUserName(username);
                javaMySql.setPassword(password);

                try {
                    conn = javaMySql.getConnection();
                    LoginPage login = new LoginPage(conn);
                    login.setVisible(true);
                    JOptionPane.showMessageDialog( new JFrame(),
                            "MySQL connect successful!");
                    System.out.println("MySQL connect successful!\n");

                } catch (Exception exception) {
                    System.out.println(username);
                    usernameTextField.setText("");
                    passwordTextField.setText("");
                    dbNameTextField.setText("");
                    JOptionPane.showMessageDialog( new JFrame(),
                            "MySQL did not connect. Please ensure your username and password", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    System.out.println(exception.getMessage());
                }

            }
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here

    }
}
