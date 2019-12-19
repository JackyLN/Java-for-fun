package coursework1;

import java.util.*;
import java.util.Calendar;
import java.util.Scanner;
import java.util.TimeZone;

public class ReceiptGenerator {
	private static final String RETURNS_POLICY_URL = "http://www.FixAllPhones.co.uk/returns";
	private static final String TERMS_AND_CONDITIONS_URL = "http://www.FixAllPhones.co.uk/terms";
	private static final String SERVICE_REVIEW_URL = "https//uk.trustpilot.com/review/www.FixAllPhones.co.uk";
	private static final String REFERRAL_URL = "http://www.FixAllPhones.co.uk/phonefriend";
	private static final String SOCIAL_MEDIA = "FaceBook and Twitter";
	private static final String COMPANY_SLOGAN = "FixAllPhones - repairs you can trust";
	private static final int VAT_NUMBER = 123456789;
	// static final variables are constants, they cannot be changed by methods once
	// assigned.
	// By convention they are written in all caps,
	// with words separated by the underscore for readability.

	private static final String[] LOCATIONS = { "King's Cross", "Victoria", "Waterloo", "Euston", "Liverpool Street",
			"London Bridge", "Paddington", "Marylebone" };

	private static final String[] PAYMENT_TYPES = { "cash", "credit card", "debit card", "bitcoin" };

	private static final String[] TEMPLATE = {
			"%n****************************************%n* %s *%n****************************************%n%n", // +
																																																					// COMPANY_SLOGAN
			"Receipt - %s%n%n", // + receipt reference no
			"FixAll - %s%n%n", // + shop location
			"VAT No: %d%n%n", // + vat no
			"Served by: %s%n%n", // + staff member's first name (TODO and initial of surname?)
			"---------------------------------------------%nYour order:%n%n", "%s%n%n", // + costs breakdown
			"Subtotal\t\t\t\t£%.2f%n", // + subtotal (TODO: make it a task to make the receipts capable of being in
																	// euros? [ie make the £/€ in the receipt configurable] necessary because the
																	// Kings Cross branch is seeing lots of European business because of being next
																	// to St Pancras International)
			"Tax (VAT @ 20%%)\t\t\t\t£%.2f%n%n", // + tax (TODO: should tax rate also be configurable? VAT may change post
																						// Brexit)
			"TOTAL : %d item%s\t\t\t\t£%.2f%n%n", // + number of items, s or no s (single or plural), total amount payable
			"PAID (%s) :\t\t\t£%.2f%n%n", // + payment type (credit card/debit card/cash/bitcoin/other crypto), amount
																		// paid
			"TO PAY :\t\t\t\t£%.2f%n%n", // + amount left to pay
			"---------------------------------------------%n%n%n", // no additional info
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

	private static String itemDescription;
	private static double unitPrice;
	private static int numberOfUnits;

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
		// loop so that the user can enter as many items as they need to
		while (keepGoing) {

			theSecondMethodnumber1();
			theSecondMethodnumber2();
			// add more items to the receipt?
			keepGoing = theSecondMethodnumber3();

		} // end of while loop

		vat = getVat(subtotal);
		// calculate total with vat
		total = addVat(subtotal);

		paid = theThirdMethodnumber1(total);
		paymentMethod = theThirdMethodnumber2();

		// clean up
		in.close();

		calculateOutstandingBalance();

		// print the formatted receipt
		printReceipt();
	}

	private static double theThirdMethodnumber1(double total) {
		double paid = getDoubleFromUser("amount paid as a deposit by the customer");
		// TODO write a new method to get the deposit from the user.
		// enforce that the method will (1) give the user the minimum and
		// the maximum amount of the deposit (minimum is 20% of the total,
		// and maximum is the total); (2) The method will enforce that deposit
		// amounts that are too low or too high will be rejected,
		// and the user asked to enter another amount. The minimum deposit,
		// once calculated, should be rounded to the nearest int
		// (normal rounding applies), before being shown to the user.
		Scanner in = new Scanner(System.in);
		Random num = new Random();

		int amount;
		int correctAmount = num.nextInt(192);
		System.out.println("Enter a deposit amount(must be at least 38 and no more than 192.0)  :");
		int guess = in.nextInt();
		// amount++;

		if (guess < correctAmount) {
			System.out.println("Deposit is too small");
		} else if (guess > correctAmount) {
			System.out.println("Deposit is too large");

		} else if (guess == correctAmount) {
			System.out.println();
		}

		return paid;
	}

	private static String theThirdMethodnumber2() {
		paymentMethod = getFromUser("deposit payment method");
		String[] payment = { "cash", "credit card", "debit card", "bitcoin" };
		boolean askForUserChoice = true;
		for (int i = 0; i < PAYMENT_TYPES.length; i++) {
		}

		return paymentMethod;
	}

	private static void theSecondMethodnumber1() {
		// ask user to enter information about item and charges
		itemDescription = getFromUser("Item service user is being charged for:");
		itemDescription = theFirstMethod(itemDescription);
		// TODO client wants the item description in title case

		// S1: split the string into String array using String split
		// S2: loop through the String array word by word
		// S3: For each word check if the word is APC -using another method
		// S4: If it is not APC, use capitalise() method to capitalise the word
		// S5: Append the word to stringToReturn - "+" String concat - not efficient

		// String buider(find out if can, impress the examiner

		unitPrice = getDoubleFromUser("the unit price (without VAT) of " + itemDescription);
		numberOfUnits = getIntFromUser("the number of " + itemDescription);
		// TODO make above statements a method
	}

