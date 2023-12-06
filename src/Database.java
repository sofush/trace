import java.nio.file.Path;
import java.sql.*;

public class Database {
    private static Database singleton_instance;
    private final Connection conn;

    private Database() throws SQLException {
        Path path = Path.of("db/trace.db");
        String url = "jdbc:sqlite://" + path.toAbsolutePath();
        Connection conn = DriverManager.getConnection(url);
        Statement st = conn.createStatement();

        System.out.println("Opretter `TransportFirma` tabellen.");
        st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS TransportFirma(
                Navn VARCHAR,
                Pakkenummer VARCHAR,
                PRIMARY KEY (Navn, Pakkenummer)
                );""");

        System.out.println("Opretter `Pakke` tabellen.");
        st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Pakke(
                Pakkenummer VARCHAR PRIMARY KEY,
                Rute INTEGER NOT NULL,
                Virksomhed INTEGER NOT NULL,
                Modtager INTEGER NOT NULL,
                FOREIGN KEY (Rute) REFERENCES Rute(Id)
                FOREIGN KEY (Virksomhed) REFERENCES Virksomhed(Id)
                FOREIGN KEY (Modtager) REFERENCES Modtager(Id)
                );""");

        System.out.println("Opretter `Virksomhed` tabellen.");
        st.execute("""
                CREATE TABLE IF NOT EXISTS Virksomhed(
                Id INTEGER PRIMARY KEY,
                Navn VARCHAR NOT NULL,
                Adresse VARCHAR NOT NULL
                );""");

        System.out.println("Opretter `Modtager` tabellen.");
        st.execute("""
                CREATE TABLE IF NOT EXISTS Modtager(
                Id INTEGER PRIMARY KEY,
                Navn VARCHAR NOT NULL,
                Adresse VARCHAR NOT NULL,
                Mobilnummer VARCHAR NOT NULL
                );""");

        System.out.println("Opretter `Rute` tabellen.");
        st.execute("""
                CREATE TABLE IF NOT EXISTS Rute(
                Pakkenummer VARCHAR,
                Id INTEGER,
                PRIMARY KEY (Pakkenummer, Id)
                );""");

        this.conn = conn;
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
