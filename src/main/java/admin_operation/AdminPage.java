package admin_operation;

import home.LoginPage;

import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import visualization.BarChartCompanyPerformance;

public class AdminPage extends JFrame {
    private JPanel adminPanel;
    private JButton signOutButton;
    private JButton scheduleAFlightButton;
    private JButton showPassengersButton;
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
    private JTextField passengerUsernameTextField;
    private JButton viewBookingButton;
    private JTable passengerTable;
    private JTextField seatsTextField;
    private JTextField durationTextField;
    private JButton refreshButton;
    private JButton addFlightCrewButton;
    private JButton visualizeButton;

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
        setSize(1200, 800);
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

                if (!flightId.isEmpty()) {
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
                        JOptionPane.showMessageDialog(new JFrame(),
                                "Successful");
                        stmt.close();

                        // after schedule flight successful, update the admin_schedule_flight table
                        String procedureAdmin_flight = "{call add_admin_scheduled_flight(?, ?)}";
                        CallableStatement cstmt = conn.prepareCall(procedureAdmin_flight);
                        cstmt.setString(1,username);
                        cstmt.setString(2, flightId);
                        cstmt.executeUpdate();
                        System.out.println("add_admin_scheduled_flight_successful");
                        cstmt.close();

                    } catch (Exception exception) {
                        JOptionPane.showMessageDialog(new JFrame(), exception.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                        System.out.println(exception.getMessage());
                    }
                }

            }
        });
        flightTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                int id = (int) flightTable.getValueAt(flightTable.getSelectedRow(), 0);
                flightIdTextField.setText(String.valueOf(id));
                seatsTextField.setText((String) flightTable.getValueAt(flightTable.getSelectedRow(), 7));
                durationTextField.setText((String) flightTable.getValueAt(flightTable.getSelectedRow(), 4));
                String dateTime = (String) flightTable.getValueAt(flightTable.getSelectedRow(), 3);
                String date[] = dateTime.split(" ");
                dateTextField.setText(date[0]);
                timeTextField.setText(date[1]);
                priceTextField.setText((String) flightTable.getValueAt(flightTable.getSelectedRow(), 8));
                departureComboBox.setSelectedItem(flightTable.getValueAt(flightTable.getSelectedRow(),1));
                destinationComboBox.setSelectedItem(flightTable.getValueAt(flightTable.getSelectedRow(),2));
            }
        });
        cancelFlightButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(flightTable.getSelectedRow() != -1) {
                    getInput();
                    String procedure = "{ call delete_flight(?) }";
                    CallableStatement stmt = null;
                    try {
                        stmt = conn.prepareCall(procedure);
                        stmt.setString(1, flightId);
                        stmt.executeUpdate();
                        flightTableModel.setRowCount(0);
                        showFlightData(conn);
                        JOptionPane.showMessageDialog(new JFrame(),
                            "Deleted Successful");
                        stmt.close();
                    } catch (SQLException exception) {
                        JOptionPane.showMessageDialog(new JFrame(), exception.getMessage(), "ERROR",
                            JOptionPane.ERROR_MESSAGE);
                    }

                } else {
                    JOptionPane.showMessageDialog(new JFrame(),
                        "Please select a flight from the table below to perform the operation.");
                }
            }
        });

        addFlightCrewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedFlight = flightIdTextField.getText();
                if (flightTable.getSelectedRow() != -1) {
                    try {
                        new AddCrewToFlightPage(conn, selectedFlight).setVisible(true);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(new JFrame(), ex.getMessage(), "ERROR",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(new JFrame(),
                        "Please select a flight from the table below to perform the operation.");
                }
            }
        });

        visualizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(() -> {

                    BarChartCompanyPerformance ex = new BarChartCompanyPerformance(conn);
                    ex.setVisible(true);
                });
            }
            });
        changeFlightButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(flightTable.getSelectedRow() != -1) {
                    getInput();
                    if (!flightId.isEmpty()) {
                        String procedure = "{ call update_flight(?,?,?,?,?,?,?,?) }";
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
                            JOptionPane.showMessageDialog(new JFrame(),
                                "Successful");
                            stmt.close();
                        } catch (Exception exception) {
                            JOptionPane.showMessageDialog(new JFrame(), exception.getMessage(),
                                "ERROR", JOptionPane.ERROR_MESSAGE);
                            System.out.println(exception.getMessage());
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(new JFrame(),
                        "Please select a flight from the table below to perform the operation.");
                }
            }
        });
        passengerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                String selected_username = (String) passengerTable.getValueAt(passengerTable.getSelectedRow(), 0);
                passengerUsernameTextField.setText(selected_username);
            }
        });
        viewBookingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected_username = passengerUsernameTextField.getText();
                if(passengerTable.getSelectedRow() == -1){
                    JOptionPane.showMessageDialog(new JFrame(),"Please choose a passenger name", "ERROR", JOptionPane.ERROR_MESSAGE);
                }else{
                    String sql = "select passenger_exists(?)";
                    PreparedStatement pstmt = null;
                    try {
                        pstmt = conn.prepareStatement(sql);
                        pstmt.clearParameters();
                        pstmt.setString(1,selected_username);
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next()) {
                            if(rs.getInt(1) == 1){
                                new ViewBookingPage(conn, selected_username).setVisible(true);
                            } else {
                                JOptionPane.showMessageDialog(new JFrame(),"username not exists", "ERROR", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                        pstmt.close();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }

                }
            }
        });

        // TODO: add a onclick listener for showing flight button, it gonna allow admin to choose a flight and check more details,
        // TODO: such as show which admin created this flight, users who book this flight.
        showPassengersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(flightTable.getSelectedRow() != -1) {
                    String selectedFlight = flightIdTextField.getText();
                    try {
                        new ShowPassengersOnFlight(conn, selectedFlight).setVisible(true);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(new JFrame(), ex.getMessage(), "ERROR",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }else {
                    JOptionPane.showMessageDialog(new JFrame(),
                        "Please select a flight from the table below to perform the operation.");
                }

            }
        });
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                flightTableModel.setRowCount(0);
                passengerTableModel.setRowCount(0);
                try {
                    showFlightData(conn);
                    showPassengerData(conn);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
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
        statement.close();
    }

    private void destinationComboBoxSetUp(Connection conn) throws SQLException {
        Statement statement = conn.createStatement();
        String query = "select name from airport";
        ResultSet rs = statement.executeQuery(query);
        while (rs.next()) {
            destinationModel.addElement(rs.getString("name"));
        }
        statement.close();
    }

    private void departureComboBoxSetUp(Connection conn) throws SQLException {
        Statement statement = conn.createStatement();
        String query = "select name from airport";
        ResultSet rs = statement.executeQuery(query);
        while (rs.next()) {
            departureCityModel.addElement(rs.getString("name"));
        }
        statement.close();
    }

    private void showPassengerData(Connection conn) throws SQLException {
        String procedure = "{ call get_ticket_total_of_passenger() }";
        CallableStatement stmt = conn.prepareCall(procedure);
        ResultSet procedure_res = stmt.executeQuery();
        while (procedure_res.next()) {
            passengerTableModel.addRow(new Object[]{procedure_res.getString(1),
                    procedure_res.getString(2),
                    procedure_res.getInt(3)});
        }
        stmt.close();
    }

    private void showFlightData(Connection conn) throws SQLException {
        String procedure = "{ call all_flights_detail() }";
        CallableStatement stmt = conn.prepareCall(procedure);
        ResultSet procedure_res = stmt.executeQuery();
        while (procedure_res.next()) {
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
        stmt.close();
    }

    private void getInput() {
        if (flightIdTextField.getText().isEmpty() || dateTextField.getText().isEmpty()
            || durationTextField.getText().isEmpty() || priceTextField.getText().isEmpty()
            || seatsTextField.getText().isEmpty() || timeTextField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(new JFrame(), "Flight details cannot be null/empty", "ERROR", JOptionPane.ERROR_MESSAGE);
        } else {
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
        passengerTableModel.addColumn("Passenger username");
        passengerTableModel.addColumn("Passenger name");
        passengerTableModel.addColumn("tickets amount");

        // combo box setup
        departureCityModel = new DefaultComboBoxModel();
        destinationModel = new DefaultComboBoxModel();
        airlineModel = new DefaultComboBoxModel();
        departureComboBox = new JComboBox(departureCityModel);
        destinationComboBox = new JComboBox(destinationModel);
        airlineComboBox = new JComboBox(airlineModel);


    }
}
