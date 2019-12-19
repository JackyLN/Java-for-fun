import java.util.Calendar;
import java.util.Scanner;
import java.util.TimeZone;

public class ReceiptGenerator{
    private static final String RETURNS_POLICY_URL = "http://www.FixAllPhones.co.uk/returns";
    private static final String TERMS_AND_CONDITIONS_URL = "http://www.FixAllPhones.co.uk/terms";
    private static final String SERVICE_REVIEW_URL = "https//uk.trustpilot.com/review/www.FixAllPhones.co.uk";
    private static final String REFERRAL_URL = "http://www.FixAllPhones.co.uk/phonefriend";
    private static final String SOCIAL_MEDIA = "FaceBook and Twitter";
    private static final String COMPANY_SLOGAN = "FixAllPhones - repairs you can trust";
    private static final int VAT_NUMBER = 123456789;
    //static final variables are constants, they cannot be changed by methods once assigned. 
    //By convention they are written in all caps,
    //with words separated by the underscore for readability.

    private static final String[] LOCATIONS = {
            "King's Cross",
            "Victoria",
            "Waterloo",
            "Euston",
            "Liverpool Street",
            "London Bridge",
            "Paddington",
            "Marylebone"
    };
    
	private static final String[] PAYMENT_TYPES = {
			"cash",
			"credit card",
			"debit card",
			"bitcoin"
	};
    
    private static final String[] TEMPLATE = {
            "%n****************************************%n* %s *%n****************************************%n%n", // + COMPANY_SLOGAN
            "Receipt - %s%n%n", // + receipt reference no
            "FixAll - %s%n%n", // + shop location
            "VAT No: %d%n%n", // + vat no
            "Served by: %s%n%n", // + staff member's first name (TODO and initial of surname?)
            "---------------------------------------------%nYour order:%n%n",
            "%s%n%n", // + costs breakdown
            "Subtotal\t\t\t\t£%.2f%n", // + subtotal (TODO: make it a task to make the receipts capable of being in euros? [ie make the £/€ in the receipt configurable] necessary because the Kings Cross branch is seeing lots of European business because of being next to St Pancras International)
            "Tax (VAT @ 20%%)\t\t\t\t£%.2f%n%n", // + tax (TODO: should tax rate also be configurable? VAT may change post Brexit)
            "TOTAL : %d item%s\t\t\t\t£%.2f%n%n", // + number of items, s or no s (single or plural), total amount payable
            "PAID (%s) :\t\t\t£%.2f%n%n", // + payment type (credit card/debit card/cash/bitcoin/other crypto), amount paid
            "TO PAY :\t\t\t\t£%.2f%n%n", // + amount left to pay
            "---------------------------------------------%n%n%n", //no additional info
            "Exchange and returns policy - %s%n%n", // + returns policy url
            "All repairs guaranteed for 18 months%nRepair T&Cs - %s%n%n%n", // + terms & conditions url
            "Rate our Service%n%s%n%n%n", // + 3rd party review site url
            "Get 20%% off your order when you refer a friend - see our web page for details%n%s%n%n", // + referral url
            "Follow us on %s%n" // + our social media channels
    };

    private static String dateAndTime;
    private static String shopLocation;
    private static String staffName;
    private static String itemisedCosts;
    private static int numberOfItemsPurchased;
    private static double subtotal;
    private static double vat;
    private static double total; 
    private static double outstandingBalance;
    private static double paid;
    private static String paymentMethod;
    private static Scanner in;

    public static void create() {
        in = new Scanner(System.in);
        
        dateAndTime = getDateAndTime();
        shopLocation = getShopLocationFromUser();
        staffName = getFromUser("your first name");
        staffName = capitalise(staffName);

        itemisedCosts = "";
        numberOfItemsPurchased = 0;
        subtotal = 0;
        
        boolean keepGoing = true;
        //loop so that the user can enter as many items as they need to
        while (keepGoing) {
            //ask user to enter information about item and charges
            String itemDescription = getFromUser("Item or service user is being charged for");
            //TODO client wants the item description in title case
            double unitPrice = getDoubleFromUser("the unit price (without VAT) of " + itemDescription);
            int numberOfUnits = getIntFromUser("the number of " + itemDescription);
            //TODO make above statements a method

            //update receipt information with item and charges for item
            itemisedCosts += addItemisedCostToReceipt(itemDescription, unitPrice, numberOfUnits);
            subtotal += (unitPrice * numberOfUnits);
            numberOfItemsPurchased += numberOfUnits;
            //TODO make above three statements a method

            //add more items to the receipt?
            String more = getFromUser("another item or service? Yes/No");
            if (more.trim().toLowerCase().startsWith("n")) {
                keepGoing = false;
            }
            //TODO make asking the user if they wish to add more items into a method
            
        }//end of while loop
        
		vat = getVat(subtotal);
        //calculate total with vat
        total = addVat(subtotal);
        
		paid = getDoubleFromUser("amount paid as a deposit by the customer"); 
										 //TODO write a new method to get the deposit from the user. 
        								 //enforce that the method will (1) give the user the minimum and 
        								 //the maximum amount of the deposit (minimum is 20% of the total, 
        								 //and maximum is the total); (2) The method will enforce that deposit 
        								 //amounts that are too low or too high will be rejected, 
        								 //and the user asked to enter another amount. The minimum deposit, 
        								 //once calculated, should be rounded to the nearest int 
        								 //(normal rounding applies), before being shown to the user.
        
        paymentMethod = getFromUser("deposit payment method"); //TODO validate this input. See PAYMENT_TYPES
        //all done with user input.

        //clean up
        in.close();
        
        calculateOutstandingBalance(); 
        
        //print the formatted receipt
        printReceipt();
    }

