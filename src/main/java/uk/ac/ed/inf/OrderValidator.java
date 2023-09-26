package uk.ac.ed.inf;

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

public class OrderValidator implements OrderValidation {
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants) {
        if (!creditCardNumberIsValid(orderToValidate.getCreditCardInformation().getCreditCardNumber()))
            return setValidationCode(orderToValidate, OrderValidationCode.CARD_NUMBER_INVALID);

        if (!creditCardExpiryIsValid(orderToValidate.getCreditCardInformation().getCreditCardExpiry()))
            return setValidationCode(orderToValidate, OrderValidationCode.EXPIRY_DATE_INVALID);

        if (!creditCardCVVIsValid(orderToValidate.getCreditCardInformation().getCvv()))
            return setValidationCode(orderToValidate, OrderValidationCode.CVV_INVALID);

        if (!totalIsCorrect(orderToValidate.getPriceTotalInPence(), orderToValidate.getPizzasInOrder()))
            return setValidationCode(orderToValidate, OrderValidationCode.TOTAL_INCORRECT);

        if (!pizzaCountIsValid(orderToValidate.getPizzasInOrder()))
            return setValidationCode(orderToValidate, OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);

        if (!pizzasAllExist(orderToValidate.getPizzasInOrder(), definedRestaurants))
            return setValidationCode(orderToValidate, OrderValidationCode.PIZZA_NOT_DEFINED);

        Restaurant restaurant = pizzaAreFromSameRestaurant(orderToValidate.getPizzasInOrder(), definedRestaurants);
        if (restaurant == null)
            return setValidationCode(orderToValidate, OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);

        if (!restaurantIsOpen(restaurant))
            return setValidationCode(orderToValidate, OrderValidationCode.RESTAURANT_CLOSED);

        orderToValidate.setOrderStatus(OrderStatus.VALID_BUT_NOT_DELIVERED);
        orderToValidate.setOrderValidationCode(OrderValidationCode.NO_ERROR);
        return orderToValidate;
    }

    private Order setValidationCode(Order order, OrderValidationCode code) {
        order.setOrderStatus(OrderStatus.INVALID);
        order.setOrderValidationCode(code);
        return order;
    }

    private boolean creditCardNumberIsValid(String creditCardNumber) {
        // Check that there are 16 characters.
        if (creditCardNumber.length() != 16)
            return false;

        // Check that every character is a number.
        try {
            String[] chars = creditCardNumber.split("");
            for (String aChar : chars)
                Integer.parseInt(aChar);
        } catch (Exception e) {
            return false;
        }

        // Check that the number is valid for either Visa or MasterCard.
        boolean isVisa = creditCardNumber.startsWith("4");
        boolean isMastercard = creditCardNumber.startsWith("2") || creditCardNumber.startsWith("5");
        return isVisa || isMastercard;
    }

    private boolean creditCardExpiryIsValid(String creditCardExpiry) {
        // Check that both the month and year are numbers.
        int expiryMonth;
        int expiryYear;
        try {
            String[] splitDate = creditCardExpiry.split("/");
            expiryMonth = Integer.parseInt(splitDate[0]);
            expiryYear = Integer.parseInt(splitDate[1]);
        } catch (Exception e) {
            return false;
        }

        // Check that those numbers are valid.
        if (expiryMonth < 1 || expiryMonth > 12 || expiryYear < 0 || expiryYear > 99)
            return false;

        // Check that the expiry date has not passed.
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();
        return expiryYear > currentYear % 100 || expiryYear == currentYear % 100 && expiryMonth > currentMonth;
    }

    private boolean creditCardCVVIsValid(String cvv) {
        // Check that there are 3 characters.
        if (cvv.length() != 3)
            return false;

        // Check that every character is a number.
        try {
            String[] chars = cvv.split("");
            for (String aChar : chars)
                Integer.parseInt(aChar);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private boolean totalIsCorrect(int totalOnOrder, Pizza[] pizzas) {
        // Check that the price of each pizza sums to the total price of the order.
        int total = 0;
        for (Pizza pizza : pizzas)
            total += pizza.priceInPence();
        return total == totalOnOrder;
    }

    private boolean pizzaCountIsValid(Pizza[] pizzas) {
        // Check the number of pizzas doesn't exceed the system maximum per order.
        return pizzas.length <= SystemConstants.MAX_PIZZAS_PER_ORDER;
    }

    private boolean pizzasAllExist(Pizza[] pizzas, Restaurant[] restaurants) {
        for (Pizza pizza : pizzas) {
            boolean foundRestaurant = false;
            for (Restaurant restaurant : restaurants) {
                Pizza[] menu = restaurant.menu();
                String[] pizzaNames = (String[]) Arrays.stream(menu).map(Pizza::name).toArray();
                boolean foundPizza = false;
                for (String pizzaName : pizzaNames) {
                    if (Objects.equals(pizzaName, pizza.name())) {
                        foundPizza = true;
                        break;
                    }
                }
                if (foundPizza)
                    foundRestaurant = true;
            }
            if (!foundRestaurant)
                return false;
        }
        return true;
    }

    private Restaurant pizzaAreFromSameRestaurant(Pizza[] pizzas, Restaurant[] restaurants) {
        Restaurant firstRestaurant = null;
        for (Pizza pizza : pizzas) {
            for (Restaurant restaurant : restaurants) {
                Pizza[] menu = restaurant.menu();
                String[] pizzaNames = (String[]) Arrays.stream(menu).map(Pizza::name).toArray();
                for (String pizzaName : pizzaNames) {
                    if (Objects.equals(pizzaName, pizza.name())) {
                        if (firstRestaurant == null)
                            firstRestaurant = restaurant;
                        else if (!Objects.equals(firstRestaurant.name(), restaurant.name()))
                            return null;
                    }
                }
            }
        }
        return firstRestaurant;
    }

    private boolean restaurantIsOpen(Restaurant restaurant) {
        // Check that the restaurant is open today.
        return Arrays.stream(restaurant.openingDays()).anyMatch(d -> d == LocalDate.now().getDayOfWeek());
    }
}
