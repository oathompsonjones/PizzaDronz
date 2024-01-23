package uk.ac.ed.inf.IntegrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import junit.framework.TestCase;
import uk.ac.ed.inf.RestService.OrderValidator;
import uk.ac.ed.inf.Serializers.OrderJSONSerializer;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.*;

import java.io.StringWriter;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class OrderJSONSerializerFromOrderValidatorTest extends TestCase {
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

    private Order generateBasicOrderWithPizzas(Pizza[] pizzas) {
        Order order = generateBasicOrder();
        order.setPizzasInOrder(pizzas);
        return order;
    }

    private Order generateBasicOrderWithDate(LocalDate date) {
        Order order = generateBasicOrder();
        order.setOrderDate(date);
        return order;
    }

    private Order generateBasicOrderWithTotal(int total) {
        Order order = generateBasicOrder();
        order.setPriceTotalInPence(total);
        return order;
    }

    private Order generateBasicOrderWithPiazzasAndTotal(Pizza[] pizzas, int total) {
        Order order = generateBasicOrder();
        order.setPizzasInOrder(pizzas);
        order.setPriceTotalInPence(total);
        return order;
    }

    private Order generateBasicOrderWithCreditCardInformation(CreditCardInformation creditCardInformation) {
        Order order = generateBasicOrder();
        order.setCreditCardInformation(creditCardInformation);
        return order;
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

    private Order[] generateOrders() {
        return new Order[] {
                generateBasicOrderWithPizzas(new Pizza[0]),
                generateBasicOrderWithPizzas(new Pizza[] {
                        new Pizza("Margarita", 1000)
                }),
                generateBasicOrderWithPizzas(new Pizza[] {
                        new Pizza("Margarita", 1000), new Pizza("Calzone", 1400)
                }),
                generateBasicOrderWithPizzas(new Pizza[] {
                        new Pizza("Margarita", 1000), new Pizza("Calzone", 1400), new Pizza("Meat Lover", 1400)
                }),
                generateBasicOrderWithPizzas(new Pizza[] {
                        new Pizza("Margarita", 1000),
                        new Pizza("Calzone", 1400),
                        new Pizza("Meat Lover", 1400),
                        new Pizza("Vegan Delight", 1100)
                }),
                generateBasicOrderWithPizzas(new Pizza[] {
                        new Pizza("Margarita", 1000),
                        new Pizza("Calzone", 1400),
                        new Pizza("Meat Lover", 1400),
                        new Pizza("Vegan Delight", 1100),
                        new Pizza("Super Cheese", 1400)
                }),
                generateBasicOrderWithPizzas(new Pizza[] {
                        new Pizza("invalid pizza", 1000)
                }),
                generateBasicOrderWithPizzas(new Pizza[] {
                        new Pizza("Margarita", 1000), new Pizza("invalid pizza", 1000)
                }),
                generateBasicOrderWithPizzas(new Pizza[] {
                        new Pizza("Margarita", 1000), new Pizza("Calzone", 1400), new Pizza("invalid pizza", 1000)
                }),
                generateBasicOrderWithPizzas(new Pizza[] {
                        new Pizza("Margarita", 1000), new Pizza("Calzone", 1400), new Pizza("invalid pizza", 1000), null
                }),
                generateBasicOrderWithPizzas(new Pizza[] { null }),
                generateBasicOrderWithPizzas(null),
                generateBasicOrderWithPizzas(new Pizza[] {
                        new Pizza("Margarita", 1000), new Pizza("Calzone", 1400), new Pizza("Meat Lover", 1400)
                }),
                generateBasicOrderWithPizzas(new Pizza[] {
                        new Pizza("Margarita", 1000), new Pizza("Meat Lover", 1400)
                }),
                generateBasicOrderWithPizzas(new Pizza[] {
                        new Pizza("Margarita", 1000), new Pizza("Calzone", 1400), new Pizza("Meat Lover", 1400)
                }),
                generateBasicOrderWithPizzas(new Pizza[] {
                        new Pizza("Margarita", 1000), new Pizza("Calzone", 1400)
                }),
                generateBasicOrderWithPizzas(new Pizza[] {
                        new Pizza("Meat Lover", 1400), new Pizza("Vegan Delight", 1100)
                }),
                generateBasicOrderWithDate(LocalDate.of(2023, 10, 4)),
                generateBasicOrderWithDate(null),
                generateBasicOrderWithDate(LocalDate.of(2023, 10, 3)),
                generateBasicOrderWithTotal(0),
                generateBasicOrderWithTotal(SystemConstants.ORDER_CHARGE_IN_PENCE),
                generateBasicOrderWithTotal(generateBasicOrder().getPriceTotalInPence() + 1),
                generateBasicOrderWithPiazzasAndTotal(new Pizza[] {
                        new Pizza("Margarita", 1000), new Pizza("Calzone", 1400)
                }, 1000 + 1400),
                generateBasicOrderWithPiazzasAndTotal(new Pizza[] {
                        new Pizza("Margarita", 500), // Correct name, but incorrect price
                        new Pizza("Calzone", 1400)
                }, SystemConstants.ORDER_CHARGE_IN_PENCE + 500 + 1400),
                generateBasicOrderWithPiazzasAndTotal(new Pizza[] {
                        new Pizza("Margarita", 1000), new Pizza("Calzone", 1400)
                }, SystemConstants.ORDER_CHARGE_IN_PENCE + 1000 + 1400),
                generateBasicOrderWithCreditCardInformation(new CreditCardInformation("invalid characters",
                                                                                      "01/01",
                                                                                      "123"
                )),
                generateBasicOrderWithCreditCardInformation(new CreditCardInformation("0000000000000000",
                                                                                      "01/01",
                                                                                      "123"
                )),
                generateBasicOrderWithCreditCardInformation(new CreditCardInformation("0", "01/01", "123")),
                generateBasicOrderWithCreditCardInformation(new CreditCardInformation(null, "01/01", "123")),
                generateBasicOrderWithCreditCardInformation(new CreditCardInformation("4123456789012345",
                                                                                      "01/01",
                                                                                      "123"
                )),
                generateBasicOrderWithCreditCardInformation(new CreditCardInformation("2123456789012345",
                                                                                      "01/01",
                                                                                      "123"
                )),
                generateBasicOrderWithCreditCardInformation(new CreditCardInformation("5123456789012345",
                                                                                      "01/01",
                                                                                      "123"
                )),
                generateBasicOrderWithCreditCardInformation(new CreditCardInformation("4123456789012345",
                                                                                      "invalid date",
                                                                                      "123"
                )),
                generateBasicOrderWithCreditCardInformation(new CreditCardInformation("4123456789012345",
                                                                                      "01/00",
                                                                                      "123"
                )),
                generateBasicOrderWithCreditCardInformation(new CreditCardInformation("4123456789012345",
                                                                                      "01/23",
                                                                                      "123"
                )),
                generateBasicOrderWithCreditCardInformation(new CreditCardInformation("4123456789012345", null, "123")),
                generateBasicOrderWithCreditCardInformation(new CreditCardInformation("4123456789012345",
                                                                                      "10/23",
                                                                                      "123"
                )),
                generateBasicOrderWithCreditCardInformation(new CreditCardInformation("4123456789012345",
                                                                                      "01/30",
                                                                                      "123"
                )),
                generateBasicOrderWithCreditCardInformation(new CreditCardInformation("4123456789012345",
                                                                                      "01/30",
                                                                                      "invalid cvv"
                )),
                generateBasicOrderWithCreditCardInformation(new CreditCardInformation("4123456789012345",
                                                                                      "01/30",
                                                                                      "1"
                )),
                generateBasicOrderWithCreditCardInformation(new CreditCardInformation("4123456789012345",
                                                                                      "01/30",
                                                                                      "12"
                )),
                generateBasicOrderWithCreditCardInformation(new CreditCardInformation("4123456789012345",
                                                                                      "01/30",
                                                                                      "1234"
                )),
                generateBasicOrderWithCreditCardInformation(new CreditCardInformation("4123456789012345",
                                                                                      "01/30",
                                                                                      null
                )),
                generateBasicOrderWithCreditCardInformation(new CreditCardInformation("4123456789012345",
                                                                                      "01/30",
                                                                                      "123"
                ))
        };
    }

    public void testSerializerWithValidatedData() {
        var jsonWriter = new StringWriter();
        var module = new SimpleModule().addSerializer(Order.class, new OrderJSONSerializer());
        var serializerProvider = new ObjectMapper().registerModule(module);
        for (Order order : generateOrders()) {
            validator.validateOrder(order, generateRestaurants());
            try {
                serializerProvider.writeValue(jsonWriter, order);
            } catch (Exception err) {
                fail("Exception thrown when serializing order: " + err.getMessage());
            }
        }
        String serialization = "{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"PIZZA_NOT_DEFINED\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"VALID_BUT_NOT_DELIVERED\",\"orderValidationCode\":\"NO_ERROR\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"TOTAL_INCORRECT\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"PIZZA_FROM_MULTIPLE_RESTAURANTS\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"PIZZA_FROM_MULTIPLE_RESTAURANTS\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"MAX_PIZZA_COUNT_EXCEEDED\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"PIZZA_NOT_DEFINED\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"PIZZA_NOT_DEFINED\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"PIZZA_NOT_DEFINED\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"PIZZA_NOT_DEFINED\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"PIZZA_NOT_DEFINED\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"PIZZA_NOT_DEFINED\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"PIZZA_FROM_MULTIPLE_RESTAURANTS\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"PIZZA_FROM_MULTIPLE_RESTAURANTS\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"PIZZA_FROM_MULTIPLE_RESTAURANTS\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"TOTAL_INCORRECT\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"TOTAL_INCORRECT\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"RESTAURANT_CLOSED\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"RESTAURANT_CLOSED\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"VALID_BUT_NOT_DELIVERED\",\"orderValidationCode\":\"NO_ERROR\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"TOTAL_INCORRECT\",\"costInPence\":0}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"TOTAL_INCORRECT\",\"costInPence\":100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"TOTAL_INCORRECT\",\"costInPence\":1101}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"TOTAL_INCORRECT\",\"costInPence\":2400}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"TOTAL_INCORRECT\",\"costInPence\":2000}{\"orderNo\":\"1\",\"orderStatus\":\"VALID_BUT_NOT_DELIVERED\",\"orderValidationCode\":\"NO_ERROR\",\"costInPence\":2500}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"CARD_NUMBER_INVALID\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"CARD_NUMBER_INVALID\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"CARD_NUMBER_INVALID\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"CARD_NUMBER_INVALID\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"EXPIRY_DATE_INVALID\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"EXPIRY_DATE_INVALID\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"EXPIRY_DATE_INVALID\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"EXPIRY_DATE_INVALID\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"EXPIRY_DATE_INVALID\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"EXPIRY_DATE_INVALID\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"EXPIRY_DATE_INVALID\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"VALID_BUT_NOT_DELIVERED\",\"orderValidationCode\":\"NO_ERROR\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"VALID_BUT_NOT_DELIVERED\",\"orderValidationCode\":\"NO_ERROR\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"CVV_INVALID\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"CVV_INVALID\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"CVV_INVALID\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"CVV_INVALID\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"INVALID\",\"orderValidationCode\":\"CVV_INVALID\",\"costInPence\":1100}{\"orderNo\":\"1\",\"orderStatus\":\"VALID_BUT_NOT_DELIVERED\",\"orderValidationCode\":\"NO_ERROR\",\"costInPence\":1100}";
        assertEquals(jsonWriter.toString(), serialization);
    }
}
