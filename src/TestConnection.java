import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class TestConnection {

    public static void main(String[] args){
        String url = "jdbc:sqlite:Mydatabase.db";

        try (Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement()) {

            //Tablo oluşturma
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS test (id INTEGER PRIMARY KEY, name TEXT)");
            IO.println("Tabelle erstellt oder bereits vorhanden.");
            IO.println("Verbindung erfolgreich!");
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}
