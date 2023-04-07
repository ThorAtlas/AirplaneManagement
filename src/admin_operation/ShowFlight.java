package admin_operation;

import javax.swing.*;
import java.sql.Connection;

public class ShowFlight extends JFrame {
    private JPanel showFlightPanel;
    private JLabel flightIdLabel;
    private JLabel scheduledByLabel;

    public ShowFlight(Connection conn, String flightId){
        setContentPane(showFlightPanel);
        setTitle("Flight Details");
        setSize(700,600);
        setLocation(500,300);

        flightIdLabel.setText(flightId);

    }
}
