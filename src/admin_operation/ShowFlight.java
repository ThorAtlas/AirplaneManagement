package admin_operation;

import javax.swing.*;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShowFlight extends JFrame {
    private JPanel showFlightPanel;
    private JLabel flightIdLabel;
    private JLabel scheduledByLabel;

    public ShowFlight(Connection conn, String flightId) throws SQLException {
        setContentPane(showFlightPanel);
        setTitle("Flight Details");
        setSize(700,600);
        setLocation(500,300);

        flightIdLabel.setText(flightId);
        System.out.println("flightId: "+ flightId);

        String procedure = "{call find_admin_of_flight(?)}";
        CallableStatement stmt = conn.prepareCall(procedure);
        stmt.setString(1, flightId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            scheduledByLabel.setText(rs.getString(1));
        }
        stmt.close();




    }
}
