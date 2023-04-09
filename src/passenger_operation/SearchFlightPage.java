package passenger_operation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;

public class SearchFlightPage extends JFrame {
    private JPanel searchFlightPanel;
    private JComboBox fromCountryComboBox;
    private JComboBox fromCityComboBox;
    private JComboBox fromAirportComboBox;
    private JComboBox toCountryComboBox;
    private JComboBox toCityComboBox;
    private JComboBox toAirportComboBox;
    private JTextField dateTextField;
    private JTextField passengerNumTextField;
    private JButton searchButton;
    private JTable searchFlightResultTable;
    private JButton bookButton;

    DefaultTableModel searchFlightResultTableModel;

    public SearchFlightPage(Connection conn, String username) {
        setContentPane(searchFlightPanel);
        setTitle("Search Flight");
        setSize(700, 600);
        setLocation(500, 300);
        
        showFlightResult(conn);
    }

    private void showFlightResult(Connection conn) {
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        searchFlightResultTableModel = new DefaultTableModel();
        searchFlightResultTable = new JTable(searchFlightResultTableModel);
        searchFlightResultTableModel.addColumn("Flight ID");
        searchFlightResultTableModel.addColumn("Departure");
        searchFlightResultTableModel.addColumn("Destination");
        searchFlightResultTableModel.addColumn("DateTime");
        searchFlightResultTableModel.addColumn("Duration");
        searchFlightResultTableModel.addColumn("Airline");
        searchFlightResultTableModel.addColumn("Available Seats");
        searchFlightResultTableModel.addColumn("Price");




    }
}
