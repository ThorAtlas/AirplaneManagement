package home;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class SignupPage extends JFrame {
    private JPanel signupPanel;
    private JButton createNewUserButton;
    private JTextField usernameTextField;
    private JTextField passwordTextField;
    private JTextField nameTextField;
    private JTextField dateOfBirthTextField;
    private JTextField genderTextField;
    private JTextField passportTextField;
    private JTextField addressTextField;
    private JTextField medicalConditionTextField;
    private JRadioButton adminRadioButton;
    private JRadioButton regularUserRadioButton;
    private JButton backButton;

    public SignupPage(Connection conn){
        setContentPane(signupPanel);
        setTitle("Sign Up");
        setSize(500,600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // create a button group
        ButtonGroup bg = new ButtonGroup();
        bg.add(adminRadioButton);
        bg.add(regularUserRadioButton);
        // set sign up or login with regular user by default
        regularUserRadioButton.setSelected(true);

        createNewUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameTextField.getText();
                String password = passwordTextField.getText();
                String name = nameTextField.getText();
                String date_of_birth = dateOfBirthTextField.getText();
                String gender = genderTextField.getText();
                String passport = passportTextField.getText();
                String address = addressTextField.getText();
                String medical_condition = medicalConditionTextField.getText();

                // Execute SQL statements
                Statement statement = null;
                PreparedStatement pstmt = null;
                try{
                    String sql = null;
                    if(regularUserRadioButton.isSelected()){
                        sql = "INSERT INTO passenger(username, password, name, date_of_birth, gender, passport, address, medical_conditions) " +
                                "VALUES (?,?,?,?,?,?,?,?)";
                        pstmt =conn.prepareStatement(sql);
                        pstmt.clearParameters();
                        pstmt.setString(1, username);
                        pstmt.setString(2, password);
                        pstmt.setString(3, name);
                        pstmt.setString(4, date_of_birth);
                        pstmt.setString(5, gender);
                        pstmt.setString(6, password);
                        pstmt.setString(7, address);
                        pstmt.setString(8, medical_condition);
                        System.out.println("passenger of row:"+ pstmt.executeUpdate());
                    } else if(adminRadioButton.isSelected()){
                        sql = "INSERT INTO admin(username, name, password) VALUES(?,?,?)";
                        pstmt =conn.prepareStatement(sql);
                        pstmt.clearParameters();
                        pstmt.setString(1, username);
                        pstmt.setString(2, name);
                        pstmt.setString(3, password);
                        System.out.println("admin num of row:"+ pstmt.executeUpdate());
                    }
                    JOptionPane.showMessageDialog( new JFrame(),
                            "Successful!");
                    dispose();
                    new LoginPage(conn).setVisible(true);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog( new JFrame(),
                            "Invalid username", "Error",
                            JOptionPane.ERROR_MESSAGE);

                }

            }
        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginPage(conn).setVisible(true);
            }
        });
    }
}
