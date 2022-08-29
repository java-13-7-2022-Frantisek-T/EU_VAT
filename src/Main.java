import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.time.format.DecimalStyle;
import java.util.*;
import java.util.List;

public class Main {

    // SEKCE KONSTANT

    // Jméno souboru se vstupními daty
    public static final String FILENAME_IN = "vat-eu.csv";

    // Základní (společná) část jména výstupních textových souborů.
    // Pro jednotlivé sazby DPH se bude mezi podtržítko a tečku vkládat sazba daně
    public static final String FILENAME_OUT_BASE = "vat-over-";

    // Oddělovač jednotlivých položek na řádku ve vstupním textovém souboru
    public static final String DELIMITER = "\t";

    // Limitní sazba DPH
    public static final double LIMIT_DPH = 20.00d;

    public static void main(String[] args) {


        System.out.println("(SAMOSTATNÝ ÚKOL - LEKCE 06 - sazby DPH v zemích EU!)");

        System.out.println("************************( S T A R T )************************");
        // Načteme obsah textového souboru (včetně ošetření vzniku možných chyb
        SeznamStatuEU seznamStatuEU = null;
        try {
            seznamStatuEU = SeznamStatuEU.importFromFile(FILENAME_IN, DELIMITER);
        } catch (StatEUException e) {
            System.err.println("Soubor " + FILENAME_IN + " se nepodařiol správně načíst !\n" + e.getLocalizedMessage());
        }
        System.out.println("************************( KONEC NAČÍTÁNÍ DAT Z TEXTOVÉHO SOUBORU )************************");

        if (seznamStatuEU != null) {

            System.out.println("************************( A JEDEME DÁL )************************");

            Double limitDPH = LIMIT_DPH;

/*

            Locale[] lo = { new Locale("cs", "CZ"),
                            new Locale("en", "US") };


            Scanner sc = new Scanner(System.in).useLocale(lo[1]);
            System.out.print("Zadej realne cislo (1,234,567.89): " );
            double d = sc.nextDouble();
            System.out.println(d);

            sc = new Scanner(System.in).useLocale(lo[0]);

            System.out.print("Zadej realne cislo (1234567,89): " );
            d = sc.nextDouble();
            System.out.println(d);
 */
            System.out.println();
            System.out.print("Zadej hraniční sazbu DPH (pro sazbu 20% stiskni Enter): " );

            // Pomocná proměnná - pokud nezadám korektně hodnotu pro hraniční DPH,
            // neprovede se zápis do textového souboru ani výstup na konzoli
            Boolean isBadInput = Boolean.FALSE;

            try {
                Scanner sc = new Scanner(System.in);
                String line = sc.nextLine();
                if (line.equals("")) {
                    // Pokud stisknu Enter, je použita hodnota 20% pro hraniční hodnotu DPH
                    line="20";
                }
                else {
                    String s1 = "";
                    int pocDesTec = 0;
                    for (int i = 0; i < line.length(); i++) {
                        if ((line.charAt(i) == '.') ||
                            (line.charAt(i) == '0') ||
                            (line.charAt(i) == '1') ||
                            (line.charAt(i) == '2') ||
                            (line.charAt(i) == '3') ||
                            (line.charAt(i) == '4') ||
                            (line.charAt(i) == '5') ||
                            (line.charAt(i) == '6') ||
                            (line.charAt(i) == '7') ||
                            (line.charAt(i) == '8') ||
                            (line.charAt(i) == '9')
                            ) {
                            if (line.charAt(i) == '.') pocDesTec++;
                            s1 = s1 + line.charAt(i);
                        }
                        else {
                            if (line.charAt(i) == ',') { s1 = s1+"."; pocDesTec++; }
                            else { System.err.println("Nepovolený znak: (" + line.charAt(i) + ") bude vymazán !"); }
                        }
                    }
                    line = s1;
                    if (pocDesTec>1) {
                        isBadInput = Boolean.TRUE;
                        System.err.println("Nalezeno více desetinných teček: (" + pocDesTec + ") řetězec nelze převést na číslo !");
                    }
                    // Toto je asi již zbytečné !
                    for (int i = 0; i < line.length(); i++) {
                        if (!((line.charAt(i) == '.') ||
                              (line.charAt(i) == '0') ||
                              (line.charAt(i) == '1') ||
                              (line.charAt(i) == '2') ||
                              (line.charAt(i) == '3') ||
                              (line.charAt(i) == '4') ||
                              (line.charAt(i) == '5') ||
                              (line.charAt(i) == '6') ||
                              (line.charAt(i) == '7') ||
                              (line.charAt(i) == '8') ||
                              (line.charAt(i) == '9'))
                            ) { isBadInput = Boolean.TRUE; }
                    }
                    if (isBadInput) { System.err.println(" Vstupní řetězec (" + line + ") obsahuje nepovolené znaky !\n" + " Nelze jej převést na číslo !"); }
                    else { limitDPH = Double.parseDouble(line); }
                }
//                Vypuštěno - zbytečné info výstupy na konzoli
//                System.out.println(line);
//                System.out.println("Hraniční sazba DPH byla zadána ve výši: " + limitDPH + " % .");
            }
            catch (InputMismatchException e) {
                System.err.println("Chyba (InputMismatchException) při zadávání čísla: " + e.getLocalizedMessage() + "\n");
                isBadInput = Boolean.TRUE;
            }
            catch (Exception e) {
                System.err.println("Chyba (IOException) při zadávání čísla: " + e.getLocalizedMessage() + "\n");
                isBadInput = Boolean.TRUE;
            }

            if (isBadInput) {
                System.err.println("Chybně zadaný vstupní údaj pro hraniční hodnotu DPH !\n"+
                                    "Program neprovede část výstupů - na konzoli a do textového souboru");
            }
            else {
                System.out.println("Hraniční sazba DPH byla zadána ve výši: " + limitDPH + " % .");
                // Vypíšeme seznam všech států a u každého uvedeme základní sazbu daně z přidané hodnoty ve formátu podle vzoru:
                // Název země (zkratka): základní sazba %
                // Například:
                // Austria (AT): 20 %
                // Belgium (BE): 21 %
                System.out.println("************************( SEZNAM VŠECH STÁTŮ )************************");
                System.out.println("Název země (zkratka): základní sazba %");
                for (StatEU statEU : seznamStatuEU.getList()) {
                    System.out.println(statEU.getNazevStatu() + " (" + statEU.getZkratka() + "): " + statEU.getPlnaSazba() + " % ");
                }
                System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^( KONEC VÝPISU )");

                // Vypíšeme všechny státy Evropské unie, které mají sazbu daně z přidané hodnoty vyšší než 20 % a nepoužívají speciální sazbu DPH/VAT.
                System.out.println("************************( SEZNAM VYBRANÝCH STÁTŮ - DPH vyšší než " + limitDPH + "% a nepoužívají speciální sazbu DPH )************************");
                for (StatEU statEU : seznamStatuEU.getList()) {
                    if ((statEU.getSpecialniSazba() == Boolean.FALSE) && ((statEU.getPlnaSazba() > limitDPH) | (statEU.getSnizenaSazba() > limitDPH))) {
                        System.out.println(statEU.getNazevStatu() + " (" + statEU.getZkratka() + "): " + statEU.getPlnaSazba() + " % ");
                    }
                }
                System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^( KONEC VÝPISU )");

                // Vypíšeme všechny státy Evropské unie, které mají sazbu daně z přidané hodnoty vyšší než 20 % a nepoužívají speciální sazbu DPH/VAT.
                // seřazené podle výše základní sazby DPH/VAT sestupně (nejprve státy s nejvyšší sazbou).
                System.out.println("************************( SETŘÍDĚNÝ SEZNAM VYBRANÝCH STÁTŮ - DPH vyšší než " + limitDPH + "% a nepoužívají speciální sazbu DPH )************************");
                System.out.println("************************( pod oddělovacím řádkem z rovnítek seznam zkratek států které nejsou na seznamu vybraných států )************************");
                // Vytvořím proměnnou statyEUArray
    //            System.out.println("------------------------------( seznam států EU převedu na kolekci )------------------------------");
                List<StatEU> statyEUArray = new ArrayList<>(seznamStatuEU.getList());
                // Kolekci setřídíme podle plné sazby DPH vzestupně
    //            System.out.println("------------------------------( kolekci setřídím SESTUPNĚ podle plné sazby DPH )------------------------------");
                Collections.sort(statyEUArray);

                // Vypíšeme všechny státy Evropské unie, které mají sazbu daně z přidané hodnoty vyšší než 20 % a nepoužívají speciální sazbu DPH/VAT.
                // seřazené podle výše základní sazby DPH/VAT sestupně (nejprve státy s nejvyšší sazbou).
                // Pod výpis doplníme řádek s rovnítky pro oddělení a poté seznam zkratek států, které ve výpisu nefigurují.
                // Je dobré kód napsat tak, aby kód prošel jen jednou a rozdělil státy na „přes“ a „pod“ limit.
                // (Aby nebylo potřeba procházet seznam dvakrát.)

                // Použiju dvě pomocné proměnné, kam budu rozdělovat prvky původního seznamu setříděného sestupně podle výše plné sazby DPH
                // DPH vyšší než 20% a nepoužívají speciální sazbu DPH
                List<StatEU> statyEUArrayTop = new ArrayList<>();
                // DPH menší nebo rovno 20% nebo používají speciální sazbu DPH
                List<StatEU> statyEUArrayBottom = new ArrayList<>();
                // Další pomocná proměnná je určena k ukládání zkratek států EU,
                // které mají DPH menší nebo rovnu 20% nebo používají speciální sazbu DPH
                StringBuffer sb = new StringBuffer();
                sb.append("Sazba VAT " + limitDPH + " % nebo nižší nebo používají speciální sazbu:");
                // V cyklu projdu seznam států EU sestupně setříděný podle plné SAZBY DPH, rozdělím jej na dva "podseznamy"
                // a ze seznamu zkratek států EU, které nesplní testovací podmínku vytvořím řetězec zkratek států EU,
                // který zobrazím na konzoli
                for (StatEU statEU : statyEUArray) {
                    if ((statEU.getSpecialniSazba() == Boolean.FALSE) && ((statEU.getPlnaSazba() > limitDPH) | (statEU.getSnizenaSazba() > limitDPH))) {
                        System.out.println(statEU.getNazevStatu() + " (" + statEU.getZkratka() + "): " + statEU.getPlnaSazba() + " % ");
                        // Zároveň objekt přidám do pole států EU, které mají sazbu DPH vyšší než 20%
                        // a nepoužívají speciální sazbu DPH - statyEUArryBottom
                        statyEUArrayTop.add(statEU);
                    }
                    else {
                        // Pokud nebyl prvek z kolekce vypsán na konzoli, přidám jej do pole států EU které mají DPH menší nebo rovno 20%
                        // nebo používají speciální sazbu DPH - statyEUArryBottom
                        statyEUArrayBottom.add(statEU);
                        // Také jeho zkratku přídám do seznamu zkratek ukládaných do pomocné proměnné - textového řetězce
                        sb.append(" " + statEU.getZkratka() + ", ");
                    }
                }

                // Nyní vypišu na konzoli oddělovací řádek
                System.out.println("================================ ");
                // Výpis pomocné proměnné
                // (tedy stringu obsahujícího seznam států EU, jež mají DPH menší nebo rovnu 20% nebo používají speciální sazbu daně)
                // na konzoli
                System.out.println(sb);
                System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^( KONEC VÝPISU )");

                // Zápis do souboru - včetně uložení seznamu zkratek států EU, které mají DPH menší nebo rovnu 20%,
                // nebo používají speciální sazbu DPH
                String filenameOutput = FILENAME_OUT_BASE+Double.toString(limitDPH)+".txt";
                try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filenameOutput)))) {
                    // Vezmeme postupně každou položku ze seznamu měst EU, setříděného podle plné sazby DPH a zapíšeme ji
                    // jako jeden řádek atributů objektu (oddělených položkou DELIMITER) do výstupního textového souboru
                    if (!statyEUArrayTop.isEmpty()) {
                        for (StatEU statEU : statyEUArrayTop) {
                            writer.println(statEU.getZkratka() + DELIMITER +
                                    statEU.getNazevStatu()     + DELIMITER +
                                    statEU.getPlnaSazba()      + DELIMITER +
                                    statEU.getSnizenaSazba()   + DELIMITER +
                                    statEU.getSpecialniSazba().toString()
                            );
                        }
                    }
                // Seznam států EU s nižší nebo rovnou sazbou DPH jak 20% nebo používajících speciální sazbu DPH
                // nebudu do textového souboru zapisovat
                // writer.println(sb);
                System.out.println("************************( KONEC ZAPISOVÁNÍ DAT DO TEXTOVÉHO SOUBORU )************************");
                }
                catch (IOException e) {
                    System.err.println("Chyba při zápisu do souboru " + filenameOutput + ": " + e.getLocalizedMessage() + "\n");
                }
            }
        }
        else
        {
            System.err.println("************************( SEZNAM STÁTŮ NENÍ VYTVOŘEN - PROGRAM SKONČÍ )************************");
        }
    }
}



