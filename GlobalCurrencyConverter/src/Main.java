import java.util.Scanner;

class CurrencyConverter {

    public void convert() {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Amount: ");
        double amount = sc.nextDouble();

        System.out.print("From Currency (USD/INR/EUR): ");
        String from = sc.next().toUpperCase();

        System.out.print("To Currency (USD/INR/EUR): ");
        String to = sc.next().toUpperCase();

        double usd = 0;
        double result = 0;

        // Convert to USD
        if (from.equals("USD"))
            usd = amount;
        else if (from.equals("INR"))
            usd = amount / 83;
        else if (from.equals("EUR"))
            usd = amount / 0.92;
        else {
            System.out.println("Invalid Currency");
            return;
        }

        // Convert from USD to target currency
        if (to.equals("USD"))
            result = usd;
        else if (to.equals("INR"))
            result = usd * 83;
        else if (to.equals("EUR"))
            result = usd * 0.92;
        else {
            System.out.println("Invalid Currency");
            return;
        }

        System.out.println("Converted Amount = " + result + " " + to);
    }
}
