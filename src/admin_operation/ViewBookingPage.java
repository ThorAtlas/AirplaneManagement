package admin_operation;

import javax.swing.*;
import java.sql.Connection;

public class ViewBookingPage extends JFrame {
    private JPanel viewBookingPanel;

    public ViewBookingPage(Connection conn, String selectedUsername) {
        setContentPane(viewBookingPanel);
        setTitle("View Booking Details");
        setSize(700,600);
        setLocation(500,300);
    }
}