/*
TEXT ZADÁNÍ ÚKOLU:

Naprogramuj aplikaci v Javě, která vypíše všechny státy Evropské unie, které mají sazbu daně z přidané hodnoty
(DPH, VAT) vyšší, než 20 % a nepoužívají speciální sazbu DPH/VAT.
Aktuální data o jednotlivých státech a jejich daních dostaneš v souboru — vzorový soubor je ke stažení zde: vat-eu.csv.
Vstupní soubor bude ve stejné složce, jako projekt aplikace.
Formát vstupního souboru
    Na každém řádku vstupního souboru jsou informace o jednom státu v následujícím formátu:
        zkratka státu (například AT)
        název státu (Austria)
        plná sazba daně z přidané hodnoty v procentech (20)
        snížená sazba daně z přidané hodnoty v procentech (10)
        informace o tom, jestli země používá speciální sazbu DPH pro některé produkty (true/false)
    Jednotlivé hodnoty jsou odděleny vždy tabulátorem (v Javě zapíšeme tabulátor jako "\t").

Příklad několika řádků souboru:
AT  Austria 20  10  true
BE  Belgium 21  12  true
BG  Bulgaria    20  9   false
CY  Cyprus  19  9   false
CZ  Czech Republic  21  15  false

Poznámka:
    Všimni si, že v souboru jsou použity desetinné čárky.
    Je potřeba je buď:
        nahradit za desetinné tečky (jednodušší varianta, hledej, jak nahradit v textovém řetězci znaky),
        nebo k načítání desetinných čísel použít Scanner a vhodné národní nastavení.
    Je to trochu výzva — i v praxi mnohokrát potkáš situaci, kdy si nějaký kód musíš najít na webu.
    Věříme, že to zvládneš!


Výstup aplikace
***************

Postupně zpracuj tyto kroky:
    Načti ze souboru všechna data do vhodné datové struktury (vytvoř třídu pro uložení dat).
    Vypiš seznam všech států a u každého uveď základní sazbu daně z přidané hodnoty ve formátu podle vzoru:
    Název země (zkratka): základní sazba %
        Například:
        Austria (AT): 20 %
        Belgium (BE): 21 %
        ...
    Vypiš ve stejném formátu pouze státy, které mají základní sazbu daně z přidané hodnoty vyšší než 20 %
    a přitom nepoužívají speciální sazbu daně.
    Výpis z bodu 3. seřaď podle výše základní sazby DPH/VAT sestupně (nejprve státy s nejvyšší sazbou).
    Pod výpis z bodu 3. doplň řádek s rovnítky pro oddělení a poté seznam zkratek států, které ve výpisu nefigurují.
    Opět dodrž formát podle vzoru (místo tří teček budou další státy):
    Sweden (SE):    25 % (12 %)
    Croatia (HR):   25 % (13 %)
    ...
    Finland (FI):   24 % (14 %)
    ...
    ====================
    Sazba VAT 20 % nebo nižší nebo používají speciální sazbu: AT, CY, CZ,...
    Výsledný výpis zapiš také do souboru s názvem vat-over-20.txt. Výstupní soubor ulož do stejné složky,
    ve které byl vstupní soubor. (Výpis na obrazovku zůstává.)
    Doplň možnost, aby uživatel z klávesnice zadal výši sazby DPH/VAT, podle které se má filtrovat.
    Vypíší se tedy státy se základní sazbou vyšší než ta, kterou uživatel zadal.
        Pokud uživatel zmáčkne pouze Enter, jako výchozí hodnota se použij jako výchozí sazbu 20 %.
        Uprav název výstupního souboru tak, aby reflektoval zadanou sazbu daně.
        Například pro zadanou sazbu 17 % se vygeneruje soubor vat-over-17.txt
                        a pro sazbu 25 % se vygeneruje soubor vat-over-25.txt.


=====================================================================================================================
                            Zkontroluj si!
                        Zajímavé body, challenge

Je dobré kód napsat tak, aby kód prošel jen jednou a rozdělil státy na „přes“ a „pod“ limit.
(Aby nebylo potřeba procházet seznam dvakrát.)
Tvůj program by měl zvládnout vstup s desetinnými čísly s desetinnou čárkou.



*/