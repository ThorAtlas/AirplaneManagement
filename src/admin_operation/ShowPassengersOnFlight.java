package admin_operation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShowPassengersOnFlight extends JFrame {
    private JPanel showFlightPanel;
    private JLabel flightIdLabel;
    private JLabel scheduledByLabel;
    private JTable passengersTable;
    DefaultTableModel passengersTableModel;

    public ShowPassengersOnFlight(Connection conn, String flightId) throws SQLException {
        setContentPane(showFlightPanel);
        setTitle("Flight Details");
        setSize(950, 500);
        setLocation(200, 100);
        setResizable(false);

        flightIdLabel.setText(flightId);
        System.out.println("flightId: " + flightId);

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

    private void showPassengers(Connection conn, String flightId) throws SQLException {
        String procedure = "{call get_all_passengers(?)}";
        CallableStatement stmt = conn.prepareCall(procedure);
        stmt.setString(1, flightId);
        ResultSet procedure_res = stmt.executeQuery();
        while (procedure_res.next()) {
            passengersTableModel.addRow(new Object[]{procedure_res.getString(1),
                procedure_res.getString(2),
                procedure_res.getInt(3),
                procedure_res.getString(4)});
        }
        stmt.close();
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