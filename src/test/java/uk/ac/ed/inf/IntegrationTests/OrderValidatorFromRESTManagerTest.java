package uk.ac.ed.inf.IntegrationTests;

import junit.framework.TestCase;
import uk.ac.ed.inf.RestService.OrderValidator;
import uk.ac.ed.inf.RestService.RESTManager;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.time.LocalDate;

public class OrderValidatorFromRESTManagerTest extends TestCase {
    OrderValidator validator = new OrderValidator();

    RESTManager manager;

    public OrderValidatorFromRESTManagerTest(String testName) {
        super(testName);
        try {
            manager = new RESTManager("https://ilp-rest.azurewebsites.net");
        } catch (Exception e) {
            fail("Exception thrown when creating RESTManager");
        }
    }

    public void testGivenDate() {
        Order[]      orders       = manager.getOrders(LocalDate.parse("2023-09-01"));
        Restaurant[] restaurants  = manager.getRestaurants();
        int          invalidCount = 0;
        int          validCount   = 0;
        for (Order order : orders) {
            Order validated = validator.validateOrder(order, restaurants);
            switch (validated.getOrderValidationCode()) {
                case NO_ERROR:
                    validCount++;
                    break;
                case UNDEFINED:
                    break;
                default:
                    invalidCount++;
                    break;
            }
            assertNotSame(OrderValidationCode.UNDEFINED, validated.getOrderValidationCode());
        }
        // The API should contain 8 invalid orders and 50 valid orders for each day with orders.
        assertEquals(orders.length, invalidCount + validCount);
        assertEquals(8, invalidCount);
        assertEquals(50, validCount);
    }

    public void testNoDate() {
        Order[]      orders       = manager.getOrders(null);
        Restaurant[] restaurants  = manager.getRestaurants();
        int          invalidCount = 0;
        int          validCount   = 0;
        for (Order order : orders) {
            Order validated = validator.validateOrder(order, restaurants);
            switch (validated.getOrderValidationCode()) {
                case NO_ERROR:
                    validCount++;
                    break;
                case UNDEFINED:
                    break;
                default:
                    invalidCount++;
                    break;
            }
            assertNotSame(OrderValidationCode.UNDEFINED, validated.getOrderValidationCode());
        }
        // The API should contain a total of 1200 invalid orders and 7500 valid orders.
        assertEquals(orders.length, invalidCount + validCount);
        assertEquals(1200, invalidCount);
        assertEquals(7500, validCount);
    }
}
