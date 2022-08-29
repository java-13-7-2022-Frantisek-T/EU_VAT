public class StatEU implements Comparable <StatEU> {

    // Atributy třídy StatEU
    // (jsou deklarovány jako soukromé, tudíž se obsluhují výhradně přes metody třídy StatyEU, do které patří)
    private String      zkratka;            // zkratka státu
    private String      nazevStatu;         // název státu
    private Double      plnaSazba;          // plná sazba daně z přidané hodnoty v procentech
    private Double      snizenaSazba;       // snížená sazba daně z přidané hodnoty v procentech
    private Boolean     specialniSazba;     // země používá speciální sazbu DPH pro některé produkty (true/false)


    // Konstruktor třídy StatEU
    public StatEU(String zkratka, String nazevStatu, Double plnaSazba, Double snizenaSazba, Boolean specialniSazba) {
        this.zkratka        = zkratka;
        this.nazevStatu     = nazevStatu;
        this.plnaSazba      = plnaSazba;
        this.snizenaSazba   = snizenaSazba;
        this.specialniSazba = specialniSazba;
    }

    // AUTOMATICKY VYGENEROVÁNO PŘI POUŽITÍ konstrukce "implements Comparable <StatEU>" ...
    // (o jsem přepsal na otherStatEU - pro lepší pochopení kódu)
    @Override
    public int compareTo(StatEU otherStatEU) {
        return -getPlnaSazba().compareTo(otherStatEU.getPlnaSazba());

        // Vrací: záporné hodnoty (když je aktuální StatEU "menší" než otherStatEU)
        //        nulovou hodnotu (když je aktuální StatEU "rovno" otherStatEU)
        //        kladnou hodnotu (když je aktuální StatEU "větší" než otherStatEU)

        //  Pokud třídíme pouze podle názvu jako jediného srovnávaného údaje
        //  return getNazevStatu().compareTo(otherStatEU.getNazevStatu());

        //  V případě shody názvů třídíme podle poznámky
        //  (obecne lze takto vnořovat více porovnávaných parametrů)
        //  int compareStatEU = compareTo(otherStatEU.getNazevStatu());
        //  if (compareStatEU != 0) return compareStatEU;
        //  else return getSpecialníSazba().compareTo(otherStatEU.getSpecialníSazba());

    }

    @Override
    public String toString() {
        return  getNazevStatu() + " (" + getZkratka() + "): " + getPlnaSazba() + " % " + getSpecialniSazba().toString();
                // getSnizenaSazba() + " " +
                // getSpecialniSazba().toString()
                // ;

    }

    // Sada metod - Set_ry a Get_ry
    public String getZkratka() {
        return zkratka;
    }

    public void setZkratka(String zkratka) { this.zkratka = zkratka; }

    public String getNazevStatu() {
        return nazevStatu;
    }

    public void setNazevStatu(String nazevStatu) {
        this.nazevStatu = nazevStatu;
    }

    public Double getPlnaSazba() {
        return plnaSazba;
    }

    public void setPlnaSazba(Double plnaSazba) {
        this.plnaSazba = plnaSazba;
    }

    public Double getSnizenaSazba() {
        return snizenaSazba;
    }

    public void setSnizenaSazba(Double snizenaSazba) {
        this.snizenaSazba = snizenaSazba;
    }

    public Boolean getSpecialniSazba() { return specialniSazba; }

    public void setSpecialníSazba(Boolean specialníSazba) {
        this.specialniSazba = specialníSazba;
    }
}

