import java.nio.file.Path;
import java.sql.*;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Database {
    private static Database singleton_instance;
    protected final Connection conn;

    private Database() throws SQLException {
        Path path = Path.of("db/trace.db");
        String url = "jdbc:sqlite://" + path.toAbsolutePath();
        Connection conn = DriverManager.getConnection(url);
        Statement st = conn.createStatement();

        // Vi vil gerne styre commits manuelt da SQLite ellers automatisk laver et
        // commit efter hvert statement. Da statements kan fejle burde programmet
        // udføre statements gennem database transaktioner som kræver manuelle commits.
        conn.setAutoCommit(false);

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
                    Virksomhed INTEGER NOT NULL,
                    Modtager INTEGER NOT NULL,
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
                    Pakkenummer TEXT NOT NULL,
                    Stop INTEGER NOT NULL,
                    FOREIGN KEY (Pakkenummer) REFERENCES Pakke(Pakkenummer),
                    FOREIGN KEY (Stop) REFERENCES Stop(Id),
                    PRIMARY KEY (Pakkenummer, Stop)
                );""");

        System.out.println("Opretter `Stop` tabellen.");
        st.execute("""
                CREATE TABLE IF NOT EXISTS Stop(
                    Id INTEGER PRIMARY KEY,
                    Type TEXT,
                    Adresse TEXT,
                    Tidspunkt INTEGER
                );""");

        conn.commit();
        this.conn = conn;
    }

    /*
    Indsætter et `Stop` objekt i databasens `Stop` tabel.
    Returnerer værdien af `Id` kolonnen fra den indsatte række.
     */
    Optional<Integer> indsaetStop(Stop stop) throws SQLException {
        PreparedStatement statement = this.conn.prepareStatement("""
                INSERT INTO Stop(Type, Adresse, Tidspunkt)
                VALUES (?, ?, ?);
                """, Statement.RETURN_GENERATED_KEYS);

        statement.setString(1, stop.type().toString());
        statement.setString(2, stop.adresse());
        statement.setLong(3, stop.tidspunkt().toEpochSecond());
        statement.executeUpdate();

        ResultSet rs = statement.getGeneratedKeys();

        if (rs.next()) {
            return Optional.of(rs.getInt(1));
        }

        return Optional.empty();
    }

    /*
    Indsætter et `Rute` objekt i databasens `Rute` tabel.
    Returnerer om database operationen var succesfuld.
     */
    void indsaetRute(String pakkenummer, List<Integer> stopIdListe) throws SQLException {
        PreparedStatement statement = this.conn.prepareStatement("""
                INSERT INTO Rute(Pakkenummer, Stop)
                VALUES (?, ?);
                """, Statement.RETURN_GENERATED_KEYS);

        statement.setString(1, pakkenummer);

        for (Integer stopId : stopIdListe) {
            statement.setInt(2, stopId);

            try {
                statement.executeUpdate();
            } catch (SQLException e) {
                this.conn.rollback();
                throw e;
            }
        }
    }

    /*
    Indsætter et `Modtager` objekt i databasens `Modtager` tabel.
    Returnerer værdien af `Id` kolonnen fra den indsatte række.
     */
    Optional<Integer> indsaetModtager(Modtager modtager) throws SQLException {
        PreparedStatement statement = this.conn.prepareStatement("""
                INSERT INTO Modtager(Navn, Adresse, Mobilnummer)
                VALUES (?, ?, ?);
                """, Statement.RETURN_GENERATED_KEYS);

        statement.setString(1, modtager.navn());
        statement.setString(2, modtager.adresse());
        statement.setString(3, modtager.mobilnummer());

        try {
            statement.executeUpdate();
        } catch (SQLException e) {
            // En `SQLException` kan opstå hvis modtageren allerede findes i tabellen.
            PreparedStatement stmnt = this.conn.prepareStatement("""
                    SELECT Id FROM Modtager
                    WHERE Navn = (?)
                        AND Adresse = (?)
                        AND Mobilnummer = (?)
                    """);

            stmnt.setString(1, modtager.navn());
            stmnt.setString(2, modtager.adresse());
            stmnt.setString(3, modtager.mobilnummer());
            ResultSet rs = stmnt.executeQuery();

            if (rs.next()) {
                return Optional.of(rs.getInt("Id"));
            }

            // Kunne ikke indsætte eller finde en modtager i tabellen, så
            // returner ingenting.
            return Optional.empty();
        }

        ResultSet rs = statement.getGeneratedKeys();

        if (rs.next()) {
            return Optional.of(rs.getInt(1));
        }

        return Optional.empty();
    }

    /*
    Indsætter et `Virksomhed` objekt i databasens `Virksomhed` tabel.
    Returnerer værdien af `Id` kolonnen fra den indsatte række.
     */
    Optional<Integer> indsaetVirksomhed(Virksomhed virksomhed) throws SQLException {
        PreparedStatement statement = this.conn.prepareStatement("""
                INSERT INTO Virksomhed(Navn, Adresse)
                VALUES (?, ?);
                """, Statement.RETURN_GENERATED_KEYS);

        statement.setString(1, virksomhed.navn());
        statement.setString(2, virksomhed.adresse());

        try {
            statement.executeUpdate();
        } catch (SQLException e) {
            // En `SQLException` kan opstå hvis virksomheden allerede findes i tabellen.
            PreparedStatement stmnt = this.conn.prepareStatement("""
                    SELECT Id FROM Virksomhed
                    WHERE Navn = (?)
                        AND Adresse = (?);
                    """);

            stmnt.setString(1, virksomhed.navn());
            stmnt.setString(2, virksomhed.adresse());
            ResultSet rs = stmnt.executeQuery();

            if (rs.next()) {
                return Optional.of(rs.getInt("Id"));
            }

            // Kunne ikke indsætte eller finde en modtager i tabellen, så
            // returner ingenting.
            return Optional.empty();
        }

        ResultSet rs = statement.getGeneratedKeys();

        if (rs.next()) {
            return Optional.of(rs.getInt(1));
        }

        return Optional.empty();
    }

    /*
    Indsætter et `Pakke` objekt i databasens `Pakke` tabel.
     */
    void indsaetPakke(Pakke pakke, int modtagerId, int virksomhedId) throws SQLException {
        PreparedStatement statement = this.conn.prepareStatement("""
                INSERT INTO Pakke(Pakkenummer, Modtager, Virksomhed)
                VALUES (?, ?, ?);
                """, Statement.RETURN_GENERATED_KEYS);

        statement.setString(1, pakke.pakkenummer());
        statement.setInt(2, modtagerId);
        statement.setInt(3, virksomhedId);
        statement.executeUpdate();
    }

    /*
    Indsætter et `TransportFirma` objekt i databasens `TransportFirma` tabel.
     */
    void indsaetTransportFirma(String transportFirmaNavn, String pakkenummer) throws SQLException {
        PreparedStatement statement = this.conn.prepareStatement("""
                INSERT INTO TransportFirma(Navn, Pakkenummer)
                VALUES (?, ?);
                """, Statement.RETURN_GENERATED_KEYS);

        statement.setString(1, transportFirmaNavn);
        statement.setString(2, pakkenummer);
        statement.executeUpdate();
    }

    /*
    Læser ruten der er associeret med en pakke med det givne pakkenummer.
     */
    Rute laesRute(String pakkenummer) throws SQLException {
        PreparedStatement stmnt = this.conn.prepareStatement("""
                SELECT Stop.* FROM Rute
                INNER JOIN Stop
                ON Rute.Stop = Stop.Id
                WHERE Rute.Pakkenummer = (?)
                ORDER BY Rute.rowid;
                """);

        stmnt.setString(1, pakkenummer);

        ResultSet rs = stmnt.executeQuery();
        ArrayList<Stop> stopListe = new ArrayList<>();

        while (rs.next()) {
            String type = rs.getString("Type");
            String adresse = rs.getString("Adresse");
            long tidspunkt = rs.getLong("Tidspunkt");

            Instant instant = Instant.ofEpochSecond(tidspunkt);
            stopListe.add(new Stop(
                    StopType.valueOf(type),
                    adresse,
                    OffsetDateTime.ofInstant(instant, ZoneOffset.UTC)
            ));
        }

        return new Rute(stopListe);
    }

    /*
    Læser alle pakke objekter fra databasen.
     */
    Optional<Pakke> laesPakke(String pakkenummer) throws SQLException {
        PreparedStatement stmnt = this.conn.prepareStatement("""
                SELECT
                    Modtager.Navn,
                    Modtager.Mobilnummer,
                    Modtager.Adresse,
                    Virksomhed.Navn,
                    Virksomhed.Adresse,
                    TransportFirma.Navn
                FROM Pakke
                INNER JOIN Modtager ON Modtager.Id = Pakke.Modtager
                INNER JOIN Virksomhed ON Virksomhed.Id = Pakke.Virksomhed
                INNER JOIN TransportFirma ON TransportFirma.Pakkenummer = Pakke.Pakkenummer
                WHERE Pakke.Pakkenummer = (?);
                """);

        stmnt.setString(1, pakkenummer);
        ResultSet rs = stmnt.executeQuery();

        if (rs.next()) {
            String mNavn = rs.getString(1);
            String mAdresse = rs.getString(2);
            String mMobilnummer = rs.getString(3);
            Modtager modtager = new Modtager(mNavn, mAdresse, mMobilnummer);

            String vNavn = rs.getString(4);
            String vAdresse = rs.getString(5);
            Virksomhed virksomhed = new Virksomhed(vNavn, vAdresse);

            String transportFirmaNavn = rs.getString(6);
            Rute rute = laesRute(pakkenummer);
            return Optional.of(new Pakke(pakkenummer, transportFirmaNavn, rute, virksomhed, modtager));
        }

        return Optional.empty();
    }

    /*
    Læser en liste af alle pakkenumre i databasen.
     */
    List<String> laesPakkenumre() throws SQLException {
        PreparedStatement stmnt = this.conn.prepareStatement("""
                SELECT Pakkenummer FROM Pakke;
                """);

        ResultSet rs = stmnt.executeQuery();
        ArrayList<String> pakkenummerListe = new ArrayList<>();

        while (rs.next()) {
            pakkenummerListe.add(rs.getString("Pakkenummer"));
        }

        return pakkenummerListe;
    }

    /*
    Opretter forbindelse til databasen.
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
