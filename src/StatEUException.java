// Veřejná třída StatEUException rozšiřuje třídu Exception a slouží k obsluze vlastních výjímek
public class StatEUException extends Exception {
    public StatEUException (String message) {
        super(message);
    }
}