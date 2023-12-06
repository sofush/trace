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
                    Navn TEXT,
                    Pakkenummer TEXT,
                    FOREIGN KEY (Pakkenummer) REFERENCES Pakke(Pakkenummer),
                    PRIMARY KEY (Navn, Pakkenummer)
                );""");

        System.out.println("Opretter `Pakke` tabellen.");
        st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Pakke(
                    Pakkenummer TEXT PRIMARY KEY,
                    Rute INTEGER NOT NULL,
                    Virksomhed INTEGER NOT NULL,
                    Modtager INTEGER NOT NULL,
                    FOREIGN KEY (Rute) REFERENCES Rute(Id),
                    FOREIGN KEY (Virksomhed) REFERENCES Virksomhed(Id),
                    FOREIGN KEY (Modtager) REFERENCES Modtager(Id)
                );""");

        System.out.println("Opretter `Virksomhed` tabellen.");
        st.execute("""
                CREATE TABLE IF NOT EXISTS Virksomhed(
                    Id INTEGER PRIMARY KEY,
                    Navn TEXT NOT NULL,
                    Adresse TEXT NOT NULL,
                    UNIQUE (Navn, Adresse)
                );""");

        System.out.println("Opretter `Modtager` tabellen.");
        st.execute("""
                CREATE TABLE IF NOT EXISTS Modtager(
                    Id INTEGER PRIMARY KEY,
                    Navn TEXT NOT NULL,
                    Adresse TEXT NOT NULL,
                    Mobilnummer TEXT NOT NULL,
                    UNIQUE (Navn, Adresse, Mobilnummer)
                );""");

        System.out.println("Opretter `Rute` tabellen.");
        st.execute("""
                CREATE TABLE IF NOT EXISTS Rute(
                    Pakkenummer TEXT PRIMARY KEY,
                    Stop INTEGER,
                    FOREIGN KEY (Pakkenummer) REFERENCES Pakke(Pakkenummer),
                    FOREIGN KEY (Stop) REFERENCES Stop(Id)
                );""");

        System.out.println("Opretter `Stop` tabellen.");
        st.execute("""
                CREATE TABLE IF NOT EXISTS Stop(
                    Id INTEGER PRIMARY KEY,
                    Type TEXT,
                    Adresse TEXT,
                    Tidspunkt INTEGER
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
