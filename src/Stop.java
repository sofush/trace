import java.time.OffsetDateTime;

public record Stop(StopType type, String adresse, OffsetDateTime tidspunkt) {}
