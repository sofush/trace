import java.sql.SQLException;

public interface TransportFirma {
    Rute rute(String pakkenummer) throws SQLException;
    String navn();
}
