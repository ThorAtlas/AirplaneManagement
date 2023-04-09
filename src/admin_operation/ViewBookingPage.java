package admin_operation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class ViewBookingPage extends JFrame {
    private JPanel viewBookingPanel;
    private JLabel usernameLabel;
    private JTable flightsTable;
    private JButton changeBookingButton;
    private JButton cancelBookingButton;
    private JTextField chosenFlightIdTextField;
    private JTextField amountTextField;
    private JTextArea detailsTextArea;

    DefaultTableModel flightsTableModel;

    public ViewBookingPage(Connection conn, String selectedUsername) throws SQLException {
        setContentPane(viewBookingPanel);
        setTitle("View Booking Details");
        setSize(700,600);
        setLocation(500,300);

        usernameLabel.setText(selectedUsername);
        
        showFlights(conn, selectedUsername);
        flightsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                chosenFlightIdTextField.setText(String.valueOf(flightsTable.getValueAt(flightsTable.getSelectedRow(), 0)) );
                amountTextField.setText(String.valueOf(flightsTable.getValueAt(flightsTable.getSelectedRow(), 6)));
                //call get_ticket_details("user", 123);
                String procedure = "{ call get_ticket_details(?,?)}";
                CallableStatement stmt = null;
                try {
                    stmt = conn.prepareCall(procedure);
                    stmt.setString(1, selectedUsername);
                    stmt.setString(2, chosenFlightIdTextField.getText());
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        detailsTextArea.setText(rs.getString(1));
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
        changeBookingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //call update_ticket("user", 123, 1000,"");
                if(chosenFlightIdTextField.getText().isEmpty()){
                    JOptionPane.showMessageDialog(new JFrame(), "Please choose a Flight", "ERROR", JOptionPane.ERROR_MESSAGE);
                } else {
                    String procedure = "{ call update_ticket(?, ?, ?,?)}";
                    CallableStatement stmt = null;
                    try {
                        stmt = conn.prepareCall(procedure);
                        stmt.setString(1, selectedUsername);
                        stmt.setString(2, chosenFlightIdTextField.getText());
                        stmt.setString(3, amountTextField.getText());
                        stmt.setString(4, detailsTextArea.getText());
                        stmt.executeUpdate();
                        flightsTableModel.setRowCount(0);
                        showFlights(conn, selectedUsername);
                        JOptionPane.showMessageDialog(new JFrame(),
                                "Update Successful");
                        stmt.close();
                    } catch (SQLException exception){
                        JOptionPane.showMessageDialog(new JFrame(), exception.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                }

            }
        });

        cancelBookingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //call delete_ticket(123, "user");
                if(chosenFlightIdTextField.getText().isEmpty()){
                    JOptionPane.showMessageDialog(new JFrame(), "Please choose a Flight", "ERROR", JOptionPane.ERROR_MESSAGE);
                } else {
                    String procedure = "{ call delete_ticket(?,?) }";
                    CallableStatement stmt = null;
                    try {
                        stmt = conn.prepareCall(procedure);
                        stmt.setString(1, chosenFlightIdTextField.getText());
                        stmt.setString(2,selectedUsername);
                        stmt.executeUpdate();
                        flightsTableModel.setRowCount(0);
                        showFlights(conn, selectedUsername);
                        JOptionPane.showMessageDialog(new JFrame(),
                                "Cancel Successful");
                        stmt.close();
                    } catch (SQLException exception) {
                        JOptionPane.showMessageDialog(new JFrame(), exception.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                }

            }
        });
    }

    private void showFlights(Connection conn, String selectedUsername) throws SQLException {
        String procedure = "{call get_all_tickets(?)}";
        CallableStatement stmt = conn.prepareCall(procedure);
        stmt.setString(1, selectedUsername);
        ResultSet procedure_res = stmt.executeQuery();
        while (procedure_res.next()) {
            flightsTableModel.addRow(new Object[]{procedure_res.getInt(1),
                    procedure_res.getString(2),
                    procedure_res.getString(3),
                    procedure_res.getString(4),
                    procedure_res.getString(5),
                    procedure_res.getString(6),
                    procedure_res.getInt(7),
                    "$"+procedure_res.getString(8)});
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