    private static String getDateAndTime() {
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"));
        return String.format("%1$te/%<Tb/%<tY::%<tH%<tM%<tz(%<tZ)", now);
    }

    private static String getShopLocationFromUser() {
        System.out.println("SHOP LOCATIONS:");
        for (int i = 0; i < LOCATIONS.length; i++) {
            System.out.println((i+1) + ") " + LOCATIONS[i]);
        }
        int choice = getIntFromUser("The number that corresponds to your shop");
        return LOCATIONS[choice-1];
    }
    
    private static String capitalise(String s) {
		if (s == null) return("");
		s = s.trim();
		if (s.length() < 2) return s.toUpperCase();
		return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
	}    
    
    private static String addItemisedCostToReceipt(String itemDescription, double unitPrice, int numberOfUnits) {
        //format: numberOfUnits x itemDescription @ £unitPrice each: £(unitPrice*NumberOfUnits)
        return String.format("%d x %s @ £%.2f each\t\t£%.2f%n", numberOfUnits, itemDescription, unitPrice, unitPrice * numberOfUnits);
    }

    private static void calculateOutstandingBalance() {
        outstandingBalance = total - paid;
    }

    private static String getFromUser(String prompt) {
        System.out.print("Enter " + prompt + ": ");
        return in.nextLine();
    }

    private static int getIntFromUser(String prompt) {
        boolean keepGoing = true;
        int i = 0;
        while (keepGoing) {
            System.out.print("Enter " + prompt + ": ");
            i = in.nextInt();

            in.nextLine(); //read the rest of the line that had the int.
            //without this, running getIntFromUser() followed by getFromUser()
            //would result in getFromUser() returning the rest of the line that the int was part of
            //(ie probably just a newline character)

            if (i < 1) { //make sure the number is more than 0
                System.out.println("Invalid number. Number must be greater than zero.");
            } else {
                keepGoing = false;
            }
        }
        return i;
    }

    private static double getDoubleFromUser(String prompt) {
        boolean keepGoing = true;
        double i = 0;

        while (keepGoing) {
            System.out.print(("Enter " + prompt + ": "));
            i = in.nextDouble();

            in.nextLine(); //read the rest of the line (probably just a newline character)

            if (i <= 0) {
                System.out.println("Invalid number. Number must be greater than zero.");
            } else {
                keepGoing = false;
            }
        }

        return i;
    }

    private static void printReceipt() {
        String s = "";

        for (int i = 0; i < TEMPLATE.length; i++) {
            s += String.format(TEMPLATE[i], getTemplateData(i));
        }

        /*
        TODO need to fix the tab spacing in the itemised costs
        and the other bits between the dashes (the costs breakdown etc)
         */
        System.out.println(s);
    }

    private static Object[] getTemplateData(int line) {
        if (line == 0) return new Object[]{ COMPANY_SLOGAN };
        if (line == 1) return new Object[]{ dateAndTime };
        if (line == 2) return new Object[]{ shopLocation };
        if (line == 3) return new Object[]{ VAT_NUMBER };
        if (line == 4) return new Object[]{ staffName };
        if (line == 6) return new Object[]{ itemisedCosts };
        if (line == 7) return new Object[]{ subtotal };
        if (line == 8) return new Object[]{ vat };
        if (line == 9) return new Object[]{ numberOfItemsPurchased, numberOfItemsPurchased > 1 ? "s" : "", total };
        if (line == 10) return new Object[]{ paymentMethod, paid };
        if (line == 11) return new Object[]{ outstandingBalance };
        if (line == 13) return new Object[]{ RETURNS_POLICY_URL };
        if (line == 14) return new Object[]{ TERMS_AND_CONDITIONS_URL };
        if (line == 15) return new Object[]{ SERVICE_REVIEW_URL };
        if (line == 16) return new Object[]{ REFERRAL_URL };
        if (line == 17) return new Object[]{ SOCIAL_MEDIA };
        else return new Object[]{ "" }; //any lines that don't need extra data
    }

    private static double addVat(double amount) {
        double vat = getVat(amount);
        return amount + vat;
    }
    
    private static double getVat(double amount) {
        return (amount*20.0)/100.0; //vat = 20%
    }

    public static void main(String[] args) {
        ReceiptGenerator.create();
    }
}

