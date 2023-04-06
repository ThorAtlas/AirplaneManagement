package home;

import admin_operation.AdminPage;
import passenger_operation.PassengerPage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginPage extends JFrame {
    private JPanel loginPanel;
    private JTextField usernameTextField;
    private JTextField passwordTextField;
    private JRadioButton adminRadioButton;
    private JRadioButton regularUserRadioButton;
    private JButton logInButton;
    private JButton signUpButton;

    public LoginPage(Connection conn) {
        setContentPane(loginPanel);
        setTitle("LOGIN");
        setSize(450,300);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // create a button group
        ButtonGroup bg = new ButtonGroup();
        bg.add(adminRadioButton);
        bg.add(regularUserRadioButton);
        // set sign up or login with regular user by default
        regularUserRadioButton.setSelected(true);

        logInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameTextField.getText();
                String password = passwordTextField.getText();

                try {
//                    statement = conn.createStatement();
                    String query = null;
                    if(regularUserRadioButton.isSelected()){
                        System.out.println("regular user");
                        query = "select * from passenger where username = ? and password = ?" ;
                    } else if(adminRadioButton.isSelected()){
                        System.out.println("admin:");
                        query = "select * from admin where username = ? and password = ?";
                    }
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.clearParameters();
                    pstmt.setString(1, username);
                    pstmt.setString(2, password);
                    ResultSet rs = pstmt.executeQuery();
                    if(rs.next() == false){
                        JOptionPane.showMessageDialog( new JFrame(),
                                "invalid user or password!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    } else{
                        do{
                            JOptionPane.showMessageDialog( new JFrame(),
                                    "Successful");
                            // if login as admin, open admin page
                            // if login as regular user, open passenger page
                            dispose();
                            if (adminRadioButton.isSelected()){
                                new AdminPage(conn, username).setVisible(true);
                            } else if (regularUserRadioButton.isSelected()){
                                new PassengerPage(conn, username).setVisible(true);
                            }


                        } while (rs.next());
                    }
                    pstmt.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog( new JFrame(),
                            ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                    //throw new RuntimeException(ex);
                }


            }
        });
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new SignupPage(conn).setVisible(true);
            }
        });
    }
}
