package uk.ac.ed.inf.RestService;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.interfaces.OrderValidation;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;

/**
 * Handles the validation of orders.
 */
public class OrderValidator implements OrderValidation {
    /**
     * Validate an order and deliver a validated version where the OrderStatus and OrderValidationCode are set
     * accordingly.
     *
     * @param order              the order which needs validation
     * @param definedRestaurants the array of defined restaurants
     *
     * @return the validated order
     */
    public Order validateOrder(Order order, Restaurant[] definedRestaurants) {
        // Check that all the pizzas exist
        Restaurant[] pizzaRestaurants = pizzasAllExist(order.getPizzasInOrder(), definedRestaurants);
        if (Arrays.stream(pizzaRestaurants).anyMatch(Objects::isNull))
            return setValidationCodeAndStatus(order, OrderValidationCode.PIZZA_NOT_DEFINED);

        // Check that the number of pizzas is valid
        if (!pizzaCountIsValid(order.getPizzasInOrder()))
            return setValidationCodeAndStatus(order, OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);

        // Check that all the pizzas are from the same restaurant
        Restaurant restaurant = pizzaAreFromSameRestaurant(pizzaRestaurants);
        if (restaurant == null)
            return setValidationCodeAndStatus(order, OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);

        // Check that the restaurant is open
        if (!restaurantIsOpen(restaurant, order.getOrderDate()))
            return setValidationCodeAndStatus(order, OrderValidationCode.RESTAURANT_CLOSED);

        // Check that the total price is correct
        if (!totalIsCorrect(order.getPriceTotalInPence(), order.getPizzasInOrder(), restaurant.menu()))
            return setValidationCodeAndStatus(order, OrderValidationCode.TOTAL_INCORRECT);

        // Check that the credit card number is valid
        if (!creditCardNumberIsValid(order.getCreditCardInformation().getCreditCardNumber()))
            return setValidationCodeAndStatus(order, OrderValidationCode.CARD_NUMBER_INVALID);

        // Check that the expiry date is valid
        if (!creditCardExpiryIsValid(order.getCreditCardInformation().getCreditCardExpiry(), order.getOrderDate()))
            return setValidationCodeAndStatus(order, OrderValidationCode.EXPIRY_DATE_INVALID);

        // Check that the CVV is valid
        if (!creditCardCVVIsValid(order.getCreditCardInformation().getCvv()))
            return setValidationCodeAndStatus(order, OrderValidationCode.CVV_INVALID);

        // If all the checks pass, the order is valid
        return setValidationCodeAndStatus(order, OrderValidationCode.NO_ERROR, OrderStatus.VALID_BUT_NOT_DELIVERED);
    }

    /**
     * Set the validation code of an order and set the status to Invalid.
     *
     * @param order the order to set the validation code and status of
     * @param code  the validation code to set
     *
     * @return the order with the validation code and status set
     */
    private Order setValidationCodeAndStatus(Order order, OrderValidationCode code) {
        return setValidationCodeAndStatus(order, code, OrderStatus.INVALID);
    }

    /**
     * Set the validation code and status of an order.
     *
     * @param order  the order to set the validation code and status of
     * @param code   the validation code to set
     * @param status the status to set
     *
     * @return the order with the validation code and status set
     */
    private Order setValidationCodeAndStatus(Order order, OrderValidationCode code, OrderStatus status) {
        // Set the order status and validation code
        order.setOrderStatus(status);
        order.setOrderValidationCode(code);
        return order;
    }

    /**
     * Check that a credit card number is valid.
     *
     * @param creditCardNumber the credit card number to check
     *
     * @return whether the credit card number is valid
     */
    private boolean creditCardNumberIsValid(String creditCardNumber) {
        // Check that the number is a valid card number for either Visa or MasterCard
        return creditCardNumber != null && creditCardNumber.matches("^[245]\\d{15}$");
    }

    /**
     * Check that a credit card expiry date is valid.
     *
     * @param creditCardExpiry the credit card expiry date to check
     *
     * @return whether the credit card expiry date is valid
     */
    private boolean creditCardExpiryIsValid(String creditCardExpiry, LocalDate orderDate) {
        // Check that both the month and year are numbers
        int expiryMonth;
        int expiryYear;
        try {
            String[] splitDate = creditCardExpiry.split("/");
            expiryMonth = Integer.parseInt(splitDate[0]);
            expiryYear = Integer.parseInt(splitDate[1]);
        } catch (Exception err) {
            return false;
        }

        // Check that those numbers are valid
        if (expiryMonth < 1 || expiryMonth > 12 || expiryYear < 0 || expiryYear > 99) return false;

        // Check that the expiry date has not passed
        int currentMonth = orderDate.getMonthValue();
        int currentYear  = orderDate.getYear() % 100;
        return expiryYear > currentYear || expiryYear == currentYear && expiryMonth >= currentMonth;
    }

