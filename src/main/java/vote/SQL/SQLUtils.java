package vote.SQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLUtils {

    private static SQLUtils instance = null;
    private Connection connection;

    SQLUtils() {
        String url = "jdbc:mysql://webinfo.iutmontp.univ-montp2.fr/jimenezh";
        String driver = "com.mysql.cj.jdbc.Driver";
        String login = "jimenezh";
        String password = "203300612DK";
        try{
            Class.forName(driver);
            connection = DriverManager.getConnection(url,login,password);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        return this.connection;
    }


public static SQLUtils getInstance() {
        if(instance == null) {
            instance = new SQLUtils();
        }
        return instance;
    }
}

