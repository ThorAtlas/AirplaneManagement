package admin_operation;

import home.LoginPage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

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
    private JTextField dateTextField;
    private JComboBox airlineComboBox;
    private JTextField priceTextField;
    private JTextField timeTextField;
    private JTable flightTable;
    private JScrollPane tablePane;
    private JTextField textField1;
    private JButton viewBookingButton;
    private JTable passengerTable;
    private JTextField seatsTextField;
    private JTextField durationTextField;

    DefaultTableModel flightTableModel;
    DefaultTableModel passengerTableModel;
    DefaultComboBoxModel departureCityModel;
    DefaultComboBoxModel destinationModel;
    DefaultComboBoxModel airlineModel;

    String flightId;
    String destination;
    String departure;
    String date;
    double price;
    int seats;
    String duration;
    String airLine;
    String dateTime;

    public AdminPage(Connection conn, String username) throws SQLException {
        setContentPane(adminPanel);
        setTitle("Admin");
        setSize(1200,800);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        nameLabel.setText(username);

        showFlightData(conn);
        showPassengerData(conn);
        departureComboBoxSetUp(conn);
        destinationComboBoxSetUp(conn);
        airlineComboBoxSetUp(conn);


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
        signOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginPage(conn).setVisible(true);
            }
        });
        scheduleAFlightButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getInput();
                String procedure = "{ call add_new_flight(?,?,?,?,?,?,?,?) }";
                CallableStatement stmt = null;
                try {
                    stmt = conn.prepareCall(procedure);
                    stmt.setString(1, flightId);
                    stmt.setString(2, departure);
                    stmt.setString(3, destination);
                    stmt.setDouble(4, price);
                    stmt.setString(5, dateTime);
                    stmt.setString(6, duration);
                    stmt.setInt(7, seats);
                    stmt.setString(8, airLine);
                    stmt.executeUpdate();
                    flightTableModel.setRowCount(0);
                    showFlightData(conn);
                    JOptionPane.showMessageDialog( new JFrame(),
                            "Successful");
                } catch (Exception exception){
                    JOptionPane.showMessageDialog(new JFrame(), exception.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                    System.out.println(exception.getMessage());
                }

            }
        });
        flightTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                int id = (int) flightTable.getValueAt(flightTable.getSelectedRow(), 0);
                flightIdTextField.setText(String.valueOf(id));
                seatsTextField.setText((String) flightTable.getValueAt(flightTable.getSelectedRow(),7));
                durationTextField.setText((String) flightTable.getValueAt(flightTable.getSelectedRow(),4));
                String dateTime = (String) flightTable.getValueAt(flightTable.getSelectedRow(),3);
                String date[] = dateTime.split(" ");
                dateTextField.setText(date[0]);
                timeTextField.setText(date[1]);
                priceTextField.setText((String) flightTable.getValueAt(flightTable.getSelectedRow(),8));
            }
        });
        cancelFlightButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cancel_flight_id = flightIdTextField.getText();
                if(cancel_flight_id.isEmpty()){
                    JOptionPane.showMessageDialog(new JFrame(), "Flight id is empty", "ERROR", JOptionPane.ERROR_MESSAGE);
                } else {
                    String procedure = "{ call delete_flight(?) }";
                    CallableStatement stmt = null;
                    try {
                        stmt = conn.prepareCall(procedure);
                        stmt.setString(1,cancel_flight_id);
                        stmt.executeUpdate();
                        flightTableModel.setRowCount(0);
                        showFlightData(conn);
                        JOptionPane.showMessageDialog( new JFrame(),
                                "Deleted Successful");
                    } catch (SQLException exception) {
                        JOptionPane.showMessageDialog(new JFrame(), exception.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                }

            }
        });
    }

    private void airlineComboBoxSetUp(Connection conn) throws SQLException {
        Statement statement = conn.createStatement();
        String query = "select name from company";
        ResultSet rs = statement.executeQuery(query);
        while (rs.next()) {
            airlineModel.addElement(rs.getString("name"));
        }
    }

    private void destinationComboBoxSetUp(Connection conn) throws SQLException {
        Statement statement = conn.createStatement();
        String query = "select name from airport";
        ResultSet rs = statement.executeQuery(query);
        while (rs.next()) {
            destinationModel.addElement(rs.getString("name"));
        }
    }

    private void departureComboBoxSetUp(Connection conn) throws SQLException {
        Statement statement = conn.createStatement();
        String query = "select name from airport";
        ResultSet rs = statement.executeQuery(query);
        while (rs.next()) {
            departureCityModel.addElement(rs.getString("name"));
        }
    }

    private void showPassengerData(Connection conn) {
    }

    private void showFlightData(Connection conn) throws SQLException {
        String procedure = "{ call all_flights_detail() }";
        CallableStatement stmt = conn.prepareCall(procedure);
        ResultSet procedure_res = stmt.executeQuery();
        while (procedure_res.next()){
            flightTableModel.addRow(new Object[]{procedure_res.getInt("flight_id"),
                    procedure_res.getString("departure_airport"),
                    procedure_res.getString("destination_airport"),
                    procedure_res.getString("departure_datetime"),
                    procedure_res.getString("duration"),
                    procedure_res.getString("name"),
                    procedure_res.getString("sold_seats"),
                    procedure_res.getString("seats"),
                    procedure_res.getString("price")});
        }
    }

    private void getInput() {
        flightId = flightIdTextField.getText();
        departure = String.valueOf(departureComboBox.getSelectedItem());
        destination = String.valueOf(destinationComboBox.getSelectedItem());
        date = dateTextField.getText();
        duration = durationTextField.getText();
        price = Double.parseDouble(priceTextField.getText());
        airLine = String.valueOf(airlineComboBox.getSelectedItem());
        seats = Integer.parseInt(seatsTextField.getText());
        dateTime = date + " " + timeTextField.getText();


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
        flightTableModel.addColumn("Sold seats");
        flightTableModel.addColumn("total seats");

        flightTableModel.addColumn("Price");

        // show passenger booking details in table
        // add title
        passengerTableModel = new DefaultTableModel();
        passengerTable = new JTable(passengerTableModel);
//        passengerTableModel.addColumn("Ticket ID");
//        passengerTableModel.addColumn("Flight ID");
//        passengerTableModel.addColumn("Username");
//        passengerTableModel.addColumn("Name");
//        passengerTableModel.addColumn("Passport");
//        passengerTableModel.addColumn("Price");
        passengerTableModel.addColumn("Passenger username");
        passengerTableModel.addColumn("Passenger name");
        passengerTableModel.addColumn("Orders amount");

        // combo box setup
        departureCityModel = new DefaultComboBoxModel();
        destinationModel = new DefaultComboBoxModel();
        airlineModel = new DefaultComboBoxModel();
        departureComboBox = new JComboBox(departureCityModel);
        destinationComboBox = new JComboBox(destinationModel);
        airlineComboBox = new JComboBox(airlineModel);



    }
}
