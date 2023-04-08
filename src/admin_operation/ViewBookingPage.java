package admin_operation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewBookingPage extends JFrame {
    private JPanel viewBookingPanel;
    private JLabel usernameLabel;
    private JTable flightsTable;

    DefaultTableModel flightsTableModel;

    public ViewBookingPage(Connection conn, String selectedUsername) throws SQLException {
        setContentPane(viewBookingPanel);
        setTitle("View Booking Details");
        setSize(700,600);
        setLocation(500,300);

        usernameLabel.setText(selectedUsername);
        
        showFlights(conn, selectedUsername);
    }

    private void showFlights(Connection conn, String selectedUsername) throws SQLException {
        String procedure = "{call get_all_tickets(?)}";
        CallableStatement stmt = conn.prepareCall(procedure);
        stmt.setString(1, selectedUsername);
        ResultSet procedure_res = stmt.executeQuery();
        while (procedure_res.next()) {
            flightsTableModel.addRow(new Object[]{procedure_res.getInt(1),
                    procedure_res.getString(2),
                    procedure_res.getInt(3),
                    procedure_res.getString(4),
                    procedure_res.getString(5),
                    procedure_res.getString(6),
                    procedure_res.getInt(7),
                    procedure_res.getString(8)});
        }
        stmt.close();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        flightsTableModel = new DefaultTableModel();
        flightsTable = new JTable(flightsTableModel);
        flightsTableModel.addColumn("Flight ID");
        flightsTableModel.addColumn("Departure");
        flightsTableModel.addColumn("Destination");
        flightsTableModel.addColumn("Datetime");
        flightsTableModel.addColumn("Duration");
        flightsTableModel.addColumn("Airline");
        flightsTableModel.addColumn("Amount");
        flightsTableModel.addColumn("Unit Price");
    }
}
