package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.time.LocalDate;
import java.util.Arrays;

public class PizzaDronz {
    private final RESTManager    restManager;
    private final OrderValidator orderValidator = new OrderValidator();
    private final LngLatHandler  lngLatHandler  = new LngLatHandler();

    public PizzaDronz(String apiUrl, LocalDate date) {
    }
}
