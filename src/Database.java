import java.nio.file.Path;
import java.sql.*;

public class Database {
    private static Database singleton_instance;
    private final Connection db;

    private Database() throws SQLException {
        Path path = Path.of("db/trace.db");
        String url = "jdbc:sqlite://" + path.toAbsolutePath();
        Connection db = DriverManager.getConnection(url);
        Statement st = db.createStatement();

        System.out.println("Opretter `TransportFirma` tabellen.");
        st.executeUpdate("" +
                "CREATE TABLE IF NOT EXISTS TransportFirma(\n" +
                "Navn VARCHAR,\n" +
                "Pakkenummer VARCHAR,\n" +
                "PRIMARY KEY (Navn, Pakkenummer),\n" +
                ");");

        System.out.println("Opretter `Pakke` tabellen.");
        st.executeUpdate("" +
                "CREATE TABLE IF NOT EXISTS Pakke(\n" +
                "Pakkenummer VARCHAR PRIMARY KEY,\n" +
                "Rute INTEGER NOT NULL,\n" +
                "Virksomhed INTEGER NOT NULL,\n" +
                "Modtager INTEGER NOT NULL,\n" +
                "FOREIGN KEY (Rute) REFERENCES Rute(Id)\n" +
                "FOREIGN KEY (Virksomhed) REFERENCES Virksomhed(Id)\n" +
                "FOREIGN KEY (Modtager) REFERENCES Modtager(Id)\n" +
                ");");

        System.out.println("Opretter `Virksomhed` tabellen.");
        st.execute("" +
                "CREATE TABLE IF NOT EXISTS Virksomhed(\n" +
                "Id INTEGER PRIMARY KEY,\n" +
                "Navn VARCHAR NOT NULL,\n" +
                "Adresse VARCHAR NOT NULL\n" +
                ");");

        System.out.println("Opretter `Modtager` tabellen.");
        st.execute("" +
                "CREATE TABLE IF NOT EXISTS Modtager(\n" +
                "Id INTEGER PRIMARY KEY,\n" +
                "Navn VARCHAR NOT NULL,\n" +
                "Adresse VARCHAR NOT NULL,\n" +
                "Mobilnummer VARCHAR NOT NULL\n" +
                ");");

        System.out.println("Opretter `Rute` tabellen.");
        st.execute("" +
                "CREATE TABLE IF NOT EXISTS Rute(\n" +
                "Pakkenummer VARCHAR,\n" +
                "Id INTEGER,\n" +
                "PRIMARY KEY (Pakkenummer, Id)\n" +
                ");");

        this.db = db;
    }

    /*
    Sikkerhed: metoden er ikke synkroniseret og burde derfor ikke kaldes af flere
    tråde på samme tid; et race condition kan opstå.
     */
    public static Database singleton() throws SQLException {
        if (singleton_instance == null) {
            singleton_instance = new Database();
        }

        return singleton_instance;
    }
}
