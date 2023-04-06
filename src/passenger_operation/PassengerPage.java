package passenger_operation;

import javax.swing.*;
import java.sql.Connection;

public class PassengerPage extends JFrame{
    private JPanel passengerPanel;
    private JTextField currentUserTextField;
    private JButton signOutButton;

    public PassengerPage(Connection conn, String username) {
        setContentPane(passengerPanel);
        setTitle("PASSENGER");
        setSize(600,600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
