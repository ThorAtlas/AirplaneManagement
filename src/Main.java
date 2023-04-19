import java_connect.DBConnector;
import home.LoginPage;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {

//        DBConnectionUI dbc = new DBConnectionUI();
//        dbc.setVisible(true);
        // connect to db
        DBConnector dbConnector = new DBConnector();
        // get the connection result
        Connection conn = dbConnector.getConnection();
        // log in
        LoginPage login = new LoginPage(conn);
        login.setVisible(true);

    }
}
