import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

public class Stop {
    public final StopType TYPE;
    public final String ADRESSE;
    public final OffsetDateTime TIDSPUNKT;

    public Stop(StopType type, String adresse, OffsetDateTime tidspunkt) {
        this.TYPE = type;
        this.ADRESSE = adresse;
        this.TIDSPUNKT = tidspunkt;
    }
}
