import java.time.ZonedDateTime;

public class Stop {
    public final StopType TYPE;
    public final String ADRESSE;
    public final ZonedDateTime TIDSPUNKT;

    public Stop(StopType type, String adresse, ZonedDateTime tidspunkt) {
        this.TYPE = type;
        this.ADRESSE = adresse;
        this.TIDSPUNKT = tidspunkt;
    }
}
