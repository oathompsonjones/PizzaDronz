package uk.ac.ed.inf.UnitTests.RestService;

import junit.framework.TestCase;
import uk.ac.ed.inf.RestService.OrderValidator;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.*;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class OrderValidatorTest extends TestCase {
    OrderValidator validator = new OrderValidator();

    private Order generateBasicOrder() {
        return new Order("1",
                         LocalDate.of(2023, 10, 3),
                         OrderStatus.UNDEFINED,
                         OrderValidationCode.UNDEFINED,
                         SystemConstants.ORDER_CHARGE_IN_PENCE + 1000,
                         new Pizza[] {
                                 new Pizza("Margarita", 1000)
                         },
                         new CreditCardInformation("4123456789012345", "01/30", "123")
        );
    }

    @SuppressWarnings("SpellCheckingInspection")
    private Restaurant[] generateRestaurants() {
        return new Restaurant[] {
                new Restaurant("Civerinos Slice", new LngLat(-3.1912869215011597, 55.945535152517735), new DayOfWeek[] {
                        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY
                }, new Pizza[] {
                        new Pizza("Margarita", 1000), new Pizza("Calzone", 1400)
                }),
                new Restaurant("Sora Lella Vegan Restaurant",
                               new LngLat(-3.202541470527649, 55.943284737579376),
                               new DayOfWeek[] {
                                       DayOfWeek.MONDAY,
                                       DayOfWeek.TUESDAY,
                                       DayOfWeek.WEDNESDAY,
                                       DayOfWeek.THURSDAY,
                                       DayOfWeek.FRIDAY
                               },
                               new Pizza[] {
                                       new Pizza("Meat Lover", 1400), new Pizza("Vegan Delight", 1100)
                               }
                ),
                new Restaurant("Domino's Pizza - Edinburgh - Southside",
                               new LngLat(-3.1838572025299072, 55.94449876875712),
                               new DayOfWeek[] {
                                       DayOfWeek.WEDNESDAY,
                                       DayOfWeek.THURSDAY,
                                       DayOfWeek.FRIDAY,
                                       DayOfWeek.SATURDAY,
                                       DayOfWeek.SUNDAY
                               },
                               new Pizza[] {
                                       new Pizza("Super Cheese", 1400), new Pizza("All Shrooms", 900)
                               }
                ),
                new Restaurant("Soderberg Pavillion",
                               new LngLat(-3.1940174102783203, 55.94390696616939),
                               new DayOfWeek[] {
                                       DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY
                               },
                               new Pizza[] {
                                       new Pizza("Proper Pizza", 1400), new Pizza("Pineapple & Ham & Cheese", 900)
                               }
                )
        };
    }

    public void testPizzaCount() {
        // Test that an invalid pizza count is rejected
        Order order = generateBasicOrder();
        order.setPizzasInOrder(new Pizza[0]);
        order = validator.validateOrder(order, generateRestaurants());
        assertNotSame(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setPizzasInOrder(new Pizza[] {
                new Pizza("Margarita", 1000)
        });
        order = validator.validateOrder(order, generateRestaurants());
        assertNotSame(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setPizzasInOrder(new Pizza[] {
                new Pizza("Margarita", 1000), new Pizza("Calzone", 1400)
        });
        order = validator.validateOrder(order, generateRestaurants());
        assertNotSame(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setPizzasInOrder(new Pizza[] {
                new Pizza("Margarita", 1000), new Pizza("Calzone", 1400), new Pizza("Meat Lover", 1400)
        });
        order = validator.validateOrder(order, generateRestaurants());
        assertNotSame(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setPizzasInOrder(new Pizza[] {
                new Pizza("Margarita", 1000),
                new Pizza("Calzone", 1400),
                new Pizza("Meat Lover", 1400),
                new Pizza("Vegan Delight", 1100)
        });
        order = validator.validateOrder(order, generateRestaurants());
        assertNotSame(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setPizzasInOrder(new Pizza[] {
                new Pizza("Margarita", 1000),
                new Pizza("Calzone", 1400),
                new Pizza("Meat Lover", 1400),
                new Pizza("Vegan Delight", 1100),
                new Pizza("Super Cheese", 1400)
        });
        order = validator.validateOrder(order, generateRestaurants());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED, order.getOrderValidationCode());
    }

    public void testPizzasExist() {
        // Test that an invalid pizza is rejected
        Order order = generateBasicOrder();
        order.setPizzasInOrder(new Pizza[] {
                new Pizza("invalid pizza", 1000)
        });
        order = validator.validateOrder(order, generateRestaurants());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.PIZZA_NOT_DEFINED, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setPizzasInOrder(new Pizza[] {
                new Pizza("Margarita", 1000), new Pizza("invalid pizza", 1000)
        });
        order = validator.validateOrder(order, generateRestaurants());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.PIZZA_NOT_DEFINED, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setPizzasInOrder(new Pizza[] {
                new Pizza("Margarita", 1000), new Pizza("Calzone", 1400), new Pizza("invalid pizza", 1000)
        });
        order = validator.validateOrder(order, generateRestaurants());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.PIZZA_NOT_DEFINED, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setPizzasInOrder(new Pizza[] {
                new Pizza("Margarita", 1000), new Pizza("Calzone", 1400), new Pizza("invalid pizza", 1000), null
        });
        order = validator.validateOrder(order, generateRestaurants());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.PIZZA_NOT_DEFINED, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setPizzasInOrder(new Pizza[] { null });
        order = validator.validateOrder(order, generateRestaurants());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.PIZZA_NOT_DEFINED, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setPizzasInOrder(null);
        order = validator.validateOrder(order, generateRestaurants());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.PIZZA_NOT_DEFINED, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setPizzasInOrder(new Pizza[] {
                new Pizza("Margarita", 1000), new Pizza("Calzone", 1400), new Pizza("Meat Lover", 1400)
        });
        order = validator.validateOrder(order, generateRestaurants());
        assertNotSame(OrderValidationCode.PIZZA_NOT_DEFINED, order.getOrderValidationCode());
    }

    public void testPizzasFromSameRestaurant() {
        // Test that pizzas from multiple restaurants are rejected
        Order order = generateBasicOrder();
        order.setPizzasInOrder(new Pizza[] {
                new Pizza("Margarita", 1000), new Pizza("Meat Lover", 1400)
        });
        order = validator.validateOrder(order, generateRestaurants());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setPizzasInOrder(new Pizza[] {
                new Pizza("Margarita", 1000), new Pizza("Calzone", 1400), new Pizza("Meat Lover", 1400)
        });
        order = validator.validateOrder(order, generateRestaurants());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setPizzasInOrder(new Pizza[] {
                new Pizza("Margarita", 1000), new Pizza("Calzone", 1400)
        });
        order = validator.validateOrder(order, generateRestaurants());
        assertNotSame(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setPizzasInOrder(new Pizza[] {
                new Pizza("Meat Lover", 1400), new Pizza("Vegan Delight", 1100)
        });
        order = validator.validateOrder(order, generateRestaurants());
        assertNotSame(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS, order.getOrderValidationCode());
    }

    public void testRestaurantOpen() {
        // Test that an order on a closed day is rejected
        Order order = generateBasicOrder();
        order.setOrderDate(LocalDate.of(2023, 10, 4));
        order = validator.validateOrder(order, generateRestaurants());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.RESTAURANT_CLOSED, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setOrderDate(null);
        order = validator.validateOrder(order, generateRestaurants());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.RESTAURANT_CLOSED, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setOrderDate(LocalDate.of(2023, 10, 3));
        order = validator.validateOrder(order, generateRestaurants());
        assertNotSame(OrderValidationCode.RESTAURANT_CLOSED, order.getOrderValidationCode());
    }

    public void testTotalPrice() {
        // Test that an order with an invalid total price is rejected
        Order order = generateBasicOrder();
        order.setPriceTotalInPence(0);
        order = validator.validateOrder(order, generateRestaurants());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.TOTAL_INCORRECT, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setPriceTotalInPence(SystemConstants.ORDER_CHARGE_IN_PENCE);
        order = validator.validateOrder(order, generateRestaurants());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.TOTAL_INCORRECT, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setPriceTotalInPence(order.getPriceTotalInPence() + 1);
        order = validator.validateOrder(order, generateRestaurants());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.TOTAL_INCORRECT, order.getOrderValidationCode());

        order = generateBasicOrder();
        order = validator.validateOrder(order, generateRestaurants());
        assertNotSame(OrderValidationCode.TOTAL_INCORRECT, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setPizzasInOrder(new Pizza[] {
                new Pizza("Margarita", 1000), new Pizza("Calzone", 1400)
        });
        order.setPriceTotalInPence(1000 + 1400);
        order = validator.validateOrder(order, generateRestaurants());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.TOTAL_INCORRECT, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setPizzasInOrder(new Pizza[] {
                new Pizza("Margarita", 500), // Correct name, but incorrect price
                new Pizza("Calzone", 1400)
        });
        order.setPriceTotalInPence(SystemConstants.ORDER_CHARGE_IN_PENCE + 500 + 1400);
        order = validator.validateOrder(order, generateRestaurants());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.TOTAL_INCORRECT, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setPizzasInOrder(new Pizza[] {
                new Pizza("Margarita", 1000), new Pizza("Calzone", 1400)
        });
        order.setPriceTotalInPence(SystemConstants.ORDER_CHARGE_IN_PENCE + 1000 + 1400);
        order = validator.validateOrder(order, generateRestaurants());
        assertNotSame(OrderValidationCode.TOTAL_INCORRECT, order.getOrderValidationCode());
    }

    public void testCreditCardNumber() {
        // Test that an invalid credit card number is rejected.
        Order order = generateBasicOrder();
        order.setCreditCardInformation(new CreditCardInformation("invalid characters", "01/01", "123"));
        order = validator.validateOrder(order, generateRestaurants());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setCreditCardInformation(new CreditCardInformation("0000000000000000", "01/01", "123"));
        order = validator.validateOrder(order, generateRestaurants());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setCreditCardInformation(new CreditCardInformation("0", "01/01", "123"));
        order = validator.validateOrder(order, generateRestaurants());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setCreditCardInformation(new CreditCardInformation(null, "01/01", "123"));
        order = validator.validateOrder(order, generateRestaurants());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setCreditCardInformation(new CreditCardInformation("4123456789012345", "01/01", "123"));
        order = validator.validateOrder(order, generateRestaurants());
        assertNotSame(OrderValidationCode.CARD_NUMBER_INVALID, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setCreditCardInformation(new CreditCardInformation("2123456789012345", "01/01", "123"));
        order = validator.validateOrder(order, generateRestaurants());
        assertNotSame(OrderValidationCode.CARD_NUMBER_INVALID, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setCreditCardInformation(new CreditCardInformation("5123456789012345", "01/01", "123"));
        order = validator.validateOrder(order, generateRestaurants());
        assertNotSame(OrderValidationCode.CARD_NUMBER_INVALID, order.getOrderValidationCode());
    }

    public void testCreditCardExpiration() {
        // Test that an invalid credit card expiration date is rejected
        Order order = generateBasicOrder();
        order.setCreditCardInformation(new CreditCardInformation("4123456789012345", "invalid date", "123"));
        order = validator.validateOrder(order, generateRestaurants());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setCreditCardInformation(new CreditCardInformation("4123456789012345", "01/00", "123"));
        order = validator.validateOrder(order, generateRestaurants());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setCreditCardInformation(new CreditCardInformation("4123456789012345", "01/23", "123"));
        order = validator.validateOrder(order, generateRestaurants());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setCreditCardInformation(new CreditCardInformation("4123456789012345", null, "123"));
        order = validator.validateOrder(order, generateRestaurants());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setCreditCardInformation(new CreditCardInformation("4123456789012345", "10/23", "123"));
        order = validator.validateOrder(order, generateRestaurants());
        assertNotSame(OrderValidationCode.EXPIRY_DATE_INVALID, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setCreditCardInformation(new CreditCardInformation("4123456789012345", "01/30", "123"));
        order = validator.validateOrder(order, generateRestaurants());
        assertNotSame(OrderValidationCode.EXPIRY_DATE_INVALID, order.getOrderValidationCode());
    }

    public void testCreditCardCVV() {
        // Test that an invalid credit card CVV is rejected
        Order order = generateBasicOrder();
        order.setCreditCardInformation(new CreditCardInformation("4123456789012345", "01/30", "invalid cvv"));
        order = validator.validateOrder(order, generateRestaurants());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.CVV_INVALID, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setCreditCardInformation(new CreditCardInformation("4123456789012345", "01/30", "1"));
        order = validator.validateOrder(order, generateRestaurants());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.CVV_INVALID, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setCreditCardInformation(new CreditCardInformation("4123456789012345", "01/30", "12"));
        order = validator.validateOrder(order, generateRestaurants());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.CVV_INVALID, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setCreditCardInformation(new CreditCardInformation("4123456789012345", "01/30", "1234"));
        order = validator.validateOrder(order, generateRestaurants());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.CVV_INVALID, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setCreditCardInformation(new CreditCardInformation("4123456789012345", "01/30", null));
        order = validator.validateOrder(order, generateRestaurants());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.CVV_INVALID, order.getOrderValidationCode());

        order = generateBasicOrder();
        order.setCreditCardInformation(new CreditCardInformation("4123456789012345", "01/30", "123"));
        order = validator.validateOrder(order, generateRestaurants());
        assertNotSame(OrderValidationCode.CVV_INVALID, order.getOrderValidationCode());
    }
}
