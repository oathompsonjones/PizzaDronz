package uk.ac.ed.inf;

import java.time.LocalDate;

/**
 * Main class of the program. It takes two arguments from the command line:
 * the date and the path to the file containing the orders.
 */
public class App {
    public static void main(String[] args) {
        new PizzaDronz(args[1], LocalDate.parse(args[0]));
    }
}
