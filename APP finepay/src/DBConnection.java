import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {




    public static Connection createConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/finepay?useSSL=false",
                    "root",
                    "123456789"
            );
        } catch (Exception e) {
            throw new RuntimeException("Erreur connexion DB: " + e.getMessage(), e);
        }

    }

}
