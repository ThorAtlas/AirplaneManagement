package admin_operation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;

public class AdminPage extends JFrame {
    private JPanel adminPanel;
    private JButton signOutButton;
    private JButton scheduleAFlightButton;
    private JButton showFlightsButton;
    private JButton changeFlightButton;
    private JButton cancelFlightButton;
    private JButton addEmployeesButton;
    private JLabel nameLabel;
    private JTextField flightIdTextField;
    private JComboBox departureComboBox;
    private JComboBox destinationComboBox;
    private JTextField DateTextField;
    private JComboBox comboBox3;
    private JTextField hhMmTextField;
    private JTextField timeTextField;
    private JTable flightTable;
    private JScrollPane tablePane;
    private JTextField textField1;
    private JButton viewBookingButton;
    private JTable passengerTable;

    DefaultTableModel flightTableModel;
    DefaultTableModel passengerTableModel;

    public AdminPage(Connection conn, String username) {
        setContentPane(adminPanel);
        setTitle("Admin");
        setSize(1000,700);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        nameLabel.setText(username);
        addEmployeesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new AddCrewPage(conn).setVisible(true);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        // show flights in table
        // add title
        flightTableModel = new DefaultTableModel();
        flightTable = new JTable(flightTableModel);
        flightTableModel.addColumn("Flight ID");
        flightTableModel.addColumn("Departure");
        flightTableModel.addColumn("Destination");
        flightTableModel.addColumn("Datetime");
        flightTableModel.addColumn("Duration");
        flightTableModel.addColumn("Airline");
        flightTableModel.addColumn("Available seats");
        flightTableModel.addColumn("Price");

        // show passenger booking details in table
        // add title
        passengerTableModel = new DefaultTableModel();
        passengerTable = new JTable(passengerTableModel);
        passengerTableModel.addColumn("Ticket ID");
        passengerTableModel.addColumn("Flight ID");
        passengerTableModel.addColumn("Username");
        passengerTableModel.addColumn("Name");
        passengerTableModel.addColumn("Passport");
        passengerTableModel.addColumn("Price");



    }
}