	private static String theFirstMethod(String itemDescription) {
		itemDescription = getFromUser("Item service user is being charged for:");
		for (int i = 0; i < itemDescription.length(); i++) {
			if (itemDescription == null || itemDescription.isEmpty()) {

				return itemDescription.substring(0, 1).toUpperCase() + itemDescription.substring(1).toLowerCase();
			}
			// itemDescription to title case
			// use capitalize method to help you
		}
		return itemDescription;
	}

	private static void theSecondMethodnumber2() {
		// update receipt information with item and charges for item
		itemisedCosts += addItemisedCostToReceipt(itemDescription, unitPrice, numberOfUnits);
		subtotal += (unitPrice * numberOfUnits);
		numberOfItemsPurchased += numberOfUnits;
		// TODO make above three statements a method
	}

	private static boolean theSecondMethodnumber3() {
		boolean keepGoing = true;
		String more = getFromUser("another item or service? Yes/No");
		if (more.trim().toLowerCase().startsWith("n")) {
			keepGoing = false;
		}

		// TODO make asking the user if they wish to add more items into a method
		return keepGoing;
	}

	private static String getDateAndTime() {
		Calendar now = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"));
		return String.format("%1$te/%<Tb/%<tY::%<tH%<tM%<tz(%<tZ)", now);
	}

	private static String getShopLocationFromUser() {
		System.out.println("SHOP LOCATIONS:");
		for (int i = 0; i < LOCATIONS.length; i++) {
			System.out.println((i + 1) + ") " + LOCATIONS[i]);
		}
		int choice = getIntFromUser("The number that corresponds to your shop");
		return LOCATIONS[choice - 1];
	}

	private static String capitalise(String s) {
		if (s == null)
			return ("");
		s = s.trim();
		if (s.length() < 2)
			return s.toUpperCase();
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}

	private static String addItemisedCostToReceipt(String itemDescription, double unitPrice, int numberOfUnits) {
		// format: numberOfUnits x itemDescription @ £unitPrice each:
		// £(unitPrice*NumberOfUnits)
		return String.format("%d x %s @ £%.2f each\t\t£%.2f%n", numberOfUnits, itemDescription, unitPrice,
				unitPrice * numberOfUnits);
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

			in.nextLine(); // read the rest of the line that had the int.
			// without this, running getIntFromUser() followed by getFromUser()
			// would result in getFromUser() returning the rest of the line that the int was
			// part of
			// (ie probably just a newline character)

			if (i < 1) { // make sure the number is more than 0
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

			in.nextLine(); // read the rest of the line (probably just a newline character)

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
		// cannnot hard code.
		/*
		 * TODO need to fix the tab spacing in the itemised costs and the other bits
		 * between the dashes (the costs breakdown etc)
		 */
		System.out.println(s);
	}

	private static Object[] getTemplateData(int line) {
		if (line == 0)
			return new Object[] { COMPANY_SLOGAN };
		if (line == 1)
			return new Object[] { dateAndTime };
		if (line == 2)
			return new Object[] { shopLocation };
		if (line == 3)
			return new Object[] { VAT_NUMBER };
		if (line == 4)
			return new Object[] { staffName };
		if (line == 6)
			return new Object[] { itemisedCosts };
		if (line == 7)
			return new Object[] { subtotal };
		if (line == 8)
			return new Object[] { vat };
		if (line == 9)
			return new Object[] { numberOfItemsPurchased, numberOfItemsPurchased > 1 ? "s" : "", total };
		if (line == 10)
			return new Object[] { paymentMethod, paid };
		if (line == 11)
			return new Object[] { outstandingBalance };
		if (line == 13)
			return new Object[] { RETURNS_POLICY_URL };
		if (line == 14)
			return new Object[] { TERMS_AND_CONDITIONS_URL };
		if (line == 15)
			return new Object[] { SERVICE_REVIEW_URL };
		if (line == 16)
			return new Object[] { REFERRAL_URL };
		if (line == 17)
			return new Object[] { SOCIAL_MEDIA };
		else
			return new Object[] { "" }; // any lines that don't need extra data
	}

	private static double addVat(double amount) {
		double vat = getVat(amount);
		return amount + vat;
	}

	private static double getVat(double amount) {
		return (amount * 20.0) / 100.0; // vat = 20%
	}

	public static void main(String[] args) {
		// ReceiptGenerator.create();

		String result = MyTest("REPLACED BATTERY WITH A NEW BATTERY");

		System.out.println(result);
	}

	private static String MyTest(String str) {
		String[] split_str = str.split("\\s+");
		String result = "";
		for (String s : split_str) {
			result += capitaliseException(s) + " ";
		}
		result = result.trim();

		return result;
	}

	private static String capitaliseException(String s) {
		if (s.compareToIgnoreCase("a") == 0 || s.compareToIgnoreCase("an") == 0 || s.compareToIgnoreCase("and") == 0
				|| s.compareToIgnoreCase("the") == 0 || s.compareToIgnoreCase("with") == 0) {
			return s.toLowerCase();
		} else {
			return capitalise(s);
		}
	}
}
