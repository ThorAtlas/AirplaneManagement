package passenger_operation;

import admin_operation.ViewBookingPage;

import java.sql.Connection;
import java.sql.SQLException;

public class CheckMyOrdersPanel {
    public CheckMyOrdersPanel(Connection conn, String username) throws SQLException {
        new ViewBookingPage(conn, username).setVisible(true);
    }
}
