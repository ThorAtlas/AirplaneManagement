package admin_operation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class AddCrewToFlightPage extends JFrame {

  Statement statement;
  String query;
  ResultSet rs;
  private JPanel MainPanel;
  private JTable crewTable;
  private JButton deleteCrewButton;
  private JTable crewOnFlightTable;
  private JButton addCrewButton;
  private JLabel flightId;
  private JLabel departure;
  private JLabel duration;
  private JLabel destination;
  private JLabel datetime;
  private JButton doneButton;
  private JPanel TablePanel;
  private DefaultTableModel crewTableModel;
  private DefaultTableModel crewOnFlightTableModel;

  public AddCrewToFlightPage(Connection conn, String flightId) throws SQLException {
    setContentPane(MainPanel);
    System.out.println(flightId);
    setTitle("Add crew to Flight");
    setSize(950, 500);
    setLocation(200, 100);
    setResizable(false);
    getFlightInfo(conn, flightId);
    getCrewData(conn, flightId);
    getCrewForFlight(conn, flightId);

    addCrewButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int[] selection = crewTable.getSelectedRows();
        if (selection.length > 0) {
          for (int i : selection) {
            String crew_id = crewTable.getValueAt(i, 0).toString();
            if (!flightId.isEmpty()) {
              String procedure = "{ call add_crew_to_flight(?,?) }";
              CallableStatement stmt = null;
              try {
                stmt = conn.prepareCall(procedure);
                stmt.setString(1, crew_id);
                stmt.setString(2, flightId);
                stmt.executeUpdate();
                stmt.close();
              } catch (Exception exception) {
                JOptionPane.showMessageDialog(new JFrame(), exception.getMessage(), "ERROR",
                    JOptionPane.ERROR_MESSAGE);
                System.out.println(exception.getMessage());
              }
            }
          }
          try {
            crewTableModel.setRowCount(0);
            crewOnFlightTableModel.setRowCount(0);
            getCrewData(conn, flightId);
            getCrewForFlight(conn, flightId);
            JOptionPane.showMessageDialog(new JFrame(),
                "Added Crew Successfully!");
          } catch (SQLException ex) {
            throw new RuntimeException(ex);
          }
        } else  {
          JOptionPane.showMessageDialog(new JFrame(),
              "Please select rows from the LEFT table to add crew to the flight.");
        }
      }
    });

    deleteCrewButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int[] selection = crewOnFlightTable.getSelectedRows();
        if (selection.length > 0) {
          for (int i : selection) {
            String crew_id = crewOnFlightTable.getValueAt(i, 0).toString();
            if (!flightId.isEmpty()) {
              String procedure = "{ call delete_crew_from_flight(?,?) }";
              CallableStatement stmt = null;
              try {
                stmt = conn.prepareCall(procedure);
                stmt.setString(1, crew_id);
                stmt.setString(2, flightId);
                stmt.executeUpdate();
                stmt.close();
              } catch (Exception exception) {
                JOptionPane.showMessageDialog(new JFrame(), exception.getMessage(), "ERROR",
                    JOptionPane.ERROR_MESSAGE);
                System.out.println(exception.getMessage());
              }
            }
          }
          try {
            crewTableModel.setRowCount(0);
            crewOnFlightTableModel.setRowCount(0);
            getCrewData(conn, flightId);
            getCrewForFlight(conn, flightId);
            JOptionPane.showMessageDialog(new JFrame(),
                "Deleted Crew Successfully!");
          } catch (SQLException ex) {
            JOptionPane.showMessageDialog(new JFrame(), ex.getMessage());
          }
        } else {
          JOptionPane.showMessageDialog(new JFrame(),
              "Please select rows from the RIGHT table to delete crew from the flight.");
        }
      }
    });

    doneButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    });

  }

  private void getCrewData(Connection conn, String flightId) throws SQLException {
    statement = conn.createStatement();
    query = "select * from crew where crew_id not IN (select c.crew_id from crew c join crew_on_scheduled_flight csf on csf.crew_id = c.crew_id and csf.scheduled_flight_id = '"+flightId+"');";
    System.out.println(query);
    rs = statement.executeQuery(query);
    while (rs.next()) {
      System.out.println(rs.getInt("crew_id"));
      System.out.println(rs.getString("name"));
      System.out.println(rs.getString("job_role"));
      crewTableModel.addRow(new Object[]{rs.getInt("crew_id"),
             rs.getString("name"),
             rs.getString("job_role") });
    }
    statement.close();
  }

  private void getCrewForFlight(Connection conn, String flightId) throws SQLException {
    statement = conn.createStatement();
    query = "select * from crew c join crew_on_scheduled_flight csf on csf.crew_id = c.crew_id and csf.scheduled_flight_id = '"+flightId+"';";
    rs = statement.executeQuery(query);
    while (rs.next()) {
      System.out.println(rs.toString());
      crewOnFlightTableModel.addRow(new Object[]{rs.getInt("crew_id"),
          rs.getString("name"),
          rs.getString("job_role") });
    }
    statement.close();
  }

  private void getFlightInfo(Connection conn, String flight_id) throws SQLException {
    statement = conn.createStatement();
    query = "select * from scheduled_flight where flight_id = '"+flight_id+"';";
    rs = statement.executeQuery(query);
    while (rs.next()) {
      flightId.setText("Flight ID :"+rs.getString("flight_id"));
      departure.setText("Departure : "+rs.getString("departure_airport"));
      destination.setText("Destination : "+rs.getString("destination_airport"));
      datetime.setText("Datetime : "+rs.getString("departure_datetime"));
      duration.setText("Duration : "+rs.getString("duration"));
    }
    statement.close();

  }
  private void createUIComponents() throws SQLException {
    // model for the crew data table
    crewTableModel = new DefaultTableModel();
    crewTable = new JTable(crewTableModel);
    crewTableModel.addColumn("Crew ID");
    crewTableModel.addColumn("Name");
    crewTableModel.addColumn("Job Role");
    crewTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    crewTable.setRowSelectionAllowed(true);
    crewTable.setColumnSelectionAllowed(false);

    // model for the crew on the flight data table
    crewOnFlightTableModel = new DefaultTableModel();
    crewOnFlightTable = new JTable(crewOnFlightTableModel);
    crewOnFlightTableModel.addColumn("Crew ID");
    crewOnFlightTableModel.addColumn("Name");
    crewOnFlightTableModel.addColumn("Job Role");
    crewOnFlightTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    crewOnFlightTable.setRowSelectionAllowed(true);
    crewOnFlightTable.setColumnSelectionAllowed(false);




  }
}