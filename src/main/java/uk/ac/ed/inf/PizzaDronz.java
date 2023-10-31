package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PizzaDronz {
    public static final OrderValidator      orderValidator = new OrderValidator();
    public static final LngLatHandler       lngLatHandler  = new LngLatHandler();
    public static final LngLat              appletonTower  = new LngLat(-3.186874, 55.944494);
    public final        FlightPathGenerator flightPathGenerator;
    private final       RESTManager         restManager;

    public PizzaDronz(String apiUrl, LocalDate date) {
        restManager = new RESTManager(apiUrl);
        flightPathGenerator = new FlightPathGenerator(restManager.getCentralArea(), restManager.getNoFlyZones(), restManager.getRestaurants());
        Order[]            validOrders = getValidOrders(date);
        List<List<LngLat>> flightPaths = flightPathGenerator.generate(validOrders);
    }

    public static Restaurant getOrderRestaurant(Order order, Restaurant[] restaurants) {
        return Arrays.stream(restaurants).filter(r -> Arrays.stream(r.menu()).anyMatch(p -> Objects.equals(p.name(), order.getPizzasInOrder()[0].name()))).toArray(Restaurant[]::new)[0];
    }

    private Order[] getValidOrders(LocalDate date) {
        Restaurant[] restaurants     = restManager.getRestaurants();
        Order[]      orders          = restManager.getOrders(date);
        Order[]      validatedOrders = Arrays.stream(orders).map(order -> orderValidator.validateOrder(order, restaurants)).toArray(Order[]::new);
        Order[]      validOrders     = Arrays.stream(validatedOrders).filter(order -> order.getOrderStatus() == OrderStatus.VALID_BUT_NOT_DELIVERED).toArray(Order[]::new);
        Order[]      invalidOrders   = Arrays.stream(validatedOrders).filter(order -> order.getOrderStatus() == OrderStatus.INVALID).toArray(Order[]::new);
        return validOrders;
    }

    private void generateDeliveryJSON(LocalDate date) {
        String fileName = "deliveries-" + date.toString() + ".json";
        writeFile(fileName, "");
    }

    private void generateFlightPathJSON(LocalDate date) {
        String fileName = "flightpath-" + date.toString() + ".json";
        writeFile(fileName, "");
    }

    private void generateFlightPathGeoJSON(LocalDate date) {
        String fileName = "drone-" + date.toString() + ".geojson";
        writeFile(fileName, "");
    }

    private void writeFile(String name, String data) {
        try {
            FileWriter fileWriter = new FileWriter(name);
            fileWriter.write(data);
            fileWriter.close();
        } catch (IOException e) {
            System.err.println("Error writing to file " + name);
        }
    }
}
