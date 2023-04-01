package java_connect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JavaMySql {
    private String userName;
    private String password;
    private final String serverName = "localhost";
    private final int portNumber = 3306;
    private String dbName;

    private String tableName;
    private final boolean useSSL = false;

    public JavaMySql() {
    }


    public JavaMySql(String userName, String password, String dbName, String tableName) {
        this.userName = userName;
        this.password = password;
        this.dbName = dbName;
        this.tableName = tableName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServerName() {
        return serverName;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean isUseSSL() {
        return useSSL;
    }

    public Connection getConnection() throws SQLException {
        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", this.getUserName());
        connectionProps.put("password", this.getPassword());
        String url = "jdbc:mysql://" + this.getServerName()+":" + this.getPortNumber()
                + "/" + this.getDbName() + "?characterEncoding=UTF-8&useSSL=false";
        conn = DriverManager.getConnection(url, connectionProps);
        return conn;
    }


}
