package passenger_operation;

import home.LoginPage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PassengerPage extends JFrame {
    private JPanel passengerPanel;
    private JButton signOutButton;
    private JLabel currentUserLabel;
    private JPanel buttonsPanel;
    private JRadioButton searchFlightRadioButton;
    private JButton checkMyOrdersButton;
    private JButton bookButton;
    private JTable allFlightsTable;
    private JTextField choosenFlightIdTextField;
    private JTextField amountTextField;
    private JButton refreshButton;
    DefaultTableModel allFlightsTableModel;
    int availableSeats;
    String flightUnitPrice;

    public PassengerPage(Connection conn, String username) throws SQLException {
        setContentPane(passengerPanel);
        setTitle("PASSENGER");
        setSize(1200, 800);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        currentUserLabel.setText(username);

        showAllFlights(conn);

        // create a button group
        ButtonGroup bg = new ButtonGroup();
        bg.add(searchFlightRadioButton);
        bg.add(checkMyOrdersButton);

        signOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginPage(conn).setVisible(true);
            }
        });
        checkMyOrdersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new CheckMyOrdersPanel(conn, username);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        allFlightsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                int id = (int) allFlightsTable.getValueAt(allFlightsTable.getSelectedRow(), 0);
                choosenFlightIdTextField.setText(String.valueOf(id));
                availableSeats = (int) allFlightsTable.getValueAt(allFlightsTable.getSelectedRow(), 6);
                flightUnitPrice = (String) allFlightsTable.getValueAt(allFlightsTable.getSelectedRow(), 7);

            }
        });
        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (allFlightsTable.getSelectedRow() != -1) {
                        int id = Integer.parseInt(choosenFlightIdTextField.getText());
                        if (choosenFlightIdTextField.getText().isEmpty()
                            || amountTextField.getText().isEmpty()) {
                            JOptionPane.showMessageDialog(new JFrame(),
                                "Error: enter valid flight and amount", "ERROR",
                                JOptionPane.ERROR_MESSAGE);
                        } else if (Integer.parseInt(amountTextField.getText()) > availableSeats) {
                            JOptionPane.showMessageDialog(new JFrame(),
                                "Error: You cannot purchase more than available seats on flight",
                                "ERROR", JOptionPane.ERROR_MESSAGE);
                        } else if (Integer.parseInt(amountTextField.getText()) <= 0) {
                            JOptionPane.showMessageDialog(new JFrame(),
                                "Error: You need to buy atleast 1 ticket.", "ERROR",
                                JOptionPane.ERROR_MESSAGE);

                        } else {
                            int amount = Integer.parseInt(amountTextField.getText());
                            double unitPrice = Double.parseDouble(flightUnitPrice.replace("$", ""));
                            new PlaceOrderPage(conn, id, amount, unitPrice, username).setVisible(
                                true);
                        }
                    } else {
                        JOptionPane.showMessageDialog(new JFrame(),
                            "Error: Please select a flight from table to book", "ERROR",
                            JOptionPane.ERROR_MESSAGE);

                    }
                } catch (NumberFormatException exception) {
                    JOptionPane.showMessageDialog(new JFrame(),
                        "Error: Please enter a correct amount(number)", "ERROR",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                allFlightsTableModel.setRowCount(0);
                try {
                    showAllFlights(conn);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private void showAllFlights(Connection conn) throws SQLException {
        String procedure = "{ call all_flights_detail() }";
        CallableStatement stmt = conn.prepareCall(procedure);
        ResultSet procedure_res = stmt.executeQuery();

        while (procedure_res.next()) {
            int sold_seats = Integer.parseInt(procedure_res.getString("sold_seats"));
            int total_seats = Integer.parseInt(procedure_res.getString("seats"));
            int available_seats = total_seats - sold_seats;
            allFlightsTableModel.addRow(new Object[]{procedure_res.getInt("flight_id"),
                procedure_res.getString("departure_airport"),
                procedure_res.getString("destination_airport"),
                procedure_res.getString("departure_datetime"),
                procedure_res.getString("duration"),
                procedure_res.getString("name"),
                available_seats,
                "$"+procedure_res.getString("price")});
        }
        stmt.close();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        allFlightsTableModel = new DefaultTableModel();
        allFlightsTable = new JTable(allFlightsTableModel);
        allFlightsTableModel.addColumn("Flight ID");
        allFlightsTableModel.addColumn("Departure");
        allFlightsTableModel.addColumn("Destination");
        allFlightsTableModel.addColumn("DateTime");
        allFlightsTableModel.addColumn("Duration");
        allFlightsTableModel.addColumn("Airline");
        allFlightsTableModel.addColumn("Available Seats");
        allFlightsTableModel.addColumn("Price");
    }
}