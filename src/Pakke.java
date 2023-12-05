public final class Pakke {
    public final String PAKKENUMMER;
    public final String TRANSPORTFIRMA;
    public final Rute RUTE;
    public final Virksomhed VIRKSOMHED;
    public final Modtager MODTAGER;

    Pakke(String pakkenummer, String transportFirma, Rute rute, Virksomhed virksomhed, Modtager modtager) {
        this.PAKKENUMMER = pakkenummer;
        this.TRANSPORTFIRMA = transportFirma;
        this.RUTE = rute;
        this.VIRKSOMHED = virksomhed;
        this.MODTAGER = modtager;
    }
}
