package admin_operation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShowFlight extends JFrame {
    private JPanel showFlightPanel;
    private JLabel flightIdLabel;
    private JLabel scheduledByLabel;
    private JTable passengersTable;
    DefaultTableModel passengersTableModel;

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

        showPassengers(conn, flightId);




    }

    private void showPassengers(Connection conn, String flightId) {
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        // show passengers of specific flight
        passengersTableModel = new DefaultTableModel();
        passengersTable = new JTable(passengersTableModel);
        passengersTableModel.addColumn("Passenger username");
        passengersTableModel.addColumn("Passenger name");
        passengersTableModel.addColumn("Amount");
        passengersTableModel.addColumn("Details");
    }
}
