import java.util.Scanner;

class CurrencyConverter {

    private double amount;
    private String fromCurrency;
    private String toCurrency;
    private double result;

    public void getInput() {
        Scanner sc = new Scanner(System.in);

        System.out.println("===== Global Currency Converter =====");
        System.out.print("Enter Amount: ");
        amount = sc.nextDouble();

        System.out.print("Enter From Currency (USD/INR/EUR/GBP): ");
        fromCurrency = sc.next().toUpperCase();

        System.out.print("Enter To Currency (USD/INR/EUR/GBP): ");
        toCurrency = sc.next().toUpperCase();
    }

    public void convertCurrency() {

        double usd = 0;

        if (fromCurrency.equals("USD"))
            usd = amount;
        else if (fromCurrency.equals("INR"))
            usd = amount / 83;
        else if (fromCurrency.equals("EUR"))
            usd = amount / 0.92;
        else if (fromCurrency.equals("GBP"))
            usd = amount / 0.79;
        else {
            System.out.println("Invalid Source Currency");
            return;
        }

        if (toCurrency.equals("USD"))
            result = usd;
        else if (toCurrency.equals("INR"))
            result = usd * 83;
        else if (toCurrency.equals("EUR"))
            result = usd * 0.92;
        else if (toCurrency.equals("GBP"))
            result = usd * 0.79;
        else {
            System.out.println("Invalid Target Currency");
            return;
        }

        System.out.println("\nConverted Amount: " + result + " " + toCurrency);
    }
}

public class Main {

    public static void main(String[] args) {
        CurrencyConverter converter = new CurrencyConverter();
        converter.getInput();
        converter.convertCurrency();
    }
}
