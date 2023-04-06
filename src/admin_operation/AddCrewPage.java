package admin_operation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AddCrewPage extends JFrame {
    private JTextField crewNameTextField;
    private JTextField startDateTextField;
    private JTextField jobRoleTextField;
    private JButton addButton;
    private JPanel addCrewPanel;
    private JTable showCrewTable;


    Statement statement;
    String query;
    ResultSet rs;
    DefaultTableModel model;

    public AddCrewPage(Connection conn) throws SQLException {
        setContentPane(addCrewPanel);
        setTitle("ADD CREW");
        setSize(700, 600);
        setLocation(500, 300);


        //model.addRow(crewList.get(0));
        showTableData(conn);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = crewNameTextField.getText();
                String startDate = startDateTextField.getText();
                String jobRole = jobRoleTextField.getText();
                System.out.println("name: " + name);
                System.out.println("startDate: " + startDate);

                // call book_has_genre（genre_p）procedure
                //System.out.println("call procedure result:");
                String procedure = "{ call add_crew(?,?,?) }";
                CallableStatement stmt = null;
                try {
                    stmt = conn.prepareCall(procedure);
                    stmt.setString(1, name);
                    stmt.setString(2, startDate);
                    stmt.setString(3, jobRole);
                    stmt.executeUpdate();
                    model.setRowCount(0);
                    showTableData(conn);
                    JOptionPane.showMessageDialog(new JFrame(),
                            "Successful");
                    stmt.close();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
    }

    private void showTableData(Connection conn) throws SQLException {
        statement = conn.createStatement();
        query = "select * from crew";
        rs = statement.executeQuery(query);
        while (rs.next()) {
            //System.out.println(rs.getInt(1));
            model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3),
                    rs.getString(4)});
        }
        statement.close();
    }

    private void createUIComponents() throws SQLException {
        // TODO: place custom component creation code here
        // show crews in table
        model = new DefaultTableModel();
        showCrewTable = new JTable(model);
        model.addColumn("id");
        model.addColumn("Name");
        model.addColumn("Start Date");
        model.addColumn("Job Role");


    }
}
