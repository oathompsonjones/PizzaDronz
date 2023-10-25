package uk.ac.ed.inf;

import java.time.LocalDate;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        new PizzaDronz(args[1], LocalDate.parse(args[0]));
    }
}
