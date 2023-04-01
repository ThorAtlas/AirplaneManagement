package java_connect;

import java.sql.Connection;
import java.util.Scanner;

public class DBConnector {
    Connection conn = null;
    public DBConnector(){
        Scanner sc = new Scanner(System.in);

        JavaMySql javaMySql = new JavaMySql();
        // using airplane_management db
        String dbName = "airplane_management";
        javaMySql.setDbName(dbName);

        String username = "";
        String password = "";
        String confirm = "N";


        while(conn == null){
            while (confirm.equals("N")) {
                System.out.print("Pleas enter your MySQL username: ");
                username = sc.nextLine().trim();
                System.out.print("Pleas enter your MySQL password: ");
                password = sc.nextLine().trim();

                System.out.println("\nusername: " + username);
                System.out.println("password: " + password);
                System.out.print("Please confirm your username and password (Y/N): ");
                confirm = sc.nextLine().toUpperCase();
                System.out.println();
            }

            javaMySql.setUserName(username);
            javaMySql.setPassword(password);
            try {
                conn = javaMySql.getConnection();
                System.out.println("MySQL connect successful!\n");
            } catch (Exception e) {
                confirm = "N";
                System.out.println("MySQL did not connect. Please ensure your username and password"+e.getMessage());
            }
        }
    }

    public Connection getConnection(){
        return conn;
    }
}
