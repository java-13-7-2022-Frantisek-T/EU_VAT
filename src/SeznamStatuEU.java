import java.io.*;
import java.time.format.DecimalStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class SeznamStatuEU {

    // METODA pro zápis do souboru
    public void exportToFile (String filename, String delimiter) throws StatEUException {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filename)))) {
            // Vezmi postupně každou položku ze seznamu států EU:
            if (seznamStatuEU.isEmpty()) {
                throw new StatEUException("Seznam států EU je prázdný, není co zapisovat do výstupního souboru " + filename + " \n");
            }
            for (StatEU statEU : seznamStatuEU) {                             //  getList()
                writer.println( statEU.getZkratka() + delimiter +
                                statEU.getNazevStatu() + delimiter +
                                statEU.getPlnaSazba() + delimiter +
                                statEU.getSnizenaSazba() + delimiter +
                                statEU.getSpecialniSazba().toString()
                );
            }
        }
        catch (IOException e) {
            System.err.println("Chyba při zápisu do souboru " + filename + ": " + e.getLocalizedMessage() + "\n");
        }
        catch (StatEUException e) {
            System.err.println("Chyba při zápisu do souboru " + filename + ": " + e.getLocalizedMessage() + "\n");
        }
    }

    // METODA pro čtení ze souboru
    public static SeznamStatuEU importFromFile(String filename, String delimiter) throws StatEUException {
        // 1. přečíst řádky ze souboru
        // 2. Rozdělit řádky na jednotlivé položky a převést je na odpovídající datový typ
        // 3. Sestavit z jednotlivých proměnných objekty

        SeznamStatuEU result = new SeznamStatuEU();

        // Pomocná proměnná typu String pro uchování načteného řádku z textového souboru
        String line = "";
        // Pomocná proměnná typu int pro počítání načtených řádků
        int pocRad = 0;
        // Pomocná proměnná pro práci s oddělovačem u desetinných čísel v podobě řetezce
        DecimalStyle ds = DecimalStyle.STANDARD;
        // 1. Čtení ze souboru přes třídu Scanner - pomocí try-with-resources
        // "windows-1250", "ISO-8859-2", "IBM852", "UTF-8", "UTF-16BE", "UTF-16LE", "UTF-16"
        // try (Scanner scanner = new Scanner(new File(filename),"IBM852"))
        try (Scanner scanner = new Scanner(new File(filename))) {      //"UTF-16"  "UTF-16BE"  "UTF-16LE"
            while (scanner.hasNextLine()) {
                // Přečteme řádek ze souboru a pro jeho další zpracování jej uložíme do pomocné proměnné
                line = scanner.nextLine();
                pocRad++;
                System.out.println("Načten " + pocRad + " řádek:" + line);      // ----- pomocný výpis na obrazovku při ladění aplikace
                // 2. Rozdělíme řádek podle nalezených odělovacích znaků (zde konkrétně tabelátorů) na jednotlivé položky a vracíme pole (array) řetězců
                String[] items = line.split(delimiter);
                // Pokud není počet prvků pole správný (má jich být 5), tak vyhodíme vyjímku kde uvedeme popis chyby
                if (items.length != 5) {
                    throw new StatEUException("Na řádku: " + line + " je špatný počet položek: " + items.length + "\n");
                }
                // 3. Sestavíme z jednotlivých proměnných objekty

                // Uložíme položky pole do proměnných typu String (pokud nebyla vyhozena vyjímka pro nesprávný počet položek na zpracovávaném řádku - to by se neprovedlo nic)
                // První položka: zkratka státu
                String zkratka = items[0];
                // Druhá položka: název státu
                String nazevStatu = items[1];
                // Třetí položka: plná sazba DPH v procentech
                String plnaSazbaAsText = items[2].replace(',',ds.getDecimalSeparator());
                // Čtvrta položka: snížená sazba DPH v procentech
                String snizenaSazbaAsText = items[3].replace(',',ds.getDecimalSeparator());
                // Pátá položka: informace jestli země používá speciální sazbu DPH
                String specialniSazbaAsText = items[4];

                // Pokračujeme tak, že převedeme položky pole, jež jsou typu String na správný datový typ
                // String zkratka;                                                      // zkratka státu - není potřeba konverze (String je správný datový typ)
                // String nazevStatu;                                                   // název státu - není potřeba konverze (String je správný datový typ)

                plnaSazbaAsText.replace(",",".");

                Double  plnaSazba = Double.parseDouble(plnaSazbaAsText);                // převede text na desetinné číslo - plnaSazba
                Double  snizenaSazba = Double.parseDouble(snizenaSazbaAsText);          // převede text na desetinné číslo - snizenaSazba
                Boolean specialniSazba = Boolean.parseBoolean(specialniSazbaAsText);    // převede text na Boolean - určující zad země používa speciální sazbu

                // Vytvoříme objekt třídy statEU (z proměnných získaných převodem položek z pole řetězců na správné datové typy)
                StatEU statEU = new StatEU(zkratka, nazevStatu, plnaSazba, snizenaSazba, specialniSazba);
                // Přidáme nově vytvořený objekt třídy StatEU do kolekce obsahující objekty třídy StatEU
                result.addStatEU(statEU);
            }
        // Zachytáváme vyjímky
        // Výjímka vznikla, pokud textový soubor nebyl nalezen (a je obsloužena vyhozením další vyjímky - jedná se o vyjímku z námi vytvořené třídy StatEUException)
        } catch (FileNotFoundException e) {
            // Oznámíme popis chyby tak, že vyhodíme výjímku přes námi vytvořenou třídu vyjímek StatEUException
            throw new StatEUException("Soubor " + filename + " nebyl nalezen \n" + e.getLocalizedMessage() + "\n");
            // Výjímka vzniklá, pokud se nepodařil převod řetězce na datum (a je obsloužena vyhozením další vyjímky - jedná se o vyjímku z námi vytvořené třídy StatEUException)
        } catch (NumberFormatException e) {
            // Oznámíme popis chyby tak, že vyhodíme výjímku přes námi vytvořenou třídu vyjímek StatEUException
            throw new StatEUException("Špatný formát čísla na řádku: " + line + " \n" + e.getLocalizedMessage() + "\n");
        }

        // Předaání-předání výsledku
        return result;
    }

    // Vytvoření pole pro ukládání jednotlivývh států
    List<StatEU> seznamStatuEU = new ArrayList<>();

    // Přidání prvku-státu do seznamu
    public void addStatEU(StatEU newStatEU) {
        seznamStatuEU.add(newStatEU); }

    // Odebrání prvku-státu do seznamu
    public void removeStatEU(StatEU statEU) {
        seznamStatuEU.remove(statEU);
    }

    // Získání prvku-státu na zadaném indexu
    public StatEU getStatEUFromIndex (int indexOfStatEU) throws StatEUException {

        // Ošetření výskytu nenaplněného seznamu prvků-států
        if (seznamStatuEU.size() == 0) {
            throw new StatEUException ("Seznam který má obsahovat seznam států EU je prázdný !");
        }

        // Ošetření chybně zadané hodnoty indexu odkazující mimo rozsah pole
        try {
            return seznamStatuEU.get(indexOfStatEU);
        }
        catch(ArrayIndexOutOfBoundsException e) {
            throw new StatEUException("Prvek seznamu nenalezen, index " + indexOfStatEU + " odkazuje mimo rozsah pole [0 .. "+ seznamStatuEU.size() +"]" + e.getLocalizedMessage() + "\n");
        }
    }

    // předávám KOPII atribut - seznamu státůu !!!!!
    public List<StatEU> getList() {
        return new ArrayList<>(seznamStatuEU);
    }

    // předávám počet prvků pole - seznamu států !!!!!
    public int rangeOfSeznamStatuEU() {
        return seznamStatuEU.size();
    }
}