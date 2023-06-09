package passenger_operation;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class PlaceOrderPage extends JFrame {
    private JPanel placeOrderPanel;
    private JTextField flightIdTextField;
    private JTextField amountTextField;
    private JTextArea detailsTextField;
    private JTextField totalPriceTextField;
    private JButton placeAnOrderButton;
    String details;

    public PlaceOrderPage(Connection conn, int flightId, int amount, double unitPrice, String username) {
        setContentPane(placeOrderPanel);
        setTitle("Place an Order");
        setSize(950, 500);
        setLocation(200, 100);
        setResizable(false);

        flightIdTextField.setText(String.valueOf(flightId));
        amountTextField.setText(String.valueOf(amount));
        totalPriceTextField.setText(String.valueOf(unitPrice * amount));


        placeAnOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                details = detailsTextField.getText();
                // call create_a_new_ticket(1, "", 123, "user")
                String procedure = "{call create_a_new_ticket(?,?,?,?)}";
                CallableStatement stmt = null;
                try {
                    stmt= conn.prepareCall(procedure);
                    stmt.setInt(1, amount);
                    stmt.setString(2, details);
                    stmt.setInt(3, flightId);
                    stmt.setString(4, username);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(new JFrame(),
                            "Successful");
                    stmt.close();
                    dispose();
                } catch (SQLException exception) {
                    if (!exception.getSQLState().equals("45000")) {
                        // Handle the error with custom error message
                        JOptionPane.showMessageDialog(new JFrame(), "Input Error", "ERROR", JOptionPane.ERROR_MESSAGE);
                    } else {
                        // Handle other errors with default error message
                        JOptionPane.showMessageDialog(new JFrame(), exception.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                    }

                }


            }
        });
    }
}