    /**
     * Check that a credit card CVV is valid.
     *
     * @param cvv the credit card CVV to check
     *
     * @return whether the credit card CVV is valid
     */
    private boolean creditCardCVVIsValid(String cvv) {
        // Check that the CVV is a three-digit number
        return cvv != null && cvv.matches("^\\d{3}$");
    }

    /**
     * Check that the total price of an order is correct.
     *
     * @param totalOnOrder the total price of the order
     * @param pizzas       the pizzas on the order
     * @param menu         the pizzas available on the restaurant menu
     *
     * @return whether the total price of the order is correct
     */
    private boolean totalIsCorrect(int totalOnOrder, Pizza[] pizzas, Pizza[] menu) {
        if (pizzas == null || menu == null) return false;
        // Check that the price of each pizza sums to the total price of the order.
        int total = SystemConstants.ORDER_CHARGE_IN_PENCE;
        for (Pizza pizza : pizzas) {
            // Find the pizza on the menu
            Pizza menuPizza = Arrays
                    .stream(menu)
                    .filter(Objects::nonNull)
                    .filter(p -> p.name().equals(pizza.name()))
                    .toArray(Pizza[]::new)[0];
            // Add the price of the pizza to the total
            total += menuPizza.priceInPence();
        }
        return total == totalOnOrder;
    }

    /**
     * Check that the number of pizzas on an order is valid.
     *
     * @param pizzas the pizzas on the order
     *
     * @return whether the number of pizzas on the order is valid
     */
    private boolean pizzaCountIsValid(Pizza[] pizzas) {
        // Check the number of pizzas doesn't exceed the system maximum per order.
        return pizzas.length <= SystemConstants.MAX_PIZZAS_PER_ORDER;
    }

    /**
     * Check that all the pizzas on an order exist.
     *
     * @param pizzas      the pizzas on the order
     * @param restaurants the restaurants that the pizzas could be from
     *
     * @return whether all the pizzas on the order exist
     */
    private Restaurant[] pizzasAllExist(Pizza[] pizzas, Restaurant[] restaurants) {
        // Check that there is at least one pizza in the order
        if (pizzas == null || pizzas.length == 0) return new Restaurant[] { null };
        var pizzaRestaurants = new Restaurant[pizzas.length];
        // For each pizza...
        for (int i = 0; i < pizzas.length; i++) {
            if (pizzas[i] == null) continue;
            // Check each restaurant...
            for (Restaurant restaurant : restaurants) {
                // Check the names of each pizza on the restaurant's menu...
                String[] pizzaNames = Arrays
                        .stream(restaurant.menu())
                        .filter(Objects::nonNull)
                        .map(Pizza::name)
                        .toArray(String[]::new);
                for (String pizzaName : pizzaNames) {
                    // If the pizza is found, set the restaurant value in the array
                    if (pizzaName.equals(pizzas[i].name())) {
                        pizzaRestaurants[i] = restaurant;
                        break;
                    }
                }
            }
        }
        return pizzaRestaurants;
    }

    /**
     * Check that all the pizzas on an order are from the same restaurant.
     *
     * @param restaurants the restaurants that the pizzas are be from
     *
     * @return the restaurant that all the pizzas are from, or null if they are not all from the same restaurant
     */
    private Restaurant pizzaAreFromSameRestaurant(Restaurant[] restaurants) {
        // Check that all the restaurants are the same, and return that restaurant or null
        return Arrays.stream(restaurants).filter(Objects::nonNull).distinct().count() == 1 ? restaurants[0] : null;
    }

    /**
     * Check that a restaurant is open.
     *
     * @param restaurant the restaurant to check
     *
     * @return whether the restaurant is open
     */
    private boolean restaurantIsOpen(Restaurant restaurant, LocalDate orderDate) {
        // Check that the restaurant is open today
        return orderDate != null && Arrays
                .stream(restaurant.openingDays())
                .filter(Objects::nonNull)
                .anyMatch(day -> day == orderDate.getDayOfWeek());
    }
}
