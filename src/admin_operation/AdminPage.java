package admin_operation;

import javax.swing.*;
import java.sql.Connection;

public class AdminPage extends JFrame {
    private JPanel adminPanel;

    public AdminPage(Connection conn, String username) {
        setContentPane(adminPanel);
        setTitle("Admin");
        setSize(600,600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
